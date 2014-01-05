package com.amphibian.ffz.geometry;

import android.util.Log;

import com.amphibian.ffz.App;
import com.amphibian.ffz.engine.sprite.Sprite;

public class ConvexPolygon {

	private final static int x = 0;
	private final static int y = 1;
	
	private float[] fpoints;
	private int numSides = 0;;
	
	private float[] center = new float[2];
	
	float[] axis = new float[2];
	float tmp, minA, maxA, minB, maxB;
	float[] smallest = new float[3];
	float overlap = 99999999f;

	public ConvexPolygon() {
		// nothing
	}
	
	private Sprite owner;
	
	/**
	 * Constructor.
	 * 
	 * @param c array representing the center. x coord is in position 0, y coord in 1.
	 * @param p array of center-relative points. every 2 elements is one point.
	 */
	public ConvexPolygon(float[] c, float[] p) {
		
		this.center[x] = c[x];
		this.center[y] = c[y];

		fpoints = new float[p.length];
		System.arraycopy(p, 0, fpoints, 0, p.length);
		numSides = p.length / 2;
		
	}

	/**
	 * Centroid-calculating constructor.
	 * 
	 * @param p array of points relative to the given offset. every 2 elements is one point.
	 * @param offsetX
	 * @param offsetY
	 */
	public ConvexPolygon(float[] p, float offsetX, float offsetY) {
		this(p, offsetX, offsetY, false);
	}

	public ConvexPolygon(float[] p, float offsetX, float offsetY, boolean b) {

		if (b) Log.i(App.name, "offset " + offsetX + ", " + offsetY);

		float cx = 0.0f;
		float cy = 0.0f;

		fpoints = new float[p.length];
		
		// create new points relative to the origin
		float[] pp = new float[p.length];
		for (int i = 0; i < p.length; i = i + 2) {
			pp[i] = p[i] + offsetX;
			pp[i+1] = p[i+1] + offsetY;
		}
		
		// find the centroid
		for (int i = 0; i < pp.length; i = i + 2) {
			if (b) Log.i(App.name, "point " + pp[i] +", " + pp[i+1]);
			cx += pp[i];
			cy += pp[i+1];
		}
		float k = (pp.length / 2.0f);
		this.center[x] = cx / k;
		this.center[y] = cy / k;
		if (b) Log.i(App.name, "centeroid at " + center[x] + ", " + center[y]);

		// create new points relative to the centroid
		int f = 0;
		for (int i = 0; i < pp.length; i = i + 2) {
			fpoints[f++] = pp[i] - this.center[x];
			fpoints[f++] = pp[i+1] - this.center[y];
		}
		numSides = p.length / 2;
	
	}
	
	public void setOwner(Sprite s) {
		this.owner = s;
	}
	
	public Sprite getOwner() {
		return this.owner;
	}

	public void move(float dx, float dy) {
		
		this.center[x] += dx;
		this.center[y] += dy;
		
	}
	
	public void rotate(float rads) {
		
		for (int i = 0; i < fpoints.length; i = i + 2) {
			fpoints[i]   = (float) (Math.cos(rads) * fpoints[i] - Math.sin(rads) * fpoints[i+1]);
			fpoints[i+1] = (float) (Math.sin(rads) * fpoints[i] + Math.cos(rads) * fpoints[i+1]);
		}
		
	}
	
	public int getNumberOfSides() {
		return numSides;
	}
	
	/**
	 * This method uses the Separating Axis Theorem to determine if this polygon
	 * is intersecting with another.
	 * 
	 * @param other
	 * @return
	 */
	public float[] intersectsWith(ConvexPolygon other) {
		
		axis[0] = 0f;
		axis[1] = 0f;
    	tmp = 0f;
    	minA = 0f;
    	maxA = 0f;
    	minB = 0f;
    	maxB = 0f;
    	smallest[0] = 0f;
    	smallest[1] = 0f;
    	smallest[2] = 0f;
    	overlap = 99999999f;
    	
    	if (this == other) return smallest;
    	
    	int myNumSides = numSides;
    	int otherNumSides = other.numSides;

    	/* test polygon A's sides */
    	for (int side = 0; side < myNumSides; side++)
    	{
    		/* get the axis that we will project onto */
    		if (side == 0)
    		{
    			axis[x] = this.fpoints[myNumSides] - this.fpoints[1];
    			axis[y] = this.fpoints[0] - this.fpoints[myNumSides - 1];
    		}
    		else
    		{
    			axis[x] = this.fpoints[((side - 1)*2) + 1] - this.fpoints[(side*2)+1];
    			axis[y] = this.fpoints[side*2] - this.fpoints[(side - 1)*2];
    		}

    		/* normalize the axis */
    		tmp = (float) Math.sqrt(axis[x] * axis[x] + axis[y] * axis[y]);
    		axis[x] /= tmp;
    		axis[y] /= tmp;

    		/* project polygon A onto axis to determine the min/max */
    		minA = this.fpoints[0] * axis[x] + this.fpoints[1] * axis[y];
    		maxA = minA;
    		for (int i = 1; i < myNumSides; i++)
    		{
    			tmp = this.fpoints[i*2] * axis[x] + this.fpoints[(i*2)+1] * axis[y];
    			if (tmp > maxA)
    				maxA = tmp;
    			else if (tmp < minA)
    				minA = tmp;
    		}

    		// correct for offset
    		tmp = this.center[x] * axis[x] + this.center[y] * axis[y];
    		minA += tmp;
    		maxA += tmp;

    		// project polygon B onto axis to determine the min/max
    		minB = other.fpoints[0] * axis[x] + other.fpoints[1] * axis[y];
    		maxB = minB;
    		for (int i = 1; i < otherNumSides; i++)
    		{
    			tmp = other.fpoints[i*2] * axis[x] + other.fpoints[(i*2)+1] * axis[y];
    			if (tmp > maxB)
    				maxB = tmp;
    			else if (tmp < minB)
    				minB = tmp;
    		}
    		
    		// correct for offset
    		tmp = other.center[x] * axis[x] + other.center[y] * axis[y];
    		minB += tmp;
    		maxB += tmp;

    		// test if lines intersect, if not, return false
    		if (maxA < minB || minA > maxB) {
    			smallest[0] = 0f;
    			smallest[1] = 0f;
    			smallest[2] = 0f;
    			return smallest;
    		} else {
    			//float o = (maxA > minB ? maxA - minB : maxB - minA);
    			float o = (maxA > maxB ? maxB - minA : maxA - minB);
			    //Log.i("ffz", "poly A side " + side + " overlap = " + o);
    			if (o < overlap) {
    				overlap = o;
    			    smallest[x] = axis[x];
    			    smallest[y] = axis[y];
    			    //Log.i("ffz", "poly A side " + side + " has smallest overlap, " + overlap);
    			    //smallest[2] = overlap + 0.001f;
    			}
    		}
    	}

    	/* test polygon B's sides */
    	for (int side = 0; side < otherNumSides; side++)
    	{
    		/* get the axis that we will project onto */
    		if (side == 0)
    		{
    			axis[x] = other.fpoints[otherNumSides] - other.fpoints[1];
    			axis[y] = other.fpoints[0] - other.fpoints[otherNumSides - 1];
    		}
    		else
    		{
    			axis[x] = other.fpoints[((side - 1)*2)+1] - other.fpoints[(side*2)+1];
    			axis[y] = other.fpoints[side*2] - other.fpoints[(side - 1)*2];
    		}

    		/* normalize the axis */
    		tmp = (float) Math.sqrt(axis[x] * axis[x] + axis[y] * axis[y]);
    		axis[x] /= tmp;
    		axis[y] /= tmp;
    		
    		//Log.i("ffz", "poly B, side " + side + ": axis is " + axis[x] + ", " + axis[y]);

    		/* project polygon A onto axis to determine the min/max */
    		minA = this.fpoints[0] * axis[x] + this.fpoints[1] * axis[y];
    		maxA = minA;
    		for (int i = 1; i < myNumSides; i++)
    		{
    			tmp = this.fpoints[i*2] * axis[x] + this.fpoints[(i*2)+1] * axis[y];
    			if (tmp > maxA)
    				maxA = tmp;
    			else if (tmp < minA)
    				minA = tmp;
    		}
    		
    		// correct for offset
    		tmp = this.center[x] * axis[x] + this.center[y] * axis[y];
    		minA += tmp;
    		maxA += tmp;
    		//Log.i("ffz", "minA = " + minA + ", maxA = " + maxA);

    		/* project polygon B onto axis to determine the min/max */
    		minB = other.fpoints[0] * axis[x] + other.fpoints[1] * axis[y];
    		maxB = minB;
    		for (int i = 1; i < otherNumSides; i++)
    		{
    			tmp = other.fpoints[i*2] * axis[x] + other.fpoints[(i*2)+1] * axis[y];
    			if (tmp > maxB)
    				maxB = tmp;
    			else if (tmp < minB)
    				minB = tmp;
    		}
    		/* correct for offset */
    		tmp = other.center[x] * axis[x] + other.center[y] * axis[y];
    		minB += tmp;
    		maxB += tmp;
    		//Log.i("ffz", "minB = " + minB + ", maxB = " + maxB);

    		/* test if lines intersect, if not, return false */
    		if (maxA < minB || minA > maxB) {
    			smallest[0] = 0f;
    			smallest[1] = 0f;
    			smallest[2] = 0f;
    			return smallest;
    		} else {
    			//float o = (maxA > minB ? maxA - minB : maxB - minA);
    			float o = (maxA > maxB ? maxB - minA : maxA - minB);
			    //Log.i("ffz", "poly B side " + side + " overlap = " + o);
    			if (o < overlap) {
    				overlap = o;
    			    smallest[x] = axis[x];
    			    smallest[y] = axis[y];
    			    //smallest[2] = overlap + 0.001f;
    			    //Log.i("ffz", "poly B side " + side + " has smallest overlap, " + overlap);
    			}
    		}
    	}

    	// always push this polygon away from the other polygon
    	float[] cacb = new float[2];
    	cacb[x] = this.center[x] - other.center[x];
    	cacb[y] = this.center[y] - other.center[y];
    	float dot = smallest[x] * cacb[x] + smallest[y] * cacb[y];
    	//Log.i("ffz", "dot = " + dot);
    	if (dot == 0.0f) {
    		// do nothing
    	} else if (dot < 0.0f) {
    		smallest[0] = -smallest[0];
    		smallest[1] = -smallest[1];
    	}
    	
    	smallest[2] = overlap + 0.001f;
    	
		return smallest;
		
	}
	
}
