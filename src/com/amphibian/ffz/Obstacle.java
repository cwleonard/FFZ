package com.amphibian.ffz;

import java.util.ArrayList;
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
		if (id == -1) {
			Frame f = FrameDataManager.getInstance().getFrame(type);
			this.id = f.getIndex();
			this.w = f.getWidth();
			this.h = f.getHeight();
			this.hh = h / 2.0f;
		}
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
	
	public List<ConvexPolygon> getBlockers() {
		
		FrameDataManager fdm = FrameDataManager.getInstance();
		
		
		
    	float[] c = { x, y - 12.5f };
    	float[] p = { -50, 25, 50, 25, 50, -25, -50, -25 };
    	List<ConvexPolygon> b = new ArrayList<ConvexPolygon>();
    	b.add(new ConvexPolygon(c, p));
    	return b;

	}

}
