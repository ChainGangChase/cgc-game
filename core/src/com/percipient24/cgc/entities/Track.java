/*
 * @(#)Track.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a track entity
 * 
 * @version 0.2 14/2/4
 * @author Christopher Rider
 * @author Clayton Andrews
 */
public class Track extends RotatableEntity 
{
	/*
	 * Creates a new Track object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 */
	public Track(Animation newLowAnimation, Animation newMidAnimation, Animation newHighAnimation,
					EntityType pEntityType, Body attachedBody)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		// These numbers are calculated based on the texture dimensions of the associated art asset.
		// The relationship is: 1.0f in world space is 96x96 in pixels (see art asset to get dimensions)
		// By default, half-width and half-height are calculated based on the base image's dimensions
		// when rotated/aligned horizontally.
		//halfWidth = -10.0f;
		//halfHeight = -0.75f;
		//halfWidth = newLowAnimation.getKeyFrame(0).getRegionWidth()/96;
		//halfHeight = newLowAnimation.getKeyFrame(0).getRegionHeight()/96;
	}
	
	/*
	 * Creates a new Track object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param startAlpha			The starting alpha for this entity
	 */
	public Track(Animation newLowAnimation, Animation newMidAnimation, Animation newHighAnimation,
					EntityType pEntityType, Body attachedBody, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
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
		lh.addEntityToLayer(this, LayerHandler.terrain);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.terrain);
	}
} // End class