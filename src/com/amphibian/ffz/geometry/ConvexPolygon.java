package com.amphibian.ffz.geometry;

import java.util.ArrayList;
import java.util.List;

public class ConvexPolygon {

	private final static int x = 0;
	private final static int y = 1;
	
	private List<float[]> points = new ArrayList<float[]>();
	
	private float[] center = new float[2];
	
	public ConvexPolygon() {
		// nothing
	}
	
	/**
	 * Constructor.
	 * 
	 * @param c array representing the center. x coord is in position 0, y coord in 1.
	 * @param p array of center-relative points. every 2 elements is one point.
	 */
	public ConvexPolygon(float[] c, float[] p) {
		
		this.center[x] = c[x];
		this.center[y] = c[y];
		
		for (int i = 0; i < p.length; i = i + 2) {
			float[] q = new float[2];
			q[x] = p[i];
			q[y] = p[i+1];
			points.add(q);
		}
		
	}

	public ConvexPolygon(float[] p, float offsetX, float offsetY) {
		
		float maxX = Float.MIN_VALUE;
		float minX = Float.MAX_VALUE;
		float maxY = Float.MIN_VALUE;
		float minY = Float.MAX_VALUE;
		
		for (int i = 0; i < p.length; i = i + 2) {
			float[] q = new float[2];
			q[x] = p[i] + offsetX;
			q[y] = p[i+1] + offsetY;
			
			if (q[x] > maxX) maxX = q[x];
			if (q[x] < minX) minX = q[x];

			if (q[y] > maxY) maxY = q[y];
			if (q[y] < minY) minY = q[y];

			points.add(q);
		}
		
		this.center[x] = (maxX - minX) / 2;
		this.center[y] = (maxY - minY) / 2;
		
	}
	
	
	public int getNumberOfSides() {
		return points.size();
	}
	
	/**
	 * This method uses the Separating Axis Theorem to determine if this polygon
	 * is intersecting with another.
	 * 
	 * @param other
	 * @return
	 */
	public float[] intersectsWith(ConvexPolygon other) {
		
		float[] axis = new float[2];
    	float tmp, minA, maxA, minB, maxB;
    	float[] smallest = new float[3];
    	float overlap = 99999999f;

    	/* test polygon A's sides */
    	for (int side = 0; side < this.getNumberOfSides(); side++)
    	{
    		/* get the axis that we will project onto */
    		if (side == 0)
    		{
    			axis[x] = this.points.get(this.getNumberOfSides() - 1)[y] - this.points.get(0)[y];
    			axis[y] = this.points.get(0)[x] - this.points.get(this.getNumberOfSides() - 1)[x];
    		}
    		else
    		{
    			axis[x] = this.points.get(side - 1)[y] - this.points.get(side)[y];
    			axis[y] = this.points.get(side)[x] - this.points.get(side - 1)[x];
    		}

    		/* normalize the axis */
    		tmp = (float) Math.sqrt(axis[x] * axis[x] + axis[y] * axis[y]);
    		axis[x] /= tmp;
    		axis[y] /= tmp;

    		/* project polygon A onto axis to determine the min/max */
    		minA = this.points.get(0)[x] * axis[x] + this.points.get(0)[y] * axis[y];
    		maxA = minA;
    		for (int i = 1; i < this.getNumberOfSides(); i++)
    		{
    			tmp = this.points.get(i)[x] * axis[x] + this.points.get(i)[y] * axis[y];
    			if (tmp > maxA)
    				maxA = tmp;
    			else if (tmp < minA)
    				minA = tmp;
    		}
    		/* correct for offset */
    		tmp = this.center[x] * axis[x] + this.center[y] * axis[y];
    		minA += tmp;
    		maxA += tmp;

    		/* project polygon B onto axis to determine the min/max */
    		minB = other.points.get(0)[x] * axis[x] + other.points.get(0)[y] * axis[y];
    		maxB = minB;
    		for (int i = 1; i < other.getNumberOfSides(); i++)
    		{
    			tmp = other.points.get(i)[x] * axis[x] + other.points.get(i)[y] * axis[y];
    			if (tmp > maxB)
    				maxB = tmp;
    			else if (tmp < minB)
    				minB = tmp;
    		}
    		/* correct for offset */
    		tmp = other.center[x] * axis[x] + other.center[y] * axis[y];
    		minB += tmp;
    		maxB += tmp;

    		/* test if lines intersect, if not, return false */
    		if (maxA < minB || minA > maxB) {
    			return new float[3];
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
    	for (int side = 0; side < other.getNumberOfSides(); side++)
    	{
    		/* get the axis that we will project onto */
    		if (side == 0)
    		{
    			axis[x] = other.points.get(other.getNumberOfSides() - 1)[y] - other.points.get(0)[y];
    			axis[y] = other.points.get(0)[x] - other.points.get(other.getNumberOfSides() - 1)[x];
    		}
    		else
    		{
    			axis[x] = other.points.get(side - 1)[y] - other.points.get(side)[y];
    			axis[y] = other.points.get(side)[x] - other.points.get(side - 1)[x];
    		}

    		/* normalize the axis */
    		tmp = (float) Math.sqrt(axis[x] * axis[x] + axis[y] * axis[y]);
    		axis[x] /= tmp;
    		axis[y] /= tmp;
    		
    		//Log.i("ffz", "poly B, side " + side + ": axis is " + axis[x] + ", " + axis[y]);

    		/* project polygon A onto axis to determine the min/max */
    		minA = this.points.get(0)[x] * axis[x] + this.points.get(0)[y] * axis[y];
    		maxA = minA;
    		for (int i = 1; i < this.getNumberOfSides(); i++)
    		{
    			tmp = this.points.get(i)[x] * axis[x] + this.points.get(i)[y] * axis[y];
    			if (tmp > maxA)
    				maxA = tmp;
    			else if (tmp < minA)
    				minA = tmp;
    		}
    		/* correct for offset */
    		tmp = this.center[x] * axis[x] + this.center[y] * axis[y];
    		minA += tmp;
    		maxA += tmp;
    		//Log.i("ffz", "minA = " + minA + ", maxA = " + maxA);

    		/* project polygon B onto axis to determine the min/max */
    		minB = other.points.get(0)[x] * axis[x] + other.points.get(0)[y] * axis[y];
    		maxB = minB;
    		for (int i = 1; i < other.getNumberOfSides(); i++)
    		{
    			tmp = other.points.get(i)[x] * axis[x] + other.points.get(i)[y] * axis[y];
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
    			return new float[3];
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
    	if (dot < 0) {
    		smallest[0] = -smallest[0];
    		smallest[1] = -smallest[1];
    	}
    	
    	smallest[2] = overlap + 0.001f;
		return smallest;
		
	}
	
}
