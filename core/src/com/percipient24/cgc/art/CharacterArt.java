package com.percipient24.cgc.art;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.ChaseApp;

/**
 * Created by percipient24 on 8/9/16.
 *
 * Holds references to all texture/animation assets needed to draw this character.
 * Provided to Player/Cop instances and used for drawing.
 * Swapping the CharacterArt is an easy way to change skins.
 */
public class CharacterArt {

	// status
	public boolean used = false;
	public int index = 0;

	// durations
	public static final float RUN_ANIM_FRAME_TIME = 1.0f/12.0f;
	public static final float STAND_ANIM_FRAME_TIME = 1.0f/8.0f;
	public static final float HIT_ANIM_FRAME_TIME = 1.0f/8.0f; // TODO Fix this when there's an animation
	public static final float JUMP_ANIM_FRAME_TIME = 1.0f/8.0f; // TODO Fix this when there's an animation
	public static final float PUNCH_ANIM_FRAME_TIME = 0.053f; // Roughly 18.5 fps
	public static final float DIE_ANIM_FRAME_TIME = 1.0f;
	public static final float TIED_ANIM_FRAME_TIME = 1.0f/8.0f;
	public static final float TERRAIN_ANIM_FRAME_TIME = 3.0f/4.0f; // TODO Fix this when there's an animation
	public static final float GATE_ANIM_FRAME_TIME = 1.0f/16.0f;

	// portrait
	public TextureRegion portrait;

	// state
	public Animation dieHighAnim;
	public Animation dieLowAnim;
	public Animation dieMidAnim;

	public Animation hitAnim;

	public Animation jumpAnim;

	public Animation runLowAnim;
	public Animation runMidAnim;
	public Animation runHighAnim;

	public Animation punchLowAnim;
	public Animation punchMidAnim;
	public Animation punchHighAnim;
					 // ~ or ~ //
	public Animation copPunchAnim;

	public Animation standLowAnim;
	public Animation standMidAnim;
	public Animation standHighAnim;

	public Animation tiedLowAnim;
	public Animation tiedMidAnim;
	public Animation tiedHighAnim;

	public CharacterArt(ChaseApp app, String concop, int characterIndex) {
		index = characterIndex;
		String charIndex = String.valueOf(characterIndex);

		portrait = app.characterAtlas.findRegion("conportrait", characterIndex);

		dieLowAnim	= extractAnimation(app.atlas, "gameanimations/", concop, "dielow", charIndex, DIE_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
		dieMidAnim	= extractAnimation(app.atlas, "gameanimations/", concop, "diemid", charIndex, DIE_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
		dieHighAnim	= extractAnimation(app.atlas, "gameanimations/", concop, "diehigh", charIndex, DIE_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);

		hitAnim	 = extractAnimation(app.atlas, "gameanimations/", concop, "hit", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
		jumpAnim = extractAnimation(app.atlas, "gameanimations/", concop, "jump", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);

		if (concop == "con") {
			punchLowAnim = extractAnimation(app.atlas, "gameanimations/", concop, "punchlow", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
			punchMidAnim = extractAnimation(app.atlas, "gameanimations/", concop, "punchmid", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
			punchHighAnim = extractAnimation(app.atlas, "gameanimations/", concop, "punchhigh", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);

			tiedLowAnim	 = extractAnimation(app.atlas, "gameanimations/", concop, "strugglelow", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
			tiedMidAnim	 = extractAnimation(app.atlas, "gameanimations/", concop, "strugglemid", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
			tiedHighAnim = extractAnimation(app.atlas, "gameanimations/", concop, "strugglehigh", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
		} else {
			copPunchAnim = extractAnimation(app.atlas, "gameanimations/", concop, "punch", "0", RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
		}

		runLowAnim 	= extractAnimation(app.atlas, "gameanimations/", concop, "runlow", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
		runMidAnim	= extractAnimation(app.atlas, "gameanimations/", concop, "runmid", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
		runHighAnim	= extractAnimation(app.atlas, "gameanimations/", concop, "runhigh", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);

		standLowAnim  = extractAnimation(app.atlas, "gameanimations/", concop, "standlow", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
		standMidAnim  = extractAnimation(app.atlas, "gameanimations/", concop, "standmid", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
		standHighAnim = extractAnimation(app.atlas, "gameanimations/", concop, "standhigh", charIndex, RUN_ANIM_FRAME_TIME, Animation.PlayMode.LOOP);
	}

	private Animation extractAnimation(TextureAtlas atlas, String prefix, String concop, String state, String characterIndex, float runTime, Animation.PlayMode playMode) {
		Animation result;
		String regionName = prefix + concop + state + characterIndex;
		Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(regionName);

		if (regions.size == 0) {
			String backup = prefix + concop + state + "0";
			regions = atlas.findRegions(backup);
		}

		result = new Animation(
			runTime,
			new Array<TextureRegion>(regions)
		);
		result.setPlayMode(playMode);

		return result;
	}
}
