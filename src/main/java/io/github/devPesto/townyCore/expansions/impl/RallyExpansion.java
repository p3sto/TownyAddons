package io.github.devPesto.townyCore.expansions.impl;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.commands.SiegeRallyCommand;
import io.github.devPesto.townyCore.expansions.TownyExpansion;
import io.github.devPesto.townyCore.util.ApolloUtil;
import io.github.devPesto.townyCore.manager.SiegeRallyManager;

public class RallyExpansion extends TownyExpansion {
    private final SiegeRallyManager manager;

    public RallyExpansion() {
        super("Rallies", "Apollo-Bukkit", "SiegeWar");
        this.manager = new SiegeRallyManager();
    }

    @Override
    public void registerCommands(TownyCore plugin) {
        ApolloUtil.registerApolloResolver(plugin);
        plugin.getCommandHandler().register(new SiegeRallyCommand(manager));
    }

    @Override
    public void register(TownyCore plugin) {
        super.register(plugin);
        manager.registerRallies();
    }
}
