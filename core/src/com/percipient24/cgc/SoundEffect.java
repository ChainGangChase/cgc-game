/*
 * @(#)SoundEffect.java		0.2 14/2/11
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Timer;

/*
 * A class for playing and disposing of sound effects
 *
 * @version 0.2 14/2/11
 * @author JD Kelly
 */
public class SoundEffect 
{
	//variables for this effect's sound
	private Sound mySound;
	private long myID;

	//booleans related to if and how this is playing
	private boolean playing = false;
	private boolean looping = false;
	private boolean paused = false;
	
	//timers for determining the when the sound ends
	private CGCTimer playTimer;
	private Timer.Task playTask;
	private float playTime = 1.0f;
	
	/*
	 * Creates a new sound effect from the file
	 * 
	 * @param f						The file handle for the audio file
	 */
	public SoundEffect(FileHandle f)
	{
		mySound = Gdx.audio.newSound(f);
		playTime = (float)(f.length())/(float)(16384);
		
		playTask = new Timer.Task() {
			
			public void run() 
			{
				if(!looping)
				{
					playing = false;
					paused = false;
				}
			}
		};
		
		playTimer = new CGCTimer(playTask, playTime, looping, "playTimer");
	}
	
	/*
	 * Gets the sound this SoundEffect will make
	 * 
	 * @return                      The Sound this SoundEffect makes
	 */
	public Sound gSound()
	{
		return mySound;
	}
	
	/*
	 * Gets the ID of the current Sound instance
	 * 
	 * @return                      The ID of the current Sound instance
	 */
	public long gID()
	{
		return myID;
	}
	
	/*
	 * Starts the play timer
	 */
	public void startPlayTimer()
	{
		if(playTimer.isRunning())
		{
			TimerManager.removeTimer(playTimer);
		}
		
		playTimer = new CGCTimer(playTask, playTime, looping, "playTimer");
		TimerManager.addTimer(playTimer);
	}
	
	/*
	 * Plays mySound and sets myID to the returned long
	 */
	public void play()
	{
		myID = mySound.play();
		startPlayTimer();
	}
	
	/*
	 * Plays mySound at the given volume and sets the myID to the returned long
	 * 
	 * @param volume                The desired volume (0 - 1)
	 */
	public void play(float volume)
	{
		myID = mySound.play(volume);
		startPlayTimer();
	}
	
	/*
	 * Plays mySound once at the volume, pitch and pan and sets myID to the returned long
	 * 
	 * @param volume                The desired volume (0 - 1)
	 * @param pitch                 The desired pitch (0.5 - 2)
	 * @param pan                   The desired pan (-1 - +1)
	 */
	public void play(float volume, float pitch, float pan)
	{
		myID = mySound.play(volume, pitch, pan);
		startPlayTimer();
	}
	
	/*
	 * Starts a loop of mySound and sets myID to the returned long
	 */
	public void loop()
	{
		myID = mySound.loop();
		playing = true;
		looping = true;
		startPlayTimer();
		
	}
	
	/*
	 * Starts a loop of mySound at the given volume. Then, sets myID to the returned long
	 * 
	 * @param volume                The desired volume (0 - 1)
	 */
	public void loop(float volume)
	{
		myID = mySound.loop(volume);
		playing = true;
		looping = true;
		startPlayTimer();
	}
	
	/*
	 * Starts a loop of mySound at the volume, pitch and pan and sets myID to the returned long.
	 * 
	 * @param volume                The desired volume (0 - 1)
	 * @param pitch                 The desired pitch (0.5 - 2.0)
	 * @param pan                   The desired pan (-1 - +1)
	 */
	public void loop(float volume, float pitch, float pan)
	{
		myID = mySound.loop(volume, pitch, pan);
		playing = true;
		looping = true;
		startPlayTimer();
	}
	
	/*
	 * Stops this instance of mySound
	 */
	public void stop()
	{
		mySound.stop();
		playing = false;
		looping = false;
		paused = false;
	}
	
	/*
	 * Pause function
	 * 
	 * @param b						Whether the sound is to be paused or unpaused
	 */
	public void pause(boolean b)
	{
		if(b)
		{
			mySound.pause();
			
			playing = false;
			paused = true;
		}
		
		else
		{
			mySound.resume();
			playing = true;
			paused = false;
		}
	}
	
	/*
	 * A setter for mySound's looping
	 * 
	 * @param loop                  Whether or not mySound is looping
	 */
	public void sLooping(boolean loop)
	{
		mySound.setLooping(myID, loop);
	}
	
	/*
	 * A setter for mySound's volume
	 * 
	 * @param volume                The desired volume (0 - 1)
	 */
	public void sVolume(float volume)
	{
		mySound.setVolume(myID, volume);
	}
	
	/*
	 * A getter for playing
	 * 
	 * @return                      Whether or not this sound is playing
	 */
	public boolean isPlaying()
	{
		return playing;
	}
	
	/*
	 * A getter for paused
	 * 
	 * @return						Whether or not the sound is paused
	 */
	public boolean isPaused()
	{
		return paused;
	}
	
	/** Sets the panning and volume of the sound instance with the given id as returned by Sound.class's play() or Sound.class's play(float).
	 * If the sound is no longer playing, this has no effect.
	 * @param soundId the sound id
	 * @param pan panning in the range -1 (full left) to 1 (full right). 0 is center position.
	 * @param volume the volume in the range [0,1]. */
	public void setPan (long soundId, float pan, float volume){
		mySound.setPan(myID, pan, volume);
	}	
	
	/*
	 * Disposes of mySound
	 */
	public void dispose()
	{
		mySound.dispose();
	}
}// End class
