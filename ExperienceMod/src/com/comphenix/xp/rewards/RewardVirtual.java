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

package com.comphenix.xp.rewards;

import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Server;

/**
 * Rewards players with experience directly by simply adding the experience to their experience bar.
 * 
 * @author Kristian
 */
public class RewardVirtual implements RewardService {

	private double searchRadius = 20;
	
	@Override
	public void reward(Player player, int amount) {
		if (player == null)
			throw new NullArgumentException("player");

		reward(player, null, amount);
	}

	@Override
	public boolean canReward(Player player, int amount) {

		if (player == null)
			throw new NullArgumentException("player");

		ExperienceManager manager = new ExperienceManager(player);
		
		// See if we'd end up with negative experience
		if (amount < 0) {
			return manager.hasExp(-amount);
		} else {
			return true;
		}
	}
	
	// Note: We ignore the location.
	@Override
	public void reward(Player player, Location point, int amount) {
		if (player == null)
			throw new NullArgumentException("player");

		ExperienceManager manager = new ExperienceManager(player);
		
		// Rely on the brilliance of others
		if (amount != 0) {
			manager.changeExp(amount);
		}
	}

	@Override
	public void reward(World world, Location point, int amount) {
		if (world == null)
			throw new NullArgumentException("world");
		if (point == null)
			throw new NullArgumentException("point");
		
		List<Player> closest = Server.getNearbyPlayers(world, point, searchRadius);
		
		// Give experience directly
		if (closest.size() == 1)
			reward(closest.get(0), null, amount);
		else
			// Spawn experience
			Server.spawnExperience(world, point, amount);
	}

	public double getSearchRadius() {
		return searchRadius;
	}

	public void setSearchRadius(double searchRadius) {
		this.searchRadius = searchRadius;
	}
	
	@Override
	public RewardTypes getRewardType() {
		return RewardTypes.VIRTUAL;
	}

	@Override
	public String getServiceName() {
		return getRewardType().name();
	}

	@Override
	public RewardService clone(Configuration config) {
		RewardVirtual copy = new RewardVirtual();
		
		copy.setSearchRadius(searchRadius);
		return copy;
	}
}
