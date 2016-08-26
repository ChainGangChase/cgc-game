/*
 * @(#)ChainGame.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc;

// Java Imports
import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;


// Libgdx/Box2D Imports
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
//import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

// CGC Imports
import com.percipient24.cgc.art.CharacterArt;
import com.percipient24.cgc.entities.terrain.CharacterSelectSensor;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.entities.ChainLink;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.Gate;
import com.percipient24.cgc.entities.Helicopter;
import com.percipient24.cgc.entities.Sensor;
import com.percipient24.cgc.entities.Track;
import com.percipient24.cgc.entities.boss.Sheriff;
import com.percipient24.cgc.entities.boss.SteelHorse;
import com.percipient24.cgc.entities.boss.Tank;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.players.RookieCop;
import com.percipient24.cgc.entities.terrain.Mud;
import com.percipient24.cgc.entities.terrain.Terrain;
import com.percipient24.cgc.entities.terrain.Water;
import com.percipient24.cgc.maps.MapBuilder;
import com.percipient24.cgc.maps.MapSorter;
import com.percipient24.cgc.net.MapVO;
import com.percipient24.cgc.overlays.HUDProgress;
import com.percipient24.cgc.overlays.KeepGoingArrows;
import com.percipient24.cgc.overlays.MapInfo;
import com.percipient24.cgc.overlays.OffScreenTimer;
import com.percipient24.cgc.overlays.Transition;
import com.percipient24.cgc.screens.CharacterSelect;
import com.percipient24.cgc.screens.Options;
import com.percipient24.enums.EntityType;
import com.percipient24.enums.Platform;
import com.percipient24.tweens.TransitionAccessor;

/*
 * Contains the logic to run and render a started game
 * 
 * @version 0.2 14/2/4
 * @author Joe Pietruch
 * @author Christopher Rider
 * @author JD Kelly
 * @author Clayton Andrews
 * @author William Ziegler
 */
public class ChainGame extends CGCWorld 
{
	private final int MAX_MAPS = 1;//3;
		
	// HUD variables
	private HUDProgress hudProgress;
	private OffScreenTimer offScreenTimer;
	private MapInfo mapInfo;
	private float[] playerProgress;
	private float[] resetProgress;
	private Array<Integer> deadKeyIDs;
	private float prevMaps;
	private KeepGoingArrows keepGoingArrows;
	
	// Map variables
	private int curTrain = 0;
	private int trainsPast;
	private int lastBeatenMap = 0;
	private MapVO map;
	private ArrayList<MapVO> mapList;
	private Array<MapBuilder> maps;
	private boolean summoned = false; // Whether or not the train is summoned
	private long mapTime = 0;
	private boolean mapsCompleted;
	private boolean trainsCompleted;
	
	// Rendering/Animation variables
	private Color[] colors;
	private Animation[] sensorSymbols;
	private int symbolShapesUsed;
	
	private boolean nextMapLoaded = false;
	private boolean changedMapName = false;
	private MapVO mdata;
	
	private Array<Vector2> directions;
	private float spawnDis = 0.9f;
	
	// SpriteCache
	//private boolean terrainCached = false;
	//private boolean treesCached = false;
	//http://stackoverflow.com/questions/19524520/libgdx-spritecache-transparency-on-images-not-displayed-properly
	
	// Debug variables
	//private Box2DDebugRenderer debug;
	
	/*
	 * Gets the maps being used in the current chase
	 * 
	 * @return						The maps being used in the current chase
	 */
	public Array<MapBuilder> getCurrentMaps()
	{
		return maps;
	}
	
	/*
	 * Gets the data object for the first map used in this chase
	 * 
	 * @return						The data object for the first map
	 */
	public MapVO getMap()
	{
		return map;
	}
	
	/*
	 * Gets the data objects for maps that could be used
	 * 
	 * @return						The data objects for the maps
	 */
	public ArrayList<MapVO> getMapList()
	{
		return mapList;
	}

	/*
	 * Gets the current train
	 * 
	 * @return						The current train ID
	 */
	public int getCurrentTrain()
	{
		return curTrain;
	}

	/*
	 * Creates a new ChainGame object
	 * 
	 * @param app						The ChaseApp to use
	 * @param numPlayers				The number of players playing this game
	 * @param mdata						Data for this game's starting map
	 * @param pMapList					The map list from the map cache
	 * @param transition				The Transition to introduce this ChainGame
	 * @param tutorial					Whether or not to load the tutorial map
	 */
	public ChainGame(ChaseApp app, int numPlayers, MapVO mdata,
			ArrayList<MapVO> pMapList, Transition transition, boolean tutorial) 
	{
		super(app, numPlayers, tutorial);
		TimerManager.start();
		super.resize(Data.ACTUAL_WIDTH, Data.ACTUAL_HEIGHT);
		
		tManager = ChaseApp.tManager;
		Tween.registerAccessor(Transition.class, new TransitionAccessor());
		this.transition = transition;
		
		animManager = new com.percipient24.cgc.art.TextureAnimationDrawer(myApp, input);
		mapsCompleted = false;
		trainsCompleted = false;
		trainsPast = 0;
		
		// Store map data
		mapList = pMapList;
		map = mdata;
		ChaseApp.favorite.sMap(map);
		
		// Generate HUD
		hudProgress = new HUDProgress(sBatch, this);
		hudProgress.setShow(true);
		mapInfo = new MapInfo(sBatch, map.mid+" : "+map.mname+" - "+map.uname, tutorial);
		mapInfo.setShow(true);
		offScreenTimer = new OffScreenTimer(sBatch);
		offScreenTimer.setShow(true);
		keepGoingArrows = new KeepGoingArrows(sBatch);
		keepGoingArrows.setShow(false);
		
		// Create players
		int start = 3;
		numPrisoners = numPlayers;
		
		deadKeyIDs = new Array<Integer>();
		
		for(int i = 0; i < numPlayers; i++)
		{
			Body tempBody = bf.createPlayerBody(start+(i*1.5f), 2, 0.6f, BodyType.DynamicBody,
												BodyFactory.CAT_PRISONER, BodyFactory.MASK_PRISONER);
			tempBody.setFixedRotation(true);

			CharacterSelectSensor chair = ChaseApp.characterSelect.activeChairs.get(i);
			
			Player tempPlayer = new Prisoner(this, chair.player.getCharacter(),
											EntityType.CONVICT, tempBody, (short) i);
			tempPlayer.copCharacter = chair.cop.getCharacter();

			tempBody.setUserData(tempPlayer);
			
			tempPlayer.addToWorldLayers(lh);
			tempPlayer.setChainGame(this);
			players.add(tempPlayer);
			
			deadKeyIDs.add(-1);

			ControllerScheme old = chair.player.getScheme();
			ControllerScheme cs = new ControllerScheme(tempPlayer, old.isLeft());
			cs.setController(old.getController());
			schemes.add(cs);
			tempPlayer.setScheme(cs);
		}
		
		// Set up control schemes for the players
//		for (int i = 0; i < input.controlList.length; i++)
//		{
//			if (input.controlList[i].isUsed())
//			{
//				ControllerScheme cs = new ControllerScheme(players.get(input.controlList[i].getPID()),
//															input.controlList[i].isLeft());
//				cs.setController(input.controlList[i]);
//				schemes.add(cs);
//				players.get(input.controlList[i].getPID()).setScheme(cs);
//			}
//		}
		
		setDifficultyMods();
		createChains(start, numChainsInLink);
		
		// Start world with no forces
		for(int i = 0; i < 18; i++)
		{
			world.step(WORLD_DELAY, 6, 2);
			world.clearForces();
		}
		
		// Generate maps for chain game
		curTrain = 0;
		maps = new Array<MapBuilder>();
		maps.add(new MapBuilder(mdata.mdata, true, false, 0, mdata.mid, tutorial));
	
		//debug = new Box2DDebugRenderer(true, false, false, false, false);
		
		// Set up progress bar
		playerProgress = new float[numPlayers];
		resetProgress = new float[numPlayers];
		for (int i = 0; i < playerProgress.length; i++)
		{
			playerProgress[i] = 0.0f;
			resetProgress[i] = 1.0f;
		}
		prevMaps = 0.0f;
		
		// Load sensor symbols
		sensorSymbols = new Animation[3];
		sensorSymbols[0] = com.percipient24.cgc.art.TextureAnimationDrawer.sensorTriAnim;
		sensorSymbols[1] = com.percipient24.cgc.art.TextureAnimationDrawer.sensorSqrAni;
		sensorSymbols[2] = com.percipient24.cgc.art.TextureAnimationDrawer.sensorStarAnim;
		symbolShapesUsed = 0;
		
		colors = new Color[5];
		colors[0] = new Color(0.0f, 1.0f, 1.0f, 1.0f);
		colors[1] = new Color(1.0f, 0.0f, 1.0f, 1.0f);
		colors[2] = new Color(1.0f, 1.0f, 0.0f, 1.0f);
		colors[3] = new Color(1.0f, 0.5f, 0.0f, 1.0f);
		colors[4] = new Color(0.0f, 0.5f, 1.0f, 1.0f);
		
		// Start up stats
		if (Options.storedTrackingOption)
		{
			ChaseApp.stats.startGame();
			ChaseApp.stats.addMapToGame(mdata.mid);
			ChaseApp.stats.getGame().numPlayers = numPlayers;
		}
		
		setTimersAndTasks();
		
		for (int i = 0; i < numPlayers; i++)
		{
			players.get(i).getBody().applyLinearImpulse(0f, 0.011f, 
														players.get(i).getBody().getPosition().x, 
														players.get(i).getBody().getPosition().y,
														true);
		}
		
		sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		directions = new Array<Vector2>();
		directions.add(new Vector2(0.0f, spawnDis));
		directions.add(new Vector2(spawnDis, spawnDis));
		directions.add(new Vector2(spawnDis, 0.0f));
		directions.add(new Vector2(spawnDis, -spawnDis));
		directions.add(new Vector2(0.0f, -spawnDis));
		directions.add(new Vector2(-spawnDis, -spawnDis));
		directions.add(new Vector2(-spawnDis, 0.0f));
		directions.add(new Vector2(spawnDis, spawnDis));
		
		CharacterSelect.tutorial = false;
	}
	
	/*
	 * Sets the different game settings based on selected difficulty
	 */
	private void setDifficultyMods()
	{
		switch (Options.storedDifficultyOption)
		{
			case 0: numChainsInLink = 15;
				Mud.speedMod = 0.6f;
				Water.speedMod = 0.85f;
				Water.forceAmount = 1.1f;
				Tank.changeNormalSpeed(70.0f);
				Tank.accuracy = 10.0f;
				SteelHorse.MAX_HP = 1;//3;
				SteelHorse.accuracy = 60.0f;
				SteelHorse.wallAvoidanceSpeed = 3.5f;
				Sheriff.trailTime = 0.50f;
				RookieCop.minDazedSpeed = 0.1f;
				RookieCop.dazedSlowRecharge = 0.1f;
				break;
			case 1: numChainsInLink = 13;
				Mud.speedMod = 0.4f;
				Water.speedMod = 0.75f;
				Water.forceAmount = 1.4f;
				Tank.changeNormalSpeed(85.0f);
				Tank.accuracy = 7.0f;
				SteelHorse.MAX_HP = 4;
				SteelHorse.wallAvoidanceSpeed = 3.5f;
				SteelHorse.accuracy = 50.0f;
				Sheriff.trailTime = 0.40f;
				RookieCop.minDazedSpeed = 0.15f;
				RookieCop.dazedSlowRecharge = 0.15f;
				break;
			case 2: numChainsInLink = 11;
				Mud.speedMod = 0.3f;
				Water.speedMod = 0.65f;
				Water.forceAmount = 1.6f;
				Tank.changeNormalSpeed(100.0f);
				Tank.accuracy = 5.5f;
				SteelHorse.MAX_HP = 5;
				SteelHorse.accuracy = 40.0f;
				SteelHorse.wallAvoidanceSpeed = 4.0f;
				Sheriff.trailTime = 0.33f;
				RookieCop.minDazedSpeed = 0.2f;
				RookieCop.dazedSlowRecharge = 0.2f;
				break;
			case 3: numChainsInLink = 11;
				Mud.speedMod = 0.2f;
				Water.speedMod = 0.55f;
				Water.forceAmount = 1.8f;
				Tank.changeNormalSpeed(115.0f);
				Tank.accuracy = 2.5f;
				SteelHorse.MAX_HP = 6;
				SteelHorse.accuracy = 30.0f;
				SteelHorse.wallAvoidanceSpeed = 4.5f;
				Sheriff.trailTime = 0.30f;
				RookieCop.minDazedSpeed = 0.2f;
				RookieCop.dazedSlowRecharge = 0.25f;
				break;
			case 4: numChainsInLink = 11;
				Mud.speedMod = 0.15f;
				Water.speedMod = 0.55f;
				Water.forceAmount = 1.95f;
				Tank.changeNormalSpeed(130.0f);
				Tank.accuracy = 0.5f;
				SteelHorse.MAX_HP = 7;
				SteelHorse.accuracy = 15.0f;
				SteelHorse.wallAvoidanceSpeed = 5.0f;
				Sheriff.trailTime = 0.25f;
				RookieCop.minDazedSpeed = 0.2f;
				RookieCop.dazedSlowRecharge = 0.3f;
				break;
			default:
				break;
		}
		
		chainDensity = 11.0f / numChainsInLink;
	}
	
	/*
	 * Creates the chains that hold players together
	 * 
	 * @param start					Start X position for the first chain
	 * @param numChainLinks			Number of links in the chain - Must be odd - Too many will break chain physics
	 */
	private void createChains(int start, int numChainLinks)
	{
		Array<Body> bodyList = new Array<Body>();
		Body b;
		GameEntity ge;
		
		for (int i = 0; i < numPlayers-1; i++)
		{
			bodyList.clear();
			for (int j = 0; j < numChainLinks; j++)
			{
				b = bf.createRectangle(start+0.3f+0.2f*j, 1.9f, 0.2f, 0.1f, 
										BodyType.DynamicBody, BodyFactory.CAT_CHAIN, 
										BodyFactory.MASK_CHAIN, CGCWorld.chainDensity);
				bodyList.add(b);
				if (j%2 == 1)
				{
					ge = new ChainLink(com.percipient24.cgc.art.TextureAnimationDrawer.chainAnims[0], null, null, EntityType.CHAINLINK, b);
				}
				else
				{
					ge = new ChainLink(com.percipient24.cgc.art.TextureAnimationDrawer.chainAnims[1], null, null, EntityType.CHAINLINK, b);
				}
				b.setUserData(ge);
				b.setAngularDamping(1000.0f);
			}
			
			for (int j = 0; j < numChainLinks; j+=2)
			{
				((ChainLink) (bodyList.get(j).getUserData())).addToWorldLayers(lh);
				if (j == numChainLinks-1)
				{
					j = -1;
				}
			}
			
			for (int j = 0; j < numChainLinks+1; j++)
			{
				if (j == 0)
				{
					bf.createRevoluteJoint(players.get(i).getBody(), bodyList.get(j),
											new Vector2(0.0f,0f), new Vector2(-0.4f,0f));
				}
				else if (j == numChainLinks)
				{
					bf.createRevoluteJoint(bodyList.get(j-1), players.get(i+1).getBody(),
											new Vector2(0.4f,0f), new Vector2(0.0f,0f));
				}
				else
				{
					bf.createRevoluteJoint(bodyList.get(j-1), bodyList.get(j),
											new Vector2(0.1f,0f), new Vector2(-0.1f,0f));
				}
			}
			
			// Set chain links left and right
			for (int j = 0; j < numChainLinks; j++)
			{
				if (j == 0)
				{
					((ChainLink) (bodyList.get(j)).getUserData()).setRightLink((ChainLink) (bodyList.get(j+1).getUserData()));
				}
				else if (j == numChainLinks-1)
				{
					((ChainLink) (bodyList.get(j)).getUserData()).setLeftLink((ChainLink) (bodyList.get(j-1).getUserData()));
				}
				else
				{
					((ChainLink) (bodyList.get(j)).getUserData()).setLeftLink((ChainLink) (bodyList.get(j-1).getUserData()));
					((ChainLink) (bodyList.get(j)).getUserData()).setRightLink((ChainLink) (bodyList.get(j+1).getUserData()));
				}
			}
			
			Joint middle = bf.createRopeJoint(players.get(i).getBody(), 
												players.get(i+1).getBody(), 0.27f*numChainLinks);
			
			Player left = players.get(i);
			Player right = players.get(i+1);
			
			patcher.leftRightCenter(left, right, middle);
			
			for (int j = 0; j < numChainLinks; j++)
			{
				patcher.setupChain(bodyList.get(j), left);
			}

			left.setRightChain((ChainLink) (bodyList.get(0).getUserData()));
			right.setLeftChain((ChainLink) (bodyList.get(numChainLinks-1).getUserData()));
		}
	}

	/*
	 * Deletes the myApp.sBatch drawer
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#dispose()
	 */
	public void dispose() 
	{
		sBatch.dispose();
	}

	/*
	 * Renders the sprites to the screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#render(float)
	 */
	public void render(float dt)
	{
		super.render(dt);
		
		gameTime += (delta * 1000);
		mapTime += (delta * 1000);
		
		
		
		shapes.begin(ShapeType.Filled);
		shapes.setColor(Color.BLACK);
		shapes.rect(0, 0, Data.ACTUAL_WIDTH, Data.ACTUAL_HEIGHT);
		shapes.end();
		
		// Handle camera logic
		camera.adjust(players, true);
		//camera.sPosition(camera.position.x, getPlayerAverageY());
		camera.setParallaxPoint(camera.position.x, camera.position.y);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
		sBatch.setProjectionMatrix(camera.combined);
		
		if (!endClock.isRunning() && !terminated && !paused)
		{
			updateLogic(delta);
		}
		
		Water.updateCurrent(delta);
		
		// Render world
		renderBodies(camera);
		
		float hudZoomLevel = (camera.getCamMes().x / camera.viewportWidth);
	
		// Hides the train as it zooms towards the map
		sBatch.setProjectionMatrix(hudMatrix);
		shapes.begin(ShapeType.Filled);
		shapes.setColor(Color.BLACK);
		shapes.rect(0, 0, Data.ACTUAL_WIDTH * ((1 - hudZoomLevel) * .5f), Data.ACTUAL_HEIGHT);
		shapes.rect(Data.ACTUAL_WIDTH - (Data.ACTUAL_WIDTH * ((1 - hudZoomLevel) * .5f)),
				0, Data.ACTUAL_WIDTH * ((1 - hudZoomLevel) * .5f), Data.ACTUAL_HEIGHT);
		shapes.end();
		sBatch.setProjectionMatrix(camera.combined);
		
		// Render player-specific HUD elements
		sBatch.begin();
		for (int i = 0; i < players.size; i++)
		{
			if (players.get(i).getShowCallout())
			{
				TextureRegion calloutFrame = com.percipient24.cgc.art.TextureAnimationDrawer.calloutAnims[i].getKeyFrame(0);
				
				if (players.get(i).getCorpse() == null)
				{
					sBatch.draw(calloutFrame, players.get(i).getBody().getPosition().x, 
							players.get(i).getBody().getPosition().y, -0.5f, -0.5f, 
							calloutFrame.getRegionWidth(), calloutFrame.getRegionHeight(), 
							camera.zoom, camera.zoom, 0);
				}
				else
				{
					sBatch.draw(calloutFrame, players.get(i).getCorpse().getBody().getPosition().x, 
								players.get(i).getCorpse().getBody().getPosition().y, -0.5f, -0.5f, 
								calloutFrame.getRegionWidth(), calloutFrame.getRegionHeight(), 
								camera.zoom, camera.zoom, 0);
				}
			}
			
			if (players.get(i) instanceof RookieCop)
			{
				if (((RookieCop) players.get(i)).getBarRatio() < 1.0f)
				{
					TextureRegion tempRegion = com.percipient24.cgc.art.TextureAnimationDrawer.barBackAnim.getKeyFrame(0);
					
					sBatch.draw(tempRegion, players.get(i).getBody().getPosition().x, 
								players.get(i).getBody().getPosition().y + .2f, -0.5f, -0.5f, 
								tempRegion.getRegionWidth(), tempRegion.getRegionHeight(), 
								camera.zoom, camera.zoom, 0);
					
					tempRegion = com.percipient24.cgc.art.TextureAnimationDrawer.grabFillAnim.getKeyFrame(0);
					
					sBatch.draw(tempRegion, players.get(i).getBody().getPosition().x + .02f, 
								players.get(i).getBody().getPosition().y + .22f, -0.5f, -0.5f, 
								tempRegion.getRegionWidth(), tempRegion.getRegionHeight(), 
								camera.zoom, camera.zoom * ((RookieCop) players.get(i)).getBarRatio(), 0);
					
					if (((RookieCop) players.get(i)).isGrabCooldown())
					{
						tempRegion = com.percipient24.cgc.art.TextureAnimationDrawer.dazedAnim.getKeyFrame(0);
						
						sBatch.draw(tempRegion, players.get(i).getBody().getPosition().x, 
								players.get(i).getBody().getPosition().y, 0, 0, 
								tempRegion.getRegionWidth(), tempRegion.getRegionHeight(), 
								camera.zoom, camera.zoom, 
								((RookieCop) players.get(i)).adjustDonutRotation(dt) * 75);
						sBatch.draw(tempRegion, players.get(i).getBody().getPosition().x, 
								players.get(i).getBody().getPosition().y, 0, 0, 
								tempRegion.getRegionWidth(), tempRegion.getRegionHeight(), 
								camera.zoom, camera.zoom, 
								((RookieCop) players.get(i)).adjustDonutRotation(dt) * 75 + 120);
						sBatch.draw(tempRegion, players.get(i).getBody().getPosition().x, 
								players.get(i).getBody().getPosition().y, 0, 0, 
								tempRegion.getRegionWidth(), tempRegion.getRegionHeight(), 
								camera.zoom, camera.zoom, 
								((RookieCop) players.get(i)).adjustDonutRotation(dt) * 75 + 240);
					}
				}
			}
			else
			{
				if (((Prisoner) players.get(i)).getBarRatio() < 1.0f)
				{
					TextureRegion grabBack = com.percipient24.cgc.art.TextureAnimationDrawer.barBackAnim.getKeyFrame(0);
					TextureRegion staminaFill = com.percipient24.cgc.art.TextureAnimationDrawer.staminaFillAnim.getKeyFrame(0);
					
					sBatch.draw(grabBack, players.get(i).getBody().getPosition().x, 
								players.get(i).getBody().getPosition().y + .2f, -0.5f, -0.5f, 
								grabBack.getRegionWidth(), grabBack.getRegionHeight(), 
								camera.zoom, camera.zoom, 0);
					
					sBatch.draw(staminaFill, players.get(i).getBody().getPosition().x + .02f, 
								players.get(i).getBody().getPosition().y + .22f, -0.5f, -0.5f, 
								staminaFill.getRegionWidth(), staminaFill.getRegionHeight(), 
								camera.zoom, camera.zoom * ((Prisoner) players.get(i)).getBarRatio(), 0);
				}
			}
		}
		sBatch.end();

		// Render HUD
		sBatch.setProjectionMatrix(hudMatrix);
		ChaseApp.menuFont.getData().setScale(1.5f);
		
		renderHUD();
		
		pauseMenu.render(delta);
		
		if (transitioning)
		{
			tManager.update(delta);
			transition.render(delta);
		}
		
		emptyDestroyList(); // Destroy any flagged entities and run final destroy logic
		
		// Step through world physics
		if (!paused)
		{
			world.step(WORLD_DELAY, 6, 2);
			world.clearForces();
		}
	}

	/*
	 * Draws the bodies for this world
	 * 
	 * @param c						The Camera to render the bodies from
	 */
	private void renderBodies(Camera c)
	{
		GameEntity ge;
		Terrain t;
		TextureRegion terrainFrame;
		int drawn = 0;
		
		// Determine what is and isn't on camera
		int startHeight = ((int) c.position.y) - CGCWorld.ROWS_TO_DISPLAY;
		int endHeight = startHeight + 2 * CGCWorld.ROWS_TO_DISPLAY;
		
		if (startHeight < 0)
		{
			startHeight = 0;
		}
		if (endHeight > lh.getWorldLength())
		{
			endHeight = lh.getWorldLength();
		}
		
		// Handle alpha
		Color colorForAlpha = sBatch.getColor();
		if (lost())
		{
			float endPer = 1.0f - endClock.getPercent();
			
			colorForAlpha.b = endPer;
			colorForAlpha.g = endPer;
			colorForAlpha.r = endPer;
		}
	
		colorForAlpha.a = 1.0f;
		sBatch.setColor(colorForAlpha);
		
		for (int i = 0; i < lh.getLayers().size; i++) // Loop through every layer
		{
			drawn = 0;
			sBatch.begin();
			
			if (lh.getLayer(i).hasGrid())
			{
				for (int y = startHeight; y < endHeight; y++) // Loop through every row in the map grid
				{
					for (int x = 0; x < MapBuilder.chunkWidth; x++) // Loop through every column in the row
					{
						if (i == LayerHandler.background)
						{
							t = lh.getLayer(i).getTerrain(x, y);
							
							if (t == null) // If the terrain is background...
							{
								terrainFrame = com.percipient24.cgc.art.TextureAnimationDrawer.bgAnims[0].getKeyFrame(0);
								sBatch.draw(terrainFrame, x+1, y, -0.5f, -0.5f, 
											terrainFrame.getRegionWidth(), 
											terrainFrame.getRegionHeight(), 
											c.zoom, c.zoom, 0);
								drawn++;
								if (drawn == MAX_IMAGES_PER_DRAW)
								{
									drawn = 0;
									sBatch.flush();
								}
							}
							else if (t instanceof Water) // ... or water...
							{
								Water w = (Water) t;
								w.step(delta, i);
								
								if (ChaseApp.platform == Platform.DESKTOP) // Draw more detailed graphics on desktop only
								{
									terrainFrame = w.getLowAnim(w.getTopRight(), 0);
									sBatch.draw(terrainFrame, x+1.5f, y+0.5f, w.getImageHalfWidth(0), w.getImageHalfHeight(0), 
												terrainFrame.getRegionWidth(), terrainFrame.getRegionHeight(), 
												c.zoom, c.zoom, 0);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
									
									terrainFrame = w.getLowAnim(w.getBotRight(), 1);
									sBatch.draw(terrainFrame, x+1.5f, y, w.getImageHalfWidth(0), w.getImageHalfHeight(0), 
												terrainFrame.getRegionWidth(), terrainFrame.getRegionHeight(), 
												c.zoom, c.zoom, 0);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
									
									terrainFrame = w.getLowAnim(w.getBotLeft(), 2);
									sBatch.draw(terrainFrame, x+1.0f, y, w.getImageHalfWidth(0), w.getImageHalfHeight(0), 
												terrainFrame.getRegionWidth(), terrainFrame.getRegionHeight(), 
												c.zoom, c.zoom, 0);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
									
									terrainFrame = w.getLowAnim(w.getTopLeft(), 3);
									sBatch.draw(terrainFrame, x+1.0f, y+0.5f, w.getImageHalfWidth(0), w.getImageHalfHeight(0), 
												terrainFrame.getRegionWidth(), terrainFrame.getRegionHeight(), 
												c.zoom, c.zoom, 0);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
								}
								else
								{
									terrainFrame = com.percipient24.cgc.art.TextureAnimationDrawer.ouyaWater.getKeyFrame(0);
									sBatch.draw(terrainFrame, x+1.5f, y+0.5f, w.getImageHalfWidth(0)*2, w.getImageHalfHeight(0)*2, 
												terrainFrame.getRegionWidth(), terrainFrame.getRegionHeight(), 
												c.zoom, c.zoom, 0);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
								}
								
								if (w.getDirection() > 0 && w.getDirection() < 9) // TODO Set to be >= 0 if we ever have a standing water anim
								{
									terrainFrame = com.percipient24.cgc.art.TextureAnimationDrawer.currentAnims[w.getDirection()].getKeyFrame(Water.getCurrentTime());
									sBatch.draw(terrainFrame, x+1.0f, y, -0.5f, -0.5f,
												terrainFrame.getRegionWidth(), terrainFrame.getRegionHeight(), 
												c.zoom, c.zoom, 0);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
								}
							}
							else // ... or mud
							{
								Mud m = (Mud) t;
								m.step(delta, i);
								
								if (ChaseApp.platform == Platform.DESKTOP)
								{
									terrainFrame = m.getLowAnim(m.getTopRight(), 0);
									sBatch.draw(terrainFrame, x+1.5f, y+0.5f, m.getImageHalfWidth(0), m.getImageHalfHeight(0), 
												terrainFrame.getRegionWidth(), terrainFrame.getRegionHeight(), 
												c.zoom, c.zoom, 0);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
									
									terrainFrame = m.getLowAnim(m.getBotRight(), 1);
									sBatch.draw(terrainFrame, x+1.5f, y, m.getImageHalfWidth(0), m.getImageHalfHeight(0), 
												terrainFrame.getRegionWidth(), terrainFrame.getRegionHeight(), 
												c.zoom, c.zoom, 0);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
									
									terrainFrame = m.getLowAnim(m.getBotLeft(), 2);
									sBatch.draw(terrainFrame, x+1.0f, y, m.getImageHalfWidth(0), m.getImageHalfHeight(0), 
												terrainFrame.getRegionWidth(), terrainFrame.getRegionHeight(), 
												c.zoom, c.zoom, 0);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
									
									terrainFrame = m.getLowAnim(m.getTopLeft(), 3);
									sBatch.draw(terrainFrame, x+1.0f, y+0.5f, m.getImageHalfWidth(0), m.getImageHalfHeight(0), 
												terrainFrame.getRegionWidth(), terrainFrame.getRegionHeight(), 
												c.zoom, c.zoom, 0);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
								}
								else
								{
									terrainFrame = com.percipient24.cgc.art.TextureAnimationDrawer.ouyaMud.getKeyFrame(0);
									sBatch.draw(terrainFrame, x+1.5f, y+0.5f, m.getImageHalfWidth(0)*2, m.getImageHalfHeight(0)*2, 
												terrainFrame.getRegionWidth(), terrainFrame.getRegionHeight(), 
												c.zoom, c.zoom, 0);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
								}
							}
						} // End terrain render
						else if (i != LayerHandler.chains && i != LayerHandler.corpses && i != LayerHandler.projectile && i != LayerHandler.aerial)
						{
							for (int j = 0; j < lh.getLayer(i).getEntitiesInGrid(x, y).size; j++)
							{
								ge = lh.getLayer(i).getEntitiesInGrid(x,  y).get(j);
								
								if (ge != null && ge.hasAnimation())
								{
									ge.draw(sBatch, delta, i);
									drawn++;
									if (drawn == MAX_IMAGES_PER_DRAW)
									{
										drawn = 0;
										sBatch.flush();
									}
									if (i == LayerHandler.ground && ge instanceof Gate)
									{
										 if (Options.storedSymbolOption)
										 {
											 drawn = renderSensorSymbols((Gate) ge, drawn);
										 }
										 
										 drawn = renderSensorHeads((Gate) ge, drawn);
									}
								} // End grid entity drawing
							}
						}
					} // End X loop
				} // End Y loop
			}
			
			// Draw anything on this layer that isn't in the grid
			for (int j = 0; j < lh.getLayer(i).getEntities().size; j++)
			{
				ge = lh.getLayer(i).getEntities().get(j);
				
				if (ge != null && ge.hasAnimation())
				{
					if(ge instanceof Track)//keeps train tracks from animating in the main game... for now...
					{
						ge.draw(sBatch, 0, i);
					}
					else
					{
						ge.draw(sBatch, delta, i);
					}
					drawn++;
					if (drawn == MAX_IMAGES_PER_DRAW)
					{
						drawn = 0;
						sBatch.flush();
					}
				}
			} // End non-grid drawing
			
			sBatch.end();
		} // End layer loop
		
		
		// Handle alpha
		colorForAlpha = sBatch.getColor();
		colorForAlpha.a = 1.0f;
		sBatch.setColor(colorForAlpha);
	}
	
	/*
	 * Renders symbols onto all Sensors connected to a single Gate
	 * 
	 * @param gate                  The Gate whose Sensors should be marked
	 * @param thingsDrawn			How many things the SpriteBatch has drawn so far
	 * @return						How many things the SpriteBatch has drawn upon completion
	 */
	private int renderSensorSymbols(Gate gate, int thingsDrawn)
	{
		if (gate.gSensors().size > 1 && gate.isClosed())
		{
			if (gate.sensorShapeID == -1)
			{
				gate.sensorShapeID = symbolShapesUsed % sensorSymbols.length;

				symbolShapesUsed++;
			}
			
			if (gate.sensorColor == Color.WHITE)
			{
				gate.sensorColor = colors[symbolShapesUsed % colors.length];
			}
			
			if(!lost())
			{
				sBatch.setColor(gate.sensorColor);
			}
			for (int i = 0; i < gate.gSensors().size; i++)
			{	
				sBatch.draw(sensorSymbols[gate.sensorShapeID].getKeyFrame(0), 
							gate.gSensors().get(i).getBody().getPosition().x - .5f, 
							gate.gSensors().get(i).getBody().getPosition().y - .5f, 
							0, 0, 96, 96, camera.zoom, camera.zoom, 0.0f);
				
				thingsDrawn++;
				if (thingsDrawn == 127)
				{
					thingsDrawn = 0;
					sBatch.flush();
				}
			}
				
			if(!lost())
			{
				sBatch.setColor(Color.WHITE);
			}
		}
		
		return thingsDrawn;
	}
	
	/*
	 * Renders player heads onto all Sensors which need specific players
	 * 
	 * @param gate                  The Gate whose Sensors should be marked
	 * @param thingsDrawn			How many things the SpriteBatch has drawn so far
	 * @return						How many things the SpriteBatch has drawn upon completion
	 */
	private int renderSensorHeads(Gate gate, int thingsDrawn)
	{
		for (int i = 0; i < gate.gSensors().size; i++)
		{	
			Sensor tempSensor = gate.gSensors().get(i);
			
			if (gate.gSensors().get(i).gLockID() != 0)
			{
				if (deadKeyIDs.contains((int)tempSensor.gLockID(), true))
				{
					continue;	
				}
				
				sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				for (int j = 0; j < players.size; j++)
				{
					if (players.get(j) instanceof Prisoner
							&& ((Prisoner)players.get(j)).canOpen(tempSensor.gLockID()))
					{
						sBatch.draw(com.percipient24.cgc.art.TextureAnimationDrawer.keyHeadAnims[players.get(j).getPID()].getKeyFrame(0),
								tempSensor.getBody().getPosition().x - 1/3f, 
								tempSensor.getBody().getPosition().y - 1/3f, 
								0, 0, 64, 64, camera.zoom, camera.zoom, 0.0f);
						
						thingsDrawn++;
						if (thingsDrawn == 127)
						{
							thingsDrawn = 0;
							sBatch.flush();
						}
					}
				}
			}
		}
		
		return thingsDrawn;
	}
	
	/*
	 * Adds a keyID to the list of dead IDs used to trigger Sensors
	 * 
	 * @param newDeadKey				The keyID to add to the array
	 */
	public void addDeadKeyID(int newDeadKey)
	{
		deadKeyIDs.add(newDeadKey);
	}
	
	/*
	 * Gets the list of dead keys
	 * 
	 * @return						An array containing all the dead Prisoners' gate keys
	 */
	public Array<Integer> getDeadKeyIDs()
	{
		return deadKeyIDs;
	}
	
	/*
	 * Updates and draws the player progress meter on the right side of the screen.
	 */
	private void renderHUD()
	{
		sBatch.begin();
		hudProgress.render(delta, curTrain, MAX_MAPS, prevMaps);
		mapInfo.render(delta, (int) CGCWorld.getPrisonerAverageY());
		offScreenTimer.render(delta);
		keepGoingArrows.render(delta);
		sBatch.end();
	}
	
	/*
	 * Moves a Transition in from off-screen to bring the players into the boss fight
	 */
	private void startTransition()
	{
		transition = new Transition(sBatch, determineBossType(), myApp);
		transition.setXPosition(Data.ACTUAL_WIDTH * 1.5f);
		transition.setShow(true);
		transitioning = true;
		
		Timeline.createSequence()
		.push
		(Tween.to(transition, TransitionAccessor.TRANSLATE_X, 1.0f).ease(Cubic.OUT).target(0))
		.pushPause(2.0f)
		.setCallback(changeToBossFight)
		.start(tManager);
	}
	
	/*
	 * Switches the game screen to BossFight once the above Timeline is over
	 */
	private TweenCallback changeToBossFight = new TweenCallback()
	{
		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			transitioning = false;
			CGCWorld.clearWorld();
			world = new World(new Vector2(0, 0), true);
			lh.createLayers();
			maps.clear();
			Array<Player> newPlayerArray = new Array<Player>();
			for (int i = 0; i < CGCWorld.numPlayers; i++)
			{
				CGCWorld.players.get(i).setInBossFight(true);
				newPlayerArray.add(CGCWorld.players.get(i));
			}
			myApp.setScreen(new BossFight(myApp, CGCWorld.getNumPlayers(), newPlayerArray, transition, tutorial));
		}
	};
	
	/*
	 * Moves the Transition off of the screen so the game may begin
	 */
	private void finishTransition()
	{
		transitioning = true;
		
		Timeline.createSequence()
		.push
		(Tween.to(transition, TransitionAccessor.TRANSLATE_X, .75f).ease(Cubic.IN).target(-Data.GAME_WIDTH * 1.5f))
		.setCallback(clearTransition)
		.start(tManager);
	}
	
	/*
	 * Allows handleInput() to function once the above Timeline finishes
	 */
	private TweenCallback clearTransition = new TweenCallback()
	{
		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			transition.setShow(false);
			transitioning = false;
		}
	};
	
	/*
	 * Checks if the players are near the train tracks
	 * 
	 * @param curMap				The map for the next train
	 * @return						Whether or not the players are at the tracks
	 */
	private boolean playersPast(int curMap) 
	{
		boolean allDead = true;
		
		for(int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			if (p.isAlive())
			{
				allDead = false;
				if (p instanceof Prisoner && p.getPosition().y < maps.get(curMap).gTrainTriggerDist())
				{
					return false;
				}
			}
		}
		
		return !allDead;
	}
	
	/*
	 * Checks if a player has passed the train trigger point of the current map
	 * 
	 * @param curMap				The map for the next train
	 * @return						Whether or not a player has passed the tracks
	 */
	private boolean onePlayerPastTrigger(int curMap)
	{
		for(int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			
			if(p.isAlive())
			{
				if(p.getPosition().y >= maps.get(curMap).gTrainTriggerDist())
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Checks if a player has passed the train tracks to determine if the next map data should be displayed
	 * 
	 * @param curMap				The map for the next train
	 * @return						Whether or not a player has reached the train tracks
	 */
	private boolean onePlayerPastTracks(int curMap)
	{
		for(int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			
			if(p.isAlive())
			{
				if(p.getPosition().y >= maps.get(curMap).gTrainTriggerDist() + 4.5f)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*
	 * Resizes the window
	 * 
	 * @param width					The new width of the window
	 * @param height				The new height of the window
	 * @see com.percipient24.cgc.screens.CGCScreen#resize(int, int)
	 */
	public void resize(int width, int height) 
	{
		super.resize(width, height);
		
		if (hudProgress != null)
		{
			hudProgress.resize();
		}
		if (keepGoingArrows != null)
		{
			keepGoingArrows.resize();
		}
	}

	/*
	 * Shows the current screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#show()
	 */
	public void show() 
	{	
		transitioning = true;
		finishTransition();
		hudProgress.setShow(true);
		mapInfo.setShow(true);
		offScreenTimer.setShow(true);
		
		super.show();
	}

	/*
	 * Hides the current screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#hide()
	 */
	public void hide() 
	{
		
	}
	
	/*
	 * Spawns dead prisoners as rookie cops
	 */
	public void spawnCops()
	{
		if(!gameWon && !gameLost)
		{
			Body b;
			Vector2 spawnPos;
			Player ply;
			
			do
			{
				ply = players.random();
				spawnPos = ply.getSpawnPosition();
			}while((!ply.isAlive() || spawnPos == null) && getLivingPrisoners().size > 0);
			
			numCops += recentlyDeceased.size;
			Array<RookieCop> cops = new Array<RookieCop>();
			
			for(int j = 0; j < players.size; j++)
			{
				Player p = players.get(j);
				if (p instanceof RookieCop)
				{
					if (!recentlyDeceased.contains(p, true))
					{
						cops.add((RookieCop)p);
					}
				}
			}
			
			Vector2 gridPos = new Vector2(Math.round(spawnPos.x) - 1, Math.round(spawnPos.y));
			Array<Vector2> possibleSpawnPositions = new Array<Vector2>();
			Array<GameEntity> age = lh.getLayer(LayerHandler.ground).getEntitiesInGrid((int)gridPos.x, (int)Math.min(gridPos.y, CGCWorld.getLH().getLayer(LayerHandler.ground).getNumChunks() * 11 - 1));
			
			if (age.size == 0)
			{
				possibleSpawnPositions.add(gridPos);
			}
			
			int dir = 1;
			
			while(possibleSpawnPositions.size < 7)
			{
				int direction = dir % 8;
				
				if(direction > 0)
				{
					Vector2 gsPos = gridPos.cpy().add(directions.get(direction).cpy().scl((int) Math.ceil(dir / 8.0f)));
					
					if (gsPos.x >= 0 && gsPos.y >= 0 && gsPos.x < 18 && gsPos.y < lh.getLayer(LayerHandler.ground).getNumChunks() * 11)
					{
						
						age = lh.getLayer(LayerHandler.ground).getEntitiesInGrid((int)gsPos.x, (int)gsPos.y);
						
						if(age.size == 0)
						{
							possibleSpawnPositions.add(gsPos);
						}
					}
				}
				
				dir++;
			}
			
			Vector2 mySpawnPos;
			
			for(int i = 0; i < recentlyDeceased.size; i++)
			{
				mySpawnPos = possibleSpawnPositions.random();
				Player p = recentlyDeceased.get(i);
				CharacterArt copArt = p.copCharacter;
				
				b = bf.createPlayerBody(mySpawnPos.x + 1, mySpawnPos.y, 0.6f, 
										BodyType.DynamicBody, BodyFactory.CAT_COP, BodyFactory.MASK_COP);
				b.setFixedRotation(true);
				
				RookieCop rc = new RookieCop(this, copArt, EntityType.COP, b, p.getPID());
				
				b.setUserData(rc);
				players.set(p.getPID(), rc);
				rc.addToWorldLayers(lh);
				rc.setChainGame(this);
				
				ControllerScheme cs = p.getScheme();
				cs.setPlayer(rc);
				rc.setScheme(cs);
				
				possibleSpawnPositions.removeValue(mySpawnPos, true);
			}
			
			recentlyDeceased.clear();
		}
	}
	
	/*
	 * Ends the current chain game and begins a boss fight
	 * 
	 * @param won					Whether or not the Prisoners made it to the end
	 */
	public void endGameWorld(boolean won)
	{
		SoundManager.endSounds();
		
		if (!won || numPrisoners <= 0) // If the game is truly lost, show them how pitiful they are...
		{
			endGameStats(false);
			sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			
			ChaseApp.lose.setPlayers(players);
			myApp.setScreen(ChaseApp.lose);
			TimerManager.clear();
		}
		else // ... otherwise show them the new world we have created
		{
			startTransition();
		}
	}
	
	/*
	 * Calls the train if the players are near the tracks and loads the next map
	 * 
	 * @param delta					Seconds elapsed since last frame
	 */
	private void updateLogic(float delta) 
	{
		// If the game is won, lost, or terminated, no logic should be run
		if (gameWon || gameLost || terminated)
		{
			// Update player logic and HUD
			for (int i = 0; i < numPlayers; i++)
			{
				players.get(i).updatePlayer(delta);
				
				playerProgress[i] = (players.get(i).getPosition().y - prevMaps) / (maps.get(curTrain).gSize() * 11 - 11);
				
				if (playerProgress[i] < 0)
				{
					playerProgress[i] = 0.0f;
				}
				else if (playerProgress[i] > 1)
				{
					playerProgress[i] = 1.0f;
				}
			}
			
			return;
		}
		else if(CGCWorld.getNumPrisoners() <= 0 || allTiedUp())
		{
			lose();
		}
		
		// Check if the next map should be generated or not yet
		if(onePlayerPastTrigger(curTrain) && !nextMapLoaded)
		{
			// If the current map (array style) is still less than maxMaps (needs another map)
			if (curTrain + 1 < MAX_MAPS)
			{
				MapSorter tempSorter = new MapSorter(mapList, false);
				tempSorter.filterMapsByPlayersSecondary(); // Make all of the maps in the list playable by any number of players
				
				mdata = tempSorter.getMaps().get(random.nextInt(tempSorter.getMaps().size()));
				if (curTrain+2 == MAX_MAPS) // If this is the last map, spawn it as an end map
				{
					maps.add(new MapBuilder(mdata.mdata, false, true, maps.get(curTrain).gTotalSize(), mdata.mid, false));
				}
				else // Otherwise just spawn a middle map
				{
					maps.add(new MapBuilder(mdata.mdata, false, false, maps.get(curTrain).gTotalSize(), mdata.mid, false));
				}
				
				nextMapLoaded = true;
				changedMapName = false;
				
				Gdx.app.log("Setting map as a favorite", "Huh?");
				ChaseApp.favorite.sMap(mdata);
				if (Options.storedTrackingOption)
				{
					ChaseApp.stats.addMapToGame(mdata.mid);
				}
			}
		}
		
		// If the players get past the train tracks, change the displayed name
		if(mdata != null && onePlayerPastTracks(lastBeatenMap) && !changedMapName)
		{
			mapInfo = new MapInfo(sBatch, mdata.mid+" : "+mdata.mname+" - "+mdata.uname, false);
			mapInfo.setShow(true);
			
			changedMapName = true;
		}
		
		if (Options.storedTrackingOption)
		{
			// Check if this map has been beaten
			if (!mapsCompleted && getPrisonerMaximum() > maps.get(lastBeatenMap).gTotalSize()*11 - 1 &&
					ChaseApp.stats.getStatByIndex(lastBeatenMap).mapCompletionTime == 0)
			{
				ChaseApp.stats.getStatByIndex(lastBeatenMap).beaten = true;
				ChaseApp.stats.getStatByIndex(lastBeatenMap).mapCompletionTime = mapTime / 1000;
				lastBeatenMap++;
				mapTime = 0;
				
				// If the last map beaten is somehow past the max number of maps, set it to the max number of maps
				if (lastBeatenMap >= MAX_MAPS)
				{
					lastBeatenMap = MAX_MAPS - 1;
				}
			}
		}
		
		// If the train isn't summoned and the players have triggered it...
		if (!summoned && playersPast(curTrain))
		{
			summoned = true;
			
			bf.summonTrain(curTrain);
			//SoundManager.playSound("trainWhistle", false);

			curTrain++;
			nextMapLoaded = false;

			if (curTrain != maps.size) // Leave summoned as true on the last map since there are no more trains
			{
				respawnHeight = (maps.get(curTrain).gLastMapsSize() - 0.4f) * 11;
				summoned = false;
			}
			if (curTrain == MAX_MAPS)
			{
				curTrain--;
				respawnHeight = (maps.get(curTrain).gTotalSize() - 1.4f) * 11;
			}
			else
			{
				hudProgress.resetProgress();
			}
			
			prevMaps = (maps.get(curTrain).gLastMapsSize()) * 11;
		}
		
		// Check if the train has passed
		bf.updateTrains();
		
		boolean trainSurvived = true;
		
		while (bf.getCurrentTrain(trainsPast) == null && trainsPast > 0)
		{
			trainsPast--;
		}
		
		if (CGCWorld.lost() || trainsCompleted || CGCWorld.terminated())
		{
			trainSurvived = false;
		}
		else if (bf.getCurrentTrain(trainsPast) != null)
		{
			if (!bf.getCurrentTrain(trainsPast).get(2).isOffCamera())
			{
				trainSurvived = false;
			}
		}
		
		if (trainSurvived)
		{
			if (Options.storedTrackingOption)
			{
				for (int i = 0; i < numPlayers; i++)
				{
					if (players.get(i) instanceof Prisoner && players.get(i).isAlive())
					{
						ChaseApp.stats.getStatByIndex(trainsPast).survivingPrisoners++;
					}
				}
			}
			
			trainsPast++;
			if (trainsPast >= MAX_MAPS)
			{
				trainsPast = MAX_MAPS-1;
				trainsCompleted = true;
			}
		}
		
		// Determine when players have set foot on a new map
		if (Options.storedTrackingOption)
		{
			for (Player p : players)
			{
				if (!(p instanceof Prisoner))
				{
					continue;
				}
				
				int curMap = getMapByYPos(p.getBody().getPosition().y);
				
				if (curMap != ((Prisoner) p).getLastOnID())
				{
					((Prisoner) p).setOnNewID(curMap);
					ChaseApp.stats.getStatByIndex(curMap).startingPrisoners++;
				}
			}
		}
		
		// Update player logic and HUD
		for (int i = 0; i < numPlayers; i++)
		{
			players.get(i).updatePlayer(delta);
			
			playerProgress[i] = (players.get(i).getPosition().y - prevMaps) / (maps.get(curTrain).gSize() * 11 - 11);
			
			if (playerProgress[i] < 0)
			{
				playerProgress[i] = 0.0f;
			}
			else if (playerProgress[i] > 1)
			{
				playerProgress[i] = 1.0f;
			}
			
			if (Options.storedTrackingOption)
			{
				if (players.get(i) instanceof Prisoner)
				{
					ChaseApp.stats.getGame().timeAsPrisoner += (delta * 1000);
				}
				else if (players.get(i) instanceof RookieCop)
				{
					ChaseApp.stats.getGame().timeAsCop += (delta * 1000);
				}
			}
		}
		
		// Check if the players have beaten the game (not boss)
		if (maps.size == MAX_MAPS)
		{
			if (getPrisonerMinimum() > maps.get(MAX_MAPS-1).gTotalSize()*11)
			{
				camera.lock();
				keepGoingArrows.setShow(true);
				
				if (allUntiedPastCamera())
				{
					win();
				}
			}
			
			if (!mapsCompleted && getPrisonerMaximum() > maps.get(MAX_MAPS-1).gTotalSize()*11)
			{
				mapsCompleted = true;
			}
		}
		
		// Create a helicopter if needed
		if (createHeli)
		{
			Body b = bf.createRectangle(0, getPlayerAverageY(), 
						2, 3, BodyType.KinematicBody, 
						BodyFactory.MASK_NON_INTERACTIVE, BodyFactory.MASK_NON_INTERACTIVE);
			Helicopter h = new Helicopter(this, null, null, com.percipient24.cgc.art.TextureAnimationDrawer.helicopterAnim,
										EntityType.HELICOPTER, b);
			b.setUserData(h);
			b.getFixtureList().get(0).setSensor(true);
			h.addToWorldLayers(lh);

			createHeli = false;
		}
	}
	
	/*
	 * Gets the map ID for the map the specified chunk is in
	 * 
	 * @param						The chunk to find
	 * @return						The map ID of the map
	 */
	public int getMapFromChunk(int chunk)
	{
		for (int i = maps.size-1; i >= 0; i--)
		{
			if (chunk > maps.get(i).gLastMapsSize())
			{
				return i;
			}
		}
		
		return maps.size-1;
	}
	
	/*
	 * Gets the percentage completion of the respawn timer
	 * 
	 * @return 						The percentage completion of the respawn timer
	 */
	public float gRespawnPercent()
	{
		return respawnClock.getPercent();
	}
	
	/*
	 * Gets the Y position of the RookieCops' spawn location
	 * 
	 * @return 						The RookieCops' spawnY
	 */
	public float gSpawnY()
	{
		return respawnHeight;
	}
	
	/*
	 * Gets the array of recently deceased Players
	 * 
	 * @return						The recently deceased Players
	 */
	public Array<Player> gRecentlyDeceased()
	{
		return recentlyDeceased;
	}
	
	/*
	 * Respawns Prisoners that were offscreen
	 * 
	 * @param p						The Prisoner who was offscreen
	 */
	/*public void respawnPrisoner(Prisoner p)
	{
		Array<Prisoner> prisoners = new Array<Prisoner>();
		for(int i = 0; i < players.size; i++)
		{
			Player pl = players.get(i);
			
			if (p instanceof Prisoner)
			{
				Prisoner pr = (Prisoner)pl;
				
				if (pr.isAlive() && !pr.isGrabbed() && camera.isOnScreen(pr))
				{
					prisoners.add(pr);
				}
			}
		}
		
		p.getBody().getPosition().set(prisoners.random().getPosition());
	}*/
	
	/*
	 * Adjusts the camera dimensions to the new width and height
	 *
	 * @param width					The new width of the window
	 * @param height				The new height of the window
	 */
	protected void adjustCamera(int width, int height)
	{
		camera.getUpperWall().addToWorldLayers(lh);
		camera.getLowerWall().addToWorldLayers(lh);
	}
	
	/*
	 * Gets the current map index based on the specified Y-position
	 * 
	 * @param yPos					The Y-position to check
	 * @return						The map index at the Y-position
	 */
	private int getMapByYPos(float yPos)
	{
		int furthestIndex = -1;
		
		for (int i = 0; i < maps.size; i++)
		{
			if (yPos > maps.get(i).gLastMapsSize() * 11)
			{
				furthestIndex = i;
			}
		}
		
		return furthestIndex;
	}
} // End class
