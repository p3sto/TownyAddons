package io.github.devPesto.townyCore.config.impl;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.Config;

public class PluginConfiguration extends Config {

    public PluginConfiguration(TownyCore plugin) {
        super(plugin, "config.yml");
    }
}
