package com.amphibian.ffz;

public class TouchInputSource implements InputSource {

	private Sprite sprite;
	
	public TouchInputSource(Sprite s) {
		sprite = s;
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
		// TODO Auto-generated method stub
		return false;
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

}
