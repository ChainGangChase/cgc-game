/*
 * @(#)MenuTextureRegionComparator.java		0.3 14/5/15
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens.helpers;

import java.util.Comparator;

/*
 * Contains the logic for comparing MenuTextureRegions
 * 
 * @version 0.3 14/5/15
 * @author William Ziegler
 */
public class MenuTextureRegionComparator implements Comparator<MenuTextureRegion>
{
	private boolean ascending = false;
	
	/*
	 * Creates a new MenuTextureRegionComparator
	 * 
	 * @param isAscending			Whether or not the MenuTextureRegions should be compared up or down
	 */
	public MenuTextureRegionComparator(boolean isAscending)
	{
		ascending = isAscending;
	}
	
	/*
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(MenuTextureRegion regionA, MenuTextureRegion regionB)
	{
		int order = 0;
		
		if (regionA.getRegionHeight() == regionB.getRegionHeight())
		{
			order = 0;
		}
		else if (regionA.getRegionHeight() < regionB.getRegionHeight())
		{
			order = -1;
		}
		else
		{
			order = 1;
		}
		
		if (ascending)
		{
			if (order < 0)
			{
				order = -1;
			}
			else if (order > 0)
			{
				order = 1;
			}
		}
		else
		{
			if (order < 0)
			{
				order = 1;
			}
			else if (order > 0)
			{
				order = -1;
			}
		}
		return order;
	}
} // End class