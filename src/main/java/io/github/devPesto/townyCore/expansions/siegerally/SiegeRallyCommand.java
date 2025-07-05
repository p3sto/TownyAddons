package io.github.devPesto.townyCore.expansions.siegerally;

import com.gmail.goosius.siegewar.SiegeWarAPI;
import com.gmail.goosius.siegewar.objects.Siege;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.impl.Locale;
import io.github.devPesto.townyCore.objects.LunarPlayer;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class SiegeRallyCommand {
	private final SiegeRallyManager manager;
	private final Locale locale;

	public SiegeRallyCommand(SiegeRallyManager manager) {
		this.manager = manager;
		this.locale = TownyCore.getInstance().getLocale();
	}

	@Command("rally")
	@Description("Call your allies to your location")
	@CommandPermission("townycore.command.rally.use")
	@Cooldown(value = 10)
	protected void rally(LunarPlayer player) {
		Siege siege = SiegeWarAPI.getActiveSiegeAtLocation(player.getBukkitLocation());
		if (siege == null || !siege.getStatus().isActive()) {
			locale.sendMessage(player.asPlayer(), Locale.Nodes.RALLY_INACTIVE_SIEGE);
			return;
		}
		manager.resendRallyLocation(siege, player);
	}

	@Command("rally")
	@Subcommand({"enable", "on"})
	@CommandPermission("townycore.command.rally.enable")
	protected void enable(LunarPlayer player) {
		manager.enableRallyNotifications(player);
	}

	@Command("rally")
	@Subcommand({"disable", "off"})
	@CommandPermission("townycore.command.rally.disable")
	protected void disable(LunarPlayer player) {
		manager.disableRallyNotifications(player);
	}

}
