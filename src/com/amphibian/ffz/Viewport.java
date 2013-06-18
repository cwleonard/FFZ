package com.amphibian.ffz;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Viewport {

	private final static float SPEED = 0.4f;
	
	private int height;
	
	private int width;
	
    private InputSource inputSource;

	private float[] cameraCoords = new float[2];
	
    private final float[] projMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    public Viewport(int h, int w) {
    	
    	this.height = h;
    	this.width = w;
    	
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
	
}
