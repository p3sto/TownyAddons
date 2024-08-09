package io.github.devPesto.townyCore;

import lombok.Getter;

public abstract class TownyExpansion {
	private @Getter	final String name;

	public TownyExpansion(String name) {
		this.name = name;
	}

	public void registerListeners(TownyCore plugin) {

	}

	public void registerCommands(TownyCore plugin) {

	}
}
