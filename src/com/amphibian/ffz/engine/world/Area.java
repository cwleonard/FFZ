package com.amphibian.ffz.engine.world;

import java.util.List;

import com.amphibian.ffz.engine.sprite.Obstacle;


public class Area {

	private int id;
	
	private String name;
	
	private String description;

	private int musicId;
	
	private Tile[][] ground;
	
	private List<Obstacle> obstacles;
	
	private int worldId;
	
	
	
	public List<Obstacle> getObstacles() {
		return obstacles;
	}

	public void setObstacles(List<Obstacle> obstacles) {
		this.obstacles = obstacles;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
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
	public int getWorldId() {
		return worldId;
	}

	/**
	 * @param worldId the worldId to set
	 */
	public void setWorldId(int worldId) {
		this.worldId = worldId;
	}

	
	
	
}
