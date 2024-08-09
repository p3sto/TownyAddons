package io.github.devPesto.townyCore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.devPesto.townyCore.command.RallyCommand;
import io.github.devPesto.townyCore.config.Config;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public final class TownyCore extends JavaPlugin {
	private @Getter Config configuration;
	private @Getter TownyExpansionManager expansions;
	private @Getter ProtocolManager protocol;
	private BukkitCommandHandler commands;

	@Override
	public void onEnable() {
		this.configuration = new Config(this, "config.yml");
		this.expansions = new TownyExpansionManager(this);
		this.commands = BukkitCommandHandler.create(this);

		if (isProtocolLibEnabled())
			this.protocol = ProtocolLibrary.getProtocolManager();

		commands.registerBrigadier();
		commands.register(new RallyCommand());


		expansions.registerAllExpansions();
	}

	@Override
	public void onDisable() {

	}

	public boolean isSiegeWarEnabled() {
		return isPluginEnabled("SiegeWar");
	}

	public boolean isApolloEnabled() {
		return isPluginEnabled("Apollo-Bukkit");
	}

	public boolean isProtocolLibEnabled() {
		return isPluginEnabled("ProtocolLib");
	}

	private boolean isPluginEnabled(String name) {
		return getServer().getPluginManager().isPluginEnabled(name);
	}
}
