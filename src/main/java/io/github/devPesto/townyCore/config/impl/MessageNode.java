package io.github.devPesto.townyCore.config.impl;

import io.github.devPesto.townyCore.config.Node;

public enum MessageNode implements Node {
    PREFIX("prefix"),

    RALLY_INACTIVE_SIEGE("rally.inactive-siege"),
    RALLY_NON_PARTICIPANT("rally.non-participant"),
    RALLY_ENABLED("rally.enabled"),
    RALLY_DISABLED("rally.disabled"),
    RALLY_ALREADY_ENABLED("rally.already-enabled"),
    RALLY_ALREADY_DISABLED("rally.already-disabled"),
    RALLY_UPDATED_LOCATION("rally.updated-location"),
    ;

    private final String path;

    MessageNode(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }
}
