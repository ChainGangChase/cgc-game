/*
 * @(#)Data.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc;

/*
 * Stores misc data
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 */
public class Data 
{
	public static int ACTUAL_WIDTH; // Width and Height of the screen
	public static int ACTUAL_HEIGHT;
	public static int GAME_WIDTH; // Camera use only
	public static int GAME_HEIGHT; // Camera use only
	public static float ASPECT_RATIO; // Aspect ratio of the screen

	public static int BASE_WIDTH = 1920; // Width and Height of native resolution
	public static int BASE_HEIGHT = 1080;
	public static int START_X; // How far over the menu has to be from both sides to fit a 5:4 screen
	public static int MENU_WIDTH; // Width and Height the menu screens have to be
	public static int MENU_HEIGHT;
	
	public static float SCALE_FACTOR; // based on our height, what do we scale images by in the menu? (computed in ChaseApp)
	
	// Conversion values
	public static final float BOX_TO_WORLD = 160.0f;
	public static final float WORLD_TO_BOX = 0.00625f;
	public static final float RADDEG = 180f/(float)Math.PI;
	public static final float DEGRAD = (float)Math.PI/180f;
	public static final float BOX_TO_SCREEN = 96.0f;
	public static final float SCREEN_TO_BOX = 1 / 96.0f;
} // End class
