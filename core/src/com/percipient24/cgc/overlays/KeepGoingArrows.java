/*
 * @(#)KeepGoingArrows.java		0.2 14/5/12
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.overlays;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.cgc.*;

/*
 * Contains the logic for the end-of-level arrows
 * 
 * @version 0.2 14/5/12
 * @author William Ziegler
 */
public class KeepGoingArrows extends CGCOverlay
{
	private com.percipient24.cgc.art.TextureAnimationDrawer animManager;
	
	private CGCTimer blinkClock;
	private Timer.Task blinkTask;
	private float blinkTime = 0.25f;
	private boolean showArrows = true;
	
	private float scaleX;
	private float scaleY;
	
	/*
	 * Creates a new KeepGoingArrows object
	 * 
	 * @param newBatch				The SpriteBatch the game is using
	 */
	public KeepGoingArrows(SpriteBatch newBatch)
	{
		super(newBatch);
		
		animManager = CGCWorld.getAnimManager();
		
		setTimersAndTasks();
	}

	/*
	 * Renders the arrows to tell players to run off-screen
	 * 
	 * @param delta					Seconds elapsed since last frame
	 */
	public void render(float delta)
	{
		if (showElement && showArrows)
		{
			animManager.drawFrame(sBatch, com.percipient24.cgc.art.TextureAnimationDrawer.keepGoingAnim, 0,
					Data.ACTUAL_WIDTH * .25f - animManager.gWidth(com.percipient24.cgc.art.TextureAnimationDrawer.keepGoingAnim) / 2 * scaleX,
					Data.ACTUAL_HEIGHT * .9f - animManager.gHeight(com.percipient24.cgc.art.TextureAnimationDrawer.keepGoingAnim) * scaleY,
					scaleX, scaleY);
			
			animManager.drawFrame(sBatch, com.percipient24.cgc.art.TextureAnimationDrawer.keepGoingAnim, 0,
					Data.ACTUAL_WIDTH * .5f - animManager.gWidth(com.percipient24.cgc.art.TextureAnimationDrawer.keepGoingAnim) / 2 * scaleX,
					Data.ACTUAL_HEIGHT * .9f - animManager.gHeight(com.percipient24.cgc.art.TextureAnimationDrawer.keepGoingAnim) * scaleY,
					scaleX, scaleY);
			
			animManager.drawFrame(sBatch, com.percipient24.cgc.art.TextureAnimationDrawer.keepGoingAnim, 0,
					Data.ACTUAL_WIDTH * .75f - animManager.gWidth(com.percipient24.cgc.art.TextureAnimationDrawer.keepGoingAnim) / 2 * scaleX,
					Data.ACTUAL_HEIGHT * .9f - animManager.gHeight(com.percipient24.cgc.art.TextureAnimationDrawer.keepGoingAnim) * scaleY,
					scaleX, scaleY);
		}
	}
	
	/*
	 * Creates the Timers and Tasks
	 */
	public void setTimersAndTasks()
	{
		blinkTask = new Timer.Task()
		{
			public void run()
			{	
				showArrows = !showArrows;
			}
		};
		
		blinkClock = new CGCTimer(blinkTask, blinkTime, false, "blinkClock");
	}

	/*
	 * (non-Javadoc)
	 * @see com.percipient24.cgc.overlays.CGCOverlay#setShow(boolean)
	 */
	public void setShow(boolean shouldShow)
	{
		super.setShow(shouldShow);
		
		if (showElement)
		{ 
			if (!TimerManager.contains(blinkClock))
			{
				TimerManager.addTimer(blinkClock);
			}
		}
		else
		{
			TimerManager.removeTimer(blinkClock);
		}
	}
	
	/*
	 * Resizes this HUD element to fit various screen sizes
	 */
	public void resize() 
	{
		scaleX = (float)Data.ACTUAL_WIDTH / (float)1920;
		scaleY = (float)Data.ACTUAL_HEIGHT / (float)1080;
	}
} // End class
