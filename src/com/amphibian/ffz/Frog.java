package com.amphibian.ffz;

import com.amphibian.ffz.geometry.ConvexPolygon;

public class Frog implements Sprite {

	private static float BASE_SPEED = 0.3f;
	
	float x = 50f;
	float y = -50f;
	
	private FrogPath fp;
	



    private int sprite = 6;
    
	 

    public Frog() {
    	
        
        
    }
    
    public ConvexPolygon getBlocker(float x, float y) {
    	
    	float[] c = { x, y };
    	float[] p = { -50, 50, 50, 50, 50, -50, -50, -50 };
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
    	this.sprite = 6;
    }
    
    public void faceRight() {
    	this.sprite = 8;
    }
    
    public void faceLeft() {
    	this.sprite = 7;
    }
    
    public void faceUp() {
    	this.sprite = 9;
    }

	@Override
	public int getVertexPosition() {
		return sprite-6;
	}

	@Override
	public int getTexturePosition() {
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
    
}