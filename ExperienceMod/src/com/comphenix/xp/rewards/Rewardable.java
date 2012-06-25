package com.comphenix.xp.rewards;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a method of rewarding a player with resources.
 * 
 * @author Kristian
 */
public interface Rewardable {
	
	/**
	 * Rewards a player with the given amount of resources.
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
	 * Retrieves the reward type.
	 * @return Reward type of this class.
	 */
	public RewardTypes getType();

	/**
	 * Retrieves a unique string identifying this reward. Will be using during parsing.
	 * @return A unique reward ID.
	 */
	public String getRewardName();
	
	
	public void hasDependencies() {
		
	}
}
