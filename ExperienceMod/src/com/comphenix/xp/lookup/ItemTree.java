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

import com.comphenix.xp.Action;

public class ItemTree extends ActionTree<ItemQuery> implements Multipliable<ItemTree> {

	protected Parameter<Integer> itemID;
	protected Parameter<Integer> durability; 

	// Only used by the hack in PotionTree.
	ItemTree() {
		super(1);
	}
	
	// For cloning
	public ItemTree(ItemTree other, double newMultiplier) {
		super(other, newMultiplier);
		
		if (other == null)
			throw new IllegalArgumentException("other");
		
		this.itemID = other.itemID;
		this.durability = other.durability;
	}
	
	public ItemTree(double multiplier) {
		super(multiplier);
		this.itemID = new Parameter<Integer>();
		this.durability = new Parameter<Integer>();
	}

	@Override
	public ItemTree withMultiplier(double newMultiplier) {
		return new ItemTree(this, newMultiplier);
	}
	
	@Override
	protected Integer putFromParameters(ItemQuery source, Integer id) {

		int paramCount = 0;
	
		if (source.hasItemID()) {
			itemID.put(source.getItemID(), id); paramCount++;
		}
		
		if (source.hasDurability()) {
			durability.put(source.getDurability(), id); paramCount++;
		}

		return paramCount;
	}

	@Override
	protected Set<Integer> getFromParameters(ItemQuery source) {

		// Begin with the item IDs this can correspond to
		Set<Integer> candidates = new HashSet<Integer>(flatten.keySet());
		
		if (source.hasItemID())
			itemID.retain(candidates, source.getItemID());
		
		// Remove items that contain conflicting durability
		if (source.hasDurability())
			durability.retain(candidates, source.getDurability());
		
		// Any remaining items will be sorted by specificity
		return candidates;
	}

	@Override
	protected void putAllParameters(SearchTree<ItemQuery, Action> other, Integer offset) {

		ItemTree tree = (ItemTree) other;

		itemID.putAll(tree.itemID, offset);
		durability.putAll(tree.durability, offset);
	}
}
