package io.github.devPesto.townyCore.expansions.teams;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.TownyExpansion;

public class TeamsExpansion extends TownyExpansion {
	private final TeamsManager manager;
	private final TeamsListener listener;

	public TeamsExpansion() {
		super("Teams", "Apollo-Bukkit");
		this.manager = new TeamsManager();
		this.listener = new TeamsListener();
	}

	@Override
	protected void registerListeners(TownyCore plugin) {
		plugin.registerEvents(new TeamsListener());
	}
}
