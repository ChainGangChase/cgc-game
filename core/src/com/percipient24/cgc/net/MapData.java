/*
 * @(#)MapData.java		0.2 14/3/3
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.enums.Platform;

/*
 * Handles client connection and data transfer to/from the server
 * 
 * @version 0.2 14/3/3
 * @author Christopher Rider
 */
public class MapData
{
	private static MapsVO maps;
	private static boolean loaded = false;
	
	/*
	 * Creates a new MapData object
	 */
	public MapData()
	{
	}
	
	/*
	 * Gets the map list stored here
	 * 
	 * @return		The map list
	 */
	public static MapsVO getMaps()
	{
		return maps;
	}
	
	/*
	 * Translates the map data into a usable form
	 * 
	 * @param mdata			The string containing all of the maps
	 */
	public static void load(String mdata)
	{
		if (!loaded) {
			try  
		    { 
		      mdata = "{\""+mdata.substring(mdata.indexOf("class")); 
		      mdata = mdata.replace("\t", ""); 
		       
		      Json json = new Json(); 
		      maps = json.fromJson(MapsVO.class, mdata); 
		      loaded = true;
		    } 
		    catch (Exception e) 
		    { 
		      Gdx.app.log("MapsResponder", "An error occurred with json"); 
		      Gdx.app.log("Error", e.getMessage()); 
		      maps = null; 
		    } 
		}
	}
} // End class