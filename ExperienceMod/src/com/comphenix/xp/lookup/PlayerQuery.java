package com.comphenix.xp.lookup;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.comphenix.xp.parser.Utility;
import com.google.common.collect.Lists;

public class PlayerQuery implements Query {

	private List<String> names;
	private List<String> groups;
	private List<DamageCause> deathCause;
	private List<Boolean> murder;
	
	// Optimize away object creations
	private static List<String> noStrings = Utility.getElementList((String) null);
	private static List<DamageCause> noDamages = Utility.getElementList((DamageCause) null);
	private static List<Boolean> noKill = Utility.getElementList((Boolean) null);
	
	/**
	 * Universal query.
	 */
	public static PlayerQuery fromAny() {
		return new PlayerQuery(noStrings, noStrings, noDamages, noKill);
	}

	/**
	 * Creates a query where NULL values match any possible value in that category. 
	 * @param name - name to match, or NULL for every possible name.
	 * @param group - group to match, or NULL for every possible group.
	 * @return Resulting query.
	 */
	public static PlayerQuery fromAny(String name, String group) {
		return new PlayerQuery(
				Utility.getElementList(name), 
				Utility.getElementList(group), 
				noDamages, 
				noKill);
	}
	
	/**
	 * Creates a query where NULL values match any possible value in that category. 
	 * @param name - name to match, or NULL for every possible name.
	 * @param group - group to match, or NULL for every possible group.
	 * @param damageCause - damage cause to match, or NULL for every possible damage cause.
	 * @param murder - whether or the player was killed by another player, or NULL if we don't care.
	 * @return Resulting query.
	 */
	public static PlayerQuery fromAny(String name, String group, DamageCause damageCause, Boolean murder) {
		return new PlayerQuery(
				Utility.getElementList(name), 
				Utility.getElementList(group), 
				Utility.getElementList(damageCause),
				Utility.getElementList(murder));
	}
	
	/**
	 * Creates an exact query - where null values only match unspecified "any" queries.
	 * @param name - name to match, or NULL to match unspecified values.
	 * @param group - group to match, or NULL to match unspecified values.
	 * @return Resulting query.
	 */
	public static PlayerQuery fromExact(String name, String group) {
		return new PlayerQuery(
				Lists.newArrayList(name), 
				Lists.newArrayList(group), 
				noDamages, 
				noKill);
	}
	
	/**
	 * Creates an exact query - where null values only match unspecified "any" queries.
	 * @param player - player to match, or  NULL to match every possible player.
	 * @param groups - groups to match. Use a NULL element to match unspecified values.
	 * @param murder - whether or the player was killed by another player, or NULL to match unknown cases.
	 * @return Resulting query.
	 */
	public static PlayerQuery fromExact(Player player, String[] groups, Boolean murder) {

		EntityDamageEvent event = player.getLastDamageCause();
		DamageCause cause = null;

		if (event != null) {
			cause = event.getCause();
		}

		// Delegate to a more specific method
		return fromExact(player != null ? player.getName() : null, 
						 groups, cause, murder);
	}
	
	
	/**
	 * Creates an exact query - where null values only match unspecified "any" queries.
	 * @param name - name to match, or NULL to match unspecified values.
	 * @param groups - groups to match. Use a NULL element to match unspecified values.
	 * @param damageCause - damage cause to match, or NULL to match unspecified values.
	 * @param murder - whether or the player was killed by another player, or NULL to match unknown cases.
	 * @return Resulting query.
	 */
	public static PlayerQuery fromExact(String name, String[] groups, DamageCause damageCause, Boolean murder) {
		return new PlayerQuery(
				Lists.newArrayList(name), 
				Arrays.asList(groups), 
				Lists.newArrayList(damageCause),
				Lists.newArrayList(murder));
	}
	
	/**
	 * Construct a player query by directly supplying the list of parameters.
	 * @param names - list of names to match.
	 * @param groups - list of groups to match.
	 * @param deathCause - list of death causes to match.
	 * @param murder - list of murder states.
	 */
	public PlayerQuery(List<String> names, List<String> groups, List<DamageCause> deathCause, List<Boolean> murder) {
		this.names = names;
		this.groups = groups;
		this.deathCause = deathCause;
		this.murder = murder;
	}
	
	public List<String> getNames() {
		return names;
	}

	public List<String> getGroups() {
		return groups;
	}

	public List<DamageCause> getDeathCause() {
		return deathCause;
	}

	public List<Boolean> getMurder() {
		return murder;
	}
	
	public boolean hasNames() {
		return names != null && !names.isEmpty();
	}
	
	public boolean hasGroups() {
		return groups != null && !groups.isEmpty();
	}
	
	public boolean hasDeathCause() {
		return deathCause != null && !deathCause.isEmpty();
	}
	
	public boolean hasMurder() {
		return murder != null && !murder.isEmpty();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(names).
	            append(groups).
	            append(deathCause).
	            append(murder).
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

        PlayerQuery other = (PlayerQuery) obj;
        return new EqualsBuilder().
            append(names, other.names).
            append(groups, other.groups).
            append(deathCause, other.deathCause).
            append(murder, other.murder).
            isEquals();
	}
	
	@Override
	public Types getQueryType() {
		return Types.PLAYERS;
	}

	@Override
	public boolean match(Query other) {

		// Match every parameter
		if (other instanceof PlayerQuery) {
			PlayerQuery query = (PlayerQuery) other;
			
			// Make sure the current query is the superset of other
			return QueryMatching.matchParameter(names, query.names) &&
				   QueryMatching.matchParameter(groups, query.groups) &&
				   QueryMatching.matchParameter(deathCause, query.deathCause) &&
				   QueryMatching.matchParameter(murder, query.murder);
		}
		
		// Query must be of the same type
		return false;
	}
	
	@Override
	public String toString() {

		return String.format("%s|%s|%s|%s", 
				hasNames() ? StringUtils.join(names, ", ") : "",
				hasGroups() ? StringUtils.join(groups, ", ") : "",
				hasDeathCause() ? StringUtils.join(deathCause, ", ") : "",
			    Utility.formatBoolean("murder", murder));
	}
}
