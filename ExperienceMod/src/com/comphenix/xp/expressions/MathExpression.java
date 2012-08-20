package com.comphenix.xp.expressions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.comphenix.xp.parser.ParsingException;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.InvalidCustomFunctionException;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class MathExpression extends VariableFunction {

	// Default value
	private static final double VARIABLE_NOT_FOUND = 0;
	
	// Extra custom functions
	protected RandomFunctions dunif;
	protected RandomFunctions iunif;
	
	// Variables we will calculate
	protected Set<String> variablesPresent;
	
	// The function to use
	protected Calculable function;
	
	// The multiplication factor
	protected double multiplier;
	
	public MathExpression(String expression, String[] parameters) throws ParsingException {
		
		// Parse the expression
		try {
			dunif = new RandomFunctions(RandomFunctions.SubFunction.DUNIF);
			iunif = new RandomFunctions(RandomFunctions.SubFunction.IUNIF);
			
			// Parse expression
			function = new ExpressionBuilder(expression).
					withCustomFunction(dunif).
					withCustomFunction(iunif).
					withVariableNames(parameters).
					build();

			// Default value
			multiplier = 1;
			variablesPresent = new HashSet<String>();
			
			// Add parameters that are present
			for (String param : parameters) {
				if (function.containsVariable(param))
					variablesPresent.add(param);
			}
			
		} catch (UnknownFunctionException e) {
			throw new ParsingException(e.getMessage(), e);
		} catch (UnparsableExpressionException e) {
			throw new ParsingException(e.getMessage(), e);
		} catch (InvalidCustomFunctionException e) {
			throw new ParsingException(e.getMessage(), e);
		}
	}
	
	// Make a shallow copy with a different multiplier
	private MathExpression(MathExpression copy, double multiplier) {
		this.dunif = copy.dunif;
		this.iunif = copy.iunif;
		this.function = copy.function;
		this.variablesPresent = copy.variablesPresent;
		this.multiplier = multiplier;
	}
	
	@Override
	public double apply(Random random, Collection<NamedParameter> params) throws Exception {

		// Don't forget to use the random number generator we got
		dunif.setRandom(random);
		iunif.setRandom(random);

		// Apply all the parameters that exists
		if (params != null) {
			for (NamedParameter param : params) {
				if (variablesPresent.contains(param.getName())) {
					function.setVariable(param.getName(), param.call());
				} else {
					function.setVariable(param.getName(), VARIABLE_NOT_FOUND);
				}
			}
		}
		
		// Right, do our thing
		return function.calculate();
	}

	@Override
	public VariableFunction withMultiplier(double newMultiplier) {
		return new MathExpression(this, newMultiplier);
	}

	@Override
	public double getMultiplier() {
		return multiplier;
	}
	
	/**
	 * Retrieves the expression that was used to create this instance, or NULL.
	 * @return Expression, or NULl if not found.
	 */
	public String getExpression() {
		return function != null ? function.getExpression() : null;
	}
	
	@Override
	public String toString() {
		if (multiplier != 1)
			return String.format("%s * %s", multiplier, function.getExpression());
		else
			return function.getExpression();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(getExpression()).
	            append(multiplier).
	            toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        MathExpression other = (MathExpression) obj;
        return new EqualsBuilder().
            append(getExpression(), other.getExpression()).
            append(multiplier, other.multiplier).
            isEquals();
	}
}
