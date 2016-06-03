/*
 * @(#)Prisoner.java		0.2 14/2/5
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities.players;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.BossFight;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.entities.Explosion;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.GuardTower;
import com.percipient24.cgc.entities.Sensor;
import com.percipient24.cgc.entities.Spotlight;
import com.percipient24.cgc.entities.TrainCar;
import com.percipient24.cgc.entities.Tree;
import com.percipient24.cgc.entities.boss.PallBearer;
import com.percipient24.cgc.entities.boss.Sheriff;
import com.percipient24.cgc.entities.boss.SteelHorse;
import com.percipient24.cgc.entities.boss.Tank;
import com.percipient24.cgc.entities.projectiles.Bullet;
import com.percipient24.cgc.entities.projectiles.RiderBullet;
import com.percipient24.cgc.entities.projectiles.TankShell;
import com.percipient24.cgc.maps.MapBuilder;
import com.percipient24.cgc.screens.Options;
import com.percipient24.enums.AnimationState;
import com.percipient24.enums.DeathType;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for players who are prisoners
 * 
 * @version 0.2 14/2/3
 * @author JD Kelly
 * @author William Ziegler
 */
public class Prisoner extends Player
{	
	private final int MAX_STAMINA_BASE = 400;
	private final int BREAK_ROPE_MODIFIER = 6;
	private final int STAMINA_LOSS_MODIFIER = 8;
	private final int MAX_STAMINA_RANGE = STAMINA_LOSS_MODIFIER * 4;
	
	private Array<RookieCop> grabbedByArray;
	private CGCWorld gameWorld;
	private boolean grabbed = false;
	private boolean[] grabMashed = new boolean[4];
	private int numberMashed = 0;
	private static float distanceRun = 0.0f;
	private boolean tiedUp = false;
	private float maxStamina;
	private float currentStamina;

	private Array<GuardTower> towerContacts; 
	private Array<Spotlight> lightContacts;
	private Array<Sensor> sensorContacts;
	private int keyID; // The number for sensors this prisoner can trigger
	
	public long startTime;
	public long endTime;
	private int lastMapOnID = -1;
	
	/*
	 * Creates a new Prisoner object
	 * 
	 * @param theWorld				Reference to the CGCWorld class (for accessing data available therein)
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param pID					The ID of the player being created (player #)
	 */
	public Prisoner(CGCWorld theWorld, Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody, short pID)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, 
				pEntityType, attachedBody, pID);
		
		gameWorld = theWorld;
		towerContacts = new Array<GuardTower>();
		lightContacts = new Array<Spotlight>();
		sensorContacts = new Array<Sensor>();
		grabbedByArray = new Array<RookieCop>();
		keyID = (int) (playerID+1);
		
		maxStamina = MAX_STAMINA_BASE + Math.round(((float)(Math.random() * 2) - 1.0f) * MAX_STAMINA_RANGE);
		currentStamina = maxStamina;
	}
	
	/*
	 * Gets this Prisoner's keyID for triggering Sensors
	 * 
	 * @return						This Prisoner's keyID for triggering Sensors
	 */
	public int getKeyID()
	{
		return keyID;
	}

	/*
	 * Checks to see if this Prisoner can trigger a Sensor
	 * 
	 * @param lockID				The lock ID of the Sensor to examine
	 * @return						Whether or not the Prisoner can trigger the Sensor
	 */
	public boolean canOpen(int lockID)
	{
		if (keyID == lockID)
		{
			return true;
		}
		
		if (chainGame.getDeadKeyIDs().contains(lockID, true))
		{
			return true;
		}
		
		return false;
	}
	
	/*
	 * Remove the chain from this player and the player on the right (if any)
	 */
	public void breakChain()
	{
		if(rightJoint != null)
		{
			if(this.alive && right != null && right.isAlive())
			{
				body.getWorld().destroyJoint(rightJoint);
			}
			rightJoint = null;
			if(right != null && right.leftJoint != null)
			{
				right.leftJoint = null;
			}
			if (right != null)
			{
				right.left = null;
			}
			right = null;
		}
	}
	
	/*
	 * Kill this Prisoner
	 * 
	 * @param death					What killed this Prisoner
	 */
	public void die(DeathType death)
	{	
		if(alive)
		{
			if (gameWorld instanceof BossFight)
			{
				((BossFight)gameWorld).startRespawnClock(this);
			}
			else
			{
				gameWorld.startRespawnClock(this);
			}
			
			if(death == DeathType.CAPTURE)
			{
				shouldMakeCorpse = false;
				CGCWorld.addToDestroyList(this);
				
				if (Options.storedTrackingOption)
				{
					ChaseApp.stats.getStatByIndex(getCurrentMapIndex()).captures++;
				}
			}
			else
			{
				Fixture f = body.getFixtureList().get(0);
				Filter fi = f.getFilterData();
				fi.categoryBits = BodyFactory.CAT_DECEASED;
				fi.maskBits = BodyFactory.MASK_DECEASED;
				f.setFilterData(fi);
				body.getFixtureList().set(0, f);
			}
			
			fixKeyIDs(playerID);
			super.die(death);
			alive = false;
		}
		rightJoint = null;
	}
	
	/*
	 * Capture this player
	 */
	public void captured()
	{
		die(DeathType.CAPTURE);
	}
	
	/*
	 * Handles logic for this Prisoner getting tied up
	 */
	public void tiedUp()
	{
		tiedUp = true;
		changeLowAnimationState(AnimationState.TIED);
		changeMidAnimationState(AnimationState.TIED);
		changeHighAnimationState(AnimationState.TIED);
	}
	
	/*
	 * Untie this Prisoner
	 */
	public void untie()
	{
		clearMash();
		maxStamina = MAX_STAMINA_BASE + Math.round(((float)(Math.random() * 2) - 1.0f) * MAX_STAMINA_RANGE);
		currentStamina = maxStamina;
		
		tiedUp = false;
		
		breakAllGrabs();
		
		changeLowAnimationState(AnimationState.STAND);
		changeMidAnimationState(AnimationState.STAND);
		changeHighAnimationState(AnimationState.STAND);
	}
	
	/*
	 * Returns whether or not this Prisoner is tied up
	 * 
	 * @return						Whether or not this Prisoner is tied up
	 */
	public boolean isTiedUp()
	{
		return tiedUp;
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
	 * @param upPressed				Whether or not the stick was just pressed up
	 * @param downPressed			Whether or not the stick was just pressed down
	 * @param rightPressed			Whether or not the stick was just pressed right
	 * @param leftPressed			Whether or not the stick was just pressed left
	 * @param bumper				Whether or not the bumper command is pressed	 
	 * @param buttonsMashed			Whether or not any of the face buttons have just been pressed
	 */
	public void controlUpdate(boolean buttonUp, boolean buttonDown, boolean buttonLeft, 
							boolean buttonRight, boolean stickPress, boolean up, boolean down, 
							boolean left, boolean right, boolean upPressed, boolean downPressed, 
							boolean rightPressed, boolean leftPressed, boolean bumper, boolean buttonsMashed)
	{
		faceButtonsInput(buttonUp, buttonDown, buttonLeft, buttonRight, stickPress, buttonsMashed);
		move(up, down, left, right, upPressed, downPressed, rightPressed, leftPressed, bumper);
	}
	
	/*
	 * Handles face button/D-pad input for a side of the controller
	 * 
	 * @param buttonUp				Whether or not the up face button/D-up is pressed
	 * @param buttonDown			Whether or not the down face button/D-down is pressed
	 * @param buttonLeft			Whether or not the left face button/D-left is pressed
	 * @param buttonRight			Whether or not the right face button/D-right is pressed
	 * @param stickPress			Whether or not the stick is pressed down (R3/L3)
	 * @param mashed				Whether or not any of the face buttons have just been pressed
	 */
	public void faceButtonsInput(boolean buttonUp, boolean buttonDown, boolean buttonLeft, 
			boolean buttonRight, boolean stickPress, boolean buttonsMashed)
	{
		super.faceButtonsInput(buttonUp, buttonDown, buttonLeft, buttonRight, stickPress, buttonsMashed);
		
		if (alive)
		{
			if (buttonUp || buttonDown || buttonLeft || buttonRight)
			{
				if (!noPunchTimer.isRunning())
				{
					if (grabbedByArray.size == 0 && !tiedUp)
					{
						punch(currentFacing);
					}
					else if (tiedUp && !CGCWorld.won() && !CGCWorld.lost() && !CGCWorld.terminated())
					{
						mashStatus(buttonUp, buttonDown, buttonLeft, buttonRight);
						breakRopes(false, numberMashed);
					}
					else
					{
						mashStatus(buttonUp, buttonDown, buttonLeft, buttonRight);
						struggle(numberMashed);
					}
				}
			}
		}
	}
	
	/*
	 * Move the player across the screen
	 * 
	 * @param up					Whether or not the up command is being used
	 * @param down					Whether or not the down command is being used
	 * @param left 					Whether or not the left command is being used
	 * @param right					Whether or not the right command is being used
	 * @param upPressed				Whether or not the up command was just used
	 * @param downPressed			Whether or not the down command was just used
	 * @param rightPressed			Whether or not the right command was just used
	 * @param leftPressed			Whether or not the left command was just used
	 * @param bumper				Whether or not the bumper command is being used
	 */
	public void move(boolean up, boolean down, boolean left, boolean right,
			boolean upPressed, boolean downPressed, boolean rightPressed, boolean leftPressed,
			boolean bumper)
	{	
		if (!tiedUp)
		{
			if (grabbedByArray.size > 0)
			{
				bumper = false;
			}
			
			super.move(up, down, left, right, bumper);
			
			if (sensorContacts.size > 0)
			{
				for (int i = 0; i < sensorContacts.size; i++)
				{
					if (!air && lastAirTime > 0)
					{
						sensorContacts.get(i).engage();
					}
					else if (air && lastAirTime == 0)
					{
						sensorContacts.get(i).disengage();
					}
				}
			}
			
			if (direction != 0)
			{
				currentFacing = direction;
			}
			
			if (grabbedByArray.size > 0 && body.getLinearDamping() > 5.0f)
			{
				for (int i = 0; i < grabbedByArray.size; i++)
				{
					if (grabbedByArray.get(i).getBody().getLinearDamping() < 50.0f)
					{
						body.setLinearDamping(5.0f);
					}
				}
			}
		}
		
		
	}
	
	/*
	 * @see com.percipient24.cgc.entities.Player#updatePlayer(float)
	 */
	public void updatePlayer(float dt)
	{
		super.updatePlayer(dt);
		
		if (grabbedByArray.size > 0)
		{
			grabbed = true;
			
			if(towerContacts.size > 0)
			{
				handleCapture();
			}
			else
			{
				setAlpha(1.0f);
			}
		}
		else
		{
			grabbed = false;
			setAlpha(1.0f);
		}
		
		if (currentStamina <= 0)
		{
			currentStamina = 0;
			clearMash();
			tiedUp();
		}
		else if (lightContacts.size > 1)
		{
			currentStamina -= lightContacts.size;
		}
		else if (lightContacts.size == 0 && !grabbed && !tiedUp)
		{
			currentStamina += .18f;
			if (currentStamina > maxStamina)
			{
				currentStamina = maxStamina;
			}
		}
		
		if (Options.storedTrackingOption)
		{
			float xDist = body.getPosition().x - previousPos.x;
			float yDist = body.getPosition().y - previousPos.y;
			float dist = (float) Math.sqrt((double) (xDist * xDist + yDist * yDist));
			
			if (dist < 0.001)
			{
				dist = 0;
			}
			
			distanceRun += dist;
		}
		
		previousPos = body.getPosition().cpy();
	}
	
	/*
	 * Handles the Prisoner punching other GameEntities
	 * 
	 * @param direction					The direction of the punch
	 */
	public void punch(int direction)
	{
		changeLowAnimationState(AnimationState.PUNCH);
		changeMidAnimationState(AnimationState.PUNCH);
		changeHighAnimationState(AnimationState.PUNCH);
		
		if (Options.storedTrackingOption)
		{
			ChaseApp.stats.getGame().punchesAttempted++;
		}
		
		if (checkPunchWall())
		{
			int knockbackDirection = direction - 4;
			if (knockbackDirection < 1)
			{
				knockbackDirection = 8 + knockbackDirection;
			}
			
			applyForceToSelf(knockbackDirection, 250);
		}
		
		if (!inBossFight && chainGame.getCurrentTrain() >= 0)
		{
			for (int i = 0; i < CGCWorld.getBF().getCurrentTrain(chainGame.getCurrentTrain()).size; i++)
			{
				TrainCar target = CGCWorld.getBF().getCurrentTrain(chainGame.getCurrentTrain()).get(i);
				
				if (checkPunchTrain(target))
				{
					deathKnockbackPosition = new Vector2(body.getPosition().x, body.getPosition().y + .2f);
					die(DeathType.TRAIN_PUNCH);
					return;
				}
			}
		}
		
		for (int i = 0; i < CGCWorld.getNumPlayers(); i++)
		{
			Player target = CGCWorld.getPlayers().get(i);
			
			if (checkPunchDirection(target))
			{
				if (body.getPosition().dst(target.getBody().getPosition()) < 1.0f)
				{
					target.punched(direction);
				}
			}	
		}
		
		if (inBossFight)
		{
			BossFight bf = (BossFight)gameWorld;
			
			if (bf.getBoss() instanceof PallBearer || bf.getBoss() instanceof SteelHorse)
			{
				Sheriff s;
				if(bf.getBoss() instanceof PallBearer)
				{
					PallBearer pb = (PallBearer)bf.getBoss();
					s = pb.gSheriff();
				}
				else
				{
					SteelHorse sh = (SteelHorse)bf.getBoss();
					s = sh.gSheriff();
				}
				
				if (checkPunchDirection(s))
				{
					if (body.getPosition().dst(s.getBody().getPosition()) < 1.0f)
					{
						s.punched();
					}
				}
			}
		}
		int xGrid = Math.round(body.getPosition().x);
		int yGrid = Math.round(body.getPosition().y);
		
		for (int i = -2; i < 3; i++) // Check two tiles in all directions for trees
		{
			if (yGrid + i > 0 && yGrid + i < CGCWorld.getLH().getWorldLength())
			{
				for (int j = -2; j < 3; j++)
				{
					if (xGrid + j >= 0 && xGrid + j < MapBuilder.chunkWidth)
					{
						for (int k = 0; k < CGCWorld.getLH().getLayer(LayerHandler.ground).getEntitiesInGrid(xGrid+j, yGrid+i).size; k++)
						{
							GameEntity target = CGCWorld.getLH().getLayer(LayerHandler.ground).getEntitiesInGrid(xGrid+j, yGrid+i).get(k);
							
							if (target != null && target instanceof Tree)
							{
								if (checkPunchDirection(target) && body.getPosition().dst(target.getBody().getPosition()) < 1.0f)
								{
									int knockbackDirection = direction - 4;
									if (knockbackDirection < 1)
									{
										knockbackDirection = 8 + knockbackDirection;
									}
									((Tree)target).punched(direction);
									applyForceToSelf(knockbackDirection, 250);
								}
							}
						}
					}
				}
			}
		}
		
		startNoPunchTimer();
	}
	
	/*
	 * Determines how this Prisoner reacts to getting punched
	 * 
	 * @param direction				The direction of the punch
	 */
	public void punched(int direction)
	{
		if (tiedUp)
		{
			breakRopes(true, numberMashed);
		}
		else
		{
			super.punched(direction);
		}
	}
	
	/*
	 * Keeps track of how many different face buttons this Prisoner has hit during a grab
	 * 
	 * @param buttonUp					Whether or not the up face button/D-Pad up was hit
	 * @param buttonDown				Whether or not the down face button/D-Pad down was hit
	 * @param buttonLeft				Whether or not the left face button/D-Pad left was hit
	 * @param buttonRight				Whether or not the right face button/D-Pad right was hit
	 */
	private void mashStatus(boolean buttonUp, boolean buttonDown, boolean buttonLeft, boolean buttonRight)
	{
		if (!grabMashed[0] && buttonUp)
		{
			numberMashed++;
			grabMashed[0] = true;
		}
		if (!grabMashed[1] && buttonDown)
		{
			numberMashed++;
			grabMashed[1] = true;
		}
		if (!grabMashed[2] && buttonLeft)
		{
			numberMashed++;
			grabMashed[2] = true;
		}
		if (!grabMashed[3] && buttonRight)
		{
			numberMashed++;
			grabMashed[3] = true;
		}
	}
	
	/*
	 * Clears how many different face buttons this Prisoner has hit during a grab
	 */
	private void clearMash()
	{
		for (int i = 0; i < grabMashed.length; i++)
		{
			grabMashed[i] = false;
		}
		
		numberMashed = 0;
	}
	
	/*
	 * Reduces RookieCops' grab strength so this Prisoner can break free
	 * 
	 * @param numberMashed				The number of different buttons this Prisoner has mashed
	 */
	public void struggle(int numberMashed)
	{
		for (int i = 0; i < grabbedByArray.size; i++)
		{
			grabbedByArray.get(i).reduceGrabStrength(numberMashed);
			
			if (grabbedByArray.get(i).getGrabStrength() <= 0)
			{
				clearMash();
				breakAllGrabs();
			}
		}
	}
	
	/*
	 * Reduces this Prisoner's stamina so the RookieCops can tie him/her up
	 * 
	 * @param numberMashed				The number of different buttons the RookieCop has mashed
	 */
	public void reduceStamina(int numberMashed)
	{
		currentStamina -= STAMINA_LOSS_MODIFIER / numberMashed;
	}
	
	/*
	 * Recovers stamina so this Prisoner can break out of being tied up
	 * 
	 * @param help						Whether or not another Prisoner is helping this one to escape
	 * @param numberMashed				The number of different buttons this Prisoner has mashed
	 */
	public void breakRopes(boolean help, int numberMashed)
	{
		if (help)
		{
			currentStamina += MAX_STAMINA_BASE / 4;
		}
		else
		{
			currentStamina += (BREAK_ROPE_MODIFIER / (grabbedByArray.size + 1)) / numberMashed;
		}
		
		if (currentStamina >= MAX_STAMINA_BASE)
		{
			untie();
		}
	}
	
	/*
	 * Sets a RookieCop to be grabbing this Prisoner
	 * 
	 * @param rc						The RookieCop grabbing this Prisoner
	 */
	public void sGrabbedBy(RookieCop rc)
	{	
		if (rc != null)
		{
			grabbedByArray.add(rc);
			TimerManager.removeTimer(noPunchTimer);
		}
	}

	/*
	 * Tell this Prisoner that a RookieCop is no longer holding him/her
	 * 
	 * @param rc						The RookieCop letting go of this Prisoner
	 */
	public void removeGrabbedBy(RookieCop rc)
	{
		if (grabbedByArray.removeValue(rc, true))
		{
			if (grabbedByArray.size == 0)
			{
				if (!tiedUp)
				{
					maxStamina = MAX_STAMINA_BASE + Math.round(((float)(Math.random() * 2) - 1.0f) * MAX_STAMINA_RANGE);
					currentStamina = maxStamina;
				}
				setAlpha(1.0f);
				startNoPunchTimer();
			}
		}
	}
	
	/*
	 * Tell this Prisoner to break all grabs
	 */
	public void breakAllGrabs()
	{
		for (int i = 0; i < grabbedByArray.size; i++)
		{
			grabbedByArray.get(i).stopGrabbing(true);
			i--;
		}
	}
	
	/*
	 * Tell this Prisoner to break all grabs at the end of ChainGame
	 */
	public void endOfLevelBreakGrabs()
	{
		for (int i = 0; i < grabbedByArray.size; i++)
		{
			grabbedByArray.get(i).endOfLevelStopGrabbing();
			i--;
		}
	}
	
	/*
	 * Gets whether or not this Prisoner is being grabbed
	 * 
	 * @return						Whether or not this Prisoner is being grabbed
	 */
	public boolean isGrabbed()
	{
		return grabbed;
	}

	/*
	 * Gets the ratio of the Prisoner's current stamina and max stamina
	 * 
	 * @return						The ratio of the Prisoner's current stamina and max stamina
	 */
	public float getBarRatio()
	{
		//If the prisoner is tied up, the bar instead needs to show
		//the ratio of current stamina and the base maximum
		//(before the random range is applied).
		if (tiedUp)
		{
			return currentStamina / MAX_STAMINA_BASE;
		}
		else
		{
			return currentStamina / maxStamina;
		}
	}
	
	/*
	 * Manages the timer for getting captured
	 */
	public void handleCapture()
	{
		if (alpha > 0)
		{
			if (tiedUp)
			{
				setAlpha(alpha - .03f);
			}
			else
			{
				setAlpha(alpha - .01f);
			}
		}
		else
		{
			captured();
		}
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param ge						The second entity colliding
	 */
	public void collide(GameEntity ge)
	{
		ge.collide(this);
	}
	

	/*
	 * Determines the second class type in a collision
	 * 
	 * @param rc						The second entity colliding
	 */
	public void collide(RookieCop rc)
	{
		rc.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param t                   The second entity colliding
	 */
	public void collide(Tree t)
	{
		super.collide(t);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param gt						The second entity colliding
	 */
	public void collide(GuardTower gt)
	{
		towerContacts.add(gt);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param t							The second entity colliding
	 */
	public void collide(Tank t)
	{
		die(DeathType.BOSS);
		t.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param ts						The second entity colliding
	 */
	public void collide(TankShell ts)
	{
		CGCWorld.addToDestroyList(ts);
		deathKnockbackPosition = ts.getBody().getWorldCenter();
		die(DeathType.BOSS);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param e							The second entity colliding
	 */
	public void collide(Explosion e)
	{
		deathKnockbackPosition = e.getBody().getWorldCenter();
		die(DeathType.BOSS);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param b							The second entity colliding
	 */
	public void collide(Bullet b)
	{
		CGCWorld.addToDestroyList(b);
		
		applyDecaySlow(1.0f);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param b							The second entity colliding
	 */
	public void collide(RiderBullet b)
	{
		if(b.gKill())
		{
			die(DeathType.BOSS);
		}
		else
		{
			applyDecaySlow(1.0f);
		}

		CGCWorld.addToDestroyList(b);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param s							The second entity colliding
	 */
	public void collide(SteelHorse s)
	{
		if(!s.isCrashing() && !s.isDown())
		{
			Vector2 dis = body.getWorldCenter().cpy().sub(s.getBody().getWorldCenter().cpy()).nor();
			
			if(dis.dot(s.getBody().getLinearVelocity().cpy().nor()) > Math.cos(45))
			{
				if(s.getBody().getLinearVelocity().cpy().len2() >= s.gMaxSpeed() * s.gMaxSpeed()  / 4)
				{
					die(DeathType.BOSS);
				}
			}
		}
	}
	
	/*
	 * Determines the second class type in a collision that is over
	 * 
	 * @param gt						The second entity not colliding anymore
	 */
	public void endCollide(GuardTower gt)
	{
		towerContacts.removeValue(gt, true);
	}
	
	/*
	 * Gets the direction this Prisoner is moving
	 * 
	 * @return						The direction this Prisoner is moving
	 */
	public int getDirection()
	{
		return direction;
	}
	
	/*
	 * Get the distance run by all Prisoners since the last reset
	 * 
	 * @return						The total distance run by Prisoners in box2D distance
	 */
	public static float getTotalDistRun()
	{
		return distanceRun;
	}
	
	/*
	 * Reset distance run by all Prisoners
	 */
	public static void resetDistRun()
	{
		distanceRun = 0;
	}
	
	/*
	 * Sets gameWorld to the one passed in
	 * 
	 * @param gw					The game world
	 */
	public void sGameWorld(CGCWorld gw)
	{
		gameWorld = gw;
	}
	
	/*
	 * Adds to the number of Sensor contacts
	 * 
	 * @param sensor				The Sensor tile to add to Sensor contacts
	 */
	public void addSensorContacts(Sensor sensor)
	{
		sensorContacts.add(sensor);
	}

	/*
	 * Removes the specified Sensor contact
	 * 
	 * @param sensor				The Sensor to remove
	 */
	public void removeSensorContact(Sensor sensor)
	{
		sensorContacts.removeValue(sensor, true);
	}
	
	/*
	 * Adds to the number of Spotlight contacts
	 * 
	 * @param light					The Spotlight tile to add to light contacts
	 */
	public void addLightContact(Spotlight light)
	{
		lightContacts.add(light);
	}
	
	/*
	 * Removes the specified Spotlight contact
	 * 
	 * @param light					The Spotlight to remove
	 */
	public void removeLightContact(Spotlight light)
	{
		lightContacts.removeValue(light, true);
	}
	
	/*
	 * Sets the index of the map the player was last on
	 * 
	 * @param newID					The index of the last map the player was on
	 */
	public void setOnNewID(int newID)
	{
		lastMapOnID = newID;
	}
	
	/*
	 * Gets the index of the map the player was last on
	 * 
	 * @return						The index of the last map the player was on
	 */
	public int getLastOnID()
	{
		return lastMapOnID;
	}
} // End class
