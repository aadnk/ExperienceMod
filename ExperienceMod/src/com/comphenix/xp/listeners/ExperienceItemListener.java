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

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.xp.Action;
import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.Presets;
import com.comphenix.xp.lookup.ItemQuery;
import com.comphenix.xp.lookup.ItemTree;
import com.comphenix.xp.lookup.PlayerRewards;
import com.comphenix.xp.messages.ChannelProvider;
import com.comphenix.xp.messages.MessagePlayerQueue;
import com.comphenix.xp.mods.BlockResponse;
import com.comphenix.xp.mods.CustomBlockProviders;
import com.comphenix.xp.rewards.RewardProvider;
import com.google.common.base.Objects;

public class ExperienceItemListener extends AbstractExperienceListener {

	private final String permissionRewardSmelting = "experiencemod.rewards.smelting";
	private final String permissionRewardBrewing = "experiencemod.rewards.brewing";
	private final String permissionRewardCrafting = "experiencemod.rewards.crafting";
	private final String permissionRewardFishing = "experiencemod.rewards.fishing";
	private final String permissionUntouchable = "experiencemod.untouchable";

	private JavaPlugin parentPlugin;
	private Debugger debugger;
	private CustomBlockProviders blockProvider;
	
	// Random source
	private Random random = new Random();
	
	public ExperienceItemListener(JavaPlugin parentPlugin, Debugger debugger, 
							      CustomBlockProviders blockProvider, Presets presets) {
		
		this.parentPlugin = parentPlugin;
		this.debugger = debugger;
		this.blockProvider = blockProvider;
		setPresets(presets);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerFishEvent(PlayerFishEvent event) {
		
		Player player = event.getPlayer();
		
		String message = null;
		Action action = null;

		if (player != null && player.hasPermission(permissionRewardFishing)) {
			
			Configuration config = getConfiguration(player);
			
			// No configuration or default configuration found
			if (config == null) {
				if (debugger != null)
					debugger.printDebug(this, "Cannot find config for player %s in fishing.", player.getName());
				return;
			}
				
			PlayerRewards playerReward = config.getPlayerRewards();
			ChannelProvider channels = config.getChannelProvider();
			
			// Reward type
			switch (event.getState()) {
			case CAUGHT_FISH:
				action = playerReward.getFishingSuccess();
				message = "Fish caught by %s: Spawned %d xp.";
				break;

			case FAILED_ATTEMPT:
				action = playerReward.getFishingFailure();
				message = "Fishing failed for %s: Spawned %d xp.";
				break;
			}
			
			// Has an action been set?
			if (action != null) {
				
				// Check and see if the player is broke
				if (!action.canRewardPlayer(config.getRewardProvider(), player, 1)) {
					if (debugger != null)
						debugger.printDebug(this, "Unable to penalize fishing for %s. Not enough funds.", player.getName());
					
					// Don't catch the fish
					if (!player.hasPermission(permissionUntouchable))
						event.setCancelled(true);
					return;
				}
				
				int exp = action.rewardPlayer(config.getRewardProvider(), random, player);
				config.getMessageQueue().enqueue(player, action, channels.getFormatter(player, exp));
				
				if (debugger != null)
					debugger.printDebug(this, message, player.getName(), exp);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBrewEvent(BrewEvent event) {
		
		// Reset the potion markers
		if (event != null &&
			event.getContents() != null) {
			
			for (ItemStack stack : event.getContents().getContents()) {
			
				// Find all potions in the brewing stand
				if (hasItems(stack) && stack.getType() == Material.POTION) {
					
					PotionMarker marker = new PotionMarker(stack.getDurability());
					
					// Reset potion markers
					marker.reset();
					stack.setDurability(marker.toDurability());
				}
			}
			
			if (debugger != null)
				debugger.printDebug(this, "Reset potion markers in brewing stand %s", getLocationString(event.getBlock()) );
		}
	}
	
	// Convert a block to a more readable format
	private String getLocationString(Block block) {
		return String.format("x=%d, y=%d, z=%d", block.getX(), block.getY(), block.getZ());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClickEvent(InventoryClickEvent event) {

		// Clicked item and player
		Player player = (Player) event.getWhoClicked();
		ItemStack toCraft = event.getCurrentItem();

		// Make sure we have a player, inventory and item
		if (player != null && event.getInventory() != null && hasItems(toCraft)) {

			ItemQuery lastBlock = blockProvider.getLastInteraction().getLastRightClick(player, null);
			BlockResponse response = blockProvider.processInventoryClick(event, lastBlock);
			
			// See if we should handle the inventory click ourself
			if (BlockResponse.isSuccessful(response) && response.hasDefaultBehavior()) {
				processInventory(event, response.getDefaultBehavior());
			}
		}
	}
	
	/**
	 * Handles the given inventory event using the default behavior for the given inventory type.
	 * @param event - inventory click event.
	 * @param type - inventory type.
	 */
	public void processInventory(InventoryClickEvent event, InventoryType type) {
		
		// Clicked item and player
		Player player = (Player) event.getWhoClicked();
		ItemStack toCraft = event.getCurrentItem();
		
		Configuration config = null;
		
		// Handle different types
		switch (type) {
		case BREWING:
			// Do not proceed if the user isn't permitted
			if (player.hasPermission(permissionRewardBrewing)) {
				config = getConfiguration(player);
				
				// Guard again
				if (config == null) {
					if (debugger != null)
						debugger.printDebug(this, "No config found for %s with brewing %s.", player.getName(), toCraft);
					return;
				}
				
				// Prepare rewards and actions
				RewardableAction future = potionItemReward(config);
				Action simpleAction = getAction(toCraft, config.getSimpleBrewingReward());
				
				// Yes, this feels a bit like a hack to me too. Blame faulty design. Anyways, the point
				// is that we get to check more complex potion matching rules, like "match all splash potions"
				// or "match all level 2 regen potions (splash or not)".
				Action complexAction = getAction(toCraft, config.getComplexBrewingReward().getItemQueryAdaptor());
				
				handleInventory(event, simpleAction, future, true);
				handleInventory(event, complexAction, future, true);
			}
			
			break;
		case CRAFTING:
		case WORKBENCH:
			if (player.hasPermission(permissionRewardCrafting)) {
				
				config = getConfiguration(player);

				if (config != null) {
					RewardableAction future = genericItemReward(config);
					Action action = getAction(toCraft, config.getSimpleCraftingReward());
					
					handleInventory(event, action, future, false);
					
				} else if (debugger != null) {
					debugger.printDebug(this, "No config found for %s with crafting %s.", player.getName(), toCraft);
				}
			}
			break;
			
		case FURNACE:
			if (player.hasPermission(permissionRewardSmelting)) {
				config = getConfiguration(player);
				
				if (config != null) {
					RewardableAction future = genericItemReward(config);
					Action action = getAction(toCraft, config.getSimpleSmeltingReward());
					
					handleInventory(event, action, future, true);
					
				} else if (debugger != null) {
					debugger.printDebug(this, "No config found for %s with smelting %s.", player.getName(), toCraft);
				}
			}
			break;
		}
	}
	
	/**
	 * Retrieves the most relevant action, or NULL if no action can be found.
	 * @param toCraft Item to query after.
	 * @param rewards Tree of rewards and actions.
	 * @return The most relevant action, or NULL.
	 */
	private Action getAction(ItemStack toCraft, ItemTree rewards) {
		
		ItemQuery retrieveKey = ItemQuery.fromExact(toCraft);
		
		if (hasExperienceReward(rewards, retrieveKey)) {
			return rewards.get(retrieveKey);
		} else {
			return null;
		}
	}
	
	private void handleInventory(InventoryClickEvent event, Action action, 
								 RewardableAction rewardAction, boolean partialResults) {
		
		final Player player = (Player) event.getWhoClicked();
		final ItemStack toStore = event.getCursor().clone();
		final ItemStack toCraft = event.getCurrentItem().clone();
		
		// Nothing do do
		if (action == null)
			return;
		
		// Set debugger
		action.setDebugger(debugger);
		
		if (event.isShiftClick()) {
			
			// Store this in case we have to cancel the event manually
			final Inventory blockInventory = event.getInventory();
			final Inventory playerInventory = player.getInventory();
			final ItemStack[] originalPlayerInventory = getInventoryCopy(playerInventory);
			final ItemStack[] originalBlockInventory = getInventoryCopy(blockInventory);
			
			// Hack ahoy. So ugly!
			schedulePostCraftingReward(player, action, rewardAction, toCraft, 
					new Runnable() {
						
						// Revert crafting. Attempt to, at least.
						public void run() {
							
							// Don't touch the inventory of the untouchables
							if (!player.hasPermission(permissionUntouchable)) {
								playerInventory.setContents(originalPlayerInventory);
								blockInventory.setContents(originalBlockInventory);
								player.setItemOnCursor(toStore);
							}
						}
					});
			
		} else {
			
			// The items are stored in the cursor. Make sure there's enough space.
			int count = getStorageCount(toStore, toCraft, partialResults);

			if (count > 0) {
				
				// Some cruft here - the stack is only divided when the user has no cursor items
				if (partialResults && event.isRightClick() && !hasItems(toStore)) {
					count = Math.max(count / 2, 1);
				}
				
				// Simple enough
				if (rewardAction.canPerform(player, action, count)) {
					rewardAction.performAction(player, toCraft, action, count);
					
				} else {
					// Events will not be cancelled for untouchables
					if (!player.hasPermission(permissionUntouchable))
						event.setCancelled(true);
				}
			}
		}
	}
	
	private RewardableAction potionItemReward(Configuration config) {
		
		final RewardableAction fundamental = genericItemReward(config);
		
		// Next, mark all potions as "consumed" or "rewarded"
		return new RewardableAction() {
			
			@Override
			public void performAction(Player player, ItemStack stack, Action action, int count) {
				
				PotionMarker marker = new PotionMarker(stack.getDurability());
				
				// Only reward potions once
				if (!marker.hasBeenRewarded()) {
					fundamental.performAction(player, stack, action, count);
				
					marker.setBeenRewarded(true);
					stack.setDurability(marker.toDurability());
				}
			}

			@Override
			public boolean canPerform(Player player, Action action, int count) {
				return fundamental.canPerform(player, action, count);
			}
		};	
	}
	
	private RewardableAction genericItemReward(Configuration config) {
		
		final RewardProvider rewardsProvider = config.getRewardProvider();
		final ChannelProvider channelsProvider = config.getChannelProvider();
		final MessagePlayerQueue messageQueue = config.getMessageQueue();
		
		// Create a rewardable future action handler
		return new RewardableAction() {
			@Override
			public void performAction(Player player, ItemStack stack, Action action, int count) {

				// Give the experience straight to the user
				Integer exp = action.rewardPlayer(rewardsProvider, random, player, count);
				messageQueue.enqueue(player, action, channelsProvider.getFormatter(player, exp, count));
				
				// Like above
				if (debugger != null)
					debugger.printDebug(this, "User %s - spawned %d xp for item %s.", 
						player.getName(), exp, stack.getType());
			}

			@Override
			public boolean canPerform(Player player, Action action, int count) {
				return action.canRewardPlayer(rewardsProvider, player, count);
			}
		};
	}
	
	/**
	 * Retrieves a copy of the content of a given inventory.
	 * @param inventory - inventory to copy.
	 * @return A copy of the content.
	 */
	private ItemStack[] getInventoryCopy(Inventory inventory) {
		
		final ItemStack[] copy = inventory.getContents();
		
		// Clone the array. The content may (was for me) mutable.
		for (int i = 0; i < copy.length; i++) {
			copy[i] = copy[i] != null ? copy[i].clone() : null;
		}
		
		return copy;
	}
	
	// HACK! The API doesn't allow us to easily determine the resulting number of
	// crafted items, so we're forced to compare the inventory before and after.
	private void schedulePostCraftingReward(final Player player, final Action action,
											final RewardableAction rewardAction, 
											final ItemStack compareItem,
											final Runnable cancel) {
											
		final ItemStack[] preInv = getInventoryCopy(player.getInventory());
		final int ticks = 1; // May need adjusting
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(parentPlugin, new Runnable() {
			@Override
			public void run() {
				final ItemStack[] postInv = player.getInventory().getContents();
				int newItemsCount = 0;
				
				// The most relevant item stack to return
				ItemStack last = compareItem;
				
				for (int i = 0; i < preInv.length; i++) {
					ItemStack pre = preInv[i];
					ItemStack post = postInv[i];

					// We're only interested in filled slots that are different
					if (hasSameItem(compareItem, post) && (hasSameItem(compareItem, pre) || pre == null)) {
						newItemsCount += post.getAmount() - (pre != null ? pre.getAmount() : 0);
						
						if (post.getAmount() > 0)
							last = post;
					}
				}
				
				// See if we actually got anything
				if (newItemsCount > 0) {
					
					// See if the event must be cancelled
					if (!rewardAction.canPerform(player, action, newItemsCount)) {
						// A big stinky hack in a hack
						cancel.run();
					} else {
						rewardAction.performAction(player, last, action, newItemsCount);
					}
				}
			}
		}, ticks);
	}
	
	private boolean hasSameItem(ItemStack a, ItemStack b) {
		if (a == null)
			return b == null;
		else if (b == null)
			return a == null;
		
		return a.getTypeId() == b.getTypeId() &&
			   a.getDurability() == b.getDurability() && 
			   Objects.equal(a.getEnchantments(), b.getEnchantments());
	}
	
	private boolean hasExperienceReward(ItemTree rewards, ItemQuery key) {
		// Make sure there is any experience
		return rewards.containsKey(key) && !rewards.get(key).equals(Action.Default);
	}
	
	// Recipes are cancelled if there's isn't exactly enough space. 
	public int getStorageCount(ItemStack storage, ItemStack addition, boolean allowPartial) {
		
		if (addition == null)
			return 0;
		else if (storage == null)
			// All storage slots have the same limits
			return addition.getAmount(); 
		// Yes, storage might be air blocks ... weird.
		else if (storage.getType() != Material.AIR && !hasSameItem(storage, addition))
			// Items MUST be the same
			return 0;
		
		int sum = storage.getAmount() + addition.getAmount();
		int max = storage.getType().getMaxStackSize();

		// Now determine the number of additional items in the storage stack
		if (sum > max) {
			return allowPartial ? max - storage.getAmount() : 0;
		} else {
			return addition.getAmount();
		}
	}
		
	private boolean hasItems(ItemStack stack) {
		return stack != null && stack.getAmount() > 0;
	}
}
