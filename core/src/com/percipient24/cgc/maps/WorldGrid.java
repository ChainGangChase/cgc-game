/*
 * @(#)WorldGrid.java		0.3 14/4/15
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.maps;

import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.terrain.Terrain;

/*
 * Holds data about the world grid
 * 
 * @version 0.3 14/4/15
 * @author Christopher Rider
 */
public class WorldGrid 
{
	private Array<Array<WorldTile>> grid;
	private Array<GameEntity> gridlessEntities;
	private int numChunks = 0;
	
	/*
	 * Creates a new WorldGrid object
	 * 
	 * @param gridless				Whether or not the grid should be left null (if true, no grid will be created)
	 */
	public WorldGrid(boolean gridless)
	{
		if (gridless)
		{
			grid = null;
		}
		else
		{
			grid = new Array<Array<WorldTile>>();
		}
		gridlessEntities = new Array<GameEntity>();
	}
	
	/*
	 * Add an empty chunk to the world based on the chunkHeight and chunkWidth in MapBuilder
	 * 
	 * @param end					Whether or not this is the chunk at the end of the level (if so, it will add extra blank space)
	 */
	public void addChunk(boolean end)
	{
		if (hasGrid())
		{
			int height = MapBuilder.chunkHeight;
			if (end)
			{
				height += 22;
			}
			
			for (int i = 0; i < height; i++)
			{
				Array<WorldTile> temp = new Array<WorldTile>();
				for (int j = 0; j < MapBuilder.chunkWidth; j++)
				{
					temp.add(new WorldTile());
				}
				grid.add(temp);
			}
			
			numChunks++;
		}
	}
	
	/*
	 * Set the terrain for the specified position
	 * 
	 * @param x						The X coordinate of the tile
	 * @param y						The Y coordinate of the tile
	 * @param newTerrain			The terrain to add to the tile
	 */
	public void setTerrain(int x, int y, Terrain newTerrain)
	{
		if (hasGrid())
		{
			if (grid.get(y).get(x).getTerrain() == null)
			{
				grid.get(y).get(x).setTerrain(newTerrain);
			}
			else
			{
				CGCWorld.getWorld().destroyBody(grid.get(y).get(x).getTerrain().getBody());
				grid.get(y).get(x).setTerrain(newTerrain);
			}
		}
	}
	
	/*
	 * Get the Terrain at the specified position
	 * 
	 * @param x						The X coordinate of the tile
	 * @param y						The Y coordinate of the tile
	 * @return						The Terrain of the tile
	 */
	public Terrain getTerrain(int x, int y)
	{
		return grid.get(y).get(x).getTerrain();
	}
	
	/*
	 * Add a GameEntity to the grid - must be a grid-based entity
	 * 
	 * @param x						The X coordinate of the tile
	 * @param y						The Y coordinate of the tile
	 * @param newEntity				The GameEntity to add to the tile
	 */
	public void addEntityToGrid(int x, int y, GameEntity newEntity)
	{
		if (hasGrid())
		{
			grid.get(y).get(x).addEntity(newEntity);
		}
	}
	
	/*
	 * Get the list of GameEntities at the specified position
	 * 
	 * @param x						The X coordinate of the tile
	 * @param y						The Y coordinate of the tile
	 * @return						The list of GameEntities in the tile
	 */
	public Array<GameEntity> getEntitiesInGrid(int x, int y)
	{
		return grid.get(y).get(x).getEntities();
	}
	
	/*
	 * Remove a GameEntity from the specified tile
	 * 
	 * @param x						The X coordinate of the tile
	 * @param y						The Y coordinate of the tile
	 * @param toRemove				The GameEntity to remove from the tile
	 * @return						True if the GameEntity was found and removed, else false
	 */
	public boolean removeEntityFromGrid(int x, int y, GameEntity toRemove)
	{
		return grid.get(y).get(x).removeEntity(toRemove);
	}
	
	/*
	 * Add a non-grid GameEntity to the world layer
	 * 
	 * @param newEntity				The GameEntity to add to this layer
	 */
	public void addEntity(GameEntity newEntity)
	{
		gridlessEntities.add(newEntity);
	}
	
	/*
	 * Get all of the non-grid GameEntities on this world layer
	 * 
	 * @return						The list of all non-grid GameEntities
	 */
	public Array<GameEntity> getEntities()
	{
		return gridlessEntities;
	}
	
	/*
	 * Remove a non-grid GameEntity from this world layer
	 * 
	 * @param toRemove				The GameEntity to remove from this layer
	 * @return						True if the GameEntity was found and removed, else false
	 */
	public boolean removeEntity(GameEntity toRemove)
	{
		return gridlessEntities.removeValue(toRemove, true);
	}
	
	/*
	 * Get how long the map is in chunks
	 * 
	 * @return						How long the world is, in chunks, according to this layer
	 */
	public int getNumChunks()
	{
		return numChunks;
	}
	
	/*
	 * Gets the length of the world (in Box2D co-ordinates)
	 * 
	 * @return						The length of the world in Box2D co-ordinates
	 */
	public int getWorldLength()
	{
		return grid.size;
	}
	
	/*
	 * Gets whether or not this WorldGrid has a grid or not
	 * 
	 * @return						Whether or not the grid exists
	 */
	public boolean hasGrid()
	{
		if (grid == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
} // End class