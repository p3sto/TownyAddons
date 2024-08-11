package io.github.devPesto.townyCore.expansions;

import io.github.devPesto.townyCore.TownyCore;
import lombok.Getter;

@Getter
public abstract class TownyExpansion {
	private final String name;
	private final String[] dependencies;

	public TownyExpansion(String name, String ... dependencies) {
		this.name = name;
		this.dependencies = dependencies;
	}

	public void registerListeners(TownyCore plugin) {

	}

	public void registerCommands(TownyCore plugin) {

	}

	public void registerPermissions(TownyCore plugin) {

	}
}
