package com.comphenix.xp.rewards.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import com.comphenix.xp.rewards.ResourceHolder;

public class ItemsHolder implements ResourceHolder {

	/**
	 * Unique enum name of the items resource.
	 */
	public static final String RESOURCE_NAME = "ITEMS";
	
	private List<ItemStack> rewards;
	
	public ItemsHolder() {
		this.rewards = new ArrayList<ItemStack>();
	}
	
	public ItemsHolder(List<ItemStack> items) {
		this.rewards = items;
	}
	
	public ItemsHolder(ItemStack item) {
		this();
		rewards.add(item);
	}
	
	@Override
	public int getAmount() {
		int count = 0;
		
		// Count the number of items in each stack
		for (ItemStack stack : rewards) {
			if (stack != null)
				count += stack.getAmount();
		}
		
		return count;
	}

	/**
	 * Get the current list of rewards.
	 * @return List of rewards.
	 */
	public List<ItemStack> getRewards() {
		return rewards;
	}

	@Override
	public String getName() {
		return RESOURCE_NAME;
	}
	
	@Override
	public ResourceHolder add(ResourceHolder other) {
		if (!(other instanceof ItemsHolder))
			throw new IllegalArgumentException("Must be of the same type.");
			
		List<ItemStack> combined = new ArrayList<ItemStack>(getRewards());
		ItemsHolder otherItems = (ItemsHolder) other;
				
		combined.addAll(otherItems.getRewards());
		return new ItemsHolder(combined);
	}

	@Override
	public String toString() {
		// Rely on ItemStack.toString()
		return StringUtils.join(rewards, ", ");
	}
}
