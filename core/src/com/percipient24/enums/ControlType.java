/*
 * @(#)ControlType.java		0.2 14/3/10
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.enums;

/*
 * Stores the different types of control input (abstracted)
 * 
 * @version 0.2 14/3/10
 * @author Christopher Rider
 */
public enum ControlType 
{
	UP, DOWN, LEFT, RIGHT, 
	UP_FACE, DOWN_FACE, LEFT_FACE, RIGHT_FACE, 
	JUMP, PUNCH, CALLOUT, PAUSE,
	MENU_UP, MENU_DOWN, MENU_LEFT, MENU_RIGHT,
	SELECT, BACK, PRIM_SORT, SEC_SORT, MENU_ACTION;
}
