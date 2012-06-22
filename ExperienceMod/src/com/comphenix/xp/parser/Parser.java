package com.comphenix.xp.parser;

/**
 * Represents an object that transforms strings of text into objects.
 * 
 * @author Kristian
 * @param <TResult> Type of the resulting object.
 */
public interface Parser<TResult> {
	/**
	 * Transforms the given text into an object. 
	 * @param text Text to parse.
	 * @return Result of the parsing.
	 * @throws ParsingException The text cannot be transformed into an object.
	 */
	TResult Parse(String text) throws ParsingException;
}
