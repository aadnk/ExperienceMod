package com.comphenix.xp.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.comphenix.xp.Range;

public abstract class RangeTree<TKey> extends SearchTree<TKey, Range>{

	protected double multiplier;
	
	public RangeTree(double multiplier) {
		this.multiplier = multiplier;
	}
	
	public RangeTree(RangeTree<TKey> other, double multiplier) {
		this.multiplier = multiplier;
		this.flatten = other.flatten;
		this.paramCount = other.paramCount;
		this.currentID = other.currentID;
	}
	
	@Override
	public Range get(TKey element) {
		Range result = super.get(element);
		
		// Automatically include the multiplier
		if (result != null)
			return result.multiply(multiplier);
		else
			return result;
	}
	
	/**
	 * Returns a list of every stored range (scaled by experience) in this search tree.
	 * @return Every stored range.
	 */
	@Override
	public Collection<Range> getValues() {

		// Add multiplier
		List<Range> scaledValues = new ArrayList<Range>();
		
		for (Range range : super.getValues()) {
			scaledValues.add(range.multiply(multiplier));
		}
		
		return scaledValues;
	}
	
	public double getMultiplier() {
		return multiplier;
	}
}
