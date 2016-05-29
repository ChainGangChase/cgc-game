/*
 * @(#)CGCTimer.java		0.2 14/3/20
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc;
import com.badlogic.gdx.utils.Timer.Task;

/*
 * Handles the logic for Timer
 * 
 * @version 0.2 14/3/20
 * @author JD Kelly
 */
public class CGCTimer
{
	private Task myTask;
	private float myDelay;
	private float myTimePassed;
	private int myRepeats;
	private int timesRepeated;
	private boolean infinite = false;
	public String name ="";
	
	/*
	 * Creates a Timer that repeats once or runs indefinitely
	 * 
	 * @param task					The task that this Timer will run
	 * @param delay					The delay (in seconds) for this task
	 * @param looping				Whether this will run once or indefinitely
	 * @param name					The name of this timer as a string
	 */
	public CGCTimer(Task task, float delay, boolean looping, String name)
	{
		myTask = task;
		myDelay = delay;
		myTimePassed = 0.0f;
		myRepeats = 0; 
		timesRepeated = 0;
		infinite = looping;
		this.name = name;
	}
	
	/*
	 * Creates a Timer that runs a set number of times
	 * 
	 * @param task					The task that this Timer will run
	 * @param delay					The delay (in seconds) before the task runs
	 * @param repeat				The number of times the task will repeat
	 */
	public CGCTimer(Task task, float delay, int repeat)
	{
		myTask = task;
		myDelay = delay;
		myTimePassed = 0;
		myRepeats = repeat;
		timesRepeated = 0;
		infinite = false;
	}
	
	/*
	 * Updates this timer
	 * 
	 * @param delta					Seconds elapsed since the last frame
	 */
	public void update(float delta)
	{
		myTimePassed += delta;
		
		if(myTimePassed > myDelay)
		{
			myTask.run();
			timesRepeated++;
			myTimePassed -= myDelay;
			
			if(timesRepeated >= myRepeats && !infinite)
			{
				TimerManager.removeTimer(this);
				myTimePassed = 0.0f;
			}
		}
	}
	
	/*
	 * Determines if the timer is running
	 * 
	 * @return						Whether or not the timer is running
	 */
	public boolean isRunning()
	{
		float per = getPercent();
		if(per > 0.0f && per <= 1.0f && !TimerManager.isPaused() && TimerManager.contains(this))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*
	 * Gets percentage completion of this timer
	 * 
	 * @return						The percent completion of this timer (0.0-1.0)
	 */
	public float getPercent()
	{
		if(myRepeats == 0)
		{
			return myTimePassed/myDelay;
		}
		
		return Math.min(((timesRepeated * myDelay) + myTimePassed)/(myDelay*myRepeats), 1.0f);
	}
	
	/*
	 * Resets the values of this timer
	 */
	public void reset()
	{
		timesRepeated = 0;
		myTimePassed = 0.0f;
	}
} // End class
