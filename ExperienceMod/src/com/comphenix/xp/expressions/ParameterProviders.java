package com.comphenix.xp.expressions;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.xp.Action;
import com.comphenix.xp.extra.ServiceProvider;

public class ParameterProviders {

	private ServiceProvider<ParameterService<Player>> playerParameters;
	private ServiceProvider<ParameterService<Entity>> entityParameters;
	private ServiceProvider<ParameterService<Block>> blockParameters;
	
	public ParameterProviders() {
		playerParameters = new ServiceProvider<ParameterService<Player>>("");
		entityParameters = new ServiceProvider<ParameterService<Entity>>("");
		blockParameters = new ServiceProvider<ParameterService<Block>>("");
	}
	
	/**
	 * Retrieves every registered parameter for a player.
	 * @param action - the triggering action.
	 * @param player - player that is the target if this action (like being killed).
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameter(Action action, Player player) {
		return getParameter(playerParameters, action, player);
	}
	
	/**
	 * Retrieves every registered parameter for an entity.
	 * @param action - the triggering action.
	 * @param entity - entity that is the target if this action (like being killed).
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameter(Action action, Entity entity) {
		return getParameter(entityParameters, action, entity);
	}
	
	/**
	 * Retrieves every registered parameter for a block.
	 * @param action - the triggering action.
	 * @param player - block that is the target if this action (like being destroyed).
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameter(Action action, Block block) {
		return getParameter(blockParameters, action, block);
	}

	// Generics didn't fail us this time. Yay!
	private <TResult> Collection<NamedParameter> getParameter(
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
	
	/**
	 * Sets whether or not a service is enabled.
	 * @param name - name of the service.
	 * @param value - TRUE to enable the service, FALSE otherwise.
	 */
	public void setEnabled(String name, Boolean value) {
		playerParameters.setEnabled(name, value);
		entityParameters.setEnabled(name, value);
		blockParameters.setEnabled(name, value);
	}
	
	/**
	 * Enable all services.
	 */
	public void enableAll() {
		playerParameters.enableAll();
		entityParameters.enableAll();
		blockParameters.enableAll();
	}
}
