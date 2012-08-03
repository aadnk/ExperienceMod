package com.comphenix.xp.rewards.xp;

import java.util.Random;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.comphenix.xp.Range;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourceHolder;

public class CurrencyFactory implements ResourceFactory {
	private Range range;
	private double multiplier;
	
	public CurrencyFactory(Range range) {
		this(range, 1);
	}
	
	private CurrencyFactory(Range range, double multiplier) {
		if (range == null)
			throw new NullArgumentException("range");
		
		this.range = range;
		this.multiplier = multiplier;
	}
	
	public CurrencyFactory(double value) {
		this(new Range(value));
	}
	
	@Override
	public ResourceHolder getResource(Random rnd) {
		return getResource(rnd, 1);
	}
	
	@Override
	public ResourceHolder getResource(Random rnd, int count) {

		final int experience = getRange().sampleInt(rnd) * count;
		
		// Doesn't have to be more complicated than this
		return new ExperienceHolder(experience);
	}
	
	public Range getRange() {
		return range.multiply(multiplier);
	}

	@Override
	public ResourceHolder getMinimum(int count) {
		return new CurrencyHolder(getRange().getMinimum() * count);
	}

	@Override
	public ResourceHolder getMaximum(int count) {
		return new CurrencyHolder(getRange().getMinimum() * count);
	}

	@Override
	public ResourceFactory withMultiplier(double newMultiplier) {
		return new CurrencyFactory(range, newMultiplier);
	}

	@Override
	public double getMultiplier() {
		return multiplier;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(range).
	            append(multiplier).
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

        CurrencyFactory other = (CurrencyFactory) obj;
        return new EqualsBuilder().
            append(range, other.range).
            append(multiplier, other.multiplier).
            isEquals();
	}
	
	@Override
	public String toString() {
		return range.toString() + " currency";
	}
}
