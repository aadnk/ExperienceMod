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

import java.util.HashMap;
import java.util.Random;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.comphenix.xp.Action;
import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.Presets;
import com.comphenix.xp.Range;
import com.comphenix.xp.extra.Permissions;
import com.comphenix.xp.lookup.MobQuery;
import com.comphenix.xp.messages.ChannelProvider;
import com.comphenix.xp.rewards.RewardProvider;

public class ExperienceMobListener extends AbstractExperienceListener {

	private Debugger debugger;
	
	// To determine spawn reason
	private HashMap<Integer, SpawnReason> spawnReasonLookup = new HashMap<Integer, SpawnReason>();

	// Random source
	private Random random = new Random();
	
	// Error report creator
	private ErrorReporting report = ErrorReporting.DEFAULT;
	
	public ExperienceMobListener(Debugger debugger, Presets presets) {
		this.debugger = debugger;
		setPresets(presets);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) 
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		
		try {
			if (event.getSpawnReason() != null) {
				spawnReasonLookup.put(event.getEntity().getEntityId(), 
									  event.getSpawnReason());
			}
		
		// Every entry method must have a generic catcher
		} catch (Exception e) {
			report.reportError(debugger, this, e, event);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDeathEvent(EntityDeathEvent event) {
		
		LivingEntity entity = event.getEntity();
		Player killer = entity.getKiller();
		
		try {
			// Only drop experience from mobs
			if (entity != null && isMob(entity)) {
				handleEntityDeath(event, entity, killer);
			}
		
		// Every entry method must have a generic catcher
		} catch (Exception e) {
			report.reportError(debugger, this, e, event);
		}
	}
	
	private void handleEntityDeath(EntityDeathEvent event, LivingEntity entity, Player killer) {
		
		boolean hasKiller = (killer != null);
		Configuration config = null;
		
		Integer id = entity.getEntityId();
		MobQuery query = MobQuery.fromExact(entity, spawnReasonLookup.get(id), hasKiller);

		if (hasKiller)
			config = getConfiguration(killer);
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
			
			RewardProvider rewards = config.getRewardProvider();
			ChannelProvider channels = config.getChannelProvider();
			
			// Spawn the experience ourself
			event.setDroppedExp(0);
			
			// Make sure the action is legal
			if (hasKiller && !action.canRewardPlayer(rewards, killer, 1)) {
				if (debugger != null)
					debugger.printDebug(this, "Entity %d kill cancelled: Player %s hasn't got enough resources.",
							id, killer.getName());
				
				// Events will not be directly cancelled for untouchables
				if (!Permissions.hasUntouchable(killer)) {
					// To cancel this event, spawn a new mob at the exact same location.
					LivingEntity spawned = entity.getWorld().spawnCreature(entity.getLocation(), entity.getType());
					spawned.addPotionEffects(entity.getActivePotionEffects());
					
					// Prevent drops
					event.getDrops().clear();
				}
				return;
			}
			
			Integer xp = action.rewardAnyone(rewards, random, entity.getWorld(), entity.getLocation());
			config.getMessageQueue().enqueue(null, action, channels.getFormatter(null, xp));
			
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
	
	private boolean isMob(LivingEntity entity) {
		
		EntityType type = entity.getType();
		
		// Exclude players
		return type != null &&
			   type != EntityType.PLAYER;
	}
}
