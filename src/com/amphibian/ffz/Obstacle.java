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
	
	public Obstacle(String t) {
		
		Frame f = FrameDataManager.getInstance().getFrame(t);
		this.type = t;
		this.id = f.getIndex();
		this.w = f.getWidth();
		this.h = f.getHeight();
		this.hh = h / 2.0f;
		this.hw = w / 2.0f;
		
	}

	public Obstacle() {
		this.id = -1;
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
	public int getBufferIndex() {
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
	@Override
	public void draw(Drawinator d) {
		
		d.setDrawPosition(x, y);
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
		
		FrameDataManager fdm = FrameDataManager.getInstance();
		return fdm.getPolygons(x, y, type);

	}

}
