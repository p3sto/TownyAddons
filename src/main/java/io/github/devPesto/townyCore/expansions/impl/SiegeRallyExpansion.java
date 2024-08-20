package io.github.devPesto.townyCore.expansions.impl;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.commands.SiegeRallyCommand;
import io.github.devPesto.townyCore.expansions.TownyExpansion;
import io.github.devPesto.townyCore.listener.SiegeRallyListener;
import io.github.devPesto.townyCore.manager.SiegeRallyManager;

public class SiegeRallyExpansion extends TownyExpansion {
    private final SiegeRallyManager manager;

    public SiegeRallyExpansion() {
        super("Rallies", "Apollo-Bukkit", "SiegeWar");
        this.manager = new SiegeRallyManager();
    }

    @Override
    public void registerCommands(TownyCore plugin) {
        plugin.getCommandHandler().register(new SiegeRallyCommand(manager));
    }

    @Override
    public void register(TownyCore plugin) {
        super.register(plugin);
        manager.registerRallies();
    }
}
