package com.comphenix.xp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.comphenix.xp.Action;

/**
 * Allows us to reward experience a tick later. This is necessary due to 
 * limitations in the Bukkit API.
 * 
 * @author Kristian
 */
public interface RewardableAction {

	public void performAction(Player player, ItemStack item, Action action, int count);
}
