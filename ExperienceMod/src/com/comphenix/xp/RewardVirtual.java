package com.comphenix.xp;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RewardVirtual implements Rewardable {

	@Override
	public void reward(Player player, int amount) {
		reward(player, null, amount);
	}

	// Note: We ignore the location.
	@Override
	public void reward(Player player, Location point, int amount) {
		if (amount >= 0)
			addExperience(player, amount);
		else 
			subtractExperience(player, Math.abs(amount));
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
		int exp = getExperience(player);

		if (value > 0) {
			setExperience(player, exp + value);
		}
	}

	@Override
	public RewardTypes getType() {
		return RewardTypes.VIRTUAL;
	}

	@Override
	public String getRewardName() {
		return getType().name();
	}
}
