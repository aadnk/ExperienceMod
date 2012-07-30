package com.comphenix.xp.history;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.comphenix.xp.Debugger;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.HawkEyeAPI;

public class HawkeyeService implements HistoryService {

	public static final String NAME = "HAWKEYE";
	
	private Debugger debugger;
	private boolean searching;
	
	// The result
	private DataEntry searchResult;
	
	// Lock token
	private final Object lock = new Object();
	
	@Override
	public String getServiceName() {
		return NAME;
	}
	
	public HawkeyeService(Debugger debugger) {
		this.debugger = debugger;
	}
	
	/**
	 * Determines if the block service can be loaded.
	 * @return TRUE if it can be loaded, FALSE otherwise.
	 */
	public static boolean exists(PluginManager manager) {
        Plugin hawkeye = manager.getPlugin("HawkEye");
        return hawkeye != null;
	}

	@Override
	public Boolean hasPlayerHistory(Location blockLocation) throws HistoryException {

		int blockID = blockLocation.getBlock().getTypeId();
		
		// Async lock
		synchronized (lock) {
			if (searching) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					throw new HistoryException("Waiting interrupted.", e);
				}
			}
			
			SearchParser searchParser = new SearchParser();
			searchParser.loc = blockLocation.toVector();
			searchParser.actions = Arrays.asList(DataType.BLOCK_PLACE, DataType.BLOCK_FORM);
			searchParser.radius = 0;
			searchParser.worlds = new String[] {blockLocation.getWorld().getName()};
			searching = true;
			
			// Search synchronously 
			// TODO: Make the HawkEye call async.
			HawkEyeAPI.performSearch(new HawkeyeCallback(debugger, this), 
					searchParser, SearchDir.DESC);
			
			// Wait for it to be done
			if (searching) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					throw new HistoryException("Waiting interrupted.", e);
				}
			}
		}
		
		// Make sure the ID corresponds
		if (searchResult instanceof BlockChangeEntry) {
		
			BlockChangeEntry changeData = (BlockChangeEntry) searchResult;
			int to = getToField(changeData);

			debugger.printDebug(this, "To field: %s", to);
			return to == blockID;
			
		} else {
		
			// We don't know
			return null;
		}
	}
	
	private int getToField(BlockChangeEntry changeData) throws HistoryException {
		
		try {
			Field toField = changeData.getClass().getDeclaredField("to");
			toField.setAccessible(true);
			
			String toValue = (String) toField.get(changeData);
			return BlockUtil.getIdFromString(toValue);
			
			// A ton of potential problems
		} catch (SecurityException e1) {
			throw new HistoryException("Security violation: Cannot access private member.");
		} catch (NoSuchFieldException e1) {
			throw new HistoryException("Hawkeye class structure has changed. No field 'to' exists.");
		} catch (IllegalArgumentException e) {
			throw new HistoryException("Illegal argument.");
		} catch (IllegalAccessException e) {
			throw new HistoryException("Cannot use reflection. Illegal access.");
		}
	}
	
	public boolean isSearching() {
		return searching;
	}

	public void setSearching(boolean searching) {
		this.searching = searching;
	}

	public DataEntry getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(DataEntry searchResult) {
		this.searchResult = searchResult;
	}

	public Object getLock() {
		return lock;
	}
	
	@Override
	public LookupSpeed getLookupSpeed() {
		return LookupSpeed.SLOW;
	}

	@Override
	public boolean hasFalsePositives() {
		return false;
	}

	@Override
	public boolean hasFalseNegatives() {
		return false;
	}
}
