package com.amphibian.ffz.engine.world;

import java.util.List;

public class World {

	private String id;
	
	private String name;
	
	private String currentArea;
	
	private List<Area> areas;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurrentArea() {
		return currentArea;
	}

	public void setCurrentArea(String currentArea) {
		this.currentArea = currentArea;
	}

	public List<Area> getAreas() {
		return areas;
	}

	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}
	
	
	
}
