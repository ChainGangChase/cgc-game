/*
 * @(#)MapVO.java		0.1 14/1/30
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.maps;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.percipient24.cgc.net.MapVO;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.enums.SortType;

/*
 * Contains the logic for sorting/filtering the map list
 * 
 * @version 0.1 14/1/30
 * @author Christopher Rider
 */
public class MapSorter
{
	private ArrayList<MapVO> maps;
	
	/*
	 * Creates a new map sorter from a map list
	 * 
	 * @param theMaps				The map list this sorter will sort
	 * @param downloading			Whether or not this sorter is running while downloading maps
	 */
	public MapSorter(ArrayList<MapVO> theMaps, boolean downloading)
	{
		maps = deepCopy(theMaps);
		if(!downloading)
		{
			filterMapsByPlayers();
		}
	}
	
	/*
	 * Gets the map sorter's sorted map list
	 * 
	 * @return						The sorter's map list
	 */
	public ArrayList<MapVO> getMaps()
	{
		return maps;
	}
	
	/*
	 * Removes any maps from the map list that cannot be played by the current number of players
	 */
	private void filterMapsByPlayers()
	{
		for (int i = 0; i < maps.size(); i++)
		{
			if (!canHandleNumPlayers(i, ChaseApp.characterSelect.getActivePlayers()))
			{
				maps.remove(i);
				i--;
			}
		}
	}
	
	/*
	 * Removes any maps from the map list that cannot be played by the current number of players
	 */
	public void filterMapsByPlayersSecondary()
	{
		for (int i = 0; i < maps.size(); i++)
		{
			if (!canHandleNumPlayersSecondary(i, ChaseApp.characterSelect.getNumPlayers()))
			{
				maps.remove(i);
				i--;
			}
		}
	}
	
	/*
	 * Removes any maps from this list that aren't favorited by the user
	 */
	public void filterMapsByFavorites()
	{
		for (int i = 0; i < maps.size(); i++)
		{
			if (!isFavorited(i))
			{
				maps.remove(i);
				i--;
			}
		}
	}
	
	/*
	 * Checks if the map is favorited or not
	 * 
	 * @param index					The map index to check
	 * @return						Whether or not this map is favorited
	 */
	private boolean isFavorited(int index)
	{
		for (int i = 0; i < ChaseApp.favorite.gFavorites().size; i++)
		{
			if (maps.get(index).mid == ChaseApp.favorite.gFavorites().get(i))
			{
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Removes any maps from the map list that are not within the specified sizes
	 * 
	 * @param minSize				The smallest map the player wants to play
	 * @param maxSize				The largest map the player wants to play
	 */
	public void filterMapsBySize(int minSize, int maxSize)
	{
		for (int i = 0; i < maps.size(); i++)
		{
			if (withinSizeLimit(i, minSize, maxSize))
			{
				maps.remove(i);
				i--;
			}
		}
	}
	
	/*
	 * Removes any maps from the map list that does not contain a keyword in the creator's name
	 * 
	 * @param keyword				The keyword to filter by
	 */
	public void filterMapsByCreator(String keyword)
	{
		for (int i = 0; i < maps.size(); i++)
		{
			if (createdBy(i, keyword))
			{
				maps.remove(i);
				i--;
			}
		}
	}
	
	/*
	 * Removes any maps from the map list that does not contain a keyword in the map's name
	 * 
	 * @param keyword				The keyword to filter by
	 */
	public void filterMapsByName(String keyword)
	{
		for (int i = 0; i < maps.size(); i++)
		{
			if (hasMapName(i, keyword))
			{
				maps.remove(i);
				i--;
			}
		}
	}
	
	/*
	 * Removes any maps from the map list that is not within the specified rating values
	 * 
	 * @param min					The minimum rating to filter by
	 * @param max					The maximum rating to filter by
	 */
	public void filterByRating(float min, float max)
	{
		for (int i = 0; i < maps.size(); i++)
		{
			if (maps.get(i).mrating < min || maps.get(i).mrating > max)
			{
				maps.remove(i);
				i--;
			}
		}
	}
	
	/*
	 * Removes any maps from the map list that is not the specified map ID
	 * 
	 * @param id					The map to find
	 */
	public void filterByID(int id)
	{
		for (int i = 0; i < maps.size(); i++)
		{
			if (maps.get(i).mid == id)
			{
				MapVO temp = maps.get(i);
				maps.clear();
				maps.add(temp);
				return;
			}
		}
	}
	
	/*
	 *  Determines maps from the list that cannot be played by the current number of players
	 *  
	 *  @param index				The current map index
	 *  @param players				The player meta-data from this map
	 *  @return						Whether or not this map should be removed from the list
	 */
	private boolean canHandleNumPlayers(int index, int players)
	{
		if (players >= maps.get(index).minPlayers && 
				players <= maps.get(index).maxPlayers)
		{
			return true;
		}
		return false;
	}
	
	/*
	 *  Determines maps from the list that cannot be played by any number of players
	 *  
	 *  @param index				The current map index
	 *  @param players				How many players there are currently
	 *  @return						Whether or not this map should be removed from the list
	 */
	private boolean canHandleNumPlayersSecondary(int index, int players)
	{
		if (players >= maps.get(index).minPlayers && 
				players <= maps.get(index).maxPlayers)
		{
			if (maps.get(index).minPlayers == 0 && maps.get(index).maxPlayers == 8)
			{
				return true;
			}
		}
		return false;
	}
	
	/*
	 *  Determines maps from the list that are not between min/max map size
	 *  
	 *  @param index				The current map index
	 *  @param minSize				The minimum requested size of the map (in chunks)
	 *  @param maxSize				The maximum requested size of the map (in chunks)
	 *  @return						Whether or not this map should be removed from the list
	 */
	private boolean withinSizeLimit(int index, int minSize, int maxSize)
	{
		if (maps.get(index).msize > maxSize || maps.get(index).msize < minSize)
		{
			return true;
		}
		return false;
	}
	
	/*
	 *  Determines maps from the list that do not contain a keyword in the creator's name
	 *  
	 *  @param index				The current map index
	 *  @param keyword				The string to search the creator's name by
	 *  @return						Whether or not this map should be removed from the list
	 */
	private boolean createdBy(int index, String keyword)
	{
		if (keyword.equals(""))
		{
			return true;
		}
		
		if (maps.get(index).uname.contains(keyword))
		{
			return false;
		}
		return true;
	}
	
	/*
	 * Determines maps from the list that do not contain a keyword in the map's name
	 * 
	 * @param index					The current map index
	 * @param keyword				The string to search the map's name by
	 * @return						Whether or not this map should be removed from the list
	 */
	private boolean hasMapName(int index, String keyword)
	{
		if (maps.get(index).mname.contains(keyword))
		{
			return false;
		}
		return true;
	}
	
	/*
	 * Sorts the map list by the specified value
	 * 
	 * @param ascending				Whether maps should be listed ascending or descending
	 * @param primarySortType		What the map list should be sorted by first
	 * @param secondarySortType		What the map list should be sub-sorted by
	 */
	public void sortMaps(boolean ascending, SortType primarySortType, SortType secondarySortType)
	{
		if (primarySortType == SortType.FILTER_BY_FAVORITE)
		{
			filterMapsByFavorites();
			sortMapList(ascending, secondarySortType, SortType.SORT_BY_ID);
		}
		else if (primarySortType == SortType.FILTER_BY_SELF)
		{
			filterMapsByCreator(ChaseApp.favorite.getPlayerAccount());
			sortMapList(ascending, SortType.SORT_BY_CREATOR, secondarySortType);
		}
		else
		{
			sortMapList(ascending, primarySortType, secondarySortType);
		}
	}
	
	/*
	 * Helper method that actually does the sorting
	 * 
	 * @param ascending				Whether maps should be listed ascending or descending
	 * @param primary				What the map list should be sorted by first
	 * @param secondary				What the map list should be sub-sorted by
	 */
	private void sortMapList(boolean ascending, SortType primary, SortType secondary)
	{
		Gdx.app.log("MapList", "Not Slytherin... Not Slytherin...");
		MapVO[] mapArray = new MapVO[maps.size()];
		maps.toArray(mapArray);
		Arrays.sort(mapArray, new MapVOComparator(ascending, primary, secondary));
		maps = new ArrayList<MapVO>(Arrays.asList(mapArray));
		Gdx.app.log("Sorter", "GRYFFINDOR!!");
	}
	
	/*
	 * Creates a deep copy of an ArrayList<MapVO>
	 * 
	 * @param oldList				The list to copy
	 * @return						The copied list
	 */
	public ArrayList<MapVO> deepCopy(ArrayList<MapVO> oldList)
	{
		ArrayList<MapVO> newList = new ArrayList<MapVO>();
		
		for (int i = 0; i < oldList.size(); i++)
		{
			newList.add(new MapVO(oldList.get(i)));
		}
		
		return newList;
	}
} // End class