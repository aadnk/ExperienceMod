package com.comphenix.xp.rewards;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Debugger;

/**
 * Rewards players with currency.
 * 
 * @author Kristian
 */
public class RewardEconomy implements Rewardable {

	private Economy economy;
	private Debugger debugger;
	private ItemRewardListener listener;
	
	// Default values
	private ItemStack defaultItem = new ItemStack(Material.SLIME_BALL);
	private int defaultWorth = 1;
	
	// Drop items instead of giving currency directly
	private ItemStack economyItem;
	private Integer economyWorth;
	
	public RewardEconomy(Economy economy, Debugger debugger, ItemRewardListener listener) {
		if (economy == null)
			throw new IllegalArgumentException("Vault (Economy) was not found.");
		if (debugger == null)
			throw new NullArgumentException("debugger");
		if (listener == null)
			throw new NullArgumentException("listener");
		
		this.listener = listener;
		this.economy = economy;
	}
	
	@Override
	public void reward(Player player, int amount) {
		if (player == null)
			throw new NullArgumentException("player");
		
		String name = player.getName();
		EconomyResponse response = null;
		
		if (amount < 0) {
			
			// See how much we can withdraw
			int removeable = (int) Math.min(economy.getBalance(name), amount);
			
			if (removeable > 0)
				response = economy.withdrawPlayer(name, removeable);
			else
				debugger.printDebug(this, "Could not withdraw %d: Player %s is broke", amount, name);
			
			// Other error
			if (response != null && !response.transactionSuccess())
				debugger.printDebug(this, "Coult not withdraw %d from player %s: %s", 
								    amount, name, response.errorMessage);

		} else {
			
			// Deposit money (shouldn't really fail)
			response = economy.depositPlayer(name, amount);
			
			if (response != null && !response.transactionSuccess())
				debugger.printDebug(this, "Could not deposit %d to player %s: %s", 
									amount, name, response.errorMessage);
		}
	}

	// Location is ignored.
	@Override
	public void reward(Player player, Location point, int amount) {

		// See if we have to reward the player directly
		if (economyItem == null || economyWorth == null || economyWorth < 1) {
			debugger.printDebug(this, "Cannot find economy settings.");
			reward(player, amount);
		} else {
			reward(player.getWorld(), point, amount);
		}
	}
	
	@Override
	public void reward(World world, Location point, int amount) {
		if (world == null)
			throw new NullArgumentException("world");
		if (point == null)
			throw new NullArgumentException("point");
		
		ItemStack stack = economyItem != null ? economyItem : defaultItem;
		Integer worth = economyWorth != null ? economyWorth : defaultWorth;
		
		// Make sure it's valid too
		if (worth < 1)
			worth = defaultWorth;
		
		// Create the proper amount of items
		for (; amount > 0; amount -= worth) {
			Item spawned = world.dropItemNaturally(point, stack);
			listener.pinReward(spawned, Math.min(amount, worth));
		}
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

	@Override
	public Rewardable clone(Configuration config) {
		RewardEconomy copy = new RewardEconomy(economy, debugger, listener);
		
		copy.setEconomyItem(config.getEconomyDropItem());
		copy.setEconomyWorth(config.getEconomyItemWorth());
		return copy;
	}
}
