package com.comphenix.xp.metrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Objects;

/**
 * Represents a version that can be compared. The input is expected to take the following form: </br >
 * <code>
 * 		(whitespace) Number1.Number2.Number3(.)NumberN Letter (whitespace)
 * </code>
 * <p>
 * 
 * Examples:
 * <ul>
 *   <li>1.2.4</li>
 *   <li>1.0</li>
 *   <li>2.3.4b</li>
 *   <li>12.4.5</li>
 * </ul>
 * 
 * @author Kristian
 */
public class Version implements Comparable<Version> {

	private String version;
	private Pattern lastElement = Pattern.compile("(\\d+)([a-zA-Z])?");
	private Pattern versionFormat = Pattern.compile("\\d+(\\.\\d+)*([a-zA-Z])?");
	
	public Version(String version) {
		if (version == null)
			throw new IllegalArgumentException("Version can not be null");
		
		Matcher match = versionFormat.matcher(version);
		
		// Make sense of the text
		if (!match.find())
			throw new IllegalArgumentException("Invalid version format: " + version);
		this.version = match.group();
	}

	@Override
	public int compareTo(Version that) {
		if (that == null)
			return 1;
		String[] thisParts = this.getVersion().split("\\.");
		String[] thatParts = that.getVersion().split("\\.");
		int length = Math.max(thisParts.length, thatParts.length);

		// The last element may have a letter - check for it
		String thisLetter = getAndRemoveLetter(thisParts);
		String thatLetter = getAndRemoveLetter(thatParts);
		boolean thisHasLetter = thisLetter != null && !thisLetter.isEmpty();
		boolean thatHasLetter = thatLetter != null && !thatLetter.isEmpty();

		for (int i = 0; i < length; i++) {
			int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
			int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;

			if (thisPart < thatPart)
				return -1;
			if (thisPart > thatPart)
				return 1;
		}

		// Compare the letters too
		if (thisHasLetter ^ thatHasLetter)
			return thisHasLetter ? -1 : 1;
		else if (thisHasLetter && thatHasLetter)
			return thisLetter.compareTo(thatLetter);
		else
			return 0;
	}

	// Note: Also modifies the array
	private String getAndRemoveLetter(String[] parts) {

		Matcher match = lastElement.matcher(parts[parts.length - 1]);

		// Remove the letter
		if (match.matches()) {
			parts[parts.length - 1] = match.group(1);

			// Retrieve it, if it exists
			return match.group(2);

		} else {

			// Weird
			return null;
		}
	}
	
	@Override
	public String toString() {
		return version;
	}

	public String getVersion() {
		return this.version;
	}
	
	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (this.getClass() != that.getClass())
			return false;
		return this.compareTo((Version) that) == 0;
	}
}
