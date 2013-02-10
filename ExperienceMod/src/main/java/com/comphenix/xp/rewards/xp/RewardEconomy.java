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

package com.comphenix.xp.rewards.xp;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;
import com.comphenix.xp.rewards.ItemRewardListener;
import com.comphenix.xp.rewards.ResourceHolder;
import com.comphenix.xp.rewards.ResourcesParser;
import com.comphenix.xp.rewards.RewardService;
import com.comphenix.xp.rewards.RewardTypes;

/**
 * Rewards players with currency.
 * 
 * @author Kristian
 */
public class RewardEconomy implements RewardService {

	private Economy economy;
	private Debugger debugger;
	private ItemRewardListener listener;
	
	// Default values
	private ItemStack defaultItem = new ItemStack(Material.SLIME_BALL);
	private int defaultWorth = 1;
	
	// Drop items instead of giving currency directly
	private ItemStack economyItem;
	private Integer economyWorth;
	
	// A currency parser
	private ResourcesParser parser = new CurrencyParser(null);
	
	public RewardEconomy(Economy economy, Debugger debugger, ItemRewardListener listener) {
		if (economy == null)
			throw new IllegalArgumentException("Vault (Economy) was not found.");
		if (listener == null)
			throw new NullArgumentException("listener");
		
		this.debugger = debugger;
		this.listener = listener;
		this.economy = economy;
	}

	// Yes, this is a bit of a hack. But it's only a constant overhead.
	@Override
	public boolean canReward(Player player, ResourceHolder resource) {
		if (player == null)
			throw new NullArgumentException("player");
		if (!(resource instanceof CurrencyHolder))
			throw new IllegalArgumentException("Can only award currency.");
		
		// Dry run
		if (!economyReward(player, resource.getAmount(), null)) {
			// Oh, no. It didn't work. 
			return false;
		}
		
		// We'll revert the changes
		if (!economyReward(player, -resource.getAmount(), null)) {
			if (debugger != null)
				debugger.printDebug(this, "Unable to revert economy reward/penalty.");
			
			// PANIC
			return false;
		}
		
		// Yes, rewarding the player worked.
		return true;
	}
	
	@Override
	public void reward(Player player, ResourceHolder resource) {
		if (!(resource instanceof CurrencyHolder))
			throw new IllegalArgumentException("Can only award currency.");
		
		economyReward(player, resource.getAmount(), debugger);
	}
	
	// Internal method
	public boolean economyReward(Player player, int amount, Debugger debugger) {
		if (player == null)
			throw new NullArgumentException("player");
		
		String name = player.getName();
				
		if (amount < 0) {
		
			// Make sure amount is positive
			amount = Math.abs(amount);
			
			// Just attempt to withdraw
			EconomyResponse response = economy.withdrawPlayer(name, amount);
			
			// Error?
			if (response != null && !response.transactionSuccess()) {
				
				// Could this be because the player is broke?
				if (isLoan(name, amount)) {
					if (debugger != null)
						debugger.printDebug(this, "Could not withdraw %d: Player %s is broke", 
								amount, name);
				} else {
					// General error
					if (debugger != null)
						debugger.printDebug(this, "Coult not withdraw %d from player %s: %s", 
							    amount, name, response.errorMessage);
				}
				
				// Failure
				return false;
			}
			
		} else {
			
			// Deposit money (shouldn't really fail)
			EconomyResponse response = economy.depositPlayer(name, amount);
			
			// But we'll do this just in case
			if (response != null && !response.transactionSuccess() && debugger != null) {
				debugger.printDebug(this, "Could not deposit %d to player %s: %s", 
									amount, name, response.errorMessage);
				return false;
			}
		}
		
		// Everything went well
		return true;
	}
	
	// Location is ignored.
	@Override
	public void reward(Player player, Location point, ResourceHolder resource) {

		// See if we have to reward the player directly
		if (economyItem == null || economyWorth == null || economyWorth < 1) {
			if (debugger != null)
				debugger.printDebug(this, "Cannot find economy settings. Reverting to direct currency.");
			
			reward(player, resource);
		} else {
			reward(player.getWorld(), point, resource);
		}
	}
	
	@Override
	public void reward(World world, Location point, ResourceHolder resource) {
		if (world == null)
			throw new NullArgumentException("world");
		if (point == null)
			throw new NullArgumentException("point");
		if (!(resource instanceof CurrencyHolder))
			throw new IllegalArgumentException("Can only award currency.");
		
		ItemStack stack = economyItem != null ? economyItem : defaultItem;
		Integer worth = economyWorth != null ? economyWorth : defaultWorth;
		
		// Support negative rewards
		int amount = Math.abs(resource.getAmount());
		int sign = resource.getAmount() < 0 ? -1 : 1;
		
		// Make sure it's valid too
		if (worth < 1) {
			worth = defaultWorth;
		}
		
		stack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, -1);
			
		// Create the proper amount of items
		for (; amount > 0; amount -= worth) {
			int thisAmount = sign * Math.min(amount, worth);
			ItemStack thisStack = stack.clone();
			ItemMeta meta = thisStack.getItemMeta();
			meta.setDisplayName(Integer.toString(thisAmount));
			thisStack.setItemMeta(meta);
			Item spawned = world.dropItemNaturally(point, thisStack);
			listener.pinReward(spawned, thisAmount);
		}
	}
	
	@Override
	public ResourcesParser getResourcesParser(String[] namedParameters) {
		return parser.withParameters(namedParameters);
	}
	
	/**
	 * Determines whether or not withdrawing given amount requires a loan.
	 * @param name - name of the player to withdraw from.
	 * @param withdraw - the amount of currency to withdraw.
	 * @return TRUE if this requires a loan (negative bank balance), FALSE otherwise.
	 */
	private boolean isLoan(String name, int withdraw) {
		return economy.getBalance(name) < withdraw;
	}

	@Override
	public RewardTypes getRewardType() {
		return RewardTypes.ECONOMY;
	}

	@Override
	public String getServiceName() {
		return getRewardType().name();
	}
	
	public ItemStack getEconomyItem() {
		return economyItem;
	}

	public void setEconomyItem(ItemStack economyItem) {
		this.economyItem = economyItem;
	}

	public Integer getEconomyWorth() {
		return economyWorth;
	}

	public void setEconomyWorth(Integer economyWorth) {
		this.economyWorth = economyWorth;
	}

	/**
	 * Determine the amount of currency a given player has access too.
	 * @param player - the player to check.
	 * @return The amount of currency belonging to this player.
	 */
	public double getBalance(Player player) {
		return economy.getBalance(player.getName());
	}
	
	@Override
	public RewardService clone(Configuration config) {
		RewardEconomy copy = new RewardEconomy(economy, debugger, listener);
		
		copy.setEconomyItem(config.getEconomyDropItem());
		copy.setEconomyWorth(config.getEconomyItemWorth());
		copy.parser = parser;
		return copy;
	}
}
