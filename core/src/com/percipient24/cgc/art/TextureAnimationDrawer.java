/*
 * @(#)AnimationManager.java		0.3 14/2/17
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.art;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.ChainGame;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.entities.terrain.Water;
import com.percipient24.input.InputManager;

/*
 * Contains the logic for rendering animations
 * 
 * @version 0.3 14/2/17
 * @author Christopher Rider
 * @author William Ziegler
 */
public class TextureAnimationDrawer
{
	public static final float RUN_ANIM_FRAME_TIME = 1.0f/12.0f;
	public static final float STAND_ANIM_FRAME_TIME = 1.0f/8.0f;
	public static final float HIT_ANIM_FRAME_TIME = 1.0f/8.0f; // TODO Fix this when there's an animation
	public static final float JUMP_ANIM_FRAME_TIME = 1.0f/8.0f; // TODO Fix this when there's an animation
	public static final float PUNCH_ANIM_FRAME_TIME = 0.053f; // Roughly 18.5 fps
	public static final float DIE_ANIM_FRAME_TIME = 1.0f;
	public static final float TIED_ANIM_FRAME_TIME = 1.0f/8.0f;
	public static final float TERRAIN_ANIM_FRAME_TIME = 3.0f/4.0f; // TODO Fix this when there's an animation
	public static final float GATE_ANIM_FRAME_TIME = 1.0f/16.0f;
	
	// Character Animations
	public static Animation[] prisonerRunLowAnims;
	public static Animation[] prisonerRunMidAnims;
	public static Animation[] prisonerRunHighAnims;
	public static Animation[] prisonerStandLowAnims;
	public static Animation[] prisonerStandMidAnims;
	public static Animation[] prisonerStandHighAnims;
	public static Animation[] prisonerHitAnims;
	public static Animation[] prisonerJumpAnims;
	public static Animation[] prisonerPunchLowAnims;
	public static Animation[] prisonerPunchMidAnims;
	public static Animation[] prisonerPunchHighAnims;
	public static Animation[] prisonerDieLowAnims;
	public static Animation[] prisonerDieMidAnims;
	public static Animation[] prisonerDieHighAnims;
	public static Animation[] prisonerTiedLowAnims;
	public static Animation[] prisonerTiedMidAnims;
	public static Animation[] prisonerTiedHighAnims;
	public static Animation[] copRunLowAnims;
	public static Animation[] copRunMidAnims;
	public static Animation[] copRunHighAnims;
	public static Animation[] copStandLowAnims;
	public static Animation[] copStandMidAnims;
	public static Animation[] copStandHighAnims;
	public static Animation[] copHitAnims;
	public static Animation[] copJumpAnims;
	public static Animation[] copPunchAnims;
	public static Animation[] copDieLowAnims;
	public static Animation[] copDieMidAnims;
	public static Animation[] copDieHighAnims;
	
	// Terrain Animations
	public static Animation[] bgAnims;
	public static Animation[] bridgeAnims;
	public static Animation[] currentAnims;
	public static Animation[] mudAnims;
	public static Animation[] waterAnims;
	public static Animation ouyaWater;
	public static Animation ouyaMud;
	
	// World Animations
	public static Animation[] chainAnims;
	public static Animation[] fenceAnims;
	public static Animation gateAnim;
	public static Animation helicopterAnim;
	public static Animation hwallAnim;
	public static Animation[] postAnims;
	public static Animation sensorAnim;
	public static Animation trackAnim;
	public static Animation trackSolid;
	public static Animation[] trainAnims;
	public static Animation[] treeAnims;
	public static Animation[] towerAnims;
	public static Animation[] vwallAnims;
	public static Animation coinAnim;
	
	// HUD Animations
	public static Animation[] calloutAnims;
	public static Animation copUpArrowAnim;
	public static Animation copDownArrowAnim;
	public static Animation barBackAnim;
	public static Animation grabFillAnim;
	public static Animation staminaFillAnim;
	public static Animation dazedAnim;
	public static Animation hudTankAnim;
	public static Animation offScreenTimerAnim;
	public static Animation prisonerDownArrowAnim;
	public static Animation prisonerUpArrowAnim;
	public static Animation progConvictAnim;
	public static Animation progCopAnim;
	public static Animation progFinishAnim;
	public static Animation progMeterAnim;
	public static Animation progTrackAppearAnim;
	public static Animation progTrackVanishAnim;
	public static Animation sensorSqrAni;
	public static Animation sensorStarAnim;
	public static Animation sensorTriAnim;
	public static Animation[] keyHeadAnims;
	public static Animation keepGoingAnim;
	public static Animation punchMeAnim;
	
	// Boss Animations
	public static Animation boostAnim;
	public static Animation crashAnim;
	public static Animation[] jeepAnims;
	public static Animation pallBearerAnim;
	public static Animation sheriffAnim;
	public static Animation steelHorseAnim;
	public static Animation[] tankAnims;
	
	// Projectile Animations
	public static Animation bulletAnim;
	public static Animation explosionAnim;
	public static Animation tankShellAnim;
	
	// Targeting Animations
	public static Animation spotlightAnim;
	public static Animation[] targetingAnims;

	/*
	 * Creates a new TextureAnimationDrawer object
	 * 
	 * @param myApp					The ChaseApp to use
	 * @param input					The InputManager used by this game
	 */
	public TextureAnimationDrawer(ChaseApp myApp, InputManager input)
	{
		loadCharacterAnimations(input);
		loadTerrainAnimations();
		loadWorldAnimations();
		loadHUDAnimations();
		loadBossAnimations();
		loadProjectileAnimations();
		loadTargetingAnimations();
	}
	
	/*
	 * Loads textures for targeting icons
	 */
	private void loadTargetingAnimations()
	{
		Array<TextureRegion> targeterRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/targeter"));
		targetingAnims = new Animation[targeterRegions.size];
		for (int i = 0; i < targetingAnims.length; i++)
		{
			Animation newTargetAnimation = new Animation(1.0f, targeterRegions.get(i));
			newTargetAnimation.setPlayMode(Animation.PlayMode.LOOP);
			targetingAnims[i] = newTargetAnimation;
		}
		
		spotlightAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/spotlight")));
		spotlightAnim.setPlayMode(Animation.PlayMode.LOOP);
	}
	
	/*
	 * Loads textures for projectiles and projectile-related entities
	 */
	private void loadProjectileAnimations()
	{
		explosionAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("explosion")));
		explosionAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		tankShellAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("tankshell")));
		tankShellAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		bulletAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("bullet")));
		bulletAnim.setPlayMode(Animation.PlayMode.LOOP);
	}
	
	/*
	 * Loads textures for anything not game world related
	 */
	private void loadHUDAnimations()
	{
		barBackAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/grabbackground")));
		barBackAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		grabFillAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/grabfill")));
		grabFillAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		staminaFillAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/staminafill")));
		staminaFillAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		dazedAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/dizzydonuts")));
		dazedAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		Array<TextureRegion> calloutRegions = new Array<TextureRegion>();
		calloutAnims = new Animation[ChainGame.numPlayers];
		for (int i = 0; i < ChainGame.numPlayers; i++)
		{
			calloutRegions.add(ChaseApp.atlas.findRegion("overlayanimations/callout", i));
			Animation calloutAnim = new Animation(1.0f, calloutRegions.get(i));
			calloutAnim.setPlayMode(Animation.PlayMode.LOOP);
			calloutAnims[i] = calloutAnim;
		}

		offScreenTimerAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/offscreentimer")));
		offScreenTimerAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		copUpArrowAnim = new Animation(1.0f, new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/cop_up")));
		copUpArrowAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		copDownArrowAnim = new Animation(1.0f, new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/cop_down")));
		copDownArrowAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		prisonerUpArrowAnim = new Animation(1.0f, new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/prisoner_up")));
		prisonerUpArrowAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		prisonerDownArrowAnim = new Animation(1.0f, new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/prisoner_down")));
		prisonerDownArrowAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		progMeterAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/progressmeter")));
		progMeterAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		progTrackVanishAnim = new Animation(1/15f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/progresstraintrack")));
		progTrackVanishAnim.setPlayMode(Animation.PlayMode.NORMAL);
		
		progTrackAppearAnim = new Animation(1/15f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/progresstraintrack")));
		progTrackAppearAnim.setPlayMode(Animation.PlayMode.REVERSED);
		
		progFinishAnim = new Animation(1/15f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/progressfinish")));
		progFinishAnim.setPlayMode(Animation.PlayMode.REVERSED);
		
		progConvictAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/progressconvict")));
		progConvictAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		progCopAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/progresscop")));
		progCopAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		hudTankAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/hudtank")));
		hudTankAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		sensorTriAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/sensortriangle")));
		sensorTriAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		sensorSqrAni = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/sensorsquare")));
		sensorSqrAni.setPlayMode(Animation.PlayMode.LOOP);
		
		sensorStarAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/sensorstar")));
		sensorStarAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		keepGoingAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/keepgoing")));
		keepGoingAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		punchMeAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("overlayanimations/punch_me")));
		punchMeAnim.setPlayMode(Animation.PlayMode.LOOP);
	}
	
	/*
	 * Loads textures for entities inside the game world
	 */
	private void loadWorldAnimations()
	{
		Array<TextureRegion> towerRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("guardtower"));
		towerAnims = new Animation[towerRegions.size];
		for (int i = 0; i < towerAnims.length; i++)
		{
			Animation newTowerAnimation = new Animation(1.0f, towerRegions.get(i));
			newTowerAnimation.setPlayMode(Animation.PlayMode.LOOP);
			towerAnims[i] = newTowerAnimation;
		}
		
		Array<TextureRegion> treeRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("tree"));
		treeAnims = new Animation[treeRegions.size];
		for (int i = 0; i < treeAnims.length; i++)
		{
			Animation newTreeAnimation = new Animation(1.0f, treeRegions.get(i));
			newTreeAnimation.setPlayMode(Animation.PlayMode.LOOP);
			treeAnims[i] = newTreeAnimation;
		}
		
		Array<TextureRegion> chainRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("chain"));
		chainAnims= new Animation[2];
		for (int i = 0; i < chainAnims.length; i++)
		{
			Animation newChainAnimation = new Animation(0.125f, chainRegions);
			newChainAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
			chainAnims[i] = newChainAnimation;
			chainRegions.reverse();
		}
		
		hwallAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("hwall")));
		hwallAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		Array<TextureRegion> vwallRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("vwall"));
		vwallAnims = new Animation[vwallRegions.size];
		for (int i = 0; i < vwallAnims.length; i++)
		{
			Animation newVwallAnimation = new Animation(1.0f, vwallRegions.get(i));
			newVwallAnimation.setPlayMode(Animation.PlayMode.LOOP);
			vwallAnims [i] = newVwallAnimation;
		}
		
		//TODO: play with this number to get a non-seizure inducing animation
		trackAnim = new Animation(0.03125f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("track")));
		trackAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		Array<TextureRegion> oneTrack = new Array<TextureRegion>();
		oneTrack.add(ChaseApp.atlas.findRegion("track", 0));
		trackSolid = new Animation(1.0f, oneTrack);
		
		
		Array<TextureRegion> trainRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("train"));
		trainAnims = new Animation[trainRegions.size];
		for (int i = 0; i < trainAnims.length; i++)
		{
			Animation newTrainAnimation = new Animation(1.0f, trainRegions.get(i));
			newTrainAnimation.setPlayMode(Animation.PlayMode.LOOP);
			trainAnims[i] = newTrainAnimation;
		}
		
		Array<TextureRegion> fenceRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("fence"));
		fenceAnims = new Animation[fenceRegions.size];
		for (int i = 0; i < fenceAnims.length; i++)
		{
			Animation newFenceAnimation = new Animation(1.0f, fenceRegions.get(i));
			newFenceAnimation.setPlayMode(Animation.PlayMode.LOOP);
			fenceAnims[i] = newFenceAnimation;
		}
		
		Array<TextureRegion> postRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("post"));
		postAnims = new Animation[postRegions.size];
		for (int i = 0; i < postAnims.length; i++)
		{
			Animation newPostAnimation = new Animation(1.0f, postRegions.get(i));
			newPostAnimation.setPlayMode(Animation.PlayMode.LOOP);
			postAnims[i] = newPostAnimation;
		}

		gateAnim = new Animation(GATE_ANIM_FRAME_TIME, 
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("gate")));
		gateAnim.setPlayMode(Animation.PlayMode.NORMAL);

		sensorAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("sensor")));
		sensorAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		helicopterAnim = new Animation(0.05f, 
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("helicopter")));
		helicopterAnim.setPlayMode(Animation.PlayMode.LOOP);

		coinAnim = new Animation(0.08f,
			new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/coin")));
		coinAnim.setPlayMode(Animation.PlayMode.LOOP);
	}
	
	/*
	 * Loads textures for game bosses and boss-related entities
	 */
	private void loadBossAnimations()
	{
		sheriffAnim = new Animation(1.0f, new Array<TextureRegion>(ChaseApp.atlas.findRegions("sheriff")));
		sheriffAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		Array<TextureRegion> jeepRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("jeep"));
		jeepAnims = new Animation[jeepRegions.size];
		for (int i = 0; i < jeepAnims.length; i++)
		{
			Animation newJeepAnimation = new Animation(1.0f, jeepRegions.get(i));
			newJeepAnimation.setPlayMode(Animation.PlayMode.LOOP);
			jeepAnims[i] = newJeepAnimation;
		}
		
		steelHorseAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("steelhorse")));
		steelHorseAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		boostAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("booststeelhorse")));
		boostAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		crashAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("crashsteelhorse")));
		crashAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		pallBearerAnim = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("pallbearer")));
		pallBearerAnim.setPlayMode(Animation.PlayMode.LOOP);
		
		Array<TextureRegion> tankRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("tank"));
		tankAnims = new Animation[tankRegions.size];
		for (int i = 0; i < tankAnims.length; i++)
		{
			Animation newTankAnimation = new Animation(1.0f, tankRegions.get(i));
			newTankAnimation.setPlayMode(Animation.PlayMode.LOOP);
			tankAnims[i] = newTankAnimation;
		}
	}

	public static void loadDefaultCharacterAnimations(InputManager input) {
		int max_players = 10;
		prisonerRunLowAnims = new Animation[max_players];
		prisonerRunMidAnims = new Animation[max_players];
		prisonerRunHighAnims = new Animation[max_players];
		prisonerStandLowAnims = new Animation[max_players];
		prisonerStandMidAnims = new Animation[max_players];
		prisonerStandHighAnims = new Animation[max_players];
		prisonerHitAnims = new Animation[max_players];
		prisonerJumpAnims = new Animation[max_players];
		prisonerPunchLowAnims = new Animation[max_players];
		prisonerPunchMidAnims = new Animation[max_players];
		prisonerPunchHighAnims = new Animation[max_players];
		prisonerDieLowAnims = new Animation[max_players];
		prisonerDieMidAnims = new Animation[max_players];
		prisonerDieHighAnims = new Animation[max_players];
		prisonerTiedLowAnims = new Animation[max_players];
		prisonerTiedMidAnims = new Animation[max_players];
		prisonerTiedHighAnims = new Animation[max_players];
		keyHeadAnims = new Animation[max_players];

		for (int i = 0; i < input.controlList.length; i++)
		{
			int pid = i;

			// Load the run cycle
			Animation newAnimation;

			newAnimation = new Animation(RUN_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conrunlow0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerRunLowAnims[pid] = newAnimation;

			newAnimation = new Animation(RUN_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conrunmid0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerRunMidAnims[pid] = newAnimation;

			newAnimation = new Animation(RUN_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conrunhigh0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerRunHighAnims[pid] = newAnimation;

			newAnimation = new Animation(STAND_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/constandlow0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerStandLowAnims[pid] = newAnimation;

			newAnimation = new Animation(STAND_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/constandmid0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerStandMidAnims[pid] = newAnimation;

			newAnimation = new Animation(STAND_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/constandhigh0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerStandHighAnims[pid] = newAnimation;

			newAnimation = new Animation(HIT_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conhit0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerHitAnims[pid] = newAnimation;

			newAnimation = new Animation(JUMP_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conjump0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerJumpAnims[pid] = newAnimation;

			newAnimation = new Animation(PUNCH_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conpunchlow0")));
			newAnimation.setPlayMode(Animation.PlayMode.NORMAL);
			prisonerPunchLowAnims[pid] = newAnimation;

			newAnimation = new Animation(PUNCH_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conpunchmid0")));
			newAnimation.setPlayMode(Animation.PlayMode.NORMAL);
			prisonerPunchMidAnims[pid] = newAnimation;

			newAnimation = new Animation(PUNCH_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conpunchhigh0")));
			newAnimation.setPlayMode(Animation.PlayMode.NORMAL);
			prisonerPunchHighAnims[pid] = newAnimation;

			newAnimation = new Animation(DIE_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/condielow0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerDieLowAnims[pid] = newAnimation;

			newAnimation = new Animation(DIE_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/condiemid0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerDieMidAnims[pid] = newAnimation;

			newAnimation = new Animation(DIE_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/condiehigh0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerDieHighAnims[pid] = newAnimation;

			newAnimation = new Animation(TIED_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/construgglelow0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerTiedLowAnims[pid] = newAnimation;

			newAnimation = new Animation(TIED_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/construgglemid0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerTiedMidAnims[pid] = newAnimation;

			newAnimation = new Animation(TIED_ANIM_FRAME_TIME,
					new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/construgglehigh0")));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			prisonerTiedHighAnims[pid] = newAnimation;

			AtlasRegion keyHeadTempRegion = ChaseApp.atlas.findRegion("overlayanimations/keyhead", 0);
		}
	}
	
	/*
	 * Loads textures for player icons
	 * 
	 * @param input					The InputManager used by the game
	 */
	private void loadCharacterAnimations(InputManager input)
	{
		// Load prisoner animations
		prisonerRunLowAnims = new Animation[CGCWorld.numPlayers];
		prisonerRunMidAnims = new Animation[CGCWorld.numPlayers];
		prisonerRunHighAnims = new Animation[CGCWorld.numPlayers];
		prisonerStandLowAnims = new Animation[CGCWorld.numPlayers];
		prisonerStandMidAnims = new Animation[CGCWorld.numPlayers];
		prisonerStandHighAnims = new Animation[CGCWorld.numPlayers];
		prisonerHitAnims = new Animation[CGCWorld.numPlayers];
		prisonerJumpAnims = new Animation[CGCWorld.numPlayers];
		prisonerPunchLowAnims = new Animation[CGCWorld.numPlayers];
		prisonerPunchMidAnims = new Animation[CGCWorld.numPlayers];
		prisonerPunchHighAnims = new Animation[CGCWorld.numPlayers];
		prisonerDieLowAnims = new Animation[CGCWorld.numPlayers];
		prisonerDieMidAnims = new Animation[CGCWorld.numPlayers];
		prisonerDieHighAnims = new Animation[CGCWorld.numPlayers];
		prisonerTiedLowAnims = new Animation[CGCWorld.numPlayers];
		prisonerTiedMidAnims = new Animation[CGCWorld.numPlayers];
		prisonerTiedHighAnims = new Animation[CGCWorld.numPlayers];
		keyHeadAnims = new Animation[CGCWorld.numPlayers];
		
		for (int i = 0; i < input.controlList.length; i++)
		{
			if (input.controlList[i].isUsed())
			{
				int charChoice = input.controlList[i].getConvictChoice();
				int pid = input.controlList[i].getPID();
				
				// Load the run cycle
				Animation newAnimation;
				Array<AtlasRegion> conRegions = ChaseApp.atlas.findRegions("gameanimations/conrunlow"+
				 String.valueOf(charChoice));
				if (conRegions.size != 0)
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerRunLowAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conrunlow0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerRunLowAnims[pid] = newAnimation;
				}
				
				conRegions = ChaseApp.atlas.findRegions("gameanimations/conrunmid"+
						String.valueOf(charChoice));
				if (conRegions.size != 0)
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerRunMidAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conrunmid0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerRunMidAnims[pid] = newAnimation;
				}
				
				conRegions = ChaseApp.atlas.findRegions("gameanimations/conrunhigh"+
						String.valueOf(charChoice));
				if (conRegions.size != 0)
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerRunHighAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conrunhigh0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerRunHighAnims[pid] = newAnimation;
				}
				
				// Load the stand cycle
				conRegions = ChaseApp.atlas.findRegions("gameanimations/constandlow" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerStandLowAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/constandlow0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerStandLowAnims[pid] = newAnimation;
				}
				
				conRegions = ChaseApp.atlas.findRegions("gameanimations/constandmid" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerStandMidAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/constandmid0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerStandMidAnims[pid] = newAnimation;
				}
				
				conRegions = ChaseApp.atlas.findRegions("gameanimations/constandhigh" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerStandHighAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/constandhigh0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerStandHighAnims[pid] = newAnimation;
				}
				
				// Load the hit animations
				conRegions = ChaseApp.atlas.findRegions("gameanimations/conhit" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(HIT_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerHitAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(HIT_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conhit0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerHitAnims[pid] = newAnimation;
				}
				
				// Load the jump animations
				conRegions = ChaseApp.atlas.findRegions("gameanimations/conjump" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(JUMP_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerJumpAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(JUMP_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conjump0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerJumpAnims[pid] = newAnimation;
				}
				
				// Load the punch animations
				conRegions = ChaseApp.atlas.findRegions("gameanimations/conpunchlow" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(PUNCH_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.NORMAL);
					prisonerPunchLowAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(PUNCH_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conpunchlow0")));
					newAnimation.setPlayMode(Animation.PlayMode.NORMAL);
					prisonerPunchLowAnims[pid] = newAnimation;
				}
				
				conRegions = ChaseApp.atlas.findRegions("gameanimations/conpunchmid" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(PUNCH_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.NORMAL);
					prisonerPunchMidAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(PUNCH_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conpunchmid0")));
					newAnimation.setPlayMode(Animation.PlayMode.NORMAL);
					prisonerPunchMidAnims[pid] = newAnimation;
				}
				
				conRegions = ChaseApp.atlas.findRegions("gameanimations/conpunchhigh" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(PUNCH_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.NORMAL);
					prisonerPunchHighAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(PUNCH_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/conpunchhigh0")));
					newAnimation.setPlayMode(Animation.PlayMode.NORMAL);
					prisonerPunchHighAnims[pid] = newAnimation;
				}
				
				// Load the death animations
				conRegions = ChaseApp.atlas.findRegions("gameanimations/condielow" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerDieLowAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/condielow0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerDieLowAnims[pid] = newAnimation;
				}
				
				conRegions = ChaseApp.atlas.findRegions("gameanimations/condiemid" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerDieMidAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/condiemid0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerDieMidAnims[pid] = newAnimation;
				}
				
				conRegions = ChaseApp.atlas.findRegions("gameanimations/condiehigh" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerDieHighAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/condiehigh0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerDieHighAnims[pid] = newAnimation;
				}
				
				// Load the tied animations
				conRegions = ChaseApp.atlas.findRegions("gameanimations/construgglelow" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(TIED_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerTiedLowAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(TIED_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/construgglelow0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerTiedLowAnims[pid] = newAnimation;
				}
				
				conRegions = ChaseApp.atlas.findRegions("gameanimations/construgglemid" +
						String.valueOf(charChoice));
				if(conRegions.size != 0)
				{
					newAnimation = new Animation(TIED_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerTiedMidAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(TIED_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/construgglemid0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerTiedMidAnims[pid] = newAnimation;
				}
				
				conRegions = ChaseApp.atlas.findRegions("gameanimations/construgglehigh" +
						String.valueOf(charChoice));
				if (conRegions.size != 0)
				{
					newAnimation = new Animation(TIED_ANIM_FRAME_TIME, new Array<TextureRegion>(conRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerTiedHighAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(TIED_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/construgglehigh0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					prisonerTiedHighAnims[pid] = newAnimation;
				}
				
				AtlasRegion keyHeadTempRegion = ChaseApp.atlas.findRegion("overlayanimations/keyhead", charChoice);
				if (keyHeadTempRegion != null)
				{
					keyHeadAnims[pid] = new Animation(1.0f, (TextureRegion)keyHeadTempRegion);
				}
				else
				{
					keyHeadAnims[pid] = new Animation(1.0f, 
							(TextureRegion)ChaseApp.atlas.findRegion("overlayanimations/keyhead", 0));
				}
			}
		}
		
		// Load cop animations
		copRunLowAnims = new Animation[CGCWorld.numPlayers];
		copRunMidAnims = new Animation[CGCWorld.numPlayers];
		copRunHighAnims = new Animation[CGCWorld.numPlayers];
		copStandLowAnims = new Animation[CGCWorld.numPlayers];
		copStandMidAnims = new Animation[CGCWorld.numPlayers];
		copStandHighAnims = new Animation[CGCWorld.numPlayers];
		copHitAnims = new Animation[CGCWorld.numPlayers];
		copJumpAnims = new Animation[CGCWorld.numPlayers];
		copPunchAnims = new Animation[CGCWorld.numPlayers];
		copDieLowAnims = new Animation[CGCWorld.numPlayers];
		copDieMidAnims = new Animation[CGCWorld.numPlayers];
		copDieHighAnims = new Animation[CGCWorld.numPlayers];
		
		// Assign cops to players
		for (int i = 0; i < input.controlList.length; i++)
		{
			if (input.controlList[i].isUsed())
			{
				int charChoice = input.controlList[i].getCopChoice();
				int pid = input.controlList[i].getPID();
				
				// Load the run cycle
				Animation newAnimation;
				Array<AtlasRegion> copRegions = ChaseApp.atlas.findRegions("gameanimations/coprunlow"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copRunLowAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/coprunlow0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copRunLowAnims[pid] = newAnimation;
				}
				
				copRegions = ChaseApp.atlas.findRegions("gameanimations/coprunmid"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copRunMidAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/coprunmid0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copRunMidAnims[pid] = newAnimation;
				}
				
				copRegions = ChaseApp.atlas.findRegions("gameanimations/conrunhigh"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copRunHighAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(RUN_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/coprunhigh0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copRunHighAnims[pid] = newAnimation;
				}
				
				// Load the stand animation
				copRegions = ChaseApp.atlas.findRegions("gameanimations/copstandlow"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copStandLowAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/copstandlow0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copStandLowAnims[pid] = newAnimation;
				}
				
				copRegions = ChaseApp.atlas.findRegions("gameanimations/copstandmid"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copStandMidAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/copstandmid0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copStandMidAnims[pid] = newAnimation;
				}
				
				copRegions = ChaseApp.atlas.findRegions("gameanimations/copstandhigh"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copStandHighAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(STAND_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/copstandhigh0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copStandHighAnims[pid] = newAnimation;
				}
				
				// Load the hit animation
				copRegions = ChaseApp.atlas.findRegions("gameanimations/cophit"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(HIT_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copHitAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(HIT_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/cophit0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copHitAnims[pid] = newAnimation;
				}
				
				// Load the jump animation
				copRegions = ChaseApp.atlas.findRegions("gameanimations/copjump"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(JUMP_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copJumpAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(JUMP_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/copjump0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copJumpAnims[pid] = newAnimation;
				}
				
				// Load the punch animation
				copRegions = ChaseApp.atlas.findRegions("gameanimations/coppunch"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(PUNCH_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copPunchAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(PUNCH_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/coppunch0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copPunchAnims[pid] = newAnimation;
				}
				
				// Load the death animation
				copRegions = ChaseApp.atlas.findRegions("gameanimations/copdielow"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copDieLowAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/copdielow0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copDieLowAnims[pid] = newAnimation;
				}
				
				copRegions = ChaseApp.atlas.findRegions("gameanimations/copdiemid"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copDieMidAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/copdiemid0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copDieMidAnims[pid] = newAnimation;
				}
				
				copRegions = ChaseApp.atlas.findRegions("gameanimations/copdiehigh"+
						String.valueOf(charChoice));
				if (copRegions.size != 0)
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME, new Array<TextureRegion>(copRegions));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copDieHighAnims[pid] = newAnimation;
				}
				else
				{
					newAnimation = new Animation(DIE_ANIM_FRAME_TIME,
							new Array<TextureRegion>(ChaseApp.atlas.findRegions("gameanimations/copdiehigh0")));
					newAnimation.setPlayMode(Animation.PlayMode.LOOP);
					copDieHighAnims[pid] = newAnimation;
				}
			}
		}
	}
	
	/*
	 * Loads textures for the various terrain in the game
	 */
	private void loadTerrainAnimations()
	{
		Animation bgAnimation = new Animation(1.0f, 
				new TextureRegion[]{ChaseApp.atlas.createSprite("terrainanimations/background")});
		bgAnimation.setPlayMode(Animation.PlayMode.LOOP);
		bgAnims = new Animation[]{bgAnimation};
		
		// Load Mud animations - should be ok with any number of frames, as long as they follow the right format
		mudAnims = new Animation[13];
		for (int i = 0; i <= 12; i++)
		{
			Array<TextureRegion> mudRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("terrainanimations/mud"+i));
			Animation newAnimation = new Animation(TERRAIN_ANIM_FRAME_TIME, new Array<TextureRegion>(mudRegions));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			mudAnims[i] = newAnimation;
		}
		
		waterAnims = new Animation[13];
		for (int i = 0; i <= 12; i++)
		{
			Array<TextureRegion> waterRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("terrainanimations/water"+i));
			Animation newAnimation = new Animation(TERRAIN_ANIM_FRAME_TIME, new Array<TextureRegion>(waterRegions));
			newAnimation.setPlayMode(Animation.PlayMode.LOOP);
			waterAnims[i] = newAnimation;
		}
		
		currentAnims = new Animation[9];
		for (int i = 0; i <= 8; i++)
		{
			Animation newAnimation;
			Array<AtlasRegion> currentRegions = ChaseApp.atlas.findRegions("terrainanimations/current"+i);
			if (currentRegions.size != 0)
			{
				newAnimation = new Animation(Water.CURRENT_FRAME_TIME, new Array<TextureRegion>(currentRegions));
				newAnimation.setPlayMode(Animation.PlayMode.LOOP);
				currentAnims[i] = newAnimation;
			}
			else
			{
				newAnimation = new Animation(Water.CURRENT_FRAME_TIME,
						new Array<TextureRegion>(ChaseApp.atlas.findRegions("terrainanimation/current0")));
				newAnimation.setPlayMode(Animation.PlayMode.LOOP);
				currentAnims[i] = newAnimation;
			}
		}
		
		Array<TextureRegion> bridgeRegions = new Array<TextureRegion>(ChaseApp.atlas.findRegions("terrainanimations/bridge"));
		bridgeAnims = new Animation[bridgeRegions.size];
		for (int i = 0; i < bridgeAnims.length; i++)
		{
			Animation newBridgeAnimation  = new Animation(1.0f, bridgeRegions.get(i));
			newBridgeAnimation.setPlayMode(Animation.PlayMode.LOOP);
			bridgeAnims[i] = newBridgeAnimation;
		}
		
		ouyaWater = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("terrainanimations/allWater")));
		ouyaWater.setPlayMode(Animation.PlayMode.LOOP);
		
		ouyaMud = new Animation(1.0f,
				new Array<TextureRegion>(ChaseApp.atlas.findRegions("terrainanimations/allMud")));
		ouyaMud.setPlayMode(Animation.PlayMode.LOOP);
	}
	
	/*
	 * Draws a single frame of an Animation
	 * 
	 * @param sBatch				The SpriteBatch to use
	 * @param region				The Animation the frame is from
	 * @param frame					Which frame to draw
	 * @param x						The X-coordinate of the drawn frame
	 * @param y						The Y-coordinate of the drawn frame
	 */
	public void drawFrame(SpriteBatch sBatch, Animation region, float frame, float x, float y)
	{
		sBatch.draw(region.getKeyFrame(frame), x, y);
	}
	
	/*
	 * Draws a single frame of an Animation
	 * 
	 * @param sBatch				The SpriteBatch to use
	 * @param region				The Animation the frame is from
	 * @param frame					Which frame to draw
	 * @param x						The X-coordinate of the drawn frame
	 * @param y						The Y-coordinate of the drawn frame
	 * @param originX				The X-coordinate of the drawn frame's origin point
	 * @param originY				The Y-coordinate of the drawn frame's origin point
	 * @param scaleX				The X-scaling of the drawn frame
	 * @param scaleY				The Y-scaling of the drawn frame
	 */
	public void drawFrame(SpriteBatch sBatch, Animation region, float frame, float x, float y,
			float originX, float originY, float scaleX, float scaleY)
	{
		sBatch.draw(region.getKeyFrame(frame), x, y, originX, originY,
				gWidth(region), gHeight(region), scaleX, scaleY, 0);
	}
	
	/*
	 * Draws a single frame of an Animation
	 * 
	 * @param sBatch				The SpriteBatch to use
	 * @param region				The Animation the frame is from
	 * @param frame					Which frame to draw
	 * @param x						The X-coordinate of the drawn frame
	 * @param y						The Y-coordinate of the drawn frame
	 * @param scaleX				The X-scaling of the drawn frame
	 * @param scaleY				The Y-scaling of the drawn frame
	 */
	public void drawFrame(SpriteBatch sBatch, Animation region, float frame, float x, float y, float scaleX, float scaleY)
	{
		sBatch.draw(region.getKeyFrame(frame), x, y, gWidth(region) / 2, gHeight(region) / 2,
				gWidth(region), gHeight(region), scaleX, scaleY, 0);
	}
	
	/*
	 * Draws a single frame of an Animation based on elapsed time
	 * 
	 * @param sBatch				The SpriteBatch to use
	 * @param region				The Animation the frame is from
	 * @param time					The elapsed time of the Animation
	 * @param looping				Whether or not the Animation should loop
	 * @param x						The X-coordinate of the drawn frame
	 * @param y						The Y-coordinate of the drawn frame
	 */
	public void drawAnimation(SpriteBatch sBatch, Animation region, float time, boolean looping, float x, float y)
	{
		sBatch.draw(region.getKeyFrame(time, looping), x, y);
	}
	
	/*
	 * Draws a single frame of an Animation based on elapsed time
	 * 
	 * @param sBatch				The SpriteBatch to use
	 * @param region				The Animation the frame is from
	 * @param time					The elapsed time of the Animation
	 * @param looping				Whether or not the Animation should loop
	 * @param x						The X-coordinate of the drawn frame
	 * @param y						The Y-coordinate of the drawn frame
	 * @param scaleX				The X-scaling of the drawn frame
	 * @param scaleY				The Y-scaling of the drawn frame
	 */
	public void drawAnimation(SpriteBatch sBatch, Animation region, float time, boolean looping,
			float x, float y, float scaleX, float scaleY)
	{
		sBatch.draw(region.getKeyFrame(time, looping), x, y, gWidth(region) / 2, gHeight(region) / 2,
				gWidth(region), gHeight(region), scaleX, scaleY, 0);
	}
	
	/*
	 * Draws a single frame of an Animation based on elapsed time
	 * 
	 * @param sBatch				The SpriteBatch to use
	 * @param region				The Animation the frame is from
	 * @param time					The elapsed time of the Animation
	 * @param looping				Whether or not the Animation should loop
	 * @param x						The X-coordinate of the drawn frame
	 * @param y						The Y-coordinate of the drawn frame
	 * @param originX				The X-coordinate of the drawn frame's origin point
	 * @param originY				The Y-coordinate of the drawn frame's origin point
	 * @param scaleX				The X-scaling of the drawn frame
	 * @param scaleY				The Y-scaling of the drawn frame
	 */
	public void drawAnimation(SpriteBatch sBatch, Animation region, float time, boolean looping,
			float x, float y, float originX, float originY, float scaleX, float scaleY)
	{
		sBatch.draw(region.getKeyFrame(time, looping), x, y, originX, originY,
				gWidth(region), gHeight(region), scaleX, scaleY, 0);
	}
	
	/*
	 * Gets the width of an Animation
	 * 
	 * @param animation				The Animation to examine
	 * @return						The width of the Animation
	 */
	public int gWidth(Animation animation)
	{
		return animation.getKeyFrame(0).getRegionWidth();
	}
	
	/*
	 * Gets the height of an Animation
	 * 
	 * @param animation				The Animation to examine
	 * @return						The height of the Animation
	 */
	public int gHeight(Animation animation)
	{
		return animation.getKeyFrame(0).getRegionHeight();
	}
} //End class