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
