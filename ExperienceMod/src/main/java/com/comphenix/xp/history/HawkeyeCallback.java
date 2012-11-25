package com.comphenix.xp.history;

import com.comphenix.xp.Debugger;

import uk.co.oliwali.HawkEye.callbacks.BaseCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;

public class HawkeyeCallback extends BaseCallback {
	
	private Debugger debugger;
	private HawkeyeService caller;
	
	public HawkeyeCallback(Debugger debugger, HawkeyeService caller) {
		this.debugger = debugger;
		this.caller = caller;
	}
	
	@Override
	public void execute() {

		// This should be the most recent change
		if (results == null || results.size() == 0) {
			caller.setSearchResult(null);
		} else {
			caller.setSearchResult(results.get(0));
		}
		
		synchronized (caller.getLock()) {
			caller.setSearching(false);
			caller.getLock().notifyAll();
		}
	}
	
	@Override
	public void error(SearchError arg0, String arg1) {
		// Damn
		if (debugger != null)
			debugger.printWarning(caller, "Error: %s %s", arg0, arg1);
	}
}
