/*
 * @(#)SoundManager.java		0.2 14/2/10
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music; 
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.percipient24.cgc.screens.Options;
import com.percipient24.enums.Platform;

/*
 * Contains functions for playing sound effects and music
 * 
 * @version 0.2 14/2/10
 * 
 * @author JD Kelly
 * @author Christopher Rider
 */

/*
 * Sound
 * Sound pitch
 * Sound pan
 * Sound pitch pan
 */

public class SoundManager 
{
	private static Music curSong = null;
	private static float overallVolume = 1.0f;
	private static float musicVolume = 1.0f;
	private static float effectsVolume = 1.0f;
	private static boolean wasPlaying = false;
	private static ObjectMap<String, SoundEffect> sounds = new ObjectMap<String,SoundEffect>();
	private static Array<String> screams = new Array<String>();
	
	/*
	 * Loads in all sounds from a directory
	 * 
	 * @param f						The directory from which to load
	 */
	public static void loadSounds(FileHandle f)
	{
		FileHandle[] fh = f.list();
		
		for(int i = 0; i < fh.length; i++)
		{
			FileHandle temp = fh[i];
			String name = temp.nameWithoutExtension();
			
			if(temp.isDirectory())
			{
				loadSounds(temp);
			}
			
			else if(temp.extension().equals("mp3") || temp.extension().equals("wav") || temp.extension().equals("ogg"))
			{
				if(name.toLowerCase().contains("scream"))
					addScream(temp);
				else
					sounds.put(name, new SoundEffect(temp));
			}
		}
		
		overallVolume = Options.storedVolumeSettings[0];
		musicVolume = Options.storedVolumeSettings[1];
		effectsVolume = Options.storedVolumeSettings[2];
	}
	
	/*
	 * Sets the song to play only once
	 * 
	 * @param s                     The name of the song in "bin/Music".  Must be an .mp3 file
	 * @param loop					Whether or not to loop the song
	 */
	public static void playSong(String s, boolean loop)
	{
		if(curSong != null)
		{
			curSong.dispose();
		}
		
		curSong = loadSong(s);
		curSong.setVolume(overallVolume * musicVolume);
		curSong.play();
		curSong.setLooping(loop);
	}
	
	/*
	 * Plays the sound in the sound map at key value name
	 * 
	 * @param s                     The name of the sound in the sounds ObjectMap
	 * @param loop					Whether or not to loop the sound
	 */
	public static void playSound(String name, boolean loop)
	{
		if(sounds.containsKey(name))
		{
			if (!loop)
			{
				sounds.get(name).play(overallVolume * effectsVolume);
			}
			else
			{
				sounds.get(name).loop(overallVolume * effectsVolume);
			}
		}
	}
	
	/*
	 * Plays the sound stored at name with the given pitch
	 * If there is no sound at name, nothing is played
	 * 
	 * @param name                  The name of the sound in the sounds ObjectMap
	 * @param loop					Whether or not to loop the sound
	 * @param pitch                 the desired pitch of the sound (0.5 - 2.0)
	 */
	public static void playSoundPitch(String name, boolean loop, float pitch)
	{
		if(sounds.containsKey(name))
		{
			if (!loop)
			{
				sounds.get(name).play(overallVolume * effectsVolume, pitch, 0);
			}
			else
			{
				sounds.get(name).loop(overallVolume * effectsVolume, pitch, 0);
			}
		}
	}
	
	/*
	 * Plays the sound stored at name with the given pan
	 * Volume and pitch are set to normal values
	 * If there is no sound at name, nothing is played
	 * 
	 * @param name                  The name of the sound in the sounds ObjectMap
	 * @param loop					Whether or not to loop the sound
	 * @param pan                   The desired pan of the sound (-1 - +1)
	 */
	public static void playSoundPan(String name, boolean loop, float pan)
	{
		if(sounds.containsKey(name))
		{
			if (!loop)
			{
				sounds.get(name).play(overallVolume * effectsVolume, 1, pan);
			}
			else
			{
				sounds.get(name).loop(overallVolume * effectsVolume, 1, pan);
			}
		}
	}
	
	/*
	 * Plays the sound stored at name with the given pitch and pan
	 * Volume is set to normal
	 * If there is no sound at name, nothing is played
	 * 
	 * @param name                  The name of the sound in the sounds ObjectMap
	 * @param loop					Whether or not to loop the sound
	 * @param pitch                 The desired pitch (0.5 - 2.0)
	 * @param pan                   The desired pan (-1 - +1)
	 */
	public static void playSoundPitchPan(String name, boolean loop, float pitch, float pan)
	{
		if(sounds.containsKey(name))
		{
			if (!loop)
			{
				sounds.get(name).play(overallVolume * effectsVolume, pitch, pan);
			}
			else
			{
				sounds.get(name).loop(overallVolume * effectsVolume, pitch, pan);
			}
		}
	}
	
	/*
	 * Adds a sound to the sound map based on the key and filename
	 * 
	 * @param name                  The key for the sound that will be used to access it later
	 * @param s                     The name of file in "bin/soundEffects".  Must be an .mp3 file
	 */
	public static void addSound(String name, String s)
	{
		sounds.put(name, loadSound(s));
	}
	
	/*
	 * Creates a sound effect from the filename and adds it to the screams map
	 * 
	 * @param name                  The key for the sound that will be used to access it later
	 */
	public static void addScream(FileHandle f)
	{
		String name = f.nameWithoutExtension();
		SoundEffect se = new SoundEffect(f);
		screams.add(name);
		sounds.put(name, se);
	}
	
	/*
	 * Sets the different volume categories
	 * 
	 * @param overall				The new master volume level
	 * @param music					The new music volume level
	 * @param effect				The new effects volume level
	 */
	public static void setVolumes(float overall, float music, float effect)
	{
		setOverallVolume(overall);
		setMusicVolume(music);
		setEffectsVolume(effect);
	}
	
	/*
	 * Sets the master volume to volume if it's between 0 and 1
	 * Then, adjusts the volumes of music and sound effects
	 * 
	 * @param volume                The desired overall volume level (0 - 1)
	 */
	private static void setOverallVolume(float volume)
	{
		overallVolume = Math.max(volume, 0);
		overallVolume = Math.min(volume, 1);
		
		if(curSong != null)
			curSong.setVolume(overallVolume * musicVolume);
		
		ObjectMap.Entries<String, SoundEffect> s = sounds.entries();
		
		while(s.hasNext)
		{
			s.next().value.sVolume(overallVolume * effectsVolume);
		}
		
	}
	
	/*
	 * Adjusts the volume of music
	 * 
	 * @param volume                The desired music volume level (0 - 1).  Acts as a percentage of overall volume
	 */
	private static void setMusicVolume(float volume)
	{
		musicVolume = Math.max(volume, 0);
		musicVolume = Math.min(volume, 1);
		
		if(curSong != null)
			curSong.setVolume(overallVolume * musicVolume);
	}
	
	/*
	 * Adjusts the volume of sound effects
	 * 
	 * @param volume                The desired effect volume (0 - 1).  Acts as a percentage of the overall volume
	 */
	private static void setEffectsVolume(float volume)
	{
		effectsVolume = Math.max(volume, 0);
		effectsVolume = Math.min(volume, 1);
		
		ObjectMap.Entries<String, SoundEffect> s = sounds.entries();
		while(s.hasNext)
		{
			s.next().value.sVolume(overallVolume * effectsVolume);
		}
	}
	
	/*
	 * Disposes of all the music and sound effects when the game is over
	 */
	public static void endGame()
	{
		//Stop the music
		if(curSong != null)
		{
			curSong.stop();
			curSong.dispose();
		}
		
		//Dispose of all Sound Effects
		ObjectMap.Entries<String, SoundEffect> s = sounds.entries();
		
		while(s.hasNext)
		{
			SoundEffect se = s.next().value;
			se.stop();
			se.dispose();
		}
		
		screams = new Array<String>();
	}
	
	/*
	 * Stops all playing sounds but doesn't delete anything
	 */
	public static void endSounds()
	{
		if(curSong != null)
			curSong.stop();
		ObjectMap.Entries<String, SoundEffect> s = sounds.entries();
		while(s.hasNext)
		{
			s.next().value.stop();
		}
	}
	
	/*
	 * Pauses and un-pauses sounds
	 * 
	 * @param b						Whether to pause (true) or un-pause (false) all the sounds
	 */
	public static void pause(boolean b)
	{
		if(b)
		{
			if(curSong != null)
			{
				curSong.pause();
				wasPlaying = true;
			}
			
			ObjectMap.Entries<String, SoundEffect> s = sounds.entries();
			while(s.hasNext)
			{
				String key = s.next().key;
				
				if(sounds.get(key).isPlaying())
				{
					sounds.get(key).pause(true);
				}
			}
		}
		
		else
		{
			if(curSong != null)
			{
				if(wasPlaying)
				{
					curSong.play();
					wasPlaying = false;
				}
			}
			
			ObjectMap.Entries<String, SoundEffect> s = sounds.entries();
			while(s.hasNext)
			{
				String key = s.next().key;
				
				if(sounds.get(key).isPaused())
				{
					sounds.get(key).pause(false);
				}
				
			}
		}
		
	}
	/*
	 * Loads a sound effect.  First checks for MP3.  If that fails, WAV and if that fails, OGG.
	 * 
	 * @param s						The name of the file in /bin/soundEffects
	 * @return						A SoundEffect object made from the file
	 */
	private static SoundEffect loadSound(String s)
	{
		FileHandle f = null;
		
		if(ChaseApp.platform == Platform.DESKTOP)
		{
			f = Gdx.files.local("bin/soundEffects/" + s + ".mp3");
		
			if(!f.exists())
			{
				f = Gdx.files.local("bin/soundEffects/" + s + ".wav");
				
				if(!f.exists())
				{
					f = Gdx.files.local("bin/soundEffects/" + s + ".ogg");
					
					if(!f.exists())
						f = null;
				}
			}
		}
		
		else
		{
			f = Gdx.files.internal("soundEffects/" + s + ".mp3");
			
			if(!f.exists())
			{
				f = Gdx.files.internal("soundEffects/" + s + ".wav");
				
				if(!f.exists())
				{
					f = Gdx.files.internal("soundEffects/" + s + ".ogg");
					
					if(!f.exists())
					{
						f = null;
					}
				}
			}
		}
		SoundEffect se = new SoundEffect(f);
		se.play(0);
		se.stop();
		se.sVolume(effectsVolume * overallVolume);
		return se;
	}
	
	/*
	 * Loads a song.  First checks for MP3.  If that fails, WAV and if that fails, OGG.
	 * 
	 * @param s						The name of the file in /bin/soundEffects
	 * @return						a Music object made from the file
	 */
	private static Music loadSong(String s)
	{
		FileHandle f = null;
	
		if(ChaseApp.platform == Platform.DESKTOP)
		{
			
			f = Gdx.files.internal("bin/music/" + s + ".mp3");
			
			if(!f.exists())
			{
				f = Gdx.files.local("bin/music/" + s + ".wav");
				
				if(!f.exists())
				{
					f = Gdx.files.local("bin/music/" + s + ".ogg");
					
					if(!f.exists())
						f = null;
				}
			}
		}
		
		else
		{
			f = Gdx.files.internal("music/" + s + ".mp3");
			
			if(!f.exists())
			{
				f = Gdx.files.internal("music/" + s + ".wav");
				
				if(!f.exists())
				{
					f = Gdx.files.internal("music/" + s + ".ogg");
					
					if(!f.exists())
						f = null;
				}
			}
		}
		
		return Gdx.audio.newMusic(f);
	}
	
	/*
	 * Plays a random scream sound effect
	 */
	public static void playRandomScream()
	{
		int i = (int)Math.floor(Math.random() * screams.size);
		
		sounds.get(screams.get(i)).play(effectsVolume * overallVolume);
	}
}// End Class