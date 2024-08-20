package io.github.devPesto.townyCore.commands;

import com.gmail.goosius.siegewar.SiegeWarAPI;
import com.gmail.goosius.siegewar.objects.Siege;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.impl.MessageConfiguration;
import io.github.devPesto.townyCore.config.impl.MessageNode;
import io.github.devPesto.townyCore.manager.SiegeRallyManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("rally")
@Description("Call your allies to your location")
@CommandPermission("townycore.command.rally")
public class SiegeRallyCommand {
    private final SiegeRallyManager manager;
    private final MessageConfiguration messaging;

    public SiegeRallyCommand(SiegeRallyManager manager) {
        this.manager = manager;
        this.messaging = TownyCore.getInstance().getMessaging();
    }

    @DefaultFor("rally")
    @Cooldown(value = 10)
    protected void rally(Player player) {
        Siege siege = SiegeWarAPI.getActiveSiegeAtLocation(player);
        if (siege == null || !siege.getStatus().isActive()) {
            messaging.sendMessage(player, MessageNode.RALLY_INACTIVE_SIEGE);
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
