package com.comphenix.xp.parser;

/**
 * Represents a generic parser. It converts objects of type TInput to objects of TOutput.
 * 
 * @author Kristian
 * @param <TInput> input type.
 * @param <TOutput> output type.
 */
public abstract class Parser<TInput, TOutput> {
	/**
	 * Transforms the given text into an object. 
	 * @param input Input to parse.
	 * @return Result of the parsing.
	 * @throws ParsingException The item cannot be parsed.
	 */
	public abstract TOutput parse(TInput input) throws ParsingException;
}
