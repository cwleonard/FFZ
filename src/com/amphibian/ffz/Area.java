package com.amphibian.ffz;

import java.util.ArrayList;
import java.util.List;

public class Area {

	private int id;
	
	private String name;
	
	private String description;

	private Tile[][] groundGrid;
	
	private Object[][] objectGrid;
	
	private int worldId;
	
	
	
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
	public Tile[][] getGroundGrid() {
		return groundGrid;
	}

	/**
	 * @param groundGrid the groundGrid to set
	 */
	public void setGroundGrid(Tile[][] groundGrid) {
		this.groundGrid = groundGrid;
	}

	/**
	 * @return the objectGrid
	 */
	public Object[][] getObjectGrid() {
		return objectGrid;
	}
	
	public List<Object> getObjects() {
		
		List<Object> list = new ArrayList<Object>();
        for (int io = 0; io < objectGrid.length; io++) {
            Object[] innerArray = objectGrid[io];
            if (innerArray != null) {
                for (int jo = 0; jo < innerArray.length; jo++) {
                    Object o = innerArray[jo];
                    if (o != null) {
                        list.add(o);
                    }
                }
            }
        }
        return list;
		
	}

	/**
	 * @param objectGrid the objectGrid to set
	 */
	public void setObjectGrid(Object[][] objectGrid) {
		this.objectGrid = objectGrid;
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
