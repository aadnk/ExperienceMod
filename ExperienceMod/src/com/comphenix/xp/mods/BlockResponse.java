package com.comphenix.xp.mods;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a immutable block service response. 
 * 
 * @author Kristian
 */
public class BlockResponse {

	/**
	 * A response indicating failure.
	 */
	public static BlockResponse FAILURE = new BlockResponse(false);
	
	private boolean success;
	private InventoryType defaultBehavior;
	private ItemStack overrideCurrentItem;
	
	public BlockResponse(InventoryType defaultBehavior) {
		this(true, defaultBehavior);
	}
	
	public BlockResponse(boolean success) {
		this.success = success;
	}
	
	public BlockResponse(boolean success, InventoryType defaultBehavior) {
		this.success = success;
		this.defaultBehavior = defaultBehavior;
	}

	/**
	 * Whether or not this block provider can handle the current inventory event.
	 * @return TRUE if it can, FALSE otherwise.
	 */
	public boolean isSuccess() {
		return success;
	}
	
	/**
	 * Retrieves the default behavior that will handle the inventory event.
	 * @return Default behavior, or NULL for a custom behavior.
	 */
	public InventoryType getDefaultBehavior() {
		return defaultBehavior;
	}
	
	/**
	 * Whether or not this response contains a default behavior.
	 * @return TRUE if it contains a default behavior, FALSE if it contains a custom behavior.
	 */
	public boolean hasDefaultBehavior() {
		return defaultBehavior != null;
	}
	
	/**
	 * Determines if a given block response is successful.
	 * @param response - block response to test.
	 * @return TRUE if the response is non-null and successful.
	 */
	public static boolean isSuccessful(BlockResponse response) {
		return response != null && response.isSuccess();
	}

	/**
	 * Contains the item that will override the current item in InventoryClickEvent.
	 * @return The item to override InventoryClickEvent, or NULL if override should take place.
	 */
	public ItemStack getOverrideCurrentItem() {
		return overrideCurrentItem;
	}

	/**
	 * If non-null, overrides the current item in InventoryClickEvent.
	 * @param overrideCurrentItem - item to override InventoryClickEvent.
	 */
	public void setOverrideCurrentItem(ItemStack overrideCurrentItem) {
		this.overrideCurrentItem = overrideCurrentItem;
	}
	
	/**
	 * Determines if the current item in InventoryClickEvent has been overriden.
	 * @return TRUE if it has been overriden, FALSE otherwise.
	 */
	public boolean hasOverridenCurrentItem() {
		return overrideCurrentItem != null;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(success).
	            append(defaultBehavior).
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

        BlockResponse other = (BlockResponse) obj;
        return new EqualsBuilder().
            append(success, other.success).
            append(defaultBehavior, other.defaultBehavior).
            isEquals();
	}
	
	@Override
	public String toString() {
		return String.format("[Success: %s, Behavior: %s]", success, defaultBehavior);
	}
}
