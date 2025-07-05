package io.github.devPesto.townyCore.expansions.siegerally;

import com.gmail.goosius.siegewar.SiegeWarAPI;
import com.gmail.goosius.siegewar.enums.SiegeSide;
import com.gmail.goosius.siegewar.objects.Siege;
import com.gmail.goosius.siegewar.utils.SiegeWarDistanceUtil;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.palmergames.bukkit.towny.object.Government;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.impl.Locale;
import io.github.devPesto.townyCore.objects.LunarPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.devPesto.townyCore.config.impl.Locale.Nodes;

public class SiegeRallyManager {
	private final Map<Siege, SiegeRallies> siegeRallies = new HashMap<>();
	private final Map<UUID, ApolloPlayer> ignoredPlayers = new HashMap<>();
	private final Locale messaging;

	public SiegeRallyManager() {
		this.messaging = TownyCore.getInstance().getLocale();

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
			messaging.sendMessage(p, Nodes.RALLY_DISABLED);
			return;
		}

		// Determine if the player is able to rally
		SiegeRally rally = getRally(siege, player);
		if (rally == null) {
			messaging.sendMessage(player.asPlayer(), Nodes.RALLY_NON_PARTICIPANT);
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
			messaging.sendMessage(pl, Nodes.RALLY_UPDATED_LOCATION, Map.of(
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
			messaging.sendMessage(p, Nodes.RALLY_ALREADY_ENABLED);
			return;
		}

		// TODO: Add persistence to rally settings using Towny's metadata api
		// Resident resident = TownyAPI.getInstance().getResident(player);

		ignoredPlayers.remove(player.getUniqueId());
		messaging.sendMessage(p, Nodes.RALLY_ENABLED);
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
			messaging.sendMessage(p, Nodes.RALLY_ALREADY_DISABLED);
			return;
		}
		ignoredPlayers.put(player.getUniqueId(), player.apollo());

		// TODO: Add persistence to rally settings using Towny's metadata api
		// Resident resident = TownyAPI.getInstance().getResident(player);

		messaging.sendMessage(p, Nodes.RALLY_DISABLED);
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