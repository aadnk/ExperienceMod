package com.comphenix.xp.mods;

import org.bukkit.event.inventory.InventoryClickEvent;

import com.comphenix.xp.extra.Service;
import com.comphenix.xp.lookup.ItemQuery;

/**
 * A handler for inventory click events on custom or standard blocks.
 * 
 * @author Kristian
 */
public interface BlockService extends Service {
	
	/**
	 * Called when a player has interacted with an inventory. 
	 * <p>
	 * The response is only considered a success if the result is non-null and successful.
	 * 
	 * @param event - the inventory event.
	 * @param block - the block the given player last right-clicked. Note that this may be any arbitrary block.
	 * @return The default or custom behavior to take.
	 */
	public BlockResponse processClickEvent(InventoryClickEvent event, ItemQuery block);
}
