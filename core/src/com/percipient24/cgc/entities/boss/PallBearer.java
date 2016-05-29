/* @(#)PallBearer.java		0.3 14/4/10
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities.boss;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.BossFight;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.players.CarrierCop;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.b2dhelpers.BodyFactory;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for the PallBearer Boss
 * 
 * @version 0.3 14/4/10
 * @author JD Kelly
 */
public class PallBearer extends Boss
{
	//The four cops that are carrying the Sheriff
	private CarrierCop ulCop = null;
	private CarrierCop urCop = null;
	private CarrierCop dlCop = null;
	private CarrierCop drCop = null;
	private Sheriff sheriff;
	private boolean swapped = false;
	private boolean carried = true;
	
	private Array<CarrierCop> cops = new Array<CarrierCop>();
	
	private float maxSpeed = 50.0f;

	public PallBearer(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType,
			Body attachedBody) {
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType,
				attachedBody);
		
		Body b = CGCWorld.getBF().createCircle(body.getPosition().cpy().x, body.getPosition().cpy().y,0.6f, 
				BodyType.DynamicBody, BodyFactory.CAT_NON_INTERACTIVE, BodyFactory.MASK_NON_INTERACTIVE);
		
		sheriff = new Sheriff(AnimationManager.sheriffAnim, AnimationManager.sheriffAnim, 
				AnimationManager.sheriffAnim, EntityType.SHERIFF, b, this);
		b.setUserData(sheriff);
		sheriff.addToWorldLayers(CGCWorld.getLH());
		sheriff.addTargeter();
	}

	/*
	 * Timestep-based update method
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The parallax layer to animate
	 */
	public void step(float deltaTime, int layer) 
	{
		Vector2 desVel = Vector2.Zero;
		
		for (CarrierCop cc:cops)
		{
			desVel = desVel.cpy().add(Player.impulses.get(cc.getDirection()).cpy().scl(maxSpeed * cc.gSpeedMul() / 4));
		}
		
		body.setLinearVelocity(desVel);
		
		for (CarrierCop cc:cops)
		{
			cc.getBody().setLinearVelocity(desVel);
		}
		
		sheriff.getBody().setTransform(body.getPosition().cpy(), rotation);
	}
	
	/*
	 * Makes the PallBearer pause its timers
	 * @see com.percipient24.cgc.entities.Boss#pause()
	 */
	public void pause()
	{
		
	}
	
	/*
	 * Makes the PallBearer resume its timers
	 * @see com.percipient24.cgc.entities.Boss#pause()
	 */
	public void resume()
	{
		
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
		
		if (!swapped)
		{	
			lh.removeEntityFromLayer(this, LayerHandler.mid);
			lh.removeEntityFromLayer(this, LayerHandler.bossTop);
			Gdx.app.log("Swapped", "PB");
		}
		else
		{
			lh.removeEntityFromLayer(this, LayerHandler.ground);
		}
	}
	
	/*
	 * Sets the first unavailable carrier position to the passed in CarrierCop
	 * 
	 * @param cc					A CarrierCop to help carry this
	 */
	public void addCarrier(CarrierCop cc)
	{
		if (ulCop == null)
		{
			ulCop = cc;
			ulCop.getBody().setTransform(body.getPosition().cpy().add(getImageHalfWidth(0), -getImageHalfHeight(0)), 0);
			cops.add(cc);
			cc.mount(this);
		} 
		else if (dlCop == null)
		{
			dlCop = cc;
			dlCop.getBody().setTransform(body.getPosition().cpy().add(getImageHalfWidth(0), getImageHalfHeight(0)), 0);
			cops.add(cc);
			cc.mount(this);
		} 
		else if (urCop == null)
		{
			urCop = cc;
			urCop.getBody().setTransform(body.getPosition().cpy().add(-getImageHalfWidth(0), -getImageHalfHeight(0)), 0);
			cops.add(cc);
			cc.mount(this);
		} 
		else
		{
			drCop = cc;
			drCop.getBody().setTransform(body.getPosition().cpy().sub(-getImageHalfWidth(0), getImageHalfHeight(0)), 0);
			cops.add(cc);
			cc.mount(this);
		}
	}
	
	/*
	 * Removes the CarrierCop from cops
	 * 
	 * @param cc					The CarrierCop to be removed
	 */
	public void removeCarrier(CarrierCop cc)
	{
		if (cops.contains(cc, true))
		{
			cops.removeValue(cc, true);
			
			if (cc == ulCop)
			{
				ulCop = null;
			}
			else if (cc == urCop)
			{
				urCop = null;
			}
			else if (cc == dlCop)
			{
				dlCop = null;
			}
			else if (cc == drCop)
			{
				drCop = null;
			}
			
			if (((ulCop == null) && (urCop == null) && (dlCop == null) && (drCop == null)))
			{
				removeFromWorldLayers(BossFight.getLH());
				sheriff.swapWorldLayers(BossFight.getLH());
				swapped = true;
				carried = false;
			}
		}
	}
	
	/*
	 * Determines the first class type in a collision
	 * 
	 * @param c						The second entity colliding
	 */
	public void collide(CarrierCop cc)
	{
		cc.collide(this);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.boss.Boss#isDefeated()
	 */
	public boolean isDead()
	{
		return !carried;
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
	 * @see com.percipient24.cgc.entities.boss.Boss#update(float)
	 */
	public void update(float deltaTime)
	{
		sheriff.update(deltaTime);
	}

}// End Class
