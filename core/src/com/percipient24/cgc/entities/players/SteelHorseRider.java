/*
 * @(#)SteelHorseRider.java		0.3 14/4/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities.players;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.b2dhelpers.BodyFactory;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.Targeter;
import com.percipient24.cgc.entities.boss.Boss;
import com.percipient24.cgc.entities.boss.SteelHorse;
import com.percipient24.cgc.entities.projectiles.RiderBullet;
import com.percipient24.enums.AnimationState;
import com.percipient24.enums.ControlType;
import com.percipient24.enums.EntityType;

/*
 * A variant of RookieCop that rides on the SteelHorse boss.
 * 
 * @version 0.3 14/4/4
 * @author JD Kelly
 * @author Christopher Rider
 */
public class SteelHorseRider extends RookieCop
{
	private Targeter target;
	private SteelHorse epona;
	
	// Fire variables
	private CGCTimer fireTimer;
	private Timer.Task fireTask;
	private float fireTime = 0.5f;
	private boolean canFire = true;

	/*
	 * Creates a new SteelHorseRider object
	 * 
	 * @param theWorld				Reference to the CGCWorld class (for accessing data available therein)
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param pID					The ID of the player being created (player #)
	 * @param sh					The SteelHorse this SteelHorseRider is riding
	 */
	public SteelHorseRider(CGCWorld theWorld, Animation newLowAnimation, 
			Animation newMidAnimation, Animation newHighAnimation, 
			EntityType pEntityType, Body attachedBody, short pID, SteelHorse sh)
	{
		super(theWorld, newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, pID);
		
		body.getFixtureList().get(0).setSensor(true);

		epona = sh;
	
		Body b = CGCWorld.getBF().createCircle(body.getWorldCenter().x, 
				body.getWorldCenter().y, 0.5f, BodyType.DynamicBody, 
				BodyFactory.CAT_NON_INTERACTIVE, BodyFactory.MASK_NON_INTERACTIVE);
		b.getFixtureList().get(0).setSensor(true);
		b.setFixedRotation(true);

		target = new Targeter(null, null, AnimationManager.targetingAnims[0], 
				EntityType.TARGETER, b, CGCWorld.getCamera(), playerID);

		b.setUserData(target);
		target.addToWorldLayers(CGCWorld.getLH());
		
		fireTask = new Timer.Task() 
		{
			public void run() {
				canFire = true;
			}
		};
		
		fireTimer = new CGCTimer(fireTask, fireTime, true, "fireTimer");
		TimerManager.addTimer(fireTimer);
		
		alive = true;
		lowState = AnimationState.STAND;
		noGrab = true;
		canJump = false;
	}
	
	/*
	 * Move the SteelHorseRider's crosshairs around the screen
	 * 
	 * @param up					Whether or not the up command is being used
	 * @param down					Whether or not the down command is being used
	 * @param left 					Whether or not the left command is being used
	 * @param right					Whether or not the right command is being used
	 * @param bumper				Whether or not the bumper command is being used
	 */
	public void move(boolean up, boolean down, boolean left, boolean right, 
			boolean bumper)
	{
		if (epona != null)
		{
			target.move(up, down, left, right, bumper);
			
			if (scheme != null)
			{
				if (scheme.getController().isPressed(ControlType.JUMP) && canFire)
				{
					fire();
					canFire = false;
				}
			}
		}
		else
		{
			super.move(up, down, left, right, bumper);
		}
	}
	
	/*
	 * FIRE!
	 */
	private void fire()
	{
		if (target != null) // If null, the targeter is missing somehow
		{
			// Create a bullet to fire
			Body b = CGCWorld.getBF().createCircle(body.getWorldCenter().x, 
					body.getWorldCenter().y, 
					0.1f, BodyType.DynamicBody, BodyFactory.CAT_INTERACTABLE, BodyFactory.MASK_INTERACTABLE);
			GameEntity ge = new RiderBullet(null, null, AnimationManager.bulletAnim, EntityType.BULLET,
					b, target.getBody().getPosition(), 
					new Vector2(target.getHighRegion().getRegionWidth() / 2,
							target.getHighRegion().getRegionHeight() / 2), false);
			b.setUserData(ge);
			
			ge.addToWorldLayers(CGCWorld.getLH());
		}
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param b                   The second entity colliding
	 */
	public void collide(Boss b)
	{
		return; // Do nothing
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param sh                  	The second entity colliding
	 */
	public void collide (SteelHorse sh)
	{
		return; // Do nothing
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.ground);
		lh.addEntityToLayer(this, LayerHandler.mid);
		lh.addEntityToLayer(this, LayerHandler.bossTop);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.ground);
		lh.removeEntityFromLayer(this, LayerHandler.mid);
		lh.removeEntityFromLayer(this, LayerHandler.bossTop);
	}
	
	/*
	 * Sets the SteelHorse for the rider
	 * 
	 * @param sh					The SteelHorse this is to ride
	 */
	public void mount(SteelHorse sh)
	{
		epona = sh;
		body.setTransform(epona.getBody().getPosition(), 0);
	}
	
	/*
	 * Dismounts the rider
	 */
	public void dismount()
	{
		epona = null;
		noGrab = false;
		canJump = true;
	}
	/*
	 * @see com.percipient24.cgc.entities.Player#updatePlayer(float)
	 */
	public void updatePlayer(float delta)
	{
		super.updatePlayer(delta);
		
		target.updateTarget(delta, CGCWorld.getCamera());	
	}
	
	/*
	 * Determines how this SteelHorseRider reacts to getting punched
	 * 
	 * @param direction				The direction of the punch
	 */
	public void punched(int direction)
	{
		
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
}// End Class
