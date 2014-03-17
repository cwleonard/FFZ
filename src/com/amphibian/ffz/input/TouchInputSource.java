package com.amphibian.ffz.input;

import java.util.ArrayList;
import java.util.List;

import com.amphibian.ffz.FrogPath;

public class TouchInputSource implements InputSource {

	private final static float TLEN = 500;
	
	private float timer = 0.0f;
	private boolean pressed = false;
	private boolean going = false;
	private List<FrogPath> paths = new ArrayList<FrogPath>();
	
	public void setPath(FrogPath p) {
		this.paths.add(p);
		this.going = true;
	}
	
	public void addToPath(FrogPath p) {
		if (this.going) { // if we're already going, start over
			this.paths.clear();
			this.going = false;
		}
		this.paths.add(p);
	}
	
	public FrogPath getFirstPath() {
		if (paths.size() > 0) {
			return paths.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public float getStickX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getStickY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isButton1Pressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isButton2Pressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isButton3Pressed() {
		return pressed;
	}

	@Override
	public boolean isButton4Pressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getStick2X() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getStick2Y() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isLeftTriggerPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void update(float delta) {
		
		this.timer += delta;
		if (this.timer >= TLEN) {
			this.pressed = false;
		}
		
	}
	
	@Override
	public float[] getMovement(float speed, float delta) {
		
		if (going && paths.size() > 0 && paths.get(0) != null) {
			float[] m = paths.get(0).getDeltaToNextPoint(speed * delta);
			if (paths.get(0).isDone()) {
				paths.remove(0);
			}
			return m;
		} else {
			return new float[]{0, 0};
		}
		
	}

	public void buttonPress() {
		this.pressed = true;
		this.timer = 0.0f;
	}
	
}
