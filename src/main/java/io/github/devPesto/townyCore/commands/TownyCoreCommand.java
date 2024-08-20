package io.github.devPesto.townyCore.commands;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.impl.MessageConfiguration;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"townycore", "tcore", "tc"})
@CommandPermission("townycore.command")
@AllArgsConstructor
public class TownyCoreCommand {
    private TownyCore plugin;

    @Subcommand("reload")
    @CommandPermission("townycore.command.reload")
    public void reload(CommandSender sender, ReloadType type) {
        switch (type) {
            case ALL -> {
                reloadMessages();
                reloadConfig();
            }
            case CONFIG -> reloadConfig();
            case MESSAGES -> reloadMessages();
        }
    }

    private void reloadMessages() {
        MessageConfiguration messaging = plugin.getMessaging();
        messaging.reload(plugin);
        plugin.getCommandHandler().setMessagePrefix(messaging.getLegacyPrefix());
    }

    private void reloadConfig() {
        plugin.getConfiguration().reload(plugin);
    }

    public enum ReloadType {
        ALL, CONFIG, MESSAGES;
    }



}
