package io.github.devPesto.townyCore.expansions.impl;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.expansions.TownyExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.concurrent.TimeUnit;

public class RallyExpansion extends TownyExpansion {

    public RallyExpansion() {
        super("Rallies");
    }

    @Override
    public void registerCommands(TownyCore plugin) {
        plugin.getCommandManager().register(this);
    }

    @Command({"rally", "here"})
    @Description("Rally your allies to your location")
    @CommandPermission("townycore.command.rally")
    @Cooldown(value = 2, unit = TimeUnit.MINUTES)
    @AutoComplete("town|nation|allies")
    public void rally(Player sender, @Default("NATION") Target target) {
        Resident resident = TownyAPI.getInstance().getResident(sender);
        if (resident == null || !resident.hasNation()) {
            sender.sendMessage(Component.text("You need to be in a nation to run this command").color(TextColor.color(255, 0, 0)));
        } else {
            sender.sendMessage(Component.text("Rallied " + target.name()));
        }
    }

    private enum Target {
        TOWN, NATION, ALLIES;
    }

}
