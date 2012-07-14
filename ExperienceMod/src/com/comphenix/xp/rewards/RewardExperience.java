package com.comphenix.xp.rewards;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Server;

/**
 * Rewards players with experience orbs.
 * 
 * @author Kristian
 */
public class RewardExperience implements RewardService {

	@Override
	public boolean canReward(Player player, int amount) {
		// Accept anything. We've already warned about negative amounts.
		return true;
	}
	
	@Override
	public void reward(Player player, int amount) {
		if (player == null)
			throw new NullArgumentException("player");
		
		// Delegate to more specific method
		reward(player, player.getLocation(), amount);
	}
	
	@Override
	public void reward(Player player, Location point, int amount) {
		if (player == null)
			throw new NullArgumentException("player");
		if (point == null)
			throw new NullArgumentException("point");
		
		// Create the experience at this location
		Server.spawnExperience(player.getWorld(), point, amount);
	}

	@Override
	public void reward(World world, Location point, int amount) {
		if (world == null)
			throw new NullArgumentException("world");
		if (point == null)
			throw new NullArgumentException("point");
		
		// And here
		Server.spawnExperience(world, point, amount);
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
		return new RewardExperience();
	}
}
