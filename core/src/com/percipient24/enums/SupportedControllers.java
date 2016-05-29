/*
 * @(#)SupportedControllers.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.enums;

/*
 * Enumeration of controller/platform combinations the game should support.
 * 
 * @version 0.1 13/5/24
 * 
 * @author Joe Pietruch
 * @author JD Kelly
 * @author William Ziegler
 */

public enum SupportedControllers 
{
		OUYA_ON_WINDOWS, OUYA_ON_OUYA, OUYA_ON_MAC, OUYA_ON_LINUX,
		XBOX_ON_WINDOWS, XBOX_ON_OUYA, XBOX_ON_MAC, XBOX_ON_LINUX, 
		PS3_ON_WINDOWS, PS3_ON_OUYA, PS3_ON_MAC, PS3_ON_LINUX,
		KEYBOARD_ON_WINDOWS, LOGITECH_ON_MAC, LOGITECH_ON_WINDOWS, LOGITECH_ON_LINUX,
		NONE 
}
