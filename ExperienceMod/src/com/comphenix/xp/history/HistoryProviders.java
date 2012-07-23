package com.comphenix.xp.history;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Location;

import com.comphenix.xp.Debugger;
import com.comphenix.xp.extra.ServiceProvider;

/**
 * Registry of history (block change logging) providers.
 * <p>
 * The default service setting is ignored.
 * @author Kristian
 */
public class HistoryProviders extends ServiceProvider<HistoryService> {

	/**
	 * Services sorted by lookup speed.
	 */
	protected SortedSet<HistoryService> speedOrder = new TreeSet<HistoryService>(new Comparator<HistoryService>() {
		public int compare(HistoryService o1, HistoryService o2) {
			return o1.getLookupSpeed().compareTo(o2.getLookupSpeed());
		}
	});
	
	public HistoryProviders() {
		super(LogBlockService.NAME);
	}
	
	public MemoryService getMemoryService() {
		return (MemoryService) getByName(MemoryService.NAME);
	}
	
	/**
	 * Determines whether or not a block has been placed by a player, using the registered service providers
	 * in order of lookup speed. 
	 * <p>
	 * If no service provider could accurately answer the query, the function returns NULL.
	 * 
	 * @param block - block to search for.
	 * @param acceptGuesses - whether or not probabilistic answers are acceptable.
	 * @param debugger - debugger that handles errors produced by any history service.
	 * @return TRUE if the block was placed by a player, FALSE if it was generated naturally, or NULL if unknown.
	 */
	public Boolean hasPlayerHistory(Location block, boolean acceptGuesses, final Debugger debugger) {
		
		// Add a reasonable error handler
		return hasPlayerHistory(block, acceptGuesses, new ErrorHandler<Exception>() {
			@Override
			public void onError(Exception error) {
				Throwable cause = ExceptionUtils.getCause(error);
				
				// Print error and its cause
				if (debugger == null)
					error.printStackTrace();
				else if (cause == null)
					debugger.printWarning(HistoryProviders.this, "Error: %s", error.getMessage());
				else
					debugger.printWarning(HistoryProviders.this, "%s: %s", error.getMessage(), cause.getMessage());
			}
		});
	}
	
	/**
	 * Determines whether or not a block has been placed by a player, using the registered service providers
	 * in order of lookup speed. 
	 * <p>
	 * If no service provider could accurately answer the query, the function returns NULL.
	 * 
	 * @param block - block to search for.
	 * @param acceptGuesses - whether or not probabilistic answers are acceptable.
	 * @param errorHandler - reports errors from individual history services.
	 * @return TRUE if the block was placed by a player, FALSE if it was generated naturally, or NULL if unknown.
	 */
	public Boolean hasPlayerHistory(Location block, boolean acceptGuesses, ErrorHandler<Exception> errorHandler) {
	
		Boolean current = null;
		
		for (HistoryService service : speedOrder) {
			
			Boolean answer = null;
			
			try {
				if (isEnabled(service))
					answer = service.hasPlayerHistory(block);
				
			} catch (Exception e) {
				if (errorHandler != null)
					errorHandler.onError(e);
			}
			
			// Skip if no answer was provided
			if (answer != null) {
				
				// Record the last answer
				current = answer;
				
				// Early answers!
				if (current && !service.hasFalsePositives())
					return true;
				else if (!current && !service.hasFalseNegatives())
					return false;
			}
		}
		
		// If we're at this point, it must be because the last answer was probabilistic.
		if (acceptGuesses && current != null) {
			return current;
		} else {
			return null;
		}
	}
	
	@Override
	public HistoryService register(HistoryService service) {
		HistoryService removed = super.register(service);
		
		speedOrder.add(service);
		
		// Removed overridden service
		if (removed != null) {
			speedOrder.remove(removed);
		}
		
		return removed;
	}
	
	@Override
	public HistoryService unregister(HistoryService service) {
		HistoryService removed = super.unregister(service);
		
		// Clean up
		if (removed != null)
			speedOrder.remove(removed);
		return removed;
	}
}
