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
