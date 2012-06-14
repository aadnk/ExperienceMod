package com.comphenix.xp.lookup;

/**
 * Marker interface for all the query types.
 * 
 * @author Kristian
 */
public interface Query {
	public enum Types {
		Items,
		Potions,
		Mobs
	}
	
	public Types getQueryType();
}
