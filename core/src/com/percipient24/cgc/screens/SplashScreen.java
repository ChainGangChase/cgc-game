/*
 * @(#)SplashScreen.java		0.1 14/4/23
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.enums.ControlType;

/*
 * Contains the data for the splash screen at the start of the game
 * 
 * @version 0.1 14/4/23
 * @author William Ziegler
 * @author Christopher Rider
 */
public class SplashScreen extends CGCScreen 
{
	private int currentImage = 0;
	private Array<MenuTextureRegion> splashImages;
	
	//Timer variables
	private CGCTimer splashTimer;
	private Timer.Task splashTask;
	private float splashTime = 2.0f;
	
	private CGCTimer fadeInTimer;
	private Timer.Task fadeInTask;
	private float fadeInTime = 1.0f;
	
	private CGCTimer fadeOutTimer;
	private Timer.Task fadeOutTask;
	private float fadeOutTime = 1.0f;
	
	private CGCTimer waitTimer;
	private Timer.Task waitTask;
	private float waitTime = 0.5f;
	
	/*
	 * Creates a SplashScreen object
	 * 
	 * @param app					The app running this screen
	 */
	public SplashScreen(ChaseApp app)
	{
		super(app);
		
		shouldDrawBackground = false;
		
		title = "";
		titleLayout.updateText(title);
		
		splashImages = new Array<MenuTextureRegion>();
		splashImages.add(new MenuTextureRegion(ChaseApp.titleScreenAtlas.findRegion("rit"), new Vector2(0, 0)));
		splashImages.add(new MenuTextureRegion(ChaseApp.titleScreenAtlas.findRegion("magic"), new Vector2(0, 0)));
		splashImages.get(0).setAlpha(0);
		splashImages.get(1).setAlpha(0);
		
		setUpTimers();
		TimerManager.start();
	}
	
	/*
	 * Handles control input to this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#handleInput()
	 */
	public void handleInput()
	{
		for (int i = 0; i < input.controlList.length; i++)
		{
			if (input.controlList[i] != null)
			{
				if (input.controlList[i].justPressed(ControlType.SELECT))
				{
					input.controlList[i].changeControlState(ControlType.SELECT, false);
					TimerManager.clear();
					myApp.setScreen(ChaseApp.title);;
				}
				else if (input.controlList[i].justPressed(ControlType.BACK))
				{
					input.controlList[i].changeControlState(ControlType.BACK, false);
					TimerManager.clear();
					myApp.setScreen(ChaseApp.title);
				}
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
		
		if (currentImage < splashImages.size)
		{
			if (fadeInTimer.isRunning())
			{
				splashImages.get(currentImage).setAlpha(fadeInTimer.getPercent());
			}
			else if (fadeOutTimer.isRunning())
			{
				splashImages.get(currentImage).setAlpha(1 - fadeOutTimer.getPercent());
			}
			
			splashImages.get(currentImage).draw(sBatch);
		}
		
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
		
		myApp.loadGame();
		TimerManager.addTimer(waitTimer);
	}
	
	/*
	 * Creates the timers to change displayed images
	 */
	public void setUpTimers()
	{
		splashTask = new Timer.Task()
		{
			public void run()
			{
				TimerManager.addTimer(fadeOutTimer);
			}
		};
		
		splashTimer = new CGCTimer(splashTask, splashTime, false, "splashTimer");
		
		fadeInTask = new Timer.Task()
		{
			public void run()
			{
				TimerManager.addTimer(splashTimer);
				splashImages.get(currentImage).setAlpha(1);
			}
		};
		
		fadeInTimer = new CGCTimer(fadeInTask, fadeInTime, false, "fadeInTimer");
		
		fadeOutTask = new Timer.Task()
		{
			public void run()
			{
				currentImage++;
				if (currentImage < splashImages.size)
				{
					TimerManager.addTimer(waitTimer);
				}
				else
				{
					myApp.setScreen(ChaseApp.title);
				}
			}
		};
		
		fadeOutTimer = new CGCTimer(fadeOutTask, fadeOutTime, false, "fadeOutTimer");
		
		waitTask = new Timer.Task()
		{
			public void run()
			{
				TimerManager.addTimer(fadeInTimer);
			}
		};
		
		waitTimer = new CGCTimer(waitTask, waitTime, false, "waitTimer");
	}
} // End class