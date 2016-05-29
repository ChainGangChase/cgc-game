/*
 * @(#)BossHUDProgress.java		0.2 14/3/3
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.overlays;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.enums.BossType;

/*
 * Contains logic for rendering the HUD progress meter in boss fights
 * 
 * @version 0.2 14/3/3
 * @author William Ziegler
 * @author Christopher Rider
 */
public class BossHUDProgress extends CGCOverlay
{
	private AnimationManager animManager;
	private BossType boss;
	
	private float playerAdjustment;
	
	private float[] playerProgress;
	
	private float scaleX;
	private float scaleY;
	
	private float trackXPosition;
	
	private float hudMargin;
	
	/*
	 * Creates a new BossHUDProgress object
	 * 
	 * @param newBatch				The SpriteBatch the game is using
	 * @param newBoss				The Boss object for this HUD
	 */
	public BossHUDProgress(SpriteBatch newBatch, BossType newBoss)
	{
		super(newBatch);
		animManager = CGCWorld.getAnimManager();
		boss = newBoss;
		
		playerProgress = new float[CGCWorld.getNumPlayers()];
		for (int i = 0; i < playerProgress.length; i++)
		{
			playerProgress[i] = 0.0f;
		}
		
		playerAdjustment = 0;
		if (boss == BossType.TANK)
		{
			playerAdjustment = animManager.gHeight(AnimationManager.hudTankAnim);
		}
	}

	/*
	 * Renders the boss progress meter during boss fights
	 * 
	 * @param delta					Seconds elapsed since last frame
	 * @param levelLength			How many chunks the level has
	 */
	public void render(float delta, float levelLength)
	{
		if (showElement)
		{
			hudMargin = (Data.ACTUAL_WIDTH * .1f);
			
			trackXPosition = Data.ACTUAL_WIDTH - hudMargin
					+ animManager.gWidth(AnimationManager.progMeterAnim) / 2f * scaleX
					- animManager.gWidth(AnimationManager.progFinishAnim) / 2f * scaleX;
			
			animManager.drawFrame(sBatch, AnimationManager.progMeterAnim, 0, Data.ACTUAL_WIDTH - hudMargin, 
					Data.ACTUAL_HEIGHT / 2 - animManager.gHeight(AnimationManager.progMeterAnim) / 2, 
					0, animManager.gHeight(AnimationManager.progMeterAnim) / 2, scaleX, 
					scaleY);
			
			animManager.drawFrame(sBatch, AnimationManager.progFinishAnim, 
					3, trackXPosition, Data.ACTUAL_HEIGHT / 2
					+ animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
					- animManager.gHeight(AnimationManager.progFinishAnim) * scaleY - 2 * scaleY,
					0, 0, scaleX, scaleY);
			
			//Draw the player dots last, so they will always be on top.
			float progressYPosition;
			Array<Player> players = CGCWorld.getPlayers();
			
			for (int i = 0; i < players.size; i++)
			{
				playerProgress[i] = players.get(i).getBody().getPosition().y / (levelLength * 11);
				
				if (playerProgress[i] < 0)
				{
					playerProgress[i] = 0.0f;
				}
				else if (playerProgress[i] > 1)
				{
					playerProgress[i] = 1.0f;
				}
			}
			
			for (int i = 0; i < players.size; i++)
			{
				if (players.get(i) instanceof Prisoner)
				{	
					progressYPosition = Data.ACTUAL_HEIGHT / 2
							- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							+ playerProgress[i] * ((animManager.gHeight(AnimationManager.progMeterAnim)
							- playerAdjustment) * scaleY
							- animManager.gHeight(AnimationManager.progConvictAnim) * scaleY);
					
					if (players.get(i).isAlive())
					{
						animManager.drawFrame(sBatch, AnimationManager.progConvictAnim, 0, 
							Data.ACTUAL_WIDTH - hudMargin
							+ animManager.gWidth(AnimationManager.progMeterAnim) / 2 * scaleX 
							- animManager.gWidth(AnimationManager.progConvictAnim) / 2, 
							progressYPosition,
							scaleX, scaleY);
					}
					else
					{
						animManager.drawFrame(sBatch, AnimationManager.progConvictAnim, 1, 
							Data.ACTUAL_WIDTH - hudMargin
							+ animManager.gWidth(AnimationManager.progMeterAnim) / 2 * scaleX
							- animManager.gWidth(AnimationManager.progConvictAnim) / 2, 
							progressYPosition,
							scaleX, scaleY);
					}
				}
			}
		}
	}
	
	/*
	 * Renders the boss progress meter during boss fights
	 * 
	 * @param delta					Seconds elapsed since last frame
	 * @param bossYPosition			The boss's Y position in the world
	 * @param levelLength			How many chunks the level has
	 */
	public void render(float delta, float bossYPosition, float levelLength)
	{
		if (showElement)
		{
			hudMargin = (Data.ACTUAL_WIDTH * .1f);
			
			trackXPosition = Data.ACTUAL_WIDTH - hudMargin
					+ animManager.gWidth(AnimationManager.progMeterAnim) / 2f * scaleX
					- animManager.gWidth(AnimationManager.progFinishAnim) / 2f * scaleX;
			
			animManager.drawFrame(sBatch, AnimationManager.progMeterAnim, 0, Data.ACTUAL_WIDTH - hudMargin, 
					Data.ACTUAL_HEIGHT / 2 - animManager.gHeight(AnimationManager.progMeterAnim) / 2, 
					0, animManager.gHeight(AnimationManager.progMeterAnim) / 2, scaleX, 
					scaleY);
			
			animManager.drawFrame(sBatch, AnimationManager.progFinishAnim, 3, 
					trackXPosition, Data.ACTUAL_HEIGHT / 2
					+ animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
					- animManager.gHeight(AnimationManager.progFinishAnim) * scaleY - 2 * scaleY,
					0, 0, scaleX, scaleY);
			
			if (boss == BossType.TANK_AI || boss == BossType.TANK)
			{
				float bossProgress = bossYPosition / (levelLength * 11);
				if (bossProgress > 1.0f)
				{
					bossProgress = 1.0f;
				}
				
				animManager.drawFrame(sBatch, AnimationManager.hudTankAnim, 0, 
						trackXPosition, Data.ACTUAL_HEIGHT / 2
						- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
						+ (bossProgress * (animManager.gHeight(AnimationManager.progMeterAnim) * scaleY
						- animManager.gHeight(AnimationManager.hudTankAnim) * scaleY)),
						0, animManager.gHeight(AnimationManager.hudTankAnim) / 2,
						scaleX, scaleY);
			}
			
			//Draw the player dots last, so they will always be on top.
			float progressYPosition;
			Array<Player> players = CGCWorld.getPlayers();
			
			for (int i = 0; i < players.size; i++)
			{
				playerProgress[i] = players.get(i).getBody().getPosition().y / (levelLength * 11);
				
				if (playerProgress[i] < 0)
				{
					playerProgress[i] = 0.0f;
				}
				else if (playerProgress[i] > 1)
				{
					playerProgress[i] = 1.0f;
				}
			}
			
			for (int i = 0; i < players.size; i++)
			{
				if (players.get(i) instanceof Prisoner)
				{	
					progressYPosition = Data.ACTUAL_HEIGHT / 2
							- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							+ playerProgress[i] * ((animManager.gHeight(AnimationManager.progMeterAnim)
							- playerAdjustment) * scaleY
							- animManager.gHeight(AnimationManager.progConvictAnim) * scaleY);
					
					if (players.get(i).isAlive())
					{
						animManager.drawFrame(sBatch, AnimationManager.progConvictAnim, 0, 
							Data.ACTUAL_WIDTH - hudMargin
							+ animManager.gWidth(AnimationManager.progMeterAnim) / 2 * scaleX 
							- animManager.gWidth(AnimationManager.progConvictAnim) / 2, 
							progressYPosition,
							scaleX, scaleY);
					}
					else
					{
						animManager.drawFrame(sBatch, AnimationManager.progConvictAnim, 1, 
							Data.ACTUAL_WIDTH - hudMargin
							+ animManager.gWidth(AnimationManager.progMeterAnim) / 2 * scaleX
							- animManager.gWidth(AnimationManager.progConvictAnim) / 2, 
							progressYPosition,
							scaleX, scaleY);
					}
				}
			}
		}
	}

	/*
	 * Resizes this HUD element to fit various screen sizes
	 */
	public void resize() 
	{
		scaleX = (float)Data.ACTUAL_WIDTH / (float)1920;
		scaleY = (float)Data.ACTUAL_HEIGHT / (float)1080;
	}
} // End class
