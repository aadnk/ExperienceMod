package com.comphenix.xp.parser;

public class BooleanParser extends Parser<Boolean> {

	private String parameterName;

	public BooleanParser(String parameterName) {
		this.parameterName = parameterName;
	}
	
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * Parses the given parameter as a boolean. 
	 * @param text Parameter to parse.
	 * @return Boolean value if parsing succeeded, or NULL otherwise.
	 */
	@Override
	Boolean parse(String text) throws ParsingException {
		
		if (text == null)
			return null;
		
		boolean value = !text.startsWith("!"); // Negative prefix
		
		// Use null instead of exceptions
		if (parameterName.startsWith(text, value ? 0 : 1))
			return value;
		else
			return null; 
	}
}
