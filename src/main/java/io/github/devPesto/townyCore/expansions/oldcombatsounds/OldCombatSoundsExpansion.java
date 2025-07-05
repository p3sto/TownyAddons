package io.github.devPesto.townyCore.expansions.oldcombatsounds;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.TownyExpansion;
import org.bukkit.Sound;
import org.bukkit.event.Listener;

public class OldCombatSoundsExpansion extends TownyExpansion implements Listener {
    private final ProtocolManager manager;
    private final PacketAdapter listener;

    public OldCombatSoundsExpansion() {
        super("OldCombatSounds", "ProtocolLib");

        this.manager = ProtocolLibrary.getProtocolManager();
        this.listener = new PacketAdapter(TownyCore.getInstance(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
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

    @Override
    public void registerListeners(TownyCore plugin) {
        manager.addPacketListener(listener);
    }

    @Override
    public void unregisterListeners(TownyCore plugin) {
        manager.removePacketListener(listener);
    }
}
