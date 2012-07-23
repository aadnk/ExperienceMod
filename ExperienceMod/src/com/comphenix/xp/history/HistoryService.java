package com.comphenix.xp.history;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.comphenix.xp.extra.Service;

/**
 * Represents a store of previous player actions.
 * 
 * @author Kristian
 */
public interface HistoryService extends Service {

	/**
	 * Determines whether or not a block has been placed by a player.
	 * @param world - world the block belongs to.
	 * @param blockLocation - location of the block in question.
	 * @return TRUE if the given block was placed by a player, FALSE otherwise.
	 */
	public boolean hasPlayerHistory(World world, Location blockLocation) throws HistoryException;
	
	/**
	 * Determines whether or not a block has been placed by a player.
	 * @param block - block to test.
	 * @return TRUE if the given block was placed by a player, FALSE otherwise.
	 */
	public boolean hasPlayerHistory(Block block) throws HistoryException;
}
