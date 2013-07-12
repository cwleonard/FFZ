package com.amphibian.ffz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import tv.ouya.console.api.OuyaController;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends FragmentActivity {

	static final int PICK_AREA = 0;
	
	private FFZSurfaceView glView;
	
	private MediaPlayer player = null;
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == PICK_AREA) {
			if (resultCode == RESULT_OK) {
				// area was picked. start playing it
				int a = (Integer) data.getExtras().get("area");
				Log.i("ffz", "user picked area " + a);
				new GetAreaAsyncTask().execute(String.valueOf(a));
			}
		}
		
	}
	
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
    	if (keyCode == OuyaController.BUTTON_MENU) {
    		Intent intent = new Intent(this, WorldListActivity.class);
    		//startActivity(intent);
    		this.startActivityForResult(intent, PICK_AREA);
    		
    		//DialogFragment dialog = new WorldSelectDialogFragment();
    		//dialog.show(getSupportFragmentManager(), "World Select Dialog");
    	}
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
    
    protected void onRestart() {
    	super.onRestart();
    	Log.i("ffz", "onRestart called");
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	Log.i("ffz", "onStop called");
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.i("ffz", "onDestroy called. is finishing? " + this.isFinishing());
    }
    
    private void createMediaPlayer() {
    	if (player == null) {
    		Log.i("ffz", "creating media player");
    		player = MediaPlayer.create(this, R.raw.olive_twist_60);
    		player.setVolume(0.2f, 0.2f);
    		player.setLooping(true);
    	}
    }

    private class GetAreaAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			try {
			
				JSONObject area = getJson("http://www.amphibian.com/ffz/area?id=" + params[0]);
				JSONArray groundGrid = area.getJSONArray("groundGrid");
				
				return groundGrid.toString();
			
			} catch (Exception e) {
				Log.e("ffz", "error getting ground grid", e);
			}
			
			return null;
		}
    	
    	protected void onPostExecute(String result) {
    		
			if (glView != null) {
				
				Engine e = glView.getEngine();
				e.setNewGround(result);
				
			}

    		
    	}
    	
    	private JSONObject getJson(String url) {

    		JSONObject jsonObject = null;

    		try {

    			URL areaUrl = new URL(url);
    			BufferedReader in = new BufferedReader(new InputStreamReader(
    					areaUrl.openStream()));

    			StringBuilder data = new StringBuilder();
    			String inputLine;
    			while ((inputLine = in.readLine()) != null) {
    				data.append(inputLine);
    			}
    			in.close();

    			jsonObject = new JSONObject(data.toString());

    		} catch (Exception e) {
    			Log.e("ffz", "problem reading area data", e);
    		}
    		return jsonObject;

    	}

    }
    

}
