/*
 * @(#)GunCop.java		0.2 14/3/4
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
import com.percipient24.cgc.BossFight;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.Jeep;
import com.percipient24.cgc.entities.Targeter;
import com.percipient24.cgc.entities.boss.Boss;
import com.percipient24.cgc.entities.boss.Tank;
import com.percipient24.cgc.entities.projectiles.Bullet;
import com.percipient24.cgc.entities.projectiles.TankShell;
import com.percipient24.enums.AnimationState;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for players who are GunCops
 * 
 * @version 0.2 14/3/4
 * @author Christopher Rider
 */
public class GunCop extends RookieCop 
{
	private Targeter target;
	private Jeep jeep;
	private BossFight fight;
	private boolean tankDriver;
	
	// Fire variables
	private CGCTimer fireTimer;
	private Timer.Task fireTask;
	private float fireTime = 0.35f;
	private boolean canFire = true;
	private int gunCopOrderNum = -1;
	private final float TANK_RATE = 2.5f;
	
	/*
	 * Creates a new GunCop object
	 * 
	 * @param theWorld				Reference to the CGCWorld class (for accessing data available therein)
	 * @param theFight				The BossFight for which this GunCop is needed
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param pID					The ID of the player being created (player #)
	 * @param order					The order this cop was spawned in
	 * @param tank					Whether or not this cop is driving a tank
	 */
	public GunCop(CGCWorld theWorld, BossFight theFight, Animation newLowAnimation, 
			Animation newMidAnimation, Animation newHighAnimation, 
			EntityType pEntityType, Body attachedBody, short pID, 
			int order, boolean tank)
	{
		super(theWorld, newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, pID);
		
		gunCopOrderNum = order;
		fight = theFight;
		body.getFixtureList().get(0).setSensor(true);
		tankDriver = tank;

		// Create targeter
		Body b = CGCWorld.getBF().createCircle(body.getWorldCenter().x, 
				body.getWorldCenter().y, 0.5f, BodyType.DynamicBody, 
				BodyFactory.CAT_NON_INTERACTIVE, BodyFactory.MASK_NON_INTERACTIVE);
		b.getFixtureList().get(0).setSensor(true);
		b.setFixedRotation(true);
		if (!tankDriver)
		{
			target = new Targeter(null, null, AnimationManager.targetingAnims[0], EntityType.TARGETER, b, CGCWorld.getCamera(), playerID);
		}
		else
		{
			target = new Targeter(null, null, AnimationManager.targetingAnims[1], EntityType.TARGETER, b, CGCWorld.getCamera(), playerID);
			fireTime = TANK_RATE;
		}
		b.setUserData(target);
		target.addToWorldLayers(CGCWorld.getLH());
		
		// If this player isn't the tank, create a jeep for them
		if (!tankDriver)
		{
			b = CGCWorld.getBF().createRectangle(-150f,
					-150f, 1.5625f, 2.6f, BodyType.StaticBody,
					BodyFactory.CAT_BOSS, BodyFactory.MASK_BOSS);
			b.getFixtureList().get(0).setSensor(true);
			b.setFixedRotation(true);
			jeep = new Jeep(AnimationManager.jeepAnims[0], AnimationManager.jeepAnims[1], 
					AnimationManager.jeepAnims[2], EntityType.JEEP, b);
			b.setUserData(jeep);
			jeep.addToWorldLayers(CGCWorld.getLH());
		}
		
		alive = true;
		lowState = AnimationState.STAND;
		
		fireTask = new Timer.Task() 
		{
			public void run() 
			{
				canFire = true;
			}
		};
		
		fireTimer = new CGCTimer(fireTask, fireTime, true, "fireTimerJeep");
		TimerManager.addTimer(fireTimer);
	}
	
	public Targeter getTarget()
	{
		return target;
	}
	
	/*
	 * Move the GunCop's crosshairs around the screen
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
		target.move(up, down, left, right, bumper);
		
		if (bumper && canFire)
		{
			fire();
			canFire = false;
		}
		
		if (fight.getBoss() instanceof Tank)
		{
			// Handle updating the player body if they are not driving the tank
			if (!tankDriver)
			{
				float xPos = 0;
				
				if (gunCopOrderNum%2 == 0)
				{
					xPos = (gunCopOrderNum / 2) * 3.0f + 4.0f;
				}
				else
				{
					xPos = (gunCopOrderNum / 2) * -3.0f - 4.0f;
				}
				
				// TODO Handle the -2.5625 offset via image, not physics
				body.setTransform(fight.getBoss().getBody().getPosition().x + xPos,
						fight.getBoss().getBody().getPosition().y + 1.3f - 2.5625f,
						body.getAngle());
				
				jeep.getBody().setTransform(body.getPosition().x+getImageHalfWidth(0)+0.2f, body.getPosition().y+getImageHalfHeight(0), 
						jeep.getBody().getAngle());
			}
			else
			{
				body.setTransform(fight.getBoss().getBody().getPosition().x, 
						fight.getBoss().getBody().getPosition().y - 2.5625f, body.getAngle());
			}
		}
	}
	
	/*
	 * FIRE!
	 */
	private void fire()
	{
		if (target != null) // If null, the targeter is missing somehow
		{
			if (!tankDriver)
			{
				// Create a bullet to fire
				Body b = CGCWorld.getBF().createCircle(body.getWorldCenter().x-getImageHalfWidth(0, body.getAngle())+target.getImageHalfWidth(0)-0.05f, 
						body.getWorldCenter().y-getImageHalfHeight(0, body.getAngle()) + 0.7f, 
						0.1f, BodyType.DynamicBody, BodyFactory.CAT_INTERACTABLE, BodyFactory.MASK_INTERACTABLE);
				GameEntity ge = new Bullet(null, null, AnimationManager.bulletAnim, EntityType.BULLET,
						b, target.getBody().getPosition(), 
						new Vector2(target.getHighRegion().getRegionWidth()/2,
								target.getHighRegion().getRegionHeight()/2));
				b.setUserData(ge);
				ge.addToWorldLayers(CGCWorld.getLH());
			}
			else
			{
				// Create a tank shell to fire
				//SoundManager.playSound("tankFiring", false);
				Vector2 fireTarget = new Vector2(target.getBody().getPosition().x, 
						target.getBody().getPosition().y);
				
				float x = (float) ((230 * Data.SCREEN_TO_BOX) * Math.cos((fight.getBoss().getRotation(100) + 90) * Data.DEGRAD));
				float y = (float) ((230 * Data.SCREEN_TO_BOX) * Math.sin((fight.getBoss().getRotation(100) + 90) * Data.DEGRAD));
				
				// Create a tank shell to fire
				Body b = CGCWorld.getBF().createCircle(fight.getBoss().getBody().getWorldCenter().x+x+.25f, 
						fight.getBoss().getBody().getWorldCenter().y+y+.25f, 0.5f, BodyType.DynamicBody, 
						BodyFactory.CAT_EXPLOSIVE, BodyFactory.MASK_EXPLOSIVE);
				GameEntity ge = new TankShell(null, null, AnimationManager.tankShellAnim, EntityType.TANK_SHELL,
						b, fireTarget, 
						new Vector2(target.getHighRegion().getRegionWidth()/2,
								target.getHighRegion().getRegionHeight()/2));
				b.setUserData(ge);
				ge.addToWorldLayers(CGCWorld.getLH());
			}
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
	 * @param t                   The second entity colliding
	 */
	public void collide (Tank t)
	{
		return; // Do nothing
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.bossPlayer);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.bossPlayer);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.Player#updatePlayer(float)
	 */
	public void updatePlayer(float delta)
	{
		super.updatePlayer(delta);
		
		target.updateTarget(delta, CGCWorld.getCamera());
	}
} // End class