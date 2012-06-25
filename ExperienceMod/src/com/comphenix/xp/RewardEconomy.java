package com.comphenix.xp;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class RewardEconomy implements Rewardable {

	private Economy economy;
	private Debugger debugger;
	
	public RewardEconomy(Economy economy, Debugger debugger) {
		if (economy == null)
			throw new IllegalArgumentException("Vault (Economy) was not found.");
		if (debugger == null)
			throw new NullArgumentException("debugger");
		
		this.economy = economy;
	}

	@Override
	public void reward(Player player, int amount) {
		reward(player, null, amount);
	}

	// Location is ignored.
	@Override
	public void reward(Player player, Location point, int amount) {

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

	@Override
	public RewardTypes getType() {
		return RewardTypes.ECONOMY;
	}

	@Override
	public String getRewardName() {
		return getType().name();
	}
}
