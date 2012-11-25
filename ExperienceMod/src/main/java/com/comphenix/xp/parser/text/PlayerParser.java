package com.comphenix.xp.parser.text;

import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.comphenix.xp.lookup.PlayerQuery;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.parser.primitives.BooleanParser;
import com.comphenix.xp.parser.primitives.StringParser;

public class PlayerParser extends TextParser<PlayerQuery> {

	private ParameterParser<String> nameParser;
	private ParameterParser<String> groupParser;
	private ParameterParser<DamageCause> damageCauseParser;
	private BooleanParser murderParser = new BooleanParser("murder");
	
	public PlayerParser() {
		this.nameParser = new ParameterParser<String>(new StringParser());
		this.groupParser = new ParameterParser<String>(new StringParser());
		this.damageCauseParser = new ParameterParser<DamageCause>(new MobDamageCauseParser());
	}
	
	@Override
	public PlayerQuery parse(String text) throws ParsingException {

		Queue<String> tokens = getParameterQueue(text);
		
		ParsingException errorReason = null;
		
		// Default values
		List<String> names = Utility.getElementList((String) null);
		List<String> groups = Utility.getElementList((String) null);
		List<DamageCause> causes = Utility.getElementList((DamageCause) null);;
		
		try {
			names = nameParser.parse(tokens);
			groups = groupParser.parse(tokens);
			causes = damageCauseParser.parse(tokens);
			
		} catch (ParsingException e) {
			// Try more
			errorReason = e;
		}
		
		// Scan all unused parameters for these options
		List<Boolean> murder = murderParser.parseAny(tokens);

		// If there are some tokens left, a problem occurred
		if (!tokens.isEmpty()) {
			
			// Let the user know about the reason too
			if (errorReason != null)
				throw errorReason;
			else
				throw ParsingException.fromFormat("Unknown item tokens: %s", StringUtils.join(tokens, ", "));
		}
		
		return new PlayerQuery(names, groups, causes, murder);
	}

	public ParameterParser<String> getNameParser() {
		return nameParser;
	}

	public void setNameParser(ParameterParser<String> nameParser) {
		this.nameParser = nameParser;
	}

	public ParameterParser<String> getGroupParser() {
		return groupParser;
	}

	public void setGroupParser(ParameterParser<String> groupParser) {
		this.groupParser = groupParser;
	}

	public ParameterParser<DamageCause> getDamageCauseParser() {
		return damageCauseParser;
	}

	public void setDamageCauseParser(ParameterParser<DamageCause> damageCauseParser) {
		this.damageCauseParser = damageCauseParser;
	}

	public BooleanParser getMurderParser() {
		return murderParser;
	}

	public void setMurderParser(BooleanParser murderParser) {
		this.murderParser = murderParser;
	}
}
