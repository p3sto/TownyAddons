package io.github.devPesto.townyCore.config.impl;

import io.github.devPesto.townyCore.config.Configuration;
import io.github.devPesto.townyCore.config.Node;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class Locale extends Configuration {
	private final MiniMessage mm = MiniMessage.miniMessage();
	private Component prefix;

	public Locale(Plugin plugin) {
		super(plugin, "locale.yml");
		reloadPrefix();
	}

	public Component getPrefix() {
		return prefix;
	}

	private void reloadPrefix() {
		String config = getString(Nodes.PREFIX);
		this.prefix = !config.isBlank() ? mm.deserialize(config) : Component.empty();
	}

	public void sendMessage(Audience audience, Nodes node) {
		sendMessage(audience, node, null);
	}

	public void sendMessage(Audience audience, Nodes node, Map<String, String> replacements) {
		sendMessage(audience, node, true, replacements);
	}

	public void sendMessage(Audience audience, Nodes node, boolean prefixed) {
		sendMessage(audience, node, prefixed, null);
	}

	/**
	 * @param audience
	 * @param node
	 * @param replacements
	 */
	public void sendMessage(Audience audience, Nodes node, boolean prefixed, Map<String, String> replacements) {
		String result = "";
		if (prefixed)
			result += getString(Nodes.PREFIX);

		result += getString(node);
		if (replacements != null && !replacements.isEmpty()) {
			for (Map.Entry<String, String> entry : replacements.entrySet())
				result = result.replaceAll(entry.getKey(), entry.getValue());
		}

		Component component = mm.deserialize(result);
		audience.sendMessage(component);
	}

	public Component parse(String message) {
		return mm.deserialize(message);
	}

	@Override
	public void reload(Plugin plugin) {
		super.reload(plugin);
		reloadPrefix();
	}

	public enum Nodes implements Node {
		PREFIX("prefix"),

		RALLY_INACTIVE_SIEGE("rally.inactive-siege"),
		RALLY_NON_PARTICIPANT("rally.non-participant"),
		RALLY_ENABLED("rally.enabled"),
		RALLY_DISABLED("rally.disabled"),
		RALLY_ALREADY_ENABLED("rally.already-enabled"),
		RALLY_ALREADY_DISABLED("rally.already-disabled"),
		RALLY_UPDATED_LOCATION("rally.updated-location"),

		MESSAGES_FORMAT("messages.format"),
		MESSAGES_NO_REPLY("messages.no-reply"),
		MESSAGES_OFFLINE_REPLY("messages.offline-reply"),
		MESSAGES_DISABLED("messages.disabled"),
		MESSAGES_TOGGLED_ON("messages.toggled-on"),
		MESSAGES_TOGGLED_OFF("messages.toggled-off"),
		MESSAGES_IGNORED_PLAYER("messages.ignored-player"),
		MESSAGES_ALREADY_IGNORED("messages.already-ignored"),
		MESSAGES_UNIGNORED_PLAYER("messages.unignored-player"),
		MESSAGES_ALREADY_UNIGNORED("messages.already-unignored"),

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
