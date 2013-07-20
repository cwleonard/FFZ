package com.amphibian.ffz;

import tv.ouya.console.api.OuyaController;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class FFZSurfaceView extends GLSurfaceView {

	private FFZRenderer r;
	
	private FrogPath path = null;
	
	private Engine engine = null;
	
	public FFZSurfaceView(Context context) {
		
		super(context);
		setEGLContextClientVersion(2);
		
		//engine = new Engine(context);
		
		r = new FFZRenderer(null, context);
		setRenderer(r);
		
		setEngine(r.getEngine());
		
//		engine = new Engine(context);
//		r.setEngine(engine);
		
	}

	public Engine getEngine() {
		return r.getEngine();
	}
	
	public void setEngine(Engine e) {
		this.engine = e;
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	boolean handled = OuyaController.onKeyDown(keyCode, event);
    	return handled || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	boolean handled = OuyaController.onKeyUp(keyCode, event);
    	return handled || super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

    	boolean handled = OuyaController.onGenericMotionEvent(event);
    	return handled || super.onGenericMotionEvent(event);
        
    }

    public boolean onTouchEvent(MotionEvent event) {
    	
    	boolean handled = false;
    	switch (event.getAction()) {
    	case MotionEvent.ACTION_DOWN:
    		handled = true;
    		path = new FrogPath();
    		path.setStart(event.getX(), -event.getY());
    		break;
    	case MotionEvent.ACTION_UP:
    		handled = true;
    		if (path != null) {
    			path.setEnd(event.getX(), -event.getY());
    			if (engine != null && engine.getFrog() != null) {
    				engine.getFrog().setFrogPath(path);
    			}
    		}
    		break;
    	}
    	return handled || super.onTouchEvent(event);
    	
    }

	
}
