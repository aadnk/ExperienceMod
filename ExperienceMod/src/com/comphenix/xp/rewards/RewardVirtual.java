package com.comphenix.xp.rewards;

import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Server;

/**
 * Rewards players with experience directly by simply adding the experience to their experience bar.
 * 
 * @author Kristian
 */
public class RewardVirtual implements Rewardable {

	private double searchRadius = 20;
	
	@Override
	public void reward(Player player, int amount) {
		if (player == null)
			throw new NullArgumentException("player");

		reward(player, null, amount);
	}

	// Note: We ignore the location.
	@Override
	public void reward(Player player, Location point, int amount) {
		if (player == null)
			throw new NullArgumentException("player");

		if (amount >= 0)
			addExperience(player, amount);
		else 
			subtractExperience(player, Math.abs(amount));
	}

	@Override
	public void reward(World world, Location point, int amount) {
		if (world == null)
			throw new NullArgumentException("world");
		if (point == null)
			throw new NullArgumentException("point");
		
		List<Player> closest = Server.getNearbyPlayers(world, point, searchRadius);
		
		// Give experience directly
		if (closest.size() == 1)
			reward(closest.get(0), null, amount);
		else
			// Spawn experience
			Server.spawnExperience(world, point, amount);
	}
	
	/**
	 * Sets the current accumulated experience points of the given player.
	 * @param player The given player
	 * @param value The new value.
	 */
	public void setExperience(Player player, int value) {
		if (player == null)
			throw new NullArgumentException("player");

		// Reset current
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0);

		// Set it again
		player.giveExp(value);
	}

	/**
	 * Retrieve the current accumulated experience points of the given player.
	 * @param player The given player.
	 * @return Total experience points counting previous levels.
	 */
	public int getExperience(Player player) {
		if (player == null)
			throw new NullArgumentException("player");

		return player.getTotalExperience();
	}

	/**
	 * Remove or withdraw experience from the given player.
	 * @param player The given player
	 * @param value Amount to withdraw
	 */
	public void subtractExperience(Player player, int value) {
		if (player == null)
			throw new NullArgumentException("player");
		
		int current = getExperience(player);

		if (current >= 0)
			current -= value;

		// Experience must be non-zero
		setExperience(player, Math.min(0, current));
	}

	/**
	 * Rewards the given player with experience.
	 * @param player Player to reward.
 	 * @param value The amount of experience to give
	 */
	public void addExperience(Player player, int value) {
		if (player == null)
			throw new NullArgumentException("player");
		
		int exp = getExperience(player);

		if (value > 0) {
			setExperience(player, exp + value);
		}
	}

	public double getSearchRadius() {
		return searchRadius;
	}

	public void setSearchRadius(double searchRadius) {
		this.searchRadius = searchRadius;
	}
	
	@Override
	public RewardTypes getRewardType() {
		return RewardTypes.VIRTUAL;
	}

	@Override
	public String getServiceName() {
		return getRewardType().name();
	}

	@Override
	public Rewardable clone(Configuration config) {
		RewardVirtual copy = new RewardVirtual();
		
		copy.setSearchRadius(searchRadius);
		return copy;
	}
}
