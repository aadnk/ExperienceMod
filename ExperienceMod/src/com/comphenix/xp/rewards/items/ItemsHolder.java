package com.comphenix.xp.rewards.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import com.comphenix.xp.rewards.ResourceHolder;
import com.google.common.base.Objects;

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
				
		// Now, merge items correctly
		top: 
		for (ItemStack stack : otherItems.getRewards()) {
			// Find the next same item
			for (ItemStack existing : combined) {
				if (hasSameItem(stack, existing)) {
					existing.setAmount(existing.getAmount() + stack.getAmount());
					
					// Exit the current for-loop and skip ahead in the topmost for-loop.
					continue top;
				}
			}
			
			// Just add it to the end
			combined.add(stack);
		}
		
		return new ItemsHolder(combined);
	}

	private boolean hasSameItem(ItemStack a, ItemStack b) {
		return a.getTypeId() == b.getTypeId() &&
			   a.getDurability() == b.getDurability() && 
			   Objects.equal(a.getEnchantments(), b.getEnchantments());
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		for (ItemStack reward : rewards) {
			if (reward != null)
				result.append(reward.getType().name() + " x " + reward.getAmount());
			else
				result.append("NULL");
			
			result.append(", ");
		}
		
		// Remvoe the last two characters
		if (result.length() >= 2) {
			result.setLength(result.length() - 2);
		}
		
		return result.toString();
	}
}
