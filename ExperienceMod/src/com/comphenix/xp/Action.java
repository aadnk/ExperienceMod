package com.comphenix.xp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.xp.lookup.Multipliable;
import com.comphenix.xp.messages.Message;
import com.comphenix.xp.rewards.RewardProvider;
import com.comphenix.xp.rewards.Rewardable;

public class Action implements Multipliable<Action> {

	private Message message;
	private List<RangedReward> rewards = new ArrayList<RangedReward>();
	
	public Action(Message message) {
		this.message = message;
	}
	
	public void addReward(String rewardType, Range range) {
		rewards.add(new RangedReward(range, rewardType));
	}
	
	public void removeReward(String rewardType) {
		for (Iterator<RangedReward> it = rewards.iterator(); it.hasNext();) {
			RangedReward item = it.next();
			
			if (item.rewardType.equals(rewardType))
				it.remove();
		}
	}
	
	public void removeAll() {
		rewards.clear();
	}

	/**
	 * Rewards a player with the given amount of resources.
	 * @param provider Reward provider that determines specifically how to reward players.
	 * @param rnd Random number generator.
	 * @param player The player to reward..
	 * @return The amount of total resources that were given.
	 */
	public int rewardPlayer(RewardProvider provider, Random rnd, Player player) {
		
		int sum = 0;
		
		// Give every reward
		for (RangedReward reward : rewards) {
			
			Rewardable manager = provider.getByName(reward.rewardType);
			int exp = reward.range.sampleInt(rnd);
			
			if (manager != null) {
				manager.reward(player, exp);
				sum += exp;
			}
		}
		
		return sum;
	}
	
	/**
	 * Rewards a given player with resources at a given location.
	 * @param provider Reward provider that determines specifically how to reward players.
	 * @param rnd Random number generator.
	 * @param player The player to reward.
	 * @param point The location to place the reward, if relevant.
	 * @return The amount of total resources that were given.
	 */
	public int rewardPlayer(RewardProvider provider, Random rnd, Player player, Location point) {
		
		int sum = 0;
		
		// As the above
		for (RangedReward reward : rewards) {
			
			Rewardable manager = provider.getByName(reward.rewardType);
			int exp = reward.range.sampleInt(rnd);
			
			if (manager != null) {
				manager.reward(player, point, exp);
				sum += exp;
			}
		}
		
		return sum;
	}
	
	public Message getMessage() {
		return message;
	}
	
	public void setMessage(Message message) {
		this.message = message;
	}
	
	/**
	 * Represents a reward.
	 */
	private static class RangedReward {
		public Range range;
		public String rewardType;
		
		public RangedReward(Range range, String rewardType) {
			this.range = range;
			this.rewardType = rewardType;
		}
	}

	@Override
	public Action withMultiplier(double newMultiplier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getMultiplier() {
		// TODO Auto-generated method stub
		return 0;
	}
}
