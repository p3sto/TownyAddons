package io.github.devPesto.townyCore.expansions;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.Config;
import io.github.devPesto.townyCore.config.ConfigNode;
import io.github.devPesto.townyCore.expansions.impl.MinerKitExpansion;
import io.github.devPesto.townyCore.expansions.impl.OldCombatSoundsExpansion;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class TownyExpansionManager {
	private @Getter Map<String, TownyExpansion> expansionMap;
	private final TownyCore plugin;
	private final Logger logger;
	private final Config config;

	public TownyExpansionManager(TownyCore plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		this.config = plugin.getConfiguration();
		this.expansionMap = new HashMap<>();

		loadExpansionsFromConfig();
	}

	public void registerAllExpansions() {
		expansionMap.values().forEach(this::registerExpansion);
	}

//	public void registerExpansion(String name) {
//		TownyExpansion expansion = expansionMap.getOrDefault(name, null);
//		if (expansion != null) {
//			registerExpansion(expansion);
//		} else
//			logger.warning("Could not register expansion: " + name);
//	}

	public void registerExpansion(TownyExpansion expansion) {
		expansion.registerCommands(plugin);
		expansion.registerListeners(plugin);
		logger.info("Successfully registered expansion: " + expansion.getName());
	}

	private void loadExpansionsFromConfig() {
		// Miner Kit
		if (config.getBoolean(ConfigNode.EXPANSION_ENABLE_MINER_KIT))
			expansionMap.put("MinerKit", new MinerKitExpansion());

		// OldCombatSounds
		expansionMap.put("OldCombatSounds", new OldCombatSoundsExpansion());
	}
}
