/*
 * @(#)CGCStats.java		0.2 14/2/26
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.net;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.percipient24.cgc.ChaseApp;

/*
 * Holds stored information about statistics for this game
 * 
 * @version 0.2 14/2/26
 * @author Christopher Rider
 */
public class CGCStats
{
	public Array<CGCStatGame> allGames;
	
	private int curGame = 0;
	
	/*
	 * Creates a new CGCStats object
	 */
	public CGCStats()
	{
		allGames = new Array<CGCStatGame>();
	}
	
	/*
	 * Converts this object to a Json object
	 * 
	 * @return						The Json object as a string
	 */
	public String toJson()
	{
		Json j = new Json();
		j.setOutputType(OutputType.json);
		j.setUsePrototypes(false);
		j.setElementType(CGCStats.class, "allGames", CGCStatGame.class);
		return j.toJson(this);
	}

	/*
	 * Adds a new game array to the stats object
	 */
	public void startGame()
	{
		allGames.add(new CGCStatGame());
	}
	
	/*
	 * Adds a new CGCStat object for a map
	 * 
	 * @param id					The ID of the map to add
	 */
	public void addMapToGame(int id)
	{
		allGames.get(curGame).addMapToGame(id);
	}
	
	/*
	 * Adds this game to the stored stats list
	 */
	public void finishGame()
	{
		allGames.get(curGame).finishGame();

		saveCurrentStats();
	}
	
	/*
	 * Gets the stats for the specified map in the current game
	 * 
	 * @param index					The specified map index
	 * @return						The chosen stat object
	 */
	public CGCStat getStatByIndex(int index)
	{
		return allGames.get(curGame).getStatByIndex(index);
	}
	
	/*
	 * Gets the current game's stat object
	 * 
	 * @return						The current game's stat object
	 */
	public CGCStatGame getGame()
	{
		return allGames.get(curGame);
	}

	/*
	 * Loads saved stats from stats.bin
	 */
	public void loadPrevStats()
	{
		String loadedData = ChaseApp.fileHandler.readFile("stats.bin");

		if (!loadedData.equals(""))
		{
			Json json = new Json();
			CGCStats newStats = json.fromJson(CGCStats.class, loadedData);
			
			for (int i = 0; i < newStats.allGames.size; i++)
			{
				allGames.add(new CGCStatGame(newStats.allGames.get(i)));
			}
		}
		
		curGame = allGames.size;
	}

	/*
	 * Removes all saved stat data
	 */
	public void resetStats()
	{
		allGames.clear();
		saveCurrentStats();
	}

	/*
	 * Saves this game's stats to stats.bin
	 */
	public void saveCurrentStats()
	{
		ChaseApp.fileHandler.writeFile("stats.bin", toJson());
		curGame = allGames.size;
	}
} // End class