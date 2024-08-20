package io.github.devPesto.townyCore.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public abstract class Config {
	private final String filePath;
	private final Logger logger;
	protected @Getter YamlDocument config;

	public Config(Plugin plugin, String filePath) {
		this.filePath = filePath;
		this.logger = plugin.getLogger();
		this.config = createOrLoadConfig(plugin, filePath);
	}

	private YamlDocument createOrLoadConfig(Plugin plugin, String fileName) {
		YamlDocument config = null;
		try (InputStream is = plugin.getResource(fileName)) {
			if (is != null)
				config = YamlDocument.create(new File(plugin.getDataFolder(), fileName), is,
						GeneralSettings.DEFAULT,
						LoaderSettings.builder().setAutoUpdate(true).build(),
						DumperSettings.DEFAULT,
						UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version"))
								.build());
		} catch (IOException e) {
			logger.severe(e.getMessage());
		}

		return config;
	}


	public void reload(Plugin plugin) {
		try {
			if(!config.reload()) {
				this.config = createOrLoadConfig(plugin, filePath);
			}
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}

	public boolean getBoolean(Node node) {
		return config.getBoolean(node.getPath(), false);
	}

	public int getInt(Node node) {
		return config.getInt(node.getPath(), 0);
	}

	public long getLong(Node node) {
		return config.getLong(node.getPath(), 0L);
	}

	public float getFloat(Node node) {
		return config.getFloat(node.getPath(), 0.0f);
	}

	public double getDouble(Node node) {
		return config.getDouble(node.getPath(), 0.0);
	}

	public String getString(Node node) {
		return config.getString(node.getPath(), "");
	}
}