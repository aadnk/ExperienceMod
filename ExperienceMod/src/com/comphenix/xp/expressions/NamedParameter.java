package com.comphenix.xp.expressions;

import java.util.concurrent.Callable;

/**
 * Represents a named parameter in a function.
 * 
 * @author Kristian
 */
public abstract class NamedParameter implements Callable<Double> {

	/**
	 * Name of the current parameter.
	 */
	protected final String name;
	
	public NamedParameter(String name) {
		this.name = name;
	}

	/**
	 * Retrieves the name of this parameter.
	 * @return Name of the current parameter.
	 */
	public String getName() {
		return name;
	}
}
