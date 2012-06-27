package com.comphenix.xp.rewards;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.comphenix.xp.Debugger;

public class ItemRewardListener implements Listener {

	private static final int soundRadius = 5;
	
	private HashMap<UUID, Integer> queue = new HashMap<UUID, Integer>();
	private RewardService reward;
	private Debugger logger;
	
	public ItemRewardListener(Debugger logger) {
		this.logger = logger;
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		
		Item item = event.getItem();
		Player player = event.getPlayer();
		
		if (item != null && player != null) {
			
			UUID id = item.getUniqueId();
			Integer amount = queue.get(id);
			
			logger.printDebug(this, "Picked up %id.", id);
			
			if (amount != null) {
				event.setCancelled(true);
				reward.reward(player, amount);
				
				queue.remove(id);
				item.remove();
				
				// Just play a sound
				player.getWorld().playEffect(item.getLocation(), Effect.CLICK1, soundRadius);
				
				// Replaced content
				logger.printDebug(this, "Gave player %s currency instead of item %s (%s).", 
						player.getName(), item.getItemStack(), id);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onItemDespawnEvent(ItemDespawnEvent event) {
		
		Item item = event.getEntity();
		
		// Remove ID too
		if (item != null) {
			queue.remove(item.getUniqueId());
		}
	}
	
	/**
	 * Marks the given object for granting economy rewards.
	 * @param item - item to mark.
	 * @param amount - the amount of currency to give.
	 */
	public void pinReward(Item item, int amount) {
		
		if (item != null) {
		
			// Enqueue this future reward
			queue.put(item.getUniqueId(), amount);
			
			logger.printDebug(this, "Pin reward %s to %d currency.", item.getUniqueId(), amount);
		}
	}
	
	public RewardService getReward() {
		return reward;
	}

	public void setReward(RewardService reward) {
		this.reward = reward;
	}
}
