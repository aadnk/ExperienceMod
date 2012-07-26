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
	private List<Boolean> spawner;
	private List<Boolean> baby;
	private List<Boolean> tamed;
	private List<Boolean> playerKill;
	
	// Optimize away object creations
	private static List<Short> noTypes = new ArrayList<Short>();
	private static List<DamageCause> noDamages = new ArrayList<DamageCause>();
	private static List<Boolean> noBooleans = new ArrayList<Boolean>();
	
	/**
	 * Universal query.
	 */
	public static MobQuery fromAny() {
		return new MobQuery(noTypes, noDamages, noBooleans, noBooleans, noBooleans, noBooleans);
	}	
	
	public static MobQuery fromAny(EntityType type) {
		return fromAny(type, null, null, null, null, null);
	}
	
	public static MobQuery fromAny(EntityType type, DamageCause cause) {
		return fromAny(type, cause, null, null, null, null);
	}

	public static MobQuery fromAny(EntityType type, DamageCause deathCause, SpawnReason reason, 
		       Boolean baby, Boolean tamed, Boolean playerKill) {
		
		// Convert type to type ID
		return fromAny(type != null ? type.getTypeId() : null, 
				deathCause, reason, baby, tamed, playerKill);
	}
	
	public static MobQuery fromAny(Short typeID, DamageCause deathCause, SpawnReason reason, 
							       Boolean baby, Boolean tamed, Boolean playerKill) {
		
		List<Boolean> spawner;
		
		if (reason != null) 
			spawner = Arrays.asList(reason == SpawnReason.SPAWNER);
		else
			spawner = new ArrayList<Boolean>();
		
		return new MobQuery(
				 	Utility.getElementList(typeID),
				 	Utility.getElementList(deathCause),
				 	spawner,
				 	Utility.getElementList(baby),
				 	Utility.getElementList(tamed),
				 	Utility.getElementList(playerKill)
		);
	}
	
	public static MobQuery fromExact(LivingEntity entity, SpawnReason reason, boolean playerKill) {
		EntityDamageEvent cause = entity.getLastDamageCause();
		DamageCause deathCause = null;
		
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

		// Load directly
		return fromExact(entity.getType(), deathCause, reason, paramBaby, paramTamed, playerKill);
	}

	public static MobQuery fromExact(EntityType type, DamageCause deathCause, SpawnReason reason, 
									 Boolean baby, Boolean tamed, Boolean playerKill) {
		
		return fromExact(type != null ? type.getTypeId() : null, 
				deathCause, reason, baby, tamed, playerKill);
	}
	
	public static MobQuery fromExact(Short typeID, DamageCause deathCause, SpawnReason reason, 
									 Boolean baby, Boolean tamed, Boolean playerKill) {
		return new MobQuery(
				Lists.newArrayList(typeID), 
				Lists.newArrayList(deathCause),
				Lists.newArrayList(reason == null ? null : 
					(reason == SpawnReason.SPAWNER)),
				Lists.newArrayList(baby),
				Lists.newArrayList(tamed),
				Lists.newArrayList(playerKill)
		);
	}
	
	
	public MobQuery(List<Short> typeID, List<DamageCause> deathCause, List<Boolean> spawner, 
					List<Boolean> baby, List<Boolean> tamed, List<Boolean> playerKill) {
		this.typeID = typeID;
		this.deathCause = deathCause;
		this.spawner = spawner;
		this.baby = baby;
		this.tamed = tamed;
		this.playerKill = playerKill;
	}

	public List<DamageCause> getDeathCause() {
		return deathCause;
	}
	
	public List<Short> getType() {
		return typeID;
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
	
	public List<Boolean> getPlayerKill() {
		return playerKill;
	}
	
	public boolean hasType() {
		return typeID != null && !typeID.isEmpty();
	}
	
	public boolean hasDeathCause() {
		return deathCause != null && !deathCause.isEmpty();
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
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(typeID).
	            append(deathCause).
	            append(spawner).
	            append(baby).
	            append(tamed).
	            append(playerKill).
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
            append(spawner, other.spawner).
            append(baby, other.baby).
            append(tamed, other.tamed).
            append(playerKill, other.playerKill).
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
				   QueryMatching.matchParameter(spawner, query.spawner) &&
				   QueryMatching.matchParameter(baby, query.baby) &&
				   QueryMatching.matchParameter(tamed, query.tamed) &&
				   QueryMatching.matchParameter(playerKill, query.playerKill);
		}
		
		// Query must be of the same type
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("%s|%s|%s|%s|%s|%s", 
							hasType() ? StringUtils.join(typeID, ", ") : "",
							hasDeathCause() ? StringUtils.join(deathCause, ", ") : "",
						    Utility.formatBoolean("spawner", spawner),
							Utility.formatBoolean("baby", baby),
							Utility.formatBoolean("tamed", tamed),
							Utility.formatBoolean("playerKill", playerKill));
	}
	
	@Override
	public Types getQueryType() {
		return Types.Mobs;
	}
}
