package com.amphibian.ffz;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Viewport {

	private final static float SPEED = 0.4f;
	
	private int height;
	
	private int width;
	
	private int areaWidth;
	private int areaHeight;
	
    private InputSource inputSource;

	private float[] cameraCoords = new float[2];
	
    private final float[] projMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    
    private Sprite follow;
    
    private int startAtY;
    private int startAtX;
    private int stopAtX;
    private int stopAtY;

    public Viewport(int h, int w) {
    	
    	this.height = h;
    	this.width = w;
    	
    	this.areaHeight = 2*h;
    	this.areaWidth= 2*w;
    	
    	startAtY = height / 2;
    	startAtX = width / 2;
    	
    	stopAtY = areaHeight - startAtY;
		stopAtX = areaWidth - startAtX;

    	
    	cameraCoords[0] = w / 2;
    	cameraCoords[1] = h / 2;
    	
		GLES20.glViewport(0, 0, w, h);
		
		//float ratio = (float)w/h;
		
		Matrix.orthoM(projMatrix, 0,
				0,   // left
				w,   // right
				-h,  // bottom
				0,   // top
				-1,  // near
				2);  // far

		// Set the camera position (View matrix)
		Matrix.setLookAtM(viewMatrix, 0,
	    		0.0f, 0.0f, 1.0f, // eye
	    		0.0f, 0.0f, 0.0f,  // center
	    		0.0f, 1.0f, 0.0f); // up
    	
    	
    }

	public int getAreaWidth() {
		return areaWidth;
	}

	public void setAreaWidth(int areaWidth) {
		this.areaWidth = areaWidth;
		stopAtX = areaWidth - startAtX;
	}

	public int getAreaHeight() {
		return areaHeight;
	}

	public void setAreaHeight(int areaHeight) {
		this.areaHeight = areaHeight;
    	stopAtY = areaHeight - startAtY;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public float[] getProjMatrix() {
		return projMatrix;
	}

	public float[] getViewMatrix() {
		return viewMatrix;
	}

	public float[] getCameraCoords() {
		return cameraCoords;
	}

	public void update(long delta) {
		
		if (inputSource != null) {

			float dx = delta * SPEED * this.inputSource.getStick2X();
			float dy = delta * SPEED * this.inputSource.getStick2Y();

			moveCamera(dx, dy);
			
		}
		
	}
	
    public void moveCamera(float x, float y) {
    	cameraCoords[0] += x;
    	cameraCoords[1] += y;
    	Matrix.translateM(viewMatrix, 0, -x, y, 0);
    }

	public InputSource getInputSource() {
		return inputSource;
	}

	public void setInputSource(InputSource inputSource) {
		this.inputSource = inputSource;
	}

	public Sprite getFollow() {
		return follow;
	}

	public void setFollow(Sprite follow) {
		this.follow = follow;
	}

	/**
	 * If a follow Sprite is set, center the camera on it
	 */
	public void center() {
		
		if (follow != null) {
			
			float cMoveX = 0f;
			float cMoveY = 0f;

			float fY = -follow.getDrawY();
			float fX = follow.getDrawX();
			
			if (fY > startAtY && fY < stopAtY) {
				if (fY > cameraCoords[1]) {
					cMoveY = fY - cameraCoords[1];
				} else if (cameraCoords[1] > (height/2)) {
					cMoveY = -cameraCoords[1] + fY;
				}
			}
			
			if (fX > startAtX && fX < stopAtX) {
				if (fX > cameraCoords[0]) {
					cMoveX = fX - cameraCoords[0];
				} else if (cameraCoords[0] > (width/2)) {
					cMoveX = -(cameraCoords[0] - fX);
				}
			}
			
			moveCamera(cMoveX, cMoveY);
			
		}
		
	}
}
