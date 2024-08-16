package io.github.devPesto.townyCore.expansions.impl;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.expansions.TownyExpansion;
import io.github.devPesto.townyCore.listener.OldCombatListener;

public class OldCombatSoundsExpansion extends TownyExpansion {
    private static OldCombatListener listener;

    public OldCombatSoundsExpansion() {
        super("OldCombatSounds", "ProtocolLib");
    }

    @Override
    public void registerListeners(TownyCore plugin) {
        listener = getListener(plugin);
        listener.register();
    }

    @Override
    public void unregisterListeners(TownyCore plugin) {
        listener = getListener(plugin);
        listener.unregister();
    }

    private OldCombatListener getListener(TownyCore plugin) {
        if (listener == null) {
            listener = new OldCombatListener(plugin);
        }
        return listener;
    }
}
