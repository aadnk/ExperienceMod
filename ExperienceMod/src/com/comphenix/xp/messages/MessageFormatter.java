package com.comphenix.xp.messages;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageFormatter {

	private Player source;
	private Integer experience;
	private Integer count;
	
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
				replace("{experience}", experience != null ? experience.toString() : "N/A").
				replace("{count}", count != null ? count.toString() : "N/A").
				replaceAll("&(?=[0-9a-fxA-FX])", Character.toString(ChatColor.COLOR_CHAR));
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
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
