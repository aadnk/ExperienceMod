package com.comphenix.xp.expressions;

import java.util.Collection;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.xp.Action;

public class ParameterProviderSet {

	private ParameterProvider<Player> playerParameters;
	private ParameterProvider<Entity> entityParameters;
	private ParameterProvider<Block> blockParameters;
	private ParameterProvider<ItemStack> itemParameters;
	
	public ParameterProviderSet() {
		playerParameters = new ParameterProvider<Player>("");
		entityParameters = new ParameterProvider<Entity>("");
		blockParameters = new ParameterProvider<Block>("");
		itemParameters = new ParameterProvider<ItemStack>("");
	}
	
	/**
	 * Retrieves every registered parameter for a player.
	 * @param action - the triggering action.
	 * @param player - player that is the target if this action (like being killed).
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameters(Action action, Player player) {
		return playerParameters.getParameters(action, player);
	}
	
	/**
	 * Retrieves every registered parameter for an entity.
	 * @param action - the triggering action.
	 * @param entity - entity that is the target if this action (like being killed).
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameters(Action action, Entity entity) {
		return entityParameters.getParameters(action, entity);
	}
	
	/**
	 * Retrieves every registered parameter for a block.
	 * @param action - the triggering action.
	 * @param block - block that is the target if this action (like being destroyed).
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameters(Action action, Block block) {
		return blockParameters.getParameters(action, block);
	}

	/**
	 * Retrieves every registered parameter for an item.
	 * @param action - the triggering action.
	 * @param item - the item that is the target if this action (like being crafted).
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameters(Action action, ItemStack item) {
		return itemParameters.getParameters(action, item);
	}
	
	/**
	 * Registers a player parameter service.
	 * @param service - the service to register.
	 */
	public void registerPlayer(ParameterService<Player> service) {
		playerParameters.register(service);
	}

	/**
	 * Registers an entity parameter service.
	 * @param service - the service to register.
	 */
	public void registerEntity(ParameterService<Entity> service) {
		entityParameters.register(service);
	}
	
	/**
	 * Registers a block parameter service.
	 * @param service - the service to register.
	 */
	public void registerBlock(ParameterService<Block> service) {
		blockParameters.register(service);
	}
	
	/**
	 * Registers an item parameter service.
	 * @param service - the service to register.
	 */
	public void registerItem(ParameterService<ItemStack> service) {
		itemParameters.register(service);
	}

	/**
	 * Sets whether or not a service is enabled.
	 * @param name - name of the service.
	 * @param value - TRUE to enable the service, FALSE otherwise.
	 */
	public void setEnabled(String name, Boolean value) {
		playerParameters.setEnabled(name, value);
		entityParameters.setEnabled(name, value);
		blockParameters.setEnabled(name, value);
		itemParameters.setEnabled(name, value);
	}
	
	/**
	 * Enable all services.
	 */
	public void enableAll() {
		playerParameters.enableAll();
		entityParameters.enableAll();
		blockParameters.enableAll();
		itemParameters.enableAll();
	}
	
	/**
	 * Get the player parameter provider.
	 * @return Player parameter provider.
	 */
	public ParameterProvider<Player> getPlayerParameters() {
		return playerParameters;
	}

	/**
	 * Get the entity parameter provider.
	 * @return Entity parameter provider.
	 */
	public ParameterProvider<Entity> getEntityParameters() {
		return entityParameters;
	}

	/**
	 * Get the block parameter provider.
	 * @return Block parameter provider.
	 */
	public ParameterProvider<Block> getBlockParameters() {
		return blockParameters;
	}

	/**
	 * Get the item parameter provider.
	 * @return Item parameter provider.
	 */
	public ParameterProvider<ItemStack> getItemParameters() {
		return itemParameters;
	}
}
