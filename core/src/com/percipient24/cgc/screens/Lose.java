/*
 * @(#)Lose.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;

import com.percipient24.cgc.ChainGame;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.overlays.Transition;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;
import com.percipient24.tweens.TransitionAccessor;

/*
 * Contains the data for the Lose screen
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 */
public class Lose extends CGCScreen 
{
	private TweenManager tManager;
	private Transition transition;
	private boolean transitioning;
	
	/*
	 * Creates a Lose object
	 * 
	 * @param app					The app running this screen
	 */
	public Lose(ChaseApp app) 
	{
		super(app);
		
		title = "The Law Prevails!";
		titleLayout.updateText(title);
		
		tManager = ChaseApp.tManager;
		
		items.add("Favorite A Map");
		items.add("Play Last Chase Again");
		items.add("Character Select");
		items.add("Map Select");
		items.add("Main Menu");
		
		prevScreen = ChaseApp.mainMenu;
	}
	
	/*
	 * Handles control input to this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#handleInput()
	 */
	public void handleInput() 
	{
		if (!transitioning)
		{
			ControlAdapter boss = input.getBoss();
			ControlAdapter keyboardLeft = input.getKeyboardLeft(); 
			ControlAdapter keyboardRight = input.getKeyboardRight();
			
			handleB(boss, keyboardLeft, keyboardRight);
			navigateMenu(boss, keyboardLeft, keyboardRight);
			handleA(boss, keyboardLeft, keyboardRight);
		}
		
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
			if (selected > 0)
			{
				ChaseApp.favorite.clearMapsPlayed();
			}
			
			switch(selected)
			{
				case 0: 
					myApp.setScreen(ChaseApp.favorite); 
					break;
				case 1: 
					startTransition(); 
					break;
				case 2: 
					myApp.setScreen(ChaseApp.characterSelect); 
					break;
				case 3: 
					myApp.setScreen(ChaseApp.mapSelect); 
					break;
				case 4:
					myApp.setScreen(prevScreen); 
					break;
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
		if (transitioning)
		{
			tManager.update(delta);
			transition.render(delta);
			sBatch.setProjectionMatrix(ChaseApp.menuCam.combined);
		}
	}
	
	/*
	 * Moves a Transition in from off-screen to bring the players into the game
	 */
	private void startTransition()
	{
		transition = new Transition(sBatch, ChaseApp.mapSelect.getLastMap(), myApp);
		transition.setXPosition(Data.ACTUAL_WIDTH * 1.5f);
		transition.setShow(true);
		transitioning = true;
		
		Timeline.createSequence()
		.push
		(Tween.to(transition, TransitionAccessor.TRANSLATE_X, 1.0f).ease(Cubic.OUT))
		.pushPause(2.0f)
		.setCallback(changeToGameScreen)
		.start(tManager);
	}
	
	/*
	 * Switches the game screen to ChainGame once the above Timeline is over
	 */
	private TweenCallback changeToGameScreen = new TweenCallback()
	{
		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			transitioning = false;
			input.getBoss().getCurrent().resetData();
			myApp.setScreen(new ChainGame(myApp, ChaseApp.characterSelect.getNumPlayers(), 
					ChaseApp.mapSelect.getLastMap(), ChaseApp.mapSelect.getMapCache(), transition, false));
		}
	};
	
	/*
	 * Shows this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#show()
	 */
	public void show() 
	{
		super.show();
		
		sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		shouldDrawBackground = true;
		selected = 0;
		render(0.0f);
	}
} // End class