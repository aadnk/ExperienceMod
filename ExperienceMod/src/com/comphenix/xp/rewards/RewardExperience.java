package com.comphenix.xp.rewards;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.xp.Server;


public class RewardExperience implements Rewardable {

	// Just delegate to the more specific method
	public void reward(Player player, int amount) {
		if (player == null)
			throw new NullArgumentException("player");
		
		reward(player, player.getLocation(), amount);
	}
	
	public void reward(Player player, Location point, int amount) {
		if (player == null)
			throw new NullArgumentException("player");
		if (point == null)
			throw new NullArgumentException("point");
		
		// Create the experience at this location
		Server.spawnExperience(player.getWorld(), point, amount);
	}

	@Override
	public RewardTypes getType() {
		return RewardTypes.EXPERIENCE;
	}

	@Override
	public String getRewardName() {
		return getType().name();
	}
}
