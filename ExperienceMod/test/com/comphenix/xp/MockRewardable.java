package com.comphenix.xp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.xp.rewards.RewardTypes;
import com.comphenix.xp.rewards.Rewardable;

public class MockRewardable implements Rewardable {

	private RewardTypes type;
	
	public MockRewardable(RewardTypes type) {
		this.type = type;
	}
	
	@Override
	public void reward(Player player, int amount) {
	}

	@Override
	public void reward(Player player, Location point, int amount) {

	}

	@Override
	public void reward(World world, Location point, int amount) {
	}

	@Override
	public RewardTypes getType() {
		return type;
	}

	@Override
	public String getRewardName() {
		return type.name();
	}

	@Override
	public Rewardable clone(Configuration config) {
		return new MockRewardable(type);
	}
}
