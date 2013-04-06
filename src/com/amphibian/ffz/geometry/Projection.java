package com.amphibian.ffz.geometry;

public class Projection {

	private float min;
	
	private float max;
	
	public Projection(float min, float max) {
		this.min = min;
		this.max = max;
	}
	
	public boolean overlaps(Projection p) {
		return (this.max > p.min || this.min < p.max);
	}
	
	
	
}
