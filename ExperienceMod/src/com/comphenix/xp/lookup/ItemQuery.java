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
import java.util.Arrays;
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
	private static List<ItemQuery> noQueries = new ArrayList<ItemQuery>();
	private static List<ItemQuery> matchNone = Arrays.asList((ItemQuery) null);
	
	private List<Integer> itemID;
	private List<Integer> durability;
	private List<ItemQuery> ingredients;

	/**
	 * Universal query.
	 */
	public static ItemQuery fromAny() {
		return new ItemQuery(noNumbers, noNumbers, noQueries);
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
				Utility.getElementList(durability),
				noQueries
		);
	}
	
	public static ItemQuery fromAny(Integer itemID, Integer durability, ItemStack[] ingredients) {
		return new ItemQuery(
				Utility.getElementList(itemID), 
				Utility.getElementList(durability),
				getIngredientList(ingredients)
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
	
	/**
	 * Extracts the item type, durability and source ingredients.
	 * @param stack - item type.
	 * @throws NullArgumentException if the stack is null.
	 */
	public static ItemQuery fromExact(ItemStack stack, ItemStack[] ingredients) {
		
		if (stack == null)
			throw new NullArgumentException("stack");
		if (ingredients == null)
			throw new NullArgumentException("ingredients");
		
		List<ItemQuery> list = getIngredientList(ingredients);
		
		// Convert the array into an ingredient list
		return fromExact(
				(int) stack.getTypeId(), 
				(int) stack.getDurability(),
				
				// Make sure we only select queries without ingredient specifier in that case
				list.isEmpty() ? matchNone : list 
		);
	}
	
	public static ItemQuery fromExact(Integer itemID, Integer durability) {
		return fromExact(itemID, durability, matchNone);
	}
	
	public static ItemQuery fromExact(Integer itemID, Integer durability, List<ItemQuery> ingredients) {
		return new ItemQuery(
				Lists.newArrayList(itemID), 
				Lists.newArrayList(durability),
				ingredients
		);
	}
	
	/**
	 * Converts an array of item stacks into a corresponding list of item queries.
	 * @param ingredients - array of item stacks.
	 * @return List of item queries constructed form each item stack in the array.
	 */
	private static List<ItemQuery> getIngredientList(ItemStack[] ingredients) {
		
		List<ItemQuery> listIngredients = new ArrayList<ItemQuery>();
		
		// Convert each ingredient into a item query
		for (ItemStack stack : ingredients) {
			listIngredients.add(fromExact(stack));
		}
		
		return listIngredients;
	}
	
	public ItemQuery(List<Integer> itemID, List<Integer> durability) {
		this(itemID, durability, noQueries);
	}
	
	public ItemQuery(List<Integer> itemID, List<Integer> durability, List<ItemQuery> ingredients) {
		this.itemID = itemID;
		this.durability = durability;
		this.ingredients = ingredients;
	}

	public List<Integer> getItemID() {
		return itemID;
	}

	public List<Integer> getDurability() {
		return durability;
	}
	
	public List<ItemQuery> getIngredients() {
		return ingredients;
	}
	
	/**
	 * This method is only used during the construction of the item query. Sets the list of ingredients to match.
	 * @param ingredients - list of ingredients to mathc.
	 */
	public void setIngredients(List<ItemQuery> ingredients) {
		this.ingredients = ingredients;
	}

	public boolean hasItemID() {
		return itemID != null && !itemID.isEmpty();
	}
	
	public boolean hasDurability() {
		return durability != null && !durability.isEmpty();
	}
	
	public boolean hasIngredients() {
		return ingredients != null && !ingredients.isEmpty();
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
	            append(ingredients).
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
            append(ingredients, other.ingredients).
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
}
