package com.comphenix.xp.lookup;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * Generic immutable representation of an item/block query.
 * 
 * @author Kristian
 */
public class ItemQuery implements Query {
	
	private Integer itemID;
	private Integer durability;

	/**
	 * Universal query.
	 */
	public ItemQuery() {
	}
	
	/**
	 * Creates a query from a given world block.
	 * @param block - block to create from.
	 */
	public ItemQuery(Block block) { 
		this(block.getTypeId(), (int) block.getData());
	}
	
	/**
	 * Extracts the item type and durability. Note that the item count property is ignored.
	 * @param stack - item type.
	 */
	public ItemQuery(ItemStack stack) {
		this(stack.getTypeId(), (int) stack.getDurability());
	}
	
	/**
	 * Creates a query from the given material.
	 * @param material material to create from.
	 */
	public ItemQuery(Material material) {
		this(material.getId(), 0);
	}
	
	/**
	 * Creates a query from the given material and data.
	 * @param material material to create from.
	 */
	public ItemQuery(Material material, Integer data) {
		this(material != null ? material.getId() : null, 
			 data);
	}
	
	public ItemQuery(Integer itemID, Integer durability) {
		this.itemID = asPositiveInt(itemID);
		this.durability = asPositiveInt(durability);
	}

	public Integer getItemID() {
		return itemID;
	}

	public Integer getDurability() {
		return durability;
	}

	public boolean hasItemID() {
		return itemID != null;
	}
	
	public boolean hasDurability() {
		return durability != null;
	}
	
	@Override
	public String toString() {
		Material material = null;

		if (hasItemID())
			material = Material.getMaterial(itemID);
		
		if (hasDurability())
			return String.format("%s|%d", material == null ? itemID : material, durability);
		else
			return String.format("%s", material == null ? itemID : material);
	}
	
	public static ItemQuery fromStack(ItemStack stack) {
		if (stack == null)
			return null;
		else
			return new ItemQuery(stack);
	}

	private Integer asPositiveInt(Integer value) {
		if (value == null)
			return null;
		else
			return value >= 0 ? value : null;
	}
	
	@Override
	public Types getQueryType() {
		return Types.Items;
	}
}
