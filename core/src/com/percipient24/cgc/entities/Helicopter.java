/*
 * @(#)Helicopter.java		0.2 14/3/10
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.Camera;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.screens.Options;
import com.percipient24.enums.EntityType;

/*
 * A helicopter unit for dropping in rookie cops
 * @version 0.2 14/3/10
 * @author JD Kelly
 */
public class Helicopter extends GameEntity {

	private boolean returnTrip = false;
	private boolean stopped = false;
	
	//Variables that control the helicopter's hovering and exiting
	private CGCTimer hoverClock;
	private Timer.Task hoverTask;
	private float hoverTime = 2.0f;
	
	private float midX = 0.0f;
	private CGCWorld gameWorld;
	
	/*
	 * Creates a new Helicopter object
	 * 
	 * @param theWorld				The world this helicopter is in
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param entityType			The type of entity this object is
	 * @param body					The Body object that represents this Helicopter in the world
	 */
	public Helicopter(CGCWorld theWorld, Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType entityType, Body body)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, entityType, body);
		gameWorld = theWorld;
		Gdx.app.log("In the pipe", "five by five");
		rotation = 90;
		
		//SoundManager.playSound("helicopter", false);
		
		hoverTask = new Timer.Task() {
			
			public void run() 
			{
				stopped = false;
				returnTrip = true;
				getBody().setLinearVelocity(new Vector2(4,0));
			}
		};
		
		hoverClock = new CGCTimer(hoverTask, hoverTime, false, "hoverClock");
		body.setLinearVelocity(new Vector2(4, 0));
	}
	
	/*
	 * Timestep-based update method
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The parallax layer to animate
	 */
	public void step(float deltaTime, int layer) 
	{
		if(CGCWorld.getNumCops() > 0)
		{
			midX = CGCWorld.getCopAverageVec().cpy().x;
		}
		else
		{
			midX = CGCWorld.getPrisonerAverageVec().cpy().x;
		}
		
		midX += getHighRegion().getRegionHeight() * Data.SCREEN_TO_BOX;
		if (highStateTime > 0.05f)
		{
			highStateTime = 0;
		}
		highStateTime += deltaTime;
		
		if (!returnTrip && !stopped)
		{
			if(body.getPosition().x >= midX)
			{
				if(!TimerManager.contains(hoverClock))
				{
					body.setLinearVelocity(Vector2.Zero);
					gameWorld.spawnCops();
					TimerManager.addTimer(hoverClock);
				}
			}
		}
		else if (returnTrip)
		{
			if(body.getPosition().x >= 22.0f + getHighRegion().getRegionHeight() * Data.SCREEN_TO_BOX)
			{
				Gdx.app.log("From Beyond", "To Heaven");
				CGCWorld.addToDestroyList(this);
			}
		}
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.aerial);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.aerial);
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
		
		if (isScaled())
		{
			if (layerNumber <= LayerHandler.LOW)
			{
				sBatch.draw(frame, baseX, baseY, 
						getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
						frame.getRegionWidth(), frame.getRegionHeight(), 
						CGCWorld.getCamera().zoom * getScale().x, 
						CGCWorld.getCamera().zoom * getScale().y, 
						getRotation(LayerHandler.LOW));
			}
			else if (layerNumber <= LayerHandler.MID)
			{
				if (Options.storedParallaxOption)
				{
					Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / getParallaxDistMod();
					Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / getParallaxDistMod();
				}
				
				sBatch.draw(frame, baseX + Xmod, baseY + Ymod, 
						getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
						frame.getRegionWidth(), frame.getRegionHeight(), 
						CGCWorld.getCamera().zoom * getScale().x, 
						CGCWorld.getCamera().zoom * getScale().y, 
						getRotation(LayerHandler.MID));
			}
			else
			{
				if (Options.storedParallaxOption)
				{
					Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / (getParallaxDistMod() / 2);
					Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / (getParallaxDistMod() / 2);
				}
				
				sBatch.draw(frame, baseX + Xmod, baseY + Ymod, 
						getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
						frame.getRegionWidth(), frame.getRegionHeight(), 
						CGCWorld.getCamera().zoom * getScale().x, 
						CGCWorld.getCamera().zoom * getScale().y, 
						getRotation(LayerHandler.HIGH));
			}
		}
		else // Not scaled
		{
			if (layerNumber <= LayerHandler.LOW)
			{
				sBatch.draw(frame, baseX, baseY, 
						getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
						frame.getRegionWidth(), frame.getRegionHeight(), 
						CGCWorld.getCamera().zoom, CGCWorld.getCamera().zoom, 
						getRotation(LayerHandler.LOW));
			}
			else if (layerNumber <= LayerHandler.MID)
			{
				if (Options.storedParallaxOption)
				{
					Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / getParallaxDistMod();
					Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / getParallaxDistMod();
				}
				
				sBatch.draw(frame, baseX + Xmod, baseY + Ymod, 
						getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
						frame.getRegionWidth(), frame.getRegionHeight(), 
						CGCWorld.getCamera().zoom, CGCWorld.getCamera().zoom, 
						getRotation(LayerHandler.MID));
			}
			else
			{
				if (Options.storedParallaxOption)
				{
					Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / (getParallaxDistMod() / 2);
					Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / (getParallaxDistMod() / 2);
				}
				
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
} // End class