package io.github.devPesto.townyCore.config;

import lombok.Getter;

public enum ConfigNode {
	EXPANSION_ENABLE_MINER_KIT("expansions.enable-miner-kit"),
	EXPANSION_ENABLE_OC_SOUNDS("expansions.enable-old-combat-sounds"),
	EXPANSION_ENABLE_RALLIES("expansions.lunar-integrations.enable-rallies"),
	;

	private final @Getter String path;

	ConfigNode(String path) {
		this.path = path;
	}
}
