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

package com.comphenix.xp.listeners;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.comphenix.xp.Action;
import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.Presets;
import com.comphenix.xp.expressions.NamedParameter;
import com.comphenix.xp.extra.Permissions;
import com.comphenix.xp.history.HistoryProviders;
import com.comphenix.xp.lookup.ItemQuery;
import com.comphenix.xp.lookup.ItemTree;
import com.comphenix.xp.messages.ChannelProvider;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.rewards.ResourceHolder;
import com.comphenix.xp.rewards.RewardProvider;

public class ExperienceBlockListener extends AbstractExperienceListener {
	
	private Debugger debugger;
	private HistoryProviders historyProviders;

	// Random source
	private Random random = new Random();
	private ErrorReporting report = ErrorReporting.DEFAULT;
	
	public ExperienceBlockListener(Debugger debugger, Presets presets, HistoryProviders historyProviders) {
		this.debugger = debugger;
		this.historyProviders = historyProviders;
		setPresets(presets);
	}
	
	// DO NOT CHANGE TO MONITOR, AS IT WILL CONFLICT WITH LOGBLOCK
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		
		try {
			Block block = event.getBlock();
			Player player = event.getPlayer();
			
			// See if this deserves more experience
			if (block != null && player != null) { 
				handleBlockBreakEvent(event, block, player);
			}
			
		} catch (Exception e) {
			report.reportError(debugger, this, e, event);
		}
	}
		
	private void handleBlockBreakEvent(BlockBreakEvent event, Block block, Player player) {
		
		ItemStack toolItem = player.getItemInHand();

		boolean allowBlockReward = Permissions.hasRewardBlock(player) && !hasSilkTouch(toolItem);
		boolean allowBonusReward = Permissions.hasRewardBonus(player);

		// Only without silk touch
		if (allowBlockReward) {
			Configuration config = getConfiguration(player);
			handleBlockReward(event, config, config.getSimpleBlockReward(), "mined");
		}
		
		if (allowBonusReward) {
			Configuration config = getConfiguration(player);
			handleBlockReward(event, config, config.getSimpleBonusReward(), "destroyed");
		}
		
		// Done
	}
	
	private void handleBlockReward(BlockBreakEvent event, Configuration config, ItemTree tree, String description) {
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		ItemQuery retrieveKey = ItemQuery.fromAny(block);
		
		// No configuration or default configuration found
		if (config == null) {
			if (hasDebugger())
				debugger.printDebug(this, "Cannot find config for player %s in mining %s.", 
					player.getName(), block);
			
		} else {
			Action action = getBlockBonusAction(tree, retrieveKey, block);
			RewardProvider rewards = config.getRewardProvider();
			ChannelProvider channels = config.getChannelProvider();
			
			// Guard
			if (action == null || action.hasNothing(channels))
				return;
			
			Collection<NamedParameter> params = config.getParameterProviders().getParameters(action, block);
			List<ResourceHolder> generated = action.generateRewards(params, rewards, random);
			
			if (!action.canRewardPlayer(rewards, player, generated)) {
				if (hasDebugger())
					debugger.printDebug(this, "Block " + description + " by %s cancelled: Not enough resources for item %s",
						player.getName(), block.getType());
				
				if (!Permissions.hasUntouchable(player))
					event.setCancelled(true);
				return;
			}
			
			// Disable vanilla experience
			event.setExpToDrop(0);

			Collection<ResourceHolder> result = action.rewardPlayer(rewards, player, generated, block.getLocation());
			config.getMessageQueue().enqueue(player, action, channels.getFormatter(player, result));

			if (hasDebugger())
				debugger.printDebug(this, "Block " + description + " by %s: Spawned %s for item %s.", 
					player.getName(), StringUtils.join(result, ", "), block.getType());
		}
	}

	private Action getBlockBonusAction(ItemTree tree, ItemQuery key, Block block) {
		
		List<Integer> ids = tree.getAllRankedID(key);
		Set<Integer> noCreation = tree.getPlayerCreated().getSingle(false);
		Set<Integer> yesCreation = tree.getPlayerCreated().getSingle(true);
		
		for (Integer id : ids) {
			// Do any of these IDs have a player-option?
			if ((noCreation != null && noCreation.contains(id)) ||
				(yesCreation != null && yesCreation.contains(id))) {
				
				// In that case, specify the player creation value
				Boolean placedBefore = hasBeenPlacedBefore(block);
				ItemQuery copy = new ItemQuery(
						key.getItemID(), key.getDurability(), Utility.getElementList(placedBefore));
				
				if (hasDebugger())
					debugger.printDebug(this, "New query: %s", copy);
				
				// Perform the search again with this additional information
				return tree.get(copy);
			}
		}
		
		// No need for more details
		return tree.get(key);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		
		try {
			Block block = event.getBlock();
			Player player = event.getPlayer();
			
			// See if this deserves experience
			if (block != null && player != null) { 
				handleBlockPlaceEvent(event, block, player);
			}
			
		} catch (Exception e) {
			report.reportError(debugger, this, e, event);
		}
	}
	
	public void handleBlockPlaceEvent(BlockPlaceEvent event, Block block, Player player) {
		
		boolean allowPlacingReward = Permissions.hasRewardPlacing(player);
		
		// Inform other listeners too
		if (historyProviders != null && historyProviders.getMemoryService() != null) {
			historyProviders.getMemoryService().onBlockPlaceEvent(event);
		}
		
		if (allowPlacingReward) {
			Configuration config = getConfiguration(player);
			
			if (config == null) {
				if (hasDebugger())
					debugger.printDebug(this, "No config found for block %s.", block);
				return;
			}
				
			ItemQuery retrieveKey = ItemQuery.fromExact(block);
			ItemTree placeReward = config.getSimplePlacingReward();
			
			if (placeReward.containsKey(retrieveKey)) {
				Action action = placeReward.get(retrieveKey);
				RewardProvider rewards = config.getRewardProvider();
				ChannelProvider channels = config.getChannelProvider();
				Collection<NamedParameter> params = config.getParameterProviders().getParameters(action, block);
				
				List<ResourceHolder> generated = action.generateRewards(params, rewards, random);
				
				// Make sure the action is legal
				if (!action.canRewardPlayer(rewards, player, generated)) {
					if (hasDebugger())
						debugger.printDebug(this, "Block place by %s cancelled: Not enough resources for item %s",
							player.getName(), block.getType());
					
					// Events will not be cancelled for untouchables
					if (!Permissions.hasUntouchable(player))
						event.setCancelled(true);
					return;
				}
				
				// Reward and print messages
				Collection<ResourceHolder> result = action.rewardPlayer(rewards, player, generated);
				config.getMessageQueue().enqueue(player, action, channels.getFormatter(player, result));
				
				if (hasDebugger())
					debugger.printDebug(this, "Block placed by %s: Spawned %s for item %s.", 
						player.getName(), StringUtils.join(result, ", "), block.getType());
			}
		}
	}
	
	/**
	 * Determines if a block has been placed before or not.
	 * @param block - block to test.
	 * @return TRUE if it has been placed before, FALSE otherwise.
	 */
	public boolean hasBeenPlacedBefore(Block block) {
		
		Location loc = block.getLocation();
		
		Boolean before = historyProviders.hasPlayerHistory(loc, true, debugger);
		
		if (before != null)
			return before;
		else {
			if (hasDebugger())
				debugger.printDebug(this, "No block history found.");
			
			// Assume it hasn't. More likely than not.
			return false;
		}
	}
	
	private boolean hasSilkTouch(ItemStack stack) {
		if (stack != null) {
			Map<Enchantment, Integer> enchantments = stack.getEnchantments();
			
			// Any silk touch enchantment?
			if (enchantments != null) {
				return enchantments.containsKey(Enchantment.SILK_TOUCH);
			}
		}
		
		// No enchantment detected
		return false;
	}
	
	private boolean hasDebugger() {
		return debugger != null && debugger.isDebugEnabled();
	}
}
