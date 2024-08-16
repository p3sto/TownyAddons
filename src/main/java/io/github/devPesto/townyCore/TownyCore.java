package io.github.devPesto.townyCore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.devPesto.townyCore.config.Config;
import io.github.devPesto.townyCore.expansions.TownyExpansionManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

@Getter
public final class TownyCore extends JavaPlugin {
	private Config configuration;
	private TownyExpansionManager expansions;
	private ProtocolManager protocol;
	private BukkitCommandHandler commandManager;

	@Override
	public void onEnable() {
		this.configuration = new Config(this, "config.yml");
		this.expansions = new TownyExpansionManager(this);
		this.commandManager = BukkitCommandHandler.create(this);

        commandHandler.registerBrigadier();
        commandHandler.setMessagePrefix(getName());

		commandManager.registerBrigadier();
		commandManager.setMessagePrefix(getName());

		expansions.registerAllExpansions();
	}

	@Override
	public void onDisable() {

	}

	public boolean isSiegeWarEnabled() {
		return isPluginEnabled("SiegeWar");
	}

    public boolean isPluginEnabled(String name) {
        return Bukkit.getPluginManager().getPlugin(name) != null;
    }
}
