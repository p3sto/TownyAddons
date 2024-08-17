package io.github.devPesto.townyCore.util;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.player.ApolloPlayer;
import io.github.devPesto.townyCore.TownyCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.CommandHandler;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.exception.ThrowableFromCommand;
import revxrsal.commands.process.SenderResolver;

import java.util.Optional;
import java.util.UUID;

public class ApolloUtil {
    private static boolean isResolverRegistered;

    public static Optional<ApolloPlayer> getOptPlayer(UUID id) {
        return Apollo.getPlayerManager().getPlayer(id);
    }

    public static Optional<ApolloPlayer> getOptPlayer(Player player) {
        return getOptPlayer(player.getUniqueId());
    }

    public static ApolloPlayer getPlayer(Player player) {
        return getOptPlayer(player).get();
    }

    public static boolean hasSupport(Player player) {
        return Apollo.getPlayerManager().hasSupport(player.getUniqueId());
    }

    public static void registerApolloResolver(TownyCore plugin) {
        if (isResolverRegistered) return;

        CommandHandler handler = plugin.getCommandHandler();
        handler.registerExceptionHandler(ApolloResolverException.class, (actor, e) -> {
            BukkitCommandActor a = actor.as(BukkitCommandActor.class);
            a.reply(e.getMessageComponent());
        });

        plugin.getCommandHandler().registerSenderResolver(new SenderResolver() {
            @Override
            public boolean isCustomType(Class<?> type) {
                return ApolloPlayer.class.isAssignableFrom(type);
            }

            @Override
            public @NotNull Object getSender(@NotNull Class<?> customSenderType, @NotNull CommandActor actor, @NotNull ExecutableCommand command) {
                BukkitCommandActor bukkitActor = (BukkitCommandActor) actor;
                if (bukkitActor.isPlayer()) {
                    UUID id = bukkitActor.getUniqueId();
                    return getOptPlayer(id).orElseThrow(ApolloResolverException::new);
                }
                throw new SenderNotPlayerException();
            }
        });
        isResolverRegistered = true;
    }

    @ThrowableFromCommand
    public static class ApolloResolverException extends RuntimeException {

        public ApolloResolverException() {
            super("This command can only be ran by players on Lunar client");
        }

        public Component getMessageComponent() {
            return Component.text(getMessage()).color(TextColor.color(170, 0, 0));
        }


    }
}
