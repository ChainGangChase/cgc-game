/*
 * @(#)HUDProgress.java		0.2 14/3/3
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.ChainGame;
import com.percipient24.cgc.Data;

/*
 * Contains logic for rendering the HUD progress meter
 * 
 * @version 0.2 14/3/3
 * @author William Ziegler
 */
public class HUDProgress extends CGCOverlay
{
	private AnimationManager animManager;
	private ChainGame game;
	
	private float[] playerProgress;
	private float[] resetProgress;
	private float trackMotion;
	private float trackElapsedVanish;
	private float trackElapsedAppear;
	private float finishElapsed;
	
	private float scaleX;
	private float scaleY;
	
	private float trackXPosition;
	
	private float hudMargin;
	
	/*
	 * Creates a new HUDProgress object
	 * 
	 * @param newBatch				The SpriteBatch the game is using
	 * @param newGame				The ChainGame object calling this constructor
	 */
	public HUDProgress(SpriteBatch newBatch, ChainGame newGame)
	{
		super(newBatch);
		game = newGame;
		animManager = CGCWorld.getAnimManager();
		
		playerProgress = new float[CGCWorld.getNumPlayers()];
		resetProgress = new float[CGCWorld.getNumPlayers()];
		for (int i = 0; i < playerProgress.length; i++)
		{
			playerProgress[i] = 0.0f;
			resetProgress[i] = 1.0f;
		}
		
		trackMotion = 1.0f;
		trackElapsedVanish = 0.0f;
		trackElapsedAppear = 0.0f;
		finishElapsed = 0.0f;
	}

	/*
	 * Renders the progress meter during normal gameplay
	 * 
	 * @param delta                 Seconds elapsed since last frame
	 * @param curMap				The current map the players are running
	 * @param maxMaps				The total number of maps in this chase
	 * @param prevMaps				The total length of all previous maps in the chase
	 */
	public void render(float delta, int curMap, int maxMaps, float prevMaps)
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
					
			//If this is the final map, draw the finish line and bottom track.
			if (curMap + 1 >= maxMaps)
			{
				if (trackMotion < 0.0f)
				{
					//Draw the finish line.
					if (!AnimationManager.progFinishAnim.isAnimationFinished(finishElapsed))
					{
						finishElapsed += Gdx.graphics.getDeltaTime();
					}
					
					animManager.drawAnimation(sBatch, AnimationManager.progFinishAnim, 
							finishElapsed, false, trackXPosition, Data.ACTUAL_HEIGHT / 2
							+ animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							- animManager.gHeight(AnimationManager.progFinishAnim) * scaleY - 2 * scaleY,
							0,
							0, scaleX, scaleY);
					
					//Draw the bottom track.
					animManager.drawFrame(sBatch, AnimationManager.progTrackVanishAnim, 
							0, trackXPosition, Data.ACTUAL_HEIGHT / 2
							- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							- animManager.gHeight(AnimationManager.progTrackVanishAnim)
							* (1 - scaleY) + 2 * scaleY,
							0,
							animManager.gHeight(AnimationManager.progTrackVanishAnim), scaleX, scaleY);
				}
				else
				{
					trackMotion -= .01f;
					
					//Move the top track to the bottom.
					animManager.drawFrame(sBatch, AnimationManager.progTrackVanishAnim, 0,
							trackXPosition, Data.ACTUAL_HEIGHT / 2
							- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							+ trackMotion * ((animManager.gHeight(AnimationManager.progMeterAnim) - 4)
							- animManager.gHeight(AnimationManager.progTrackVanishAnim)) * scaleY + 2 * scaleY,
							0, animManager.gHeight(AnimationManager.progTrackVanishAnim), 
							scaleX, scaleY);
					
					//Make the bottom track disappear.
					if (!AnimationManager.progTrackVanishAnim.isAnimationFinished(trackElapsedVanish))
					{
						trackElapsedVanish += Gdx.graphics.getDeltaTime();
						animManager.drawAnimation(sBatch, AnimationManager.progTrackVanishAnim, 
							trackElapsedVanish, false, trackXPosition, Data.ACTUAL_HEIGHT / 2
							- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							- animManager.gHeight(AnimationManager.progTrackVanishAnim)
							* (1 - scaleY) + 2 * scaleY,
							0, animManager.gHeight(AnimationManager.progTrackVanishAnim), 
							scaleX, scaleY);
					}
				}
			}
			//If it's not the final map, draw a track at the top and one at the bottom.
			else
			{
				if (trackMotion == 1.0f)
				{
					//Draw a track at the top.
					animManager.drawFrame(sBatch, AnimationManager.progTrackVanishAnim, 0,
							trackXPosition, Data.ACTUAL_HEIGHT / 2
							+ animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							- animManager.gHeight(AnimationManager.progTrackVanishAnim) * scaleY - 2 * scaleY,
							0, 0, scaleX, scaleY);
					
					//After the first map, draw a second track icon at the bottom.
					if (curMap > 0)
					{
						animManager.drawFrame(sBatch, AnimationManager.progTrackVanishAnim, 0, 
								trackXPosition, Data.ACTUAL_HEIGHT / 2
								- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
								- animManager.gHeight(AnimationManager.progTrackVanishAnim)
								* (1 - scaleY) + 2 * scaleY,
								0, animManager.gHeight(AnimationManager.progTrackVanishAnim), 
								scaleX, scaleY);
					}
				}
				else if (trackMotion <= 0.0f)
				{
					trackMotion = 0.0f;
					
					if (!AnimationManager.progTrackAppearAnim.isAnimationFinished(trackElapsedAppear))
					{
						//Make the top track appear.
						trackElapsedAppear += Gdx.graphics.getDeltaTime();
						animManager.drawAnimation(sBatch, AnimationManager.progTrackAppearAnim, 
							trackElapsedAppear, false, trackXPosition, Data.ACTUAL_HEIGHT / 2
							+ animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							- animManager.gHeight(AnimationManager.progTrackVanishAnim) * scaleY - 2 * scaleY,
							0, 0, scaleX, scaleY);	
						
						//Draw the bottom track as an unmoving image.
						animManager.drawFrame(sBatch, AnimationManager.progTrackVanishAnim, 0,
								trackXPosition, Data.ACTUAL_HEIGHT / 2
								- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
								- animManager.gHeight(AnimationManager.progTrackVanishAnim)
								* (1 - scaleY) + 2 * scaleY,
								0, animManager.gHeight(AnimationManager.progTrackVanishAnim), 
								scaleX, scaleY);
					}
					//Once the animation is done, just draw unmoving images.
					else
					{
						trackMotion = 1.0f;
					}
				}
				else if (trackMotion < 1.0f)
				{
					trackMotion -= .01f;
					
					//Make the bottom track disappear, if there is one.
					if (curMap > 1 && !AnimationManager.progTrackVanishAnim.isAnimationFinished(trackElapsedVanish))
					{
						trackElapsedVanish += Gdx.graphics.getDeltaTime();
						animManager.drawAnimation(sBatch, AnimationManager.progTrackVanishAnim, 
							trackElapsedVanish, false, trackXPosition, Data.ACTUAL_HEIGHT / 2
							- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							- animManager.gHeight(AnimationManager.progTrackVanishAnim)
							* (1 - scaleY) + 2 * scaleY,
							0, animManager.gHeight(AnimationManager.progTrackVanishAnim), 
							scaleX, scaleY);
					}
					
					//Move the top track down to the bottom.
					animManager.drawFrame(sBatch, AnimationManager.progTrackVanishAnim, 0, 
							trackXPosition, Data.ACTUAL_HEIGHT / 2
							- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							+ trackMotion * ((animManager.gHeight(AnimationManager.progMeterAnim) - 4)
							- animManager.gHeight(AnimationManager.progTrackVanishAnim)) * scaleY + 2 * scaleY,
							0, animManager.gHeight(AnimationManager.progTrackVanishAnim),
							scaleX, scaleY);
				}
			}

			//Draw the player dots last, so they will always be on top.
			float resetYPosition = 0.0f;
			float progressYPosition;
			for (int i = 0; i < playerProgress.length; i++)
			{
				playerProgress[i] = (CGCWorld.getPlayers().get(i).getBody().getPosition().y - prevMaps)
						/ (game.getCurrentMaps().get(curMap).gSize() * 11 - 11);
				
				if (playerProgress[i] < 0)
				{
					playerProgress[i] = 0.0f;
				}
				else if (playerProgress[i] > 1)
				{
					playerProgress[i] = 1.0f;
				}
				
				if (resetProgress[i] < 1.0f)
				{
					resetYPosition = Data.ACTUAL_HEIGHT / 2
							- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							+ resetProgress[i] * (animManager.gHeight(AnimationManager.progMeterAnim) * scaleY
							- animManager.gHeight(AnimationManager.progConvictAnim) * scaleY);
					resetProgress[i] -= .01f;
				}
				
				if (CGCWorld.getPlayersTypes()[i])
				{
					progressYPosition = Data.ACTUAL_HEIGHT / 2
							- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							+ (playerProgress[i] * (animManager.gHeight(AnimationManager.progMeterAnim)
							- animManager.gHeight(AnimationManager.progConvictAnim)) * scaleY);
					
					if (resetProgress[i] != 1.0f && resetProgress[i] > playerProgress[i])
					{
						animManager.drawFrame(sBatch, AnimationManager.progConvictAnim, 0, 
								Data.ACTUAL_WIDTH - hudMargin
								+ animManager.gWidth(AnimationManager.progMeterAnim) / 2 * scaleX 
								- animManager.gWidth(AnimationManager.progConvictAnim) / 2, 
								resetYPosition, animManager.gWidth(AnimationManager.progConvictAnim) / 2,
								0, scaleX, scaleY);
					}
					else
					{
						animManager.drawFrame(sBatch, AnimationManager.progConvictAnim, 0, 
								Data.ACTUAL_WIDTH - hudMargin
								+ animManager.gWidth(AnimationManager.progMeterAnim) / 2 * scaleX
								- animManager.gWidth(AnimationManager.progConvictAnim) / 2, 
								progressYPosition, animManager.gWidth(AnimationManager.progConvictAnim) / 2,
								0, scaleX, scaleY);
						
						resetProgress[i] = 1.0f;
					}
				}
				else
				{
					progressYPosition = Data.ACTUAL_HEIGHT / 2
							- animManager.gHeight(AnimationManager.progMeterAnim) / 2 * scaleY
							+ (playerProgress[i] * (animManager.gHeight(AnimationManager.progMeterAnim)
							- animManager.gHeight(AnimationManager.progCopAnim)) * scaleY);
					
					if (resetProgress[i] != 1.0f && resetProgress[i] > playerProgress[i])
					{
						animManager.drawFrame(sBatch, AnimationManager.progCopAnim, 0, 
								Data.ACTUAL_WIDTH - hudMargin 
								+ animManager.gWidth(AnimationManager.progMeterAnim) / 2 * scaleX
								- animManager.gWidth(AnimationManager.progCopAnim) / 2, 
								resetYPosition, animManager.gWidth(AnimationManager.progCopAnim) / 2,
								0, scaleX, scaleY);
					}
					else
					{
						animManager.drawFrame(sBatch, AnimationManager.progCopAnim, 0, 
								Data.ACTUAL_WIDTH - hudMargin
								+ animManager.gWidth(AnimationManager.progMeterAnim) / 2 * scaleX
								- animManager.gWidth(AnimationManager.progCopAnim) / 2, 
								progressYPosition, animManager.gWidth(AnimationManager.progCopAnim) / 2,
								0, scaleX, scaleY);
						
						resetProgress[i] = 1.0f;
					}
				}
			}
		}
	}
	
	public void resize() 
	{
		scaleX = (float)Data.ACTUAL_WIDTH / (float)1920;
		scaleY = (float)Data.ACTUAL_HEIGHT / (float)1080;
	}
	
	/*
	 * Moves the players' dots back to the bottom of the tracker to prepare for the new map
	 */
	public void resetProgress()
	{
		for (int i = 0; i < playerProgress.length; i++)
		{
			resetProgress[i] = playerProgress[i] - .01f;
			if (resetProgress[i] < 0)
			{
				resetProgress[i] = 0;
			}
		}
		
		trackMotion -= .01f;
		trackElapsedVanish = 0.0f;
		trackElapsedAppear = 0.0f;
		finishElapsed = 0.0f;
	}

	
} // End class

