/*
 * @(#)Credits.java		0.3 14/4/23
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.screens;

import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.percipient24.helpers.StringLayout;
import com.percipient24.cgc.CGCTimer;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.Data;
import com.percipient24.cgc.TimerManager;
import com.percipient24.cgc.screens.helpers.ControllerDrawer;
import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.cgc.screens.helpers.LanguageKeys;
//import com.percipient24.cgc.screens.helpers.MenuTextureRegion;
import com.percipient24.enums.ControlType;
import com.percipient24.input.ControlAdapter;

/*
 * Contains the data for the Credits screen
 * 
 * @version 0.3 14/4/23
 * @author Joe Pietruch
 * @author William Ziegler
 */
public class Credits extends CGCScreen
{
	private Array<String> headers;
	
	private Array<String> developers;
	private Array<String> developerRoles;
	private Array<String> staff;
	private Array<String> staffRoles;
	private Array<String> backers;
	private Array<String> specialThanks;

	private StringLayout layout;
	
	//private Array<MenuTextureRegion> creditsThumbnails;
	private Array<MenuTextureRegion> endLogos;
	
	private float developersHeaderY = MENU_BUFFER;
	private float developersSpacing = 75;
	private float staffHeaderY;
	private float staffSpacing = 75;
	private float backersHeaderY;
	private float backersSpacing = 75;
	private float specialThanksHeaderY;
	private float specialThanksSpacing = 100;
	private float logosStartY;
	private float logosSpacing = 100;
	private float creditsBottom;
	
	// Scrolling variables
	private float yDisplacement;
	private final float Y_SCROLL_SPEED = 1.5f;
	private boolean autoScroll = true;
	private CGCTimer autoScrollTimer;
	private Timer.Task autoScrollTask;
	private float autoScrollTime = 3.0f;
	
	private final float COLUMN_GAP = 15;
	
	// Button prompt variables
	private CGCTimer hidePromptTimer;
	private Timer.Task hidePromptTask;
	private float hidePromptTime = 5.0f;
	private boolean showButtonPrompt = false;
	private ControllerDrawer navigation;
	private ControllerDrawer downButton;
	private ControllerDrawer rightButton;
	
	/*
	 * Creates a Credits object
	 * 
	 * @param app					The app running this screen
	 */
	public Credits(ChaseApp app)
	{
		super(app);
		
		title = "";
		titleLayout.updateText(title);
		
		prevScreen = ChaseApp.mainMenu;
		
		setUpNames();
		setUpImages();
		setUpTimer();
		
		staffHeaderY = developersHeaderY + (developersSpacing * (developers.size + 2)) + 300;
		backersHeaderY = staffHeaderY + (staffSpacing * (staff.size + 2)) + 300;
		specialThanksHeaderY = backersHeaderY + (backersSpacing * (backers.size + 2)) + 300;
		logosStartY = specialThanksHeaderY + (specialThanksSpacing * (specialThanks.size + 2)) + 300;
		creditsBottom = logosStartY + (endLogos.size) * (logosSpacing * 3) 
				+ (Data.MENU_HEIGHT / 2 - MENU_BUFFER);
		
		yDisplacement = 0;
		
		navigation = new ControllerDrawer(MenuTextureRegion.LOWER_RIGHT, MenuTextureRegion.LOWER_RIGHT);
		navigation.setMessage(ChaseApp.lang.get(LanguageKeys.scroll), -50, 20, Align.right);
		downButton = new ControllerDrawer(MenuTextureRegion.LOWER_RIGHT, MenuTextureRegion.LOWER_RIGHT);
		downButton.setMessage(ChaseApp.lang.get(LanguageKeys.back), -50, 20, Align.right);
		rightButton = new ControllerDrawer(MenuTextureRegion.LOWER_RIGHT, MenuTextureRegion.LOWER_RIGHT);
		rightButton.setMessage(ChaseApp.lang.get(LanguageKeys.back), -50, 20, Align.right);

		layout = new StringLayout("", ChaseApp.menuFont);
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
		
		handleB(boss, keyboardLeft, keyboardRight);
		handleA(boss, keyboardLeft, keyboardRight);
		
		if (display)
		{
			if (boss.anyInput())
			{
				showButtonPrompt = true;
			}
			else
			{
				if (!TimerManager.contains(hidePromptTimer))
				{
					TimerManager.addTimer(hidePromptTimer);
				}
			}
			
			if (boss.isPressed(ControlType.MENU_UP) || keyboardLeft.isPressed(ControlType.MENU_UP)
					|| keyboardRight.isPressed(ControlType.MENU_UP))
			{
				autoScroll = false;
					
				if (autoScrollTimer.isRunning())
				{
					TimerManager.removeTimer(autoScrollTimer);
				}
				
				if (yDisplacement > Data.MENU_HEIGHT)
				{
					framesHeld++;
					if (framesHeld < 30)
					{
						yDisplacement -= Y_SCROLL_SPEED * 4;
					}
					else if (framesHeld > 90)
					{
						yDisplacement -= Y_SCROLL_SPEED * 16;
					}
					else if (framesHeld > 30)
					{
						yDisplacement -= Y_SCROLL_SPEED * 8;	
					}
				}
			}
			else if (boss.isPressed(ControlType.MENU_DOWN) || keyboardLeft.isPressed(ControlType.MENU_DOWN)
					|| keyboardRight.isPressed(ControlType.MENU_DOWN))
			{
				autoScroll = false;
				
				if (autoScrollTimer.isRunning())
				{
					TimerManager.removeTimer(autoScrollTimer);
				}
				
				//Change this when we add the logos at the end.
				if (yDisplacement < creditsBottom)
				{
					framesHeld++;
					if (framesHeld < 30)
					{
						yDisplacement += Y_SCROLL_SPEED * 4;
					}
					else if (framesHeld > 90)
					{
						yDisplacement += Y_SCROLL_SPEED * 16;
					}
					else if (framesHeld > 30)
					{
						yDisplacement += Y_SCROLL_SPEED * 8;
					}
					
					if (yDisplacement > creditsBottom)
					{
						yDisplacement = creditsBottom;
						showButtonPrompt = true;
					}
				}
			}
			else
			{
				if (!autoScrollTimer.isRunning())
				{
					TimerManager.addTimer(autoScrollTimer);
				}
				
				framesHeld = 0;
			}
		}
		
		super.handleInput();
	}
	
	/*
	 * Draws this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#render(float)
	 */
	public void render(float delta) 
	{
		super.render(delta);
		
		if (autoScroll && yDisplacement < creditsBottom)
		{
			yDisplacement += Y_SCROLL_SPEED;
			
			if (yDisplacement > creditsBottom)
			{
				yDisplacement = creditsBottom;
				showButtonPrompt = true;
			}
		}
		
		if(display)
		{	
			sBatch.setProjectionMatrix(ChaseApp.menuCam.combined);
			
			renderColumns(0, developersHeaderY, developersSpacing, developers, developerRoles);
			renderColumns(1, staffHeaderY, staffSpacing, staff, staffRoles);
			renderColumn(2, backersHeaderY, backersSpacing, backers);
			renderColumn(3, specialThanksHeaderY, specialThanksSpacing, specialThanks);
			renderLogos();
			
			if (showButtonPrompt)
			{
				renderButtonPrompt(delta);
			}
		}
		
		sBatch.end();
	}
	
	/*
	 * Renders the header @headerIndex at @headerY using @spacing, with names coming from @column
	 */
	private void renderColumn(int headerIndex, float headerY, float spacing, Array<String> column)
	{
		setFontToHeader();

		layout.updateText(headers.get(headerIndex));
		GlyphLayout glyphLayout = layout.getLayout();
		
		ChaseApp.menuFont.draw(
			sBatch,
			glyphLayout,
			Data.MENU_WIDTH * .5f - glyphLayout.width / 2, 
			-headerY + yDisplacement);

		ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		setFontToNames();
		
		for (int i = 0; i < column.size; i++)
		{
			layout.updateText(column.get(i));

			ChaseApp.menuFont.draw(
				sBatch,
				glyphLayout,
				Data.MENU_WIDTH * .5f - glyphLayout.width / 2, 
				-headerY - (125 + spacing * i) + yDisplacement);
		}
	}
	/*
	 * Renders the header @headerIndex at @headerY using @spacing, with names coming from @leftColumn and roles from @rightColumn
	 */
	private void renderColumns(int headerIndex, float headerY, float spacing, Array<String> leftColumn, Array<String> rightColumn)
	{
		setFontToHeader();
		
		layout.updateText(headers.get(headerIndex));
		GlyphLayout glyphLayout = layout.getLayout();

		ChaseApp.menuFont.draw(
			sBatch,
			glyphLayout,
			Data.MENU_WIDTH * .5f - glyphLayout.width / 2, 
			-headerY + yDisplacement);

		ChaseApp.menuFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		setFontToNames();
		
		for (int i = 0; i < leftColumn.size; i++)
		{
			layout.updateText(leftColumn.get(i));
			ChaseApp.menuFont.draw(
					sBatch,
					glyphLayout,
					Data.MENU_WIDTH * .5f - COLUMN_GAP - glyphLayout.width, 
					-headerY - (125 + spacing * i) + yDisplacement);
		}
		
		for (int i = 0; i < rightColumn.size; i++)
		{
			layout.updateText(rightColumn.get(i));
			ChaseApp.menuFont.draw(
					sBatch,
					glyphLayout,
					Data.MENU_WIDTH * .5f + COLUMN_GAP, 
					-headerY - (125 + spacing * i) + yDisplacement);
		}
	}
	
	/*
	 * Draws the logos for supporting organizations at the end of the credits
	 */
	private void renderLogos()
	{
		float logosCurrentY = 0;
		
		//TODO These are in sequential order for now. Joe said he would fix the positioning later.
		for (int i = 0; i < endLogos.size; i++)
		{
			logosCurrentY += Math.max(logosSpacing * 3, endLogos.get(i).getRegionHeight());
			
			endLogos.get(i).setY(-logosStartY - logosCurrentY + yDisplacement);
			endLogos.get(i).draw(sBatch);
			
			logosCurrentY += logosSpacing;
		}
	}
	
	/*
	 * Draws the button prompt to return players to the main menu
	 * 
	 * @param delta						Seconds elapsed since last frame
	 */
	private void renderButtonPrompt(float delta)
	{
		navigation.draw(sBatch, delta);
		downButton.draw(sBatch, delta);
		rightButton.draw(sBatch, delta);
	}
	
	/*
	 * Sets the menuFont to the size and brightness of the Credits headers
	 */
	private void setFontToHeader()
	{
		ChaseApp.menuFont.getData().setScale(FONT_MAIN);
		
		ChaseApp.menuFont.setColor(ChaseApp.selectedOrange);
	}
	
	/*
	 * Sets the menuFont to the size and brightness of the Credits names
	 */
	private void setFontToNames()
	{
		ChaseApp.menuFont.getData().setScale(FONT_SIDE);
		
		ChaseApp.menuFont.setColor(0.9f, 0.9f, 0.9f, 1.0f);
	}
	
	/*
	 * Shows this screen
	 * 
	 * @see com.percipient24.cgc.screens.CGCScreen#show()
	 */
	public void show() 
	{
		yDisplacement = 0;

		autoScroll = true;
		
		ControlAdapter boss = input.getBoss();
		
		if(boss.isController())
		{
			if(boss.isLeft())
			{
				navigation.showWing(true);
				
				downButton.showAnimation(ControllerDrawer.DPAD_DOWN_BLINK);
				rightButton.showAnimation(ControllerDrawer.DPAD_RIGHT_BLINK);
			}
			else
			{
				navigation.showWing(false);
				
				downButton.showAnimation(ControllerDrawer.FACE_DOWN_BLINK);
				rightButton.showAnimation(ControllerDrawer.FACE_RIGHT_BLINK);
			}
			
			navigation.showAnimation(ControllerDrawer.STICK_UP_DOWN);
			
			navigation.setWiggle(-23, 323);
			downButton.setWiggle(-23, 153);
			rightButton.setWiggle(-23, 33);
		}
		
		super.show();
	}
	
	/*
	 * Sets up arrays of the images that will appear in the credits
	 */
	private void setUpImages()
	{
		//TODO Get the right thumbnails for people who have them
		//creditsThumbnails = new Array<MenuTextureRegion>();
		
		endLogos = new Array<MenuTextureRegion>();
		endLogos.add(new MenuTextureRegion(ChaseApp.creditsAtlas.findRegion("cgc_logotype"), new Vector2(0, 0)));
		endLogos.add(new MenuTextureRegion(ChaseApp.creditsAtlas.findRegion("magicspell"), new Vector2(0, 0)));
		endLogos.add(new MenuTextureRegion(ChaseApp.creditsAtlas.findRegion("kickstarter-badge-funded"), new Vector2(0, 0)));
		endLogos.add(new MenuTextureRegion(ChaseApp.creditsAtlas.findRegion("ouya_logo_white"), new Vector2(0, 0)));
		endLogos.add(new MenuTextureRegion(ChaseApp.creditsAtlas.findRegion("ftg_logo"), new Vector2(0, 0)));
	}
	
	/*
	 * Creates the timer that restarts auto-scrolling of the credits
	 */
	private void setUpTimer()
	{
		hidePromptTask = new Timer.Task()
		{
			public void run()
			{
				if (yDisplacement < creditsBottom)
				{
					showButtonPrompt = false;
				}
			}
		};
		
		autoScrollTask = new Timer.Task()
		{
			public void run()
			{
				autoScroll = true;
			}
		};
		
		hidePromptTimer = new CGCTimer(hidePromptTask, hidePromptTime, false, "hidePromptTimer");
		autoScrollTimer = new CGCTimer(autoScrollTask, autoScrollTime, false, "autoScrollTimer");
	}

	/*
	 * Sets up arrays of the names that will appear in the credits
	 */
	public void setUpNames()
	{
		headers = new Array<String>();
		headers.add(ChaseApp.lang.get(LanguageKeys.developers));
		headers.add(ChaseApp.lang.get(LanguageKeys.staff));
		headers.add(ChaseApp.lang.get(LanguageKeys.backers));
		headers.add(ChaseApp.lang.get(LanguageKeys.special_thanks));
		
		developers = new Array<String>();
		developerRoles = new Array<String>();
		
		developers.add("Joe Pietruch");		developerRoles.add(ChaseApp.lang.get(LanguageKeys.originator)); // TODO 24601*
		developers.add("Nate Perry");		developerRoles.add(ChaseApp.lang.get(LanguageKeys.lead_web)); // TODO 24601*
		developers.add("William Ziegler");	developerRoles.add(ChaseApp.lang.get(LanguageKeys.game_developer)); // TODO Rookie Cop*
		developers.add("Chris Rider");		developerRoles.add(ChaseApp.lang.get(LanguageKeys.game_developer)); // TODO 24601*
		developers.add("Jeffery Kelly");	developerRoles.add(ChaseApp.lang.get(LanguageKeys.game_developer)); // TODO 24601*
		developers.add("Kurt Venezuela");	developerRoles.add(ChaseApp.lang.get(LanguageKeys.lead_artist)); // TODO 24601*
		developers.add("Forrest Shooster");	developerRoles.add(ChaseApp.lang.get(LanguageKeys.audio_production)); // TODO 24601*
		developers.add("Cathy Razim");		developerRoles.add(ChaseApp.lang.get(LanguageKeys.character_artist)); // TODO 24601*
		developers.add("Michael Wega");		developerRoles.add(ChaseApp.lang.get(LanguageKeys.ui_designer)); // TODO 24601
		developers.add("Clayton Andrews");	developerRoles.add(ChaseApp.lang.get(LanguageKeys.supporting_game_dev)); // TODO 24601*
	
		staff = new Array<String>();
		staffRoles = new Array<String>();
		
		staff.add("Andy Phelps");			staffRoles.add(ChaseApp.lang.get(LanguageKeys.dark_lord)); // TODO Rookie Cop
		staff.add("Brenda Schlageter");		staffRoles.add(ChaseApp.lang.get(LanguageKeys.operations));
		staff.add("Jennifer Hinton");		staffRoles.add(ChaseApp.lang.get(LanguageKeys.assistant_director));
		staff.add("Gary Scarborough");		staffRoles.add(ChaseApp.lang.get(LanguageKeys.lab_manager));
		staff.add("Chris Egert");			staffRoles.add(ChaseApp.lang.get(LanguageKeys.associate_director)); // TODO The Sheriff
	
		backers = new Array<String>();
		backers.add("Aaron Wolfrom");
		backers.add("Abimael Ordonez");
		backers.add("Adam Luptak");
		backers.add("Al Biles");
		backers.add("Alan Gerding");
		backers.add("Alberto Camacho");
		backers.add("Alec Herbert");
		backers.add("Alex Herdzik");
		backers.add("Alex Karantza"); // TODO 24601
		backers.add("Alex Nowak");
		backers.add("Alexander Dunn");
		backers.add("Alexander Y. Hawson");
		backers.add("Alexandria Mack");
		backers.add("Alexis Montoya");
		backers.add("Amanda Scheerbaum");
		backers.add("Andrew 'Codemaster' Kane");
		backers.add("Andrew Gucwa");
		backers.add("Andrew Hollenbach");
		backers.add("Andrew Pahuru");
		backers.add("ANdy Lee");
		backers.add("andy pacher");
		backers.add("Andy Phelps"); // TODO Rookie Cop
		backers.add("Anthony Martins");
		backers.add("Ari Check");
		backers.add("Austin Donnell");
		backers.add("Austin Williams");
		backers.add("Axel Terizaki");
		backers.add("Aziz Chaudhry");
		backers.add("Barbara Grossberg");
		backers.add("Ben Coppens");
		backers.add("Ben DeCamp");
		backers.add("Benjamin J Weeg");
		backers.add("Blake Gross");
		backers.add("bloodbond3");
		backers.add("BrambigfootNL");
		backers.add("Brandon Adams");
		backers.add("Brandon Littell");
		backers.add("Brian Clanton");
		backers.add("Brian Gunn");
		backers.add("C. Hayungs");
		backers.add("C.J. Heflin");
		backers.add("Capital M - Matt Siciliano");
		backers.add("Carl C. Burgers");
		backers.add("Carl Domingo");
		backers.add("Carl Milazzo");
		backers.add("Carmine T. Guida");
		backers.add("Chad Cohen");
		backers.add("Chad Weeden"); // TODO 24601
		backers.add("Changbai Li");
		backers.add("Chip Beck");
		backers.add("Chius Daigle");
		backers.add("Chris 'cw' MacDonald");
		backers.add("Chris Cascioli"); // TODO 24601
		backers.add("Chris Knepper"); // TODO 24601
		backers.add("Chris Lord");
		backers.add("Christian Dywan");
		backers.add("Christoph Werner");
		backers.add("Christopher 'DarkWolfNine' Muzatko");
		backers.add("Chuck Smith");
		backers.add("Clayton Andrews");
		backers.add("Cody Van De Mark"); // TODO 24601
		backers.add("Colden Cullen");
		backers.add("Colin Doody");
		backers.add("Colin Knud-Hansen");
		backers.add("Corey Flickinger");
		backers.add("Cot\u00E9");
		backers.add("CYBR Labs");
		backers.add("Damien 'Enterthusiast' Bernard");
		backers.add("Dan Cashmore");
		backers.add("Dan Tilford");
		backers.add("Daniel Jost");
		backers.add("danShumway");
		backers.add("Darren Urmey");
		backers.add("David I. Schwartz"); // TODO Rookie Cop
		backers.add("David J. Heberle");
		backers.add("David Logan 'Daevo'");
		backers.add("David Simkins");
		backers.add("dbhjed");
		backers.add("den svenska bj\u00F6rnstammen");
		backers.add("Devrin Ryther");
		backers.add("Don Gerbracht");
		backers.add("Drew Diamantoukos"); // TODO 24601
		backers.add("dustinkochensparger.com");
		backers.add("Eberhard");
		backers.add("Ed Boremski"); // TODO 24601
		backers.add("Ed Hoc");
		backers.add("Ed Huyer");
		backers.add("Edward Finer");
		backers.add("Elizabeth Goins");
		backers.add("Eric Zegarelli");
		backers.add("Evan 'Flyer3232' Schneller");
		backers.add("eyecreate");
		backers.add("falldeaf");
		backers.add("FantomBlaze");
		backers.add("Finn Morgan");
		backers.add("Forrest Z. Shooster");
		backers.add("Fred Clause");
		backers.add("George Gaspari");
		backers.add("Greg & Brandy Dalton");
		backers.add("Gustav Wedholm");
		backers.add("Hallvard U.");
		backers.add("Ian Morency");
		backers.add("Ian Switaj");
		backers.add("Jackie Wiley");
		backers.add("Jacob Burdecki");
		backers.add("James Arnold");
		backers.add("Jan Dvorak IV.");
		backers.add("Jana Boremski"); // TODO Rookie Cop
		backers.add("Jared Kinkade");
		backers.add("Jared Yeager");
		backers.add("Jason Peretz");
		backers.add("Jason S Chandler");
		backers.add("Jeff Sherman");
		backers.add("Jeffrey N. Smith");
		backers.add("Joe (Dziadzi) Pietruch"); // TODO Rookie Cop
		backers.add("Joe Natalzia");
		backers.add("Joey B.");
		backers.add("Jon Harden");
		backers.add("Jon Palmer");
		backers.add("Jon Pratt");
		backers.add("Jon Schull");
		backers.add("Jonathan Lowden");
		backers.add("Joseph Fong");
		backers.add("Josh Davis");
		backers.add("Justin and Jillian Pietruch");
		backers.add("Justin Gold"); // TODO 24601
		backers.add("Justine Raymond");
		backers.add("Katie Pustolski");
		backers.add("Katie Tigue");
		backers.add("Keenan Munnings");
		backers.add("Kenneth Brevard");
		backers.add("Kevin Kieser");
		backers.add("Kevin Tesch");
		backers.add("Kris Attfield");
		backers.add("Kristen Lapenta");
		backers.add("Kyle Haas");
		backers.add("Kyler Connare");
		backers.add("Kyler Mulherin");
		backers.add("Lachlan Cooper");
		backers.add("Lance Laughlin");
		backers.add("Lenka Razim");
		backers.add("Levi Flaman");
		backers.add("Liam Middlebrook"); // TODO 24601
		backers.add("Liz Lawley");
		backers.add("Logan Thompson"); // TODO 24601
		backers.add("Logan Thompson"); // TODO Rookie Cop
		backers.add("Lorenzo Batallones");
		backers.add("Luka Marcetic");
		backers.add("Luke Hovington");
		backers.add("Marcelo 'Yino' Sanchez");
		backers.add("Mark 'Experimental games go!' Terrano");
		backers.add("Mark Lores");
		backers.add("Martha Pietruch");
		backers.add("Matt 'The Raptor' Monasch");
		backers.add("Matt Dyson");
		backers.add("Matt Reichardt");
		backers.add("Matt W");
		backers.add("Matthew Everett");
		backers.add("Max Juchheim");
		backers.add("Mclean Oshiokpekhai");
		backers.add("Meg Sczerby");
		backers.add("Michael 'Apollo' Harrison");
		backers.add("Michael Ey"); // TODO 24601
		backers.add("Michael Lynch");
		backers.add("Mike Nolan");
		backers.add("mildmojo");
		backers.add("Mitch DeHond");
		backers.add("Nancy Doubleday");
		backers.add("Nate Brengle");
		backers.add("Nate Perry");
		backers.add("Nathan Popham");
		backers.add("Neil Guertin");
		backers.add("Nicholas Yu");
		backers.add("Nick Buonarota");
		backers.add("OUYACentral.TV");
		backers.add("Patrick Gage Kelley");
		backers.add("Patrick Kidd");
		backers.add("Paul B");
		backers.add("Paul Forgione");
		backers.add("Paulo de Ti\u00E8ge");
		backers.add("Per Kristian Brastad");
		backers.add("Philip Minchin");
		backers.add("Pimmon-Storch");
		backers.add("Purple Pwny Studios");
		backers.add("Rachel Diesel");
		backers.add("Randi L. Butler");
		backers.add("Randy '@Randygbk' Greenback");
		backers.add("Rebecca Vessal");
		backers.add("R\u00E9my 'Skuz974' STIEGLITZ");
		backers.add("Rev. Samuel Tunnell");
		backers.add("Rob Clifford");
		backers.add("Robin Basalla");
		backers.add("Ron, Melanie, and Bridget Phillips");
		backers.add("Ross Cleaver");
		backers.add("Russ Pedersen");
		backers.add("Ryan Oliver");
		backers.add("Sam Howard");
		backers.add("Sausage Mahoney");
		backers.add("Scott Nicholson");
		backers.add("Screaming Lord Byron");
		backers.add("Sebastian Deterding");
		backers.add("Shaf");
		backers.add("Shaun Foster");
		backers.add("Shiv Rawal");
		backers.add("Simon Prefontaine");
		backers.add("Stella Lee");
		backers.add("Stephen Carlson");
		backers.add("Stephen Jacobs");
		backers.add("Stu Gollan");
		backers.add("Taylor Becker");
		backers.add("Taylor Ryan");
		backers.add("Ted Kotz");
		backers.add("Terry of Wexford");
		backers.add("Thane E. Armbruster");
		backers.add("Thierry Allard");
		backers.add("Thomas Warde Bentley");
		backers.add("Tim Massoth");
		backers.add("Tom James Allen Jr");
		backers.add("Tonethar of the IGM People (or just Tonethar)");
		backers.add("Troy 'Wrongtown' Hall");
		backers.add("Tyler Wozniak"); // TODO 24601
		backers.add("Veronica Wharton");
		backers.add("W. Scott Warren");
		backers.add("Wayne A Arthurton");
		backers.add("weez Oyzon");
		backers.add("Whomba");
		backers.add("Wildheart Baby");
		backers.add("Will 'CJ Hamster' Hagen");
		backers.add("William Dougherty");
		backers.add("William Limratana");
		backers.add("Yana Malysheva");
		
		//TODO Get special thanks messages from everybody
		specialThanks = new Array<String>();
		specialThanks.add(ChaseApp.lang.get(LanguageKeys.funded_kickstarter));
		specialThanks.add(ChaseApp.lang.get(LanguageKeys.hashtag_ftg));
		specialThanks.add(ChaseApp.lang.get(LanguageKeys.ouya_inc));
		specialThanks.add(ChaseApp.lang.get(LanguageKeys.logo_design));
		specialThanks.add(ChaseApp.lang.get(LanguageKeys.kickstarter_planning));
		specialThanks.add(ChaseApp.lang.get(LanguageKeys.players_deavors));
		specialThanks.add(ChaseApp.lang.get(LanguageKeys.advice));
		//TODO Get a huge list of blog credits
		specialThanks.add(ChaseApp.lang.get(LanguageKeys.family));
		specialThanks.add(ChaseApp.lang.get(LanguageKeys.mud));
		specialThanks.add(ChaseApp.lang.get(LanguageKeys.will_z));
		specialThanks.add(ChaseApp.lang.get(LanguageKeys.chris_r));
	}
	
} // End class