package com.comphenix.xp.rewards.xp;

import java.util.Random;

import com.comphenix.xp.Range;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourceHolder;

public class ExperienceFactory implements ResourceFactory {

	/**
	 * Unique enum name of the experience resource.
	 */
	public static final String RESOURCE_NAME = "EXP";
	
	private Range range;
		
	public ExperienceFactory(Range range) {
		this.range = range;
	}
	
	@Override
	public ResourceHolder getResource(Random rnd) {
		return getResource(rnd, 1);
	}
	
	@Override
	public ResourceHolder getResource(Random rnd, int count) {

		final int experience = range.sampleInt(rnd) * count;
		
		// Doesn't have to be more complicated than this
		return new ResourceHolder() {
			@Override
			public String getName() {
				return RESOURCE_NAME;
			}
			
			@Override
			public int getAmount() {
				return experience;
			}
		};
	}
	
	public Range getRange() {
		return range;
	}
}
