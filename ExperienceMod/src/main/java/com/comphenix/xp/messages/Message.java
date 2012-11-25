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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Message {

	private String text;
	private List<String> channels;
	
	// Default
	public Message() {
	}
	
	public Message(String text, String... channels) {
		this.text = text;
		this.channels = new ArrayList<String>(Arrays.asList(channels));
	}
	
	public Message(String text, List<String> channels) {
		this.text = text;
		this.channels = channels;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<String> getChannels() {
		return channels;
	}
	public void setChannels(List<String> channels) {
		this.channels = channels;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(text).
	            append(channels).
	            toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        Message other = (Message) obj;
        return new EqualsBuilder().
            append(text, other.text).
            append(channels, other.channels).
            isEquals();
	}
	
	@Override
	public String toString() {
		// Convert to a more readable format
		return String.format("[%s, %s]", text, StringUtils.join(channels, ", "));
	}
}
