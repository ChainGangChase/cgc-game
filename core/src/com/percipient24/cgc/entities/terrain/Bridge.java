/*
 * @(#)Bridge.java		0.3 14/4/3
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities.terrain;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a bridge entity
 * 
 * @version 0.3 14/4/3
 * @author Christopher Rider
 */
public class Bridge extends GameEntity 
{
	/*
	 * Creates a new Bridge object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this Bridge in the world
	 * @param newGridX				The grid X-position of this object
	 * @param newGridY				The grid Y-position of this object
	 */
	public Bridge(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, int newGridX, int newGridY)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		gridX = newGridX;
		gridY = newGridY;
	}
	
	/*
	 * Creates a new Bridge object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this Bridge in the world
	 * @param newGridX				The grid X-position of this object
	 * @param newGridY				The grid Y-position of this object
	 * @param startAlpha			The starting alpha for this entity
	 */
	public Bridge(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation,	EntityType pEntityType, Body attachedBody,
			int newGridX, int newGridY, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, 
				attachedBody, newGridX, newGridY);
		this.alpha = startAlpha;
	}
	
	/*
	 * Timestep-based update method
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The BodyFactory layer to animate
	 */
	public void step(float deltaTime, int layer) 
	{

	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToGridLayer(gridX, gridY, this, LayerHandler.terrain);
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromGridLayer(gridX, gridY, this, LayerHandler.terrain);
	}
} // End class