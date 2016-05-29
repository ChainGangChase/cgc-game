/*
 * @(#)TrainCar.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a train car entity
 * 
 * @version 0.2 14/2/4
 * @author Christopher Rider
 * @author Clayton Andrews
 */
public class TrainCar extends RotatableEntity 
{
	private boolean horizontalMovement = true;		//otherwise vertical movement; assumes left to right and top to bottom
	private boolean reverse_direction = false;		//flag for flipping movement direction
	private boolean offCamera = false;
	
	/*
	 * Creates a new TrainCar object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param left					Whether this is going left to right (true) or right to left (false)
	 */
	public TrainCar(Animation newLowAnimation, Animation newMidAnimation,
					Animation newHighAnimation, EntityType pEntityType, 
					Body attachedBody, boolean left)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		reverse_direction = left; //TODO: actually need to change how the constructor works for this value
		// These numbers are calculated based on the texture dimensions of the associated art asset.
		// The relationship is: 1.0f in world space is 96x96 in pixels (see art asset to get dimensions)
		// By default, half-width and half-height are calculated based on the base image's dimensions
		// when rotated/aligned horizontally.
		//halfWidth = -2.5f;
		//halfHeight = -0.75f;
		
		if(!horizontalMovement)
		{
			if(!reverse_direction)
			{
				this.rotation = 90;
			}
			else
			{
				this.rotation = 270;
			}
		}
		else
		{
			if (!reverse_direction)
			{
				this.rotation = 180;
			}
		}
	}
	
	/*
	 * Creates a new TrainCar object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param left					Whether this is going left to right (true) or right to left (false)
	 * @param startAlpha			The starting alpha for this entity
	 */
	public TrainCar(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, boolean left, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, left);
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
	}
	
	/*
	 * Determines the first class type in a collision
	 * 
	 * @param g						The second entity colliding
	 */
	public void collide(GameEntity g)
	{
		g.collide(this);
	}

	/*
	 * Determines the second class type in a collision
	 * 
	 * @param p						The first entity colliding
	 */
	public void collide(Player p)
	{
		CGCWorld.getCM().handlePlayerTrain(p);
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.ground);
		lh.addEntityToLayer(this, LayerHandler.mid);
		lh.addEntityToLayer(this, LayerHandler.above);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.ground);
		lh.removeEntityFromLayer(this, LayerHandler.mid);
		lh.removeEntityFromLayer(this, LayerHandler.above);
	}
	
	/*
	 * Updates the train car
	 */
	public void updateTrain()
	{
		//TODO: Add checks against vertical bounds for train rush
		if (!offCamera)
		{
			if (reverse_direction)
			{
				if (body.getPosition().x < -5.0f)
				{
					offCamera = true;
				}
			}
			else
			{
				if (body.getPosition().x > 25.0f)
				{
					offCamera = true;
				}
			}
		}
	}
	
	/*
	 * Gets whether or not this TrainCars is moving from left to right
	 * 
	 * @return						Whether the TrainCar goes left to right (true) or right to left (false)
	 */
	public boolean directionReversed()
	{
		return reverse_direction;
	}
	
	/*
	 * Get whether or not the train has passed by
	 * 
	 * @return						Whether or not the train has gone off the map again
	 */
	public boolean isOffCamera()
	{
		return offCamera;
	}
} // End class