package io.github.devPesto.townyCore.manager;

import com.comphenix.protocol.PacketType;
import com.gmail.goosius.siegewar.SiegeWarAPI;
import com.gmail.goosius.siegewar.enums.SiegeSide;
import com.gmail.goosius.siegewar.objects.Siege;
import com.gmail.goosius.siegewar.utils.SiegeWarDistanceUtil;
import com.palmergames.bukkit.towny.object.Government;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import io.github.devPesto.townyCore.objects.SiegeRally;
import io.github.devPesto.townyCore.util.Pair;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class SiegeRallyManager {
    private final Map<Siege, SiegeRallies> siegeRallies;
    private final Map<UUID, Player> ignoredPlayers;

    public SiegeRallyManager() {
        this.siegeRallies = new HashMap<>();
        this.ignoredPlayers = new HashMap<>();
    }

    /**
     * Register {@link SiegeRally} instance for the passed in siege
     *
     * @param siege
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

    public void addViewer(Player player) {
        Siege siege = SiegeWarDistanceUtil.getActiveSiegeZonePlayerIsRegisteredTo(player);
        if (siege == null)
            return;

        // Ignore if not part of siege
        SiegeRally rally = getRally(siege, player);
        if (rally != null) {
            rally.addRecipient(player);
        }
    }

    public void removeViewer(Player player) {
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
    public void resendRallyLocation(Siege siege, Player player) {
        // Ignore if rallies are disabled for the player
        if (ignoredPlayers.containsKey(player.getUniqueId())) {
            player.sendMessage("You have rallies disabled");
            return;
        }

        // Determine if the player is able to rally
        SiegeRally rally = getRally(siege, player);
        if (rally == null) {
            player.sendMessage("You are not part of this siege");
            return;
        }

        // Update the waypoint for Lunar players
        rally.send(player);

        // Send message and play sound to all players (both Lunar and non-Lunar)
        Location location = player.getLocation();
        Set<Player> players = determineRecipients(siege, SiegeSide.getPlayerSiegeSide(siege, player));

        // Send message and play rally sound
        players.forEach(p -> {
            // TODO: Move to messages.yml
            String strLoc = String.format("X:%d , Y:%d, Z:%d", location.getBlockX(), location.getBlockY(), location.getBlockZ());
            player.sendMessage(player.getName() + " has updated the rally to " + strLoc);

            // Play horn sound for rally
            // TODO: Investigate why sound doesn't stay with player
            Key key = Key.key("entity.experience_orb.pickup");
            Sound sound = Sound.sound(key, Sound.Source.PLAYER, 2f, 1f);
            player.playSound(sound);
        });
    }

    /**
     * Enables player to receive rally notifications
     *
     * @param player
     */
    public void enableRallyNotifications(Player player) {
        if (!ignoredPlayers.containsKey(player.getUniqueId())) {
            player.sendMessage("You already have rallies enabled");
            return;
        }

        // TODO: Add persistence to rally settings using Towny's metadata api
        // Resident resident = TownyAPI.getInstance().getResident(player);

        ignoredPlayers.remove(player.getUniqueId());
        player.sendMessage("Rallies have been enabled");

        Key key = Key.key("entity.experience_orb.pickup");
        Sound sound = Sound.sound(key, Sound.Source.PLAYER, 2f, 1f);
        player.playSound(sound);
    }

    /**
     * Disables any future rally notifications and removes all existing rallies
     *
     * @param player
     */
    public void disableRallyNotifications(Player player) {
        if (ignoredPlayers.containsKey(player.getUniqueId())) {
            player.sendMessage("You already have rallies disabled");
            return;
        }
        ignoredPlayers.put(player.getUniqueId(), player);

        // TODO: Add persistence to rally settings using Towny's metadata api
        // Resident resident = TownyAPI.getInstance().getResident(player);

        player.sendMessage("Rallies have been disabled");
        for (Siege siege : SiegeWarAPI.getSieges()) {
            SiegeRally rally = getRally(siege, player);
            if (rally != null) {
                rally.removeRecipient(player);
                break;
            }
        }

        Key key = Key.key("entity.experience_orb.pickup");
        Sound sound = Sound.sound(key, Sound.Source.PLAYER, 2f, 0.01f);
        player.playSound(sound);
    }


    private SiegeRally getRally(Siege siege, Player player) {
        SiegeSide side = SiegeSide.getPlayerSiegeSide(siege, player);
        return siegeRallies.get(siege).getRally(side);
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

    private class SiegeRallies {
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
