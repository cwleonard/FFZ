package com.amphibian.ffz.engine.world;

import java.util.ArrayList;
import java.util.List;

import com.amphibian.ffz.engine.sprite.Obstacle;


public class Area {

	private String id;
	
	private String name;
	
	private String description;

	private int musicId;
	
	private Tile[][] ground;
	
	private List<Obstacle> obstacles;
	
	private String worldId;
	
	private List<Portal> portals;
	
	public Area() {
		obstacles = new ArrayList<Obstacle>();
		portals = new ArrayList<Portal>();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the groundGrid
	 */
	public Tile[][] getGround() {
		return ground;
	}

	/**
	 * @param groundGrid the groundGrid to set
	 */
	public void setGround(Tile[][] groundGrid) {
		this.ground = groundGrid;
	}


	public int getMusicId() {
		return musicId;
	}

	public void setMusicId(int musicId) {
		this.musicId = musicId;
	}

	/**
	 * @return the worldId
	 */
	public String getWorldId() {
		return worldId;
	}

	/**
	 * @param worldId the worldId to set
	 */
	public void setWorldId(String worldId) {
		this.worldId = worldId;
	}

	public List<Portal> getPortals() {
		return portals;
	}

	public List<Obstacle> getObstacles() {
		return obstacles;
	}
	
}
