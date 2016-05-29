/*
 * @(#)Corpse.java		0.2 14/4/15
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities.players;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.Camera;
import com.percipient24.cgc.ChainGame;
import com.percipient24.cgc.entities.RotatableEntity;
import com.percipient24.cgc.entities.terrain.Bridge;
import com.percipient24.cgc.entities.terrain.Mud;
import com.percipient24.cgc.entities.terrain.Water;
import com.percipient24.cgc.screens.Options;
import com.percipient24.enums.AnimationState;
import com.percipient24.enums.EntityType;

/*
 * Handles the logic for a dead player
 * 
 * @version 0.2 14/4/15
 * @author William Ziegler
 */
public class Corpse extends RotatableEntity 
{
	// Movement values
	protected static final float DEAD_DAMP = 2.5f;
	
	// Player values
	protected boolean inBossFight;
	private int copNumber;
	private short playerID;
	protected int playerNumber;
	protected Timer.Task blankTask;
	
	private Vector2 terrainForce;
	protected boolean air = false;
	
	// Move impulse values
	public static final Array<Vector2> impulses = new Array<Vector2>(9);
	protected int direction = 0;
	protected int currentFacing;
	
	// Terrain variables
	protected Array<Mud> mudContacts;
	protected Array<Water> waterContacts;
	protected Array<Bridge> bridgeContacts;

	// Animation variables
	protected AnimationState animState = AnimationState.DIE;
	
	protected ChainGame chainGame;
	
	/*
	 * Creates a new Corpse object
	 * 
	 * @param deadPlayer			The Player who died to make this Corpse
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param pID					The ID of the player being created (player #)
	 * @param knockbackPosition		The position to launch this Corpse away from (may be null)
	 */
	public Corpse(Player deadPlayer, Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, short pID, Vector2 knockbackPosition)
	{
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType, attachedBody);
		
		body.setLinearDamping(DEAD_DAMP);
		
		chainGame = deadPlayer.chainGame;
		currentFacing = deadPlayer.getCurrentFacing();
		
		playerID = pID;
		playerNumber = pID-1;
		copNumber = playerNumber;
		
		terrainForce = new Vector2(0,0);
		mudContacts = new Array<Mud>();
		waterContacts = new Array<Water>();
		bridgeContacts = new Array<Bridge>();
		
		if (knockbackPosition != null)
		{
			body.applyForce(checkExplosionForce(knockbackPosition), body.getWorldCenter(), true);
		}
		
		parallaxDistMod = 6.0f;
	}
	
	/*
	 * Creates a new Corpse object
	 * 
	 * @param deadPlayer			The Player who died to make this Corpse
	 * @param newLowAnimation		The Animation for the bottom of this object
	 * @param newMidAnimation		The Animation for the middle of this object
	 * @param newHighAnimation		The Animation for the top of this object
	 * @param pEntityType			The type of entity this object is
	 * @param attachedBody			The Body object that represents this GameEntity in the world
	 * @param startAlpha			The starting alpha for this entity
	 * @param pID					The ID of the player being created (player #)
	 * @param knockbackPosition		The position to launch this Corpse away from (may be null)
	 */
	public Corpse(Player deadPlayer, Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType, 
			Body attachedBody, float startAlpha, short pID, Vector2 knockbackPosition)
	{
		this(deadPlayer, newLowAnimation, newMidAnimation, newHighAnimation, 
				pEntityType, attachedBody, pID, knockbackPosition);
		alpha = startAlpha;
	}
	
	/*
	 * Timestep-based update method for animations
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame
	 * @param layer					The parallax layer to animate
	 */
	public void step(float deltaTime, int layer)
	{	
		rotation = (gCurrentFacing() - 1) * -45.0f;
		
		switch (layer)
		{
			case LayerHandler.LOW:
				if (lowStateTime < 1f)
				{
					lowStateTime += deltaTime;
				}
				break;
			case LayerHandler.MID:
				if (midStateTime < 1f)
				{
					midStateTime += deltaTime;
				}
				break;
			case LayerHandler.HIGH:
				if (highStateTime < 1f)
				{
					highStateTime += deltaTime;
				}
				break;
			default:
				break;
		}
	}
	
	/*
	 * Sets the new player Body - only used for generating new world players
	 * 
	 * @param newBody				The new player's Body
	 */
	public void sBody(Body newBody)
	{
		body = newBody;
	}
	
	/*
	 * Gets this Corpse's ID number
	 * 
	 * @return						This Corpse's ID number
	 */
	public short gPID()
	{
		return playerID;
	}
	
	/*
	 * Gets this Corpse's cop number
	 * 
	 * @return						This Corpse's cop number
	 */
	public int gCopNumber()
	{
		return copNumber;
	}
	
	/*
	 * Gets this Corpse's number
	 * 
	 * @return						This Corpse's player number, 0-7
	 */
	public int gPNum()
	{
		return playerNumber;
	}
	
	/*
	 * Gets the number of Mud tiles this Corpse is contacting
	 * 
	 * @return						The number of touched Mud tiles
	 */
	public Array<Mud> getMudContacts()
	{
		return mudContacts;
	}
	
	/*
	 * Gets the number of Water tiles this Corpse is contacting
	 * 
	 * @return						The number of touched Water tiles
	 */
	public Array<Water> getWaterContacts()
	{
		return waterContacts;
	}
	
	/*
	 * Gets the number of Bridge tiles this Corpse is contacting
	 * 
	 * @return						The number of touched Bridge tiles
	 */
	public Array<Bridge> getBridgeContacts()
	{
		return bridgeContacts;
	}
	
	/*
	 * Adds to the number of Mud contacts
	 * 
	 * @param mud					The Mud tile to add to Mud contacts
	 */
	public void addMudContacts(Mud mud)
	{
		mudContacts.add(mud);
	}
	
	/*
	 * Adds to the number of Water contacts
	 * 
	 * @param water					The Water tile to add to Water contacts
	 */
	public void addWaterContacts(Water water)
	{
		waterContacts.add(water);
	}
	
	/*
	 * Adds to the number of Bridge contacts
	 * 
	 * @param bridge					The Bridge tile to add to Bridge contacts
	 */
	public void addBridgeContacts(Bridge bridge)
	{
		bridgeContacts.add(bridge);
	}
	
	/*
	 * Removes the specified Mud contact
	 * 
	 * @param mud					The Mud tile to remove
	 */
	public void removeMudContact(Mud mud)
	{
		mudContacts.removeValue(mud, true);
	}
	
	/*
	 * Removes the specified Water contact
	 * 
	 * @param water					The Water tile to remove
	 */
	public void removeWaterContact(Water water)
	{
		waterContacts.removeValue(water, true);
	}
	
	/*
	 * Removes the specified Bridge contact
	 * 
	 * @param bridge				The Bridge tile to remove
	 */
	public void removeBridgeContact(Bridge bridge)
	{
		bridgeContacts.removeValue(bridge, true);
	}
	
	/*
	 * Determines if the Corpse is in terrain or not
	 * 
	 * @return						Whether or not the Corpse is affected by terrain
	 */
	public boolean isInTerrain()
	{
		if (bridgeContacts.size > 0)
		{
			return false;
		}
		
		if (mudContacts.size > 0 || waterContacts.size > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * Sets the amount of force being applied to this Corpse
	 * 
	 * @param amount				The amount being applied
	 * @param dir					The direction to apply the force, 1 is up, goes clockwise
	 */
	public void addTerrainForce(float amount, int dir)
	{
		float amountRt = amount / (float)Math.sqrt(2);
		
		switch(dir)
		{
			case 0: terrainForce.add(0, 0);
				break;
			case 1: terrainForce.add(amount, 0);
				break;
			case 2: terrainForce.add(amountRt, -amountRt);
				break;
			case 3: terrainForce.add(0, -amount);
				break;
			case 4: terrainForce.add(-amountRt, -amountRt);
				break;
			case 5: terrainForce.add(-amount, 0);
				break;
			case 6: terrainForce.add(-amountRt, amountRt);
				break;
			case 7: terrainForce.add(0, amount);
				break;
			case 8: terrainForce.add(amountRt, amountRt);
				break;
			default: terrainForce.add(0,0);
				break;
		}
		
		terrainForce.nor().scl(amount);
	}
	
	/*
	 * Removes the terrain forces
	 */
	public void resetTerrainForce()
	{
		terrainForce.set(0, 0);
	}
	
	/*
	 * Gets the direction this Corpse is facing
	 * 
	 * @return						The direction the Corpse is facing
	 */
	public int gCurrentFacing()
	{
		return currentFacing;
	}

	/*
	 * Updates this Corpse
	 * 
	 * @param dt					Second elapsed since last frame
	 */
	public void updateCorpse(float dt)
	{	
		if (isInTerrain())
		{
			if (mudContacts.size > 0 && waterContacts.size <= 0)
			{
				resetTerrainForce();
			}
			else if (waterContacts.size > 0 && mudContacts.size <= 0)
			{
				for (int i = 0; i < waterContacts.size; i++)
				{
					addTerrainForce(Water.forceAmount, waterContacts.get(i).getDirection());
				}
			}
			else if (waterContacts.size > 0 && mudContacts.size > 0)
			{
				for (int i = 0; i < waterContacts.size; i++)
				{
					addTerrainForce(Water.forceAmount, waterContacts.get(i).getDirection());
				}
			}
		}
		else
		{
			resetTerrainForce();
		}
	}
	
	/*
	 * Draws this Corpse in the CGCWorld
	 * 
	 * @param sBatch				The SpriteBatch the game is using
	 * @param delta					Seconds elapsed since the last frame
	 * @param layerNumber			Which BodyFactory layer to draw
	 */
	public void draw(SpriteBatch sBatch, float delta, int layerNumber) 
	{
		Vector2 relativeScreenPosition = CGCWorld.getRelativeScreenPosition(this);
		
		if (relativeScreenPosition.x != Float.MAX_VALUE
				&& relativeScreenPosition.y != Float.MAX_VALUE)
		{
			// Handle alpha
			Color colorForAlpha = sBatch.getColor();
			colorForAlpha.a = getAlpha();
			sBatch.setColor(colorForAlpha);
			
			TextureRegion frameLow = getLowRegion();
			TextureRegion frameMid = getMidRegion();
			TextureRegion frameHigh = getHighRegion();
			
			if (layerNumber <= LayerHandler.CORPSE)
			{
				if (!CGCWorld.isPaused())
				{
					step(delta, LayerHandler.LOW);
					step(delta, LayerHandler.MID);
					step(delta, LayerHandler.HIGH);
				}
			}
			
			// Position and Rotation
			Vector2 pos = body.getPosition();
			float Xmod = 0.0f;
			float Ymod = 0.0f;
			
			float baseX = 0.0f;
			float baseY = 0.0f;
			
			
			baseX = pos.x + ((RotatableEntity)this).getImageHalfWidth(layerNumber, getRotation(layerNumber))
					+ getTransformMod().x;
			baseY = pos.y + ((RotatableEntity)this).getImageHalfHeight(layerNumber, getRotation(layerNumber))
					+ getTransformMod().y;
			
			if (isScaled())
			{
				sBatch.draw(frameLow, baseX, baseY, 0, 0, 
						frameLow.getRegionWidth(), frameLow.getRegionHeight(), 
						CGCWorld.getCamera().zoom, 
						CGCWorld.getCamera().zoom, 
						getRotation(LayerHandler.LOW));
				
				
				if (Options.storedParallaxOption)
				{
					Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / getParallaxDistMod();
					Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / getParallaxDistMod();
				}
				
				sBatch.draw(frameMid, baseX + Xmod, baseY + Ymod, 0, 0, 
						frameMid.getRegionWidth(), frameMid.getRegionHeight(), 
						CGCWorld.getCamera().zoom, 
						CGCWorld.getCamera().zoom, 
						getRotation(LayerHandler.MID));
			
				if (Options.storedParallaxOption)
				{
					Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / (getParallaxDistMod() / 2);
					Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / (getParallaxDistMod() / 2);
				}
				
				sBatch.draw(frameHigh, baseX + Xmod, baseY + Ymod, 0, 0, 
						frameHigh.getRegionWidth(), frameHigh.getRegionHeight(), 
						CGCWorld.getCamera().zoom, 
						CGCWorld.getCamera().zoom, 
						getRotation(LayerHandler.HIGH));
			}
			else // Not scaled
			{
				sBatch.draw(frameLow, baseX, baseY, 0, 0, 
						frameLow.getRegionWidth(), frameLow.getRegionHeight(), 
						CGCWorld.getCamera().zoom, 
						CGCWorld.getCamera().zoom, 
						getRotation(LayerHandler.LOW));
				
				
				if (Options.storedParallaxOption)
				{
					Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / getParallaxDistMod();
					Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / getParallaxDistMod();
				}
				
				sBatch.draw(frameMid, baseX + Xmod, baseY + Ymod, 0, 0, 
						frameMid.getRegionWidth(), frameMid.getRegionHeight(), 
						CGCWorld.getCamera().zoom, 
						CGCWorld.getCamera().zoom, 
						getRotation(LayerHandler.MID));
			
				if (Options.storedParallaxOption)
				{
					Xmod = Camera.PARALLAX_MOD * relativeScreenPosition.x / (getParallaxDistMod() / 2);
					Ymod = Camera.PARALLAX_MOD * relativeScreenPosition.y / (getParallaxDistMod() / 2);
				}
				
				sBatch.draw(frameHigh, baseX + Xmod, baseY + Ymod, 0, 0, 
						frameHigh.getRegionWidth(), frameHigh.getRegionHeight(), 
						CGCWorld.getCamera().zoom, 
						CGCWorld.getCamera().zoom, 
						getRotation(LayerHandler.HIGH));
			}
			
			colorForAlpha = sBatch.getColor();
			colorForAlpha.a = 1.0f;
			sBatch.setColor(colorForAlpha);
		}
	}
	
	/*
	 * Applies the total force from all terrain objects
	 */
	public void applyTerrainForces()
	{
		body.applyForce(terrainForce, body.getWorldCenter(), true);
	}
	
	/*
	 * Calculate the force an Explosion should apply to this Corpse
	 * 
	 * @param ePosition					The position of the Explosion
	 */
	private Vector2 checkExplosionForce(Vector2 ePosition)
	{
		Vector2 eForce = body.getWorldCenter().cpy();
		
		eForce.sub(ePosition).scl(200);
		
		return eForce;
	}
	
	/*
	 * Applies a force to this Corpse
	 * 
	 * @param direction				The direction of the force
	 * @param strength				The strength of the force
	 */
	public void applyForceToSelf(int direction, int strength)
	{
		Vector2 appliedForce = impulses.get(direction).cpy();
		appliedForce.scl(strength);
		body.applyForce(appliedForce, body.getWorldCenter(), true);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void addToWorldLayers(LayerHandler lh) 
	{
		lh.addEntityToLayer(this, LayerHandler.corpses);
	}
	
	/*
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.b2dhelpers.BodyFactory)
	 */
	public void removeFromWorldLayers(LayerHandler lh) 
	{
		lh.removeEntityFromLayer(this, LayerHandler.corpses);
	}
	
	/*
	 * Gets the position of this Corpse
	 * 
	 * @return						The position of the Corpse's body
	 */
	public Vector2 gPosition()
	{
		return body.getPosition();
	}
	
	/*
	 * Sets the ChainGame for this Corpse
	 * 
	 * @param cg					The ChainGame to add to this Corpse
	 */
	public void setChainGame(ChainGame cg)
	{
		chainGame = cg;
	}
} // End class
