/*
 * @(#)Mud.java		0.2 14/2/19
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities.terrain;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a mud entity
 * 
 * @version 0.2 14/2/19
 * @author Christopher Rider
 */
public class Mud extends Terrain 
{
	public static float speedMod = 0.1f;
	
	private float topRightTime = 0.0f;
	private float botRightTime = 0.0f;
	private float botLeftTime = 0.0f;
	private float topLeftTime = 0.0f;
	
	/*
	 * Creates a new Mud object
	 *
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param tr					The version of mud the top right should use
	 * @param br					The version of mud the bottom right should use
	 * @param bl					The version of mud the bottom left should use
	 * @param tl					The version of mud the top left should use
	 */
	public Mud(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, 
			EntityType pEntityType, Body attachedBody,
			int tr, int br, int bl, int tl)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody,
				tr, br, bl, tl);
		
		topRightTime = CGCWorld.getRandom().nextFloat() * (3 * AnimationManager.TERRAIN_ANIM_FRAME_TIME);
		botRightTime = CGCWorld.getRandom().nextFloat() * (3 * AnimationManager.TERRAIN_ANIM_FRAME_TIME);
		botLeftTime = CGCWorld.getRandom().nextFloat() * (3 * AnimationManager.TERRAIN_ANIM_FRAME_TIME);
		topLeftTime = CGCWorld.getRandom().nextFloat() * (3 * AnimationManager.TERRAIN_ANIM_FRAME_TIME);
	}
	
	/*
	 * Timestep-based update method
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The BodyFactory layer to animate
	 */
	public void step(float deltaTime, int layer) 
	{
		if (topRightTime > 4 * AnimationManager.TERRAIN_ANIM_FRAME_TIME)
		{
			topRightTime = 0;
		}
		//topRightTime += deltaTime;
		
		if (botRightTime > 4 * AnimationManager.TERRAIN_ANIM_FRAME_TIME)
		{
			botRightTime = 0;
		}
		//botRightTime += deltaTime;
		
		if (botLeftTime > 4 * AnimationManager.TERRAIN_ANIM_FRAME_TIME)
		{
			botLeftTime = 0;
		}
		//botLeftTime += deltaTime;
		
		if (topLeftTime > 4 * AnimationManager.TERRAIN_ANIM_FRAME_TIME)
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
	 * @param mudType				The type of mud this corner is
	 * @param corner				Which corner this tile is (0 - top right, goes clockwise)
	 * @return						The Texture for this mud tile
	 */
	public TextureRegion getLowAnim(int mudType, int corner)
	{
		switch (corner)
		{
			case 0: return AnimationManager.mudAnims[mudType].getKeyFrame(topRightTime);
			case 1: return AnimationManager.mudAnims[mudType].getKeyFrame(botRightTime);
			case 2: return AnimationManager.mudAnims[mudType].getKeyFrame(botLeftTime);
			default: return AnimationManager.mudAnims[mudType].getKeyFrame(topLeftTime);
		}
	}
} // End class