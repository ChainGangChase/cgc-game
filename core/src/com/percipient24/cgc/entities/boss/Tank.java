/*
 * @(#)Tank.java		0.2 14/2/27
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities.boss;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.percipient24.cgc.*;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.enums.EntityType;
import com.percipient24.cgc.entities.ChainLink;
import com.percipient24.cgc.entities.Fence;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.RotatableEntity;
import com.percipient24.cgc.entities.Targeter;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.projectiles.TankShell;
import com.percipient24.cgc.entities.Tree;
import com.percipient24.cgc.screens.Options;
import com.badlogic.gdx.utils.Timer;

/*
 * Handles the logic for a tank entity
 * 
 * @version 0.2 14/2/27
 * @author Christopher Rider
 * @author William Ziegler
 */
public class Tank extends Boss 
{
	private static float normalSpeed = 0.0f;
	private static float currentSpeed;
	private static float slowedSpeed; 
	private static float speedRestoreRate;
	private static float speedDecayRate;
	
	public static float accuracy = 1.0f;
	
	private Vector2 moveForce;
	
	// Fire variables
	private CGCTimer fireTimer;
	private Timer.Task fireTask;
	private float fireTime = 3.5f;
	private boolean aiControl;
	private float turretRotation = 0.0f; // In degrees
	private GameEntity target;
	
	// Slow variables
	private boolean slowed;
	private CGCTimer slowTimer;
	private Timer.Task slowTask;
	private float slowTime = 2.0f;
	
	/*
	 * Creates a new Tank object
	 *
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param ai					Whether or not this Tank is ai controlled
	 * @param targeter				The Targeter for the player controller, null if ai
	 */
	public Tank(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, boolean ai, Targeter targeter)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		moveForce = new Vector2(0, currentSpeed);
		aiControl = ai;
		
		if (ai)
		{
			fireTask = new Timer.Task() 
			{
				public void run()
				{
					fire();
				}
			};
			fireTimer = new CGCTimer(fireTask, fireTime, true, "fireTimer");
			
			TimerManager.addTimer(fireTimer);
			
			target = getTargetPlayer();
		}
		else
		{
			target = targeter;
		}
		
		slowTask = new Timer.Task()
		{
			public void run()
			{
				slowed = false;
			}
		};
		
		slowTimer = new CGCTimer(slowTask, slowTime, false, "slowTimer");
	}
	
	/*
	 * Creates a new Tank object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param ai					Whether or not this Tank is ai controlled
	 * @param targeter				The targeter for the player controller, null if ai
	 * @param startAlpha			The starting alpha for this entity
	 */
	public Tank(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, boolean ai, Targeter targeter, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, 
				pEntityType, attachedBody, ai, targeter);
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
	}
	
	/*
	 * Moves the tank
	 * 
	 * @param delta					Seconds elapsed since the last frame
	 */
	public void move(float delta)
	{
		if (CGCWorld.getDistToBoss(this) < 10)
		{
			moveForce.y = currentSpeed;
		}
		else 
		{
			moveForce.y = currentSpeed * 5;
		}
		
		body.applyLinearImpulse(moveForce, body.getWorldCenter(), true);
		
		if (target != null)
		{
			float o = target.getBody().getPosition().y - body.getPosition().y;
			float a = target.getBody().getPosition().x - body.getPosition().x;
			
			turretRotation = (float) (Math.atan2(o, a) * Data.RADDEG) - 90.0f;
			
			if (turretRotation > 90f)
			{
				turretRotation = 90f;
			}
			else if (turretRotation < -90f)
			{
				turretRotation = -90f;
			}
		}
		
		if (slowed)
		{
			currentSpeed -= speedDecayRate;
			if (currentSpeed <= slowedSpeed)
			{
				currentSpeed = slowedSpeed;
				if (!slowTimer.isRunning())
				{
					TimerManager.addTimer(slowTimer);
				}
			}
		}
		else if (currentSpeed < normalSpeed)
		{
			currentSpeed += speedRestoreRate;
			if (currentSpeed > normalSpeed)
			{
				currentSpeed = normalSpeed;
			}
		}
	}
	
	/*
	 * FIRE!
	 */
	public void fire()
	{
		if (aiControl)
		{
			// Determine which player to fire at - Sequential order for now
			target = getTargetPlayer();
			
			if (target != null) // If null, all players are dead or are cops
			{
				float offX = (CGCWorld.getRandom().nextFloat() - 0.5f) * accuracy;
				float offY = (CGCWorld.getRandom().nextFloat() - 0.5f) * accuracy;
				
				float x = (float) ((230 * Data.SCREEN_TO_BOX) * Math.cos((turretRotation + 90) * Data.DEGRAD));
				float y = (float) ((230 * Data.SCREEN_TO_BOX) * Math.sin((turretRotation + 90) * Data.DEGRAD));
				
				//SoundManager.playSound("tankFiring", false);
				Vector2 fireTarget = new Vector2(target.getBody().getPosition().x + offX, 
						target.getBody().getPosition().y+target.getBody().getLinearVelocity().y + offY);
				
				if (fireTarget.y < body.getPosition().y)
				{
					fireTarget.set(fireTarget.x, body.getPosition().y);
				}
				
				// Create a tank shell to fire
				Body b = CGCWorld.getBF().createCircle(body.getWorldCenter().x+x+.25f, 
						body.getWorldCenter().y+y+.25f-2.5625f, 0.5f, BodyType.DynamicBody, 
						BodyFactory.CAT_EXPLOSIVE, BodyFactory.MASK_EXPLOSIVE);
				GameEntity ge = new TankShell(null, null, com.percipient24.cgc.art.TextureAnimationDrawer.tankShellAnim, EntityType.TANK_SHELL,
						b, fireTarget, 
						new Vector2(target.getHighRegion().getRegionWidth()/2,
								target.getHighRegion().getRegionHeight()/2));
				b.setUserData(ge);
				ge.addToWorldLayers(CGCWorld.getLH());
			}
		}
	}
	
	/*
	 * Gets a target Prisoner for this Tank to fire at
	 * 
	 * @return						The player to fire at
	 */
	private Player getTargetPlayer()
	{
		if (CGCWorld.getLivingPrisoners().size > 0)
		{
			int randIndex = CGCWorld.getRandom().nextInt(CGCWorld.getLivingPrisoners().size);
			return CGCWorld.getLivingPrisoners().get(randIndex);
		}
		else
		{
			return null;
		}
	}
	
	/*
	 * Gets the rotation of this tank image
	 * 
	 * @param height				Which layer to find the rotation for
	 * @return						The tank's rotation if not the turret layer, otherwise the turret's rotation
	 */
	public float getRotation(int height)
	{
		if (height > LayerHandler.MID)
		{
			return turretRotation;
		}
		else
		{
			return rotation;
		}
	}
	
	/*
	 * Changes the normal speed and other variables having to do with it
	 * 
	 * @param newSpeed					The new movement speed for this Tank
	 */
	public static void changeNormalSpeed(float newSpeed)
	{
		normalSpeed = newSpeed;
		currentSpeed = normalSpeed;
		slowedSpeed = normalSpeed * .6f;
		speedRestoreRate = slowedSpeed * .025f;
		speedDecayRate = speedRestoreRate * 1.5f;
	}
	
	/*
	 * Makes the Tank pause its timers
	 * @see com.percipient24.cgc.entities.Boss#pause()
	 */
	public void pause()
	{
		
	}
	
	/*
	 * Makes the Tank resume its timers
	 * @see com.percipient24.cgc.entities.Boss#pause()
	 */
	public void resume()
	{
		
	}
	
	/*
	 * Determines the first class type in a collision
	 * 
	 * @param ge					The second entity colliding
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
		slowed = true;
		if (p.isAlive())
		{
			p.collide(this);
		}
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param cl						The first entity colliding
	 */
	public void collide(ChainLink cl)
	{
		cl.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param t						The first entity colliding
	 */
	public void collide(Tree t)
	{
		t.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param f						The first entity colliding
	 */
	public void collide (Fence f)
	{
		f.collide(this);
	}

	/*
	 * Draws this Tank in the CGCWorld
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
			float yAdjust = -2.5625f; // Move the center of the image down so the top edge aligns with the body's top edge
			float Xmod = 0.0f;
			float Ymod = 0.0f;
			
			float baseX = 0.0f;
			float baseY = 0.0f;
			
			if (isScaled())
			{
				baseX = pos.x + getTransformMod().x;
				baseY = pos.y + getTransformMod().y;

				if (layerNumber <= LayerHandler.LOW || layerNumber == LayerHandler.chains)
				{
					sBatch.draw(frame, baseX, baseY + yAdjust, 
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
					
					sBatch.draw(frame, baseX + Xmod, baseY + Ymod + yAdjust, 
							getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
							frame.getRegionWidth(), frame.getRegionHeight(), 
							CGCWorld.getCamera().zoom * getScale().x, 
							CGCWorld.getCamera().zoom * getScale().y, 
							getRotation(LayerHandler.MID));
				}
				else
				{
					baseX = pos.x + ((RotatableEntity)this).getImageHalfWidth(0, getRotation(layerNumber));
					baseY = pos.y + ((RotatableEntity)this).getImageHalfHeight(0, getRotation(layerNumber));
					
					if (Options.storedParallaxOption)
					{
						Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / (getParallaxDistMod() / 2);
						Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / (getParallaxDistMod() / 2);
					}
					
					sBatch.draw(frame, 
							baseX + Xmod, baseY + Ymod + yAdjust, 0, 0,  
							frame.getRegionWidth(), frame.getRegionHeight(), 
							CGCWorld.getCamera().zoom * getScale().x, 
							CGCWorld.getCamera().zoom * getScale().y, 
							getRotation(LayerHandler.HIGH));
				}
			}
			else // Not scaled
			{
				baseX = pos.x + getTransformMod().x;
				baseY = pos.y + getTransformMod().y;

				if (layerNumber <= LayerHandler.LOW || layerNumber == LayerHandler.chains)
				{
					sBatch.draw(frame, baseX, baseY + yAdjust, 
							getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
							frame.getRegionWidth(), frame.getRegionHeight(), 
							CGCWorld.getCamera().zoom, 
							CGCWorld.getCamera().zoom, 
							getRotation(LayerHandler.LOW));
				}
				else if (layerNumber <= LayerHandler.MID && layerNumber != LayerHandler.chains)
				{
					if (Options.storedParallaxOption)
					{
						Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / getParallaxDistMod();
						Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / getParallaxDistMod();
					}
					
					sBatch.draw(frame, baseX + Xmod, baseY + Ymod + yAdjust, 
							getImageHalfWidth(layerNumber), getImageHalfHeight(layerNumber), 
							frame.getRegionWidth(), frame.getRegionHeight(), 
							CGCWorld.getCamera().zoom, 
							CGCWorld.getCamera().zoom, 
							getRotation(LayerHandler.MID));
				}
				else
				{
					baseX = pos.x + ((RotatableEntity)this).getImageHalfWidth(0, getRotation(layerNumber));
					baseY = pos.y + ((RotatableEntity)this).getImageHalfHeight(0, getRotation(layerNumber));
					
					if (Options.storedParallaxOption)
					{
						Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / (getParallaxDistMod() / 2);
						Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / (getParallaxDistMod() / 2);
					}
					
					sBatch.draw(frame, 
							baseX + Xmod, baseY + Ymod + yAdjust, 0, 0, 
							frame.getRegionWidth(), frame.getRegionHeight(), 
							CGCWorld.getCamera().zoom, 
							CGCWorld.getCamera().zoom, 
							getRotation(LayerHandler.HIGH));
				}
			}
			
			colorForAlpha = sBatch.getColor();
			colorForAlpha.a = 1.0f;
			sBatch.setColor(colorForAlpha);
		}
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.ground);
		lh.addEntityToLayer(this, LayerHandler.mid);
		lh.addEntityToLayer(this, LayerHandler.bossTop);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.ground);
		lh.removeEntityFromLayer(this, LayerHandler.mid);
		lh.removeEntityFromLayer(this, LayerHandler.bossTop);
	}
} // End class