package com.comphenix.xp.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.comphenix.xp.Action;

public abstract class ActionTree<TKey> extends SearchTree<TKey, Action>{

	protected double multiplier;
	
	public ActionTree(double multiplier) {
		this.multiplier = multiplier;
	}
	
	public ActionTree(ActionTree<TKey> other, double multiplier) {
		this.multiplier = multiplier;
		this.flatten = other.flatten;
		this.paramCount = other.paramCount;
		this.currentID = other.currentID;
	}
	
	@Override
	public Action get(TKey element) {
		Action result = super.get(element);
		
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
	public Collection<Action> getValues() {

		// Add multiplier
		List<Action> scaledValues = new ArrayList<Action>();
		
		for (Action action : super.getValues()) {
			scaledValues.add(action.multiply(multiplier));
		}
		
		return scaledValues;
	}
	
	public double getMultiplier() {
		return multiplier;
	}
}
