/*
 * @(#)MapVOComparator.java		0.1 14/1/30
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.maps;

import java.util.Comparator;

import com.percipient24.cgc.net.MapVO;
import com.percipient24.enums.SortType;

/*
 * Contains the logic for comparing MapVOs
 * 
 * @version 0.1 14/1/30
 * @author Christopher Rider
 */
public class MapVOComparator implements Comparator<MapVO>
{
	private boolean ascending = false;
	private SortType primarySortingType;
	private SortType secondarySortingType;
	
	/*
	 * Creates a new MapVOComparator
	 * 
	 * @param isAscending			Whether or not the maps should be compared up or down
	 * @param primarySortType		How the maps should be sorted strongly
	 * @param secondarySortType		How the maps should be sorted weakly
	 */
	public MapVOComparator(boolean isAscending, SortType primarySortType, SortType secondarySortType)
	{
		ascending = isAscending;
		primarySortingType = primarySortType;
		secondarySortingType = secondarySortType;
	}
	
	/*
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(MapVO mapA, MapVO mapB)
	{
		int order = 0;
		
		switch(primarySortingType)
		{
			case SORT_BY_MAP: order = mapA.mname.compareToIgnoreCase(mapB.mname);
				break;
			case SORT_BY_CREATOR: order = mapA.uname.compareToIgnoreCase(mapB.uname);
				break;
			case SORT_BY_SIZE:
				if (mapA.msize == mapB.msize)
				{
					order = 0;
				}
				else if (mapA.msize < mapB.msize)
				{
					order = -1;
				}
				else
				{
					order = 1;
				}
				break;
			case SORT_BY_ID: 
				if (mapA.mid < mapB.mid)
				{
					order = -1;
				}
				else if (mapA.mid > mapB.mid)
				{
					order = 1;
				}
				else
				{
					order = 0;
				}
				break;
			case SORT_BY_RATING:
				if (mapA.mrating < mapB.mrating)
				{
					order = 1;
				}
				else if (mapA.mrating > mapB.mrating)
				{
					order = -1;
				}
				else
				{
					order = 0;
				}
				break;
			default: break;
		}
		
		if (order == 0)
		{
			if (primarySortingType == secondarySortingType || 
					secondarySortingType == SortType.NONE)
			{
				return order;
			}
			switch (secondarySortingType)
			{
				case SORT_BY_MAP: order = mapA.mname.compareToIgnoreCase(mapB.mname);
					break;
				case SORT_BY_CREATOR: order = mapA.uname.compareToIgnoreCase(mapB.uname);
					break;
				case SORT_BY_SIZE:
					if (mapA.msize == mapB.msize)
					{
						order = 0;
					}
					else if (mapA.msize < mapB.msize)
					{
						order = -1;
					}
					else
					{
						order = 1;
					}
				case SORT_BY_RATING:
					if (mapA.mrating < mapB.mrating)
					{
						order = 1;
					}
					else if (mapA.mrating > mapB.mrating)
					{
						order = -1;
					}
					else
					{
						order = 0;
					}
					break;
				default: break;
			}
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