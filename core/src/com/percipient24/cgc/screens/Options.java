/*
 * @(#)Options.java		0.2 14/2/11
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.SoundManager;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.cgc.screens.helpers.LanguageKeys;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;

/*
 * Contains the data for the Options screen
 * 
 * @version 0.2 14/2/11
 * @author William Ziegler
 * @author Christopher Rider
 */
public class Options extends CGCScreen
{
	private String[] items = new String[11];
	
	private String[] messages = new String[11];
	private String[] difficultyLabels = {
		ChaseApp.lang.get(LanguageKeys.difficulty_1),
		ChaseApp.lang.get(LanguageKeys.difficulty_2),
		ChaseApp.lang.get(LanguageKeys.difficulty_3),
		ChaseApp.lang.get(LanguageKeys.difficulty_4),
		ChaseApp.lang.get(LanguageKeys.difficulty_5)};

	private String[] keyboardTypes = { "QWERTY", "DVORAK", "AZERTY" };
	
	private boolean isOuya;
	
	private MenuTextureRegion slideBar;
	private MenuTextureRegion[] selectors;
	
	private MenuTextureRegion optionsConvict;
	private MenuTextureRegion optionsCop;
	
	private int selected = 0;
	private int framesHeldVert = 0;
	private int framesHeldHoriz = 0;
	public static float[] storedVolumeSettings;
	private float[] displayVolumeSettings;
	private int[] volumePercentages;
	public static boolean storedTrackingOption;
	private boolean statTrackingOption;
	public static boolean storedSymbolOption;
	private boolean symbolOption;
	public static int storedDifficultyOption;
	private int difficultyOption;
	public static boolean storedParallaxOption;
	private boolean parallaxOption;
	public static int storedKeyboardOption = 0;
	private int keyboardOption = 0;
	
	private int toggleOnX = 80;
	private int toggleOffX = 155;
	
	private float slideScaleX = (float)Data.ACTUAL_WIDTH / (float)Data.BASE_WIDTH;
	private float slideBarX = Data.START_X + 100.0f 
			+ Data.MENU_WIDTH / 4f;
	private float slideBarY = 0;
	
	public static boolean storedLauncherOption;
	private boolean launcherOption;
	private String[] resolutionInfo;
	
	/*
	 * Creates an Options object
	 * 
	 * @param app					The app running this screen
	 */
	public Options(ChaseApp app)
	{
		super(app);
		title = ChaseApp.lang.get(LanguageKeys.options);
		titleLayout.updateText(title);
		
		isOuya = (ChaseApp.os == "OUYA");
		
		if(!isOuya)
		{
			items = new String[12];
			messages = new String[12];
		}
		
		items[0] = ChaseApp.lang.get(LanguageKeys.master_volume);
		items[1] = ChaseApp.lang.get(LanguageKeys.music_volume);
		items[2] = ChaseApp.lang.get(LanguageKeys.effects_volume);
		items[3] = ChaseApp.lang.get(LanguageKeys.stats_tracking);
		items[4] = ChaseApp.lang.get(LanguageKeys.sensor_symbols);
		items[5] = ChaseApp.lang.get(LanguageKeys.change_balance);
		items[6] = ChaseApp.lang.get(LanguageKeys.keyboard_layout);
		items[7] = ChaseApp.lang.get(LanguageKeys.parallax_graphics);
		
		if(!isOuya)
		{
			items[8] = ChaseApp.lang.get(LanguageKeys.show_launcher);
			items[9] = ChaseApp.lang.get(LanguageKeys.accept_changes);
			items[10] = ChaseApp.lang.get(LanguageKeys.restore_defaults);
			items[11] = ChaseApp.lang.get(LanguageKeys.back);
		}
		else
		{
			items[8] = ChaseApp.lang.get(LanguageKeys.accept_changes);
			items[9] = ChaseApp.lang.get(LanguageKeys.restore_defaults);
			items[10] = ChaseApp.lang.get(LanguageKeys.back);
		}
		
		messages[0] = ChaseApp.lang.get(LanguageKeys.master_volume_message);
		messages[1] = ChaseApp.lang.get(LanguageKeys.music_volume_message);
		messages[2] = ChaseApp.lang.get(LanguageKeys.effects_volume_message);
		messages[3] = ChaseApp.lang.get(LanguageKeys.stats_message);
		messages[4] = ChaseApp.lang.get(LanguageKeys.symbols_message);
		messages[5] = ChaseApp.lang.get(LanguageKeys.balance_message);
		messages[6] = ChaseApp.lang.get(LanguageKeys.keyboard_message);
		messages[7] = ChaseApp.lang.get(LanguageKeys.parallax_message);
		
		if(!isOuya)
		{
			messages[8] = ChaseApp.lang.get(LanguageKeys.launcher_message);
			messages[9] = ChaseApp.lang.get(LanguageKeys.apply_message);
			messages[10] = ChaseApp.lang.get(LanguageKeys.revert_message);
			messages[11] = ChaseApp.lang.get(LanguageKeys.back_message);
		}
		else
		{
			messages[8] = ChaseApp.lang.get(LanguageKeys.apply_message);
			messages[9] = ChaseApp.lang.get(LanguageKeys.revert_message);
			messages[10] = ChaseApp.lang.get(LanguageKeys.back_message);
		}
		
		slideBar = new MenuTextureRegion(ChaseApp.atlas.findRegion("sliderbar"), 
				new Vector2(slideBarX, slideBarY), MenuTextureRegion.UPPER_LEFT, MenuTextureRegion.LOWER_LEFT);
		slideBar.setScaleX(slideScaleX);
		selectors = new MenuTextureRegion[4];
		
		optionsConvict = new MenuTextureRegion(ChaseApp.atlas.findRegion("optionsconvict"),
				new Vector2(0, 0), MenuTextureRegion.UPPER_LEFT, MenuTextureRegion.LOWER_LEFT);
		optionsCop = new MenuTextureRegion(ChaseApp.atlas.findRegion("optionscop"),
				new Vector2(0, 0), MenuTextureRegion.UPPER_LEFT, MenuTextureRegion.LOWER_LEFT);
		
		volumePercentages = new int[3];
		storedVolumeSettings = new float[3];
		displayVolumeSettings = new float[3];
		
		loadPreferences();
		
		// Finish options initialization
		statTrackingOption = storedTrackingOption;
		symbolOption = storedSymbolOption;
		difficultyOption = storedDifficultyOption;
		parallaxOption = storedParallaxOption;
		keyboardOption = storedKeyboardOption;
		launcherOption = storedLauncherOption;
		input.getKeyboardLeft().setKeyboardType(Options.storedKeyboardOption);
	}
	
	/*
	 * Handles control input to this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#handleInput()
	 */
	public void handleInput() 
	{
		ControlAdapter boss = input.getBoss();
		ControlAdapter keyboardLeft = input.getKeyboardLeft(); 
		ControlAdapter keyboardRight = input.getKeyboardRight();
		
		// Select the current option
		if (boss.justPressed(ControlType.SELECT) || keyboardLeft.justPressed(ControlType.SELECT)
				|| keyboardRight.justPressed(ControlType.SELECT))
		{
			boss.changeControlState(ControlType.SELECT, false);
			keyboardLeft.changeControlState(ControlType.SELECT, false);
			keyboardRight.changeControlState(ControlType.SELECT, false);
			switch(selected)
			{
				case 3:
					statTrackingOption = !statTrackingOption;
					break;
				case 4:
					symbolOption = !symbolOption;
					break;
				case 5:
					difficultyOption++;
					if (difficultyOption == difficultyLabels.length)
					{
						difficultyOption = 0;
					}
					break;
				case 6:
					keyboardOption++;
					if (keyboardOption == keyboardTypes.length)
					{
						keyboardOption = 0;
					}
					break;
				case 7:
					parallaxOption = !parallaxOption;
					break;
				case 8:
					if(isOuya)
					{
						acceptChanges();
					}
					else
					{
						launcherOption = !launcherOption;
					}
					break;
				case 9: 
					if(isOuya)
					{
						restoreDefault();
						savePreferences();
					}
					else
					{
						acceptChanges();
					}
					break;
				case 10:
					if(isOuya)
					{
						resetPreferences();
						myApp.setScreen(ChaseApp.mainMenu); 
					}
					else
					{
						restoreDefault();
						savePreferences();
					}
					break;
				case 11:
					resetPreferences();
					myApp.setScreen(ChaseApp.mainMenu);
					break;
			}
		}
		
		// Go back
		if (boss.justPressed(ControlType.BACK) || keyboardLeft.justPressed(ControlType.BACK)
				|| keyboardRight.justPressed(ControlType.BACK))
		{
			boss.changeControlState(ControlType.BACK, false);
			keyboardLeft.changeControlState(ControlType.BACK, false);
			keyboardRight.changeControlState(ControlType.BACK, false);
			resetPreferences();
			myApp.setScreen(ChaseApp.mainMenu);
		}
		
		// Change the selected option
		if (boss.isPressed(ControlType.MENU_UP) || keyboardLeft.isPressed(ControlType.MENU_UP)
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
			
			if (selected == 7 && isOuya)
			{
				selected--;
			}
		}
		else if (boss.isPressed(ControlType.MENU_DOWN) || keyboardLeft.isPressed(ControlType.MENU_DOWN)
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
			
			if (selected == 7 && isOuya)
			{
				selected++;
			}
		}
		else
		{
			framesHeldVert = 0;
		}
		
		// Move the selected option
		if (selected < 3)
		{	
			if (boss.isPressed(ControlType.MENU_LEFT) || keyboardLeft.isPressed(ControlType.MENU_LEFT)
					|| keyboardRight.isPressed(ControlType.MENU_LEFT))
			{
				framesHeldHoriz++;
				if (framesHeldHoriz == 1)
				{
					displayVolumeSettings[selected] -= .05f;
				}
				else if (framesHeldHoriz > 90 && framesHeldHoriz % 5 == 0)
				{
					displayVolumeSettings[selected] -= .05f;
				}
				else if (framesHeldHoriz > 30 && framesHeldHoriz % 10 == 0)
				{
					displayVolumeSettings[selected] -= .05f;
				}
				
				if (displayVolumeSettings[selected] < 0.0f)
				{
					displayVolumeSettings[selected] = 0.0f;
				}
			}
			else if (boss.isPressed(ControlType.MENU_RIGHT) || keyboardLeft.isPressed(ControlType.MENU_RIGHT)
					|| keyboardRight.isPressed(ControlType.MENU_RIGHT))
			{
				framesHeldHoriz++;
				if (framesHeldHoriz == 1)
				{
					displayVolumeSettings[selected] += .05f;
				}
				else if (framesHeldHoriz > 90 && framesHeldHoriz % 5 == 0)
				{
					displayVolumeSettings[selected] += .05f;
				}
				else if (framesHeldHoriz > 30 && framesHeldHoriz % 10 == 0)
				{
					displayVolumeSettings[selected] += .05f;
				}
				
				if (displayVolumeSettings[selected] > 1.0f)
				{
					displayVolumeSettings[selected] = 1.0f;
				}
			}
			else
			{
				framesHeldHoriz = 0;
			}
			
			volumePercentages[selected] = Math.round(displayVolumeSettings[selected] * 100);
		}
		else if (selected == 3)
		{	
			if (boss.isPressed(ControlType.MENU_RIGHT) || boss.isPressed(ControlType.MENU_LEFT)
				|| keyboardLeft.isPressed(ControlType.MENU_RIGHT) || keyboardLeft.isPressed(ControlType.MENU_LEFT)
				|| keyboardRight.isPressed(ControlType.MENU_RIGHT) || keyboardRight.isPressed(ControlType.MENU_LEFT))
			{
				framesHeldHoriz++;
				
				if (framesHeldHoriz == 1)
				{
					statTrackingOption = !statTrackingOption;
				}
			}
			else
			{
				framesHeldHoriz = 0;
			}
		}
		else if (selected == 4)
		{
			if (boss.isPressed(ControlType.MENU_RIGHT) || boss.isPressed(ControlType.MENU_LEFT)
					|| keyboardLeft.isPressed(ControlType.MENU_RIGHT) || keyboardLeft.isPressed(ControlType.MENU_LEFT)
					|| keyboardRight.isPressed(ControlType.MENU_RIGHT) || keyboardRight.isPressed(ControlType.MENU_LEFT))
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
		else if (selected == 5)
		{
			if (boss.isPressed(ControlType.MENU_RIGHT) || keyboardLeft.isPressed(ControlType.MENU_RIGHT)
					|| keyboardRight.isPressed(ControlType.MENU_RIGHT))
			{
				framesHeldHoriz++;
				
				if (framesHeldHoriz == 1 && difficultyOption < difficultyLabels.length - 1)
				{
					difficultyOption++;
				}
			}
			else if (boss.isPressed(ControlType.MENU_LEFT) || keyboardLeft.isPressed(ControlType.MENU_LEFT)
					|| keyboardRight.isPressed(ControlType.MENU_LEFT))
			{
				framesHeldHoriz++;
				
				if (framesHeldHoriz == 1 && difficultyOption > 0)
				{
					difficultyOption--;
				}
			}
			else
			{
				framesHeldHoriz = 0;
			}
		}
		else if (selected == 6)
		{
			if (boss.isPressed(ControlType.MENU_RIGHT) || keyboardLeft.isPressed(ControlType.MENU_RIGHT)
					|| keyboardRight.isPressed(ControlType.MENU_RIGHT))
			{
				framesHeldHoriz++;
				
				if (framesHeldHoriz == 1)
				{
					keyboardOption++;
					if (keyboardOption == keyboardTypes.length)
					{
						keyboardOption = 0;
					}
				}
			}
			else if (boss.isPressed(ControlType.MENU_LEFT) || keyboardLeft.isPressed(ControlType.MENU_LEFT)
					|| keyboardRight.isPressed(ControlType.MENU_LEFT))
			{
				framesHeldHoriz++;
				
				if (framesHeldHoriz == 1)
				{
					keyboardOption--;
					if (keyboardOption == -1)
					{
						keyboardOption = keyboardTypes.length-1;
					}
				}
			}
			else
			{
				framesHeldHoriz = 0;
			}
		}
		else if (selected == 7)
		{
			if (boss.isPressed(ControlType.MENU_RIGHT) || boss.isPressed(ControlType.MENU_LEFT)
					|| keyboardLeft.isPressed(ControlType.MENU_RIGHT) || keyboardLeft.isPressed(ControlType.MENU_LEFT)
					|| keyboardRight.isPressed(ControlType.MENU_RIGHT) || keyboardRight.isPressed(ControlType.MENU_LEFT))
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
		else if (selected == 8 && !isOuya)
		{
			if (boss.isPressed(ControlType.MENU_RIGHT) || boss.isPressed(ControlType.MENU_LEFT)
					|| keyboardLeft.isPressed(ControlType.MENU_RIGHT) || keyboardLeft.isPressed(ControlType.MENU_LEFT)
					|| keyboardRight.isPressed(ControlType.MENU_RIGHT) || keyboardRight.isPressed(ControlType.MENU_LEFT))
			{
				framesHeldHoriz++;
				
				if (framesHeldHoriz == 1)
				{
					launcherOption = !launcherOption;
				}
			}
			else
			{
				framesHeldHoriz = 0;
			}
		}
		
		super.handleInput();
	}
	
	/*
	 * Show this screen
	 * 
	 * @see com.badlogic.gdx.Screen#show()
	 */
	public void show()
	{
		super.show();
		selected = 0;
		initializeSettings();
	}
	
	/*
	 * Draws this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#render(float)
	 */
	public void render(float delta) 
	{
		super.render(delta);
		
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
		
		for(int i = 0; i < items.length; i++)
		{
			if(i == selected) 
			{ 
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
				ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
			}
			else
			{
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
				ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
			}
			
			if (i < displayVolumeSettings.length)
			{
				slideBar.setY(-120 - (55 * (i) + slideBar.getRegionHeight() / 2));

				if (i == selected)
				{
					sBatch.setColor(ChaseApp.selectedOrange);
				}
				slideBar.draw(sBatch);
				sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				
				selectors[i].setX(slideBar.getX() + displayVolumeSettings[i] * slideBar.getRegionWidth()
						* slideBar.getScaleX() - selectors[i].getRegionWidth() / 2);
				selectors[i].draw(sBatch);
				
				ChaseApp.menuFont.draw(sBatch, volumePercentages[i]+"%", 
						slideBar.getX() + slideBar.getRegionWidth() * slideBar.getScaleX() + 220, 
						(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
						- (55 * i) + ChaseApp.menuFont.getLineHeight() / 2f);
			}
			
			if (i == 5)
			{
				slideBar.setY(-120 - (55 * (i) + slideBar.getRegionHeight() / 2));
				
				if (i == selected)
				{
					sBatch.setColor(ChaseApp.selectedOrange);
				}
				
				slideBar.draw(sBatch);
				sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				
				selectors[3].setX(slideBar.getX() + (slideBar.getRegionWidth()
						* ((float)difficultyOption / (float)(difficultyLabels.length - 1)))
						* slideBar.getScaleX() - selectors[3].getRegionWidth() / 2);
				selectors[3].draw(sBatch);
				
				if (i != selected)
				{
					sBatch.setColor(0.8f, 0.8f, 0.8f, 1.0f);
				}
				
				optionsConvict.draw(sBatch);
				optionsCop.draw(sBatch);
				
				sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
			
            if (i < 7)
            {	 
            	ChaseApp.menuFont.draw(sBatch, items[i], Data.START_X + MENU_BUFFER, 
            			(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
            			- (55 * i) + ChaseApp.menuFont.getLineHeight()/2f);
            }
            else
            {
                if (isOuya && i != 7)
                {
                    ChaseApp.menuFont.draw(sBatch, items[i], Data.START_X + MENU_BUFFER, 
                    		(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
                			- (55 * (i - 1)) + ChaseApp.menuFont.getLineHeight()/2f);
                }
                else if (!isOuya)
                {
                    ChaseApp.menuFont.draw(sBatch, items[i], Data.START_X + MENU_BUFFER, 
                    		(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
                			- (55 * i) + ChaseApp.menuFont.getLineHeight()/2f);
                }
            }
		}
		
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
		ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		
		renderToggles();
		
		ChaseApp.menuFont.draw(sBatch, messages[selected], 
				slideBarX, Data.MENU_HEIGHT - MENU_BUFFER - (55 * (9 + 2)) + ChaseApp.menuFont.getLineHeight()/2f);
		
		sBatch.end();
	}
	
	/*
	 * Draws all of the option choices that are not slider bars on the right side
	 */
	private void renderToggles()
	{
		//Stat Tracking on/off visuals
		if (selected == 3)
		{
			ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
		}
		if (statTrackingOption)
		{
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
			if (selected == 3)
			{
				ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
			}
			else
			{
				ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		ChaseApp.menuFont.draw(sBatch, ChaseApp.lang.get(LanguageKeys.on), slideBar.getX() + slideBar.getRegionWidth()
				* slideBar.getScaleX() + toggleOnX,
				(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
    			- (55 * 3) + ChaseApp.menuFont.getLineHeight()/2f);
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
		if (selected == 3)
		{
			ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
		}
		else
		{
			ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		}
		if (!statTrackingOption)
		{
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
			if (selected == 3)
			{
				ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
			}
			else
			{
				ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		ChaseApp.menuFont.draw(sBatch, ChaseApp.lang.get(LanguageKeys.off), slideBar.getX() + slideBar.getRegionWidth()
				* slideBar.getScaleX() + toggleOffX, 
				(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
    			- (55 * 3) + ChaseApp.menuFont.getLineHeight()/2f);
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
		ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		
		//Sensor Symbols on/off visuals
		if (selected == 4)
		{
			ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
		}
		if (symbolOption)
		{
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
			if (selected == 4)
			{
				ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
			}
			else
			{
				ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		ChaseApp.menuFont.draw(sBatch, ChaseApp.lang.get(LanguageKeys.on), slideBar.getX() + slideBar.getRegionWidth()
				* slideBar.getScaleX() + toggleOnX,
				(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
    			- (55 * 4) + ChaseApp.menuFont.getLineHeight()/2f);
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
		if (selected == 4)
		{
			ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
		}
		else
		{
			ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		}
		if (!symbolOption)
		{
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
			if (selected == 4)
			{
				ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
			}
			else
			{
				ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		ChaseApp.menuFont.draw(sBatch, ChaseApp.lang.get(LanguageKeys.off), slideBar.getX() + slideBar.getRegionWidth()
				* slideBar.getScaleX() + toggleOffX, 
				(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
    			- (55 * 4) + ChaseApp.menuFont.getLineHeight()/2f);
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
		ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		
		if (selected == 6)
		{
			ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
		}
		else
		{
			ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		}
		ChaseApp.menuFont.draw(sBatch, keyboardTypes[keyboardOption], 
				slideBar.getX() + slideBar.getRegionWidth() * slideBar.getScaleX() + toggleOnX,
				(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
    			- (55 * 6) + ChaseApp.menuFont.getLineHeight()/2f);
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
		ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		
		if (!isOuya)
		{
			//Parallax Graphics on/off visuals
			if (selected == 7)
			{
				ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
			}
			if (parallaxOption)
			{
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
				if (selected == 7)
				{
					ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
				}
				else
				{
					ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}
			ChaseApp.menuFont.draw(sBatch, ChaseApp.lang.get(LanguageKeys.on), slideBar.getX() + slideBar.getRegionWidth()
					* slideBar.getScaleX() + toggleOnX, 
					(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
	    			- (55 * 7) + ChaseApp.menuFont.getLineHeight()/2f);
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
			if (selected == 7)
			{
				ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
			}
			else
			{
				ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
			}
			if (!parallaxOption)
			{
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
				if (selected == 7)
				{
					ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
				}
				else
				{
					ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}
			ChaseApp.menuFont.draw(sBatch, ChaseApp.lang.get(LanguageKeys.off), slideBar.getX() + slideBar.getRegionWidth()
					* slideBar.getScaleX() + toggleOffX, 
					(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
	    			- (55 * 7) + ChaseApp.menuFont.getLineHeight()/2f);
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
			ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
			
			//End 7
			if (selected == 8)
			{
				ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
			}
			if (launcherOption)
			{
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
				if (selected == 8)
				{
					ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
				}
				else
				{
					ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}
			ChaseApp.menuFont.draw(sBatch, ChaseApp.lang.get(LanguageKeys.on), slideBar.getX() + slideBar.getRegionWidth()
					* slideBar.getScaleX() + toggleOnX, 
					(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
	    			- (55 * 8) + ChaseApp.menuFont.getLineHeight()/2f);
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
			if (selected == 8)
			{
				ChaseApp.menuFont.setColor(ChaseApp.unselectedOrange);
			}
			else
			{
				ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
			}
			if (!launcherOption)
			{
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
				if (selected == 8)
				{
					ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
				}
				else
				{
					ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}
			ChaseApp.menuFont.draw(sBatch, ChaseApp.lang.get(LanguageKeys.off), slideBar.getX() + slideBar.getRegionWidth()
					* slideBar.getScaleX() + toggleOffX, 
					(int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.UPPER_LEFT].y - 120
	    			- (55 * 8) + ChaseApp.menuFont.getLineHeight()/2f);
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
			ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 0.9f);
		}
		
		
		
	}
	
	/*
	 * Returns preferences back to the way they were upon opening the options screen
	 */
	private void resetPreferences()
	{
		for (int i = 0; i < storedVolumeSettings.length; i++)
		{
			displayVolumeSettings[i] = storedVolumeSettings[i];
			volumePercentages[i] = Math.round(displayVolumeSettings[i] * 100);
		}

		statTrackingOption = storedTrackingOption;
		symbolOption = storedSymbolOption;
		difficultyOption = storedDifficultyOption;
		parallaxOption = storedParallaxOption;
		keyboardOption = storedKeyboardOption;
		launcherOption = storedLauncherOption;
		input.getKeyboardLeft().setKeyboardType(Options.storedKeyboardOption);
	}
	
	/*
	 * Returns preferences back to default values
	 */
	private void restoreDefault()
	{
		for (int i = 0; i < storedVolumeSettings.length; i++)
		{
			storedVolumeSettings[i] = 1.0f;
			displayVolumeSettings[i] = 1.0f;
			volumePercentages[i] = Math.round(displayVolumeSettings[i] * 100);
		}
		
		storedTrackingOption = true;
		statTrackingOption = true;
		
		storedSymbolOption = true;
		symbolOption = true;
		
		difficultyOption = 2;
		storedDifficultyOption = 2;
		
		storedParallaxOption = true;
		parallaxOption = true;
		
		keyboardOption = 0;
		storedKeyboardOption = 0;
		input.getKeyboardLeft().setKeyboardType(Options.storedKeyboardOption);
	}
	
	/*
	 * Saves the current state of the user preferences
	 */
	private void savePreferences()
	{
		SoundManager.setVolumes(storedVolumeSettings[0], storedVolumeSettings[1], storedVolumeSettings[2]);
		
		String toSave = "";
		
		for (int i = 0; i < storedVolumeSettings.length; i++)
		{
			toSave += storedVolumeSettings[i] + "\n";
		}
		
		toSave += storedTrackingOption + "\n";
		
		toSave += storedSymbolOption + "\n";
		
		toSave += storedDifficultyOption + "\n";
		
		if (!isOuya)
		{
			toSave += parallaxOption + "\n";
		}
		else
		{
			toSave += "false\n";
		}
		
		toSave += storedKeyboardOption + "\n";

		
		if (!ChaseApp.fileHandler.writeFile("preferences.bin", toSave))
		{
			ChaseApp.overlay = new Overlay(myApp, this, ChaseApp.lang.get(LanguageKeys.no_memory));
			myApp.setScreen(ChaseApp.overlay);
		}
		
		if(!isOuya)
		{
			
			String resSave = "";
			
			for(int i = 0; i < 4; i += 1)
			{
				resSave += resolutionInfo[i] + "\n";
			}
			
			if(!ChaseApp.fileHandler.writeFile("resolutionPreferences.bin", resSave))
			{
				ChaseApp.overlay = new Overlay(myApp, this, ChaseApp.lang.get(LanguageKeys.no_memory));
				myApp.setScreen(ChaseApp.overlay);
			}
		}
	}
	
	/*
	 * Loads the saved state of the user preferences
	 */
	private void loadPreferences()
	{
		// Load saved preferences from file, set to default if no file is found
		String loadedPrefs = ChaseApp.fileHandler.readFile("preferences.bin");
		String[] splitLoadedPrefs = loadedPrefs.split("\n");
		
		for (int i = 0; i < storedVolumeSettings.length; i++)
		{	
			if (!loadedPrefs.equals(""))
			{
				storedVolumeSettings[i] = Float.parseFloat(splitLoadedPrefs[i]);
			}
			else
			{
				storedVolumeSettings[i] = 1.0f;
			}
			
			displayVolumeSettings[i] = storedVolumeSettings[i];
			volumePercentages[i] = Math.round(displayVolumeSettings[i] * 100);
		}
		
		if (!loadedPrefs.equals(""))
		{
			if (splitLoadedPrefs.length >= 4)
			{
				storedTrackingOption = Boolean.parseBoolean(splitLoadedPrefs[3]);
			}
			else
			{
				storedTrackingOption = true;
			}
			
			if (splitLoadedPrefs.length >= 5)
			{
				storedSymbolOption = Boolean.parseBoolean(splitLoadedPrefs[4]);
			}
			else
			{
				storedSymbolOption = true;
			}
			
			if (splitLoadedPrefs.length >= 6)
			{
				storedDifficultyOption = Integer.parseInt(splitLoadedPrefs[5]);
			}
			else
			{
				storedDifficultyOption = 1;
			}
			
			if (splitLoadedPrefs.length >= 7)
			{
				storedParallaxOption = Boolean.parseBoolean(splitLoadedPrefs[6]);
			}
			else
			{
				storedParallaxOption = false;
			}
			
			if (splitLoadedPrefs.length >= 8)
			{
				storedKeyboardOption = Integer.parseInt(splitLoadedPrefs[7]);
				input.getKeyboardLeft().setKeyboardType(Options.storedKeyboardOption);
			}
			else
			{
				storedKeyboardOption = 0;
				input.getKeyboardLeft().setKeyboardType(Options.storedKeyboardOption);
			}
		}
		else
		{
			storedTrackingOption = true;
			storedSymbolOption = true;
			storedDifficultyOption = 2;
			storedParallaxOption = false;
			storedKeyboardOption = 0;
			input.getKeyboardLeft().setKeyboardType(Options.storedKeyboardOption);
		}
		
		if(!isOuya)
		{
			String loadedResPrefs = ChaseApp.fileHandler.readFile("resolutionPreferences.bin");
		
			String[] splitResPrefs = loadedResPrefs.split("\n");
			
			if(!loadedResPrefs.equals(""))
			{
				storedLauncherOption = Boolean.parseBoolean(splitResPrefs[0]);
				resolutionInfo = splitResPrefs;
			}
			else
			{
				storedLauncherOption = true;
				resolutionInfo = new String[4];
				resolutionInfo[0] = "true";
				resolutionInfo[1] = Integer.toString(Gdx.graphics.getWidth());
				resolutionInfo[2] = Integer.toString(Gdx.graphics.getHeight());
				resolutionInfo[3] = Boolean.toString(Gdx.graphics.isFullscreen());
			}
		}
	}
	
	/*
	 * Retrieves settings from a file and stores them for the player to see
	 */
	private void initializeSettings()
	{
		loadPreferences();
		
		for (int i = 0; i < storedVolumeSettings.length; i++)
		{
			slideBar.setY(-120 - (55 * (i) + slideBar.getRegionHeight() / 2));
			
			selectors[i] = new MenuTextureRegion(ChaseApp.atlas.findRegion("selector"),
					new Vector2(0, slideBar.getY() + slideBar.getRegionHeight() - 48),
					MenuTextureRegion.UPPER_LEFT, MenuTextureRegion.LOWER_LEFT);
		}
		
		slideBar.setY(-120 - (55 * (5) + slideBar.getRegionHeight() / 2));
		
		selectors[3] = new MenuTextureRegion(ChaseApp.atlas.findRegion("selector"),
				new Vector2(0, slideBar.getY() + slideBar.getRegionHeight() - 48),
				MenuTextureRegion.UPPER_LEFT, MenuTextureRegion.LOWER_LEFT);
		
		optionsConvict.setX(slideBar.getX() - optionsConvict.getRegionWidth() - 5);
		optionsConvict.setY(slideBar.getY() + slideBar.getRegionHeight() / 2f
				- optionsConvict.getRegionHeight() / 2f);
		
		optionsCop.setX(slideBar.getX() + slideBar.getRegionWidth()
				* slideBar.getScaleX() + 5);
		optionsCop.setY(slideBar.getY() + slideBar.getRegionHeight() / 2f
				- optionsCop.getRegionHeight() / 2f);
	}
	
	/*
	 * Handles the setting of stored values to the new values and saves them
	 */
	private void acceptChanges()
	{
		for (int i = 0; i < displayVolumeSettings.length; i++)
		{
			storedVolumeSettings[i] = displayVolumeSettings[i];
		}
		storedTrackingOption = statTrackingOption;
		storedSymbolOption = symbolOption;
		storedDifficultyOption = difficultyOption;
		storedParallaxOption = parallaxOption;
		storedKeyboardOption = keyboardOption;
		input.getKeyboardLeft().setKeyboardType(Options.storedKeyboardOption);
		
		if(!isOuya)
		{
			storedLauncherOption = launcherOption;
			resolutionInfo[0] = Boolean.toString(launcherOption);
		}
		savePreferences();
		myApp.setScreen(ChaseApp.mainMenu);
	}
} // End class