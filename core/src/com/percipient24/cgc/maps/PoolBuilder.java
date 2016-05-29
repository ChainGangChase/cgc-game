 /*
 * @(#)PoolBuilder.java		0.3 14/5/19
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.maps;

import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.enums.TerrainType;

/*
 * Contains logic for creating randomly generated pools of objects
 * 
 * @version 0.3 14/5/19
 * @author Christopher Rider
 */
public class PoolBuilder 
{
	private static final int DEPTH_MAX = 20;
	
	/*
	 * Creates a pool of an object (in any direction)
	 * 
	 * @param terrainGrid			The grid of terrain types for storing the terrain data before generation
	 * @param startX				The initial X-position of the pool
	 * @param startY				The initial Y-position of the pool
	 * @param poolUsed				How much of the pool of terrain has been used so far
	 * @param POOL_SIZE				The number of the specified terrain type to generate
	 * @param SPAWN_CHANCE			How likely the terrain is to spread to a nearby tile
	 * @param maxX					The highest X-position the pool can be at
	 * @param maxY					The highest Y-position the pool can be at
	 * @param fill					The kind of terrain for this pool to be
	 */
	public static int createPool(Array<Array<TerrainType>> terrainGrid, int startX, int startY, int poolUsed, 
			final int POOL_SIZE, final int SPAWN_CHANCE, int maxX, int maxY, TerrainType fill, int depth)
	{

		if (poolUsed >= POOL_SIZE || depth >= DEPTH_MAX)
		{
			return poolUsed;
		}
		
		if (terrainGrid.get(startX).get(startY) != fill)
		{
			terrainGrid.get(startX).set(startY, fill);
			poolUsed++;
		}
		
		if (startX - 1 >= 0 && CGCWorld.getRandom().nextInt(100) < SPAWN_CHANCE)
		{
			poolUsed = createPool(terrainGrid, startX-1, startY, poolUsed, POOL_SIZE, SPAWN_CHANCE, maxX, maxY, fill, depth+1);
		}
		
		if (startX + 1 < maxX && CGCWorld.getRandom().nextInt(100) < SPAWN_CHANCE)
		{
			poolUsed = createPool(terrainGrid, startX+1, startY, poolUsed, POOL_SIZE, SPAWN_CHANCE, maxX, maxY, fill, depth+1);
		}
		
		if (startY - 1 >= 0 && CGCWorld.getRandom().nextInt(100) < SPAWN_CHANCE)
		{
			poolUsed = createPool(terrainGrid, startX, startY-1, poolUsed, POOL_SIZE, SPAWN_CHANCE, maxX, maxY, fill, depth+1);
		}
		
		if (startY + 1 < maxY && CGCWorld.getRandom().nextInt(100) < SPAWN_CHANCE)
		{
			poolUsed = createPool(terrainGrid, startX, startY+1, poolUsed, POOL_SIZE, SPAWN_CHANCE, maxX, maxY, fill, depth+1);
		}
		
		return poolUsed;
	}
	
	/*
	 * Creates a pool of an object (horizontal only)
	 * 
	 * @param terrainGrid			The grid of terrain types for storing the terrain data before generation
	 * @param startX				The initial X-position of the pool
	 * @param startY				The initial Y-position of the pool
	 * @param poolUsed				How much of the pool of terrain has been used so far
	 * @param POOL_SIZE				The number of the specified terrain type to generate
	 * @param SPAWN_CHANCE			How likely the terrain is to spread to a nearby tile
	 * @param maxX					The highest X-position the pool can be at
	 * @param maxY					The highest Y-position the pool can be at
	 * @param fill					The kind of terrain for this pool to be
	 */
	public static int createHorPool(Array<Array<TerrainType>> terrainGrid, int startX, int startY, int poolUsed, 
			final int POOL_SIZE, final int SPAWN_CHANCE, int maxX, int maxY, TerrainType fill, int depth)
	{

		if (poolUsed >= POOL_SIZE || depth >= DEPTH_MAX)
		{
			return poolUsed;
		}
		
		if (terrainGrid.get(startX).get(startY) != fill)
		{
			terrainGrid.get(startX).set(startY, fill);
			poolUsed++;
		}
		
		if (startX - 1 >= 0 && CGCWorld.getRandom().nextInt(100) < SPAWN_CHANCE)
		{
			poolUsed = createHorPool(terrainGrid, startX-1, startY, poolUsed, POOL_SIZE, SPAWN_CHANCE, maxX, maxY, fill, depth+1);
		}
		
		if (startX + 1 < maxX && CGCWorld.getRandom().nextInt(100) < SPAWN_CHANCE)
		{
			poolUsed = createHorPool(terrainGrid, startX+1, startY, poolUsed, POOL_SIZE, SPAWN_CHANCE, maxX, maxY, fill, depth+1);
		}
		
		return poolUsed;
	}
} // End class
