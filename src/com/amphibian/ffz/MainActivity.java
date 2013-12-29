package com.amphibian.ffz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amphibian.ffz.input.InputSource;
import com.amphibian.ffz.input.OuyaInputSource;

import tv.ouya.console.api.OuyaController;
import tv.ouya.console.api.OuyaFacade;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class MainActivity extends FragmentActivity {

	static final int PICK_AREA = 0;
	
	private FFZSurfaceView glView;
	
	private Engine engine = null;
	
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

		Log.i("ffz", "MainActivity onCreate called");
		
		engine = new Engine();
        glView = new FFZSurfaceView(engine);
		setContentView(glView);

		if (OuyaFacade.getInstance().isRunningOnOUYAHardware()) {
			OuyaController.init(this);
			OuyaController oc1 = OuyaController.getControllerByPlayer(0);
			InputSource is = new OuyaInputSource(oc1);
			engine.setInputSource(is);
		} else {
			// set touch-screen input source
			glView.setTouchInput(engine);
		}
        
		
        
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
		
		engine.cleanup();
		
        glView.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
		Log.i("ffz", "onResume called");
		engine.resume();
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
    
    private class GetAreaAsyncTask extends AsyncTask<String, Void, List<String>> {

		@Override
		protected List<String> doInBackground(String... params) {
			
			try {
			
				JSONObject area = getJson("http://www.amphibian.com/ffz/area?id=" + params[0]);
				JSONArray groundGrid = area.getJSONArray("groundGrid");
				JSONArray obsList = area.getJSONArray("objectList");
				
				List<String> s = new ArrayList<String>();
				s.add(groundGrid.toString());
				
				// convert from web format
				JSONArray converted = new JSONArray();
				for (int i = 0; i < obsList.length(); i++) {
					
					JSONObject ob = obsList.getJSONObject(i);
					JSONObject cob = new JSONObject();

					boolean canBeConverted = false;
					
					int type = ob.getInt("type");
					if (type == 2) {
						cob.put("type", "flower1");
						canBeConverted = true;
					} else if (type == 1) {
						cob.put("type", "tree1");
						canBeConverted = true;
					} else if (type == 4) {
						cob.put("type", "rock");
						canBeConverted = true;
					} else if (type == 13) {
						cob.put("type", "flower2");
						canBeConverted = true;
					} else if (type == 14) {
						cob.put("type", "pine_tree");
						canBeConverted = true;
					} else if (type == 3) {
						cob.put("type", "tall_grass");
						canBeConverted = true;
					}
					
					JSONObject pos = ob.getJSONObject("position");
					double x = pos.getDouble("x");
					double y = pos.getDouble("y");
					cob.put("x", x*2.5d);
					cob.put("y", -y*2.5d);

					if (canBeConverted) {
						converted.put(cob);
					}
					
				}
				
				
				s.add(converted.toString());
				
				return s;
			
			} catch (Exception e) {
				Log.e("ffz", "error getting ground grid", e);
			}
			
			return null;
		}
    	
    	protected void onPostExecute(List<String> result) {
    		
			if (glView != null) {
				
				engine.setNewGround(result.get(0));
				engine.setNewObstacles(result.get(1));
				
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
