/*
 * @(#)CharacterSelect.java		0.3 14/4/8
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import java.util.Arrays;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.percipient24.tweens.*;
import com.badlogic.gdx.utils.Array;
import com.percipient24.b2helpers.StringLayout;
import com.percipient24.cgc.ChainGame;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.net.MapVO;
import com.percipient24.cgc.overlays.Transition;
import com.percipient24.cgc.screens.helpers.ControllerDrawer;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.cgc.screens.helpers.MenuTextureRegionComparator;
import com.percipient24.cgc.screens.helpers.PlayerCard;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;

/*
 * Enables players to select their characters before the game starts
 * 
 * @version 0.3 14/4/8
 * @author William Ziegler
 * @author Christopher Rider
 */
public class CharacterSelect extends CGCScreen
{
	private final int MAX_PLAYERS = 8;
	
	// How far apart each character portrait is from each other
	private float portraitXIncrement;
	private float convictPortraitY;
	private float copPortraitY;
	
	private final float WHITE_BANNER_Y = 0.69f;
	private final float WHITE_BANNER_HEIGHT = 0.12f;
	
	private final float BLACK_BANNER_Y = 0.7f;
	private final float BLACK_BANNER_HEIGHT = 0.1f;
	
	private boolean forgetPlayers = true;
	
	private int numPlayers;
	private int numPortraitsToShow;
	private int[] framesHeld;
	
	public static Array<MenuTextureRegion> convictPortraits;
	private Array<MenuTextureRegion> sortedConvictPortraits;
	public static Array<MenuTextureRegion> copPortraits;
	private Array<MenuTextureRegion> sortedCopPortraits;
	public static Array<MenuTextureRegion> convictThumbnails;
	public static Array<MenuTextureRegion> copThumbnails;
	
	public static boolean tutorial = false;
	
	private int numConvictChoices;
	private int numCopChoices;
	
	private Array<PlayerCard> playerCards;
	
	private ShapeRenderer shapes;
	
	private Transition transition;
	private boolean transitioning;
	private TweenManager tManager;
	
	private MenuTextureRegion backgroundRepeater;
	private MenuTextureRegion leftNumbers;
	private MenuTextureRegion rightNumbers;
	
	private ControllerDrawer leftJoin;
	private ControllerDrawer rightJoin;
	
	private ControllerDrawer leftSelect;
	private ControllerDrawer rightSelect;
	
	private ControllerDrawer leftCancel;
	private ControllerDrawer rightCancel;

	private ControllerDrawer leftWho;
	private ControllerDrawer rightWho;
	
	private ControllerDrawer advanceScreen;

	private StringLayout layout;
	
	/*
	 * Creates a new CharacterSelect object
	 * 
	 * @param app				The ChaseApp to use
	 */
	public CharacterSelect(ChaseApp app)
	{
		super(app);
		title = "Select Convicts/Cops";
		titleLayout.updateText(title);

		layout = new StringLayout("", ChaseApp.menuFont);
		
		convictPortraits = new Array<MenuTextureRegion>();
		sortedConvictPortraits = new Array<MenuTextureRegion>();
		copPortraits = new Array<MenuTextureRegion>();
		convictThumbnails = new Array<MenuTextureRegion>();
		copThumbnails = new Array<MenuTextureRegion>();
		
		numConvictChoices = ChaseApp.characterAtlas.findRegions("conportrait").size;
		numCopChoices = ChaseApp.characterAtlas.findRegions("copportrait").size;
		
		playerCards = new Array<PlayerCard>(MAX_PLAYERS);
		TextureRegion tempTextureRegion;
		
		backgroundRepeater = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("backgroundRepeat"), Vector2.Zero);
		
		leftNumbers = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("heights"), Vector2.Zero, MenuTextureRegion.MID_LEFT, MenuTextureRegion.LOWER_CENTER);
		rightNumbers = new MenuTextureRegion(ChaseApp.characterAtlas.findRegion("heights"), Vector2.Zero, MenuTextureRegion.MID_RIGHT, MenuTextureRegion.LOWER_CENTER);
		
		framesHeld = new int[input.controlList.length];
		for (int i = 0; i < MAX_PLAYERS; i++)
		{
			framesHeld[i] = 0;
			tempTextureRegion = ChaseApp.characterAtlas.findRegion("conportrait", 0);
			convictPortraits.add(new MenuTextureRegion(tempTextureRegion,
					new Vector2(-MenuTextureRegion.TITLE_SAFE_X, 0),
					MenuTextureRegion.MID_LEFT, MenuTextureRegion.LOWER_LEFT));
			
			// TODO change conportrait back to copportrait once we have cop portraits
			tempTextureRegion = ChaseApp.characterAtlas.findRegion("conportrait", 0);
			copPortraits.add(new MenuTextureRegion(tempTextureRegion,
					new Vector2(-MenuTextureRegion.TITLE_SAFE_X, 0),
					MenuTextureRegion.LOWER_LEFT, MenuTextureRegion.LOWER_LEFT));
			
			tempTextureRegion = ChaseApp.characterAtlas.findRegion("conthumbnail", 0);
			convictThumbnails.add(new MenuTextureRegion(tempTextureRegion, 
					new Vector2(0, 0), 
					MenuTextureRegion.MID_LEFT, MenuTextureRegion.LOWER_LEFT));
			tempTextureRegion = ChaseApp.characterAtlas.findRegion("copthumbnail", 0);
			copThumbnails.add(new MenuTextureRegion(tempTextureRegion, 
					new Vector2(0, 0), 
					MenuTextureRegion.MID_LEFT, MenuTextureRegion.LOWER_LEFT));
			playerCards.add(new PlayerCard(-1, null));
		}
		
		shapes = app.getShapes();
		tManager = ChaseApp.tManager;
		Tween.registerAccessor(MenuTextureRegion.class, new MenuTextureRegionAccessor());
		Tween.registerAccessor(Transition.class, new TransitionAccessor());
		
		prevScreen = ChaseApp.mainMenu;

		leftJoin = new ControllerDrawer(MenuTextureRegion.MID_LEFT, MenuTextureRegion.MID_LEFT);
		leftJoin.showWing(true);
		leftJoin.showAnimation(ControllerDrawer.L_BUMPER);
		leftJoin.setWiggle(23, 0);
		leftJoin.setMessage("Join\nDrop", -25, 40, Align.left);
		
		rightJoin = new ControllerDrawer(MenuTextureRegion.MID_RIGHT, MenuTextureRegion.MID_RIGHT);
		rightJoin.showWing(false);
		rightJoin.showAnimation(ControllerDrawer.R_BUMPER);
		rightJoin.setWiggle(-23, 0);
		rightJoin.setMessage("Join\nDrop", 25, 40, Align.right);
		
		leftSelect = new ControllerDrawer(MenuTextureRegion.MID_LEFT, MenuTextureRegion.MID_LEFT);
		leftSelect.showAnimation(ControllerDrawer.DPAD_DOWN);
		leftSelect.setWiggle(23, -180);
		leftSelect.setMessage("Select", -20, 80, Align.left);
		
		rightSelect = new ControllerDrawer(MenuTextureRegion.MID_RIGHT, MenuTextureRegion.MID_RIGHT);
		rightSelect.showAnimation(ControllerDrawer.FACE_DOWN);
		rightSelect.setWiggle(-23, -180);
		rightSelect.setMessage("Select", 20, 80, Align.right);
		
		leftCancel = new ControllerDrawer(MenuTextureRegion.MID_LEFT, MenuTextureRegion.MID_LEFT);
		leftCancel.showAnimation(ControllerDrawer.DPAD_RIGHT);
		leftCancel.setWiggle(23, -320);
		leftCancel.setMessage("Cancel", -20, 80, Align.left);
		
		rightCancel = new ControllerDrawer(MenuTextureRegion.MID_RIGHT, MenuTextureRegion.MID_RIGHT);
		rightCancel.showAnimation(ControllerDrawer.FACE_RIGHT);
		rightCancel.setWiggle(-23, -320);
		rightCancel.setMessage("Cancel", 20, 80, Align.right);
		
		leftWho = new ControllerDrawer(MenuTextureRegion.MID_LEFT, MenuTextureRegion.MID_LEFT);
		leftWho.showAnimation(ControllerDrawer.STICK_3_BLINK);
		leftWho.setWiggle(23, -440);
		leftWho.setMessage("Who's me?", -20, 60, Align.left);
		
		rightWho = new ControllerDrawer(MenuTextureRegion.MID_RIGHT, MenuTextureRegion.MID_RIGHT);
		rightWho.showAnimation(ControllerDrawer.STICK_3_BLINK);
		rightWho.setWiggle(-23, -440);
		rightWho.setMessage("Who's me?", 20, 60, Align.right);
		
		advanceScreen = new ControllerDrawer(MenuTextureRegion.MID_CENTER, MenuTextureRegion.MID_CENTER);
		advanceScreen.setWiggle(0, 270);
	}
	
	/*
	 * Sets up the layout of visual elements on this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#performLayout()
	 */
	public void performLayout()
	{
		super.performLayout();
		
		float upperY = 0;
		float lowerY = 0 - MenuTextureRegion.TITLE_SAFE_Y;
		
		setUpPortraitX();
		
		//Set up the character portraits and selection cursors.
		for (int i = 0; i < MAX_PLAYERS; i++)
		{
			// Calculate the Y position for each portrait and set it
			convictPortraitY = (upperY);
			convictPortraits.get(i).setY(convictPortraitY);
			convictPortraits.get(i).setRegion(getConvictPortraitByIndex(playerCards.get(i).getConvictSelection()));
			
			// Calculate the Y position for each portrait and set it
			copPortraitY = (lowerY);
			copPortraits.get(i).setY(copPortraitY);
			copPortraits.get(i).setRegion(getCopPortraitByIndex(playerCards.get(i).getCopSelection()));
		}
	}
	
	/*
	 * Set the proper X values of all of the character portraits
	 */
	private void setUpPortraitX()
	{
		float portraitStartX = 0;
		// How far apart each character portrait is from the next
		if (convictPortraits.get(0).getRegionWidth() * numPortraitsToShow > Data.MENU_WIDTH)
		{
			portraitStartX = -MenuTextureRegion.TITLE_SAFE_X;
			portraitXIncrement = (float)(Data.MENU_WIDTH - convictPortraits.get(0).getRegionWidth())
					/ (float)(numPortraitsToShow-1);
		}
		else
		{
			portraitStartX = MenuTextureRegion.TITLE_SAFE_X
					+ (((Data.MENU_WIDTH) - MenuTextureRegion.TITLE_SAFE_X) / 2f)
					- (convictPortraits.get(0).getRegionWidth() * numPortraitsToShow) / 2f;
			portraitXIncrement = convictPortraits.get(0).getRegionWidth();
		}
		
		float portraitX;
		
		//Set up the character portraits and thumbnails
		for (int i = 0; i < MAX_PLAYERS; i++)
		{	
			if (tManager.containsTarget(convictPortraits.get(i),
					MenuTextureRegionAccessor.TRANSLATE_X))
			{
				tManager.killTarget(convictPortraits.get(i));
				tManager.killTarget(copPortraits.get(i));
				tManager.killTarget(convictThumbnails.get(i));
				tManager.killTarget(copThumbnails.get(i));
			}
			
			portraitX = portraitStartX + i * portraitXIncrement;
			
			// Calculate the X position for each portrait and set it

			Tween.to(convictPortraits.get(i), MenuTextureRegionAccessor.TRANSLATE_X, 1f).target(portraitX)
			.ease(Cubic.INOUT).start(tManager);
			Tween.to(copPortraits.get(i), MenuTextureRegionAccessor.TRANSLATE_X, 1f).target(portraitX)
			.ease(Cubic.INOUT).start(tManager);
			
			moveThumbnails(i, portraitX + convictPortraits.get(0).getRegionWidth() / 2, !playerCards.get(i).getLockedInConvict());
		}
	}
	
	/*
	 * Gets the number of players who are participating in this game
	 * 
	 * @return					The number of players participating in this game
	 */
	public int getNumPlayers()
	{
		return numPlayers;
	}
	
	/*
	 * Gets the maximum allowed number of players
	 * 
	 * @return					The maximum allowed number of players
	 */
	public int getMaxPlayers()
	{
		return MAX_PLAYERS;
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
			
			// Adds players to the game if possible
			for (int i = 0; i < input.controlList.length; i++)
			{
				if (!input.controlList[i].isConnected())
				{
					// if this particular controller is not connected, skip it
					continue;
				}
				
				if (input.controlList[i].justPressed(ControlType.MENU_ACTION))
				{
					// if this particular controller just tried to jump in
					if (getCardIndex(i) == -1)
					{
						// and it is not in already, add it
						input.controlList[i].changeControlState(ControlType.MENU_ACTION, false);
						addNewPlayer(input.controlList[i]);
					}
					else
					{
						// and it is in already, remove it unless it's locked in
						if (playerCards.get(getCardIndex(i)).getLockedInCop())
						{
							playerCards.get(getCardIndex(i)).setLockedInCop(false);
						}
						else if (playerCards.get(getCardIndex(i)).getLockedInConvict())
						{
							playerCards.get(getCardIndex(i)).setLockedInConvict(false);
							moveThumbnails(getCardIndex(i), true);
						}
						else
						{
							input.controlList[i].changeControlState(ControlType.MENU_ACTION, false);
							removePlayer(getCardIndex(i));
						}
						continue;
					}
				}
				
				if (getCardIndex(i) != -1)
				{
					// if this particular controller is in the game
					boolean isHolding = false;
					
					if (input.controlList[i].justPressed(ControlType.SELECT))
					{
						// and just pressed select
						input.controlList[i].changeControlState(ControlType.SELECT, false);
						
						if (boss.equals(input.controlList[i]))
						{
							// and is the boss
							if (allConfirmed())
							{
								numPlayers = 0;
								
								// and all players have confirmed their selections
								for (int j = 0; j < input.controlList.length; j++)
								{
									// iterate through all controllers ensuring they are connected and have a cardIndex
									if (input.controlList[j].isConnected() && getCardIndex(j) != -1)
									{	
										// apply the convict choice
										input.controlList[j].setConvictChoice(
												playerCards.get(getCardIndex(j)).getConvictSelection());
										// apply the copchoice
										input.controlList[j].setCopChoice(
												playerCards.get(getCardIndex(j)).getCopSelection());
										// assign the corresponding controller
										input.controlList[j].assignControls(getCardIndex(j), j);
										numPlayers++;
									}
								}
								
								if (tutorial)
								{
									startTransition();
								}
								else
								{
									// MAP SELECT GO
									myApp.setScreen(ChaseApp.mapSelect);
								}
							}
							else
							{
								// not everyone has confirmed, so I must be selecting
								if (!playerCards.get(getCardIndex(i)).getLockedInConvict())
								{
									playerCards.get(getCardIndex(i)).setLockedInConvict(true);
									playerCards.get(getCardIndex(i)).setCopSelection(findNextEmptySelection(false));
									changePortrait(getCardIndex(i));
									moveThumbnails(getCardIndex(i), false);
								}
								else
								{
									playerCards.get(getCardIndex(i)).setLockedInCop(true);
								}
							}
						}
						else
						{
							// I'm just a regular player so I must be selecting
							if (!playerCards.get(getCardIndex(i)).getLockedInConvict())
							{
								playerCards.get(getCardIndex(i)).setLockedInConvict(true);
								playerCards.get(getCardIndex(i)).setCopSelection(findNextEmptySelection(false));
								changePortrait(getCardIndex(i));
								moveThumbnails(getCardIndex(i), false);
							}
							else
							{
								playerCards.get(getCardIndex(i)).setLockedInCop(true);
							}
						}
					}
					
					if (input.controlList[i].justPressed(ControlType.BACK))
					{
						// I just pressed Back
						if (boss.equals(input.controlList[i]))
						{
							// and I'm the boss
							if (!playerCards.get(getCardIndex(i)).getLockedInConvict())
							{
								// and I was not yet confirmed
								// I must want to go back in the menu
								handleB(boss, keyboardLeft, keyboardRight);
							}
							else if (playerCards.get(getCardIndex(i)).getLockedInCop())
							{
								// I was confirmed
								// so I should cancel my cop selection
								playerCards.get(getCardIndex(i)).setLockedInCop(false);
							}
							else
							{
								// I was confirmed
								// so I should cancel my convict selection
								playerCards.get(getCardIndex(i)).setLockedInConvict(false);
								moveThumbnails(getCardIndex(i), true);
							}
						}
						else
						{
							if (playerCards.get(getCardIndex(i)).getLockedInCop())
							{
								// I was confirmed
								// so I should cancel my cop selection
								playerCards.get(getCardIndex(i)).setLockedInCop(false);
							}
							else
							{
								// I was confirmed
								// so I should cancel my convict selection
								playerCards.get(getCardIndex(i)).setLockedInConvict(false);
								moveThumbnails(getCardIndex(i), true);
							}
						}
	
						input.controlList[i].changeControlState(ControlType.BACK, false);
					}
					
					if (input.controlList[i].justPressed(ControlType.CALLOUT))
					{
						// wiggle as a callout
						calloutWiggle(getCardIndex(i));
						input.controlList[i].changeControlState(ControlType.CALLOUT, false);
					}
					
					if (!playerCards.get(getCardIndex(i)).getLockedInConvict())
					{
						if (input.controlList[i].isPressed(ControlType.LEFT))
						{
							// going left
							isHolding = true;
							handleHeldMovement(i, false, true);
						}
						else if (input.controlList[i].isPressed(ControlType.RIGHT))
						{
							// going right
							isHolding = true;
							handleHeldMovement(i, true, true);
						}
						
						if(isHolding == false)
						{
							// cancel framesHeld if no direction was held
							framesHeld[i] = 0;
						}
					}
					else if (!playerCards.get(getCardIndex(i)).getLockedInCop())
					{
						if (input.controlList[i].isPressed(ControlType.LEFT))
						{
							// going left
							isHolding = true;
							handleHeldMovement(i, false, false);
						}
						else if (input.controlList[i].isPressed(ControlType.RIGHT))
						{
							// going right
							isHolding = true;
							handleHeldMovement(i, true, false);
						}
						
						if(isHolding == false)
						{
							// cancel framesHeld if no direction was held
							framesHeld[i] = 0;
						}
					}
				}
				else
				{
					// I'm not participating
					if (input.controlList[i] == boss)
					{
						// but I am the boss
						if (input.controlList[i].justPressed(ControlType.BACK))
						{
							// and I just pressed back
							handleB(boss, keyboardLeft, keyboardRight);
							input.controlList[i].changeControlState(ControlType.BACK, false);
						}
					}
				}
			}
		}
		super.handleInput();
	}
	
	/*
	 * A convenience function to cut down on repetition in handleInput
	 * 
	 * @param i						The index of the controller in question
	 * @param isPositive			Direction of movement through the array (true is positive, false is negative)
	 * @param isConvict				Whether to adjust the selection for the convict (true) or the cop (false)
	 * 
	 */
	private void handleHeldMovement(int i, boolean isPositive, boolean isConvict)
	{
		framesHeld[i]++;
		if (framesHeld[i] == 1)
		{
			moveSelection(getCardIndex(i), isPositive, isConvict);
			changePortrait(getCardIndex(i));
		}
		else if (framesHeld[i] > 90 &&
				framesHeld[i] % 5 == 0)
		{
			moveSelection(getCardIndex(i), isPositive, isConvict);
			changePortrait(getCardIndex(i));
		}
		else if (framesHeld[i] > 10 &&
				framesHeld[i] % 10 == 0)
		{
			moveSelection(getCardIndex(i), isPositive, isConvict);
			changePortrait(getCardIndex(i));
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
		
		tManager.update(delta);
		
		sBatch.setProjectionMatrix(ChaseApp.menuCam.combined);
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);

		for(int i = 0; i < 96; i++)
		{
			sBatch.draw(
					/* region */ backgroundRepeater.getRegion(),
					/*  pos x */ i*20,
					/*  pos y */ 0,
					/*  ori x */ 0,
					/*  ori y */ 0,
					/* size X */ 20,
					/* size Y */ 1080,
					/* scales */ 1,1,
					/* rotate */ 0);
		}
		
		leftNumbers.draw(sBatch);
		rightNumbers.draw(sBatch);
		
		MenuTextureRegion drawPortrait;
		
		sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		// Draw the convict portraits
		for (int i = 0; i < sortedConvictPortraits.size; i++)
		{
			PlayerCard tempCard = findCardBySortedConvict(i);
			
			if (tempCard.isUsed())
			{
				drawPortrait = sortedConvictPortraits.get(i);
			
				if (tempCard.getConvictSelection() != -1)
				{
					if (tempCard.getLockedInConvict())
					{
						//Dim convict portraits slightly if the player has
						//locked in their convict choice.
						sBatch.setColor(0.8f, 0.8f, 0.8f, 1.0f);
						drawPortrait.draw(sBatch);
						sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
					}
					else
					{
						drawPortrait.draw(sBatch);
					}
				}
			}
		}
		
		sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		//Draw the cop portraits
		for (int i = 0; i < sortedCopPortraits.size; i++)
		{
			PlayerCard tempCard = findCardBySortedCop(i);
			
			if (tempCard.getCopSelection() != -1 && tempCard.getLockedInConvict())
			{	
				drawPortrait = sortedCopPortraits.get(i);
				if (tempCard.getLockedInCop())
				{
					//Dim cop portraits slightly if the player has
					//locked in their cop choice.
					sBatch.setColor(0.8f, 0.8f, 0.8f, 1.0f);
					drawPortrait.draw(sBatch);
					sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
				else
				{
					drawPortrait.draw(sBatch);
				}
			}
		}
		
		sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		// Draw the thumbnails
		for (int i = 0; i < playerCards.size; i++)
		{			
			if (playerCards.get(i).getConvictSelection() != -1)
			{
				if (playerCards.get(i).getLockedInConvict())
				{
					//If the player has locked in their convict,
					//dim the convict thumbnail and show the cop one
					sBatch.setColor(0.8f, 0.8f, 0.8f, 1.0f);
					convictThumbnails.get(i).draw(sBatch);
					sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
					
					//If the cop is locked in too, dim the cop one
					//Otherwise, show the cop in full color
					if (playerCards.get(i).getLockedInCop())
					{
						sBatch.setColor(0.8f, 0.8f, 0.8f, 1.0f);
						copThumbnails.get(i).draw(sBatch);
						sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
					}
					else
					{
						copThumbnails.get(i).draw(sBatch);
					}
				}
				//If the player has not locked their convict,
				//Just show the convict thumbnail in full color
				else
				{
					convictThumbnails.get(i).draw(sBatch);
				}
			}
		}
		sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		leftJoin.draw(sBatch, delta);
		rightJoin.draw(sBatch, delta);
		
		leftCancel.draw(sBatch, delta);
		rightCancel.draw(sBatch, delta);
		
		leftSelect.draw(sBatch, delta);
		rightSelect.draw(sBatch, delta);
		
		leftWho.draw(sBatch, delta);
		rightWho.draw(sBatch, delta);
		
		sBatch.end();
		
		String upperString;
		String lowerString;
		
		ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
		if (noneUsed())
		{
			upperString = "No Controllers Set Up!";
			lowerString = "Press LB/RB to set up controllers.";
			
			// Draws the info banner on the screen - Width of screen, height is arbitrary
			shapes.begin(ShapeType.Filled);
			shapes.setColor(ChaseApp.selectedOrange);
			shapes.rect(0, Data.MENU_HEIGHT * WHITE_BANNER_Y, 
					Data.GAME_WIDTH, Data.MENU_HEIGHT * WHITE_BANNER_HEIGHT);
			shapes.end();
			
			// Draws the inside of the info banner
			shapes.begin(ShapeType.Filled);
			shapes.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			shapes.rect(0, Data.MENU_HEIGHT * BLACK_BANNER_Y, 
					Data.GAME_WIDTH, Data.MENU_HEIGHT * BLACK_BANNER_HEIGHT);
			shapes.end();
			
			sBatch.begin();
			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
			layout.updateText(upperString);
			ChaseApp.menuFont.draw(
				sBatch,
				layout.getLayout(),
				Data.MENU_WIDTH / 2 - layout.getLayout().width / 2, 
				Data.MENU_HEIGHT * (BLACK_BANNER_Y + (BLACK_BANNER_HEIGHT * .9f)));

			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
			layout.updateText(lowerString);
			ChaseApp.menuFont.draw(
				sBatch,
				layout.getLayout(),
				Data.MENU_WIDTH / 2 - layout.getLayout().width / 2, 
				Data.MENU_HEIGHT * (BLACK_BANNER_Y + (BLACK_BANNER_HEIGHT * .4f)));

			ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
			sBatch.end();
			sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
		else if (allConfirmed())
		{
			// Same banners
			shapes.begin(ShapeType.Filled);
			shapes.setColor(ChaseApp.selectedOrange);
			shapes.rect(0, Data.MENU_HEIGHT * WHITE_BANNER_Y,
					Data.GAME_WIDTH, Data.MENU_HEIGHT * WHITE_BANNER_HEIGHT);
			shapes.end();
			
			shapes.begin(ShapeType.Filled);
			shapes.setColor(0.0f, 0.0f, 0.0f, 1.0f);
			shapes.rect(0, Data.MENU_HEIGHT * BLACK_BANNER_Y,
					Data.GAME_WIDTH, Data.MENU_HEIGHT * BLACK_BANNER_HEIGHT);
			shapes.end();
			
			if (!bossConnected())
			{
				upperString = "No Start Controller!";
				lowerString = "The start controller must be set up to play.";
				
				sBatch.begin();

				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);
				layout.updateText(upperString);
				ChaseApp.menuFont.draw(
					sBatch,
					layout.getLayout(),
					Data.MENU_WIDTH / 2 - layout.getLayout().width / 2,
					Data.MENU_HEIGHT * (BLACK_BANNER_Y + (BLACK_BANNER_HEIGHT * .9f)));

				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
				layout.updateText(lowerString);
				ChaseApp.menuFont.draw(
					sBatch,
					layout.getLayout(),
					Data.MENU_WIDTH / 2 - layout.getLayout().width / 2,
					Data.MENU_HEIGHT * (BLACK_BANNER_Y + (BLACK_BANNER_HEIGHT * .4f)));

				ChaseApp.menuFont.getData().setScale(0.5f);

				sBatch.end();
				sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
			else
			{
				upperString = "All Players Ready!";
				lowerString = "to continue.";
				
				sBatch.begin();
				ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_MAIN);

				layout.updateText(upperString);
				ChaseApp.menuFont.draw(
					sBatch,
					layout.getLayout(),
					Data.MENU_WIDTH / 2 - layout.getLayout().width - Data.MENU_WIDTH * .05f, 
					Data.MENU_HEIGHT * (BLACK_BANNER_Y + (BLACK_BANNER_HEIGHT * .7f)));

				layout.updateText(lowerString);
				ChaseApp.menuFont.draw(
					sBatch,
					layout.getLayout(),
					Data.MENU_WIDTH / 2 + Data.MENU_WIDTH * .05f, 
					Data.MENU_HEIGHT * (BLACK_BANNER_Y + (BLACK_BANNER_HEIGHT * .7f)));

				ChaseApp.menuFont.getData().setScale(0.5f);
				advanceScreen.draw(sBatch, delta);
				sBatch.end();
				sBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		ChaseApp.menuFont.getData().setScale(CGCScreen.FONT_SIDE);
		
		if (transition != null && transition.getShow())
		{
			transition.render(delta);
			sBatch.setProjectionMatrix(ChaseApp.menuCam.combined);
		}
	}
	
	/*
	 * Show this screen
	 * 
	 * @see com.badlogic.gdx.Screen#show()
	 */
	public void show()
	{
		super.show();
		
		shapes.setProjectionMatrix(sBatch.getProjectionMatrix());
		
		framesHeld = new int[input.controlList.length];
		
		if (forgetPlayers)
		{
			for (int i = 0; i < MAX_PLAYERS; i++)
			{
				removePlayer(0);
			}
			
			numPlayers = 0;
			numPortraitsToShow = 1;
			
			for (int i = 0; i < input.controlList.length; i++)
			{
				if (input.controlList[i] == input.getBoss())
				{
					addNewPlayer(input.controlList[i]);
					break;
				}
			}
		}
		
		forgetPlayers = true;
		setUpPortraitX();
		
		if (input.getBoss().isLeft())
		{
			advanceScreen.showAnimation(ControllerDrawer.DPAD_DOWN);
		}
		else
		{
			advanceScreen.showAnimation(ControllerDrawer.FACE_DOWN);
		}
	}
	
	/*
	 * Hide this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#hide()
	 */
	public void hide()
	{
		killWiggleTweens();
	}
	
	/*
	 * Sets whether or not the CharacterSelect screen should forget the players
	 * 
	 * @param forgetPlayers			Whether or not this screen should forget the players
	 */
	public void setForgetPlayers(boolean forgetPlayers)
	{
		this.forgetPlayers = forgetPlayers;
	}
	
	/*
	 * Determines if all players have confirmed their character selections
	 * 
	 * @return						Whether or not all players have chosen characters
	 */
	private boolean allConfirmed()
	{
		int numIn = 0;
		
		for (int i = 0; i < MAX_PLAYERS; i++)
		{
			if (playerCards.get(i).isUsed())
			{
				if (!playerCards.get(i).getLockedInCop())
				{
					return false;
				}
				
				numIn++;
			}
		}
		
		if (numIn <= 0)
		{
			return false;
		}
		
		return true;
	}
	
	/*
	 * Determines if the boss controller is connected to this screen
	 * 
	 * @return						Whether or not the boss is connected to this screen
	 */
	private boolean bossConnected()
	{
		for (int i = 0; i < MAX_PLAYERS; i++)
		{
			if (playerCards.get(i).isUsed())
			{
				if (playerCards.get(i).getAdapter() == input.getBoss())
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*
	 * Helper method to move player selections through the convict and cop arrays
	 * 
	 * @param cardIndex				The index of the PlayerCard for this cursor
	 * @boolean isPositive			Whether to move in the positive/negative (true/false) direction
	 * @param isConvict				Whether to adjust the selection for the convict (true) or the cop (false)
	 * 
	 */
	private void moveSelection(int cardIndex, boolean isPositive, boolean isConvict)
	{
		if (isConvict)
		{
			if (isPositive)
			{
				if (playerCards.get(cardIndex).getConvictSelection() == numConvictChoices - 1)
				{
					playerCards.get(cardIndex).setConvictSelection(0);
				}
				else
				{
					playerCards.get(cardIndex).adjustSelection(1, isConvict);
				}
			}
			else
			{
				if (playerCards.get(cardIndex).getConvictSelection() == 0)
				{
					playerCards.get(cardIndex).setConvictSelection(numConvictChoices - 1);
				}
				else
				{
					playerCards.get(cardIndex).adjustSelection(-1, isConvict);
				}
			}
			
			if (isSelectionTaken(playerCards.get(cardIndex).getConvictSelection(), cardIndex, isConvict))
			{
				moveSelection(cardIndex, isPositive, isConvict);
			}
		}
		else
		{
			if (isPositive)
			{
				if (playerCards.get(cardIndex).getCopSelection() == numCopChoices - 1)
				{
					playerCards.get(cardIndex).setCopSelection(0);
				}
				else
				{
					playerCards.get(cardIndex).adjustSelection(1, isConvict);
				}
			}
			else
			{
				if (playerCards.get(cardIndex).getCopSelection() == 0)
				{
					playerCards.get(cardIndex).setCopSelection(numCopChoices - 1);
				}
				else
				{
					playerCards.get(cardIndex).adjustSelection(-1, isConvict);
				}
			}
			
			if (isSelectionTaken(playerCards.get(cardIndex).getCopSelection(), cardIndex, isConvict))
			{
				moveSelection(cardIndex, isPositive, isConvict);
			}
		}
	}
	
	/*
	 * Adds a new player to the game
	 * 
	 * @param controller			The controller the player is using
	 */
	private void addNewPlayer(ControlAdapter controller)
	{
		int tempCardIndex = findNextEmptyCard();
		int emptyConvictSelection = findNextEmptySelection(true);
		int emptyCopSelection = -1;
		
		if (tempCardIndex != -1)
		{
			playerCards.get(tempCardIndex).setAdapter(controller);
			playerCards.get(tempCardIndex).setConvictSelection(emptyConvictSelection);
			playerCards.get(tempCardIndex).setCopSelection(emptyCopSelection);
			
			framesHeld[tempCardIndex] = 0;
			
			convictPortraits.get(tempCardIndex).setRegion(
					getConvictPortraitByIndex(playerCards.get(tempCardIndex).getConvictSelection()));
			copPortraits.get(tempCardIndex).setRegion(
					getCopPortraitByIndex(playerCards.get(tempCardIndex).getCopSelection()));
			convictThumbnails.get(tempCardIndex).setRegion(
					getConvictThumbnailByIndex(playerCards.get(tempCardIndex).getConvictSelection()));
			copThumbnails.get(tempCardIndex).setRegion(
					getCopThumbnailByIndex(playerCards.get(tempCardIndex).getCopSelection()));
			
			if (numPortraitsToShow < MAX_PLAYERS)
			{
				numPortraitsToShow++;
			}
			setUpPortraitX();
			sortedConvictPortraits = sortPortraitsByHeight(convictPortraits, false);
			sortedCopPortraits = sortPortraitsByHeight(copPortraits, false);
		}
	}
	
	/*
	 * Removes a player from the game
	 * 
	 * @param removeNum				The PlayerCard number of the player to remove
	 */
	private void removePlayer(int removeNum)
	{
		if (playerCards.get(removeNum).getAdapter() != null)
		{
			playerCards.get(removeNum).getAdapter().assignControls(-1, -1);
		}
		playerCards.removeIndex(removeNum);
		playerCards.add(new PlayerCard(-1, null));
		
		for (int i = 0; i < playerCards.size; i++)
		{
			TextureRegion tempRegion = convictPortraits.get(i).getRegion();
			changePortrait(i);
			if (convictPortraits.get(i).getRegion() != tempRegion && i != 7)
			{
				convictPortraits.get(i).setX(convictPortraits.get(i + 1).getX());
				copPortraits.get(i).setX(convictPortraits.get(i + 1).getX());
				convictThumbnails.get(i).setX(convictPortraits.get(i + 1).getX()
						+ convictPortraits.get(0).getRegionWidth() / 2
						- convictThumbnails.get(0).getRegionWidth() / 2);
				copThumbnails.get(i).setX(convictPortraits.get(i + 1).getX()
						+ convictPortraits.get(0).getRegionWidth() / 2
						- copThumbnails.get(0).getRegionWidth() / 2);
			}
		}
		
		int tempIndex = removeNum;
		while (tempIndex < 7)
		{
			framesHeld[tempIndex] = framesHeld[tempIndex + 1];
			tempIndex++;
		}
		framesHeld[tempIndex] = 0;
		
		if (numPortraitsToShow > 1 && findNextEmptyCard() != 7)
		{
			numPortraitsToShow--;
		}
		
		setUpPortraitX();
		sortedConvictPortraits = sortPortraitsByHeight(convictPortraits, false);
		sortedCopPortraits = sortPortraitsByHeight(copPortraits, false);
	}
	
	/*
	 * Finds the index of the lowest-index unused PlayerCard
	 * 
	 * @return						The index of the unused PlayerCard (-1 if all cards are used)
	 */
	public int findNextEmptyCard()
	{
		for (int i = 0; i < playerCards.size; i++)
		{
			if (playerCards.get(i).getAdapter() == null)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/*
	 * Finds the index of the lowest-index unused selection
	 * 
	 * @param isConvict				Whether to look for the next unused convict (true) or cop (false)
	 * @return						The index of the unused selection (-1 if all selections are used)
	 */
	public int findNextEmptySelection(boolean isConvict)
	{
		// The number to look for
		for (int i = 0; i < playerCards.size; i++)
		{
			boolean numberTaken = false;
			// The cards to look through
			for (int j = 0; j < playerCards.size; j++)
			{
				if (isConvict)
				{
					if (playerCards.get(j).getConvictSelection() == i)
					{
						numberTaken = true;
					}
				}
				else
				{
					if (playerCards.get(j).getCopSelection() == i)
					{
						numberTaken = true;
					}
				}
			}
			
			if (!numberTaken)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	/*
	 * Determines if a selection is being taken by another player 
	 * 
	 * @param check					The selection to check for vacancy
	 * @param currentPlayer			The player who is looking for vacancy
	 * @param isConvict				Whether to check convict selections (true) or cop selections (false)
	 * @return						Whether or not the selection is taken by another player
	 */
	private boolean isSelectionTaken(int check, int currentPlayer, boolean isConvict)
	{
		for (int i = 0; i < MAX_PLAYERS; i++)
		{
			if (isConvict)
			{
				if (i != currentPlayer && playerCards.get(i).getConvictSelection() == check)
				{
					return true;
				}
			}
			else
			{
				if (i != currentPlayer && playerCards.get(i).getCopSelection() == check)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*
	 * Finds a convict portrait in the character atlas file
	 * 
	 * @param index					The number of the portrait to find
	 * @return						The TextureRegion of the portrait (portrait 0 with invalid index)		
	 */
	private TextureRegion getConvictPortraitByIndex(int index)
	{
		AtlasRegion textureRegion = ChaseApp.characterAtlas.findRegion("conportrait", index);
		
		if (textureRegion != null)
		{
			return textureRegion;
		}
		else
		{
			return ChaseApp.characterAtlas.findRegion("conportrait", 0);
		}
	}
	
	/*
	 * Finds a cop portrait in the character atlas file
	 * 
	 * @param index					The number of the portrait to find
	 * @return						The TextureRegion of the portrait (portrait 0 with invalid index)		
	 */
	private TextureRegion getCopPortraitByIndex(int index)
	{
		// TODO change this back to copportrait once we have cop portraits
		AtlasRegion textureRegion = ChaseApp.characterAtlas.findRegion("conportrait", index);
		
		if (textureRegion != null)
		{
			return textureRegion;
		}
		else
		{
			return ChaseApp.characterAtlas.findRegion("copportrait", 0);
		}
	}
	
	/*
	 * Finds the PlayerCard corresponding to a sorted convict portrait
	 * 
	 * @param index					The index of the sorted convict portrait
	 * @return						The PlayerCard corresponding to the portrait index (or null with invalid index)		
	 */
	private PlayerCard findCardBySortedConvict(int index)
	{
		for (int i = 0; i < playerCards.size; i++)
		{
			if (sortedConvictPortraits.get(index) == convictPortraits.get(i))
			{
				return playerCards.get(i);
			}
		}
		
		return null;
	}
	
	/*
	 * Finds the PlayerCard corresponding to a sorted cop portrait
	 * 
	 * @param index					The index of the sorted cop portrait
	 * @return						The PlayerCard corresponding to the portrait index (or null with invalid index)	
	 */
	private PlayerCard findCardBySortedCop(int index)
	{
		for (int i = 0; i < playerCards.size; i++)
		{
			if (sortedCopPortraits.get(index) == copPortraits.get(i))
			{
				return playerCards.get(i);
			}
		}
		
		return playerCards.get(0);
	}
	
	/*
	 * Finds a convict thumbnail in the character atlas file
	 * 
	 * @param index					The number of the thumbnail to find		
	 * @return						The TextureRegion of the thumbnail (thumbnail 0 with invalid index)	
	 */
	private TextureRegion getConvictThumbnailByIndex(int index)
	{
		AtlasRegion textureRegion = ChaseApp.characterAtlas.findRegion("conthumbnail", index);
		
		if (textureRegion != null)
		{
			return textureRegion;
		}
		else
		{
			return ChaseApp.characterAtlas.findRegion("conthumbnail", 0);
		}
	}
	
	/*
	 * Finds a cop thumbnail in the character atlas file
	 * 
	 * @param index					The number of the thumbnail to find
	 * @return						The TextureRegion of the thumbnail (thumbnail 0 with invalid index)		
	 */
	private TextureRegion getCopThumbnailByIndex(int index)
	{
		AtlasRegion textureRegion = ChaseApp.characterAtlas.findRegion("copthumbnail", index);
		
		if (textureRegion != null)
		{
			return textureRegion;
		}
		else
		{
			return ChaseApp.characterAtlas.findRegion("copthumbnail", 0);
		}
	}
	
	/*
	 * Moves the thumbnails to the correct place relative to a player's active portrait
	 * 
	 * @param playerIndex			The player whose thumbnails to move
	 * @param isConvict				Whether to place the thumbnails on the convict (true) or cop (false) portrait
	 */
	private void moveThumbnails(int playerIndex, boolean isConvict)
	{	
		float theY;
		
		if (isConvict)
		{
			convictThumbnails.get(playerIndex).setY(convictPortraits.get(playerIndex).getY()
					- convictThumbnails.get(playerIndex).getRegionHeight() / 4);
		}
		else
		{
			theY = convictPortraits.get(playerIndex).getY()
					- convictThumbnails.get(playerIndex).getRegionHeight() / 4;
			convictThumbnails.get(playerIndex).setY(theY);
			copThumbnails.get(playerIndex).setY(theY - copThumbnails.get(playerIndex).getRegionHeight());
		}
	}
	
	/*
	 * Moves the thumbnails to the correct place relative to a player's active portrait
	 * 
	 * @param playerIndex			The player whose thumbnails to move
	 * @param destinationX			The X position to move to
	 * @param isConvict				Whether to place the thumbnails on the convict (true) or cop (false) portrait
	 */
	private void moveThumbnails(int playerIndex, float destinationX, boolean isConvict)
	{
		destinationX = destinationX - convictThumbnails.get(playerIndex).getRegionWidth() / 2;
		Tween.to(convictThumbnails.get(playerIndex), MenuTextureRegionAccessor.TRANSLATE_X, 1f).target(destinationX)
		.ease(Cubic.INOUT).start(tManager);
		Tween.to(copThumbnails.get(playerIndex), MenuTextureRegionAccessor.TRANSLATE_X, 1f).target(destinationX)
		.ease(Cubic.INOUT).start(tManager);
		
		moveThumbnails(playerIndex, isConvict);
	}
	
	/*
	 * Helper method that sorts portraits by height
	 * 
	 * @param toSort				The array of portraits to sort
	 * @param ascending				Whether portraits should be listed ascending or descending
	 * @return						The array of portraits sorted by height
	 */
	private Array<MenuTextureRegion> sortPortraitsByHeight(Array<MenuTextureRegion> toSort, boolean ascending)
	{
		int arraySize = 0;
		Array<MenuTextureRegion> returnArray = new Array<MenuTextureRegion>();
		
		for (int i = 0; i < toSort.size; i++)
		{
			if (playerCards.get(i).isUsed())
			{
				arraySize++;
			}
		}
		
		MenuTextureRegion[] regionArray = new MenuTextureRegion[arraySize];
		
		for (int i = 0; i < arraySize; i++)
		{
			regionArray[i] = toSort.get(i);
		}
		
		Arrays.sort(regionArray, new MenuTextureRegionComparator(ascending));
		
		for (int i = 0; i < regionArray.length; i++)
		{
			returnArray.add(regionArray[i]);
		}
		
		return returnArray;
	}
	
	/*
	 * Changes a player's portraits and associated thumbnails
	 * 
	 * @param playerNumber 		The number of the player who needs new portraits
	 */
	private void changePortrait(int playerNumber)
	{
		convictPortraits.get(playerNumber).setRegion(
			getConvictPortraitByIndex(playerCards.get(playerNumber).getConvictSelection()));
		convictThumbnails.get(playerNumber).setRegion(
			getConvictThumbnailByIndex(playerCards.get(playerNumber).getConvictSelection()));
		
		sortedConvictPortraits = sortPortraitsByHeight(convictPortraits, false);
	
		copPortraits.get(playerNumber).setRegion(
				getCopPortraitByIndex(playerCards.get(playerNumber).getCopSelection()));
		copThumbnails.get(playerNumber).setRegion(
				getCopThumbnailByIndex(playerCards.get(playerNumber).getCopSelection()));
		
		sortedCopPortraits = sortPortraitsByHeight(copPortraits, false);
	}
	
	/*
	 * Causes a portrait to wiggle back and forth when its player presses the callout button
	 * 
	 * @param portraitNumber		The number of the portrait to wiggle
	 */
	private void calloutWiggle(int portraitNumber)
	{
		if (tManager.containsTarget(convictPortraits.get(portraitNumber),
				MenuTextureRegionAccessor.TRANSLATE_X))
		{
			return;
		}
		
		if (tManager.containsTarget(convictPortraits.get(portraitNumber),
				MenuTextureRegionAccessor.WIGGLE_X))
		{
			tManager.killTarget(convictPortraits.get(portraitNumber));
			tManager.killTarget(copPortraits.get(portraitNumber));
			tManager.killTarget(convictThumbnails.get(portraitNumber));
			tManager.killTarget(copThumbnails.get(portraitNumber));
			
			float portraitStartX;
			
			if (convictPortraits.get(0).getRegionWidth() * numPortraitsToShow > Data.MENU_WIDTH)
			{
				portraitStartX = -MenuTextureRegion.TITLE_SAFE_X;
				portraitXIncrement = (float)(Data.MENU_WIDTH - convictPortraits.get(0).getRegionWidth())
						/ (float)(numPortraitsToShow-1);
			}
			else
			{
				portraitStartX = 2*MenuTextureRegion.TITLE_SAFE_X + ((float)(Data.MENU_WIDTH)/2f)-(convictPortraits.get(0).getRegionWidth()*numPortraitsToShow)/2f;
				portraitXIncrement = convictPortraits.get(0).getRegionWidth();
			}
			
			float portraitX = portraitStartX + portraitNumber * portraitXIncrement;
			
			convictPortraits.get(portraitNumber).setX(portraitX);
			copPortraits.get(portraitNumber).setX(convictPortraits.get(portraitNumber).getX());
			convictThumbnails.get(portraitNumber).setX(portraitX 
					+ convictPortraits.get(portraitNumber).getRegionWidth() / 2
					- convictThumbnails.get(portraitNumber).getRegionWidth() / 2);
			copThumbnails.get(portraitNumber).setX(portraitX 
					+ convictPortraits.get(portraitNumber).getRegionWidth() / 2
					- copThumbnails.get(portraitNumber).getRegionWidth() / 2);
		}
		
		Timeline.createSequence()
		.push
		(Tween.to(convictPortraits.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(-25))
		.push
		(Tween.to(convictPortraits.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(50))
		.push
		(Tween.to(convictPortraits.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(-25))
		.start(tManager);
		
		Timeline.createSequence()
		.push
		(Tween.to(copPortraits.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(-25))
		.push
		(Tween.to(copPortraits.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(50))
		.push
		(Tween.to(copPortraits.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(-25))
		.start(tManager);
		
		Timeline.createSequence()
		.push
		(Tween.to(convictThumbnails.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(-25))
		.push
		(Tween.to(convictThumbnails.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(50))
		.push
		(Tween.to(convictThumbnails.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(-25))
		.start(tManager);
		
		Timeline.createSequence()
		.push
		(Tween.to(copThumbnails.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(-25))
		.push
		(Tween.to(copThumbnails.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(50))
		.push
		(Tween.to(copThumbnails.get(portraitNumber), MenuTextureRegionAccessor.WIGGLE_X, .05f).targetRelative(-25))
		.start(tManager);
	}
	
	/*
	 * Called to clean up tweens in progress when players leave CharacterSelect
	 */
	private void killWiggleTweens()
	{
		for (int i = 0; i < playerCards.size; i++)
		{
			if (tManager.containsTarget(convictPortraits.get(i), 
					MenuTextureRegionAccessor.TRANSLATE_X))
			{
				tManager.killTarget(convictPortraits.get(i));
				tManager.killTarget(copPortraits.get(i));
				tManager.killTarget(convictThumbnails.get(i));
				tManager.killTarget(copThumbnails.get(i));
			}
			
			convictPortraits.get(i).setX(-convictPortraits.get(i).getRegionWidth() * 2);
			copPortraits.get(i).setX(-copPortraits.get(i).getRegionWidth() * 2);
			convictThumbnails.get(i).setX(-convictPortraits.get(i).getRegionWidth() * 2);
			copThumbnails.get(i).setX(-convictPortraits.get(i).getRegionWidth() * 2);
		}
	}
	
	/*
	 * Finds the index of the PlayerCard which is using a certain ControlAdapter
	 * 
	 * @param adapterIndex			The index of the ControlAdapter
	 * @return						The index of the PlayerCard using the ControlAdapter (-1 if no card uses it)
	 */
	private int getCardIndex(int adapterIndex)
	{
		for (int i = 0; i < playerCards.size; i++)
		{
			if (!playerCards.get(i).isUsed())
			{
				break;
			}
			else
			{
				if (playerCards.get(i).getAdapter().equals(input.controlList[adapterIndex]))
				{
					return i;
				}
			}
		}
		
		return -1;
	}
	
	/*
	 * Determines if all controls are empty (not being used)
	 * 
	 * @return						Whether or not the controls are all empty
	 */
	private boolean noneUsed()
	{	
		for (int i = 0; i < playerCards.size; i++)
		{
			if (playerCards.get(i).isUsed())
			{
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * Moves a Transition in from off-screen to bring the players into the game
	 */
	private void startTransition()
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
	
	/*
	 * Switches the game screen to the tutorial map once the above Timeline is over
	 */
	private TweenCallback changeToTutorialScreen = new TweenCallback()
	{
		public void onEvent(int type, BaseTween<?> source)
		{
			transitioning = false;
			input.getBoss().getCurrent().resetData();
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
			myApp.setScreen(new ChainGame(myApp, getNumPlayers(), tutorialVO, ChaseApp.mapSelect.getMapCache(), transition, true));
		}
	};
} // End class