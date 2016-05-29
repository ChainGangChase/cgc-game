/*
 * @(#)WorldTile.java		0.3 14/4/14
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.maps;

import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.terrain.Terrain;

/*
 * Holds data about individual world tiles
 * 
 * @version 0.3 14/4/15
 * @author Christopher Rider
 */
public class WorldTile 
{
	private Terrain terrain;
	private Array<GameEntity> items;
	
	/*
	 * Creates a new WorldTile object
	 */
	public WorldTile()
	{
		terrain = null;
		items = new Array<GameEntity>(3);
	}
	
	/*
	 * Set the terrain for this tile
	 * 
	 * @param newTerrain			The terrain to add
	 */
	public void setTerrain(Terrain newTerrain)
	{
		terrain = newTerrain;
	}
	
	/*
	 * Get the terrain for this tile
	 * 
	 * @return						The terrain used by this tile
	 */
	public Terrain getTerrain()
	{
		return terrain;
	}
	
	/*
	 * Add a GameEntity to this tile
	 * 
	 * @param newEntity				The grid entity to be added
	 */
	public void addEntity(GameEntity newEntity)
	{
		items.add(newEntity);
	}
	
	/*
	 * Remove a GameEntity from this tile
	 * 
	 * @param toRemove				The grid entity to be removed
	 * @return						True if the entity was found and removed, else false
	 */
	public boolean removeEntity(GameEntity toRemove)
	{
		return items.removeValue(toRemove, true);
	}
	
	/*
	 * Get all grid entites in this tile
	 * 
	 * @return						The list of entites added to this tile
	 */
	public Array<GameEntity> getEntities()
	{
		return items;
	}
} // End class