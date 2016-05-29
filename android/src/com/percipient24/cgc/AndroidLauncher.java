/*
 * @(#)MainActivity.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc;

import java.io.InputStream;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.percipient24.enums.Platform;

/*
 * Main method for an Android CGC app
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 * @author Christopher Rider
 */
public class MainActivity extends AndroidApplication 
{	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        //OpenGL ES version set in AndroidManifest.xml

        initialize(new ChaseApp(Platform.OUYA), cfg);
    }
    
    public void exit()
    {
    	handler.post(new Runnable() 
    	{
			public void run () 
			{
				SoundManager.endGame();
				ChaseApp.stats.saveCurrentStats();
				OuyaFacade.getInstance().shutdown();
				finish();
			}
		});
    }
} // End class