package com.comphenix.xp.messages;

import org.bukkit.entity.Player;

public class MessageFormatter {

	private Player source;
	private Integer experience;

	// Default
	public MessageFormatter() {
	}

	public MessageFormatter(Player player, Integer experience) {
		setSource(player);
		setExperience(experience);
	}
	
	/**
	 * Replaces parameters in the text with their respective value.
	 * @param message - message to format.
	 * @return Message with every parameter replaced with the corresponding value.
	 */
	public String formatMessage(String message) {
		
		if (message == null)
			return null;
		
		// A simple system for now. 
		// TODO: Add more variables.
		return message.
				replace("{player}", source != null ? source.getDisplayName() : "Unknown").
				replace("{exp}", experience != null ? experience.toString() : "N/A");
	}

	public Player getSource() {
		return source;
	}

	public void setSource(Player source) {
		this.source = source;
	}

	public Integer getExperience() {
		return experience;
	}

	public void setExperience(Integer experience) {
		this.experience = experience;
	}
	
	// Create a copy of the message formatter with the given parameters
	public MessageFormatter createView(Player player, Integer experience) {
		return new MessageFormatter(player, experience);
	}
	
	// Create a copy of the message formatter with the given parameters
	public MessageFormatter createView(Integer experience) {
		return createView(null, experience);
	}
}
