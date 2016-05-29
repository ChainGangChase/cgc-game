/*
 * @(#)ControllerDrawer.java		0.3 14/1/31 TODO When was this made?
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.screens.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.percipient24.b2helpers.StringLayout;
import com.percipient24.cgc.ChaseApp;

/*
 * Contains the logic for drawing controller images
 * 
 * @version 0.3 14/1/31 TODO When was this made?
 * @author Joe Pietruch
 */
public class ControllerDrawer {
	
	private boolean shouldShowWing = false;
	private MenuTextureRegion wing;
	private boolean isLeftWing = false;
	
	private int anchor;
	private int registration;
	
	private int myAnimation = -1;
	private float myAnimationAccumulator = 0;
	
	private MenuTextureRegion inputRegion;
	
	public static final int NONE = -1;
	
	public static final int DPAD_DOWN_BLINK 	= 0;
	public static final int DPAD_UP_BLINK 		= 1;
	public static final int DPAD_LEFT_BLINK 	= 2;
	public static final int DPAD_RIGHT_BLINK 	= 3;
	
	public static final int DPAD_ANY_BLINK 		= 4;
	
	public static final int FACE_DOWN_BLINK 	= 5;
	public static final int FACE_UP_BLINK 		= 6;
	public static final int FACE_LEFT_BLINK 	= 7;
	public static final int FACE_RIGHT_BLINK 	= 8;
	
	public static final int FACE_ANY_BLINK 		= 9;
	
	public static final int STICK_UP_DOWN 		= 10;
	public static final int STICK_LEFT_RIGHT 	= 11;
	public static final int STICK_ROTATE 		= 12;
	public static final int STICK_3_BLINK		= 13;
	
	public static final int L_BUMPER 			= 14;
	public static final int R_BUMPER 			= 15;
	
	public static final int DPAD_DOWN 			= 16;
	public static final int DPAD_UP 			= 17;
	public static final int DPAD_LEFT			= 18;
	public static final int DPAD_RIGHT 			= 19;
	
	public static final int FACE_DOWN 			= 20;
	public static final int FACE_UP 			= 21;
	public static final int FACE_LEFT			= 22;
	public static final int FACE_RIGHT 			= 23;
	
	public static final int control_animations_total = 24;
	
	private static boolean animationsLoaded = false;
	private static Animation[] controlAnimations;
	
	private Vector2 myWiggle = new Vector2();
	
	private String message;
	private StringLayout layout;
	private Vector2 messagePosition = new Vector2();
	private int messageAlignment = Align.left;
	private boolean hasMessage = false;
	
	//TODO Javadocs
	public ControllerDrawer(int anchor, int registration)
	{
		this.anchor = anchor;
		this.registration = registration;
		layout = new StringLayout("", ChaseApp.menuFont);
		
		inputRegion = new MenuTextureRegion(null, Vector2.Zero, MenuTextureRegion.IGNORE, MenuTextureRegion.MID_CENTER);
		
		if(animationsLoaded == false)
		{
			loadAnimations();
		}
	}
	
	//TODO Javadocs
	private void loadAnimations()
	{
		if(animationsLoaded)
		{
			// This should only ever be called once!
			// Complaining loudly
			for (int i = 0; i < 5; i++)
			{
				Gdx.app.log("Drawer", "ANIMATIONS HAVE ALREADY BEEN LOADED");
			}
		}
		
		controlAnimations = new Animation[control_animations_total];

		buildAnimation(
				DPAD_DOWN_BLINK, // animation ID
				0.5f,			 // frame duration
				"dpad",			 // region name
				new int[]{5,2},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				DPAD_UP_BLINK,   // animation ID
				0.5f,			 // frame duration
				"dpad",			 // region name
				new int[]{5,4},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				DPAD_LEFT_BLINK, // animation ID
				0.5f,			 // frame duration
				"dpad",			 // region name
				new int[]{5,1},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				DPAD_RIGHT_BLINK,// animation ID
				0.5f,			 // frame duration
				"dpad",			 // region name
				new int[]{5,3},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				DPAD_ANY_BLINK,  // animation ID
				0.5f,			 // frame duration
				"dpad",			 // region name
				new int[]{5,0},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				FACE_DOWN_BLINK, // animation ID
				0.5f,			 // frame duration
				"buttons",			 // region name
				new int[]{5,2},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				FACE_UP_BLINK, // animation ID
				0.5f,			 // frame duration
				"buttons",			 // region name
				new int[]{5,4},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				FACE_LEFT_BLINK, // animation ID
				0.5f,			 // frame duration
				"buttons",			 // region name
				new int[]{5,1},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				FACE_RIGHT_BLINK, // animation ID
				0.5f,			  // frame duration
				"buttons",		  // region name
				new int[]{5,3},   // array of frame numbers
				Animation.PlayMode.LOOP    // animation type
			);

		buildAnimation(
				FACE_ANY_BLINK, // animation ID
				0.5f,			 // frame duration
				"buttons",			 // region name
				new int[]{5,0},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				STICK_UP_DOWN, 			// animation ID
				0.5f,			 		// frame duration
				"stick",			 	// region name
				new int[]{7,8,3},		// array of frame numbers
				Animation.PlayMode.LOOP_PINGPONG // animation type
			);

		buildAnimation(
				STICK_LEFT_RIGHT,		// animation ID
				0.5f,			 		// frame duration
				"stick",			 	// region name
				new int[]{1,8,5},		// array of frame numbers
				Animation.PlayMode.LOOP_PINGPONG // animation type
			);

		buildAnimation(
				STICK_ROTATE, 			// animation ID
				0.5f,			 		// frame duration
				"stick",			 	// region name
				new int[]{7,6,5,4,3,2,1,0},		// array of frame numbers
				Animation.PlayMode.LOOP // animation type
			);

		buildAnimation(
				STICK_3_BLINK, 			// animation ID
				0.5f,			 		// frame duration
				"stick",			 	// region name
				new int[]{8,10},		// array of frame numbers
				Animation.PlayMode.LOOP // animation type
			);

		buildAnimation(
				L_BUMPER, 			// animation ID
				0.5f,			 	// frame duration
				"lShoulder",		// region name
				new int[]{1,0},		// array of frame numbers
				Animation.PlayMode.LOOP 		// animation type
			);

		buildAnimation(
				R_BUMPER, 			// animation ID
				0.5f,			 	// frame duration
				"rShoulder",		// region name
				new int[]{1,0},		// array of frame numbers
				Animation.PlayMode.LOOP 		// animation type
			);

		buildAnimation(
				DPAD_DOWN, // animation ID
				0.5f,			 // frame duration
				"dpad",			 // region name
				new int[]{2},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				DPAD_UP,   // animation ID
				0.5f,			 // frame duration
				"dpad",			 // region name
				new int[]{4},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				DPAD_LEFT, // animation ID
				0.5f,			 // frame duration
				"dpad",			 // region name
				new int[]{1},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				DPAD_RIGHT,// animation ID
				0.5f,			 // frame duration
				"dpad",			 // region name
				new int[]{3},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				FACE_DOWN, // animation ID
				0.5f,			 // frame duration
				"buttons",			 // region name
				new int[]{2},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				FACE_UP, // animation ID
				0.5f,			 // frame duration
				"buttons",			 // region name
				new int[]{4},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				FACE_LEFT, // animation ID
				0.5f,			 // frame duration
				"buttons",			 // region name
				new int[]{1},  // array of frame numbers
				Animation.PlayMode.LOOP   // animation type
			);

		buildAnimation(
				FACE_RIGHT, // animation ID
				0.5f,			  // frame duration
				"buttons",		  // region name
				new int[]{3},   // array of frame numbers
				Animation.PlayMode.LOOP    // animation type
			);
		
		animationsLoaded = true;
	}
	
	//TODO Javadocs
	private void buildAnimation(int TYPE, float frameDuration, String regionID, int[] indices, Animation.PlayMode playMode)
	{
		Array<TextureRegion> regionArray = new Array<TextureRegion>();
		
		
		for(int i = 0; i < indices.length; i++)
		{
			regionArray.add(ChaseApp.menuControlsAtlas.findRegion(regionID, indices[i]));
		}
			
		controlAnimations[TYPE] = new Animation(frameDuration, regionArray);
		controlAnimations[TYPE].setPlayMode(playMode);
	}
	
	//TODO Javadocs
	public void showWing(boolean isLeft)
	{
		shouldShowWing = true;
		
		if(isLeft)
		{
			wing = new MenuTextureRegion(ChaseApp.menuControlsAtlas.findRegion("wing",0),
					new Vector2(-20,-28),
					MenuTextureRegion.IGNORE,
					MenuTextureRegion.MID_CENTER);
		}
		else
		{
			wing = new MenuTextureRegion(ChaseApp.menuControlsAtlas.findRegion("wing",1),
					new Vector2(20,-28),
					MenuTextureRegion.IGNORE,
					MenuTextureRegion.MID_CENTER);
		}
		
		isLeftWing = isLeft;
		
		setPositionByWingAnimation();
	}
	
	//TODO Javadocs
	public void showAnimation(int whichAnimation)
	{
		if(whichAnimation > -2 && whichAnimation < control_animations_total)
		{
			myAnimation = whichAnimation;
		}
		else
		{
			myAnimation = NONE;
			// Complaining loudly
			for (int i = 0; i < 5; i++)
			{
				Gdx.app.log("Drawer", "THERE IS NO ANIMATION HERE TO DRAW");
			}
		}
		
		setPositionByWingAnimation();
	}
	
	//TODO Javadocs
	public void setPositionByWingAnimation()
	{
		if(shouldShowWing)
		{
			switch(myAnimation)
			{
				case ControllerDrawer.DPAD_UP_BLINK:
				case ControllerDrawer.DPAD_DOWN_BLINK:
				case ControllerDrawer.DPAD_LEFT_BLINK:
				case ControllerDrawer.DPAD_RIGHT_BLINK:
				case ControllerDrawer.FACE_UP_BLINK:
				case ControllerDrawer.FACE_DOWN_BLINK:
				case ControllerDrawer.FACE_LEFT_BLINK:
				case ControllerDrawer.FACE_RIGHT_BLINK:
					inputRegion.setPosition(0,0);
					break;
				case ControllerDrawer.STICK_UP_DOWN:
				case ControllerDrawer.STICK_LEFT_RIGHT:
				case ControllerDrawer.STICK_ROTATE:
				case ControllerDrawer.STICK_3_BLINK:
					inputRegion.setPosition(0,7);
					break;
				case ControllerDrawer.L_BUMPER:
						inputRegion.setPosition(8,62);
					break;
				case ControllerDrawer.R_BUMPER:
						inputRegion.setPosition(-8,62);
					break;
			}
		}
		else
		{
			inputRegion.setPosition(0,0);
		}
	}
	
	//TODO Javadocs
	public void draw(SpriteBatch sBatch, float delta)
	{
		if(shouldShowWing)
		{
			wing.draw(sBatch, myWiggle);
		}
		
		if(myAnimation != NONE)
		{
			myAnimationAccumulator += delta;
			TextureRegion frame = controlAnimations[myAnimation].getKeyFrame(myAnimationAccumulator);
			inputRegion.setRegion(frame);
			inputRegion.draw(sBatch, myWiggle);
		}
		
		if(hasMessage)
		{
			float alignmentOffsetX = 0;
			
			if(messageAlignment == Align.right)
			{
				alignmentOffsetX = -layout.getLayout().width;
			}
			
			// TODO revisit this later
			ChaseApp.menuFont.draw(
				sBatch,
				layout.getLayout(),
				myWiggle.x + messagePosition.x + alignmentOffsetX,
				myWiggle.y + messagePosition.y);
		}
	}
	
	//TODO Javadocs
	public void setWiggle(float x, float y)
	{
		myWiggle.x = MenuTextureRegion.MENU_ANCHORS[this.anchor].x + x;
		myWiggle.y = MenuTextureRegion.MENU_ANCHORS[this.anchor].y + y;
	}
	
	//TODO Javadocs
	public void setMessage(String to, float x, float y, int alignment)
	{
		hasMessage = true;
		messagePosition.x = x;
		messagePosition.y = y;
		message = to;
		messageAlignment = alignment;
		layout.updateText(message);
	}
	
	//TODO Javadocs
	public void clearMessage()
	{
		hasMessage = false;
	}
}
