package com.percipient24.cgc.entities.terrain;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.cgc.art.Characters;
import com.percipient24.cgc.entities.ChairSensor;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.helpers.BodyFactory;

/**
 * Created by percipient24 on 8/14/16.
 */
public class CharacterSelectSensor {
	public int index;
	public Player player;
	public Player cop;
	public boolean isOccupied = false;
	public boolean isPlayerLocked = false;
	public boolean isCopLocked = false;
	private Body sensor;
	private float x;

	public CharacterSelectSensor(int index, float x, Player cop) {
		this.index = index;
		this.x = x;
		this.cop = cop;
		sensor = CGCWorld.getBF().createSensor(
			x,
			2.9f,
			0.2f,
			0.2f,
			BodyDef.BodyType.StaticBody,
			BodyFactory.CAT_INTERACTABLE,
			BodyFactory.MASK_INTERACTABLE);

		sensor.setUserData(new ChairSensor(this, sensor));
	}

	public void handleContact(GameEntity p, GameEntity chair, boolean start) {
		if (start) {
			player = (Player) p;
			player.sit(x, 2.8f, this);
			isOccupied = true;
			ChaseApp.characterSelect.respondToAdjustedPlayers();
		} else {
			if (player != null) {
				player.standUp();
			}
			ChaseApp.characterSelect.respondToAdjustedPlayers();
		}
	}

	public boolean isReady() {
		return (isOccupied && isPlayerLocked && isCopLocked) || !isOccupied;
	}

	public void standUp() {
		player = null;
		isOccupied = false;
		unlockCopSelection();
		unlockPlayerSelection();
		ChaseApp.characterSelect.respondToAdjustedPlayers();
	}

	public void lockPlayerSelection() {
		isPlayerLocked = true;
		player.copCharacter = Characters.getNextCopStartingAt(player.copCharacter, true);
		cop.setCharacter(player.copCharacter);
		cop.setAlpha(1);
		ChaseApp.characterSelect.respondToAdjustedPlayers();
	}

	public void lockCopSelection() {
		isCopLocked = true;
	}

	public void unlockPlayerSelection() {
		isPlayerLocked = false;
		isCopLocked = false;
		cop.setAlpha(0);
		ChaseApp.characterSelect.respondToAdjustedPlayers();
	}

	public void unlockCopSelection() {
		isCopLocked = false;
	}
}
