/*
 * @(#)ControlAdapter.java		0.3 14/4/1
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.input;

import com.badlogic.gdx.controllers.Controller;
import com.percipient24.enums.ControlType;
import com.percipient24.input.maps.ControllerMap;

/*
 * Handles generic control updates and logic
 * 
 * @version 0.3 14/4/1
 * @author Christopher Rider
 */
public class ControlAdapter 
{
	private ControlData previous;
	private ControlData current;
	
	private boolean controller = false;
	private boolean keyboard = false;
	private boolean connected = false;
	private boolean used = false;
	
	private boolean ouyaPause = false;
	
	private Controller control = null;
	private int controllerNum = -1;
	private boolean leftSide = false;
	private KeyboardManager keys = null;
	
	private int playerID = -1;
	private int convictChoice = -1;
	private int copChoice = -1;
	
	/*
	 * Creates a new ControlAdapter object
	 */
	public ControlAdapter()
	{
		previous = new ControlData(this);
		current = new ControlData(this);
	}
	
	/*
	 * Sets this ControlAdapter to adapt for a Controller
	 * 
	 * @param newControl			The Controller to adapt from
	 * @param left					If this is the left side of a controller
	 */
	public void setController(Controller newControl, boolean left)
	{
		control = newControl;
		controller = true;
		connected = true;
		leftSide = left;
	}
	
	/*
	 * Removes the attached Controller
	 */
	public void removeController()
	{
		control = null;
		controller = false;
		leftSide = false;
	}
	
	/*
	 * Sets this ControlAdapter to adapt for a Keyboard
	 * 
	 * @param newKeys				The Keyboard to adapt from
	 * @param left					If this is the left side of a controller
	 */
	public void setKeys(KeyboardManager newKeys, boolean left)
	{
		keys = newKeys;
		keyboard = true;
		connected = true;
		leftSide = left;
	}
	
	public void setKeyboardType(int newType)
	{
		keys.setKeyboardType(newType);
	}
	
	/*
	 * Removes the attached Keyboard
	 */
	public void removeKeyboard()
	{
		keys = null;
		keyboard = false;
		leftSide = false;
	}
	
	/*
	 * Checks if this Adapter is using a Controller
	 * 
	 * @return						Whether or not this is using a Controller
	 */
	public boolean isController()
	{
		return controller;
	}
	
	/*
	 * Gets the Controller this ControlAdapter is using
	 * 
	 * @return						The Controller used, null if no Controller is used
	 */
	public Controller getController()
	{
		return control;
	}
	
	/*
	 * Checks if this Adapter is using a Keyboard
	 * 
	 * @return						Whether or not this is using a Keyboard
	 */
	public boolean isKeyboard()
	{
		return keyboard;
	}
	
	/*
	 * Gets the KeyboardManager attached to this Adapter
	 * 
	 * @return						The KeyboardManager updating this Adapter
	 */
	public KeyboardManager getKeyboard()
	{
		return keys;
	}
	
	/*
	 * Checks if this Adapter should use the left side of a Controller
	 * 
	 * @return						Whether or not this is using the left side of a Controller
	 */
	public boolean isLeft()
	{
		return leftSide;
	}
	
	/*
	 * Checks if this Adapter is using a connected Controller or Keyboard
	 * 
	 * @return						Whether or not this is still connected
	 */
	public boolean isConnected()
	{
		return connected;
	}
	
	/*
	 * Checks if this Adapter is being used by a player
	 * 
	 * @return						Whether or not the Adapter is being used
	 */
	public boolean isUsed()
	{
		used = used && playerID != -1;
		
		return used;
	}
	
	/*
	 * Gets the player that is using this set of controls
	 * 
	 * @return						The ID number of the player using this ControlAdapter
	 */
	public int getPID()
	{
		return playerID;
	}
	
	/*
	 * Gets the convict choice ID for the player in this slot
	 * 
	 * @return						The convict choice ID for the player in this slot (-1 is no choice)
	 */
	public int getConvictChoice()
	{
		return convictChoice;
	}
	
	/*
	 * Gets the character choice ID for the player in this slot
	 * 
	 * @return						The cop choice ID for the player in this slot (-1 is no choice)
	 */
	public int getCopChoice()
	{
		return copChoice;
	}
	
	/*
	 * Sets the convict choice ID for the player in this slot
	 * 
	 * @param newChoice				The new convict choice for the player (negative values remove choice)
	 */
	public void setConvictChoice(int newChoice)
	{
		if (newChoice < 0)
		{
			newChoice = -1;
		}
		
		convictChoice = newChoice;
	}
	
	/*
	 * Sets the cop choice ID for the player in this slot
	 * 
	 * @param newChoice				The new cop choice for the player (negative values remove choice)
	 */
	public void setCopChoice(int newChoice)
	{
		if (newChoice < 0)
		{
			newChoice = -1;
		}
		
		copChoice = newChoice;
	}
	
	/*
	 * Gets the controller number for this slot
	 * 
	 * @return						The controller number that is in this slot
	 */
	public int getControllerNum()
	{
		return controllerNum;
	}
	
	/*
	 * Updates this ControlAdapter
	 */
	public void update()
	{
		previous.setData(current);
		ouyaPause = false;
	}
	
	/*
	 * Gets the current control state
	 * 
	 * @return						The current control state
	 */
	public ControlData getCurrent()
	{
		return current;
	}
	
	/*
	 * Assigns this control set a player
	 * 
	 * @param playerNum				The player number to use for this control set, -1 to remove
	 * @param controlNum			The controller ID for this control set
	 * @return						Whether or not the assignment was successful
	 */
	public boolean assignControls(int playerNum, int controlNum)
	{
		if (isConnected())
		{
			playerID = playerNum;
			controllerNum = controlNum;
			used = true;
			return true;
		}
		else if (playerNum == -1)
		{
			playerID = playerNum;
			controllerNum = -1;
			convictChoice = -1;
			used = false;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * Checks if the specified control is being pressed
	 * 
	 * @param check					The control to check
	 * @return						Whether or not the control is currently pressed
	 */
	public boolean isPressed(ControlType check)
	{
		switch (check)
		{
			case UP: return current.up;
			case DOWN: return current.down;
			case LEFT: return current.left;
			case RIGHT: return current.right;
			case JUMP: return current.jump;
			case PUNCH: return current.punch;
			case CALLOUT: return current.callout;
			case UP_FACE: return current.upFace;
			case DOWN_FACE: return current.downFace;
			case LEFT_FACE: return current.leftFace;
			case RIGHT_FACE: return current.rightFace;
			case MENU_UP: return current.menuUp;
			case MENU_DOWN: return current.menuDown;
			case MENU_LEFT: return current.menuLeft;
			case MENU_RIGHT: return current.menuRight;
			case SELECT: return current.select;
			case BACK: return current.back;
			case MENU_ACTION: return current.random;
			case PRIM_SORT: return current.primSort;
			case SEC_SORT: return current.secSort;
			case PAUSE: return current.pause;
			default: return false;
		}
	}
	
	/*
	 * Checks if any input is being pressed
	 * 
	 * @return						Whether or not this ControlAdapter is sending any input
	 */
	public boolean anyInput()
	{
		return (current.menuUp || current.menuDown || current.menuLeft || current.menuRight 
				|| current.select || current.back || current.random || current.primSort
				|| current.secSort || current.pause);
	}
	
	/*
	 * Checks if any valid pause menu input is being pressed
	 * 
	 * @return						Whether or not any pause menu input is being pressed
	 */
	public boolean anyPauseMenuInput()
	{
		return (current.menuUp || current.menuDown || current.select || current.back);
	}
	
	/*
	 * Gets whether or not an OUYA controller is sending pause input
	 * 
	 * @return						Whether or not an OUYA controller is sending pause input
	 */
	public boolean getOuyaPause()
	{
		return ouyaPause;
	}
	
	/*
	 * Sets whether or not an OUYA controller is sending pause input
	 * 
	 * @param pause					Whether or not an OUYA controller is sending pause input
	 */
	public void setOuyaPause(boolean pause)
	{
		ouyaPause = pause;
	}
	
	/*
	 * Checks if the specified control was just pressed
	 * 
	 * @param check					The control to check
	 * @return						Whether or not the control was just pressed
	 */
	public boolean justPressed(ControlType check)
	{
		switch (check)
		{
			case UP: return (current.up == true && previous.up == false) ? true : false;
			case DOWN: return (current.down == true && previous.down == false) ? true : false;
			case LEFT: return (current.left == true && previous.left == false) ? true : false;
			case RIGHT: return (current.right == true && previous.right == false) ? true : false;
			case JUMP: return (current.jump == true && previous.jump == false) ? true : false;
			case PUNCH: return (current.punch == true && previous.punch == false) ? true : false;
			case CALLOUT: return (current.callout == true && previous.callout == false) ? true : false;
			case UP_FACE: return (current.upFace == true && previous.upFace == false) ? true : false;
			case DOWN_FACE: return (current.downFace == true && previous.downFace == false) ? true : false;
			case LEFT_FACE: return (current.leftFace == true && previous.leftFace == false) ? true : false;
			case RIGHT_FACE: return (current.rightFace == true && previous.rightFace == false) ? true : false;
			case MENU_UP: return (current.menuUp == true && previous.menuUp == false) ? true : false;
			case MENU_DOWN: return (current.menuDown == true && previous.menuDown == false) ? true : false;
			case MENU_LEFT: return (current.menuLeft == true && previous.menuLeft == false) ? true : false;
			case MENU_RIGHT: return (current.menuRight == true && previous.menuRight == false) ? true : false;
			case SELECT: return (current.select == true && previous.select == false) ? true : false;
			case BACK: return (current.back == true && previous.back == false) ? true : false;
			case MENU_ACTION: return (current.random == true && previous.random == false) ? true : false;
			case PRIM_SORT: return (current.primSort == true && previous.primSort == false) ? true : false;
			case SEC_SORT: return (current.secSort == true && previous.secSort == false) ? true : false;
			case PAUSE: return (current.pause == true && previous.pause == false) ? true : false;
			default: return false;
		}
	}
	
	/*
	 * Checks if the face buttons have been mashed
	 * 
	 * @return						Whether or not the face buttons have been mashed
	 */
	public boolean faceJustMashed()
	{
		return (justPressed(ControlType.UP_FACE) || justPressed(ControlType.DOWN_FACE) ||
				justPressed(ControlType.LEFT_FACE) || justPressed(ControlType.RIGHT_FACE));
	}
	
	/*
	 * Checks if the specified control was just released
	 * 
	 * @param check					The control to check
	 * @return						Whether or not the control was just released
	 */
	public boolean justReleased(ControlType check)
	{
		switch (check)
		{
			case UP: return (current.up == false && previous.up == true) ? true : false;
			case DOWN: return (current.down == false && previous.down == true) ? true : false;
			case LEFT: return (current.left == false && previous.left == true) ? true : false;
			case RIGHT: return (current.right == false && previous.right == true) ? true : false;
			case JUMP: return (current.jump == false && previous.jump == true) ? true : false;
			case PUNCH: return (current.punch == false && previous.punch == true) ? true : false;
			case CALLOUT: return (current.callout == false && previous.callout == true) ? true : false;
			case UP_FACE: return (current.upFace == false && previous.upFace == true) ? true : false;
			case DOWN_FACE: return (current.downFace == false && previous.downFace == true) ? true : false;
			case LEFT_FACE: return (current.leftFace == false && previous.leftFace == true) ? true : false;
			case RIGHT_FACE: return (current.rightFace == false && previous.rightFace == true) ? true : false;
			case MENU_UP: return (current.menuUp == false && previous.menuUp == true) ? true : false;
			case MENU_DOWN: return (current.menuDown == false && previous.menuDown == true) ? true : false;
			case MENU_LEFT: return (current.menuLeft == false && previous.menuLeft == true) ? true : false;
			case MENU_RIGHT: return (current.menuRight == false && previous.menuRight == true) ? true : false;
			case SELECT: return (current.select == false && previous.select == true) ? true : false;
			case BACK: return (current.back == false && previous.back == true) ? true : false;
			case MENU_ACTION: return (current.random == false && previous.random == true) ? true : false;
			case PRIM_SORT: return (current.primSort == false && previous.primSort == true) ? true : false;
			case SEC_SORT: return (current.secSort == false && previous.secSort == true) ? true : false;
			case PAUSE: return (current.pause == false && previous.pause == true) ? true : false;
			default: return false;
		}
	}
	
	/*
	 * Tells the current state to update a button to the specified state
	 * 
	 * @param bid					The button ID to check
	 * @param pressed				The new state for the button
	 */
	public void updateButton(int bid, boolean pressed)
	{
		current.updateButton(bid, pressed);
	}
	
	/*
	 * Sets the mapping for this Adapter
	 * 
	 * @param newMap				The new mapping type
	 */
	public void setMap(ControllerMap newMap)
	{
		current.setMap(newMap);
		previous.setMap(newMap);
	}
	
	/*
	 * Directly change a specific control state
	 * 
	 * @param change				The control to change
	 * @param newState				The new state for the control
	 */
	public void changeControlState(ControlType change, boolean newState)
	{
		switch (change)
		{
			case UP: current.up = newState; return;
			case DOWN: current.down = newState; return;
			case LEFT: current.left = newState; return;
			case RIGHT: current.right = newState; return;
			case JUMP: current.jump = newState; return;
			case PUNCH: current.punch = newState; return;
			case CALLOUT: current.callout = newState; return;
			case UP_FACE: current.upFace = newState; return;
			case DOWN_FACE: current.downFace = newState; return;
			case LEFT_FACE: current.leftFace = newState; return;
			case RIGHT_FACE: current.rightFace = newState; return;
			case MENU_UP: current.menuUp = newState; return;
			case MENU_DOWN: current.menuDown = newState; return;
			case MENU_LEFT: current.menuLeft = newState; return;
			case MENU_RIGHT: current.menuRight = newState; return;
			case SELECT: current.select = newState; return;
			case BACK: current.back = newState; return;
			case MENU_ACTION: current.random = newState; return;
			case PRIM_SORT: current.primSort = newState; return;
			case SEC_SORT: current.secSort = newState; return;
			case PAUSE: current.pause = newState; return;
			default: return;
		}
	}
} // End class