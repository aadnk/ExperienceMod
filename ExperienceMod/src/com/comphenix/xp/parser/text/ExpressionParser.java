package com.comphenix.xp.parser.text;

import com.comphenix.xp.expressions.MathExpression;
import com.comphenix.xp.expressions.VariableFunction;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.TextParser;

public class ExpressionParser extends TextParser<VariableFunction> {

	protected String[] parameterNames;

	public ExpressionParser(String[] parameterNames) {
		this.parameterNames = parameterNames;
	}

	@Override
	public VariableFunction parse(String text) throws ParsingException {

		MathExpression expression = new MathExpression(text, parameterNames);
		
		// Any parsing problems will be thrown by the constructor
		return expression;
	}
	
	public String[] getParameterNames() {
		return parameterNames;
	}
}
