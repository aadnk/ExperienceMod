package com.comphenix.xp.messages;

import java.util.List;

public class Message {

	private String text;
	private List<String> channels;
	
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
}
