/*
 * @(#)PallBearerBuilder.java		0.2 14/2/27
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc.boss;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.percipient24.cgc.art.TextureAnimationDrawer;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.GuardTower;
import com.percipient24.cgc.entities.Wall;
import com.percipient24.cgc.entities.boss.Boss;
import com.percipient24.cgc.entities.boss.PallBearer;
import com.percipient24.enums.BossType;
import com.percipient24.enums.EntityType;

/*
 * Creates and stores a PallBearer boss level
 * 
 * @version 0.2 14/2/27
 * @author Christopher Rider
 */
public class PallBearerBuilder extends BossBuilder 
{
	/*
	 * Creates a new PallBearerBuilder object
	 * 
	 * @param type					The type of boss for this builder
	 */
	public PallBearerBuilder(BossType type)
	{
		super(type);
		
		buildBossArea();
	}
	
	/*
	 * @see com.percipient24.cgc.boss.BossBuilder#buildBossArea()
	 */
	protected void buildBossArea() 
	{
		levelLength = 3;
		Body wall;
		GameEntity ge;
		int towers = 0;
		
		//Make the bottom wall.
		wall = CGCWorld.getBF().createRectangle(9.5f, 0, 20, 1, BodyType.StaticBody, 
				BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
		ge = new Wall(TextureAnimationDrawer.hwallAnim, null,
				TextureAnimationDrawer.hwallAnim, EntityType.WALL,
				wall, false);
		wall.setUserData(ge);

		ge.addToWorldLayers(CGCWorld.getLH());
		
		for (int i = 0; i < levelLength; i++)
		{
			CGCWorld.getLH().addNewChunk(false);
			
			wall = CGCWorld.getBF().createRectangle(0, 5+(10+3f/5f)*i, 1, 11, BodyType.StaticBody, 
					BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
			ge = new Wall(TextureAnimationDrawer.vwallAnims[0], null,
					TextureAnimationDrawer.vwallAnims[0], EntityType.WALL,
					wall, true);
			wall.setUserData(ge);

			ge.addToWorldLayers(CGCWorld.getLH());
			
			wall = CGCWorld.getBF().createRectangle(19, 5+(10+3f/5f)*i, 1, 11, BodyType.StaticBody, 
					BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
			ge = new Wall(TextureAnimationDrawer.vwallAnims[0], null,
					TextureAnimationDrawer.vwallAnims[0], EntityType.WALL,
					wall, true);
			wall.setUserData(ge);
			ge.addToWorldLayers(CGCWorld.getLH());
		}
		
		while ((towers == 0 || CGCWorld.getRandom().nextInt(100) < 30) && towers <= 10)
		{
			int x = CGCWorld.getRandom().nextInt(17)+1;
			int y = CGCWorld.getRandom().nextInt(levelLength*11);
			Body tower = CGCWorld.getBF().createRectangle(x, y, 1, 1, 
					BodyType.StaticBody, BodyFactory.CAT_TERRAIN,
					BodyFactory.MASK_TERRAIN);
			ge = new GuardTower(TextureAnimationDrawer.towerAnims[0], null, TextureAnimationDrawer.towerAnims[1],
					EntityType.TOWER, tower, x, y);
			tower.setUserData(ge);
			tower.getFixtureList().get(0).setSensor(true);
			ge.addToWorldLayers(CGCWorld.getLH());
			towers++;
		}
		
		//Make the top wall.
		wall = CGCWorld.getBF().createRectangle(9.5f, 32.1f, 20, 1, BodyType.StaticBody, 
				BodyFactory.CAT_WALL, BodyFactory.MASK_WALL);
		ge = new Wall(TextureAnimationDrawer.hwallAnim, null,
				TextureAnimationDrawer.hwallAnim, EntityType.WALL,
				wall, false);
		wall.setUserData(ge);

		ge.addToWorldLayers(CGCWorld.getLH());
	}

	/*
	 * @see com.percipient24.cgc.boss.BossBuilder#createBoss()
	 */
	public Boss createBoss() 
	{
		Boss boss = null;
		Body bossBody;
		
		bossBody = CGCWorld.getBF().createRectangle(9.5f, 4.5f, 0.75f, 1.03f, BodyType.DynamicBody, 
				BodyFactory.CAT_PALL_BEARER, BodyFactory.MASK_PALL_BEARER);
		boss = new PallBearer(TextureAnimationDrawer.pallBearerAnim, TextureAnimationDrawer.pallBearerAnim, TextureAnimationDrawer.pallBearerAnim, EntityType.PALL_BEARER, bossBody);
		bossBody.setUserData(boss);
		bossBody.setFixedRotation(false);
		bossBody.setLinearDamping(0.1f);
		boss.addToWorldLayers(CGCWorld.getLH());
		
		return boss;
	}

}
