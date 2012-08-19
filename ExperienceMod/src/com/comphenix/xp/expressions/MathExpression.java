package com.comphenix.xp.expressions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.comphenix.xp.parser.ParsingException;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.InvalidCustomFunctionException;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class MathExpression extends VariableFunction {

	// Extra custom functions
	private RandomFunctions dunif;
	private RandomFunctions iunif;
	
	// Variables we will calculate
	private Set<String> variablesPresent;
	
	// The function to use
	private Calculable function;
	
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

			variablesPresent = new HashSet<String>();
			
			// Add parameters that are present
			for (String param : parameters) {
				if (function.containsVariable(param))
					variablesPresent.add(param);
			}
			
		} catch (UnknownFunctionException e) {
			throw new ParsingException("Unknown function encountered in math expression..", e);
		} catch (UnparsableExpressionException e) {
			throw new ParsingException("Unable to parse math expression.", e);
		} catch (InvalidCustomFunctionException e) {
			throw new ParsingException("Invalid function detected.", e);
		}
	}
	
	@Override
	public double apply(Random random, Collection<NamedParameter> params) throws Exception {

		// Don't forget to use the random number generator we got
		dunif.setRandom(random);
		iunif.setRandom(random);
		
		// Apply all the parameters that exists
		for (NamedParameter param : params) {
			if (variablesPresent.contains(param.getName())) {
				function.setVariable(param.getName(), param.call());
			}
		}
		
		// Right, do our thing
		return function.calculate();
	}
}
