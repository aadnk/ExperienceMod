package com.comphenix.xp.extra;

/**
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

/**
* Represents the four different types of Smooth Brick
*/
public enum SmoothBrickType {
    NORMAL(0x0),
    MOSSY(0x1),
    CRACKED(0x2),
    CIRCLE(0x3);

    private final byte data;

    private SmoothBrickType(final int data) {
        this.data = (byte) data;
    }

    /**
	* Gets the associated data value representing this type of smooth brick
	*
	* @return A byte containing the data value of this smooth brick type
	*/
    public byte getData() {
        return data;
    }
}
