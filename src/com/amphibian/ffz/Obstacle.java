package com.amphibian.ffz;

public class Obstacle implements Sprite {

	private int id;
	
	private float x;
	private float y;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	@Override
	public int getVertexPosition() {
		return id;
	}
	@Override
	public int getTexturePosition() {
		return id;
	}
	@Override
	public float getDrawX() {
		return x;
	}
	@Override
	public float getDrawY() {
		return y;
	}
	
	
}
