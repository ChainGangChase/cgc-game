/*
 * @(#)PlayerWall.java		0.2 14/2/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a player wall entity
 * 
 * @version 0.2 14/2/24
 * @author JD Kelly
 */
public class PlayerWall extends GameEntity 
{
	private boolean top;
	
	/*
	 * Creates a new PlayerWall object
	 * 
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param topWall				If this is the top or bottom wall
	 */
	public PlayerWall(EntityType pEntityType, Body attachedBody, boolean topWall) 
	{
		super(null, null, null, pEntityType, attachedBody);
		
		top = topWall;
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
	 * Gets the current TextureRegion (frame) of this PlayerWall's current Animation
	 *
	 * @return				This PlayerWall's current TextureRegion/"frame" (from its Animation)
	 */
	public TextureRegion getCurrentTextureRegion() 
	{
		return null;
	}
	
	/*
	 * Determines the first class type in a collision
	 * 
	 * @param g						The second entity colliding
	 */
	public void collide(GameEntity g)
	{
		g.collide(this);
	}
	
	public void endCollide(Player p)
	{
		p.endCollide(this);
	}
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.aerial);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.aerial);
	}
	
	/*
	 * Gets whether or not this PlayerWall is in the LayerHandler
	 * 
	 * @return						Whether or not this PlayerWall is in the layer handler
	 */
	public boolean inLayerHandler()
	{
		return CGCWorld.getLH().getLayer(LayerHandler.aerial).getEntities().contains(this, false);
	}
	
	/*
	 * Gets whether or not this PlayerWall is the top wall
	 * 
	 * @return						Whether or not this is the top wall
	 */
	public boolean isTop()
	{
		return top;
	}
} // End class