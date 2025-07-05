package io.github.devPesto.townyCore.expansions.siegerally;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.TownyExpansion;
import io.github.devPesto.townyCore.config.impl.Locale;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class SiegeRallyExpansion extends TownyExpansion {
    private final SiegeRallyManager manager;
    private final Locale messaging;
    private final Listener listener;

    public SiegeRallyExpansion() {
        super("Rallies", "Apollo-Bukkit", "SiegeWar");
        this.manager = new SiegeRallyManager();
        this.messaging = TownyCore.getInstance().getLocale();
        this.listener = new SiegeRallyListener(manager);
    }

    @Override
    public void registerCommands(TownyCore plugin) {
        plugin.getCommandHandler().register(new SiegeRallyCommand(manager));
    }

    @Override
    protected void registerListeners(TownyCore plugin) {
        plugin.registerEvents(listener);
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
