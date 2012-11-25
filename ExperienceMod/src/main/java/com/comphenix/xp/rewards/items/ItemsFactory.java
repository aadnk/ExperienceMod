package com.comphenix.xp.rewards.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.inventory.ItemStack;

import com.comphenix.xp.expressions.NamedParameter;
import com.comphenix.xp.expressions.VariableFunction;
import com.comphenix.xp.extra.ConstantRandom;
import com.comphenix.xp.lookup.ItemQuery;
import com.comphenix.xp.rewards.ResourceFactory;
import com.comphenix.xp.rewards.ResourceHolder;

public class ItemsFactory implements ResourceFactory {

	// Going above this seems ridiculous
	public static final int MAX_STACKS = 100;
	
	private List<Entry> entries = new ArrayList<Entry>();
	private double multiplier;
	
	public ItemsFactory() {
		this(new ArrayList<Entry>(), 1);
	}
		
	public ItemsFactory(double multiplier) {
		this(new ArrayList<Entry>(), multiplier);
	}
	
	private ItemsFactory(List<Entry> entries, double multiplier) {
		this.entries = entries;
		this.multiplier = multiplier;
	}
	
	public void addItems(ItemQuery item, VariableFunction range) {
		if (range == null)
			throw new NullArgumentException("range");
		if (item == null)
			throw new NullArgumentException("item");
		if (!item.hasItemID())
			throw new IllegalArgumentException("Item must have an ID.");
		if (getMaximum(range) > 64 * MAX_STACKS)
			throw new IllegalArgumentException(String.format("Range cannot exceed %d.", 64 * MAX_STACKS));
		
		entries.add(new Entry(item, range));
	}
	
	private double getMaximum(VariableFunction range) {
		try {
			// Estimate the maximum
			return range.apply(ConstantRandom.MAXIMUM, null);
		} catch (Exception e) {
			return 0;
		}
	}
	
	@Override
	public ResourceHolder getResource(Collection<NamedParameter> params, Random rnd, int count) {

		List<ItemStack> stacks = new ArrayList<ItemStack>();
		
		// Find the correct item ID and metadata
		for (Entry entry : entries) {
			try {
				fillInventory(params, rnd, stacks, entry, count);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return new ItemsHolder(stacks);
	}
	
	private void fillInventory(Collection<NamedParameter> params, Random rnd, List<ItemStack> inventory, Entry entry, int count) throws Exception {
		
		int itemID = RandomSampling.getRandomElement(entry.getItemID());
		int data = RandomSampling.getRandomElement(entry.getDurability(), 0);
		int amount = (int) (entry.getRange().apply(rnd, params) * count);

		while (amount > 0) {
			ItemStack stack = new ItemStack(itemID, 1, (short) data);
			Integer size = stack.getMaxStackSize();
			
			if (amount / size > MAX_STACKS) {
				// NO, stop!
				return;
			}
			
			// Now, see how big this stack can become
			stack.setAmount(Math.min(amount, size));
			
			amount -= stack.getAmount();
			inventory.add(stack);
		}
	}
	
	@Override
	public ResourceFactory withMultiplier(double newMultiplier) {
		return new ItemsFactory(entries, newMultiplier);
	}

	@Override
	public double getMultiplier() {
		return multiplier;
	}

	@Override
	public ResourceHolder getResource(Collection<NamedParameter> params, Random rnd) {
		// Default value
		return getResource(params, rnd, 1);
	}
	
	@Override
	public ResourceHolder getMinimum(Collection<NamedParameter> params, int count) {
		
		return getResource(params, ConstantRandom.MINIMUM, count);
	}

	@Override
	public ResourceHolder getMaximum(Collection<NamedParameter> params, int count) {

		return getResource(params, ConstantRandom.MAXIMUM, count);
	}
	
	@Override
	public String toString() {
		return StringUtils.join(entries, ", ");
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(multiplier).
	            append(entries).
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

        ItemsFactory other = (ItemsFactory) obj;
        return new EqualsBuilder().
            append(multiplier, other.multiplier).
            append(entries, other.entries).
            isEquals();
	}
	
	// An item and the amount to award
	private class Entry {
		private ItemQuery item;
		private VariableFunction range;
		
		public Entry(ItemQuery item, VariableFunction range) {
			this.item = item;
			this.range = range;
		}

		public VariableFunction getRange() {
			return range;
		}
		
		public List<Integer> getItemID() {
			return item.getItemID();
		}
		
		public List<Integer> getDurability() {
			return item.getDurability();
		}
		
		@Override
		public String toString() {
			return String.format("%s %s", range, item);
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 31).
		            append(item).
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

	        Entry other = (Entry) obj;
	        return new EqualsBuilder().
	            append(item, other.item).
	            append(range, other.range).
	            isEquals();
		}
	}
}
