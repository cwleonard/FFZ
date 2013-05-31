package com.amphibian.ffz;

import io.socket.SocketIO;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
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
	
	
	private String fid = UUID.randomUUID().toString();
	
	public Engine(Context context) {
		
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
		
		drawinator = new Drawinator(fdman.readVertexData(context));
		
		Frog.init();
		frog.faceRight();
		

		
		try {
			
			Tile[][] tiles = gson.fromJson(new InputStreamReader(context.getResources().openRawResource(R.raw.area)), Tile[][].class);
			ground.setTiles(tiles);
			
			Type collectionType = new TypeToken<List<Obstacle>>(){}.getType();
			List<Obstacle> obs = gson.fromJson(new InputStreamReader(context.getResources().openRawResource(R.raw.obstacles)), collectionType);
			drawinator.setStuff(obs);
			
		} catch (Exception e) {
			Log.e("ff", "json error", e);
		}
		
		blockers = drawinator.getBlockers();

		drawinator.getStuff().add(frog);

	}
	
	public void createViewport(int height, int width) {
		
		viewport = new Viewport(height, width);
		
	}
	
	public void update() {
		
		// how long has it been?
		long delta = SystemClock.uptimeMillis() - lastUpdate;
		
		
		// player 1 (frog)
		
		float stickX = 0;
		float stickY = 0;
		
		OuyaController c1 = OuyaController.getControllerByPlayer(0);
		if (c1 != null) {
			boolean a = c1.getButton(OuyaController.BUTTON_A);
			stickX = c1.getAxisValue(OuyaController.AXIS_LS_X);
			stickY = -c1.getAxisValue(OuyaController.AXIS_LS_Y);
			if (Math.abs(stickX) < 0.25f) {
				stickX = 0;
			}
			if (Math.abs(stickY) < 0.25f) {
				stickY = 0;
			}
			
			float axisX2 = c1.getAxisValue(OuyaController.AXIS_RS_X);
			float axisY2 = -c1.getAxisValue(OuyaController.AXIS_RS_Y);
			float moveX2 = delta * 0.4f * axisX2;
			float moveY2 = delta * 0.4f * axisY2;
			if (Math.abs(axisX2) < 0.25f) {
				moveX2 = 0;
			}
			if (Math.abs(axisY2) < 0.25f) {
				moveY2 = 0;
			}
			viewport.moveCamera(moveX2, -moveY2);
			
		}
		
		float[] deltaMove = frog.getMovement(delta, stickX, stickY);

		// set direction intent before possible collision
		frog.setDirection(deltaMove[0], deltaMove[1]);
		
		ConvexPolygon fPoly = frog.getBlocker(frog.x + deltaMove[0], frog.y + deltaMove[1]);

		// mtv will be all 0 if no collision, or if there is a collision it will contain
		// the axis and overlap to move fPoly out of collision. MTV = Minimum Translation Vector

		//TODO: put the ground blockers in the same array as below...
		float[] mtv = fPoly.intersectsWith(testBlock);
		deltaMove[0] += mtv[0] * mtv[2];			  
		deltaMove[1] += mtv[1] * mtv[2];
		
		for (int i = 0; i < blockers.size(); i++) {
			mtv = fPoly.intersectsWith(blockers.get(i));
			deltaMove[0] += mtv[0] * mtv[2];			  
			deltaMove[1] += mtv[1] * mtv[2];
		}
		
		frog.move(deltaMove[0], deltaMove[1]);
		
		
		OuyaController c2 = OuyaController.getControllerByPlayer(1);
		if (c2 != null) {
			if (frog2 == null) {
				frog2 = new Frog();
				drawinator.getStuff().add(frog2);
			}
			boolean a = c2.getButton(OuyaController.BUTTON_A);
			stickX = c2.getAxisValue(OuyaController.AXIS_LS_X);
			stickY = -c2.getAxisValue(OuyaController.AXIS_LS_Y);
			if (Math.abs(stickX) < 0.25f) {
				stickX = 0;
			}
			if (Math.abs(stickY) < 0.25f) {
				stickY = 0;
			}
		}
		

		if (frog2 != null) {
			
			float[] deltaMove2 = frog2.getMovement(delta, stickX, stickY);

			// set direction intent before possible collision
			frog2.setDirection(deltaMove2[0], deltaMove2[1]);
			
			ConvexPolygon fPoly2 = frog2.getBlocker(frog2.x + deltaMove2[0], frog2.y + deltaMove2[1]);
			float[] mtv2 = fPoly2.intersectsWith(testBlock);
			// mtv will be all 0 if no collision, or if there is a collision it will contain
			// the axis and overlap to move fPoly out of collision. MTV = Minimum Translation Vector
			deltaMove2[0] += mtv2[0] * mtv2[2];			  
			deltaMove2[1] += mtv2[1] * mtv2[2];
			frog2.move(deltaMove2[0], deltaMove2[1]);
			
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
		drawinator.draw(prog, viewport);
		
		frameCounter++;
		
		//triangle.draw(viewport.getProjMatrix(), viewport.getViewMatrix());
		
		cycle = SystemClock.uptimeMillis();
		
		
	}

	public Frog getFrog() {
		return frog;
	}

	
	
	
}
