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
		String config = getString(LangNodes.PREFIX);
		this.prefix = !config.isBlank() ? mm.deserialize(config) : Component.empty();
	}

	public void sendMessage(Audience audience, LangNodes node) {
		sendMessage(audience, node, null);
	}

	public void sendMessage(Audience audience, LangNodes node, Map<String, String> replacements) {
		sendMessage(audience, node, true, replacements);
	}

	public void sendMessage(Audience audience, LangNodes node, boolean prefixed) {
		sendMessage(audience, node, prefixed, null);
	}

	/**
	 * @param audience
	 * @param node
	 * @param replacements
	 */
	public void sendMessage(Audience audience, LangNodes node, boolean prefixed, Map<String, String> replacements) {
		String result = "";
		if (prefixed)
			result += getString(LangNodes.PREFIX);

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

	public enum LangNodes implements Node {
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

		LangNodes(String path) {
			this.path = path;
		}

		@Override
		public String getPath() {
			return path;
		}
	}
}
