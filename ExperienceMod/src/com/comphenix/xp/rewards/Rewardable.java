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
public interface Rewardable extends Service {
	
	/**
	 * Rewards a player directly.
	 * @param player player to award.
	 * @param amount amount of resources to give.
	 */
	public void reward(Player player, int amount);
	
	/**
	 * Rewards a player with the given amount of resources.
	 * @param player player to award.
	 * @param point If possible, the location the award will be placed.
	 * @param amount amount of resources to give.
	 */
	public void reward(Player player, Location point, int amount);
	
	
	/**
	 * Creates a reward at a given location through any means necessary.
	 * @param world The world to create this reward.
	 * @param point Where to put this reward.
	 * @param amount The amount of experience to award.
	 */
	public void reward(World world, Location point, int amount);
	
	/**
	 * Retrieves the reward type.
	 * @return Reward type of this class.
	 */
	public RewardTypes getRewardType();

	
	/**
	 * Clones this object with the settings from the given configuration.
	 * @param config Configuration file.
	 * @return A new reward manager with the given settings.
	 */
	public Rewardable clone(Configuration config);
}
