package com.comphenix.xp.history;

import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.event.block.BlockPlaceEvent;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * A simple memory-based history service.
 * 
 * @author Kristian
 */
public class MemoryService implements HistoryService {

	public static final String NAME = "MEMORY";
	
	// Maximum number of block changes to store
	private static final int MAXIMUM_SIZE = 1000;
	
	private Cache<Location, String> cache;
	
	/**
	 * Constructs a simple memory-based history. 
	 * @param maximumSize - maximum number of block changes to store.
	 * @param timeout - number of seconds until a location is removed from the history.
	 */
	public MemoryService(int maximumSize, int timeout) {
		cache = CacheBuilder.newBuilder()
	    .maximumSize(maximumSize)
	    .expireAfterWrite(timeout, TimeUnit.SECONDS)
	    .build(new CacheLoader<Location, String>() {
	    	@Override
	    	public String load(Location arg0) throws Exception {
	    		throw new RuntimeException("Impossible to load unknown value.");
	    	}
		});
	}
	
	/**
	 * Constructs a simple memory-based history. 
	 * @param timeout - number of seconds until a location is removed from the history.
	 */
	public MemoryService(int timeout) {
		this(MAXIMUM_SIZE, timeout);
	}

	@Override
	public String getServiceName() {
		return NAME;
	}
	
	/**
	 * Called by ExperienceBlockListener when a block has been placed by a plyer.
	 * @param event - even to be notified about.
	 */
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
	
		if (event.getPlayer() != null && event.getBlock() != null) {
		
			// Store this
			cache.asMap().put(event.getBlock().getLocation(), 
					          event.getPlayer().getName());
		}
	}
	
	@Override
	public Boolean hasPlayerHistory(Location blockLocation) throws HistoryException {

		// See if a player has updated a certain block before
		return cache.asMap().containsKey(blockLocation);
	}

	@Override
	public LookupSpeed getLookupSpeed() {
		return LookupSpeed.FAST;
	}

	@Override
	public boolean hasFalsePositives() {
		return false;
	}

	@Override
	public boolean hasFalseNegatives() {
		// Only a fraction of all block changes are stored in memory.
		return true;
	}
}
