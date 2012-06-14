package com.comphenix.xp;

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
