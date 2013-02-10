/*
 *  ExperienceMod - Bukkit server plugin for modifying the experience system in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.xp.rewards;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.comphenix.xp.Debugger;
import com.comphenix.xp.rewards.xp.CurrencyHolder;

public class ItemRewardListener implements Listener {

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
			
			if (amount != null) {
				// Add the item count too
				amount *= item.getItemStack().getAmount();
				CurrencyHolder currency = new CurrencyHolder(amount);
				
				event.setCancelled(true);
				reward.reward(player, currency);
				
				queue.remove(id);
				item.remove();
				
				if (logger != null) {
					// See if we in fact rewarded the player
					if (reward.canReward(player, currency)) {
						// Replaced content
						logger.printDebug(this, "Gave player %s currency instead of item %s (%s).", 
								player.getName(), item.getItemStack(), id);
					}
				}
				
				// Just play a sound
				player.getWorld().playSound(item.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
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
		}
	}
	
	public RewardService getReward() {
		return reward;
	}

	public void setReward(RewardService reward) {
		this.reward = reward;
	}
	
	// Cleanup dropped items on plugin disable
	// not the most elegant solution but it prevents players
	// getting weird items
	public void cleanupItems() {
		for (World world : Bukkit.getWorlds()) {
			for (Item item : world.getEntitiesByClass(Item.class)) {
				if (queue.containsKey(item.getUniqueId())) {
					item.remove();
				}
			}
		}
	}
}
