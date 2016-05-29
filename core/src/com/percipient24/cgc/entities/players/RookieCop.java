/*
 * @(#)RookieCop.java		0.2 14/2/5
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities.players;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.b2dhelpers.BodyFactory;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.Gate;
import com.percipient24.cgc.entities.TrainCar;
import com.percipient24.cgc.entities.Tree;
import com.percipient24.cgc.entities.boss.Tank;
import com.percipient24.cgc.maps.MapBuilder;
import com.percipient24.cgc.screens.Options;
import com.percipient24.enums.AnimationState;
import com.percipient24.enums.DeathType;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for players who are RookieCops
 * 
 * @version 0.2 14/1/31
 * @author JD Kelly
 * @author William Ziegler
 */
public class RookieCop extends Player
{
	CGCWorld gameWorld;
	private Joint handcuffs;
	private Prisoner grabbed;
	private int maxGrabStrength;
	private int currentGrabStrength;
	private int grabDifference;
	protected boolean noGrab;
	
	private boolean[] grabMashed = new boolean[4];
	private int numberMashed = 0;
	
	// Timer variables
	protected CGCTimer grabCooldownTimer;
	protected Timer.Task grabCooldownTask;
	protected float grabCooldown = 2.0f; //Only set here to prevent crashes.
	
	//This number will change pending balance discussions.
	private final int MAX_GRAB_STRENGTH_BASE = 200;
	private final int GRAB_BREAK_MULTIPLIER = 8;
	private final int MAX_GRAB_RANGE = GRAB_BREAK_MULTIPLIER * 4;
	
	private static float distanceRun = 0.0f;
	
	public static float minDazedSpeed = 0.0f;
	public static float dazedSlowRecharge = 0.3f;
	
	private float donutRotationAngle = 0.0f;
	
	/*
	 * Creates a new RookieCop object
	 * 
	 * @param theWorld				Reference to the CGCWorld class (for accessing data available therein)
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param pID					The ID of the player being created (player #)
	 */
	public RookieCop(CGCWorld theWorld, Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody, short pID)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, pID);
		
		gameWorld = theWorld;
		
		maxGrabStrength = MAX_GRAB_STRENGTH_BASE + Math.round(((float)(Math.random() * 2) - 1.0f) * MAX_GRAB_RANGE);
		currentGrabStrength = maxGrabStrength;
		noGrab = false;
		
		grabCooldownTask = new Timer.Task() 
		{	
			public void run() 
			{	
				currentGrabStrength = maxGrabStrength;
				noGrab = false;
			}
		};
		
		grabCooldownTimer = new CGCTimer(grabCooldownTask, grabCooldown, false, "grabCooldownTimer");
	}
	
	/*
	 * Kill this RookieCop
	 * 
	 * @param death					What killed this RookieCop
	 */
	public void die(DeathType death) 
	{
		if(alive)
		{
			if (grabbed != null)
			{
				stopGrabbing(false);
			}
			
			gameWorld.startRespawnClock(this);
			
			Fixture f = body.getFixtureList().get(0);
			Filter fi = f.getFilterData();
			fi.categoryBits = BodyFactory.CAT_DECEASED;
			fi.maskBits = BodyFactory.MASK_DECEASED;
			f.setFilterData(fi);
			body.getFixtureList().set(0, f);
			
			super.die(death);
		}
	}
	
	/*
	 * Handles face button/d-pad input for a side of the controller
	 * 
	 * @param buttonUp				Whether or not the up face button/D-up is pressed
	 * @param buttonDown			Whether or not the down face button/D-down is pressed
	 * @param buttonLeft			Whether or not the left face button/D-left is pressed
	 * @param buttonRight			Whether or not the right face button/D-right is pressed
	 * @param stickPress			Whether or not the stick is pressed down (L3/R3)
	 * @param mashed				Whether or not any of the face buttons/D-pad were just pressed
	 */
	public void faceButtonsInput(boolean buttonUp, boolean buttonDown, boolean buttonLeft, 
			boolean buttonRight, boolean stickPress, boolean mashed)
	{
		super.faceButtonsInput(buttonUp, buttonDown, buttonLeft, buttonRight, stickPress, mashed);
		
		if (alive)
		{
			if (buttonUp || buttonDown || buttonLeft || buttonRight)
			{
				if (grabbed != null)
				{
					mashStatus(buttonUp, buttonDown, buttonLeft, buttonRight);
					grabbed.reduceStamina(numberMashed);
				}
				if (!noPunchTimer.isRunning() && grabbed == null)
				{
					punch(currentFacing);
				}
			}
		}
	}
	
	/*
	 * Move the RookieCop across the screen and handle grab forces
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
		if (alive)
		{
			if (grabbed != null && handcuffs == null)
			{
				handcuffs = CGCWorld.getBF().createRopeJoint(body, grabbed.getBody(), .61f);
			}
			
			if ((bumper && handcuffs != null))
			{
				stopGrabbing(false);
				bumper = false;
			}
			
			super.move(up, down, left, right, bumper);
			
			if (direction != 0)
			{
				currentFacing = direction;
				
				if (grabbed != null)
				{
					applyForceToSelf(direction, 60);
					applyForceToPrisoner(direction, 15);
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
		
		if (grabbed != null)
		{
			if (!alive || !grabbed.isAlive())
			{
				stopGrabbing(false);
			}
		}
		
		if (noGrab)
		{
			applyDecaySlow(1.0f);
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
	 * Gets this RookieCop's grab strength
	 * 
	 * @return						The RookieCop's grab strength
	 */
	public int getGrabStrength()
	{
		return currentGrabStrength;
	}
	
	/*
	 * Gets whether or not the RookieCop's grab is on cooldown
	 * 
	 * @return						Whether or not the RookieCop's grab is on cooldown						
	 */
	public boolean isGrabCooldown()
	{
		return grabCooldownTimer.isRunning();
	}
	
	/*
	 * Gets this RookieCop's grab strength bar scaling ratio
	 * 
	 * @return						The RookieCop's grab strength bar scaling ratio						
	 */
	public float getBarRatio()
	{
		if (noGrab)
		{
			return grabCooldownTimer.getPercent();
		}
		else
		{
			return (float)currentGrabStrength / (float)maxGrabStrength;
		}
	}
	
	/*
	 * Moves the "dizzy donuts" which are spinning around this RookieCop
	 * 
	 * @param adjust				How much to adjust the rotation of the donuts
	 */
	public float adjustDonutRotation(float adjust)
	{
		return donutRotationAngle += adjust;
	}
	
	/*
	 * Applies a decaying slow to this Player
	 * 
	 * @param amount				The percent slow amount
	 */
	public void applyDecaySlow(float amount)
	{
		decaySpeedMod -= amount;
		
		if (decaySpeedMod < minDazedSpeed)
		{
			decaySpeedMod = minDazedSpeed;
		}
		else if (decaySpeedMod > 1.0f)
		{
			decaySpeedMod = 1.0f;
		}
	}
	
	/*
	 * Handles the RookieCop punching other GameEntities
	 * 
	 * @param direction					The direction of the punch
	 */
	public void punch(int direction)
	{
		changeMidAnimationState(AnimationState.PUNCH);
		
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
		
		if (chainGame != null)
		{
			//Punch things.
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
	 * Keeps track of how many different face buttons this RookieCop has hit during a grab
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
	 * Clears how many different face buttons this RookieCop has hit during a grab
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
	 * Causes the RookieCop to grab onto a Prisoner
	 * 
	 * @param p						The Prisoner to be grabbed
	 */
	public void grab(Prisoner p)
	{
		if (p.isAlive() && !noGrab)
		{
			grabbed = p;
			grabbed.sGrabbedBy(this);
			body.getFixtureList().get(0).setDensity(2.5f);
			body.resetMassData();
		}
	}

	/*
	 * Reduces this RookieCop's grab strength
	 * 
	 * @param numberMashed				The number of different buttons the grabbed Prisoner has mashed
	 */
	public void reduceGrabStrength(int numberMashed)
	{
		if (currentGrabStrength > 0)
		{
			currentGrabStrength -= GRAB_BREAK_MULTIPLIER / numberMashed;
		}
	}
	
	/*
	 * Applies a force to this RookieCop
	 * 
	 * @param direction				The direction of the force
	 * @param strength				The strength of the force
	 */
	public void applyForceToSelf(int direction, int strength)
	{
		Vector2 tempForce = impulses.get(direction).cpy();
		tempForce.scl(strength);
		getBody().applyForce(tempForce, body.getWorldCenter(), true);
		
		Vector2 vel = getBody().getLinearVelocity();
		float grabbedSpeed = vel.len();
		if(grabbedSpeed > getSpeed())
		{
			vel.nor();
			getBody().setLinearVelocity(vel.scl(getSpeed()));
		}
	}

	/*
	 * Applies a force to drag Prisoners around
	 * 
	 * @param direction				The direction of the force
	 * @param strength				The strength of the force
	 */
	public void applyForceToPrisoner(int direction, int strength)
	{
		Vector2 tempForce = impulses.get(direction).cpy();
		tempForce.scl(strength);
		grabbed.getBody().applyForce(tempForce, body.getWorldCenter(), true);
		
		Vector2 vel = grabbed.getBody().getLinearVelocity();
		float grabbedSpeed = vel.len();
		if(grabbedSpeed > getSpeed())
		{
			vel.nor();
			grabbed.getBody().setLinearVelocity(vel.scl(getSpeed()));
		}
	}

	/*
	 * Tell this RookieCop to stop grabbing a Prisoner
	 * 
	 * @param struggle				Whether or not the Prisoner broke free
	 */
	public void stopGrabbing(boolean struggle)
	{
		grabDifference = maxGrabStrength - currentGrabStrength;
		grabCooldown = Math.max(grabDifference / 60.0f, 0.5f);
		
		if (struggle)
		{
			int knockbackDirection = checkDirectionFrom(grabbed);
			breakRopeJoint();
			applyForceToSelf(knockbackDirection, 500);
		}
		else
		{
			breakRopeJoint();
		}
		
		if (grabbed != null)
		{
			grabbed.removeGrabbedBy(this);
			grabbed = null;
		}
		
		clearMash();	
		
		maxGrabStrength = MAX_GRAB_STRENGTH_BASE + Math.round(((float)(Math.random() * 2) - 1.0f) * MAX_GRAB_RANGE);
		noGrab = true;
		
		if(!grabCooldownTimer.isRunning())
		{
			TimerManager.removeTimer(grabCooldownTimer);
			grabCooldownTimer = new CGCTimer(grabCooldownTask, grabCooldown, false, "grabCooldownTimer");
			TimerManager.addTimer(grabCooldownTimer);		
		}
	}

	/*
	 * Tell this RookieCop to stop grabbing a Prisoner at the end of ChainGame
	 */
	public void endOfLevelStopGrabbing()
	{
		grabCooldown = 0.0f;
		
		breakRopeJoint();
		
		grabbed.removeGrabbedBy(this);
		grabbed = null;
		
		clearMash();	
		
		maxGrabStrength = MAX_GRAB_STRENGTH_BASE + Math.round(((float)(Math.random() * 2) - 1.0f) * MAX_GRAB_RANGE);
		currentGrabStrength = maxGrabStrength;
	}
	
	/*
	 * Determines how this RookieCop reacts to getting punched
	 * 
	 * @param direction				The direction of the punch
	 */
	public void punched(int direction)
	{
		if (grabbed != null)
		{
			stopGrabbing(false);
		}
		super.punched(direction);
	}

	/*
	 * Destroys the RopeJoint connecting this RookieCop to a Prisoner
	 */
	private void breakRopeJoint()
	{
		if (body != null && body.getFixtureList().size > 0)
		{
			body.getFixtureList().get(0).setDensity(0.5f);
			body.resetMassData();
			if (handcuffs != null && handcuffs.getBodyB() != null)
			{
				body.getWorld().destroyJoint(handcuffs);
			}
			handcuffs = null;
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
	 * @param p						The second entity colliding
	 */
	public void collide(Prisoner p)
	{
		if (grabbed == null)
		{
			grab(p);
		}
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
	 * @param g                   The second entity colliding
	 */
	public void collide(Gate g)
	{
		super.collide(g);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param t                   The second entity colliding
	 */
	public void collide(Tank t)
	{
		return; // Do nothing
	}
	
	/*
	 * Determines the second class type in a collision that is over
	 * 
	 * @param ge						The second entity not colliding anymore
	 */
	public void endCollide(GameEntity ge)
	{
		
	}
	
	/*
	 * Get the distance run by all cops since the last reset
	 * 
	 * @return						The total distance run by cops in box2D distance
	 */
	public static float getTotalDistRun()
	{
		return distanceRun;
	}
	
	/*
	 * Reset distance run by all cops
	 */
	public static void resetDistRun()
	{
		distanceRun = 0;
	}
} // End class