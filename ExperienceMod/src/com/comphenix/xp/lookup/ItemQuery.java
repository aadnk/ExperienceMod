package com.comphenix.xp.lookup;

/**
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.comphenix.xp.parser.Parsing;

/**
 * Generic immutable representation of an item/block query.
 * 
 * @author Kristian
 */
public class ItemQuery implements Query {
	
	private List<Integer> itemID;
	private List<Integer> durability;

	/**
	 * Universal query.
	 */
	public ItemQuery() {
		itemID = new ArrayList<Integer>();
		durability = new ArrayList<Integer>();
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
		this(material.getId(), null);
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
		this.itemID = Parsing.getElementList(itemID);
		this.durability = Parsing.getElementList(durability);
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
		
		// See if the item list contains this item only
		return hasItemID() && itemID.size() == 1 && itemID.contains(item.getId());
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

	@Override
	public String toString() {
		
		String itemsText = StringUtils.join(getMaterials(), ", ");
		String durabilityText = StringUtils.join(durability, ", ");

		if (hasDurability())
			return String.format("%s|%d", itemsText, durabilityText);
		else
			return String.format("%s", itemsText);
	}
	
	public static ItemQuery fromStack(ItemStack stack) {
		if (stack == null)
			return null;
		else
			return new ItemQuery(stack);
	}
	
	@Override
	public Types getQueryType() {
		return Types.Items;
	}
}
