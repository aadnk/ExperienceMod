package com.comphenix.xp.lookup;

import java.util.HashSet;
import java.util.Set;

import com.comphenix.xp.Range;

public class ItemTree extends SearchTree<ItemQuery, Range> {

	private Parameter<Integer> itemID = new Parameter<Integer>();
	private Parameter<Integer> durability = new Parameter<Integer>();
	
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
}
