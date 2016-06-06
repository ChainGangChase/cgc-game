/*
 * @(#)MapHelper.java		0.1 16/28/5
 * 
 * Copyright 2016, MAGIC Spell Studios, LLC
 */
package com.percipient24.helpers;

/*
 * Static helper methods for computing Map attributes.
 * 
 * @version 0.1 16/28/5
 * @author Joe Pietruch
 */
public class MapHelper 
{
	public static int[] getMapMinMax(int msuggested)
	{
		int[] minMax = new int[2];
		
		if (msuggested == 0)
		{
			minMax[0] = 0;
			minMax[1] = 8;
		}
		else if (msuggested < 10)
		{
			minMax[0] = msuggested;
			minMax[1] = msuggested;
		}
		else if (msuggested%10 == 0)
		{
			minMax[0] = msuggested/10;
			minMax[1] = 8;
		}
		else if (msuggested%10 == 9)
		{
			minMax[0] = 0;
			minMax[1] = msuggested/10;
		}
		else
		{
			minMax[0] = msuggested / 10;
			minMax[1] = msuggested % 10;
		}
		
		return minMax;
	}
}