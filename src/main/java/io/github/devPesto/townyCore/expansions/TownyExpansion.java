package io.github.devPesto.townyCore.expansions;

import io.github.devPesto.townyCore.TownyCore;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Getter
public abstract class TownyExpansion {
    private final String name;
    private final String[] dependencies;

    public TownyExpansion(String name, String... dependencies) {
        this.name = name;
        this.dependencies = dependencies;
    }

    public void register(TownyCore plugin) throws MissingDependencyException {
        validateDependencies(plugin);
        registerPermissions(plugin);
        registerListeners(plugin);
        registerCommands(plugin);
    }


    protected void validateDependencies(TownyCore plugin) throws MissingDependencyException {
        List<String> missing = Arrays.stream(dependencies)
                .filter(Predicate.not(plugin::isPluginEnabled))
                .toList();

        if (!missing.isEmpty()) {
            String strList = String.join(",", missing);
            throw new MissingDependencyException("Could not register module. Missing dependencies: " + strList);
        }
    }

    protected void registerListeners(TownyCore plugin) {

    }

    protected void registerCommands(TownyCore plugin) {

    }

    protected void registerPermissions(TownyCore plugin) {

    }

    protected void unregisterListeners(TownyCore plugin) {

    }

    protected void unregisterCommands(TownyCore plugin) {

    }

    protected void unregisterPermissions(TownyCore plugin) {

    }

    public static class MissingDependencyException extends RuntimeException {

        public MissingDependencyException() {
            super();
        }

        public MissingDependencyException(String message) {
            super(message);
        }

        public MissingDependencyException(Throwable cause) {
            super(cause);
        }
    }
}
