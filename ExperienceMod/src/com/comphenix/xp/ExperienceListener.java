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

	public Configuration getConfiguration(Player player) {
		return presets.getConfiguration(player);
	}
	
	public Configuration getConfiguration(World world) {
		return presets.getConfiguration(null, world.getName());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		// See if this deserves more experience
		if (block != null && player != null) { 
			
			ItemStack toolItem = player.getItemInHand();
			ItemQuery retrieveKey = new ItemQuery(block);
			
			boolean allowBlockReward = player.hasPermission(permissionRewardBlock) && !hasSilkTouch(toolItem);
			boolean allowBonusReward = player.hasPermission(permissionRewardBonus);

			// Only without silk touch
			if (allowBlockReward) {
				Configuration config = getConfiguration(player);

				if (config.getSimpleBlockReward().containsKey(retrieveKey))
					handleBlockEvent("Block mined by %s: Spawned %d xp for item %s.", 
							config.getRewardManager(), config.getSimpleBlockReward(), 
							retrieveKey, block, player);
			}
			
			if (allowBonusReward) {
				Configuration config = getConfiguration(player);

				if (config.getSimpleBonusReward().containsKey(retrieveKey))
					handleBlockEvent("Block destroyed by %s: Spawned %d xp for item %s.", 
									 config.getRewardManager(), config.getSimpleBonusReward(), 
									 retrieveKey, block, player);
			}
		}
	}
	
	private void handleBlockEvent(String debugMessage, Rewardable manager, 
								  ItemTree reward, ItemQuery query, 
								  Block block, Player player) {
		
		int exp = reward.get(query).sampleInt(random);
		
		manager.reward(player, block.getLocation(), exp);
		debugger.printDebug(this, debugMessage, player.getName(), exp, block.getType());
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
		Range reward = null;

		if (player != null && player.hasPermission(permissionRewardFishing)) {
			
			Configuration config = getConfiguration(player);
			PlayerRewards playerReward = config.getPlayerRewards();
			
			// Reward type
			switch (event.getState()) {
			case CAUGHT_FISH:
				reward = playerReward.getFishingSuccess();
				message = "Fish caught by %s: Spawned %d xp.";
				break;

			case FAILED_ATTEMPT:
				reward = playerReward.getFishingFailure();
				message = "Fishing failed for %s: Spawned %d xp.";
				break;
			}
			
			// Has a reward been set?
			if (reward != null) {
				int exp = reward.sampleInt(random);
				
				config.getRewardManager().reward(player, player.getLocation(), exp);
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
				ItemQuery retrieveKey = new ItemQuery(block);
				ItemTree placeReward = config.getSimplePlacingReward();
				
				if (placeReward.containsKey(retrieveKey)) {
					int exp = placeReward.get(retrieveKey).sampleInt(random);
					config.getRewardManager().reward(player, exp);
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
			MobQuery query = new MobQuery(entity, spawnReasonLookup.get(id));

			if (hasKiller)
				config = getConfiguration(entity.getKiller());
			else
				config = getConfiguration(entity.getWorld());
			
			Range reward = config.getExperienceDrop().get(query);

			// Make sure the reward has been changed
			if (reward != null) {
				int xp = reward.sampleInt(random);
				
				event.setDroppedExp(xp);
				debugger.printDebug(this, "Entity %d: Changed experience drop to %d", id, xp);
			
			} else if (config.isDefaultRewardsDisabled() && hasKiller) {
				
				// Disable all mob XP
				event.setDroppedExp(0);
				debugger.printDebug(this, "Entity %d: Default mob experience disabled.", id);
	
			} else if (!config.isDefaultRewardsDisabled() && hasKiller) {
				
				int expDropped = event.getDroppedExp();
				
				// Alter the default experience drop too
				if (config.getMultiplier() != 1) {
					Range increase = new Range(expDropped * config.getMultiplier());
					int expChanged = increase.sampleInt(random);
					
					debugger.printDebug(this, "Entity %d: Changed experience drop to %d", id, expChanged);
					event.setDroppedExp(expChanged);
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
		Configuration config = null;

		// Was this from a result slot (crafting, smelting or brewing)?
		if (player != null &&
		    event.getInventory() != null &&
		    event.getSlotType() == SlotType.RESULT) {
			
			// Handle different types
			switch (event.getInventory().getType()) {
			case BREWING:
				config = getConfiguration(player);
				handleInventory(permissionRewardBrewing, event, 
						config.getRewardManager(), config.getSimpleBrewingReward());
				
				// Yes, this feels a bit like a hack to me too. Blame faulty design. Anyways, the point
				// is that we get to check more complex potion matching rules, like "match all splash potions"
				// or "match all level 2 regen potions (splash or not)".
				handleInventory(permissionRewardBrewing, event, 
						config.getRewardManager(), config.getComplexBrewingReward().getItemQueryAdaptor());
				break;
			case CRAFTING:
			case WORKBENCH:
				config = getConfiguration(player);
				handleCrafting(permissionRewardCrafting, event, 
						config.getRewardManager(), config.getSimpleCraftingReward());
				break;
			case FURNACE:
				config = getConfiguration(player);
				handleInventory(permissionRewardSmelting, event, 
						config.getRewardManager(), config.getSimpleSmeltingReward());
				break;
			}
		}
	}
	
	private void handleInventory(String permission, InventoryClickEvent event, Rewardable manager, ItemTree rewards)
	{
		HumanEntity player = event.getWhoClicked();
		ItemStack toRetrieve = event.getCurrentItem();
		boolean hasCursorItems = hasItems(event.getCursor());

		// Is there any items to get?
		if (player != null && hasItems(toRetrieve)) {
			// Make sure this player CAN receive experience
			boolean allowReward = player.hasPermission(permission);
			ItemQuery matchKey = new ItemQuery(toRetrieve);
			
			if (hasExperienceReward(rewards, matchKey) && allowReward) {
				// Some cruft here - the stack is only divided when the user has no cursor items
				int divisor = !hasCursorItems && event.isRightClick() ? 2 : 1;
				int factor = toRetrieve.getAmount() / divisor;
				int exp = rewards.get(matchKey).sampleInt(random) * Math.max(factor, 1);
				
				// Give the experience straight to the user
				manager.reward((Player) player, exp);
				debugger.printDebug(this, "User %s - spawned %d xp for item %s.", 
						player.getName(), permission, exp, toRetrieve.getType());
			}
		}
	}
	
	private void handleCrafting(String permission, InventoryClickEvent event, Rewardable manager, ItemTree rewards) {
		
		HumanEntity player = event.getWhoClicked();
		ItemStack toCraft = event.getCurrentItem();
		ItemStack toStore = event.getCursor();

		// Make sure we are actually crafting anything
		if (player != null && hasItems(toCraft)) {

			ItemQuery retrieveKey = new ItemQuery(toCraft);
			
			// Do not proceed if the user isn't permitted
			if (!player.hasPermission(permission)) 
				return; 
			// Make sure there is an experience reward
			if (!hasExperienceReward(rewards, retrieveKey))
				return;
			
			int expPerItem = rewards.get(retrieveKey).sampleInt(random);
			
			if (event.isShiftClick()) {
				// Hack ahoy
				schedulePostCraftingReward(player, manager, expPerItem, toCraft);
			} else {
				// The items are stored in the cursor. Make sure there's enough space.
				if (isStackSumLegal(toCraft, toStore)) {
					int exp = toCraft.getAmount() * expPerItem;
					
					// Give the experience straight to the user
					manager.reward((Player) player, exp);
				
					// Like above
					debugger.printDebug(this, "User %s - spawned %d xp for item %s.", 
							player.getName(), permission, exp, toCraft.getType());
				}
			}
		}
	}
	
	// HACK! The API doesn't allow us to easily determine the resulting number of
	// crafted items, so we're forced to compare the inventory before and after.
	private void schedulePostCraftingReward(final HumanEntity player, final Rewardable manager, 
											final int expPerItem, final ItemStack compareItem) {
		
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
					manager.reward((Player) player, expPerItem * newItemsCount);
					
					// We know this is from crafting
					debugger.printDebug(this, "User %s - spawned %d xp for %d items of %s.", 
							player.getName(), permissionRewardCrafting, expPerItem * newItemsCount, 
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
		return rewards.containsKey(key) && !rewards.get(key).equals(Range.Default);
	}
	
	private boolean isStackSumLegal(ItemStack a, ItemStack b) {
		// See if we can create a new item stack with the combined elements of a and b
		if (a == null || b == null)
			return true; // Treat null as an empty stack
		else
			return a.getAmount() + b.getAmount() <= a.getType().getMaxStackSize();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		
		Player player = event.getEntity();
		
		if (player != null) {
			// Permission check
	        if(player.hasPermission(permissionKeepExp)){
				debugger.printDebug(this, "Prevented experience loss for %s.", player.getName());
				
	            event.setDroppedExp(0);
	            event.setKeepLevel(true);
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
