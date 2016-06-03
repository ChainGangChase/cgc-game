/*
 * @(#)Bullet.java		0.2 14/3/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a bullet entity
 * 
 * @version 0.2 14/3/4
 * @author Christopher Rider
 */
public class Bullet extends Projectile 
{ 	
	/*
	 * Creates a new Bullet object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param target				The target for the projectile to hit
	 * @param targetOffset			The center of the target
	 */
	public Bullet(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, 
			EntityType pEntityType, Body attachedBody,
			Vector2 target, Vector2 targetOffset)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, 
				pEntityType, attachedBody, target, targetOffset);
		
		vel.scl(1.0f/10.0f);
		//halfWidth = -0.265625f;
		//halfHeight = -0.265625f;
		Flight_Speed = 5.5f;
	}
	
	/*
	 * Creates a new Bullet object
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
	public Bullet(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody,
			Vector2 target, Vector2 targetOffset, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, 
				pEntityType, attachedBody, target, targetOffset);
		alpha = startAlpha;
	}
	
	/*
	 * Move this bullet
	 */
	public void move()
	{
		super.move();
	}
	
	/*
	 * Timestep-based update method
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The parallax layer to animate
	 */
	public void step(float deltaTime, int layer) 
	{
		// TODO Add a travel animation to a target point - handle collision near target point
	}
	
	/*
	 * Determines the first class type in a collision
	 * 
	 * @param ge					The second entity colliding
	 */
	public void collide (GameEntity ge)
	{
		ge.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param p						The first entity colliding
	 */
	public void collide (Player p)
	{
		p.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param p						The first entity colliding
	 */
	public void collide (Prisoner p)
	{
		CGCWorld.addToDestroyList(this);
		p.collide(this);
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.projectile);
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.projectile);
	}
} // End class