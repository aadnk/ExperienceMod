package com.comphenix.xp.rewards.xp;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.comphenix.xp.rewards.ResourceHolder;

public class CurrencyHolder implements ResourceHolder {
	/**
	 * Unique ENUM name of the currency resource.
	 */
	public static final String RESOURCE_NAME = "CURRENCY";
	
	protected int amount;

	public CurrencyHolder(int amount) {
		this.amount = amount;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public String getName() {
		return RESOURCE_NAME;
	}

	@Override
	public ResourceHolder add(ResourceHolder other) {
		if (other == null)
			throw new NullArgumentException("other");
		if (!(other instanceof CurrencyHolder))
			throw new IllegalArgumentException("Must be of the same type.");
		
		// Just add the values together
		return new CurrencyHolder(getAmount() + other.getAmount());
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(getAmount()).
	            append(getName()).
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

        CurrencyHolder other = (CurrencyHolder) obj;
        return new EqualsBuilder().
            append(amount, other.amount).
            isEquals();
	}
	
	@Override
	public String toString() {
		return Integer.toString(amount);
	}
}
