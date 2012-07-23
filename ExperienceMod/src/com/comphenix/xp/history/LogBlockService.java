package com.comphenix.xp.history;

import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.diddiz.LogBlock.BlockChange;
import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;
import de.diddiz.LogBlock.QueryParams.Order;

public class LogBlockService implements HistoryService {

	public static final String NAME = "LOGBLOCK";
	
	private LogBlock logBlock;
	
	/**
	 * Attempts to create a log block service. 
	 * @param manager - local plugin manager.
	 * @return A LogBlockService if successful, otherwise NULL.
	 */
	public static LogBlockService create(PluginManager manager) {
		
		Plugin plugin = manager.getPlugin("LogBlock");
		
		if (plugin != null) {
			return new LogBlockService((LogBlock) plugin);
		} else {
			return null;
		}
	}
	
	/**
	 * Determines if the log block service can be loaded.
	 * @param manager - local plugin manager.
	 * @return TRUE if it can be loaded, FALSE otherwise.
	 */
	public static boolean exists(PluginManager manager) {
		return create(manager) != null;
	}
	
	public LogBlockService(LogBlock logBlock) {
		this.logBlock = logBlock;
	}
	
	@Override
	public String getServiceName() {
		return NAME;
	}


	@Override
	public boolean hasPlayerHistory(Block block) throws HistoryException {
		return hasPlayerHistory(block.getWorld(), block.getLocation());
	}
	
	@Override
	public boolean hasPlayerHistory(World world, Location blockLocation) throws HistoryException {

		QueryParams params = new QueryParams(logBlock);
		
		// Now, see if any players have modified this block 
		params.setLocation(blockLocation);
		params.bct = BlockChangeType.CREATED;
		params.limit = 1;
		params.radius = 0;
		params.world = world;
		params.needType = true;
		params.needPlayer = true;
		params.order = Order.DESC;
		
		try {
			// This should be the most recent change
			for (BlockChange bc : logBlock.getBlockChanges(params)) {

				int current = world.getBlockAt(blockLocation).getTypeId();
				
				// Make sure the ID corresponds
				return (current == bc.type);
			}
			
			// No changes recorded, so we'll assume this block is natural
			return false;
			
		} catch (SQLException e) {
			throw new HistoryException("Could not load player history.", e);
		}
	}
}
