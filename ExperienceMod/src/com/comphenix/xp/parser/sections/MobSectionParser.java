package com.comphenix.xp.parser.sections;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.configuration.ConfigurationSection;

import com.comphenix.xp.Action;
import com.comphenix.xp.expressions.ParameterProviderSet;
import com.comphenix.xp.lookup.MobQuery;
import com.comphenix.xp.lookup.MobTree;
import com.comphenix.xp.parser.ActionParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.text.MobParser;

public class MobSectionParser extends SectionParser<MobTree> {

	// Mob variables
	public static String[] NAMED_PARAMETERS = {};

	protected ParameterProviderSet parameterProviders;
	protected ActionParser actionParser;
	protected MobParser mobParser;
	protected double multiplier;
	
	public MobSectionParser(ActionParser actionParser, MobParser mobParser, 
							ParameterProviderSet parameterProviders, double multiplier) {
		
		this.actionParser = actionParser;
		this.mobParser = mobParser;
		this.parameterProviders = parameterProviders;
		this.multiplier = multiplier;
	}

	@Override
	public MobTree parse(ConfigurationSection input, String sectionName) throws ParsingException {

		MobTree experienceDrop = new MobTree(multiplier);
		ActionParser parser = actionParser.createView(NAMED_PARAMETERS);
		
		if (input == null)
			throw new NullArgumentException("input");
		
		// Null is handled as the root
		if (sectionName != null) {
			input = input.getConfigurationSection(sectionName);
			
			// No rewards found
			if (input == null)
				return experienceDrop;
		}
		
		// Parse every sub-section
		for (String key : input.getKeys(false)) {
			try {
				Action value = parser.parse(input, key);
				MobQuery query = mobParser.parse(key);
	
				if (value != null)
					experienceDrop.put(query, value);
				else 
					throw new ParsingException("Cannot find configuration.");
			
			} catch (ParsingException e) {
				if (isCollectExceptions()) {
					// For now, record it
					debugger.printWarning(this, "Unable to parse entity %s: %s", key, e.getMessage());
				} else {
					// Just invoke the error
					throw e;
				}
			}
		}
		
		return experienceDrop;
	}
}
