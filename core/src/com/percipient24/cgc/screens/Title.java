/*
 * @(#)Title.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.screens.helpers.ControllerDrawer;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.enums.ControlType;
import com.percipient24.enums.SupportedControllers;
import com.percipient24.input.ControlAdapter;
import com.percipient24.tweens.MenuTextureRegionAccessor;

/*
 * Contains the data for the Title screen
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 * @author William Ziegler
 * @author Christopher Rider
 */
public class Title extends CGCScreen 
{
	// pieces of the CGC logo
	// sliced for future animation
	private MenuTextureRegion border;
	private MenuTextureRegion chain;
	private MenuTextureRegion chase;
	private MenuTextureRegion gang;
	private MenuTextureRegion ground;
	private MenuTextureRegion railway;
	private MenuTextureRegion sky;
	private MenuTextureRegion trees;
	
	private TweenManager tManager;
	
	// whether or not to wiggle during update
	private boolean wiggling;
	
	// the amount of random offset to apply as a wiggle
	private Vector2 wiggle = new Vector2();
	
	// Controller variables
	private SupportedControllers[] se;
	private boolean showPressStart = false;
	private boolean controllersFound = false;
	
	// Timer variables
	private CGCTimer startClock;
	private Timer.Task startTask;
	private float startTime = 4.0f;
	
	private ControllerDrawer left;
	private ControllerDrawer right;

	private MenuTextureRegion betaBadge;
	/*
	 * Creates a Title object
	 * 
	 * @param app					The app running this screen
	 */
	public Title(ChaseApp app) 
	{
		super(app);
		this.title = "";
		titleLayout.updateText(title);
		
		tManager = new TweenManager();
		Tween.registerAccessor(MenuTextureRegion.class, new MenuTextureRegionAccessor());
		
		// The magic numbers below were calculated with the help of
		// Photoshop and Joe's Position Helper spreadsheet: https://docs.google.com/spreadsheets/d/1NHRIiHrQDuAQwMn-k1fISl9f-i2aWqgwyswKgc3lwHA/edit#gid=0
		// The first number of each coordinate is the difference between the lower left corners of the art asset, relative to the sky asset, when positioned correctly in photoshop.
		// The second number (-450f) is half the width of the sky texture region.
		
		border  = new MenuTextureRegion(
				ChaseApp.titleScreenAtlas.findRegion("border"),
				new Vector2(0f, 113f),
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_CENTER);
		
		chain   = new MenuTextureRegion(ChaseApp.titleScreenAtlas.findRegion("chain"),
				new Vector2(0f, 334f),
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_CENTER);
		
		chase   = new MenuTextureRegion(ChaseApp.titleScreenAtlas.findRegion("chase"),
				new Vector2(0f, -111f),
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_CENTER);
		
		gang    = new MenuTextureRegion(ChaseApp.titleScreenAtlas.findRegion("gang"),
				new Vector2(0, 102.5f),
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_CENTER);
		
		ground  = new MenuTextureRegion(ChaseApp.titleScreenAtlas.findRegion("ground"),
				new Vector2(-14f, -349.5f),
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_CENTER);
		
		railway = new MenuTextureRegion(ChaseApp.titleScreenAtlas.findRegion("railway"),
				new Vector2(12f, -289f),
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_CENTER);
		
		sky     = new MenuTextureRegion(ChaseApp.titleScreenAtlas.findRegion("sky"),
				new Vector2(0f, 0f),
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_CENTER);
		
		trees   = new MenuTextureRegion(ChaseApp.titleScreenAtlas.findRegion("trees"),
				new Vector2(-4f, -33f),
				MenuTextureRegion.MID_CENTER,
				MenuTextureRegion.MID_CENTER);
		
		left = new ControllerDrawer(MenuTextureRegion.MID_LEFT, MenuTextureRegion.MID_LEFT);
		left.showWing(true);
		left.showAnimation(ControllerDrawer.DPAD_DOWN_BLINK);
		left.setWiggle(23, 0);
		
		right = new ControllerDrawer(MenuTextureRegion.MID_RIGHT, MenuTextureRegion.MID_RIGHT);
		right.showWing(false);
		right.showAnimation(ControllerDrawer.FACE_DOWN_BLINK);
		right.setWiggle(-23, 0);
		
		playTitleAnimation();
	}
	
	/*
	 * Resets title textures to original positions and kicks off the animation.
	 */
	private void playTitleAnimation()
	{
		tManager.killAll();
		
		// these things start really tiny
		chain.setScaleX(0.0f);
		chain.setScaleY(0.0f);
		gang.setScaleX(0.0f);
		gang.setScaleY(0.0f);
		chase.setScaleX(0.0f);
		chase.setScaleY(0.0f);
		border.setScaleX(0.0f);
		border.setScaleY(0.0f);
		trees.setScaleX(0f);
		trees.setScaleY(0f);

		// these things start in their usual position
		chain.setStartingOffsetFromRestingPosition(0, 0);
		gang.setStartingOffsetFromRestingPosition(0, 0);
		chase.setStartingOffsetFromRestingPosition(0, 0);
		border.setStartingOffsetFromRestingPosition(0, 0);
		
		// these things start off-screen		
		ground.setStartingOffsetFromRestingPosition(0, -1080);
		railway.setStartingOffsetFromRestingPosition(0, -1080);
		sky.setStartingOffsetFromRestingPosition(0, 1080);		
		
		// start a timeline sequence:
		Timeline.createSequence()

		// make the title bounce in a word at a time
		.beginParallel()
			.push(Tween.to(chain, MenuTextureRegionAccessor.SCALE_XY, .55f)
					.ease(Bounce.OUT)
					.target(1.0f, 1.0f))
			
			.push(Tween.to(gang, MenuTextureRegionAccessor.SCALE_XY, .55f)
					.ease(Bounce.OUT)
					.delay(0.5f)
					.target(1.0f, 1.0f))
				
			.push(Tween.to(chase, MenuTextureRegionAccessor.SCALE_XY, .55f)
					.ease(Bounce.OUT)
					.delay(1.0f)
					.target(1.0f, 1.0f))
			
			.push(Tween.to(border, MenuTextureRegionAccessor.SCALE_XY, .55f)
					.ease(Quad.OUT)
					.delay(1.0f)
					.target(1.0f, 1.0f))
		.end()
				
		
		// have the sky fall in
		// while the words fly off the top of the screen
		// ...
		// eventually the words fall back to the ground
		// and a callback starts everything shaking
		.beginParallel()
			.push(Tween.to(sky, MenuTextureRegionAccessor.TRANSLATE_Y_NEGATIVE, 1.8f)
				.ease(Back.OUT)
				.target(sky.getRestingPosition().y))
			.push(Tween.to(chain, MenuTextureRegionAccessor.TRANSLATE_Y_POSITIVE, 0.8f)
				.ease(Circ.INOUT)
				.delay(0.2f)
				.target(chain.getY()+Data.ACTUAL_HEIGHT))
			.push(Tween.to(gang, MenuTextureRegionAccessor.TRANSLATE_Y_POSITIVE, 0.8f)
				.ease(Circ.INOUT)
				.delay(0.25f)
				.target(gang.getY()+Data.ACTUAL_HEIGHT))
			.push(Tween.to(chase, MenuTextureRegionAccessor.TRANSLATE_Y_POSITIVE, 0.8f)
				.ease(Circ.INOUT)
				.delay(0.3f)
				.target(chase.getY()+Data.ACTUAL_HEIGHT))
			.push(Tween.to(border, MenuTextureRegionAccessor.TRANSLATE_Y_POSITIVE, 0.8f)
				.delay(0.3f)
				.ease(Circ.INOUT)
				.target(border.getY()+Data.ACTUAL_HEIGHT))

			.push(Tween.to(chase, MenuTextureRegionAccessor.TRANSLATE_Y_NEGATIVE, 0.5f)
				.ease(Quad.IN)
				.delay(1.3f)
				.target(chase.getY()))
			.push(Tween.to(gang, MenuTextureRegionAccessor.TRANSLATE_Y_NEGATIVE, 0.5f)
				.ease(Quad.IN)
				.delay(1.35f)
				.target(gang.getY()))
			.push(Tween.to(chain, MenuTextureRegionAccessor.TRANSLATE_Y_NEGATIVE, 0.5f)
				.ease(Quad.IN)
				.delay(1.4f)
				.target(chain.getY()))
			.push(Tween.to(border, MenuTextureRegionAccessor.TRANSLATE_Y_NEGATIVE, 0.5f)
				.ease(Quad.IN)
				.delay(1.3f)
				.target(border.getY())
				.setCallbackTriggers(TweenCallback.COMPLETE)
				.setCallback(startWiggle)) // start wiggling assets because of the impact

			.push(Tween.to(ground, MenuTextureRegionAccessor.TRANSLATE_Y_POSITIVE, 0.6f)
				.ease(Quad.INOUT)
				.delay(1.0f)
				.target(ground.getRestingPosition().y))
			
			.push(Tween.to(railway, MenuTextureRegionAccessor.TRANSLATE_Y_POSITIVE, 0.6f)
				.ease(Quad.INOUT)
				.delay(1.0f)
				.target(railway.getRestingPosition().y))
		.end()
		
		// the trees slide out elastically
		.push(Tween.set(trees, MenuTextureRegionAccessor.SCALE_XY)
				.target(0.8f,0.8f))			
		.push(Tween.to(trees, MenuTextureRegionAccessor.SCALE_XY, 2.2f)
				.ease(Elastic.OUT)
				.target(1.0f, 1.0f))
		
		.start(tManager);
	}
	
	// called when Chase hits the ground
	private TweenCallback startWiggle = new TweenCallback()
	{
		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			// starts wiggling, and sets a callback to stop wiggling
			wiggling = true;	
			Tween.call(stopWiggle).delay(0.7f).start(tManager);
			showPressStart = true;
			TimerManager.addTimer(startClock);
			Tween.to(background, MenuTextureRegionAccessor.ALPHA, 1.0f).target(1.0f).start(tManager);
		}
	};
	
	// called after a delay from startWiggle
	private TweenCallback stopWiggle = new TweenCallback()
	{
		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			// stops us from wiggling
			wiggling = false;
		}
	};

	private void transitionOut() {
		Tween.to(background, MenuTextureRegionAccessor.ALPHA, 1.0f).target(0.0f).start(tManager);
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
				TimerManager.removeTimer(startClock);
				input.setBoss(o);
				input.getBoss().changeControlState(ControlType.SELECT, false);
				super.handleInput();
				myApp.setScreen(ChaseApp.mainMenu);
				transitionOut();
				return;
			}
		}
		
		super.handleInput();
	}
	
	/*
	 * Applies a random value to wiggle's components
	 */
	private void updateWiggle()
	{
		if(wiggling)
		{
			wiggle.x = MathUtils.random(-3f, 3f);
			wiggle.y = MathUtils.random(-3f, 3f);
		}
		else
		{
			wiggle.x = 0;
			wiggle.y = 0;
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
		tManager.update(delta);
		sBatch.end();
		
		sBatch.setProjectionMatrix(ChaseApp.menuCam.combined);
		sBatch.begin();
		sky.draw(sBatch);
		trees.draw(sBatch);
		updateWiggle();
		ground.draw(sBatch, wiggle);
		updateWiggle();
		railway.draw(sBatch, wiggle);
		updateWiggle();
		border.draw(sBatch);
		updateWiggle();
		chain.draw(sBatch, wiggle);
		updateWiggle();
		gang.draw(sBatch, wiggle);
		updateWiggle();
		chase.draw(sBatch, wiggle);
		
		if (showPressStart)
		{
			ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			// TODO Graphically represent Keyboard Input
			
			left.draw(sBatch, delta);
			right.draw(sBatch, delta);
			betaBadge.setRotation(0);
			betaBadge.draw(sBatch);
		}
		
		sBatch.end();
	}

	public void renderNoClear(float delta)
	{
		super.renderNoClear(delta);
		tManager.update(delta);
		sBatch.end();
		
		sBatch.setProjectionMatrix(ChaseApp.menuCam.combined);
		sBatch.begin();
		sky.draw(sBatch);
		trees.draw(sBatch);
		updateWiggle();
		ground.draw(sBatch, wiggle);
		updateWiggle();
		railway.draw(sBatch, wiggle);
		updateWiggle();
		border.draw(sBatch);
		updateWiggle();
		chain.draw(sBatch, wiggle);
		updateWiggle();
		gang.draw(sBatch, wiggle);
		updateWiggle();
		chase.draw(sBatch, wiggle);
		
		if (showPressStart)
		{
			ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			// TODO Graphically represent Keyboard Input
			
			left.draw(sBatch, delta);
			right.draw(sBatch, delta);
			betaBadge.setRotation(0);
			betaBadge.draw(sBatch);
		}
		
		sBatch.end();
	}
	
	/*
	 * Shows this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#show()
	 */
	public void show() 
	{	
		super.show();
		
		if(betaBadge == null)
		{
			betaBadge = new MenuTextureRegion(ChaseApp.menuControlsAtlas.findRegion("betaBadge"),new Vector2(0, 250),MenuTextureRegion.LOWER_CENTER,MenuTextureRegion.MID_CENTER);
		}
		
		background.setAlpha(0.0f);
		showPressStart = false;
		betaBadge.setRotation(0f);
		
		determineControllerTypes();
		
		//SoundManager.playSong("testMenu", true);
		TimerManager.start();
		setUpTimer();
		playTitleAnimation();
	}
	
	/*
	 * Hides the current screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#hide()
	 */
	public void hide()
	{
		wiggling = false;
	}
	
	/*
	 * Determine what kinds of controller are connected
	 */
	private void determineControllerTypes()
	{
		se = myApp.getSupportedControllers();
		
		for (SupportedControllers sc: se)
		{
			if (sc != null)
			{
				controllersFound = true;
				break;
			}
		}
	}
	
	/*
	 * Sets up a timer that shows what players can press to start the game
	 */
	private void setUpTimer()
	{
		startTask = new Timer.Task()
		{
			public void run()
			{
				if (!controllersFound)
				{
					Gdx.app.exit();
				}
			}
		};
		
		startClock = new CGCTimer(startTask, startTime, false, "startClock");
	}
} // End class