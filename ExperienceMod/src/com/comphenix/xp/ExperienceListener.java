package com.comphenix.xp;

/**
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.xp.lookup.*;
import com.comphenix.xp.messages.ChannelProvider;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.rewards.RewardProvider;
import com.google.common.base.Objects;

public class ExperienceListener implements Listener {
	
	private final String permissionKeepExp = "experiencemod.keepexp";
	private final String permissionRewardSmelting = "experiencemod.rewards.smelting";
	private final String permissionRewardBrewing = "experiencemod.rewards.brewing";
	private final String permissionRewardCrafting = "experiencemod.rewards.crafting";
	private final String permissionRewardBonus = "experiencemod.rewards.bonus";
	private final String permissionRewardBlock = "experiencemod.rewards.block";
	private final String permissionRewardPlacing = "experiencemod.rewards.placing";
	private final String permissionRewardFishing = "experiencemod.rewards.fishing";

	private JavaPlugin parentPlugin;
	private Debugger debugger;
	private Presets presets;
	
	// To determine spawn reason
	private HashMap<Integer, SpawnReason> spawnReasonLookup = new HashMap<Integer, SpawnReason>();

	// Random source
	private Random random = new Random();
	
	public ExperienceListener(JavaPlugin parentPlugin, Debugger debugger, Presets presets) {
		this.parentPlugin = parentPlugin;
		this.debugger = debugger;
		setPresets(presets);
	}
	
	public Presets getPresets() {
		return presets;
	}

	public void setPresets(Presets presets) {
		this.presets = presets;
	}

	public String getPermissionRewardPlacing() {
		return permissionRewardPlacing;
	}

	/**
	 * Load the correct configuration for a given player.
	 * @param world - the given player.
	 * @return The most relevant configuration, or NULL if none were found.
	 */
	public Configuration getConfiguration(Player player) {
		try {
			return presets.getConfiguration(player);
			
		} catch (ParsingException e) {
			// We most likely have complained about this already
			return null;
		}
	}
	
	/**
	 * Load the correct configuration for general world events not associated with any player.
	 * @param world - the world to look for.
	 * @return The most relevant configuration, or NULL if none were found.
	 */
	public Configuration getConfiguration(World world) {
		try {
			return presets.getConfiguration(null, world.getName());
			
		} catch (ParsingException e) {
			//debugger.printDebug(this, "Preset error: %s", e.getMessage());
			return null;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		// See if this deserves more experience
		if (block != null && player != null) { 
			
			ItemStack toolItem = player.getItemInHand();
			ItemQuery retrieveKey = ItemQuery.fromExact(block);
			
			boolean allowBlockReward = player.hasPermission(permissionRewardBlock) && !hasSilkTouch(toolItem);
			boolean allowBonusReward = player.hasPermission(permissionRewardBonus);

			// Only without silk touch
			if (allowBlockReward) {
				Configuration config = getConfiguration(player);
			
				// No configuration or default configuration found
				if (config == null) {
					if (debugger != null)
						debugger.printDebug(this, "Cannot find config for player %s in mining %s.", 
							player.getName(), block);
					
				} else if (config.getSimpleBlockReward().containsKey(retrieveKey)) {

					Action action = config.getSimpleBlockReward().get(retrieveKey);
					RewardProvider rewards = config.getRewardProvider();
					ChannelProvider channels = config.getChannelProvider();
					
					Integer exp = action.rewardPlayer(rewards, random, player, block.getLocation());
					action.emoteMessages(channels, channels.getFormatter(player, exp), player);
					
					if (debugger != null)
						debugger.printDebug(this, "Block mined by %s: Spawned %d xp for item %s.", 
							player.getName(), exp, block.getType());
				}
			}
			
			if (allowBonusReward) {
				Configuration config = getConfiguration(player);

				// No configuration or default configuration found
				if (config == null) {
					if (debugger != null)
						debugger.printDebug(this, "Cannot find config for player %s in mining %s.", 
							player.getName(), block);
					
				} else if (config.getSimpleBonusReward().containsKey(retrieveKey)) {
					
					Action action = config.getSimpleBonusReward().get(retrieveKey);
					RewardProvider rewards = config.getRewardProvider();
					ChannelProvider channels = config.getChannelProvider();
					
					Integer exp = action.rewardPlayer(rewards, random, player, block.getLocation());
					action.emoteMessages(channels, channels.getFormatter(player, exp), player);
					
					if (debugger != null)
						debugger.printDebug(this, "Block destroyed by %s: Spawned %d xp for item %s.", 
							player.getName(), exp, block.getType());
				}
			}
			
			// Done
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) 
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != null) {
			spawnReasonLookup.put(event.getEntity().getEntityId(), 
								  event.getSpawnReason());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
				int exp = action.rewardPlayer(config.getRewardProvider(), random, player);

				action.emoteMessages(channels, channels.getFormatter(player, exp), player);
				
				if (debugger != null)
					debugger.printDebug(this, message, player.getName(), exp);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		// See if this deserves experience
		if (block != null && player != null) { 
			
			boolean allowPlacingReward = player.hasPermission(permissionRewardPlacing);
			
			if (allowPlacingReward) {
				Configuration config = getConfiguration(player);
				
				if (config == null) {
					if (debugger != null)
						debugger.printDebug(this, "No config found for block %s.", block);
					return;
				}
					
				ItemQuery retrieveKey = ItemQuery.fromExact(block);
				ItemTree placeReward = config.getSimplePlacingReward();
				
				if (placeReward.containsKey(retrieveKey)) {
					Action action = placeReward.get(retrieveKey);
					RewardProvider rewards = config.getRewardProvider();
					ChannelProvider channels = config.getChannelProvider();
					
					Integer exp = action.rewardPlayer(rewards, random, player);
					
					// Print messages
					action.emoteMessages(channels, channels.getFormatter(player, exp), player);
					
					if (debugger != null)
						debugger.printDebug(this, "Block placed by %s: Spawned %d xp for item %s.", 
							player.getName(), exp, block.getType());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDeathEvent(EntityDeathEvent event) {
		
		Configuration config;
		LivingEntity entity = event.getEntity();
		boolean hasKiller = entity.getKiller() != null;
		
		// Only drop experience from mobs
		if (entity != null && isMob(entity)) {
			
			Integer id = entity.getEntityId();
			MobQuery query = MobQuery.fromExact(entity, spawnReasonLookup.get(id));

			if (hasKiller)
				config = getConfiguration(entity.getKiller());
			else
				config = getConfiguration(entity.getWorld());
			
			// Guard
			if (config == null) {
				if (debugger != null)
					debugger.printDebug(this, "No config found for mob %d, query: %s", id, query);
				return;
			}
			
			Action action = config.getExperienceDrop().get(query);

			// Make sure the reward has been changed
			if (action != null) {
				
				// Spawn the experience ourself
				event.setDroppedExp(0);
				RewardProvider rewards = config.getRewardProvider();
				ChannelProvider channels = config.getChannelProvider();
				Integer xp = action.rewardAnyone(rewards, random, entity.getWorld(), entity.getLocation());
				
				action.announceMessages(channels, channels.getFormatter(null, xp));
				
				if (debugger != null)
					debugger.printDebug(this, "Entity %d: Changed experience drop to %d", id, xp);
			
			} else if (config.isDefaultRewardsDisabled() && hasKiller) {
				
				// Disable all mob XP
				event.setDroppedExp(0);
				
				if (debugger != null)
					debugger.printDebug(this, "Entity %d: Default mob experience disabled.", id);
	
			} else if (!config.isDefaultRewardsDisabled() && hasKiller) {
				
				int expDropped = event.getDroppedExp();
				
				// Alter the default experience drop too
				if (config.getMultiplier() != 1) {
					Range increase = new Range(expDropped * config.getMultiplier());
					int expChanged = increase.sampleInt(random);
					
					event.setDroppedExp(expChanged);
					
					if (debugger != null)
						debugger.printDebug(this, "Entity %d: Changed experience drop to %d", id, expChanged);
				}
			}
			
			// Remove it from the lookup
			spawnReasonLookup.remove(id);
		}
	}
	
	private boolean isMob(LivingEntity entity) {
		
		EntityType type = entity.getType();
		
		// Exclude players
		return type != null &&
			   type != EntityType.PLAYER;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClickEvent(InventoryClickEvent event) {

		Player player = (Player) event.getWhoClicked();
		ItemStack toCraft = event.getCurrentItem();
		
		Configuration config = null;

		// Was this from a result slot (crafting, smelting or brewing)?
		if (player != null &&
		    event.getInventory() != null &&
		    event.getSlotType() == SlotType.RESULT &&
		    hasItems(toCraft)) {
			
			// Handle different types
			switch (event.getInventory().getType()) {
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
					
					handleInventory(event, config.getRewardProvider(), config.getChannelProvider(),
								    config.getSimpleBrewingReward(), true);
				
					// Yes, this feels a bit like a hack to me too. Blame faulty design. Anyways, the point
					// is that we get to check more complex potion matching rules, like "match all splash potions"
					// or "match all level 2 regen potions (splash or not)".
					handleInventory(event, config.getRewardProvider(), config.getChannelProvider(),
							config.getComplexBrewingReward().getItemQueryAdaptor(), true);
				}
				
				break;
			case CRAFTING:
			case WORKBENCH:
				if (player.hasPermission(permissionRewardCrafting)) {
					config = getConfiguration(player);
					
					if (config != null) {
						handleInventory(event, config.getRewardProvider(), config.getChannelProvider(),
										config.getSimpleCraftingReward(), false);
					} else if (debugger != null) {
						debugger.printDebug(this, "No config found for %s with crafting %s.", player.getName(), toCraft);
					}
				}
				break;
				
			case FURNACE:
				if (player.hasPermission(permissionRewardSmelting)) {
					config = getConfiguration(player);
					
					if (config != null) {
						handleInventory(event, config.getRewardProvider(), config.getChannelProvider(),
										config.getSimpleSmeltingReward(), true);
					} else if (debugger != null) {
						debugger.printDebug(this, "No config found for %s with smelting %s.", player.getName(), toCraft);
					}
				}
				break;
			}
		}
	}
	
	private void handleInventory(InventoryClickEvent event, RewardProvider rewardsProvider, 
								 ChannelProvider channelsProvider, ItemTree rewards, boolean partialResults) {
		
		Player player = (Player) event.getWhoClicked();
		ItemStack toStore = event.getCursor();
		ItemStack toCraft = event.getCurrentItem();
		
		ItemQuery retrieveKey = ItemQuery.fromExact(toCraft);
		Action action = rewards.get(retrieveKey);
		
		// Make sure there is an experience reward
		if (!hasExperienceReward(rewards, retrieveKey))
			return;

		if (event.isShiftClick()) {
			// Hack ahoy
			schedulePostCraftingReward(player, rewardsProvider, action, toCraft);
		} else {
			
			// The items are stored in the cursor. Make sure there's enough space.
			int count = getStorageCount(toStore, toCraft, partialResults);

			if (count > 0) {
				
				// Some cruft here - the stack is only divided when the user has no cursor items
				if (partialResults && event.isRightClick() && !hasItems(toStore)) {
					count = Math.max(count / 2, 1);
				}
				
				// Give the experience straight to the user
				Integer exp = action.rewardPlayer(rewardsProvider, random, player, count);
				action.emoteMessages(channelsProvider, channelsProvider.getFormatter(player, exp), player);
				
				// Like above
				if (debugger != null)
					debugger.printDebug(this, "User %s - spawned %d xp for item %s.", 
						player.getName(), exp, toCraft.getType());
			}
		}
	}
	
	// HACK! The API doesn't allow us to easily determine the resulting number of
	// crafted items, so we're forced to compare the inventory before and after.
	private void schedulePostCraftingReward(final HumanEntity player, final RewardProvider provider, 
											final Action action, final ItemStack compareItem) {
		
		final ItemStack[] preInv = player.getInventory().getContents();
		final int ticks = 1; // May need adjusting
		
		// Clone the array. The content may (was for me) mutable.
		for (int i = 0; i < preInv.length; i++) {
			preInv[i] = preInv[i] != null ? preInv[i].clone() : null;
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(parentPlugin, new Runnable() {
			@Override
			public void run() {
				final ItemStack[] postInv = player.getInventory().getContents();
				int newItemsCount = 0;
				
				for (int i = 0; i < preInv.length; i++) {
					ItemStack pre = preInv[i];
					ItemStack post = postInv[i];

					// We're only interested in filled slots that are different
					if (hasSameItem(compareItem, post) && (hasSameItem(compareItem, pre) || pre == null)) {
						newItemsCount += post.getAmount() - (pre != null ? pre.getAmount() : 0);
					}
				}
				
				if (newItemsCount > 0) {
					int exp = action.rewardPlayer(
							provider, random, (Player) player, newItemsCount);
					
					// We know this is from crafting
					if (debugger != null)
						debugger.printDebug(this, "User %s - spawned %d xp for %d items of %s.", 
							player.getName(), permissionRewardCrafting, exp, 
							newItemsCount, compareItem.getType());
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
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		
		Player player = event.getEntity();
		
		if (player != null) {
			// Permission check
	        if(player.hasPermission(permissionKeepExp)){

	            event.setDroppedExp(0);
	            event.setKeepLevel(true);
	            
	            if (debugger != null)
	        		debugger.printDebug(this, "Prevented experience loss for %s.", player.getName());
	            
	        } else {
	        	event.setKeepLevel(false);
	        }
		}
	}
	
	private boolean hasItems(ItemStack stack) {
		return stack != null && stack.getAmount() > 0;
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
}
