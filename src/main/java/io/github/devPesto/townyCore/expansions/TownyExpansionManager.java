package io.github.devPesto.townyCore.expansions;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.Config;
import io.github.devPesto.townyCore.config.ConfigNode;
import io.github.devPesto.townyCore.expansions.impl.MinerKitExpansion;
import io.github.devPesto.townyCore.expansions.impl.OldCombatSoundsExpansion;
import io.github.devPesto.townyCore.expansions.impl.SiegeRallyExpansion;
import static io.github.devPesto.townyCore.expansions.TownyExpansion.MissingDependencyException;
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
        this.expansionMap = loadExpansions();
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

    private Map<String, TownyExpansion> loadExpansions() {
        // Miner Kit
        Map<String, TownyExpansion> map = new HashMap<>();
        if (config.getBoolean(ConfigNode.EXPANSION_ENABLE_MINER_KIT))
            map.put("MinerKit", new MinerKitExpansion());

        // OldCombatSounds
        if (config.getBoolean(ConfigNode.EXPANSION_ENABLE_OC_SOUNDS))
            map.put("OldCombatSounds", new OldCombatSoundsExpansion());

        // Rallies
        if (config.getBoolean(ConfigNode.EXPANSION_ENABLE_RALLIES))
            map.put("SiegeRally", new SiegeRallyExpansion());

        return map;
    }
}
