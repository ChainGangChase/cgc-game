/*
 * @(#)GameEntity.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.Camera;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.entities.boss.PallBearer;
import com.percipient24.cgc.entities.boss.Sheriff;
import com.percipient24.cgc.entities.boss.SteelHorse;
import com.percipient24.cgc.entities.boss.Tank;
import com.percipient24.cgc.entities.players.CarrierCop;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.players.RookieCop;
import com.percipient24.cgc.entities.projectiles.Projectile;
import com.percipient24.cgc.entities.projectiles.TankShell;
import com.percipient24.cgc.screens.Options;
import com.percipient24.enums.EntityType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/*
 * Handles the logic for a game entity
 * 
 * @version 0.2 14/2/4
 * @author Christopher Rider
 * @author Clayton Andrews
 */
public abstract class GameEntity
{
	protected Animation lowAnimation;
	protected Animation midAnimation;
	protected Animation highAnimation;
	protected float lowStateTime;
	protected float midStateTime;
	protected float highStateTime;
	protected EntityType entityType;
	protected boolean destroy = false;
	protected float alpha;
	protected Vector2 scaling;
	protected boolean scaled;
	protected Body body;
	//protected float halfWidth = -0.5f;
	//protected float halfHeight = -0.5f;
	//protected float initHalfWidth;
	//protected float initHalfHeight;
	protected float parallaxDistMod = 8.0f;
	protected float rotation = 0.0f; // In degrees
	private Vector3 transformMod;
	protected int gridX = -1;
	protected int gridY = -1;
	
	protected float lowImageHalfHeight = -0.5f;
	protected float lowImageHalfWidth = -0.5f;
	protected float midImageHalfHeight = -0.5f;
	protected float midImageHalfWidth = -0.5f;
	protected float highImageHalfHeight = -0.5f;
	protected float highImageHalfWidth = -0.5f;
	private float lowInitImageHalfHeight = lowImageHalfHeight;
	private float lowInitImageHalfWidth = lowImageHalfWidth;
	private float midInitImageHalfHeight = midImageHalfHeight;
	private float midInitImageHalfWidth = midImageHalfWidth;
	private float highInitImageHalfHeight = highImageHalfHeight;
	private float highInitImageHalfWidth = highImageHalfWidth;
	
	/*
	 * Creates a new GameEntity object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 */
	public GameEntity(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody)
	{
		lowAnimation = newLowAnimation;
		midAnimation = newMidAnimation;
		highAnimation = newHighAnimation;
		entityType = pEntityType;
		alpha = 1.0f;
		lowStateTime = 0.0f;
		midStateTime = 0.0f;
		highStateTime = 0.0f;
		recalcHalfDimensions();
		scaled = false;
		body = attachedBody;

		transformMod = new Vector3(0, 0, 1);
		
		// These numbers are calculated based on the texture dimensions of the associated art asset.
		// The relationship is: 1.0f in world space is 96x96 in pixels (see art asset to get dimensions)
		// By default, half-width and half-height are calculated based on the base image's dimensions
		// when rotated/aligned horizontally.
		scaling = new Vector2(1.0f, 1.0f);
	}
	
	/*
	 * Creates a new GameEntity object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param startAlpha			The starting alpha for this entity
	 */
	public GameEntity(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		alpha = startAlpha;
	}
	
	private void recalcHalfDimensions()
	{
		if (lowAnimation != null)
		{
			lowImageHalfHeight = (lowAnimation.getKeyFrame(0).getRegionHeight()*Data.SCREEN_TO_BOX)/-2;
			lowImageHalfWidth = (lowAnimation.getKeyFrame(0).getRegionWidth()*Data.SCREEN_TO_BOX)/-2;
		}
		if (midAnimation != null)
		{
			midImageHalfHeight = (midAnimation.getKeyFrame(0).getRegionHeight()*Data.SCREEN_TO_BOX)/-2;
			midImageHalfWidth = (midAnimation.getKeyFrame(0).getRegionWidth()*Data.SCREEN_TO_BOX)/-2;
		}
		if (highAnimation != null)
		{
			highImageHalfHeight = (highAnimation.getKeyFrame(0).getRegionHeight()*Data.SCREEN_TO_BOX)/-2;
			highImageHalfWidth = (highAnimation.getKeyFrame(0).getRegionWidth()*Data.SCREEN_TO_BOX)/-2;
		}
	}
	
	/*
	 * Gets the mod for parallax distance
	 * 
	 * @return						The mod for parallax distance
	 */
	public float getParallaxDistMod()
	{
		return parallaxDistMod;
	}
	
	/*
	 * Gets the rotation
	 * 
	 * @param height				The image layer this rotation is for - not used in GameEntity
	 * @return						The rotation
	 */
	public float getRotation(int height)
	{
		return rotation;
	}
	
	/*
	 * Sets the rotation
	 * 
	 * @param newRotation			The new rotation for this entity
	 */
	public void setRotation(float newRotation)
	{
		rotation = newRotation;
	}

	/*
	 * Gets the modifier for this GameEntity's transform
	 * 
	 * @return						The modifier for the GameEntity's transform
	 */
	public Vector3 getTransformMod()
	{
		return transformMod;
	}
	
	/*
	 * Sets the modifier for this GameEntity's transform
	 * 
	 * @param x						The new X value for this transform
	 * @param y						The new Y value for this transform
	 */
	public void setTransformMod(float x, float y)
	{
		transformMod.x = x;
		transformMod.y = y;
	}
	
	/*
	 * Gets the half-width of this entity's image
	 * 
	 * @param layer					The layer to get this entitiy's half width for
	 * @return						This entity's current half-width
	 */
	public float getImageHalfWidth(int layer)
	{
		if (layer <= LayerHandler.LOW || this instanceof ChainLink)
		{
			return lowImageHalfWidth;
		}
		else if (layer <= LayerHandler.MID)
		{
			return midImageHalfWidth;
		}
		else
		{
			return highImageHalfWidth;
		}
	}

	/*
	 * Gets the half-height of this entity's image
	 * 
	 * @param layer					The layer to get this entitiy's half height for
	 * @return						This entity's current half-height
	 */
	public float getImageHalfHeight(int layer)
	{
		if (layer <= LayerHandler.LOW || this instanceof ChainLink)
		{
			return lowImageHalfHeight;
		}
		else if (layer <= LayerHandler.MID)
		{
			return  midImageHalfHeight;
		}
		else
		{
			return highImageHalfHeight;
		}
	}
	
	/*
	 * Timestep-based update method
	 */
	public abstract void step(float deltaTime, int layer);
	
	/*
	 * Gets the entity type for this object
	 * 
	 * @return						This entity's type
	 */
	public EntityType getType()
	{
		return entityType;
	}
	
	/*
	 * Check if the entity has any animation assigned to it
	 * 
	 * @return						Returns true if an animation is present, false if there isn't one
	 */
	public boolean hasAnimation()
	{
		if (lowAnimation == null && midAnimation == null && highAnimation == null)
		{
			return false;
		}
		return true;
	}
	
	/*
	 * Sets the entity type for this object
	 * 
	 * @param type					This entity's type
	 */
	public void setType(EntityType type)
	{
		entityType = type;
	}
	
	/*
	 * Gets the alpha for this object
	 * 
	 * @return						This entity's alpha
	 */
	public float getAlpha()
	{
		return alpha;
	}
	
	/*
	 * Sets the alpha for this object
	 * 
	 * @param newAlpha				The new alpha for this entity, must be between 0.0f and 1.0f
	 */
	public void setAlpha(float newAlpha)
	{
		if (newAlpha > 1.0f)
		{
			alpha = 1.0f;
		}
		else if (newAlpha < 0.0f)
		{
			alpha = 0.0f;
		}
		else
		{
			alpha = newAlpha;
		}
	}
	
	/*
	 * Gets the scale for this object
	 * 
	 * @return						This entity's scale
	 */
	public Vector2 getScale()
	{
		return scaling;
	}

	/*
	 * Sets the scale for this object, auto sets scaled
	 * 
	 * @param x						The new scale's x for this entity
	 * @param y						The new scale's y for this entity
	 */
	public void setScale(float x, float y)
	{
		if (!scaled)
		{
			lowInitImageHalfWidth = lowImageHalfWidth;
			lowInitImageHalfHeight = lowImageHalfHeight;
			midInitImageHalfWidth = midImageHalfWidth;
			midInitImageHalfHeight = midImageHalfHeight;
			highInitImageHalfWidth = highImageHalfWidth;
			highInitImageHalfHeight = highImageHalfHeight;
		}

		if (x != 1.0f)
		{
			lowImageHalfWidth = lowInitImageHalfWidth;
			midImageHalfWidth = midInitImageHalfWidth;
			highImageHalfWidth = highInitImageHalfWidth;
			scaling.x = x;
			scaled = true;
			lowImageHalfWidth *= scaling.x;
			midImageHalfWidth *= scaling.x;
			highImageHalfWidth *= scaling.x;
		}
		else
		{
			lowImageHalfWidth = lowInitImageHalfWidth;
			midImageHalfWidth = midInitImageHalfWidth;
			highImageHalfWidth = highInitImageHalfWidth;
			scaling.x = x;
			if (scaling.y == 1.0f)
			{
				scaled = false;
			}
		}
		
		if (y != 1.0f)
		{
			lowImageHalfHeight = lowInitImageHalfHeight;
			midImageHalfHeight = midInitImageHalfHeight;
			highImageHalfHeight = highInitImageHalfHeight;
			scaling.y = y;
			scaled = true;
			lowImageHalfHeight *= scaling.y;
			midImageHalfHeight *= scaling.y;
			highImageHalfHeight *= scaling.y;
		}
		else
		{
			lowImageHalfHeight = lowInitImageHalfHeight;
			midImageHalfHeight = midInitImageHalfHeight;
			highImageHalfHeight = highInitImageHalfHeight;
			scaling.y = y;
			if (scaling.x == 1.0f)
			{
				scaled = false;
			}
		}
	}
	
	/*
	 * Gets the Body this entity is attached to
	 * 
	 * @return						The Body this is attached to
	 */
	public Body getBody()
	{
		return body;
	}
	
	/*
	 * Sets the new player Body - only used for generating new world players
	 * 
	 * @param newBody				The new player's Body
	 */
	public void setBody(Body newBody)
	{
		body = newBody;
	}
	
	/*
	 * Gets whether or not this entity has been scaled
	 * 
	 * @return						If scaled, true
	 */
	public boolean isScaled()
	{
		return scaled;
	}
	
	/*
	 * Gets whether or not this entity is about to be destroyed
	 * 
	 * @return						If this is about to be destroyed, true
	 */
	public boolean toDestroy()
	{
		return destroy;
	}
	
	/*
	 * Gets the X-position of this object in the world grid
	 * 
	 * @return						This object's grid X-position
	 */
	public int getGridX()
	{
		return gridX;
	}
	
	/*
	 * Gets the Y-position of this object in the world grid
	 * 
	 * @return						This object's grid Y-position
	 */
	public int getGridY()
	{
		return gridY;
	}
	
	/*
	 * Add this entity to the correct graphic layers
	 */
	public abstract void addToWorldLayers(LayerHandler lh);
	public abstract void removeFromWorldLayers(LayerHandler lh);
	
	/*
	 * Gets the lower region for parallax drawing
	 */
	public TextureRegion getLowRegion()
	{
		return lowAnimation.getKeyFrame(lowStateTime);
	}
	
	/*
	 * Gets the lower Animation for this GameEntity
	 * 
	 * @return						The lower Animation for this GameEntity
	 */
	public Animation getLowAnim()
	{
		return lowAnimation;
	}
	
	/*
	 * Sets the lower Animation for this GameEntity
	 * 
	 * @param newAnim				The new lower Animation for this GameEntity
	 */
	public void setLowAnim(Animation newAnim)
	{
		lowAnimation = newAnim;
		if (lowAnimation != null)
		{
			lowImageHalfHeight = (lowAnimation.getKeyFrame(0).getRegionHeight()*Data.SCREEN_TO_BOX)/-2;
			lowImageHalfWidth = (lowAnimation.getKeyFrame(0).getRegionWidth()*Data.SCREEN_TO_BOX)/-2;
		}
	}
	
	/*
	 * Gets the middle region for parallax drawing
	 */
	public TextureRegion getMidRegion()
	{
		return midAnimation.getKeyFrame(midStateTime);
	}
	
	/*
	 * Gets the middle Animation for this GameEntity
	 * 
	 * @return						The middle Animation for this GameEntity
	 */
	public Animation getMidAnim()
	{
		return midAnimation;
	}
	
	/*
	 * Sets the middle Animation for this GameEntity
	 * 
	 * @param newAnim				The new middle Animation for this GameEntity
	 */
	public void setMidAnim(Animation newAnim)
	{
		midAnimation = newAnim;
		if (midAnimation != null)
		{
			midImageHalfHeight = (midAnimation.getKeyFrame(0).getRegionHeight()*Data.SCREEN_TO_BOX)/-2;
			midImageHalfWidth = (midAnimation.getKeyFrame(0).getRegionWidth()*Data.SCREEN_TO_BOX)/-2;
		}
	}
	
	/*
	 * Gets the higher region for parallax drawing
	 */
	public TextureRegion getHighRegion()
	{
		return highAnimation.getKeyFrame(highStateTime);
	}
	
	/*
	 * Gets the upper Animation for this GameEntity
	 * 
	 * @return						The upper Animation for this GameEntity
	 */
	public Animation getHighAnim()
	{
		return highAnimation;
	}
	
	/*
	 * Sets the upper Animation for this GameEntity
	 * 
	 * @param newAnim				The new upper Animation for this GameEntity
	 */
	public void setHighAnim(Animation newAnim)
	{
		highAnimation = newAnim;
		if (highAnimation != null)
		{
			highImageHalfHeight = (highAnimation.getKeyFrame(0).getRegionHeight()*Data.SCREEN_TO_BOX)/-2;
			highImageHalfWidth = (highAnimation.getKeyFrame(0).getRegionWidth()*Data.SCREEN_TO_BOX)/-2;
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
			
			if (layerNumber <= LayerHandler.LOW || layerNumber == LayerHandler.chains)
			{
				if (!CGCWorld.isPaused())
				{
					step(delta, LayerHandler.LOW);
				}
				frame = getLowRegion();
			}
			else if (layerNumber <= LayerHandler.MID && layerNumber != LayerHandler.chains)
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
			
			if (this instanceof RotatableEntity)
			{
				baseX = pos.x + ((RotatableEntity)this).getImageHalfWidth(layerNumber, getRotation(layerNumber))
						+ getTransformMod().x;
				baseY = pos.y + ((RotatableEntity)this).getImageHalfHeight(layerNumber, getRotation(layerNumber))
						+ getTransformMod().y;
				
				if (isScaled())
				{
					if (layerNumber <= LayerHandler.LOW || layerNumber == LayerHandler.chains)
					{
						sBatch.draw(frame, 
								baseX, baseY, 0, 0, 
								frame.getRegionWidth(), frame.getRegionHeight(), 
								CGCWorld.getCamera().zoom * getScale().x, 
								CGCWorld.getCamera().zoom * getScale().y, 
								getRotation(LayerHandler.LOW));
					}
					else if (layerNumber <= LayerHandler.MID && layerNumber != LayerHandler.chains)
					{
						if (Options.storedParallaxOption)
						{
							Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / getParallaxDistMod();
							Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / getParallaxDistMod();
						}
						
						sBatch.draw(frame, 
								baseX + Xmod, baseY + Ymod, 0, 0, 
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
						
						sBatch.draw(frame, 
								baseX + Xmod, baseY + Ymod, 0, 0, 
								frame.getRegionWidth(), frame.getRegionHeight(), 
								CGCWorld.getCamera().zoom * getScale().x, 
								CGCWorld.getCamera().zoom * getScale().y, 
								getRotation(LayerHandler.HIGH));
					}
				}
				else // Not scaled
				{
					if (layerNumber <= LayerHandler.LOW || layerNumber == LayerHandler.chains)
					{
						sBatch.draw(frame, 
								baseX, baseY, 0, 0, 
								frame.getRegionWidth(), frame.getRegionHeight(), 
								CGCWorld.getCamera().zoom, CGCWorld.getCamera().zoom, 
								getRotation(LayerHandler.LOW));
					}
					else if (layerNumber <= LayerHandler.MID && layerNumber != LayerHandler.chains)
					{
							if (Options.storedParallaxOption)
							{
								Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / getParallaxDistMod();
								Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / getParallaxDistMod();
							}
							
							sBatch.draw(frame, 
									baseX + Xmod, baseY + Ymod, 0, 0, 
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

						sBatch.draw(frame, 
								baseX + Xmod, baseY + Ymod, 0, 0, 
								frame.getRegionWidth(), frame.getRegionHeight(), 
								CGCWorld.getCamera().zoom, CGCWorld.getCamera().zoom, 
								getRotation(LayerHandler.HIGH));
					}
				}
			}
			else // Non-rotatable entities
			{
				baseX = pos.x + getTransformMod().x;
				baseY = pos.y + getTransformMod().y;
				
				if (isScaled())
				{
					if (layerNumber <= LayerHandler.LOW || layerNumber == LayerHandler.chains)
					{
						sBatch.draw(frame, baseX, baseY, 
								getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
								frame.getRegionWidth(), frame.getRegionHeight(), 
								CGCWorld.getCamera().zoom * getScale().x, 
								CGCWorld.getCamera().zoom * getScale().y, 
								getRotation(LayerHandler.LOW));
					}
					else if (layerNumber <= LayerHandler.MID && layerNumber != LayerHandler.chains)
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
					if (layerNumber <= LayerHandler.LOW || layerNumber == LayerHandler.chains)
					{
						sBatch.draw(frame, baseX, baseY, 
								getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
								frame.getRegionWidth(), frame.getRegionHeight(), 
								CGCWorld.getCamera().zoom, CGCWorld.getCamera().zoom, 
								getRotation(LayerHandler.LOW));
					}
					else if (layerNumber <= LayerHandler.MID && layerNumber != LayerHandler.chains)
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
			}
			
			colorForAlpha = sBatch.getColor();
			colorForAlpha.a = 1.0f;
			sBatch.setColor(colorForAlpha);
		}
	}

	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(GameEntity g)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Gate g)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Tree t)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Player p)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(ChainLink c)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Wall w)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Fence f)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Sensor s)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Track t)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(TrainCar t)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Wheel w)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(RookieCop rc)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Prisoner p)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(GuardTower gt)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Tank t)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Projectile p)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Helicopter h)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(SteelHorse sh)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(PallBearer pb)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(CarrierCop cc)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(Sheriff s)
	{
		return;
	}
	
	/*
	 * Default collide function, placeholder for compiler
	 */
	public void collide(TankShell ts)
	{
		return;
	}
	
	/*
	 * Default endCollide function, placeholder for compiler
	 */
	public void endCollide(GameEntity g)
	{
		return;
	}
	
	/*
	 * Default endCollide function, placeholder for compiler
	 */
	public void endCollide(Prisoner p)
	{
		return;
	}
	/*
	 * Default endCollide function, placeholder for compiler
	 */
	public void endCollide(GuardTower gt)
	{
		return;
	}
	
	/*
	 * Default endCollide function, placeholder for compiler
	 */
	public void endCollide(Helicopter h)
	{
		return;
	}
	
	/*
	 * Defaults endCollide function, placeholder for compiler
	 */
	public void endCollide(SteelHorse sh)
	{
		return;
	}
	
	/*
	 * Defaults endCollide function, placeholder for compiler
	 */
	public void endCollide(PallBearer pb)
	{
		return;
	}
	
	/*
	 * Defaults endCollide function, placeholder for compiler
	 */
	public void endCollide(CarrierCop cc)
	{
		return;
	}
	
	/*
	 * Defaults endCollide function, placeholder for compiler
	 */
	public void endCollide(Sheriff s)
	{
		return;
	}
	
	/*
	 * Called when an entity is flagged for destruction
	 */
	public void initCleanup()
	{
		destroy = true;
	}
	
	/*
	 * Called before an entity is actually destroyed
	 */
	public void finalCleanup()
	{
		destroy = false;
	}
} // End class