package com.comphenix.xp.rewards.xp;

import com.comphenix.xp.SampleRange;
import com.comphenix.xp.expressions.VariableFunction;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourceHolder;

/**
 * Represents a factory that generates currency rewards.
 * 
 * @author Kristian
 */
public class CurrencyFactory extends GenericFactory {

	public CurrencyFactory(VariableFunction range) {
		this(range, 1);
	}
	
	public CurrencyFactory(double value) {
		this(VariableFunction.fromRange(new SampleRange(value)));
	}

	private CurrencyFactory(VariableFunction range, double newMultiplier) {
		super(range, newMultiplier);
	}
	
	@Override
	protected ResourceHolder constructFactory(int amount) {
		return new CurrencyHolder(amount);
	}
	
	@Override
	public ResourceFactory withMultiplier(double newMultiplier) {
		return new CurrencyFactory(range, newMultiplier);
	}
	
	@Override
	public String toString() {
		return range.toString() + " currency";
	}
}
