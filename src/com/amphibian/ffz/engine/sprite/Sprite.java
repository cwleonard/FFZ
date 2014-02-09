package com.amphibian.ffz.engine.sprite;

import java.util.List;

import com.amphibian.ffz.engine.layers.SpriteLayer;
import com.amphibian.ffz.geometry.ConvexPolygon;

public interface Sprite {

	public int getProperties();
	
	public float getDrawX();
	
	public float getDrawY();
	
	public void draw(SpriteLayer d);
	
	public float getBottom();
	
	public void move(float dx, float dy);

	public boolean checkMovement();
	
	public float getShadowX();
	public float getShadowY();
	
	public List<ConvexPolygon> getBlockers();
	
	public void update(long delta);
	
	public void hurt();
	
	public boolean remove();
	
}
