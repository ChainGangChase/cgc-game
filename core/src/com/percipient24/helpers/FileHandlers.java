/*
 * @(#)FileHandler.java		0.2 14/2/10
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.helpers;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.percipient24.cgc.ChaseApp;

/*
 * Handles file IO for CGC
 * 
 * @version 0.2 14/2/10
 * @author Christopher Rider
 */
public class FileHandlers 
{
	private ChaseApp myApp;
	
	/*
	 * Creates a new FileHandler object
	 * 
	 * @param app					The ChaseApp this handler is attached to
	 */
	public FileHandlers(ChaseApp app)
	{
		myApp = app;
	}
	
	/*
	 * Determines if there is local memory for this handler to read/write from/to
	 * 
	 * @return						Whether or not local memory exists
	 */
	public boolean isMemoryAvailable()
	{
		if (!Gdx.files.isLocalStorageAvailable())
		{
			myApp.alert("Memory", "Local memory is not available.");
			return false;
		}
		return true;
	}
	
	/*
	 * Writes to the specified file - will overwrite anything in an existing file with that name
	 * 
	 * @param fileName				The file name to write to - "name.bin"
	 * @param toWrite				The information to write to the file
	 * @return						Whether or not there was memory available
	 */
	public boolean writeFile(String fileName, String toWrite)
	{	
		if (isMemoryAvailable())
		{
			FileHandle file = Gdx.files.local(fileName);
			file.writeBytes(toWrite.getBytes(), false);
			return true;
		}
		else
		{
			myApp.alert("Memory", "Can't save file, local memory may not exist");
			return false;
		}
	}
	
	/*
	 * Adds data to the end of the specified file
	 * 
	 * @param fileName				The file name to add to - "name.bin"
	 * @param toAppend				The information to write to the end of the file
	 * @return						Whether or not there was memory available
	 */
	public boolean addToFile(String fileName, String toAppend)
	{
		if (isMemoryAvailable())
		{
			FileHandle file = Gdx.files.local(fileName);
			file.writeBytes(toAppend.getBytes(), true);
			return true;
		}
		else
		{
			myApp.alert("Memory", "Can't save file, local memory may not exist");
			return false;
		}
	}
	
	/*
	 * Reads in the specified file
	 * 
	 * @param fileName				The file name to read from - "name.bin"
	 * @return						The string containing all of the data in the file - returns "" if there's an error
	 */
	public String readFile(String fileName)
	{
		FileHandle file = Gdx.files.local(fileName);
		if (!file.exists())
		{
			myApp.alert("FileHandler", "File " + fileName + " not found. May not exist");
			return "";
		}
		try
		{
			return new String(file.readBytes());
		}
		catch(RuntimeException re)
		{
			myApp.alert("FileHandler", "Error reading file " + fileName + ". May be corrupt or incorrectly formatted");
			return "";
		}
	}
} // End class