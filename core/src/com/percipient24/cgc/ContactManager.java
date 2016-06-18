/*
 * @(#)ContactManager.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.cgc.entities.ChainLink;
import com.percipient24.cgc.entities.Coin;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.GuardTower;
import com.percipient24.cgc.entities.PlayerWall;
import com.percipient24.cgc.entities.Sensor;
import com.percipient24.cgc.entities.Spotlight;
import com.percipient24.cgc.entities.Wall;
import com.percipient24.cgc.entities.boss.SteelHorse;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.entities.players.CarrierCop;
import com.percipient24.cgc.entities.terrain.Bridge;
import com.percipient24.cgc.entities.terrain.Mud;
import com.percipient24.cgc.entities.terrain.Water;
import com.percipient24.enums.DeathType;
import com.percipient24.enums.EntityType;

/*
 * Contains the logic to handle collisions
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 * @author Christopher Rider
 * @author William Ziegler
 */
public class ContactManager implements ContactListener 
{
	/*
	 * Starts when a collision is possible
	 * 
	 * @param contact				The AABB where contact may be occurring
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#beginContact(com.badlogic.gdx.physics.box2d.Contact)
	 */
	public void beginContact(Contact contact) 
	{
		if (contact == null || contact.getFixtureA() == null || contact.getFixtureB() == null ||
				contact.getFixtureA().getBody() == null || contact.getFixtureB().getBody() == null ||
				(GameEntity)contact.getFixtureA().getBody().getUserData() == null || 
				(GameEntity)contact.getFixtureB().getBody().getUserData() == null)
		{
			return;
		}

		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();
		GameEntity geA = (GameEntity)a.getUserData();
		GameEntity geB = (GameEntity)b.getUserData();
		
		if (geA.getType() == EntityType.CHAINLINK && geB.getType() == EntityType.CHAINLINK)
		{
			return;
		}
		
		geA.collide(geB);

		testPlayerCoin(geA, geB, true);
		testPlayerSensor(geA, geB, true);
		testPlayerMud(geA, geB, fA, fB, true);
		testPlayerWater(geA, geB, fA, fB, true);
		testPlayerTower(geA, geB, fA, fB, true);
		
		testPlayerBridge(geA, geB, fA, fB, true);
		testPlayerSpotlight(geA, geB, fA, fB, true);
		testCarrierCopChainLink(geA, geB, true);
		
		testSteelHorseWall(geA, geB, true);
	}
	
	/*
	 * Handles a ChainLink collision with a Wheel or Tank
	 * 
	 * @param chain					The chain to destroy
	 */
	public void handleChainWheel(GameEntity chain)
	{
		CGCWorld.addToDestroyList(chain);
	}
	
	/*
	 * Handles a Player collision with a train
	 * 
	 * @param p						The Player to kill by a train
	 */
	public void handlePlayerTrain(Player p)
	{
		if (p.isAlive())
		{
			p.die(DeathType.TRAIN);
		}
	}
	
	/*
	 * Handles a Player collision with a Sensor
	 * 
	 * @param player				The Player colliding with the Sensor
	 * @param sensor				The Sensor colliding with the Player
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void handlePlayerSensor(GameEntity player, GameEntity sensor, boolean start)
	{
		if (((Sensor)sensor).getDisabled())
		{
			return;
		}
		
		Prisoner p = (Prisoner) player;
		boolean canActivate = false;
		
		if (((Sensor) sensor).gLockID() == 0)
		{
			canActivate = true;
		}
		
		if (!canActivate)
		{
			if (p.canOpen(((Sensor) sensor).gLockID()))
			{
				canActivate = true;
			}
		}
		
		if (canActivate)
		{
			if (start)
			{
				if (!p.isJumping())
				{
					((Sensor) sensor).engage();
				}
				p.addSensorContacts(((Sensor)sensor));
			}
			else
			{
				if (!p.isJumping())
				{
					((Sensor) sensor).disengage();
				}
				p.removeSensorContact(((Sensor)sensor));
			}
		}
	}
	
	/*
	 * Handles a Player collision with Terrain
	 * 
	 * @param player				The Player colliding with the Terrain
	 * @param terrain				The Terrain colliding with the Player
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void handlePlayerTerrain(GameEntity player, GameEntity terrain, boolean start)
	{
		Player p = (Player) player;
		
		if (start)
		{
			if (terrain instanceof Mud)
			{
				p.addMudContacts((Mud) terrain);
			}
			else if (terrain instanceof Water)
			{
				p.addWaterContacts((Water) terrain);
			}
		}
		else
		{
			if (terrain instanceof Mud)
			{
				p.removeMudContact((Mud) terrain);
			}
			else if (terrain instanceof Water)
			{
				p.removeWaterContact((Water) terrain);
				if (p.getWaterContacts().size <= 0 && !p.isMoving() && p.getBridgeContacts().size <= 0)
				{
					p.getBody().setLinearVelocity(0.0f,  0.0f);
				}
			}
		}
	}

	/*
	 * Handles a Player collision with a GuardTower
	 * 
	 * @param player				The Player colliding with the GuardTower
	 * @param guardTower			The GuardTower colliding with the Player
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void handlePlayerTower(GameEntity player, GameEntity guardTower, boolean start)
	{
		if (start)
		{
			if (player.getType() == EntityType.CONVICT)
			{
				((Prisoner) player).collide((GuardTower)guardTower);
			}
		}
		else
		{
			if (player.getType() == EntityType.CONVICT)
			{
				((Prisoner) player).endCollide((GuardTower)guardTower);
			}
		}
	}
	
	/*
	 * Handles a SteelHorse collision with a Wall
	 * 
	 * @param horse					The SteelHorse colliding with the Wall
	 * @param wall					The Wall colliding with the SteelHorse
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void handleSteelHorseWall(GameEntity horse, GameEntity wall, boolean start)
	{
		if(!start)
		{
			((SteelHorse)horse).endCollide((Wall)wall);
		}
	}
	/*
	 * Handles a Player collision with a Bridge
	 * 
	 * @param player				The Player colliding with the Bridge
	 * @param bridge				The Bridge colliding with the Player
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void handlePlayerBridge(GameEntity player, GameEntity bridge, boolean start)
	{
		if (start)
		{
			((Player) player).addBridgeContacts((Bridge) bridge);
		}
		else
		{
			((Player) player).removeBridgeContact((Bridge) bridge);
		}
	}
	
	/*
	 * Handles a Prisoner collision with a Spotlight
	 * 
	 * @param player				The Prisoner colliding with the Spotlight
	 * @param spotlight				The Spotlight colliding with the Prisoner
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void handlePlayerSpotlight(GameEntity player, GameEntity spotlight, boolean start)
	{
		if (start)
		{
			((Prisoner) player).addLightContact((Spotlight) spotlight);
		}
		else
		{
			((Prisoner) player).removeLightContact((Spotlight) spotlight);
		}
	}
	
	/*
	 * Handles a CarrierCop collision with a ChainLink
	 * 
	 * @param cop					The CarrierCop colliding with the ChainLink
	 * @param chain					The ChainLink colliding with the CarrierCop
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void handleCarrierCopChain(GameEntity cop, GameEntity chain, boolean start)
	{
		if (start)
		{
			((CarrierCop) cop).addChainLinkContact((ChainLink) chain);
		}
		else
		{
			((CarrierCop) cop).removeChainLinkContact((ChainLink) chain);
		}
	}
	
	/*
	 * Handles a CarrierCop collision with a ChainLink
	 * 
	 * @param cop					The CarrierCop colliding with the ChainLink
	 * @param chain					The ChainLink colliding with the CarrierCop
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void handlePlayerCoin(Player player, Coin coin, boolean start)
	{
		if (start && !coin.getCollected() && coin.isCollectable())
		{
			coin.setCollected(true);
			CGCWorld.addToDestroyList(coin);
			player.pickupCoin();
		}
	}
	
	/*
	 * Test a Player entity against a Sensor entity
	 * 
	 * @param geA					The first entity
	 * @param geB					The second entity
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void testPlayerCoin(GameEntity geA, GameEntity geB, boolean start)
	{
		EntityType a = geA.getType();
		EntityType b = geB.getType();
		if(a == EntityType.COIN || b == EntityType.COIN)
		{
			if((b == EntityType.CONVICT || b == EntityType.COP) && a == EntityType.COIN)
			{
				handlePlayerCoin((Player)geB,(Coin)geA,start);
			}
			else if((a == EntityType.CONVICT || a == EntityType.COP) && b == EntityType.COIN)
			{
				handlePlayerCoin((Player)geA,(Coin)geB,start);
			}
		}
	}
	
	/*
	 * Test a Player entity against a Sensor entity
	 * 
	 * @param geA					The first entity
	 * @param geB					The second entity
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void testPlayerSensor(GameEntity geA, GameEntity geB, boolean start)
	{
		if(geA.getType() == EntityType.CONVICT || geB.getType() == EntityType.CONVICT)
		{
			if(geB.getType() == EntityType.SENSOR && geA instanceof Prisoner && ((Prisoner)geA).isAlive())
			{
				handlePlayerSensor(geA,geB,start);
			}
			else if(geA.getType() == EntityType.SENSOR && geB instanceof Prisoner && ((Prisoner)geB).isAlive())
			{
				handlePlayerSensor(geB,geA,start);
			}
		}
	}
	
	/*
	 * Test a Player entity against a Mud entity
	 * 
	 * @param geA					The first entity
	 * @param geB					The second entity
	 * @param fA					The fixture for the first entity
	 * @param fB					The fixture for the second entity
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void testPlayerMud(GameEntity geA, GameEntity geB, Fixture fA, Fixture fB, boolean start)
	{
		if (geA instanceof Player || geB instanceof Player)
		{
			if (geA instanceof Player && fA.getDensity() == BodyFactory.HIT_BOX_DEN || 
					geB instanceof Player && fB.getDensity() == BodyFactory.HIT_BOX_DEN)
			{
				if (geB.getType() == EntityType.MUD)
				{
					handlePlayerTerrain(geA, geB, start);
				}
				else if (geA.getType() == EntityType.MUD)
				{
					handlePlayerTerrain(geB, geA, start);
				}
			}
		}
	}
	
	/*
	 * Test a Player entity against a Water entity
	 * 
	 * @param geA					The first entity
	 * @param geB					The second entity
	 * @param fA					The fixture for the first entity
	 * @param fB					The fixture for the second entity
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void testPlayerWater(GameEntity geA, GameEntity geB, Fixture fA, Fixture fB, boolean start)
	{
		if (geA instanceof Player || geB instanceof Player)
		{
			if (geA instanceof Player && fA.getDensity() == BodyFactory.HIT_BOX_DEN || 
					geB instanceof Player && fB.getDensity() == BodyFactory.HIT_BOX_DEN)
			{
				if (geB.getType() == EntityType.WATER)
				{
					handlePlayerTerrain(geA, geB, start);
				}
				else if (geA.getType() == EntityType.WATER)
				{
					handlePlayerTerrain(geB, geA, start);
				}
			}
		}
	}
	
	/*
	 * Test a Player entity against a GuardTower entity
	 * 
	 * @param geA					The first entity
	 * @param geB					The second entity
	 * @param fA					The fixture for the first entity
	 * @param fB					The fixture for the second entity
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void testPlayerTower(GameEntity geA, GameEntity geB, Fixture fA, Fixture fB, boolean start)
	{
		if (geA instanceof Player || geB instanceof Player)
		{
			if (geA instanceof Player && fA.getDensity() == BodyFactory.HIT_BOX_DEN || 
					geB instanceof Player && fB.getDensity() == BodyFactory.HIT_BOX_DEN)
			{
				if (geB.getType() == EntityType.TOWER)
				{
					handlePlayerTower(geA, geB, start);
				}
				else if (geA.getType() == EntityType.TOWER)
				{
					handlePlayerTower(geB, geA, start);
				}
			}
		}
	}
	
	/*
	 * Test a Player entity against a Bridge entity
	 * 
	 * @param geA					The first entity
	 * @param geB					The second entity
	 * @param fA					The fixture for the first entity
	 * @param fB					The fixture for the second entity
	 * @param start					Whether or not the entities are about to start or stop colliding
	 */
	public void testPlayerBridge(GameEntity geA, GameEntity geB, Fixture fA, Fixture fB, boolean start)
	{
		if (geA instanceof Player || geB instanceof Player)
		{
			if (geA instanceof Player && fA.getDensity() == BodyFactory.BRIDGE_BOX_DEN || 
					geB instanceof Player && fB.getDensity() == BodyFactory.BRIDGE_BOX_DEN)
			{
				if (geB.getType() == EntityType.BRIDGE)
				{
					handlePlayerBridge(geA, geB, start);
				}
				else if (geA.getType() == EntityType.BRIDGE)
				{
					handlePlayerBridge(geB, geA, start);
				}
			}
		}
	}
	
	/*
	 * Test a Player entity against a Spotlight entity
	 * 
	 * @param geA					The first entity
	 * @param geB					The second entity
	 * @param fA					The fixture for the first entity
	 * @param fB					The fixture for the second entity
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void testPlayerSpotlight(GameEntity geA, GameEntity geB, Fixture fA, Fixture fB, boolean start)
	{
		if (geA instanceof Player && fA.getDensity() == BodyFactory.CIRCLE_DEN || 
				geB instanceof Player && fB.getDensity() == BodyFactory.CIRCLE_DEN)
		{
			if (geB instanceof Spotlight && geA instanceof Prisoner && ((Prisoner)geA).isAlive())
			{
				handlePlayerSpotlight(geA, geB, start);
			}
			else if (geA instanceof Spotlight && geB instanceof Prisoner && ((Prisoner)geB).isAlive())
			{
				handlePlayerSpotlight(geB, geA, start);
			}
		}
	}
	
	/*
	 * Test a CarrierCop entity against a ChainLink entity
	 * 
	 * @param geA					The first entity
	 * @param geB					The second entity
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void testCarrierCopChainLink(GameEntity geA, GameEntity geB, boolean start)
	{
		if (geA instanceof CarrierCop || geB instanceof CarrierCop)
		{
			if (geB instanceof CarrierCop && geA instanceof ChainLink && ((CarrierCop) geB).isAlive())
			{
				this.handleCarrierCopChain(geB, geA, start);
			}
			else if (geA instanceof CarrierCop && geB instanceof ChainLink && ((CarrierCop)geA).isAlive())
			{
				handlePlayerSpotlight(geB, geA, start);
			}
		}
	}

	/*
	 * Starts when a collision is no longer possible
	 * 
	 * @param contact				The AABB where contact may be ending
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#endContact(com.badlogic.gdx.physics.box2d.Contact)
	 */
	public void endContact(Contact contact) 
	{
		if (contact == null || contact.getFixtureA() == null || contact.getFixtureB() == null ||
				contact.getFixtureA().getBody() == null || contact.getFixtureB().getBody() == null ||
				(GameEntity)contact.getFixtureA().getBody().getUserData() == null || 
				(GameEntity)contact.getFixtureB().getBody().getUserData() == null)
		{
			return;
		}
		
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();
		GameEntity geA = (GameEntity)a.getUserData();
		GameEntity geB = (GameEntity)b.getUserData();
		
		testPlayerCoin(geA, geB, false);
		testPlayerSensor(geA, geB, false);
		testPlayerMud(geA, geB, fA, fB, false);
		testPlayerWater(geA, geB, fA, fB, false);
		testPlayerTower(geA, geB, fA, fB, false);
		
		testPlayerBridge(geA, geB, fA, fB, false);
		testPlayerSpotlight(geA, geB, fA, fB, false);
		testCarrierCopChainLink(geA, geB, false);
		
		testSteelHorseWall(geA, geB, false);
	}

	/*
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#preSolve(com.badlogic.gdx.physics.box2d.Contact, com.badlogic.gdx.physics.box2d.Manifold)
	 */
	public void preSolve(Contact contact, Manifold oldManifold) 
	{
		Object goA = contact.getFixtureA().getBody().getUserData();
		Object goB = contact.getFixtureB().getBody().getUserData();

		Player p = null;
		PlayerWall pw = null;
		if(goA instanceof Player && goB instanceof PlayerWall)
		{
			p = (Player)goA;
			pw = (PlayerWall)goB;
		}
		else if(goA instanceof PlayerWall && goB instanceof Player)
		{
			p = (Player)goB;
			pw = (PlayerWall)goA;
		}
		
		if(pw != null && p != null)
		{
			if((pw.isTop() && p.getBody().getWorldCenter().cpy().y >= pw.getBody().getWorldCenter().y) || (!pw.isTop() && p.getBody().getWorldCenter().cpy().y <= pw.getBody().getWorldCenter().y))
			{
				contact.setEnabled(false);
			}
			return;
		}	
	}

	/*
	 * Test a SteelHorse entity against a Wall entity
	 * 
	 * @param geA					The first entity
	 * @param geB					The second entity
	 * @param start					Whether the entities are about to start or stop colliding
	 */
	public void testSteelHorseWall(GameEntity geA, GameEntity geB, boolean start)
	{
		
			if (geA instanceof SteelHorse || geB instanceof SteelHorse)
			{
				if (geB instanceof Wall)
				{
					handleSteelHorseWall(geA, geB, start);
				}
				else if (geA instanceof Wall)
				{
					handleSteelHorseWall(geB, geA, start);
				}
			}
	}
		
	/*
	 * @see com.badlogic.gdx.physics.box2d.ContactListener#postSolve(com.badlogic.gdx.physics.box2d.Contact, com.badlogic.gdx.physics.box2d.ContactImpulse)
	 */
	public void postSolve(Contact contact, ContactImpulse impulse) 
	{
		
	}
} // End class
