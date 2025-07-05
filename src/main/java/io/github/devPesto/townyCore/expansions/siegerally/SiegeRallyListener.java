package io.github.devPesto.townyCore.expansions.siegerally;

import com.gmail.goosius.siegewar.events.SiegeWarStartEvent;
import io.github.devPesto.townyCore.objects.LunarPlayer;
import io.github.devPesto.townyCore.util.ApolloUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SiegeRallyListener implements Listener {
	private final SiegeRallyManager manager;

	public SiegeRallyListener(SiegeRallyManager manager) {
		this.manager = manager;
	}

	@EventHandler
	public void onSiegeBegin(SiegeWarStartEvent event) {
		manager.registerRally(event.getSiege());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		LunarPlayer player = ApolloUtil.getLunarPlayer(event.getPlayer());
		if (player != null)
			manager.addViewer(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		LunarPlayer player = ApolloUtil.getLunarPlayer(event.getPlayer());
		if (player != null)
			manager.removeViewer(player);
	}
}
