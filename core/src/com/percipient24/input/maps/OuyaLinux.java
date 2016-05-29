/*
 * @(#)Louya.java		0.1 1/31/14
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.input.maps;

/*
 * Contains the key bindings for an Ouya controller on Linux
 * 
 * @version 0.1 1/31/14
 * @author Clayton Andrews
 */

/*
 * Developer Note: The inputs below are based on personal testing within Eclipse in Ubuntu/Xubuntu 13.10
 * 64 bit; The "correct" button mappings are commented out in lieu of the "functional" mapping I've found
 * via testing. I utilized jstest-gtk for this all of the Linux controller classes, and only the Ouya
 * controller seems to behave differently. The axis numbers are the same as they "should" be, but the buttons
 * appear to be different. Please change them as necessary.
 */
public class OuyaLinux
{
	public static final int O = 3; //according to jstest-gtk this should be button 0
	public static final int U = 4; //according to jstest-gtk this should be button 1
	public static final int Y = 5; //according to jstest-gtk this should be button 2
	public static final int A = 6; //according to jstest-gtk this should be button 3
	
	public static final int DU = 11; //according to jstest-gtk this should be button 8
	public static final int DD = 12; //according to jstest-gtk this should be button 9
	public static final int DL = 13; //according to jstest-gtk this should be button 10
	public static final int DR = 14; //according to jstest-gtk this should be button 11
	
	public static final int SYSTEM = 17; //according to jstest-gtk this cannot be detected?
	
	public static final int L1 = 7; //according to jstest-gtk this should be button 4
	public static final int L2 = 15; //according to jstest-gtk this should be button 12
	public static final int L3 = 9; //according to jstest-gtk this should be button 6
	
	public static final int R1 = 8; //according to jstest-gtk this should be button 5
	public static final int R2 = 16; //according to jstest-gtk this should be button 13
	public static final int R3 = 10; //according to jstest-gtk this should be button 7
	
	public static final int AR2 = 5; //according to jstest-gtk this should be Axis 5
	public static final int ARX = 3; //according to jstest-gtk this should be Axis 3
	public static final int ARY = 4; //according to jstest-gtk this should be Axis 4
	
	public static final int AL2 = 2; //according to jstest-gtk this should be Axis 2
	public static final int ALX = 0; //according to jstest-gtk this should be Axis 0
	public static final int ALY = 1; //according to jstest-gtk this should be Axis 1
} // End class