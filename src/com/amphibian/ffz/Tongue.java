package com.amphibian.ffz;

import java.util.ArrayList;
import java.util.List;

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
	
	private static float lr_part_w;
	private static float lr_part_h;
	private static float ud_part_w;
	private static float ud_part_h;
	private static float lr_end_w;
	private static float lr_end_h;
	private static float ud_end_w;
	private static float ud_end_h;
	
	private Frog frog;
	
	public static void init() {
		
		FrameDataManager fdm = FrameDataManager.getInstance();
		END_TONGUE_RIGHT = fdm.getFrameIndex("end_tongue_right");
		END_TONGUE_LEFT = fdm.getFrameIndex("end_tongue_left");
		END_TONGUE_UP = fdm.getFrameIndex("end_tongue_up");
		END_TONGUE_DOWN = fdm.getFrameIndex("end_tongue_down");
		TONGUE_PART_LR = fdm.getFrameIndex("middle_tongue_lr");
		TONGUE_PART_UD = fdm.getFrameIndex("middle_tongue_ud");
		
		lr_part_w = fdm.getFrame("middle_tongue_lr").getWidth();
		lr_part_h = fdm.getFrame("middle_tongue_lr").getHeight();
		ud_part_w = fdm.getFrame("middle_tongue_ud").getWidth();
		ud_part_h = fdm.getFrame("middle_tongue_ud").getHeight();
		lr_end_w = fdm.getFrame("end_tongue_right").getWidth();
		lr_end_h = fdm.getFrame("end_tongue_right").getHeight();
		ud_end_w = fdm.getFrame("end_tongue_up").getWidth();
		ud_end_h = fdm.getFrame("end_tongue_up").getHeight();
		
		
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
		
		float pw = 0.0f, ph = 0.0f;
		float ew = 0.0f, eh = 0.0f; 
		float m = 1.0f;
		float n = 1.0f;
		if (frog.getDirection() == Frog.LEFT) {
			d.setBufferPosition(TONGUE_PART_LR);
			pw = lr_part_w;
			ph = lr_part_h;
			ew = lr_end_w;
			eh = lr_end_h;
			m = -1.0f;
			n = 0.0f;
		} else if (frog.getDirection() == Frog.RIGHT) {
			d.setBufferPosition(TONGUE_PART_LR);
			pw = lr_part_w;
			ph = lr_part_h;
			ew = lr_end_w;
			eh = lr_end_h;
			n = 0.0f;
		} else if (frog.getDirection() == Frog.DOWN) {
			d.setBufferPosition(TONGUE_PART_UD);
			pw = ud_part_w;
			ph = ud_part_h;
			ew = ud_end_w;
			eh = ud_end_h;
			m = 0.0f;
			n = -1.0f;
		} else if (frog.getDirection() == Frog.UP) {
			d.setBufferPosition(TONGUE_PART_UD);
			pw = ud_part_w;
			ph = ud_part_h;
			ew = ud_end_w;
			eh = ud_end_h;
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

	@Override
	public void move(float dx, float dy) {
		// TODO let these move someday
	}

	@Override
	public boolean checkMovement() {
		// TODO return true sometimes
		return false;
	}

	@Override
	public void update(long delta) {
		// TODO Auto-generated method stub
		
	}
	

}
