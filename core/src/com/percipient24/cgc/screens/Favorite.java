/*
 * @(#)Favorite.java		0.1 14/2/7
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.ChainGame;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.net.MapVO;
import com.percipient24.cgc.overlays.Transition;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;
import com.percipient24.tweens.TransitionAccessor;

/*
 * Contains the data for the Favorite screen
 * 
 * @version 0.1 14/2/7
 * @author Christopher Rider
 */
public class Favorite extends CGCScreen 
{
	private Array<Integer> favoriteMapIds;
	private Array<MapVO> mapsPlayed;
	
	private TweenManager tManager;
	private Transition transition;
	private boolean transitioning;
	private String playerAccount = "";
	
	/*
	 * Creates a Favorite object
	 * 
	 * @param app					The app running this screen
	 */
	public Favorite(ChaseApp app) 
	{
		super(app);
		
		favoriteMapIds = new Array<Integer>();
		mapsPlayed = new Array<MapVO>();
		readFavorites();
		
		title = "Did you like this map?";
		titleLayout.updateText(title);
		message = "Map has been favorited!";
		
		tManager = ChaseApp.tManager;
		
		prevScreen = ChaseApp.mainMenu;
	}
	
	/*
	 * Sets the map being played currently
	 * 
	 * @param mapToAdd				The map that is being played
	 */
	public void sMap(MapVO mapToAdd)
	{
		if (mapToAdd.mname.equals("Tutorial"))
		{
			return;
		}
		
		mapsPlayed.add(mapToAdd);
	}
	
	/*
	 * Gets the list of maps played
	 * 
	 * @return						A list of all maps played in this game
	 */
	public Array<MapVO> getMapsPlayed()
	{
		return mapsPlayed;
	}
	
	/*
	 * Gets the list of favorite IDs
	 * 
	 * @return						The Array of favorite map IDs
	 */
	public Array<Integer> gFavorites()
	{
		return favoriteMapIds;
	}
	
	/*
	 * Handles control input to this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#handleInput()
	 */
	public void handleInput() 
	{
		if (!transitioning)
		{
			ControlAdapter boss = input.getBoss();
			ControlAdapter keyboardLeft = input.getKeyboardLeft(); 
			ControlAdapter keyboardRight = input.getKeyboardRight();
			
			handleB(boss, keyboardLeft, keyboardRight);
			navigateMenu(boss, keyboardLeft, keyboardRight);
			handleA(boss, keyboardLeft, keyboardRight);
		}
		
		super.handleInput();
	}
	
	/*
	 * Handles what happens when A is pressed on this menu
	 * 
	 * @param boss					The controller in charge of menus
	 * @param keyboardLeft			The left side of the attached keyboard (if any)
	 * @param keyboardRight			The right side of the attached keyboard (if any)
	 */
	protected void handleA(ControlAdapter boss, ControlAdapter keyboardLeft, ControlAdapter keyboardRight)
	{
		if(boss.justPressed(ControlType.SELECT) || keyboardLeft.justPressed(ControlType.SELECT) 
				|| keyboardRight.justPressed(ControlType.SELECT))
		{
			boss.changeControlState(ControlType.SELECT, false);
			keyboardLeft.changeControlState(ControlType.SELECT, false);
			keyboardRight.changeControlState(ControlType.SELECT, false);
			if (display)
			{
				if (selected < items.size - 4)
				{
					display = false;
					favoriteMap(selected);
				}
				else if (selected == items.size - 4)
				{
					clearMapsPlayed();
					startTransition();
				}
				else if (selected == items.size - 3)
				{
					clearMapsPlayed();
					myApp.setScreen(ChaseApp.characterSelect);
				}
				else if (selected == items.size - 2)
				{
					clearMapsPlayed();
					myApp.setScreen(ChaseApp.mapSelect);
				}
				else if (selected == items.size - 1)
				{
					clearMapsPlayed();
					myApp.setScreen(prevScreen);
				}
			}
			else
			{
				display = true;
			}
		}
	}
	
	/*
	 * Handles what happens when B is pressed on this menu
	 * 
	 * @param boss					The controller in charge of menus
	 * @param keyboardLeft			The left side of the attached keyboard (if any)
	 * @param keyboardRight			The right side of the attached keyboard (if any)
	 */
	protected void handleB(ControlAdapter boss, ControlAdapter keyboardLeft, ControlAdapter keyboardRight)
	{
		if (boss.justPressed(ControlType.BACK) || keyboardLeft.justPressed(ControlType.BACK) 
				|| keyboardRight.justPressed(ControlType.BACK))
		{
			boss.changeControlState(ControlType.BACK, false);
			keyboardLeft.changeControlState(ControlType.BACK, false);
			keyboardRight.changeControlState(ControlType.BACK, false);
			if (display)
			{
				clearMapsPlayed();
				myApp.setScreen(ChaseApp.mainMenu);
			}
			else
			{
				display = true;
			}
		}
	}
	
	/*
	 * Draws this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#render(float)
	 */
	public void render(float delta) 
	{
		super.render(delta);
		
		drawGenericMenu();
		
		sBatch.end();
		if (transitioning)
		{
			tManager.update(delta);
			transition.render(delta);
			sBatch.setProjectionMatrix(ChaseApp.menuCam.combined);
		}
	}
	
	/*
	 * Moves a Transition in from off-screen to bring the players into the game
	 */
	private void startTransition()
	{
		transition = new Transition(sBatch, ChaseApp.mapSelect.getLastMap(), myApp);
		transition.setXPosition(Data.ACTUAL_WIDTH * 1.5f);
		transition.setShow(true);
		transitioning = true;
		
		Timeline.createSequence()
		.push
		(Tween.to(transition, TransitionAccessor.TRANSLATE_X, 1.0f).ease(Cubic.OUT))
		.pushPause(2.0f)
		.setCallback(changeToGameScreen)
		.start(tManager);
	}
	
	/*
	 * Switches the game screen to ChainGame once the above Timeline is over
	 */
	private TweenCallback changeToGameScreen = new TweenCallback()
	{
		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			transitioning = false;
			input.getBoss().getCurrent().resetData();
			myApp.setScreen(new ChainGame(myApp, ChaseApp.characterSelect.getNumPlayers(), 
					ChaseApp.mapSelect.getLastMap(), ChaseApp.mapSelect.getMapCache(), transition, false));
		}
	};
	
	/*
	 * Shows this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#show()
	 */
	public void show() 
	{
		transition = new Transition(sBatch, ChaseApp.mapSelect.getLastMap(), myApp);
		transitioning = false;
		
		items.clear();
		
		for (int i = 0; i < mapsPlayed.size; i++)
		{
			items.add("Favorite " + mapsPlayed.get(i).mname);
		}
		items.add("Play Last Chase Again");
		items.add("Character Select");
		items.add("Map Select");
		items.add("Main Menu");
		
		shouldDrawBackground = true;
		
		super.show();
	}
	
	/*
	 * Hides this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#hide()
	 */
	public void hide() 
	{
		clearMapsPlayed();
	}
	
	/*
	 * Clears the stored maps
	 */
	public void clearMapsPlayed()
	{
		mapsPlayed.clear();
		items.clear();
	}
	
	/*
	 * Reads in the file for favorited maps
	 */
	private void readFavorites()
	{
		String favoritesData = ChaseApp.fileHandler.readFile("favorites.bin");
		
		if (!favoritesData.equals(""))
		{
			String[] favoritesArray = favoritesData.split("\n");
			
			try
			{
				playerAccount = favoritesArray[0];
				
				for (int i = 1; i < favoritesArray.length; i++)
				{
					favoriteMapIds.add(Integer.parseInt(favoritesArray[i]));
				}
			}
			catch (RuntimeException re)
			{
				if (!ChaseApp.fileHandler.writeFile("favorites.bin", "")) // Erase the save file because it's corrupted
				{
					ChaseApp.overlay = new Overlay(myApp, this, "No free local memory");
					myApp.setScreen(ChaseApp.overlay);
				}
			}
		}
	}
	
	/*
	 * Favorites the previously played map
	 * 
	 * @param index					The index of the chosen map
	 */
	private void favoriteMap(int index)
	{
		if (mapsPlayed.size < index+1)
		{
			message = "You didn't play enough maps";
			return;
		}
		for (int i = 0; i < favoriteMapIds.size; i++)
		{
			if (favoriteMapIds.get(i) == mapsPlayed.get(index).mid)
			{
				message = "This map has already been favorited!";
				return;
			}
		}
		
		favoriteMapIds.add(mapsPlayed.get(index).mid);
		writeFavoritesFile();
		message = "Map has been favorited!";
	}
	
	/*
	 * Writes the favorites list to memory
	 */
	private void writeFavoritesFile()
	{
		String toSave = playerAccount+"\n";
		
		for (int i = 0; i < favoriteMapIds.size; i++)
		{
			toSave += (favoriteMapIds.get(i)+"\n");
		}
		
		if (!ChaseApp.fileHandler.writeFile("favorites.bin", toSave))
		{
			ChaseApp.overlay = new Overlay(myApp, this, "No free local memory to save favorite maps");
			myApp.setScreen(ChaseApp.overlay);
		}
	}
	
	// TODO Possibly remove this before open-sourcing the game
	public void setPlayerAccount(String newAccountName)
	{
		playerAccount = newAccountName;
	}
	
	public String getPlayerAccount()
	{
		return playerAccount;
	}
} // End class