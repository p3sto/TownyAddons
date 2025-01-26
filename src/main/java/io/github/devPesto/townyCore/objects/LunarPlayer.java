package io.github.devPesto.townyCore.objects;

import com.lunarclient.apollo.player.ApolloPlayer;
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

	public Location getBukkitLocation() {
		return asPlayer().getLocation();
	}

	public UUID getUniqueId() {
		return apollo.getUniqueId();
	}

}
