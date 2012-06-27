package com.comphenix.xp;

public interface Debugger {
	
	/**
	 * Whether or not a debug mode is enabled.
	 * @return TRUE if debug mode is enabled, FALSE otherwise.
	 */
	public boolean isDebugEnabled();
	
	/**
	 * Prints or logs a debug message.
	 * @param sender - the object that sent this message.
	 * @param message - the format of the debug message to send.
	 * @param params - the parameters to include in the debug message.
	 */
	public void printDebug(Object sender, String message, Object... params);
	
	/**
	 * Prints or logs a warning.
	 * @param sender - the object that sent this message.
	 * @param message - the format of the warning message to send.
	 * @param params - the parameters to include in the warning message.
	 */
	public void printWarning(Object sender, String message, Object... params);
}
