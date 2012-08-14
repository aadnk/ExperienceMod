package com.comphenix.xp.parser.sections;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.Action;
import com.comphenix.xp.lookup.PlayerRewards;
import com.comphenix.xp.parser.ActionParser;
import com.comphenix.xp.parser.ParsingException;

public class PlayerSectionParser extends SectionParser<PlayerRewards> {

	protected ActionParser actionParser;
	protected double multiplier;
	
	public PlayerSectionParser(ActionParser actionParser, double multiplier) {
		this.actionParser = actionParser;
		this.multiplier = multiplier;
	}

	@Override
	public PlayerRewards parse(ConfigurationSection input, String sectionName) throws ParsingException {

		PlayerRewards playerRewards = new PlayerRewards(multiplier);
		
		if (input == null)
			throw new NullArgumentException("input");
		
		// Null is handled as the root
		if (sectionName != null) {
			input = input.getConfigurationSection(sectionName);
		
			// No rewards found
			if (input == null)
				return playerRewards;
		}
		
		// Handle every reward
		for (String key : input.getKeys(false)) {
			
			try {
				Action value = actionParser.parse(input, key);
				
				if (value != null)
					playerRewards.put(key, value);
				else
					throw ParsingException.fromFormat("No value found.");
				
			} catch (ParsingException ex) {
				if (isCollectExceptions()) {
					// For now, record it
					debugger.printWarning(this, "Cannot parse player reward %s - %s", key, ex.getMessage());
				} else {
					// Just invoke the error
					throw ex;
				}
			}
		}
		
		// And we're done
		return playerRewards;
	}
}
