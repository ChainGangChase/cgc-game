/*
 * @(#)Fence.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.entities.boss.Tank;
import com.percipient24.cgc.entities.projectiles.TankShell;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a fence entity
 * 
 * @version 0.2 14/2/4
 * @author Christopher Rider
 * @author Clayton Andrews
 */
public class Fence extends RotatableEntity 
{
	/*
	 * Creates a new Fence object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param newGridX				The grid X-position of this object
	 * @param newGridY				The grid Y-position of this object
	 */
	public Fence(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody,
			int newGridX, int newGridY)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		rotation = body.getAngle()*Data.RADDEG;
		gridX = newGridX;
		gridY = newGridY;
		
		parallaxDistMod = 4.0f;
		
		// Determine the half-width and half-height based on fence type:
		// These numbers are calculated based on the texture dimensions of the associated art asset.
		// The relationship is: 1.0f in world space is 96x96 in pixels (see art asset to get dimensions)
		// By default, half-width and half-height are calculated based on the base image's dimensions
		// when rotated/aligned horizontally.
		
		/*if(entityType != EntityType.POST)
		{
			halfWidth = -.25f;
			halfHeight = -0.041666667f;
		}
		else
		{
			halfWidth = halfHeight = -0.052083333f;
		}*/
	}
	
	/*
	 * Creates a new Fence object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param newGridX				The grid X-position of this object
	 * @param newGridY				The grid Y-position of this object
	 * @param startAlpha			The starting alpha for this entity
	 */
	public Fence(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody,
			int newGridX, int newGridY, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, 
				attachedBody, newGridX, newGridY);
		alpha = startAlpha;
	}
	
	/*
	 * Timestep-based update method
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The parallax layer to animate
	 */
	public void step(float deltaTime, int layer)
	{
		//update this Entity's animation state
		lowStateTime += deltaTime;
		
		//add additional code here
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param ge					The second entity colliding
	 */
	public void collide(GameEntity ge)
	{
		ge.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param ts					The second entity colliding
	 */
	public void collide(TankShell ts)
	{
		ts.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param t						The second entity colliding
	 */
	public void collide(Tank t)
	{
		CGCWorld.addToDestroyList(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param e						The second entity colliding
	 */
	public void collide(Explosion e)
	{
		CGCWorld.addToDestroyList(this);
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToGridLayer(gridX, gridY, this, LayerHandler.ground);
		lh.addEntityToGridLayer(gridX, gridY, this, LayerHandler.fenceHigh);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromGridLayer(gridX, gridY, this, LayerHandler.ground);
		lh.removeEntityFromGridLayer(gridX, gridY, this, LayerHandler.fenceHigh);
	}
} // End class
