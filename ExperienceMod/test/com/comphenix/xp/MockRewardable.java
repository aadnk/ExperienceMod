package com.comphenix.xp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.xp.rewards.RewardTypes;
import com.comphenix.xp.rewards.RewardService;

public class MockRewardable implements RewardService {

	private RewardTypes type;
	
	public MockRewardable(RewardTypes type) {
		this.type = type;
	}

	@Override
	public boolean canReward(Player player, int amount) {
		return true;
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
	public RewardTypes getRewardType() {
		return type;
	}

	@Override
	public String getServiceName() {
		return type.name();
	}

	@Override
	public RewardService clone(Configuration config) {
		return new MockRewardable(type);
	}
}
