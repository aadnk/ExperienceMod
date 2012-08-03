package com.comphenix.xp.rewards.items;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.comphenix.xp.Configuration;
import com.comphenix.xp.Server;
import com.comphenix.xp.parser.text.ItemNameParser;
import com.comphenix.xp.rewards.ResourceHolder;
import com.comphenix.xp.rewards.ResourcesParser;
import com.comphenix.xp.rewards.RewardService;
import com.comphenix.xp.rewards.RewardTypes;

public class RewardDrops implements RewardService {
	
	private ItemsParser itemsParser;
	private ItemNameParser itemNameParser;
	
	public RewardDrops(ItemNameParser parser) {
		setItemNameParser(parser);
	}
	
	public RewardDrops() {
		// No parsers are loaded
	}

	@Override
	public RewardTypes getRewardType() {
		return RewardTypes.DROPS;
	}

	@Override
	public String getServiceName() {
		return getRewardType().name();
	}

	@Override
	public boolean canReward(Player player, ResourceHolder resource) {

		if (resource instanceof ItemsHolder)
			return true;
		else
			throw new IllegalArgumentException("Can only reward in items.");
	}

	@Override
	public void reward(Player player, ResourceHolder resource) {
		if (player == null)
			throw new NullArgumentException("player");
		if (resource instanceof ItemsHolder)
			throw new IllegalArgumentException("Can only reward in items.");
		
		// Delegate to more specific method
		reward(player, player.getLocation(), resource);
	}

	@Override
	public void reward(Player player, Location point, ResourceHolder resource) {
		if (player == null)
			throw new NullArgumentException("player");
		
		// Delegate again
		reward(player.getWorld(), point, resource);
	}

	@Override
	public void reward(World world, Location point, ResourceHolder resource) {
		if (world == null)
			throw new NullArgumentException("world");
		if (point == null)
			throw new NullArgumentException("point");
		if (resource == null)
			throw new NullArgumentException("resource");
		
		ItemsHolder holder = (ItemsHolder) resource;
		Server.spawnItem(world, point, holder.getRewards());
	}

	@Override
	public ResourcesParser getResourcesParser() {
		return itemsParser;
	}

	public ItemNameParser getItemNameParser() {
		return itemNameParser;
	}
	
	public void setItemNameParser(ItemNameParser nameParser) {
		this.itemsParser = new ItemsParser(nameParser);
		this.itemNameParser = nameParser;
	}

	@Override
	public RewardService clone(Configuration config) {
		return new RewardDrops(itemNameParser);
	}
}
