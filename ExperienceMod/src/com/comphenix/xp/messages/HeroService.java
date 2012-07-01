package com.comphenix.xp.messages;

import org.bukkit.entity.Player;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;

public class HeroService implements ChannelService {

	public static final String NAME = "HEROCHAT";
	
	public HeroService() {
		// Make sure we haven't screwed up
		if (!exists())
			throw new IllegalArgumentException("HeroChat hasn't been enabled.");
	}
	
	/**
	 * Determines whether or not the HeroChat plugin is loaded AND enabled.
	 * @return TRUE if it is, FALSE otherwise.
	 */
	public static boolean exists() {
		try {
			// Make sure
			if (Herochat.getPlugin().isEnabled())
				return true;
			else
				return false;
			
			// Cannot load plugin
		} catch (NullPointerException e)  {
			return false;
		} catch (NoClassDefFoundError e) {
			return false;
		}
	}
	
	@Override
	public boolean hasChannel(String channelID) {
		try {
			// See if this channel exists
			return Herochat.getChannelManager().hasChannel(channelID);
		} catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public void announce(String channelID, String message) {
	
		try {
			getChannel(channelID).announce(message);
			
			// Handle this too
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Cannot find HeroChat channel manager.");
		}
	}

	@Override
	public void emote(String channelID, String message, Player sender) {

		try {
			Chatter playerChatter = Herochat.getChatterManager().getChatter(sender);
			
			if (playerChatter == null)
				throw new IllegalArgumentException("Player doesn't have a chatter channel.");
			
			// Emote for this character
			getChannel(channelID).emote(playerChatter, message);
		
		// Handle this too
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Cannot find HeroChat channel manager.");
		}
	}

	private Channel getChannel(String channelID) {
		
		// Stores channels in a HashMap, so it should return NULL if the channel doesn't exist 
		Channel channel = Herochat.getChannelManager().getChannel(channelID);
			
		if (channel == null) {
			throw new IllegalArgumentException("Channel doesn't exist.");
		} else {
			return channel;
		}
	}
	
	@Override
	public String getServiceName() {
		return NAME;
	}
}
