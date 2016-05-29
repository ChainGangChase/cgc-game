/*
 * @(#)Explosion.java		0.2 14/3/12
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.players.RookieCop;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for an explosion entity
 * 
 * @version 0.2 14/3/12
 * @author William Ziegler
 */
public class Explosion extends GameEntity
{
	private float radius;
	
	private final float lifetime = 4/60f;
	private float timeActive;
	
	public Explosion(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType,
			Body attachedBody, float newRadius)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);

		radius = newRadius;
		timeActive = 0;
	}
	
	/*
	 * Timestep-based update method
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The parallax layer to animate
	 */
	public void step(float deltaTime, int layer)
	{
		timeActive += deltaTime;
		if (timeActive >= lifetime)
		{
			CGCWorld.addToDestroyList(this);
		}
		else
		{
			for (int i = 0; i < CGCWorld.getPlayers().size; i++)
			{
				if (CGCWorld.getPlayers().get(i) instanceof RookieCop || 
						!CGCWorld.getPlayers().get(i).isAlive())
				{
					continue;
				}
				
				float a = this.getBody().getWorldCenter().x - CGCWorld.getPlayers().get(i).getBody().getWorldCenter().x;
				float b = this.getBody().getWorldCenter().y - CGCWorld.getPlayers().get(i).getBody().getWorldCenter().y;
				
				float dist = a*a + b*b;
				
				if (dist < radius * radius)
				{
					collide(CGCWorld.getPlayers().get(i));
				}
			}
		}
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
		p.collide(this);
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
	public void collide(Fence f)
	{
		f.collide(this);
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.ground);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.ground);
	}
} // End class