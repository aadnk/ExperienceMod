package com.comphenix.xp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;

public class Helper {
	private static final int[] threshhold = { 2477, 1237, 617, 307, 149, 73, 37, 17, 7, 3, 1 };
	
	public static void spawnExperienceAtBlock(Block block, int amount) {
		
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		
		// Create experience at this location
        spawnExperience(block.getWorld(), center, amount);
	}
	
	public static void spawnExperience(World world, Location center, int amount) {
		
		int split = getXPSplit(amount);
		
		// Split into n pieces
		for (int current = 0; current < amount; current += split) {
	        ExperienceOrb orb = world.spawn(center, ExperienceOrb.class);
	        orb.setExperience(Math.min(amount - current, split));
		}
	}
	
    public static int getXPSplit(int xp) {	
    	
    	// Determine the split
    	for (int i = 0; i < threshhold.length; i++) {
    		if (xp >= threshhold[i])
    			return threshhold[i];
    	}
    	
    	// Usually due to zero experience
    	return 1;
    }
}
