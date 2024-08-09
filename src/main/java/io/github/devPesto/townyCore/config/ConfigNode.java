package io.github.devPesto.townyCore.config;

import lombok.Getter;

public enum ConfigNode {
	EXPANSION_ENABLE_MINER_KIT("expansions.enable-miner-kit")
	;

	private final @Getter String path;

	ConfigNode(String path) {
		this.path = path;
	}
}
