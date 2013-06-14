package com.amphibian.ffz;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.amphibian.ffz.geometry.ConvexPolygon;

public class Tongue implements Sprite {

	private static float OFFSET = 23.0f;
	
	private static int END_TONGUE_RIGHT;
	private static int END_TONGUE_LEFT;
	private static int END_TONGUE_UP;
	private static int END_TONGUE_DOWN;
	private static int TONGUE_PART_UD;
	private static int TONGUE_PART_LR;

	private float x;
	private float y;
	
	private static float pw;
	private static float ew;
	private static float ph;
	private static float eh;
	
	private Frog frog;
	
	public static void init() {
		
		FrameDataManager fdm = FrameDataManager.getInstance();
		END_TONGUE_RIGHT = fdm.getFrameIndex("end_tongue_right");
		END_TONGUE_LEFT = fdm.getFrameIndex("end_tongue_left");
		END_TONGUE_UP = fdm.getFrameIndex("end_tongue_up");
		END_TONGUE_DOWN = fdm.getFrameIndex("end_tongue_down");
		TONGUE_PART_LR = fdm.getFrameIndex("middle_tongue_lr");
		TONGUE_PART_UD = fdm.getFrameIndex("middle_tongue_ud");
		
		pw = fdm.getFrame("middle_tongue_lr").getWidth();
		ew = fdm.getFrame("end_tongue_right").getWidth();
		ph = fdm.getFrame("middle_tongue_lr").getHeight();
		eh = fdm.getFrame("end_tongue_right").getHeight();
		
		
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
		return END_TONGUE_RIGHT;
	}
	
	@Override
	public float getDrawX() {
		
		float fx = frog.getDrawX();
		if (frog.getDirection() == Frog.LEFT) {
			return fx - OFFSET;
		} else if (frog.getDirection() == Frog.RIGHT) {
			return fx + OFFSET;
		} else {
			return fx;
		}
		
	}
	
	@Override
	public float getDrawY() {
		return frog.getDrawY();
	}
	
	@Override
	public void draw(Drawinator d) {
		
		float m = 1.0f;
		float n = 1.0f;
		if (frog.getDirection() == Frog.LEFT) {
			d.setBufferPosition(TONGUE_PART_LR);
			m = -1.0f;
			n = 0.0f;
		} else if (frog.getDirection() == Frog.RIGHT) {
			d.setBufferPosition(TONGUE_PART_LR);
			n = 0.0f;
		} else if (frog.getDirection() == Frog.DOWN) {
			d.setBufferPosition(TONGUE_PART_UD);
			m = 0.0f;
			n = -1.0f;
		} else if (frog.getDirection() == Frog.UP) {
			d.setBufferPosition(TONGUE_PART_UD);
			m = 0.0f;
		}
		
//		d.setDrawPosition(this.getShadowX(), this.getShadowY());
//		d.setMode(Drawinator.SHADOW_MODE);
//		d.performDraw();
		
		d.setMode(Drawinator.NORMAL_MODE);

		float th = 0;
		float tw = 0;
		for (int i = 0; i < 10; i++) {
		
			d.setDrawPosition(this.getDrawX() + tw, this.getDrawY() + th);
			d.performDraw();
			tw += (pw * m);
			th += (ph * n);
			
		}

		if (frog.getDirection() == Frog.LEFT) {
			d.setBufferPosition(END_TONGUE_LEFT);
		} else if (frog.getDirection() == Frog.RIGHT) {
			d.setBufferPosition(END_TONGUE_RIGHT);
		} else if (frog.getDirection() == Frog.UP) {
			d.setBufferPosition(END_TONGUE_UP);
		} else if (frog.getDirection() == Frog.DOWN) {
			d.setBufferPosition(END_TONGUE_DOWN);
		}
		d.setDrawPosition(this.getDrawX() + tw - (pw*m) + (ew*m), this.getDrawY() + th - (ph*n) + (eh*n));
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
