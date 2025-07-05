package io.github.devPesto.townyCore.expansions.minerkit;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.TownyExpansion;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

// TODO: Add enable/disable messages. Investigate why event triggers on damage
public class MinerKitExpansion extends TownyExpansion {
    private final Listener listener;

    public MinerKitExpansion() {
        super("Miner Kit");
        this.listener = new MinerKitListener();
    }

    @Override
    public void registerListeners(TownyCore plugin) {
        plugin.registerEvents(listener);
    }

    @Override
    protected void unregisterListeners(TownyCore plugin) {
        HandlerList.unregisterAll(listener);
    }
}
