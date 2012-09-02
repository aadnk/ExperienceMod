package com.comphenix.xp.rewards.xp;

import java.util.Collection;
import java.util.Random;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.comphenix.xp.expressions.NamedParameter;
import com.comphenix.xp.expressions.VariableFunction;
import com.comphenix.xp.extra.ConstantRandom;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourceHolder;

public abstract class GenericFactory implements ResourceFactory {
	protected VariableFunction range;

	public GenericFactory(VariableFunction range, double multiplier) {
		if (range == null)
			throw new NullArgumentException("range");
		
		this.range = range.withMultiplier(range.getMultiplier() * multiplier);
	}
	
	@Override
	public ResourceHolder getResource(Collection<NamedParameter> params, Random rnd) {
		return getResource(params, rnd, 1);
	}
	
	@Override
	public ResourceHolder getResource(Collection<NamedParameter> params, Random rnd, int count) {
		
		try {
			int resource = (int) (getRange().apply(rnd, params) * count);
			
			// Doesn't have to be more complicated than this
			return constructFactory(resource);
			
		} catch (Exception e) {
			// Might occur if, for instance, someone divides by zero
			throw new RuntimeException("Calculation error.", e);
		}
	}
	
	@Override
	public ResourceHolder getMinimum(Collection<NamedParameter> params, int count) {
		return getResource(params, ConstantRandom.MINIMUM, count);
	}

	@Override
	public ResourceHolder getMaximum(Collection<NamedParameter> params, int count) {
		return getResource(params, ConstantRandom.MAXIMUM, count);
	}

	public VariableFunction getRange() {
		return range;
	}
	
	@Override
	public double getMultiplier() {
		return range.getMultiplier();
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
            isEquals();
	}
}
