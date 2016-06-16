/*
 * @(#)CGCScreen.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import com.badlogic.gdx.utils.Array;
import com.percipient24.helpers.StringLayout;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;

/*
 * Base class for a game screen
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 * @author Christopher Rider
 */
public class EndScreen extends CGCScreen 
{
	public StringLayout coinLayout;
	protected Array<Player> players;

	/*
	 * Creates a new CGCScreen object
	 * 
	 * @param app					The app running this screen
	 */
	public EndScreen(ChaseApp app)
	{
		super(app);
		coinLayout = new StringLayout("", ChaseApp.menuFont);
	}

	protected void drawPlayerResults() {
		int startX = (int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_RIGHT].x;
		int x;
		int y = (int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_RIGHT].y - 120;
		int index = 0;
		for (Player player : players) {
			coinLayout.updateText((index + 1) + " - " + player.getCoins());
			x = startX - (int)coinLayout.getLayout().width;


			ChaseApp.menuFont.draw(
				sBatch,
				coinLayout.getLayout(),
				x, 
				y);

			index++;
			y -= 40;
		}
	}

	public void setPlayers(Array<Player> p) {
		players = p;
	}

} // End class