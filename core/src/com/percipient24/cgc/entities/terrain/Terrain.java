/*
 * @(#)Terrain.java		0.3 14/4/17
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities.terrain;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.b2dhelpers.BodyFactory;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.enums.EntityType;

/*
 * Holds basic logic for terrain
 * 
 * @version 0.3 14/4/17
 * @author Christopher Rider
 */
public abstract class Terrain extends GameEntity 
{
	protected int topRight;
	protected int topLeft;
	protected int bottomRight;
	protected int bottomLeft;
	
	/*
	 * Creates a new Terrain object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param tr					The version of this terrain the top right should use
	 * @param br					The version of this terrain the bottom right should use
	 * @param bl					The version of this terrain the bottom left should use
	 * @param tl					The version of this terrain the top left should use
	 */
	public Terrain(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody,
			int tr, int br, int bl, int tl)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		topRight = 0;
		topLeft = 0;
		bottomRight = 0;
		bottomLeft = 0;
		
		calcImageDirs(tr, br, bl, tl);
	}
	
	/*
	 * Creates a new Terrain object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param tr					The version of this terrain the top right should use
	 * @param br					The version of this terrain the bottom right should use
	 * @param bl					The version of this terrain the bottom left should use
	 * @param tl					The version of this terrain the top left should use
	 * @param startAlpha			The starting alpha for this entity
	 */
	public Terrain(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody,
			int tr, int br, int bl, int tl,
			float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody,
				tr, br, bl, tl);
		alpha = startAlpha;
	}
	
	/*
	 * Gets the image ID for the top right quadrant
	 * 
	 * @return						The image ID for the top right quadrant
	 */
	public int getTopRight()
	{
		return topRight;
	}
	
	/*
	 * Sets the image ID for the top right quadrant
	 * 
	 * @param newTR					The image ID for the top right quadrant
	 */
	public void setTopRight(int newTR)
	{
		switch(newTR)
		{
			case 0: 
			case 2:topRight = 9;
				break;
			case 1: 
			case 3: topRight = 3;
				break;
			case 4:
			case 6: topRight = 1;
				break;
			case 5: topRight = 2;
				break;
			case 7: topRight = 0;
				break;
			default: topRight = 0;
				break;
		}
	}
	
	/*
	 * Gets the image ID for the top left quadrant
	 * 
	 * @return						The image ID for the top left quadrant
	 */
	public int getTopLeft()
	{
		return topLeft;
	}
	
	/*
	 * Sets the image ID for the top left quadrant
	 * 
	 * @param newTL					The image ID for the top left quadrant
	 */
	public void setTopLeft(int newTL)
	{
		switch(newTL)
		{
			case 0: 
			case 2:topLeft = 10;
				break;
			case 1: 
			case 3: topLeft = 5;
				break;
			case 4:
			case 6: topLeft = 3;
				break;
			case 5: topLeft = 4;
				break;
			case 7: topLeft = 0;
				break;
			default: topLeft = 0;
				break;
		}
	}
	
	/*
	 * Gets the image ID for the bottom right quadrant
	 * 
	 * @return						The image ID for the bottom right quadrant
	 */
	public int getBotRight()
	{
		return bottomRight;
	}
	
	/*
	 * Sets the image ID for the bottom right quadrant
	 * 
	 * @param newBR					The image ID for the bottom right quadrant
	 */
	public void setBotRight(int newBR)
	{
		switch(newBR)
		{
			case 0: 
			case 2:bottomRight = 12;
				break;
			case 1: 
			case 3: bottomRight = 1;
				break;
			case 4:
			case 6: bottomRight = 7;
				break;
			case 5: bottomRight = 8;
				break;
			case 7: bottomRight = 0;
				break;
			default: bottomRight = 0;
				break;
		}
	}
	
	/*
	 * Gets the image ID for the bottom left quadrant
	 * 
	 * @return						The image ID for the bottom left quadrant
	 */
	public int getBotLeft()
	{
		return bottomLeft;
	}
	
	/*
	 * Sets the image ID for the bottom left quadrant
	 * 
	 * @param newBL					The image ID for the bottom left quadrant
	 */
	public void setBotLeft(int newBL)
	{
		switch(newBL)
		{
			case 0: 
			case 2:bottomLeft = 11;
				break;
			case 1: 
			case 3: bottomLeft = 7;
				break;
			case 4:
			case 6: bottomLeft = 5;
				break;
			case 5: bottomLeft = 6;
				break;
			case 7: bottomLeft = 0;
				break;
			default: bottomLeft = 0;
				break;
		}
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
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(BodyFactory bf) 
	{
		
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(BodyFactory bf) 
	{

	}
	
	/*
	 * Determines image directions
	 * 
	 * @param tr					The editor value for the top-right corner type
	 * @param br					The editor value for the bot-right corner type
	 * @param bl					The editor value for the bot-left corner type
	 * @param tl					The editor value for the top-left corner type
	 */
	private void calcImageDirs(int tr, int br, int bl, int tl)
	{
		switch(tr)
		{
			case 0: 
			case 2:topRight = 9;
				break;
			case 1: 
			case 3: topRight = 3;
				break;
			case 4:
			case 6: topRight = 1;
				break;
			case 5: topRight = 2;
				break;
			case 7: topRight = 0;
				break;
			default: topRight = 0;
				break;
		}
		
		switch(br)
		{
			case 0: 
			case 2:bottomRight = 12;
				break;
			case 1: 
			case 3: bottomRight = 1;
				break;
			case 4:
			case 6: bottomRight = 7;
				break;
			case 5: bottomRight = 8;
				break;
			case 7: bottomRight = 0;
				break;
			default: bottomRight = 0;
				break;
		}
		
		switch(bl)
		{
			case 0: 
			case 2:bottomLeft = 11;
				break;
			case 1: 
			case 3: bottomLeft = 7;
				break;
			case 4:
			case 6: bottomLeft = 5;
				break;
			case 5: bottomLeft = 6;
				break;
			case 7: bottomLeft = 0;
				break;
			default: bottomLeft = 0;
				break;
		}
		
		switch(tl)
		{
			case 0: 
			case 2:topLeft = 10;
				break;
			case 1: 
			case 3: topLeft = 5;
				break;
			case 4:
			case 6: topLeft = 3;
				break;
			case 5: topLeft = 4;
				break;
			case 7: topLeft = 0;
				break;
			default: topLeft = 0;
				break;
		}
	}
} // End class