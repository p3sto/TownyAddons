package io.github.devPesto.townyCore.listener;

import com.palmergames.bukkit.towny.event.time.NewShortTimeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RallyListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onShortTime(NewShortTimeEvent event) {

    }
}
