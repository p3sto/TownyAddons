package io.github.devPesto.townyCore.expansions.impl;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.commands.RallyCommand;
import io.github.devPesto.townyCore.expansions.TownyExpansion;
import io.github.devPesto.townyCore.util.ApolloUtil;
import io.github.devPesto.townyCore.util.RallyManager;

public class RallyExpansion extends TownyExpansion {
    private final RallyManager manager;

    public RallyExpansion() {
        super("Rallies", "Apollo-Bukkit", "SiegeWar");
        this.manager = new RallyManager();
    }

    @Override
    public void registerCommands(TownyCore plugin) {
        ApolloUtil.registerApolloResolver(plugin);
        plugin.getCommandHandler().register(new RallyCommand(manager));
    }

    @Override
    public void register(TownyCore plugin) {
        super.register(plugin);
        manager.registerRallies();
    }
}
