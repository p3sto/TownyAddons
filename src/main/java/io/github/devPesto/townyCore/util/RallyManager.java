package io.github.devPesto.townyCore.util;

import com.gmail.goosius.siegewar.SiegeWarAPI;
import com.gmail.goosius.siegewar.enums.SiegeSide;
import com.gmail.goosius.siegewar.objects.Siege;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.github.devPesto.townyCore.objects.SiegeRally;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class RallyManager {
    private final Map<Siege, Pair<SiegeRally, SiegeRally>> rallyMap;
    private final Map<UUID, ApolloPlayer> ignoredPlayers;

    public RallyManager() {
        this.rallyMap = new HashMap<>();
        this.ignoredPlayers = new HashMap<>();
    }

    /**
     * Register {@link SiegeRally} instances for all active sieges
     */
    public void registerRallies() {
        for (Siege siege : SiegeWarAPI.getSieges()) {
            if (siege.getStatus().isActive()) {
                SiegeRally attacker = new SiegeRally();
                SiegeRally defender = new SiegeRally();
                rallyMap.put(siege, Pair.of(attacker, defender));
            }
        }
    }

    /**
     * Updates the location of rally point
     *
     * @param siege  The siege the rally belongs to
     * @param caller The player calling for the location update
     */
    public void updateRallyLocation(Siege siege, Player caller) {
        if (ignoredPlayers.containsKey(caller.getUniqueId())) {
            caller.sendMessage("You have rallies disabled");
            return;
        }

        SiegeSide side = SiegeSide.getPlayerSiegeSide(siege, caller);
        if (side == SiegeSide.NOBODY) {
            caller.sendMessage("You are not part of this siege");
            return;
        }

        Pair<SiegeRally, SiegeRally> pair = rallyMap.get(siege);
        SiegeRally rally = side == SiegeSide.ATTACKERS ? pair.first() : pair.second();
        rally.update(caller);
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
        ignoredPlayers.put(player.getUniqueId(), ApolloUtil.getPlayer(player));

        // TODO: Add persistence to rally settings using Towny's metadata api
        // Resident resident = TownyAPI.getInstance().getResident(player);

        player.sendMessage("Rallies have been disabled");
        for (Siege siege : SiegeWarAPI.getSieges()) {
            SiegeSide side = SiegeSide.getPlayerSiegeSide(siege, player);
            if (side != SiegeSide.NOBODY) {
                Pair<SiegeRally, SiegeRally> pair = rallyMap.get(siege);
                SiegeRally rally = side == SiegeSide.ATTACKERS ? pair.first() : pair.second();
                rally.removeRecipient(player);
                break;
            }
        }

        Key key = Key.key("entity.experience_orb.pickup");
        Sound sound = Sound.sound(key, Sound.Source.PLAYER, 2f, 0.01f);
        player.playSound(sound);
    }


    /**
     * Determines a list of all players receiving the rally
     *
     * @param resident {@link com.palmergames.bukkit.towny.Towny} resident representation of the rally caller
     * @param target   The intended target group
     * @return {@link HashSet} of online resident that fit the {@link RallyTarget} criteria
     */
//    public Set<Player> determineRecipients(Resident resident, RallyTarget target) {
//        Government government = target == RallyTarget.TOWN ? resident.getTownOrNull() : resident.getNationOrNull();
//        Set<Resident> targets = new HashSet<>(government.getResidents());
//
//        // Add all other allies only if the target is allies
//        if (target == RallyTarget.ALLIES)
//            targets.addAll(((Nation) government).getMutualAllies()
//                    .stream()
//                    .flatMap(n -> n.getResidents().stream())
//                    .toList()
//            );
//
//        return targets.stream()
//                .filter(Resident::isOnline)
//                .map(Resident::getPlayer)
//                .collect(Collectors.toSet());
//    }
}
