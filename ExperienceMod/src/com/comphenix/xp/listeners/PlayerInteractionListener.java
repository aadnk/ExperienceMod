package com.comphenix.xp.listeners;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.comphenix.xp.lookup.ItemQuery;

/**
 * Keeps track of a player's last block interaction.
 * 
 * @author Kristian
 */
public class PlayerInteractionListener implements PlayerCleanupListener, Listener {

	// Last clicked block
	private Map<String, ClickEvent> lastRightClicked = new HashMap<String, ClickEvent>();

	// Last clicked event
	private class ClickEvent {
		// public org.bukkit.event.block.Action ...
		public long time;
		public ItemQuery block;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		
		// Make sure this is a valid block right-click event
		if (player != null && event.hasBlock() && 
				event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
		
			// Store relevant information
			ClickEvent click = new ClickEvent();
			click.block = ItemQuery.fromExact(event.getClickedBlock());
			click.time = System.currentTimeMillis();
			
			// Store this block (by copy, so we don't keep chunks in memory)
			lastRightClicked.put(player.getName(), click);
		}
	}
	
	/**
	 * Retrieves the given player's most recent right click event.
	 * @param player - the player whose interaction we're looking for.
	 * @param maxAge - the maximum age (in milliseconds) of the action. NULL indicates infinity.
	 * @return The most recent action within the given limit, or NULL if no such action can be found.
	 */
	public ItemQuery getLastRightClick(Player player, Integer maxAge) {
		if (player == null)
			throw new NullArgumentException("player");
		
		ClickEvent last = lastRightClicked.get(player.getName());
		
		// Make sure we're not outside the age limit
		if (last != null && (
				 maxAge == null ||
				 last.time + maxAge < System.currentTimeMillis()
		   )) {
			
			return last.block;
		}
	
		// No action found
		return null;
	}
	
	@Override
	public void removePlayerCache(Player player) {
		// Cleanup
		lastRightClicked.remove(player.getName());
	}
}
