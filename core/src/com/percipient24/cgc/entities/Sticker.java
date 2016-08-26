package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.enums.EntityType;
import com.percipient24.helpers.LayerHandler;

/**
 * Created by percipient24 on 8/13/16.
 */
public class Sticker extends GameEntity {
	public Sticker(Animation low, Body body) {
		super(low, null, null, EntityType.BACKGROUND, body);
	}

	@Override
	public void step(float deltaTime, int layer) {
	}

	@Override
	public void addToWorldLayers(LayerHandler lh) {
		lh.addEntityToLayer(this, LayerHandler.terrain);
	}

	@Override
	public void removeFromWorldLayers(LayerHandler lh) {
		lh.removeEntityFromLayer(this, LayerHandler.terrain);
	}
}
