package io.github.devPesto.townyCore.commands;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.impl.Locale;
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
        Locale messaging = plugin.getLocale();
        messaging.reload(plugin);
    }

    private void reloadConfig() {
        plugin.getSettings().reload(plugin);
    }

    public enum ReloadType {
        ALL, CONFIG, MESSAGES
    }
}
