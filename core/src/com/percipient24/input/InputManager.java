/*
 * @(#)InputManager.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.percipient24.enums.SupportedControllers;
import com.percipient24.input.maps.ControllerMap;

/*
 * Handles interaction between controls and the main game program
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 * @author Christopher Rider
 */
public class InputManager extends ControllerAdapter 
{
	public ControlAdapter[] controlList = new ControlAdapter[10];
	
	private Array<ControllerHalf> mapA;
	
	public Array<Controller> controllerList;
	public KeyboardManager testKeys;
	
	private ControlAdapter boss;
	private ControlAdapter keyboardRight;
	private ControlAdapter keyboardLeft;
	
	private int controlsUsed;

	/*
	 * Create a new InputManager
	 * 
	 * @param se					The controller types supported
	 */
	public InputManager(SupportedControllers[] se)
	{
		for (int i = 0; i < controlList.length; i++)
		{
			controlList[i] = new ControlAdapter();
		}
		
		// Get all connected controllers
		controllerList = Controllers.getControllers();
		mapA = new Array<ControllerHalf>(8);
		
		if (controllerList.size > 0)
		{
			for (int i = 0; i < controlList.length; i++)
			{
				if (i/2 >= controllerList.size)
				{
					continue;
				}
				controlList[i].setController(controllerList.get(i/2), (i % 2 == 1 ? true : false));
				
				ControllerHalf tempHalf = new ControllerHalf(controllerList.get(i/2), (i%2 == 1 ? true : false));
				mapA.add(tempHalf);

				ControllerMap tempMap = new ControllerMap();
				tempMap.mapFrom(se[i/2]);
				controlList[i].setMap(tempMap);
			}
			controlsUsed = controllerList.size * 2;
		}
		
		// Add keyboard to 1 and 2
		testKeys = new KeyboardManager(controlList[controlsUsed], controlList[controlsUsed + 1]);
		controlList[controlsUsed].setKeys(testKeys, true);
		controlList[controlsUsed + 1].setKeys(testKeys, false);
		
		keyboardRight = controlList[controlsUsed];
		keyboardLeft = controlList[controlsUsed + 1];
		
		Gdx.app.log("CONTROLLER", "There are "+controllerList.size+" controllers!");
	}
	
	/*
	 * Get the controller in charge of menu navigation
	 * 
	 * @return						The controller currently in charge of menus
	 */
	public ControlAdapter getBoss() 
	{
		return boss;
	}

	/*
	 * Set the controller in charge of menu navigation
	 * 
	 * @param boss					The new controller in charge of menus
	 */
	public void setBoss(ControlAdapter boss) 
	{
		this.boss = boss;
	}

	/*
	 * Gets the ControlAdapter corresponding to the left side of the keyboard
	 * 
	 * @return						The ControlAdapter for the left side of the keyboard
	 */
	public ControlAdapter getKeyboardLeft()
	{
		return keyboardLeft;
	}
	
	/*
	 * Gets the ControlAdapter corresponding to the right side of the keyboard
	 * 
	 * @return						The ControlAdapter for the right side of the keyboard
	 */
	public ControlAdapter getKeyboardRight()
	{
		return keyboardRight;
	}
	
	/*
	 * Whether or not the accelerometer has moved
	 * 
	 * @see com.badlogic.gdx.controllers.ControllerAdapter#accelerometerMoved(com.badlogic.gdx.controllers.Controller, int, com.badlogic.gdx.math.Vector3)
	 */
	public boolean accelerometerMoved(Controller arg0, int arg1, Vector3 arg2) 
	{
		return false;
	}

	/*
	 * Whether or not the axis has moved
	 * 
	 * @see com.badlogic.gdx.controllers.ControllerAdapter#axisMoved(com.badlogic.gdx.controllers.Controller, int, float)
	 */
	public boolean axisMoved(Controller arg0, int arg1, float arg2) 
	{
		//Gdx.app.log("AXIS", ""+arg1);
		//Gdx.app.log("AXIS VALUE", ""+arg2);
		
		for (int i = 0; i < mapA.size; i++)
		{
			if (mapA.get(i).controller.equals(arg0))
			{
				controlList[i].getCurrent().moveAxis(arg1, arg2);
			}
		}
		
		return false;
	}

	/*
	 * Whether or not a button is down
	 * 
	 * @see com.badlogic.gdx.controllers.ControllerAdapter#buttonDown(com.badlogic.gdx.controllers.Controller, int)
	 */
	public boolean buttonDown(Controller arg0, int arg1) 
	{
		//Gdx.app.log("CONTROLLER BUTTON", ""+arg1);
		
		for (int i = 0; i < mapA.size; i++)
		{
			if (mapA.get(i).controller.equals(arg0))
			{
				controlList[i].updateButton(arg1, true);
			}
		}
		
		return false;
	}

	/*
	 * Whether or not a button is up
	 * 
	 * @see com.badlogic.gdx.controllers.ControllerAdapter#buttonUp(com.badlogic.gdx.controllers.Controller, int)
	 */
	public boolean buttonUp(Controller arg0, int arg1) 
	{	
		for (int i = 0; i < mapA.size; i++)
		{
			if (mapA.get(i).controller.equals(arg0))
			{
				controlList[i].updateButton(arg1, false);
			}
		}
		
		return false;
	}
	
	/*
	 * Whether or not a controller is connected
	 * 
	 * @see com.badlogic.gdx.controllers.ControllerAdapter#connected(com.badlogic.gdx.controllers.Controller)
	 */
	public void connected(Controller arg0) 
	{
		// put the new controller into the first available controller
		/*for(int i = 0; i < controlList.length; i++)
		{
			if(controlList[i].controller == null)
			{
				controlList[i].connect(controllerList.get(i));
				map.put(controllerList.get(i), controlList[i]);
				controlList[i].setControllerMap(new ControllerMap());
				
				controlList[i].getControllerMap().mapFrom(se[i]);
				
				return;
			}
		}*/
	}
	
	/*
	 * Whether or not a controller is disconnected
	 * 
	 * @see com.badlogic.gdx.controllers.ControllerAdapter#disconnected(com.badlogic.gdx.controllers.Controller)
	 */
	public void disconnected(Controller arg0) 
	{
		//map.get(arg0).disconnect();
		//map.remove(arg0);
		// TODO Ask to reconnect?
	}

	/*
	 * Whether or not a POV hat has moved
	 * 
	 * @see com.badlogic.gdx.controllers.ControllerAdapter#povMoved(com.badlogic.gdx.controllers.Controller, int, com.badlogic.gdx.controllers.PovDirection)
	 */
	public boolean povMoved(Controller arg0, int arg1, PovDirection arg2)
	{
		//Gdx.app.log("CONTROLLER POV", ""+arg1);
		//Gdx.app.log("CONTROLLER POV", ""+arg2);
		
		if (arg2 == PovDirection.center)
		{
			for (int i = 0; i < mapA.size; i++)
			{
				if (mapA.get(i).controller.equals(arg0))
				{
					controlList[i].getCurrent().releasePov();
				}
			}
		}
		else
		{
			for (int i = 0; i < mapA.size; i++)
			{
				if (mapA.get(i).controller.equals(arg0))
				{
					controlList[i].getCurrent().pressPov(arg2);
				}
			}
		}
		
		return false;
	}

	/*
	 * Whether or not the X slider has moved
	 * 
	 * @see com.badlogic.gdx.controllers.ControllerAdapter#xSliderMoved(com.badlogic.gdx.controllers.Controller, int, boolean)
	 */
	public boolean xSliderMoved(Controller arg0, int arg1, boolean arg2) 
	{
		//Gdx.app.log("xSlide", ""+arg1+" "+arg2);
		return false;
	}

	/*
	 * Whether or not the Y slider has moved
	 * @see com.badlogic.gdx.controllers.ControllerAdapter#ySliderMoved(com.badlogic.gdx.controllers.Controller, int, boolean)
	 */
	public boolean ySliderMoved(Controller arg0, int arg1, boolean arg2) 
	{
		//Gdx.app.log("ySlide", ""+arg1+" "+arg2);
		return false;
	}

	/*
	 * Go to the next controller
	 */
	public void step()
	{
		for(int i = 0; i < controlsUsed; i++)
		{
			controlList[i].update();
		}
		
		controlList[controlsUsed].update();
		controlList[controlsUsed+1].update();
	}

	/*
	 * Gets the total number of controllers connected
	 * 
	 * @return						The number of controllers connected
	 */
	public int totalConnected() 
	{
		return Controllers.getControllers().size;
	}
} // End class