package com.amphibian.ffz;

public interface Sprite {

	public int getBufferIndex();
	
	public float getDrawX();
	
	public float getDrawY();
	
	public void draw(Drawinator d);
	
	public float getBottom();
	
	public float getShadowY();
	
}
