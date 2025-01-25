package io.github.devPesto.townyCore.commands;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.impl.MessageConfiguration;
import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"townycore", "tcore", "tc"})
@CommandPermission("townycore.command")
public class TownyCoreCommand {
    private final TownyCore plugin;

    public TownyCoreCommand(TownyCore plugin) {
        this.plugin = plugin;
    }

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
        MessageConfiguration messaging = plugin.getLangConfiguration();
        messaging.reload(plugin);
        // plugin.getCommandHandler().setMessagePrefix(messaging.getLegacyPrefix());
    }

    private void reloadConfig() {
        plugin.getConfiguration().reload(plugin);
    }

    public enum ReloadType {
        ALL, CONFIG, MESSAGES
    }



}
