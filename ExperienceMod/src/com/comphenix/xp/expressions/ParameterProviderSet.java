package com.comphenix.xp.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import com.comphenix.xp.Action;
import com.comphenix.xp.extra.ServiceProvider;

public class ParameterProviderSet {

	private ServiceProvider<ParameterService<Player>> playerParameters;
	private ServiceProvider<ParameterService<Entity>> entityParameters;
	private ServiceProvider<ParameterService<Block>> blockParameters;
	private ServiceProvider<ParameterService<Item>> itemParameters;
	
	public ParameterProviderSet() {
		playerParameters = new ServiceProvider<ParameterService<Player>>("");
		entityParameters = new ServiceProvider<ParameterService<Entity>>("");
		blockParameters = new ServiceProvider<ParameterService<Block>>("");
		itemParameters = new ServiceProvider<ParameterService<Item>>("");
	}
	
	/**
	 * Retrieves every registered parameter for a player.
	 * @param action - the triggering action.
	 * @param player - player that is the target if this action (like being killed).
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameters(Action action, Player player) {
		return getParameters(playerParameters, action, player);
	}
	
	/**
	 * Retrieves every registered parameter for an entity.
	 * @param action - the triggering action.
	 * @param entity - entity that is the target if this action (like being killed).
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameters(Action action, Entity entity) {
		return getParameters(entityParameters, action, entity);
	}
	
	/**
	 * Retrieves every registered parameter for a block.
	 * @param action - the triggering action.
	 * @param block - block that is the target if this action (like being destroyed).
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameters(Action action, Block block) {
		return getParameters(blockParameters, action, block);
	}

	/**
	 * Retrieves every registered parameter for an item.
	 * @param action - the triggering action.
	 * @param item - the item that is the target if this action (like being crafted).
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameters(Action action, Item item) {
		return getParameters(itemParameters, action, item);
	}
	
	// Generics didn't fail us this time. Yay!
	private <TResult> Collection<NamedParameter> getParameters(
			ServiceProvider<ParameterService<TResult>> serviceProvider, Action action, TResult target) {
		
		Collection<NamedParameter> result = new ArrayList<NamedParameter>();
		
		// Retrieve the named parameters in every registered service
		for (ParameterService<TResult> service : serviceProvider.getRegisteredServices()) {
			Collection<NamedParameter> sublist = service.getParameters(action, target);
			
			if (sublist != null && !sublist.isEmpty()) {
				result.addAll(sublist);
			}
		}
		
		return result;
	}
	
	// Another helper function
	private <TResult> Collection<String> getParameterNames(ServiceProvider<ParameterService<TResult>> serviceProvider) {
		
		Collection<String> result = new ArrayList<String>();
		
		// Retrieve the named parameters in every registered service
		for (ParameterService<TResult> service : serviceProvider.getRegisteredServices()) {
			String[] sublist = service.getParameterNames();
			
			if (sublist != null && sublist.length > 0) {
				result.addAll(Arrays.asList(sublist));
			}
		}
		
		return result;
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
	public void registerItem(ParameterService<Item> service) {
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
	
	public ServiceProvider<ParameterService<Player>> getPlayerParameters() {
		return playerParameters;
	}

	public ServiceProvider<ParameterService<Entity>> getEntityParameters() {
		return entityParameters;
	}

	public ServiceProvider<ParameterService<Block>> getBlockParameters() {
		return blockParameters;
	}

	public ServiceProvider<ParameterService<Item>> getItemParameters() {
		return itemParameters;
	}
}
