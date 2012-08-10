package com.comphenix.xp.listeners;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.Presets;
import com.comphenix.xp.SampleRange;
import com.comphenix.xp.lookup.LevelingRate;
import com.comphenix.xp.rewards.items.RandomSampling;
import com.comphenix.xp.rewards.xp.ExperienceManager;

public class ExperienceLevelListener extends AbstractExperienceListener {

	protected Debugger debugger;
	
	public ExperienceLevelListener(Debugger debugger, Presets presets) {
		this.debugger = debugger;
		this.presets = presets;
	}	

	// Invoked after the player picks up an experience orb, but before he or she is awarded any experience
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEnchantItemEvent(PlayerExpChangeEvent event) {
		
		try {
			handleEnchantItemEvent(event);
			
		} catch (Exception e) {
			ErrorReporting.DEFAULT.reportError(debugger, this, e, event);
		}
	}
	
	private void handleEnchantItemEvent(PlayerExpChangeEvent event) {
		
		Player player = event.getPlayer();
		Configuration config = getConfiguration(player);
		ExperienceManager manager = new ExperienceManager(player);
		
		Random rnd = RandomSampling.getThreadRandom();
		LevelingRate rate = config.getLevelingRate();
		
		// Retrieve the desired amount of experience required to level up
		int desiredLevelUp = rate.get(player.getLevel());
		int defaultLevelUp = manager.getXpNeededToLevelUp(player.getLevel());
		
		// See if we need to modify the experience gained
		if (desiredLevelUp != defaultLevelUp) {
			// Make experience drops correspond to the desired level rate
			double factor = (double)desiredLevelUp / (double)defaultLevelUp;
			
			// We can't give fractional values with ordinary experience orbs, but we can approximate it
			SampleRange approximate = new SampleRange(factor * event.getAmount());
			event.setAmount(Math.max(approximate.sampleInt(rnd), 0));
		}
	}
}
