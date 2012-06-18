package com.comphenix.xp;

public interface Debugger {
	
	/**
	 * Whether or not a debug mode is enabled.
	 * @return TRUE if debug mode is enabled, FALSE otherwise.
	 */
	public boolean isDebugEnabled();
	
	/**
	 * Prints or logs a debug message.
	 * @param sender The object that sent this message.
	 * @param message The format of the debug message to send.
	 * @param params The parameters to include in the debug message.
	 */
	public void printDebug(Object sender, String message, Object... params);
}
