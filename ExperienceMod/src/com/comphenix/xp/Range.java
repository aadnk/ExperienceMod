package com.comphenix.xp;

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
