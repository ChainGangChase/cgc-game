 /*
 * @(#)Main.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc;


import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.SwingUtilities;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.percipient24.enums.Platform;

/*
 * Main method for a desktop CGC app
 *
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 * @author William Ziegler
 */
@SuppressWarnings("unused")
public class Main
{
	public static void main(String[] args)
	{
		//Try to load the resolutionPreferences file
		try {

			FileReader fr = new FileReader("resolutionPreferences.bin");
			BufferedReader br = new BufferedReader(fr);

			//If it exists, check the first line to see if the launcher should be shown
			boolean showLauncher = Boolean.parseBoolean(br.readLine());

			if(showLauncher)
			{
				br.close();
				Launcher l = new Launcher();
		        // Schedules the application to be run at the correct time in the event queue.
		        SwingUtilities.invokeLater(l);
			}
			else
			{
				//If the launcher isn't shown, start the game with the settings given in the file
				LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
				cfg.title = "Chain Gang Chase";
				cfg.useGL30 = false;

				cfg.width = Integer.parseInt(br.readLine());
				cfg.height = Integer.parseInt(br.readLine());

				cfg.fullscreen = Boolean.parseBoolean(br.readLine());
				br.close();

				new LwjglApplication(new ChaseApp(Platform.DESKTOP), cfg)
				{
					public void exit()
					{
						SoundManager.endGame();
						TimerManager.clear();
						ChaseApp.stats.saveCurrentStats();

						postRunnable(new Runnable()
						{
							public void run()
							{
								running = false;
							}
						});
					}
				};
			}

		}
		//If something goes wrong (file doesn't exist, bad value), just launch the launcher
		catch (Exception e) {
			Launcher l = new Launcher();
	        // Schedules the application to be run at the correct time in the event queue.
	        SwingUtilities.invokeLater(l);
		}
	}
} // End class