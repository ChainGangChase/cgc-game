/*
 * @(#)CGCStat.java		0.2 14/3/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.net;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/*
 * Holds information about statistics for a map in a game
 * 
 * @version 0.2 14/3/4
 * @author Christopher Rider
 */
public class CGCStat implements Json.Serializable
{
	public int punchesLandedPrisoner = 0;
	public int punchesLandedCop = 0;
	public int trainKillsPrisoner = 0;
	public int trainKillsCop = 0;
	public int trainsDerailed = 0;
	public int mapID = 0;
	public boolean beaten = false;
	public int captures = 0;
	public long mapCompletionTime = 0; // 0 if not completed
	public int startingPrisoners = 0;
	public int survivingPrisoners = 0;

	/*
	 * Creates a new CGCStat object
	 */
	public CGCStat()
	{
		
	}

	/*
	 * Creates a deep copy CGCStat object
	 * 
	 * @param other					The CGCStat to copy from
	 */
	public CGCStat(CGCStat other)
	{
		this.punchesLandedPrisoner = other.punchesLandedPrisoner;
		this.punchesLandedCop = other.punchesLandedCop;
		this.trainKillsPrisoner = other.trainKillsPrisoner;
		this.trainKillsCop = other.trainKillsCop;
		this.trainsDerailed = other.trainsDerailed;
		this.mapID = other.mapID;
		this.beaten = other.beaten;
		this.captures = other.captures;
		this.mapCompletionTime = other.mapCompletionTime;
		this.startingPrisoners = other.startingPrisoners;
		this.survivingPrisoners = other.survivingPrisoners;
	}
	
	/*
	 * Writes this object as a json object
	 * 
	 * @param json					The json object to write to
	 */
	public void write(Json json) 
	{
		json.writeValue("beaten", beaten);
		json.writeValue("captures", captures);
		json.writeValue("mapId", mapID);
		json.writeValue("mapTime", mapCompletionTime);
		json.writeValue("punchesLandedPrisoner", punchesLandedPrisoner);
		json.writeValue("punchesLandedCop", punchesLandedCop);
		json.writeValue("trainKillsPrisoner", trainKillsPrisoner);
		json.writeValue("trainKillsCop", trainKillsCop);
		json.writeValue("trainsDerailed", trainsDerailed);
		json.writeValue("startingPrisoners", startingPrisoners);
		json.writeValue("survivingPrisoners", survivingPrisoners);
	}

	/*
	 * Reads this object as a json object
	 * 
	 * @param json					The json object to read from
	 * @param jsonData				The data from the json object
	 */
	public void read(Json json, JsonValue jsonData) 
	{
		punchesLandedPrisoner = jsonData.getInt("punchesLandedPrisoner");
		punchesLandedCop = jsonData.getInt("punchesLandedCop");
		trainKillsPrisoner = jsonData.getInt("trainKillsPrisoner");
		trainKillsCop = jsonData.getInt("trainKillsCop");
		beaten = jsonData.getBoolean("beaten");
		captures = jsonData.getInt("captures");
		mapID = jsonData.getInt("mapId");
		mapCompletionTime = jsonData.getLong("mapTime");
		trainsDerailed = jsonData.getInt("trainsDerailed");
		startingPrisoners = jsonData.getInt("startingPrisoners");
		survivingPrisoners = jsonData.getInt("survivingPrisoners");
	}
} // End class