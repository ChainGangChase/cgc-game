/*
 * @(#)PlayerCard.java		0.3 14/4/3
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens.helpers;

import com.percipient24.input.ControlAdapter;

/*
 * Stores information about player character selections
 * 
 * @version 0.3 14/4/3
 * @author William Ziegler
 */
public class PlayerCard
{
	private int convictSelection;
	private int copSelection;
	private ControlAdapter adapter;
	private boolean lockedInConvict;
	private boolean lockedInCop;
	
	/*
	 * Creates a new PlayerCard object
	 * 
	 * @param selection				The convict selection to assign to this PlayerCard
	 * @param adapter				The ControlAdapter which is controlling this PlayerCard
	 */
	public PlayerCard(int selection, ControlAdapter adapter)
	{
		this.convictSelection = selection;
		this.copSelection = -1;
		this.adapter = adapter;
		lockedInConvict = false;
		lockedInCop = false;
	}
	
	/*
	 * Get the convict selection this PlayerCard is using
	 * 
	 * @return						The convict selection for this PlayerCard
	 */
	public int getConvictSelection()
	{
		return convictSelection;
	}
	
	/*
	 * Get the cop selection this PlayerCard is using
	 * 
	 * @return						The cop selection for this PlayerCard
	 */
	public int getCopSelection()
	{
		return copSelection;
	}
	
	/*
	 * Get the ControlAdapter this PlayerCard is using
	 * 
	 * @return						The ControlAdapter for this PlayerCard
	 */
	public ControlAdapter getAdapter()
	{
		return adapter;
	}
	
	/*
	 * Get whether or not this PlayerCard has confirmed its convict selection
	 * 
	 * @return						Whether or not the PlayerCard has confirmed its selection
	 */
	public boolean getLockedInConvict()
	{
		return lockedInConvict;
	}
	
	/*
	 * Get whether or not this PlayerCard has confirmed its cop selection
	 * 
	 * @return						Whether or not the PlayerCard has confirmed its selection
	 */
	public boolean getLockedInCop()
	{
		return lockedInCop;
	}
	
	/*
	 * Set the convict selection this PlayerCard is using
	 * 
	 * @param selection				The new convict selection for this PlayerCard
	 */
	public void setConvictSelection(int selection)
	{
		this.convictSelection = selection;
	}
	
	/*
	 * Set the cop selection this PlayerCard is using
	 * 
	 * @param selection				The new cop selection for this PlayerCard
	 */
	public void setCopSelection(int selection)
	{
		this.copSelection = selection;
	}
	
	/*
	 * Change the character selection this PlayerCard is using by an amount
	 * 
	 * @param selectionAdjust		How far to adjust this PlayerCard's character selection
	 * @param isConvict				Whether the player is selecting convicts (true) or cops (false)
	 */
	public void adjustSelection(int selectionAdjust, boolean isConvict)
	{
		if (isConvict)
		{
			convictSelection += selectionAdjust;
		}
		else
		{
			copSelection += selectionAdjust;
		}
	}
	
	/*
	 * Set the ControlAdapter this PlayerCard is using
	 * 
	 * @param adapter				The new ControlAdapter for this PlayerCard
	 */
	public void setAdapter(ControlAdapter adapter)
	{
		this.adapter = adapter;
	}
	
	/*
	 * Set whether or not this PlayerCard has locked its convict selection
	 * 
	 * @param locked				The new convict selection lock status
	 */
	public void setLockedInConvict(boolean locked)
	{
		this.lockedInConvict = locked;
		if (!locked)
		{
			copSelection = -1;
		}
	}
	
	/*
	 * Set whether or not this PlayerCard has locked its cop selection
	 * 
	 * @param locked				The new cop selection lock status
	 */
	public void setLockedInCop(boolean locked)
	{
		this.lockedInCop = locked;
	}
	
	/*
	 * Determines whether or not this PlayerCard is being used by a player
	 * 
	 * @return						Whether or not this PlayerCard is being used
	 */
	public boolean isUsed()
	{
		return (convictSelection != -1 && adapter != null);
	}
	
}
