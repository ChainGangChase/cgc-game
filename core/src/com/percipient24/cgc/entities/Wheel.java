/*
 * @(#)Wheel.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a wheel entity
 * 
 * @version 0.2 14/2/4
 * @author Christopher Rider
 * @author Clayton Andrews
 */
public class Wheel extends RotatableEntity 
{
	/*
	 * Creates a new Wheel object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 */
	public Wheel(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
	}
	
	/*
	 * Creates a new Wheel object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param startAlpha			The starting alpha for this entity
	 */
	public Wheel(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody, boolean left, float startAlpha)
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
	 * @param c						The first entity colliding
	 */
	public void collide(ChainLink c)
	{
		if (c != null)
		{
			CGCWorld.getCM().handleChainWheel(c);
		}
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		
	}
} // End class