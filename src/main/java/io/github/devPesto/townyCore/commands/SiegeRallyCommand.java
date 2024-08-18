package io.github.devPesto.townyCore.commands;

import com.gmail.goosius.siegewar.SiegeWarAPI;
import com.gmail.goosius.siegewar.objects.Siege;
import io.github.devPesto.townyCore.manager.SiegeRallyManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("rally")
@Description("Call your allies to your location")
@CommandPermission("townycore.command.rally")
public class SiegeRallyCommand {
    private final SiegeRallyManager manager;

    public SiegeRallyCommand(SiegeRallyManager manager) {
        this.manager = manager;
    }

    @DefaultFor("rally")
//    @Cooldown(value = 90)
    protected void rally(Player player) {
        Siege siege = SiegeWarAPI.getActiveSiegeAtLocation(player);
        if (siege == null || !siege.getStatus().isActive()) {
            player.sendMessage("You are not in an active siege zone or the siege is inactive"); // TODO: Move to message.yml
            return;
        }
        manager.resendRallyLocation(siege, player);
    }

    @Subcommand({"enable", "on"})
    protected void enable(Player player) {
        manager.enableRallyNotifications(player);
    }

    @Subcommand({"disable", "off"})
    protected void disable(Player player) {
        manager.disableRallyNotifications(player);
    }
}
