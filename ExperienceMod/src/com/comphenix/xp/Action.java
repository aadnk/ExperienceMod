package com.comphenix.xp;

import com.comphenix.xp.messages.Message;

public class Action {

	
	private Range experience;
	private Message message;
	
	protected Action(Range experience, Message message) {
		super();
		this.experience = experience;
		this.message = message;
	}
	
	public Range getExperience() {
		return experience;
	}
	
	public void setExperience(Range experience) {
		this.experience = experience;
	}
	
	public Message getMessage() {
		return message;
	}
	
	public void setMessage(Message message) {
		this.message = message;
	}
}
