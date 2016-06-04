/*
 * @(#)TrainRushBuilder.java		0.2 14/2/27
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.boss;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.percipient24.helpers.BodyFactory;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.Track;
import com.percipient24.cgc.entities.Wall;
import com.percipient24.cgc.entities.boss.Boss;
import com.percipient24.cgc.entities.boss.Conductor;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.enums.BossType;
import com.percipient24.enums.EntityType;

/*
 * Builder for the Train Rush map
 * 
 * @version 0.2 14/2/27
 * @author Clayton Andrews
 */
public class TrainRushBuilder extends BossBuilder
{
	Body trainAnchors[];
	Track trackList[];
	Conductor conductor;
	Array<Player> players;
	
	/*
	 * Creates a new TrainRushBuilder object
	 * 
	 * @param type					The type of boss for this builder
	 */
	public TrainRushBuilder(BossType type, Array<Player> players)
	{
		super(type);
		this.players = players;
		buildBossArea();
	}

	/*
	 * @see com.percipient24.cgc.boss.BossBuilder#buildBossArea()
	 */
	protected void buildBossArea() 
	{
		levelLength = 1;
		Body wall;
		GameEntity ge;
		
		CGCWorld.getLH().addNewChunk(false);
		
		//generate walls to bound the map
		wall = CGCWorld.getBF().createRectangle(0, 5, 1, 11, BodyType.StaticBody, BodyFactory.CAT_WALL,
												BodyFactory.MASK_WALL);
		ge = new Wall(AnimationManager.vwallAnims[0], AnimationManager.vwallAnims[0],
						AnimationManager.vwallAnims[0], EntityType.WALL, wall, true);
		wall.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		wall = CGCWorld.getBF().createRectangle(19, 5, 1, 11, BodyType.StaticBody, BodyFactory.CAT_WALL,
												BodyFactory.MASK_WALL);
		ge = new Wall(AnimationManager.vwallAnims[0], AnimationManager.vwallAnims[0],
						AnimationManager.vwallAnims[0], EntityType.WALL, wall, true);
		wall.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		wall = CGCWorld.getBF().createRectangle(9.5f, 0, 20, 1, BodyType.StaticBody, BodyFactory.CAT_WALL,
												BodyFactory.MASK_WALL);
		ge = new Wall(AnimationManager.hwallAnim, AnimationManager.hwallAnim,
						AnimationManager.hwallAnim, EntityType.WALL, wall, false);
		wall.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		wall = CGCWorld.getBF().createRectangle(9.5f, 11, 20, 1, BodyType.StaticBody, BodyFactory.CAT_WALL,
												BodyFactory.MASK_WALL);
		ge = new Wall(AnimationManager.hwallAnim, AnimationManager.hwallAnim,
						AnimationManager.hwallAnim, EntityType.WALL, wall, false);
		wall.setUserData(ge);
		ge.addToWorldLayers(CGCWorld.getLH());
		
		//initialize trainAnchor list and trackList to store the track data
		//for use in Conductor; currently only supports horizontal anchors
		trainAnchors = new Body[6];
		trackList = new Track[6];
		
		//generate horizontal tracks and anchors
		for (int i = 0; i < 6; i++)
		{
			Body track = CGCWorld.getBF().createRectangle(9.5f, 1.25f + (i * 1.7f), 20, 1, BodyType.StaticBody,
															BodyFactory.CAT_NON_INTERACTIVE,
															BodyFactory.CAT_NON_INTERACTIVE);
			ge = new Track(AnimationManager.trackAnim, null, null, EntityType.TRACK, track);
			track.setUserData(ge);
			ge.addToWorldLayers(CGCWorld.getLH());
			track.getFixtureList().get(0).setSensor(true);
			
			//add to reference list for use in Conductor
			trackList[i] = (Track)ge;
			
			//add the background anchor to attach trains to
			Body backgroundAnchor;
			backgroundAnchor =  CGCWorld.getBF().createRectangle(9.5f, 1.25f + (i * 1.7f), 20, 11,BodyType.StaticBody,
																BodyFactory.CAT_NON_INTERACTIVE,
																BodyFactory.MASK_NON_INTERACTIVE);
			backgroundAnchor.getFixtureList().get(0).setSensor(true);
			
			//add to reference list for use in Conductor
			trainAnchors[i] = backgroundAnchor;
		}
		
		conductor = new Conductor(trainAnchors, trackList, players);
	}

	/*
	 * @see com.percipient24.cgc.boss.BossBuilder#createBoss()
	 */
	public Boss createBoss()
	{
		return conductor;
	}
}
