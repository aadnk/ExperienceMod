package com.comphenix.xp;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
}
