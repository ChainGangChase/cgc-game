/*
 * @(#)Wall.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.boss.SteelHorse;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a wall entity
 * 
 * @version 0.2 14/2/4
 * @author Christopher Rider
 * @author Clayton Andrews
 */
public class Wall extends RotatableEntity 
{
	/*
	 * Creates a new Wall object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param isVertical			If true, vertical
	 */
	public Wall(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, boolean isVertical)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		// These numbers are calculated based on the texture dimensions of the associated art asset.
		// The relationship is: 1.0f in world space is 96x96 in pixels (see art asset to get dimensions)
		// By default, half-width and half-height are calculated based on the base image's dimensions
		// when rotated/aligned horizontally.
		
		/*if(isVertical)
		{
			halfWidth = -0.5f;
			halfHeight = -5.5f;
		}
		else
		{
			halfWidth = -10.0f;
			halfHeight = -0.5f;
		}
		
		if(pEntityType == EntityType.BACKGROUND)
		{
			halfWidth = -10.0f;
			halfHeight = -5.5f;
		}*/
		
		parallaxDistMod = 6.0f;
	}
	
	/*
	 * Creates a new Wall object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param startAlpha			The starting alpha for this entity
	 * @param isVertical			If true, vertical
	 */
	public Wall(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, float startAlpha, 
			boolean isVertical)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, isVertical);

		alpha = startAlpha;
	}
	
	/*
	 * Timestep-based update method
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The BodyFactory layer to animate
	 */
	public void step(float deltaTime, int layer)
	{
		//update this Entity's animation state
		lowStateTime += deltaTime;
		
		//add additional code here
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		if (entityType == EntityType.WALL)
		{
			lh.addEntityToLayer(this, LayerHandler.terrain);
			lh.addEntityToLayer(this, LayerHandler.high);
		}
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		if (entityType == EntityType.WALL)
		{
			lh.removeEntityFromLayer(this, LayerHandler.terrain);
			lh.removeEntityFromLayer(this, LayerHandler.high);
		}
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param ge					The first entity colliding
	 */
	public void collide(GameEntity ge)
	{
		ge.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param s						The first entity colliding
	 */
	public void collide(SteelHorse s)
	{
		s.collide(this);
	}
	
	/*
	 * Gets whether this Wall is vertical or horizontal
	 * 
	 * @return						If this wall is vertical
	 */
	public boolean isVertical()
	{
		return CGCWorld.getAnimManager().gHeight(getHighAnim()) > CGCWorld.getAnimManager().gWidth(getHighAnim());
	}

} // End class