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

import com.comphenix.xp.extra.Service;

public interface ChannelService extends Service {
	
	/**
	 * Determines whether or not the channel with the given identifier exists.
	 * @param channelID - channel identifier.
	 * @return TRUE if the channel exists, FALSE otherwise.
	 */
	public boolean hasChannel(String channelID);
	
	/**
	 * Broadcast a message on a given channel.
	 * @param channelID - channel identifier. 
	 * @param message - message to broadcast.
	 */
	public void announce(String channelID, String message);
	
	/**
	 * Sends a message with a sender on a given channel.
	 * @param channelID - channel identifier. 
	 * @param message - message to broadcast.
	 * @param sender - sender to include in the message.
	 */
	public void emote(String channelID, String message, Player sender);
}
