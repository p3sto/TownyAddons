package io.github.devPesto.townyCore.commands;

import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"townycore", "tcore", "tc"})
@CommandPermission("townycore.command")
public class TownyCoreCommand {

    @Subcommand("reload")
    public void reload(CommandSender sender) {

    }



}
