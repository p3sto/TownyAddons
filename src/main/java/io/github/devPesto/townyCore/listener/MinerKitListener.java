package io.github.devPesto.townyCore.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Map;
import java.util.Set;

import static org.bukkit.potion.PotionEffect.INFINITE_DURATION;
import static org.bukkit.potion.PotionEffectType.*;

// TODO: Add enable/disable messages. Investigate why event triggers on damage
public class MinerKitListener implements Listener {

	/**
	 * Check if the player should have the effects added on join
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		checkAndApplyEffects(event.getPlayer());
	}

	/**
	 * Clear potion effects when the player leaves
	 */
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		removeEffects(event.getPlayer());
	}

	/**
	 * Handles armor change events to determine if the player
	 *
	 * @param event {@link PlayerArmorChangeEvent} instance
	 */
	@EventHandler(ignoreCancelled = true)
	public void onArmorEquip(PlayerArmorChangeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getNewItem();

		// Removing effects if armor is removed or changed to other armor
		if (!minerKitMap.containsKey(item.getType()))
			removeEffects(player);
		else
			checkAndApplyEffects(player);
	}
	/**
	 * Checks if player has all iron armor equipped. If they do, apply effects
	 */
	private void checkAndApplyEffects(Player player) {
		for (EquipmentSlot slot : minerKitMap.values()) {
			ItemStack armor = player.getEquipment().getItem(slot);
			if (!minerKitMap.containsKey(armor.getType())) {
				return;
			}
		}
		player.addPotionEffects(kitEffects);
	}

	/**
	 * Clears all infinite potion effects
	 */
	private void removeEffects(Player player) {
		kitEffects.forEach(effect -> player.removePotionEffect(effect.getType()));
	}

	private static final Map<Material, EquipmentSlot> minerKitMap = Map.of(
			Material.IRON_HELMET, EquipmentSlot.HEAD,
			Material.IRON_CHESTPLATE, EquipmentSlot.CHEST,
			Material.IRON_LEGGINGS, EquipmentSlot.LEGS,
			Material.IRON_BOOTS, EquipmentSlot.FEET
	);

	private static final Set<PotionEffect> kitEffects = Set.of(
			new PotionEffect(HASTE, INFINITE_DURATION, 0, false, false),
			new PotionEffect(NIGHT_VISION, INFINITE_DURATION, 0, false, false),
			new PotionEffect(FIRE_RESISTANCE, INFINITE_DURATION, 0, false, false));

}
