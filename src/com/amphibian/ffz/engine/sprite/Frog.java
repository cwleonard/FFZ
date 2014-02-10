package com.amphibian.ffz.engine.sprite;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.amphibian.ffz.engine.Engine;
import com.amphibian.ffz.engine.layers.SpriteLayer;
import com.amphibian.ffz.geometry.ConvexPolygon;
import com.amphibian.ffz.input.InputSource;

public class Frog implements Sprite {

	public final static int UP = 0;
	public final static int DOWN = 1;
	public final static int LEFT = 2;
	public final static int RIGHT = 3;
	
	private static float BASE_SPEED = 0.25f;
	
	private final static long HURT_LENGTH = 200;

	private float moisture;
	private float life;
	
	private int mode = 0;
	
	float x =  50f;
	float y = -50f;

    private int sprite;
    
    private int direction;
    
    private Engine engine;
    
    private ConvexPolygon mainBlocker;
    private List<ConvexPolygon> blockPolys;
    
    private InputSource inputSource;
    
	private static int SIT_FACE_RIGHT;
	private static int SIT_FACE_LEFT;
	private static int SIT_FACE_DOWN;
	private static int SIT_FACE_UP;
	private static int JUMPING_UP;
	private static int JUMPING_DOWN;
	private static int JUMPING_LEFT_1;
	private static int JUMPING_LEFT_2;
	private static int JUMPING_RIGHT_1;
	private static int JUMPING_RIGHT_2;
	private static int OPEN_MOUTH_RIGHT;
	private static int OPEN_MOUTH_LEFT;
	private static int OPEN_MOUTH_DOWN;

	private static int SIT_FACE_RIGHT_SCI;
	private static int SIT_FACE_LEFT_SCI;
	private static int SIT_FACE_DOWN_SCI;
	private static int SIT_FACE_UP_SCI;
	private static int JUMPING_UP_SCI;
	private static int JUMPING_DOWN_SCI;
	private static int JUMPING_LEFT_1_SCI;
	private static int JUMPING_LEFT_2_SCI;
	private static int JUMPING_RIGHT_1_SCI;
	private static int JUMPING_RIGHT_2_SCI;
	private static int OPEN_MOUTH_RIGHT_SCI;
	private static int OPEN_MOUTH_LEFT_SCI;
	private static int OPEN_MOUTH_DOWN_SCI;
	
	
	private static int WATER_FACE_RIGHT;
	private static int WATER_FACE_LEFT;
	private static int WATER_FACE_DOWN;
	private static int WATER_FACE_UP;

	private int[] frames;
	
	private static int[] rightFrames;
	private static int[] leftFrames;
	private static int[] upFrames;
	private static int[] downFrames;
	
	private static int[] rightFramesW;
	private static int[] leftFramesW;
	private static int[] upFramesW;
	private static int[] downFramesW;

	private static int[] rightFramesSci;
	private static int[] leftFramesSci;
	private static int[] upFramesSci;
	private static int[] downFramesSci;
	
	private boolean dead = false;
	private long hurtTimer = 0;
	private boolean hurting = false;

	private long elapsed;
	private boolean moving = false;
	private int frameIndex = 0;

	private Tongue t;
	private boolean om = false;
	private boolean swimming = false;
	
	public static void init() {
		
		FrameDataManager fdm = FrameDataManager.getInstance();
		SIT_FACE_RIGHT = fdm.getFrameIndex("sitting_right");
		SIT_FACE_LEFT = fdm.getFrameIndex("sitting_left");
		SIT_FACE_DOWN = fdm.getFrameIndex("sitting_down");
		SIT_FACE_UP = fdm.getFrameIndex("sitting_up");
		JUMPING_UP = fdm.getFrameIndex("frog_jumping_up");
		JUMPING_DOWN = fdm.getFrameIndex("frog_jumping_down");
		JUMPING_LEFT_1 = fdm.getFrameIndex("jumping_left_1");
		JUMPING_LEFT_2 = fdm.getFrameIndex("jumping_left_2");
		JUMPING_RIGHT_1 = fdm.getFrameIndex("jumping_right_1");
		JUMPING_RIGHT_2 = fdm.getFrameIndex("jumping_right_2");
		OPEN_MOUTH_RIGHT = fdm.getFrameIndex("open_mouth_right");
		OPEN_MOUTH_LEFT = fdm.getFrameIndex("open_mouth_left");
		OPEN_MOUTH_DOWN = fdm.getFrameIndex("open_mouth_down");
		WATER_FACE_RIGHT = fdm.getFrameIndex("frog_water_right");
		WATER_FACE_LEFT = fdm.getFrameIndex("frog_water_left");
		WATER_FACE_DOWN = fdm.getFrameIndex("frog_water_down");
		WATER_FACE_UP = fdm.getFrameIndex("frog_water_up");

		SIT_FACE_RIGHT_SCI = fdm.getFrameIndex("sitting_right_science");
		SIT_FACE_LEFT_SCI = fdm.getFrameIndex("sitting_left_science");
		SIT_FACE_DOWN_SCI = fdm.getFrameIndex("sitting_down");
		SIT_FACE_UP_SCI = fdm.getFrameIndex("sitting_up_science");
		JUMPING_UP_SCI = fdm.getFrameIndex("frog_jumping_up_science");
		JUMPING_DOWN_SCI = fdm.getFrameIndex("frog_jumping_down");
		JUMPING_LEFT_1_SCI = fdm.getFrameIndex("jumping_left_1_science");
		JUMPING_LEFT_2_SCI = fdm.getFrameIndex("jumping_left_2_science");
		JUMPING_RIGHT_1_SCI = fdm.getFrameIndex("jumping_right_1_science");
		JUMPING_RIGHT_2_SCI = fdm.getFrameIndex("jumping_right_2_science");
		OPEN_MOUTH_RIGHT_SCI = fdm.getFrameIndex("open_mouth_right_science");
		OPEN_MOUTH_LEFT_SCI = fdm.getFrameIndex("open_mouth_left_science");
		OPEN_MOUTH_DOWN_SCI = fdm.getFrameIndex("open_mouth_down");
		
		rightFrames = new int[] { JUMPING_RIGHT_1, JUMPING_RIGHT_2, SIT_FACE_RIGHT };
		leftFrames = new int[] { JUMPING_LEFT_1, JUMPING_LEFT_2, SIT_FACE_LEFT };
		upFrames = new int[] { JUMPING_UP, SIT_FACE_UP };
		downFrames = new int[] { JUMPING_DOWN, SIT_FACE_DOWN };

		rightFramesW = new int[] { WATER_FACE_RIGHT };
		leftFramesW = new int[] { WATER_FACE_LEFT };
		upFramesW = new int[] { WATER_FACE_UP };
		downFramesW = new int[] { WATER_FACE_DOWN };

		upFramesSci = new int[] { JUMPING_UP_SCI, SIT_FACE_UP_SCI };
		downFramesSci = new int[] { JUMPING_DOWN_SCI, SIT_FACE_DOWN_SCI };
		rightFramesSci = new int[] { JUMPING_RIGHT_1_SCI, JUMPING_RIGHT_2_SCI, SIT_FACE_RIGHT_SCI };
		leftFramesSci = new int[] { JUMPING_LEFT_1_SCI, JUMPING_LEFT_2_SCI, SIT_FACE_LEFT_SCI };

		Tongue.init();
		
	}

    public Frog() {
    	
    	this.faceRight();
    	//t = new Tongue(this);
    	this.initBlocker();
    	this.moisture = 1.0f;
    	this.life = 3f;
        
    }
    
    private void initBlocker() {

    	float[] p = { -50, 25, 50, 25, 50, -25, -50, -25 };
    	blockPolys = new ArrayList<ConvexPolygon>();
    	mainBlocker = new ConvexPolygon(p, x, y - 12.5f);
    	blockPolys.add(mainBlocker);

    }
    
	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	public InputSource getInputSource() {
		return inputSource;
	}

	public void setInputSource(InputSource inputSource) {
		this.inputSource = inputSource;
	}

	public void update(long delta) {
		
		if (this.hurting) {
			this.hurtTimer += delta;
			if (this.hurtTimer > HURT_LENGTH) {
				this.hurting = false;
			}
		}

		
		if (inputSource != null) {
			this.ribbit(this.inputSource.isButton3Pressed());
			if (this.inputSource.isLeftTriggerPressed()) {
				this.hydrate(delta);
				if (mode == 0) {
					mode = 1;
				} else {
					mode = 0;
				}
			}
			if (t == null && !om) {
				getMovement(delta);
			}
		}
		
	}
	
	public void hydrate(long delta) {
		this.moisture += (delta * BASE_SPEED) * .003f;
		if (this.moisture > 1.0f) this.moisture = 1.0f;
	}

	private void getMovement(long delta) {
		
		float[] m = this.inputSource.getMovement(BASE_SPEED, delta);
		
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
		
		this.setDirection(m[0], m[1]);
		this.move(m[0], m[1]);
		
	}
	
	public void ribbit(boolean b) {
		om = b;
		if (t == null && b && moisture > 0f && !swimming) {
			t = new Tongue(this);
			if (engine != null) {
				engine.addSprite(t);
			}
			this.moisture -= 0.03f;
		} else if (t != null && !b) {
			if (engine != null) {
				engine.removeSprite(t);
			}
			t = null;
		}
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
    
    public float getLife() {
    	return this.life;
    }
    
    public float getMoisture() {
    	return this.moisture;
    }
    
	public void hurt(float[] vector) {
		
    	if (!this.hurting) {
    		this.hurting = true;
    		this.hurtTimer = 0;
    		this.life -= 0.25f;
    		if (this.life <= 0.0f) {
    			this.dead = true;
    		}
    		Log.d("ffz", "ouch!");
    	}
		
	}

    public void faceDown() {
    	if (swimming) {
    		this.sprite = WATER_FACE_DOWN;
    		this.frames = downFramesW;
    	} else {
    		this.sprite = (mode == 0 ? SIT_FACE_DOWN : SIT_FACE_DOWN_SCI);
    		this.frames = (mode == 0 ? downFrames : downFramesSci);
    	}
    	this.direction = DOWN;
    }
    
    public void faceRight() {
    	if (swimming) {
    		this.sprite = WATER_FACE_RIGHT;
    		this.frames = rightFramesW;
    	} else {
    		this.sprite = (mode == 0 ? SIT_FACE_RIGHT : SIT_FACE_RIGHT_SCI);
    		this.frames = (mode == 0 ? rightFrames : rightFramesSci);
    	}
    	this.direction = RIGHT;
    }
    
    public void faceLeft() {
    	if (swimming) {
    		this.sprite = WATER_FACE_LEFT;
    		this.frames = leftFramesW;
    	} else {
    		this.sprite = (mode == 0 ? SIT_FACE_LEFT : SIT_FACE_LEFT_SCI);
    		this.frames = (mode == 0 ? leftFrames : leftFramesSci);
    	}
    	this.direction = LEFT;
    }
    
    public void faceUp() {
    	if (swimming) {
    		this.sprite = WATER_FACE_UP;
    		this.frames = upFramesW;
    	} else {
    		this.sprite = (mode == 0 ? SIT_FACE_UP : SIT_FACE_UP_SCI);
    		this.frames = (mode == 0 ? upFrames : upFramesSci);
    	}
    	this.direction = UP;
    }
    
    public int getDirection() {
    	return this.direction;
    }

	public int getBufferIndex() {
		if (!moving && t == null && !om) {
			return sprite;
		} else if (t != null || om) {
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
			if (frameIndex > frames.length - 1) frameIndex = 0; // TODO: check this. I shouldn't need this check
			return frames[frameIndex];
		}
	}

	public boolean isSwimming() {
		return swimming;
	}

	public void setSwimming(boolean swimming) {
		if (!this.swimming && swimming) {
			this.frameIndex = 0;
		}
		this.swimming = swimming;
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
	public void draw(SpriteLayer d) {

		float z = -1f - (this.getBottom() / 999999f);

		
		d.setBufferPosition(this.getBufferIndex());
		
		d.setDrawPosition(this.getShadowX(), this.getShadowY(), z - 0.00001f);
		d.setMode(SpriteLayer.SHADOW_MODE);
		d.performDraw();
		
		if (this.hurting) {
			d.setMode(SpriteLayer.HURT_MODE);
		} else {
			d.setMode(SpriteLayer.NORMAL_MODE);
		}
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
		return this.y - (25f * (1f - SpriteLayer.SHADOW_SCALE));
	}
	
	public List<ConvexPolygon> getBlockers() {
		
    	return blockPolys;

	}

	@Override
	public boolean checkMovement() {
		return true;
	}

	@Override
	public boolean remove() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public int getProperties() {
		return SpriteProperties.NONHOSTILE;
	}
	
}