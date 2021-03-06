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

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.comphenix.xp.Action;

public class MobTree extends ActionTree<MobQuery> implements Multipliable<MobTree> {

	// DON'T CARE fields are marked with NULL
	protected Parameter<Short> type;
	protected Parameter<DamageCause> deathCause;
	protected Parameter<Integer> size;
	protected Parameter<SkeletonType> skeletonType;
	
	protected Parameter<Boolean> spawner;
	protected Parameter<Boolean> baby;
	protected Parameter<Boolean> tamed;
	protected Parameter<Boolean> playerKill;
	protected Parameter<Boolean> villaged;
	
	// For cloning
	protected MobTree(MobTree other, double newMultiplier) { 
		super(other, newMultiplier);
		
		if (other == null)
			throw new IllegalArgumentException("other");
		
		this.type = other.type;
		this.deathCause = other.deathCause;
		this.size = other.size;
		this.skeletonType = other.skeletonType;
		
		this.spawner = other.spawner;
		this.baby = other.baby;
		this.tamed = other.tamed;
		this.playerKill = other.playerKill;
		this.villaged = other.villaged;
	}
	
	public MobTree(double multiplier) {
		super(multiplier);
		this.type = new Parameter<Short>();
		this.deathCause = new Parameter<DamageCause>();
		this.size = new Parameter<Integer>();
		this.skeletonType = new Parameter<SkeletonType>();
		
		this.spawner = new Parameter<Boolean>();
		this.baby = new Parameter<Boolean>();
		this.tamed = new Parameter<Boolean>();
		this.playerKill = new Parameter<Boolean>();
		this.villaged = new Parameter<Boolean>();
	}

	@Override
	public MobTree withMultiplier(double newMultiplier) {
		return new MobTree(this, newMultiplier);
	}
	
	@Override
	protected Integer putFromParameters(MobQuery source, Integer id) {

		int paramCount = 0;
		
		if (source.hasType()) {
			type.put(source.getType(), id); paramCount++;
		}
		
		if (source.hasDeathCause()) {
			deathCause.put(source.getDeathCause(), id); paramCount++;
		}
		
		if (source.hasSize()) {
			size.put(source.getSize(), id); paramCount++;
		}
		
		if (source.hasSkeletonType()) {
			skeletonType.put(source.getSkeletonType(), id); paramCount++;
		}
		
		if (source.hasSpawner()) {
			spawner.put(source.getSpawner(), id); paramCount++;
		}
		
		if (source.hasBaby()) {
			baby.put(source.getBaby(), id); paramCount++;
		}
		
		if (source.hasTamed()) {
			tamed.put(source.getTamed(), id); paramCount++;
		}
		
		if (source.hasPlayerKill()) {
			playerKill.put(source.getPlayerKill(), id); paramCount++;
		}
		
		if (source.hasVillaged()) {
			villaged.put(source.getVillaged(), id); paramCount++;
		}
		
		return paramCount;
	}

	@Override
	protected Set<Integer> getFromParameters(MobQuery source) {

		Set<Integer> candidates = new HashSet<Integer>(flatten.keySet());
		
		if (source.hasType()) {
			type.retain(candidates, source.getType());
		}
		
		if (source.hasDeathCause()) {
			deathCause.retain(candidates, source.getDeathCause());
		}
		
		if (source.hasSize()) {
			size.retain(candidates, source.getSize());
		}
		
		if (source.hasSkeletonType()) {
			skeletonType.retain(candidates, source.getSkeletonType());
		}
		
		if (source.hasSpawner()) {
			spawner.retain(candidates, source.getSpawner());
		}
		
		if (source.hasBaby()) {
			baby.retain(candidates, source.getBaby());
		}
		
		if (source.hasTamed()) {
			tamed.retain(candidates, source.getTamed());
		}
		
		if (source.hasPlayerKill()) {
			playerKill.retain(candidates, source.getPlayerKill());
		}
		
		if (source.hasVillaged()) {
			villaged.retain(candidates, source.getVillaged());
		}
		
		return candidates;
	}

	@Override
	protected void putAllParameters(SearchTree<MobQuery, Action> other, Integer offset) {
		MobTree tree = (MobTree) other;

		type.putAll(tree.type, offset);
		deathCause.putAll(tree.deathCause, offset);
		size.putAll(tree.size, offset);
		skeletonType.putAll(tree.skeletonType, offset);
		
		spawner.putAll(tree.spawner, offset);
		baby.putAll(tree.baby, offset);
		tamed.putAll(tree.tamed, offset);
		playerKill.putAll(tree.playerKill, offset);
		villaged.putAll(tree.villaged, offset);
	}

	public Parameter<Short> getType() {
		return type;
	}

	public Parameter<DamageCause> getDeathCause() {
		return deathCause;
	}

	public Parameter<Integer> getSize() {
		return size;
	}
	
	public Parameter<SkeletonType> getSkeletonType() {
		return skeletonType;
	}
	
	public Parameter<Boolean> getSpawner() {
		return spawner;
	}

	public Parameter<Boolean> getBaby() {
		return baby;
	}

	public Parameter<Boolean> getTamed() {
		return tamed;
	}

	public Parameter<Boolean> getPlayerKill() {
		return playerKill;
	}
	
	public Parameter<Boolean> getVillaged() {
		return villaged;
	}
}
