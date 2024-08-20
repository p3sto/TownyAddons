package io.github.devPesto.townyCore.config.impl;

import io.github.devPesto.townyCore.config.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PluginNode implements Node {
	EXPANSION_ENABLE_MINER_KIT("expansions.enable-miner-kit"),
	EXPANSION_ENABLE_OC_SOUNDS("expansions.enable-old-combat-sounds"),
	EXPANSION_ENABLE_RALLIES("expansions.lunar-integrations.enable-rallies"),
	;

	private final String path;
}
