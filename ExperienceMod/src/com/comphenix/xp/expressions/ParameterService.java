package com.comphenix.xp.expressions;

import java.util.Collection;

import com.comphenix.xp.Action;
import com.comphenix.xp.extra.Service;

/**
 * Represents a parameter service.
 * 
 * @author Kristian
 */
public interface ParameterService<TTarget> extends Service {

	/**
	 * Retrieves the parameter names this service recognizes. These will be verified during
	 * parsing, and must remain constant thereafter.
	 * @return Names of the parameters this service recognizes.
	 */
	public String[] getParameterNames();
	
	/**
	 * Retrieves the parameters with respect to a certain action and target object.
	 * @param action - the action object.
	 * @param targetObject - object that was the target of the action, such as a player, block or entity.
	 * @return A collection of parameters.
	 */
	public Collection<NamedParameter> getParameters(Action action, TTarget targetObject);
}
