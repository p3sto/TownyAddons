package io.github.devPesto.townyCore.config.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.devPesto.townyCore.config.Config;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class MessageConfiguration extends Config {
    private final MiniMessage mm;

    public MessageConfiguration(Plugin plugin) {
        super(plugin, "messages.yml");
        this.mm = MiniMessage.miniMessage();
    }

    public String getLegacyPrefix() {
        return toLegacy(getString(MessageNode.PREFIX));
    }

    public Component getPrefix() {
        return mm.deserialize(getString(MessageNode.PREFIX));
    }

    public void sendMessage(Audience audience, MessageNode node) {
        sendMessage(audience, node, null);
    }

    public void sendMessage(Audience audience, MessageNode node, Map<String, String> replacements) {
        sendMessage(audience, node, true, replacements);
    }

    public void sendMessage(Audience audience, MessageNode node, boolean prefixed) {
        sendMessage(audience, node, prefixed, null);
    }

    /**
     * @param audience
     * @param node
     * @param args
     */
    public void sendMessage(Audience audience, MessageNode node, boolean prefixed, Map<String, String> replacements) {
        String result = "";
        if (prefixed)
            result += getString(MessageNode.PREFIX);

        result += getString(node);
        if (replacements != null && !replacements.isEmpty()) {
            for (Map.Entry<String, String> entry : replacements.entrySet())
                result = result.replaceAll(entry.getKey(), entry.getValue());
        }

        Component component = mm.deserialize(result);
        audience.sendMessage(component);
    }

    /**
     * Maps old color codes to
     */
    private static final BiMap<String, String> map = HashBiMap.create(Map.ofEntries(
                    Map.entry("&0", "<black>"),
                    Map.entry("&1", "<dark_blue>"),
                    Map.entry("&2", "<dark_green>"),
                    Map.entry("&3", "<dark_aqua>"),
                    Map.entry("&4", "<dark_red>"),
                    Map.entry("&5", "<dark_purple>"),
                    Map.entry("&6", "<gold>"),
                    Map.entry("&7", "<gray>"),
                    Map.entry("&8", "<dark_gray>"),
                    Map.entry("&9", "<blue>"),
                    Map.entry("&a", "<green>"),
                    Map.entry("&b", "<aqua>"),
                    Map.entry("&c", "<red>"),
                    Map.entry("&d", "<light_purple>"),
                    Map.entry("&e", "<yellow>"),
                    Map.entry("&f", "<white>")
            )
    );


    public Component toMiniMesage(String s) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            s = s.replaceAll(entry.getKey(), entry.getValue());
        }
        return mm.deserialize(s);
    }

    public String toLegacy(String s) {
        // Replace American and British versions of gray/grey
        s = s.replaceAll("grey>", "gray>");
        for (Map.Entry<String, String> entry : map.inverse().entrySet()) {
            s = s.replaceAll(entry.getKey(), entry.getValue());
        }
        return s;
    }
}
