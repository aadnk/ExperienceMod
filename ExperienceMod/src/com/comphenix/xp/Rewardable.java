package com.comphenix.xp;

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
}
