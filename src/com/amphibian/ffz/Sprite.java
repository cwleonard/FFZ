package com.amphibian.ffz;

import java.util.List;

import com.amphibian.ffz.geometry.ConvexPolygon;

public interface Sprite {

	public int getBufferIndex();
	
	public float getDrawX();
	
	public float getDrawY();
	
	public void draw(Drawinator d);
	
	public float getBottom();
	
	public float getShadowX();
	public float getShadowY();
	
	public List<ConvexPolygon> getBlockers();
	
}
