/*
 * @(#)ControllerHalf.java		0.3 14/4/2
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.input;

import com.badlogic.gdx.controllers.Controller;

/*
 * Holds a controller and which side it is
 * 
 * @version 0.3 14/4/2
 * @author Christopher Rider
 */
public class ControllerHalf 
{
	public Controller controller;
	public boolean leftSide;
	
	/*
	 * Creates a new ControllerHalf object
	 * 
	 * @param owner					The Controller to use
	 * @param left					Which side of the controller to use
	 */
	public ControllerHalf(Controller owner, boolean left)
	{
		controller = owner;
		leftSide = left;
	}
} // End class