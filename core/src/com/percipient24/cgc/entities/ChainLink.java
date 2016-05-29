/*
 * @(#)ChainLink.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.entities.boss.SteelHorse;
import com.percipient24.cgc.entities.boss.Tank;
import com.percipient24.cgc.entities.players.CarrierCop;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a chain link entity
 * 
 * @version 0.2 14/2/4
 * @author Christopher Rider
 * @author Clayton Andrews
 */
public class ChainLink extends RotatableEntity 
{
	private Player leftPlayer = null;
	private ChainLink leftLink = null;
	private ChainLink rightLink = null;
	
	/*
	 * Creates a new ChainLink object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 */
	public ChainLink(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation,	EntityType pEntityType, Body attachedBody)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		//Determine half-values for use with rendering logic
		
		// These numbers are calculated based on the texture dimensions of the associated art asset.
		// The relationship is: 1.0f in world space is 96x96 in pixels (see art asset to get dimensions)
		// By default, half-width and half-height are calculated based on the base image's dimensions
		// when rotated/aligned horizontally.
		//this.halfHeight = -0.104166667f;
		//this.halfWidth = -0.15625f;
		
		rotation = body.getAngle() * Data.RADDEG;
	}
	
	/*
	 * Creates a new ChainLink object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param startAlpha			The starting alpha for this entity
	 */
	public ChainLink(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody, float startAlpha)
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
		if (layer != LayerHandler.LOW)
		{
			return;
		}
		
		rotation = body.getAngle() * Data.RADDEG;
		
		//update this Entity's animation state
		lowStateTime += deltaTime*body.getLinearVelocity().len2()/5.0f;
		
		//add additional code here
	}
	
	/*
	 * Gets the player to this chain's left
	 * 
	 * @return						The player to the left
	 */
	public Player gLeft()
	{
		return leftPlayer;
	}
	
	/*
	 * Sets the player to this chain's left
	 * 
	 * @param newLeft				The new player to the left
	 */
	public void sLeft(Player newLeft)
	{
		leftPlayer = newLeft;
	}
	
	/*
	 * Sets the chain link to this chain's left
	 * 
	 * @param newLink				The new chain link to the left
	 */
	public void setLeftLink(ChainLink newLink)
	{
		leftLink = newLink;
	}
	
	/*
	 * Gets the chain link to this chain's left
	 * 
	 * @return						The chain link to the left
	 */
	public ChainLink getLeftLink()
	{
		return leftLink;
	}
	
	/*
	 * Sets the chain link to this chain's right
	 * 
	 * @param newLink				The new chain link to the right
	 */
	public void setRightLink(ChainLink newLink)
	{
		rightLink = newLink;
	}
	
	/*
	 * Gets the chain link to this chain's right
	 * 
	 * @return						The chain link to the right
	 */
	public ChainLink getRightLink()
	{
		return rightLink;
	}
	
	/*
	 * Sets the new chain body - only used for generating new world chains
	 * 
	 * @param						The new chain's body
	 */
	public void sBody(Body newBody)
	{
		body = newBody;
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
	 * @param w						The first entity colliding
	 */
	public void collide(Wheel w)
	{
		CGCWorld.addToDestroyList(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param t						The first entity colliding
	 */
	public void collide(Tank t)
	{
		CGCWorld.addToDestroyList(this);
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
	 * Determines the second class type in a collision
	 * 
	 * @param cc					The first entity colliding
	 */
	public void collide(CarrierCop cc)
	{
		return;
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.chains);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.chains);
	}
	
	public void initCleanup()
	{
		// Remove references to this chainLink
		if (leftLink != null)
		{
			leftLink.setRightLink(null);
		}
		if (rightLink != null)
		{
			rightLink.setLeftLink(null);
		}
	}
	
	public void finalCleanup()
	{
		if (leftPlayer != null)
		{
			leftPlayer.breakChain();
		}
	}
} // End class