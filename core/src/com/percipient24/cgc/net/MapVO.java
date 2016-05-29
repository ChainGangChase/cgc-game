/*
 * @(#)MapVO.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.net;

/*
 * Contains the meta-data for a map
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 */
public class MapVO
{
	public int mid;
	public String mname;
	public String uname;
	public float mrating;
	public int msuggested; // Only used to parse into min/max on download
	public int msize;
	public String mdata;
	public int minPlayers;
	public int maxPlayers;
	
	/*
	 * Creates an empty MapVO object
	 */
	public MapVO()
	{
	}
	
	/*
	 * Creates a deep copy MapVO object
	 * 
	 * @param other					The MapVO to copy from
	 */
	public MapVO(MapVO other)
	{
		this.mid = other.mid;
		this.mname = other.mname;
		this.uname = other.uname;
		this.mrating = other.mrating;
		this.msuggested = other.msuggested;
		this.msize = other.msize;
		this.mdata = other.mdata;
		this.minPlayers = other.minPlayers;
		this.maxPlayers = other.maxPlayers;
	}
} // End class