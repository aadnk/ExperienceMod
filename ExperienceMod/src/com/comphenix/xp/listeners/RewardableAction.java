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

	/**
	 * Performs a given action for a player.
	 * @param player - player to perform action.
	 * @param item - item that caused this action.
	 * @param action - the action (reward) itself.
	 * @param count - number of times to perform it.
	 */
	public void performAction(Player player, ItemStack item, Action action, int count);
	
	/**
	 * Determine whether or not the given player can be rewarded or penalized by the given action and item count.
	 * @param player - player to test against.
	 * @param action - action to perform.
	 * @param count - number of times to perform the action.
	 * @return TRUE if the player can be rewarded, FALSE otherwise.
	 */
	public boolean canPerform(Player player, Action action, int count);
}
