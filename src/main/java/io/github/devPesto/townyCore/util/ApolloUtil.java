package io.github.devPesto.townyCore.util;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.apollo.player.ApolloPlayerManager;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.objects.LunarPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.bukkit.util.BukkitUtils;
import revxrsal.commands.command.CommandParameter;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.exception.ThrowableFromCommand;
import revxrsal.commands.process.SenderResolver;

import java.util.Optional;
import java.util.UUID;

public class ApolloUtil {
	private static final ApolloPlayerManager players = Apollo.getPlayerManager();

	public static LunarPlayer getLunarPlayer(Player player) {
		Optional<ApolloPlayer> apollo = players.getPlayer(player.getUniqueId());
		return apollo.map(LunarPlayer::new).orElse(null);
	}

	public static void initialize(Lamp.Builder<BukkitCommandActor> builder) {
		builder.exceptionHandler(new ApolloUtil.ApolloExceptionHandler())
				.senderResolver(new ApolloSenderResolver());
	}

	@ThrowableFromCommand
	public static class ApolloResolverException extends RuntimeException {

		public ApolloResolverException() {
			super();
		}

		public Component getMessageComponent() {
			return Component.text(getMessage()).color(TextColor.color(170, 0, 0));
		}
	}

	public static class ApolloExceptionHandler extends BukkitExceptionHandler {

		@HandleException
		public void onApolloException(ApolloResolverException e, BukkitCommandActor actor) {
			actor.error(BukkitUtils.legacyColorize("&cThis command can only be ran by players on Lunar client"));
		}
	}

	public static class ApolloSenderResolver implements SenderResolver<BukkitCommandActor> {

		@Override
		public boolean isSenderType(@NotNull CommandParameter parameter) {
			return ApolloPlayer.class.isAssignableFrom(parameter.type()) || Player.class.isAssignableFrom(parameter.type());
		}

		@NotNull
		@Override
		public Object getSender(@NotNull Class<?> customSenderType, @NotNull BukkitCommandActor actor, @NotNull ExecutableCommand<BukkitCommandActor> command) {
			if (actor.isPlayer()) {
				UUID id = actor.asPlayer().getUniqueId();
				ApolloPlayer player = players.getPlayer(id)
						.orElseThrow(ApolloResolverException::new);
				return new LunarPlayer(player);
			}
			throw new SenderNotPlayerException();
		}
	}
}
