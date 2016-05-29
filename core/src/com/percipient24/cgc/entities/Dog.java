package com.percipient24.cgc.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.b2dhelpers.LayerHandler;
import com.percipient24.enums.EntityType;

public class Dog extends RotatableEntity {

	public Dog(Animation newLowAnimation, Animation newMidAnimation,
			Animation newHighAnimation, EntityType pEntityType,
			Body attachedBody) {
		super(newLowAnimation, newMidAnimation, newHighAnimation, pEntityType,
				attachedBody);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void step(float deltaTime, int layer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addToWorldLayers(LayerHandler lh) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFromWorldLayers(LayerHandler lh) {
		// TODO Auto-generated method stub

	}

}
