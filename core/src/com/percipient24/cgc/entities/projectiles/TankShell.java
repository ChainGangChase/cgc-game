/*
 * @(#)TankShell.java		0.2 14/2/28
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.percipient24.cgc.art.TextureAnimationDrawer;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.BossFight;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.Explosion;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.Tree;
import com.percipient24.cgc.entities.Fence;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a tank shell entity
 * 
 * @version 0.2 14/2/28
 * @author Christopher Rider
 * @author William Ziegler
 */
public class TankShell extends Projectile 
{	
	private final float EXPLOSION_RADIUS = 1.6f;
	
	/*
	 * Creates a new TankShell object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param target				The target to fire this shell at
	 * @param targetOffset			The offset required to find the center of the target
	 */
	public TankShell(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody,
			Vector2 target, Vector2 targetOffset)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, target, targetOffset);
		Flight_Speed = 4.0f;
	}
	
	/*
	 * Creates a new TankShell object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param target				The target to fire this shell at
	 * @param targetOffset			The offset required to find the center of the target
	 * @param theFight				The BossFight which is using this TankShell
	 * @param startAlpha			The starting alpha for this entity
	 */
	public TankShell(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, Body attachedBody, 
			Vector2 target, Vector2 targetOffset, 
			BossFight theFight, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, 
				target, targetOffset);
		alpha = startAlpha;
	}
	
	/*
	 * Move this tank shell
	 */
	public void move()
	{
		if (!toDestroy())
		{
			super.move();
		}
	}
	
	/*
	 * Timestep-based update method
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The parallax layer to animate
	 */
	public void step(float deltaTime, int layer) 
	{
		// TODO Add a travel animation to a target point - handle collision near target point
	}
	
	/*
	 * Determines the first class type in a collision
	 * 
	 * @param ge					The second entity colliding
	 */
	public void collide (GameEntity ge)
	{
		ge.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param p						The first entity colliding
	 */
	public void collide (Player p)
	{
		p.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param p						The first entity colliding
	 */
	public void collide (Prisoner p)
	{
		CGCWorld.addToDestroyList(this);
		p.collide(this);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param t						The first entity colliding
	 */
	public void collide (Tree t)
	{
		CGCWorld.addToDestroyList(this);
		CGCWorld.addToDestroyList(t);
	}
	
	/*
	 * Determines the second class type in a collision
	 * 
	 * @param f						The first entity colliding
	 */
	public void collide (Fence f)
	{
		CGCWorld.addToDestroyList(this);
		CGCWorld.addToDestroyList(f);
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.projectile);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.projectile);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#finalCleanup()
	 */
	public void finalCleanup()
	{
		Body explosionBody = CGCWorld.getBF().createCircle(this.getBody().getWorldCenter().x, 
				this.getBody().getWorldCenter().y, EXPLOSION_RADIUS * 2, BodyType.StaticBody, 
				BodyFactory.CAT_EXPLOSIVE, BodyFactory.MASK_EXPLOSIVE);
		GameEntity ge = new Explosion(TextureAnimationDrawer.explosionAnim, null, null, EntityType.TANK_SHELL,
				explosionBody, EXPLOSION_RADIUS);
		explosionBody.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
	}
} // End class