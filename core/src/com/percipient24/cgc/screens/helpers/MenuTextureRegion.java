/*
 * @(#)MenuTextureRegion.java		0.3 14/4/7
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens.helpers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.percipient24.cgc.Data;

/*
 * Stores information about specialized TextureRegions for menu screens
 * 
 * @version 0.3 14/4/7
 * @author William Ziegler
 * @author Joe Pietruch
 */
public class MenuTextureRegion
{
	private TextureRegion region;

	public static final int LOWER_LEFT = 0;
	public static final int LOWER_CENTER = 1;
	public static final int LOWER_RIGHT = 2;
	public static final int MID_LEFT = 3;
	public static final int MID_CENTER = 4;
	public static final int MID_RIGHT = 5;
	public static final int UPPER_LEFT = 6;
	public static final int UPPER_CENTER = 7;
	public static final int UPPER_RIGHT = 8;
	public static final int IGNORE = 9;
	
	private int registrationPoint = MID_CENTER;
	private int screenAttachment = MID_CENTER;
	
	public static int TITLE_SAFE_Y = 50;
	public static int TITLE_SAFE_X = 50;
	
	public static final Vector2[] MENU_ANCHORS = {new Vector2(),new Vector2(),new Vector2(),new Vector2(),new Vector2(),new Vector2(),new Vector2(),new Vector2(),new Vector2(),new Vector2()};
	
	//TODO Javadocs
	public static void updateMenuAnchors()
	{
		float WIDTH = Data.ASPECT_RATIO * 1080f;
		
		TITLE_SAFE_Y = (int)(1080*0.1f);
		TITLE_SAFE_X = (int)(WIDTH*0.1f);
		
		MENU_ANCHORS[LOWER_LEFT].x 	  = TITLE_SAFE_X;
		MENU_ANCHORS[MID_LEFT  ].x 	  = TITLE_SAFE_X;
		MENU_ANCHORS[UPPER_LEFT].x    = TITLE_SAFE_X;
		
		MENU_ANCHORS[LOWER_CENTER].x  = WIDTH/2f;
		MENU_ANCHORS[MID_CENTER  ].x  = WIDTH/2f;
		MENU_ANCHORS[UPPER_CENTER].x  = WIDTH/2f;
		
		MENU_ANCHORS[LOWER_RIGHT].x   = WIDTH-TITLE_SAFE_X;
		MENU_ANCHORS[MID_RIGHT  ].x   = WIDTH-TITLE_SAFE_X;
		MENU_ANCHORS[UPPER_RIGHT].x   = WIDTH-TITLE_SAFE_X;
		
		MENU_ANCHORS[LOWER_LEFT  ].y  = TITLE_SAFE_Y;
		MENU_ANCHORS[LOWER_CENTER].y  = TITLE_SAFE_Y;
		MENU_ANCHORS[LOWER_RIGHT ].y  = TITLE_SAFE_Y;
		
		MENU_ANCHORS[MID_LEFT  ].y    = 1080f/2f;
		MENU_ANCHORS[MID_CENTER].y    = 1080f/2f;
		MENU_ANCHORS[MID_RIGHT ].y    = 1080f/2f;
		
		MENU_ANCHORS[UPPER_LEFT  ].y  = 1080f-TITLE_SAFE_Y;
		MENU_ANCHORS[UPPER_CENTER].y  = 1080f-TITLE_SAFE_Y;
		MENU_ANCHORS[UPPER_RIGHT ].y  = 1080f-TITLE_SAFE_Y;
		
		MENU_ANCHORS[IGNORE      ].x  = 0;
		MENU_ANCHORS[IGNORE      ].y  = 0;
	}
	
	private Vector2 position = new Vector2();
	private float scaleX;
	private float scaleY;
	
	// this is where the thing should end up at the end
	// having it stored is useful for resetting tweens and such
	private Vector2 restingPosition = new Vector2();
	
	private float alpha = 1.0f;
	private float rotation = 0.0f;
	
	/*
	 * Creates a MenuTextureRegion object
	 * 
	 * @param region				The TextureRegion to display
	 * @param x						The new X position for this MenuTextureRegion
	 * @param y						The new Y position for this MenuTextureRegion
	 * @param scaleX				The new X scale for this MenuTextureRegion
	 * @param scaleY				The new Y scale for this MenuTextureRegion
	 * @param screenAnchorPoint		Which point position is relative to. MenuTextureRegion.MID_CENTER in other constructors.
	 * @param registrationPoint		Where the texture draws itself relative to screenAnchorPoint. MenuTextureRegion.MID_CENTER in other constructors.
	 */
	public MenuTextureRegion(TextureRegion region, float x, float y, float scaleX, float scaleY, int screenAnchorPoint, int registrationPoint)
	{
		this.region = region;
		this.setPosition(x, y);
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.setRestingPosition(x,y);
		this.screenAttachment = screenAnchorPoint;
		this.registrationPoint = registrationPoint;
	}

	/*
	 * Creates a MenuTextureRegion object
	 * 
	 * @param region				The TextureRegion to display
	 * @param position				The new position for this MenuTextureRegion
	 * @param scaleX				The new X scale for this MenuTextureRegion
	 * @param scaleY				The new Y scale for this MenuTextureRegion
	 */
	public MenuTextureRegion(TextureRegion region, Vector2 position, float scaleX, float scaleY)
	{
		this(region, position.x, position.y, scaleX, scaleY, MenuTextureRegion.MID_CENTER, MenuTextureRegion.MID_CENTER);
	}
	
	/*
	 * Creates a MenuTextureRegion object
	 * 
	 * @param region				The TextureRegion to display
	 * @param position				The new position for this MenuTextureRegion
	 */
	public MenuTextureRegion(TextureRegion region, Vector2 position)
	{
		this(region, position.x, position.y, 1.0f, 1.0f, MenuTextureRegion.MID_CENTER, MenuTextureRegion.MID_CENTER);
	}
	
	/*
	 * Creates a MenuTextureRegion object
	 * 
	 * @param region				The TextureRegion to display
	 * @param position				The new position for this MenuTextureRegion
	 * @param screenAnchorPoint		Which point position is relative to. MenuTextureRegion.MID_CENTER in other constructors.
	 * @param registrationPoint		Where the texture draws itself relative to screenAnchorPoint. MenuTextureRegion.MID_CENTER in other constructors.
	 */
	public MenuTextureRegion(TextureRegion region, Vector2 position, int screenAnchorPoint, int registrationPoint)
	{
		this(region, position.x, position.y, 1.0f, 1.0f, screenAnchorPoint, registrationPoint);
	}

	/*
	 * Draws this MenuTextureRegion with no tween adjustments
	 * 
	 * @param sBatch				The SpriteBatch the game is using
	 */
	public void draw(SpriteBatch sBatch)
	{
		this.draw(sBatch,Vector2.Zero);
	}

	/*
	 * Draws this MenuTextureRegion with tween adjustments
	 * 
	 * @param sBatch				The SpriteBatch the game is using
	 * @param wiggle				The tween adjustment on this MenuTextureRegion
	 */
	public void draw(SpriteBatch sBatch, Vector2 wiggle)
	{
		if(this.alpha != 1.0f)
		{
			sBatch.setColor(1, 1, 1, this.alpha);
		}
		
		switch(registrationPoint)
		{
			case (UPPER_RIGHT):
				sBatch.draw(
				/* region */ this.region,
				/*  pos x */ MENU_ANCHORS[this.screenAttachment].x +(-this.region.getRegionWidth() + this.position.x + wiggle.x),
				/*  pos y */ MENU_ANCHORS[this.screenAttachment].y +(-this.region.getRegionWidth() + this.position.y + wiggle.y),
				/*  ori x */ this.region.getRegionWidth(),
				/*  ori y */ this.region.getRegionHeight(),
				/* size X */ this.region.getRegionWidth(),
				/* size Y */ this.region.getRegionHeight(),
				/* scales */ this.scaleX, this.scaleY,
				/* rotate */ this.rotation);
				break;
			case (UPPER_CENTER):
				sBatch.draw(
				/* region */ this.region,
				/*  pos x */ MENU_ANCHORS[this.screenAttachment].x - (this.region.getRegionWidth() / 2f ) + this.position.x + wiggle.x,
				/*  pos y */ MENU_ANCHORS[this.screenAttachment].y - this.region.getRegionHeight() + this.position.y + wiggle.y,
				/*  ori x */ this.region.getRegionWidth()  / 2f,
				/*  ori y */ this.region.getRegionHeight(),
				/* size X */ this.region.getRegionWidth(),
				/* size Y */ this.region.getRegionHeight(),
				/* scales */ this.scaleX, this.scaleY,
				/* rotate */ this.rotation);
				break;
			case (UPPER_LEFT):
				sBatch.draw(
				/* region */ this.region,
				/*  pos x */ MENU_ANCHORS[this.screenAttachment].x + this.position.x + wiggle.x,
				/*  pos y */ MENU_ANCHORS[this.screenAttachment].y - this.region.getRegionHeight()+ this.position.y + wiggle.y,
				/*  ori x */ 0,
				/*  ori y */ this.region.getRegionHeight(),
				/* size X */ this.region.getRegionWidth(),
				/* size Y */ this.region.getRegionHeight(),
				/* scales */ this.scaleX, this.scaleY,
				/* rotate */ this.rotation);
				break;
			case (MID_RIGHT):
				sBatch.draw(
				/* region */ this.region,
				/*  pos x */ MENU_ANCHORS[this.screenAttachment].x - this.region.getRegionWidth() + this.position.x + wiggle.x,
				/*  pos y */ MENU_ANCHORS[this.screenAttachment].y - (this.region.getRegionHeight() / 2f) + this.position.y + wiggle.y,
				/*  ori x */ this.region.getRegionWidth(),
				/*  ori y */ this.region.getRegionHeight() / 2f,
				/* size X */ this.region.getRegionWidth(),
				/* size Y */ this.region.getRegionHeight(),
				/* scales */ this.scaleX, this.scaleY,
				/* rotate */ this.rotation);
				break;
			case (MID_CENTER):
				sBatch.draw(
				/* region */ this.region,
				/*  pos x */ MENU_ANCHORS[this.screenAttachment].x - this.region.getRegionWidth()/2f + this.position.x + wiggle.x,
				/*  pos y */ MENU_ANCHORS[this.screenAttachment].y - this.region.getRegionHeight()/2f + this.position.y + wiggle.y,
				/*  ori x */ this.region.getRegionWidth()  / 2f,
				/*  ori y */ this.region.getRegionHeight() / 2f,
				/* size X */ this.region.getRegionWidth(),
				/* size Y */ this.region.getRegionHeight(),
				/* scales */ this.scaleX, this.scaleY,
				/* rotate */ this.rotation);
				break;
			case (MID_LEFT):
				sBatch.draw(
				/* region */ this.region,
				/*  pos x */ MENU_ANCHORS[this.screenAttachment].x + this.position.x + wiggle.x,
				/*  pos y */ MENU_ANCHORS[this.screenAttachment].y - (this.region.getRegionHeight() / 2f) + this.position.y + wiggle.y,
				/*  ori x */ 0,
				/*  ori y */ this.region.getRegionHeight() / 2f,
				/* size X */ this.region.getRegionWidth(),
				/* size Y */ this.region.getRegionHeight(),
				/* scales */ this.scaleX, this.scaleY,
				/* rotate */ this.rotation);
				break;
			case (LOWER_RIGHT):
				sBatch.draw(
				/* region */ this.region,
				/*  pos x */ MENU_ANCHORS[this.screenAttachment].x - this.region.getRegionWidth() + this.position.x + wiggle.x,
				/*  pos y */ MENU_ANCHORS[this.screenAttachment].y + this.position.y + wiggle.y,
				/*  ori x */ this.region.getRegionWidth(),
				/*  ori y */ 0,
				/* size X */ this.region.getRegionWidth(),
				/* size Y */ this.region.getRegionHeight(),
				/* scales */ this.scaleX, this.scaleY,
				/* rotate */ this.rotation);
				break;
			case (LOWER_CENTER):
				sBatch.draw(
				/* region */ this.region,
				/*  pos x */ MENU_ANCHORS[this.screenAttachment].x - (this.region.getRegionWidth() / 2f) + this.position.x + wiggle.x,
				/*  pos y */ MENU_ANCHORS[this.screenAttachment].y + this.position.y + wiggle.y,
				/*  ori x */ this.region.getRegionWidth()  / 2f,
				/*  ori y */ 0,
				/* size X */ this.region.getRegionWidth(),
				/* size Y */ this.region.getRegionHeight(),
				/* scales */ this.scaleX, this.scaleY,
				/* rotate */ this.rotation);
				break;
			case (LOWER_LEFT):
			default:
				sBatch.draw(
				/* region */ this.region,
				/*  pos x */ MENU_ANCHORS[this.screenAttachment].x + this.position.x + wiggle.x,
				/*  pos y */ MENU_ANCHORS[this.screenAttachment].y + this.position.y + wiggle.y,
				/*  ori x */ 0,
				/*  ori y */ 0,
				/* size X */ this.region.getRegionWidth(),
				/* size Y */ this.region.getRegionHeight(),
				/* scales */ this.scaleX, this.scaleY,
				/* rotate */ this.rotation);
			 break;
		}
		
		if(this.alpha != 1.0f)
		{
			sBatch.setColor(1, 1, 1, 1);
		}
	}
	
	/*
	 * Gets the width of the TextureRegion
	 * 
	 * @return						The width of the TextureRegion
	 */
	public int getRegionWidth()
	{
		return region.getRegionWidth();
	}
	
	/*
	 * Gets the height of the TextureRegion
	 * 
	 * @return						The height of the TextureRegion
	 */
	public int getRegionHeight()
	{
		return region.getRegionHeight();
	}
	
	/*
	 * Gets the TextureRegion
	 * 
	 * @return						The TextureRegion to display
	 */
	public TextureRegion getRegion()
	{
		return region;
	}
	
	/*
	 * Gets the position
	 * 
	 * @return						The position for this MenuTextureRegion
	 */
	public Vector2 getPosition()
	{
		return position;
	}
	
	/*
	 * Gets the X position
	 * 
	 * @return						The X position for this MenuTextureRegion
	 */
	public float getX()
	{
		return position.x;
	}
	
	/*
	 * Gets the Y position
	 * 
	 * @return						The Y position for this MenuTextureRegion
	 */
	public float getY()
	{
		return position.y;
	}
	
	/*
	 * Gets the X scale for this MenuTextureRegion
	 * 
	 * @return						The X scale used by this MenuTextureRegion
	 */
	public float getScaleX()
	{
		return scaleX;
	}
	
	/*
	 * Gets the Y scale for this MenuTextureRegion
	 * 
	 * @return						The Y scale used by this MenuTextureRegion
	 */
	public float getScaleY()
	{
		return scaleY;
	}
	
	/*
	 * Sets the TextureRegion
	 * 
	 * @param region				The new TextureRegion to display
	 */
	public void setRegion(TextureRegion region)
	{
		this.region = region;
	}
	
	/*
	 * Sets the position with a Vector2
	 * 
	 * @param position				The new position for this MenuTextureRegion
	 */
	public void setPosition(Vector2 position)
	{
		this.position.x = position.x;
		this.position.y = position.y;
	}
	
	/*
	 * Sets the position with separate X and Y values
	 * 
	 * @param x						The new X position for this MenuTextureRegion
	 * @param y						The new Y position for this MenuTextureRegion
	 */
	public void setPosition(float x, float y)
	{
		this.position.x = x;
		this.position.y = y;
	}
	
	//TODO Javadocs
	public void setStartingOffsetFromRestingPosition(float x, float y)
	{
		this.position.x = this.restingPosition.x + x;
		this.position.y = this.restingPosition.y + y;
	}
	
	/*
	 * Sets the X position
	 * 
	 * @param xPos					The new X position for this MenuTextureRegion
	 */
	public void setX(float xPos)
	{
		position.x = xPos;
	}
	
	/*
	 * Sets the Y position
	 * 
	 * @param yPos					The new Y position for this MenuTextureRegion
	 */
	public void setY(float yPos)
	{
		position.y = yPos;
	}
	
	/*
	 * Adjusts the X position
	 * 
	 * @param xAdjust				The value by which to adjust the X position
	 */
	public void adjustX(float xAdjust)
	{
		position.x += xAdjust;
	}
	
	/*
	 * Adjusts the Y position
	 * 
	 * @param yAdjust				The value by which to adjust the Y position
	 */
	public void adjustY(float yAdjust)
	{
		position.y += yAdjust;
	}
	
	/*
	 * Sets the X scale for this MenuTextureRegion
	 * 
	 * @param scaleX				The new X scale for this MenuTextureRegion
	 */
	public void setScaleX(float scaleX)
	{
		this.scaleX = scaleX;
	}
	
	/*
	 * Sets the Y scale for this MenuTextureRegion
	 * 
	 * @param scaleY				The new Y scale for this MenuTextureRegion
	 */
	public void setScaleY(float scaleY)
	{
		this.scaleY = scaleY;
	}
	
	/*
	 * Returns the resting position (final on-screen position) of this region
	 * 
	 * @return						The resting position for this MenuTextureRegion
	 */
	public Vector2 getRestingPosition()
	{
		return restingPosition;
	}
	
	/*
	 * Explicitly sets the resting position (final on-screen position), without needing a Vector2
	 * 
	 * @param x						The new X coordinate for this MenuTextureRegion
	 * @param y						The new X coordinate for this MenuTextureRegion
	 */
	public void setRestingPosition(float x, float y)
	{
		this.restingPosition.x = x;
		this.restingPosition.y = y;
	}

	/*
	 * Explicitly sets the resting position (final on-screen position), copying the passed Vector2's values
	 * 
	 * @param restingPosition 		The Vector2 whose coordinates will be assigned to this.restingPosition
	 */
	public void setRestingPosition(Vector2 restingPosition)
	{
		this.restingPosition.y = restingPosition.y;
		this.restingPosition.x = restingPosition.x;
	}

	/*
	 * Gets the alpha value of this MenuTextureRegion
	 * 
	 * @return						The alpha value of this MenuTextureRegion
	 */
	public float getAlpha()
	{
		return alpha;
	}

	/*
	 * Sets the alpha value of this MenuTextureRegion
	 * 
	 * @param alpha					The new alpha value for this MenuTextureRegion
	 */
	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}

	/*
	 * Gets the rotation of this MenuTextureRegion (in degrees)
	 * 
	 * @return						The rotation of this MenuTextureRegion
	 */
	public float getRotation()
	{
		return rotation;
	}

	/*
	 * Sets the rotation of this MenuTextureRegion (in degrees)
	 * 
	 * @param rotation	`			The new rotation for this MenuTextureRegion
	 */
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
}
