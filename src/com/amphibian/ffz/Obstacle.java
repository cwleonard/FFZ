package com.amphibian.ffz;

import java.util.List;

import com.amphibian.ffz.geometry.ConvexPolygon;

public class Obstacle implements Sprite {

	private int id;
	
	private String type;
	
	private float x;
	private float y;
	
	private float w;
	private float h;
	private float hh;
	private float hw;
	
	private List<ConvexPolygon> blockers;
	
	public Obstacle(String t, float x, float y) {
		
		FrameDataManager fdm = FrameDataManager.getInstance();
		Frame f = fdm.getFrame(t);
		this.type = t;
		this.id = f.getIndex();
		this.w = f.getWidth();
		this.h = f.getHeight();
		this.hh = h / 2.0f;
		this.hw = w / 2.0f;
		this.x = x;
		this.y = y;
		
		blockers = fdm.getPolygons(x, y, type);
		
	}

	public Obstacle() {
		this.id = -1;
	}
	
	public void hurt() {
		// nothing
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public float getDrawX() {
		return x;
	}
	@Override
	public float getDrawY() {
		return y;
	}
	@Override
	public void draw(Drawinator d) {

		float z = -1f - (this.getBottom() / 999999f);

		d.setBufferPosition(this.id);
		
		d.setDrawPosition(this.getShadowX(), this.getShadowY(), z - 0.00001f);
		d.setMode(Drawinator.SHADOW_MODE);
		d.performDraw();
		
		d.setMode(Drawinator.NORMAL_MODE);
		d.setDrawPosition(x, y, z);
		d.performDraw();
		
	}
	
	public float getBottom() {
		return this.y - this.hh;
	}
	
	public float getShadowY() {
		return this.y - (this.hh * (1f - Drawinator.SHADOW_SCALE));
	}
	
	public float getShadowX() {
		return this.x + (this.hw * 0.7f);
	}
	
	public List<ConvexPolygon> getBlockers() {
		
		return blockers;

	}

	@Override
	public void move(float dx, float dy) {
		// TODO let these move someday
	}

	@Override
	public boolean checkMovement() {
		// TODO return true if these move
		return false;
	}

	@Override
	public void update(long delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean remove() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
