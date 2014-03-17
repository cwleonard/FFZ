package com.amphibian.ffz;

import tv.ouya.console.api.OuyaController;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.amphibian.ffz.engine.Engine;
import com.amphibian.ffz.input.TouchInputSource;

public class FFZSurfaceView extends GLSurfaceView {

	private FFZRenderer r;
	
	private FrogPath path = null;
	
	private TouchInputSource tis = null;
	
	public FFZSurfaceView(Engine e) {
		
		super(App.getContext());
		setEGLContextClientVersion(2);
		

		r = new FFZRenderer(e);
		
		setRenderer(r);
		
	}
	
	public void setTouchInput(Engine e) {
		tis = new TouchInputSource();
		e.setInputSource(tis);
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
    			if (this.tis != null) {
    				FrogPath first = this.tis.getFirstPath();
    				if (first != null) {
    					float d = first.distanceTo(path);
    					if (d > 15) {
    						this.tis.setPath(path);
    					} else {
    						this.tis.buttonPress();
    					}
    				} else {
    					//this.tis.setPath(path);
    					this.tis.buttonPress();
    				}
    			}
    		}
    		break;
    	case MotionEvent.ACTION_MOVE:
    		handled = true;
    		if (path != null) {
    			path.setEnd(event.getX(), -event.getY());
    			if (this.tis != null) {
    				this.tis.addToPath(path);
    			}
        		path = new FrogPath();
        		path.setStart(event.getX(), -event.getY());
    		}
    		break;
    	}
    	return handled || super.onTouchEvent(event);
    	
    }

	
}
