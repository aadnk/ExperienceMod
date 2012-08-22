/*
 *  ExperienceMod - Bukkit server plugin for modifying the experience system in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.xp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.xp.expressions.NamedParameter;
import com.comphenix.xp.messages.*;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.rewards.*;

public class Action {

	public static final Action Default = new Action();

	private double inheritMultiplier;
	private boolean inherit;
	
	private int id;
	private Message message;
	private Map<String, ResourceFactory> rewards;

	private Debugger debugger;
	
	public Action() {
		// Default constructor
		rewards = new LinkedHashMap<String, ResourceFactory>();;
		inheritMultiplier = 1;
	}
	
	public Action(String rewardType, ResourceFactory reward) {
		this();
		addReward(rewardType, reward);
	}
	
	private Action(Message message, Map<String, ResourceFactory> rewards, Debugger debugger, int id) {
		this.message = message;
		this.rewards = rewards;
		this.debugger = debugger;
		this.id = id;
	}
	
	/**
	 * Adds the given reward to the action that will be triggered.
	 * @param rewardType - name of the reward.
	 * @param factory - factory that generates the rewards when they are needed.
	 */
	public void addReward(String rewardType, ResourceFactory factory) {
		rewards.put(Utility.getEnumName(rewardType), factory);
	}
	
	/**
	 * Remove a reward by name.
	 * @param rewardType - name of the reward to remove.
	 */
	public void removeReward(String rewardType) {
		rewards.remove(Utility.getEnumName(rewardType));
	}
	
	/**
	 * Retrieves a associated reward by name.
	 * @param name - name of the reward to retrieve.
	 * @return Factory that generates rewards of this type.
	 */
	public ResourceFactory getReward(String name) {
		return rewards.get(Utility.getEnumName(name));
	}
	
	/**
	 * Retrieves a associated reward by type.
	 * @param type - type of the reward to retrieve.
	 * @return Factory that generates rewards of this type.
	 */
	public ResourceFactory getReward(RewardTypes type) {
		return rewards.get(type.name());
	}
	
	/**
	 * Retrieves a list of the name of every reward.
	 * @return Names of every reward.
	 */
	public Collection<String> getRewardNames() {
		return rewards.keySet();
	}
	
	/**
	 * Removes all associated rewards.
	 */
	public void removeAll() {
		rewards.clear();
	}
	
	/**
	 * Determines if the given action has any rewards or messages.
	 * @return
	 */
	public boolean hasNothing(ChannelProvider provider) {
		return rewards.isEmpty() && (message == null || getChannels(provider, message) == null);
	}
	
	/**
	 * Generates a list of resources, in the same order as each associated reward factory.
	 * @param params - parameters to use when calculating the reward.
	 * @param provider - provider of reward services.
	 * @param rnd - random number generator.
	 * @return A list of resources in a specific order.
	 */
	public List<ResourceHolder> generateRewards(Collection<NamedParameter> params, RewardProvider provider, Random rnd) {
		
		// Reward the player or anyone once
		return generateRewards(params, provider, rnd, 1);
	}
	
	/**
	 * Generates a list of resources, in the same order as each associated reward factory.
	 * @param params - parameters to use when calculating the reward.
	 * @param provider - provider of reward services.
	 * @param rnd - random number generator.
	 * @param count - number of times to reward this action.
	 * @return A list of resources in a specific order.
	 */
	public List<ResourceHolder> generateRewards(Collection<NamedParameter> params, RewardProvider provider, Random rnd, int count) {
		
		List<ResourceHolder> result = new ArrayList<ResourceHolder>(rewards.size());
		
		// Save some time
		if (count == 0) {
			return result;
		}
		
		// Generate every reward in "insertion" order
		for (ResourceFactory factory : rewards.values()) {
			result.add(factory.getResource(params, rnd, count));
		}
		
		return result;
	}

	/**
	 * Determines whether or not a player can be rewarded (or penalized) with the given list of rewards.
	 * @param provider - reward provider.
	 * @param player - the player to test.
	 * @param generatedRewards - the list of rewards to use.
	 * @return TRUE if the action can be rewarded with this list, FALSE otherwise.
	 */
	public boolean canRewardPlayer(RewardProvider provider, Player player, List<ResourceHolder> generatedRewards) {

		// This is why the order is important
		int index = 0;
		
		// Enumerate the list of rewards
		for (Map.Entry<String, ResourceFactory> entry : rewards.entrySet()) {
			
			// Quit if we've exhausted the list
			if (index >= generatedRewards.size())
				break;
			
			String key = entry.getKey();
			RewardService manager = provider.getByName(key);
			ResourceHolder resource = generatedRewards.get(index++);
			
			// See if the manager allows this 
			if (manager != null) {
				if (!manager.canReward(player, resource)) {
					return false;
				}
			}
		}
		
		// Yes we can
		return true;
	}
	
	/**
	 * Rewards or penalizes a player with the given amount of resources.
	 * <p>
	 * In the resulting list the resources will be arbitrarily ordered, and resources of the same type
	 * will be combined into one.
	 * 
	 * @param provider - reward provider that determines specifically how to reward players.
	 * @param player - the player to reward.
	 * @param generatedRewards - the list of rewards to use.
	 * @return Combined amount of resources given.
	 */
	public Collection<ResourceHolder> rewardPlayer(RewardProvider provider, Player player, List<ResourceHolder> generatedRewards) {
		
		Map<String, ResourceHolder> result = new HashMap<String, ResourceHolder>();
		
		// Like above
		int index = 0;
		
		// Give every reward
		for (Map.Entry<String, ResourceFactory> entry : rewards.entrySet()) {
			
			// Quit if we've exhausted the list
			if (index >= generatedRewards.size())
				break;
			
			String key = entry.getKey();
			RewardService manager = provider.getByName(key);
			ResourceHolder resource = generatedRewards.get(index++);
			
			if (manager != null) {
				manager.reward(player, resource);
				addResource(result, resource);
			}
		}
		
		return result.values();
	}
	
	/**
	 * Rewards or penalizes a given player with resources at a given location.
	 * <p>
	 * In the resulting list the resources will be arbitrarily ordered, and resources of the same type
	 * will be combined into one.
	 * 
	 * @param provider - reward provider that determines specifically how to reward players.
	 * @param player - the player to reward.
	 * @param generatedRewards - the list of rewards to use.
	 * @param point - the location to place the reward, if relevant.
	 * @return Combined amount of resources given.
	 */
	public Collection<ResourceHolder> rewardPlayer(RewardProvider provider, Player player, 
												   List<ResourceHolder> generatedRewards, Location point) {
		
		Map<String, ResourceHolder> result = new HashMap<String, ResourceHolder>();
		int index = 0;
		
		// Give every reward
		for (Map.Entry<String, ResourceFactory> entry : rewards.entrySet()) {
			
			// Quit if we've exhausted the list
			if (index >= generatedRewards.size())
				break;
			
			String key = entry.getKey();
			RewardService manager = provider.getByName(key);
			ResourceHolder resource = generatedRewards.get(index++);
			
			if (manager != null) {
				manager.reward(player, point, resource);
				addResource(result, resource);
			}
		}
		
		return result.values();
	}
	
	/**
	 * Spawns resources at the given location.
	 * 
	 * In the resulting list the resources will be arbitrarily ordered, and resources of the same type
	 * will be combined into one.
	 * 
	 * @param provider - reward provider that determines specifically how to award resources.
	 * @param world - the world where the resources should be spawned.
	 * @param generatedRewards - the list of rewards to use.
	 * @param point - the location to place the reward.
	 * @return Combined amount of resources given.
	 */
	public Collection<ResourceHolder> rewardAnyone(RewardProvider provider, World world, 
												   List<ResourceHolder> generatedRewards, Location point) {
		
		Map<String, ResourceHolder> result = new HashMap<String, ResourceHolder>();
		int index = 0;
		
		// Give every reward
		for (Map.Entry<String, ResourceFactory> entry : rewards.entrySet()) {
			
			// Quit if we've exhausted the list
			if (index >= generatedRewards.size())
				break;
			
			String key = entry.getKey();
			RewardService manager = provider.getByName(key);
			ResourceHolder resource = generatedRewards.get(index++);
			
			if (manager != null) {
				manager.reward(world, point, resource);
				addResource(result, resource);
			}
		}
		
		return result.values();
	}
	
	private void addResource(Map<String, ResourceHolder> result, ResourceHolder resource) {
		
		if (result.containsKey(resource.getName())) {
			// Add the previous value
			resource = result.get(resource.getName()).add(resource);
		}
		
		// Save value
		result.put(resource.getName(), resource);
	}
	
	/**
	 * Sends a message from the given player informing anyone listening of 
	 * the action performed and the resources awarded.
	 * @param provider - channel provider to use.
	 * @param formatter - message formatter, complete with all the parameter information.
	 * @param player - the sender.
	 */
	public void emoteMessages(ChannelProvider provider, MessageFormatter formatter, Player player) {
	
		List<String> channels = getChannels(provider, message);
		List<String> failures = new ArrayList<String>();
		ChannelService service = provider.getDefaultService();

		// Guard against NULL messages
		if (channels != null && service != null) {
			// Transmit the message on all channels
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
	
	/**
	 * Sends a general message informing anyone listening of the resources awarded.
	 * @param provider - channel provider to use.
	 * @param formatter - message formatter, complete with all the parameter information.
	 */
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

		Map<String, ResourceFactory> copy = new HashMap<String, ResourceFactory>();
		
		// Multiply everything
		for (Map.Entry<String, ResourceFactory> entry : rewards.entrySet()) {
			ResourceFactory old = entry.getValue();	
			copy.put(entry.getKey(), old.withMultiplier(old.getMultiplier() * multiply));
		}
		
		// Copy everything
		Action action = new Action(message, copy, debugger, id);
		action.setInheritMultiplier(inheritMultiplier);
		action.setInheritance(inherit);
		return action;
	}
	
	/**
	 * Inherit traits from the previous action into the current action, returning a new action with the result.
	 * @param previous - the previous action to inherit from.
	 * @return A new action with the traits of this and the previois action.
	 */
	public Action inheritAction(Action previous) {
		
		// Scale the previous action
		Action scaled = previous.multiply(getInheritMultiplier());
		Action current = multiply(1);
		
		// Find any rewards that are not overwritten
		Collection<String> rewards = scaled.getRewardNames();
		rewards.removeAll(getRewardNames());
		
		// Copy over
		for (String reward : rewards) {
			current.addReward(reward, scaled.getReward(reward));
		}
		
		// And copy the message too, if it hasn't already been set
		if (current.message == null) {
			current.message = scaled.message;
		}
	
		return current;
	}
	
	/**
	 * Retrieves the resource multiplier to use during inheritance, if any.
	 * @return The resource multiplier.
	 */
	public double getInheritMultiplier() {
		return inheritMultiplier;
	}
	
	/**
	 * Sets the resource multiplier to use during inheritance, if any.
	 * @param inheritMultiplier - The new resource multiplier.
	 */
	public void setInheritMultiplier(double inheritMultiplier) {
		this.inheritMultiplier = inheritMultiplier;
	}

	/**
	 * Whether or not this action should inherit rewards from any previous actions.
	 * @return TRUE if inheritance is enabled, FALSE otherwise.
	 */
	public boolean hasInheritance() {
		return inherit;
	}
	
	/**
	 * Sets whether or not this action should inherit rewards from any previous actions.
	 * @param value - TRUE to use inheritance.
	 */
	public void setInheritance(boolean value) {
		this.inherit = value;
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
            append(inherit, other.inherit).
            append(inheritMultiplier, other.inheritMultiplier).
            append(id, other.id).
            isEquals();
	}

	@Override
	public String toString() {
		
		List<String> textRewards = new ArrayList<String>();
		
		// Build list of rewards
		for (Map.Entry<String, ResourceFactory> entry : rewards.entrySet()) {
			String key = entry.getKey();
			ResourceFactory value = entry.getValue();
			
			textRewards.add(String.format("%s: %s", key, value));
		}
		
		return String.format("%s %s (%d)", 
				StringUtils.join(textRewards, ", "),
				message,
				id
		);
	}
	
	public Debugger getDebugger() {
		return debugger;
	}

	public void setDebugger(Debugger debugger) {
		this.debugger = debugger;
	}
}
