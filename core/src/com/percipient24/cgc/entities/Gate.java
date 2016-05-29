/*
 * @(#)Gate.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.players.RookieCop;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a gate entity
 * 
 * @version 0.2 14/2/4
 * @author Christopher Rider
 * @author Clayton Andrews
 */
public class Gate extends RotatableEntity 
{
	private Array<Sensor> sensors;
	private boolean closed;
	private short gateID;
	public int sensorShapeID = -1;
	public Color sensorColor;
	
	/*
	 * Creates a new Gate object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param newGridX				The grid X-position of this object
	 * @param newGridY				The grid Y-position of this object
	 */
	public Gate(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, int newGridX, int newGridY)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		closed = true;
		sensors = new Array<Sensor>();
		gateID = -1;
		
		gridX = newGridX;
		gridY = newGridY;

		rotation = body.getAngle() * Data.RADDEG;
		
		sensorColor = Color.WHITE;
		
		// These numbers are calculated based on the texture dimensions of the associated art asset.
		// The relationship is: 1.0f in world space is 96x96 in pixels (see art asset to get dimensions)
		// By default, half-width and half-height are calculated based on the base image's dimensions
		// when rotated/aligned horizontally.
		//halfWidth = -0.5f;
		//halfHeight = -0.104166667f;
	}
	
	/*
	 * Creates a new Gate object
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
	public Gate(Animation newLowAnimation, Animation newMidAnimation,
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
		//update this Entity's animation state
		if (!closed)
		{
			lowStateTime += deltaTime;
			midStateTime += deltaTime;
			highStateTime += deltaTime;
		}
	}
	
	/*
	 * Gets whether or not this Gate is closed
	 * 
	 * @return						If closed, true
	 */
	public boolean isClosed()
	{
		return closed;
	}
	
	/*
	 * Adds a Sensor to this gate's Array of Sensors
	 * 
	 * @param s						The Sensor to add
	 */
	public void addSensor(Sensor s)
	{
		sensors.add(s);
	}
	
	/*
	 * Gets the Array of Sensors for this Gate
	 * 
	 * @return 						This Gate's Sensors
	 */
	public Array<Sensor> gSensors()
	{
		return sensors;
	}
	
	/*
	 * Gets the ID of this Gate
	 * 
	 * @return						This Gate's ID
	 */
	public short gID()
	{
		return gateID;
	}
	
	/*
	 * Sets the ID of this Gate
	 * 
	 * @param GID					This Gate's new ID
	 */
	public void sID(short newID)
	{
		gateID = newID;
	}
	
	/*
	 * Tests if this Gate has all of its Sensors activated
	 * 
	 * @return						Whether or not the Gate opened
	 */
	public boolean test()
	{
		for(Sensor sensor : sensors)
		{
			if(sensor.getContacts() == 0)
			{
				return false;
			}
		}
		open();
		return true;
	}
	
	/*
	 * Opens this Gate
	 */
	private void open()
	{
		closed = false;
		body.getFixtureList().get(0).setSensor(true);
		
		for(Sensor sensor : sensors)
		{
			sensor.setOpenGateState();
		}
	}
	
	/*
	 * Determines the first class type in a collision
	 * 
	 * @param g						The first entity colliding
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
		p.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param p						The first entity colliding
	 */
	public void collide(Prisoner p)
	{
		p.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param rc						The first entity colliding
	 */
	public void collide(RookieCop rc)
	{
		rc.collide(this);
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		rotation = body.getAngle() * Data.RADDEG;
		lh.addEntityToGridLayer(gridX, gridY, this, LayerHandler.ground);
		lh.addEntityToGridLayer(gridX, gridY, this, LayerHandler.fenceHigh);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromGridLayer(gridX, gridY, this, LayerHandler.ground);
		lh.removeEntityFromGridLayer(gridX, gridY, this, LayerHandler.fenceHigh);
	}
} // End class
