/*
 * @(#)PunchMeArrow.java		0.2 14/6/2
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.BossFight;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.entities.boss.Sheriff;

/*
 * Contains the logic for the arrows that let the players know to punch the Sheriff to end the game
 * 
 * @version 0.2 14/6/2
 * @author J.D. Kelly
 */
public class PunchMeArrow extends CGCOverlay {

	private AnimationManager animManager;
	
	private CGCTimer blinkClock;
	private Timer.Task blinkTask;
	private float blinkTime = 1.0f;
	private boolean showArrows = true;
	
	private float scaleX;
	private float scaleY;
	private Vector2 pos = Vector2.Zero;

	public PunchMeArrow(SpriteBatch newBatch) {
		super(newBatch);
		animManager = BossFight.getAnimManager();
		setTimersAndTasks();
	}
	
	/*
	 * Renders the arrows to tell players to punch the Sheriff
	 * 
	 * @param delta					Seconds elapsed since last frame
	 */
	public void render(float delta)
	{
		if (showElement && showArrows)
		{
			animManager.drawFrame(sBatch, AnimationManager.punchMeAnim, 0,
					pos.x + animManager.gWidth(AnimationManager.punchMeAnim) * scaleX / 4,
					pos.y + animManager.gHeight(AnimationManager.punchMeAnim) * scaleY,
					scaleX, scaleY);
		}
	}
	
	/*
	 * Sets the position of the arrow
	 * 
	 * @param v						The new position vector
	 */
	public void setPosition(Vector2 v)
	{
		pos = v;
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
				//showArrows = !showArrows;
			}
		};
		
		blinkClock = new CGCTimer(blinkTask, blinkTime, true, "blinkClock");
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
}
