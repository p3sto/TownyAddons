package io.github.devPesto.townyCore.config.impl;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.Configuration;
import io.github.devPesto.townyCore.config.Node;

public class Settings extends Configuration {

    public Settings(TownyCore plugin) {
        super(plugin, "config.yml");
    }

    public boolean enableMinerKit() {
        return getBoolean(Nodes.EXPANSION_ENABLE_MINER_KIT);
    }

    public boolean enableOldCombatSounds() {
        return getBoolean(Nodes.EXPANSION_ENABLE_OC_SOUNDS);
    }

    public boolean enableRallies() {
        return getBoolean(Nodes.EXPANSION_ENABLE_RALLIES);
    }

    public boolean enableMessaging() {
        return getBoolean(Nodes.EXPANSION_ENABLE_MESSAGING);
    }

    public boolean enableTeamIndicator() {
        return getBoolean(Nodes.EXPANSION_ENABLE_TEAM_INDICATOR);
    }

    public boolean enableBannerIndicator() {
        return getBoolean(Nodes.EXPANSION_ENABLE_BANNER_INDICATOR);
    }

    public enum Nodes implements Node {
        EXPANSION_ENABLE_MINER_KIT("expansions.enable-miner-kit"),
        EXPANSION_ENABLE_OC_SOUNDS("expansions.enable-old-combat-sounds"),
        EXPANSION_ENABLE_RALLIES("expansions.enable-rallies"),
        EXPANSION_ENABLE_MESSAGING("expansions.enable-messaging"),
        EXPANSION_ENABLE_TEAM_INDICATOR("expansions.enable-team-indicator"),
        EXPANSION_ENABLE_BANNER_INDICATOR("expansions.enable-banner-indicator"),
        ;

        private final String path;

        Nodes(String path) {
            this.path = path;
        }

        @Override
        public String getPath() {
            return path;
        }
    }
}
