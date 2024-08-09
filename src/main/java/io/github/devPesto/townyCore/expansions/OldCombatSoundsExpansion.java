package io.github.devPesto.townyCore.expansions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.TownyExpansion;
import org.bukkit.Sound;

public class OldCombatSoundsExpansion extends TownyExpansion {

	public OldCombatSoundsExpansion() {
		super("OldCombatSounds");
	}

	@Override
	public void registerListeners(TownyCore plugin) {
		if (!plugin.isProtocolLibEnabled()) {
			// TODO: Move to messages.yml
			String message = String.format("Could not enable '%s' expansion. Missing dependency ProtocolLib", getName());
			plugin.getLogger().warning(message);
			return;
		}

		ProtocolManager manager = plugin.getProtocol();
		manager.addPacketListener(
				new PacketAdapter(plugin, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
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
				}
		);
	}
}
