package io.github.devPesto.townyCore.expansions.messaging;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.TownyExpansion;

public class MessagingExpansion extends TownyExpansion {
	private final MessagingCommand command;

	public MessagingExpansion() {
		super("Messaging");
		this.command = new MessagingCommand();
	}

	@Override
	protected void registerCommands(TownyCore plugin) {
		plugin.getCommandHandler().register(command);
	}
}
