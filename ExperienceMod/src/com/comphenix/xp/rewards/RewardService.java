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

package com.comphenix.xp.rewards;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.extra.Service;

/**
 * Represents a method of rewarding a player with resources.
 * 
 * @author Kristian
 */
public interface RewardService extends Service {
	
	/**
	 * Whether or not the player actually can be rewarded (or penalized, if negative) 
	 * with the given amount of resources.
	 * @param player - player to test.
	 * @param resource - the resource to give or take away.
	 */
	public boolean canReward(Player player, ResourceHolder resource);
	
	/**
	 * Rewards a player directly. 
	 * @param player - player to award.
	 * @param resource - the resource to give or take away.
	 */
	public void reward(Player player, ResourceHolder resource);
	
	/**
	 * Rewards a player with the given amount of resources.
	 * @param player - player to award.
	 * @param point - if possible, the location the award will be placed.
	 * @param resource - the resource to give or take away.
	 */
	public void reward(Player player, Location point, ResourceHolder resource);
	
	/**
	 * Creates a reward at a given location through any means necessary.
	 * @param world - the world to create this reward.
	 * @param point - where to put this reward.
	 * @param resource - the resource to give or take away.
	 */
	public void reward(World world, Location point, ResourceHolder resource);
	
	/**
	 * Retrieves the reward type.
	 * @return Reward type of this class.
	 */
	public RewardTypes getRewardType();
	
	/**
	 * Retrieves the standard or custom resource parser associated with this reward.
	 * @return Resource parser.
	 */
	public ResourcesParser getResourcesParser();
	
	/**
	 * Clones this object with the settings from the given configuration.
	 * @param config - configuration file.
	 * @return A new reward manager with the given settings.
	 */
	public RewardService clone(Configuration config);
}
