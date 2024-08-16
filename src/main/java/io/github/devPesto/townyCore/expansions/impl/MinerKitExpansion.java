package io.github.devPesto.townyCore.expansions.impl;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.expansions.TownyExpansion;
import io.github.devPesto.townyCore.listener.MinerKitListener;

public class MinerKitExpansion extends TownyExpansion {

	public MinerKitExpansion() {
		super("MinerKit");
	}

	@Override
	public void registerListeners(TownyCore plugin) {
		plugin.getServer().getPluginManager().registerEvents(new MinerKitListener(), plugin);
	}
}
