/*
 * @(#)Spotlight.java		0.3 14/4/18
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.Camera;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for cops' spotlights
 * 
 * @version 0.3 14/4/18
 * @author Christopher Rider
 */
public class Spotlight extends Targeter 
{
	private final float TARGET_SPEED = 900.0f;
	
	/*
	 * Creates a new Spotlight object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param c						The Camera the game is currently using
	 * @param ownerID				The player ID of the player that owns this Spotlight
	 */
	public Spotlight(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, Camera c, int ownerID)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, c, ownerID);
		
		if(impulses.size == 0)
		{
			float leg = TARGET_SPEED / (float)Math.sqrt(2);
			
			//Neutral
			impulses.add(new Vector2(0, 0));
			//Up
			impulses.add(new Vector2(0, TARGET_SPEED));
			//Up Right
			impulses.add(new Vector2(leg, leg));
			//Right
			impulses.add(new Vector2(TARGET_SPEED, 0));
			//Down Right
			impulses.add(new Vector2(leg, -leg));
			//Down
			impulses.add(new Vector2(0, -TARGET_SPEED));
			//Down Left
			impulses.add(new Vector2(-leg, -leg));
			//Left
			impulses.add(new Vector2(-TARGET_SPEED, 0));
			//Up Left
			impulses.add(new Vector2(-leg, leg));
			
			for(int i = 0; i < impulses.size; i++)
			{
				impulses.get(i).scl(0.003f);
			}
			
			curCamPos = new Vector2(c.position.x, c.position.y);
		}
		
		//halfHeight = -1;
		//halfWidth = -1;
		parallaxDistMod = 10.0f;
	}
	
	/*
	 * Updates this Spotlight
	 * 
	 * @param delta					Seconds elapsed since the last frame
	 * @param c						The camera for the game
	 */
	public void updateTarget(float delta, Camera c)
	{
		super.updateTarget(delta, c);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.Targeter#step(float, int)
	 */
	public void step(float deltaTime, int layer) 
	{

	}

	/*
	 * @see com.percipient24.cgc.entities.Targeter#addToWorldLayers(com.percipient24.b2dhelpers.LayerHandler)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.aerial);
	}

	/*
	 * @see com.percipient24.cgc.entities.Targeter#removeFromWorldLayers(com.percipient24.b2dhelpers.LayerHandler)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.aerial);
	}
} // End class