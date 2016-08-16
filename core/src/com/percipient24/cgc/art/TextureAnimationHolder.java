package com.percipient24.cgc.art;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.percipient24.cgc.ChaseApp;

/**
 * Created by percipient24 on 8/9/16.
 *
 * Holds references to all regions and animations on every atlas.
 * Loads the assets so no one else has to.
 */
public class TextureAnimationHolder {

	public static Animation characterSelect;

	public TextureAnimationHolder(ChaseApp app) {
		characterSelect = new Animation(1.0f, app.atlas.findRegion("characterSelect"));
	}
}
