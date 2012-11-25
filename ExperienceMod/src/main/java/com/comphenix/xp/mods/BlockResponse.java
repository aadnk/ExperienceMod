package com.comphenix.xp.mods;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
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
	
	/**
	 * A response indicating success, but that that ExperienceMod should leave everything up to the caller.
	 */
	public static BlockResponse CUSTOM = new BlockResponse((InventoryType) null, "", "");

	private boolean success;
	private boolean forceHack;
	
	private boolean overrideCurrent;
	private ItemStack currentItem;
	private InventoryType defaultBehavior;
	
	private String actionType;
	private String permission;
	
	public BlockResponse(String actionType, String permission) {
		this(true, InventoryType.WORKBENCH, actionType, permission);
	}
	
	public BlockResponse(InventoryType defaultBehavior, String actionType, String permission) {
		this(true, defaultBehavior, actionType, permission);
	}
	
	public BlockResponse(boolean success) {
		this.success = success;
	}
	
	public BlockResponse(boolean success, InventoryType defaultBehavior, String actionType, String permission) {
		this.success = success;
		this.defaultBehavior = defaultBehavior;
		this.actionType = actionType;
		this.permission = permission;
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
	 * Sets the default behavior that will handle the inventory event.
	 * @param defaultBehavior - new default behavior, or NULL for a custom behavior.
	 */
	public void setDefaultBehavior(InventoryType defaultBehavior) {
		this.defaultBehavior = defaultBehavior;
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
	 * Whether or not to always derive the crafted items from how the player inventory changed.
	 * @return TRUE to use the hack, FALSE otherwise.
	 */
	public boolean isForceHack() {
		return forceHack;
	}

	/**
	 * Sets whether or not to always derive the crafted items from how the player inventory changed.
	 * @param forceHack - TRUE to force the hack method, FALSE otherwise.
	 */
	public void setForceHack(boolean forceHack) {
		this.forceHack = forceHack;
	}
	
	/**
	 * Retrieves the current action type (or trigger).
	 * @return Action type.
	 */
	public String getActionType() {
		return actionType;
	}

	/**
	 * Sets the current action type.
	 * @param actionType - new action type.
	 */
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
	/**
	 * Whether or not to override the default current item in InventoryClickEvent.
	 * @return TRUE if it has been overriden, FALSE otherwise.
	 */
	public boolean isOverrideCurrent() {
		return overrideCurrent;
	}

	/**
	 * Sets whether or not to override the default current item in InventoryClickEvent.
	 * @param overrideCurrent - TRUE to override it, FALSE otherwise.
	 */
	public void setOverrideCurrent(boolean overrideCurrent) {
		this.overrideCurrent = overrideCurrent;
	}

	/**
	 * The current item to override with InventoryClickEvent.
	 * @return Current overriden item.
	 */
	public ItemStack getCurrentItem() {
		return currentItem;
	}

	/**
	 * Sets the current overriden item (if isOverrideCurrent is TRUE). 
	 * @param currentItem - the new overriden item.
	 */
	public void setCurrentItem(ItemStack currentItem) {
		this.currentItem = currentItem;
	}
	
	/**
	 * Retrieves the permission this player must have in order to be rewarded.
	 * @return The permission the player must have.
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * Sets the permission the player must have in order to be rewarded.
	 * @param permission - the new permission the player must have.
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}

	/**
	 * If override current is FALSE, return the current item in the given event. If override
	 * current is TRUE, return the current item in the block response.
	 * @param event - event to draw from.
	 * @return The current event, when taking into account overriden items.
	 */
	public ItemStack getOverridableCurrentItem(InventoryClickEvent event) {
		if (isOverrideCurrent())
			return getCurrentItem();
		else
			return event.getCurrentItem();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(success).
	            append(forceHack).
	            append(overrideCurrent).
	            append(currentItem).
	            append(defaultBehavior).
	            append(actionType).
	            append(permission).
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
            append(forceHack, other.forceHack).
            append(overrideCurrent, other.overrideCurrent).
            append(currentItem, other.currentItem).
            append(defaultBehavior, other.defaultBehavior).
            append(actionType, other.actionType).
            append(permission, other.permission).
            isEquals();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);  
	}
}
