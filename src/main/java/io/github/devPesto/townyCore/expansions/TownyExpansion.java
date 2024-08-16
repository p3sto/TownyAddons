package io.github.devPesto.townyCore.expansions;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.objects.MissingDependencyException;
import lombok.Getter;

@Getter
public abstract class TownyExpansion {
	private final String name;
	private final String[] dependencies;

	public TownyExpansion(String name, String ... dependencies) {
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

	public void registerCommands(TownyCore plugin) {

	}

	public void registerPermissions(TownyCore plugin) {

	}
}
