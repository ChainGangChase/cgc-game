/*
 * @(#)Camera.java		0.2 14/2/13
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.percipient24.b2dhelpers.BodyFactory;
import com.percipient24.cgc.boss.TankBuilder;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.PlayerWall;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.players.RookieCop;
import com.percipient24.enums.EntityType;

/*
 * A class for controlling the game's cameras more easily
 * @version 0.2 14/2/13
 * @author JD Kelly
 * @author William Ziegler
 * @author Christopher Rider
 */
public class Camera extends OrthographicCamera 
{
	private Vector2 camMes;
	private Vector2 parallaxPoint;
	private PlayerWall upperWall = null;
	private PlayerWall lowerWall = null;
	private float wallHeight = 0.1f;
	private Vector2 topLeftScreen;
	private Vector2 botRightScreen;
	private boolean locked = false;
	private float lockHeight = 0.0f;
	public static final float PARALLAX_MOD = 0.5f;
	public static float prisonerWeight = 0.75f;

	
	/*
	 * Creates a basic camera with new viewport height or viewport width
	 */
	public Camera()
	{
		super();
		
		createWalls();

		parallaxPoint = new Vector2(position.x, position.y);
	}
	
	/*
	 * Creates a camera with the given viewport width and height
	 * @param vpw                   The desired viewport width
	 * @param vph                   The desired viewport height
	 * @param menu					Whether or not this camera is for the menu
	 */
	public Camera(float vpw, float vph, boolean menu)
	{
		super(vpw, vph);
		camMes = new Vector2(vpw, vph);
		viewportWidth = vpw;
		viewportHeight = vph;
		
		if (!menu)
		{
			createWalls();
				
			parallaxPoint = new Vector2(position.x, position.y);
			
			topLeftScreen = toScreenPos(new Vector2(getLeftEdge(), getTopEdge()));
			botRightScreen = toScreenPos(new Vector2(getRightEdge(), getBottomEdge()));
		}
	}
	
	/*
	 * Adjusts the Camera to move with players
	 * 
	 * @param players               The Array of Player objects from game
	 * @param showCops				Whether or not cops should be included in calculations
	 */
	public void adjust(Array<Player> players, boolean showCops)
	{
		if (!CGCWorld.terminated())
		{
			if(!locked)
			{
				if (showCops)
				{
					if(CGCWorld.getNumCops() == 0)
					{
						setPosition(9.5f, Math.max(viewportHeight * Data.WORLD_TO_BOX / 2 + 1.8f, CGCWorld.getPrisonerAverageY()));
					}
					else
					{
						if(CGCWorld.getNumFreePrisoners() > 0)
						{
							setPosition(9.5f, Math.max(viewportHeight * Data.WORLD_TO_BOX / 2 + 1.8f, calcCamPos()));
						}
						else
						{
							setPosition(9.5f, Math.max(viewportHeight * Data.WORLD_TO_BOX / 2 + 1.8f, calcCamPos()));
						}
					}
				}
				else
				{
					setPosition(9.5f, Math.max(CGCWorld.getPrisonerAverageY(), (viewportHeight * Data.WORLD_TO_BOX / 2) + 1.8f));
				}
				
				float pDis = CGCWorld.getPlayerMaximum() - CGCWorld.getPlayerMinimum();
				
				
				if(CGCWorld.isBossFight() && BossFight.getLevel() != null && BossFight.getLevel() instanceof TankBuilder)
				{
					pDis = CGCWorld.getPrisonerMaximum() - CGCWorld.getPrisonerMinimum();
					
				}
				
				pDis *= Data.BOX_TO_WORLD;
				if(CGCWorld.allChainedTogether())
				{
					if(pDis > camMes.y)
					{
						viewportHeight = Math.min(pDis, camMes.y * 2.0f);
						viewportWidth = viewportHeight * camMes.x / camMes.y;
					}
					else
					{
						viewportHeight = camMes.y;
						viewportWidth = camMes.x;
					}
				}
				else if(!CGCWorld.allChainedTogether() && pDis > camMes.y)
				{
					viewportHeight = Math.min(camMes.y * 1.5f, pDis);
					viewportWidth = viewportHeight * camMes.x / camMes.y;
				}
				else if(!CGCWorld.allChainedTogether() && viewportHeight > camMes.y && pDis < camMes.y)
				{
					
					viewportHeight *= .998f;
					viewportHeight = Math.max(viewportHeight, camMes.y);
					viewportWidth = viewportHeight * camMes.x / camMes.y;
				}
				else
				{
					viewportHeight = camMes.y;
					viewportWidth = camMes.x;
				}
				
				update();
				
				topLeftScreen = toScreenPos(new Vector2(getLeftEdge(), getTopEdge()));
				botRightScreen = toScreenPos(new Vector2(getRightEdge(), getBottomEdge()));
			}
			else
			{
				setPosition(new Vector3(position.x, lockHeight, 0));
				update();
			}
		}
	}
	
	/*
	 * Quick setter for position using Vector3
	 * 
	 * @param v                     The new Vector3 for position
	 */
	public void setPosition(Vector3 v)
	{
		position.set(v.x, v.y, v.z);
		
		if(!locked)
		{
			float te = getTopEdge();
			if (upperWall != null)
			{
				upperWall.getBody().setTransform(new Vector2(position.x, te), 0);
			}
			
			float be = getBottomEdge();
			if (lowerWall != null)
			{
				lowerWall.getBody().setTransform(new Vector2(position.x, be), 0);
			}
		}
	}
	
	/*
	 * Sets this Camera's position using Vector2 (Z will be set to 0)
	 * 
	 * @param v                     Vector2 with the new X and Y values for position
	 */
	public void setPosition(Vector2 v)
	{
		setPosition(new Vector3(v.x, v.y, 0));
	}
	
	/*
	 * Sets this Camera's position using 3 values for X, Y, and Z
	 * 
	 * @param x                     The new X value for position
	 * @param y                     The new Y value for position
	 * @param z                     The new Z value for position
	 */
	public void setPosition(float x, float y, float z)
	{
		setPosition(new Vector3(x, y, z));
	}
	
	/*
	 * Sets this Camera's position using 2 values for X and Y (Z will be set to 0)
	 * 
	 * @param x                     The new X value for position
	 * @param y                     The new Y value for position
	 */
	public void setPosition(float x, float y)
	{
		setPosition(new Vector3(x, y, 0));
	}
	
	/*
	 * Set the point this Camera uses for parallax calculations
	 * 
	 * @param x                     The point's new X-coordinate
	 * @param y                     The point's new Y-coordinate
	 */
	public void setParallaxPoint(float x, float y)
	{
		parallaxPoint = new Vector2(x, y);
	}
	
	/*
	 * Gets the initial width and height of the viewport
	 * 
	 * @return						The initial width and height of the viewport
	 */
	public Vector2 getCamMes()
	{
		return camMes;
	}
	
	/*
	 * Returns the true center of the Camera
	 * 
	 * @return						The true screen center
	 */
	public Vector2 getCameraCenter()
	{
		return new Vector2(position.x+0.5f, position.y);
	}
	
	/*
	 * Gets this Camera's left edge in box coordinates
	 * 
	 * @return                      The X-position of the left edge of the camera's view in world co-ords
	 */
	private float getLeftEdge()
	{
		return position.x-((viewportWidth * Data.SCREEN_TO_BOX) / 2);
	}
	
	/*
	 * Gets this Camera's right edge in box coordinates
	 * 
	 * @return                      The X-position of the right edge of the camera's view in world co-ords
	 */
	private float getRightEdge()
	{
		return position.x+((viewportWidth/2 - 1) * Data.SCREEN_TO_BOX);
	}
	
	/*
	 * Gets this Camera's top edge in box coordinates
	 * 
	 * @return                      The Y-position of the top edge of the camera's view in world co-ords
	 */
	private float getTopEdge()
	{
		return (position.y + (((viewportHeight - 2) * Data.SCREEN_TO_BOX) / 2));
	}
	
	/*
	 * Gets this Camera's bottom edge in box coordinates
	 * 
	 * @return                      The Y-position of the bottom edge of the camera's view in world co-ords
	 */
	private float getBottomEdge()
	{
		return (position.y - ((viewportHeight * Data.SCREEN_TO_BOX) / 2));
	}
	
	/*
	 * Gets this Camera's top-left corner
	 * 
	 * @return						The top-left corner of the camera's view in world co-ords
	 */
	public Vector2 getTopLeftCorner()
	{
		return topLeftScreen;
	}
	
	/*
	 * Gets this Camera's bottom-right corner
	 * 
	 * @return						The bottom-right corner of the camera's view in world co-ords
	 */
	public Vector2 getBotRightCorner()
	{
		return botRightScreen;
	}
	
	/*
	 * Quick getter for the upper wall
	 * 
	 * @return                      The upper wall
	 */
	public PlayerWall getUpperWall()
	{
		return upperWall;
	}

	/*
	 * Quick getter for the lower wall
	 * 
	 * @return                      The lower wall
	 */
	public PlayerWall getLowerWall()
	{
		return lowerWall;
	}

	/*
	 * Gets the point this Camera uses for parallax calculations
	 * 
	 * @return						The point used for parallax calculations
	 */
	public Vector2 getParallaxPoint()
	{
		return parallaxPoint;
	}
	
	/*
	 * Determines if the given Player is onscreen
	 * 
	 * @param p                     The Player to be checked
	 * @return                      Whether or not p is onscreen
	 */
	public boolean isOnScreen(Player p)
	{
		if (isOnScreenDir(p) == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * Checks if the specified Player is on-screen
	 * 
	 * @param p						The Player to check for
	 * @return						Returns 0 if onscreen, +-1 if below/above screen
	 */
	public int isOnScreenDir(Player p)
	{
		if (CGCWorld.terminated())
		{
			return 0;
		}
		
		Vector2 screenPos = toScreenPos(p.getPosition());
			
		if (screenPos.y < botRightScreen.y)
		{
			return 1;
		}
		else if (screenPos.y > topLeftScreen.y)
		{
			return -1;
		}
		return 0;
	}
	
	/*
	 * Determines if the given Vector2 is onscreen
	 * 
	 * @param v                     The Vector2 to be checked
	 * @return                      Whether or not the Vector2 is onscreen
	 */
	public boolean isOnScreen(Vector2 v)
	{
		if (isOnScreenDir(v) == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * Checks if the specified Vector2 is on-screen
	 * 
	 * @param v						The Vector2 to check for
	 * @return						Returns 0 if onscreen, +-1 if below/above screen
	 */
	public int isOnScreenDir(Vector2 v)
	{
		if (CGCWorld.terminated())
		{
			return 0;
		}
		
		Vector2 screenPos = toScreenPos(v);
			
		if (screenPos.y < botRightScreen.y)
		{
			return 1;
		}
		else if (screenPos.y > topLeftScreen.y)
		{
			return -1;
		}
		return 0;
	}
	
	/*
	 * Gets whether or not this Camera is locked
	 * 
	 * @return						Whether or not this Camera is locked
	 */
	public boolean isLocked()
	{
		return locked;
	}
	
	/*
	 * Determines the position of a GameEntity relative to the Camera
	 * 
	 * @param ge                    The GameEntity to be checked
	 * @return                      The position of the GameEntity relative to the Camera
	 */
	public Vector2 posRelativeToCamera(GameEntity ge)
	{
		Vector2 pos = ge.getBody().getPosition();
		Vector2 rPos = new Vector2();
		
		float relativePosX = (pos.x - parallaxPoint.x) / ((getRightEdge() - getLeftEdge()) / 2);
		
		if (Math.abs(relativePosX) > 2.5f)
		{
			rPos.x = Float.MAX_VALUE;
			rPos.y = Float.MAX_VALUE;
			return rPos;
		}
		else
		{
			rPos.x = relativePosX;
		}
		
		float relativePosY;
		
		
		relativePosY = (pos.y - parallaxPoint.y) / ((getTopEdge() - getBottomEdge()) / 2);
		
		if (Math.abs(relativePosY) > 2.5f)
		{
			rPos.y = Float.MAX_VALUE;
		}
		else
		{
			rPos.y = relativePosY;
		}
		return rPos;
	}
	
	/*
	 * Determines the position of a point relative to the Camera
	 * 
	 * @param pos                   The point to be checked
	 * @return                      The position of the point relative to the Camera
	 */
	public Vector2 posRelativeToCamera(Vector2 pos)
	{
		Vector2 rPos = new Vector2();
		
		float relativePosX = (pos.x - parallaxPoint.x) / ((getRightEdge() - getLeftEdge()) / 2);
		
		if (Math.abs(relativePosX) > 2.5f)
		{
			rPos.x = Float.MAX_VALUE;
			rPos.y = Float.MAX_VALUE;
			return rPos;
		}
		else
		{
			rPos.x = relativePosX;
		}
		
		float relativePosY;
		
		
		relativePosY = (pos.y - parallaxPoint.y) / ((getTopEdge() - getBottomEdge()) / 2);
		
		if (Math.abs(relativePosY) > 2.5f)
		{
			rPos.y = Float.MAX_VALUE;
		}
		else
		{
			rPos.y = relativePosY;
		}
		return rPos;
	}
	
	/*
	 * Converts screen coordinates to world coordinates
	 * 
	 * @param screenPos				The screen position of an object
	 * @return						The world position of the object
	 */
	public Vector2 toWorldPos(Vector2 screenPos)
	{
		Vector2 worldPos = new Vector2(0, 0);
		
		worldPos.x = screenPos.x + position.x;
		worldPos.y = screenPos.y + position.y;
		
		return worldPos;
	}
	
	/*
	 * Converts world coordinates to screen coordinates
	 * 
	 * @param worldPos				The world position of an object
	 * @return						The screen position of the object
	 */
	public Vector2 toScreenPos(Vector2 worldPos)
	{
		Vector2 screenPos = new Vector2(0, 0);
		
		screenPos.x = worldPos.x - position.x;
		screenPos.y = worldPos.y - position.y;
		
		return screenPos;
	}
	
	/*
	 * Sets CamMes and VPH
	 * 
	 * @param v						The Vector2 for VPH/VPW
	 */
	public void setViewportHeight(Vector2 v)
	{
		camMes = v;
		viewportWidth = camMes.x;
		viewportHeight = camMes.y;
		setPosition(position);
	}
	
	/*
	 * Locks the camera in place
	 */
	public void lock()
	{
		locked = true;
		upperWall.getBody().setTransform(new Vector2(position.x, Float.MAX_VALUE), 0);
		lowerWall.getBody().setTransform(new Vector2(position.x, Float.MIN_VALUE), 0);
		lockHeight = position.y;
	}
	
	/*
	 * Unlocks the camera
	 */
	public void unlock()
	{
		locked = false;
		setPosition(position);
	}
	
	/*
	 * Resizes the window
	 * 
	 * @param width					The new width of the window
	 * @param height				The new height of the window
	 * @see com.percipient24.cgc.screens.CGCScreen#resize(int, int)
	 */
	public void resize(float width, float height)
	{
		camMes = new Vector2(width, height);
		setPosition(position);
	}
	

	/*
	 * Destroys the walls
	 */
	public void destroyWalls()
	{
		if (upperWall != null)
		{
			if (CGCWorld.addToDestroyList(upperWall))
			{
				upperWall = null;
			}
			else
			{
				upperWall = null;
			}
		}
		
		if (lowerWall != null)
		{
			if(CGCWorld.addToDestroyList(lowerWall))
			{
				lowerWall = null;
			}
			else
			{
				lowerWall = null;
			}
		}
	}
	
	/*
	 * Creates the upper and lower walls
	 */
	public void createWalls()
	{
		Body b = CGCWorld.getBF().createRectangle(position.x + viewportWidth / 2, position.y+ (viewportHeight + wallHeight / 2), 
						viewportWidth,wallHeight, BodyType.DynamicBody,
						BodyFactory.CAT_IMPASSABLE, BodyFactory.MASK_PLAYER_WALL);
		upperWall = new PlayerWall(EntityType.PLAYERWALL, b, true);
		b.setUserData(upperWall);
		upperWall.addToWorldLayers(CGCWorld.getLH());
		
		Body b2 = CGCWorld.getBF().createRectangle(position.x + viewportWidth / 2, position.y - viewportHeight + wallHeight / 2, 
						viewportWidth, wallHeight, BodyType.DynamicBody,
						BodyFactory.CAT_IMPASSABLE, BodyFactory.MASK_PLAYER_WALL);
		lowerWall = new PlayerWall(EntityType.PLAYERWALL, b2, false);
		b2.setUserData(lowerWall);
		lowerWall.addToWorldLayers(CGCWorld.getLH());
	}
	
	/*
	 * Calculates the new position of the camera
	 * 
	 * @return						The camera's new position vector
	 */
	private float calcCamPos()
	{
		float newPos = 0;
		int tiedPrisoners = 0;
		
		for (Player p : CGCWorld.getPlayers())
		{
			if (p.isAlive() && p instanceof Prisoner && ((Prisoner) p).isTiedUp())
			{
				tiedPrisoners++;
			}
		}
		
		for (Player p : CGCWorld.getPlayers())
		{
			if (p.isAlive())
			{
				if (p instanceof Prisoner && !((Prisoner) p).isTiedUp()) // Free prisoners
				{
					newPos += p.getBody().getPosition().y * (prisonerWeight / (CGCWorld.numPrisoners-tiedPrisoners));
				}
				
				if (p instanceof RookieCop) // Rookie cops
				{
					newPos += p.getBody().getPosition().y * ((1-prisonerWeight) / CGCWorld.numCops);
				}
			}
		}
		
		return newPos;
	}
	
	/*
	 * Resets the size of the camera
	 */
	public void reset()
	{
		viewportWidth = camMes.x;
		viewportHeight = camMes.y;
	}
	
}// End class
