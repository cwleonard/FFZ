package com.amphibian.ffz;

import java.util.ArrayList;
import java.util.List;

import com.amphibian.ffz.geometry.ConvexPolygon;

public class Frog implements Sprite {

	public final static int UP = 0;
	public final static int DOWN = 1;
	public final static int LEFT = 2;
	public final static int RIGHT = 3;
	
	private static float BASE_SPEED = 0.3f;
	
	float x = 50f;
	float y = -50f;

	private FrogPath fp;

    private int sprite;
    
    private int direction;
    
	private static int SIT_FACE_RIGHT;
	private static int SIT_FACE_LEFT;
	private static int SIT_FACE_DOWN;
	private static int SIT_FACE_UP;
	private static int JUMPING_LEFT_1;
	private static int JUMPING_LEFT_2;
	private static int JUMPING_RIGHT_1;
	private static int JUMPING_RIGHT_2;
	private static int OPEN_MOUTH_RIGHT;
	private static int OPEN_MOUTH_LEFT;
	private static int OPEN_MOUTH_DOWN;
	
	private int[] frames;
	
	private static int[] rightFrames;
	private static int[] leftFrames;
	
	private long elapsed;
	private boolean moving = false;
	private boolean ribbit = false;
	private int frameIndex = 0;

	private Tongue t;
	
	public static void init() {
		
		FrameDataManager fdm = FrameDataManager.getInstance();
		SIT_FACE_RIGHT = fdm.getFrameIndex("sitting_right");
		SIT_FACE_LEFT = fdm.getFrameIndex("sitting_left");
		SIT_FACE_DOWN = fdm.getFrameIndex("sitting_down");
		SIT_FACE_UP = fdm.getFrameIndex("sitting_up");
		JUMPING_LEFT_1 = fdm.getFrameIndex("jumping_left_1");
		JUMPING_LEFT_2 = fdm.getFrameIndex("jumping_left_2");
		JUMPING_RIGHT_1 = fdm.getFrameIndex("jumping_right_1");
		JUMPING_RIGHT_2 = fdm.getFrameIndex("jumping_right_2");
		OPEN_MOUTH_RIGHT = fdm.getFrameIndex("open_mouth_right");
		OPEN_MOUTH_LEFT = fdm.getFrameIndex("open_mouth_left");
		OPEN_MOUTH_DOWN = fdm.getFrameIndex("open_mouth_down");
		
		rightFrames = new int[] { JUMPING_RIGHT_1, JUMPING_RIGHT_2, SIT_FACE_RIGHT };
		leftFrames = new int[] { JUMPING_LEFT_1, JUMPING_LEFT_2, SIT_FACE_LEFT };
		
		Tongue.init();
		
	}

    public Frog() {
    	
    	this.faceRight();
    	t = new Tongue(this);
        
    }
    
    public ConvexPolygon getBlocker(float x, float y) {
    	
    	float[] p = { -50, 25, 50, 25, 50, -25, -50, -25 };
    	return new ConvexPolygon(p, x, y - 12.5f);
    	
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
		
		if (Math.abs(m[0]) > 0.0f || Math.abs(m[1]) > 0.0f) {
			this.moving = true;
		} else {
			this.moving = false;
			frameIndex = 0;
		}
		
		elapsed += delta;
		if (elapsed > 200) {
			if (moving) {
				frameIndex++;
				if (frameIndex == frames.length) {
					frameIndex = 0;
				}
			}
			elapsed -= 200;
		}
		
		return m;
		
	}
	
	public void ribbit(boolean b) {
		this.ribbit = b;
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
    	this.direction = DOWN;
    }
    
    public void faceRight() {
    	this.sprite = SIT_FACE_RIGHT;
    	this.frames = rightFrames;
    	this.direction = RIGHT;
    }
    
    public void faceLeft() {
    	this.sprite = SIT_FACE_LEFT;
    	this.frames = leftFrames;
    	this.direction = LEFT;
    }
    
    public void faceUp() {
    	this.sprite = SIT_FACE_UP;
    	this.direction = UP;
    }
    
    public int getDirection() {
    	return this.direction;
    }

	@Override
	public int getBufferIndex() {
		if (!moving && !ribbit) {
			return sprite;
		} else if (ribbit) {
			if (this.sprite == SIT_FACE_RIGHT) {
				return OPEN_MOUTH_RIGHT;
			} else if (this.sprite == SIT_FACE_LEFT) {
				return OPEN_MOUTH_LEFT;
			} else if (this.sprite == SIT_FACE_DOWN) {
				return OPEN_MOUTH_DOWN;
			} else {
				return sprite;
			}
		} else {
			return frames[frameIndex];
		}
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
		
		d.setBufferPosition(this.getBufferIndex());
		
		d.setDrawPosition(this.getShadowX(), this.getShadowY());
		d.setMode(Drawinator.SHADOW_MODE);
		d.performDraw();
		
		d.setMode(Drawinator.NORMAL_MODE);
		d.setDrawPosition(this.getDrawX(), this.getDrawY());
		d.performDraw();
		
		if (this.ribbit) {
			t.draw(d);
		}

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
		
    	List<ConvexPolygon> b = new ArrayList<ConvexPolygon>();
    	b.add(this.getBlocker(x, y));
    	return b;

	}
	

    
}