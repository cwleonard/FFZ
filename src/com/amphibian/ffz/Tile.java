package com.amphibian.ffz;

public class Tile {

	private int id;
	
	private String name;
	
	private String description;
	
	private boolean water;
	
	private boolean rock;
	
	/**
	 * @return the id
	 */
	public int getId() {
		if (id == 59) {
			return 46; 
		} else {
			return id;
		}
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
	
	public String getNameJS() {
		if (name != null) {
			return name.toLowerCase().replaceAll("\\s+", "_");
		} else {
			return name;
		}
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
	 * @return the water
	 */
	public boolean isWater() {
		return water;
	}

	/**
	 * @param water the water to set
	 */
	public void setWater(boolean water) {
		this.water = water;
	}

	/**
	 * @return the rock
	 */
	public boolean isRock() {
		return rock;
	}

	/**
	 * @param rock the rock to set
	 */
	public void setRock(boolean rock) {
		this.rock = rock;
	}

	
}
