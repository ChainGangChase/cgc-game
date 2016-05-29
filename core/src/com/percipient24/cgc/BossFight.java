/*
 * @(#)BossFight.java		0.2 14/2/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.b2dhelpers.BodyFactory;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.boss.BossBuilder;
import com.percipient24.cgc.boss.PallBearerBuilder;
import com.percipient24.cgc.boss.SteelHorseBuilder;
import com.percipient24.cgc.boss.TankBuilder;
import com.percipient24.cgc.boss.TrainRushBuilder;
import com.percipient24.cgc.boss.TrenchRunBuilder;
import com.percipient24.cgc.entities.ChainLink;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.Track;
import com.percipient24.cgc.entities.boss.Boss;
import com.percipient24.cgc.entities.boss.PallBearer;
import com.percipient24.cgc.entities.boss.Sheriff;
import com.percipient24.cgc.entities.boss.SteelHorse;
import com.percipient24.cgc.entities.players.CarrierCop;
import com.percipient24.cgc.entities.players.GunCop;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.players.RookieCop;
import com.percipient24.cgc.entities.players.SpotlightCop;
import com.percipient24.cgc.entities.players.SteelHorseRider;
import com.percipient24.cgc.entities.projectiles.Projectile;
import com.percipient24.cgc.entities.terrain.Mud;
import com.percipient24.cgc.entities.terrain.Terrain;
import com.percipient24.cgc.entities.terrain.Water;
import com.percipient24.cgc.entities.Spotlight;
import com.percipient24.cgc.entities.Targeter;
import com.percipient24.cgc.maps.MapBuilder;
import com.percipient24.cgc.overlays.BossHUDProgress;
import com.percipient24.cgc.overlays.KeepGoingArrows;
import com.percipient24.cgc.overlays.OffScreenTimer;
import com.percipient24.cgc.overlays.PauseMenu;
import com.percipient24.cgc.overlays.PunchMeArrow;
import com.percipient24.cgc.overlays.Transition;
import com.percipient24.cgc.screens.Options;
import com.percipient24.enums.BossType;
import com.percipient24.enums.ControlType;
import com.percipient24.enums.EntityType;
import com.percipient24.enums.Platform;
import com.percipient24.input.ControlAdapter;
import com.percipient24.tweens.TransitionAccessor;

/*
 * Contains the logic to run and render a boss fight
 * 
 * @version 0.2 14/2/24
 * @author Christopher Rider
 * @author William Ziegler
 * @author JD Kelly
 * @author Clayton Andrews
 */
public class BossFight extends CGCWorld 
{
	private BossType curBoss;
	private static BossBuilder level;
	private Boss boss;
	
	// HUD variables
	private BossHUDProgress bossHUDProgress;
	private OffScreenTimer offScreenTimer;
	private KeepGoingArrows keepGoingArrows;
	private PunchMeArrow punchMeArrow;
	
	//Timer variables
	private CGCTimer bossRespawnClock;
	private Timer.Task bossRespawnTask;
	private float bossRespawnTime = 1.0f;
	
	/*
	 * Gets the boss
	 * 
	 * @return							The boss
	 */
	public Boss getBoss()
	{
		return boss;
	}
	
	/*
	 * Creates a new BossFight object
	 * 
	 * @param app						The ChaseApp to use
	 * @param numPlayers				The number of players playing this game
	 * @param players					The players list from the first part of the game
	 * @param transition				The Transition to introduce this BossFight
	 * @param tutorial					Whether or not the preceding ChainGame had the tutorial map
	 */
	public BossFight(ChaseApp app, int numPlayers,
			Array<Player> players, Transition transition, boolean tutorial)
	{
		super(app, numPlayers, tutorial);
		TimerManager.start();
		
		camera.destroyWalls();
		bossFight = true;
		CGCWorld.players = players;
		
		tManager = ChaseApp.tManager;
		Tween.registerAccessor(Transition.class, new TransitionAccessor());
		this.transition = transition;
		
		// "Clean" prisoner chain data and make bodies for living prisoners
		int start = 3;
		for (int i = 0; i < players.size; i++)
		{
			Body tempBody;
			Player p = players.get(i);
			
			if (p instanceof Prisoner)
			{
				if (p.isAlive())
				{
					if (((Prisoner) p).isTiedUp())
					{
						((Prisoner) p).untie();
					}
					
					tempBody = bf.createPlayerBody(start+(p.getPID()*1.5f), 10, 0.6f, 
							BodyType.DynamicBody, BodyFactory.CAT_PRISONER, BodyFactory.MASK_PRISONER);
					tempBody.setFixedRotation(true);
					p.setBody(tempBody);
					tempBody.setUserData(p);
					p.addToWorldLayers(lh);

					if (!(p.getLeftPlayer() == null) && !p.getLeftPlayer().isAlive()) // If the player to the left is dead, break the chain
					{
						p.getLeftPlayer().setRightPlayer(null);
						p.setLeftPlayer(null);
					}
					if (!(p.getRightPlayer() == null) && !p.getRightPlayer().isAlive()) // If the player to the right is dead, break the chain
					{
						p.getRightPlayer().setLeftPlayer(null);
						p.setRightPlayer(null);
					}
					
					p.resetPlayer();
					((Prisoner) p).sGameWorld(this);
					numPrisoners++;
				}
			}
		}
		
		curBoss = transition.getBossType();
		myApp.alert("Boss", ""+curBoss);
		if (Options.storedTrackingOption)
		{
			ChaseApp.stats.getGame().bossType = curBoss;
		}
		
		// Create the appropriate boss world
		switch (curBoss)
		{
			case TANK: level = new TankBuilder(curBoss, false); numCops = 0; break;
			case TANK_AI: level = new TankBuilder(curBoss, true); numCops = 0; break;
			case STEEL_HORSE: level = new SteelHorseBuilder(curBoss); break;
			case PALL_BEARER: level = new PallBearerBuilder(curBoss); break;
			case TRAIN_RUSH: level = new TrainRushBuilder(curBoss, players); break;
			case TRENCH_RUN: level = new TrenchRunBuilder(curBoss); break;
			default: level = new TankBuilder(curBoss, true); break;
		}
		
		// Create the initial cops for the boss fight
		Array<RookieCop> rookieCops = new Array<RookieCop>();
		
		if(curBoss == BossType.PALL_BEARER || curBoss == BossType.STEEL_HORSE)
		{
			boss = level.createBoss();
		}
		
		for (int i = 0; i < players.size; i++)
		{
			Body tempBody;
			Player p = players.get(i);
			
			if ((p instanceof Prisoner && !p.isAlive()) || p instanceof RookieCop)
			{
				switch (curBoss)
				{

					case TANK_AI:
						int curGunCop = 0;
						p.removeFromWorldLayers(lh);

						tempBody = bf.createPlayerBody(-100+(p.getPID()*1.5f), -100, 0.6f, 
								BodyType.DynamicBody, BodyFactory.CAT_TIED_PRISONER, BodyFactory.MASK_BOSS_COP);
						tempBody.setFixedRotation(true);
						
						GunCop gc = new GunCop(this, this, AnimationManager.copStandLowAnims[p.getPID()],
								AnimationManager.copStandMidAnims[p.getPID()],
								AnimationManager.copStandHighAnims[p.getPID()],
								EntityType.COP, tempBody, p.getPID(), curGunCop, false);
						gc.setBody(tempBody);
						tempBody.setUserData(gc);
						gc.addToWorldLayers(lh);
						
						rookieCops.add(gc);
						
						curGunCop++;
						gc.resetPlayer();
						break;
					case TANK:
						boolean tankAssigned = false;
						curGunCop = 0;
						p.removeFromWorldLayers(lh);
						
						tempBody = bf.createPlayerBody(-100+(p.getPID()*1.5f),-100, 0.6f, 
								BodyType.DynamicBody, BodyFactory.CAT_TIED_PRISONER, BodyFactory.MASK_TIED_PRISONER);
						tempBody.setFixedRotation(true);
						
						if (!tankAssigned)
						{
							gc = new GunCop(this, this, AnimationManager.copStandLowAnims[p.getPID()],
									AnimationManager.copStandMidAnims[p.getPID()],
									AnimationManager.copStandHighAnims[p.getPID()],
									EntityType.COP, tempBody, p.getPID(), 0, true);
							tempBody.setUserData(gc);
							gc.addToWorldLayers(lh);
							rookieCops.add(gc);
							gc.resetPlayer();
							tankAssigned = true;
							((TankBuilder) level).setTankController(gc);
							boss = level.createBoss();
							Gdx.app.log("Game", "Creating tank boss");
						}
						else
						{
							gc = new GunCop(this, this, AnimationManager.copStandLowAnims[p.getPID()],
									AnimationManager.copStandMidAnims[p.getPID()],
									AnimationManager.copStandHighAnims[p.getPID()],
									EntityType.COP, tempBody, p.getPID(), curGunCop, false);
							tempBody.setUserData(gc);
							gc.addToWorldLayers(lh);
							rookieCops.add(gc);
							gc.resetPlayer();
							curGunCop++;
						}
						break;
					case STEEL_HORSE:
						p.removeFromWorldLayers(lh);
						tempBody = CGCWorld.getBF().createCircle(start+(p.getPID()*1.5f), 10, 0.6f, 
								BodyType.DynamicBody, BodyFactory.CAT_COP, BodyFactory.MASK_COP);
						
						SteelHorseRider shr = new SteelHorseRider(this, 
								AnimationManager.copStandLowAnims[p.getPID()],
								AnimationManager.copStandMidAnims[p.getPID()], 
								AnimationManager.copStandHighAnims[p.getPID()],
								EntityType.COP, tempBody, p.getPID(), (SteelHorse) boss);
						tempBody.setUserData(shr);
						shr.addToWorldLayers(lh);
						rookieCops.add(shr);
						shr.resetPlayer();
						((SteelHorse) boss).addRider(shr);
						break;
					case PALL_BEARER:
						myApp.alert(boss instanceof PallBearer);
						p.removeFromWorldLayers(lh);
						tempBody = bf.createPlayerBody(start+(p.getPID()*1.5f), 10, 0.6f, BodyType.KinematicBody,
								BodyFactory.CAT_CARRIER_COP, BodyFactory.MASK_CARRIER_COP);
						CarrierCop cc = new CarrierCop(this, AnimationManager.copStandLowAnims[p.getPID()],
								AnimationManager.copStandMidAnims[p.getPID()],
								AnimationManager.copStandHighAnims[p.getPID()],
								EntityType.COP, tempBody, p.getPID());
						tempBody.setUserData(cc);
						cc.addToWorldLayers(lh);
						rookieCops.add(cc);
						cc.resetPlayer();
						((PallBearer) boss).addCarrier(cc);
						break;
					case TRENCH_RUN:
						p.removeFromWorldLayers(lh);
						tempBody = bf.createPlayerBody(start+(p.getPID()*1.5f), 10, 0.6f, BodyType.DynamicBody, 
								BodyFactory.CAT_COP, BodyFactory.MASK_COP);
						SpotlightCop sc = new SpotlightCop(this, AnimationManager.copStandLowAnims[p.getPID()],
								AnimationManager.copStandMidAnims[p.getPID()], 
								AnimationManager.copStandHighAnims[p.getPID()],
								EntityType.COP, tempBody, p.getPID(),
								camera.position.x, camera.position.y);
						tempBody.setUserData(sc);
						sc.addToWorldLayers(lh);
						rookieCops.add(sc);
						sc.resetPlayer();
						tempBody.setTransform(-100f,  -100f, 0);
						break;
					default:
						break;
				}
				
				numCops++;
			}
		}
		myApp.alert("BossFight", "Teleport complete. Activating manual control");
		
		// Re-chain any players that are chained together
		for (Player p: players)
		{
			if (p instanceof Prisoner)
			{
				if (p.getRightPlayer() != null)
				{
					chainTogether(p, p.getRightPlayer(), ChainGame.numChainsInLink);
					p.setChainedRight();
					p.getRightPlayer().setChainedLeft();
				}
			}
		}
		
		// Re-add any chains that were attached to players but not destroyed
		for (Player p: players)
		{
			if (p instanceof Prisoner)
			{
				recreateChains(p);
			}
		}
		
		if (curBoss != BossType.TANK && curBoss != BossType.PALL_BEARER && curBoss != BossType.STEEL_HORSE)
		{
			boss = level.createBoss();
		}
		
		createBossHUD(curBoss);
		
		pauseMenu = new PauseMenu(sBatch, this, myApp, this);
		
		CGCWorld.emptyDestroyList();
		
		camera.zoom = 0.010416666667f;
		camera.setPosition(9.5f, 5f);
		camera.setParallaxPoint(camera.position.x, camera.position.y);
		camera.unlock();
		camera.reset();
		camera.update();
		
		// Recreate the control schemes
		schemes = new Array<ControllerScheme>(players.size);
		for (int i = 0; i < input.controlList.length; i++)
		{
			if (input.controlList[i].isUsed())
			{
				ControllerScheme cs = new ControllerScheme(players.get(input.controlList[i].getPID()),
															input.controlList[i].isLeft());
				cs.setController(input.controlList[i]);
				schemes.add(cs);
				players.get(input.controlList[i].getPID()).setScheme(cs);
			}
		}
		
		for (int i = 0; i < rookieCops.size; i++)
		{
			RookieCop rc = rookieCops.get(i);
			Player pl = players.get(rc.getPID());
			ControllerScheme sc = pl.getScheme();
			rc.setScheme(sc);
			sc.setPlayer(rc);
			players.set(rc.getPID(), rc);
		}
		
		cm = new ContactManager();
		world.setContactListener(cm);
		
		for(int i = 0; i < 18; i++)
		{
			world.step(1 / 60f, 6, 2);
			world.clearForces();
		}
		
		setTimersAndTasks();
		hudMatrix = new Matrix4().setToOrtho2D(0f, 0f, (float)Data.ACTUAL_WIDTH, (float)Data.ACTUAL_HEIGHT);
	
		for (int i = 0; i < numPlayers; i++)
		{
			players.get(i).getBody().applyLinearImpulse(0f, 0.011f, 
					players.get(i).getBody().getPosition().x, 
					players.get(i).getBody().getPosition().y,
					true);
		}
	}
	
	/*
	 * Chains the specified players together
	 * 
	 * @param p1					The left player of the chain
	 * @param p2					The right player of the chain
	 * @param numChainLinks			Number of links in the chain - Must be odd - Too many will break chain physics
	 */
	private void chainTogether(Player p1, Player p2, int numChainLinks)
	{
		Array<Body> bodyList = new Array<Body>();
		Body b;
		GameEntity ge;
		
		for (int j = 0; j < numChainLinks; j++)
		{
			b = bf.createRectangle(p1.getBody().getPosition().x+0.3f+0.2f*j, p1.getBody().getPosition().y, 
									0.2f, 0.1f, BodyType.DynamicBody, BodyFactory.CAT_CHAIN, 
									BodyFactory.MASK_CHAIN, CGCWorld.chainDensity);
			bodyList.add(b);
			if (j%2 == 1)
			{
				ge = new ChainLink(AnimationManager.chainAnims[0], null, null, EntityType.CHAINLINK, b);
			}
			else
			{
				ge = new ChainLink(AnimationManager.chainAnims[1], null, null, EntityType.CHAINLINK, b);
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
				bf.createRevoluteJoint(p1.getBody(), bodyList.get(j),
										new Vector2(0.0f,0f), new Vector2(-0.4f,0f));
			}
			else if (j == numChainLinks)
			{
				bf.createRevoluteJoint(bodyList.get(j-1), p2.getBody(),
										new Vector2(0.4f,0f), new Vector2(0.0f,0f));
			}
			else
			{
				bf.createRevoluteJoint(bodyList.get(j-1), bodyList.get(j),
										new Vector2(0.1f,0f), new Vector2(-0.1f,0f));
			}
		}
		
		Joint middle = bf.createRopeJoint(p1.getBody(), p2.getBody(), 0.27f*numChainLinks);
		
		Player left = p1;
		Player right = p2;
		
		CGCWorld.patcher.leftRightCenter(left, right, middle);
		
		for (int j = 0; j < numChainLinks; j++)
		{
			CGCWorld.patcher.setupChain(bodyList.get(j), left);
		}
	}
	
	/*
	 * Creates lingering chains that don't bind Players together
	 * 
	 * @param p						The Player to rebuild chains for
	 */
	private void recreateChains(Player p)
	{
		ChainLink tempLink = p.getLeftChain();
		int curChain = 0;
		Array<Body> bodyList = new Array<Body>();
		Body b;
		
		// Create the chains on the left of the player
		
		// Recreate the bodies for the chain-links
		while(tempLink != null && !p.isChainedLeft())
		{
			if (tempLink.getBody() != null)
			{
				tempLink.sBody(null);
			}
			
			b = bf.createRectangle(p.getBody().getPosition().x-0.3f-0.2f*curChain, p.getBody().getPosition().y, 
									0.2f, 0.1f, BodyType.DynamicBody, BodyFactory.CAT_CHAIN, 
									BodyFactory.MASK_CHAIN, CGCWorld.chainDensity);
			
			bodyList.add(b);

			b.setUserData(tempLink);
			b.setAngularDamping(1000.0f);
			
			tempLink.sBody(b);
			if (tempLink.getLeftLink() == null)
			{
				tempLink.sLeft(null);
			}
			
			Array<Body> bodyArray = new Array<Body>();
			world.getBodies(bodyArray);
			boolean contained = bodyArray.contains(tempLink.getBody(), true);
			Gdx.app.log("Game", ""+contained);
			
			tempLink = tempLink.getLeftLink();
			curChain++;
		}
		
		// Re-add the chains to the bodyFactory layers
		for (int i = 0, j = 0; i < curChain; i++, j++)
		{
			if (i % 2 == 0 && j < curChain)
			{
				((ChainLink) (bodyList.get(i).getUserData())).addToWorldLayers(lh);
			}
			else if (i % 2 == 0 && i + 1 < curChain)
			{
				((ChainLink) (bodyList.get(i+1).getUserData())).addToWorldLayers(lh);
			}
			
			if (i == curChain - 1 && j <= curChain)
			{
				i = -1;
			}
		}
		
		// Recreate the joints between the chain-links
		for (int i = 0; i < curChain; i++)
		{
			if (i == 0)
			{
				bf.createRevoluteJoint(p.getBody(), bodyList.get(i),
										new Vector2(0.0f,0f), new Vector2(-0.4f, 0f));
			}
			else
			{
				bf.createRevoluteJoint(bodyList.get(i - 1), bodyList.get(i),
										new Vector2(0.1f, 0f), new Vector2(-0.1f, 0f));
			}
		}
		
		// Create the chains on the right of the player
		
		tempLink = p.getRightChain();
		curChain = 0;
		bodyList.clear();
		
		// Recreate the bodies for the chain-links
		while(tempLink != null && !p.isChainedRight())
		{
			tempLink.sBody(null);
			
			b = bf.createRectangle(p.getBody().getPosition().x+0.3f+0.2f*curChain, p.getBody().getPosition().y, 
									0.2f, 0.1f, BodyType.DynamicBody, BodyFactory.CAT_CHAIN, 
									BodyFactory.MASK_CHAIN, CGCWorld.chainDensity);
			bodyList.add(b);

			b.setUserData(tempLink);
			b.setAngularDamping(1000.0f);
			
			tempLink.sBody(b);
			tempLink = tempLink.getRightLink();
			curChain++;
		}
		
		// Re-add the chains to the bodyFactory layers
		for (int i = 0, j = 0; i < curChain; i++, j++)
		{
			if (i % 2 == 0 && j < curChain)
			{
				((ChainLink) (bodyList.get(i).getUserData())).addToWorldLayers(lh);
			}
			else if (i % 2 == 0 && i+1 < curChain)
			{
				((ChainLink) (bodyList.get(i+1).getUserData())).addToWorldLayers(lh);
			}
			
			if (i == curChain - 1 && j <= curChain)
			{
				i = -1;
			}
		}
		
		// Recreate the joints between the chainlinks
		for (int i = 0; i < curChain; i++)
		{
			if (i == 0)
			{
				bf.createRevoluteJoint(p.getBody(), bodyList.get(i),
										new Vector2(0.0f,0f), new Vector2(-0.4f,0f));
			}
			else
			{
				bf.createRevoluteJoint(bodyList.get(i-1),bodyList.get(i),
										new Vector2(0.1f,0f), new Vector2(-0.1f,0f));
			}
		}
	}
	
	/*
	 * Pauses the game
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#pause()
	 */
	public void pause(int pauseController) 
	{
		ControlAdapter pauseBoss = schemes.get(pauseController).getController();
		
		pauseBoss.changeControlState(ControlType.PAUSE, false);
		
		TimerManager.setPause(true);
		SoundManager.pause(true);
		
		if (boss != null)
		{
			boss.pause();
		}
		
		pauseMenu.setPauseBoss(pauseBoss);
		pauseMenu.resetSelected();
		pauseMenu.setShow(true);
		paused = true;
	}

	/*
	 * Resumes the game
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#resume()
	 */
	public void resume() 
	{
		if (pauseMenu.getShow())
		{
			ControlAdapter pauseBoss = pauseMenu.getPauseBoss();
			
			pauseBoss.changeControlState(ControlType.PAUSE, false);
			pauseMenu.setShow(false);
		}
		
		TimerManager.setPause(false);
		SoundManager.pause(false);
		
		if (boss != null)
		{
			boss.resume();
		}
		
		paused = false;
	}
	
	/*
	 * Creates the proper HUD elements for the BossFight that is starting
	 * 
	 * @param boss					The BossType of the BossFight that is starting
	 */
	private void createBossHUD(BossType boss)
	{
		switch (boss)
		{
		case NONE:
			break;
		case PALL_BEARER:
		case STEEL_HORSE:
			punchMeArrow = new PunchMeArrow(sBatch);
			punchMeArrow.setShow(false);
			break;
		case TRENCH_RUN:
			offScreenTimer = new OffScreenTimer(sBatch);
			offScreenTimer.setShow(true);
		case TANK:
		case TANK_AI:
			bossHUDProgress = new BossHUDProgress(sBatch, curBoss);
			bossHUDProgress.setShow(true);
			keepGoingArrows = new KeepGoingArrows(sBatch);
			keepGoingArrows.setShow(false);
			break;
		case TRAIN_RUSH: //TODO: show a timer (potentially drawing from the Conductor)
			break;
		default:
			break;
		}
	}
	
	/*
	 * Handles end game cleanup for BossFight
	 * 
	 * @param won					Whether or not the Prisoners won the BossFight
	 */
	public void endGameWorld(boolean won)
	{
		myApp.alert("BossFight", "End game function");
		TimerManager.clear();
		Gdx.gl.glViewport(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		endGameStats(won);
		
		if (won)
		{
			myApp.setScreen(ChaseApp.win);
		}
		else
		{
			myApp.alert("Setting screen", "To lose");
			myApp.setScreen(ChaseApp.lose);
		}
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
		
		shapes.begin(ShapeType.Filled);
		shapes.setColor(Color.BLACK);
		shapes.rect(0, 0, Data.ACTUAL_WIDTH, Data.ACTUAL_HEIGHT);
		shapes.end();
		
		// Handle camera logic
		if (curBoss == BossType.TRENCH_RUN || curBoss == BossType.TANK || curBoss == BossType.TANK_AI)
		{
			camera.adjust(players, false);
		}
		else if(curBoss == BossType.TRAIN_RUSH)
		{
			//we DON'T want to move the camera here
			camera.setPosition(camera.position.x, 5.5f);
		}
		else
		{
			camera.adjust(players, true);
		}
		
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
		
		Color cfa = sBatch.getColor();
		if(lost())
		{
			cfa.g = cfa.r = cfa.b = 1.0f - endClock.getPercent();
		}
		cfa.a = 1.0f;
		sBatch.setColor(cfa);
		// Render player-specific HUD elements
		sBatch.begin();
		for (int i = 0; i < players.size; i++)
		{
			if (players.get(i).getShowCallout())
			{	
				TextureRegion calloutFrame = AnimationManager.calloutAnims[i].getKeyFrame(0);
				
				sBatch.draw(calloutFrame, players.get(i).getBody().getPosition().x, 
							players.get(i).getBody().getPosition().y, -0.5f, -0.5f, 
							calloutFrame.getRegionWidth(), calloutFrame.getRegionHeight(), 
							camera.zoom, camera.zoom, 0);
			}
			
			if (players.get(i) instanceof RookieCop)
			{
				if (((RookieCop) players.get(i)).getBarRatio() < 1.0f && !(players.get(i) instanceof SteelHorseRider))
				{
					TextureRegion grabBack = AnimationManager.barBackAnim.getKeyFrame(0);
					TextureRegion grabFill = AnimationManager.grabFillAnim.getKeyFrame(0);
					
					sBatch.draw(grabBack, players.get(i).getBody().getPosition().x, 
								players.get(i).getBody().getPosition().y + .2f, -0.5f, -0.5f, 
								grabBack.getRegionWidth(), grabBack.getRegionHeight(), 
								camera.zoom, camera.zoom, 0);
					
					sBatch.draw(grabFill, players.get(i).getBody().getPosition().x + .02f, 
								players.get(i).getBody().getPosition().y + .22f, -0.5f, -0.5f, 
								grabFill.getRegionWidth(), grabFill.getRegionHeight(), 
								camera.zoom, camera.zoom * ((RookieCop) players.get(i)).getBarRatio(), 0);
				}
				if (((RookieCop) players.get(i)).isGrabCooldown())
				{
					TextureRegion tempRegion = AnimationManager.dazedAnim.getKeyFrame(0);
					
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
			else
			{
				if (((Prisoner) players.get(i)).getBarRatio() < 1.0f)
				{
					TextureRegion grabBack = AnimationManager.barBackAnim.getKeyFrame(0);
					TextureRegion staminaFill = AnimationManager.staminaFillAnim.getKeyFrame(0);
					
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
		renderHUD();
		pauseMenu.render(delta);
		
		if (transitioning)
		{
			tManager.update(delta);
			transition.render(delta);
		}
		
		emptyDestroyList(); // Destroy any flagged entities and run final destroy logic
		
		// Step through world physics
		if (!paused && toDestroyList.size <= 0)
		{
			world.step(1/60f, 6, 2);
			world.clearForces();
		}
	}
	
	/*
	 * Renders the HUD elements for this boss fight
	 */
	public void renderHUD()
	{
		sBatch.begin();
		switch (curBoss)
		{
		case PALL_BEARER:
			Sheriff s = ((PallBearer)boss).gSheriff();
			Vector2 paPos = Vector2.Zero;
			paPos = new Vector2(0, 0);
			paPos.x = (Data.ACTUAL_WIDTH - animManager.gWidth(s.getHighAnim()))
					/ 2  - (9.75f - s.getBody().getWorldCenter().cpy().x)
					* Data.ACTUAL_WIDTH / (20.0f);
			
			paPos.y =  (Data.ACTUAL_HEIGHT - animManager.gHeight(s.getHighAnim())) / 2  
					- (camera.position.cpy().y - s.getBody().getWorldCenter().cpy().y)
					* Data.ACTUAL_HEIGHT / (12.25f);
			
			punchMeArrow.setPosition(paPos);
			punchMeArrow.render(delta);
			break;
		case STEEL_HORSE:
			s = ((SteelHorse)boss).gSheriff();
			paPos = new Vector2(0, 0);
			paPos.x = (Data.ACTUAL_WIDTH - animManager.gWidth(s.getHighAnim()))
					/ 2  - (9.75f - s.getBody().getWorldCenter().cpy().x)
					* Data.ACTUAL_WIDTH / (20.0f);
			
			paPos.y =  (Data.ACTUAL_HEIGHT - animManager.gHeight(s.getHighAnim())) / 2  
					- (camera.position.cpy().y - s.getBody().getWorldCenter().cpy().y)
					* Data.ACTUAL_HEIGHT / (12.25f);
			
			punchMeArrow.setPosition(paPos);
			punchMeArrow.render(delta);
			break;
		case TANK:
		case TANK_AI:
			bossHUDProgress.render(delta, boss.getBody().getPosition().y, level.getLevelLength());
			keepGoingArrows.render(delta);
			break;
		case TRAIN_RUSH: //TODO: Add the HUD (What HUD elements do we need for this? -Will)
			break;
		case TRENCH_RUN:
			bossHUDProgress.render(delta, level.getLevelLength());
			offScreenTimer.render(delta);
			keepGoingArrows.render(delta);
			break;
		default:
			break;
		}
		sBatch.end();
	}
	
	/*
	 * Moves the Transition off of the screen so the boss fight may begin
	 */
	private void finishTransition()
	{
		transitioning = true;
		transition.setShow(true);
		
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
	 * Draws the bodies for this world
	 * 
	 * @param c						The Camera to render from
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
		
		if (curBoss == BossType.TRENCH_RUN)
		{
			colorForAlpha.b = 0.7f;
			colorForAlpha.g = 0.5f;
			colorForAlpha.r = 0.5f;
		}
		
		float endPer = 0.0f;
		
		if(lost())
		{
			endPer = 1.0f - endClock.getPercent();
			colorForAlpha.b *= endPer;
			colorForAlpha.g *= endPer;
			colorForAlpha.r *= endPer;
		}
		
		colorForAlpha.a = 1.0f;
		sBatch.setColor(colorForAlpha);
		
		for (int i = 0; i < lh.getLayers().size; i++)
		{
			sBatch.begin();
			drawn = 0;
			
			if (lh.getLayer(i).hasGrid())
			{
				for (int y = startHeight; y < endHeight; y++)
				{
					for (int x = 0; x < MapBuilder.chunkWidth; x++)
					{
						if (i == LayerHandler.background)
						{
							t = lh.getLayer(i).getTerrain(x, y);
							
							if (t == null) // If the terrain is background...
							{
								terrainFrame = AnimationManager.bgAnims[0].getKeyFrame(0);
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
									terrainFrame = AnimationManager.ouyaWater.getKeyFrame(0);
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
									terrainFrame = AnimationManager.currentAnims[w.getDirection()].getKeyFrame(Water.getCurrentTime());
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
									terrainFrame = AnimationManager.ouyaMud.getKeyFrame(0);
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

						for (int j = 0; j < lh.getLayer(i).getEntitiesInGrid(x, y).size; j++)
						{
							ge = lh.getLayer(i).getEntitiesInGrid(x, y).get(j);
							
							if (ge != null && ge.hasAnimation())
							{
								ge.draw(sBatch, delta, i);
								drawn++;
								if (drawn == MAX_IMAGES_PER_DRAW)
								{
									drawn = 0;
									sBatch.flush();
								}
							} // End grid entity drawing
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
					if (ge instanceof Spotlight && curBoss == BossType.TRENCH_RUN)
					{
						sBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
					}
					
					ge.draw(sBatch, delta, i);
					
					drawn++;
					if (drawn == MAX_IMAGES_PER_DRAW)
					{
						drawn = 0;
						sBatch.flush();
					}
					
					if (ge instanceof Spotlight && curBoss == BossType.TRENCH_RUN)
					{
						sBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					}
					
					
					if (i > LayerHandler.MID && (ge instanceof Targeter) && ((Targeter) ge).getOwner() >= 0)
					{
						sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
						
						TextureRegion calloutFrame = AnimationManager.calloutAnims[((Targeter) ge).getOwner()].getKeyFrame(0);
						
						sBatch.draw(calloutFrame, ge.getBody().getPosition().x, 
									ge.getBody().getPosition().y, -0.5f, -0.5f, 
									calloutFrame.getRegionWidth(), calloutFrame.getRegionHeight(), 
									camera.zoom, camera.zoom, 0);
						
						sBatch.setColor(colorForAlpha);
					}
				}
			} // End non-grid drawing
			sBatch.end();
		} // End layer loop
		
		// Handle alpha
		colorForAlpha = sBatch.getColor();
		colorForAlpha.a = 1.0f;
		
		if (curBoss == BossType.TRENCH_RUN)
		{
			colorForAlpha.b = 1.0f;
			colorForAlpha.g = 1.0f;
			colorForAlpha.r = 1.0f;
		}
		
		sBatch.setColor(colorForAlpha);
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
		
		if (bossHUDProgress != null)
		{
			bossHUDProgress.resize();
		}
		if (keepGoingArrows != null)
		{
			keepGoingArrows.resize();
		}
		if (punchMeArrow != null)
		{
			punchMeArrow.resize();
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
		super.show();
	}
	
	/*
	 * Handles logic for each update
	 * 
	 * @param delta					Seconds elapsed since last frame
	 */
	private void updateLogic(float delta)
	{	
		if (!paused && !transitioning)
		{
			if(boss != null)
			{
				boss.move(delta);
				boss.update(delta);
			}
			
			GameEntity ge;
			
			for (int i = 0; i < lh.getLayer(LayerHandler.projectile).getEntities().size; i++)
			{
				ge = lh.getLayer(LayerHandler.projectile).getEntities().get(i);
				
				if (ge instanceof Projectile)
				{
					((Projectile) ge).move();
					((Projectile) ge).checkPosition(camera);
				}
			}
			
			for (int i = 0; i < numPlayers; i++)
			{
				players.get(i).updatePlayer(delta);
				
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
		}
		
		if (!gameWon && !gameLost && !terminated)
		{
			if((getNumPrisoners() <= 0 || allTiedUp()))
			{
				myApp.alert("BossFight", "Being lost");
				lose();
			}
			
			if (curBoss == BossType.TANK || curBoss == BossType.TANK_AI || curBoss == BossType.TRENCH_RUN)
			{
				if (getPrisonerMinimum() / 11 > level.getLevelLength())
				{
					camera.lock();
					keepGoingArrows.setShow(true);
					
					if (allUntiedPastCamera())
					{
						win();
					}
				}
			}
			
			else if(curBoss == BossType.TRAIN_RUSH)
			{
				camera.lock();
				
				if(boss.isDefeated())
				{
					win();
				}
			}
			
			else if(curBoss == BossType.STEEL_HORSE || curBoss == BossType.PALL_BEARER)
			{
				if(boss.isDefeated())
				{
					win();
				}
				
				else if(curBoss == BossType.STEEL_HORSE && ((SteelHorse)boss).isDead()) {
					if(!punchMeArrow.getShow())
					{
						punchMeArrow.setShow(true);
						Sheriff s = ((SteelHorse)boss).gSheriff();
						Vector2 paPos = s.getBody().getWorldCenter().cpy();
					
						paPos.x = (Data.ACTUAL_WIDTH - CGCWorld.getAnimManager().gWidth(s.getHighAnim()))
								/ 2  - (9.5f - s.getBody().getWorldCenter().cpy().x) * Data.BOX_TO_SCREEN 
								* Data.ACTUAL_WIDTH / (20.0f * Data.BOX_TO_SCREEN);
						
						paPos.y =  (Data.ACTUAL_HEIGHT - CGCWorld.getAnimManager().gHeight(s.getHighAnim()))
								/ 2  - (3.75f - s.getBody().getWorldCenter().cpy().y) * Data.BOX_TO_SCREEN 
								* Data.ACTUAL_HEIGHT / (7.5f * Data.BOX_TO_SCREEN);
						punchMeArrow.setPosition(paPos);
					}
				}
				
				else if(curBoss == BossType.PALL_BEARER && ((PallBearer)boss).isDead())
				{
					if(!punchMeArrow.getShow())
					{
						punchMeArrow.setShow(true);
						Sheriff s = ((PallBearer)boss).gSheriff();
						Vector2 paPos = s.getBody().getWorldCenter().cpy();
					
						paPos.x = (Data.ACTUAL_WIDTH - CGCWorld.getAnimManager().gWidth(s.getHighAnim()))
								/ 2  - (9.5f - s.getBody().getWorldCenter().cpy().x) * Data.BOX_TO_SCREEN 
								* Data.ACTUAL_WIDTH / (20.0f * Data.BOX_TO_SCREEN);
						
						paPos.y = Data.ACTUAL_HEIGHT / 2;
						punchMeArrow.setPosition(((PallBearer)boss).gSheriff().getBody().getWorldCenter().cpy());
					}
				}
			}
		}
	}
	
	/*
	 * Creates the Timers and Tasks
	 */
	public void setTimersAndTasks()
	{
		super.setTimersAndTasks();
		
		bossRespawnTask = new Timer.Task()
		{
			public void run()
			{
				spawnCops();
			}
		};
		
		bossRespawnClock = new CGCTimer(bossRespawnTask, bossRespawnTime, false, "bossRespawnClock");
	}
	
	/*
	 * Controls the respawn timer for dead Players
	 * 
	 * @param p						The Player who is about to respawn
	 */
	public void startRespawnClock(Player p)
	{
		if (!recentlyDeceased.contains(p, true))
		{
			recentlyDeceased.add(p);
			
			if (p instanceof Prisoner)
			{
				numPrisoners--;
			}
			else
			{
				numCops--;
			}
		}
		
		if (!bossRespawnClock.isRunning() && !endClock.isRunning())
		{	
			bossRespawnClock.reset();
			
			TimerManager.addTimer(bossRespawnClock);
		}
	}
	
	/*
	 * Gets the BossType of the current BossFight
	 * 
	 * @return						The BossType of the current BossFight
	 */
	public BossType getCurBoss()
	{
		return curBoss;
	}
	
	/*
	 * Respawns dead players as SpotlightCops in the Trench Run BossFight
	 */
	public void spawnCops()
	{
		if (curBoss == BossType.TRENCH_RUN)
		{
			for (int i = 0; i < recentlyDeceased.size; i++)
			{
				Player p = recentlyDeceased.get(i);
				
				if (p instanceof Prisoner)
				{
					p.removeFromWorldLayers(lh);
					
					Body tempBody = bf.createPlayerBody(-100, -100, 0.6f, 
							BodyType.DynamicBody, BodyFactory.CAT_COP, BodyFactory.MASK_COP);
					
					SpotlightCop sc = new SpotlightCop(this, ((Prisoner) p).getLowAnim(),
													((Prisoner) p).getMidAnim(), ((Prisoner) p).getHighAnim(),
													EntityType.COP, tempBody, p.getPID(), 
													camera.position.x, camera.position.y);
					tempBody.setUserData(sc);
					sc.addToWorldLayers(lh);
					
					sc.resetPlayer();
					
					Player pl = players.get(sc.getPID());
					ControllerScheme cs = pl.getScheme();
					sc.setScheme(cs);
					cs.setPlayer(sc);
					players.set(sc.getPID(), sc);
				}
			}
		}
		
		recentlyDeceased.clear();
	}
	
	/*
	 * Gets this BossFight's BossBuilder
	 * 
	 * @return						This BossFight's BossBuilder
	 */
	public static BossBuilder getLevel()
	{
		return level;
	}
	
	/*
	 * Gets the distance of the Player closest to the start
	 * 
	 * @return						The distance of the closest Player
	 */
	public static float getPlayerMinimum()
	{
		float min = Float.MAX_VALUE;
		
		for (int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			
			if(!(level instanceof TankBuilder && p instanceof GunCop))
			{
				if (p.isAlive() && p.getPosition().y < min)
				{
					min = p.getPosition().y;
				}
			}
		}
		
		if(numPlayers > 0)
		{
			return min;
		}
		else
		{
			return Float.MIN_VALUE;
		}
	}
	
	/*
	 * Gets the distance of the Player farthest from the start
	 * 
	 * @return						The distance of the farthest Player
	 */
	public static float getPlayerMaximum()
	{
		float max = -1;
		for (int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			
			if(!(level instanceof TankBuilder && p instanceof GunCop))
			{
				if (p.isAlive() && p.getPosition().y > max)
				{
					max = p.getPosition().y;
				}
				
			}
		}
		return max;
	}
	
} // End class