package com.amphibian.ffz.engine.sprite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amphibian.ffz.engine.util.CollisionDataHolder;
import com.amphibian.ffz.geometry.ConvexPolygon;

public class FrameDataManager {
	

	private static FrameDataManager instance;

	private Map<String,Frame> frames;
	
	private Map<String, CollisionDataHolder> cPolys;
	
	private FrameDataManager() {
		frames = new HashMap<String,Frame>();
		cPolys = new HashMap<String,CollisionDataHolder>();
	}
	
	public static synchronized FrameDataManager getInstance() {
		if (instance == null) {
			instance = new FrameDataManager();
		}
		return instance;
	}
	
	public static synchronized void destroy() {
		if (instance != null) {
			instance.clear();
			instance = null;
		}
	}
	
	public synchronized void clear() {
		
		frames.clear();
		cPolys.clear();
		
	}
	
	public synchronized void putCollider(CollisionDataHolder cdh) {
		if (cdh != null) {
			cPolys.put(cdh.getName(), cdh);
		}
	}
	
	public synchronized void putFrame(Frame f) {
		if (f != null) {
			frames.put(f.getName(), f);
		}
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
			float[][] z = cdh.getPolygons();
			for (int i = 0; i < z.length; i++) {
				ConvexPolygon p = new ConvexPolygon(cdh.getPolygons()[i], x, y);
				s.add(p);
			}
		}
		return s;
		
	}
	
	
}
