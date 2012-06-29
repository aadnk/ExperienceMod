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
public class RewardVirtual implements RewardService {

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

		ExperienceManager manager = new ExperienceManager(player);
		
		// Rely on the brilliance of others
		if (amount != 0) {
			manager.changeExp(amount);
		}
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
	public RewardService clone(Configuration config) {
		RewardVirtual copy = new RewardVirtual();
		
		copy.setSearchRadius(searchRadius);
		return copy;
	}
}
