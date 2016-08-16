/*
 * @(#)SpotlightCop.java		0.3 14/4/17
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities.players;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.percipient24.cgc.art.TextureAnimationDrawer;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.Spotlight;
import com.percipient24.cgc.entities.Targeter;
import com.percipient24.cgc.entities.boss.Boss;
import com.percipient24.cgc.entities.boss.Tank;
import com.percipient24.enums.AnimationState;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a spotlight cop
 * 
 * @version 0.3 14/4/17
 * @author Christopher Rider
 */
public class SpotlightCop extends RookieCop 
{
	private Targeter spotlight;
	
	/*
	 * Creates a new SpotlightCop object
	 * 
	 * @param theWorld				Reference to the CGCWorld class (for accessing data available therein)
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param pID					The ID of the player being created (player #)
	 * @param cameraPosX			The X position of the camera
	 * @param caemraPosY			The Y position of the camera
	 */
	public SpotlightCop(CGCWorld theWorld, Animation newLowAnimation, 
			Animation newMidAnimation, Animation newHighAnimation, 
			EntityType pEntityType, Body attachedBody, short pID, 
			float cameraPosX, float cameraPosY)
	{
		super(theWorld, newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, pID);
		
		body.getFixtureList().get(0).setSensor(true);

		// Create spotlight
		Body b = CGCWorld.getBF().createCircle(cameraPosX, cameraPosY, 
				2.1f, BodyType.DynamicBody, BodyFactory.CAT_INTERACTABLE, BodyFactory.MASK_INTERACTABLE);
		b.getFixtureList().get(0).setSensor(true);
		b.setFixedRotation(true);
		spotlight = new Spotlight(null, null, TextureAnimationDrawer.spotlightAnim,
				EntityType.TARGETER, b, CGCWorld.getCamera(), getPID());
		b.setUserData(spotlight);
		spotlight.addToWorldLayers(CGCWorld.getLH());
		
		alive = true;
		lowState = AnimationState.STAND;
	}
	
	/*
	 * Move the SpotlightCop's spotlight around the screen
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
		spotlight.move(up, down, left, right, bumper);
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
	public void collide(Tank t)
	{
		return; // Do nothing
	}
	
	public void collide(Spotlight s)
	{
		return; // Do nothing
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.bossPlayer);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
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
		
		spotlight.updateTarget(delta, CGCWorld.getCamera());
	}
}
