/*
 * @(#)Overlay.java		0.3 14/5/14
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.screens;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.percipient24.helpers.StringLayout;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.screens.helpers.ControllerDrawer;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;

/*
 * Contains the logic to create a menu overlay
 * 
 * @version 0.3 14/5/14
 * @author Christopher Rider
 */
public class Overlay extends CGCScreen 
{
	private final float WHITE_BANNER_Y = 0.69f;
	private final float WHITE_BANNER_HEIGHT = 0.12f;
	
	private final float BLACK_BANNER_Y = 0.7f;
	private final float BLACK_BANNER_HEIGHT = 0.1f;
	
	public static boolean displayedConnectError = false;
	
	private CGCScreen owner;
	private TextureRegion backColor;
	private String overMessage;
	private StringLayout layout;
	private ShapeRenderer shapes;
	private ControllerDrawer accept;
	
	/*
	 * Creates a new Overlay object
	 * 
	 * @param app					The ChaseApp this overlay is for
	 * @param owner					The screen this overlay is from
	 * @param initMessage			The message to display
	 */
	public Overlay(ChaseApp app, CGCScreen owner, String initMessage) 
	{
		super(app);
		
		this.owner = owner;
		backColor = ChaseApp.menuControlsAtlas.findRegion("overlaycolor");
		title = "";
		titleLayout.updateText(title);
		overMessage = initMessage;
		layout = new StringLayout(overMessage, ChaseApp.menuFont);
		items.add("");
		shapes = myApp.getShapes();
		
		accept = new ControllerDrawer(MenuTextureRegion.LOWER_RIGHT, MenuTextureRegion.MID_RIGHT);
		
		/*
		 * USE THIS TO CALL THE OVERLAY
		 * ChaseApp.overlay = new Overlay(myApp, this, "Main Menu reached!");
		   myApp.setScreen(ChaseApp.overlay);
		 */
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

			myApp.setScreen(owner);
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
			
			myApp.setScreen(owner);
		}
	}
	
	/*
	 * Draws this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#render(float)
	 */
	public void render(float delta)
	{
		handleInput();
		
		owner.render(delta);
		
		ControlAdapter boss = input.getBoss();
		sBatch.begin();
		if(boss.isController())
		{
			accept.draw(sBatch, delta);
		}
		
		
		sBatch.draw(backColor, 0, 0, Data.ACTUAL_WIDTH, Data.ACTUAL_HEIGHT);
		sBatch.end();
		
		// Draws the info banner on the screen - Width of screen, height is arbitrary
		shapes.begin(ShapeType.Filled);
		shapes.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		shapes.rect(0, Data.MENU_HEIGHT * WHITE_BANNER_Y, 
				Data.GAME_WIDTH, Data.MENU_HEIGHT * WHITE_BANNER_HEIGHT);
		shapes.end();
		
		// Draws the inside of the info banner
		shapes.begin(ShapeType.Filled);
		shapes.setColor(0.0f, 0.0f, 0.0f, 1.0f);
		shapes.rect(0, Data.MENU_HEIGHT * BLACK_BANNER_Y, 
				Data.GAME_WIDTH, Data.MENU_HEIGHT * BLACK_BANNER_HEIGHT);
		shapes.end();
		
		sBatch.begin();

		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
		ChaseApp.menuFont.draw(
			sBatch,
			layout.getLayout(),
			Data.MENU_WIDTH / 2 - layout.getLayout().width / 2, 
			Data.MENU_HEIGHT * (BLACK_BANNER_Y + (BLACK_BANNER_HEIGHT * .9f)));

		sBatch.end();
		sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	/*
	 * @see com.percipient24.cgc.screens.CGCScreen#resize(int, int)
	 */
	public void resize(int width, int height)
	{
		
	}
	
	/*
	 * Show this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#show()
	 */
	public void show()
	{
		selected = 0;
		
		ControlAdapter boss = input.getBoss();

		if(boss.isController())
		{
			if(boss.isLeft())
			{
				accept.showWing(true);
				accept.showAnimation(ControllerDrawer.DPAD_DOWN_BLINK);
				accept.setWiggle(-23, 63);
				
			}
			else
			{
				accept.showWing(false);	
				accept.showAnimation(ControllerDrawer.FACE_DOWN_BLINK);
				accept.setWiggle(-23, 63);
			}
		}
	}
	
	/*
	 * Hide this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#hide()
	 */
	public void hide()
	{
		ChaseApp.overlay = null;
	}
} // End class