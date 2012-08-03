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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import com.comphenix.xp.extra.ServiceProvider;
import com.comphenix.xp.rewards.ResourceHolder;

/**
 * Contains every channel messaging channel plugins known and enabled on the server. The default plugin will
 * be used to send messages.
 * 
 * @author Kristian
 */
public class ChannelProvider extends ServiceProvider<ChannelService> {

	private List<String> defaultChannels = new ArrayList<String>();
	private MessageFormatter messageFormatter;

	public ChannelProvider() {
		super(HeroService.NAME);
	}
	
	public ChannelProvider(ChannelProvider other) {
		super(other.getDefaultName());
		setDefaultChannels(other.getDefaultChannels());
		setMessageFormatter(other.getMessageFormatter());
	}
	
	public ChannelProvider(String defaultService) {
		super(defaultService);
	}
	
	public MessageFormatter getFormatter(Player player, Collection<ResourceHolder> result) {
		return messageFormatter.createView(player, result);
	}
	
	public MessageFormatter getFormatter(Player player, Collection<ResourceHolder> result, Integer count) {
		return messageFormatter.createView(player, result, count);
	}
	
	public MessageFormatter getMessageFormatter() {
		return messageFormatter;
	}

	public void setMessageFormatter(MessageFormatter messageFormatter) {
		this.messageFormatter = messageFormatter;
	}
	
	public List<String> getDefaultChannels() {
		return defaultChannels;
	}

	public void setDefaultChannels(List<String> defaultChannels) {
		this.defaultChannels = defaultChannels;
	}
	
	/**
	 * Creates a copy of this channel provider with shallow references to the same list of channel services, except with a different
	 * internal default reward type. 
	 * @return A shallow copy of this reward service provider.
	 */
	public ChannelProvider createView() {
		return new ChannelProvider(this);
	}
}
