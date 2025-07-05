package io.github.devPesto.townyCore.objects;

import com.lunarclient.apollo.player.ApolloPlayer;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Wrapper type for {@link com.lunarclient.apollo.player.ApolloPlayer}
 */
public record LunarPlayer(ApolloPlayer apollo) {

	public String getName() {
		return apollo.getName();
	}

	public Player asPlayer() {
		return (Player) apollo.getPlayer();
	}

	public Resident asResident() {
		return TownyAPI.getInstance().getResident(apollo.getUniqueId());
	}

	public Location getBukkitLocation() {
		return asPlayer().getLocation();
	}

	public UUID getUniqueId() {
		return apollo.getUniqueId();
	}

}
