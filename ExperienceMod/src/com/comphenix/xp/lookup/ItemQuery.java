/*
 *  ExperienceMod - Bukkit server plugin for modifying the experience system in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.xp.lookup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.comphenix.xp.parser.Utility;
import com.google.common.collect.Lists;

/**
 * Generic immutable representation of an item/block query.
 * 
 * @author Kristian
 */
public class ItemQuery implements Query {
	
	private static List<Integer> noNumbers = new ArrayList<Integer>();
	
	private List<Integer> itemID;
	private List<Integer> durability;

	/**
	 * Universal query.
	 */
	public static ItemQuery fromAny() {
		return new ItemQuery(noNumbers, noNumbers);
	}	
	
	/**
	 * Creates a query from the given material and data.
	 * @param material - material to create from.
	 */
	public static ItemQuery fromAny(Material material) {
		return fromAny(material != null ? material.getId() : null, null);
	}
	
	/**
	 * Creates a query from the given material and data.
	 * @param material - material to create from.
	 * @param durability - durability to create from.
	 */
	public static ItemQuery fromAny(Material material, Integer durability) {
		return fromAny(material != null ? material.getId() : null, durability);
	}
	
	public static ItemQuery fromAny(Integer itemID, Integer durability) {
		return new ItemQuery(
				Utility.getElementList(itemID), 
				Utility.getElementList(durability)
		);
	}
	
	/**
	 * Creates a query from a given world block.
	 * @param block - block to create from.
	 */
	public static ItemQuery fromExact(Block block) {
		return fromExact(block.getTypeId(), (int) block.getData());
	}
	
	/**
	 * Extracts the item type and durability. Note that the item count property is ignored.
	 * @param stack - item type.
	 * @throws NullArgumentException if the stack is null.
	 */
	public static ItemQuery fromExact(ItemStack stack) {
		
		if (stack == null)
			throw new NullArgumentException("stack");
		
		return fromExact(stack.getTypeId(), (int) stack.getDurability());
	}
	
	public static ItemQuery fromExact(Integer itemID, Integer durability) {
		return new ItemQuery(
				Lists.newArrayList(itemID), 
				Lists.newArrayList(durability)
		);
	}
	
	public ItemQuery(List<Integer> itemID, List<Integer> durability) {
		this.itemID = itemID;
		this.durability = durability;
	}

	public List<Integer> getItemID() {
		return itemID;
	}

	public List<Integer> getDurability() {
		return durability;
	}

	public boolean hasItemID() {
		return itemID != null && !itemID.isEmpty();
	}
	
	public boolean hasDurability() {
		return durability != null && !durability.isEmpty();
	}
	
	public boolean hasSingleItem(Material item) {
		return hasSingleItem(item.getId(), null);
	}
	
	/**
	 * Determine if this query only contains the given item.
	 * @param id - id of the item.
	 * @param durability - durability of the item, or NULL to match all.
	 * @return TRUE if it does contain the given item, FALSE otherwise.
	 */
	public boolean hasSingleItem(Integer id, Integer durability) {
		// See if the item list contains this item only
		return hasItemID() && 
				this.itemID.size() == 1 && this.itemID.contains(id) && 
				(durability == null ||
				this.durability.size() == 1 && this.durability.contains(durability));
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(itemID).
	            append(durability).
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

        ItemQuery other = (ItemQuery) obj;
        return new EqualsBuilder().
            append(itemID, other.itemID).
            append(durability, other.durability).
            isEquals();
	}
	
	// We're only interested in the String representation
	private List<Object> getMaterials() {
		
		List<Object> materials = new ArrayList<Object>();
	
		if (itemID == null)
			return materials;
		
		// Map each integer to its corresponding material
		for (Integer id : itemID) {
			if (id != null) {
			
				Material material = Material.getMaterial(id);
				
				if (material != null) 
					materials.add(material);
				else
					materials.add(id);
			}
		}
	
		return materials;
	}

	/**
	 * Converts the given query to an item stack if possible.
	 * @return The converted item stack, or NULL if not possible.
	 */
	public ItemStack toItemStack(int amount) {

		int data = 0;
		
		if (containsSingleNonNull(itemID)) {
			
			// We'll treat no durability as zero
			if (containsSingleNonNull(durability)) {
				data = durability.get(0);
			} else if (durability.isEmpty()) {
				data = 0;
			} else {
				return null;
			}
			
			// Create the item stack
			return new ItemStack(itemID.get(0), amount, (short) data);
		}
		
		return null;
	}
	
	private boolean containsSingleNonNull(List<Integer> list) {
		return list != null && list.size() == 1 && !list.contains(null);
	}
	
	@Override
	public String toString() {
		
		String itemsText = StringUtils.join(getMaterials(), ", ");
		String durabilityText = StringUtils.join(durability, ", ");

		if (hasDurability())
			return String.format("%s|%s", itemsText, durabilityText);
		else
			return String.format("%s", itemsText);
	}
		
	@Override
	public Types getQueryType() {
		return Types.Items;
	}
	
	/**
	 * Determines if the given item stack is non-empty.
	 * @param stack - item stack to test.
	 * @return TRUE if it is non-null and non-empty, FALSE otherwise.
	 */
	public static boolean hasItems(ItemStack stack) {
		return stack != null && stack.getAmount() > 0;
	}
}
