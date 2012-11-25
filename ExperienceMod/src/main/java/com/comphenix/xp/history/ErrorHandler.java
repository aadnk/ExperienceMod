package com.comphenix.xp.history;

/**
 * A simple Exception handler.
 * 
 * @author Kristian
 */
public interface ErrorHandler<TError extends Throwable> {

	/**
	 * Invoked when an error occurs.
	 * @param error - error that occured.
	 */
	public void onError(TError error);
}
