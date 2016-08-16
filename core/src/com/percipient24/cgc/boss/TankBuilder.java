/*
 * @(#)TankBuilder.java		0.3 14/4/15
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.boss;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.art.TextureAnimationDrawer;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.Fence;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.Tree;
import com.percipient24.cgc.entities.Wall;
import com.percipient24.cgc.entities.boss.Boss;
import com.percipient24.cgc.entities.boss.Tank;
import com.percipient24.cgc.entities.players.GunCop;
import com.percipient24.cgc.entities.terrain.Mud;
import com.percipient24.cgc.entities.terrain.Terrain;
import com.percipient24.cgc.entities.terrain.Water;
import com.percipient24.cgc.maps.PoolBuilder;
import com.percipient24.cgc.maps.WorldGrid;
import com.percipient24.enums.BossType;
import com.percipient24.enums.EntityType;
import com.percipient24.enums.TerrainType;

/*
 * Creates a tank boss fight
 * 
 * @version 0.3 14/4/15
 * @author Christopher Rider
 */
public class TankBuilder extends BossBuilder 
{
	private boolean aiControl;
	private GunCop tankControl;
	
	/*
	 * Creates a BossBuilder object
	 * 
	 * @param type					The type of boss for this builder
	 * @param ai					Whether or not this boss is an AI version
	 */
	public TankBuilder(BossType type, boolean ai)
	{
		super (type);
		
		aiControl = ai;
		buildBossArea();
	}
	
	/*
	 * @see com.percipient24.cgc.boss.BossBuilder#buildBossArea()
	 */
	protected void buildBossArea()
	{
		levelLength = 20;
		Body wall;
		GameEntity ge;
		
		for (int chunk = 0; chunk < levelLength; chunk++)
		{
			if (chunk == levelLength-1)
			{
				CGCWorld.getLH().addNewChunk(true);
			}
			else
			{
				CGCWorld.getLH().addNewChunk(false);
			}
			
			wall = CGCWorld.getBF().createRectangle(0, 6+11*chunk, 1, 11, BodyType.StaticBody, 
					BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
			ge = new Wall(TextureAnimationDrawer.vwallAnims[0], null,
					TextureAnimationDrawer.vwallAnims[0], EntityType.WALL,
					wall, true);
			wall.setUserData(ge);

			ge.addToWorldLayers(CGCWorld.getLH());
			
			wall = CGCWorld.getBF().createRectangle(19, 6+11*chunk, 1, 11, BodyType.StaticBody, 
					BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
			ge = new Wall(TextureAnimationDrawer.vwallAnims[0], null,
					TextureAnimationDrawer.vwallAnims[0], EntityType.WALL,
					wall, true);
			wall.setUserData(ge);

			ge.addToWorldLayers(CGCWorld.getLH());
			
			if (chunk == 0)
			{
				continue;
			}
			
			if (chunk == levelLength-1)
			{
				for (int i = 0; i < 2; i++)
				{
					wall = CGCWorld.getBF().createRectangle(0, 6 + 11 * (chunk+i+1),
							1, 11, BodyType.StaticBody,
							BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
					ge = new Wall(TextureAnimationDrawer.vwallAnims[0], null,
							TextureAnimationDrawer.vwallAnims[1], EntityType.WALL, wall, true);
					wall.setUserData(ge);
					ge.addToWorldLayers(CGCWorld.getLH());
					
					wall = CGCWorld.getBF().createRectangle(19, 6 + 11 * (chunk+i+1),
							1, 11, BodyType.StaticBody,
							BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
					ge = new Wall(TextureAnimationDrawer.vwallAnims[0], null,
							TextureAnimationDrawer.vwallAnims[1], EntityType.WALL, wall, true);
					wall.setUserData(ge);
					ge.addToWorldLayers(CGCWorld.getLH());
				}
			}
		}
		
		// Generate a world
		Body b;
		WorldGrid grid = CGCWorld.getLH().getLayer(LayerHandler.background);
		
		Array<Array<TerrainType>> terrainGrid = createTerrain();
		
		for (int x = 0; x < terrainGrid.size; x++)
		{
			for (int y = 0; y < terrainGrid.get(x).size; y++)
			{
				switch(terrainGrid.get(x).get(y))
				{
					case WATER_STILL: 
						b = CGCWorld.getBF().createRectangle(x+1, y+14, 0.89f, 0.89f, BodyType.StaticBody, 
								BodyFactory.CAT_TERRAIN, BodyFactory.MASK_TERRAIN);
						b.getFixtureList().get(0).setSensor(true);
						ge = new Water(null, null, null, EntityType.WATER, b, 0, 7, 7, 7, 7);
						b.setUserData(ge);
						grid.setTerrain(x, y+14, (Water) ge);
						break;
					case WATER_RIVER_LEFT:
						b = CGCWorld.getBF().createRectangle(x+1, y+14, 0.89f, 0.89f, BodyType.StaticBody, 
								BodyFactory.CAT_TERRAIN, BodyFactory.MASK_TERRAIN);
						b.getFixtureList().get(0).setSensor(true);
						ge = new Water(null, null, null, EntityType.WATER, b, 5, 7, 7, 7, 7);
						b.setUserData(ge);
						grid.setTerrain(x, y+14, (Water) ge);
						break;
					case WATER_RIVER_RIGHT:
						b = CGCWorld.getBF().createRectangle(x+1, y+14, 0.89f, 0.89f, BodyType.StaticBody, 
								BodyFactory.CAT_TERRAIN, BodyFactory.MASK_TERRAIN);
						b.getFixtureList().get(0).setSensor(true);
						ge = new Water(null, null, null, EntityType.WATER, b, 1, 7, 7, 7, 7);
						b.setUserData(ge);
						grid.setTerrain(x, y+14, (Water) ge);
						break;
					case MUD:
						b = CGCWorld.getBF().createRectangle(x+1, y+14, 0.89f, 0.89f, BodyType.StaticBody, 
								BodyFactory.CAT_TERRAIN, BodyFactory.MASK_TERRAIN);
						b.getFixtureList().get(0).setSensor(true);
						ge = new Mud(null, null, null, EntityType.MUD, b, 7, 7, 7, 7);
						b.setUserData(ge);
						CGCWorld.getLH().getLayer(LayerHandler.background).setTerrain(x, y+14, (Mud) ge);
					default: break;
				}
			}
		}
		
		Array<Array<TerrainType>> entityGrid = createEntities();
		
		for (int x = 0; x < entityGrid.size; x++)
		{
			for (int y = 0; y < entityGrid.get(x).size; y++)
			{
				switch(entityGrid.get(x).get(y))
				{
					case TREE:
						b = CGCWorld.getBF().createCircle(x+1, y+14, 0.9f, BodyType.StaticBody, 
								BodyFactory.CAT_TREE, BodyFactory.MASK_TREE);		
						ge = new Tree(TextureAnimationDrawer.treeAnims[0], TextureAnimationDrawer.treeAnims[1], TextureAnimationDrawer.treeAnims[2],
								EntityType.TREE, b, x, y+14);
						b.setUserData(ge);
						ge.addToWorldLayers(CGCWorld.getLH());
						break;
					case POST:
						b = CGCWorld.getBF().createCircle(x+1, y+14, 0.1f, BodyType.StaticBody, 
								BodyFactory.CAT_FENCE, BodyFactory.MASK_FENCE);
						ge = new Fence(TextureAnimationDrawer.postAnims[0], TextureAnimationDrawer.postAnims[1],
								null, EntityType.POST, b, x, y+14);
						b.setUserData(ge);
						ge.addToWorldLayers(CGCWorld.getLH());
						
						if (x-1 >= 0) // If the post is not to the far left...
						{
							if (entityGrid.get(x-1).get(y) == TerrainType.POST) // ... add a fence to the left if there's a post there...
							{
								b = CGCWorld.getBF().createRectangle(x+1-0.25f, y+14, 0.5f, 0.08333f, BodyType.StaticBody, 
										BodyFactory.CAT_FENCE, BodyFactory.MASK_FENCE);
								ge = new Fence(TextureAnimationDrawer.fenceAnims[0], TextureAnimationDrawer.fenceAnims[1],
										null, EntityType.FENCE, b, x, y+14);
								b.setUserData(ge);
								ge.addToWorldLayers(CGCWorld.getLH());
							}
						}
						else // ... otherwise add a fence to the left if it's against the wall
						{
							b = CGCWorld.getBF().createRectangle(x+1-0.25f, y+14, 0.5f, 0.08333f, BodyType.StaticBody, 
									BodyFactory.CAT_FENCE, BodyFactory.MASK_FENCE);
							ge = new Fence(TextureAnimationDrawer.fenceAnims[0], TextureAnimationDrawer.fenceAnims[1],
									null, EntityType.FENCE, b, x, y+14);
							b.setUserData(ge);
							ge.addToWorldLayers(CGCWorld.getLH());
						}
						
						if (x+1 <= 17)
						{
							if (entityGrid.get(x+1).get(y) == TerrainType.POST)
							{
								b = CGCWorld.getBF().createRectangle(x+1+0.25f, y+14, 0.5f, 0.08333f, BodyType.StaticBody, 
										BodyFactory.CAT_FENCE, BodyFactory.MASK_FENCE);
								ge = new Fence(TextureAnimationDrawer.fenceAnims[0], TextureAnimationDrawer.fenceAnims[1],
										null, EntityType.FENCE, b, x, y+14);
								b.setUserData(ge);
								ge.addToWorldLayers(CGCWorld.getLH());
							}
						}
						else
						{
							b = CGCWorld.getBF().createRectangle(x+1+0.25f, y+14, 0.5f, 0.08333f, BodyType.StaticBody, 
									BodyFactory.CAT_FENCE, BodyFactory.MASK_FENCE);
							ge = new Fence(TextureAnimationDrawer.fenceAnims[0], TextureAnimationDrawer.fenceAnims[1],
									null, EntityType.FENCE, b, x, y+14);
							b.setUserData(ge);
							ge.addToWorldLayers(CGCWorld.getLH());
						}
						break;
					default: break;
				}
			}
		}
		
		// Change graphics for terrain to match nearby other terrain
		for (int row = 0; row < levelLength*11; row++)
		{
			for (int col = 0; col < 18; col++)
			{
				Terrain t = CGCWorld.getLH().getLayer(LayerHandler.background).getTerrain(col, row);
				
				if (t == null)
				{
					continue;
				}
				
				Terrain tU = null;
				Terrain tD = null;
				Terrain tL = null;
				Terrain tR = null;
				Terrain tUR = null;
				Terrain tDR = null;
				Terrain tDL = null;
				Terrain tUL = null;
				
				if (row+1 < levelLength*11)
				{
					tU = CGCWorld.getLH().getLayer(LayerHandler.background).getTerrain(col, row+1);
				}
				if (row-1 >= 0)
				{
					tD = CGCWorld.getLH().getLayer(LayerHandler.background).getTerrain(col, row-1);
				}
				if (col+1 < 18)
				{
					tR = CGCWorld.getLH().getLayer(LayerHandler.background).getTerrain(col+1, row);
				}
				if (col-1 >= 0)
				{
					tL = CGCWorld.getLH().getLayer(LayerHandler.background).getTerrain(col-1, row);
				}
				if (tU != null && tR != null)
				{
					tUR = CGCWorld.getLH().getLayer(LayerHandler.background).getTerrain(col+1, row+1);
				}
				if (tD != null && tR != null)
				{
					tDR = CGCWorld.getLH().getLayer(LayerHandler.background).getTerrain(col+1, row-1);
				}
				if (tD != null && tL != null)
				{
					tDL = CGCWorld.getLH().getLayer(LayerHandler.background).getTerrain(col-1, row-1);
				}
				if (tU != null && tL != null)
				{
					tUL = CGCWorld.getLH().getLayer(LayerHandler.background).getTerrain(col-1, row+1);
				}
				
				if (t instanceof Terrain)
				{
					for (int i = 0; i < 4; i++) // 0 starts in top right, goes clockwise
					{
						boolean one = false;
						boolean two = false;
						boolean three = false;
					
						if (i == 0)
						{
							if (tU != null && tU instanceof Terrain)
								one = true;
							if ((tUR != null && tUR instanceof Terrain) || col == 17)
								two = true;
							if ((tR != null && tR instanceof Terrain) || col == 17)
								three = true;
							
							t.setTopRight(calcNewImage(one, two, three));
						}
						else if (i == 1)
						{
							if ((tR != null && tR instanceof Terrain) || col == 17)
								one = true;
							if ((tDR != null && tDR instanceof Terrain) || col == 17)
								two = true;
							if (tD != null && tD instanceof Terrain)
								three = true;
							
							t.setBotRight(calcNewImage(one, two, three));
						}
						else if (i == 2)
						{
							if (tD != null && tD instanceof Terrain)
								one = true;
							if ((tDL != null && tDL instanceof Terrain) || col == 0)
								two = true;
							if ((tL != null && tL instanceof Terrain) || col == 0)
								three = true;
							
							t.setBotLeft(calcNewImage(one, two, three));
						}
						else
						{
							if ((tL != null && tL instanceof Terrain) || col == 0)
								one = true;
							if ((tUL != null && tUL instanceof Terrain) || col == 0)
								two = true;
							if (tU != null && tU instanceof Terrain)
								three = true;
							
							t.setTopLeft(calcNewImage(one, two, three));
						}
					}
				} // End terrain image fixing
			}
		}
	}
	
	/*
	 * Sets the player who is driving the tank for Tank fight
	 * 
	 * @param newController			The GunCop driving the tank
	 */
	public void setTankController(GunCop newController)
	{
		tankControl = newController;
	}
	
	/*
	 * @see com.percipient24.cgc.boss.BossBuilder#createBoss()
	 */
	public Boss createBoss()
	{
		Boss boss;
		Body bossBody = CGCWorld.getBF().createRectangle(9.5f, 0, 20f, 0.5f, BodyType.DynamicBody, 
			BodyFactory.CAT_BOSS, BodyFactory.MASK_BOSS);
		if (aiControl)
		{
			boss = new Tank(TextureAnimationDrawer.tankAnims[0], TextureAnimationDrawer.tankAnims[1],
					TextureAnimationDrawer.tankAnims[2], EntityType.TANK, bossBody, aiControl, null);
		}
		else
		{
			boss = new Tank(TextureAnimationDrawer.tankAnims[0], TextureAnimationDrawer.tankAnims[1],
					TextureAnimationDrawer.tankAnims[2], EntityType.TANK, bossBody, aiControl, tankControl.getTarget());
		}
		bossBody.setUserData(boss);
		bossBody.setFixedRotation(true);
		bossBody.setLinearDamping(50.0f);

		boss.addToWorldLayers(CGCWorld.getLH());
		
		return boss;
	}
	
	/*
	 * Creates a terrain grid for the level so that it can actually build a level
	 * 
	 * @return						The grid to build the world with
	 */
	private Array<Array<TerrainType>> createTerrain()
	{
		int minY = 0;
		int maxY = levelLength*11-14;
		int minX = 0;
		int maxX = 18;
		
		// Create the empty terrain grid
		Array<Array<TerrainType>> terrainGrid = new Array<Array<TerrainType>>(18);
		for (int x = minX; x < maxX; x++)
		{
			terrainGrid.add(new Array<TerrainType>(maxY));
			for (int y = minY; y < maxY; y++)
			{
				terrainGrid.get(x).add(TerrainType.NONE);
			}
		}
		
		// Mud generation
		final int MUD_POOL_MAX = 1800;
		final int MUD_CHANCE_MAX = 25;
		final int MUD_CHANCE_MIN = 4;
		int mudPool = CGCWorld.getRandom().nextInt(MUD_POOL_MAX);
		int mudChance = CGCWorld.getRandom().nextInt(MUD_CHANCE_MAX-MUD_CHANCE_MIN)+MUD_CHANCE_MIN;
		int mudUsed = 0;
		int startPosX = 0;
		int startPosY = 0;
		
		while (mudUsed < mudPool)
		{
			// Position the mud pool
			startPosX = CGCWorld.getRandom().nextInt(maxX);
			startPosY = CGCWorld.getRandom().nextInt(maxY);
			
			mudUsed = PoolBuilder.createPool(terrainGrid, startPosX, startPosY, mudUsed, 
					mudPool, mudChance, maxX, maxY, TerrainType.MUD, 0);
		}
		
		// Lake generation
		final int LAKE_POOL_MAX = 950;
		final int LAKE_CHANCE_MAX = 30;
		final int LAKE_CHANCE_MIN = 7;
		int lakesUsed = 0;
		int lakePool = CGCWorld.getRandom().nextInt(LAKE_POOL_MAX);
		int lakeChance = CGCWorld.getRandom().nextInt(LAKE_CHANCE_MAX-LAKE_CHANCE_MIN)+LAKE_CHANCE_MIN;
		
		while (lakesUsed < lakePool)
		{
			// Position the lake
			startPosX = CGCWorld.getRandom().nextInt(maxX);
			startPosY = CGCWorld.getRandom().nextInt(maxY);
			
			lakesUsed = PoolBuilder.createPool(terrainGrid, startPosX, startPosY, lakesUsed, 
					lakePool, lakeChance, maxX, maxY, TerrainType.WATER_STILL, 0);
		}
		
		// Create rivers
		final int RIVER_VAL = 90;
		final int RIVER_POOL = CGCWorld.getRandom().nextInt(RIVER_VAL) * 18;
		int riversUsed = 0;
		
		while (riversUsed < RIVER_POOL)
		{
			int yPos = CGCWorld.getRandom().nextInt(maxY);
			
			for (int i = minX; i < maxX; i++)
			{
				terrainGrid.get(i).set(yPos, TerrainType.WATER_STILL);
				riversUsed++;
			}
		}
		
		// Make sure adjacent water tiles have the same direction
		for (int y = minY; y < maxY; y++)
		{
			TerrainType t = terrainGrid.get(0).get(y);
			
			if (t == TerrainType.WATER_STILL || t == TerrainType.WATER_RIVER_LEFT || t == TerrainType.WATER_RIVER_RIGHT)
			{
				if (y-1 >= minY)
				{
					if (terrainGrid.get(0).get(y-1) == TerrainType.WATER_RIVER_LEFT ||
							terrainGrid.get(0).get(y-1) == TerrainType.WATER_RIVER_RIGHT)
					{
						for (int i = minX; i < maxX; i++)
						{
							terrainGrid.get(i).set(y, terrainGrid.get(i).get(y-1));
						}
					}
					else
					{
						terrainGrid.get(0).set(y, TerrainType.WATER_STILL);
					}
				}
				else
				{
					terrainGrid.get(0).set(y, TerrainType.WATER_STILL);
				}
				
				if (terrainGrid.get(0).get(y) == TerrainType.WATER_STILL)
				{
					TerrainType dir = CGCWorld.getRandom().nextInt(2) < 1 ? TerrainType.WATER_RIVER_LEFT : TerrainType.WATER_RIVER_RIGHT;
					for (int i = minX; i < maxX; i++)
					{
						terrainGrid.get(i).set(y, dir);
					}
				}
				
			}
		}
		
		return terrainGrid;
	}
	
	/*
	 * Creates an entity grid for the level so that it can actually build a level
	 * 
	 * @return						The grid to build the world entities with
	 */
	private Array<Array<TerrainType>> createEntities()
	{
		int minY = 0;
		int maxY = levelLength*11-14;
		int minX = 0;
		int maxX = 18;
		
		// Create the empty entity grid
		Array<Array<TerrainType>> entityGrid = new Array<Array<TerrainType>>(18);
		for (int x = minX; x < maxX; x++)
		{
			entityGrid.add(new Array<TerrainType>(maxY));
			for (int y = minY; y < maxY; y++)
			{
				entityGrid.get(x).add(TerrainType.NONE);
			}
		}
		
		// Forest generation
		final int FOREST_POOL_MAX = 990;
		final int FOREST_CHANCE_MIN = 1;
		final int FOREST_CHANCE_MAX = 8;
		int forestUsed = 0;
		int forestPool = CGCWorld.getRandom().nextInt(FOREST_POOL_MAX);
		int forestChance = CGCWorld.getRandom().nextInt(FOREST_CHANCE_MAX-FOREST_CHANCE_MIN)+FOREST_CHANCE_MIN;
		int startPosX = 0;
		int startPosY = 0;
		
		while (forestUsed < forestPool)
		{
			// Position the lake
			startPosX = CGCWorld.getRandom().nextInt(maxX);
			startPosY = CGCWorld.getRandom().nextInt(maxY);
			
			forestUsed = PoolBuilder.createPool(entityGrid, startPosX, startPosY, forestUsed, 
					forestPool, forestChance, maxX, maxY, TerrainType.TREE, 0);
		}
		
		// Fence generation
		final int FENCE_POOL_MAX = 500;
		final int FENCE_CHANCE_MIN = 1;
		final int FENCE_CHANCE_MAX = 45;
		int fencesUsed = 0;
		int fencePool = CGCWorld.getRandom().nextInt(FENCE_POOL_MAX);
		int fenceChance = CGCWorld.getRandom().nextInt(FENCE_CHANCE_MAX-FENCE_CHANCE_MIN)+FENCE_CHANCE_MIN;
		
		while (fencesUsed < fencePool)
		{
			// Position the lake
			startPosX = CGCWorld.getRandom().nextInt(maxX);
			startPosY = CGCWorld.getRandom().nextInt(maxY);
			
			fencesUsed = PoolBuilder.createHorPool(entityGrid, startPosX, startPosY, fencesUsed, 
					fencePool, fenceChance, maxX, maxY, TerrainType.POST, 0);
		}
		
		return entityGrid;
	}
} // End class