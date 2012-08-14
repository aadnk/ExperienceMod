/*
 *  ExperienceMod - Bukkit server plugin for modifying the experience system in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.xp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.comphenix.xp.listeners.PlayerCleanupListener;
import com.comphenix.xp.lookup.PresetQuery;
import com.comphenix.xp.lookup.PresetTree;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.primitives.StringParser;
import com.comphenix.xp.parser.text.ParameterParser;
import com.comphenix.xp.parser.text.PresetParser;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;

/**
 * Contains every loaded configuration preset.
 */
public class Presets implements PlayerCleanupListener {

	public static final String OPTION_PRESET_SETTING = "experiencePreset";
	
	private static final String IMPORT_FILE_SETTING = "file";
	private static final String LOCAL_SETTING = "local";

	// MineCraft servers with more than 1000 players? Ridiculous.
	private static final int MAXIMUM_CACHE_SIZE = 1000;
	
	// We can reference players directly as the Guava Cache is using WeakReferences under the hood
	private Cache<Player, Optional<Configuration>> configCache;
	private Supplier<Configuration> defaultCached;
	
	// Mapping of preset name and configuration
	private PresetTree presets;
	
	// Parser
	private PresetParser presetParser = new PresetParser();
	private ParameterParser<String> stringParser = new ParameterParser<String>(new StringParser());
	
	private Debugger logger;
	
	// Chat
	private Chat chat;
	
	public Presets(ConfigurationSection config, ConfigurationLoader loader, 
				   int cacheTimeout, Debugger logger, Chat chat) {
		
		this.presets = new PresetTree();
		this.logger = logger;
		this.chat = chat;
		
		if (config != null) {
			loadPresets(config, loader);
		}
			
		initializeCache(cacheTimeout);
	}
	
	private void initializeCache(int cacheTimeout) {
		// Don't create a cache if we're just going to throw away the values all the time
		if (cacheTimeout > 0) {
			// Construct our cache
			configCache = CacheBuilder.newBuilder().
				weakKeys().
				weakValues().
				maximumSize(MAXIMUM_CACHE_SIZE).
				expireAfterWrite(cacheTimeout, TimeUnit.SECONDS).
				build(new CacheLoader<Player, Optional<Configuration>>() {
					@Override
					public Optional<Configuration> load(Player player) throws Exception {
						return Optional.fromNullable(getPlayerConfiguration(player));
					}
				});
		}
		
		// Cache the default configuration too
		defaultCached = Suppliers.memoizeWithExpiration(new Supplier<Configuration>() {
			@Override
			public Configuration get() {
				try {
					return getConfiguration(null, null);
				} catch (ParsingException e) {
					throw new RuntimeException("Parsing problem.", e);
				}
			}
		}, cacheTimeout, TimeUnit.SECONDS);
	}
	
	/**
	 * Retrieves a stored configuration from a key value. Note that while NULL in rules will match 
	 * any query, a query with null will NOT match any rule. In that case, it will only match rules with
	 * null in the corresponding parameters.
	 * 
	 * @param presetNames - key value(s) of the configuration to retrieve.
	 * @param worldName - name of the world the preset is associated with. 
	 * @return The stored configuration, or NULL if no configuration exists.
	 * @throws ParsingException If the given list of keys is malformed.
	 */
	public Configuration getConfiguration(String presetNames, String worldName) throws ParsingException {
		
		List<String> names = stringParser.parseExact(presetNames);
		
		PresetQuery query = PresetQuery.fromExact(names, worldName);
		Configuration result = presets.get(query);
	
		// Determine what to return
		if (result != null) {
			return result;
		}
		
		// Error
		return null;
	}
	
	/**
	 * Retrieves the default configuration for the given player.
	 * @param sender - the sender, or NULL to retrieve the generic/default configuration.
	 * @return Configuration, or NULL if no configuration could be found.
	 * @throws ParsingException - If the sender has a malformed preset list.
	 */
	public Configuration getConfiguration(CommandSender sender) throws ParsingException {
		
		// See if we in fact can use presets
		if (chat != null && sender instanceof Player) {
			// Use the cache if it's present
			if (configCache != null) {
				try {
					return configCache.get((Player) sender).orNull();
				} catch (Exception e) {
					throw new ParsingException("Cannot load configuration.", e);
				}
			} else {
				return getPlayerConfiguration((Player) sender);
			}
			
		// Default configuration
		} else {
			
			try {
				return defaultCached.get();
				
				// Catch our runtime error
			} catch (RuntimeException e) {
				if (e.getCause() instanceof ParsingException)
					throw (ParsingException) e.getCause();
				else
					throw e;
			}
		}
	}
	
	private Configuration getPlayerConfiguration(Player sender) throws ParsingException {
		
		Player player = (Player) sender;
		String preset = null;
		String world = null;
		
		try {
			preset = chat.getPlayerInfoString(player, OPTION_PRESET_SETTING, null);
			
		} catch (RuntimeException e) {
			// Must be a runtime exception, otherwise we'd have to handle it from the method above.
			if (!ignorableException(e)) {
				throw e;
			} else {
				logger.printDebug(this, "Ignored NPE from mChat.");
			}
		}
		
		world = player.getWorld().getName();
		return getConfiguration(preset, world);
	}
	
	public boolean usesPresetParameters() {
		return presets.usesPresetNames();
	}
	
	private void loadPresets(ConfigurationSection section, ConfigurationLoader loader) {
		for (String key : section.getKeys(false)) { 
			
			// Load query
			try {
				PresetQuery query = presetParser.parse(key);
			
				// Load section
				Configuration data = loadPreset(
						section.getConfigurationSection(key), loader);
				
				// Remember if we have presets or not
				data.setPreset(query.hasPresetNames());
				
				if (data != null)
					presets.put(query, data);
			
			} catch (ParsingException ex) {
				if (logger != null)
					logger.printWarning(this, "Cannot parse preset - %s", ex.getMessage());
			}
		}
	}
	
	private Configuration loadPreset(ConfigurationSection data, ConfigurationLoader loader) {
		
		List<Configuration> files = getConfigurations(data, loader);
		Configuration local = getLocal(data, loader);
		Configuration result = null;
		
		// Local configuration has the highest priority
		if (local != null) {
			files.add(local);
		}
		
		// Make sure there is anything to return
		if (files.isEmpty())
			result = new Configuration(logger, loader.getActionTypes());
		else
			result = Configuration.fromMultiple(files, logger);
		
		return result;
	}
	
	public boolean containsPreset(String preset, String world) throws ParsingException {
		Configuration result = getConfiguration(preset, world);
		
		// Whether or not the matching rule has specified presets
		return result.hasPreset();
	}
	
	private Configuration getLocal(ConfigurationSection data, ConfigurationLoader loader) {
		
		// Retrieve using the configuration section
		if (data.isConfigurationSection(LOCAL_SETTING)) {
			return loader.getFromSection(data.getConfigurationSection(LOCAL_SETTING));
		} else {
			return null;
		}
	}
	
	private List<Configuration> getConfigurations(ConfigurationSection data, ConfigurationLoader loader) {
		
		List<Configuration> result = new ArrayList<Configuration>();
		
		for (String path : getFiles(data)) {
		
			// Load from folder
			Configuration config = loader.getFromPath(path);
			
			if (config != null)
				result.add(config);
			else if (logger != null)
				logger.printWarning(this, "Cannot find configuration file %s.", path);
		}
		
		return result;
	}
	
	private List<String> getFiles(ConfigurationSection data) {
		
		if (data.isString(IMPORT_FILE_SETTING))
			return Lists.newArrayList(data.getString(IMPORT_FILE_SETTING));
		else if (data.isList(IMPORT_FILE_SETTING))
			return data.getStringList(IMPORT_FILE_SETTING);
		else
			return Lists.newArrayList();
	}

	public Collection<Configuration> getConfigurations() {
		return presets.getValues();
	}
		
	public void onTick() {
		
		// Make sure messages are being sent
		if (presets != null) {
			for (Configuration config : presets.getValues()) {
				config.onTick();
			}		
		}
	}

	@Override
	public void removePlayerCache(Player player) {

		// Make sure messages are being sent
		if (presets != null) {
			for (Configuration config : presets.getValues()) {
				config.removePlayerCache(player);
			}	
		}
	}

	/**
	 * Determines whether or not the given exception can be ignored if thrown from getGroupInfoString.
	 * @param e - the exception to test.
	 * @see net.milkbowl.vault.chat.Chat#getGroupInfoString(String, String, String, String) Chat.getGroupInfoString
	 * @return TRUE if it can be ignored, FALSE otherwise.
	 */
	public boolean ignorableException(Exception e) {
		// This plugin insists on using NullPointerExceptions as a good flag for "not found". Excellent.
		if (e instanceof NullPointerException && chat.getName().equals("mChatSuite"))
			return true;
		
		// Treat it normally
		return false;
	}
}
