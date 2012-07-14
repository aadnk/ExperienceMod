package com.comphenix.xp.listeners;

import java.util.Map;
import java.util.Random;

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
import com.comphenix.xp.lookup.ItemQuery;
import com.comphenix.xp.lookup.ItemTree;
import com.comphenix.xp.messages.ChannelProvider;
import com.comphenix.xp.rewards.RewardProvider;

public class ExperienceBlockListener extends AbstractExperienceListener {
	
	private final String permissionRewardBonus = "experiencemod.rewards.bonus";
	private final String permissionRewardBlock = "experiencemod.rewards.block";
	private final String permissionRewardPlacing = "experiencemod.rewards.placing";
	private final String permissionUntouchable = "experiencemod.untouchable";
	
	private Debugger debugger;

	// Random source
	private Random random = new Random();
	
	public ExperienceBlockListener(Debugger debugger, Presets presets) {
		this.debugger = debugger;
		setPresets(presets);
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
					config.getMessageQueue().enqueue(player, action, channels.getFormatter(player, exp));
					
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
					config.getMessageQueue().enqueue(player, action, channels.getFormatter(player, exp));
					
					if (debugger != null)
						debugger.printDebug(this, "Block destroyed by %s: Spawned %d xp for item %s.", 
							player.getName(), exp, block.getType());
				}
			}
			
			// Done
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
					
					// Make sure the action is legal
					if (!action.canRewardPlayer(rewards, player, 1)) {
						if (debugger != null)
							debugger.printDebug(this, "Block place by %s cancelled: Not enough resources for item %s",
								player.getName(), block.getType());
						
						// Events will not be cancelled for untouchables
						if (!player.hasPermission(permissionUntouchable))
							event.setCancelled(true);
						return;
					}
					
					// Reward and print messages (possibly in the future)
					Integer exp = action.rewardPlayer(rewards, random, player);
					config.getMessageQueue().enqueue(player, action, channels.getFormatter(player, exp));
					
					if (debugger != null)
						debugger.printDebug(this, "Block placed by %s: Spawned %d xp for item %s.", 
							player.getName(), exp, block.getType());
				}
			}
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
}
