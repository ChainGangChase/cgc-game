/*
 * @(#)RiderBullet.java		0.3 14/4/10
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.Wall;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for bullets shot by SteelHorseRiders
 * 
 * @version 0.3 14/4/10
 * @author JD Kelly
 */
public class RiderBullet extends Bullet 
{
	private boolean kill = false;
	
	/*
	 * Creates a new RiderBullet object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param target				The target for the projectile to hit
	 * @param targetOffset			The center of the target
	 */
	public RiderBullet(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType,
			Body attachedBody, Vector2 target, Vector2 targetOffset, boolean deathBullet) {
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType,
				attachedBody, target, targetOffset);
		kill = deathBullet;
		
		if (kill) 
		{
			Flight_Speed = 1.0f;
		}
		else
		{
			Flight_Speed = 2.0f;
		}
	}
	
	/*
	 * Creates a new RiderBullet object
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
	public RiderBullet(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType,
			Body attachedBody, Vector2 target, Vector2 targetOffset, float startAlpha) 
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType,
				attachedBody, target, targetOffset, startAlpha);
		Flight_Speed = 1.0f;
	}
	
	/*
	 * @see com.percipient24.cgc.entities.Projectile#calcFlightSpeed()
	 */
	public void calcFlightSpeed() 
	{
		vel = targetPoint.cpy().sub(startLoc.cpy()).nor().scl(Flight_Speed);
	}
	
	/*
	 * Move this projectile
	 */
	public void move()
	{
		body.applyLinearImpulse(vel.cpy(), body.getWorldCenter().cpy(), true);
		
		// Make the projectile stop at its target location
		curLoc = body.getWorldCenter().cpy();
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param w						The first entity colliding
	 */
	public void collide (Wall w)
	{
		CGCWorld.addToDestroyList(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param p						The first entity colliding
	 */
	public void collide (Prisoner p)
	{
		p.collide(this);
	}
	
	/*
	 * Gets whether or not this RiderBullet should kill its target
	 * 
	 * @return						Whether or not this RiderBullet should kill its target
	 */
	public boolean gKill()
	{
		return kill;
	}
} // End class