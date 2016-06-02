/*
 * @(#)MainMenu.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.math.Vector2;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.screens.helpers.ControllerDrawer;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;
import com.percipient24.b2helpers.LanguageKeys;

/*
 * Contains the data for the Main Menu screen
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 */
public class MainMenu extends CGCScreen 
{
	private ControllerDrawer navigation;
	private ControllerDrawer accept;
	
	private MenuTextureRegion betaBadge;
	
	private String messageText = "Now with public GitHub!";
	
	/*
	 * Creates a MainMenu object
	 * 
	 * @param app					The app running this screen
	 */
	public MainMenu(ChaseApp app)
	{
		super(app);
		title = ChaseApp.lang.get(LanguageKeys.main_menu);
		titleLayout.updateText(title);

		items.add("Start a Game");
		items.add("How to Play");
		items.add("Options");
		items.add("Credits");
		items.add("Title");
		items.add("Exit");
		//items.add("_ResTest");
		
		navigation = new ControllerDrawer(MenuTextureRegion.LOWER_RIGHT, MenuTextureRegion.MID_RIGHT);
		navigation.setMessage("Change Item", -40, 20, Align.right);
		
		accept = new ControllerDrawer(MenuTextureRegion.LOWER_RIGHT, MenuTextureRegion.MID_RIGHT);
		accept.setMessage("Select", -40, 20, Align.right);
		
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
		if(boss.justPressed(ControlType.SELECT) || keyboardLeft.justPressed(ControlType.SELECT)
				|| keyboardRight.justPressed(ControlType.SELECT))
		{
			boss.changeControlState(ControlType.SELECT, false);
			keyboardLeft.changeControlState(ControlType.SELECT, false);
			keyboardRight.changeControlState(ControlType.SELECT, false);
			switch(selected)
			{
				case 0: 
					/*if (ChaseApp.overlay == null)
					{
						ChaseApp.overlay = new Overlay(myApp, this, "Help I'm stuck in the menu");
						myApp.setScreen(ChaseApp.overlay);
					}
					break;*/myApp.setScreen(ChaseApp.characterSelect); break;
				case 1: myApp.setScreen(ChaseApp.howToPlay); break;
				case 2: myApp.setScreen(ChaseApp.options); break;
				case 3: myApp.setScreen(ChaseApp.credits); break;
				case 4: myApp.setScreen(ChaseApp.title); break;
				case 5: myApp.setScreen(ChaseApp.exit); break;
				//case 6: myApp.setScreen(ChaseApp._resTest); break;
			}
		}
	}
	
	/*
	 * Handles what happens when B is pressed on this menu
	 * 
	 * @param boss					The controller in charge of menus
	 * @param keyboardLeft			The left side of the attached keyboard (if any)
	 * @param keyboardRight			The right side of the attached keyboard (if any)
	 */
	protected void handleB(ControlAdapter boss, ControlAdapter keyboardLeft, ControlAdapter keyboardRight)
	{
		if(boss.justPressed(ControlType.BACK) || keyboardLeft.justPressed(ControlType.BACK)
				|| keyboardRight.justPressed(ControlType.BACK))
		{
			boss.changeControlState(ControlType.BACK, false);
			keyboardLeft.changeControlState(ControlType.BACK, false);
			keyboardRight.changeControlState(ControlType.BACK, false);
			if (selected == 6 || selected == 7)
			{
				selected = 7;
			}
			else
			{
				selected = 6;
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
		
		ChaseApp.menuFont.getData().setScale(FONT_SIDE);
		ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		
		ControlAdapter boss = input.getBoss();

		if(boss.isController())
		{
			navigation.draw(sBatch, delta);
			accept.draw(sBatch, delta);
		}
		
		betaBadge.setRotation(betaBadge.getRotation() + 0.25f);
		betaBadge.draw(sBatch);
		
		messageLayout.updateText(messageText);
		float messageY = messageLayout.getLayout().height;
		ChaseApp.menuFont.draw(
			sBatch,
			messageLayout.getLayout(),
			MenuTextureRegion.TITLE_SAFE_X + 0,
			2 * MenuTextureRegion.TITLE_SAFE_Y + messageY);
		
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
		
		if(betaBadge == null)
		{
			betaBadge = new MenuTextureRegion(ChaseApp.menuControlsAtlas.findRegion("betaBadge"),new Vector2(-100, -50),MenuTextureRegion.UPPER_RIGHT,MenuTextureRegion.MID_CENTER);
		}
		
		ControlAdapter boss = input.getBoss();

		if(boss.isController())
		{
			if(boss.isLeft())
			{
				navigation.showWing(true);
				navigation.showAnimation(ControllerDrawer.STICK_UP_DOWN);
				
				accept.showWing(true);
				accept.showAnimation(ControllerDrawer.DPAD_DOWN_BLINK);
			}
			else
			{
				navigation.showWing(false);
				navigation.showAnimation(ControllerDrawer.STICK_UP_DOWN);
				
				accept.showWing(false);	
				accept.showAnimation(ControllerDrawer.FACE_DOWN_BLINK);
			}
			
			navigation.setWiggle(-23, 233);
			accept.setWiggle(-23, 63);
		}

		selected = 0;
	}
} // End class