package com.comphenix.xp.mods;

import org.bukkit.event.inventory.InventoryClickEvent;

import com.comphenix.xp.extra.ServiceProvider;
import com.comphenix.xp.listeners.PlayerInteractionListener;
import com.comphenix.xp.lookup.ItemQuery;

/**
 * Represents a registry of custom block providers. 
 * <p>
 * The default service will be the first to process inventory events.
 * 
 * @author Kristian
 */
public class CustomBlockProviders extends ServiceProvider<BlockService> {

	private PlayerInteractionListener lastInteraction;
	
	public CustomBlockProviders() {
		super(StandardBlockService.NAME);
	}
	
	/**
	 * Processes the given inventory click event with every registered block service,
	 * starting with the default block service.
	 * @param event - inventory click event to process.
	 * @param block - last right-clicked block.
	 */
	public BlockResponse processInventoryClick(InventoryClickEvent event, ItemQuery block) {
		
		BlockService def = getDefaultService();
		BlockResponse response = null;
		
		// Try the default service first
		if (isEnabled(def)) {
			response = def.processClickEvent(event, block);
		}
		
		if (BlockResponse.isSuccessful(response))
			return response;
		
		// See if any other service can do it
		for (BlockService service : getEnabledServices()) {
			if (service != def) {

				response = service.processClickEvent(event, block);
				
				// Check for success
				if (BlockResponse.isSuccessful(response))
					return response;
			}
		}
		
		// Failure
		return new BlockResponse(false);
	}
	
	public PlayerInteractionListener getLastInteraction() {
		return lastInteraction;
	}

	public void setLastInteraction(PlayerInteractionListener lastInteraction) {
		this.lastInteraction = lastInteraction;
	}
	
	/**
	 * Retrieves the standard block service.
	 * @return Standard block service.
	 */
	public StandardBlockService getStandardBlockService() {
		return (StandardBlockService) getByName(StandardBlockService.NAME);
	}
}
