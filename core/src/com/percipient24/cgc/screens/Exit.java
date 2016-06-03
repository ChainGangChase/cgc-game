/*
 * @(#)Exit.java		0.1 14/1/31
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import com.badlogic.gdx.Gdx;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.enums.ControlType;
//import com.percipient24.input.GenericController;
import com.percipient24.input.ControlAdapter;
import com.percipient24.cgc.screens.helpers.LanguageKeys;

/*
 * Contains the data for the Exit Game screen
 * 
 * @version 0.1 13/5/24
 * @author Christopher Rider
 */
public class Exit extends CGCScreen
{
	/*
	 * Creates an Exit object
	 * 
	 * @param app					The app running this screen
	 */
	public Exit(ChaseApp app)
	{
		super(app);
		title = ChaseApp.lang.get(LanguageKeys.exit_menu);
		titleLayout.updateText(title);
		
		items.add(ChaseApp.lang.get(LanguageKeys.exit_no));
		items.add(ChaseApp.lang.get(LanguageKeys.exit_yes));
		
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
			switch(selected)
			{
				case 0: myApp.setScreen(prevScreen); break;
				case 1: myApp.alert("System", "System.kill(Target.USER) aborted");
					Gdx.app.exit(); break;
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
	 * @see com.percipient24.cgc.screens.CGCScreen#show()
	 */
	public void show()
	{
		selected = 0;
	}
} // End class