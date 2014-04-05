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

package com.comphenix.xp.parser.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.comphenix.xp.ExperienceMod;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.Utility;
import com.google.common.collect.Lists;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.EntityType;

/**
 * Used by the MobEntityTypeParser to parse mob names or categories.
 * @author Kristian
 */
public class MobMatcher extends TextParser<List<Short>> {
	
	private Map<String, List<Short>> categories = new HashMap<String, List<Short>>();
	private Map<String, Short> names = new HashMap<String, Short>();
	
	public MobMatcher() {
		loadDefaultCategories();
		loadDefaultMobs();
	}
		
	protected void loadDefaultCategories() {
		// Utility mobs may be created by and serve the player. 
		registerCategory("UTILITY", "IRON_GOLEM", "SNOWMAN", "WITHER");
		
		// Passive mobs will never attack the player.
		registerCategory("PASSIVE", "BAT", "CHICKEN", "COW", "MUSHROOM_COW", "OCELOT", 
				"PIG", "SHEEP", "SQUID", "VILLAGER");
		
		// Neutral mobs will not attack the player unless provoked. The act of provoking neutral mobs varies between mobs. 
		registerCategory("NEUTRAL", "IRON_GOLEM", "ENDERMAN", "PIG_ZOMBIE", "WOLF");
		
		// Hostile mobs will attack the player when in range.
		registerCategory("HOSTILE", "BLAZE", "CAVE_SPIDER", "CREEPER", "GHAST", 
				"GIANT", "MAGMA_CUBE", "SILVERFISH", "SKELETON", "SLIME", 
				"SPIDER", "ZOMBIE", "WITCH");
		
		// Boss mobs have a large amount of health and spawn only once per world. 
		registerCategory("BOSS", "ENDER_DRAGON", "WITHER");
	}
	
	protected void loadDefaultMobs() {
		
		// Add every default entity type and name
		for (EntityType type : EntityType.values()) {
			if (type != null && type.isAlive() && type.isSpawnable()) {
				registerMob(type.name(), type.getTypeId());
				registerMob(type.getName(), type.getTypeId());
			}
		}
	}
	
	/**
	 * Retrieves the category with the same ENUM name, or NULL if no such category exists.
	 * @param text - name of the category to find.
	 * @return The found category's list of mobs, or NULL if no such category can be found.
	 */
	public List<Short> getCategoryFromName(String text) {
		
		String enumed = Utility.getEnumName(text);
		return categories.get(enumed);
	}
	
	/**
	 * Retrieves the mob with the same ENUM name, or NULL if no such mob is registered.
	 * @param text - name of the mob to find.
	 * @return The found mob ID, or NULL if no such mob can be found.
	 */
	public Short getMobFromName(String text) {
		
		String enumed = Utility.getEnumName(text);
		return names.get(enumed);
	}
	
	/**
	 * Registers a category with the given name.
	 * @param categoryName - name of the category.
	 * @param types - list of mobs in that category.
	 */
	public void registerCategory(String categoryName, String... typeNames) {
		
		if (categoryName == null)
			throw new NullArgumentException("categoryName");
		if (typeNames == null)
			throw new NullArgumentException("types");
		
		List<Short> ids = new ArrayList<Short>();
		
		// Get all the type IDs
		for (String typeName : typeNames) {
			try {
				if (typeName != null) {
					ids.add(EntityType.valueOf(typeName).getTypeId());
				}
			} catch (IllegalArgumentException e) {
				// Notify the user
				ExperienceMod.getDefaultDebugger().
					printWarning(this, "Cannot register %s as an %s.", typeName, categoryName);
			}
		}
		categories.put(categoryName, ids);
	}
	
	/**
	 * Registers a category with the given name.
	 * @param categoryName - name of the category.
	 * @param types - list of mobs in that category.
	 */
	public void registerCategory(String categoryName, EntityType... types) {
		
		if (categoryName == null)
			throw new NullArgumentException("categoryName");
		if (types == null)
			throw new NullArgumentException("types");
		
		List<Short> ids = new ArrayList<Short>();
		
		// Get all the type IDs
		for (EntityType type : types) {
			if (type != null)
				ids.add(type.getTypeId());
		}
		
		categories.put(categoryName, ids);
	}
	
	/**
	 * Registers a category with the given name.
	 * @param categoryName - name of the category.
	 * @param types - list of mob ids in that category.
	 */
	public void registerCategory(String categoryName, Short... types) {
		
		if (categoryName == null)
			throw new NullArgumentException("categoryName");
		if (types == null)
			throw new NullArgumentException("types");
		
		categories.put(categoryName, Lists.newArrayList(types));
	}
	
	/**
	 * Unregisters the given category.
	 * @param categoryName - name of the category to unregister.
	 * @return List of mobs from the category that was unregistered, or NULL if no category could be found.
	 */
	public List<Short> unregisterCategory(String categoryName) {
		
		if (categoryName == null)
			throw new NullArgumentException("categoryName");	
		
		return categories.remove(categoryName);
	}
	
	/**
	 * Retrieves a collection of currently registered categories.
	 * @return Collection of registered categories.
	 */
	public Collection<String> getRegisteredCategories() {
		return categories.keySet();
	}
	
	/**
	 * Register an individual mob.
 	 * @param mobName - ENUM name of the mob.
	 * @param id - the unique type ID of the mob.
	 */
	public void registerMob(String mobName, Short id) {

		if (mobName == null)
			throw new NullArgumentException("mobName");
		if (id == null)
			throw new NullArgumentException("id");

		names.put(mobName, id);
	}

	/**
	 * Unregisters an individual mob.
	 * @param mobName - name of the mob to unregister.
	 * @return The previously registered mob's ID, or NULl if no mob could be found.
	 */
	public Short unregisterMob(String mobName) {
		
		if (mobName == null)
			throw new NullArgumentException("mobName");
		
		return names.remove(mobName);
	}
	
	/**
	 * Retrieves a collection of currently registered mobs.
	 * @return Collection of registered mobs.
	 */
	public Collection<String> getRegisteredMobs() {
		return names.keySet();
	}

	@Override
	public List<Short> parse(String text) throws ParsingException {

		String enumed = Utility.getEnumName(text);
		
		if (categories.containsKey(enumed))
			return categories.get(enumed);
		else if (names.containsKey(enumed))
			return Utility.getElementList(names.get(enumed));
		else
			return Utility.getElementList(null);
	}
}
