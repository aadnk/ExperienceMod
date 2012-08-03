package com.comphenix.xp;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.Test;

import com.comphenix.xp.lookup.MobQuery;
import com.comphenix.xp.messages.ChannelProvider;
import com.comphenix.xp.parser.ActionParser;
import com.comphenix.xp.parser.text.ItemNameParser;
import com.comphenix.xp.parser.text.ItemParser;
import com.comphenix.xp.parser.text.MobMatcher;
import com.comphenix.xp.parser.text.MobParser;
import com.comphenix.xp.rewards.RewardProvider;
import com.comphenix.xp.rewards.RewardTypes;
import com.comphenix.xp.rewards.xp.ExperienceFactory;
import com.google.common.collect.Lists;

public class ConfigurationTest {

	@Test
	public void testMerging() {
		
		Debugger debugger = new MockDebugger();
		
		RewardProvider provider = new RewardProvider();
		provider.register(new MockRewardable(RewardTypes.EXPERIENCE));
		provider.register(new MockRewardable(RewardTypes.VIRTUAL));
		provider.register(new MockRewardable(RewardTypes.ECONOMY));
		provider.setDefaultReward(RewardTypes.EXPERIENCE);
		String def = "EXPERIENCE";
		
		// Reset parser
		ActionParser.setCurrentID(0);
		
		Configuration first = createConfig(
				"multiplier: 1\n" +
				"mobs:\n" + 
				"  ?:\n" +
				"    experience: 5\n" +
				"    economy: 1\n" + 
				"    message: 'hei'\n" + 
				"    channels: [mining, general]", debugger, provider);
		
		Configuration second = createConfig(
				"multiplier: 1\n" +
				"mobs:\n" + 
				"  Blaze|ENTITY_ATTACK: 20\n" +
				"  Cave Spider|ENTITY_ATTACK: 5\n" +
				"  Chicken|ENTITY_ATTACK: 2\n" +
				"  Cow|ENTITY_ATTACK: 4\n" +
				"  Creeper|ENTITY_ATTACK: 17\n" +
				"  Enderman|ENTITY_ATTACK: 25\n" +
				"  Ghast|ENTITY_ATTACK: 35\n" +
				"  Giant|ENTITY_ATTACK: 55\n" +
				"  Iron golem|ENTITY_ATTACK: 55\n" +
				"  Magma cube|ENTITY_ATTACK: 15\n" +
				"  Mushroom cow|ENTITY_ATTACK: 10\n" +
				"  Ocelot|ENTITY_ATTACK: 15\n" +
				"  Pig|ENTITY_ATTACK: 3\n" +
				"  Pig Zombie|ENTITY_ATTACK: 17\n" +
				"  Sheep|ENTITY_ATTACK: 3\n" +
				"  Silverfish|ENTITY_ATTACK: 15\n" +
				"  Skeleton|ENTITY_ATTACK: 15\n" +
				"  Slime|ENTITY_ATTACK: 15\n" +
				"  Snowman|ENTITY_ATTACK: 15\n" +
				"  Spider|ENTITY_ATTACK: 15\n" +
				"  Squid|ENTITY_ATTACK: 7\n" +
				"  Villager|ENTITY_ATTACK: 10\n" +
				"  Wolf|ENTITY_ATTACK: 10\n" +
				"  Zombie|ENTITY_ATTACK: 15\n", debugger, provider);
		
		Configuration result = Configuration.fromMultiple(Lists.newArrayList(first, second), debugger);
		
		MobQuery queryBlace = MobQuery.fromAny(EntityType.BLAZE, DamageCause.ENTITY_ATTACK);
		MobQuery queryZombie = MobQuery.fromAny(EntityType.ZOMBIE, DamageCause.ENTITY_ATTACK);
		
		Action blaceAction = new Action();
		blaceAction.addReward(def, new ExperienceFactory(20));
		blaceAction.setId(1);
		
		Action zombieAction = new Action(def, new ExperienceFactory(15));
		zombieAction.setId(24);
		
		assertEquals(blaceAction, result.getExperienceDrop().get(queryBlace));
		assertEquals(zombieAction, result.getExperienceDrop().get(queryZombie));
	}
	
	// Load configuration from text
	private Configuration createConfig(String text, Debugger debugger, RewardProvider provider) {
		Configuration config = new Configuration(debugger, provider, new ChannelProvider());
		ItemNameParser nameParser = new ItemNameParser();
		
		config.setItemParser(new ItemParser(nameParser));
		config.setMobParser(new MobParser(new MobMatcher()));
		config.setActionTypes(ActionTypes.Default());
		config.loadFromConfig(fromText(text));
		return config;
	}
	
	private YamlConfiguration fromText(String text) {
		try {
			InputStream buffer = new ByteArrayInputStream(text.getBytes("UTF-8"));
			return YamlConfiguration.loadConfiguration(buffer);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("No UTF-8 installed.");
		}
	}
}
