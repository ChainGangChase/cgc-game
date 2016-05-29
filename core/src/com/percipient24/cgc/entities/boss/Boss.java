/*
 * @(#)Boss.java		0.2 14/2/27
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities.boss;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.cgc.entities.RotatableEntity;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a boss entity
 * 
 * @version 0.2 14/2/27
 * @author Christopher Rider
 */
public abstract class Boss extends RotatableEntity 
{	
	/*
	 * Creates a new Boss object
	 *
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 */
	protected boolean defeated = false;
	public Boss(Animation newLowAnimation, 
			Animation newMidAnimation, Animation newHighAnimation, 
			EntityType pEntityType, Body attachedBody)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
	}
	
	/*
	 * Creates a new Boss object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param startAlpha			The starting alpha for this entity
	 */
	public Boss(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
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

	}
	
	/*
	 * Draws this Boss in the CGCWorld
	 * 
	 * @param sBatch				The SpriteBatch the game is using
	 * @param delta					Seconds elapsed since the last frame
	 * @param layerNumber			Which BodyFactory layer to draw
	 */
	public void draw(SpriteBatch sBatch, float delta, int layerNumber) 
	{
		super.draw(sBatch, delta, layerNumber);
	}
	
	/*
	 * Move the boss - Override
	 * 
	 * @param delta					Seconds elapsed since the last frame
	 */
	public void move(float delta)
	{
		
	}
	
	/*
	 * Have the boss fire - Override
	 */
	public void fire()
	{
		
	}
	
	/*
	 * Have the boss pause - Override
	 */
	public void pause()
	{
		
	}
	
	/*
	 * Have the boss resume - Override
	 */
	public void resume()
	{
		
	}
	
	/*
	 * Update function
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 */
	public void update(float deltaTime)
	{
		
	}
	
	/*
	 * Gets whether this boss has been defeated
	 * 
	 * @return						Whether or not this boss has been defeated
	 */
	public boolean isDefeated()
	{
		return defeated;
	}
	
	/*
	 * Sets defeated to true
	 */
	public void setDefeated()
	{
		defeated = true;
	}
} // End class