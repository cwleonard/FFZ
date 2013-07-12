package com.amphibian.ffz;

import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.amphibian.ffz.geometry.ConvexPolygon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FrameDataManager {
	
	private final static int FLOATS_PER_UNIT = 20;

	private static FrameDataManager instance;

	private Map<String,Frame> frames;
	
	private Map<String, CollisionDataHolder> cPolys;
	
	private Set<Class> toInit;
	
	private Set<VertexDataReader> readers;
	
	private FrameDataManager() {
		frames = new HashMap<String,Frame>();
		cPolys = new HashMap<String,CollisionDataHolder>();
		toInit = new HashSet<Class>();
		readers = new HashSet<VertexDataReader>();
	}
	
	public static synchronized FrameDataManager getInstance() {
		if (instance == null) {
			instance = new FrameDataManager();
		}
		return instance;
	}
	
	
	
	public Drawinator init(Context context) {
		
		Drawinator d = new Drawinator(this.readVertexData(context));
		this.performInitializations();
		return d;
		
	}
	
	private float[] readVertexData(Context context) {
		
		frames.clear();
		
		float[] data = {};
		try {
			
			Gson gson = new Gson();
			
			Type collectionType = new TypeToken<List<VertexDataHolder>>(){}.getType();
			List<VertexDataHolder> vList = gson.fromJson(new InputStreamReader(
					context.getResources().openRawResource(R.raw.frames)),
					collectionType);			
			
			data = new float[vList.size() * FLOATS_PER_UNIT];
			
			for (int i = 0; i < vList.size(); i++) {
				
				VertexDataHolder vdh = vList.get(i);
				
				Frame f = new Frame();
				f.setName(vdh.getName());
				f.setIndex(i);
				
				float[] vdata = vdh.getVertexData();
				float width = vdata[10] - vdata[0];
				float height = vdata[1] - vdata[16];
				
				f.setHeight(height);
				f.setWidth(width);
				
				System.arraycopy(vdh.getVertexData(), 0, data, i * FLOATS_PER_UNIT, FLOATS_PER_UNIT);
				
				frames.put(f.getName(), f);
				
			}

			collectionType = new TypeToken<List<CollisionDataHolder>>(){}.getType();
			List<CollisionDataHolder> cList = gson.fromJson(new InputStreamReader(
					context.getResources().openRawResource(R.raw.collisions)),
					collectionType);
			
			for (int i = 0; i < cList.size(); i++) {
				
				CollisionDataHolder cdh = cList.get(i);
				if (cdh != null) {
					cPolys.put(cdh.getName(), cdh);
				}
				
			}

			
		} catch (Exception e) {
			Log.e("ffz", "vertex data read error", e);
		}
		
		return data;
		
	}
	
	public Frame getFrame(String n) {
		return frames.get(n);
	}
	
	public int getFrameIndex(String n) {
		int i = -1;
		Frame f = frames.get(n);
		if (f != null) {
			i = f.getIndex();
		}
		return i;
	}
	
	public List<ConvexPolygon> getPolygons(float x, float y, String n) {
		
		List<ConvexPolygon> s = new ArrayList<ConvexPolygon>();
		CollisionDataHolder cdh = cPolys.get(n);
		if (cdh != null) {
			float[] c = { x, y };
			float[][] z = cdh.getPolygons();
			for (int i = 0; i < z.length; i++) {
				ConvexPolygon p = new ConvexPolygon(cdh.polygons[i], x, y);
				s.add(p);
			}
		}
		return s;
		
	}
	
	public void add(Class c) {
		this.toInit.add(c);
	}
	
	public void addReader(VertexDataReader vdr) {
		this.readers.add(vdr);
	}

	private void performInitializations() {
		
		Iterator<VertexDataReader> vdri = this.readers.iterator();
		while (vdri.hasNext()) {
			
			VertexDataReader r = vdri.next();
			
			Log.d("ffz", "initializing " + r.getClass().getCanonicalName());
			
			float[] data = r.readVertexData();
			r.init(data);
			
		}
		
		// -----------------
		
		Iterator<Class> i = this.toInit.iterator();
		while (i.hasNext()) {
			
			try {

				Class c = i.next();

				Log.d("ffz", "initializing " + c.getCanonicalName());

				try {

					Method m = c.getMethod("init", null);
					m.invoke(null, null);

				} catch (Exception e) {
					// ignore
				}
				
			} catch (Exception e) {
				Log.e("ffz", "error initializing", e);
			}
			
		}
		
	}
	
	private class CollisionDataHolder {
	
		private String name;
		private float[] anchor;
		private float[][] polygons;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public float[] getAnchor() {
			return anchor;
		}
		public void setAnchor(float[] anchor) {
			this.anchor = anchor;
		}
		public float[][] getPolygons() {
			return polygons;
		}
		public void setPolygons(float[][] polygons) {
			this.polygons = polygons;
		}

	}
	
}
