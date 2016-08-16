/*
 * @(#)Water.java		0.2 14/2/19
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities.terrain;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.cgc.art.TextureAnimationDrawer;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a water entity
 * 
 * @version 0.2 14/2/19
 * @author Christopher Rider
 */
public class Water extends Terrain 
{
	public static float speedMod = 0.05f;
	private int direction = 1; // Right
	public static float forceAmount = 2.0f;
	
	private float topRightTime = 0.0f;
	private float botRightTime = 0.0f;
	private float botLeftTime = 0.0f;
	private float topLeftTime = 0.0f;
	private static float currentTime = 0.0f;
	public static final float CURRENT_FRAME_TIME = 0.14285714f;
	
	/*
	 * Creates a new Water object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param dir					The direction the water is flowing, 0 is none
	 * @param tr					The version of water the top right should use
	 * @param br					The version of water the bottom right should use
	 * @param bl					The version of water the bottom left should use
	 * @param tl					The version of water the top left should use
	 */
	public Water(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody,
			int dir, int tr, int br, int bl, int tl)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody,
				tr, br, bl, tl);
		
		direction = dir;
		topRightTime = CGCWorld.getRandom().nextFloat() * (3 * TextureAnimationDrawer.TERRAIN_ANIM_FRAME_TIME);
		botRightTime = CGCWorld.getRandom().nextFloat() * (3 * TextureAnimationDrawer.TERRAIN_ANIM_FRAME_TIME);
		botLeftTime = CGCWorld.getRandom().nextFloat() * (3 * TextureAnimationDrawer.TERRAIN_ANIM_FRAME_TIME);
		topLeftTime = CGCWorld.getRandom().nextFloat() * (3 * TextureAnimationDrawer.TERRAIN_ANIM_FRAME_TIME);
	}
	
	/*
	 * Creates a new Water object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param dir					The direction the water is flowing, 0 is none
	 * @param tr					The version of water the top right should use
	 * @param br					The version of water the bottom right should use
	 * @param bl					The version of water the bottom left should use
	 * @param tl					The version of water the top left should use
	 * @param startAlpha			The starting alpha for this entity
	 */
	public Water(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody,
			int dir, int tr, int br, int bl, int tl,
			float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, dir,
				tr, br, bl, tl);
		alpha = startAlpha;
	}
	
	/*
	 * Gets the direction this water is flowing
	 * 
	 * @return						The water's direction
	 */
	public int getDirection()
	{
		return direction;
	}
	
	public void setDirection(int newDir)
	{
		if (newDir < 0 || newDir > 8)
		{
			newDir = 0;
		}
		
		direction = newDir;
	}
	
	/*
	 * Timestep-based update method
	 * 
	 * @see com.percipient24.cgc.entities.terrain.Terrain#step(float, int)
	 */
	public void step(float deltaTime, int layer) 
	{
		if (topRightTime > 4 * TextureAnimationDrawer.TERRAIN_ANIM_FRAME_TIME)
		{
			topRightTime = 0;
		}
		//topRightTime += deltaTime;
		
		if (botRightTime > 4 * TextureAnimationDrawer.TERRAIN_ANIM_FRAME_TIME)
		{
			botRightTime = 0;
		}
		//botRightTime += deltaTime;
		
		if (botLeftTime > 4 * TextureAnimationDrawer.TERRAIN_ANIM_FRAME_TIME)
		{
			botLeftTime = 0;
		}
		//botLeftTime += deltaTime;
		
		if (topLeftTime > 4 * TextureAnimationDrawer.TERRAIN_ANIM_FRAME_TIME)
		{
			topLeftTime = 0;
		}
		//topLeftTime += deltaTime;
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
	
	/*
	 * Gets the animation for this tile's corner
	 * 
	 * @param waterType				The type of water this corner is
	 * @param corner				Which corner this tile is (0 - top right, goes clockwise)
	 */
	public TextureRegion getLowAnim(int waterType, int corner)
	{
		switch (corner)
		{
			case 0: return TextureAnimationDrawer.waterAnims[waterType].getKeyFrame(topRightTime);
			case 1: return TextureAnimationDrawer.waterAnims[waterType].getKeyFrame(botRightTime);
			case 2: return TextureAnimationDrawer.waterAnims[waterType].getKeyFrame(botLeftTime);
			default: return TextureAnimationDrawer.waterAnims[waterType].getKeyFrame(topLeftTime);
		}
	}
	
	/*
	 * Updates the animation state time of this Water's current
	 * 
	 * @param dt					Seconds elapsed since last frame
	 */
	public static void updateCurrent(float dt)
	{
		if (currentTime > 7 * CURRENT_FRAME_TIME)
		{
			currentTime = 0;
		}
		currentTime += dt;
	}
	
	/*
	 * Gets the current animation state time for this Water
	 */
	public static float getCurrentTime()
	{
		return currentTime;
	}
} // End class