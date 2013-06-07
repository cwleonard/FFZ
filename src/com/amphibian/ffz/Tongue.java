package com.amphibian.ffz;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.amphibian.ffz.geometry.ConvexPolygon;

public class Tongue implements Sprite {

	private static float OFFSET = 23.0f;
	
	private static int END_TONGUE;
	private static int TONGUE;

	private float x;
	private float y;
	
	private static float w;
	private static float h;
	
	private Frog frog;
	
	public static void init() {
		
		FrameDataManager fdm = FrameDataManager.getInstance();
		END_TONGUE = fdm.getFrameIndex("end_tounge");
		TONGUE = fdm.getFrameIndex("middle_tounge");
		
		w = fdm.getFrame("end_tounge").getWidth();
		h = fdm.getFrame("end_tounge").getHeight();
		
		
	}
	
	public Tongue(Frog f) {
		
		frog = f;

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
		return END_TONGUE;
	}
	
	@Override
	public float getDrawX() {
		
		return frog.getDrawX() + OFFSET;
		
	}
	
	@Override
	public float getDrawY() {
		return frog.getDrawY();
	}
	@Override
	public void draw(Drawinator d) {
		
		d.setBufferPosition(TONGUE);
		
//		d.setDrawPosition(this.getShadowX(), this.getShadowY());
//		d.setMode(Drawinator.SHADOW_MODE);
//		d.performDraw();
		
		d.setMode(Drawinator.NORMAL_MODE);

		float tw = 0;
		for (int i = 0; i < 10; i++) {
		
			d.setDrawPosition(this.getDrawX() + tw, this.getDrawY());
			d.performDraw();
			tw += w;
			
		}

		d.setBufferPosition(END_TONGUE);
		d.setDrawPosition(this.getDrawX() + tw, this.getDrawY());
		d.performDraw();

		
	}
	
	public float getBottom() {
		return this.y;
	}
	
	public float getShadowY() {
		return this.y;
	}
	
	public float getShadowX() {
		return this.x;
	}
	
	public List<ConvexPolygon> getBlockers() {
		
		return  new ArrayList<ConvexPolygon>();

	}

}
