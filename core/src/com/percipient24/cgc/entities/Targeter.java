/*
 * @(#)Targeter.java		0.2 14/3/5
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.Camera;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for GunCops' crosshairs
 * 
 * @version 0.2 14/3/5
 * @author Christopher Rider
 */
public class Targeter extends GameEntity 
{
	private final float TARGET_SPEED = 2000.0f;
	public static final Array<Vector2> impulses = new Array<Vector2>(9);
	protected int direction = 0;
	protected Vector2 prevCamPos;
	protected Vector2 curCamPos;
	protected int ownerID; // Zero based
	
	/*
	 * Creates a new Targeter object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param c						The Camera the game is currently using
	 * @param ownerID				The player ID of the player that owns this Targeter
	 */
	public Targeter(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, Camera c, int ownerID)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		if(impulses.size == 0)
		{
			float leg = TARGET_SPEED / (float)Math.sqrt(2);
			
			//Neutral
			impulses.add(new Vector2(0, 0));
			//Up
			impulses.add(new Vector2(0, TARGET_SPEED));
			//Up Right
			impulses.add(new Vector2(leg, leg));
			//Right
			impulses.add(new Vector2(TARGET_SPEED, 0));
			//Down Right
			impulses.add(new Vector2(leg, -leg));
			//Down
			impulses.add(new Vector2(0, -TARGET_SPEED));
			//Down Left
			impulses.add(new Vector2(-leg, -leg));
			//Left
			impulses.add(new Vector2(-TARGET_SPEED, 0));
			//Up Left
			impulses.add(new Vector2(-leg, leg));
			
			for(int i = 0; i < impulses.size; i++)
			{
				impulses.get(i).scl(0.003f);
			}
			
			curCamPos = new Vector2(c.position.x, c.position.y);
		}
		this.ownerID = ownerID;
	}
	
	/*
	 * Creates a new Targeter object
	 * 
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param c						The Camera the game is currently using
	 * @param ownerID				The player ID of the player that owns this target
	 * @param startAlpha			The starting alpha for this entity
	 */
	public Targeter(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, Camera c, int ownerID, float startAlpha)
	{
		this(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody, c, ownerID);
		alpha = startAlpha;
	}
	
	/*
	 * Move the Targeter across the screen
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
		if(up && !(left || right || down))
		{
			// up
			direction = 1;
		}
		else if((up && right) && !(down || left))
		{
			// up right
			direction = 2;
		}
		else if(right && !(left || up || down))
		{
			// right
			direction = 3;
		}
		else if((down && right) && !(up || left))
		{
			// down right
			direction = 4;
		}
		else if(down && !(left || up || right))
		{
			// down
			direction = 5;
		}
		else if((down && left) && !(up || right))
		{
			// down left
			direction = 6;
		}
		else if(left && !(up || right || down))
		{
			// left
			direction = 7;
		}
		else if((up && left) && !(down || right))
		{
			// up left
			direction = 8;
		}
		
		body.setLinearVelocity(impulses.get(direction).cpy());
	}
	
	/*
	 * Updates this Targeter
	 * 
	 * @param delta					Seconds elapsed since the last frame
	 * @param c						The camera for the game
	 */
	public void updateTarget(float delta, Camera c)
	{
		if (curCamPos == null)
		{
			curCamPos = new Vector2(CGCWorld.getCamera().position.x, CGCWorld.getCamera().position.y);
		}
		prevCamPos = curCamPos.cpy();
		curCamPos = new Vector2(c.position.x, c.position.y);
		
		Vector2 dif = new Vector2(curCamPos.x-prevCamPos.x, curCamPos.y-prevCamPos.y);
		body.setTransform(body.getPosition().x+dif.x, body.getPosition().y+dif.y, body.getAngle());
		
		confineToScreen(c);
	}
	
	/*
	 * Confines the Targeter to the screen
	 * 
	 * @param c						The camera for the screen
	 */
	protected void confineToScreen(Camera c)
	{
		Vector2 tarPosScreen = c.toScreenPos(body.getPosition()).cpy();
		
		// Keep on screen horizontally
		if (tarPosScreen.x < c.getTopLeftCorner().x + 0.5f)
		{
			Vector2 newPos = new Vector2(c.toWorldPos(c.getTopLeftCorner()).x + 0.5f, body.getPosition().y);
			body.setTransform(newPos, body.getAngle());
		}
		else if (tarPosScreen.x > c.getBotRightCorner().x - 0.5f)
		{
			Vector2 newPos = new Vector2(c.toWorldPos(c.getBotRightCorner()).x - 0.5f, body.getPosition().y);
			body.setTransform(newPos, body.getAngle());
		}
		
		// Keep on screen vertically
		if (tarPosScreen.y < c.getBotRightCorner().y)
		{
			Vector2 newPos = new Vector2(body.getPosition().x, c.toWorldPos(c.getBotRightCorner()).y);
			body.setTransform(newPos, body.getAngle());
		}
		else if (tarPosScreen.y > c.getTopLeftCorner().y)
		{
			Vector2 newPos = new Vector2(body.getPosition().x, c.toWorldPos(c.getTopLeftCorner()).y);
			body.setTransform(newPos, body.getAngle());
		}
	}
	
	/*
	 * Gets the playerID of the player who owns this Targeter
	 * 
	 * @return						The playerID of the player who owns this Targeter
	 */
	public int getOwner()
	{
		return ownerID;
	}
	
	/*
	 * Timestep-based update method
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The BodyFactory layer to animate
	 */
	public void step(float deltaTime, int layer) 
	{
		
	}

	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.aerial);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.aerial);
	}
} // End class