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
	 * @param item - item that caused this action.
	 * @param action - action to perform.
	 * @param count - number of times to perform the action.
	 * @return TRUE if the player can be rewarded, FALSE otherwise.
	 */
	public boolean canPerform(Player player, ItemStack item, Action action, int count);
}
