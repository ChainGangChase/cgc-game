/*
 * @(#)CGCStatMaps.java		0.2 14/3/11
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.net;

import com.badlogic.gdx.utils.Array;

/*
 * Holds maps-stats for all maps in a game
 * 
 * @version 0.2 14/3/11
 * @author Christopher Rider
 */
public class CGCStatMaps
{
	public Array<CGCStat> maps;
	
	/*
	 * Creates a new CGCStatMaps object
	 */
	public CGCStatMaps()
	{
		maps = new Array<CGCStat>();
	}
	
	/*
	 * Creates a deep copy CGCStatGame object
	 * 
	 * @param other					The CGCStatGame to copy from
	 */
	public CGCStatMaps(CGCStatMaps other)
	{
		maps = new Array<CGCStat>();
		
		for (int i = 0; i < other.maps.size; i++)
		{
			maps.add(new CGCStat(other.maps.get(i)));
		}
	}
	
	/*
	 * Adds a new CGCStat object for a map
	 * 
	 * @param id					The ID of the map to add
	 */
	public void addMapToGame(int id)
	{
		CGCStat tempStat = new CGCStat();
		
		tempStat.mapID = id;
		
		maps.add(tempStat);
	}
	
	/*
	 * Gets the stats for the specified map
	 * 
	 * @param index					The specified map index
	 * @return						The chosen stat object
	 */
	public CGCStat getStatByIndex(int index)
	{
		return maps.get(index);
	}
} // End class