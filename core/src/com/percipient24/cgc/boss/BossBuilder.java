/*
 * @(#)BossBuilder.java		0.2 14/2/27
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.boss;

import com.percipient24.cgc.entities.boss.Boss;
import com.percipient24.enums.BossType;

/*
 * Creates and stores a boss level
 * 
 * @version 0.2 14/2/27
 * @author Christopher Rider
 */
public abstract class BossBuilder 
{
	protected BossType bossType;
	protected int levelLength;
	
	/*
	 * Creates a BossBuilder object
	 * 
	 * @param type					The type of boss to build for
	 */
	public BossBuilder(BossType type)
	{
		bossType = type;
	}
	
	/*
	 * Gets the length of the boss level, in chunks
	 * 
	 * @return						The length of the boss level
	 */
	public int getLevelLength()
	{
		return levelLength;
	}
	
	/*
	 * Builds the boss world
	 */
	protected abstract void buildBossArea();
	
	/*
	 * Builds a boss
	 * 
	 * @return						The created boss
	 */
	public abstract Boss createBoss();
	
	/*
	 * Calculates the game image for a terrain section based on neighboring tiles
	 * 
	 * @param one					The first tile to check with
	 * @param two					The second tile to check with
	 * @param three					The third tile to check with
	 * @return						The corner type to use (as if it were from the map-editor)
	 */
	protected int calcNewImage(boolean one, boolean two, boolean three)
	{
		if (one && two && three)
			return 7;
		else if ((one && two && !three) || (one && !two && !three))
			return 4;
		else if (one && !two && three)
			return 5;
		else if ((!one && two && three) || (!one && !two && three))
			return 1;
		else 
			return 0;
	}
} // End class