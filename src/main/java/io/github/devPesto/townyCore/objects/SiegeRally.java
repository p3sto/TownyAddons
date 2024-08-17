package io.github.devPesto.townyCore.objects;

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
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

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

    public SiegeRally() {
        this.viewers = new HashMap<>();
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
            ApolloUtil.getOptPlayer(player).ifPresent(p -> {
                viewers.put(id, p);
                task.addViewer(p);
                module.displayWaypoint(Recipients.of(viewers.values()), waypoint);
            });
            return true;
        }
        return false;
    }

    /**
     * Removes a {@link Player} recipient from the viewer map and hides any visible rallies
     *
     * @param player The player being added to the viewer map
     * @return {@code true} if the player was removed, otherwise {@code false}
     */
    public boolean removeRecipient(Player player) {
        UUID id = player.getUniqueId();
        if (viewers.containsKey(id)) {
            ApolloPlayer apollo = viewers.get(id);
            module.removeWaypoint(Recipients.of(Collections.singleton(apollo)), waypoint);
            task.removeViewer(apollo);
            viewers.remove(id);
            return true;
        }
        return false;
    }

    /**
     * Updates the waypoint location for the current viewers only.
     *
     * @param caller The player who called for the updated location
     * @apiNote Use {@link #addRecipient(Player)} to add to the viewer list
     */
    public void update(Player caller) {
        Location location = caller.getLocation();
        if (viewers.containsKey(caller.getUniqueId()) || addRecipient(caller)) {
            module.removeWaypoint(Recipients.of(viewers.values()), waypoint);
            waypoint = Waypoint.builder()
                    .location(ApolloBlockLocation.builder()
                            .world(location.getWorld().getName())
                            .x(location.getBlockX())
                            .y(location.getBlockY())
                            .z(location.getBlockZ())
                            .build())
                    .color(new Color(0xFFAA00))
                    .preventRemoval(true)
                    .build();
            module.displayWaypoint(Recipients.of(viewers.values()), waypoint);

            // Send message and play rally sound
            viewers.values().forEach(p -> {
                // TODO: Move to messages.yml
                String strLoc = String.format("X:%d, Y:%d, Z:%d", location.getBlockX(), location.getBlockY(), location.getBlockZ());
                Player player = (Player) p.getPlayer();
                player.sendMessage(caller.getName() + " has updated the rally to " + strLoc);

                // Play horn sound for rally
                // TODO: Investigate why sound doesn't stay with player
                Key key = Key.key("entity.experience_orb.pickup");
                Sound sound = Sound.sound(key, Sound.Source.PLAYER, 2f, 1f);
                player.playSound(sound);
            });
            rescheduleTask();
        }
    }

    public void rescheduleTask() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.cancelTask(task.getTaskId());
        this.task = new RallyRemovalTask(viewers, waypoint);
        task.runTaskLater(TownyCore.getInstance(), SchedulerUtil.toTicks(10, TimeUnit.MINUTES));
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
