package com.amphibian.ffz;

import android.util.Log;

import com.amphibian.ffz.geometry.ConvexPolygon;

public class Frog implements Sprite {

	private static float BASE_SPEED = 0.3f;
	
	float x = 50f;
	float y = -50f;

	private FrogPath fp;

    private int sprite;
    
	private static int SIT_FACE_RIGHT;
	private static int SIT_FACE_LEFT;
	private static int SIT_FACE_DOWN;
	private static int SIT_FACE_UP;

	public static void init() {
		
		FrameDataManager fdm = FrameDataManager.getInstance();
		SIT_FACE_RIGHT = fdm.getFrameIndex("sitting_right");
		SIT_FACE_LEFT = fdm.getFrameIndex("sitting_left");
		SIT_FACE_DOWN = fdm.getFrameIndex("sitting_down");
		SIT_FACE_UP = fdm.getFrameIndex("sitting_up");
		
		Log.i("ffz", "SIT_FACE_RIGHT = " + SIT_FACE_RIGHT);
		
	}

    public Frog() {
    	
    	sprite = SIT_FACE_RIGHT;
        
    }
    
    public ConvexPolygon getBlocker(float x, float y) {
    	
    	float[] c = { x, y - 12.5f };
    	float[] p = { -50, 25, 50, 25, 50, -25, -50, -25 };
    	return new ConvexPolygon(c, p);
    	
    }
    
	public void setFrogPath(FrogPath fp) {
		this.fp = fp;
	}
	
	public float[] getMovement(long delta, float stickX, float stickY) {
		
		float[] m = new float[2];
		if (fp != null) { // this frog has a path set, follow it!
			
			float dist = delta * BASE_SPEED;
			float[] cp = new float[2];
			cp[0] = this.x;
			cp[1] = this.y;
			float[] p = fp.getNextPoint(cp, dist);
			
			m[0] = p[0]-this.x;
			m[1] = p[1]-this.y;
			
			if (p[2] == 1f) {
				fp = null;
			}
			
		} else { // we might have a joystick
			
			m[0] = delta * BASE_SPEED * stickX;
			m[1] = delta * BASE_SPEED * stickY;
			
		}
		return m;
		
	}
	
	public void update(long delta) {
		
		//TODO: update something
		
	}

	public void setDirection(float x, float y) {

		if (Math.abs(x) > Math.abs(y)) {
			if (x < 0) {
				this.faceLeft();
			}
			if (x > 0) {
				this.faceRight();
			}
		} else {
			if (y > 0) {
				this.faceUp();
			}
			if (y < 0) {
				this.faceDown();
			}
		}
		
	}
	
	public void move(float x, float y) {
    	this.x += x;
    	this.y += y;
    }
    
    public void faceDown() {
    	this.sprite = SIT_FACE_DOWN;
    }
    
    public void faceRight() {
    	this.sprite = SIT_FACE_RIGHT;
    }
    
    public void faceLeft() {
    	this.sprite = SIT_FACE_LEFT;
    }
    
    public void faceUp() {
    	this.sprite = SIT_FACE_UP;
    }

	@Override
	public int getBufferIndex() {
		return sprite;
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
		return y - 25f;
	}
	
	public float getShadowY() {
		return this.y - (25f * (1f - Drawinator.SHADOW_SCALE));
	}

    
}