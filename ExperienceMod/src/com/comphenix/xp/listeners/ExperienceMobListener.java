package com.comphenix.xp.listeners;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.comphenix.xp.Action;
import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.Presets;
import com.comphenix.xp.Range;
import com.comphenix.xp.lookup.MobQuery;
import com.comphenix.xp.messages.ChannelProvider;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.rewards.RewardProvider;

public class ExperienceMobListener implements Listener {

	private Debugger debugger;
	private Presets presets;
	
	// To determine spawn reason
	private HashMap<Integer, SpawnReason> spawnReasonLookup = new HashMap<Integer, SpawnReason>();

	// Random source
	private Random random = new Random();
	
	public ExperienceMobListener(Debugger debugger, Presets presets) {
		this.debugger = debugger;
		setPresets(presets);
	}
	
	public Presets getPresets() {
		return presets;
	}

	public void setPresets(Presets presets) {
		this.presets = presets;
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
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) 
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != null) {
			spawnReasonLookup.put(event.getEntity().getEntityId(), 
								  event.getSpawnReason());
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
			MobQuery query = MobQuery.fromExact(entity, spawnReasonLookup.get(id), hasKiller);

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
				
				action.setDebugger(debugger);
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
}
