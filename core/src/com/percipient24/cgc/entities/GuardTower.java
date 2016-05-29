/*
 * @(#)GuardTower.java		0.2 14/2/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a guard tower entity
 * 
 * @version 0.2 14/2/24
 * @author William Ziegler
 */
public class GuardTower extends GameEntity 
{
	/*
	 * Creates a new GuardTower object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param newGridX				The grid X-position of this object
	 * @param newGridY				The grid Y-position of this object
	 */
	public GuardTower(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody,
			int newGridX, int newGridY)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		gridX = newGridX;
		gridY = newGridY;
	}
	
	/*
	 * Creates a new GuardTower object
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
	public GuardTower(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, int newGridX, int newGridY, float startAlpha)
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
		lowStateTime += deltaTime;
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToGridLayer(gridX, gridY, this, LayerHandler.terrain);
		lh.addEntityToGridLayer(gridX, gridY, this, LayerHandler.above);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromGridLayer(gridX, gridY, this, LayerHandler.terrain);
		lh.removeEntityFromGridLayer(gridX, gridY, this, LayerHandler.above);
	}
} // End class