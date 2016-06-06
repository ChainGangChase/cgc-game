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
import com.percipient24.cgc.screens.helpers.LanguageKeys;


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
			messageList.put(0, ChaseApp.lang.get(LanguageKeys.tutorial_move));
			messageList.put(11, ChaseApp.lang.get(LanguageKeys.tutorial_chains));
			messageList.put(33, ChaseApp.lang.get(LanguageKeys.tutorial_trees));
			messageList.put(66, ChaseApp.lang.get(LanguageKeys.tutorial_forest));
			messageList.put(85, ChaseApp.lang.get(LanguageKeys.tutorial_mud));
			messageList.put(96, ChaseApp.lang.get(LanguageKeys.tutorial_water));
			messageList.put(107, ChaseApp.lang.get(LanguageKeys.tutorial_current));
			messageList.put(116, ChaseApp.lang.get(LanguageKeys.tutorial_jump));
			messageList.put(127, ChaseApp.lang.get(LanguageKeys.tutorial_bridge));
			messageList.put(148, ChaseApp.lang.get(LanguageKeys.tutorial_sensor));
			messageList.put(164, ChaseApp.lang.get(LanguageKeys.tutorial_icons));
			messageList.put(176, ChaseApp.lang.get(LanguageKeys.tutorial_special));
		    messageList.put(185, ChaseApp.lang.get(LanguageKeys.tutorial_correct));
			messageList.put(196, ChaseApp.lang.get(LanguageKeys.tutorial_guard));
			messageList.put(206, ChaseApp.lang.get(LanguageKeys.tutorial_train));
			messageList.put(212, ChaseApp.lang.get(LanguageKeys.tutorial_escape));
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
