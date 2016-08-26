/*
 * @(#)OffScreenTimer.java		0.2 14/3/3
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.art.TextureAnimationDrawer;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;

/*
 * Contains the logic for the off-screen timer HUD element
 * 
 * @version 0.2 14/3/3
 * @author JD Kelly
 * @author William Ziegler
 */
public class OffScreenTimer extends CGCOverlay
{
	private TextureAnimationDrawer animManager;
	
	/*
	 * Creates a new OffScreenTimer object
	 * 
	 * @param newBatch				The SpriteBatch the game is using
	 */
	public OffScreenTimer(SpriteBatch newBatch)
	{
		super(newBatch);
		animManager = CGCWorld.getAnimManager();
	}
	
	/*
	 * Renders the off-screen timers, if necessary
	 * 
	 * @param delta					Seconds elapsed since last frame
	 */
	public void render(float delta)
	{
		if (showElement)
		{
			Array<Player> players = CGCWorld.getPlayers();
			
			for(int i = 0; i < players.size; i++)
			{
				Player p = players.get(i);
				
				if(p.getOffScreenTimerStarted())
				{
					float osx = (Data.ACTUAL_WIDTH - CGCWorld.getAnimManager().gWidth(p.getHighAnim())) / 2  - (9.5f - p.getBody().getWorldCenter().x) * Data.BOX_TO_SCREEN * Data.ACTUAL_WIDTH / (19.0f * 1.565f * Data.BOX_TO_SCREEN);
					if(p instanceof Prisoner)
					{
						if(p.getPosition().y > CGCWorld.getCamera().position.y)
						{
							animManager.drawFrame(sBatch, TextureAnimationDrawer.prisonerUpArrowAnim, p.getTimeLeft(),
									osx, Gdx.graphics.getHeight() - CGCWorld.getAnimManager().gWidth(p.getHighAnim()));
						}
						else
						{
							animManager.drawFrame(sBatch, TextureAnimationDrawer.prisonerDownArrowAnim, p.getTimeLeft(), osx, 0);
						}
					}
					else
					{
						if(p.getPosition().y > CGCWorld.getCamera().position.y)
						{
							animManager.drawFrame(sBatch, TextureAnimationDrawer.copUpArrowAnim, p.getTimeLeft(),
									osx, Gdx.graphics.getHeight() -  CGCWorld.getAnimManager().gWidth(p.getHighAnim()));
						}
						else
						{
							animManager.drawFrame(sBatch, TextureAnimationDrawer.copDownArrowAnim, p.getTimeLeft(), osx, 0);
						}
					}
					
					animManager.drawFrame(sBatch, TextureAnimationDrawer.offScreenTimerAnim, p.getTimeLeft(),
							Gdx.graphics.getWidth() / 2 - animManager.gWidth(TextureAnimationDrawer.offScreenTimerAnim) / 2,
							Gdx.graphics.getHeight() / 2 - animManager.gHeight(TextureAnimationDrawer.offScreenTimerAnim) / 2);
				}
			}
		}
	}

}
