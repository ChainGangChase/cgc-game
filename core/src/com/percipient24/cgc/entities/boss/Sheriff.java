/* @(#)Sheriff.java		0.3 14/4/16
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities.boss;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.RotatableEntity;
import com.percipient24.cgc.entities.Targeter;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.projectiles.RiderBullet;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for the Sheriff in the Pallbearer/SteelHorse boss fight
 * 
 * @version 0.3 14/4/16
 * @author JD Kelly
 */

public class Sheriff extends RotatableEntity {
	private Prisoner target = null;
	private Targeter targeter = null;
	private CGCTimer fireTimer;
	private Timer.Task fireTask;
	private float fireTime = 3.0f;
	private float maxSpeed = 30.0f;
	private float maxForce = 30.0f;
	
	private CGCTimer swapTimer;
	private Timer.Task swapTask;
	private float swapTime = 2.0f;
	
	private boolean canFire = true;
	private Boss boss;
	public static float trailTime = 1.0f;
	private boolean swapped = false;
	
	/*
	 * Creates a new Sheriff
	 * 
	 * @param newLowAnimation		The original lowAnimation
	 * @param newMidAnimation		The original midAnimation
	 * @param newHighAnimation		The original highAnimation
	 * @param pEntityType			The entity type
	 * @param attachedBody			The Sheriff's body.
	 * @param b						The boss of the fight
	 */
	public Sheriff(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType,
			Body attachedBody, Boss b)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType,
				attachedBody);
		boss = b;
		
		fireTask = new Timer.Task()
		{
			
			public void run()
			{
				if (canFire)
				{
					fire();
				}
			}
		};
		
		fireTimer = new CGCTimer(fireTask, fireTime, true, "fireTimer");
		
		swapTask = new Timer.Task()
		{
			
			public void run()
			{
				canFire = true;
				TimerManager.addTimer(fireTimer);
			}
		};
		
		swapTimer = new CGCTimer(swapTask, swapTime, false, "swapTimer");
		
		do
		{
			target = CGCWorld.getPrisoners().random();
		}while(!target.isAlive() || target == null);
	}

	/*
	 * Creates a new Sheriff
	 * 
	 * @param newLowAnimation		The original lowAnimation
	 * @param newMidAnimation		The original midAnimation
	 * @param newHighAnimation		The original highAnimation
	 * @param pEntityType			The entity type
	 * @param attachedBody			The Sheriff's body.
	 * @param bo					The boss of the fight
	 * @param startAlpha			The starting alpha of the Sheriff
	 */
	public Sheriff(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType,
			Body attachedBody, Boss bo, float startAlpha)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType,
				attachedBody, startAlpha);
		boss = bo;
		fireTask = new Timer.Task()
		{
			
			public void run()
			{
				fire();
			}
		};
		
		fireTimer = new CGCTimer(fireTask, fireTime, true, "fireTimer");
		
		do
		{
			target = CGCWorld.getPrisoners().random();
		}while(!target.isAlive() || target == null);
		
		TimerManager.addTimer(fireTimer);
	}

	/*
	 * Update function
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 */
	public void update(float deltaTime)
	{
		if (target != null && targeter != null)
		{
			if (targeter.getBody().getWorldCenter().cpy().dst2(target.getBody().getWorldCenter().cpy()) > 0.01)
			{
				Vector2 avg = target.getBody().getWorldCenter().cpy();
				Vector2 desVel = avg.sub(targeter.getBody().getWorldCenter().cpy()).nor().scl(maxSpeed);
				Vector2 steer = desVel.sub(body.getLinearVelocity().cpy());
				steer.clamp(0, maxForce);
				steer = steer.cpy().scl(1.0f/targeter.getBody().getMass());
				
				Vector2 vel = body.getLinearVelocity().cpy().add(steer.scl(deltaTime));
				vel.clamp(0, maxSpeed);
				
				targeter.getBody().setLinearVelocity(vel);
			}
			else
			{
				Vector2 dif = target.getBody().getWorldCenter().cpy().sub(targeter.getBody().getWorldCenter().cpy());
				targeter.getBody().setLinearVelocity(dif.scl(1 / deltaTime));
			}
		}
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.LayerHandler)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.bossPlayer);
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.LayerHandler)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		if (swapped)
		{
			lh.removeEntityFromLayer(this, LayerHandler.ground);
		}
		else
		{
			lh.removeEntityFromLayer(this, LayerHandler.bossPlayer);
		}
	}
	
	/*
	 * Swaps the layers of the Sheriff when the pallbearer falls
	 * 
	 * @param lh					The LayerHandler
	 */
	public void swapWorldLayers(LayerHandler lh)
	{
		removeFromWorldLayers(lh);
		lh.addEntityToLayer(this, LayerHandler.ground);
		swapped = true;
		
		Fixture f = body.getFixtureList().get(0);
		Filter fi = f.getFilterData();
		fi.categoryBits = BodyFactory.CAT_IMPASSABLE;
		fi.maskBits = BodyFactory.MASK_SHERIFF_GROUND;
		f.setFilterData(fi);
		body.getFixtureList().set(0, f);
		
		canFire = false;
		TimerManager.addTimer(swapTimer);
	}
	
	/*
	 * Handles bullet firing
	 */
	public void fire()
	{
		if((boss instanceof SteelHorse && ((SteelHorse) boss).getNumCops() == 0) || boss instanceof PallBearer)
		{
			if (target != null && targeter != null) // If null, the targeter is missing somehow
			{
				// Create a bullet to fire
				Body b = CGCWorld.getBF().createCircle(body.getWorldCenter().x, 
						body.getWorldCenter().y, 
						0.1f, BodyType.DynamicBody, BodyFactory.CAT_INTERACTABLE, BodyFactory.MASK_INTERACTABLE);
				GameEntity ge = new RiderBullet(null, null, AnimationManager.bulletAnim, EntityType.BULLET,
						b, targeter.getBody().getPosition().cpy(), 
						new Vector2(targeter.getHighRegion().getRegionWidth() / 2,
								targeter.getHighRegion().getRegionHeight() / 2), swapped);
				b.setUserData(ge);
				ge.addToWorldLayers(CGCWorld.getLH());
			}
		}
		
		if (!CGCWorld.lost() && !CGCWorld.terminated())
		{
			if(CGCWorld.getPrisoners().size > 0)
			{
				do
				{
					target = CGCWorld.getPrisoners().random();
				}while(!target.isAlive() || target == null);
			}
			else
			{
				target = null;
			}
		}
	}
	
	/*
	 * Determines the first class type in a collision
	 * 
	 * @param pb					The second entity colliding
	 */
	public void collide(PallBearer pb)
	{
		
	}
	
	/*
	 * Determines the first class type in a collision
	 * 
	 * @param sh					The second entity colliding
	 */
	public void collide(SteelHorse sh)
	{
		
	}
	
	/*
	 * Ends the game if condition have been met
	 */
	public void punched()
	{
		boolean b = false;
		
		if(boss instanceof SteelHorse)
		{
			b = ((SteelHorse)boss).isDead();
		}
		else if(boss instanceof PallBearer)
		{
			b = ((PallBearer)boss).isDead();
		}
		if(b && !(CGCWorld.won() || CGCWorld.lost() || CGCWorld.terminated()))
		{
			boss.setDefeated();
		}
	}
	
	/*
	 * Tells the sheriff and his targeter to be destroyed
	 */
	public void destroySheriff()
	{
		CGCWorld.addToDestroyList(this);
		if(targeter != null)
		{
			CGCWorld.addToDestroyList(targeter);
		}
	}

	/*
	 * Adds the Targeter back in
	 */
	public void addTargeter()
	{
		if(targeter == null)
		{
			Body bod = CGCWorld.getBF().createCircle(body.getWorldCenter().cpy().x, 
					body.getWorldCenter().cpy().y, 0.5f, BodyType.DynamicBody, 
					BodyFactory.CAT_NON_INTERACTIVE, BodyFactory.MASK_NON_INTERACTIVE);
			bod.getFixtureList().get(0).setSensor(true);
			bod.setFixedRotation(true);
	
			targeter = new Targeter(null, null, AnimationManager.targetingAnims[0], EntityType.TARGETER, bod, CGCWorld.getCamera(), -1);
			bod.setUserData(targeter);
	
			targeter.addToWorldLayers(CGCWorld.getLH());
			
			TimerManager.addTimer(fireTimer);
		}
	}
	
	/*
	 * Removes the Targeter
	 */
	public void removeTargeter()
	{
		TimerManager.removeTimer(fireTimer);
		targeter.removeFromWorldLayers(CGCWorld.getLH());
		CGCWorld.addToDestroyList(targeter);
	}
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#step(float, int)
	 */
	@Override
	public void step(float deltaTime, int layer)
	{
		
	}
	
	/*
	 * Gets the Boss
	 * 
	 * @return						The boss to which this Sheriff is attached
	 */
	public Boss getBoss()
	{
		return boss;
	}
}// End Class
