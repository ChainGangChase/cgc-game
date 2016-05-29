/*
 * @(#)Patcher.java		0.1 13/5/24
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.b2dhelpers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.percipient24.cgc.entities.ChainLink;
import com.percipient24.cgc.entities.players.Player;

/*
 * Passes references between players chains and wheels
 * 
 * @version 0.1 13/5/24
 * @author Joe Pietruch
 */
public class Patcher 
{
	/*
	 * Creates a new Patcher
	 */
	public Patcher()
	{
	}
	
	/*
	 * Tells chain link what the related player is
	 * 
	 * @param link					The chain link to inform
	 * @param leftPlayer			The related player
	 */
	public void setupChain(Body link, Player leftPlayer)
	{
		ChainLink cl = (ChainLink) link.getUserData();
		cl.sLeft(leftPlayer);
	}
	
	/*
	 * Tells players who their neighbor is and what chain center to use
	 * 
	 * @param left					The player on the left
	 * @param right					The player on the right
	 * @param center				The center of the chain linking the players
	 */
	public void leftRightCenter(Player left, Player right, Joint center)
	{
		left.setRightPlayer(right);
		left.setRightJoint(center);
		right.setLeftPlayer(left);
		right.setLeftJoint(center);
	}
} // End class