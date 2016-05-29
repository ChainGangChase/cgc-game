/*
 * @(#)MapBuilder.java		0.2 14/2/5
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.maps;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.percipient24.b2dhelpers.BodyFactory;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.Fence;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.Gate;
import com.percipient24.cgc.entities.GuardTower;
import com.percipient24.cgc.entities.Sensor;
import com.percipient24.cgc.entities.Track;
import com.percipient24.cgc.entities.Tree;
import com.percipient24.cgc.entities.Wall;
import com.percipient24.cgc.entities.terrain.Bridge;
import com.percipient24.cgc.entities.terrain.Mud;
import com.percipient24.cgc.entities.terrain.Terrain;
import com.percipient24.cgc.entities.terrain.Water;
import com.percipient24.enums.EntityType;

/*
 * Creates and stores a map
 * 
 * @version 0.2 14/2/5
 * @author Christopher Rider
 * @author Clayton Andrews
 */
public class MapBuilder 
{
	public static final int chunkHeight = 11;
	public static final int chunkWidth = 18;
	private BodyFactory bf;
	
	private String[] chunks;
	private int mLimit;
	private boolean start = false;
	private boolean end = false;
	private int mLength;
	private int prevLength;
	private int mapID;
	
	private Array<Gate> gates;
	private Array<Sensor> sensors;
	
	/*
	 * Creates a new map builder from a map list.
	 * Automatically creates the map and its objects.
	 * 
	 * @param mdata					The selected starting map
	 * @param mStart				Whether or not this is the first map to create
	 * @param mEnd					Whether or not this is the last map to create
	 * @param mPrevLength			The total length (in chunks) of all previous maps
	 * @param mID					The map ID
	 * @param tutorial				Whether or not to load the tutorial map
	 */
	public MapBuilder(String mdata, 
			Boolean mStart, Boolean mEnd, int mPrevLength,
			int mID, boolean tutorial)
	{
		bf = CGCWorld.getBF();
		chunks = mdata.split(";", -1);
		start = mStart;
		end = mEnd;
		mLength = 0;
		prevLength = mPrevLength;
		mapID = mID;
		buildMap(tutorial);
	}
	
	/*
	 * Gets the map ID for the map this is building
	 * 
	 * @return						This map's ID
	 */
	public int getMapID()
	{
		return mapID;
	}
	
	/*
	 * Gets the trigger distance for when the train should spawn
	 * 
	 * @return						The train's trigger distance
	 */
	public int gTrainTriggerDist()
	{
		return mLimit;
	}
	
	/*
	 * Gets the size of this map (in chunks)
	 * 
	 * @return						The size of this map in chunks
	 */
	public int gSize()
	{
		return mLength;
	}
	
	/*
	 * Gets the total length of this map and its predecessors
	 * 
	 * @return						The total length of the maps in chunks
	 */
	public int gTotalSize()
	{
		return prevLength+mLength;
	}
	
	/*
	 * Gets the total length of this map's predecessors
	 * 
	 * @return						The total length of the maps in chunks
	 */
	public int gLastMapsSize()
	{
		return prevLength;
	}
	
	/*
	 * Builds the map
	 * 
	 * @param tutorial				Whether or not to load the tutorial map
	 */
	private void buildMap(boolean tutorial)
	{
		gates = new Array<Gate>();
		sensors = new Array<Sensor>();
		
		int i = 0;
		
		if (start)
		{
			buildStart(tutorial);
			
			for(i = 0; i < chunks.length; i++)
			{
				createChunk(i + 1, chunks[i]);
			}
		}
		else
		{
			for(i = 0; i < chunks.length; i++)
			{
				createChunk(i, chunks[i]);
			}
		}
		
		if (end)
		{
			buildTrack(i);
			//buildBossArea(i + 1);
		}
		else if (start)
		{
			buildTrack(i + 1);
		}
		else
		{
			buildTrack(i);
		}
		
		pairGates();
	}
	
	/*
	 * Builds the start chunk of the game map
	 * 
	 * @param tutorial				Whether or not to load the tutorial map
	 */
	private void buildStart(boolean tutorial)
	{
		gates = new Array<Gate>();
		sensors = new Array<Sensor>();
		
		CGCWorld.getLH().addNewChunk(false);
		
		Body wall;
		GameEntity ge;
		
		wall = bf.createRectangle(0, 6, 1, 11, BodyType.StaticBody, 
				BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
		ge = new Wall(AnimationManager.vwallAnims[0], null,
				AnimationManager.vwallAnims[1], EntityType.WALL, 
				wall, true);
		wall.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		wall = bf.createRectangle(19, 6, 1, 11, BodyType.StaticBody, 
				BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
		ge = new Wall(AnimationManager.vwallAnims[0], null,
				AnimationManager.vwallAnims[1], EntityType.WALL, 
				wall, true);
		wall.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		wall = bf.createRectangle(9.5f, 0, 20, 1, BodyType.StaticBody, 
				BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
		ge = new Wall(AnimationManager.hwallAnim, null,
				AnimationManager.hwallAnim, EntityType.WALL, 
				wall, false);
		wall.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		//Only load these trees outside of the tutorial
		if (!tutorial)
		{
			for (int i = 0; i < 7; i++)
			{
				int x = CGCWorld.getRandom().nextInt(17)+1;
				int y = 0;
				
				while (y < 3 || y == 10)
				{
					y = CGCWorld.getRandom().nextInt(11);
				}

				Body b = CGCWorld.getBF().createCircle(x, y, 0.9f, BodyType.StaticBody,
						BodyFactory.CAT_TREE, BodyFactory.MASK_TREE);
				ge = new Tree(AnimationManager.treeAnims[0], AnimationManager.treeAnims[1],
						AnimationManager.treeAnims[2], EntityType.TREE, b, x-1, y);
				b.setUserData(ge);
				ge.addToWorldLayers(CGCWorld.getLH());
			}
		}
		
		mLength++;
	}
	
	/*
	 * Creates a chunk of the level
	 * 
	 * @param multiple				The number of chunks from the start this one is
	 * @param data					The world creation data for this chunk
	 */
	private void createChunk(int multiple, String data)
	{
		CGCWorld.getLH().addNewChunk(false);
		
		Body wall;
		GameEntity ge;
		
		wall = bf.createRectangle(0, 6 + 11 * (multiple + prevLength), 
				1, 11, BodyType.StaticBody, 
				BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
		ge = new Wall(AnimationManager.vwallAnims[0], null,
				AnimationManager.vwallAnims[1], EntityType.WALL, wall, true);
		wall.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		wall = bf.createRectangle(19, 6 + 11 * (multiple + prevLength), 
				1, 11, BodyType.StaticBody, 
				BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
		ge = new Wall(AnimationManager.vwallAnims[0], null,
				AnimationManager.vwallAnims[1], EntityType.WALL, wall, true);
		wall.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		String[] elements = data.split(":");
		for(int j = 0; j < elements.length; j++)
		{
			String[] fields = elements[j].split(",");

			if (fields.length >= 3)
			{
				int type = Integer.parseInt(fields[0]);
				int x = Integer.parseInt(fields[1]) + 1;
				int y = Integer.parseInt(fields[2]) + 11 * (multiple + prevLength);
				
				if (type == 0)
				{
					Body b = bf.createCircle(x, y, 0.9f, BodyType.StaticBody, 
							BodyFactory.CAT_TREE, BodyFactory.MASK_TREE);
					ge = new Tree(AnimationManager.treeAnims[0], AnimationManager.treeAnims[1],
							AnimationManager.treeAnims[2], EntityType.TREE, b, x-1, y);
					b.setUserData(ge);
					ge.addToWorldLayers(CGCWorld.getLH());
				}
				else if (type == 10)
				{
					Body b;
					
					int meta = Integer.parseInt(fields[3]);
		
					if ((meta & 0x1) > 0)
					{
						b = bf.createRectangle(x, y + 0.25f, 0.5f, 0.08333f, BodyType.StaticBody,
								BodyFactory.CAT_FENCE, BodyFactory.MASK_FENCE);
						b.setTransform(b.getPosition().x, b.getPosition().y, 90f * MathUtils.degRad);
						ge = new Fence(AnimationManager.fenceAnims[0], AnimationManager.fenceAnims[1],
								null, EntityType.FENCE, b, x-1, y);
						b.setUserData(ge);
						ge.addToWorldLayers(CGCWorld.getLH());
					}
		
					if ((meta & 0x2) > 0)
					{
						b = bf.createRectangle(x + 0.25f, y, 0.5f, 0.08333f, BodyType.StaticBody, 
								BodyFactory.CAT_FENCE, BodyFactory.MASK_FENCE);
						ge = new Fence(AnimationManager.fenceAnims[0], AnimationManager.fenceAnims[1],
								null, EntityType.FENCE, b, x-1, y);
						b.setUserData(ge);
						ge.addToWorldLayers(CGCWorld.getLH());
					}
		
					if ((meta & 0x4) > 0)
					{
						b = bf.createRectangle(x, y - 0.25f, 0.5f, 0.08333f, BodyType.StaticBody, 
								BodyFactory.CAT_FENCE, BodyFactory.MASK_FENCE);
						b.setTransform(b.getPosition().x, b.getPosition().y, 90f * MathUtils.degRad);
						ge = new Fence(AnimationManager.fenceAnims[0], AnimationManager.fenceAnims[1],
								null, EntityType.FENCE, b, x-1, y);
						b.setUserData(ge);
						ge.addToWorldLayers(CGCWorld.getLH());
					}
		
					if ((meta & 0x8) > 0)
					{
						b = bf.createRectangle(x - 0.25f, y, 0.5f, 0.08333f, BodyType.StaticBody, 
								BodyFactory.CAT_FENCE, BodyFactory.MASK_FENCE);
						ge = new Fence(AnimationManager.fenceAnims[0], AnimationManager.fenceAnims[1],
								null, EntityType.FENCE, b, x-1, y);
						b.setUserData(ge);
						ge.addToWorldLayers(CGCWorld.getLH());
					}
		
					b = bf.createCircle(x, y, 0.1f, BodyType.StaticBody, 
							BodyFactory.CAT_FENCE, BodyFactory.MASK_FENCE);
					ge = new Fence(AnimationManager.postAnims[0], AnimationManager.postAnims[1],
							null, EntityType.POST, b, x-1, y);
					b.setUserData(ge);
					ge.addToWorldLayers(CGCWorld.getLH());
				}
				else if (type == 11)
				{
					Body b;
					
					int gateOrient = Integer.parseInt(fields[3]);
					short gateID = Short.parseShort(fields[4]);
				
					b = bf.createRectangle(x, y, 1.0f, 0.2f, BodyType.StaticBody, 
							BodyFactory.CAT_FENCE, BodyFactory.MASK_FENCE);
					
					b.setTransform(b.getPosition(), gateOrient * (float)Math.PI/2);
						
					ge = new Gate(AnimationManager.gateAnim, AnimationManager.gateAnim, 
							null, EntityType.GATE, b, x-1, y);
					b.setUserData(ge);
					((Gate) ge).sID(gateID);
					ge.addToWorldLayers(CGCWorld.getLH());
				
					gates.add((Gate) b.getUserData());
				}
				else if (type == 12)
				{
					Body b;
					
					short playerID = Short.parseShort(fields[3]);
					short gateID = Short.parseShort(fields[4]);
		
					if (playerID <= CGCWorld.getNumPlayers())
					{
						b = bf.createSensor(x, y, 1.0f, 1.0f, BodyType.StaticBody, 
								BodyFactory.CAT_INTERACTABLE, BodyFactory.MASK_INTERACTABLE);
						
						ge = new Sensor(AnimationManager.sensorAnim, 
								null, null, EntityType.SENSOR, b, x-1, y);
						b.setUserData(ge);
						((Sensor) ge).sLockID(playerID);
						((Sensor) ge).sGID(gateID);
						ge.addToWorldLayers(CGCWorld.getLH());

						sensors.add((Sensor) b.getUserData());
					}
				}
				else if (type == 14)
				{
					Body b;
					
					b = bf.createRectangle(x, y, 1.0f, 1.0f, BodyType.StaticBody, 
							BodyFactory.CAT_INTERACTABLE, BodyFactory.MASK_INTERACTABLE);
					ge = new GuardTower(AnimationManager.towerAnims[0], null, AnimationManager.towerAnims[1], EntityType.TOWER, b, x-1, y);
					b.setUserData(ge);
					b.getFixtureList().get(0).setSensor(true);
					ge.addToWorldLayers(CGCWorld.getLH());
				}
				else if (type == 20 || type == 21)
				{
					Body b;
					
					int tr = Integer.parseInt(fields[3]);
					int br = Integer.parseInt(fields[4]);
					int bl = Integer.parseInt(fields[5]);
					int tl = Integer.parseInt(fields[6]);

					b = bf.createRectangle(x, y, 0.89f, 0.89f, BodyType.StaticBody, 
							BodyFactory.CAT_TETRAIN, BodyFactory.MASK_TETRAIN);
					b.getFixtureList().get(0).setSensor(true);
					
					if (type == 20)
					{
						ge = new Mud(null, null, null, EntityType.MUD, b, tr, br, bl, tl);
					}
					else
					{
						int flow = Integer.parseInt(fields[7]);
						ge = new Water(null, null, null, EntityType.WATER, b, flow, tr, br, bl, tl);
					}
					
					b.setUserData(ge);
					
					if (type == 20)
					{				
						CGCWorld.getLH().getLayer(LayerHandler.background).setTerrain(x-1, y, (Terrain) ge);
					}
					else
					{
						CGCWorld.getLH().getLayer(LayerHandler.background).setTerrain(x-1, y, (Terrain) ge);
					}
				}
				else if (type == 22)
				{
					Body b;
					
					int id = Integer.parseInt(fields[3]);

					b = bf.createRectangle(x, y, 0.7f, 0.7f, BodyType.StaticBody, 
							BodyFactory.CAT_TETRAIN, BodyFactory.MASK_TETRAIN);
					ge = new Bridge(AnimationManager.bridgeAnims[id], null, null, 
							EntityType.BRIDGE, b, x-1, y);
					b.setUserData(ge);
					b.getFixtureList().get(0).setSensor(true);
					ge.addToWorldLayers(CGCWorld.getLH());
				}
			}
		}
		
		mLength++;
	}
	
	/*
	 * Builds a track for the end of a map, but not the end of the game
	 * 
	 * @param multiple				The number of chunks from the start this one is
	 */
	private void buildTrack(int multiple)
	{
		boolean left_to_right = false;
		
		if(Math.random() < 0.5)
			left_to_right = true;
			
		CGCWorld.getLH().addNewChunk(end);
		
		Body backgroundThing;
		GameEntity ge;
		
		if (left_to_right)
		{
			backgroundThing = bf.createRectangle(9.5f, 5 + (11*multiple) + (11*prevLength), 
					20, 11, BodyType.StaticBody, 
					BodyFactory.CAT_NON_INTERACTIVE, BodyFactory.MASK_NON_INTERACTIVE);
			backgroundThing.getFixtureList().get(0).setSensor(true);
		}
		else
		{
			backgroundThing = bf.createRectangle(45, 5 + 11 * (multiple + prevLength), 
					1, 11, BodyType.StaticBody, 
					BodyFactory.CAT_NON_INTERACTIVE, BodyFactory.MASK_NON_INTERACTIVE);
			backgroundThing.getFixtureList().get(0).setSensor(true);
		}
		
		Body tower = bf.createRectangle(9, 2 + 11 * (multiple + prevLength), 1.0f, 1.0f, 
				BodyType.StaticBody, BodyFactory.CAT_TETRAIN, BodyFactory.MASK_TETRAIN);
		ge = new GuardTower(AnimationManager.towerAnims[0], null, AnimationManager.towerAnims[1], EntityType.TOWER, tower, 
				9, 2 + 11 * (multiple + prevLength));
		tower.setUserData(ge);
		tower.getFixtureList().get(0).setSensor(true);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		Body wall = bf.createRectangle(0, 6 + 11 * (multiple + prevLength), 
				1, 11, BodyType.StaticBody, 
				BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
		ge = new Wall(AnimationManager.vwallAnims[0], null,
				AnimationManager.vwallAnims[1], EntityType.WALL, wall, true);
		wall.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		wall = bf.createRectangle(19, 6 + 11 * (multiple + prevLength), 
				1, 11, BodyType.StaticBody, 
				BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
		ge = new Wall(AnimationManager.vwallAnims[0], null,
				AnimationManager.vwallAnims[1], EntityType.WALL, wall, true);
		wall.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		// Generate the walls for end of the level
		if (end)
		{
			for (int i = 0; i < 2; i++)
			{
				wall = bf.createRectangle(0, 6 + 11 * (multiple+prevLength+i+1),
						1, 11, BodyType.StaticBody,
						BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
				ge = new Wall(AnimationManager.vwallAnims[0], null, 
						AnimationManager.vwallAnims[1], EntityType.WALL, wall, true);
				wall.setUserData(ge);
				ge.addToWorldLayers(CGCWorld.getLH());
				
				wall = bf.createRectangle(19, 6 + 11 * (multiple+prevLength+i+1),
						1, 11, BodyType.StaticBody,
						BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
				ge = new Wall(AnimationManager.vwallAnims[0], null, 
						AnimationManager.vwallAnims[1], EntityType.WALL, wall, true);
				wall.setUserData(ge);
				ge.addToWorldLayers(CGCWorld.getLH());
			}
		}
		
		Body track = bf.createRectangle(9.5f, 5 + 11 * (multiple + prevLength), 
				20, 1, BodyType.StaticBody, 
				BodyFactory.CAT_NON_INTERACTIVE, BodyFactory.MASK_NON_INTERACTIVE);
		ge = new Track(AnimationManager.trackAnim, null, null,
				EntityType.TRACK, track);
		track.setUserData(ge);
		
		ge.addToWorldLayers(CGCWorld.getLH());
		
		track.getFixtureList().get(0).setSensor(true);

		if (left_to_right)
		{
			bf.createCar(backgroundThing, 50, 5 + 11 * (multiple + prevLength), false, true);
			bf.createCar(backgroundThing, 55.5f, 5 + 11 * (multiple + prevLength), false, true);
			bf.createCar(backgroundThing, 61, 5 + 11 * (multiple + prevLength), false, true);
		}
		else
		{
			bf.createCar(backgroundThing, -30, 5 + 11 * (multiple + prevLength), false, false);
			bf.createCar(backgroundThing, -35.5f, 5 + 11 * (multiple + prevLength), false, false);
			bf.createCar(backgroundThing, -41, 5 + 11 * (multiple + prevLength), false, false);
		}
		
		mLimit = 11 * (multiple + prevLength) + 3;
		
		mLength++;
	}
	
	/*
	 * Pairs gates with their sensors
	 */
	private void pairGates() 
	{
		for(Gate g : gates)
		{
			for(Sensor s : sensors)
			{
				if (g.gID() == s.gGID())
				{
					g.addSensor(s);
					s.sGate(g);
				}
			}
		}
	}
} // End class