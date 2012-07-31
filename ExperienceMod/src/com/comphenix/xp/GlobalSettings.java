package com.comphenix.xp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.configuration.file.FileConfiguration;

import com.comphenix.xp.parser.StringListParser;

public class GlobalSettings {

	// Settings
	public static final String MAX_BLOCKS_IN_HISTORY_SETTING = "max blocks in history";
	public static final String MAX_AGE_IN_HISTORY_SETTING = "max age in history";
	public static final String USE_PERMISSIONS = "use permissions";
	public static final String USE_METRICS = "use metrics";
	public static final String DISABLED_SERVICES = "disabled services";
	
	private static final int DEFAULT_MAX_BLOCKS_IN_HISTORY = 5000;
	private static final int DEFAULT_MAX_AGE_IN_HISTORY = 600; // 10 minutes
	private static final boolean DEFAULT_USE_PERMISSIONS = true;
	private static final boolean DEFAULT_USE_METRICS = true;
	
	// Configuration file
	private FileConfiguration currentConfig;
	
	// Block changes memory history
	private int maxBlocksInHistory;
	private int maxAgeInHistory;

	private boolean useMetrics;
	private boolean usePermissions;
	private List<String> disabledServices;
	
	// Parsers
	private StringListParser listParser = new StringListParser();
	
	// Debugger
	private Debugger debugger;
	
	public GlobalSettings(Debugger debugger) {
		this.debugger = debugger;
	}
	
	/**
	 * Initialize configuration from a configuration section.
	 * @param config - configuration section to load from.
	 */
	public void loadFromConfig(FileConfiguration config) {
	
		// Load history memory settings
		maxBlocksInHistory = config.getInt(MAX_BLOCKS_IN_HISTORY_SETTING, DEFAULT_MAX_BLOCKS_IN_HISTORY);
		maxAgeInHistory = config.getInt(MAX_AGE_IN_HISTORY_SETTING, DEFAULT_MAX_AGE_IN_HISTORY);
		
		useMetrics = config.getBoolean(USE_METRICS, DEFAULT_USE_METRICS);
		usePermissions = config.getBoolean(USE_PERMISSIONS, DEFAULT_USE_PERMISSIONS);
		disabledServices = listParser.parseSafe(config, DISABLED_SERVICES);
		
		// Handle errors
		if (disabledServices == null) {
			debugger.printDebug(this, "No disabled service setting found.");
			disabledServices = new ArrayList<String>();
		}
		if (maxAgeInHistory < -1) {
			debugger.printWarning(this, "Maximum age (in seconds) cannot be %s.", maxAgeInHistory);
			maxAgeInHistory = -1;
		}
		if (maxBlocksInHistory < -1) {
			debugger.printWarning(this, "Maximum number of blocks (in seconds) cannot be %s.", maxBlocksInHistory);
			maxBlocksInHistory = -1;
		}
		
		// Save it
		this.currentConfig = config;
	}
	
	/***
	 * Retrieves the current updated configuration file.
	 * @return Configuration file.
	 */
	public FileConfiguration getConfiguration() {
		return getConfiguration(true);
	}
	
	/**
	 * Retrieves the current configuration file.
	 * @param updated - whether or not it needs to be updated with the current values.
	 * @return The current file configuration.
	 */
	public FileConfiguration getConfiguration(boolean updated) {
		
		if (currentConfig == null)
			throw new IllegalStateException("Settings hasn't been loaded yet.");
		
		// See if the configuration section should be up to date
		if (updated) {
			currentConfig.set(MAX_BLOCKS_IN_HISTORY_SETTING, maxBlocksInHistory);
			currentConfig.set(MAX_AGE_IN_HISTORY_SETTING, maxAgeInHistory);
			currentConfig.set(USE_METRICS, useMetrics);
			currentConfig.set(USE_PERMISSIONS, usePermissions);
			currentConfig.set(DISABLED_SERVICES, disabledServices);
		}
		
		return currentConfig;
	}
	
	public boolean isUseMetrics() {
		return useMetrics;
	}

	public void setUseMetrics(boolean useMetrics) {
		this.useMetrics = useMetrics;
	}
	
	public int getMaxBlocksInHistory() {
		return maxBlocksInHistory;
	}

	public void setMaxBlocksInHistory(int maxBlocksInHistory) {
		if (maxBlocksInHistory < -1)
			throw new IllegalArgumentException("Maximum number of blocks (in seconds) cannot be less than negative one.");
		
		this.maxBlocksInHistory = maxBlocksInHistory;
	}

	public int getMaxAgeInHistory() {
		return maxAgeInHistory;
	}

	public void setMaxAgeInHistory(int maxAgeInHistory) {
		if (maxAgeInHistory < -1)
			throw new IllegalArgumentException("Maximum age (in seconds) cannot be less than negative one.");
		
		this.maxAgeInHistory = maxAgeInHistory;
	}

	public boolean isUsePermissions() {
		return usePermissions;
	}

	public void setUsePermissions(boolean usePermissions) {
		this.usePermissions = usePermissions;
	}

	public List<String> getDisabledServices() {
		return disabledServices;
	}

	public void setDisabledServices(List<String> disabledServices) {
		if (disabledServices == null)
			throw new NullArgumentException("disabledServices");
		
		this.disabledServices = disabledServices;
	}
}
