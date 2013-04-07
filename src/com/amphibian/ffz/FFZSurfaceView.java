package com.amphibian.ffz;

import tv.ouya.console.api.OuyaController;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class FFZSurfaceView extends GLSurfaceView {

	private FFZRenderer r;
	
	private FrogPath path = null;
	
	public FFZSurfaceView(Context context) {
		
		super(context);
		setEGLContextClientVersion(2);
		
		r = new FFZRenderer(context);
		setRenderer(r);
		
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
        		r.getEngine().getFrog().setFrogPath(path);
    		}
    		break;
    	}
    	return handled || super.onTouchEvent(event);
    	
    }

	
}
