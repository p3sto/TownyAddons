package io.github.devPesto.townyCore.expansions.impl;

import com.gmail.goosius.siegewar.utils.SiegeWarDistanceUtil;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.expansions.TownyExpansion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ApolloExpansion extends TownyExpansion {

    public ApolloExpansion() {
        super("LunarTools");
    }

    @Override
    public void registerListeners(TownyCore plugin) {

    }

    private static class ApolloListener implements Listener {

        @EventHandler
        public void onSiegeZoneEnter(PlayerMoveEvent event) {
            Player player = event.getPlayer();
            if (!event.hasChangedBlock()) return;
            if(!SiegeWarDistanceUtil.isPlayerRegisteredToActiveSiegeZone(player)) return;


        }
    }


}
