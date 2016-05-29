/*
 * @(#)RotatableEntity.java		0.2 14/2/10
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.cgc.Data;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a rotatable entity
 * 
 * @version 0.2 14/2/10
 * @author Clayton Andrews
 */
public abstract class RotatableEntity extends GameEntity 
{
	/*
	 * Creates a new RotatableEntity object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 */
	public RotatableEntity(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
	}
	
	/*
	 * Creates a new RotatableEntity object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param startAlpha			The starting alpha for this entity
	 */
	public RotatableEntity(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		alpha = startAlpha;
	}

	/*
	 * Gets the current rotated half-width of this RotatableEntity
	 *
	 * @param layer					The layer of the entity to examine
	 * @param rotation 				The rotation of the entity
	 * @return						The current rotated half-width of this RotatableEntity
	 */
	public float getImageHalfWidth(int layer, float rotation)
	{
		rotation = rotation * Data.DEGRAD;
		return getImageHalfWidth(layer)*(float)Math.cos(rotation) - getImageHalfHeight(layer)*(float)Math.sin(rotation);
	}

	/*
	 * Gets the current rotated half-height of this RotatableEntity
	 *
	 * @param layer					The layer of the entity to examine
	 * @param rotation 				The rotation of the entity
	 * @return						The current rotated half-height of this RotatableEntity
	 */
	public float getImageHalfHeight(int layer, float rotation)
	{
		rotation = rotation * Data.DEGRAD;
		return getImageHalfWidth(layer)*(float)Math.sin(rotation) + getImageHalfHeight(layer)*(float)Math.cos(rotation);
	}
	
	/*
	 * Draws this RotatableEntity in the CGCWorld
	 * 
	 * @param sBatch				The SpriteBatch the game is using
	 * @param delta					Seconds elapsed since the last frame
	 * @param layerNumber			Which BodyFactory layer to draw
	 */
	public void draw(SpriteBatch sBatch, float delta, int layerNumber) 
	{
		super.draw(sBatch, delta, layerNumber);
	}
	
} // End class