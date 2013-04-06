package com.amphibian.ffz;

public class FrogPath {

	private float x1;
	
	private float x2;
	
	private float y1;
	
	private float y2;
	
	private float slope;
	
	private float yIntercept;
	
	private float distMoved = 0f;
	
	private float dist = 0f;
	
	public void setStart(float x, float y) {
		this.x1 = x;
		this.y1 = y;
	}
	
	public void setEnd(float x, float y) {
		
		this.x2 = x;
		this.y2 = y;
		calculateSlope();
		
		float a = (x1 - x2);
		float b = (y1 - y2);
		
		dist = (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
		
	}
	
	public float getDeltaX() {
		return (x1 - x2);
	}
	
	public float getDeltaY() {
		return (y1 - y2);
	}
	
	private void calculateSlope() {
		if (x1 - x2 == 0) { // don't divide by 0! no!
			slope = 1;
			return;
		}
		slope = (y1 - y2) / (x1 - x2);
	}

	private float yIntercept(float x, float y, float m) {
		return (y - (m * x));
	}
	
	public float[] getNextPoint(float[] cp, float dist) {
		
		float[] ret = new float[3];
		
		float b = yIntercept(cp[0], cp[1], slope);
		
		float xp = 0f;
		if (x2 < x1) {
			xp = (float) (cp[0] - (dist / (Math.sqrt(1+Math.pow(slope, 2)))));
		} else {
			xp = (float) (cp[0] + (dist / (Math.sqrt(1+Math.pow(slope, 2)))));
		}
		float yp = (slope * xp) + b;
		
		ret[0] = xp;
		ret[1] = yp;
		
		distMoved += dist;
		if (distMoved >= this.dist) {
			ret[2] = 1f;
		}
		
		return ret;
		
	}

	public float getSlope() {
		return slope;
	}

	public float getIntercept() {
		return yIntercept;
	}

	
	
}
