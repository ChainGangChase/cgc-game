/*
 * @(#)SteelHorse.java		0.2 14/3/19
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities.boss;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.BossFight;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.entities.ChainLink;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.Wall;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.players.SteelHorseRider;
import com.percipient24.cgc.maps.MapBuilder;
import com.percipient24.enums.DeathType;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for the SteelHorse boss
 * 
 * @version 0.2 14/3/19
 * @author Christopher Rider
 * @author JD Kelly
 */
public class SteelHorse extends Boss 
{
	private int hp = 0;
	public static int MAX_HP = 0;
	
	//Determines how far off the SteelHorse can be when seeking a target
	public static float accuracy = 0;
	//Determines how fast the SteelHorse will turn away from Walls in front of it
	public static float wallAvoidanceSpeed = 0;
	
	private float offset = 0.0f;
	private Prisoner target = null;
	
	//Starting velocity for crashing
	private Vector2 startVel;
	
	private float maxSpeed = 4.0f;
	private float maxForce = 2.0f;

	//Players
	private SteelHorseRider leftShooter = null;
	private SteelHorseRider rightShooter = null;
	
	//AI Controller
	private Sheriff sheriff = null;
	
	//Crash variables
	private boolean crashing = false;
	private CGCTimer crashTimer;
	private Timer.Task crashTask;
	private boolean down = false;
	
	//Boost variables
	private CGCTimer boostClock;
	private Timer.Task boostTask;
	private float boostTime = 2.0f;
	private boolean canBoost = true;
	private boolean boosting = false;
	
	//Boost Reset Variables
	private CGCTimer resetClock;
	private Timer.Task resetTask;
	private float resetTime = 3.0f;
	
	//Cooldown Variables
	private CGCTimer cooldownClock;
	private Timer.Task cooldownTask;
	private float cooldownTime = 3.0f;
	
	//BodyType Swap Variables
	private CGCTimer btSwapClock;
	private Timer.Task btSwapTask;
	private float btSwapTime = 0.1f;
	
	//BackUp variables
	private CGCTimer backUpClock;
	private Timer.Task backUpTask;
	private float backUpTime = 0.5f;
	
	//Die Timer
	private CGCTimer dieClock;
	private Timer.Task dieTask;
	private float dieTime = 0.1f;
	
	private boolean swapped = false;

	/*
	 * Creates a new SteelHorse object
	 *
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 */
	public SteelHorse(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		setTimers();

		hp = MAX_HP;
		
		offset = (float) ((Math.random() - 0.5f) * accuracy);
		
		target = CGCWorld.getPrisoners().random();

		Vector2 sheriffPos = body.getWorldCenter().cpy().sub(new Vector2(0, getImageHalfHeight(0) / 2).rotate(rotation));
		Body b = CGCWorld.getBF().createCircle(0, 0, 
				0.6f, BodyType.StaticBody, BodyFactory.CAT_IMPASSABLE, BodyFactory.MASK_SHERIFF_GROUND);
		
		sheriff = new Sheriff(AnimationManager.sheriffAnim, AnimationManager.sheriffAnim, AnimationManager.sheriffAnim, EntityType.SHERIFF, b, this);
		sheriff.addToWorldLayers(CGCWorld.getLH());
		//sheriff.addTargeter();
		b.setUserData(sheriff);
		b.setTransform(sheriffPos, 0);
		
	}
	
	/*
	 * Creates a new SteelHorse object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param startAlpha			The starting alpha for this entity
	 */
	public SteelHorse(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
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
		if(BossFight.lost() && !swapped)
		{
			if(leftShooter != null)
			{
				Vector2 leftPos = sheriff.getBody().getPosition().cpy().add(new Vector2(0, leftShooter.getImageHalfHeight(0) - 0.15f).rotate(rotation));
				
				leftShooter.getBody().setTransform(leftPos.cpy(), 0);
				leftShooter.setRotation(rotation);
				leftShooter.updatePlayer(deltaTime);
				
				if (rightShooter != null)
				{
					Vector2 rightPos = leftShooter.getBody().getPosition().cpy().add(new Vector2(0, rightShooter.getImageHalfHeight(0)).rotate(rotation));
					
					rightShooter.getBody().setTransform(rightPos.cpy(), 0);
					rightShooter.setRotation(rotation);
					rightShooter.updatePlayer(deltaTime);
				}
			}
			
			sheriff.setRotation(rotation);
			
			Vector2 sheriffPos = body.getWorldCenter().cpy().sub(new Vector2(0, getImageHalfHeight(0) / 2).rotate(rotation));
			
			sheriff.getBody().setTransform(sheriffPos, 0);
			sheriff.update(deltaTime);
		}
	}
	
	/*
	 * Makes the Steel Horse pause its timers
	 * @see com.percipient24.cgc.entities.Boss#pause()
	 */
	public void pause()
	{
		
	}
	
	/*
	 * Makes the Steel Horse resume its timers
	 * @see com.percipient24.cgc.entities.Boss#pause()
	 */
	public void resume()
	{
		
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
		if(swapped)
		{
			lh.removeEntityFromLayer(this, LayerHandler.ground);
		}
		else
		{
			lh.removeEntityFromLayer(this, LayerHandler.mid);
			lh.removeEntityFromLayer(this, LayerHandler.bossTop);
		}
	}
	
	/*
	 * Draws the SteelHorse in the CGCWorld
	 * 
	 * @param sBatch				The SpriteBatch the game is using
	 * @param delta					Seconds elapsed since the last frame
	 * @param layerNumber			Which BodyFactory layer to draw
	 */
	public void draw(SpriteBatch sBatch, float delta, int layerNumber) 
	{
		Color oldColor = sBatch.getColor().cpy();
		if (!CGCWorld.lost())
		{
			float healthRatio = (float)(hp + 0.001f) / (float)MAX_HP;
		
			Color tint = new Color(1.0f, healthRatio, healthRatio, 1.0f);
			sBatch.setColor(tint);
		}
		
		super.draw(sBatch, delta, layerNumber);
		
		sBatch.setColor(oldColor);
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
	 * @param w						The first entity colliding
	 */
	public void collide(Wall w)
	{
		if(w.isVertical())
		{
			if(!BossFight.lost())
			{
				body.setLinearVelocity(-body.getLinearVelocity().cpy().x, body.getLinearVelocity().cpy().y);
			}
		}
		else
		{	
			if(!BossFight.lost())
			{
				body.setLinearVelocity(body.getLinearVelocity().cpy().x, -body.getLinearVelocity().cpy().y);
			}
		}
		
		if ((boosting || crashing) && !BossFight.lost())
		{
			if(boosting)
			{
				TimerManager.removeTimer(boostClock);
				boosting = false;
				maxSpeed /= 2;
				maxForce /= 2;
			}
			
			if (crashing)
			{
				TimerManager.removeTimer(crashTimer);
			}
			
			hp--;
			
			setLowAnim(AnimationManager.crashAnim);
			setMidAnim(AnimationManager.crashAnim);
			setHighAnim(AnimationManager.crashAnim);
			
			if (hp > 0)
			{
				startVel = body.getLinearVelocity().cpy();
				
				rotation = startVel.angle() - 90;
				TimerManager.addTimer(backUpClock);
				
				canBoost = false;
				down = true;
			}
		}
		else if(BossFight.lost())
		{
			startVel = body.getLinearVelocity().cpy();
			TimerManager.addTimer(backUpClock);;
		}
	}
	/*
	 * Handles the end of the collision with a wall
	 */
	public void endCollide(Wall w)
	{
		if(isDead() && !swapped)
		{
			die();
		}
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
		if (!(down || crashing))
		{
			if (body.getLinearVelocity().cpy().len2() >= maxSpeed * maxSpeed / 4)
			{
				Vector2 dif = body.getPosition().cpy().sub(p.getPosition().cpy());
				if (dif.dot(body.getLinearVelocity().cpy().nor()) >= 0)
				{
					p.die(DeathType.BOSS);
				}
			}
		}
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param c						The first entity colliding
	 */
	public void collide(ChainLink c)
	{
		Player left = c.gLeft();
		
		if (!resetClock.isRunning() && !crashTimer.isRunning())
		{
			if (left != null)
			{
				if (left.isAlive())
				{
					Player right = left.getRightPlayer();
					if (right != null)
					{
						if (right.isAlive())
						{
							Vector2 lPos = left.getBody().getWorldCenter().cpy();
							Vector2 lDis = lPos.sub(body.getWorldCenter().cpy()).nor();
							float lDot = body.getLinearVelocity().cpy().nor().dot(lDis);
						
							Vector2 rPos = right.getBody().getWorldCenter().cpy();
							Vector2 rDis = rPos.sub(body.getWorldCenter().cpy()).nor();
							float rDot = body.getLinearVelocity().cpy().nor().dot(rDis);
							
							if (lDot >= 0 && rDot >= 0)
							{				
								if(body.getLinearVelocity().cpy().len2() >= 9.0f)
								{
									float distance = left.getPosition().cpy().dst(right.getPosition().cpy());
									
									if (distance >= (CGCWorld.gNumChains() * 0.2f + 0.3f) * 0.5f)
									{
										crashing = true;
										startVel = body.getLinearVelocity().cpy();
										rotation = startVel.angle() - 90;
										TimerManager.addTimer(crashTimer);
										
										if (boostClock.isRunning())
										{
											TimerManager.removeTimer(boostClock);
											maxSpeed /= 2;
											maxForce /= 2;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	/*
	 * Quick getter for down
	 * 
	 * @return						Whether or not this is down 
	 */
	public boolean isDown()
	{
		return down;
	}
	
	/*
	 * Quick getter for crashing
	 * 
	 * @return						Whether or not this is crashing
	 */
	public boolean isCrashing()
	{
		return crashing;
	}
	
	/*
	 * Doubles the max speed and max force of the SteelHorse if it can boost
	 */
	public void boost()
	{
		if (canBoost)
		{
			maxSpeed *= 2;
			maxForce *= 2;
			canBoost = false;
			boosting = true;
			setLowAnim(AnimationManager.boostAnim);
			setMidAnim(AnimationManager.boostAnim);
			setHighAnim(AnimationManager.boostAnim);
			TimerManager.addTimer(boostClock);
		}
	}
	
	/*
	 * Creates the Timers for this class
	 */
	public void setTimers()
	{
		crashTask = new Timer.Task() {
			
			public void run() {
				body.setLinearVelocity(startVel.cpy().scl(1 - crashTimer.getPercent()));
				
				if (crashTimer.getPercent() >= 1.0f)
				{
					crashing = false;
					down = true;
					hp--;
					body.setType(BodyType.StaticBody);
					
					setLowAnim(AnimationManager.crashAnim);
					setMidAnim(AnimationManager.crashAnim);
					setHighAnim(AnimationManager.crashAnim);
					
					if (hp > 0)
					{
						TimerManager.addTimer(resetClock);
						
						TimerManager.removeTimer(crashTimer);
					}
					else if (hp == 0)
					{
						TimerManager.addTimer(dieClock);
					}
				}
			}
		};

		crashTimer = new CGCTimer(crashTask, CGCWorld.WORLD_DELAY, (int)(1 / CGCWorld.WORLD_DELAY));
		
		resetTask = new Timer.Task() {
			
			public void run() {
				down = false;
				canBoost = true;
				crashing = false;
				body.setTransform(body.getPosition().cpy().sub(body.getLinearVelocity().cpy().nor().scl(0.8f)), 0);
				body.setType(BodyType.DynamicBody);
				
				setLowAnim(AnimationManager.steelHorseAnim);
				setMidAnim(AnimationManager.steelHorseAnim);
				setHighAnim(AnimationManager.steelHorseAnim);
				
				pickTarget();
				offset = (float) (Math.random() * accuracy);
				Vector2 newVel = target.getPosition().cpy().add(new Vector2((float) Math.cos(offset), (float) Math.sin(offset)).scl(1.5f)).sub(body.getPosition().cpy()).nor().scl(maxSpeed / 2);
				body.setLinearVelocity(newVel);
			}
		};
		
		resetClock = new CGCTimer(resetTask, resetTime, false, "resetClock");
		
		boostTask = new Timer.Task()
		{
			public void run()
			{
				
				setLowAnim(AnimationManager.steelHorseAnim);
				setMidAnim(AnimationManager.steelHorseAnim);
				setHighAnim(AnimationManager.steelHorseAnim);
				
				boosting = false;
				maxSpeed /= 2;
				maxForce /= 2;
				
				cooldownClock.reset();
				TimerManager.addTimer(cooldownClock);
			}
		};
		
		boostClock = new CGCTimer(boostTask, boostTime, false, "boostClock");
		
		cooldownTask = new Timer.Task()
		{
			public void run()
			{
				
				canBoost = true;
				boostClock.reset();
			}
		};
		
		cooldownClock = new CGCTimer(cooldownTask, cooldownTime, false, "cooldownClock");
		
		btSwapTask = new Timer.Task()
		{
			public void run()
			{
				body.setType(BodyType.StaticBody);
			}
		};
		
		btSwapClock = new CGCTimer(btSwapTask, btSwapTime, false, "btSwapClock");
		
		backUpTask = new Timer.Task() {
			
			public void run() {
				body.setLinearVelocity(startVel.cpy().scl(1 - backUpClock.getPercent()));
				
				if(backUpClock.getPercent() >= 1.0f)
				{
					TimerManager.addTimer(resetClock);
					TimerManager.addTimer(btSwapClock);
				}
			}
		};
		
		backUpClock = new CGCTimer(backUpTask, backUpTime, false, "backUpClock");
		
		dieTask = new Timer.Task() {
			
			public void run() {
				die();
			}
		};
		
		dieClock = new CGCTimer(dieTask, dieTime, false, "dieClock");
	}
	
	/*
	 * Quick getter for maxForce
	 * 
	 * @return						Max force
	 */
	public float gMaxForce()
	{
		return maxForce;
	}
	
	/*
	 * Quick getter for maxSpeed
	 * 
	 * @return						Max speed
	 */
	public float gMaxSpeed()
	{
		return maxSpeed;
	}
	
	/*
	 * Adds a rider to the array
	 * 
	 * @param shr						A SteelHorseRider to ride this SteelHorse
	 */
	public void addRider(SteelHorseRider shr)
	{
		if (leftShooter == null)
		{
			leftShooter = shr;
			Vector2 leftPos = sheriff.getBody().getPosition().cpy().add(new Vector2(0, leftShooter.getImageHalfHeight(0) - 0.15f).rotate(rotation));
			
			leftShooter.getBody().setTransform(leftPos.cpy(), 0);
			sheriff.removeTargeter();
		}
		
		else
		{
			double ran = Math.random();
			Vector2 rightPos = leftShooter.getBody().getPosition().cpy().add(new Vector2(0, rightShooter.getImageHalfHeight(0)).rotate(rotation));
			
			if (ran < 0.5)
			{
				rightShooter = shr;
				rightShooter.getBody().setTransform(rightPos, 0);
			}
			else
			{
				Vector2 leftPos = sheriff.getBody().getPosition().cpy().add(new Vector2(0, leftShooter.getImageHalfHeight(0) - 0.15f).rotate(rotation));
				
				leftShooter.removeFromWorldLayers(CGCWorld.getLH());
				rightShooter = leftShooter;
				leftShooter = shr;
				leftShooter.addToWorldLayers(CGCWorld.getLH());
				rightShooter.addToWorldLayers(CGCWorld.getLH());
				
				leftShooter.getBody().setTransform(leftPos, 0);
				rightShooter.getBody().setTransform(rightPos, 0);
			}
			
		}
		shr.mount(this);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.boss.Boss#update(float)
	 */
	public void update(float deltaTime)
	{
		if (hp > 0)
		{
			pickTarget();
			
			Vector2 desVel = new Vector2(0, 0);
			
			if (!down && !crashing)
			{
				if(target != null)
				{
					Vector2 avg = target.getPosition();
					desVel = avg.sub(body.getPosition()).nor().scl(maxSpeed * 1.5f);
				}
			
				Vector2 steer = desVel.sub(body.getLinearVelocity().cpy());
				steer.clamp(0, maxForce * 1.5f);
				steer = steer.cpy().scl(1.0f/body.getMass());
				steer = steer.cpy().rotate(offset);
				
				Vector2 myPos = body.getPosition().cpy();
				boolean avoiding = false;
				if(myPos.x <= 3.0f && body.getLinearVelocity().cpy().x <= 0)
				{
					steer = steer.cpy().add(new Vector2(wallAvoidanceSpeed, 0));
					avoiding = true;
				}
				else if(myPos.x >= MapBuilder.chunkWidth - 3 && body.getLinearVelocity().cpy().x >= 0)
				{
					steer = steer.cpy().add(new Vector2(-wallAvoidanceSpeed, 0));
					avoiding = true;
				}
				
				if(myPos.y <= 3.0f && body.getLinearVelocity().cpy().y <= 0)
				{
					steer = steer.cpy().add(new Vector2(0, wallAvoidanceSpeed));
					avoiding = true;
				}
				else if(myPos.y >= BossFight.getLevel().getLevelLength() * 11 - 5.0f && body.getLinearVelocity().cpy().y >= 0)
				{
					steer = steer.cpy().add(new Vector2(0, -wallAvoidanceSpeed));
					avoiding = true;
				}
				
				Vector2 vel = body.getLinearVelocity().cpy().add(steer.scl(deltaTime));
				vel.clamp(0, maxSpeed);
				
				if (vel.len2() >= maxSpeed * maxSpeed * 9 / 16)
				{
					boost();
				}
				
				body.setLinearVelocity(vel);
				vel = body.getLinearVelocity().cpy();
				
				rotation = body.getLinearVelocity().angle() - 90;
				
			}
			
			if(leftShooter != null)
			{
				Vector2 leftPos = sheriff.getBody().getPosition().cpy().add(new Vector2(0, leftShooter.getImageHalfHeight(0) - 0.15f).rotate(rotation));
				
				leftShooter.getBody().setTransform(leftPos.cpy(), 0);
				leftShooter.setRotation(rotation);
				leftShooter.updatePlayer(deltaTime);
				
				if (rightShooter != null)
				{
					Vector2 rightPos = leftShooter.getBody().getPosition().cpy().add(new Vector2(0, rightShooter.getImageHalfHeight(0)).rotate(rotation));
					
					rightShooter.getBody().setTransform(rightPos.cpy(), 0);
					rightShooter.setRotation(rotation);
					rightShooter.updatePlayer(deltaTime);
				}
			}
			
			sheriff.setRotation(rotation);
			
			Vector2 sheriffPos = body.getWorldCenter().cpy().sub(new Vector2(0, getImageHalfHeight(0) / 2).rotate(rotation));
			
			sheriff.getBody().setTransform(sheriffPos, 0);
			sheriff.update(deltaTime);
			
			if(BossFight.getLivingPrisoners().size <= 0)
			{
				sheriff.getBody().setType(BodyType.DynamicBody);
				sheriff.getBody().setLinearVelocity(body.getLinearVelocity().cpy());
			}
		}
		else
		{
			if(body.getLinearVelocity().cpy().len2() >= 0.01f)
			{
				body.setLinearVelocity(body.getLinearVelocity().cpy().scl(0.94f));
				
				if(leftShooter != null)
				{
					Vector2 leftPos = sheriff.getBody().getPosition().cpy().add(new Vector2(0, leftShooter.getImageHalfHeight(0) - 0.15f).rotate(rotation));
					
					leftShooter.getBody().setTransform(leftPos.cpy(), 0);
					leftShooter.setRotation(rotation);
					
					if (rightShooter != null)
					{
						Vector2 rightPos = leftShooter.getBody().getPosition().cpy().add(new Vector2(0, rightShooter.getImageHalfHeight(0)).rotate(rotation));
						
						rightShooter.getBody().setTransform(rightPos.cpy(), 0);
						rightShooter.setRotation(rotation);
					}
				}
				
			
				sheriff.setRotation(rotation);
				
				Vector2 sheriffPos = body.getWorldCenter().cpy().sub(new Vector2(0, getImageHalfHeight(0) / 2).rotate(rotation));
				sheriff.getBody().setTransform(sheriffPos, 0);
			}
			else
			{
				body.setLinearVelocity(0, 0);
			}
			if (sheriff != null)
			{
				sheriff.update(deltaTime);
			}
			
			if (leftShooter != null)
			{
				leftShooter.updatePlayer(deltaTime);
			}
			
			if (rightShooter != null)
			{
				rightShooter.updatePlayer(deltaTime);
			}
		}
	}
	
	/*
	 * Picks the target for the SteelHorse and determines offset
	 */
	public void pickTarget()
	{
		if(BossFight.getLivingPrisoners().size == 0)
		{
			target = null;
			return;
		}
		
		if (target == null || !target.isAlive())
		{
			if (!CGCWorld.lost())
			{
				do
				{
					target = CGCWorld.getPrisoners().random();
				}while(!target.isAlive() || target == null);
			}
		}
		
		offset = (float) ((Math.random() - 0.5f) * accuracy);
	}
	
	/*
	 * Gets Whether or not the SteelHorse is dead but the boss fight is still going
	 * 
	 * @return						If the SteelHorse is dead
	 */
	public boolean isDead()
	{
		return (hp <= 0);
	}
	
	/*
	 * Quick getter for the Sheriff
	 * 
	 * @return						The Sheriff
	 */
	public Sheriff gSheriff()
	{
		return sheriff;
	}
	
	/*
	 * Handles the death of the SteelHorse
	 */
	public void die()
	{
		removeFromWorldLayers(CGCWorld.getLH());
		swapped = true;
		
		Fixture f = body.getFixtureList().get(0);
		Filter fi = f.getFilterData();
		fi.categoryBits = BodyFactory.CAT_NON_INTERACTIVE;
		fi.maskBits = BodyFactory.MASK_NON_INTERACTIVE;
		f.setFilterData(fi);
		body.getFixtureList().set(0, f);
		body.setUserData(this);
		
		if (leftShooter != null)
		{
			if (rightShooter != null)
			{
				rightShooter.dismount();
			}
				leftShooter.dismount();
				sheriff.addTargeter();
		}
	
		sheriff.swapWorldLayers(CGCWorld.getLH());
		sheriff.getBody().setUserData(sheriff);
		TimerManager.removeTimer(dieClock);
	}
	
	/*
	 * Quick getter for the number of players in this fight
	 * 
	 * @return						The number of player controlled cops in this battle
	 */
	public int getNumCops()
	{
		if (leftShooter != null)
		{
			if(rightShooter != null)
			{
				return 2;
			}
			return 1;
		}
		return 0;
	}
} // End class