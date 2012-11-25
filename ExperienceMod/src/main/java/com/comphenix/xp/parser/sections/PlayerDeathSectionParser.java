package com.comphenix.xp.parser.sections;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.Action;
import com.comphenix.xp.expressions.ParameterProviderSet;
import com.comphenix.xp.lookup.PlayerQuery;
import com.comphenix.xp.lookup.PlayerTree;
import com.comphenix.xp.parser.ActionParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.text.PlayerParser;

public class PlayerDeathSectionParser extends SectionParser<PlayerTree> {

	protected ParameterProviderSet parameterProviders;
	protected ActionParser actionParser;
	protected PlayerParser playerParser;
	protected double multiplier;
	
	public PlayerDeathSectionParser(ActionParser actionParser, PlayerParser playerParser, 
									ParameterProviderSet parameterProviders, double multiplier) {
		
		this.actionParser = actionParser;
		this.playerParser = playerParser;
		this.parameterProviders = parameterProviders;
		this.multiplier = multiplier;
	}

	@Override
	public PlayerTree parse(ConfigurationSection input, String sectionName) throws ParsingException {

		PlayerTree playerDeathDrop = new PlayerTree(multiplier);
		
		String[] names = parameterProviders.getPlayerParameters().getParameterNames();
		ActionParser parser = actionParser.createView(names);

		if (input == null)
			throw new NullArgumentException("input");
		
		// Null is handled as the root
		if (sectionName != null) {
			input = input.getConfigurationSection(sectionName);
			
			// No rewards found
			if (input == null)
				return playerDeathDrop;
		}
		
		// Parse every sub-section
		for (String key : input.getKeys(false)) {
			try {
				Action value = parser.parse(input, key);
				PlayerQuery query = playerParser.parse(key);
	
				if (value != null)
					playerDeathDrop.put(query, value);
				else 
					throw new ParsingException("Cannot find configuration.");
			
			} catch (ParsingException e) {
				if (isCollectExceptions()) {
					e.printStackTrace();
					// For now, record it
					debugger.printWarning(this, "Unable to parse entity %s: %s", key, e.getMessage());
				} else {
					// Just invoke the error
					throw e;
				}
			}
		}
		
		return playerDeathDrop;
	}
}
