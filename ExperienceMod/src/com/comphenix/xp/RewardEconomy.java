package com.comphenix.xp;

import net.milkbowl.vault.economy.Economy;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RewardEconomy implements Rewardable {

	private Economy economy;
	
	public RewardEconomy(Economy economy) {
		if (economy == null)
			throw new IllegalArgumentException("Vault (Economy) was not found.");
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
		
		if (amount < 0) {
			
			// See how much we can withdraw
			int removeable = (int) Math.min(economy.getBalance(name), amount);
			
			if (removeable > 0)
				economy.withdrawPlayer(name, removeable);
			
			// ToDo: Add logging.
			
		} else {
			economy.depositPlayer(name, amount);
		}
	}
}
