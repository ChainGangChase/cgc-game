/*
 * @(#)ControlData.java		0.3 14/4/1
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.input;

import com.badlogic.gdx.controllers.PovDirection;
import com.percipient24.input.maps.ControllerMap;

/*
 * Handles control input and converts it to game format
 * 
 * @version 0.3 14/4/1
 * @author Christopher Rider
 */
public class ControlData 
{
	public boolean up = false;
	public boolean down = false;
	public boolean left = false;
	public boolean right = false;
	public boolean punch = false;
	public boolean jump = false;
	public boolean callout = false;
	public boolean pause = false;
	public boolean upFace = false;
	public boolean downFace = false;
	public boolean leftFace = false;
	public boolean rightFace = false;
	public boolean select = false;
	public boolean back = false;
	public boolean menuUp = false;
	public boolean menuDown = false;
	public boolean menuLeft = false;
	public boolean menuRight = false;
	public boolean primSort = false;
	public boolean secSort = false;
	public boolean random = false;

	private ControllerMap mapping = null;
	private ControlAdapter owner = null;
	private String cName = null;
	
	/*
	 * Creates a ControlData object
	 * 
	 * @param newOwner				The ControlAdapter that owns this data
	 */
	public ControlData(ControlAdapter newOwner)
	{
		owner = newOwner;
	}
	
	/*
	 * Sets the generic data using another ControlData object
	 * 
	 * @param other					The other ControlData to copy from
	 */
	public void setData(ControlData other)
	{
		up = other.up;
		down = other.down;
		left = other.left;
		right = other.right;
		punch = other.punch;
		jump = other.jump;
		callout = other.callout;
		pause = other.pause;
		upFace = other.upFace;
		downFace = other.downFace;
		leftFace = other.leftFace;
		rightFace = other.rightFace;
		select = other.select;
		back = other.back;
		menuUp = other.menuUp;
		menuDown = other.menuDown;
		menuLeft = other.menuLeft;
		menuRight = other.menuRight;
		primSort = other.primSort;
		secSort = other.secSort;
		random = other.random;
	}
	
	/*
	 * Resets the input states to false
	 */
	public void resetData()
	{
		up = false;
		down = false;
		left = false;
		right = false;
		punch = false;
		jump = false;
		callout = false;
		pause = false;
		upFace = false;
		downFace = false;
		leftFace = false;
		rightFace = false;
		select = false;
		back = false;
		menuUp = false;
		menuDown = false;
		menuLeft = false;
		menuRight = false;
		primSort = false;
		secSort = false;
		random = false;
	}
	
	/*
	 * Tells this ControlData how to read the controls
	 * 
	 * @param newMap				The new control map to use
	 */
	public void setMap(ControllerMap newMap)
	{
		mapping = newMap;
		cName = owner.getController().getName().toLowerCase();
	}
	
	/*
	 * Determines what buttons are pressed
	 * 
	 * @param bid					The current button ID
	 * @param pressed				Whether or not the button was pressed
	 */
	public void updateButton(int bid, boolean pressed)
	{
		if (owner.isController())
		{
			if (owner.isLeft())
			{
				if (bid == mapping.DL) 
				{ 
					leftFace = pressed;
					primSort = pressed;
				}
				else if (bid == mapping.DR) 
				{ 
					rightFace = pressed;
					back = pressed;
				}
				else if (bid == mapping.DU) 
				{
					upFace = pressed;
					secSort = pressed;
				}
				else if (bid == mapping.DD)
				{ 
					downFace = pressed;
					select = pressed;
				}
				else if (bid == mapping.L1) 
				{ 
					jump = pressed;
					random = pressed;
				}
				else if (bid == mapping.L3) 
				{ 
					callout = pressed;
				}
			}
			else
			{
				if (bid == mapping.A) 
				{ 
					downFace = pressed;
					select = pressed;
				}
				else if (bid == mapping.Y) 
				{ 
					upFace = pressed;
					secSort = pressed;
				}
				else if (bid == mapping.B) 
				{ 
					rightFace = pressed;
					back = pressed;
				}
				else if (bid == mapping.X) 
				{ 
					leftFace = pressed;
					primSort = pressed;
				}
				else if (bid == mapping.R1) 
				{ 
					jump = pressed;
					random = pressed;
				}
				else if (bid == mapping.R3) 
				{ 
					callout = pressed;
				}
			}
			
			if (bid == mapping.PAUSE) 
			{ 
				if ((cName.contains("ouya") || cName.contains("connected"))
						&& pause && !pressed)
				{
					owner.setOuyaPause(!owner.getOuyaPause());
				}
				pause = pressed;
			}
		}
	}
	
	/*
	 * Determines what POV directions have been pressed
	 * 
	 * @param dir					The direction pressed on the POV hat
	 */
	public void pressPov(PovDirection dir)
	{	
		if (owner.isController() && owner.isLeft())
		{
			rightFace = false;
			leftFace = false;
			upFace = false;
			downFace = false;
			
			switch (dir)
			{
				case center:
					break;
				case east:
					rightFace = true;
					back = true;
					break;
				case north:
					upFace = true;
					secSort = true;
					break;
				case northEast:
					upFace = true;
					rightFace = true;
					secSort = false;
					back = false;
					break;
				case northWest:
					upFace = true;
					leftFace = true;
					secSort = false;
					primSort = false;
					break;
				case south:
					downFace = true;
					select = true;
					break;
				case southEast:
					rightFace = true;
					downFace = true;
					select = false;
					back = false;
					break;
				case southWest:
					leftFace = true;
					downFace = true;
					select = false;
					primSort = false;
					break;
				case west:
					leftFace = true;
					primSort = true;
					break;
				default:
					break;
			}
		}
	}
	
	/*
	 * Sets all POV hat values to false
	 */
	public void releasePov()
	{
		if (owner.isController() && owner.isLeft())
		{
			rightFace = false;
			leftFace = false;
			upFace = false;
			downFace = false;
			select = false;
			back = false;
			primSort = false;
			secSort = false;
		}
	}
	
	/*
	 * Handles value changes along each axis
	 * 
	 * @param aid				The current axis ID
	 * @param value				The value along the axis
	 */
	public void moveAxis(int aid, float value) 
	{
		//Gdx.app.log("Moving Axis", aid + " : " + value);
		if (owner.isController())
		{
			if (owner.isLeft())
			{
				if (aid == mapping.ALX) 
				{ 
					if (value > 0.5f)
					{
						right = true;
						left = false;
						menuRight = true;
						menuLeft = false;
					}
					else if (value < -0.5f)
					{
						left = true;
						right = false;
						menuLeft = true;
						menuRight = false;
					}
					else
					{
						left = false;
						right = false;
						menuLeft = false;
						menuRight = false;
					}
				}
				else if (aid == mapping.ALY) 
				{ 
					if (value > 0.5f)
					{
						up = false;
						down = true;
						menuUp = false;
						menuDown = true;
					}
					else if (value < -0.5f)
					{
						up = true;
						down = false;
						menuUp = true;
						menuDown = false;
					}
					else
					{
						up = false;
						down = false;
						menuUp = false;
						menuDown = false;
					}
				}
				else if (aid == mapping.ADX) 
				{
					if (value > 0)
					{
						rightFace = true;
						leftFace = false;
						back = true;
						primSort = false;
					}
					else if (value < 0)
					{
						rightFace = false;
						leftFace = true;
						back = false;
						primSort = true;
					}
					else
					{
						rightFace = false;
						leftFace = false;
						back = false;
						primSort = false;
					}
				}
				else if (aid == mapping.ADY) 
				{
					if (value > 0)
					{
						upFace = false;
						downFace = true;
						secSort = false;
						select = true;
					}
					else if (value < 0)
					{
						upFace = true;
						downFace = false;
						secSort = true;
						select = false;
					}
					else
					{
						upFace = false;
						downFace = false;
						secSort = false;
						select = false;
					}
				}
			}
			else
			{
				if (aid == mapping.ARX) 
				{ 
					if (value > 0.5f)
					{
						right = true;
						left = false;
						menuRight = true;
						menuLeft = false;
					}
					else if (value < -0.5f)
					{
						left = true;
						right = false;
						menuLeft = true;
						menuRight = false;
					}
					else
					{
						left = false;
						right = false;
						menuLeft = false;
						menuRight = false;
					}
				}
				else if (aid == mapping.ARY) 
				{ 
					if (value > 0.5f)
					{
						up = false;
						down = true;
						menuUp = false;
						menuDown = true;
					}
					else if (value < -0.5f)
					{
						up = true;
						down = false;
						menuUp = true;
						menuDown = false;
					}
					else
					{
						up = false;
						down = false;
						menuUp = false;
						menuDown = false;
					}
				}
			}
		}
	}
} // End class