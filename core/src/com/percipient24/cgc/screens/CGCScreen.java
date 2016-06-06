/*
 * @(#)CGCScreen.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.percipient24.helpers.StringLayout;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;
import com.percipient24.input.InputManager;

/*
 * Base class for a game screen
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 * @author Christopher Rider
 */
public class CGCScreen implements Screen 
{
	public static int MENU_BUFFER = 50;
	public static float FONT_MAIN = 1.0f;
	public static float FONT_SIDE = 0.75f;
	public String title = "Screen Title";
	public StringLayout titleLayout;
	public StringLayout messageLayout;
	public StringLayout itemLayout;
	protected ChaseApp myApp;
	
	// Generic menu control
	protected String message;
	protected Array<String> items;
	protected int selected;
	protected int framesHeld;
	protected boolean display;
	protected CGCScreen prevScreen;
	
	protected SpriteBatch sBatch;
	protected InputManager input;
	
	protected boolean shouldDrawBackground = true;
	protected MenuTextureRegion background;
	
	/*
	 * Creates a new CGCScreen object
	 * 
	 * @param app					The app running this screen
	 */
	public CGCScreen(ChaseApp app)
	{
		myApp = app;
		selected = 0;
		framesHeld = 0;
		display = true;
		message = "Message not set";
		items = new Array<String>();
		sBatch = app.getBatch();
		input = app.getInput();

		titleLayout = new StringLayout(title, ChaseApp.menuFont);
		messageLayout = new StringLayout(message, ChaseApp.menuFont);
		itemLayout = new StringLayout("", ChaseApp.menuFont);
		
		background = new MenuTextureRegion(ChaseApp.menuControlsAtlas.findRegion("background"),
				Vector2.Zero,
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_CENTER);
	}
	
	/*
	 * @see com.percipient24.input.InputManager
	 */
	public void handleInput()
	{	
		input.step();
	}
	
	/*
	 * Handles moving up/down a generic menu screen
	 * 
	 * @param boss					The controller in charge of menus
	 * @param keyboardLeft			The left side of the attached keyboard (if any)
	 * @param keyboardRight			The right side of the attached keyboard (if any)
	 */
	protected void navigateMenu(ControlAdapter boss, ControlAdapter keyboardLeft, 
			ControlAdapter keyboardRight)
	{
		if (display)
		{
			if (boss.isPressed(ControlType.MENU_UP) || keyboardLeft.isPressed(ControlType.MENU_UP)
					|| keyboardRight.isPressed(ControlType.MENU_UP))
			{
				framesHeld++;
				if (framesHeld == 1)
				{
					selected = (selected-1+items.size)%items.size;
				}
				else if (framesHeld > 90 && framesHeld % 5 == 0)
				{
					selected = (selected-1+items.size)%items.size;
				}
				else if (framesHeld > 30 && framesHeld % 10 == 0)
				{
					selected = (selected-1+items.size)%items.size;
				}
			}
			else if (boss.isPressed(ControlType.MENU_DOWN) || keyboardLeft.isPressed(ControlType.MENU_DOWN)
					|| keyboardRight.isPressed(ControlType.MENU_DOWN))
			{
				framesHeld++;
				if (framesHeld == 1)
				{
					selected = (selected+1+items.size)%items.size;
				}
				else if (framesHeld > 90 && framesHeld % 5 == 0)
				{
					selected = (selected+1+items.size)%items.size;
				}
				else if (framesHeld > 30 && framesHeld % 10 == 0)
				{
					selected = (selected+1+items.size)%items.size;
				}
			}
			else
			{
				framesHeld = 0;
			}
		}
	}
	
	/*
	 * Handles what happens when A is pressed on a generic menu
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
			if (!display)
			{
				display = true;
			}
			else
			{
				myApp.setScreen(prevScreen);
			}
		}
	}
	
	/*
	 * Handles what happens when B is pressed on a generic menu
	 * 
	 * @param boss					The controller in charge of menus
	 * @param keyboardLeft			The left side of the attached keyboard (if any)
	 * @param keyboardRight			The right side of the attached keyboard (if any)
	 */
	protected void handleB(ControlAdapter boss, ControlAdapter keyboardLeft, ControlAdapter keyboardRight)
	{
		if (boss.justPressed(ControlType.BACK) || keyboardLeft.justPressed(ControlType.BACK)
				|| keyboardRight.justPressed(ControlType.BACK))
		{
			boss.changeControlState(ControlType.BACK, false);
			keyboardLeft.changeControlState(ControlType.BACK, false);
			keyboardRight.changeControlState(ControlType.BACK, false);
			if (display)
			{
				myApp.setScreen(prevScreen);
			}
			else
			{
				display = true;
			}
		}
	}
	
	/*
	 * Renders this screen
	 * 
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	public void render(float delta) 
	{
		if (!(this instanceof Overlay) && ChaseApp.overlay == null)
		{
			handleInput();
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		sBatch.begin();
		
		if(shouldDrawBackground)
		{
			background.draw(sBatch);
		}
		
		ChaseApp.titleFont.getData().setScale(1.0f);
		ChaseApp.titleFont.draw(
			sBatch,
			titleLayout.getLayout(),
			MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].x,
			MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y);
		ChaseApp.menuFont.getData().setScale(FONT_MAIN);
	}
	
	/*
	 * Draws a generic menu screen
	 */
	protected void drawGenericMenu()
	{
		if (display)
		{
			sBatch.setProjectionMatrix(ChaseApp.menuCam.combined);
			
			ChaseApp.menuFont.getData().setScale(FONT_SIDE);
			
			int startY = (int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120;
			int yChange = (int)(40);
			
			for(int i = 0; i < items.size; i++)
			{
				if(i == selected) 
				{
					ChaseApp.menuFont.getData().setScale(FONT_MAIN);
					ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
				}
				else
				{
					ChaseApp.menuFont.getData().setScale(FONT_SIDE);
					ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
				}
				itemLayout.updateText(items.get(i));
				ChaseApp.menuFont.draw(
					sBatch,
					itemLayout.getLayout(),
					Data.START_X + MENU_BUFFER, 
					(startY + ChaseApp.menuFont.getLineHeight() / 2f));

				startY -= yChange;
			}
			ChaseApp.menuFont.getData().setScale(FONT_SIDE);
			ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		}
		else
		{
			itemLayout.updateText(message);
			ChaseApp.menuFont.draw(
				sBatch,
				itemLayout.getLayout(),
				MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.LOWER_LEFT].x, 
				MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.LOWER_LEFT].y + MENU_BUFFER);
		}
	}

	/*
	 * Resize the screen
	 * 
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	public void resize(int width, int height) 
	{
	}

	/*
	 * Show this screen
	 * 
	 * @see com.badlogic.gdx.Screen#show()
	 */
	public void show() 
	{
		performLayout();
	}
	
	/*
	 * This function is called by ChaseApp ::after:: ChaseApp processes a resize event.
	 * It should be overridden to re-layout the screen based on the new size.
	 */
	public void performLayout()
	{
		sBatch.setProjectionMatrix(ChaseApp.menuCam.combined);
	}

	/*
	 * Hide this screen
	 * 
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	public void hide() 
	{
	}

	/*
	 * Pause this screen
	 * 
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	public void pause() 
	{
	}

	/*
	 * Resume this screen
	 * 
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	public void resume() 
	{
	}

	/*
	 * Delete this screen
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	public void dispose() 
	{
	}
} // End class