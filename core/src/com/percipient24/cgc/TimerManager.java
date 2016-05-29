/*
 * @(#)TimerManager.java		0.2 14/3/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;;

/*
 * Contains functions for the ease of use of timers
 * 
 * @version 0.2 14/3/3
 * @author JD Kelly
 */
public class TimerManager 
{
	public static final float TIMER_DELAY = 1.0f/60.0f;
	
	private static Array<CGCTimer> timers = new Array<CGCTimer>();
	private static com.badlogic.gdx.utils.Timer myTimer = null;
	private static Task myTask;
	private static boolean paused = false;
	private static long milly;
	private static long ashford;
	
	/*
	 * Starts the TimerManager
	 */
	public static void start()
	{
		milly = TimeUtils.millis();
		if(timers == null)
		{
			timers = new Array<CGCTimer>();
		}
		else
		{
			timers.clear();
		}
		
		if(myTimer == null)
		{
			myTimer = new Timer();
			
			myTask = new Task()
			{
				public void run()
				{
					ashford = milly;
					milly = TimeUtils.millis();
					
					for(int i = 0; i < timers.size; i++)
					{
						 CGCTimer t = timers.get(i);
						 t.update((milly - ashford)/1000.0f);
					}
					
					if(myTimer != null)
					{
						myTimer.scheduleTask(this, TIMER_DELAY);
					}
				}
			};
			
			myTimer.scheduleTask(myTask, TIMER_DELAY);
		}
	}
	
	/*
	 * Adds a timer to the array of timers
	 * 
	 * @param t						The CGCTimer to be added
	 */
	public static void addTimer(CGCTimer t)
	{
		if(contains(t))
		{
			timers.get(timers.indexOf(t, true)).reset();
		}
		else
		{
			t.reset();
			timers.add(t);
		}
	}
	
	/*
	 * Removes a timer from the array of timers
	 * 
	 * @param t						The CGCTimer to be removed
	 */
	public static void removeTimer(CGCTimer t)
	{
		timers.removeValue(t, true);
	}
	
	/*
	 * Pauses all the timers
	 * 
	 * @param b						Whether the timers should be paused (true) or unpaused (false)
	 */
	public static void setPause(boolean b)
	{
		paused = b;
		if (b)
		{
			myTimer.stop();
		}
		else
		{
			myTimer.start();
			milly = TimeUtils.millis();
		}
	}
	
	/*
	 * Gets whether this is paused
	 * 
	 * @return						Whether or not this is paused
	 */
	public static boolean isPaused()
	{
		return paused;
	}
	
	/*
	 * Determines if a CGCTimer is in the timers array
	 * 
	 * @param t						The CGCTimer to look for in the array
	 * @return						Whether or not the CGCTimer is in the array
	 */
	public static boolean contains(CGCTimer t)
	{
		return timers.contains(t, true);
	}
	
	/*
	 * Clears all the timers
	 */
	public static void clear()
	{
		if(myTimer != null)
		{
			myTask.cancel();
			myTimer.stop();
			myTimer.clear();
			myTimer = null;
		}
	
		timers.clear();
	}
} // End class