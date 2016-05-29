/*
 * @(#)CreateOwn.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import com.percipient24.cgc.ChaseApp;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;

/*
 * Contains the data for the Create Own Map screen
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 */
public class CreateOwn extends CGCScreen 
{
	/*
	 * Creates a CreateOwn object
	 * 
	 * @param app					The app running this screen
	 */
	public CreateOwn(ChaseApp app)
	{
		super(app);
		title = "Create Your Own Map";
		titleLayout.updateText(title);
		message = "Go to http://www.chaingangchase.com/maps to make your own versions!";
		
		items.add("Create A Map");
		items.add("Back");
		
		prevScreen = ChaseApp.mainMenu;
	}
	
	/*
	 * Handles control input to this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#handleInput()
	 */
	public void handleInput() 
	{
		ControlAdapter boss = input.getBoss();
		ControlAdapter keyboardLeft = input.getKeyboardLeft(); 
		ControlAdapter keyboardRight = input.getKeyboardRight();
		
		handleB(boss, keyboardLeft, keyboardRight);
		navigateMenu(boss, keyboardLeft, keyboardRight);
		handleA(boss, keyboardLeft, keyboardRight);
		
		super.handleInput();
	}

	/*
	 * Handles what happens when A is pressed on this menu
	 * 
	 * @param boss					The controller in charge of menus
	 * @param keyboardLeft			The left side of the attached keyboard (if any)
	 * @param keyboardRight			The right side of the attached keyboard (if any)
	 */
	protected void handleA(ControlAdapter boss, ControlAdapter keyboardLeft, ControlAdapter keyboardRight)
	{
		if (boss.justPressed(ControlType.SELECT) || keyboardLeft.justPressed(ControlType.SELECT)
				|| keyboardRight.justPressed(ControlType.SELECT))
		{
			boss.changeControlState(ControlType.SELECT, false);
			keyboardLeft.changeControlState(ControlType.SELECT, false);
			keyboardRight.changeControlState(ControlType.SELECT, false);
			if (display)
			{
				switch(selected)
				{
					case 0: display = false; break;
					case 1: myApp.setScreen(prevScreen); break;
				}
			}
			else
			{
				display = true;
			}
		}
	}
	
	/*
	 * Draws this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#render(float)
	 */
	public void render(float delta) 
	{
		super.render(delta);
		
		drawGenericMenu();
		
		sBatch.end();
	}
	
	/*
	 * Show this screen
	 * 
	 * @see com.badlogic.gdx.Screen#show()
	 */
	public void show()
	{
		super.show();
		
		selected = 0;
	}
} // End class