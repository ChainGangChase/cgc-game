package com.percipient24.cgc.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.terrain.CharacterSelectSensor;
import com.percipient24.enums.EntityType;
import com.percipient24.helpers.LayerHandler;

/**
 * Created by percipient24 on 8/14/16.
 */
public class ChairSensor extends GameEntity {

	public CharacterSelectSensor handler;

	public ChairSensor(CharacterSelectSensor css, Body body) {
		super(null, null, null, EntityType.CHAIR_SENSOR, body);
		handler = css;
	}

	@Override
	public void step(float deltaTime, int layer) {

	}

	@Override
	public void addToWorldLayers(LayerHandler lh) {

	}

	@Override
	public void removeFromWorldLayers(LayerHandler lh) {

	}

	@Override
	public void collide(Player p) {
		ChaseApp.alert("hehehe");
	}

}
