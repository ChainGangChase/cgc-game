package com.percipient24.cgc.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.percipient24.b2helpers.StringLayout;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;
import com.percipient24.tweens.MenuTextureRegionAccessor;

public class _ResolutionTestScreen extends CGCScreen {
	
	// pieces of the CGC logo
	// sliced for future animation
	/*private MenuTextureRegion lower_right;
	private MenuTextureRegion lower_left;
	private MenuTextureRegion lower_center;
	private MenuTextureRegion upper_right;
	private MenuTextureRegion upper_left;
	private MenuTextureRegion upper_center;
	private MenuTextureRegion mid_right;
	private MenuTextureRegion mid_left;
	private MenuTextureRegion mid_center;*/
	
	private MenuTextureRegion[] perimeterRegions = new MenuTextureRegion[81];
	
	private TweenManager tManager;

	private StringLayout mLayout;
	
	/*
	 * Creates a Title object
	 * 
	 * @param app					The app running this screen
	 */
	public _ResolutionTestScreen(ChaseApp app) 
	{
		super(app);
		this.title = "";
		titleLayout.updateText(title);
		//myApp.setMenuMatrix(sBatch.getProjectionMatrix());
		
		tManager = new TweenManager();
		Tween.registerAccessor(MenuTextureRegion.class, new MenuTextureRegionAccessor());
		
		// The magic numbers below were calculated with the help of
		// Photoshop and Joe's Position Helper spreadsheet: https://docs.google.com/spreadsheets/d/1NHRIiHrQDuAQwMn-k1fISl9f-i2aWqgwyswKgc3lwHA/edit#gid=0
		// The first number of each coordinate is the difference between the lower left corners of the art asset, relative to the sky asset, when positioned correctly in photoshop.
		// The second number (-450f) is half the width of the sky texture region.
		
		/*lower_right  = new MenuTextureRegion(
				ChaseApp.characterAtlas.findRegion("thumbnail", 1),
				Vector2.Zero,
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.LOWER_RIGHT);
		
		lower_left   = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 3),
				Vector2.Zero,
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.LOWER_LEFT);
		
		lower_center = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 2),
				Vector2.Zero,
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.LOWER_CENTER);
		
		upper_right  = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 11),
				Vector2.Zero,
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.UPPER_RIGHT);
		
		upper_left   = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 14),
				Vector2.Zero,
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.UPPER_LEFT);
		
		upper_center = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 12),
				Vector2.Zero,
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.UPPER_CENTER);
		
		mid_right    = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 4),
				Vector2.Zero,
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_RIGHT);
		
		mid_left     = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 7),
				Vector2.Zero,
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_LEFT);
		
		mid_center   = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 0),
				Vector2.Zero,
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_CENTER);*/
		
		int i = 0;
		for(int anchor = 0; anchor < 9; anchor++)
		{
			perimeterRegions[i]  = new MenuTextureRegion(
					ChaseApp.characterAtlas.findRegion("thumbnail", 1),
					Vector2.Zero,
					anchor,
					MenuTextureRegion.LOWER_RIGHT);
			
			perimeterRegions[i+1]   = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 3),
					Vector2.Zero,
					anchor,
					MenuTextureRegion.LOWER_LEFT);
			
			perimeterRegions[i+2] = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 2),
					Vector2.Zero,
					anchor,
					MenuTextureRegion.LOWER_CENTER);
			
			perimeterRegions[i+3]  = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 11),
					Vector2.Zero,
					anchor,
					MenuTextureRegion.UPPER_RIGHT);
			
			perimeterRegions[i+4]   = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 14),
					Vector2.Zero,
					anchor,
					MenuTextureRegion.UPPER_LEFT);
			
			perimeterRegions[i+5] = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 12),
					Vector2.Zero,
					anchor,
					MenuTextureRegion.UPPER_CENTER);
			
			perimeterRegions[i+6]    = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 4),
					Vector2.Zero,
					anchor,
					MenuTextureRegion.MID_RIGHT);
			
			perimeterRegions[i+7]     = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 7),
					Vector2.Zero,
					anchor,
					MenuTextureRegion.MID_LEFT);
			
			perimeterRegions[i+8]   = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("thumbnail", 0),
					Vector2.Zero,
					anchor,
					MenuTextureRegion.MID_CENTER);
			i = i+9;
		}

		mLayout = new StringLayout("O Back to Menu!", ChaseApp.menuFont);
	}
	
	/*
	 * Handles control input to this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#handleInput()
	 */
	public void handleInput() 
	{
		for(int i = 0; i < input.controlList.length; i++)
		{
			ControlAdapter o = input.controlList[i];
			if(o.isConnected() && o.justPressed(ControlType.SELECT))
			{
				super.handleInput();
				myApp.setScreen(ChaseApp.mainMenu);
				return;
			}
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
		tManager.update(delta);
		
		/*lower_right  .draw(sBatch);
		lower_left   .draw(sBatch);
		lower_center .draw(sBatch);
		upper_right  .draw(sBatch);
		upper_left   .draw(sBatch);
		upper_center .draw(sBatch);
		mid_right    .draw(sBatch);
		mid_left     .draw(sBatch);
		mid_center   .draw(sBatch);*/
		
		for(int i = 0; i < perimeterRegions.length; i++)
		{
			perimeterRegions[i].draw(sBatch);
		}

		ChaseApp.menuFont.draw(
			sBatch, mLayout.getLayout(),
			MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.LOWER_CENTER].x - mLayout.getLayout().width/2, 
			MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.LOWER_CENTER].y + ChaseApp.menuFont.getLineHeight());
		
		sBatch.end();
	}
	
	/*
	 * Shows this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#show()
	 */
	public void show() 
	{
		sBatch = myApp.getBatch();
		//SoundManager.playSong("testMenu", true);
		TimerManager.start();
	}
} // End class