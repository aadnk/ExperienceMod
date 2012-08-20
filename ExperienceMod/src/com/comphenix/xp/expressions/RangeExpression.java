package com.comphenix.xp.expressions;

import java.util.Collection;
import java.util.Random;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.comphenix.xp.SampleRange;

public class RangeExpression extends VariableFunction {

	protected SampleRange range;
	protected double multiplier;
	
	public RangeExpression(SampleRange range, double multiplier) {
		this.range = range;
		this.multiplier = multiplier;
	}
	
	/**
	 * Creates a simple range container with a multiplier of one.
	 * @param value - value of the range.
	 */
	public RangeExpression(double value) {
		this(new SampleRange(value), 1);
	}

	/**
	 * Creates a simple range container with a multiplier of one.
	 * @param start - start value of range.
	 * @param end - end value of range.
	 */
	public RangeExpression(double start, double end) {
		this(new SampleRange(start, end), 1);
	}

	@Override
	public double apply(Random rnd, Collection<NamedParameter> params) throws Exception {
		return range.sampleInt(rnd);
	}

	@Override
	public VariableFunction withMultiplier(double newMultiplier) {
		return new RangeExpression(range, newMultiplier);
	}

	@Override
	public double getMultiplier() {
		return multiplier;
	}
	
	@Override
	public String toString() {
		return range.multiply(multiplier).toString();
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

        RangeExpression other = (RangeExpression) obj;
        return new EqualsBuilder().
            append(range, other.range).
            append(multiplier, other.multiplier).
            isEquals();
	}
}
