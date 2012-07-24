package com.comphenix.xp.history;

import org.bukkit.Location;

import com.comphenix.xp.extra.Service;

/**
 * Represents a store of previous player actions.
 * 
 * @author Kristian
 */
public interface HistoryService extends Service {

	/**
	 * The lookup speed of a particular service.
	 * 
	 * @author Kristian
	 */
	public enum LookupSpeed {
		/**
		 * Represents services that are contained in memory and have a constant lookup speed (like hash tables or Bloom filters).
		 */
		FASTEST,
		
		/**
		 * Represents services that are entirely contained in memory, and have a lookup speed proportional to their storage size.
		 */
		VERY_FAST,
		
		/**
		 * Represents services that are almost entirely contained within memory, and have a fast lookup speed.
		 */
		FAST,
		
		/**
		 * Represents services that are mostly contained in memory, but may require I/O and slow down in certain circumstances.
		 */
		NORMAL,
		
		/**
		 * Represents services that store their data externally, such as a flat file or database.
		 */
		SLOW
	}
	
	/**
	 * Retrieve the lookup (read) speed.
	 * @return How quickly values can be read from this service.
	 */
	public LookupSpeed getLookupSpeed();
	
	/**
	 * Whether or not this service may return false positives. 
	 * <p>
	 * If it does, {@link HistoryProviders} may decide to inquire other services, if at all possible.
	 * @return TRUE if this service returns false positives, FALSE otherwise.
	 */
	public boolean hasFalsePositives();
	
	
	/**
	 * Whether or not this service returns false negatives.
	 * @return TRUE if this service returns false negatives, FALSE otherwise.
	 */
	public boolean hasFalseNegatives();
	
	/**
	 * Determines whether or not a block has been placed by a player.
	 * <p>
	 * A service should return NULL if it cannot decide on a value, OR report that its return values 
	 * always probabilistic (using {@link #hasFalsePositives()} or {@link #hasFalseNegatives()}).
	 * @param blockLocation - location of the block in question.
	 * @return TRUE if the given block was placed by a player, FALSE if it was placed naturally, NULL if uknown.
	 */
	public Boolean hasPlayerHistory(Location blockLocation) throws HistoryException;
}
