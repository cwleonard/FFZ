package com.amphibian.ffz.engine.util;

public class CollisionDataHolder {

	private String name;
	private float[] anchor;
	private float[][] polygons;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float[] getAnchor() {
		return anchor;
	}
	public void setAnchor(float[] anchor) {
		this.anchor = anchor;
	}
	public float[][] getPolygons() {
		return polygons;
	}
	public void setPolygons(float[][] polygons) {
		this.polygons = polygons;
	}

}