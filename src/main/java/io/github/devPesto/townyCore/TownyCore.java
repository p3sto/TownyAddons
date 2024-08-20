package io.github.devPesto.townyCore;

import io.github.devPesto.townyCore.config.impl.MessageConfiguration;
import io.github.devPesto.townyCore.config.impl.PluginConfiguration;
import io.github.devPesto.townyCore.expansions.TownyExpansionManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

@Getter
public final class TownyCore extends JavaPlugin {
    private PluginConfiguration configuration;
    private MessageConfiguration messaging;
    private TownyExpansionManager expansions;
    private BukkitCommandHandler commandHandler;
    private @Getter static TownyCore instance;

    @Override
    public void onEnable() {
        instance = this;
        this.configuration = new PluginConfiguration(this);
        this.messaging = new MessageConfiguration(this);
        this.expansions = new TownyExpansionManager(this);
        this.commandHandler = BukkitCommandHandler.create(this);

        commandHandler.registerBrigadier();
        commandHandler.setMessagePrefix(getName());

        expansions.registerExpansions();
    }

    @Override
    public void onDisable() {
        if (commandHandler != null)
            commandHandler.unregisterAllCommands();
    }

    public boolean isPluginEnabled(String name) {
        return Bukkit.getPluginManager().getPlugin(name) != null;
    }
}
