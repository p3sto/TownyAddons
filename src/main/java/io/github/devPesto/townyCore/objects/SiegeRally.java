package io.github.devPesto.townyCore.objects;

import com.gmail.goosius.siegewar.objects.Siege;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.apollo.recipients.Recipients;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.util.ApolloUtil;
import io.github.devPesto.townyCore.util.SchedulerUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SiegeRally {
    private static final WaypointModule module = Apollo.getModuleManager().getModule(WaypointModule.class);
    private final Map<UUID, ApolloPlayer> viewers;
    private Waypoint waypoint;
    private RallyRemovalTask task;

    public SiegeRally(Siege siege) {
        this.viewers = new HashMap<>();
        updateWaypoint(null, siege.getFlagLocation());
        this.task = null;
    }

    /**
     * Adds a {@link Player} recipient to the viewer map
     *
     * @param player The player being added to the viewer map
     * @return {@code true} if the player was added and has Lunar client support, otherwise {@code false}
     */
    public boolean addRecipient(Player player) {
        UUID id = player.getUniqueId();
        if (!viewers.containsKey(id) && ApolloUtil.hasSupport(player)) {
            ApolloUtil.getOptPlayer(player)
                    .ifPresent(p -> {
                        viewers.put(id, p);
                        if (task != null)
                            task.addViewer(p);
                    });
            return true;
        }
        return false;
    }

    /**
     * Removes a {@link Player} recipient from the viewer map and hides any visible rallies
     *
     * @param player The player being added to the viewer map
     */
    public void removeRecipient(Player player) {
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
     * @apiNote Use {@link #addRecipient(Player)} to add to the viewer list
     */
    public void send(Player caller) {
        Location location = caller.getLocation();
        if (viewers.containsKey(caller.getUniqueId()) || addRecipient(caller)) {
            module.removeWaypoint(Recipients.of(viewers.values()), waypoint);
            updateWaypoint(caller, location);
            module.displayWaypoint(Recipients.of(viewers.values()), waypoint);
            rescheduleTask();
        }
    }

    public void rescheduleTask() {
        // May be first rally call
        if (task != null)
            Bukkit.getScheduler().cancelTask(task.getTaskId());

        // Waypoint cannot be null at this point
        this.task = new RallyRemovalTask(viewers, waypoint);
        task.runTaskLater(TownyCore.getInstance(), SchedulerUtil.toTicks(10, TimeUnit.MINUTES));
    }

    private void updateWaypoint(Player player, Location location) {
        String name = "Siege Banner";
        if (player != null) {
            location = player.getLocation();
            name = player.getName();
        }

        this.waypoint = Waypoint.builder()
                .location(ApolloBlockLocation.builder()
                        .world(location.getWorld().getName())
                        .x(location.getBlockX())
                        .y(location.getBlockY())
                        .z(location.getBlockZ())
                        .build())
                .name(name)
                .color(new Color(0xFFAA00))
                .preventRemoval(true)
                .build();
    }

    @Getter
    @AllArgsConstructor
    private static class RallyRemovalTask extends BukkitRunnable {
        private Map<UUID, ApolloPlayer> viewers;
        private Waypoint waypoint;

        public void addViewer(ApolloPlayer player) {
            viewers.putIfAbsent(player.getUniqueId(), player);
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
