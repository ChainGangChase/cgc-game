/*
 * @(#)MapInfo.java		0.2 14/3/3
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.overlays;

import java.util.HashMap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;

/*
 * Contains the logic for the map info HUD element
 * 
 * @version 0.2 14/3/3
 * @author William Ziegler
 * @author Christopher Rider
 */
public class MapInfo extends CGCOverlay
{
	private float screenScaleX;
	private float screenScaleY;
	
	private HashMap<Integer, String> messageList;
	private int lastMessage;
	
	/*
	 * Creates a new MapInfo object
	 * 
	 * @param newBatch				The SpriteBatch the game is using
	 * @param initMessage			The message to be displayed initially
	 * @param tutorial				Whether or not this info is for a tutorial - if true, preset values are used instead of initMessage
	 */
	public MapInfo(SpriteBatch newBatch, String initMessage, boolean tutorial)
	{
		super(newBatch);
		
		messageList = new HashMap<Integer, String>();
		messageList.put(0, initMessage);
		lastMessage = 0;
		
		if (tutorial)
		{
			messageList.clear();
			messageList.put(0, "Time to get back to work in the chain gang. Use the joystick to move around.");
			messageList.put(11, "Those chains aren't too heavy, are they? They'll keep you together at least.");
			messageList.put(33, "Aren't the trees great this time of year? At least your chains can't go over them.");
			messageList.put(66, "The forests can get pretty hard to navigate. Single file, convicts!");
			messageList.put(85, "The mud in this area is pretty deep, so it'll slow you down quite a bit.");
			messageList.put(96, "The water ain't bad, just wade on through.");
			messageList.put(107, "Watch the current, convicts! Don't get washed away.");
			messageList.put(116, "Oh come on. Just jump over the water with the bumper.");
			messageList.put(127, "Alright, you can't jump in the mud, so just go over the bridge there.");
			messageList.put(148, "That box on the ground ain't just pretty, convicts. It'll open up nearby gates.");
			messageList.put(164, "Calm down, just hit all the sensors with the same icons to open the gate.");
			messageList.put(176, "Some of these can only be used by one of you convicts.");
		    messageList.put(185, "Go find the right one, and maybe I'll help your parole next time.");
			messageList.put(196, "Ah, just kidding. Just go head back to the guard towers there to come back.");
			messageList.put(206, "Hey! Where are you going? Get back here! Don't you know there's a train coming?!");
			messageList.put(212, "The prisoners are escaping! After them, boys!");
		}
	}

	/*
	 * Renders the information about the map being played
	 * 
	 * @param delta					Seconds elapsed since last frame
	 * @param yPos					The average Y position of the Prisoners
	 */
	public void render(float delta, int yPos)
	{
		if (showElement)
		{
			screenScaleX = (float)Data.ACTUAL_WIDTH / 1920.0f;
			screenScaleY = (float)Data.ACTUAL_HEIGHT / 1080.0f;
			
			ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
			
			if(CGCWorld.lost())
			{
				float endPer = 1 - CGCWorld.getEndPercent();
				Color colorForAlpha = ChaseApp.menuFont.getColor();
				colorForAlpha.r *= endPer;
				colorForAlpha.g *= endPer;
				colorForAlpha.b *= endPer;
				colorForAlpha.a = 1.0f;
				
				ChaseApp.menuFont.setColor(colorForAlpha);
			}
			float newScale = (screenScaleX + screenScaleY) / 2.0f;
			ChaseApp.menuFont.getData().setScale(newScale);
			ChaseApp.menuFont.draw(sBatch, getMessage(yPos), 
					MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.LOWER_LEFT].x, 
					MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.LOWER_LEFT].y + (int)(ChaseApp.menuFont.getLineHeight()));
		}
	}
	
	/*
	 * Get the message to be displayed
	 * 
	 * @param yPos					The current Y average of all Prisoners
	 * @return						The string to be displayed
	 */
	private String getMessage(int yPos)
	{
		String returned = messageList.get(yPos);
		if (returned == null)
		{
			returned = messageList.get(lastMessage);
			if (returned == null)
			{
				return "";
			}
			else
			{
				return returned;
			}
		}
		else
		{
			lastMessage = yPos;
			return returned;
		}
	}
} // End class
