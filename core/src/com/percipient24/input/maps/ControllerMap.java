/*
 * @(#)ControllerMap.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.input.maps;

import com.percipient24.enums.SupportedControllers;

/*
 * Binds various forms of controllers to generic input commands
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 * @author JD Kelly
 * @author William Ziegler
 * @author Clayton Andrews
 */
public class ControllerMap 
{
	public int A;
	public int B;
	public int X;
	public int Y;
	
	public int DU;
	public int DD;
	public int DL;
	public int DR;
	
	public int L1;
	public int L2;
	public int L3;
	
	public int R1;
	public int R2;
	public int R3;
	
	public int PAUSE;
	
	public int AR2;
	public int ARX;
	public int ARY;
	
	public int AL2;
	public int ALX;
	public int ALY;
	
	public int ADX;
	public int ADY;
	public float STICK_DEAD_ZONE;
	
	public static final int WOUYA = 0;
	
	/*
	 * Bind controller input to CGC input
	 * 
	 * @param controllerType		The type of the current controller
	 */
	public void mapFrom(SupportedControllers controllerType)
	{
		switch(controllerType)
		{
			case OUYA_ON_OUYA:
				A = OuyaOuya.O;
				B = OuyaOuya.A;
				X = OuyaOuya.U;
				Y = OuyaOuya.Y;
				
				DU = OuyaOuya.DU;
				DD = OuyaOuya.DD;
				DL = OuyaOuya.DL;
				DR = OuyaOuya.DR;
				
				PAUSE = OuyaOuya.SYSTEM; // TODO: TEST ME
				
				L1 = OuyaOuya.L1;
				L2 = OuyaOuya.L2;
				L3 = OuyaOuya.L3;
				
				R1 = OuyaOuya.R1;
				R2 = OuyaOuya.R2;
				R3 = OuyaOuya.R3;
				
				AR2 = OuyaOuya.AR2;
				ARX = OuyaOuya.ARX;
				ARY = OuyaOuya.ARY;
				
				AL2 = OuyaOuya.AL2;
				ALX = OuyaOuya.ALX;
				ALY = OuyaOuya.ALY;
				break;
				
			case XBOX_ON_OUYA:
				A = XboxOuya.A;
				B = XboxOuya.B;
				X = XboxOuya.X;
				Y = XboxOuya.Y;
				
				DU = XboxOuya.DU;
				DD = XboxOuya.DD;
				DL = XboxOuya.DL;
				DR = XboxOuya.DR;
				
				PAUSE = XboxOuya.START;
				
				L1 = XboxOuya.L1;
				L2 = XboxOuya.AL2;
				L3 = XboxOuya.L3;
				
				R1 = XboxOuya.R1;
				R2 = XboxOuya.AR2;
				R3 = XboxOuya.R3;
				
				AR2 = XboxOuya.AR2;
				ARX = XboxOuya.ARX;
				ARY = XboxOuya.ARY;
				
				AL2 = XboxOuya.AL2;
				ALX = XboxOuya.ALX;
				ALY = XboxOuya.ALY;
				
				ADX = XboxOuya.ADX;
				ADY = XboxOuya.ADY;
				break;
				
			case PS3_ON_OUYA:
				A = PS3Ouya.X;
				B = PS3Ouya.CIRCLE;
				X = PS3Ouya.SQUARE;
				Y = PS3Ouya.TRIANGLE;
				
				DU = PS3Ouya.DU;
				DD = PS3Ouya.DD;
				DL = PS3Ouya.DL;
				DR = PS3Ouya.DR;
				
				PAUSE = PS3Ouya.START;
				
				L1 = PS3Ouya.L1;
				L2 = PS3Ouya.L2;
				L3 = PS3Ouya.L3;
				
				R1 = PS3Ouya.R1;
				R2 = PS3Ouya.R2;
				R3 = PS3Ouya.R3;
				
				AR2 = PS3Ouya.AR2;
				ARX = PS3Ouya.ARX;
				ARY = PS3Ouya.ARY;
				
				AL2 = PS3Ouya.AL2;
				ALX = PS3Ouya.ALX;
				ALY = PS3Ouya.ALY;
			break;
		
			case OUYA_ON_WINDOWS:
				A = OuyaWindows.O;
				B = OuyaWindows.A;
				X = OuyaWindows.U;
				Y = OuyaWindows.Y;
				
				DU = OuyaWindows.DU;
				DD = OuyaWindows.DD;
				DL = OuyaWindows.DL;
				DR = OuyaWindows.DR;
				
				//PAUSE = OuyaWindows.
				
				L1 = OuyaWindows.L1;
				L2 = OuyaWindows.L2;
				L3 = OuyaWindows.L3;
				
				R1 = OuyaWindows.R1;
				R2 = OuyaWindows.R2;
				R3 = OuyaWindows.R3;
				
				AR2 = OuyaWindows.AR2;
				ARX = OuyaWindows.ARX;
				ARY = OuyaWindows.ARY;
				
				AL2 = OuyaWindows.AL2;
				ALX = OuyaWindows.ALX;
				ALY = OuyaWindows.ALY;
				break;
				
			case XBOX_ON_WINDOWS:
				A = XboxWindows.A;
				B = XboxWindows.B;
				X = XboxWindows.X;
				Y = XboxWindows.Y;
				
				DU = XboxWindows.DU;
				DD = XboxWindows.DD;
				DL = XboxWindows.DL;
				DR = XboxWindows.DR;
				
				PAUSE = XboxWindows.START;
				
				L1 = XboxWindows.L1;
				L2 = XboxWindows.AL2;
				L3 = XboxWindows.BACK;
				
				R1 = XboxWindows.R1;
				R2 = XboxWindows.AR2;
				R3 = XboxWindows.R3;
				
				AR2 = XboxWindows.AR2;
				ARX = XboxWindows.ARX;
				ARY = XboxWindows.ARY;
				
				AL2 = XboxWindows.AL2;
				ALX = XboxWindows.ALX;
				ALY = XboxWindows.ALY;
				break;
			
			case PS3_ON_WINDOWS:
				A = PS3Windows.X;
				B = PS3Windows.CIRCLE;
				X = PS3Windows.TRIANGLE;
				Y = PS3Windows.SQUARE;
				
				DU = PS3Windows.DU;
				DD = PS3Windows.DD;
				DL = PS3Windows.DL;
				DR = PS3Windows.DR;
				
				PAUSE = PS3Windows.START;
				
				L1 = PS3Windows.L1;
				L2 = PS3Windows.AL2;
				L3 = PS3Windows.L3;
				
				R1 = PS3Windows.R1;
				R2 = PS3Windows.AR2;
				R3 = PS3Windows.R3;
				
				AR2 = PS3Windows.AR2;
				ARX = PS3Windows.ARX;
				ARY = PS3Windows.ARY;
				
				AL2 = PS3Windows.AL2;
				ALX = PS3Windows.ALX;
				ALY = PS3Windows.ALY;
				break;
			case LOGITECH_ON_WINDOWS:
				A = LogitechWindows.A;
				B = LogitechWindows.B;
				X = LogitechWindows.X;
				Y = LogitechWindows.Y;
				
				DU = LogitechWindows.DU;
				DD = LogitechWindows.DD;
				DL = LogitechWindows.DL;
				DR = LogitechWindows.DR;
				
				PAUSE = LogitechWindows.START;
				
				L1 = LogitechWindows.L1;
				L2 = LogitechWindows.L2;
				L3 = LogitechWindows.L3;
				
				R1 = LogitechWindows.R1;
				R2 = LogitechWindows.R2;
				R3 = LogitechWindows.R3;
				
				AR2 = LogitechWindows.AR2;
				ARX = LogitechWindows.ARX;
				ARY = LogitechWindows.ARY;
				
				AL2 = LogitechWindows.AL2;
				ALX = LogitechWindows.ALX;
				ALY = LogitechWindows.ALY;
			break;
				
			case KEYBOARD_ON_WINDOWS:
				A = KeyboardWindows.A;
				B = KeyboardWindows.B;
				X = KeyboardWindows.X;
				Y = KeyboardWindows.Y;
				
				DU = KeyboardWindows.DU;
				DD = KeyboardWindows.DD;
				DL = KeyboardWindows.DL;
				DR = KeyboardWindows.DR;
				
				//PAUSE = KeyboardWindows.START;
				
				L1 = KeyboardWindows.L1;
				L2 = KeyboardWindows.AL2;
				L3 = KeyboardWindows.L3;
				
				R1 = KeyboardWindows.R1;
				R2 = KeyboardWindows.AR2;
				R3 = KeyboardWindows.R3;
				
				AR2 = KeyboardWindows.AR2;
				ARX = KeyboardWindows.ARX;
				ARY = KeyboardWindows.ARY;
				
				AL2 = KeyboardWindows.AL2;
				ALX = KeyboardWindows.ALX;
				ALY = KeyboardWindows.ALY;
				break;
				
			case OUYA_ON_MAC:
				A = OuyaMac.O;
				B = OuyaMac.A;
				X = OuyaMac.U;
				Y = OuyaMac.Y;
				
				DU = OuyaMac.DU;
				DD = OuyaMac.DD;
				DL = OuyaMac.DL;
				DR = OuyaMac.DR;
				
				PAUSE = OuyaMac.SYSTEM;
				
				L1 = OuyaMac.L1;
				L2 = OuyaMac.L2;
				L3 = OuyaMac.L3;
				
				R1 = OuyaMac.R1;
				R2 = OuyaMac.R2;
				R3 = OuyaMac.R3;
				
				AR2 = OuyaMac.AR2;
				ARX = OuyaMac.ARX;
				ARY = OuyaMac.ARY;
				
				AL2 = OuyaMac.AL2;
				ALX = OuyaMac.ALX;
				ALY = OuyaMac.ALY;
				
				STICK_DEAD_ZONE = OuyaMac.STICK_DEAD_ZONE;
			break;
				
			case XBOX_ON_MAC:
				A = XboxMac.A;
				B = XboxMac.B;
				X = XboxMac.X;
				Y = XboxMac.Y;
				
				DU = XboxMac.DU;
				DD = XboxMac.DD;
				DL = XboxMac.DL;
				DR = XboxMac.DR;
				
				PAUSE = XboxMac.START;
				
				L1 = XboxMac.L1;
				L2 = XboxMac.L2;
				L3 = XboxMac.L3;
				
				R1 = XboxMac.R1;
				R2 = XboxMac.R2;
				R3 = XboxMac.R3;
				
				AR2 = XboxMac.AR2;
				ARX = XboxMac.ARX;
				ARY = XboxMac.ARY;
				
				AL2 = XboxMac.AL2;
				ALX = XboxMac.ALX;
				ALY = XboxMac.ALY;
				break;
			
			case PS3_ON_MAC:
				A = PS3Mac.X;
				B = PS3Mac.CIRCLE;
				X = PS3Mac.SQUARE;
				Y = PS3Mac.TRIANGLE;
				
				DU = PS3Mac.DU;
				DD = PS3Mac.DD;
				DL = PS3Mac.DL;
				DR = PS3Mac.DR;
				
				PAUSE = PS3Mac.START;
				
				L1 = PS3Mac.L1;
				L2 = PS3Mac.L2;
				L3 = PS3Mac.L3;
				
				R1 = PS3Mac.R1;
				R2 = PS3Mac.R2;
				R3 = PS3Mac.R3;
				
				AR2 = PS3Mac.AR2;
				ARX = PS3Mac.ARX;
				ARY = PS3Mac.ARY;
				
				AL2 = PS3Mac.AL2;
				ALX = PS3Mac.ALX;
				ALY = PS3Mac.ALY;
			break;
			
			case LOGITECH_ON_MAC:
				A = LogitechMac.A;
				B = LogitechMac.B;
				X = LogitechMac.X;
				Y = LogitechMac.Y;
				
				DU = LogitechMac.DU;
				DD = LogitechMac.DD;
				DL = LogitechMac.DL;
				DR = LogitechMac.DR;
				
				PAUSE = LogitechMac.START;
				
				L1 = LogitechMac.L1;
				L2 = LogitechMac.L2;
				L3 = LogitechMac.L3;
				
				R1 = LogitechMac.R1;
				R2 = LogitechMac.R2;
				R3 = LogitechMac.R3;
				
				AR2 = LogitechMac.AR2;
				ARX = LogitechMac.ARX;
				ARY = LogitechMac.ARY;
				
				AL2 = LogitechMac.AL2;
				ALX = LogitechMac.ALX;
				ALY = LogitechMac.ALY;
			break;
			
			case SERVAL_ON_MAC:
				A = ServalMac.A;
				B = ServalMac.B;
				X = ServalMac.X;
				Y = ServalMac.Y;
				
				DU = ServalMac.DU;
				DD = ServalMac.DD;
				DL = ServalMac.DL;
				DR = ServalMac.DR;
				
				PAUSE = ServalMac.START;
				
				L1 = ServalMac.L1;
				L2 = ServalMac.L2;
				L3 = ServalMac.L3;
				
				R1 = ServalMac.R1;
				R2 = ServalMac.R2;
				R3 = ServalMac.R3;
				
				AR2 = ServalMac.AR2;
				ARX = ServalMac.ARX;
				ARY = ServalMac.ARY;
				
				AL2 = ServalMac.AL2;
				ALX = ServalMac.ALX;
				ALY = ServalMac.ALY;
			break;
			
			case OUYA_ON_LINUX:
				A = OuyaLinux.O;
				B = OuyaLinux.A;
				X = OuyaLinux.U;
				Y = OuyaLinux.Y;
				
				DU = OuyaLinux.DU;
				DD = OuyaLinux.DD;
				DL = OuyaLinux.DL;
				DR = OuyaLinux.DR;
				
				PAUSE = OuyaLinux.SYSTEM;
				
				L1 = OuyaLinux.L1;
				L2 = OuyaLinux.L2;
				L3 = OuyaLinux.L3;
				
				R1 = OuyaLinux.R1;
				R2 = OuyaLinux.R2;
				R3 = OuyaLinux.R3;
				
				AR2 = OuyaLinux.AR2;
				ARX = OuyaLinux.ARX;
				ARY = OuyaLinux.ARY;
				
				AL2 = OuyaLinux.AL2;
				ALX = OuyaLinux.ALX;
				ALY = OuyaLinux.ALY;
				break;
			
			case XBOX_ON_LINUX:
				A = XboxLinux.A;
				B = XboxLinux.B;
				X = XboxLinux.X;
				Y = XboxLinux.Y;
				
				DU = XboxLinux.DU;
				DD = XboxLinux.DD;
				DL = XboxLinux.DL;
				DR = XboxLinux.DR;
				
				PAUSE = XboxLinux.START;
				
				L1 = XboxLinux.L1;
				L2 = XboxLinux.AL2;
				L3 = XboxLinux.L3;
				
				R1 = XboxLinux.R1;
				R2 = XboxLinux.AR2;
				R3 = XboxLinux.R3;
				
				AR2 = XboxLinux.AR2;
				ARX = XboxLinux.ARX;
				ARY = XboxLinux.ARY;
				
				AL2 = XboxLinux.AL2;
				ALX = XboxLinux.ALX;
				ALY = XboxLinux.ALY;
			break;
			
			case PS3_ON_LINUX:
				A = PS3Linux.X;
				B = PS3Linux.CIRCLE;
				X = PS3Linux.SQUARE;
				Y = PS3Linux.TRIANGLE;
					
				DU = PS3Linux.DU;
				DD = PS3Linux.DD;
				DL = PS3Linux.DL;
				DR = PS3Linux.DR;
				
				PAUSE = PS3Linux.START;
				
				L1 = PS3Linux.L1;
				L2 = PS3Linux.AL2;
				L3 = PS3Linux.L3;
				
				R1 = PS3Linux.R1;
				R2 = PS3Linux.AR2;
				R3 = PS3Linux.R3;
					
				AR2 = PS3Linux.AR2;
				ARX = PS3Linux.ARX;
				ARY = PS3Linux.ARY;
				
				AL2 = PS3Linux.AL2;
				ALX = PS3Linux.ALX;
				ALY = PS3Linux.ALY;
			break;
			
			case NONE: break;
			default: break;
		}
	}
} // End class