/*
 * @(#)LayerHandler.java		0.3 14/4/15
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.helpers;

import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.maps.WorldGrid;

/*
 * Creates and handles graphic layers for the game world
 * 
 * @version 0.3 14/4/15
 * @author Christopher Rider
 */
public class LayerHandler 
{
	// Graphic layer variables
	public static final int background = 0;
	public static final int terrain = background+1;
	public static final int sensor = terrain+1;
	public static final int corpses = sensor+1;
	public static final int ground = corpses+1; // 4
	public static final int fenceHigh = ground+1;
	public static final int chains = fenceHigh+1;
	public static final int mid = chains+1; // 7
	public static final int heads = mid+1;
	public static final int above = heads+1;
	public static final int high = above+1; // 10
	public static final int bossTop = high+1;
	public static final int bossPlayer = bossTop+1;
	public static final int projectile = bossPlayer+1;
	public static final int aerial = projectile+1;
	private int numLayers = aerial+1;
	private Array<WorldGrid> layers;
	
	// Graphic types of layers
	public static final int CORPSE = corpses;
	public static final int LOW = ground;
	public static final int MID = mid;
	public static final int HIGH = aerial;
	
	/*
	 * Creates a new LayerHandler object
	 */
	public LayerHandler()
	{
		createLayers();
	}
	
	/*
	 * Gets the different body layers for the world
	 * 
	 * @return						The array of layers containing all game entities
	 */
	public Array<WorldGrid> getLayers()
	{
		return layers;
	}
	
	/*
	 * Gets a specific body layer of the world
	 * 
	 * @param layer					The layer to get from the handler
	 * @return						The array of bodies on the layer
	 */
	public WorldGrid getLayer(int layer)
	{
		return layers.get(layer);
	}
	
	/*
	 * Creates the layers for graphics
	 */
	public void createLayers()
	{
		layers = new Array<WorldGrid>(numLayers);
		
		for (int i = 0; i < numLayers; i++)
		{
			if (i == chains || i == corpses || i == projectile || i == aerial)
			{
				layers.add(new WorldGrid(true));
			}
			else
			{
				layers.add(new WorldGrid(false));
			}
		}
	}
	
	/*
	 * Add a GameEntity to a grid layer
	 * 
	 * @param x						The X-coordinate for this entity
	 * @param y						The Y-coordinate for this entity
	 * @param ge					The entity to add
	 * @param layer					The grid layer to add this entity to
	 */
	public void addEntityToGridLayer(int x, int y, GameEntity ge, int layer)
	{
		layers.get(layer).addEntityToGrid(x, y, ge);
	}
	
	/*
	 * Remove a GameEntity from a grid layer
	 * 
	 * @param x						The X-coordinate for this entity
	 * @param y						The Y-coordinate for this entity
	 * @param ge					The entity to add
	 * @param layer					The grid layer to add this entity to
	 */
	public void removeEntityFromGridLayer(int x, int y, GameEntity ge, int layer)
	{
		layers.get(layer).removeEntityFromGrid(x,  y, ge);
	}
	
	/*
	 * Add a non-grid GameEntity to a layer
	 * 
	 * @param ge					The entity to add
	 * @param layer					The layer to add this entity to
	 */
	public void addEntityToLayer(GameEntity ge, int layer)
	{
		layers.get(layer).addEntity(ge);
	}
	
	/*
	 * Remove a non-grid GameEntity from a layer
	 * 
	 * @param ge					The entity to remove
	 * @param layer					The layer to remove this entity from
	 */
	public void removeEntityFromLayer(GameEntity ge, int layer)
	{
		layers.get(layer).removeEntity(ge);
	}
	
	/*
	 * Tells the handler to add a new chunk to the world grid
	 * 
	 * @param end					Whether or not this chunk is a chunk at the end of the map
	 */
	public void addNewChunk(boolean end)
	{
		for (int i = 0; i < numLayers; i++)
		{
			layers.get(i).addChunk(end);
		}
	}
	
	/*
	 * Gets the length of the world in box2D distance
	 * 
	 * @return						The number of rows long the world is
	 */
	public int getWorldLength()
	{
		return layers.get(0).getWorldLength();//layers.get(0).getNumChunks() * MapBuilder.chunkHeight - 2;
	}
} // End class