package io.github.devPesto.townyCore;

import io.github.devPesto.townyCore.commands.TownyCoreCommand;
import io.github.devPesto.townyCore.config.impl.Locale;
import io.github.devPesto.townyCore.config.impl.Settings;
import io.github.devPesto.townyCore.util.ApolloUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.process.MessageSender;

public final class TownyCore extends JavaPlugin {
	private Settings configuration;
	private Locale messaging;
	private TownyExpansionManager expansions;
	private Lamp<BukkitCommandActor> commandHandler;
	private static TownyCore instance;

	@Override
	public void onEnable() {
		instance = this;
		this.configuration = new Settings(this);
		this.messaging = new Locale(this);
		this.expansions = new TownyExpansionManager(this);

		Lamp.Builder<BukkitCommandActor> builder = BukkitLamp.builder(this);
		if (expansions.isApolloRequired())
			ApolloUtil.initialize(builder);

		MessageSender<BukkitCommandActor, String> sender = (a, m) ->
				a.reply(messaging.getPrefix()
						.append(Component.space())
						.append(messaging.parse(m))
				);

		builder.defaultErrorSender(sender);
		builder.defaultMessageSender(sender);
		this.commandHandler = builder.build();

		expansions.registerExpansions();
		commandHandler.register(new TownyCoreCommand(this));
	}

	@Override
	public void onDisable() {
		if (commandHandler != null)
			commandHandler.unregisterAllCommands();
	}

	public boolean isPluginEnabled(String name) {
		return Bukkit.getPluginManager().getPlugin(name) != null;
	}

	public static TownyCore getInstance() {
		return instance;
	}

	public Lamp<BukkitCommandActor> getCommandHandler() {
		return commandHandler;
	}

	public Locale getLocale() {
		return messaging;
	}

	public Settings getSettings() {
		return configuration;
	}

	public TownyExpansionManager getExpansionManager() {
		return expansions;
	}

	public void registerEvents(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}
}
