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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.comphenix.xp.parser.Utility;
import com.google.common.collect.Lists;

public class MobQuery implements Query {

	// DON'T CARE fields are empty
	private List<Short> typeID;
	private List<DamageCause> deathCause;
	private List<Integer> size;
	private List<SkeletonType> skeletonType;
	
	private List<Boolean> spawner;
	private List<Boolean> baby;
	private List<Boolean> tamed;
	private List<Boolean> playerKill;
	private List<Boolean> villaged;
	
	// Optimize away object creations
	private static List<Short> noTypes = Utility.getElementList((Short) null);
	private static List<Integer> noSizes = Utility.getElementList((Integer) null);
	private static List<DamageCause> noDamages = Utility.getElementList((DamageCause) null);
	private static List<SkeletonType> noSkeletons = Utility.getElementList((SkeletonType) null);
	private static List<Boolean> noBooleans = Utility.getElementList((Boolean) null);
	
	/**
	 * Universal query.
	 */
	public static MobQuery fromAny() {
		return new MobQuery(noTypes, noDamages, noSizes, noSkeletons,
							noBooleans, noBooleans, noBooleans, noBooleans, noBooleans);
	}	
	
	/**
	 * Creates a query where NULL values match any possible value in that category. 
	 * @param type - the unique type to search for, or NULL for all mob types.
	 * @return The new mob query.
	 */
	public static MobQuery fromAny(EntityType type) {
		return fromAny(type, null, null, null, null, null, null);
	}
	
	/**
	 * Creates a query where NULL values match any possible value in that category. 
	 * @param type - the unique type to search for, or NULL for all mob types.
	 * @param cause - the damage type that killed the mob, or NULL for all damage types.
	 * @return The new mob query.
	 */
	public static MobQuery fromAny(EntityType type, DamageCause cause) {
		return fromAny(type, cause, null, null, null, null, null);
	}

	/**
	 * Creates a query where NULL values match any possible value in that category. 
	 * @param type - the unique type to search for, or NULL for all mob types.
	 * @param deathCause - the damage type that killed the mob, or NULL for all damage types.
	 * @param reason - the cause for spawning this mob, or NULL for all possible causes.
	 * @param baby - TRUE to match babies, FALSE to match adults, and NULL to match both.
	 * @param tamed - TRUE to matched tamed animals (wolfs), FALSE to match non-tamed or not-tamable, NULL for both.
	 * @param playerKill - TRUE to match mobs killed by players, FALSE to match mobs killed by anything else, NULL for both.
	 * @param villaged - TRUE to match zombie villagers, FALSE to match mobs killed by anything else, NULL for both.
	 * @return The new mob query.
	 */
	public static MobQuery fromAny(EntityType type, DamageCause deathCause, SpawnReason reason, 
			Boolean baby, Boolean tamed, Boolean playerKill, Boolean villaged) {

		// Don't specify a size
		return fromAny(type, deathCause, null, reason, baby, tamed, playerKill, villaged);
	}
	
	/**
	 * Creates a query where NULL values match any possible value in that category. 
	 * @param type - the unique type to search for, or NULL for all mob types.
	 * @param deathCause - the damage type that killed the mob, or NULL for all damage types.
	 * @param size - size of the slime/magma cube, or NULL for all sizes (or mobs without different sizes).
	 * @param reason - the cause for spawning this mob, or NULL for all possible causes.
	 * @param baby - TRUE to match babies, FALSE to match adults, and NULL to match both.
	 * @param tamed - TRUE to matched tamed animals (wolfs), FALSE to match non-tamed or not-tamable, NULL for both.
	 * @param playerKill - TRUE to match mobs killed by players, FALSE to match mobs killed by anything else, NULL for both.
	 * @param villaged - TRUE to match zombie villagers, FALSE to match mobs killed by anything else, NULL for both.
	 * @return The new mob query.
	 */
	public static MobQuery fromAny(EntityType type, DamageCause deathCause, Integer size, 
			   SpawnReason reason, Boolean baby, Boolean tamed, Boolean playerKill, Boolean villaged) {
		
		// Convert type to type ID
		return fromAny(type != null ? type.getTypeId() : null, 
				deathCause, size, reason, baby, tamed, playerKill, villaged);
	}
	
	/**
	 * Creates a query where NULL values match any possible value in that category. 
	 * @param typeID - the unique type ID to search for, or NULL for all mob types.
	 * @param deathCause - the damage type that killed the mob, or NULL for all damage types.
	 * @param size - size of the slime/magma cube, or NULL for all sizes (or mobs without different sizes).
	 * @param reason - the cause for spawning this mob, or NULL for all possible causes.
	 * @param baby - TRUE to match babies, FALSE to match adults, and NULL to match both.
	 * @param tamed - TRUE to matched tamed animals (wolfs), FALSE to match non-tamed or not-tamable, NULL for both.
	 * @param playerKill - TRUE to match mobs killed by players, FALSE to match mobs killed by anything else, NULL for both.
	 * @param villaged - TRUE to match zombie villagers, FALSE to match mobs killed by anything else, NULL for both.
	 * @return The new mob query.
	 */
	public static MobQuery fromAny(Short typeID, DamageCause deathCause, Integer size,
								   SpawnReason reason, Boolean baby, Boolean tamed, Boolean playerKill, Boolean villaged) {
		
		return fromAny(typeID, deathCause, size, null, reason, baby, tamed, playerKill, villaged);
	}
	
	/**
	 * Creates a query where NULL values match any possible value in that category. 
	 * @param typeID - the unique type ID to search for, or NULL for all mob types.
	 * @param deathCause - the damage type that killed the mob, or NULL for all damage types.
	 * @param size - size of the slime/magma cube, or NULL for all sizes (or mobs without different sizes).
	 * @param skeleton - the type of the skeleton to find, or NULL for all skeleton types (or non-skeletons).
	 * @param reason - the cause for spawning this mob, or NULL for all possible causes.
	 * @param baby - TRUE to match babies, FALSE to match adults, and NULL to match both.
	 * @param tamed - TRUE to matched tamed animals (wolfs), FALSE to match non-tamed or not-tamable, NULL for both.
	 * @param playerKill - TRUE to match mobs killed by players, FALSE to match mobs killed by anything else, NULL for both.
	 * @param villaged - TRUE to match zombie villagers, FALSE to match mobs killed by anything else, NULL for both.
	 * @return The new mob query.
	 */
	public static MobQuery fromAny(Short typeID, DamageCause deathCause, Integer size, SkeletonType skeleton,
								   SpawnReason reason, Boolean baby, Boolean tamed, Boolean playerKill, Boolean villaged) {
		
		List<Boolean> spawner;
		
		if (reason != null) 
			spawner = Arrays.asList(reason == SpawnReason.SPAWNER);
		else
			spawner = new ArrayList<Boolean>();
		
		return new MobQuery(
				 	Utility.getElementList(typeID),
				 	Utility.getElementList(deathCause),
				 	Utility.getElementList(size),
				 	Utility.getElementList(skeleton),
				 	spawner,
				 	Utility.getElementList(baby),
				 	Utility.getElementList(tamed),
				 	Utility.getElementList(playerKill),
				 	Utility.getElementList(villaged)
		);
	}
	
	/**
	 * Creates an exact query - where null values only match unspecified "any" queries - from the given 
	 * entity, spawn reason and killer.
	 * @param entity - the entity to create from.
	 * @param reason - the action that spawned the given entity.
	 * @param playerKill - TRUE if this mob was killed by a player, FALSE if it was killed by anything else.
	 * @return The exact query.
	 */
	public static MobQuery fromExact(LivingEntity entity, SpawnReason reason, boolean playerKill) {
		EntityDamageEvent cause = entity.getLastDamageCause();
		DamageCause deathCause = null;
		Integer size = null;
		SkeletonType skeleton = null;
		
		Boolean villaged = false;
		Boolean paramBaby = false;
		Boolean paramTamed = false;
		
		if (cause != null) {
			deathCause = cause.getCause();
		}
		
		// Check age and tame status
		if (entity instanceof Ageable) {
			paramBaby = !((Ageable) entity).isAdult();
		}
		
		if (entity instanceof Tameable) {
			paramTamed = ((Tameable)entity).isTamed();
		}

		// Check size
		if (entity instanceof Slime) {
			size = ((Slime)entity).getSize();
		}
		
		// Check skeleton type
		if (entity instanceof Skeleton) {
			skeleton = ((Skeleton) entity).getSkeletonType();
		} 
		
		// Check villaged
		if (entity instanceof Zombie) {
			villaged = ((Zombie) entity).isVillager();
		}
		
		// Load directly
		return fromExact(entity.getType(), deathCause, size, skeleton, reason, 
					     paramBaby, paramTamed, playerKill, villaged);
	}

	/**
	 * Creates an exact query - where null values only match unspecified "any" queries - from the given 
	 * entity, spawn reason and killer.
	 * @param type - the entity type to search for.
	 * @param deathCause - the last damage to include in the search.
	 * @param reason - the action that spawned the given entity.
	 * @param baby - TRUE to search for babies, FALSE to search for adults.
	 * @param tamed - TRUE to search for tamed animals, FALSE for everything else.
	 * @param playerKill - TRUE to search for mobs killed by a player, FALSE for anything else.
	 * @param villaged - TRUE if we are looking for village mobs, such as zombie villagers.
	 * @return The exact query.
	 */
	public static MobQuery fromExact(EntityType type, DamageCause deathCause, SpawnReason reason, 
									 Boolean baby, Boolean tamed, Boolean playerKill, Boolean villaged) {
		
		return fromExact(type != null ? type.getTypeId() : null, 
						 deathCause, null, reason, baby, tamed, playerKill, villaged);
	}
	
	/**
	 * Creates an exact query - where null values only match unspecified "any" queries - from the given 
	 * entity, spawn reason and killer.
	 * @param type - the entity type to search for.
	 * @param deathCause - the last damage to include in the search.
	 * @param size - the slime/magma cube size to search for.
	 * @param reason - the action that spawned the given entity.
	 * @param baby - TRUE to search for babies, FALSE to search for adults.
	 * @param tamed - TRUE to search for tamed animals, FALSE for everything else.
	 * @param playerKill - TRUE to search for mobs killed by a player, FALSE for anything else.
	 * @param villaged - TRUE if we are looking for village mobs, such as zombie villagers.
	 * @return The exact query.
	 */
	public static MobQuery fromExact(EntityType type, DamageCause deathCause, Integer size, 
									 SpawnReason reason, Boolean baby, Boolean tamed, Boolean playerKill, Boolean villaged) {

		return fromExact(type != null ? type.getTypeId() : null, 
						 deathCause, size, reason, baby, tamed, playerKill, villaged);
	}	
	
	/**
	 * Creates an exact query - where null values only match unspecified "any" queries - from the given 
	 * entity, spawn reason and killer.
	 * @param type - the entity type to search for.
	 * @param deathCause - the last damage to include in the search.
	 * @param size - the slime/magma cube size to search for.
	 * @param skeleton - the type of skeleton to search for.
	 * @param reason - the action that spawned the given entity.
	 * @param baby - TRUE to search for babies, FALSE to search for adults.
	 * @param tamed - TRUE to search for tamed animals, FALSE for everything else.
	 * @param playerKill - TRUE to search for mobs killed by a player, FALSE for anything else.
	 * @param villaged - TRUE if we are looking for village mobs, such as zombie villagers.
	 * @return The exact query.
	 */
	public static MobQuery fromExact(EntityType type, DamageCause deathCause, Integer size, SkeletonType skeleton,
									 SpawnReason reason, Boolean baby, Boolean tamed, Boolean playerKill, Boolean villaged) {

		return fromExact(type != null ? type.getTypeId() : null, 
						 deathCause, size, skeleton, 
						 reason, baby, tamed, playerKill, villaged);
	}	
	
	/**
	 * Creates an exact query - where null values only match unspecified "any" queries - from the given 
	 * entity, spawn reason and killer.
	 * @param typeID - the unique entity type ID to search for.
	 * @param deathCause - the last damage to include in the search.
	 * @param size - the slime/magma cube size to search for.
	 * @param reason - the action that spawned the given entity.
	 * @param baby - TRUE to search for babies, FALSE to search for adults.
	 * @param tamed - TRUE to search for tamed animals, FALSE for everything else.
	 * @param playerKill - TRUE to search for mobs killed by a player, FALSE for anything else.
	 * @param villaged - TRUE if we are looking for village mobs, such as zombie villagers.
	 * @return The exact query.
	 */
	public static MobQuery fromExact(Short typeID, DamageCause deathCause, Integer size,
									 SpawnReason reason, Boolean baby, Boolean tamed, Boolean playerKill, Boolean villaged) {
		return new MobQuery(
				Lists.newArrayList(typeID), 
				Lists.newArrayList(deathCause),
				Lists.newArrayList(size),
				Lists.newArrayList(reason == null ? null : 
					(reason == SpawnReason.SPAWNER)),
				Lists.newArrayList(baby),
				Lists.newArrayList(tamed),
				Lists.newArrayList(playerKill),
				Lists.newArrayList(villaged)
		);
	}
	
	/**
	 * Creates an exact query - where null values only match unspecified "any" queries - from the given 
	 * entity, spawn reason and killer.
	 * @param typeID - the unique entity type ID to search for.
	 * @param deathCause - the last damage to include in the search.
	 * @param size - the slime/magma cube size to search for.
	 * @param skeleton - the type of skeleton to search for.
	 * @param reason - the action that spawned the given entity.
	 * @param baby - TRUE to search for babies, FALSE to search for adults.
	 * @param tamed - TRUE to search for tamed animals, FALSE for everything else.
	 * @param playerKill - TRUE to search for mobs killed by a player, FALSE for anything else.
	 * @param villaged - TRUE if we are looking for village mobs, such as zombie villagers.
	 * @return The exact query.
	 */
	public static MobQuery fromExact(Short typeID, DamageCause deathCause, Integer size, SkeletonType skeleton,
									 SpawnReason reason, Boolean baby, Boolean tamed, Boolean playerKill, Boolean villaged) {
		return new MobQuery(
				Lists.newArrayList(typeID), 
				Lists.newArrayList(deathCause),
				Lists.newArrayList(size),
				Lists.newArrayList(skeleton),
				Lists.newArrayList(reason == null ? null : 
					(reason == SpawnReason.SPAWNER)),
				Lists.newArrayList(baby),
				Lists.newArrayList(tamed),
				Lists.newArrayList(playerKill),
				Lists.newArrayList(villaged)
		);
	}
	
	public MobQuery(List<Short> typeID, List<DamageCause> deathCause, List<Integer> size, 
					List<Boolean> spawner, List<Boolean> baby, List<Boolean> tamed, 
					List<Boolean> playerKill, List<Boolean> villaged) {
		this(typeID, deathCause, size, noSkeletons, spawner, baby, tamed, playerKill, villaged); 
	}

	public MobQuery(List<Short> typeID, List<DamageCause> deathCause, List<Integer> size, List<SkeletonType> skeleton, 
					List<Boolean> spawner, List<Boolean> baby, List<Boolean> tamed, List<Boolean> playerKill, List<Boolean> villaged) {
		this.typeID = typeID;
		this.deathCause = deathCause;
		this.size = size;
		this.skeletonType = skeleton;
		this.spawner = spawner;
		this.baby = baby;
		this.tamed = tamed;
		this.playerKill = playerKill;
		this.villaged = villaged;
	}
	
	public List<DamageCause> getDeathCause() {
		return deathCause;
	}
	
	public List<Short> getType() {
		return typeID;
	}
	
	public List<Integer> getSize() {
		return size;
	}
	
	public List<Boolean> getSpawner() {
		return spawner;
	}
	
	public List<Boolean> getBaby() {
		return baby;
	}
	
	public List<Boolean> getTamed() {
		return tamed;
	}
	
	public List<Boolean> getVillaged() {
		return villaged;
	}
	
	public List<Boolean> getPlayerKill() {
		return playerKill;
	}
	
	public List<SkeletonType> getSkeletonType() {
		return skeletonType;
	}

	public boolean hasType() {
		return typeID != null && !typeID.isEmpty();
	}
	
	public boolean hasDeathCause() {
		return deathCause != null && !deathCause.isEmpty();
	}
	
	public boolean hasSize() {
		return size != null && !size.isEmpty();
	}
	
	public boolean hasSpawner() {
		return spawner != null && !spawner.isEmpty();
	}
	
	public boolean hasBaby() {
		return baby != null && !baby.isEmpty();
	}
	
	public boolean hasTamed() {
		return tamed != null && !tamed.isEmpty();
	}
	
	public boolean hasPlayerKill() {
		return playerKill != null && !playerKill.isEmpty();
	}
	
	public boolean hasVillaged() {
		return villaged != null && !villaged.isEmpty();
	}
	
	public boolean hasSkeletonType() {
		return skeletonType != null && !skeletonType.isEmpty();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(typeID).
	            append(deathCause).
	            append(size).
	            append(skeletonType).
	            append(spawner).
	            append(baby).
	            append(tamed).
	            append(playerKill).
	            append(villaged).
	            toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        MobQuery other = (MobQuery) obj;
        return new EqualsBuilder().
            append(typeID, other.typeID).
            append(deathCause, other.deathCause).
            append(size, other.size).
            append(skeletonType, other.skeletonType).
            append(spawner, other.spawner).
            append(baby, other.baby).
            append(tamed, other.tamed).
            append(playerKill, other.playerKill).
            append(villaged, other.villaged).
            isEquals();
	}
	
	@Override
	public boolean match(Query other) {

		// Yes, we could construct a MobTree, put the current MobQuery 
		// into it and query after other, but this is faster. Probably.
		if (other instanceof MobQuery) {
			MobQuery query = (MobQuery) other;
			
			// Make sure the current query is the superset of other
			return QueryMatching.matchParameter(typeID, query.typeID) &&
				   QueryMatching.matchParameter(deathCause, query.deathCause) &&
				   QueryMatching.matchParameter(size, query.size) &&
				   QueryMatching.matchParameter(skeletonType, query.skeletonType) &&
				   QueryMatching.matchParameter(spawner, query.spawner) &&
				   QueryMatching.matchParameter(baby, query.baby) &&
				   QueryMatching.matchParameter(tamed, query.tamed) &&
				   QueryMatching.matchParameter(playerKill, query.playerKill) && 
				   QueryMatching.matchParameter(villaged, query.villaged);
		}
		
		// Query must be of the same type
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s", 
							hasType() ? StringUtils.join(typeID, ", ") : "",
							hasDeathCause() ? StringUtils.join(deathCause, ", ") : "",
							hasSize() ? StringUtils.join(size, ", ") : "",
							hasSkeletonType() ? StringUtils.join(skeletonType, ", ") : "",
						    Utility.formatBoolean("spawner", spawner),
							Utility.formatBoolean("baby", baby),
							Utility.formatBoolean("tamed", tamed),
							Utility.formatBoolean("playerKill", playerKill),
							Utility.formatBoolean("villaged", villaged));
	}
	
	@Override
	public Types getQueryType() {
		return Types.MOBS;
	}
}
