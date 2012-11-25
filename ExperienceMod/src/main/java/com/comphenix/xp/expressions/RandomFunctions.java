package com.comphenix.xp.expressions;

import java.util.Random;

import com.comphenix.xp.SampleRange;
import com.comphenix.xp.rewards.items.RandomSampling;

import de.congrace.exp4j.CustomFunction;
import de.congrace.exp4j.InvalidCustomFunctionException;

public class RandomFunctions extends CustomFunction {

	/**
	 * Every random function in this library.
	 * 
	 * @author Kristian
	 */
	public enum SubFunction {
		IUNIF(2),
		DUNIF(2);
		
		final int argCount;
		
		private SubFunction(int argCount) {
			this.argCount = argCount;
		}
		
		/**
		 * Retrieves the call name of this function.
		 * @return Function name.
		 */
		public String getFunctionName() {
			return this.name().toLowerCase();
		}

		/**
		 * Retrieves the number of arguments this function uses.
		 * @return Number of arguments.
		 */
		public int getArgCount() {
			return argCount;
		}
	}
	
	private Random random;

	// Our standard function type
	final private SubFunction function;
	
	/**
	 * Initialize a random number function.
	 * @param function - the function to initialize.
	 * @throws InvalidCustomFunctionException
	 */
	public RandomFunctions(SubFunction function) throws InvalidCustomFunctionException {
		super(function.getFunctionName());
		super.argc = function.getArgCount();
		this.function = function;
	}
	
	@Override
	public double applyFunction(double... args) {

		boolean etheral = false;
		double result = Double.NaN;
		
		if (random == null) {
			// Damn. Well, we better get a RNG quick
			random = RandomSampling.getThreadRandom();
			etheral = true;
		}
		
		switch (function) {
		case IUNIF:
		case DUNIF:
			
			SampleRange range = new SampleRange(args[0], args[1]);
			
			// Handle the double and the int versions
			if (function == SubFunction.DUNIF)
				result = range.sampleDouble(random);
			else
				result = range.sampleInt(random);
			break;
			
		default:
			throw new IllegalStateException("Illegal random function detected.");
		}
		
		// Revert back to normal
		if (etheral) {
			random = null;
		}
		return result;
	}

	/**
	 * Returns the random number generator all of these functions will use.
	 * @return Random number generator to use.
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * Sets the random number generator all of these functions will use.
	 * @param random - the new random number generator to use.
	 */
	public void setRandom(Random random) {
		this.random = random;
	}

	/**
	 * Retrieves the function type.
	 * @return The current function type.
	 */
	public SubFunction getFunction() {
		return function;
	}
}
