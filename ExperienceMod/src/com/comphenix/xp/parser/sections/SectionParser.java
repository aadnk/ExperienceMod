package com.comphenix.xp.parser.sections;

import com.comphenix.xp.Debugger;
import com.comphenix.xp.parser.ConfigurationParser;

/**
 * Represents a section parser. It is similar to a configuration parser, but has different
 * semantics when it comes to handling errors.
 * 
 * @author Kristian
 *
 * @param <TOutput> - output type.
 */
public abstract class SectionParser<TOutput> extends ConfigurationParser<TOutput> {

	protected Debugger debugger;
	
	/**
	 * Retrieves whether or not to push exceptions from individual parse lines, 
	 * and print it to the assigned debugger instead of throwing them at once.
	 * @return TRUE to do wait until the end, FALSE to throw a ParsingException immediately.
	 */
	public boolean isCollectExceptions() {
		return debugger != null;
	}

	/**
	 * Retrieves the current debugger.
	 * @return Current debugger.
	 */
	public Debugger getDebugger() {
		return debugger;
	}

	/**
	 * Sets the debugger that will handle parse exceptions along the way. By setting this to a non-null
	 * value, the parse semantics changes - now, a single parsing exception will simply be printed to the
	 * debugger, and the operation will continue.
	 * @param debugger - new debugger to attach.
	 */
	public void setDebugger(Debugger debugger) {
		this.debugger = debugger;
	}
}
