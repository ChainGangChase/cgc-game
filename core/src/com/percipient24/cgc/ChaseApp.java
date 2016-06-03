/*
 * @(#)ChaseApp.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc;

import aurelienribon.tweenengine.TweenManager;


import com.badlogic.gdx.files.FileHandle;
import java.util.Locale;
import com.badlogic.gdx.utils.I18NBundle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.percipient24.helpers.FileHandlers;
import com.percipient24.cgc.net.CGCStats;
import com.percipient24.cgc.screens.*;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.enums.Platform;
import com.percipient24.enums.SupportedControllers;
import com.percipient24.input.InputManager;

/*
 * Contains the logic to run the program
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 * @author JD Kelly
 * @author Christopher Rider
 */
public class ChaseApp extends Game 
{
	// Tween variables
	public static TweenManager tManager;
	
	// Image variables
	public static TextureAtlas atlas;
	public static TextureAtlas characterAtlas;
	public static TextureAtlas gameAnimAtlas;
	public static TextureAtlas titleScreenAtlas;
	public static TextureAtlas menuControlsAtlas;
	public static TextureAtlas creditsAtlas;
	public static BitmapFont menuFont;
	public static BitmapFont titleFont;
	public static Screen lastScreen;
	public static ChaseApp self;
	private ShapeRenderer shapes;
	private SpriteBatch sBatch;
	private Matrix4 menuMatrix;
	public static Camera menuCam;
	
	// IO variables
	public static FileHandlers fileHandler;
	private InputManager input;
	private SupportedControllers se[] = new SupportedControllers[5];
	
	// Menus
	public static SplashScreen splash;
	public static MainMenu mainMenu;
	public static SelectMap mapSelect;
	public static CharacterSelect characterSelect;
	public static HowPlay howToPlay;
	public static CreateOwn create;
	public static Win win;
	public static Lose lose;
	public static Title title;
	public static Credits credits;
	public static Exit exit;
	public static Favorite favorite;
	public static Options options;
	public static Overlay overlay;
	
	// Test Screen
	// TODO: Remove this before the game is released
	//public static _ResolutionTestScreen _resTest;
	
	// System variables
	public static Platform platform;
	public static String os;
	public static Locale locale;
	public static I18NBundle lang;
	
	// Text colors
	public static Color selectedOrange = new Color(.953f, .643f, .027f, 1.0f);
	public static Color unselectedOrange = new Color(.853f, .543f, .027f, 1.0f);
	
	// Stats variables
	public static CGCStats stats;
	
	/*
	 * Gets the SpriteBatch used by this app
	 * 
	 * @return						The SpriteBatch used by this app
	 */
	public SpriteBatch getBatch()
	{
		return sBatch;
	}
	
	/*
	 * Gets the ShapeRenderer used by this app
	 * 
	 * @return						The ShapeRenderer used by this app
	 */
	public ShapeRenderer getShapes()
	{
		return shapes;
	}
	
	/*
	 * Gets the InputManager for this app
	 * 
	 * @return						The InputManager used by this app
	 */
	public InputManager getInput()
	{
		return input;
	}
	
	/*
	 * Gets the maximum player count this game can hold
	 * 
	 * @return						The game's maximum player count
	 */
	public int getMaxPlayers()
	{
		return characterSelect.getMaxPlayers();
	}
	
	/*
	 * Gets the controller types which are being used in the game
	 * 
	 * @return						The array of supported controller types being used
	 */
	public SupportedControllers[] getSupportedControllers()
	{
		return se;
	}
	
	/*
	 * Sets the projection matrix to be used for menu screens
	 * 
	 * @param newMenuMatrix			The new projection matrix for menu screens
	 */
	public void setMenuMatrix(Matrix4 newMenuMatrix)
	{
		menuMatrix = newMenuMatrix;
	}
	
	/*
	 * Gets the projection matrix to be used for menu screens
	 * 
	 * @return						The projection matrix for menu screens
	 */
	public Matrix4 getMenuMatrix()
	{
		return menuMatrix;
	}
	
	/*
	 * Creates a new ChaseApp
	 * 
	 * @param p						The platform this app is running on
	 * @param purchaser				The purchaser to use for map purchases
	 */
	public ChaseApp(Platform p, Locale l)
	{
		platform = p;
		self = this;
		locale = l;
	}

	/*
	 * Creates the application
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	public void create() 
	{
		switch(platform)
		{
			case DESKTOP:
				getOS();
				break;
			case OUYA:
				ouyaSetup();
				break;
			default:
				break;
		}
		
		preLoad();
		
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		splash = new SplashScreen(this);
		
		setScreen(splash);
	}
	
	/*
	 * Resizes the screen
	 * 
	 * @see com.badlogic.gdx.Game#resize(int, int)
	 */
	public void resize(int width, int height)
	{
		Data.ACTUAL_WIDTH = width;
		Data.ACTUAL_HEIGHT = height;
		Data.ASPECT_RATIO = (float)Data.ACTUAL_WIDTH / (float)Data.ACTUAL_HEIGHT;
	
		// This method should be called any time the resolution changes.
		// It updates essential anchor points used for screen layout.
		MenuTextureRegion.updateMenuAnchors();
		
		Data.GAME_WIDTH = 1920;
		Data.GAME_HEIGHT = (int) (1920.0f / ((float) Data.ASPECT_RATIO));
		Data.START_X = (int)MenuTextureRegion.MENU_ANCHORS[MenuTextureRegion.LOWER_LEFT].x;
		Data.MENU_WIDTH = (int)(1080f*Data.ASPECT_RATIO); // 1920x1080 = 1350
		Data.MENU_HEIGHT = 1080;
		
		Data.SCALE_FACTOR = (float)Data.ACTUAL_HEIGHT/1080f;
		
		if (menuCam == null)
		{
			menuCam = new Camera(1080f*Data.ASPECT_RATIO, 1080f, true);
			menuCam.translate(1080f*Data.ASPECT_RATIO/2f,540f);
		}
		
		menuCam.update();
		
		sBatch.setProjectionMatrix(menuCam.projection);
		setMenuMatrix(menuCam.projection);
		
		if(this.getScreen() != null)
		{
			((CGCScreen)this.getScreen()).resize(width, height);
			((CGCScreen)this.getScreen()).performLayout();
		}
	}
	
	/*
	 * Loads basic files to even run a loading screen
	 */
	public void preLoad()
	{
		input = new InputManager(se);
		Controllers.addListener(input);
		
		atlas = new TextureAtlas("main.atlas");
		titleScreenAtlas = new TextureAtlas("titlescreen.atlas");
		menuControlsAtlas = new TextureAtlas("menu.atlas");
		menuFont = new BitmapFont(Gdx.files.internal("data/cgcfont.fnt"), atlas.findRegion("cgcfont"), false);
		titleFont = new BitmapFont(Gdx.files.internal("data/cgctitlefont.fnt"), atlas.findRegion("cgctitlefont"), false);
		sBatch = new SpriteBatch(1625);
		shapes = new ShapeRenderer();
		tManager = new TweenManager();

		FileHandle baseFileHandle = Gdx.files.internal("i18n/CGCLang");
		I18NBundle myBundle = I18NBundle.createBundle(baseFileHandle, locale);

		lang = myBundle;
	}
	
	/*
	 * Loads the game
	 */
	public void loadGame()
	{
		characterAtlas = new TextureAtlas("characters.atlas");
		creditsAtlas = new TextureAtlas("credits.atlas");
		
		fileHandler = new FileHandlers(this);
		
		stats = new CGCStats();
		
		title = new Title(this);
		mainMenu = new MainMenu(this);
		characterSelect = new CharacterSelect(this);
		mapSelect = new SelectMap(this);
		howToPlay = new HowPlay(this);
		create = new CreateOwn(this);
		win = new Win(this);
		lose = new Lose(this);
		favorite = new Favorite(this);
		credits = new Credits(this);
		exit = new Exit(this);
		options = new Options(this);
		
		// TODO: Remove before launch!
		//_resTest = new _ResolutionTestScreen(this);
		
		if(platform == Platform.DESKTOP)
		{
			SoundManager.loadSounds(Gdx.files.local("bin/soundEffects"));
		}
		
		else
		{
			SoundManager.loadSounds(Gdx.files.internal("soundEffects"));
		}
	}
	
	/*
	 * Determines the OS this App is running on
	 */
	public void getOS()
	{
		os = System.getProperty("os.name");
		
		if(os.indexOf("Mac") >= 0)
		{
			os = "Mac";
			se[4] = SupportedControllers.KEYBOARD_ON_WINDOWS;
		}
		else if(os.indexOf("Win") >= 0)
		{
			os = "Windows";
			se[4] = SupportedControllers.KEYBOARD_ON_WINDOWS;
		}
		//Linux check, change if needed
		else if(os.indexOf("nux") >= 0 || os.indexOf("nix") >= 0 || os.indexOf("aix") >= 0)
		{
			//definitely change this later
			os = "Linux";
			se[4] = SupportedControllers.KEYBOARD_ON_WINDOWS;
		}
		
		if (Controllers.getControllers().size > 0)
		{
			for(int i = 0; i < Controllers.getControllers().size; i++)
			{
				String cName = Controllers.getControllers().get(i).getName().toLowerCase();
				
				//"x-box" is needed for Linux (maybe) because of xpad; may need to remove later
				if(cName.contains("xbox") || cName.contains("microsoft") || cName.contains("x-box"))
				{
					if(os.equals("Mac"))
					{
						se[i] = SupportedControllers.XBOX_ON_MAC;
					}
					else if(os.equals("Windows"))
					{
						se[i] = SupportedControllers.XBOX_ON_WINDOWS;
					}
					else if(os.equals("Linux"))
					{
						se[i] = SupportedControllers.XBOX_ON_LINUX;
					}
				}
				
				else if(cName.contains("playstation") || cName.contains("sony"))
				{
					if(os.equals("Mac"))
					{
						se[i] = SupportedControllers.PS3_ON_MAC;
					}
					else if(os.equals("Windows"))
					{
						se[i] = SupportedControllers.PS3_ON_WINDOWS;
						//Gdx.app.log("CONTROLLER", "PS3 on Windows");
					}
					else if(os.equals("Linux"))
					{
						se[i] = SupportedControllers.PS3_ON_LINUX;
					}
				}
				
				else if(cName.contains("ouya") || cName.contains("connected"))
				{
					if(os.equals("Mac"))
					{
						se[i] = SupportedControllers.OUYA_ON_MAC;
					}
					else if(os.equals("Windows"))
					{
						se[i] = SupportedControllers.OUYA_ON_WINDOWS;
					}
					else if(os.equals("Linux"))
					{
						se[i] = SupportedControllers.OUYA_ON_LINUX;
					}
				}
				
				else if(cName.contains("logitech"))
				{
					if(os.equals("Mac"))
					{
						se[i] = SupportedControllers.LOGITECH_ON_MAC;
					}
					else if(os.equals("Windows"))
					{
						se[i] = SupportedControllers.LOGITECH_ON_WINDOWS;
					}
					else if(os.equals("Linux"))
					{
						se[i] = SupportedControllers.LOGITECH_ON_LINUX;
					}
				}
				
				else
				{
					se[i] = SupportedControllers.KEYBOARD_ON_WINDOWS;
				}
			}
		}
		else
		{
			se[0] = SupportedControllers.KEYBOARD_ON_WINDOWS;
		}
	}
	
	/*
	 * Performs setup for this App on the Ouya system
	 */
	public void ouyaSetup()
	{
		os = "OUYA";
		
		Gdx.app.log("Controllers:", "My size is " + Controllers.getControllers().size);
		
		for(int i = 0; i < Controllers.getControllers().size; i++)
		{
			String cName = Controllers.getControllers().get(i).getName().toLowerCase();
			
			if(cName.contains("xbox") || cName.contains("microsoft") || cName.contains("x-box"))
			{
				se[i] = SupportedControllers.XBOX_ON_OUYA;
			}
			else if(cName.contains("playstation") || cName.contains("sony"))
			{
				se[i] = SupportedControllers.PS3_ON_OUYA;
			}
			else if(cName.contains("ouya"))
			{
				se[i] = SupportedControllers.OUYA_ON_OUYA;
			}
			else
			{	
				se[i] = SupportedControllers.NONE;
			}
		}
	}
	
	/*
	 * Fast way of using Gdx log function
	 * 
	 *  @param s1					What is sending the message
	 *  @param s2					The message to send
	 */
	public void alert(String s1, String s2)
	{
		Gdx.app.log(s1, s2);
	}
	
	/*
	 * Fast way of using Gdx log function
	 * 
	 *  @param s1					What is sending the message
	 *  @param i					The message to send
	 */
	public void alert(String s1, int i)
	{
		Gdx.app.log(s1, "" + i);
	}
	
	/*
	 * Fast way of using Gdx log function
	 * 
	 *  @param s1					What is sending the message
	 *  @param f					The message to send
	 */
	public void alert(String s1, float f)
	{
		Gdx.app.log(s1, "" + f);
	}
	
	/*
	 * Fast way of using Gdx log function
	 * 
	 *  @param s1					What is sending the message
	 *  @param d					The message to send
	 */
	public void alert(String s1, double d)
	{
		Gdx.app.log(s1, "" + d);
	}
	
	/*
	 * Fast way of using Gdx log function
	 * 
	 *  @param s1					What is sending the message
	 *  @param b					The message to send
	 */
	public void alert(String s1, boolean b)
	{
		Gdx.app.log(s1, "" + b);
	}
	
	/*
	 * Fast way of using Gdx log function
	 * 
	 *  @param s1					What is sending the message
	 *  @param v					The message to send
	 */
	public void alert(String s1, Vector2 v)
	{
		Gdx.app.log(s1, v.x + " " + v.y);
	}
	
	/*
	 * Fast way of using Gdx log function - always from ChaseApp
	 * 
	 * @param s1					The message to end
	 */
	public void alert(String s1)
	{
		Gdx.app.log("ChaseApp", s1);
	}
	
	/*
	 * Fast way of using Gdx log function - always from ChaseApp
	 * 
	 * @param b						The message to end
	 */
	public void alert(Boolean b)
	{
		Gdx.app.log("ChaseApp", ""+b);
	}
	
	/*
	 * Fast way of using Gdx log function - always from ChaseApp
	 * 
	 * @param f						The message to end
	 */
	public void alert(float f)
	{
		Gdx.app.log("ChaseApp", "" + f);
	}
	
	/*
	 * Fast way of using Gdx log function - always from ChaseApp
	 * 
	 * @param d						The message to end
	 */
	public void alert(double d)
	{
		Gdx.app.log("ChaseApp", "" + d);
	}
	
	/*
	 * Fast way of using Gdx log function - always from ChaseApp
	 * 
	 * @param i						The message to end
	 */
	public void alert(int i)
	{
		Gdx.app.log("ChaseApp", "" + i);
	}
	
	/*
	 * Fast way of using Gdx log function - always from ChaseApp
	 * 
	 * @param v						The message to end
	 */
	public void alert(Vector2 v)
	{
		Gdx.app.log("ChaseApp", v.x + " " + v.y);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.Game#setScreen(com.badlogic.gdx.Screen)
	 */
	public void setScreen(Screen screen)
	{
		lastScreen = screen;
		super.setScreen(screen);
	}
} // End class
