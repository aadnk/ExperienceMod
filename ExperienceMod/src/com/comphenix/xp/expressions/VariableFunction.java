package com.comphenix.xp.expressions;

import java.util.Collection;
import java.util.Random;

import javax.annotation.Nullable;
import com.comphenix.xp.SampleRange;
import com.comphenix.xp.parser.ParsingException;

/**
 * Represents a function that accepts a variable number of named parameters.
 * 
 * @author Kristian
 */
public abstract class VariableFunction {
	
	/**
	 * Constructs a variable function that ignores any parameters and simply uses the given range to compute a number.
	 * @param range - range to use for computing a number.
	 * @return Function that computes the value from a range.
	 */
	public static VariableFunction fromRange(SampleRange range) {
		final SampleRange rangeCopy = range;
		
		// Construct the function
		return new VariableFunction() {
			public double apply(Random rnd, @Nullable Collection<NamedParameter> arg0) {
				return rangeCopy.sampleInt(rnd);
			}
		};
	}
	
	/**
	 * Constructs a variable function from a general mathematical expression.
	 * <p>
	 * The mathematical expression may contain functions, such as most of those found in 
	 * java.lang.Math. In addition, the following custom functions are supported:
	 * <ul>
	 *   <li>dunif(a, b)</li>
	 *   <li>iunif(a, b)</li>
	 *   <li>lerp(a, b, x)</li>
	 *   <li>norm(a, b, x)</li>
	 * </ul>
	 * 
	 * @param expression - mathematical expression.
	 * @param paramNames - variable names.
	 * @return Variable function that takes named parameters with the above names.
	 * @throws ParsingException - If a general parsing problem occures.
	 */
	public static VariableFunction fromExpression(String expression, String[] paramNames) throws ParsingException {
		return new MathExpression(expression, paramNames);
	}
	
	/**
	 * Calculates a double using the random number generator and the given named parameters.
	 * @param rnd - random number generator.
	 * @param params - named parameters.
	 * @return The calculated value.
	 * @throws Exception A runtime error occurred.
	 */
	public abstract double apply(Random rnd, Collection<NamedParameter> params) throws Exception;
}
