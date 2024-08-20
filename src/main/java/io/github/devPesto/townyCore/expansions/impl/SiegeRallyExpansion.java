package io.github.devPesto.townyCore.expansions.impl;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.commands.SiegeRallyCommand;
import io.github.devPesto.townyCore.expansions.TownyExpansion;
import io.github.devPesto.townyCore.listener.SiegeRallyListener;
import io.github.devPesto.townyCore.manager.SiegeRallyManager;
import org.bukkit.event.HandlerList;

public class SiegeRallyExpansion extends TownyExpansion {
    private final SiegeRallyManager manager;
    private final SiegeRallyListener listener;

    public SiegeRallyExpansion() {
        super("Rallies", "Apollo-Bukkit", "SiegeWar");
        this.manager = new SiegeRallyManager();
        this.listener = new SiegeRallyListener(manager);
    }

    @Override
    public void registerCommands(TownyCore plugin) {
        plugin.getCommandHandler().register(new SiegeRallyCommand(manager));
    }

    @Override
    protected void registerListeners(TownyCore plugin) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void register(TownyCore plugin) {
        super.register(plugin);
        manager.registerRallies();
    }

    @Override
    protected void unregisterListeners(TownyCore plugin) {
        HandlerList.unregisterAll(listener);
    }
}
