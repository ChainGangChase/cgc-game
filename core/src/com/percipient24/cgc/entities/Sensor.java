/*
 * @(#)Sensor.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a sensor entity
 * 
 * @version 0.2 14/2/4
 * @author Christopher Rider
 * @author Clayton Andrews
 */
public class Sensor extends GameEntity 
{
	private short lockID;
	private short gateID;
	private Gate relatedGate;
	private int contacts;
	private boolean disabled;
	
	/*
	 * Creates a new Sensor object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param newGridX				The grid X-position of this object
	 * @param newGridY				The grid Y-position of this object
	 */
	public Sensor(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, int newGridX, int newGridY)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		lockID = -1;
		gateID = -1;
		contacts = 0;
		gridX = newGridX;
		gridY = newGridY;
	}
	
	/*
	 * Creates a new Sensor object
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
	public Sensor(Animation newLowAnimation, Animation newMidAnimation,
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
	 * @param layer					The BodyFactory layer to animate
	 */
	public void step(float deltaTime, int layer)
	{
		//update this Entity's animation state
	}
	
	/*
	 * Gets this Sensor's lock ID
	 * 
	 * @return						This Sensor's lock ID
	 */
	public short gLockID()
	{
		return lockID;
	}
	
	/*
	 * Sets this Sensor's lock ID
	 * 
	 * @param newID					This Sensor's new lock ID
	 */
	public void sLockID(short newID)
	{
		lockID = newID;
	}
	
	/*
	 * Gets this Sensor's gate ID
	 * 
	 * @return						This Sensor's gate ID
	 */
	public short gGID()
	{
		return gateID;
	}
	
	/*
	 * Sets this Sensor's gate ID
	 * 
	 * @param newID					This Sensor's new gate ID
	 */
	public void sGID(short newID)
	{
		gateID = newID;
	}
	
	/*
	 * Gets this Sensor's attached Gate
	 * 
	 * @return						This Sensor's attached Gate
	 */
	public Gate gGate()
	{
		return relatedGate;
	}
	
	/*
	 * Sets this Sensor's attached Gate
	 * 
	 * @param newGate					This Sensor's new attached Gate
	 */
	public void sGate(Gate newGate)
	{
		relatedGate = newGate;
	}
	
	/*
	 * Gets this Sensor's alpha value
	 * 
	 * @return						This Sensor's alpha value
	 */
	public float getAlpha()
	{
		return alpha;
	}
	
	/*
	 * Gets this Sensor's number of contacts
	 * 
	 * @return						This Sensor's number of contacts
	 */
	public int getContacts()
	{
		return contacts;
	}
	
	/*
	 * Sets the animation state for this Sensor to reflect an open Gate
	 */
	public void setOpenGateState()
	{
		//On and triggered
		lowStateTime = 2.0f;
	}
	
	/*
	 * Gets whether or not this Sensor is disabled
	 * 
	 * @return						Whether or not this Sensor is disabled
	 */
	public boolean getDisabled()
	{
		return disabled;
	}
	
	/*
	 * Activate this sensor
	 */
	public void engage() 
	{	
		contacts++;
		if (!relatedGate.test())
		{
			//On and not triggered
			lowStateTime = 1.0f;
		}
	}
	
	/*
	 * Deactivate this sensor
	 */
	public void disengage() 
	{
		contacts--;
		if (!relatedGate.isClosed())
		{
			if (contacts == 0)
			{
				//Off and triggered
				lowStateTime = 3.0f;
				disabled = true;
			}
		}
		else
		{
			if (contacts == 0)
			{
				//Off and not triggered
				lowStateTime = 0.0f;
			}
		}
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToGridLayer(gridX, gridY, this, LayerHandler.sensor);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromGridLayer(gridX, gridY, this, LayerHandler.sensor);
	}
} // End class