/*
 * @(#)PauseMenu.java		0.2 14/3/3
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.overlays;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.percipient24.b2helpers.StringLayout;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.ChainGame;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.SoundManager;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.net.MapVO;
import com.percipient24.cgc.screens.CGCScreen;
import com.percipient24.cgc.screens.CharacterSelect;
import com.percipient24.cgc.screens.Options;
import com.percipient24.cgc.screens.Overlay;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;
import com.percipient24.tweens.TransitionAccessor;

/*
 * Contains the logic for the pause menu
 * 
 * @version 0.2 14/3/3
 * @author William Ziegler
 */
public class PauseMenu extends CGCOverlay
{
	private ChaseApp myApp;
	private ShapeRenderer shapes;
	private ControlAdapter pauseBoss;
	private Controller pauseController;
	private Array<ControlAdapter> possibleBosses;
	private CGCScreen owner;
	
	//Transition variables
	private TweenManager tManager;
	private Transition transition;
	private boolean transitioning;
	
	//Input variables
	private int selected = 0;
	private int settingsSelected = 0;
	private int framesHeldVert = 0;
	private int framesHeldHoriz = 0;
	
	//Dimensions variables
	private float pauseMenuMargin = 50;
	private float pauseMenuWidth;
	private float pauseMenuHeight = Data.ACTUAL_HEIGHT / 2.0f;
	private float pauseSettingsMargin;
	private float pauseSettingsWidth = Data.ACTUAL_WIDTH / 1.25f;
	private float pauseSettingsHeight = Data.ACTUAL_HEIGHT / 1.5f;
	private float pauseConfirmMargin = 75;
	private float pauseConfirmWidth;
	private float pauseConfirmHeight = Data.ACTUAL_HEIGHT / 4;
	private float resizeScaleX;
	private float resizeScaleY;
	private float resizeFontScale;
	
	//Selection arrays
	private String items[] = new String[6];
	
	private String settingsItems[] = new String[8];
	private String settingsMessages[] = new String[8];
	private String confirmMessages[] = new String[4];
	
	//Settings window variables
	private boolean showSettings;
	private Sprite volumeBar;
	private Sprite[] selectors = new Sprite[3];
	private float[] displayVolumeSettings = new float[3];
	private int[] volumePercentages = new int[3];
	private boolean statTrackingOption;
	private boolean symbolOption;
	private boolean parallaxOption;
	private boolean isOuya;
	
	//Confirm window variables
	private boolean showConfirm;
	private boolean confirmExit;
	private String confirmWindowMessage = "";

	private StringLayout layout;
	
	/*
	 * Creates a new PauseMenu object
	 * 
	 * @param newBatch				The SpriteBatch the game is using
	 * @param newGame				The ChainGame object calling this constructor
	 * @param app					The ChaseApp object the game is using
	 * @param owner					The CGCScreen which created this PauseMenu
	 */
	public PauseMenu(SpriteBatch newBatch, CGCWorld newGame, ChaseApp app, CGCScreen owner)
	{
		super(newBatch);
		myApp = app;
		
		this.owner = owner;
		isOuya = (ChaseApp.os == "OUYA");

		layout = new StringLayout("", ChaseApp.menuFont);
		
		tManager = ChaseApp.tManager;
		
		shapes = myApp.getShapes();
		selected = 0;
		
		items[0] = "Resume Game";
		items[1] = "Settings";
		items[2] = "Restart Chase";
		items[3] = "Select Characters";
		items[4] = "Select Map";
		items[5] = "Main Menu";
		
		settingsItems[0] = "Master Volume";
		settingsItems[1] = "Music Volume";
		settingsItems[2] = "Effects Volume";
		settingsItems[3] = "Connected Sensor Symbols";
		settingsItems[4] = "Parallax Graphics";
		settingsItems[5] = "Accept Changes";
		settingsItems[6] = "Restore Defaults";
		settingsItems[7] = "Back";
		
		settingsMessages[0] = "Adjust overall volume of the game";
		settingsMessages[1] = "Adjust the volume of the music";
		settingsMessages[2] = "Adjust the volume of sound effects";
		settingsMessages[3] = "Draw matching symbols on\nconnected Sensors";
		settingsMessages[4] = "Give most 2D graphics a 3D look\n";
		settingsMessages[5] = "Apply the current settings to the\ngame";
		settingsMessages[6] = "Revert these settings back to their\ndefault values";
		settingsMessages[7] = "Return to the pause menu without\napplying changes";
		
		confirmMessages[0] = "Restart this chase?";
		confirmMessages[1] = "Return to character select?";
		confirmMessages[2] = "Return to map select?";
		confirmMessages[3] = "Return to the main menu?";
		
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
		
		layout.updateText(items[3]);
		pauseMenuWidth = layout.getLayout().width + pauseMenuMargin;
		
		volumeBar = ChaseApp.atlas.createSprite("sliderbar");
		
		volumeBar.setOrigin(0.0f, 0.0f);
		
		layout.updateText(settingsItems[2]);
		volumeBar.setX(Data.ACTUAL_WIDTH / 2 - pauseSettingsWidth / 2
				+ layout.getLayout().width / 2 + Math.min(300, Data.ACTUAL_WIDTH / 3));
		
		initializeSettings();
	}
	
	/*
	 * Handles command inputs for the pause menu
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#handleInput()
	 */
	public void handleInput()
	{
		if (!transitioning)
		{
			ControlAdapter keyboardLeft = myApp.getInput().getKeyboardLeft();
			ControlAdapter keyboardRight = myApp.getInput().getKeyboardRight();
			
			if (pauseBoss == null)
			{	
				for (int i = 0; i < possibleBosses.size; i++)
				{
					if (possibleBosses.get(i).justPressed(ControlType.PAUSE) || possibleBosses.get(i).getOuyaPause())
					{
						pauseBoss = possibleBosses.get(i);
						for (ControlAdapter ca: possibleBosses)
						{
							ca.setOuyaPause(false);
							ca.changeControlState(ControlType.PAUSE, false);
						}
						CGCWorld.resumeGame();
					}
					else if (possibleBosses.get(i).anyPauseMenuInput())
					{
						pauseBoss = possibleBosses.get(i);
						break;
					}
				}
			}
			
			if (pauseBoss != null)
			{
				if (pauseBoss.justPressed(ControlType.PAUSE) || pauseBoss.getOuyaPause())
				{
					pauseBoss.setOuyaPause(false);
					pauseBoss.changeControlState(ControlType.PAUSE, false);
					
					CGCWorld.resumeGame();
				}
				
				if (showConfirm)
				{
					handleConfirmInput(keyboardLeft, keyboardRight);
				}
				else if (showSettings)
				{
					handleSettingsInput(keyboardLeft, keyboardRight);
				}
				else
				{
					if (pauseBoss.justPressed(ControlType.SELECT) || keyboardLeft.justPressed(ControlType.SELECT)
							|| keyboardRight.justPressed(ControlType.SELECT))
					{
						pauseBoss.changeControlState(ControlType.SELECT, false);
						keyboardLeft.changeControlState(ControlType.SELECT, false);
						keyboardRight.changeControlState(ControlType.SELECT, false);
						switch (selected)
						{
							case 0:
								CGCWorld.resumeGame();
								break;
							case 1:
								initializeSettings();
								showSettings = true;
								break;
							case 2:
							case 3:
							case 4:
							case 5:
								confirmWindowMessage = confirmMessages[selected - 2];
								ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN * resizeFontScale);
								layout.updateText(confirmWindowMessage);
								pauseConfirmWidth = layout.getLayout().width
										+ pauseConfirmMargin;
								confirmExit = false;
								showConfirm = true;
								break;
							default:
								break;
						}
					}
					
					if (pauseBoss.justPressed(ControlType.BACK) || keyboardLeft.justPressed(ControlType.BACK)
							|| keyboardRight.justPressed(ControlType.BACK))
					{
						pauseBoss.changeControlState(ControlType.BACK, false);
						keyboardLeft.changeControlState(ControlType.BACK, false);
						keyboardRight.changeControlState(ControlType.BACK, false);
						CGCWorld.resumeGame();
					}
					
					if (pauseBoss.isPressed(ControlType.MENU_UP) || keyboardLeft.isPressed(ControlType.MENU_UP)
							|| keyboardRight.isPressed(ControlType.MENU_UP))
					{
						framesHeldVert++;
						if (framesHeldVert == 1)
						{
							selected = (selected-1+items.length)%items.length;
						}
						else if (framesHeldVert > 90 && framesHeldVert % 5 == 0)
						{
							selected = (selected-1+items.length)%items.length;
						}
						else if (framesHeldVert > 30 && framesHeldVert % 10 == 0)
						{
							selected = (selected-1+items.length)%items.length;
						}
					}
					else if (pauseBoss.isPressed(ControlType.MENU_DOWN) || keyboardLeft.isPressed(ControlType.MENU_DOWN)
							|| keyboardRight.isPressed(ControlType.MENU_DOWN))
					{
						framesHeldVert++;
						if (framesHeldVert == 1)
						{
							selected = (selected+1+items.length)%items.length;
						}
						else if (framesHeldVert > 90 && framesHeldVert % 5 == 0)
						{
							selected = (selected+1+items.length)%items.length;
						}
						else if (framesHeldVert > 30 && framesHeldVert % 10 == 0)
						{
							selected = (selected+1+items.length)%items.length;
						}
					}
					else
					{
						framesHeldVert = 0;
					}
				}
			}
		}
	}
	
	/*
	 * Handles command inputs for the confirm pop-up
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#handleInput()
	 */
	public void handleConfirmInput(ControlAdapter keyboardLeft, ControlAdapter keyboardRight)
	{
		if (pauseBoss.justPressed(ControlType.SELECT) || keyboardLeft.justPressed(ControlType.SELECT)
				|| keyboardRight.justPressed(ControlType.SELECT))
		{
			pauseBoss.changeControlState(ControlType.SELECT, false);
			pauseBoss.changeControlState(ControlType.DOWN_FACE, false);
			keyboardLeft.changeControlState(ControlType.SELECT, false);
			keyboardRight.changeControlState(ControlType.SELECT, false);
			
			if (confirmExit)
			{
				switch (selected)
				{
					case 2: // Restart chase
						TimerManager.clear();
						SoundManager.endSounds();
						CGCWorld.endGameStats(false);
						ChaseApp.favorite.clearMapsPlayed();
						startTransition();
						break;
					case 3: // Character select
						TimerManager.clear();
						SoundManager.endSounds();
						CGCWorld.endGameStats(false);
						CGCWorld.terminate();
						ChaseApp.favorite.clearMapsPlayed();
						myApp.getInput().setBoss(pauseBoss);
						ChaseApp.characterSelect.setForgetPlayers(false);
						CharacterSelect.tutorial = ((CGCWorld)owner).isTutorial();
						myApp.setScreen(ChaseApp.characterSelect);
						break;
					case 4: // Map select
						TimerManager.clear();
						SoundManager.endSounds();				
						CGCWorld.endGameStats(false);
						CGCWorld.terminate();
						ChaseApp.favorite.clearMapsPlayed();
						myApp.getInput().setBoss(pauseBoss);
						myApp.setScreen(ChaseApp.mapSelect);
						break;
					case 5: // Main menu
						TimerManager.clear();
						SoundManager.endSounds();
						CGCWorld.endGameStats(false);
						CGCWorld.terminate();
						ChaseApp.favorite.clearMapsPlayed();
						myApp.getInput().setBoss(pauseBoss);
						myApp.setScreen(ChaseApp.mainMenu);
						break;
					default:
						break;
				}
			}
			else
			{
				showConfirm = false;
			}
		}
		
		if (pauseBoss.justPressed(ControlType.BACK) || keyboardLeft.justPressed(ControlType.BACK)
				|| keyboardRight.justPressed(ControlType.BACK))
		{
			pauseBoss.changeControlState(ControlType.BACK, false);
			keyboardLeft.changeControlState(ControlType.BACK, false);
			keyboardRight.changeControlState(ControlType.BACK, false);
			confirmExit = false;
			showConfirm = false;
		}
		
		if (pauseBoss.isPressed(ControlType.MENU_LEFT) || pauseBoss.isPressed(ControlType.MENU_RIGHT)
				|| keyboardLeft.isPressed(ControlType.MENU_LEFT) || keyboardLeft.isPressed(ControlType.MENU_RIGHT)
				|| keyboardRight.isPressed(ControlType.MENU_LEFT) || keyboardRight.isPressed(ControlType.MENU_RIGHT))
		{
			framesHeldHoriz++;
			if (framesHeldHoriz == 1)
			{
				confirmExit = !confirmExit;
			}
		}
		else
		{
			framesHeldHoriz = 0;
		}
	}
	
	/*
	 * Handles command inputs for the pause settings menu
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#handleInput()
	 */
	public void handleSettingsInput(ControlAdapter keyboardLeft, ControlAdapter keyboardRight)
	{
		if (pauseBoss.justPressed(ControlType.SELECT) || keyboardLeft.justPressed(ControlType.SELECT)
				|| keyboardRight.justPressed(ControlType.SELECT))
		{
			pauseBoss.changeControlState(ControlType.SELECT, false);
			keyboardLeft.changeControlState(ControlType.SELECT, false);
			keyboardRight.changeControlState(ControlType.SELECT, false);
			
			switch (settingsSelected)
			{
				case 3:
					symbolOption = !symbolOption;
					break;
				case 4:
					parallaxOption = !parallaxOption;
					break;
				case 5:
					for (int i = 0; i < displayVolumeSettings.length; i++)
					{
						Options.storedVolumeSettings[i] = displayVolumeSettings[i];
					}
					Options.storedTrackingOption = statTrackingOption;
					Options.storedSymbolOption = symbolOption;
					Options.storedParallaxOption = parallaxOption;
					savePreferences();
					settingsSelected = 0;
					showSettings = false;
					break;
				case 6:
					restoreDefault();
					savePreferences();
					break;
				case 7: 
					settingsSelected = 0;
					showSettings = false;
					break;
				default:
					break;
			}
		}
		
		if (pauseBoss.justPressed(ControlType.BACK) || keyboardLeft.justPressed(ControlType.BACK)
				|| keyboardRight.justPressed(ControlType.BACK))
		{
			pauseBoss.changeControlState(ControlType.BACK, false);
			keyboardLeft.changeControlState(ControlType.BACK, false);
			keyboardRight.changeControlState(ControlType.BACK, false);
			settingsSelected = 0;
			showSettings = false;
		}
		
		if (pauseBoss.isPressed(ControlType.MENU_UP) || keyboardLeft.isPressed(ControlType.MENU_UP)
				|| keyboardRight.isPressed(ControlType.MENU_UP))
		{
			framesHeldVert++;
			if (framesHeldVert == 1)
			{
				settingsSelected = (settingsSelected-1+settingsItems.length)%settingsItems.length;
			}
			else if (framesHeldVert > 90 && framesHeldVert % 5 == 0)
			{
				settingsSelected = (settingsSelected-1+settingsItems.length)%settingsItems.length;
			}
			else if (framesHeldVert > 30 && framesHeldVert % 10 == 0)
			{
				settingsSelected = (settingsSelected-1+settingsItems.length)%settingsItems.length;
			}
			
			if (settingsSelected == 4 && isOuya)
			{
				settingsSelected--;
			}
		}
		else if (pauseBoss.isPressed(ControlType.MENU_DOWN) || keyboardLeft.isPressed(ControlType.MENU_DOWN)
				|| keyboardRight.isPressed(ControlType.MENU_DOWN))
		{
			framesHeldVert++;
			if (framesHeldVert == 1)
			{
				settingsSelected = (settingsSelected+1+settingsItems.length)%settingsItems.length;
			}
			else if (framesHeldVert > 90 && framesHeldVert % 5 == 0)
			{
				settingsSelected = (settingsSelected+1+settingsItems.length)%settingsItems.length;
			}
			else if (framesHeldVert > 30 && framesHeldVert % 10 == 0)
			{
				settingsSelected = (settingsSelected+1+settingsItems.length)%settingsItems.length;
			}
			
			if (settingsSelected == 4 && isOuya)
			{
				settingsSelected++;
			}
		}
		else
		{
			framesHeldVert = 0;
		}
		
		if (settingsSelected < 3)
		{
			if (pauseBoss.isPressed(ControlType.MENU_LEFT) || keyboardLeft.isPressed(ControlType.MENU_LEFT)
					|| keyboardRight.isPressed(ControlType.MENU_LEFT))
			{
				framesHeldHoriz++;
				if (framesHeldHoriz == 1)
				{
					displayVolumeSettings[settingsSelected] -= .05f;
				}
				else if (framesHeldHoriz > 90 && framesHeldHoriz % 5 == 0)
				{
					displayVolumeSettings[settingsSelected] -= .05f;
				}
				else if (framesHeldHoriz > 30 && framesHeldHoriz % 10 == 0)
				{
					displayVolumeSettings[settingsSelected] -= .05f;
				}
				
				if (displayVolumeSettings[settingsSelected] < 0.0f)
				{
					displayVolumeSettings[settingsSelected] = 0.0f;
				}
			}
			else if (pauseBoss.isPressed(ControlType.MENU_RIGHT) || keyboardLeft.isPressed(ControlType.MENU_RIGHT)
					|| keyboardRight.isPressed(ControlType.MENU_RIGHT))
			{
				framesHeldHoriz++;
				if (framesHeldHoriz == 1)
				{
					displayVolumeSettings[settingsSelected] += .05f;
				}
				else if (framesHeldHoriz > 90 && framesHeldHoriz % 5 == 0)
				{
					displayVolumeSettings[settingsSelected] += .05f;
				}
				else if (framesHeldHoriz > 30 && framesHeldHoriz % 10 == 0)
				{
					displayVolumeSettings[settingsSelected] += .05f;
				}
				
				if (displayVolumeSettings[settingsSelected] > 1.0f)
				{
					displayVolumeSettings[settingsSelected] = 1.0f;
				}
			}
			else
			{
				framesHeldHoriz = 0;
			}
			
			volumePercentages[settingsSelected] = Math.round(displayVolumeSettings[settingsSelected] * 100);
		}
		else if (settingsSelected == 3)
		{
			if (pauseBoss.isPressed(ControlType.MENU_LEFT) || pauseBoss.isPressed(ControlType.MENU_RIGHT)
					|| keyboardLeft.isPressed(ControlType.MENU_LEFT) || keyboardLeft.isPressed(ControlType.MENU_RIGHT)
					|| keyboardRight.isPressed(ControlType.MENU_LEFT) || keyboardRight.isPressed(ControlType.MENU_RIGHT))
			{
				framesHeldHoriz++;
				
				if (framesHeldHoriz == 1)
				{
					symbolOption = !symbolOption;
				}
			}
			else
			{
				framesHeldHoriz = 0;
			}
		}
		else if (settingsSelected == 4)
		{
			if (pauseBoss.isPressed(ControlType.MENU_LEFT) || pauseBoss.isPressed(ControlType.MENU_RIGHT)
					|| keyboardLeft.isPressed(ControlType.MENU_LEFT) || keyboardLeft.isPressed(ControlType.MENU_RIGHT)
					|| keyboardRight.isPressed(ControlType.MENU_LEFT) || keyboardRight.isPressed(ControlType.MENU_RIGHT))
			{
				framesHeldHoriz++;
				
				if (framesHeldHoriz == 1)
				{
					parallaxOption = !parallaxOption;
				}
			}
			else
			{
				framesHeldHoriz = 0;
			}
		}
	}
	
	/*
	 * Renders the pause menu
	 * 
	 * @param delta					Seconds elapsed since last frame
	 */
	public void render(float delta)
	{
		if (showElement)
		{
			shapes.begin(ShapeType.Filled);
			shapes.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			shapes.rect(Data.ACTUAL_WIDTH / 2 - pauseMenuWidth / 2, 
					Data.ACTUAL_HEIGHT / 2 - pauseMenuHeight / 2,
					pauseMenuWidth, pauseMenuHeight);
			
			shapes.end();
			shapes.begin(ShapeType.Line);
			shapes.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			shapes.rect(Data.ACTUAL_WIDTH / 2 - pauseMenuWidth / 2, 
					Data.ACTUAL_HEIGHT / 2 - pauseMenuHeight / 2,
					pauseMenuWidth, pauseMenuHeight);
			shapes.end();
			sBatch.begin();
			
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE * resizeFontScale);
			
			for(int i = 0; i < items.length; i++)
			{
				if(i == selected) 
				{ 
					ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN * resizeFontScale); 
					ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
				}
				else
				{
					ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE * resizeFontScale); 
					ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
				}
				
				layout.updateText(items[i]);
				ChaseApp.menuFont.draw(
					sBatch,
					layout.getLayout(),
					Data.ACTUAL_WIDTH / 2 - layout.getLayout().width / 2,
					(Data.ACTUAL_HEIGHT) / 2 
						- pauseMenuHeight / 2
						+ pauseMenuHeight
						- (pauseMenuHeight / (items.length) 
						* (i + .5f)) + ChaseApp.menuFont.getLineHeight()/2f);
			}
			ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			
			if (showConfirm)
			{
				renderConfirm();
			}
			else if (showSettings)
			{
				renderSettings();
			}
			
			sBatch.end();
			if (transition != null && transition.getShow())
			{
				tManager.update(delta);
				transition.render(delta);
				sBatch.setProjectionMatrix(ChaseApp.menuCam.combined);
			}
		}
	}
	
	/*
	 * Renders the confirmation windows for the pause menu
	 */
	private void renderConfirm()
	{
		sBatch.end();
		shapes.begin(ShapeType.Filled);
		shapes.setColor(0.0f, 0.0f, 0.0f, 1.0f);
		shapes.rect(Data.ACTUAL_WIDTH / 2 - pauseConfirmWidth / 2, 
				(Data.ACTUAL_HEIGHT / Data.ASPECT_RATIO) / 2 + pauseConfirmHeight / 2,
				pauseConfirmWidth, pauseConfirmHeight);
		shapes.end();
		shapes.begin(ShapeType.Line);
		shapes.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		shapes.rect(Data.ACTUAL_WIDTH / 2 - pauseConfirmWidth / 2, 
				(Data.ACTUAL_HEIGHT / Data.ASPECT_RATIO) / 2 + pauseConfirmHeight / 2,
				pauseConfirmWidth, pauseConfirmHeight);
		shapes.end();
		sBatch.begin();
		
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN * resizeFontScale);
		layout.updateText(confirmWindowMessage);
		ChaseApp.menuFont.draw(
			sBatch,
			layout.getLayout(),
			Data.ACTUAL_WIDTH / 2 - layout.getLayout().width / 2, 
			((Data.ACTUAL_HEIGHT / Data.ASPECT_RATIO) / 2 + pauseConfirmHeight * 1.25f)
				+ ChaseApp.menuFont.getLineHeight()/2f);
		
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE * resizeFontScale);
		ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		
		if (confirmExit)
		{
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN * resizeFontScale);
			ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
		}

		layout.updateText("Yes");
		ChaseApp.menuFont.draw(
			sBatch,
			layout.getLayout(),
			Data.ACTUAL_WIDTH / 2
				- layout.getLayout().width / 2 - 50 * resizeScaleX, 
			(Data.ACTUAL_HEIGHT / Data.ASPECT_RATIO) / 2 + pauseConfirmHeight * .75f 
				+ ChaseApp.menuFont.getLineHeight()/2f);

		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE * resizeFontScale);
		ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		if (!confirmExit)
		{
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN * resizeFontScale);
			ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
		}
		layout.updateText("No");
		ChaseApp.menuFont.draw(
			sBatch,
			layout.getLayout(),
			Data.ACTUAL_WIDTH / 2
				- layout.getLayout().width / 2 + 50 * resizeScaleX, 
			(Data.ACTUAL_HEIGHT / Data.ASPECT_RATIO) / 2 + pauseConfirmHeight * .75f 
				+ ChaseApp.menuFont.getLineHeight()/2f);

		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE * resizeFontScale);
		ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	/*
	 * Renders the settings window for the pause menu
	 */
	private void renderSettings()
	{
		sBatch.end();
		shapes.begin(ShapeType.Filled);
		shapes.setColor(0.0f, 0.0f, 0.0f, 1.0f);
		shapes.rect(Data.ACTUAL_WIDTH / 2 - pauseSettingsWidth / 2, 
				Data.ACTUAL_HEIGHT / 2 - pauseSettingsHeight / 2,
				pauseSettingsWidth, pauseSettingsHeight);
		shapes.end();
		shapes.begin(ShapeType.Line);
		shapes.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		shapes.rect(Data.ACTUAL_WIDTH / 2 - pauseSettingsWidth / 2, 
				Data.ACTUAL_HEIGHT / 2 - pauseSettingsHeight / 2,
				pauseSettingsWidth, pauseSettingsHeight);
		shapes.end();
		sBatch.begin();
		
		int effectiveLength = settingsItems.length;
		
		if (isOuya)
		{
			effectiveLength -= 1;
		}
		
		for(int i = 0; i < settingsItems.length; i++)
		{
			if(i == settingsSelected) 
			{ 
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN * resizeFontScale);
				ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
			}
			else
			{
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE * resizeFontScale);
				ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
			}
			
			if (i < displayVolumeSettings.length)
			{	
				volumeBar.setY((Data.ACTUAL_HEIGHT / 2 + pauseSettingsHeight / 2) 
							- ((pauseSettingsHeight / (effectiveLength)) * (i + .5f))
							- volumeBar.getRegionHeight() / 2);
			
				if (i == settingsSelected)
				{
					volumeBar.setColor(ChaseApp.selectedOrange);
				}
				volumeBar.draw(sBatch);
				volumeBar.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				
				selectors[i].setX(volumeBar.getX() + displayVolumeSettings[i] 
						* volumeBar.getWidth() * resizeScaleX - selectors[i].getWidth() / 2);
				selectors[i].setY(volumeBar.getY()
						+ volumeBar.getHeight() / 2 * resizeScaleY
						- (selectors[i].getHeight()) / 2);
				selectors[i].draw(sBatch);
				
				ChaseApp.menuFont.draw(sBatch, volumePercentages[i]+"%", volumeBar.getX() 
						+ volumeBar.getWidth() * volumeBar.getScaleX() + 30, 
						volumeBar.getY() + volumeBar.getRegionHeight() / 1.8f
						+ (ChaseApp.menuFont.getLineHeight()/2f) * ChaseApp.menuFont.getScaleY());
			}
			sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			
            if (i < 4)
            {
            	layout.updateText(settingsItems[i]);
                ChaseApp.menuFont.draw(
                	sBatch,
                	layout.getLayout(),
                	Data.ACTUAL_WIDTH / 2 
                        - pauseSettingsWidth / 2
                        - layout.getLayout().width / 2
                        + pauseSettingsMargin, 
                    (Data.ACTUAL_HEIGHT / 2 + pauseSettingsHeight / 2) 
                        - ((pauseSettingsHeight / (effectiveLength)) 
                        * (i + .5f)) + (ChaseApp.menuFont.getLineHeight()/2f) * ChaseApp.menuFont.getScaleY());
            }
            else
            {
                if (isOuya && i != 4)
                {
            		layout.updateText(settingsItems[i]);
                    ChaseApp.menuFont.draw(
                    	sBatch,
                    	layout.getLayout(),
                    	Data.ACTUAL_WIDTH / 2 
                            - pauseSettingsWidth / 2
                            - layout.getLayout().width / 2
                            + pauseSettingsMargin, 
                        (Data.ACTUAL_HEIGHT / 2 + pauseSettingsHeight / 2) 
                            - ((pauseSettingsHeight / (effectiveLength)) 
                            * (i - .5f)) + (ChaseApp.menuFont.getLineHeight()/2f) * ChaseApp.menuFont.getScaleY());
                }
                else if (!isOuya)
                {
            		layout.updateText(settingsItems[i]);
                    ChaseApp.menuFont.draw(
                    	sBatch,
                    	layout.getLayout(),
                    	Data.ACTUAL_WIDTH / 2 
                            - pauseSettingsWidth / 2
                            - layout.getLayout().width / 2
                            + pauseSettingsMargin, 
                        (Data.ACTUAL_HEIGHT / 2 + pauseSettingsHeight / 2) 
                            - ((pauseSettingsHeight / (effectiveLength)) 
                            * (i + .5f)) + (ChaseApp.menuFont.getLineHeight()/2f) * ChaseApp.menuFont.getScaleY());
                }
            }
		}
		ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		//Sensor Symbols on/off visuals
		if (settingsSelected == 3)
		{
			ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
		}
		if (symbolOption)
		{
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN * resizeFontScale);
			if (settingsSelected == 3)
			{
				ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
			}
			else
			{
				ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		ChaseApp.menuFont.draw(sBatch, "On", volumeBar.getX() 
				+ volumeBar.getWidth() * volumeBar.getScaleX() - 30, 
				(Data.ACTUAL_HEIGHT / 2 + pauseSettingsHeight / 2) 
				- ((pauseSettingsHeight / (effectiveLength)) 
				* 3.5f) + (ChaseApp.menuFont.getLineHeight()/2f) * ChaseApp.menuFont.getScaleY());
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE * resizeFontScale);
		if (settingsSelected == 3)
		{
			ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
		}
		else
		{
			ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		}
		if (!symbolOption)
		{
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN * resizeFontScale);
			if (settingsSelected == 3)
			{
				ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
			}
			else
			{
				ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		ChaseApp.menuFont.draw(sBatch, "Off", volumeBar.getX() 
				+ volumeBar.getWidth() * volumeBar.getScaleX() + 45, 
				(Data.ACTUAL_HEIGHT / 2 + pauseSettingsHeight / 2) 
				- ((pauseSettingsHeight / (effectiveLength)) 
				* 3.5f) + (ChaseApp.menuFont.getLineHeight()/2f) * ChaseApp.menuFont.getScaleY());
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE * resizeFontScale);
		ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		
		if (!isOuya)
		{
			//Parallax Graphics on/off visuals
			if (settingsSelected == 4)
			{
				ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
			}
			if (parallaxOption)
			{
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN * resizeFontScale);
				if (settingsSelected == 4)
				{
					ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
				}
				else
				{
					ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}
			ChaseApp.menuFont.draw(sBatch, "On", volumeBar.getX() 
					+ volumeBar.getWidth() * volumeBar.getScaleX() - 30, 
					(Data.ACTUAL_HEIGHT / 2 + pauseSettingsHeight / 2) 
					- ((pauseSettingsHeight / (effectiveLength)) 
					* 4.5f) + (ChaseApp.menuFont.getLineHeight()/2f) * ChaseApp.menuFont.getScaleY());
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE * resizeFontScale);
			if (settingsSelected == 4)
			{
				ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
			}
			else
			{
				ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
			}
			if (!parallaxOption)
			{
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN * resizeFontScale);
				if (settingsSelected == 4)
				{
					ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
				}
				else
				{
					ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}
			ChaseApp.menuFont.draw(sBatch, "Off", volumeBar.getX() 
					+ volumeBar.getWidth() * volumeBar.getScaleX() + 45, 
					(Data.ACTUAL_HEIGHT / 2 + pauseSettingsHeight / 2) 
					- ((pauseSettingsHeight / (effectiveLength)) 
					* 4.5f) + (ChaseApp.menuFont.getLineHeight()/2f) * ChaseApp.menuFont.getScaleY());
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE * resizeFontScale);
			ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		}
		
		ChaseApp.menuFont.draw(sBatch, settingsMessages[settingsSelected], 
				Data.ACTUAL_WIDTH / 2, Data.ACTUAL_HEIGHT / 3);
	}
	
	/*
	 * Brings a Transition in from off-screen to bring the players into the game
	 */
	private void startTransition()
	{
		if (owner instanceof CGCWorld && ((CGCWorld)owner).isTutorial())
		{
			transition = new Transition(sBatch, "Now entering Tutorial level", "Created by me,", "your loving sheriff", myApp);
			transition.setXPosition(Data.ACTUAL_WIDTH * 1.5f);
			transition.setShow(true);
			transitioning = true;
			
			Timeline.createSequence()
			.push
			(Tween.to(transition, TransitionAccessor.TRANSLATE_X, 1.0f).ease(Cubic.OUT).target(0))
			.pushPause(2.0f)
			.setCallback(changeToTutorialScreen)
			.start(tManager);
		}
		else
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
			pauseBoss.getCurrent().resetData();
			myApp.setScreen(new ChainGame(myApp, ChaseApp.characterSelect.getNumPlayers(), 
					ChaseApp.mapSelect.getLastMap(), ChaseApp.mapSelect.getMapCache(), transition, false));
		}
	};
	
	/*
	 * Switches the game screen to the tutorial map once the above Timeline is over
	 */
	private TweenCallback changeToTutorialScreen = new TweenCallback()
	{
		@Override
		public void onEvent(int type, BaseTween<?> source)
		{
			transitioning = false;
			pauseBoss.getCurrent().resetData();
			MapVO tutorialVO = new MapVO(); // Create a MapVO based on the Tutorial Map
			tutorialVO.mdata = ";;0,6,2:0,12,4:0,3,8;0,12,0:0,9,1:0,1,3:0,14,3:0,4,4:0,9,5:0,14,7:0,1,8:0,4,8:0,10,8:0,14,10;0,6,0:0,13,0:0,0,1:0,12,1:0,2,2:0,3,2:0,3,3:0,4,3:0,9,4:0,12,4:0,2,5:0,3,5:0,7,5:0,8,5:0,12,5:0,1,6:0,2,6:0,8,6:0,13,6:0,15,6:0,0,7:0,1,7:0,6,7:0,12,7:0,13,7:0,0,8:0,1,8:0,6,8:0,12,8:0,14,8:0,16,8:0,0,9:0,1,9:0,14,9:0,15,9:0,16,9:0,0,10:0,1,10:0,9,10:0,10,10:0,11,10:0,12,10:0,13,10:0,14,10:0,15,10:0,16,10:0,17,10;0,0,0:0,1,0:0,2,0:0,3,0:0,4,0:0,7,0:0,8,0:0,9,0:0,10,0:0,11,0:0,12,0:0,13,0:0,14,0:0,15,0:0,16,0:0,17,0:0,0,1:0,1,1:0,2,1:0,3,1:0,4,1:0,7,1:0,8,1:0,9,1:0,10,1:0,11,1:0,12,1:0,13,1:0,14,1:0,15,1:0,16,1:0,17,1:0,2,2:0,3,2:0,4,2:0,5,2:0,8,2:0,9,2:0,10,2:0,11,2:0,12,2:0,13,2:0,14,2:0,15,2:0,16,2:0,17,2:0,1,3:0,2,3:0,3,3:0,4,3:0,5,3:0,8,3:0,9,3:0,10,3:0,11,3:0,13,3:0,14,3:0,15,3:0,16,3:0,17,3:0,4,4:0,5,4:0,8,4:0,9,4:0,10,4:0,14,4:0,15,4:0,16,4:0,17,4:0,4,5:0,5,5:0,8,5:0,14,5:0,15,5:0,16,5:0,17,5:0,4,6:0,5,6:0,15,6:0,16,6:0,17,6:0,5,7:0,6,7:0,7,7:0,8,7:0,10,7:0,11,7:0,14,7:0,15,7:0,16,7:0,17,7:0,5,8:0,6,8:0,7,8:0,8,8:0,9,8:0,10,8:0,14,8:0,15,8:0,16,8:0,17,8:0,6,9:0,7,9:0,8,9:0,9,9:0,10,9:0,14,9:0,15,9:0,16,9:0,17,9:0,8,10:0,9,10:0,10,10:0,14,10:0,15,10:0,16,10:0,17,10;0,14,0:0,15,0:0,16,0:0,17,0:0,14,1:0,15,1:0,16,1:0,17,1:0,15,2:0,16,2:0,17,2;20,0,0,7,4,3,7:20,1,0,7,4,1,7:20,2,0,7,4,1,7:20,3,0,7,4,1,7:20,4,0,7,4,1,7:20,5,0,7,4,1,7:20,6,0,7,4,1,7:20,7,0,7,4,1,7:20,8,0,7,4,1,7:20,9,0,7,4,1,7:20,10,0,7,4,1,7:20,11,0,7,4,1,7:20,12,0,7,4,1,7:20,13,0,7,4,1,7:20,14,0,7,4,1,7:20,15,0,7,4,1,7:20,16,0,7,4,1,7:20,17,0,7,6,1,7:20,0,1,7,7,7,7:20,1,1,7,7,7,7:20,2,1,7,7,7,7:20,3,1,7,7,7,7:20,4,1,7,7,7,7:20,5,1,7,7,7,7:20,6,1,7,7,7,7:20,7,1,7,7,7,7:20,8,1,7,7,7,7:20,9,1,7,7,7,7:20,10,1,7,7,7,7:20,11,1,7,7,7,7:20,12,1,7,7,7,7:20,13,1,7,7,7,7:20,14,1,7,7,7,7:20,15,1,7,7,7,7:20,16,1,7,7,7,7:20,17,1,7,7,7,7:20,0,2,7,7,7,7:20,1,2,7,7,7,7:20,2,2,7,7,7,7:20,3,2,7,7,7,7:20,4,2,7,7,7,7:20,5,2,7,7,7,7:20,6,2,7,7,7,7:20,7,2,7,7,7,7:20,8,2,7,7,7,7:20,9,2,7,7,7,7:20,10,2,7,7,7,7:20,11,2,7,7,7,7:20,12,2,7,7,7,7:20,13,2,7,7,7,7:20,14,2,7,7,7,7:20,15,2,7,7,7,7:20,16,2,7,7,7,7:20,17,2,7,7,7,7:20,0,3,1,7,7,6:20,1,3,1,7,7,4:20,2,3,1,7,7,4:20,3,3,1,7,7,4:20,4,3,1,7,7,4:20,5,3,1,7,7,4:20,6,3,1,7,7,4:20,7,3,1,7,7,4:20,8,3,1,7,7,4:20,9,3,1,7,7,4:20,10,3,1,7,7,4:20,11,3,1,7,7,4:20,12,3,1,7,7,4:20,13,3,1,7,7,4:20,14,3,1,7,7,4:20,15,3,1,7,7,4:20,16,3,1,7,7,4:20,17,3,3,7,7,4;21,0,0,7,4,3,7,0:21,1,0,7,4,1,7,0:21,2,0,7,4,1,7,0:21,3,0,7,4,1,7,0:21,4,0,7,4,1,7,0:21,5,0,7,4,1,7,0:21,6,0,7,4,1,7,0:21,7,0,7,4,1,7,0:21,8,0,7,4,1,7,0:21,9,0,7,4,1,7,0:21,10,0,7,4,1,7,0:21,11,0,7,4,1,7,0:21,12,0,7,4,1,7,0:21,13,0,7,4,1,7,0:21,14,0,7,4,1,7,0:21,15,0,7,4,1,7,0:21,16,0,7,4,1,7,0:21,17,0,7,6,1,7,0:21,0,1,7,7,7,7,0:21,1,1,7,7,7,7,0:21,2,1,7,7,7,7,0:21,3,1,7,7,7,7,0:21,4,1,7,7,7,7,0:21,5,1,7,7,7,7,0:21,6,1,7,7,7,7,0:21,7,1,7,7,7,7,0:21,8,1,7,7,7,7,0:21,9,1,7,7,7,7,0:21,10,1,7,7,7,7,0:21,11,1,7,7,7,7,0:21,12,1,7,7,7,7,0:21,13,1,7,7,7,7,0:21,14,1,7,7,7,7,0:21,15,1,7,7,7,7,0:21,16,1,7,7,7,7,0:21,17,1,7,7,7,7,0:21,0,2,7,7,7,7,0:21,1,2,7,7,7,7,0:21,2,2,7,7,7,7,0:21,3,2,7,7,7,7,0:21,4,2,7,7,7,7,0:21,5,2,7,7,7,7,0:21,6,2,7,7,7,7,0:21,7,2,7,7,7,7,0:21,8,2,7,7,7,7,0:21,9,2,7,7,7,7,0:21,10,2,7,7,7,7,0:21,11,2,7,7,7,7,0:21,12,2,7,7,7,7,0:21,13,2,7,7,7,7,0:21,14,2,7,7,7,7,0:21,15,2,7,7,7,7,0:21,16,2,7,7,7,7,0:21,17,2,7,7,7,7,0:21,0,3,1,7,7,6,0:21,1,3,1,7,7,4,0:21,2,3,1,7,7,4,0:21,3,3,1,7,7,4,0:21,4,3,1,7,7,4,0:21,5,3,1,7,7,4,0:21,6,3,1,7,7,4,0:21,7,3,1,7,7,4,0:21,8,3,1,7,7,4,0:21,9,3,1,7,7,4,0:21,10,3,1,7,7,4,0:21,11,3,1,7,7,4,0:21,12,3,1,7,7,4,0:21,13,3,1,7,7,4,0:21,14,3,1,7,7,4,0:21,15,3,1,7,7,4,0:21,16,3,1,7,7,4,0:21,17,3,3,7,7,4,0;21,0,0,7,4,3,7,1:21,1,0,7,4,1,7,1:21,2,0,7,4,1,7,1:21,3,0,7,4,1,7,1:21,4,0,7,4,1,7,1:21,5,0,7,4,1,7,1:21,6,0,7,4,1,7,1:21,7,0,7,4,1,7,1:21,8,0,7,4,1,7,1:21,9,0,7,4,1,7,1:21,10,0,7,4,1,7,1:21,11,0,7,4,1,7,1:21,12,0,7,4,1,7,1:21,13,0,7,4,1,7,1:21,14,0,7,4,1,7,1:21,15,0,7,4,1,7,1:21,16,0,7,4,1,7,1:21,17,0,7,6,1,7,1:21,0,1,7,7,7,7,1:21,1,1,7,7,7,7,1:21,2,1,7,7,7,7,1:21,3,1,7,7,7,7,1:21,4,1,7,7,7,7,1:21,5,1,7,7,7,7,1:21,6,1,7,7,7,7,1:21,7,1,7,7,7,7,1:21,8,1,7,7,7,7,1:21,9,1,7,7,7,7,1:21,10,1,7,7,7,7,1:21,11,1,7,7,7,7,1:21,12,1,7,7,7,7,1:21,13,1,7,7,7,7,1:21,14,1,7,7,7,7,1:21,15,1,7,7,7,7,1:21,16,1,7,7,7,7,1:21,17,1,7,7,7,7,1:21,0,2,7,7,7,7,1:21,1,2,7,7,7,7,1:21,2,2,7,7,7,7,1:21,3,2,7,7,7,7,1:21,4,2,7,7,7,7,1:21,5,2,7,7,7,7,1:21,6,2,7,7,7,7,1:21,7,2,7,7,7,7,1:21,8,2,7,7,7,7,1:21,9,2,7,7,7,7,1:21,10,2,7,7,7,7,1:21,11,2,7,7,7,7,1:21,12,2,7,7,7,7,1:21,13,2,7,7,7,7,1:21,14,2,7,7,7,7,1:21,15,2,7,7,7,7,1:21,16,2,7,7,7,7,1:21,17,2,7,7,7,7,1:21,0,3,1,7,7,6,1:21,1,3,1,7,7,4,1:21,2,3,1,7,7,4,1:21,3,3,1,7,7,4,1:21,4,3,1,7,7,4,1:21,5,3,1,7,7,4,1:21,6,3,1,7,7,4,1:21,7,3,1,7,7,4,1:21,8,3,1,7,7,4,1:21,9,3,1,7,7,4,1:21,10,3,1,7,7,4,1:21,11,3,1,7,7,4,1:21,12,3,1,7,7,4,1:21,13,3,1,7,7,4,1:21,14,3,1,7,7,4,1:21,15,3,1,7,7,4,1:21,16,3,1,7,7,4,1:21,17,3,3,7,7,4,1:20,5,9,7,4,0,3:20,6,9,7,4,1,7:20,7,9,4,0,1,7:20,9,9,7,4,0,1:20,10,9,7,4,1,7:20,11,9,6,0,1,7:20,4,10,1,6,0,0:20,5,10,3,7,5,4:20,6,10,7,7,7,5:20,7,10,6,1,7,7:20,9,10,7,7,4,3:20,10,10,5,7,7,7:20,11,10,1,5,7,6:20,12,10,0,0,3,4;0,0,0:0,1,0:0,2,0:0,3,0:0,4,0:0,5,0:21,6,0,7,7,6,1,3:21,7,0,7,5,7,7,3:21,8,0,7,6,3,7,3:21,9,0,7,7,5,7,3:21,10,0,4,3,7,7,3:0,11,0:0,12,0:0,13,0:0,14,0:0,15,0:0,16,0:0,17,0:0,0,1:0,1,1:0,2,1:0,3,1:0,4,1:0,5,1:21,6,1,7,7,4,3,3:21,7,1,5,7,7,7,3:21,8,1,3,7,7,6,3:21,9,1,7,7,7,5,3:21,10,1,6,1,7,7,3:0,11,1:0,12,1:0,13,1:0,14,1:0,15,1:0,16,1:0,17,1:20,4,2,3,4,0,0:20,5,2,7,6,1,5:20,6,2,7,7,5,7:20,7,2,4,3,7,7:20,9,2,7,7,6,1:20,10,2,7,5,7,7:20,11,2,5,4,3,7:20,12,2,0,0,1,6:20,5,3,1,7,6,0:20,6,3,1,7,7,4:20,7,3,0,1,7,4:20,9,3,1,7,4,0:20,10,3,1,7,7,4:20,11,3,0,3,7,4:20,12,9,7,4,0,3:20,13,9,7,4,1,7:20,14,9,7,4,1,7:22,14,9,1:20,15,9,7,4,1,7:20,16,9,6,0,1,7:20,11,10,1,6,0,0:20,12,10,3,7,5,4:20,13,10,7,7,7,5:20,14,10,7,7,7,7:22,14,10,5:20,15,10,5,7,7,7:20,16,10,1,5,7,6:20,17,10,3,6,3,4;0,0,0:0,1,0:0,2,0:0,3,0:0,4,0:0,5,0:0,6,0:0,7,0:0,8,0:0,9,0:0,10,0:0,11,0:0,12,0:21,13,0,7,7,6,1,3:21,14,0,7,7,7,7,3:22,14,0,5:21,15,0,4,3,7,7,3:0,16,0:0,17,0:0,0,1:0,1,1:0,2,1:0,3,1:0,4,1:0,5,1:0,6,1:0,7,1:0,8,1:0,9,1:0,10,1:0,11,1:0,12,1:21,13,1,7,7,4,3,3:21,14,1,7,7,7,7,3:22,14,1,5:21,15,1,6,1,7,7,3:0,16,1:0,17,1:20,11,2,3,4,0,0:20,12,2,7,6,1,5:20,13,2,7,7,5,7:20,14,2,7,7,7,7:22,14,2,5:20,15,2,7,5,7,7:20,16,2,5,4,3,7:20,17,2,3,6,1,6:20,12,3,1,7,6,0:20,13,3,1,7,7,4:20,14,3,1,7,7,4:22,14,3,4:20,15,3,1,7,7,4:20,16,3,0,3,7,4;12,8,8,0,1:0,0,10:0,1,10:0,2,10:0,3,10:0,4,10:0,5,10:0,6,10:0,7,10:11,8,10,0,1:0,9,10:0,10,10:0,11,10:0,12,10:0,13,10:0,14,10:0,15,10:0,16,10:0,17,10;0,0,10:0,1,10:0,2,10:0,3,10:0,4,10:0,5,10:0,6,10:0,7,10:0,10,10:0,11,10:0,12,10:0,13,10:0,14,10:0,15,10:0,16,10:0,17,10;0,7,0:0,10,0:0,7,1:0,10,1:0,7,2:12,8,2,1,2:12,9,2,1,2:0,10,2:0,7,3:0,10,3:0,7,4:0,10,4:0,7,5:0,8,5:11,9,5,0,2:0,10,5;0,0,8:0,1,8:0,2,8:0,3,8:0,4,8:0,5,8:0,6,8:10,8,8,1:0,10,8:0,11,8:0,12,8:0,13,8:0,14,8:0,15,8:0,16,8:0,17,8:0,6,9:10,8,9,5:0,10,9:0,6,10:10,8,10,5:0,10,10;0,6,0:10,8,0,5:0,10,0:0,6,1:10,8,1,5:0,10,1:0,6,2:12,7,2,1,4:10,8,2,5:12,9,2,2,3:0,10,2:0,6,3:10,8,3,5:0,10,3:0,6,4:10,8,4,5:0,10,4:0,6,5:11,7,5,0,3:10,8,5,14:11,9,5,0,4:0,10,5;0,0,0:0,1,0:0,2,0:0,3,0:0,4,0:0,5,0:0,12,0:0,13,0:0,14,0:0,15,0:0,16,0:0,17,0:0,0,1:0,1,1:0,2,1:0,3,1:0,4,1:14,8,1:14,9,1:0,13,1:0,14,1:0,15,1:0,16,1:0,17,1:0,0,2:0,1,2:0,2,2:0,3,2:0,4,2:0,8,2:0,9,2:0,13,2:0,14,2:0,15,2:0,16,2:0,17,2:0,0,3:0,1,3:0,2,3:0,3,3:0,7,3:0,8,3:0,9,3:0,10,3:0,14,3:0,15,3:0,16,3:0,17,3:0,0,4:0,1,4:0,2,4:0,3,4:0,7,4:0,8,4:0,9,4:0,10,4:0,14,4:0,15,4:0,16,4:0,17,4:0,0,5:0,1,5:0,2,5:0,6,5:0,7,5:0,8,5:0,9,5:0,10,5:0,11,5:0,15,5:0,16,5:0,17,5:0,0,6:0,1,6:0,2,6:0,6,6:0,7,6:0,8,6:0,9,6:0,10,6:0,11,6:0,15,6:0,16,6:0,17,6:0,0,7:0,1,7:0,5,7:0,6,7:0,7,7:0,8,7:0,9,7:0,10,7:0,11,7:0,12,7:0,16,7:0,17,7:0,0,8:0,1,8:0,5,8:0,6,8:0,7,8:0,8,8:0,9,8:0,10,8:0,11,8:0,12,8:0,16,8:0,17,8:0,0,9:0,4,9:0,5,9:0,6,9:0,7,9:0,8,9:0,9,9:0,10,9:0,11,9:0,12,9:0,13,9:0,17,9:0,0,10:0,4,10:0,5,10:0,6,10:0,7,10:0,8,10:0,9,10:0,10,10:0,11,10:0,12,10:0,13,10:0,17,10";
			tutorialVO.maxPlayers = 8;
			tutorialVO.minPlayers = 1;
			tutorialVO.mid = 0;
			tutorialVO.mname = "Tutorial";
			tutorialVO.msize = 18;
			tutorialVO.uname = "CGC Dev";
			tutorialVO.mrating = 5.0f;
			ChaseApp.mapSelect.createMapCacheFromPreset();
			myApp.setScreen(new ChainGame(myApp, ChaseApp.characterSelect.getNumPlayers(),
					tutorialVO, ChaseApp.mapSelect.getMapCache(), transition, true));
		}
	};
	
	/*
	 * Recalculate the dimensions of the pause menu if the screen resizes
	 */
	public void resize()
	{
		//Dimensions variables
		pauseMenuHeight = Data.ACTUAL_HEIGHT / 2.0f;
		
		pauseSettingsWidth = Data.ACTUAL_WIDTH / 1.25f;
		pauseSettingsHeight = Data.ACTUAL_HEIGHT / 1.5f;
		
		resizeScaleX = (float)Data.ACTUAL_WIDTH / 1920.0f;
		resizeScaleY = (float)Data.ACTUAL_HEIGHT / 1080.0f;
		
		resizeFontScale = (resizeScaleX + resizeScaleY) / 2.0f;
		
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN * resizeFontScale);
		layout.updateText(confirmWindowMessage);
		pauseConfirmWidth = layout.getLayout().width + pauseConfirmMargin * resizeScaleX;
		pauseConfirmHeight = Data.ACTUAL_HEIGHT / 4;
		
		layout.updateText(settingsItems[3]);
		pauseSettingsMargin = layout.getLayout().width / 2
				+ 50 * resizeScaleX;
		
		for (int i = 0; i < selectors.length; i++)
		{
			selectors[i].setScale(resizeScaleX, resizeScaleY);
		}
		
		volumeBar.setScale(resizeScaleX, resizeScaleY);
		
		layout.updateText(settingsItems[2]);
		volumeBar.setX(Data.ACTUAL_WIDTH / 2
			- pauseSettingsWidth / 2
			+ layout.getLayout().width / 2 * resizeScaleX
			+ 1920/3 * resizeScaleX);
		
		resizeFontScale = (resizeScaleX + resizeScaleY) / 2.0f;
		
		layout.updateText(items[3]);
		pauseMenuWidth = layout.getLayout().width
				+ pauseMenuMargin;
		
		shapes.setProjectionMatrix(CGCWorld.getHudMatrix());
	}
	
	/*
	 * Sets whether or not to show the pause menu
	 * 
	 * @param shouldShow			Whether or not to show the pause menu
	 */
	public void setShow(boolean shouldShow)
	{
		super.setShow(shouldShow);
		resetShowSettings();
	}
	
	/*
	 * Makes the selection reset to the top of the list
	 */
	public void resetSelected()
	{
		selected = 0;
		settingsSelected = 0;
	}
	
	/*
	 * Sets which ControlAdapter can give commands to the pause menu
	 * 
	 * @param newPauseBoss			The ControlAdapter from which to take commands
	 */
	public void setPauseBoss(ControlAdapter newPauseBoss)
	{
		pauseBoss = newPauseBoss;
	}
	
	/*
	 * Sets which controller was the one which paused the game
	 * 
	 * @param pauseController		The controller which paused the game
	 */
	public void setPauseController(Controller pauseController)
	{
		this.pauseController = pauseController;
	}
	
	/*
	 * Show the pause menu and perform setup tasks for the pause menu
	 */
	public void showPauseMenu()
	{
		pauseBoss = null;
		
		possibleBosses = new Array<ControlAdapter>();
		
		for (int i = 0; i < myApp.getMaxPlayers(); i++)
		{
			if (myApp.getInput().controlList[i].getController() == pauseController
					&& myApp.getInput().controlList[i].isUsed())
			{
				possibleBosses.add(myApp.getInput().controlList[i]);
				int controlIndex = possibleBosses.indexOf(myApp.getInput().controlList[i], false);
				possibleBosses.get(controlIndex).changeControlState(ControlType.PAUSE, false);
			}
		}
		
		possibleBosses.add(myApp.getInput().getKeyboardLeft());
		possibleBosses.add(myApp.getInput().getKeyboardRight());
		possibleBosses.get(possibleBosses.size - 1).changeControlState(ControlType.PAUSE, false);
		possibleBosses.get(possibleBosses.size - 2).changeControlState(ControlType.PAUSE, false);
		
		setShow(true);
	}
	
	/*
	 * Set the pause menu only to show the main pause menu upon pausing
	 */
	public void resetShowSettings()
	{
		showSettings = false;
		showConfirm = false;
	}
	
	/*
	 * Gets the ControlAdapter from which this class accepts commands
	 * 
	 * @return                      The ControlAdapter from which to take commands
	 */
	public ControlAdapter getPauseBoss()
	{
		return pauseBoss;
	}
	
	/*
	 * Gets the array of ControlAdapters which may have given input to the pause menu
	 * 
	 * @return						The array of ControlAdapters which might control this menu
	 */
	public Array<ControlAdapter> getPossibleBosses()
	{
		return possibleBosses;
	}
	
	/*
	 * Returns preferences back to default values
	 */
	private void restoreDefault()
	{
		for (int i = 0; i < Options.storedVolumeSettings.length; i++)
		{
			Options.storedVolumeSettings[i] = 1.0f;
			displayVolumeSettings[i] = 1.0f;
			volumePercentages[i] = Math.round(displayVolumeSettings[i] * 100);
		}
		
		Options.storedSymbolOption = true;
		symbolOption = true;
		
		Options.storedParallaxOption = true;
		parallaxOption = true;
	}
	
	/*
	 * Saves the current state of the user preferences
	 */
	private void savePreferences()
	{
		SoundManager.setVolumes(Options.storedVolumeSettings[0], 
				Options.storedVolumeSettings[1], Options.storedVolumeSettings[2]);
		
		String toSave = "";
		
		for (int i = 0; i < Options.storedVolumeSettings.length; i++)
		{
			toSave += Options.storedVolumeSettings[i] + "\n";
		}
		
		toSave += Options.storedTrackingOption + "\n";
		
		toSave += Options.storedSymbolOption + "\n";
		
		toSave += Options.storedDifficultyOption + "\n";
		
		if (!isOuya)
		{
			toSave += Options.storedParallaxOption + "\n";
		}
		else
		{
			toSave += "false\n";
		}
		
		if (!ChaseApp.fileHandler.writeFile("preferences.bin", toSave))
		{
			ChaseApp.overlay = new Overlay(myApp, owner, "No free local memory to save preferences");
			myApp.setScreen(ChaseApp.overlay);
		}
	}
	
	/*
	 * Loads the saved state of the user preferences
	 */
	private String loadPreferences()
	{
		String loadedPrefs = ChaseApp.fileHandler.readFile("preferences.bin");
		return loadedPrefs;
	}
	
	/*
	 * Retrieves settings from a file and stores them for the player to see
	 */
	private void initializeSettings()
	{
		String loadedPrefs = loadPreferences();
		String[] splitLoadedPrefs = loadedPrefs.split("\n");
		
		for (int i = 0; i < selectors.length; i++)
		{
			selectors[i] = ChaseApp.atlas.createSprite("selector");
			
			if (!loadedPrefs.equals(""))
			{
				Options.storedVolumeSettings[i] = Float.parseFloat(splitLoadedPrefs[i]);
			}
			else
			{
				Options.storedVolumeSettings[i] = 1.0f;
			}
			
			displayVolumeSettings[i] = Options.storedVolumeSettings[i];
			volumePercentages[i] = Math.round(displayVolumeSettings[i] * 100);
		}
		
		displayVolumeSettings = Options.storedVolumeSettings;
		for (int i = 0; i < displayVolumeSettings.length; i++)
		{
			volumePercentages[i] = Math.round(displayVolumeSettings[i] * 100);
		}
		symbolOption = Options.storedSymbolOption;
		
		parallaxOption = Options.storedParallaxOption;
	}
} // End class