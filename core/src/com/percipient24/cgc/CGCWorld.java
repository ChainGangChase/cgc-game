/*
 * @(#)CGCWorld.java		0.2 14/3/20
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc;

import java.util.Random;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.boss.Boss;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.players.RookieCop;
import com.percipient24.cgc.overlays.PauseMenu;
import com.percipient24.cgc.overlays.Transition;
import com.percipient24.cgc.screens.CGCScreen;
import com.percipient24.cgc.screens.Options;
import com.percipient24.enums.BossType;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;
import com.percipient24.helpers.Patcher;

/*
 * Contains the logic to run and render a level in Chain Gang Chase
 * 
 * @version 0.2 14/3/20
 * @author Christopher Rider
 * @author JD Kelly
 * @author William Ziegler
 */
public abstract class CGCWorld extends CGCScreen 
{
	public static final float CAMERA_DELAY = 1.0f/60.0f; // In seconds
	public static final float WORLD_DELAY = 1.0f/60.0f; // In seconds
	
	protected static final int ROWS_TO_DISPLAY = 15;
	protected final int MAX_IMAGES_PER_DRAW = 127;
	
	// Variables for difficulty modification
	protected static int numChainsInLink = 11;
	protected static float chainDensity = 1.0f;
	
	// Game variables
	protected static com.percipient24.cgc.art.TextureAnimationDrawer animManager;
	protected static BodyFactory bf;
	protected static boolean bossFight;
	protected static ContactManager cm;
	protected static float delta; // Time since the last frame, in seconds
	protected static boolean gameLost;
	protected static long gameTime; // How long the game has been played, in seconds
	protected static boolean gameWon;
	protected static LayerHandler lh;
	protected static int numCops = 0; //The number of living cops
	public static int numPlayers; // The number of players in the game
	protected static int numPrisoners = 0; // The number of living prisoners
	protected static Patcher patcher;
	protected static Array<Player> players;
	protected static Random random;
	protected static Array<Player> recentlyDeceased; // The players who will respawn as cops
	protected static Array<ControllerScheme> schemes; // Maps players to ControlAdapters
	protected ShapeRenderer shapes;
	protected static boolean terminated; // If true, NO logic should run, other than close logic
	protected static Array<GameEntity> toDestroyList;
	protected boolean tutorial;
	
	// Transition variables
	protected TweenManager tManager;
	protected Transition transition;
	protected boolean transitioning;
	
	// World variables
	protected static Camera camera;
	protected static Matrix4 hudMatrix;
	public static int spritesDrawn;
	protected static World world;
	
	// Pause variables
	protected static PauseMenu pauseMenu;
	protected static boolean paused;
	private boolean justPaused = false;
	
	// Respawn Camera variables
	protected static CGCTimer respawnClock;
	protected static Timer.Task respawnTask;
	protected static float respawnTime = 2.25f;
	protected static boolean createHeli;
	protected static float respawnHeight;
	
	// End game variables
	protected static CGCTimer endClock;
	protected static Timer.Task endTask;
	protected static float endTime = 2.0f;
	
	//End game spinning variables
	protected static Timer.Task spinTask;
	
	/*
	 * Creates a new CGCWorld object
	 * 
	 * @param app						The ChaseApp to use
	 * @param numPlayers				The number of players playing this game
	 * @param tutorial					Whether or not to run the tutorial map
	 */
	public CGCWorld(ChaseApp app, int numPlayers, boolean tutorial)
	{
		super(app);
		TimerManager.start();
		TimerManager.setPause(false);
		
		terminated = false;
		gameLost = false;
		gameWon = false;
		bossFight = false;
		this.tutorial = tutorial;
		
		shapes = app.getShapes();
		toDestroyList = new Array<GameEntity>();
		
		world = new World(new Vector2(0, 0), true);
		bf = new BodyFactory(world);
		lh = new LayerHandler();
		
		cm = new ContactManager();
		players = new Array<Player>();
		CGCWorld.numPlayers = numPlayers;
		numCops = 0;
		numPrisoners = 0;
		recentlyDeceased = new Array<Player>();
		patcher = new Patcher();
		random = new Random();
		paused = false;
		pauseMenu = new PauseMenu(sBatch, this, myApp, this);
		pauseMenu.setShow(false);
		world.setContactListener(cm);
		gameTime = 0;
		
		createHeli = false;
		respawnHeight = 0.0f;
		Prisoner.resetDistRun();
		RookieCop.resetDistRun();
		setTimersAndTasks();
	}
	
	/*
	 * Gets the game's TextureAnimationDrawer
	 * 
	 * @return						The game's TextureAnimationDrawer
	 */
	public static com.percipient24.cgc.art.TextureAnimationDrawer getAnimManager()
	{
		return animManager;
	}

	/*
	 * Gets the BodyFactory for the game
	 * 
	 * @return						The BodyFactory used by the game
	 */
	public static BodyFactory getBF()
	{
		return bf;
	}
	
	/*
	 * Gets the game's Camera
	 * 
	 * @return						The game's Camera
	 */
	public static Camera getCamera()
	{
		return camera;
	}

	/*
	 * Gets the ContactManager for the game
	 * 
	 * @return						The ContactManager used by the game
	 */
	public static ContactManager getCM()
	{
		return cm;
	}

	/*
	 * Gets the delta value
	 * 
	 * @return						Time since last update in ChainGame
	 */
	public static float getDelta()
	{
		return delta;
	}

	/*
	 * Gets the projection matrix the game is using for HUD elements
	 * 
	 * @return						The game's HUD projection matrix
	 */
	public static Matrix4 getHudMatrix()
	{
		return hudMatrix;
	}

	/*
	 * Gets the LayerHandler for the game
	 * 
	 * @return						The LayerHandler used by the game
	 */
	public static LayerHandler getLH()
	{
		return lh;
	}
	
	/*
	 * Gets the number of Players
	 * 
	 * @return						How many Players there are
	 */
	public static int getNumPlayers()
	{
		return numPlayers;
	}

	/*
	 * Gets the array of Players
	 * 
	 * @return						The array containing all Players
	 */
	public static Array<Player> getPlayers()
	{
		return players;
	}

	/*
	 * Gets the Prisoner/RookieCop types of all Players
	 * 
	 * @return                      An array of Players' types (true if Prisoner, false if RookieCop)
	 */
	public static boolean[] getPlayersTypes()
	{
		boolean[] playersTypes = new boolean[players.size];
		
		for (int i = 0; i < players.size; i++)
		{
			playersTypes[i] = (players.get(i) instanceof Prisoner);
		}
		
		return playersTypes;
	}

	/*
	 * Gets number of Prisoners
	 * 
	 * @return                      How many Prisoners are left alive
	 */
	public static int getNumPrisoners()
	{
		return numPrisoners;
	}
	
	/*
	 * Gets the number of free Prisoners
	 * 
	 * @return						The number of Prisoners not grabbed or tied up
	 */
	public static int getNumFreePrisoners()
	{
		int np = 0;
		
		for(Player p: players)
		{
			if (p instanceof Prisoner)
			{
				Prisoner pr = (Prisoner)p;
				if (!pr.isGrabbed() || !pr.isTiedUp())
				{
					np++;
				}
			}
		}
		return np;
	}
	/*
	 * Sets the number of Prisoners
	 * 
	 * @param newNumPrisoners		The new number of Prisoners, negative values set to 0
	 */
	public static void setNumPrisoners(int newNumPrisoners)
	{
		if (newNumPrisoners < 0)
		{
			newNumPrisoners = 0;
		}
		
		numPrisoners = newNumPrisoners;
	}

	/*
	 * Gets the array of Prisoners
	 * 
	 * @return						The array containing all Prisoners
	 */
	public static Array<Prisoner> getPrisoners()
	{
		Array<Prisoner> prs = new Array<Prisoner>();
		
		for (Player p : players)
		{
			if(p instanceof Prisoner)
			{
				prs.add((Prisoner) p);
			}
		}
		
		return prs;
	}

	/*
	 * Gets the array of living Prisoners
	 * 
	 * @return						The array containing all living Prisoners
	 */
	public static Array<Prisoner> getLivingPrisoners()
	{
		Array<Prisoner> prs = new Array<Prisoner>();
		
		for (Player p : players)
		{
			if (p instanceof Prisoner && p.isAlive())
			{
				prs.add((Prisoner) p);
			}
		}
		
		return prs;
	}

	/*
	 * Gets the number of RookieCops alive
	 * 
	 * @return						How many RookieCops are in pursuit of Prisoners
	 */
	public static int getNumCops()
	{
		return numCops;
	}

	/*
	 * Sets the number of RookieCops
	 * 
	 * @param newNumCops			The new number of RookieCops, negative values set to 0
	 */
	public static void setNumCops(int newNumCops)
	{
		if (newNumCops < 0)
		{
			newNumCops = 0;
		}
		
		numCops = newNumCops;
	}

	/*
	 * Gets the game's Random generator
	 * 
	 * @return						The game's Random generator
	 */
	public static Random getRandom()
	{
		return random;
	}

	/*
	 * Gets the respawn timer
	 * 
	 * @return						The respawn timer
	 */
	public static CGCTimer getRespawnClock()
	{
		return respawnClock;
	}

	/*
	 * Gets the last set respawn Y position
	 * 
	 * @return						Last set respawn Y position
	 */
	public static float getRespawnHeight()
	{
		return respawnHeight;
	}

	/*
	 * Gets the game world
	 * 
	 * @return						The World used by the game
	 */
	public static World getWorld()
	{
		return world;
	}
	
	/*
	 * Gets whether or not this is a boss fight
	 * 
	 * @return						Whether or not this is a boss fight
	 */
	public static boolean isBossFight()
	{
		return bossFight;
	}
	
	/*
	 * Gets the game's pause status
	 * 
	 * @return						Whether or not the game is paused
	 */
	public static boolean isPaused()
	{
		return paused;
	}
	
	/*
	 * Gets whether or not this CGCWorld is running/has run the tutorial map
	 * 
	 * @return						Whether or not the tutorial map is/was active
	 */
	public boolean isTutorial()
	{
		return tutorial;
	}
	
	/*
	 * Flags this CGCWorld to be terminated
	 */
	public static void terminate()
	{
		terminated = true;
	}
	
	/*
	 * Gets the terminated flag for this CGCWorld
	 * 
	 * @return						Whether or not this CGCWorld has been terminated
	 */
	public static boolean terminated()
	{
		return terminated;
	}
	
	/*
	 * Flags this game as won and not lost (by the Prisoners)
	 */
	public static void win()
	{
		gameWon = true;
		gameLost = false;
		startEndClock();
	}
	
	/*
	 * Flags this game as lost and not won (by the Prisoners)
	 */
	public static void lose()
	{
		gameLost = true;
		gameWon = false;
		startEndClock();
	}
	
	/*
	 * Gets if the game is won
	 * 
	 * @return						Whether or not the game has been won by the Prisoners
	 */
	public static boolean won()
	{
		return gameWon;
	}
	
	/*
	 * Gets if the game is lost
	 * 
	 * @return						Whether or not the game has been lost by the Prisoners
	 */
	public static boolean lost()
	{
		return gameLost;
	}
	
	/*
	 * Checks to see if all of the Prisoners are tied up
	 * 
	 * @return						Whether or not the Prisoners are tied up
	 */
	public static boolean allTiedUp()
	{
		for (int i = 0; i < players.size; i++)
		{
			if (players.get(i) instanceof Prisoner && players.get(i).isAlive())
			{
				if (!((Prisoner) players.get(i)).isTiedUp())
				{
					return false;
				}
			}
		}
		
		return true;
	}

	/*
	 * Determines if all the players are chained together
	 * 
	 * @return                      Whether or not the players are all chained together
	 */
	public static boolean allChainedTogether()
	{
		if (numPrisoners < numPlayers)
			return false;
		int i = 1;
		Player p = players.get(0);
		while(p.getRightPlayer() != null)
		{
			i++;
			p = p.getRightPlayer();
		}
		
		return (i == numPlayers);
	}

	/*
	 * @see com.percipient24.cgc.screens.CGCScreen#handleInput()
	 */
	public void handleInput()
	{
		if (transitioning)
		{
			super.handleInput();
			return;
		}
		
		if (paused)
		{
			pauseMenu.handleInput();
		}
		else
		{
			for(int i = 0; i < schemes.size; i++)
			{
				if (schemes.get(i).checkPause() && !justPaused)
				{
					justPaused = true;
					for (int j = 0; j < schemes.size; j++)
					{
						schemes.get(j).getController().setOuyaPause(false);
					}
					pauseGame(i);
				}
				
				schemes.get(i).drivePlayer();
			}
		}
		
		justPaused = false;
		
	    super.handleInput();
	}
	
	/*
	 * @see com.percipient24.cgc.screens.CGCScreen#render(float)
	 */
	public void render(float dt)
	{
		delta = dt;
		
		if (!lost() && !terminated())
		{
			handleInput();
		}
	}
	
	/*
	 * @see com.percipient24.cgc.screens.CGCScreen#resize(int, int)
	 */
	public void resize(int width, int height)
	{
		
		super.resize(width, height);
		
		//cleanOldCamera();
	
		float aspect = (float)width / (float)height;
		float w = 1920;
		float h = 1920.0f / aspect;
		
		if(camera != null)
		{
			camera.destroyWalls();
		}
		camera = new Camera(w, h, false);
		
		camera.zoom = 0.010416666667f;
		camera.translate(9.5f, h/(w/19f)/2f);
		camera.update();
		
		hudMatrix = new Matrix4().setToOrtho2D(0f, 0f, (float)Data.ACTUAL_WIDTH, (float)Data.ACTUAL_HEIGHT);
		
		sBatch.setProjectionMatrix(camera.combined);
		
		adjustCamera(width, height);
		
		if (pauseMenu != null)
		{
			pauseMenu.resize();
		}
	}

	/*
	 * Pauses the game
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#pause()
	 */
	public static void pauseGame(int pauseAdapter) 
	{
		ControlAdapter pauseBoss = schemes.get(pauseAdapter).getController();
		
		TimerManager.setPause(true);
		SoundManager.pause(true);
		
		pauseMenu.setPauseController(pauseBoss.getController());
		pauseMenu.resetSelected();
		pauseMenu.showPauseMenu();
		
		paused = true;
	}
	
	/*
	 * Resumes the game
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#resume()
	 */
	public static void resumeGame() 
	{
		if (pauseMenu.getShow())
		{
			ControlAdapter pauseBoss = pauseMenu.getPauseBoss();
			
			pauseBoss.getCurrent().resetData();
			
			Array<ControlAdapter> possibleBosses = pauseMenu.getPossibleBosses();
			for (ControlAdapter ca: possibleBosses)
			{
				ca.changeControlState(ControlType.PAUSE, false);
			}
			
			pauseMenu.setShow(false);
		}
		TimerManager.setPause(false);
		SoundManager.pause(false);
		
		pauseMenu.setShow(false);
		paused = false;
	}
	
	/*
	 * Handles end game stats
	 * 
	 * @param won					Whether or not this boss fight was won (by the Prisoners)
	 */
	public static void endGameStats(boolean won)
	{
		camera.destroyWalls();
		if (Options.storedTrackingOption)
		{
			ChaseApp.stats.getGame().gameTime = gameTime/1000;
			ChaseApp.stats.getGame().won = won;
			ChaseApp.stats.finishGame();
		}
	}
	
	/*
	 * Gets the average Y-value of all living players or the recently deceased if the game is over
	 * 
	 * @return                      The average Y-value of all living players
	 */
	public static float getPlayerAverageY()
	{
		float avg = 0.0f;
		float living = 0.0f;
		
		if (!gameLost)
		{
			for(int i = 0; i < players.size; i++)
			{
				Player p = players.get(i);
				
				if (p.isAlive() && (p instanceof RookieCop || !(((Prisoner)p).isTiedUp())))
				{
					avg += p.getPosition().y;
					living++;
				}
			}
		}
		else
		{
			for(int i = 0; i < recentlyDeceased.size; i++)
			{
				Player p = recentlyDeceased.get(i);
				
				avg += p.getBody().getPosition().y;
				living++;
				
			}
		}
	
		if (living == 0)
		{
			return camera.position.y;
		}
		return avg/living;
		
	}
	
	/*
	 * Gets the distance of the Player closest to the start
	 * 
	 * @return						The distance of the closest Player
	 */
	public static float getPlayerMinimum()
	{
		float min = Float.MAX_VALUE;
		
		for (int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			
			if (p.isAlive() && p.getPosition().y < min)
			{
				min = p.getPosition().y;
			}
		}
		
		if(numPlayers > 0)
		{
			return min;
		}
		else
		{
			return Float.MIN_VALUE;
		}
	}
	
	/*
	 * Gets the distance of the Player furthest from the start
	 * 
	 * @return						The distance of the furthest Player
	 */
	public static float getPlayerMaximum()
	{
		float max = -1;
		for (int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			
			if (p.isAlive() && p.getPosition().y > max)
			{
				max = p.getPosition().y;
			}
		}
		return max;
	}
	
	/*
	 * Gets the average X and Y values for all Players in the Array
	 * 
	 * @param pls                   The Array of Players to be averaged
	 * @return                      The average position vector of those Players
	 */
	public static Vector2 getAveragePlayerArrayVec(Array<Player> pls)
	{
		Vector2 avg = new Vector2();
		int living = 0;
		
		for(int i = 0; i < pls.size; i++)
		{
			Player p = pls.get(i);
			
			if (p.isAlive() && (p instanceof RookieCop && (((Prisoner)p).isTiedUp())))
			{
				Vector2 pos = pls.get(i).getPosition();
				
				avg = avg.add(pos);
				living++;
			}
		}
		return avg.scl(1.0f/living);
	}

	/*
	 * Gets the average Y position of all Prisoners
	 * 
	 * @return						The average Y position of all Prisoners
	 */
	public static float getPrisonerAverageY()
	{
		float avg = 0;
		int living = 0;
		
		for(int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			
			if (!(p instanceof Prisoner))
			{
				continue;
			}
			
			if (p.isAlive() && !(((Prisoner)p).isTiedUp()))
			{
				avg += p.getBody().getPosition().y;
				living++;
			}
		}
		
		if (living == 0)
		{
			return camera.position.y;
		}
		return avg/living;
	}
	
	/*
	 * Gets the average position of all Prisoners
	 * 
	 * @return						The average position Vector2 of all Prisoners
	 */
	public static Vector2 getPrisonerAverageVec()
	{
		Vector2 avg = new Vector2(0, 0);
		int living = 0;
		
		for(int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			
			if (!(p instanceof Prisoner))
			{
				continue;
			}
			
			if (p.isAlive() && !(((Prisoner)p).isTiedUp()))
			{
				avg = avg.add(p.getPosition());
				living++;
			}
		}
		return avg.scl(1.0f/living);
	}
	
	/*
	 * Gets the distance of the Prisoner closest to the start
	 * 
	 * @return						The distance of the closest Prisoner
	 */
	public static float getPrisonerMinimum()
	{
		float min = Float.MAX_VALUE;
		
		for (int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			
			if (p.isAlive() && p instanceof Prisoner && !(((Prisoner)p).isTiedUp()) && p.getPosition().y < min)
			{
				min = p.getPosition().y;
			}
		}
		
		if(numPrisoners > 0)
		{
			return min;
		}
		else
		{
			return Float.MIN_VALUE;
		}
	}
	
	/*
	 * Gets whether or not all untied Prisoners have escaped this CGCWorld
	 * 
	 * @return						Whether or not the untied Prisoners have escaped this CGCWorld
	 */
	public static boolean allUntiedPastCamera()
	{
		for (int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			
			if (p instanceof Prisoner && p.isAlive() && !((Prisoner)p).isTiedUp())
			{
				if (camera.isOnScreenDir(p) != -1)
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	/*
	 * Gets the distance of the Prisoner farthest from the start
	 * 
	 * @return						The distance of the farthest Prisoner
	 */
	public static float getPrisonerMaximum()
	{
		float max = -1;
		for (int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			
			if (p.isAlive() && p instanceof Prisoner && !(((Prisoner)p).isTiedUp())&& p.getPosition().y > max)
			{
				max = p.getPosition().y;
			}
		}
		return max;
	}
	
	/*
	 * Gets the average Y-value of all living RookieCops
	 * 
	 * @return                      The average Y-value of all living RookieCops
	 */
	public static float getCopAverage()
	{
		float avg = 0.0f;
		int lc = 0;
		if (!terminated)
		{
			for(int i = 0; i < players.size; i++)
			{
				Player p = players.get(i);
				
				if (p.isAlive() && p instanceof RookieCop)
				{
					avg += p.getPosition().y;
					lc++;
				}
			}
		}
		
		if (lc > 0)
		{
			return avg/lc;
		}
		return Float.MIN_VALUE;
	}
	
	/*
	 * Gets the average position of all living RookieCops
	 * 
	 * @return                      The average position of all living RookieCops
	 */
	public static Vector2 getCopAverageVec()
	{
		Vector2 avg = new Vector2(0, 0);
		int lc = 0;
		if (!terminated)
		{
			for(int i = 0; i < players.size; i++)
			{
				Player p = players.get(i);
				
				if (p.isAlive() && p instanceof RookieCop)
				{
					avg = avg.add(p.getBody().getWorldCenter().cpy());
					lc++;
				}
			}
		}
		
		if (lc > 0)
		{
			return avg.scl(1.0f/lc);
		}
		return Vector2.Zero;
	}
	
	/*
	 * Gets the distance vector between the min and max Players in an array
	 * 
	 * @param pls					The array of Players to be measured
	 * @return						The distance vector between the min and max values
	 */
	public static Vector2 distance(Array<Player> pls)
	{
		Vector2 min = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
		Vector2 max = new Vector2(Float.MIN_VALUE, Float.MIN_VALUE);
		
		for(int i = 0; i < pls.size; i++)
		{
			Player p = pls.get(i);
			if (p.isAlive() && (p instanceof RookieCop || !(((Prisoner)p).isTiedUp())))
			{
				Vector2 pos = p.getPosition();
				
				min.x = Math.min(pos.x, min.x);
				min.y = Math.min(pos.y, min.y);
				
				max.x = Math.max(pos.x, max.x);
				max.y = Math.max(pos.y, max.y);
			}
		}
		
		return max.sub(min).scl(Data.BOX_TO_WORLD);
	}
	
	/*
	 * Gets the Y distance between the min and max Prisoners
	 * 
	 * @return                      The Y distance between the highest and lowest Prisoners
	 */
	public static float prisonerDistance()
	{
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		
		for(int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			if (p instanceof Prisoner)
			{
				Prisoner pr = (Prisoner)p;
				if (pr.isAlive() && !pr.isGrabbed() && !pr.isTiedUp())
				{
					Vector2 pos = p.getPosition();
					
					min = Math.min(pos.y, min);
					max = Math.max(pos.y, max);
				}
			}
		}
		
		return (max-min)*Data.BOX_TO_WORLD;
	}
	
	/*
	 * Gets the Y distance between the min and max RookieCops
	 * 
	 * @return                      The Y distance between the highest and lowest RookieCops
	 */
	public static float copDistance()
	{
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		
		for(int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			if (p.isAlive() && p instanceof RookieCop)
			{
				Vector2 pos = p.getPosition();
				
				min = Math.min(pos.y, min);
				max = Math.max(pos.y, max);
			}
		}
		
		return (max-min)*Data.BOX_TO_WORLD;
	}
	
	/*
	 * Gets the distance the Boss is from the Prisoners
	 * 
	 * @param boss					The Boss to check for
	 * @return						The distance the Prisoners are from the Boss
	 */
	public static float getDistToBoss(Boss boss)
	{
		if(!terminated)
		{
			return getPrisonerAverageY() - boss.getBody().getPosition().y;
		}
		return 9.9f;
	}
	
	/*
	 * Gets the relative screen position of the specified object
	 * 
	 * @param ge					The GameEntity to locate
	 * @return						The position of the GameEntity relative to the Camera
	 */
	public static Vector2 getRelativeScreenPosition(GameEntity ge)
	{
		return camera.posRelativeToCamera(ge);
	}
	
	/*
	 * Gets the number of chain links in a chain between two players
	 * 
	 * @return						The number of chains
	 */
	public static int gNumChains()
	{
		return numChainsInLink;
	}
	
	/*
	 * Creates the Timers and Tasks
	 */
	public void setTimersAndTasks()
	{
		endTask = new Timer.Task()
		{
			public void run()
			{	
				Gdx.app.log("timer", "Ending game");
				endGameWorld(won());
			}
		};
		
		endClock = new CGCTimer(endTask, endTime, false, "endClock");
		
		respawnTask = new Timer.Task()
		{
			public void run()
			{
				respawnHeight = getPlayerAverageY();
			}
		};
		
		respawnClock = new CGCTimer(respawnTask, CAMERA_DELAY, (int)respawnTime * 60);
		respawnClock.name = "respawnClock";
	}

	/*
	 * Controls the respawn timer for dead Players
	 * 
	 * @param p						The Player who is about to respawn
	 */
	public void startRespawnClock(Player p)
	{
		if (!recentlyDeceased.contains(p, true))
		{
			recentlyDeceased.add(p);
			
			if (p instanceof Prisoner)
			{
				numPrisoners--;
			}
			else
			{
				numCops--;
			}
		}
		
		if (!respawnClock.isRunning() && !endClock.isRunning())
		{	
			if (this instanceof ChainGame)
			{
				createHeli = true;
			}
			
			respawnClock.reset();
			
			TimerManager.addTimer(respawnClock);
		}
	}

	/*
	 * Starts the end timer (endClock) if it hasn't already
	 */
	public static void startEndClock()
	{
		if (!endClock.isRunning())
		{
			TimerManager.addTimer(endClock);
		}
	}
	
	/*
	 * Determines what kind of boss the players are going to be fighting
	 * 
	 * @return						The boss the players will be fighting as a BossType
	 */
	protected BossType determineBossType()
	{
		return BossType.TRAIN_RUSH;
		/*
		int numCops = 0;
		for (Player p : players)
		{
			if (p instanceof RookieCop)
			{
				numCops++;
				continue;
			}
			
			if (p instanceof Prisoner && !p.isAlive())
			{
				numCops++;
				continue;
			}
		}
		
		switch (numCops)
		{
			case 0: return BossType.TRAIN_RUSH; //TODO - UNCOMMENT ME WHEN I WORK
			case 2: return BossType.TANK_AI;
			case 1:
			case 3: return BossType.TANK;
			case 4:
			case 5:
			case 6:
			case 7: return BossType.TRENCH_RUN;
			default: return BossType.TANK_AI;
		}
		//*/

		/*
		switch(getMaxChainLength())
		{
			case 0:
				if (numCops == 0 || numCops == 2)
				{
					return BossType.TANK_AI;
				}
				else if (numCops == 1 || numCops == 3)
				{
					return BossType.TANK;
				}
				else if (numCops >= 4 && numCops <= 7)
				{
					return BossType.TANK_AI;
					//return BossType.TRENCH_RUN;
				}
				break;
			case 1:
				if (numCops == 0 || numCops == 2)
				{
					return BossType.TANK_AI;
				}
				else if (numCops == 1 || numCops == 3)
				{
					return BossType.TANK;
				}
				else if (numCops >= 4 && numCops <= 6)
				{
					return BossType.TANK_AI;
					//return BossType.TRENCH_RUN;
				}
				break;
			case 2:
				if (numCops == 0 || numCops == 2)
				{
					return BossType.TANK_AI;
				}
				else if (numCops == 1 || numCops == 3)
				{
					return BossType.TANK;
				}
				else if (numCops == 4 || numCops == 5)
				{
					return BossType.TANK_AI;
					//return BossType.TRENCH_RUN;
				}
				break;
			case 3:
				if (numCops == 0 || numCops == 2)
				{
					return BossType.STEEL_HORSE_AI;
				}
				else if (numCops == 1)
				{
					return BossType.STEEL_HORSE;
				}
				else if (numCops == 3 || numCops == 4)
				{
					return BossType.PALL_BEARER;
				}
				break;
			case 4:
				if (numCops == 0 || numCops == 2)
				{
					return BossType.STEEL_HORSE_AI;
				}
				else if (numCops == 1)
				{
					return BossType.STEEL_HORSE;
				}
				else if (numCops == 3)
				{
					return BossType.PALL_BEARER;
				}
				break;
			case 5: 
				if (numCops == 0 || numCops == 2)
				{
					return BossType.STEEL_HORSE_AI;
				}
				else if (numCops == 1)
				{
					return BossType.STEEL_HORSE;
				}
				break;
			case 6: 
				if (numCops == 0)
				{
					return BossType.STEEL_HORSE_AI;
				}
				else if (numCops == 1)
				{
					return BossType.STEEL_HORSE;
				}
				break;
			case 7: 
				if (numCops == 0)
				{
					return BossType.TRAIN_RUSH;
				}
				break;
			default: myApp.alert("Boss Fight", "How did you get that number of chains?");
					break;
		};
		
		return BossType.NONE;//*/
	}
	
	/*
	 * Determines the max chain length among all Prisoners
	 * 
	 * @return						The max chain length
	 */
	private int getMaxChainLength()
	{
		int maxChainLength = 0;
		int curChainLength = 0;
		
		for (int i = 0; i < ChainGame.numPlayers; i++)
		{
			if (players.get(i) instanceof Prisoner && players.get(i).isAlive()) // Player is prisoner
			{
				if (players.get(i).getRightJoint() != null) // Prisoner still has chain to right
				{
					curChainLength++;
				}
				else // Chain broken, reset counter
				{
					if (curChainLength > maxChainLength)
					{
						maxChainLength = curChainLength;
					}
					curChainLength = 0;
				}
			}
			else // Chain broken, reset counter
			{
				if (curChainLength > maxChainLength)
				{
					maxChainLength = curChainLength;
				}
				curChainLength = 0;
			}
		}

		return maxChainLength;
	}
	
	/*
	 * Adjusts the camera dimensions to the new width and height
	 *
	 * @param width					The new width of the window
	 * @param height				The new height of the window
	 */
	protected void adjustCamera(int width, int height)
	{
		
	}

	/*
	 * The method for ending the game in the world
	 */
	public abstract void endGameWorld(boolean won);

	/*
	 * The method for spawning cops in the world
	 */
	public abstract void spawnCops();
	
	/*
	 * Gets the completion percentage of the game-ending clock
	 * 
	 * @return						The completion percentage of the game-end clock
	 */
	public static float getEndPercent()
	{
		return endClock.getPercent();
	}
	
	/*
	 * Clears all remnants of the world - call before creating a new one
	 */
	public static void clearWorld()
	{
		for (int i = 0; i < getPrisoners().size; i++)
		{
			getPrisoners().get(i).endOfLevelBreakGrabs();
		}
		camera.destroyWalls();
		world.dispose(); // Call anything involving joints, bodies, or fixtures before this
		toDestroyList.clear();
		Gdx.app.log("Game", "Clearing world");
	}
	
	/*
	 * Destroys the body for each entity in the list and then removes them
	 */
	public static void emptyDestroyList()
	{
		if (!world.isLocked())
		{
			while(toDestroyList.size > 0)
			{
				if (toDestroyList.get(0).getBody() != null)
				{
					Body b = toDestroyList.get(0).getBody();
					Array<Fixture> fixes = new Array<Fixture>();
					world.getFixtures(fixes);
					if (fixes.contains(b.getFixtureList().first(), false)) {
						world.destroyBody(b);
					}
				}
				toDestroyList.get(0).finalCleanup();
				toDestroyList.get(0).removeFromWorldLayers(lh);
				toDestroyList.removeValue(toDestroyList.get(0), true);
			}
		}
	}
	
	/*
	 * Adds an entity to the destroy list
	 * 
	 * @param toDestroy				The entity to be destroyed
	 */
	public static boolean addToDestroyList(GameEntity toDestroy)
	{
		if (!toDestroyList.contains(toDestroy, true))
		{
			toDestroyList.add(toDestroy);
			toDestroy.initCleanup();
			return true;
		}
		else
		{
			return false;
		}
	}
} // End class