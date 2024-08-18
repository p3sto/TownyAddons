package io.github.devPesto.townyCore.listener;

import com.gmail.goosius.siegewar.events.SiegeWarStartEvent;
import io.github.devPesto.townyCore.manager.SiegeRallyManager;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class SiegeRallyListener implements Listener {
    private final SiegeRallyManager manager;

    @EventHandler()
    public void onSiegeBegin(SiegeWarStartEvent event) {
        manager.registerRally(event.getSiege());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        manager.addViewer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        manager.removeViewer(event.getPlayer());
    }

}
