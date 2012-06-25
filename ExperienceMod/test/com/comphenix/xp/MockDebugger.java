package com.comphenix.xp;

import static org.junit.Assert.fail;

public class MockDebugger implements Debugger {
	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void printDebug(Object sender, String message, Object... params) {
		
	}

	@Override
	public void printWarning(Object sender, String message, Object... params) {
		fail(String.format(message, params));
	}
}
