/*
 * @(#)HowPlay.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.screens.helpers.ControllerDrawer;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.cgc.screens.helpers.LanguageKeys;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;

/*
 * Contains the data for the How To Play screen
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 * @author William Ziegler
 */
public class HowPlay extends CGCScreen 
{
	private int selected;
	private int menuSafeWidth = Data.MENU_WIDTH - (2 * MENU_BUFFER);
	private int menuSafeHeight = Data.MENU_HEIGHT - (2 * MENU_BUFFER);
	private float resizeScaleX = Data.MENU_WIDTH / 1920;
	private int framesHeld = 0;
	private String[] items = new String[2];
	private ShapeRenderer shapes;
	
	private String[] messages = {
		ChaseApp.lang.get(LanguageKeys.half_input),
		ChaseApp.lang.get(LanguageKeys.change_left),
		ChaseApp.lang.get(LanguageKeys.change_right),
		ChaseApp.lang.get(LanguageKeys.confirm_right),
		ChaseApp.lang.get(LanguageKeys.previous_right),
		ChaseApp.lang.get(LanguageKeys.confirm_left),
		ChaseApp.lang.get(LanguageKeys.previous_left),
		ChaseApp.lang.get(LanguageKeys.confirm_left),
		ChaseApp.lang.get(LanguageKeys.confirm_right),
		ChaseApp.lang.get(LanguageKeys.cops_punch_grab),
		ChaseApp.lang.get(LanguageKeys.convicts_punch_grab),
		ChaseApp.lang.get(LanguageKeys.convicts_mash),
		ChaseApp.lang.get(LanguageKeys.cops_punch),
		ChaseApp.lang.get(LanguageKeys.tree_punch),
		ChaseApp.lang.get(LanguageKeys.pause),
		ChaseApp.lang.get(LanguageKeys.move_left),
		ChaseApp.lang.get(LanguageKeys.move_right),
		ChaseApp.lang.get(LanguageKeys.punch_left),
		ChaseApp.lang.get(LanguageKeys.punch_right),
		ChaseApp.lang.get(LanguageKeys.jump_left),
		ChaseApp.lang.get(LanguageKeys.jump_right),
		ChaseApp.lang.get(LanguageKeys.callout_left),
		ChaseApp.lang.get(LanguageKeys.callout_right)
	};
	
	//Timer variables
	private boolean showAlternateTips;
	private CGCTimer tipClock;
	private Timer.Task tipTask;
	private float tipTime = 4.0f;
	
	private final int TUTORIAL = 0;
	private final int BACK = 1;
	
	/*
	 * Creates a HowPlay object
	 * 
	 * @param app					The app running this screen
	 */
	public HowPlay(ChaseApp app)
	{
		super(app);
		title = ChaseApp.lang.get(LanguageKeys.how_play);
		titleLayout.updateText(title);
		
		prevScreen = ChaseApp.mainMenu;
		
		items[TUTORIAL] = ChaseApp.lang.get(LanguageKeys.tutorial);
		items[BACK] = ChaseApp.lang.get(LanguageKeys.back);
		
		selected = 0;
		shapes = app.getShapes();
		shapes.setProjectionMatrix(ChaseApp.menuCam.combined);
		
		setUpTimer();
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
		
		if (boss.isPressed(ControlType.MENU_UP) || keyboardLeft.isPressed(ControlType.MENU_UP)
				|| keyboardRight.isPressed(ControlType.MENU_UP))
		{
			framesHeld++;
			if (framesHeld == 1)
			{
				selected = (selected-1+items.length)%items.length;
			}
			
		}
		else if (boss.isPressed(ControlType.MENU_DOWN) || keyboardLeft.isPressed(ControlType.MENU_DOWN)
				|| keyboardRight.isPressed(ControlType.MENU_DOWN))
		{
			framesHeld++;
			if (framesHeld == 1)
			{
				selected = (selected+1+items.length)%items.length;
			}
		}
		else
		{
			framesHeld = 0;
		}
		
		if (boss.justPressed(ControlType.SELECT) || keyboardLeft.justPressed(ControlType.SELECT)
				|| keyboardRight.justPressed(ControlType.SELECT))
		{
			if (selected == BACK)
			{
				boss.changeControlState(ControlType.SELECT, false);
				keyboardLeft.changeControlState(ControlType.SELECT, false);
				keyboardRight.changeControlState(ControlType.SELECT, false);
				TimerManager.removeTimer(tipClock);
				TimerManager.clear();
				CharacterSelect.tutorial = false;
				myApp.setScreen(ChaseApp.mainMenu);
			}
			else if (selected == TUTORIAL)
			{
				boss.changeControlState(ControlType.SELECT, false);
				keyboardLeft.changeControlState(ControlType.SELECT, false);
				keyboardRight.changeControlState(ControlType.SELECT, false);
				TimerManager.removeTimer(tipClock);
				TimerManager.clear();
				CharacterSelect.tutorial = true;
				myApp.setScreen(ChaseApp.characterSelect);
			}
		}
		
		if (boss.justPressed(ControlType.BACK) || keyboardLeft.justPressed(ControlType.BACK)
				|| keyboardRight.justPressed(ControlType.BACK))
		{
			boss.changeControlState(ControlType.BACK, false);
			keyboardLeft.changeControlState(ControlType.BACK, false);
			keyboardRight.changeControlState(ControlType.BACK, false);
			TimerManager.removeTimer(tipClock);
			TimerManager.clear();
			CharacterSelect.tutorial = false;
			myApp.setScreen(ChaseApp.mainMenu);
		}
		
		super.handleInput();
	}

	/*
	 * Draws this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#render(float)
	 */
	public void render(float delta) 
	{
		super.render(delta);
		
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
		
		for(int i = 0; i < items.length; i++)
		{
			if(i == selected) 
			{ 
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
				ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
			}
			else
			{
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
				ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
			}
			ChaseApp.menuFont.draw(sBatch, items[i], Data.START_X + MENU_BUFFER, 
					((int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
					- (55 * i) + ChaseApp.menuFont.getLineHeight()/2f));
		}
		ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);

		renderJoeStuff(delta);

		renderGameDiagram();
		
		sBatch.end();
	}
	
	ControllerDrawer leftMove;
	ControllerDrawer rightMove;
	
	ControllerDrawer leftPunch;
	ControllerDrawer rightPunch;
	
	ControllerDrawer leftJump;
	ControllerDrawer rightJump;
	
	ControllerDrawer leftWho;
	ControllerDrawer rightWho;
	
	/*
	 * Renders the visual button prompts to explain how to play the game
	 * 
	 * @param delta					Seconds elapsed since last frame
	 */
	private void renderJoeStuff(float delta)
	{
		// move
		leftMove.draw(sBatch, delta);
		rightMove.draw(sBatch, delta);
		// punch
		leftPunch.draw(sBatch, delta);
		rightPunch.draw(sBatch, delta);
		// jump
		leftJump.draw(sBatch, delta);
		rightJump.draw(sBatch, delta);
		// who am i?
		leftWho.draw(sBatch, delta);
		rightWho.draw(sBatch, delta);
	}

	/*
	 * Show this screen
	 * 
	 * @see com.badlogic.gdx.Screen#show()
	 */
	public void show()
	{
		super.show();
		
		float col1_left_x = -200;
		float col1_right_x = -80;
		
		float col2_left_x = 280;
		float col2_right_x = 400;
		
		float row1_y = 200;
		
		float row2_y = -100;
		
		if(leftMove == null)
		{
			leftMove = new ControllerDrawer(MenuTextureRegion.MID_CENTER, MenuTextureRegion.MID_CENTER);
			leftMove.showWing(true);
			leftMove.showAnimation(ControllerDrawer.STICK_ROTATE);
			leftMove.setWiggle(col1_left_x, row1_y);
			
			leftMove.setMessage(ChaseApp.lang.get(LanguageKeys.make_moves_son), 0, 110, Align.center);
			
			rightMove = new ControllerDrawer(MenuTextureRegion.MID_CENTER, MenuTextureRegion.MID_CENTER);
			rightMove.showWing(false);
			rightMove.showAnimation(ControllerDrawer.STICK_ROTATE);
			rightMove.setWiggle(col1_right_x, row1_y);
			
			leftPunch = new ControllerDrawer(MenuTextureRegion.MID_CENTER, MenuTextureRegion.MID_CENTER);
			leftPunch.showWing(true);
			leftPunch.showAnimation(ControllerDrawer.DPAD_ANY_BLINK);
			leftPunch.setWiggle(col2_left_x, row1_y);
			
			leftPunch.setMessage(ChaseApp.lang.get(LanguageKeys.punch), 0, 120, Align.center);
			
			rightPunch = new ControllerDrawer(MenuTextureRegion.MID_CENTER, MenuTextureRegion.MID_CENTER);
			rightPunch.showWing(false);
			rightPunch.showAnimation(ControllerDrawer.FACE_ANY_BLINK);
			rightPunch.setWiggle(col2_right_x, row1_y);
			
			leftJump = new ControllerDrawer(MenuTextureRegion.MID_CENTER, MenuTextureRegion.MID_CENTER);
			leftJump.showWing(true);
			leftJump.showAnimation(ControllerDrawer.L_BUMPER);
			leftJump.setWiggle(col1_left_x, row2_y);

			leftJump.setMessage(ChaseApp.lang.get(LanguageKeys.jump), 0, 110, Align.center);
			
			rightJump = new ControllerDrawer(MenuTextureRegion.MID_CENTER, MenuTextureRegion.MID_CENTER);
			rightJump.showWing(false);
			rightJump.showAnimation(ControllerDrawer.R_BUMPER);
			rightJump.setWiggle(col1_right_x, row2_y);
			
			leftWho = new ControllerDrawer(MenuTextureRegion.MID_CENTER, MenuTextureRegion.MID_CENTER);
			leftWho.showWing(true);
			leftWho.showAnimation(ControllerDrawer.STICK_3_BLINK);
			leftWho.setWiggle(col2_left_x, row2_y);
			
			leftWho.setMessage(ChaseApp.lang.get(LanguageKeys.who_am_i), 0, 110, Align.center);
			
			rightWho = new ControllerDrawer(MenuTextureRegion.MID_CENTER, MenuTextureRegion.MID_CENTER);
			rightWho.showWing(false);
			rightWho.showAnimation(ControllerDrawer.STICK_3_BLINK);
			rightWho.setWiggle(col2_right_x, row2_y);
		}
		
		selected = 0;
		
		TimerManager.start();
		TimerManager.addTimer(tipClock);
	}
	
	/*
	 * Show the controller diagram for the game controls
	 */
	public void renderGameDiagram()
	{
		if (!showAlternateTips)
		{
			//Tip 1
			ChaseApp.menuFont.draw(sBatch, messages[9], 
					MENU_BUFFER + menuSafeWidth / 2 - 550, 
					MENU_BUFFER + menuSafeHeight / 2 - 300);
			//Tip 2
			ChaseApp.menuFont.draw(sBatch, messages[10], 
					MENU_BUFFER + menuSafeWidth / 2 + 25, 
					MENU_BUFFER + menuSafeHeight / 2 - 300);
		}
		else
		{
			//Tip 3
			ChaseApp.menuFont.draw(sBatch, messages[11], 
					MENU_BUFFER + menuSafeWidth / 2 - 550, 
					MENU_BUFFER + menuSafeHeight / 2 - 300);
			//Tip 4
			ChaseApp.menuFont.draw(sBatch, messages[12], 
					MENU_BUFFER + menuSafeWidth / 2 + 25, 
					MENU_BUFFER + menuSafeHeight / 2 - 300);
			//Tip 5
			ChaseApp.menuFont.draw(sBatch, messages[13], 
					MENU_BUFFER + menuSafeWidth / 2 + 25, 
					MENU_BUFFER + menuSafeHeight / 2 - 400);
		}
	}
	
	/*
	 * Resize the screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#resize(int, int)
	 */
	public void resize(int width, int height)
	{
		menuSafeWidth = Data.MENU_WIDTH - (2 * MENU_BUFFER);
		menuSafeHeight = Data.MENU_HEIGHT - (2 * MENU_BUFFER);
		
		resizeScaleX = (float)(menuSafeWidth) / 1920.0f;
		if (resizeScaleX > 1.0f)
		{
			resizeScaleX = 1.0f;
		}
	}
	
	/*
	 * Creates the Timer to change displayed tips
	 */
	public void setUpTimer()
	{
		tipTask = new Timer.Task()
		{
			public void run()
			{
				showAlternateTips = !showAlternateTips;
			}
		};
		
		tipClock = new CGCTimer(tipTask, tipTime, true, "tipClock");
	}
} // End class