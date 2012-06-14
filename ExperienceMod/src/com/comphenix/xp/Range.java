package com.comphenix.xp;

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

import java.util.Random;

public class Range {
	private double start;
	private double end;
	
	public static Range Default = new Range(0);
	
	public Range(double value) {
		this.start = value;
		this.end = value;
	}
	
	public Range(double start, double end) {
		this.start = start;
		this.end = end;
	}
	
	public double getStart() {
		return start;
	}
	
	public double getEnd() {
		return end;
	}
	
	public Range multiply(double multiply) {
		// Consider the average value of a uniform distribution
		return new Range(start * multiply, end * multiply);
	}
	
	public double sampleDouble(Random rnd) {
		if (start == end)
			return start;
		else
			return start + (end - start) * rnd.nextDouble();
	}
	
	public int sampleInt(Random rnd) {
		double value = sampleDouble(rnd);
		
		// Probability of adding the fraction
		double fraction = value - Math.floor(value);
		double toAdd = rnd.nextDouble() < fraction ? Math.signum(value) : 0;
		
		return (int)value + (int)toAdd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(end);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(start);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Range other = (Range) obj;
		return Double.compare(start, other.start) == 0 &&
		       Double.compare(end, other.end) == 0;
	}

	@Override
	public String toString() {
		return "{start: " + start + ", end: " + end + "]";
	}
}
