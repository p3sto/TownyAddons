package io.github.devPesto.townyCore.expansions.impl;

import com.gmail.goosius.siegewar.SiegeWarAPI;
import com.gmail.goosius.siegewar.enums.SiegeSide;
import com.gmail.goosius.siegewar.events.SiegeWarStartEvent;
import com.gmail.goosius.siegewar.objects.Siege;
import com.gmail.goosius.siegewar.utils.SiegeWarDistanceUtil;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.apollo.recipients.Recipients;
import com.palmergames.bukkit.towny.object.Government;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.impl.LangConfiguration;
import io.github.devPesto.townyCore.config.impl.LangNodes;
import io.github.devPesto.townyCore.expansions.TownyExpansion;
import io.github.devPesto.townyCore.objects.LunarPlayer;
import io.github.devPesto.townyCore.util.ApolloUtil;
import io.github.devPesto.townyCore.util.SchedulerUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.awt.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SiegeRallyExpansion extends TownyExpansion implements Listener {
    private final SiegeRallyManager manager;
    private final LangConfiguration messaging;


    public SiegeRallyExpansion() {
        super("Rallies", "Apollo-Bukkit", "SiegeWar");
        this.manager = new SiegeRallyManager();
        this.messaging = TownyCore.getInstance().getLangConfiguration();
    }

    @Override
    public void registerCommands(TownyCore plugin) {
        plugin.getCommandHandler().register();
    }

    @Override
    protected void registerListeners(TownyCore plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void register(TownyCore plugin) {
        super.register(plugin);
        manager.registerRallies();
    }

    @Override
    protected void unregisterListeners(TownyCore plugin) {
        HandlerList.unregisterAll(this);
    }


    @Command("rally")
    @Description("Call your allies to your location")
    @CommandPermission("townycore.command.rally.use")
    @Cooldown(value = 10)
    protected void rally(LunarPlayer player) {
        Siege siege = SiegeWarAPI.getActiveSiegeAtLocation(player.getBukkitLocation());
        if (siege == null || !siege.getStatus().isActive()) {
            messaging.sendMessage(player.asPlayer(), LangNodes.RALLY_INACTIVE_SIEGE);
            return;
        }
        manager.resendRallyLocation(siege, player);
    }

    @Command("rally")
    @Subcommand({"enable", "on"})
    @CommandPermission("townycore.command.rally.enable")
    protected void enable(LunarPlayer player) {
        manager.enableRallyNotifications(player);
    }

    @Command("rally")
    @Subcommand({"disable", "off"})
    @CommandPermission("townycore.command.rally.disable")
    protected void disable(LunarPlayer player) {
        manager.disableRallyNotifications(player);
    }

    /**
     * Event listeners
     */
    protected static class SiegeRallyListener implements Listener {
        private final SiegeRallyManager manager;

        public SiegeRallyListener(SiegeRallyManager manager) {
            this.manager = manager;
        }

        @EventHandler()
        public void onSiegeBegin(SiegeWarStartEvent event) {
            manager.registerRally(event.getSiege());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onJoin(PlayerJoinEvent event) {
            LunarPlayer player = ApolloUtil.getLunarPlayer(event.getPlayer());
            if (player != null)
                manager.addViewer(player);
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onQuit(PlayerQuitEvent event) {
            LunarPlayer player = ApolloUtil.getLunarPlayer(event.getPlayer());
            if (player != null)
                manager.removeViewer(player);
        }

    }

    /**
     * Rally manager
     */
    protected static class SiegeRallyManager {
        private final Map<Siege, SiegeRallies> siegeRallies = new HashMap<>();
        private final Map<UUID, ApolloPlayer> ignoredPlayers = new HashMap<>();
        private final LangConfiguration messaging;

        public SiegeRallyManager() {
            this.messaging = TownyCore.getInstance().getLangConfiguration();
        }

        /**
         * Register {@link SiegeRally} instance for the passed in siege
         *
         * @param siege Siege instance
         */
        public void registerRally(Siege siege) {
            if (siege.getStatus().isActive())
                siegeRallies.put(siege, new SiegeRallies(siege));
        }

        /**
         * Register {@link SiegeRally} instances for all active sieges
         */
        public void registerRallies() {
            SiegeWarAPI.getSieges().forEach(this::registerRally);
        }

        /**
         * Determines if a player should be able to view a rally
         *
         * @param player A {@link LunarPlayer} playing on Lunar client
         */
        public void addViewer(LunarPlayer player) {
            Siege siege = SiegeWarDistanceUtil.getActiveSiegeZonePlayerIsRegisteredTo(player.asPlayer());
            if (siege == null)
                return;

            // Ignore if not part of siege
            SiegeRally rally = getRally(siege, player);
            if (rally != null) {
                rally.addRecipient(player);
            }
        }

        /**
         * Removes a player from viewing a rally
         *
         * @param player The lunar player
         */
        public void removeViewer(LunarPlayer player) {
            SiegeWarAPI.getSieges().forEach(siege -> {
                SiegeRally rally = getRally(siege, player);
                if (rally != null)
                    rally.removeRecipient(player);
            });
        }

        /**
         * Resend the location of the rally point
         *
         * @param siege  The siege the rally belongs to
         * @param player The player responsible for calling the resend
         */
        public void resendRallyLocation(Siege siege, LunarPlayer player) {
            Player p = player.asPlayer();
            // Ignore if rallies are disabled for the player
            if (ignoredPlayers.containsKey(player.getUniqueId())) {
                messaging.sendMessage(p, LangNodes.RALLY_DISABLED);
                return;
            }

            // Determine if the player is able to rally
            SiegeRally rally = getRally(siege, player);
            if (rally == null) {
                messaging.sendMessage(player.asPlayer(), LangNodes.RALLY_NON_PARTICIPANT);
                return;
            }

            // Update the waypoint for Lunar players
            rally.send(player);

            // Send message and play sound to all players (both Lunar and non-Lunar)
            Location location = player.getBukkitLocation();
            Set<Player> players = determineRecipients(siege, SiegeSide.getPlayerSiegeSide(siege, p));

            // Send message and play rally sound
            players.forEach(pl -> {
                String strLoc = String.format("%d, %d, %d", location.getBlockX(), location.getBlockY(), location.getBlockZ());
                messaging.sendMessage(pl, LangNodes.RALLY_UPDATED_LOCATION, Map.of(
                        "%player%", player.getName(),
                        "%location%", strLoc
                ));
                playNotificationSound(pl, false);
            });
        }

        /**
         * Enables player to receive rally notifications
         *
         * @param player
         */
        public void enableRallyNotifications(LunarPlayer player) {
            Player p = player.asPlayer();
            if (!ignoredPlayers.containsKey(player.getUniqueId())) {
                messaging.sendMessage(p, LangNodes.RALLY_ALREADY_ENABLED);
                return;
            }

            // TODO: Add persistence to rally settings using Towny's metadata api
            // Resident resident = TownyAPI.getInstance().getResident(player);

            ignoredPlayers.remove(player.getUniqueId());
            messaging.sendMessage(p, LangNodes.RALLY_ENABLED);
            playNotificationSound(p, false);
        }

        /**
         * Disables any future rally notifications and removes all existing rallies
         *
         * @param player
         */
        public void disableRallyNotifications(LunarPlayer player) {
            Player p = player.asPlayer();
            if (ignoredPlayers.containsKey(player.getUniqueId())) {
                messaging.sendMessage(p, LangNodes.RALLY_ALREADY_DISABLED);
                return;
            }
            ignoredPlayers.put(player.getUniqueId(), player.apollo());

            // TODO: Add persistence to rally settings using Towny's metadata api
            // Resident resident = TownyAPI.getInstance().getResident(player);

            messaging.sendMessage(p, LangNodes.RALLY_DISABLED);
            for (Siege siege : SiegeWarAPI.getSieges()) {
                SiegeRally rally = getRally(siege, player);
                if (rally != null) {
                    rally.removeRecipient(player);
                    break;
                }
            }
            playNotificationSound(p, true);
        }

        private SiegeRally getRally(Siege siege, LunarPlayer player) {
            SiegeSide side = SiegeSide.getPlayerSiegeSide(siege, player.asPlayer());
            SiegeRallies rallies = siegeRallies.get(siege);
            return rallies != null ? rallies.getRally(side) : null;
        }

        private void playNotificationSound(Audience audience, boolean negative) {
            Key key = Key.key("entity.experience_orb.pickup");
            Sound sound = Sound.sound(key, Sound.Source.PLAYER, 1f, negative ? 0.01f : 1f);
            audience.playSound(sound);
        }

        /**
         * Determines a list of all players receiving the rally
         */
        private Set<Player> determineRecipients(Siege siege, SiegeSide side) {
            Government government = side == SiegeSide.ATTACKERS ? siege.getAttackingNationIfPossibleElseTown() :
                    siege.getDefendingNationIfPossibleElseTown();

            // Add all other allies only if the target is allies
            Set<Resident> players = new HashSet<>();
            if (government instanceof Nation nation) {
                players.addAll(nation.getMutualAllies()
                        .stream()
                        .flatMap(n -> n.getResidents().stream())
                        .toList());
            }

            players.addAll(government.getResidents());
            return players.stream()
                    .map(Resident::getPlayer)
                    .filter(p -> Objects.nonNull(p) && p.isOnline() && !ignoredPlayers.containsKey(p.getUniqueId()) &&
                            SiegeWarDistanceUtil.getActiveSiegeZonePlayerIsRegisteredTo(p) == siege)
                    .collect(Collectors.toSet());
        }

        private static class SiegeRallies {
            private final SiegeRally attacker;
            private final SiegeRally defender;

            public SiegeRallies(Siege siege) {
                this.attacker = new SiegeRally(siege);
                this.defender = new SiegeRally(siege);
            }

            public SiegeRally getRally(SiegeSide side) {
                if (side == SiegeSide.ATTACKERS)
                    return attacker;
                else if (side == SiegeSide.DEFENDERS)
                    return defender;
                else
                    return null;
            }
        }
    }

    /**
     * Rally object
     */
    protected static class SiegeRally {
        private static final WaypointModule module = Apollo.getModuleManager().getModule(WaypointModule.class);
        private final Map<UUID, ApolloPlayer> viewers = new HashMap<>();
        private Waypoint waypoint;
        private RallyRemovalTask task;

        public SiegeRally(Siege siege) {
            updateWaypoint("Siege Banner", siege.getFlagLocation());
        }

        /**
         * Adds a {@link Player} recipient to the viewer map
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
         * Removes a {@link Player} recipient from the viewer map and hides any visible rallies
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
            // May be first rally call
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

}
