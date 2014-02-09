package com.amphibian.ffz.engine.sprite;

import java.util.List;

import android.opengl.Matrix;

import com.amphibian.ffz.engine.layers.SpriteLayer;
import com.amphibian.ffz.geometry.ConvexPolygon;

public class Obstacle implements Sprite {

	public final static float SHADOW_SCALE = 0.7f;

	private int id;
	
	private String type;
	
	private float x;
	private float y;
	
	private float w;
	private float h;
	private float hh;
	private float hw;
	
	private float[] nMatrix = new float[16];
	private float[] sMatrix = new float[16];
	
    protected static float skewMatrix[] = {
    	1f,    0f, 0f, 0f,
      0.5f,    1f, 0f, 0f,
    	0f,    0f, 1f, 0f,
    	0f,    0f, 0f, 1f
    };
    
    protected float normalColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    protected float shadowColor[] = { 0.0f, 0.0f, 0.0f, 0.2f };


	private List<ConvexPolygon> blockers;
	
	public Obstacle(String t, float x, float y) {
		
		FrameDataManager fdm = FrameDataManager.getInstance();
		Frame f = fdm.getFrame(t);
		this.type = t;
		this.id = f.getIndex();
		this.w = f.getWidth();
		this.h = f.getHeight();
		this.hh = h / 2.0f;
		this.hw = w / 2.0f;
		this.x = x;
		this.y = y;

		// set up the "normal" draw matrix
        Matrix.setIdentityM(nMatrix, 0);
    	Matrix.translateM(nMatrix, 0, x, y, 0);

    	// set up the shadow draw matrix
        Matrix.setIdentityM(sMatrix, 0);
    	Matrix.translateM(sMatrix, 0, x + (this.hw * SpriteLayer.SHADOW_SCALE), this.y - (this.hh * (1f - SpriteLayer.SHADOW_SCALE)), 0);
        Matrix.scaleM(sMatrix, 0, 1f, SHADOW_SCALE, 1f); // half the y axis, leave x and z alone
        Matrix.multiplyMM(sMatrix, 0, sMatrix, 0, skewMatrix, 0);
    	
		blockers = fdm.getPolygons(x, y, type);
		
	}

	public Obstacle() {
		this.id = -1;
	}
	
	public void hurt() {
		// nothing
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
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

		d.setBufferPosition(this.id);
		
		d.setColor(shadowColor);
		d.performDraw(sMatrix);
		
		d.setColor(normalColor);
		d.performDraw(nMatrix);
		
	}
	
	public float getBottom() {
		return this.y - this.hh;
	}
	
	public float getShadowY() {
		return this.y - (this.hh * (1f - SpriteLayer.SHADOW_SCALE));
	}
	
	public float getShadowX() {
		return this.x + (this.hw * SpriteLayer.SHADOW_SCALE);
	}
	
	public List<ConvexPolygon> getBlockers() {
		
		return blockers;

	}

	@Override
	public void move(float dx, float dy) {
		// TODO let these move someday
	}

	@Override
	public boolean checkMovement() {
		// TODO return true if these move
		return false;
	}

	@Override
	public void update(long delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean remove() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public int getProperties() {
		return 0;
	}

}
