/*
 * @(#)ControllerScheme.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc;

import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;

/*
 * Separates input commands into left/right players for controllers
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 * @author William Ziegler
 */
public class ControllerScheme 
{
	private Player player;
	private ControlAdapter controller;
	private boolean isLeftPlayer;
	
	/*
	 * Creates a new ControllerScheme
	 * 
	 * @param p						The player to make the scheme for
	 * @param isLeft				Is the player on the left of the controller
	 */
	public ControllerScheme(Player p, boolean isLeft)
	{
		player = p;
		isLeftPlayer = isLeft;
	}
	
	/*
	 * Sets up the controller for this player's input
	 * 
	 * @param c						The controller being used (generic)
	 */
	public void setController(ControlAdapter c)
	{
		controller = c;
	}
	
	/*
	 * Returns the controller for this player's input
	 * 
	 * @return						The controller for this player's input
	 */
	public ControlAdapter getController()
	{
		return controller;
	}
	
	/*
	 *  Checks to see if the player is hitting the pause button
	 *  
	 *  @return						Whether or not the player is hitting the pause button
	 */
	public boolean checkPause()
	{
		return controller.justPressed(ControlType.PAUSE) || controller.getOuyaPause();
	}
	
	/*
	 * Moves the player based on their side of the controller/mode of input
	 */
	public void drivePlayer()
	{
		if (player instanceof Prisoner)
		{
			((Prisoner) player).controlUpdate(controller.justPressed(ControlType.UP_FACE), 
					controller.justPressed(ControlType.DOWN_FACE), 
					controller.justPressed(ControlType.LEFT_FACE), 
					controller.justPressed(ControlType.RIGHT_FACE),
					controller.justPressed(ControlType.CALLOUT),
					controller.isPressed(ControlType.UP),
					controller.isPressed(ControlType.DOWN),
					controller.isPressed(ControlType.LEFT), 
					controller.isPressed(ControlType.RIGHT),
					controller.justPressed(ControlType.UP),
					controller.justPressed(ControlType.DOWN),
					controller.justPressed(ControlType.RIGHT),
					controller.justPressed(ControlType.LEFT),
					controller.justPressed(ControlType.JUMP),
					controller.faceJustMashed());
		}
		else
		{
			player.controlUpdate(controller.justPressed(ControlType.UP_FACE), 
									controller.justPressed(ControlType.DOWN_FACE), 
									controller.justPressed(ControlType.LEFT_FACE), 
									controller.justPressed(ControlType.RIGHT_FACE),
									controller.justPressed(ControlType.CALLOUT),
									controller.isPressed(ControlType.UP),
									controller.isPressed(ControlType.DOWN),
									controller.isPressed(ControlType.LEFT), 
									controller.isPressed(ControlType.RIGHT),
									controller.justPressed(ControlType.JUMP),
									controller.faceJustMashed());
		}
	}
	
	/*
	 * Gets whether or not the player is on the left of the controller
	 * 
	 * @return 						Whether or not the player is using the left side
	 */
	public boolean isLeft()
	{
		return isLeftPlayer;
	}
	
	/*
	 * Sets the Player for this controller scheme
	 * 
	 * @param p						The Player that will use this control scheme
	 */
	public void setPlayer(Player p)
	{
		player = p;
	}
} // End class
