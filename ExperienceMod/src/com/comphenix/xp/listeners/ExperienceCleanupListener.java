package com.comphenix.xp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.xp.Presets;

public class ExperienceCleanupListener extends AbstractExperienceListener {
	
	public ExperienceCleanupListener(Presets presets) {
		setPresets(presets);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		
		if (event.getPlayer() != null) {
			// Cleanup after the player is removed
			getPresets().removePlayer(event.getPlayer());
		}
	}
}
