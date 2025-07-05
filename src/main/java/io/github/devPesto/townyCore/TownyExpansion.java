package io.github.devPesto.townyCore;

import java.util.List;
import java.util.function.Predicate;

public abstract class TownyExpansion {
    private final String name;
    private final List<String> dependencies;

    public TownyExpansion(String name, String... dependencies) {
        this.name = name;
        this.dependencies = List.of(dependencies);
    }

    public void register(TownyCore plugin) throws MissingDependencyException {
        List<String> missing = dependencies.stream()
                .filter(Predicate.not(plugin::isPluginEnabled))
                .toList();

        if (!missing.isEmpty()) {
            String strList = String.join(",", missing);
            throw new MissingDependencyException("Could not register module. Missing dependencies: " + strList);
        }

        registerPermissions(plugin);
        registerListeners(plugin);
        registerCommands(plugin);
    }

    public String getName() {
        return name;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    protected void registerListeners(TownyCore plugin) {}

    protected void registerCommands(TownyCore plugin) {}

    protected void registerPermissions(TownyCore plugin) {}

    protected void unregisterListeners(TownyCore plugin) {}

    protected void unregisterCommands(TownyCore plugin) {}

    protected void unregisterPermissions(TownyCore plugin) {}

    public static class MissingDependencyException extends RuntimeException {

        public MissingDependencyException(String message) {
            super(message);
        }
    }
}
