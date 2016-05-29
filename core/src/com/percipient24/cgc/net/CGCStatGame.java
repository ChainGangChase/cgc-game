/*
 * @(#)CGCStatGame.java		0.2 14/3/6
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.net;

import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.players.RookieCop;
import com.percipient24.enums.BossType;

/*
 * Holds information about statistics for a game
 * 
 * @version 0.2 14/3/6
 * @author Christopher Rider
 */
public class CGCStatGame
{
	public BossType bossType = BossType.NONE;
	public int bossKills = 0;
	public long gameTime = 0;
	public long timeAsPrisoner = 0;
	public long timeAsCop = 0;
	public boolean won = false;
	public long timestamp = 0;
	public int treesPunched = 0;
	public int punchesAttempted = 0;
	public int numPlayers = 0;
	public long distAsPrisoner = 0;
	public long distAsCop = 0;
	public int bossFightPunchesLandedCop = 0;
	public int bossFightPunchesLandedPrisoner = 0;
	public CGCStatMaps allMaps;
	
	/*
	 * Creates a new CGCStatGame object
	 */
	public CGCStatGame()
	{
		allMaps = new CGCStatMaps();
	}
	
	/*
	 * Creates a deep copy CGCStatGame object
	 * 
	 * @param other					The CGCStatGame to copy from
	 */
	public CGCStatGame(CGCStatGame other)
	{
		allMaps = new CGCStatMaps(other.allMaps);
		this.bossType = other.bossType;
		this.bossKills = other.bossKills;
		this.gameTime = other.gameTime;
		this.timeAsPrisoner = other.timeAsPrisoner;
		this.timeAsCop = other.timeAsCop;
		this.won = other.won;
		this.timestamp = other.timestamp;
		this.treesPunched = other.treesPunched;
		this.punchesAttempted = other.punchesAttempted;
		this.numPlayers = other.numPlayers;
		this.distAsCop = other.distAsCop;
		this.distAsPrisoner = other.distAsPrisoner;
		this.bossFightPunchesLandedCop = other.bossFightPunchesLandedCop;
		this.bossFightPunchesLandedPrisoner = other.bossFightPunchesLandedPrisoner;
	}
	
	/*
	 * Adds a new CGCStat object for a map
	 * 
	 * @param id					The ID of the map to add
	 */
	public void addMapToGame(int id)
	{
		allMaps.addMapToGame(id);
	}
	
	/*
	 * Gets the stats for the specified map
	 * 
	 * @param id					The specified map index
	 * @return						The chosen stat object
	 */
	public CGCStat getStatByIndex(int index)
	{
		return allMaps.getStatByIndex(index);
	}
	
	/*
	 * Updates the time for this game
	 */
	public void finishGame()
	{
		timestamp = System.currentTimeMillis()/1000;
		
		timeAsPrisoner /= 1000;
		timeAsCop /= 1000;
		distAsCop = (long) RookieCop.getTotalDistRun();
		distAsPrisoner = (long) Prisoner.getTotalDistRun();
	}
} // End class