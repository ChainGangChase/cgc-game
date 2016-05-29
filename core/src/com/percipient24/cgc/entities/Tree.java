/*
 * @(#)Tree.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.Camera;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.entities.boss.Tank;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.players.RookieCop;
import com.percipient24.cgc.entities.projectiles.TankShell;
import com.percipient24.cgc.screens.Options;
import com.percipient24.cgc.SoundManager;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a tree entity
 * 
 * @version 0.2 14/2/4
 * @author Christopher Rider
 * @author Clayton Andrews
 * @author William Ziegler
 */
public class Tree extends GameEntity
{
	private float bottomRotation;
	private float midRotation;
	private float topRotation;
	
	private boolean wiggling = false;
	private boolean wigglePositive = true;
	private int wiggleDirection = 0;
	private float wiggleTotalReps = 2;
	private float wiggleCurrentReps = 0;
	private float wiggleModifier = 0;
	private float wiggleRate = 0.1f;
	private float wiggleAmplitude;
	private final float wiggleMaxAmplitude = .5f;
	private final float wiggleDecay = .75f;
	private final float wiggleMinRate = .01f;
	
	/*
	 * Creates a new Tree object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param newGridX				The grid X-position of this object
	 * @param newGridY				The grid Y-position of this object
	 */
	public Tree(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, int newGridX, int newGridY)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		parallaxDistMod = 4.0f;
		gridX = newGridX;
		gridY = newGridY;
		calculateRandomRotations();
	}
	
	/*
	 * Creates a new Tree object
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
	public Tree(Animation newLowAnimation, Animation newMidAnimation,
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
		lowStateTime += deltaTime;
		
		if (wiggling)
		{
			if (wigglePositive)
			{
				if (wiggleModifier < 0 && wiggleModifier >= -wiggleRate)
				{
					wiggleModifier = 0;
					wiggleCurrentReps++;
				}
				else
				{
					wiggleRate = Math.max(wiggleMinRate, Math.abs(wiggleAmplitude - wiggleModifier) * .25f);
					wiggleModifier += wiggleRate;
				}
				if (wiggleCurrentReps == wiggleTotalReps)
				{
					wiggleCurrentReps = 0;
					setTransformMod(0, 0);
					setScale(1.0f, 1.0f);
					wiggling = false;
				}
				else if (wiggleModifier >= wiggleAmplitude)
				{
					wiggleAmplitude *= wiggleDecay;
					wigglePositive = false;
				}
			}
			else
			{
				wiggleRate = Math.max(wiggleMinRate, Math.abs(-wiggleAmplitude - wiggleModifier) * .25f);
				wiggleModifier -= wiggleRate;
				if (wiggleModifier <= -wiggleAmplitude)
				{
					wiggleAmplitude *= wiggleDecay;
					wigglePositive = true;
				}
			}
			
			calculateWiggle(layer);
		}
	}
	
	/*
	 * Splits the Tree's wiggleModifier into X and Y components
	 * 
	 * @param layer					The layer of animation to wiggle
	 */
	private void calculateWiggle(int layer)
	{
		float wiggleDiag;
		
		int divideMod;
		switch (layer)
		{
			case LayerHandler.LOW:
				divideMod = 8;
				break;
			case LayerHandler.MID:
				divideMod = 4;
				break;
			case LayerHandler.HIGH:
				divideMod = 2;
				break;
			default:
				divideMod = 0;
				break;
		}
		
		switch(wiggleDirection)
		{
			case 1:
				setTransformMod(getTransformMod().x, wiggleModifier / divideMod);
				break;
			case 2:
				wiggleDiag = wiggleModifier / (float)Math.sqrt(2);
				setTransformMod(wiggleDiag / divideMod, wiggleDiag / divideMod);
				break;
			case 3:
				setTransformMod(wiggleModifier / divideMod, getTransformMod().y);
				break;
			case 4:
				wiggleDiag = wiggleModifier / (float)Math.sqrt(2);
				setTransformMod(wiggleDiag / divideMod, -wiggleDiag / divideMod);
				break;
			case 5:
				setTransformMod(getTransformMod().x, -wiggleModifier / divideMod);
				break;
			case 6:
				wiggleDiag = wiggleModifier / (float)Math.sqrt(2);
				setTransformMod(-wiggleDiag / divideMod, -wiggleDiag / divideMod);
				break;
			case 7:
				setTransformMod(-wiggleModifier / divideMod, getTransformMod().y);
				break;
			case 8:
				wiggleDiag = wiggleModifier / (float)Math.sqrt(2);
				setTransformMod(-wiggleDiag / divideMod, wiggleDiag / divideMod);
				break;
			default:
				break;
		}
	}
	
	/*
	 * Gives this Tree random rotation angles for each segment
	 */
	private void calculateRandomRotations()
	{
		bottomRotation = CGCWorld.getRandom().nextFloat() * 360;
		midRotation = CGCWorld.getRandom().nextFloat() * 360;
		topRotation = CGCWorld.getRandom().nextFloat() * 360;
		
		float tempHeight = lowImageHalfHeight;
		float tempWidth = lowImageHalfWidth;
		
		float rot = bottomRotation * Data.DEGRAD;
		lowImageHalfWidth = tempWidth * (float) Math.cos(rot) - tempHeight * (float) Math.sin(rot);
		lowImageHalfHeight = tempWidth * (float) Math.sin(rot) + tempHeight * (float) Math.cos(rot);
		
		tempHeight = midImageHalfHeight;
		tempWidth = midImageHalfWidth;
		
		rot = midRotation * Data.DEGRAD;
		midImageHalfWidth = tempWidth * (float) Math.cos(rot) - tempHeight * (float) Math.sin(rot);
		midImageHalfHeight = tempWidth * (float) Math.sin(rot) + tempHeight * (float) Math.cos(rot);
		
		tempHeight = highImageHalfHeight;
		tempWidth = highImageHalfWidth;
		
		rot = topRotation * Data.DEGRAD;
		highImageHalfWidth = tempWidth * (float) Math.cos(rot) - tempHeight * (float) Math.sin(rot);
		highImageHalfHeight = tempWidth * (float) Math.sin(rot) + tempHeight * (float) Math.cos(rot);
	}
	
	/*
	 * Makes this Tree wiggle when it gets punched
	 * 
	 * @param direction				The direction to wiggle
	 */
	public void punched(int direction)
	{
		if (!wiggling)
		{
			wiggling = true;
			wiggleRate = wiggleMinRate;
			wiggleAmplitude = wiggleMaxAmplitude;
			wiggleDirection = direction;
			SoundManager.playSound("punch tree", false);
		}
		
		if (Options.storedTrackingOption)
		{
			ChaseApp.stats.getGame().treesPunched++;
		}
	}
	
	/*
	 * Draws this GameEntity in the CGCWorld
	 * 
	 * @param sBatch				The SpriteBatch the game is using
	 * @param delta					Seconds elapsed since the last frame
	 * @param layerNumber			Which BodyFactory layer to draw
	 */
	public void draw(SpriteBatch sBatch, float delta, int layerNumber) 
	{
		Vector2 relativeScreenPosition = CGCWorld.getRelativeScreenPosition(this);
		
		if (relativeScreenPosition.x != Float.MAX_VALUE
				&& relativeScreenPosition.y != Float.MAX_VALUE)
		{
			// Handle alpha
			Color colorForAlpha = sBatch.getColor();
			colorForAlpha.a = getAlpha();
			sBatch.setColor(colorForAlpha);
			
			TextureRegion frame;
			
			if (layerNumber <= LayerHandler.LOW)
			{
				if (!CGCWorld.isPaused())
				{
					step(delta, LayerHandler.LOW);
				}
				frame = getLowRegion();
			}
			else if (layerNumber <= LayerHandler.MID)
			{
				if (!CGCWorld.isPaused())
				{
					step(delta, LayerHandler.MID);
				}
				frame = getMidRegion();
			}
			else
			{
				if (!CGCWorld.isPaused())
				{
					step(delta, LayerHandler.HIGH);
				}
				frame = getHighRegion();
			}
			
			// Position and Rotation
			Vector2 pos = body.getPosition();
			float Xmod = 0.0f;
			float Ymod = 0.0f;
			
			float baseX = 0.0f;
			float baseY = 0.0f;
			
			baseX = pos.x + getTransformMod().x;
			baseY = pos.y + getTransformMod().y;
				
			if (layerNumber <= LayerHandler.LOW)
			{
				if (Options.storedParallaxOption)
				{
					sBatch.draw(frame, baseX, baseY, 
							getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
							frame.getRegionWidth(), frame.getRegionHeight(), 
							CGCWorld.getCamera().zoom, CGCWorld.getCamera().zoom, 
							getRotation(LayerHandler.LOW));
				}
				else
				{
					frame = AnimationManager.treeAnims[3].getKeyFrame(0);
					sBatch.draw(frame, baseX, baseY, 
							getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
							frame.getRegionWidth(), frame.getRegionHeight(), 
							CGCWorld.getCamera().zoom, CGCWorld.getCamera().zoom,
							getRotation(LayerHandler.LOW));
				}
			}
			else if (layerNumber <= LayerHandler.MID)
			{
				if (Options.storedParallaxOption)
				{
					Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / getParallaxDistMod();
					Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / getParallaxDistMod();
					
					sBatch.draw(frame, baseX + Xmod, baseY + Ymod, 
							getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
							frame.getRegionWidth(), frame.getRegionHeight(), 
							CGCWorld.getCamera().zoom, CGCWorld.getCamera().zoom, 
							getRotation(LayerHandler.MID));
				}
			}
			else
			{
				if (Options.storedParallaxOption)
				{
					Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / (getParallaxDistMod() / 2);
					Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / (getParallaxDistMod() / 2);
					
					sBatch.draw(frame, baseX + Xmod, baseY + Ymod, 
							getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
							frame.getRegionWidth(), frame.getRegionHeight(), 
							CGCWorld.getCamera().zoom, CGCWorld.getCamera().zoom, 
							getRotation(LayerHandler.HIGH));
				}
			}
			
			colorForAlpha = sBatch.getColor();
			colorForAlpha.a = 1.0f;
			sBatch.setColor(colorForAlpha);
		}
	}
	
	/*
	 * Determines the first class in a collision
	 * 
	 * @param ge					The first entity colliding
	 */
	public void collide(GameEntity ge)
	{
		ge.collide(this);
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
	 * @param rc					The first entity colliding
	 */
	public void collide(RookieCop rc)
	{
		rc.collide(this);
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
	 * @param ts					The second entity colliding
	 */
	public void collide(TankShell ts)
	{
		ts.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param e						The second entity colliding
	 */
	public void collide(Explosion e)
	{
		CGCWorld.addToDestroyList(this);
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToGridLayer(gridX, gridY, this, LayerHandler.ground);
		lh.addEntityToGridLayer(gridX, gridY, this, LayerHandler.mid);
		lh.addEntityToGridLayer(gridX, gridY, this, LayerHandler.high);
	}
	
	/*
	 * Gets the rotation of this Tree
	 * 
	 * @param layer					The image layer this rotation is for
	 * @return						The rotation of the layer in the Tree
	 */
	public float getRotation(int layer)
	{
		if (layer <= LayerHandler.LOW)
		{
			return bottomRotation;
		}
		else if (layer <= LayerHandler.MID)
		{
			return midRotation;
		}
		else
		{
			return topRotation;
		}
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromGridLayer(gridX, gridY, this, LayerHandler.ground);
		lh.removeEntityFromGridLayer(gridX, gridY, this, LayerHandler.mid);
		lh.removeEntityFromGridLayer(gridX, gridY, this, LayerHandler.high);
	}
} // End class