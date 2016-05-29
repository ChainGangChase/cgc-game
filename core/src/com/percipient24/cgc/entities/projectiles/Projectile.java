/*
 * @(#)Projectile.java		0.2 14/2/28
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.Camera;
import com.percipient24.cgc.entities.RotatableEntity;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a projectile entity
 * 
 * @version 0.2 14/2/28
 * @author Christopher Rider
 */
public abstract class Projectile extends RotatableEntity 
{
	protected Vector2 vel;
	protected Vector2 targetPoint;
	protected Vector2 startLoc;
	protected float maxYDist = 0;
	protected float maxXDist = 0;
	protected Vector2 curLoc;
	protected final float MAX_TRAVEL_TIME = 3.5f;
	protected float Flight_Speed = 5.0f;
	
	/*
	 * Creates a new Projectile object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param target				The target for the projectile to hit
	 * @param targetOffset			The center of the target
	 */
	public Projectile(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody,
			Vector2 target, Vector2 targetOffset)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		body.setFixedRotation(true);
		body.setLinearDamping(50.0f);
		body.setBullet(true);
		body.getFixtureList().get(0).setSensor(true);
		
		targetPoint = target.cpy();
		startLoc = body.getWorldCenter().cpy();
		
		maxYDist = Math.abs(targetPoint.y - startLoc.y);
		maxXDist = Math.abs(targetPoint.x - startLoc.x);
		
		calcFlightSpeed();
	}
	
	/*
	 * Creates a new Projectile object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param target				The target for the projectile to hit
	 * @param targetOffset			The center of the target
	 * @param startAlpha			The starting alpha for this entity
	 */
	public Projectile(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody,
			Vector2 target, Vector2 targetOffset, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, 
				pEntityType, attachedBody, target, targetOffset);
		alpha = startAlpha;
	}
	
	/*
	 * Calculates how fast this Projectile should be moving to hit a target point in a specific amount of time
	 */
	protected void calcFlightSpeed()
	{	
		float a = targetPoint.x - startLoc.x;
		float b = targetPoint.y - startLoc.y;
		
		float xVel = a / MAX_TRAVEL_TIME;
		float yVel = b / MAX_TRAVEL_TIME;
		
		vel = new Vector2(xVel, yVel);
		vel.nor();
		vel.scl(Flight_Speed);
	}
	
	/*
	 * Timestep-based update method
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The parallax layer to animate
	 */
	public void step(float deltaTime, int layer) 
	{

	}
	
	/*
	 * Move this projectile
	 */
	public void move()
	{
		body.applyLinearImpulse(vel, body.getWorldCenter(), true);
		
		// Make the projectile stop at its target location
		curLoc = body.getWorldCenter();
		
		if (Math.abs(curLoc.y - startLoc.y) >= maxYDist)
		{
			CGCWorld.addToDestroyList(this);
		}
		else if (Math.abs(curLoc.x - startLoc.x) > maxXDist)
		{
			CGCWorld.addToDestroyList(this);
		}
	}
	
	/*
	 * Flag projectiles to be destroyed if they go off-camera
	 * 
	 * @param camera				The Camera to check against
	 */
	public void checkPosition(Camera camera)
	{
		if (camera.toScreenPos(body.getWorldCenter()).y > camera.getTopLeftCorner().y)
		{
			CGCWorld.addToDestroyList(this);
		}
	}
	
	/*
	 * Resets this Projectile's velocity to a zero vector
	 */
	public void clearVel()
	{
		vel = new Vector2(0, 0);
	}
} // End class