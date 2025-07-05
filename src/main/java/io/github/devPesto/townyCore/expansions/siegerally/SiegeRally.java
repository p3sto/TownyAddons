package io.github.devPesto.townyCore.expansions.siegerally;

import com.gmail.goosius.siegewar.objects.Siege;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.apollo.recipients.Recipients;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.objects.LunarPlayer;
import io.github.devPesto.townyCore.util.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SiegeRally {
	private static final WaypointModule module = Apollo.getModuleManager().getModule(WaypointModule.class);
	private final Map<UUID, ApolloPlayer> viewers = new HashMap<>();
	private Waypoint waypoint;
	private RallyRemovalTask task;

	public SiegeRally(Siege siege) {
		updateWaypoint("Siege Banner", siege.getFlagLocation());
	}

	/**
	 * Adds a {@link org.bukkit.entity.Player} recipient to the viewer map
	 *
	 * @param player The player being added to the viewer map
	 * @return {@code true} if the player was added and has Lunar client support, otherwise {@code false}
	 */
	public boolean addRecipient(LunarPlayer player) {
		UUID id = player.getUniqueId();
		if (!viewers.containsKey(id)) {
			viewers.put(id, player.apollo());
			if (task != null)
				task.addViewer(player);
			return true;
		}
		return false;
	}

	/**
	 * Removes a {@link org.bukkit.entity.Player} recipient from the viewer map and hides any visible rallies
	 *
	 * @param player The player being added to the viewer map
	 */
	public void removeRecipient(LunarPlayer player) {
		UUID id = player.getUniqueId();
		if (viewers.containsKey(id)) {
			ApolloPlayer apollo = viewers.get(id);
			viewers.remove(id);
			module.removeWaypoint(Recipients.of(Collections.singleton(apollo)), waypoint);
			if (task != null)
				task.removeViewer(apollo);
		}
	}

	/**
	 * Updates the waypoint location for the current viewers only.
	 *
	 * @param caller The player who called for the updated location
	 * @apiNote Use {@link #addRecipient(LunarPlayer)} to add to the viewer list
	 */
	public void send(LunarPlayer caller) {
		if (viewers.containsKey(caller.getUniqueId()) || addRecipient(caller)) {
			module.removeWaypoint(Recipients.of(viewers.values()), waypoint);
			updateWaypoint(caller);
			module.displayWaypoint(Recipients.of(viewers.values()), waypoint);
			rescheduleTask();
		}
	}

	public void rescheduleTask() {
		// Maybe the first rally call
		if (task != null)
			Bukkit.getScheduler().cancelTask(task.getTaskId());

		// Waypoint cannot be null at this point
		this.task = new RallyRemovalTask(viewers, waypoint);
		task.runTaskLater(TownyCore.getInstance(), SchedulerUtil.toTicks(10, TimeUnit.MINUTES));
	}

	private void updateWaypoint(String title, Location location) {
		this.waypoint = Waypoint.builder()
				.location(ApolloBlockLocation.builder()
						.world(location.getWorld().getName())
						.x(location.getBlockX())
						.y(location.getBlockY())
						.z(location.getBlockZ())
						.build())
				.name(title)
				.color(new Color(0xFFAA00))
				.build();
	}

	private void updateWaypoint(LunarPlayer player) {
		updateWaypoint(player.getName() + "'s Rally", player.getBukkitLocation());
	}

	private static class RallyRemovalTask extends BukkitRunnable {
		private final Map<UUID, ApolloPlayer> viewers;
		private final Waypoint waypoint;

		public RallyRemovalTask(Map<UUID, ApolloPlayer> viewers, Waypoint waypoint) {
			this.viewers = viewers;
			this.waypoint = waypoint;
		}

		public void addViewer(LunarPlayer player) {
			viewers.putIfAbsent(player.getUniqueId(), player.apollo());
		}

		public void removeViewer(ApolloPlayer player) {
			viewers.remove(player.getUniqueId());
		}

		@Override
		public void run() {
			module.removeWaypoint(Recipients.of(viewers.values()), waypoint);
		}
	}
}
