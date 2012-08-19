package com.comphenix.xp.rewards.xp;

import com.comphenix.xp.SampleRange;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourceHolder;

public class ExperienceFactory extends GenericFactory {
	
	public ExperienceFactory(SampleRange range) {
		this(range, 1);
	}
	
	public ExperienceFactory(double value) {
		this(new SampleRange(value));
	}

	private ExperienceFactory(SampleRange range, double newMultiplier) {
		super(range, newMultiplier);
	}
	
	@Override
	protected ResourceHolder constructFactory(int amount) {
		return new ExperienceHolder(amount);
	}
	
	@Override
	public ResourceFactory withMultiplier(double newMultiplier) {
		return new ExperienceFactory(range, newMultiplier);
	}
	
	@Override
	public String toString() {
		return range.toString() + " currency";
	}
}
