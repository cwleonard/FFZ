package com.amphibian.ffz;

import tv.ouya.console.api.OuyaController;
import android.app.Activity;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private GLSurfaceView glView;
	
	private MediaPlayer player = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		Log.i("ffz", "onCreate called");
		
        // full-screen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)

        OuyaController.init(this);
        
		glView = new FFZSurfaceView(this);
		setContentView(glView);
		
		this.createMediaPlayer();
		
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return glView.onKeyDown(keyCode,  event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return glView.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if((event.getSource() & InputDevice.SOURCE_CLASS_JOYSTICK) == 0){
            //Not a joystick movement, so ignore it.
            return false;
        }
        return glView.onGenericMotionEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
		Log.i("ffz", "onPause called");
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        player.pause();
        player.release();
        player = null;
        glView.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
		Log.i("ffz", "onResume called");
		this.createMediaPlayer();
    	player.start();
        glView.onResume();
    }
    
    private void createMediaPlayer() {
    	if (player == null) {
    		Log.i("ffz", "creating media player");
    		player = MediaPlayer.create(this, R.raw.olive_twist_60);
    		player.setVolume(0.2f, 0.2f);
    		player.setLooping(true);
    	}
    }

}
