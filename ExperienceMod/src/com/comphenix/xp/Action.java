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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
	private List<Message> messages;
	private Map<String, MessagedResource> rewards;

	private Debugger debugger;
	
	public Action() {
		// Default constructor
		rewards = new LinkedHashMap<String, MessagedResource>();;
		inheritMultiplier = 1;
	}
	
	public Action(String rewardType, ResourceFactory reward) {
		this();
		addReward(rewardType, reward);
	}
	
	private Action(List<Message> messages, Map<String, MessagedResource> rewards, Debugger debugger, int id) {
		this.messages = messages;
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
		getEntry(rewardType, true).setResourceFactory(factory);
	}
	
	/**
	 * Set (or remove, using NULL) a list of messages that will be sent when the reward is successful.
	 * @param rewardType - name of the reward.
	 * @param message - messages to send.
	 */
	public void addMessage(String rewardType, List<Message> messages) {
		getEntry(rewardType, true).setMessages(messages);
	}
	
	/**
	 * Creates the underlying message-reward entry, if it doesn't already exist.
	 * @param rewardType - name of the reward.
	 */
	private MessagedResource getEntry(String rewardType, boolean createNew) {
		String enumedReward = Utility.getEnumName(rewardType);
		MessagedResource resource = rewards.get(enumedReward);
	
		if (createNew && resource == null) {
			resource = new MessagedResource();
			rewards.put(enumedReward, resource);
		}
		
		return resource;
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
		MessagedResource resource = getEntry(name, false);
		return resource != null ? resource.getResourceFactory() : null;
	}
	
	/**
	 * Retrieves a associated reward by type.
	 * @param type - type of the reward to retrieve.
	 * @return Factory that generates rewards of this type.
	 */
	public ResourceFactory getReward(RewardTypes type) {
		return getReward(type.name());
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
		return rewards.isEmpty() && (messages == null || getChannels(provider, messages) == null);
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
		for (MessagedResource factory : rewards.values()) {
			ResourceFactory generator = factory.getResourceFactory();
			result.add(generator != null ? generator.getResource(params, rnd, count) : null);
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
		for (String key : rewards.keySet()) {
			
			// Quit if we've exhausted the list
			if (index >= generatedRewards.size())
				break;
			
			RewardService manager = provider.getByName(key);
			ResourceHolder resource = generatedRewards.get(index++);
			
			// See if the manager allows this 
			if (manager != null && resource != null) {
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
		for (String key : rewards.keySet()) {
			
			// Quit if we've exhausted the list
			if (index >= generatedRewards.size())
				break;
			
			RewardService manager = provider.getByName(key);
			ResourceHolder resource = generatedRewards.get(index++);
			
			if (manager != null && resource != null) {
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
		for (String key : rewards.keySet()) {
			
			// Quit if we've exhausted the list
			if (index >= generatedRewards.size())
				break;
			
			RewardService manager = provider.getByName(key);
			ResourceHolder resource = generatedRewards.get(index++);
			
			if (manager != null && resource != null) {
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
		for (String key : rewards.keySet()) {
			
			// Quit if we've exhausted the list
			if (index >= generatedRewards.size())
				break;
			
			RewardService manager = provider.getByName(key);
			ResourceHolder resource = generatedRewards.get(index++);
			
			if (manager != null && resource != null) {
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
	 * Sends a general message informing anyone listening of the resources awarded.
	 * @param provider - channel provider to use.
	 * @param formatter - message formatter, complete with all the parameter information.
	 */
	public void announceMessages(ChannelProvider provider, MessageFormatter formatter) {
		// Emote from no one
		emoteMessages(provider, formatter, null);
	}
	
	/**
	 * Sends a message from the given player informing anyone listening of 
	 * the action performed and the resources awarded.
	 * @param provider - channel provider to use.
	 * @param formatter - message formatter, complete with all the parameter information.
	 * @param player - the sender.
	 */
	public void emoteMessages(ChannelProvider provider, MessageFormatter formatter, Player player) {
	
		List<ResourceHolder> generated = formatter.getGenerated();
		Iterator<String> rewardKeys = rewards.keySet().iterator();

		// Dispatch every listed message
		for (Message message : messages) {
			dispatchMessages(provider, formatter, message, player);
		}
		
		// Handle reward specific messages
		for (int i = 0; i < generated.size() && rewardKeys.hasNext(); i++) {
			ResourceHolder element = generated.get(i);
			List<ResourceHolder> elements = Arrays.asList(generated.get(i));
			
			List<Message> current = getMessages(rewardKeys.next());
			
			// Print the messages (and ensure that the amount is greater than zero)
			if (current != null && element.getAmount() > 0) {
				for (Message message : current) {
					dispatchMessages(provider, formatter.createView(elements, null), message, player);
				}
			}
		}
	}

	/**
	 * Dispatch a given message from a given player to the target channels.
	 * @param provider - channel provider to use.
	 * @param formatter - message formatter, complete with all the parameter information.
	 * @param currentMessage - the message to transmit.
	 * @param player - the sender, or NULL to simply announce the message.
	 */
	private void dispatchMessages(ChannelProvider provider, MessageFormatter formatter, Message currentMessage, Player player) {
		
		List<String> channels = getChannels(provider, currentMessage);
		List<String> failures = new ArrayList<String>();
		ChannelService service = provider.getDefaultService();
		
		if (channels != null &&  service != null) {
			// Like above, only without the player
			for (String channel : channels) {
				String text = currentMessage.getText();
				
				try {
					if (service.hasChannel(channel)) {
						if (player == null)
							service.announce(channel, formatter.formatMessage(text));
						else
							service.emote(channel, formatter.formatMessage(text), player);
					} else {
						failures.add(channel);
					}
					
				} catch (IllegalArgumentException e) {
					failures.add(channel);
				}
			}
		}
		
		// Error!
		if (debugger != null && !failures.isEmpty()) {
			debugger.printDebug(this, "Cannot find channels: %s",
					StringUtils.join(failures, ", "));
		}
	}
	
	private List<String> getChannels(ChannelProvider provider, List<Message> messages) {
		
		List<String> result = new ArrayList<String>();
		
		// Combine every channel into a big list
		for (Message message : messages) {
			List<String> channels = getChannels(provider, message);
			
			if (channels != null && channels.size() >0) {
				result.addAll(channels);
			}
		}
		
		// Return NULL instead of an empty list
		if (result.size() != 0)
			return result;
		else
			return null;
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
	 
	/**
	 * Retrieve the specific reward messages.
	 * @param rewardName - name of the reward.
	 * @return The reward messages, or NULL if they don't exist or the reward doesn't exist.
	 */
	public List<Message> getMessages(String rewardName) {
		MessagedResource resource = getEntry(rewardName, false);
		return resource != null ? resource.getMessages() : null;
	}
	
	/**
	 * Retrieve the messages that will be sent when the action is performed.
	 * @return The messages to send when the action triggers.
	 */
	public List<Message> getMessages() {
		return messages;
	}

	/**
	 * Sets the messages that will be sent when the action is performed.
	 * @param messages - Messages to send when the action triggers.
	 */
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Action multiply(double multiply) {

		Map<String, MessagedResource> copy = new HashMap<String, MessagedResource>();
		
		// Multiply everything
		for (Map.Entry<String, MessagedResource> entry : rewards.entrySet()) {
			MessagedResource old = entry.getValue();	
			copy.put(entry.getKey(), old.multiply(multiply));
		}
		
		// Copy everything
		Action action = new Action(messages, copy, debugger, id);
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
		
		// Include the previous multiplier
		current.setInheritMultiplier(current.getInheritMultiplier() * previous.getInheritMultiplier());
		
		// Find any rewards that are not overwritten
		Collection<String> rewards = scaled.getRewardNames();
		rewards.removeAll(getRewardNames());
		
		// Copy over
		for (String reward : rewards) {
			current.addReward(reward, scaled.getReward(reward));
		}
		
		// And copy the message too, if it hasn't already been set
		if (current.messages == null) {
			current.messages = scaled.messages;
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
            append(messages, other.messages).
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
		for (Map.Entry<String, MessagedResource> entry : rewards.entrySet()) {
			String key = entry.getKey();
			MessagedResource value = entry.getValue();
			
			textRewards.add(String.format("%s: %s", key, value));
		}
		
		return String.format("%s %s (%d)", 
				StringUtils.join(textRewards, ", "),
				messages,
				id
		);
	}
	
	public Debugger getDebugger() {
		return debugger;
	}

	public void setDebugger(Debugger debugger) {
		this.debugger = debugger;
	}
	
	
	/**
	 * Represents a resource factory that transmits a message when it produced a non-zero resource.
	 * 
	 * @author Kristian
	 */
	private static class MessagedResource {
		
		private ResourceFactory resourceFactory;
		private List<Message> messages;
		
		public MessagedResource() { 
			// Default values
		}
		
		private MessagedResource(ResourceFactory resourceFactory, List<Message> messages) {
			this.resourceFactory = resourceFactory;
			this.messages = messages;
		}

		public ResourceFactory getResourceFactory() {
			return resourceFactory;
		}
		
		public void setResourceFactory(ResourceFactory resourceFactory) {
			this.resourceFactory = resourceFactory;
		}
		
		public List<Message> getMessages() {
			return messages;
		}
		
		public void setMessages(List<Message> messages) {
			this.messages = messages;
		}
		
		private ResourceFactory getMultipliedFactory(double multiply) {
			if (resourceFactory != null)
				return resourceFactory.withMultiplier(resourceFactory.getMultiplier() * multiply);
			else
				return null;
		}
		
		public MessagedResource multiply(double multiply) {
			return new MessagedResource(getMultipliedFactory(multiply), messages);
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 31).
		            append(resourceFactory).
		            append(messages).
		            toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
	            return false;
	        if (obj == this)
	            return true;
	        if (obj.getClass() != getClass())
	            return false;

	        MessagedResource other = (MessagedResource) obj;
	        return new EqualsBuilder().
	            append(resourceFactory, other.resourceFactory).
	            append(messages, other.messages).
	            isEquals();
		}
		
		@Override
		public String toString() {
			if (messages == null || messages.size() == 0)
				return String.format("%s", resourceFactory);
			else
				return String.format("%s [%s]", resourceFactory, StringUtils.join(messages, ", "));
		}
	}
}
