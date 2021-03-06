package com.comphenix.xp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.xp.rewards.ResourceHolder;
import com.comphenix.xp.rewards.ResourcesParser;
import com.comphenix.xp.rewards.RewardTypes;
import com.comphenix.xp.rewards.RewardService;
import com.comphenix.xp.rewards.xp.ExperienceParser;

public class MockRewardable implements RewardService {

	private RewardTypes type;
	private ResourcesParser parser;
	
	public MockRewardable(RewardTypes type) {
		this(type, new ExperienceParser(null));
	}
	
	public MockRewardable(RewardTypes type, ResourcesParser parser) {
		this.type = type;
		this.parser = parser;
	}

	@Override
	public boolean canReward(Player player, ResourceHolder resource) {
		return true;
	}
	
	@Override
	public void reward(Player player, ResourceHolder resource) {
	}

	@Override
	public void reward(Player player, Location point, ResourceHolder resource) {
	}

	@Override
	public void reward(World world, Location point, ResourceHolder resource) {
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

	@Override
	public ResourcesParser getResourcesParser(String[] namedParameters) {
		return parser.withParameters(namedParameters);
	}
}
