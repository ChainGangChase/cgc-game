/*
 * @(#)TransitionOut.java		0.3 14/4/25
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.overlays;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.percipient24.helpers.StringLayout;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.net.MapVO;
import com.percipient24.enums.BossType;
import com.percipient24.cgc.screens.helpers.LanguageKeys;

/*
 * Contains the logic for the transition to the game screen
 * 
 * @version 0.3 14/4/25
 * @author William Ziegler
 */
public class Transition extends CGCOverlay
{
	private ShapeRenderer shapes;
	
	private float screenScaleX;
	private float screenScaleY;
	
	private Vector2 position;
	
	private String topMessage;
	private String midMessage;
	private String botMessage;

	private StringLayout topLayout;
	private StringLayout midLayout;
	private StringLayout botLayout;
	
	private BossType boss;
	
	/*
	 * Creates a new Transition object for entering ChainGames
	 * 
	 * @param newBatch				The SpriteBatch the game is using
	 * @param mapData				The map the players are entering
	 * @param app					The ChaseApp the game is using
	 */
	public Transition(SpriteBatch newBatch, MapVO mapData, ChaseApp app)
	{
		super(newBatch);
		shapes = app.getShapes();
		
		topMessage = ChaseApp.lang.get(LanguageKeys.entering_map);
		midMessage = mapData.mname;
		botMessage = ChaseApp.lang.format(LanguageKeys.by_x, mapData.uname);

		topLayout = new StringLayout(topMessage, ChaseApp.menuFont);
		midLayout = new StringLayout(midMessage, ChaseApp.menuFont);
		botLayout = new StringLayout(botMessage, ChaseApp.menuFont);

		position = new Vector2(0, 0);
	}

	/*
	 * Creates a new Transition object for entering BossFights
	 * 
	 * @param newBatch				The SpriteBatch the game is using
	 * @param bossType				The BossType the players are facing
	 * @param app					The ChaseApp the game is using
	 */
	public Transition(SpriteBatch newBatch, BossType bossType, ChaseApp app)
	{
		super(newBatch);
		shapes = app.getShapes();
		
		boss = bossType;
		
		switch (bossType)
		{
			case NONE:
				break;
			case PALL_BEARER:
				topMessage = ChaseApp.lang.get(LanguageKeys.pall_top);
				midMessage = ChaseApp.lang.get(LanguageKeys.pall_mid);
				botMessage = ChaseApp.lang.get(LanguageKeys.pall_bot);
				break;
			case STEEL_HORSE:
				topMessage = ChaseApp.lang.get(LanguageKeys.steel_top);
				midMessage = ChaseApp.lang.get(LanguageKeys.steel_mid);
				botMessage = ChaseApp.lang.get(LanguageKeys.steel_bot);
				break;
			case TANK:
			case TANK_AI:
				topMessage = ChaseApp.lang.get(LanguageKeys.tank_top);
				midMessage = ChaseApp.lang.get(LanguageKeys.tank_mid);
				botMessage = ChaseApp.lang.get(LanguageKeys.tank_bot);
				break;
			case TRAIN_RUSH:
				topMessage = ChaseApp.lang.get(LanguageKeys.train_top);
				midMessage = ChaseApp.lang.get(LanguageKeys.train_mid);
				botMessage = ChaseApp.lang.get(LanguageKeys.train_bot);
				break;
			case TRENCH_RUN:
				topMessage = ChaseApp.lang.get(LanguageKeys.trench_top);
				midMessage = ChaseApp.lang.get(LanguageKeys.trench_mid);
				botMessage = ChaseApp.lang.get(LanguageKeys.trench_bot);
				break;
			default:
				break;
		}

		topLayout = new StringLayout(topMessage, ChaseApp.menuFont);
		midLayout = new StringLayout(midMessage, ChaseApp.menuFont);
		botLayout = new StringLayout(botMessage, ChaseApp.menuFont);
		
		position = new Vector2(0, 0);
	}
	
	/*
	 * Creates a general-purpose Transition object
	 * 
	 * @param newBatch				The SpriteBatch the game is using
	 * @param topMessage			The top line of the message to show to players
	 * @param midMessage			The middle line of the message to show to players
	 * @param botMessage			The bottom line of the message to show to players
	 * @param app					The ChaseApp the game is using
	 */
	public Transition(SpriteBatch newBatch, String topMessage, String midMessage,
			String botMessage, ChaseApp app)
	{
		super(newBatch);
		shapes = app.getShapes();
		
		this.topMessage = topMessage;
		this.midMessage = midMessage;
		this.botMessage = botMessage;

		topLayout = new StringLayout(topMessage, ChaseApp.menuFont);
		midLayout = new StringLayout(midMessage, ChaseApp.menuFont);
		botLayout = new StringLayout(botMessage, ChaseApp.menuFont);

		position = new Vector2(0, 0);
	}
	
	/*
	 * Renders the information about the map being played
	 * 
	 * @param delta					Seconds elapsed since last frame
	 */
	public void render(float delta)
	{
		if (showElement)
		{
			shapes.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			shapes.begin(ShapeType.Filled);
			shapes.rect(position.x, position.y, Data.GAME_WIDTH, Data.GAME_HEIGHT);
			shapes.end();
			
			screenScaleX = (float)Data.ACTUAL_WIDTH / 1920.0f;
			screenScaleY = (float)Data.ACTUAL_HEIGHT / 1080.0f;
			
			float newScale = (screenScaleX + screenScaleY) / 2.0f;
			ChaseApp.menuFont.getData().setScale(newScale);
			
			sBatch.setProjectionMatrix(overlayMatrix);
			sBatch.begin();

			ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			ChaseApp.menuFont.draw(
				sBatch,
				topLayout.getLayout(), 
				Data.ACTUAL_WIDTH / 2 - topLayout.getLayout().width / 2f + position.x,
				Data.ACTUAL_HEIGHT * .75f + position.y);
			
			ChaseApp.menuFont.draw(
				sBatch,
				midLayout.getLayout(), 
				Data.ACTUAL_WIDTH / 2 - midLayout.getLayout().width / 2f + position.x,
				Data.ACTUAL_HEIGHT * .6f + position.y);
			
			ChaseApp.menuFont.draw(
				sBatch,
				botLayout.getLayout(), 
				Data.ACTUAL_WIDTH / 2 - botLayout.getLayout().width / 2f + position.x,
				Data.ACTUAL_HEIGHT * .45f + position.y);

			sBatch.end();
		}
	}
	
	/*
	 * Gets the X position of this Transition
	 * 
	 * @return						The X position of this Transition
	 */
	public float getXPosition()
	{
		return position.x;
	}
	
	/*
	 * Sets the X position of this Transition
	 * 
	 * @param xPosition				The new X position of this Transition
	 */
	public void setXPosition(float xPosition)
	{
		position.x = xPosition;
	}
	
	/*
	 * Gets the Y position of this Transition
	 * 
	 * @return						The Y position of this Transition
	 */
	public float getYPosition()
	{
		return position.y;
	}
	
	/*
	 * Sets the Y position of this Transition
	 * 
	 * @param yPosition				The new Y position of this Transition
	 */
	public void setYPosition(float yPosition)
	{
		position.y = yPosition;
	}
	
	/*
	 * If this Transition goes to a BossFight, get the BossType of the BossFight
	 * 
	 * @return						The BossType of the impending BossFight (null if this doesn't go to a boss)
	 */
	public BossType getBossType()
	{
		return boss;
	}
}
