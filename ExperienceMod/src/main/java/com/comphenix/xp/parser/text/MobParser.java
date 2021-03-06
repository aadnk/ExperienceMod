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

package com.comphenix.xp.parser.text;

import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.comphenix.xp.lookup.MobQuery;
import com.comphenix.xp.parser.TextParser;
import com.comphenix.xp.parser.ParsingException;
import com.comphenix.xp.parser.Utility;
import com.comphenix.xp.parser.primitives.BooleanParser;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class MobParser extends TextParser<MobQuery> {
	
	private ParameterParser<List<Short>> entityTypeParser;
	private ParameterParser<DamageCause> damageCauseParser;
	private ParameterParser<Integer> mobSizeParser;
	private ParameterParser<SkeletonType> skeletonParser;
	
	private BooleanParser spawnerParser = new BooleanParser("spawner");
	private BooleanParser babyParser = new BooleanParser("baby");
	private BooleanParser tamedParser = new BooleanParser("tamed");
	private BooleanParser playerParser = new BooleanParser("player");
	private BooleanParser villagedParser = new BooleanParser("villaged");
	
	public MobParser(MobMatcher matcher) {
		this.entityTypeParser = new ParameterParser<List<Short>>(new MobEntityTypeParser(matcher));
		this.damageCauseParser = new ParameterParser<DamageCause>(new MobDamageCauseParser());
		this.mobSizeParser = new ParameterParser<Integer>(new MobSizeParser());
		this.skeletonParser = new ParameterParser<SkeletonType>(new MobSkeletonParser());
	}
	
	@Override
	public MobQuery parse(String text) throws ParsingException {
		
		Queue<String> tokens = getParameterQueue(text);
		
		ParsingException errorReason = null;
		
		List<Short> types = Utility.getElementList((Short) null);
		List<DamageCause> causes = Utility.getElementList((DamageCause) null);
		List<Integer> sizes = Utility.getElementList((Integer) null);
		List<SkeletonType> skeletons = Utility.getElementList((SkeletonType) null);
		
		try {
			types = flatten(entityTypeParser.parse(tokens));
			causes = damageCauseParser.parse(tokens);
			sizes = mobSizeParser.parse(tokens);
			skeletons = skeletonParser.parse(tokens);
			
		} catch (ParsingException e) {
			// Try more
			errorReason = e;
		}
		
		// Scan all unused parameters for these options first
		List<Boolean> spawner = spawnerParser.parseAny(tokens);
		List<Boolean> baby = babyParser.parseAny(tokens);
		List<Boolean> tamed = tamedParser.parseAny(tokens);
		List<Boolean> player = playerParser.parseAny(tokens);
		List<Boolean> villaged = villagedParser.parseAny(tokens); // Zombies

		// If there are some tokens left, a problem occured
		if (!tokens.isEmpty()) {
			
			// Let the user know about the reason too
			if (errorReason != null)
				throw errorReason;
			else
				throw ParsingException.fromFormat("Unknown item tokens: %s", StringUtils.join(tokens, ", "));
		}
		
		return new MobQuery(types, causes, sizes, skeletons, spawner, baby, tamed, player, villaged);
	}

	private List<Short> flatten(List<List<Short>> list) {
		return Lists.newArrayList(Iterables.concat(list));
	}
	
	public ParameterParser<List<Short>> getEntityTypeParser() {
		return entityTypeParser;
	}

	public void setEntityTypeParser(ParameterParser<List<Short>> entityTypeParser) {
		this.entityTypeParser = entityTypeParser;
	}

	public ParameterParser<DamageCause> getDamageCauseParser() {
		return damageCauseParser;
	}

	public void setDamageCauseParser(ParameterParser<DamageCause> damageCauseParser) {
		this.damageCauseParser = damageCauseParser;
	}
	
	public ParameterParser<Integer> getMobSizeParser() {
		return mobSizeParser;
	}

	public void setMobSizeParser(ParameterParser<Integer> mobSizeParser) {
		this.mobSizeParser = mobSizeParser;
	}

	public BooleanParser getSpawnerParser() {
		return spawnerParser;
	}

	public void setSpawnerParser(BooleanParser spawnerParser) {
		this.spawnerParser = spawnerParser;
	}

	public BooleanParser getBabyParser() {
		return babyParser;
	}

	public void setBabyParser(BooleanParser babyParser) {
		this.babyParser = babyParser;
	}

	public BooleanParser getTamedParser() {
		return tamedParser;
	}

	public void setTamedParser(BooleanParser tamedParser) {
		this.tamedParser = tamedParser;
	}

	public BooleanParser getPlayerParser() {
		return playerParser;
	}

	public void setPlayerParser(BooleanParser playerParser) {
		this.playerParser = playerParser;
	}
}
