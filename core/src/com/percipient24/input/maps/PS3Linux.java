/*
 * @(#)PS3Linux.java		0.1 14/1/31
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.input.maps;

/*
 * Contains the key bindings for a PS3 controller on Linux
 * 
 * @version 0.1 14/1/31
 * @author Clayton Andrews
 */

/* Developer note: 
 * Kind of awesomely, The PS3 controller appears to have mapping of several buttons and triggers to
 * additional axes. Though this game really doesn't need some of these axes, they're recorded for
 * future developers to utilize (e.g. for actions based on sensitivity). Additionally,
 * the following axes are used for SixAxis input (based solely on the author's testing):
 * 
 * Axis #23: Turn the controller to the left
 * Axis #24: Turn the controller right
 * Axis #25: Turn the controller toward or away from you
 * 
 * Please note however that this just causes changes in the corresponding axis' returned value, and is
 * somewhat spotty in terms of results.
*/ 

public class PS3Linux
{
	public static final int X = 14;
	public static final int CIRCLE = 13;
	public static final int TRIANGLE = 12;
	public static final int SQUARE = 15;
	
	public static final int DU = 4; //also mapped to Axis 8 (according to jstest-gtk)
	public static final int DD = 6; //also mapped to Axis 10 (according to jstest-gtk)
	public static final int DL = 7; //no Axis pair? (according to jstest-gtk)
	public static final int DR = 5; //also mapped to Axis 9 (according to jstest-gtk)
	
	public static final int SELECT = 0;
	public static final int START = 3;
	public static final int HOME = 16;
	
	public static final int L1 = 10; //also mapped to Axis 14 (according to jstest-gtk)
	public static final int L3 = 1;
	
	public static final int R1 = 11; //also mapped to Axis 15 (according to jstest-gtk)
	public static final int R3 = 2;
	
	public static final int AR2 = 13; //also mapped as Button #8 (according to jstest-gtk)
	public static final int ARX = 2;
	public static final int ARY = 3;
	
	public static final int AL2 = 12; //also mapped as Button #9 (according to jstest-gtk)
	public static final int ALX = 0;
	public static final int ALY = 1;
} // End class