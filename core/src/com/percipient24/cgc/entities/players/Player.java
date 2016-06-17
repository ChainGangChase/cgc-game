/*
 * @(#)Player.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities.players;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.ChainGame;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.ControllerScheme;
import com.percipient24.cgc.SoundManager;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.entities.ChainLink;
import com.percipient24.cgc.entities.Coin;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.GuardTower;
import com.percipient24.cgc.entities.PlayerWall;
import com.percipient24.cgc.entities.RotatableEntity;
import com.percipient24.cgc.entities.TrainCar;
import com.percipient24.cgc.entities.Tree;
import com.percipient24.cgc.entities.boss.Tank;
import com.percipient24.cgc.entities.terrain.Bridge;
import com.percipient24.cgc.entities.terrain.Mud;
import com.percipient24.cgc.entities.terrain.Water;
import com.percipient24.cgc.screens.Options;
import com.percipient24.enums.AnimationState;
import com.percipient24.enums.DeathType;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a player
 * 
 * @version 0.2 14/2/4
 * @author Christopher Rider
 * @author Clayton Andrews
 * @author William Ziegler
 */
public class Player extends RotatableEntity 
{
	// Movement values
	private static final float HANG = 0.7f;
	private static final float MOVE_DAMP = 5.0f;
	private static final float STAND_DAMP = 500.0f;
	protected static final float DEAD_DAMP = 2.5f;
	protected static final float AIR_DAMP = 0.2f;
	
	// Player values
	protected boolean alive;
	protected boolean inBossFight;
	protected boolean shouldMakeCorpse = true;
	protected Corpse corpse;
	protected short playerID; // Zero based
	private final float calloutTime = 1.0f;
	protected CGCTimer calloutTimer;
	protected Timer.Task blankTask;
	
	// Movement variables
	private float speed;
	private boolean moving;
	private boolean start = false;
	private boolean stop = false;
	private float speedMod = 1.0f;
	protected float decaySpeedMod = 1.0f;
	private final float DECAY_RECHARGE_RATE = 0.3f;
	private final float MIN_DECAY_SLOW = 0.2f;
	private Vector2 terrainForce;
	protected boolean air = false;
	protected boolean canJump = true;
	protected Vector2 previousPos;
	protected Vector2 deathKnockbackPosition;
	
	protected float airTime = 0.0f;
	protected float lastAirTime = 0.0f;
	
	// Move impulse values
	public static final Array<Vector2> impulses = new Array<Vector2>(9);
	protected int direction = 0;
	protected int currentFacing;
	
	// Terrain variables
	protected Array<Mud> mudContacts;
	protected Array<Water> waterContacts;
	protected Array<Bridge> bridgeContacts;
	
	// Chain variables
	protected Player left = null;
	protected Player right = null;
	protected Joint leftJoint = null;
	protected Joint rightJoint = null;
	protected ChainLink rightChain = null;
	protected ChainLink leftChain = null;
	protected boolean chainedRight = false;
	protected boolean chainedLeft = false;
	
	// Camera-wall variables
	protected boolean wallContact = false;
	protected boolean wallAbove = false;

	// Timer variables
	protected CGCTimer offScreenTimer;
	protected Timer.Task offScreenTask;
	protected float offScreenTime = 5.0f;

	// Animation variables
	protected AnimationState lowState = AnimationState.STAND;
	protected AnimationState midState = AnimationState.STAND;
	protected AnimationState highState = AnimationState.STAND;
	
	// Punching variables
	protected float noPunchTime = 1.0f;
	protected CGCTimer noPunchTimer;
	
	protected ChainGame chainGame;
	protected ControllerScheme scheme;
	private int resetInt = 0;
	private boolean onScreen = true;
	
	private CGCTimer oldPositionTimer;
	private Timer.Task oldPositionTask;
	private Vector2 oldPosition;
	private Vector2 spawnPosition;
	protected int firstDirection = -1;

	private int coins = 10;
	
	/*
	 * Creates a new Player object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param pID					The ID of the player being created (player #)
	 */
	public Player(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, short pID)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		alive = true;
		inBossFight = false;
		playerID = pID;
		speed = 20.0f;//25.0f;
		
		if(impulses.size == 0)
		{
			float leg = speed / (float)Math.sqrt(2);
			
			//Neutral
			impulses.add(new Vector2(0, 0));
			//Up
			impulses.add(new Vector2(0, speed));
			//Up Right
			impulses.add(new Vector2(leg, leg));
			//Right
			impulses.add(new Vector2(speed, 0));
			//Down Right
			impulses.add(new Vector2(leg, -leg));
			//Down
			impulses.add(new Vector2(0, -speed));
			//Down Left
			impulses.add(new Vector2(-leg, -leg));
			//Left
			impulses.add(new Vector2(-speed, 0));
			//Up Left
			impulses.add(new Vector2(-leg, leg));
			
			for(int i = 0; i < impulses.size; i++)
			{
				impulses.get(i).scl(0.003f);
			}
		}
		
		currentFacing = 1;
		terrainForce = new Vector2(0,0);
		mudContacts = new Array<Mud>();
		waterContacts = new Array<Water>();
		bridgeContacts = new Array<Bridge>();
		
		parallaxDistMod = 8.0f;
		previousPos = body.getPosition().cpy();
		
		oldPosition = body.getPosition().cpy();
		offScreenTask = new Timer.Task() 
		{	
			public void run() 
			{	
				die(DeathType.CAMERA);
			}
		};
		
		offScreenTimer = new CGCTimer(offScreenTask, offScreenTime, false, "offScreenTimer");
		
		blankTask = new Timer.Task() 
		{
			public void run()
			{
				
			}
		};
		
		oldPositionTask = new Timer.Task() {
			
			public void run() {
				float dist = getPosition().cpy().sub(oldPosition).len2();
				
				if(dist >= 1)
				{
					spawnPosition = oldPosition.cpy();
					
					if(spawnPosition.x < 0)
					{
						spawnPosition.x = 0;
					}
					else if(spawnPosition.x >= 18)
					{
						spawnPosition.x = 17;
					}
					
					if(spawnPosition.y < 0)
					{
						spawnPosition.y = 0;
					}
					else if(spawnPosition.y >= CGCWorld.getLH().getLayer(LayerHandler.ground).getNumChunks() * 11)
					{
						spawnPosition.y = CGCWorld.getLH().getLayer(LayerHandler.ground).getNumChunks() * 11 - 1;
					}
					oldPosition = getPosition().cpy();
				}
				
			}
		};
		
		oldPositionTimer = new CGCTimer(oldPositionTask, 0.25f, true, "oldPositionTimer");
		
		TimerManager.addTimer(oldPositionTimer);
		calloutTimer = new CGCTimer(blankTask, calloutTime, false, "calloutTimer");
		noPunchTimer = new CGCTimer(blankTask, noPunchTime, false, "noPunchTimer");
		
		oldPosition = getPosition().cpy();
	}
	
	/*
	 * Creates a new Player object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param startAlpha			The starting alpha for this entity
	 * @param pID					The ID of the player being created (player #)
	 */
	public Player(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, float startAlpha, short pID)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, pID);
		alpha = startAlpha;
	}
	
	/*
	 * Timestep-based update method for animations
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The parallax layer to animate
	 */
	public void step(float deltaTime, int layer)
	{	
		rotation = (getCurrentFacing() - 1) * -45.0f;
		
		if (layer <= LayerHandler.LOW)
		{
			// Update this Entity's low animation state
			switch (lowState)
			{
				case STAND:
					if (lowStateTime > 8 * AnimationManager.STAND_ANIM_FRAME_TIME)
					{
						lowStateTime = 0;
					}
					lowStateTime += deltaTime;
					break;
				case RUN:
					if (lowStateTime > 8 * AnimationManager.RUN_ANIM_FRAME_TIME)
					{
						lowStateTime = 0;
					}
					lowStateTime += deltaTime; 
					break;
				case JUMP:
					if (lowStateTime > 8 * AnimationManager.JUMP_ANIM_FRAME_TIME)
					{
						lowStateTime = 0;
					}
					lowStateTime += deltaTime;
					break;
				case TIED:
					if (lowStateTime > 8 * AnimationManager.TIED_ANIM_FRAME_TIME)
					{
						lowStateTime = 0;
					}
					lowStateTime += deltaTime;
					break;
				case PUNCH:
					if (lowStateTime > 6 * AnimationManager.PUNCH_ANIM_FRAME_TIME)
					{
						changeLowAnimationState(AnimationState.STAND);
					}
					lowStateTime += deltaTime;
				default:
					break;
			}
		}
		else if (layer <= LayerHandler.MID)
		{
			// Update this Entity's mid animation state
			switch (midState)
			{
				case STAND:
					if (midStateTime > 8 * AnimationManager.STAND_ANIM_FRAME_TIME)
					{
						midStateTime = 0;
					}
					midStateTime += deltaTime;
				break;
				case RUN:
					if (midStateTime > 8 * AnimationManager.RUN_ANIM_FRAME_TIME)
					{
						midStateTime = 0;
					}
					midStateTime += deltaTime;
					break;
				case PUNCH:
					if (midStateTime > 6 * AnimationManager.PUNCH_ANIM_FRAME_TIME)
					{
						changeMidAnimationState(AnimationState.STAND);
					}
					midStateTime += deltaTime;
					break;
				case HIT:
					if (midStateTime > AnimationManager.HIT_ANIM_FRAME_TIME)
					{
					 	changeMidAnimationState(AnimationState.STAND);
					}
					midStateTime += deltaTime;
					break;
				case TIED:
					if (midStateTime > 8 * AnimationManager.TIED_ANIM_FRAME_TIME)
					{
						midStateTime = 0;
					}
					midStateTime += deltaTime;
					break;
				default:
					break;
			}
		}
		else
		{
			// Update this Entity's high animation state
			switch (highState)
			{
				case STAND:
					if (highStateTime > 8 * AnimationManager.STAND_ANIM_FRAME_TIME)
					{
						highStateTime = 0;
					}
					highStateTime += deltaTime;
					break;
				case RUN:
					if (highStateTime > 8 * AnimationManager.RUN_ANIM_FRAME_TIME)
					{
						highStateTime = 0;
					}
					highStateTime += deltaTime;
					break;
				case PUNCH:
					if (highStateTime > 6 * AnimationManager.PUNCH_ANIM_FRAME_TIME)
					{
						changeHighAnimationState(AnimationState.STAND);
					}
					highStateTime += deltaTime;
					break;
				case TIED: 
					if (highStateTime > 8 * AnimationManager.TIED_ANIM_FRAME_TIME)
					{
						highStateTime = 0;
					}
					highStateTime += deltaTime;
					break;
				default:
					break;
			}
		}
	}
	
	/*
	 * Gets the map ID of the map this player is currently on
	 * 
	 * @return						The map ID of the current map
	 */
	public int getCurrentMapIndex()
	{
		if (chainGame == null)
		{
			return -1;
		}
		
		float box2DPos = body.getPosition().y;
		int chunkNum = (int) (box2DPos / 11);
		
		return chainGame.getMapFromChunk(chunkNum);
	}
	
	/*
	 * Helper method for changing low AnimationStates
	 *
	 * @param newState		The new low AnimationState to show
	 */
	protected void changeLowAnimationState(AnimationState newState)
	{
		boolean reset = false;
		if (lowState != newState)
		{
			reset = true;
		}
		
		switch (newState)
		{
			case STAND:
				switch(lowState)
				{
					case PUNCH:
						if (lowStateTime > 6 * AnimationManager.PUNCH_ANIM_FRAME_TIME)
						{
							lowState = newState;
							if (this instanceof Prisoner)
							{
								setLowAnim(AnimationManager.prisonerStandLowAnims[playerID]);
							}
							else
							{
								setLowAnim(AnimationManager.copStandLowAnims[playerID]);
							}
						}
						break;
					default:
						lowState = newState;
						if (this instanceof Prisoner)
						{
							setLowAnim(AnimationManager.prisonerStandLowAnims[playerID]);
						}
						else
						{
							setLowAnim(AnimationManager.copStandLowAnims[playerID]);
						}
						
						if (reset)
						{
							lowStateTime = 0;
						}
						break;
				}
				break;
			case RUN:
				lowState = newState;
				if (this instanceof Prisoner)
				{
					setLowAnim(AnimationManager.prisonerRunLowAnims[playerID]);
				}
				else
				{
					setLowAnim(AnimationManager.copRunLowAnims[playerID]);
				}
				
				if (reset)
				{
					lowStateTime = 0;
				}
				break;
			case JUMP:
				lowState = newState;
				if (this instanceof Prisoner)
				{
					setLowAnim(AnimationManager.prisonerJumpAnims[playerID]);
				}
				else
				{
					setLowAnim(AnimationManager.copJumpAnims[playerID]);
				}
				
				if (reset)
				{
					lowStateTime = 0;
				}
				break;
			case TIED:
				lowState = newState;
				if (this instanceof Prisoner)
				{
					setLowAnim(AnimationManager.prisonerTiedLowAnims[playerID]);
				}
				
				if (reset)
				{
					lowStateTime = 0;
				}
				break;
			case PUNCH:
				lowState = newState;
				if (this instanceof Prisoner)
				{
					setLowAnim(AnimationManager.prisonerPunchLowAnims[playerID]);
				}
				else
				{
					setLowAnim(AnimationManager.copStandLowAnims[playerID]);
				}
				
				if (reset)
				{
					lowStateTime = 0;
				}
			default:
				break;
		}
	}
	
	/*
	 * Helper method for changing mid AnimationStates
	 *
	 * @param newState		The new mid AnimationState to show
	 */
	protected void changeMidAnimationState(AnimationState newState)
	{
		if (newState == AnimationState.TIED && this instanceof Prisoner)
		{
			midState = newState;
			setMidAnim(AnimationManager.prisonerTiedMidAnims[playerID]);
			midStateTime = 0;
		}
		else if (newState == AnimationState.HIT)
		{
			midState = newState;
			if (this instanceof Prisoner)
			{
				setMidAnim(AnimationManager.prisonerHitAnims[playerID]);
			}
			else
			{
				setMidAnim(AnimationManager.copHitAnims[playerID]);
			}
			midStateTime = 0;
		}
		// Punching is important enough that only dying and 
		// being hit should stop it showing
		else if (newState == AnimationState.PUNCH && midState != AnimationState.HIT)
		{
			midState = newState;
			if (this instanceof Prisoner)
			{
				setMidAnim(AnimationManager.prisonerPunchMidAnims[playerID]);
			}
			else
			{
				setMidAnim(AnimationManager.copPunchAnims[playerID]);
			}
			midStateTime = 0;
		}
		else if (newState == AnimationState.RUN)
		{
			switch (midState)
			{
				case PUNCH:
					if (midStateTime > 6 * AnimationManager.PUNCH_ANIM_FRAME_TIME)
					{
						midState = newState;
						if (this instanceof Prisoner)
						{
							setMidAnim(AnimationManager.prisonerRunMidAnims[playerID]);
						}
						else
						{
							setMidAnim(AnimationManager.copRunMidAnims[playerID]);
						}
						midStateTime = lowStateTime;
					}
					break;
				case HIT:
					if (midStateTime > AnimationManager.HIT_ANIM_FRAME_TIME)
					{
						midState = newState;
						if (this instanceof Prisoner)
						{
							setMidAnim(AnimationManager.prisonerRunMidAnims[playerID]);
						}
						else
						{
							setMidAnim(AnimationManager.copRunMidAnims[playerID]);
						}
						midStateTime = lowStateTime;
					}
					break;
				default:
					midState = newState;
					if (this instanceof Prisoner)
					{
						setMidAnim(AnimationManager.prisonerRunMidAnims[playerID]);
					}
					else
					{
						setMidAnim(AnimationManager.copRunMidAnims[playerID]);
					}
					midStateTime = lowStateTime;
					break;
			}
		}
		else if (newState == AnimationState.STAND)
		{
			switch (midState)
			{
				case PUNCH:
					if (midStateTime > 6 * AnimationManager.PUNCH_ANIM_FRAME_TIME)
					{
						midState = newState;
						if (this instanceof Prisoner)
						{
							setMidAnim(AnimationManager.prisonerStandMidAnims[playerID]);
						}
						else
						{
							setMidAnim(AnimationManager.copStandMidAnims[playerID]);
						}
						midStateTime = lowStateTime;
					}
					break;
				case HIT:
					if (midStateTime > AnimationManager.HIT_ANIM_FRAME_TIME)
					{
						midState = newState;
						if (this instanceof Prisoner)
						{
							setMidAnim(AnimationManager.prisonerStandMidAnims[playerID]);
						}
						else
						{
							setMidAnim(AnimationManager.copStandMidAnims[playerID]);
						}
						midStateTime = lowStateTime;
					}
					break;
				default:
					midState = newState;
					if (this instanceof Prisoner)
					{
						setMidAnim(AnimationManager.prisonerStandMidAnims[playerID]);
					}
					else
					{
						setMidAnim(AnimationManager.copStandMidAnims[playerID]);
					}
					midStateTime = lowStateTime;
					break;
			}
		}
	}
	
	/*
	 * Helper method for changing high AnimationStates
	 *
	 * @param newState		The new high AnimationState to show
	 */
	protected void changeHighAnimationState(AnimationState newState)
	{
		if (newState == AnimationState.TIED)
		{
			highState = newState;
			if (this instanceof Prisoner)
			{
				setHighAnim(AnimationManager.prisonerTiedHighAnims[playerID]);
			}
			highStateTime = lowStateTime;
		}
		else if (newState == AnimationState.PUNCH)
		{
			highState = newState;
			if (this instanceof Prisoner)
			{	
				setHighAnim(AnimationManager.prisonerPunchHighAnims[playerID]);
			}
			else
			{
				setHighAnim(AnimationManager.copStandHighAnims[playerID]);
			}
			highStateTime = lowStateTime;
		}
		else if (newState == AnimationState.RUN)
		{
			switch (highState)
			{
				case PUNCH:
					if (highStateTime > 6 * AnimationManager.PUNCH_ANIM_FRAME_TIME)
					{
						highState = newState;
						if (this instanceof Prisoner)
						{
							setHighAnim(AnimationManager.prisonerRunHighAnims[playerID]);
						}
						else
						{
							setHighAnim(AnimationManager.copRunHighAnims[playerID]);
						}
						highStateTime = lowStateTime;
					}
					break;
				case HIT:
					if (highStateTime > AnimationManager.HIT_ANIM_FRAME_TIME)
					{
						highState = newState;
						if (this instanceof Prisoner)
						{
							setHighAnim(AnimationManager.prisonerRunHighAnims[playerID]);
						}
						else
						{
							setHighAnim(AnimationManager.copRunHighAnims[playerID]);
						}
						highStateTime = lowStateTime;
					}
					break;
				default:
					highState = newState;
					if (this instanceof Prisoner)
					{
						setHighAnim(AnimationManager.prisonerRunHighAnims[playerID]);
					}
					else
					{
						setHighAnim(AnimationManager.copRunHighAnims[playerID]);
					}
					highStateTime = lowStateTime;
					break;
			}
		}
		else if (newState == AnimationState.STAND)
		{
			switch(highState)
			{
				case PUNCH:
					if (highStateTime > 6 * AnimationManager.PUNCH_ANIM_FRAME_TIME)
					{
						highState = newState;
						if (this instanceof Prisoner)
						{
							setHighAnim(AnimationManager.prisonerStandHighAnims[playerID]);
						}
						else
						{
							setHighAnim(AnimationManager.copStandHighAnims[playerID]);
						}
						highStateTime = lowStateTime;
					}
					break;
				default:
					highState = newState;
					if (this instanceof Prisoner)
					{
						setHighAnim(AnimationManager.prisonerStandHighAnims[playerID]);
					}
					else
					{
						setHighAnim(AnimationManager.copStandHighAnims[playerID]);
					}
					highStateTime = lowStateTime;
					break;
			}
		}
	}

	/*
	 * Sets the new ControllerScheme for this player
	 * 
	 * @param newScheme				The new control scheme
	 */
	public void setScheme(ControllerScheme newScheme)
	{
		scheme = newScheme;
	}
	
	/*
	 * Gets the ControllerScheme for this player
	 * 
	 * @return						The control scheme
	 */
	public ControllerScheme getScheme()
	{
		return scheme;
	}
	
	/*
	 * Gets whether or not this Player is alive
	 * 
	 * @return						True if the Player is alive, false if dead
	 */
	public boolean isAlive()
	{
		return alive;
	}
	
	/*
	 * Gets whether or not this Player is in a BossFight
	 * 
	 * @return						Whether or not this Player is in a BossFight
	 */
	public boolean isInBossFight()
	{
		return inBossFight;
	}
	
	/*
	 * Sets whether or not this Player is in a BossFight
	 * 
	 * @param inBossfight			Whether or not this Player is in a BossFight
	 */
	public void setInBossFight(boolean inBossFight)
	{
		this.inBossFight = inBossFight;
	}
	
	/*
	 * Gets whether or not this Player is chained to the right - Only used in new world creation
	 * 
	 * @return						True if the Player is chained, false if free
	 */
	public boolean isChainedRight()
	{
		return chainedRight;
	}
	
	/*
	 * Sets the chained flag to true - Only used in new world creation
	 */
	public void setChainedRight()
	{
		chainedRight = true;
	}
	
	/*
	 * Gets whether or not this player is chained to the left - Only used in new world creation
	 * 
	 * @return						True if the Player is chained, false if free
	 */
	public boolean isChainedLeft()
	{
		return chainedLeft;
	}
	
	/*
	 * Sets the chained flag to true - Only used in new world creation
	 */
	public void setChainedLeft()
	{
		chainedLeft = true;
	}
	
	/*
	 * Sets whether or not this Player is in contact with a PlayerWall
	 * 
	 * @param b                     Whether or not this Player is in contact with a PlayerWall
	 */
	public void setWallContact(boolean b)
	{
		//Gdx.app.log("Wall Contacted?", ""+b);
		wallContact = b;
	}
	
	/*
	 * Sets whether or not this Player is below a PlayerWall
	 * 
	 * @param b                     Whether or not this Player is below a PlayerWall
	 */
	public void setWallAbove(boolean b)
	{
		wallAbove = b;
	}
	
	/*
	 * Kill this player
	 * 
	 * @param death					What killed this Player
	 */
	public void die(DeathType death) 
	{
		if (alive)
		{
			//SoundManager.playRandomScream();
			CGCWorld.addToDestroyList(this);
			
			if (death == DeathType.CAMERA)
			{
				shouldMakeCorpse = false;
			}
			
			if (Options.storedTrackingOption)
			{
				//Prisoners dying for any reason in a BossFight register as Boss kills
				if (CGCWorld.isBossFight() && this instanceof Prisoner)
				{
					ChaseApp.stats.getGame().bossKills++;
				}
				else
				{
					switch(death)
					{
						case TRAIN_PUNCH:	
						case TRAIN:
							if (this instanceof Prisoner)
							{
								ChaseApp.stats.getStatByIndex(getCurrentMapIndex()).trainKillsPrisoner++;
							}
							else if (this instanceof RookieCop)
							{
								ChaseApp.stats.getStatByIndex(getCurrentMapIndex()).trainKillsCop++;
							}
							break;
						default:
							break;
					}
				}
			}
		
			alive = false;
		}
	}
	
	/*
	 * Gets this Player's ID number
	 * 
	 * @return						This Player's ID number
	 */
	public short getPID()
	{
		return playerID;
	}
	
	/*
	 * Gets this Player's Corpse
	 * 
	 * @return						This Player's Corpse
	 */
	public Corpse getCorpse()
	{
		return corpse;
	}
	
	/*
	 * Gets whether or not to show this Player's callout
	 * 
	 * @return						Whether or not to show this Player's callout
	 */
	public boolean getShowCallout()
	{
		return calloutTimer.isRunning();
	}
	
	/*
	 * Gets the Player to this Player's left
	 * 
	 * @return						The Player to the left
	 */
	public Player getLeftPlayer()
	{
		return left;
	}
	
	/*
	 * Sets the Player to this Player's left
	 * 
	 * @param newLeft				The new Player to the left
	 */
	public void setLeftPlayer(Player newLeft)
	{
		left = newLeft;
	}
	
	/*
	 * Gets the Player to this Player's right
	 * 
	 * @return						The Player to the right
	 */
	public Player getRightPlayer()
	{
		return right;
	}
	
	/*
	 * Sets the Player to this Player's right
	 * 
	 * @param newRight				The new Player to the right
	 */
	public void setRightPlayer(Player newRight)
	{
		right = newRight;
	}
	
	/*
	 * Gets the chain to this Player's left
	 * 
	 * @return						The chain to the left
	 */
	public Joint getLeftJoint()
	{
		return leftJoint;
	}
	
	/*
	 * Sets the chain to this Player's left
	 * 
	 * @param newLeft				The new chain to the left
	 */
	public void setLeftJoint(Joint newLeft)
	{
		leftJoint = newLeft;
	}
	
	/*
	 * Gets the chain to this Player's right
	 * 
	 * @return						The chain to the right
	 */
	public Joint getRightJoint()
	{
		return rightJoint;
	}
	
	/*
	 * Sets the chain to this Player's right
	 * 
	 * @param newRight				The new chain to the right
	 */
	public void setRightJoint(Joint newRight)
	{
		rightJoint = newRight;
	}
	
	/*
	 * Gets the ChainLink to the Player's left
	 * 
	 * @return						The ChainLink to the left
	 */
	public ChainLink getLeftChain()
	{
		return leftChain;
	}
	
	/*
	 * Set the ChainLink to the Player's left
	 * 
	 * @param newChain				The ChainLink to set as the Player's left
	 */
	public void setLeftChain(ChainLink newChain)
	{
		leftChain = newChain;
	}
	
	/*
	 * Gets the ChainLink to the Player's right
	 * 
	 * @return						The ChainLink to the right
	 */
	public ChainLink getRightChain()
	{
		return rightChain;
	}
	
	/*
	 * Set the ChainLink to the player's right
	 * 
	 * @param newChain				The ChainLink to set as the Player's right
	 */
	public void setRightChain(ChainLink newChain)
	{
		rightChain = newChain;
	}
	
	/*
	 * Sets the speed modifier for this Player's movespeed
	 * 
	 * @param mult					The amount to multiply the speed by
	 */
	public void setSpeedMod(float mult)
	{
		speedMod = mult;
	}
	
	/*
	 * Gets whether or not the Player is jumping
	 * 
	 * @return						Whether or not the Player is jumping
	 */
	public Boolean isJumping()
	{
		return air;
	}
	
	/*
	 * Gets whether or not the Player is moving
	 * 
	 * @return						Whether or not the Player is moving
	 */
	public Boolean isMoving()
	{
		return moving;
	}
	
	/*
	 * Gets the number of Mud tiles this Player is contacting
	 * 
	 * @return						The number of touched Mud tiles
	 */
	public Array<Mud> getMudContacts()
	{
		return mudContacts;
	}
	
	/*
	 * Gets the number of Water tiles this Player is contacting
	 * 
	 * @return						The number of touched Water tiles
	 */
	public Array<Water> getWaterContacts()
	{
		return waterContacts;
	}
	
	/*
	 * Gets the number of Bridge tiles this Player is contacting
	 * 
	 * @return						The number of touched Bridge tiles
	 */
	public Array<Bridge> getBridgeContacts()
	{
		return bridgeContacts;
	}
	
	/*
	 * Adds to the number of Mud contacts
	 * 
	 * @param mud					The Mud tile to add to Mud contacts
	 */
	public void addMudContacts(Mud mud)
	{
		mudContacts.add(mud);
	}
	
	/*
	 * Adds to the number of Water contacts
	 * 
	 * @param water					The Water tile to add to Water contacts
	 */
	public void addWaterContacts(Water water)
	{
		waterContacts.add(water);
	}
	
	/*
	 * Adds to the number of Bridge contacts
	 * 
	 * @param bridge					The Bridge tile to add to Bridge contacts
	 */
	public void addBridgeContacts(Bridge bridge)
	{
		bridgeContacts.add(bridge);
	}
	
	/*
	 * Removes the specified Mud contact
	 * 
	 * @param mud					The Mud tile to remove
	 */
	public void removeMudContact(Mud mud)
	{
		mudContacts.removeValue(mud, true);
	}
	
	/*
	 * Removes the specified Water contact
	 * 
	 * @param water					The Water tile to remove
	 */
	public void removeWaterContact(Water water)
	{
		waterContacts.removeValue(water, true);
	}
	
	/*
	 * Removes the specified Bridge contact
	 * 
	 * @param bridge				The Bridge tile to remove
	 */
	public void removeBridgeContact(Bridge bridge)
	{
		bridgeContacts.removeValue(bridge, true);
	}
	
	/*
	 * Determines if the Player is in terrain or not
	 * 
	 * @return						Whether or not the Player is affected by terrain
	 */
	public boolean isInTerrain()
	{
		if (bridgeContacts.size > 0)
		{
			return false;
		}
		
		if (mudContacts.size > 0 || waterContacts.size > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * Sets the amount of force being applied to this Player
	 * 
	 * @param amount				The amount of force being applied
	 * @param dir					The direction to apply the force (1 is up, increases clockwise)
	 */
	public void addTerrainForce(float amount, int dir)
	{
		float amountRt = amount / (float)Math.sqrt(2);
		
		switch(dir)
		{
			case 0: terrainForce.add(0, 0);
				break;
			case 1: terrainForce.add(amount, 0);
				break;
			case 2: terrainForce.add(amountRt, -amountRt);
				break;
			case 3: terrainForce.add(0, -amount);
				break;
			case 4: terrainForce.add(-amountRt, -amountRt);
				break;
			case 5: terrainForce.add(-amount, 0);
				break;
			case 6: terrainForce.add(-amountRt, amountRt);
				break;
			case 7: terrainForce.add(0, amount);
				break;
			case 8: terrainForce.add(amountRt, amountRt);
				break;
			default: terrainForce.add(0,0);
				break;
		}
		
		terrainForce.nor().scl(amount);
	}
	
	/*
	 * Removes the terrain forces affecting this Player
	 */
	public void resetTerrainForce()
	{
		terrainForce.set(0, 0);
	}
	
	/*
	 * Gets this Player's speed
	 * 
	 * @return						The Player's speed
	 */
	public float getSpeed()
	{
		return speed;
	}
	
	/*
	 * Gets this Player's movement direction
	 * 
	 * @return						The Player's movement direction
	 */
	public int getDirection()
	{
		return direction;
	}
	
	/*
	 * Gets the direction this Player is facing
	 * 
	 * @return						The direction the Player is facing
	 */
	public int getCurrentFacing()
	{
		return currentFacing;
	}

	/*
	 * Adds a Player's key ID to the array of dead key IDs
	 * 
	 * @param playerNum				The Player who died
	 */
	protected void fixKeyIDs(int playerNum)
	{
		if (alive && (this instanceof Prisoner))
		{
			chainGame.addDeadKeyID(((Prisoner)this).getKeyID());
		}
	}
	
	/*
	 * Handles all control input for a side of the controller
	 * 
	 * @param buttonUp				Whether or not the up face button/D-up is pressed
	 * @param buttonDown			Whether or not the down face button/D-down is pressed
	 * @param buttonLeft			Whether or not the left face button/D-left is pressed
	 * @param buttonRight			Whether or not the right face button/D-right is pressed
	 * @param stickPress			Whether or not the stick is pressed down (R3/L3)
	 * @param up					Whether or not the stick is in the up direction
	 * @param down					Whether or not the stick is in the down direction
	 * @param left 					Whether or not the stick is in the left direction
	 * @param right					Whether or not the stick is in the right direction
	 * @param bumper				Whether or not the bumper command is pressed
	 * @param buttonsMashed			Whether or not any of the face buttons/D-pad were just pressed
	 */
	public void controlUpdate(boolean buttonUp, boolean buttonDown, boolean buttonLeft, 
							boolean buttonRight, boolean stickPress, boolean up, boolean down, 
							boolean left, boolean right, boolean bumper, boolean buttonsMashed)
	{
		faceButtonsInput(buttonUp, buttonDown, buttonLeft, buttonRight, stickPress, buttonsMashed);
		move(up, down, left, right, bumper);
	}
	
	/*
	 * Handles face button/D-pad input for a side of the controller
	 * 
	 * @param buttonUp				Whether or not the up face button/D-up is pressed
	 * @param buttonDown			Whether or not the down face button/D-down is pressed
	 * @param buttonLeft			Whether or not the left face button/D-left is pressed
	 * @param buttonRight			Whether or not the right face button/D-right is pressed
	 * @param stickPress			Whether or not R3/L3 is pressed
	 */
	public void faceButtonsInput(boolean buttonUp, boolean buttonDown, boolean buttonLeft, 
								boolean buttonRight, boolean stickPress, boolean mashed)
	{
		//Override this if necessary
		
		if (stickPress)
		{
				startCalloutTimer();
		}
	}
	
	/*
	 * Move the Player across the screen
	 * 
	 * @param up					Whether or not the up command is being used
	 * @param down					Whether or not the down command is being used
	 * @param left 					Whether or not the left command is being used
	 * @param right					Whether or not the right command is being used
	 * @param bumper				Whether or not the bumper command is being used
	 */
	public void move(boolean up, boolean down, boolean left, boolean right, 
			boolean bumper)
	{
		if(alive)
		{
			start = moving;
			stop = moving;
			moving = (up || down || left || right) && !((left && right) || (up && down));
			
			// I just started moving:
			start = (start == false && moving);
			
			// I just stopped moving:
			stop = (stop == true && moving == false);
			
			lastAirTime = airTime;
			
			if(!air && canJump && moving && bumper && (mudContacts.size <= 0 || bridgeContacts.size > 0))
			{
				// I just jumped.
				air = true;
				canJump = false;
				airTime = HANG;
				body.setLinearDamping(AIR_DAMP);
			}
			
			/*if(!moving && bumper)
			{
				// TODO I want to drop/swap/pickup item.
			}*/
			
			if(air)
			{
				changeLowAnimationState(AnimationState.JUMP);
				changeMidAnimationState(AnimationState.RUN);
				airTime -= CGCWorld.getDelta();

				float curHang = HANG - airTime;
				if (curHang <= 0)
				{
					curHang = 0;
				}
				
				float jumpScale = -1.633f * (curHang * curHang) + 1.143f * curHang + 1;
				//float jumpScale = -4.08f * (curHang * curHang) + 2.86f * curHang + 1;
				// http://jwilson.coe.uga.edu/EMT668/EMAT6680.F99/Jones/Instructional%20Unit/writingquads.html
				// Enjoy making parabolic equations
				if (curHang <= 0)
				{
					jumpScale = 1.0f;
				}
				
				setScale(jumpScale, jumpScale);
				
				if(airTime <= 0)
				{
					airTime = 0;
					air = false;
					canJump = true;
					
					if(moving)
					{
						// I landed while running.
						changeLowAnimationState(AnimationState.RUN);
						changeMidAnimationState(AnimationState.RUN);
						changeHighAnimationState(AnimationState.RUN);
						body.setLinearDamping(MOVE_DAMP);
					}
					else
					{
						// I landed while standing.
						tryToStand();
					}
				}
			}
			else
			{
				if(start)
				{
					// I just started moving.
					changeLowAnimationState(AnimationState.RUN);
					changeMidAnimationState(AnimationState.RUN);
					changeHighAnimationState(AnimationState.RUN);
					body.setLinearDamping(MOVE_DAMP);
				}
				else if(stop)
				{
					// I just stopped moving.
					tryToStand();
				}
			}
			
			if(!air)
			{
				applyTerrainForces();
				
				direction = 0;
				if(moving)
				{
					changeLowAnimationState(AnimationState.RUN);
					changeMidAnimationState(AnimationState.RUN);
					changeHighAnimationState(AnimationState.RUN);
					if(up && !(left || right || down))
					{
						// up
						direction = 1;
					}
					else if((up && right) && !(down || left))
					{
						// up right
						direction = 2;
					}
					else if(right && !(left || up || down))
					{
						// right
						direction = 3;
					}
					else if((down && right) && !(up || left))
					{
						// down right
						direction = 4;
					}
					else if(down && !(left || up || right))
					{
						// down
						direction = 5;
					}
					else if((down && left) && !(up || right))
					{
						// down left
						direction = 6;
					}
					else if(left && !(up || right || down))
					{
						// left
						direction = 7;
					}
					else if((up && left) && !(down || right))
					{
						// up left
						direction = 8;
					}
					
					Vector2 tempVec = new Vector2(impulses.get(direction).x * speedMod * decaySpeedMod, 
							impulses.get(direction).y * speedMod * decaySpeedMod);
					//tempVec.add(force);
					body.applyLinearImpulse(tempVec, body.getWorldCenter(), true);
					
					Vector2 vel = body.getLinearVelocity();
					float mySpeed = vel.len();
					if(mySpeed > speed)
					{
						vel.nor();
						body.setLinearVelocity(vel.scl(speed));
					}
				}
				else
				{
					tryToStand();
				}
			}
		}
	}
	
	/*
	 * Updates this Player
	 * 
	 * @param dt					Time since last frame
	 */
	public void updatePlayer(float dt)
	{	
		if (this instanceof RookieCop)
		{
			decaySpeedMod += RookieCop.dazedSlowRecharge * dt;
		}
		else
		{
			decaySpeedMod += DECAY_RECHARGE_RATE * dt;
		}
		
		if (resetInt == 1)
		{
			if (body.getWorldCenter().y + 1 < CGCWorld.getLH().getLayer(LayerHandler.ground).getNumChunks() * 11)
			{
				Array<GameEntity> blockers =CGCWorld.getLH().getLayer(LayerHandler.ground).getEntitiesInGrid((int)Math.floor(body.getWorldCenter().x),(int)Math.ceil(body.getWorldCenter().y));
				if (blockers.size == 0)
				{
					body.setTransform(new Vector2(body.getPosition().x, CGCWorld.getCamera().getLowerWall().getBody().getPosition().y + 0.25f), 0);
					resetInt = 0;
					onScreen = true;
				}
			}
		}
		else if (resetInt == -1)
		{
			
			Vector2 wc = body.getWorldCenter().cpy();
			if (wc.cpy().y - 1 >= 0)
			{
				Array<GameEntity> blockers = CGCWorld.getLH().getLayer(LayerHandler.ground).getEntitiesInGrid((int)Math.floor(wc.cpy().x), (int)Math.floor(wc.cpy().y));
				if (blockers.size == 0)
				{
					body.setTransform(new Vector2(body.getPosition().x, CGCWorld.getCamera().getUpperWall().getBody().getPosition().y - 0.5f), 0);
					resetInt = 0;
					onScreen = true;
				}
			}
		}
		
		if (decaySpeedMod > 1.0f)
		{
			decaySpeedMod = 1.0f;
		}
		
		if (isInTerrain())
		{
			if (mudContacts.size > 0 && waterContacts.size <= 0)
			{
				setSpeedMod(Mud.speedMod);
				resetTerrainForce();
			}
			else if (waterContacts.size > 0 && mudContacts.size <= 0)
			{
				setSpeedMod(Water.speedMod);
				boolean noCurrent = true;
				for (int i = 0; i < waterContacts.size; i++)
				{
					addTerrainForce(Water.forceAmount, waterContacts.get(i).getDirection());
					if (waterContacts.get(i).getDirection() != 0)
					{
						noCurrent = false;
					}
				}
				
				if (noCurrent)
				{
					resetTerrainForce();
				}
			}
			else if (waterContacts.size > 0 && mudContacts.size > 0)
			{
				setSpeedMod((Water.speedMod + Mud.speedMod) / 2);
				boolean noCurrent = true;
				for (int i = 0; i < waterContacts.size; i++)
				{
					addTerrainForce(Water.forceAmount, waterContacts.get(i).getDirection());
					if (waterContacts.get(i).getDirection() != 0)
					{
						noCurrent = false;
					}
				}
				
				if (noCurrent)
				{
					resetTerrainForce();
				}
			}
		}
		else
		{
			setSpeedMod(1.0f);
			resetTerrainForce();
		}

		int offScreenDir = CGCWorld.getCamera().isOnScreenDir(this);
		
		if(offScreenDir == 0)
		{
			if(offScreenTimer.isRunning() && alive)
			{
				TimerManager.removeTimer(offScreenTimer);
			}
		}
		else if(alive && !(this instanceof SpotlightCop) && !(this instanceof GunCop)
				&& !CGCWorld.won())
		{
			if (!(CGCWorld.getCamera().isLocked() && offScreenDir == -1))
			{
				startOffScreenTimer();
			}
		}
		
		if (toDestroy() && shouldMakeCorpse)
		{
			Body corpseBody = CGCWorld.getBF().createPlayerBody(this.getBody().getWorldCenter().x, this.getBody().getWorldCenter().y,
																0.6f, BodyType.DynamicBody, BodyFactory.CAT_DECEASED,
																BodyFactory.MASK_DECEASED);
			corpseBody.setFixedRotation(true);
			if (this instanceof Prisoner)
			{
				corpse = new Corpse(this, AnimationManager.prisonerDieLowAnims[playerID], 
								AnimationManager.prisonerDieMidAnims[playerID], 
								AnimationManager.prisonerDieHighAnims[playerID], 
								EntityType.PLAYER, corpseBody, 0.5f, 
								playerID, deathKnockbackPosition);
			}
			else
			{
				corpse = new Corpse(this, AnimationManager.copDieLowAnims[playerID],
								AnimationManager.copDieMidAnims[playerID],
								AnimationManager.copDieHighAnims[playerID],
								EntityType.PLAYER, corpseBody, 0.5f,
								playerID, deathKnockbackPosition);
			}
			corpseBody.setUserData(corpse);
			corpse.addToWorldLayers(CGCWorld.getLH());
			shouldMakeCorpse = false;
		}
	}
	
	/*
	 * Remove the chain from this Player and the Player on the right (if any)
	 */
	public void breakChain() 
	{
		if(rightJoint != null && right != null)
		{
			if(this.alive && right.isAlive() && body != null)
			{
				body.getWorld().destroyJoint(rightJoint);
			}
			rightJoint = null;
			if(right.leftJoint != null)
			{
				right.leftJoint = null;
			}
			right.left = null;
			right = null;
		}
	}
	
	/*
	 * Stand the Player back up
	 */
	public void tryToStand()
	{
		changeLowAnimationState(AnimationState.STAND);
		changeMidAnimationState(AnimationState.STAND);
		changeHighAnimationState(AnimationState.STAND);
		float damp = body.getLinearDamping();
		damp += (STAND_DAMP - damp) * 0.2f * CGCWorld.getDelta();

		body.setLinearDamping(damp);
	}
	
	/*
	 * Resets all of the movement variables for this Player - Call before re-making the body
	 */
	public void resetPlayer()
	{
		currentFacing = 1;
		direction = 0;
		move(false, false, false, false, false);
	}
	
	/*
	 * Applies a decaying slow to this Player
	 * 
	 * @param amount				The percent slow amount
	 */
	public void applyDecaySlow(float amount)
	{
		decaySpeedMod -= amount;
		
		if (decaySpeedMod < MIN_DECAY_SLOW)
		{
			decaySpeedMod = MIN_DECAY_SLOW;
		}
		else if (decaySpeedMod > 1.0f)
		{
			decaySpeedMod = 1.0f;
		}
	}
	
	/*
	 * Applies the total force from all terrain objects
	 */
	public void applyTerrainForces()
	{
		body.setLinearDamping(MOVE_DAMP);
		body.applyForce(terrainForce, body.getWorldCenter(), true);
		//body.applyLinearImpulse(terrainForce, body.getWorldCenter());
	}
	
	/*
	 * Get the direction going directly away from the target entity
	 * 
	 * @param target					The GameEntity to examine
	 * @return							The direction going directly away from the target
	 */
	protected int checkDirectionFrom(GameEntity target)
	{
		if (target != this)
		{
			int returnDirection;
			Vector2 positionDifference = body.getPosition().cpy().sub(target.getBody().getPosition().cpy());
			
			returnDirection = 3 - (int)(positionDifference.angle() / 45);
			
			if (returnDirection < 1)
			{
				returnDirection = 8 + returnDirection;
			}
			
			return returnDirection;
		}
		else
		{
			return 0;
		}
	}
	
	/*
	 * Confirm the Player is facing the right direction to punch something
	 * 
	 * @param target					The GameEntity that may be punched
	 * @return							Whether or not the Player is facing the right way
	 */
	protected boolean checkPunchDirection(GameEntity target)
	{
		if (target != this)
		{
			if (currentFacing == 8 || currentFacing == 1 || currentFacing == 2)
			{
				if (target.getBody().getPosition().y < body.getPosition().y)
				{
					return false;
				}
			}
			if (currentFacing >= 2 && currentFacing <= 4)
			{
				if (target.getBody().getPosition().x < body.getPosition().x)
				{
					return false;
				}
			}
			if (currentFacing >= 4 && currentFacing <= 6)
			{
				if (target.getBody().getPosition().y > body.getPosition().y)
				{
					return false;
				}
			}
			if (currentFacing >= 6 && currentFacing <= 8)
			{
				if (target.getBody().getPosition().x > body.getPosition().x)
				{
					return false;
				}
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * Confirm the Player is facing the right direction to punch a train
	 * 
	 * @param target					The TrainCar that may be punched
	 * @return							Whether or not the Player is facing the right way
	 */
	protected boolean checkPunchTrain(TrainCar target)
	{
		if (currentFacing == 8 || currentFacing == 1 || currentFacing == 2)
		{
			if (Math.abs((target.getBody().getPosition().y + target.getImageHalfHeight(0)) - body.getPosition().y) < 0.5f)
			{
				if (Math.abs(target.getBody().getPosition().x - body.getPosition().x) < 2.5f)
				{
					return true;
				}
			}
		}
		else if (currentFacing >= 4 && currentFacing <= 6)
		{
			if (Math.abs((target.getBody().getPosition().y - target.getImageHalfHeight(0)) - body.getPosition().y) < 0.5f)
			{
				if (Math.abs(target.getBody().getPosition().x - body.getPosition().x) < 2.5f)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*
	 * Confirm the Player is facing the right direction to punch one of the side Walls
	 */
	protected boolean checkPunchWall()
	{
		if (body.getPosition().x < 1.0 && (direction >= 6 && direction <= 8))
		{
			return true;
		}
		else if (body.getPosition().x > 18.0 && (direction >= 2 && direction <= 4))
		{
			return true;
		}
		
		return false;
	}
	
	/*
	 * Manages the cooldown for punching after throwing a punch
	 */
	protected void startNoPunchTimer()
	{
		if(!noPunchTimer.isRunning())
		{
			TimerManager.addTimer(noPunchTimer);
		}
	}
	
	/*
	 * Applies a force to this Player
	 * 
	 * @param direction				The direction of the force
	 * @param strength				The strength of the force
	 */
	public void applyForceToSelf(int direction, int strength)
	{
		Vector2 appliedForce = impulses.get(direction).cpy();
		appliedForce.scl(strength);
		body.applyForce(appliedForce, body.getWorldCenter(), true);
		
		Vector2 vel = body.getLinearVelocity();
		float mySpeed = vel.len();
		if(mySpeed > speed)
		{
			vel.nor();
			body.setLinearVelocity(vel.scl(speed));
		}
	}
	
	/*
	 * Applies a force from a punch to this Player
	 * 
	 * @param direction				The direction of the force
	 * @param strength				The strength of the force
	 */
	public void applyPunchForce(int direction, int strength)
	{
		Vector2 appliedForce = impulses.get(direction).cpy();
		appliedForce.scl(strength);
		body.applyLinearImpulse(appliedForce, body.getWorldCenter(), true);
		
		Vector2 vel = body.getLinearVelocity();
		float mySpeed = vel.len();
		if(mySpeed > speed)
		{
			vel.nor();
			body.setLinearVelocity(vel.scl(speed));
		}
	}

	public void dropCoins(int max, int direction) {
		int start = getCoins();
		int dropped;
		if (start == 0) {
			return;
		} else if (max == 0) {
			setCoins(0);
			dropped = start;
			return;
		}
		dropped = MathUtils.random(1, Math.min(start, max));
		setCoins(start - dropped);
		// TODO : actually drop coins

		while (dropped > 0) {
			Body coinBody = CGCWorld.getBF().createPlayerBody(
				this.getBody().getWorldCenter().x,
				this.getBody().getWorldCenter().y,
				0.6f,
				BodyType.DynamicBody,
				BodyFactory.CAT_DECEASED,
				BodyFactory.MASK_DECEASED);

			coinBody.setFixedRotation(true);
			Coin coin = new Coin(
				this,
				AnimationManager.prisonerDieLowAnims[playerID], 
				AnimationManager.prisonerDieMidAnims[playerID], 
				AnimationManager.prisonerDieHighAnims[playerID], 
				EntityType.PLAYER,
				coinBody,
				0.5f, 
				playerID,
				null);

			coinBody.setUserData(coin);
			coin.addToWorldLayers(CGCWorld.getLH());
			coin.applyForceToSelf(direction, 800);
			shouldMakeCorpse = false;
			dropped--;
		}
	}
	
	/*
	 * Determines how this Player reacts to getting punched
	 * 
	 * @param direction				The direction of the punch
	 */
	public void punched(int direction)
	{
		changeMidAnimationState(AnimationState.HIT);
		SoundManager.playSound("punch person", false);
		// TODO : weight droppage by difficulty
		ChaseApp.alert("punched dir", direction);
		dropCoins(3, direction);
		//Override in subclass if necessary.
		body.setLinearDamping(MOVE_DAMP);
		applyPunchForce(direction, 500);
		if (Options.storedTrackingOption)
		{
			if (this instanceof Prisoner)
			{
				int id = getCurrentMapIndex();
				if (id >= 0)
				{
					ChaseApp.stats.getStatByIndex(id).punchesLandedPrisoner++;
				}
				else
				{
					ChaseApp.stats.getGame().bossFightPunchesLandedPrisoner++;
				}
				
			}
			else if (this instanceof RookieCop)
			{
				int id = getCurrentMapIndex();
				if (id >= 0)
				{
					ChaseApp.stats.getStatByIndex(id).punchesLandedCop++;
				}
				else
				{
					ChaseApp.stats.getGame().bossFightPunchesLandedCop++;
				}
			}
		}
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param g						The second entity colliding
	 */
	public void collide(GameEntity g)
	{
		g.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param t						The second entity colliding
	 */
	public void collide(TrainCar t)
	{
		CGCWorld.getCM().handlePlayerTrain(this);
	}

	/*
	 * Determines the second class type in a collision
	 * 
	 * @param t						The second entity colliding
	 */
	public void collide(Tree t)
	{	
		Vector2 tempVec = impulses.get(direction).cpy();
		body.applyLinearImpulse(tempVec.scl(0.1f), body.getWorldCenter(), true);
	}
	
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param gt					The second entity colliding
	 */
	public void collide(GuardTower gt)
	{
		return;
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param t						The second entity colliding
	 */
	public void collide(Tank t)
	{
		return;
		//die(DeathType.BOSS);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param p						The second entity colliding
	 */
	public void collide(PlayerWall p)
	{
		p.collide(this);
	}
	
	/*
	 * Starts the off screen timer
	 */
	public void startOffScreenTimer()
	{
		if (!offScreenTimer.isRunning())
		{
			TimerManager.addTimer(offScreenTimer);
		}
	}
	
	/*
	 * Gets the seconds left on the off-screen timer
	 * 
	 * @return						The seconds left on the off-screen timer
	 */
	public int getTimeLeft()
	{
		return (int)Math.ceil(offScreenTime * (1 - offScreenTimer.getPercent()));
	}
	
	/*
	 * Gets whether or not the off-screen timer is started
	 * 
	 * @return						Whether or not the off-screen timer is started
	 */
	public boolean getOffScreenTimerStarted()
	{
		return offScreenTimer.isRunning();
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.ground);
		lh.addEntityToLayer(this, LayerHandler.mid);
		lh.addEntityToLayer(this, LayerHandler.heads);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.ground);
		lh.removeEntityFromLayer(this, LayerHandler.mid);
		lh.removeEntityFromLayer(this, LayerHandler.heads);
	}
	
	/*
	 * Gets the position of this Player
	 * 
	 * @return						The position of the Player's body
	 */
	public Vector2 getPosition()
	{
		return body.getPosition();
	}
	
	/*
	 * Sets the ChainGame for this Player
	 * 
	 * @param cg					The ChainGame to add to this Player
	 */
	public void setChainGame(ChainGame cg)
	{
		chainGame = cg;
	}
	
	/*
	 * Starts this Player's calloutTimer
	 */
	protected void startCalloutTimer()
	{
		if(!calloutTimer.isRunning())
		{
			TimerManager.addTimer(calloutTimer);		
		}
	}
	
	/*
	 * Resets this Player's calloutTimer
	 */
	public void resetCalloutTimer()
	{
		if (calloutTimer.isRunning())
		{
			calloutTimer.reset();
		}
	}
	
	/*
	 * Sets the marker for shifting back onscreen to the correct value
	 * 
	 * @param i						1 if it's going up, -1 if it's going down, 0 if it's staying still
	 */
	public void setResetInt(int i)
	{
		resetInt = i;
		
		if(i == 0)
		{
			onScreen = true;
		}
		else
		{
			onScreen = false;
		}
	}
	
	/*
	 * Gets a spawn position for incoming RookieCops
	 * 
	 * @return					The position of this Player 1 second ago
	 */
	public Vector2 getSpawnPosition()
	{
		return spawnPosition;
	}
	
	/*
	 *  Gets whether or not this player is on-screen
	 *  
	 *  @return					Whether or not this player is on-screen
	 */
	public boolean isOnScreen()
	{
		return onScreen;
	}

	public ChainGame getChainGame() {
		return chainGame;
	}

	public int getCoins()
	{
		return coins;
	}
	
	public void setCoins(int i)
	{
		coins = i;
	}
} // End class
