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

package com.comphenix.xp.rewards.xp;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Server;
import com.comphenix.xp.rewards.ResourceHolder;
import com.comphenix.xp.rewards.ResourcesParser;
import com.comphenix.xp.rewards.RewardService;
import com.comphenix.xp.rewards.RewardTypes;

/**
 * Rewards players with experience orbs.
 * 
 * @author Kristian
 */
public class RewardExperience implements RewardService {

	private ResourcesParser parser = new ExperienceParser(null);
	
	@Override
	public boolean canReward(Player player, ResourceHolder resource) {
		if (!isExperience(resource))
			throw new IllegalArgumentException("Must be a experience resource.");
		
		// Accept anything. We've already warned about negative amounts.
		return true;
	}
	
	@Override
	public void reward(Player player, ResourceHolder resource) {
		if (player == null)
			throw new NullArgumentException("player");
		if (!isExperience(resource))
			throw new IllegalArgumentException("Must be a experience resource.");
		
		// Delegate to more specific method
		reward(player, player.getLocation(), resource);
	}
	
	@Override
	public void reward(Player player, Location point, ResourceHolder resource) {
		if (player == null)
			throw new NullArgumentException("player");
		if (point == null)
			throw new NullArgumentException("point");
		if (!isExperience(resource))
			throw new IllegalArgumentException("Must be a experience resource.");
		
		// Create the experience at this location
		Server.spawnExperience(player.getWorld(), point, resource.getAmount());
	}

	@Override
	public void reward(World world, Location point, ResourceHolder resource) {
		if (world == null)
			throw new NullArgumentException("world");
		if (point == null)
			throw new NullArgumentException("point");
		if (!isExperience(resource))
			throw new IllegalArgumentException("Must be a experience resource.");
		
		// And here
		Server.spawnExperience(world, point, resource.getAmount());
	}
	
	private boolean isExperience(ResourceHolder resource) { 
		return resource instanceof ExperienceHolder;
	}
	
	@Override
	public ResourcesParser getResourcesParser(String[] namedParameters) {
		return parser.withParameters(namedParameters);
	}
	
	@Override
	public RewardTypes getRewardType() {
		return RewardTypes.EXPERIENCE;
	}

	@Override
	public String getServiceName() {
		return getRewardType().name();
	}

	@Override
	public RewardService clone(Configuration config) {
		RewardExperience copy = new RewardExperience();
		
		copy.parser = parser;
		return copy;
	}
}
