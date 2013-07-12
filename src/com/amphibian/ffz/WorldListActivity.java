package com.amphibian.ffz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import tv.ouya.console.api.OuyaController;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WorldListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_world_list);

		new GetWorldsAsyncTask().execute();
		
		ListView lv = (ListView) findViewById(R.id.listView1);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				
				final WorldListItem item = (WorldListItem) parent
						.getItemAtPosition(position);
				//Log.i("ffz", "user selected area " + item.getId());
				
				Intent t = new Intent();
				t.putExtra("area", item.getId());
				setResult(RESULT_OK, t);
				finish();
				
			}

		});

		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_world_list, menu);
		return true;
	}

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	
    	if (keyCode == OuyaController.BUTTON_MENU) {
    		this.finish();
    	
    	} else {
    		return super.onKeyUp(keyCode, event);
    	}
		return true;
        
    }
    
    private class WorldListItem {
    	
    	private int id;
    	private String name;
    	private String description;
    	
    	public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String toString() {
    		return name + " - " + description;
    	}
    	
    }
    
    
    private class GetWorldsAsyncTask extends AsyncTask<Void, Void, ArrayList<WorldListItem>> {

    	@Override
    	protected ArrayList<WorldListItem> doInBackground(Void... params) {

    		ArrayList<WorldListItem> myList = new ArrayList<WorldListItem>();
    		
    		JSONArray allAreas = getJson("http://www.amphibian.com/ffz/area");
    		
    		try {
    			
    			for (int i = 0; i < allAreas.length(); i++) {
    			
    				JSONObject ta = allAreas.getJSONObject(i);
    				WorldListItem wli = new WorldListItem();
    				wli.setId(ta.getInt("id"));
    				wli.setName(ta.getString("name"));
    				wli.setDescription(ta.getString("description"));
    				myList.add(wli);
    				
    			}
    			
    		} catch (Exception e) {
    			Log.e("ffz", "error parsing json area data", e);
    		}
    		
    		return myList;
    	}
    	
    	protected void onPostExecute(ArrayList<WorldListItem> result) {
    		
    		
    		ListView lv = (ListView) findViewById(R.id.listView1);
    		
    		ArrayAdapter<WorldListItem> arrayAdapter = new ArrayAdapter<WorldListItem>(WorldListActivity.this, android.R.layout.simple_list_item_1, result);
    		lv.setAdapter(arrayAdapter);

    		
    	}
    	
    	private JSONArray getJson(String url) {

    		JSONArray jsonObject = null;

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

    			jsonObject = new JSONArray(data.toString());

    		} catch (Exception e) {
    			Log.e("ffz", "problem reading area data", e);
    		}
    		return jsonObject;

    	}

    }

    

}
