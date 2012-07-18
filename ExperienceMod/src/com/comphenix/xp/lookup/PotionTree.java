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

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.potion.PotionType;
import com.comphenix.xp.Action;

public class PotionTree extends ActionTree<PotionQuery> implements Multipliable<PotionTree> {

	protected Parameter<PotionType> type;
	protected Parameter<Integer> level;
	protected Parameter<Boolean> extended;
	protected Parameter<Boolean> splash;

	// For cloning
	public PotionTree(PotionTree other, double newMultiplier) { 
		super(other, newMultiplier);
		
		if (other == null)
			throw new IllegalArgumentException("other");
		
		this.level = other.level;
		this.extended = other.extended;
		this.splash = other.splash;
	}
	
	public PotionTree(double multiplier) {
		super(multiplier);
		this.type = new Parameter<PotionType>();
		this.level = new Parameter<Integer>();
		this.extended = new Parameter<Boolean>();
		this.splash = new Parameter<Boolean>();
	}
	
	@Override
	public PotionTree withMultiplier(double newMultiplier) {
		return new PotionTree(this, newMultiplier);
	}
	
	@Override
	protected Integer putFromParameters(PotionQuery source, Integer id) {

		int paramCount = 0;
		
		// Add parameters
		if (source.hasType()) {
			type.put(source.getType(), id); paramCount++;
		}
		
		if (source.hasLevel()) {
			level.put(source.getLevel(), id); paramCount++;
		}
		
		if (source.hasExtended()) {
			extended.put(source.getExtended(), id); paramCount++;
		}
		
		if (source.hasSplash()) {
			splash.put(source.getSplash(), id); paramCount++;
		}
		
		return paramCount;
	}

	@Override
	protected Set<Integer> getFromParameters(PotionQuery source) {

		Set<Integer> candidates = new HashSet<Integer>(flatten.keySet());
		
		// Filter by parameters
		if (source.hasType()) {
			type.retain(candidates, source.getType());
		}
		
		if (source.hasLevel()) {
			level.retain(candidates, source.getLevel());
		}
		
		if (source.hasExtended()) {
			extended.retain(candidates, source.getExtended());
		}
			
		if (source.hasSplash()) {
			splash.retain(candidates, source.getSplash());
		}
			
		return candidates;
	}
	
	// I always end up making these ugly hacks. Damn it.
	public ItemTree getItemQueryAdaptor() {
		return new ItemTree() {
			@Override
			public Action get(ItemQuery element) {
				return PotionTree.this.get(new PotionQuery(element));
			}

			@Override
			public boolean containsKey(ItemQuery element) {
				return PotionTree.this.containsKey(new PotionQuery(element));
			}
			
			@Override
			public Integer put(ItemQuery element, Action value) {
				throw new NotImplementedException();
			}
			
			@Override
			protected Integer putFromParameters(ItemQuery source, Integer id) {
				throw new NotImplementedException();
			}

			@Override
			protected Set<Integer> getFromParameters(ItemQuery source) {
				throw new NotImplementedException();
			}
		};
	}
	
	@Override
	protected void putAllParameters(SearchTree<PotionQuery, Action> other, Integer offset) {
		PotionTree tree = (PotionTree) other;

		type.putAll(tree.type, offset);
		level.putAll(tree.level, offset);
		extended.putAll(tree.extended, offset);
		splash.putAll(tree.splash, offset);
	}
}
