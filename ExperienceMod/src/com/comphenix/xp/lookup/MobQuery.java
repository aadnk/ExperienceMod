package com.comphenix.xp.lookup;

/**
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

import com.comphenix.xp.parser.Parsing;

public class MobQuery implements Query {

	// DON'T CARE fields are empty
	private List<EntityType> type;
	private List<DamageCause> deathCause;
	private List<Boolean> spawner;
	private List<Boolean> baby;
	private List<Boolean> tamed;
	
	/**
	 * Universal query.
	 */
	public MobQuery() {
		this(new ArrayList<EntityType>(),
		     new ArrayList<DamageCause>(),
		     null, null, null);
	}
	
	public MobQuery(EntityType type) {
		this(type, null, null, null, null);
	}
	
	public MobQuery(EntityType type, DamageCause deathCause, SpawnReason reason, Boolean baby, Boolean tamed) {
		setSingles(type, deathCause, reason, baby, tamed);
	}
	
	public MobQuery(List<EntityType> type, List<DamageCause> deathCause,
			Boolean spawner, Boolean baby, Boolean tamed) {
		this.type = type;
		this.deathCause = deathCause;
		this.spawner = Parsing.getElementList(spawner);
		this.baby = Parsing.getElementList(baby);
		this.tamed = Parsing.getElementList(tamed);
	}

	public MobQuery(LivingEntity entity, SpawnReason reason) {
		
		EntityDamageEvent cause = entity.getLastDamageCause();
		DamageCause deathCause = null;
		
		Boolean paramBaby = null;
		Boolean paramTamed = null;
		
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

		// Load from singles
		setSingles(entity.getType(), deathCause, reason, paramBaby, paramTamed);
	}
	
	private void setSingles(EntityType type, DamageCause deathCause, SpawnReason reason, Boolean baby, Boolean tamed) {
		this.type = Parsing.getElementList(type);
		this.deathCause = Parsing.getElementList(deathCause);
		this.baby = Parsing.getElementList(baby);
		this.tamed = Parsing.getElementList(tamed);
		
		if (reason != null) 
			this.spawner = Arrays.asList(reason == SpawnReason.SPAWNER);
		else
			this.spawner = new ArrayList<Boolean>();
	}

	public List<DamageCause> getDeathCause() {
		return deathCause;
	}
	
	public List<EntityType> getType() {
		return type;
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
	
	public boolean hasType() {
		return type != null && !type.isEmpty();
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
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(type).
	            append(deathCause).
	            append(spawner).
	            append(baby).
	            append(tamed).
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
            append(type, other.type).
            append(deathCause, other.deathCause).
            append(spawner, other.spawner).
            append(baby, other.baby).
            append(tamed, other.tamed).
            isEquals();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%s|%s|%s|%s|%s", 
							hasType() ? StringUtils.join(type, ", ") : "",
							hasDeathCause() ? StringUtils.join(deathCause, ", ") : "",
						    Parsing.formatBoolean("spawner", spawner),
							Parsing.formatBoolean("baby", baby),
							Parsing.formatBoolean("tamed", tamed));
	}
	
	@Override
	public Types getQueryType() {
		return Types.Mobs;
	}
}
