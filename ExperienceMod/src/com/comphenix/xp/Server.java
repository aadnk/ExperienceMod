/*
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

package com.comphenix.xp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Server {
	private static final int[] threshhold = { 2477, 1237, 617, 307, 149, 73, 37, 17, 7, 3, 1 };
	
	public static void spawnExperienceAtBlock(Block block, int amount) {
		
		// Create experience at this location
        spawnExperience(block.getWorld(), block.getLocation(), amount);
	}
	
	public static void spawnExperience(World world, Location corner, int amount) {

		int xpSplit = getXPSplit(amount);

		// Experience orbs cannot give negative experience
		if (amount < 0)
			return;
		
		// Split into n pieces
		for (int current = 0; current < amount; current += xpSplit) {
	        ExperienceOrb orb = world.spawn(corner, ExperienceOrb.class);
	        orb.setExperience(Math.min(amount - current, xpSplit));
		}
	}
	
	public static void spawnItem(World world, Location corner, List<ItemStack> items) {
		
		// Just drop these items on the ground
		for (ItemStack stack : items)
			world.dropItemNaturally(corner, stack);
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
    
    /**
     * Retrieve a list of nearby players.
     * @param world - the world to search in.
     * @param point - the origin point to search from.
     * @param radius - the maximum distance away from the origin point to search in.
     * @return Every player within the radius distance from the given point.
     */
    public static List<Player> getNearbyPlayers(World world, Location point, double radius) {

    	List<Player> result = new ArrayList<Player>();
    	double radiusSquared = radius * radius;
    	
    	// We'll just search through the entire list
    	for (Player player : world.getPlayers()) {
    	
    		if (player != null && point.distanceSquared(player.getLocation()) < radiusSquared) {
    			result.add(player);
    		}
    	}
    	
    	return result;
    }
}
