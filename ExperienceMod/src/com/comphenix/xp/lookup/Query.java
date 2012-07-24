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
		Mobs,
		Configurations
	}
	
	public Types getQueryType();
	
	/**
	 * Determines if the given query matches the current query. 
	 * <p>
	 * If a query can be considered to be a subset of the universe of items, 
	 * then this operation determines if the current set (A) has a non-empty intersection 
	 * with the other set (B).
	 * @param other - the other query to match.
	 * @return TRUE if the current query contains every element of the other query.
	 */
	public boolean match(Query other);
}
