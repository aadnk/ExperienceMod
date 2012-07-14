package com.comphenix.xp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.xp.messages.ChannelProvider;
import com.comphenix.xp.messages.ChannelService;
import com.comphenix.xp.messages.Message;
import com.comphenix.xp.messages.MessageFormatter;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.rewards.RewardProvider;
import com.comphenix.xp.rewards.RewardTypes;
import com.comphenix.xp.rewards.RewardService;

public class Action {

	public static final Action Default = new Action();

	private int id;
	private Message message;
	private Map<String, Range> rewards;

	private Debugger debugger;
	
	public Action() {
		// Default constructor
		rewards = new HashMap<String, Range>();;
	}
	
	public Action(String rewardType, Range reward) {
		this();
		addReward(rewardType, reward);
	}
	
	private Action(Message message, Map<String, Range> rewards, Debugger debugger, int id) {
		this.message = message;
		this.rewards = rewards;
		this.debugger = debugger;
		this.id = id;
	}
	
	public void addReward(String rewardType, Range range) {
		rewards.put(Utility.getEnumName(rewardType), range);
	}
	
	public void removeReward(String rewardType) {
		rewards.remove(Utility.getEnumName(rewardType));
	}
	
	public Range getReward(String name) {
		return rewards.get(Utility.getEnumName(name));
	}
	
	public Range getReward(RewardTypes type) {
		return rewards.get(type.name());
	}
	
	public void removeAll() {
		rewards.clear();
	}

	/**
	 * Determines whether or not a player can be rewarded (or penalized) the given number of times.
	 * @param provider - reward provider.
	 * @param player - the player to test.
	 * @param count - number of times to reward this action.
	 * @return TRUE if the action can be rewarded that number of times, FALSE otherwise.
	 */
	public boolean canRewardPlayer(RewardProvider provider, Player player, int count) {
		
		// Give every reward
		for (Map.Entry<String, Range> entry : rewards.entrySet()) {
			
			String key = Utility.getEnumName(entry.getKey());
			RewardService manager = provider.getByName(key);
			
			// That is, the highest penalty we can give
			int minimum = entry.getValue().getMinimum() * count;
			
			// See if the manager allows this extreme
			if (manager != null) {
				if (!manager.canReward(player, minimum))
					return false;
			}
		}
		
		// Yes, we can
		return true;
	}
	
	/**
	 * Rewards or penalizes a player with the given amount of resources.
	 * @param provider - reward provider that determines specifically how to reward players.
	 * @param rnd - random number generator.
	 * @param player - the player to reward.
	 * @return The amount of total resources that were given.
	 */
	public int rewardPlayer(RewardProvider provider, Random rnd, Player player) {
		
		// Give the reward once
		return rewardPlayer(provider, rnd, player, 1);
	}
	
	/**
	 * Rewards or penalizes a player with the given amount of resources.
	 * @param provider - reward provider that determines specifically how to reward players.
	 * @param rnd - random number generator.
	 * @param player - the player to reward.
	 * @param count - the number of times to give this resource.
	 * @return The amount of total resources that were given.
	 */
	public int rewardPlayer(RewardProvider provider, Random rnd, Player player, int count) {
		
		int sum = 0;
		
		// No need to do anything
		if (count == 0)
			return 0;
		
		// Give every reward
		for (Map.Entry<String, Range> entry : rewards.entrySet()) {
			
			String key = Utility.getEnumName(entry.getKey());
			RewardService manager = provider.getByName(key);
			
			int exp = entry.getValue().sampleInt(rnd) * count;
			
			if (manager != null) {
				manager.reward(player, exp);
				sum += exp;
			}
		}
		
		return sum;
	}
	
	/**
	 * Rewards or penalizes a given player with resources at a given location.
	 * @param provider - reward provider that determines specifically how to reward players.
	 * @param rnd - random number generator.
	 * @param player - the player to reward.
	 * @param point - the location to place the reward, if relevant.
	 * @return The amount of total resources that were given.
	 */
	public int rewardPlayer(RewardProvider provider, Random rnd, Player player, Location point) {
		
		int sum = 0;
		
		// As the above
		for (Map.Entry<String, Range> entry : rewards.entrySet()) {
			
			String key = Utility.getEnumName(entry.getKey());
			RewardService manager = provider.getByName(key);
			
			int exp = entry.getValue().sampleInt(rnd);
			
			if (manager != null) {
				manager.reward(player, point, exp);
				sum += exp;
			}
		}
		
		return sum;
	}
	
	/**
	 * Spawns resources at the given location.
	 * @param provider - reward provider that determines specifically how to award resources.
	 * @param rnd - random number generator.
	 * @param world - the world where the resources should be spawned.
	 * @param point - the location to place the reward.
	 * @return The amount of total resources that were given.
	 */
	public int rewardAnyone(RewardProvider provider, Random rnd, World world, Location point) {
		
		int sum = 0;
		
		// As the above
		for (Map.Entry<String, Range> entry : rewards.entrySet()) {
			
			String key = Utility.getEnumName(entry.getKey());
			RewardService manager = provider.getByName(key);
			
			int exp = entry.getValue().sampleInt(rnd);
			
			if (manager != null) {
				manager.reward(world, point, exp);
				sum += exp;
			}
		}
		
		return sum;
	}
	
	public void emoteMessages(ChannelProvider provider, MessageFormatter formatter, Player player) {
	
		List<String> channels = getChannels(provider, message);
		List<String> failures = new ArrayList<String>();
		ChannelService service = provider.getDefaultService();
		
		// Guard against NULL messages
		if (channels != null && service != null) {
			// Transmit the message on all the channels
			for (String channel : channels) {
				String text = message.getText();
				
				try {
					if (service.hasChannel(channel))
						service.emote(channel, formatter.formatMessage(text), player);
					else
						failures.add(channel);
					
				} catch (IllegalArgumentException e) {
					failures.add(channel);
				}
			}
		}
		
		// Print warnings
		if (debugger != null && !failures.isEmpty()) {
			debugger.printDebug(this, "Cannot find channels: %s", 
					StringUtils.join(failures, ", "));
		}
	}
	
	public void announceMessages(ChannelProvider provider, MessageFormatter formatter) {

		List<String> channels = getChannels(provider, message);
		List<String> failures = new ArrayList<String>();
		ChannelService service = provider.getDefaultService();
		
		if (channels != null &&  service != null) {
			// Like above, only without the player
			for (String channel : channels) {
				String text = message.getText();
				
				try {
					if (service.hasChannel(channel))
						service.announce(channel, formatter.formatMessage(text));
					else
						failures.add(channel);
						
				} catch (IllegalArgumentException e) {
					failures.add(channel);
				}
			}
		}
		
		if (debugger != null && !failures.isEmpty()) {
			debugger.printDebug(this, "Cannot find channels: %s",
					StringUtils.join(failures, ", "));
		}
	}
	
	private List<String> getChannels(ChannelProvider provider, Message message) {
		
		// Guard against NULL
		if (message == null)
			return null;
		
		// See if we can return a list of channels
		if (message.getChannels() != null)
			return message.getChannels();
		else if (provider != null && provider.getDefaultChannels() != null)
			return provider.getDefaultChannels();
		else
			return null; 
	}
	 
	public Message getMessage() {
		return message;
	}
	
	public void setMessage(Message message) {
		this.message = message;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Action multiply(double multiply) {

		Map<String, Range> copy = new HashMap<String, Range>();
		
		for (Map.Entry<String, Range> entry : rewards.entrySet()) {
			copy.put(entry.getKey(), entry.getValue().multiply(multiply));
		}
		
		return new Action(message, copy, debugger, id);
	}
	
	@Override
	public int hashCode() {
		return 17 * id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        Action other = (Action) obj;
        return new EqualsBuilder().
            append(message, other.message).
            append(rewards, other.rewards).
            append(id, other.id).
            isEquals();
	}

	@Override
	public String toString() {
		
		List<String> textRewards = new ArrayList<String>();
		
		// Build list of rewards
		for (Map.Entry<String, Range> entry : rewards.entrySet()) {
			String key = entry.getKey();
			Range value = entry.getValue();
			
			textRewards.add(String.format("%s: %s", key, value));
		}
		
		return String.format("%s %s", 
				StringUtils.join(textRewards, ", "),
				message
		);
	}
	
	public Debugger getDebugger() {
		return debugger;
	}

	public void setDebugger(Debugger debugger) {
		this.debugger = debugger;
	}
}
