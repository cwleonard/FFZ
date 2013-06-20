package com.amphibian.ffz;

import io.socket.SocketIO;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import tv.ouya.console.api.OuyaController;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.amphibian.ffz.data.ObstacleDeserializer;
import com.amphibian.ffz.geometry.ConvexPolygon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Engine {

	private long lastUpdate;
	
	private Viewport viewport;
	
	//private Triangle triangle;
	//private Square square;
	private Frog frog = null;
	private Frog frog2 = null;
	private Ground ground;
	
	//private Obstacles obstacles;
	private Drawinator drawinator;
	
	//private int fps = 0;
	private long cycle = 0;
	private long secondCounter = 0;
	private int frameCounter = 0;
	
	private StandardProgram prog;
	
	private SocketIO socket;
	
	private ConvexPolygon testBlock;
	private List<ConvexPolygon> blockers;
	private List<Sprite> sprites;
	
	private InputSource input1;
	private InputSource input2;
	
	private String fid = UUID.randomUUID().toString();
	

	public Engine(Context context) {
		
		sprites = new ArrayList<Sprite>();
		
		Gson gson = new GsonBuilder()
			.registerTypeAdapter(Obstacle.class, new ObstacleDeserializer())
			.create();

		lastUpdate = SystemClock.uptimeMillis();
		cycle = SystemClock.uptimeMillis();
		//triangle = new Triangle();
		//square = new Square();
		frog = new Frog();
		ground = new Ground();


		Ground.loadGLTexture(context);
		Drawinator.loadGLTexture(context);
		
//		float[] c = {300, -300};
//		float[] p = {0, 100, 200, -100, -200, -100};
//		testBlock = new ConvexPolygon(c, p); // blocking triangle

		float[] c = {200, -900};
		float[] p = {-200, 100, 200, 100, 0, -100, -200, -100};
		testBlock = new ConvexPolygon(c, p); // blocking rock wall
		
		
		prog = new StandardProgram();

//		try {
//			socket = new SocketIO("http://www.amphibian.com:8080/");
//			socket.connect(new FrogSocketMessageHandler());
//			JSONObject jo = new JSONObject();
//			jo.put("fid", fid);
//			socket.emit("startup", jo);
//		} catch (Exception e) {
//			Log.e("ffz", "socket.io error", e);
//		}

		FrameDataManager fdman = FrameDataManager.getInstance();
		fdman.add(Frog.class);
		drawinator = fdman.init(context);
		
		frog.faceRight();
		
		try {
			
			Tile[][] tiles = gson.fromJson(new InputStreamReader(context.getResources().openRawResource(R.raw.area)), Tile[][].class);
			ground.setTiles(tiles);
			
			Type collectionType = new TypeToken<List<Obstacle>>(){}.getType();
			List<Obstacle> obs = gson.fromJson(new InputStreamReader(context.getResources().openRawResource(R.raw.obstacles)), collectionType);
			sprites.addAll(obs);
			
		} catch (Exception e) {
			Log.e("ff", "json error", e);
		}
		
		blockers = getBlockers();

		sprites.add(frog);
		
		
		
		

	}
	
	public void createViewport(int height, int width) {
		
		viewport = new Viewport(height, width);
		viewport.setInputSource(new OuyaInputSource(OuyaController.getControllerByPlayer(0)));
		
	}
	
	public void update() {
		
		// how long has it been?
		long delta = SystemClock.uptimeMillis() - lastUpdate;

		
		if (input1 == null) {
			OuyaController oc1 = OuyaController.getControllerByPlayer(0);
			if (oc1 != null) {
				input1 = new OuyaInputSource(oc1);
				frog.setInputSource(input1);
			}
		}
		if (input2 == null) {
			OuyaController oc2 = OuyaController.getControllerByPlayer(1);
			if (oc2 != null) {
				if (frog2 == null) {
					frog2 = new Frog();
					input2 = new OuyaInputSource(oc2);
					frog2.setInputSource(input2);
					sprites.add(frog2);
				}
			}
		}
		
		
		
		// move things that might move
		Iterator<Sprite> si = sprites.iterator();
		while (si.hasNext()) {
			Sprite s = si.next();
			s.update(delta);
		}
		viewport.update(delta);
		
		
		
		// now check for collisions. we may have to back some things off
		si = sprites.iterator();
		while (si.hasNext()) {
			
			Sprite s = si.next();
			if (s.checkMovement()) {
				
				List<ConvexPolygon> blist = s.getBlockers();
				Iterator<ConvexPolygon> pi = blist.iterator();
				while (pi.hasNext()) {
					
					ConvexPolygon poly = pi.next();
					
					//TODO: put the ground blockers in the same array as below...
					float[] mtv = poly.intersectsWith(testBlock);
					float[] correction = new float[] { mtv[0] * mtv[2], mtv[1] * mtv[2] };
					
					for (int i = 0; i < blockers.size(); i++) {
						mtv = poly.intersectsWith(blockers.get(i));
						correction[0] += mtv[0] * mtv[2];
						correction[1] += mtv[1] * mtv[2];
					}

					s.move(correction[0], correction[1]);

				}
				
			}
			
		}
		
		
		


		// center the frog, if possible
		float cMoveX = 0f;
		float cMoveY = 0f;
		if (-frog.y > viewport.getCameraCoords()[1]) {
			cMoveY = (-frog.y) - viewport.getCameraCoords()[1];
		} else if (viewport.getCameraCoords()[1] > (viewport.getHeight()/2)) {
			cMoveY = -viewport.getCameraCoords()[1] - frog.y;
		}
		if (frog.x > viewport.getCameraCoords()[0]) {
			cMoveX = frog.x - viewport.getCameraCoords()[0];
		} else if (viewport.getCameraCoords()[0] > (viewport.getWidth()/2)) {
			cMoveX = -(viewport.getCameraCoords()[0] - frog.x);
		}
		viewport.moveCamera(cMoveX, cMoveY);
		

		/*
		JSONObject o = new JSONObject();
		try {
			o.put("id", fid);
			o.put("imgId", "3");
			o.put("inArea", "7");
			JSONObject pos = new JSONObject();
			pos.put("x", square.x);
			pos.put("y", -square.y);
			o.put("pos", pos);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		socket.emit("frogmove", o);
		*/
		
		// order the sprites so they draw correctly
        Collections.sort(sprites, new Comparator<Sprite>() {
			@Override
			public int compare(Sprite lhs, Sprite rhs) {
				float lhsBottom = lhs.getBottom();
				float rhsBottom = rhs.getBottom();
				if (rhsBottom - lhsBottom < 0f) {
					return -1;
				} else if (rhsBottom - lhsBottom > 0f) {
					return 1;
				} else {
					return 0;
				}
			}
        });

		

		// we're done. update the time.
		lastUpdate = SystemClock.uptimeMillis();
		
	}
	
	public void draw() {

		long delta = SystemClock.uptimeMillis() - cycle;
		secondCounter += delta;
		if (secondCounter > 5000) {
			float se = ((float)secondCounter/1000f);
//			Log.i("ffz", "frames = " + frameCounter);
//			Log.i("ffz", "seconds elapsed = " + se);
			Log.i("ffz", frameCounter / se + " FPS");
			secondCounter = 0;
			frameCounter = 0;
		}
		
		prog.enable();

		ground.draw(prog, viewport);

		//square.draw(prog, viewport);

		//obstacles.draw(prog, viewport);
		drawinator.draw(sprites, prog, viewport);
		
		frameCounter++;
		
		//triangle.draw(viewport.getProjMatrix(), viewport.getViewMatrix());
		
		cycle = SystemClock.uptimeMillis();
		
		
	}

	public Frog getFrog() {
		return frog;
	}

    public List<ConvexPolygon> getBlockers() {
    	
    	//ConvexPolygon[] blockers = new ConvexPolygon[stuff.size()];
    	List<ConvexPolygon> blockers = new ArrayList<ConvexPolygon>();
    	Iterator<? extends Sprite> i = sprites.iterator();
    	while (i.hasNext()) {
    		
    		Sprite s = i.next();
    		blockers.addAll(s.getBlockers());
    		
    	}
    	return blockers;
    	
    }

	
	
}
