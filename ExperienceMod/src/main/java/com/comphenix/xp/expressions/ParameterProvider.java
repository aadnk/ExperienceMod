package com.comphenix.xp.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.comphenix.xp.Action;
import com.comphenix.xp.extra.ServiceProvider;

public class ParameterProvider<TTarget> extends ServiceProvider<ParameterService<TTarget>> {

	public ParameterProvider(String defaultName) {
		super(defaultName);
	}

	/**
	 * Retrieves every registered parameter.
	 * @param action - the triggering action.
	 * @param target - whatever is the target of this action.
	 * @return Every registered named parameter.
	 */
	public Collection<NamedParameter> getParameters(Action action, TTarget target) {
		
		Collection<NamedParameter> result = new ArrayList<NamedParameter>();
		
		// Retrieve the named parameters in every registered service
		for (ParameterService<TTarget> service : getRegisteredServices()) {
			Collection<NamedParameter> sublist = service.getParameters(action, target);
			
			if (sublist != null && !sublist.isEmpty()) {
				result.addAll(sublist);
			}
		}
		
		return result;
	}
	
	/**
	 * Retrieves the name of every registered parameter.
	 * @return The name of every registered parameter.
	 */
	public String[] getParameterNames() {
		
		Collection<String> result = new ArrayList<String>();
		
		// Retrieve the named parameters in every registered service
		for (ParameterService<TTarget> service : getRegisteredServices()) {
			String[] sublist = service.getParameterNames();
			
			if (sublist != null && sublist.length > 0) {
				result.addAll(Arrays.asList(sublist));
			}
		}
		
		return result.toArray(new String[result.size()]);
	}
}
