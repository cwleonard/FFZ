package com.amphibian.ffz;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.amphibian.ffz.geometry.ConvexPolygon;

public class Rabbit implements Sprite {

	public final static int UP = 0;
	public final static int DOWN = 1;
	public final static int LEFT = 2;
	public final static int RIGHT = 3;
	
	private static float BASE_SPEED = 0.35f;
	
	private static float MAX_TRAVEL = 1000f;
	
	private float life;
	
	float x =  800f;
	float y = -500f;

    private int sprite;
    
    private int direction;
    
    private Engine engine;
    
    private ConvexPolygon mainBlocker;
    private List<ConvexPolygon> blockPolys;
    
	private static int FACE_RIGHT;
	private static int FACE_LEFT;
	
	private int[] frames;
	
	private static int[] rightFrames;
	private static int[] leftFrames;
	
	private long elapsed;
	private boolean moving = false;
	private int frameIndex = 0;
	private float distanceMoved = 0f;
	
	private boolean dead = false;

	public static void init() {
		
		FrameDataManager fdm = FrameDataManager.getInstance();
		FACE_RIGHT = fdm.getFrameIndex("rabbit_right");
		FACE_LEFT = fdm.getFrameIndex("rabbit_left");
		
		rightFrames = new int[] { FACE_RIGHT };
		leftFrames = new int[] { FACE_LEFT };
		
	}

    public Rabbit() {
    	
    	this.faceRight();
    	this.initBlocker();
    	this.life = 3f;
        
    }
    
    private void initBlocker() {

    	float[] p = { -50, 25, 50, 25, 50, -25, -50, -25 };
    	blockPolys = new ArrayList<ConvexPolygon>();
    	mainBlocker = new ConvexPolygon(p, x, y - 12.5f);
    	mainBlocker.setOwner(this);
    	blockPolys.add(mainBlocker);

    }
    
    public void hurt() {
    	this.life -= 0.1f;
    	if (this.life <= 0.0f) {
    		this.dead = true;
    	}
    	Log.d("ffz", "rabbit hurt!");
    }
    
	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	public void update(long delta) {
		
		if (dead) return;
		
		float[] m = new float[2];

		float move = delta * BASE_SPEED;
		
		if (this.direction == LEFT) {
			m[0] = -move;
		} else if (this.direction == RIGHT) {
			m[0] = move;
		}
		m[1] = 0f;
		
		
		distanceMoved += move;
		if (distanceMoved >= MAX_TRAVEL) {
			distanceMoved = 0f;
			if (this.direction == LEFT) {
				faceRight();
			} else if (this.direction == RIGHT) {
				faceLeft();
			}
		}
		
		this.move(m[0], m[1]);
		
	}

	public boolean remove() {
		return this.dead;
	}

	public void setDirection(float x, float y) {

		int oldDirection = this.getDirection();
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
		if (this.getDirection() != oldDirection) {
			this.frameIndex = 0;
		}
		
	}
	
	public void move(float dx, float dy) {
    	this.x += dx;
    	this.y += dy;
    	this.mainBlocker.move(dx, dy);
    }
    
    public void faceDown() {
    	this.direction = DOWN;
    }
    
    public void faceRight() {
    	this.direction = RIGHT;
    	this.frames = rightFrames;
    	this.direction = RIGHT;
    }
    
    public void faceLeft() {
    	this.sprite = FACE_LEFT;
    	this.frames = leftFrames;
    	this.direction = LEFT;
    }
    
    public void faceUp() {
    	this.direction = UP;
    }
    
    public int getDirection() {
    	return this.direction;
    }

	public int getBufferIndex() {
		return frames[frameIndex];
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

		float z = -1f - (this.getBottom() / 999999f);

		
		d.setBufferPosition(this.getBufferIndex());
		
		d.setDrawPosition(this.getShadowX(), this.getShadowY(), z - 0.00001f);
		d.setMode(Drawinator.SHADOW_MODE);
		d.performDraw();
		
		d.setMode(Drawinator.NORMAL_MODE);
		d.setDrawPosition(this.getDrawX(), this.getDrawY(), z);
		d.performDraw();
		

	}
	
	public float getBottom() {
		return y - 50f;
	}
	
	public float getShadowX() {
		return this.x + 25f;
	}
	
	public float getShadowY() {
		return this.y - (25f * (1f - Drawinator.SHADOW_SCALE));
	}
	
	public List<ConvexPolygon> getBlockers() {
		
    	return blockPolys;

	}

	@Override
	public boolean checkMovement() {
		return true;
	}
	

    
}