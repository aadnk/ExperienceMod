package com.comphenix.xp.listeners;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.comphenix.xp.Debugger;

public class ExperienceEnhancements extends AbstractExperienceListener {
	
	private final String permissionKeepExp = "experiencemod.keepexp";
	private final String permissionMaxEnchant = "experiencemod.maxenchant";
	
	private Debugger debugger;
	
	public ExperienceEnhancements(Debugger debugger) {
		this.debugger = debugger;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		
		Player player = event.getEntity();
		
		if (player != null) {
			// Permission check
	        if(player.hasPermission(permissionKeepExp)) {

	            event.setDroppedExp(0);
	            event.setKeepLevel(true);
	            
	            if (debugger != null)
	        		debugger.printDebug(this, "Prevented experience loss for %s.", player.getName());
	            
	        } else {
	        	event.setKeepLevel(false);
	        }
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPrepareItemEnchantEvent(PrepareItemEnchantEvent event) {
	
		Player player = event.getEnchanter();
		
		if (player != null) {
			// Permission check
	        if(player.hasPermission(permissionMaxEnchant)) {
	        	
	        	int[] costs = event.getExpLevelCostsOffered();
	        	int index = costs.length - 1;
	        	
	        	if (index >= 0) {
	        		costs[index] = getMaxBonus(event.getEnchantmentBonus(), index);

		            if (debugger != null)
		        		debugger.printDebug(this, "Changed experience level costs for %s.", player.getName());
	        		
	        	} else if (debugger != null)
	        		debugger.printDebug(this, "Got empty list of experience costs.");
	        }
		}
	}

	public static int getMinBonus(int bookshelves, int slot) {
		
		final double[] slotFactor = { 0.5, 0.66, 1 };  
		Validate.isTrue(slot > 0, "Slot # cannot be less than zero.");
		Validate.isTrue(slot < 3, "Slot # cannot be greater than 3.");
		
		return (int) ((1 + bookshelves / 2) * slotFactor[slot]);
	}
	
	public static int getMaxBonus(int bookshelves, int slot) {
		
		final double[] slotFactor = { 0.5, 0.66, 1 };  
		Validate.isTrue(slot > 0, "Slot # cannot be less than zero.");
		Validate.isTrue(slot < 3, "Slot # cannot be greater than 3.");
		
		return (int) ((5 + bookshelves * 1.5f) * slotFactor[slot]);
	}
}
