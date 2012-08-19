package com.comphenix.xp.rewards.xp;

import java.util.Random;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.comphenix.xp.SampleRange;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourceHolder;

public abstract class GenericFactory implements ResourceFactory {
	protected SampleRange range;
	protected double multiplier;
	
	public GenericFactory(SampleRange range, double multiplier) {
		if (range == null)
			throw new NullArgumentException("range");
		
		this.range = range;
		this.multiplier = multiplier;
	}
	
	@Override
	public ResourceHolder getResource(Random rnd) {
		return getResource(rnd, 1);
	}
	
	@Override
	public ResourceHolder getResource(Random rnd, int count) {

		final int resource = getRange().sampleInt(rnd) * count;
		
		// Doesn't have to be more complicated than this
		return constructFactory(resource);
	}
	
	@Override
	public ResourceHolder getMinimum(int count) {
		return constructFactory(getRange().getMinimum() * count);
	}

	@Override
	public ResourceHolder getMaximum(int count) {
		return constructFactory(getRange().getMinimum() * count);
	}

	public SampleRange getRange() {
		return range.multiply(multiplier);
	}
	
	@Override
	public double getMultiplier() {
		return multiplier;
	}
	
	/**
	 * Constructs this factories' resource holder given an integer amount.
	 * @param amount - integer amount.
	 * @return Resource holder.
	 */
	protected abstract ResourceHolder constructFactory(int amount);
	
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

        GenericFactory other = (GenericFactory) obj;
        return new EqualsBuilder().
            append(range, other.range).
            append(multiplier, other.multiplier).
            isEquals();
	}
}
