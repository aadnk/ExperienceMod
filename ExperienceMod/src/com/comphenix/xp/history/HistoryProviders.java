package com.comphenix.xp.history;

import com.comphenix.xp.extra.ServiceProvider;

/**
 * Registry of history (block change logging) providers.
 * 
 * @author Kristian
 */
public class HistoryProviders extends ServiceProvider<HistoryService> {

	public HistoryProviders() {
		super(LogBlockService.NAME);
	}
}
