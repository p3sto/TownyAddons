package io.github.devPesto.townyCore.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.github.devPesto.townyCore.TownyCore;
import org.bukkit.Sound;

public class OldCombatListener {
    private final ProtocolManager manager;
    private final PacketAdapter handler;

    public OldCombatListener(TownyCore plugin) {
        this.manager = ProtocolLibrary.getProtocolManager();
        this.handler = new PacketAdapter(plugin, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket().deepClone();
                final Sound sound = packet.getSoundEffects().read(0);
                switch (sound) {
                    case ENTITY_PLAYER_ATTACK_NODAMAGE:
                    case ENTITY_PLAYER_ATTACK_SWEEP:
                    case ENTITY_PLAYER_ATTACK_WEAK:
                    case ENTITY_PLAYER_ATTACK_STRONG:
                    case ENTITY_PLAYER_ATTACK_KNOCKBACK:
                        event.setCancelled(true);
                }
            }
        };
    }

    public void register() {
        manager.addPacketListener(handler);
    }

    public void unregister() {
        manager.removePacketListener(handler);
    }

}
