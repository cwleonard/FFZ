package com.amphibian.ffz.input;

public interface InputSource {

	public float[] getMovement(float speed, float delta);
	
	public float getStickX();
	
	public float getStickY();
	
	public boolean isButton1Pressed();
	
	public boolean isButton2Pressed();

	public boolean isButton3Pressed();

	public boolean isButton4Pressed();
	
	public boolean isLeftTriggerPressed();

	public float getStick2X();
	
	public float getStick2Y();
	
	public void update(float delta);
	
}
