package io.github.devPesto.townyCore;

import io.github.devPesto.townyCore.config.impl.Settings;
import io.github.devPesto.townyCore.expansions.minerkit.MinerKitExpansion;
import io.github.devPesto.townyCore.expansions.oldcombatsounds.OldCombatSoundsExpansion;
import io.github.devPesto.townyCore.expansions.siegerally.SiegeRallyExpansion;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static io.github.devPesto.townyCore.TownyExpansion.MissingDependencyException;

public class TownyExpansionManager {
	private final Map<String, TownyExpansion> expansionMap;
	private final TownyCore plugin;
	private final Logger logger;
	private final Settings settings;

	public TownyExpansionManager(TownyCore plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		this.settings = plugin.getSettings();
		this.expansionMap = loadExpansions();
	}

	public Map<String, TownyExpansion> getExpansions() {
		return expansionMap;
	}

	public void registerExpansions() {
		logger.info("================ [TownyCore] ================");
		expansionMap.values().forEach(expansion -> {
			try {
				expansion.register(plugin);
				logger.info("Successfully registered module: " + expansion.getName());
			} catch (MissingDependencyException e) {
				logger.severe(e.getMessage());
			}
		});
		logger.info("-------------------------------------------");
	}

	public boolean isApolloRequired() {
		return expansionMap.values()
				.stream()
				.flatMap(e -> e.getDependencies().stream())
				.anyMatch(d -> d.equalsIgnoreCase("apollo-bukkit"));
	}

	private Map<String, TownyExpansion> loadExpansions() {
		// Miner Kit
		Map<String, TownyExpansion> map = new HashMap<>();
		if (settings.enableMinerKit())
			map.put("MinerKit", new MinerKitExpansion());

		// OldCombatSounds
		if (settings.enableOldCombatSounds())
			map.put("OldCombatSounds", new OldCombatSoundsExpansion());

		// Rallies
		if (settings.enableRallies())
			map.put("SiegeRally", new SiegeRallyExpansion());

		return map;
	}
}
