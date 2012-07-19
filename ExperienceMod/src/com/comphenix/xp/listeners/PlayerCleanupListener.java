package com.comphenix.xp.listeners;

import org.bukkit.entity.Player;

/**
 * Represents an object that need to clean up player references.
 * 
 * @author Kristian
 */
public interface PlayerCleanupListener {

	/**
	 * Removes a given player from being referenced by any preset node. Must be called when a player logs out.
	 * @param player - player to remove.
	 */
	public void removePlayerCache(Player player);
}
