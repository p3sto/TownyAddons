package io.github.devPesto.townyCore.expansions.messaging;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.impl.Locale;
import io.github.devPesto.townyCore.config.impl.Locale.Nodes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import javax.annotation.Syntax;
import java.util.*;
import java.util.concurrent.TimeUnit;

// TODO: Implement persistence to ignored and disabled states
public class MessagingCommand {
	private final Cache<UUID, UUID> cahce;
	private final Map<UUID, Set<UUID>> ignored;
	private final Set<UUID> disabled;
	private final Locale locale;

	public MessagingCommand() {
		this.cahce = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).build();
		this.ignored = new HashMap<>();
		this.disabled = new HashSet<>();
		this.locale = TownyCore.getInstance().getLocale();
	}

	@Command({"message", "msg", "whisper", "w", "tell", "t"})
	@Syntax("<target> <message>")
	@Description("Sends a private message to the target player")
	@CommandPermission(value = "townycore.command.message", defaultAccess = PermissionDefault.TRUE)
	public void message(Player sender, Player target, String message) {
		// Check if the person has messages disabled or has blocked sender
		UUID senderId = sender.getUniqueId();
		UUID targetId = target.getUniqueId();

		if (disabled.contains(targetId) || isIgnored(targetId, senderId)) {
			locale.sendMessage(sender, Nodes.MESSAGES_DISABLED, Map.of("%player%", target.getName()));
			return;
		}

		locale.sendMessage(target, Nodes.MESSAGES_FORMAT, false, Map.of(
				"%sender%", sender.getName(),
				"%target%", target.getName(),
				"%message%", message
		));
		cahce.put(senderId, targetId);
		cahce.put(targetId, senderId);
	}

	@Command({"reply", "r"})
	@Syntax("<message>")
	@Description("Replies to the player who last sent you a message")
	@CommandPermission(value = "townycore.command.reply", defaultAccess = PermissionDefault.TRUE)
	public void reply(Player sender, String message) {
		UUID targetId = cahce.getIfPresent(sender.getUniqueId());

		// Check if there's anyone to reply to
		if (targetId == null) {
			locale.sendMessage(sender, Nodes.MESSAGES_NO_REPLY);
			return;
		}

		// Check if the target is online
		Player target = Bukkit.getPlayer(targetId);
		if (target == null || !target.isOnline()) {
			locale.sendMessage(sender, Nodes.MESSAGES_OFFLINE_REPLY);
			return;
		}

		message(sender, target, message);
	}

	@Command({"ignore", "block"})
	@Syntax("<target>")
	@Description("Ignores receiving messages from the target player")
	@CommandPermission(value = "townycore.command.ignore", defaultAccess = PermissionDefault.TRUE)
	public void ignore(Player sender, Player target) {
		UUID senderId = sender.getUniqueId();
		UUID targetId = target.getUniqueId();

		Set<UUID> ids = ignored.computeIfAbsent(senderId, k -> new HashSet<>());
		Nodes node = ids.add(targetId) ? Nodes.MESSAGES_IGNORED_PLAYER : Nodes.MESSAGES_ALREADY_IGNORED;
		locale.sendMessage(sender, node, Map.of("%player%", target.getName()));
	}

	@Command({"unignore", "unblock"})
	@Syntax("<target>")
	@Description("Allows receiving messages from previously ignored players")
	@CommandPermission(value = "townycore.command.unignore", defaultAccess = PermissionDefault.TRUE)
	public void unignore(Player sender, Player target) {
		UUID senderId = sender.getUniqueId();
		UUID targetId = target.getUniqueId();

		boolean alreadyUnignored = !ignored.containsKey(senderId) || !ignored.get(senderId).remove(targetId);
		Nodes node = alreadyUnignored ? Nodes.MESSAGES_ALREADY_UNIGNORED : Nodes.MESSAGES_UNIGNORED_PLAYER;
		locale.sendMessage(sender, node, Map.of("%player%", target.getName()));
	}

	@Command("msgtoggle")
	@Description("Toggle message receiving")
	@CommandPermission(value = "townycore.command.msgtoggle", defaultAccess = PermissionDefault.TRUE)
	public void toggle(Player sender) {
		UUID id = sender.getUniqueId();
		if (disabled.remove(id)) {
			locale.sendMessage(sender, Nodes.MESSAGES_TOGGLED_ON);
			return;
		}

		disabled.add(id);
		locale.sendMessage(sender, Nodes.MESSAGES_TOGGLED_OFF);
	}

	private boolean isIgnored(UUID key, UUID blocked) {
		if (!ignored.containsKey(key))
			return false;

		return ignored.get(key).contains(blocked);
	}
}
