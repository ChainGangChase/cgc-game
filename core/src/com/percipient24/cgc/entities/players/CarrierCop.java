/*
 * @(#)CarrierCop.java		0.3 14/4/11
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities.players;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.entities.ChainLink;
import com.percipient24.cgc.entities.boss.PallBearer;
import com.percipient24.enums.ControlType;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for the cops that carry the PallBearer
 * 
 * @version 0.3 14/4/11
 * @author JD Kelly
 */
public class CarrierCop extends RookieCop {

	private CGCTimer rechargeTimer;
	private Timer.Task rechargeTask;
	private float rechargeTime = 5.0f;
	
	private CGCTimer resetTimer;
	private Timer.Task resetTask;
	private float resetTime = 1.0f;
	
	private boolean canSprint = true;
	private float sprintLeft = 300.0f;
	private float maxSprint = 300.0f;
	private float speedMul = 1.0f;
	
	private float pullTime = 3.0f;
	private float maxPullTime = 3.0f;
	private PallBearer boss;
	private Array<ChainLink> chainLinkContacts;
	
	public CarrierCop(CGCWorld theWorld, Animation newLowAnimation,
			Animation newMidAnimation, Animation newHighAnimation,
			EntityType pEntityType, Body attachedBody, short pID) {
		super(theWorld, newLowAnimation, newMidAnimation, newHighAnimation,
				pEntityType, attachedBody, pID);
		
		setSpeedMod(0.5f);
		noGrab = true;
		
		rechargeTask = new Timer.Task() {
			
			public void run() {
				speedMul = 1.0f;
				canSprint = true;
				sprintLeft = maxSprint;
			}
		};
		
		rechargeTimer = new CGCTimer(rechargeTask, rechargeTime, false, "rechargeTimer");
		
		resetTask = new Timer.Task() {
			

			public void run() {
				noGrab = false;
				
			}
		};
		
		resetTimer = new CGCTimer(resetTask, resetTime, false, "ccResetTimer");
		canJump = false;
		chainLinkContacts = new Array<ChainLink>();
	}
	
	/*
	 * Determines the first class type in a collision
	 * 
	 * @param pb					The second entity colliding
	 */
	public void collide(PallBearer pb)
	{
		
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#collide(com.percipient24.cgc.entities.ChainLink)
	 */
	public void collide(ChainLink cl)
	{
		
	}
	
	/*
	 * @see com.percipient24.cgc.entities.players.Player#step(float, int)
	 */
	public void step(float deltaTime, int layer)
	{
		super.step(deltaTime, layer);
		
		if (pullTime <= 0)
		{
			if (body.getType() == BodyType.KinematicBody)
			{
				body.setType(BodyType.DynamicBody);
			}
		}
		
		if (layer > LayerHandler.LOW && layer <= LayerHandler.mid)
		{
			if (chainLinkContacts.size > 0 && boss != null)
			{
				for(int i = 0; i < chainLinkContacts.size; i++)
				{
					ChainLink cl = chainLinkContacts.get(i);
					
					Player left = cl.gLeft();
					int ldir = left.getDirection();
					Player right = left.getRightPlayer();
					int rdir = right.getDirection();
					Vector2 bossDif = boss.getBody().getPosition().cpy().sub(body.getPosition().cpy()).nor();
					double cos = Math.cos(Math.PI / 4);
					
					if (!(ldir == 0 && rdir == 0))
					{
						float ldot = Player.impulses.get(ldir).cpy().dot(bossDif.cpy());
						float rdot = Player.impulses.get(rdir).cpy().dot(bossDif.cpy());
						
						if (ldot < cos && rdot < cos)
						{
							pullTime -= deltaTime;
							
							if (pullTime <= 0.0f)
							{
								dismount();
							}
							
							break;
						}
					}
				}
			}
		}
	}
	
	/*
	 * Adds the new ChainLink to the ChainLink contacts
	 * 
	 * @param cl					The new ChainLink
	 */
	public void addChainLinkContact(ChainLink cl)
	{
		if (boss != null)
		{
			Player left = cl.gLeft();
			
			if (left != null)
			{
				if (left.isAlive())
				{
					Player right = left.getRightPlayer();
				
					if (right != null)
					{
						if (right.isAlive())
						{
							Vector2 dis = boss.getBody().getPosition().cpy().sub(body.getPosition().cpy()).nor();
							Vector2 clDis = cl.getBody().getPosition().cpy().sub(body.getPosition().cpy()).nor();
							
							if (clDis.dot(dis) > Math.cos(Math.PI / 3))
							{
								if (!chainLinkContacts.contains(cl, true))
								{	
									chainLinkContacts.add(cl);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/*
	 * Removes the ChainLink from the contacts list
	 * 
	 * @param cl					The ChainLink to remove
	 */
	public void removeChainLinkContact(ChainLink cl)
	{
		chainLinkContacts.removeValue(cl, true);
		
		if (chainLinkContacts.size == 0)
		{
			pullTime = maxPullTime;
		}
	}
	
	/*
	 * Determines direction and speedMul
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
		super.move(up, down, left, right, bumper);
		
		if (boss != null)
		{
			if (scheme != null)
			{
				if (scheme.getController().isPressed(ControlType.JUMP) && canSprint)
				{
					sprintLeft--;
					speedMul = 2.0f;
					if (sprintLeft == 0.0f)
					{
						canSprint = false;
						speedMul = 0.5f;
						TimerManager.addTimer(rechargeTimer);
					}
					
				}
			}
			else if (canSprint && sprintLeft < maxSprint)
			{
				sprintLeft += 0.5f;
			}
		}
	}
	
	/*
	 * Quick getter for speedMul
	 * 
	 * @return						The CarrierCop's current speed mod
	 */
	public float gSpeedMul()
	{
		return speedMul;
	}
	
	/*
	 * Sets this CarrierCop's boss
	 * 
	 * @param pb					The boss (Warning: May not be Bruce Springsteen)
	 */
	public void mount(PallBearer pb)
	{
		boss = pb;
	}
	
	/*
	 * Gets this CarrierCop removed from the PallBearer
	 */
	public void dismount()
	{
		boss.removeCarrier(this);
		boss = null;
		canJump = true;
		TimerManager.addTimer(resetTimer);
	}
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.ground);
		lh.addEntityToLayer(this, LayerHandler.mid);
		lh.addEntityToLayer(this, LayerHandler.heads);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.ground);
		lh.removeEntityFromLayer(this, LayerHandler.mid);
		lh.removeEntityFromLayer(this, LayerHandler.heads);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.players.RookieCop#getBarRatio()
	 */
	public float getBarRatio()
	{
		if (boss == null)
		{
			return super.getBarRatio();
		}
		else
		{
			return pullTime / maxPullTime;
		}
	}
}// End Class
