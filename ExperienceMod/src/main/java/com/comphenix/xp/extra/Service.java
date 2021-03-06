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

package com.comphenix.xp.extra;

public interface Service {

	/**
	 * Retrieves a unique string identifying this service. May also be used during parsing. 
	 * <p>
	 * Note that this identifier must conform to an ENUM convention: upper case only, 
	 * underscore for space. 
	 * <p>
	 * A service MUST not alter its identifier once it has been registered.
	 * 
	 * @return A unique reward ID.
	 */
	public String getServiceName();
}
