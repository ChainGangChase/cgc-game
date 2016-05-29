/*
 * @(#)BodyFactory.java		0.2 14/2/4
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.b2dhelpers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.AnimationManager;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.GameEntity;
import com.percipient24.cgc.entities.TrainCar;
import com.percipient24.cgc.entities.Wheel;
import com.percipient24.enums.EntityType;

/*
 * Creates world elements
 * 
 * @version 0.2 14/2/4
 * @author Joe Pietruch
 * @author Christopher Rider
 * @author Clayton Andrews
 */
public class BodyFactory 
{
	// Basic Collision categories
	private static final short CAT_NONE = 0x0000; // Will not collide with anything
	private static final short CAT_1 = 0x0001;
	private static final short CAT_2 = 0x0002;
	private static final short CAT_3 = 0x0004;
	private static final short CAT_4 = 0x0008;
	private static final short CAT_5 = 0x0010;
	private static final short CAT_6 = 0x0020;
	private static final short CAT_7 = 0x0040;
	private static final short CAT_8 = 0x0080;
	private static final short CAT_9 = 0x0100;
	private static final short CAT_10 = 0x0200;
	private static final short CAT_11 = 0x0400;
	private static final short CAT_12 = 0x0800;
	private static final short CAT_13 = 0x1000; 
	private static final short CAT_14 = 0x2000; 
	private static final short CAT_15 = 0x4000;
	
	// Compound Collision categories
	public static final short CAT_PRISONER = CAT_1 | CAT_12;
	public static final short CAT_COP = CAT_1;
	public static final short CAT_DECEASED = CAT_11;
	public static final short CAT_CARRIER_COP = CAT_1 | CAT_9;
	public static final short CAT_CHAIN = CAT_10;
	public static final short CAT_FENCE = CAT_2; // Also used for gates
	public static final short CAT_INTERACTABLE = CAT_7; // Used for Towers, Sensors, Spotlights, and Bullets
	public static final short CAT_BOSS = CAT_4; // Used for Jeeps and Tanks
	public static final short CAT_IMPASSABLE = CAT_5; // Used for Player Walls and Sheriff-Ground
	public static final short CAT_TETRAIN = CAT_5 | CAT_6; // Used for Train, Mud, Water, and Bridges
	public static final short CAT_TREE = CAT_3;
	public static final short CAT_WALL = CAT_5 | CAT_6 | CAT_13;
	public static final short CAT_WHEEL = CAT_9;
	public static final short CAT_EXPLOSIVE = CAT_7 | CAT_8;
	public static final short CAT_NON_INTERACTIVE = CAT_NONE; // Used for Helicopters and other non-collision entities
	public static final short CAT_STEEL_HORSE = CAT_4 | CAT_14;
	public static final short CAT_PALL_BEARER = CAT_14;
	public static final short CAT_SIDE_CAR = CAT_4 | CAT_14;
	public static final short CAT_TIED_PRISONER = CAT_5 | CAT_4 | CAT_12 | CAT_15;

	// Collision masks
	public static final short MASK_COP = CAT_1 | CAT_2 | CAT_5 | CAT_3;
	public static final short MASK_PRISONER = MASK_COP | CAT_7 | CAT_4;
	public static final short MASK_DECEASED = CAT_11 | CAT_2 | CAT_6 | CAT_3;
	public static final short MASK_CARRIER_COP = MASK_COP | CAT_10;
	public static final short MASK_CHAIN = CAT_9 | CAT_4 | CAT_3 | CAT_13;
	public static final short MASK_FENCE = CAT_1 | CAT_11 | CAT_4 | CAT_8; // Also used for gates
	public static final short MASK_INTERACTABLE = CAT_12; // Used for Towers, Sensors, Spotlights, and Bullets
	public static final short MASK_BOSS = CAT_12 | CAT_10 | CAT_2 | CAT_3; // Used for Jeeps, Tanks, and Steel Horse
	public static final short MASK_PLAYER_WALL = CAT_1;
	public static final short MASK_SHERIFF_GROUND = MASK_PLAYER_WALL | CAT_15;
	public static final short MASK_TETRAIN = CAT_1 | CAT_11 | CAT_15; // Used for Train, Mud, Water, and Bridges
	public static final short MASK_TREE = CAT_1 | CAT_11 | CAT_10 | CAT_4 | CAT_8;
	public static final short MASK_WALL = CAT_1 | CAT_11 | CAT_10 | CAT_14 | CAT_15;
	public static final short MASK_WHEEL = CAT_10;
	public static final short MASK_EXPLOSIVE = CAT_12 | CAT_2 | CAT_3;
	public static final short MASK_NON_INTERACTIVE = CAT_NONE; // Used for Helicopters and other non-collision entities
	public static final short MASK_STEEL_HORSE = CAT_12 | CAT_10 | CAT_2 | CAT_3 | CAT_13;
	public static final short MASK_PALL_BEARER = CAT_13;
	public static final short MASK_SIDE_CAR = CAT_10 | CAT_2 | CAT_3 | CAT_13 | CAT_14;
	public static final short MASK_TIED_PRISONER = CAT_1 | CAT_2 | CAT_7 | CAT_5 | CAT_4 | CAT_3;
	public static final short MASK_BOSS_COP = CAT_1 | CAT_2 | CAT_7 | CAT_5 | CAT_3;
	
	public static final float HIT_BOX_DEN = 0.001f;
	public static final float BRIDGE_BOX_DEN = 0.002f;
	public static final float CIRCLE_DEN = 0.5f;
	
	private World world;
	
	private Array<Joint> cars = new Array<Joint>();
	private Array<TrainCar> trains = new Array<TrainCar>();
	
	private static BodyFactory _instance;

	
	/*
	 * Creates a BodyFactory object using a World object
	 * 
	 * @param w						The world to generate
	 */
	public BodyFactory(World w)
	{
		if(_instance == null)
		{
			_instance = this;
		}
		else
		{
			//Gdx.app.log("Error", "You've already made a BodyFactory.");
		}
		world = w;
	}
	
	/*
	 * Gets an instance of this BodyFactory
	 * 
	 * @return 						An instance of this BodyFactory
	 */
	public static BodyFactory getInstance() 
	{ 
		return _instance; 
	}

	/*
	 * Creates a sensor object
	 * 
	 * @param x 					The X-position of the sensor
	 * @param y						The Y-position of the sensor
	 * @param w						The width of the sensor
	 * @param h						The height of the sensor
	 * @param t						The type of object this entity is
	 * @param cat					The category of this entity
	 * @param mask					Bitmask for collision
	 * @return						The sensor entity created
	 */
	public Body createSensor(float x, float y, float w, float h, 
			BodyType t, short cat, short mask)
	{
		Body b = createRectangle(x, y, w, h, t, cat, mask);
		b.getFixtureList().get(0).setSensor(true);
		return b;
	}

	/*
	 * Creates a rectangle
	 * 
	 * @param x 					The X-position of the rectangle
	 * @param y						The Y-position of the rectangle
	 * @param w						The width of the rectangle
	 * @param h						The height of the rectangle
	 * @param t						The type of object this entity is
	 * @param cat					The category of this entity
	 * @param mask					Bitmask for collision
	 * @return						The rectangle entity created
	 */
	public Body createRectangle(float x, float y, float w, float h,
			BodyType t, short cat, short mask)
	{
		BodyDef def;
		Body body;
		PolygonShape shape;
		FixtureDef fd;
				
		def = new BodyDef();
		def.type = t;
		def.position.set(x, y);
		
		body = world.createBody(def);
		
		shape = new PolygonShape();
		shape.setAsBox(w/2.0f, h/2.0f);
		
		fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 1.0f;
		fd.friction = 0.9f;
		fd.restitution = 0.6f;
		
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = mask;
		
		body.createFixture(fd);
		
		body.setLinearDamping(2.0f);
		
		shape.dispose();
		
		return body;
	}
	
	public Body createOrientedRectangle(float x, float y, float w, float h,
			float centerX, float centerY, BodyType t, short cat, short mask)
	{
		BodyDef def;
		Body body;
		PolygonShape shape;
		FixtureDef fd;
				
		def = new BodyDef();
		def.type = t;
		def.position.set(x, y);
		
		body = world.createBody(def);
		
		shape = new PolygonShape();
		shape.setAsBox(w/2.0f, h/2.0f, new Vector2(centerX, centerY), 0);
		
		fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 1.0f;
		fd.friction = 0.9f;
		fd.restitution = 0.6f;
		
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = mask;
		
		body.createFixture(fd);
		
		body.setLinearDamping(2.0f);
		
		shape.dispose();
		
		return body;
	}
	
	/*
	 * Creates a rectangle
	 * 
	 * @param x 					The X-position of the rectangle
	 * @param y						The Y-position of the rectangle
	 * @param w						The width of the rectangle
	 * @param h						The height of the rectangle
	 * @param t						The type of object this entity is
	 * @param cat					The category of this entity
	 * @param mask					Bitmask for collision
	 * @param density				The density of this entity
	 * @return						The rectangle entity created
	 */
	public Body createRectangle(float x, float y, float w, float h,
			BodyType t, short cat, short mask, float density)
	{
		BodyDef def;
		Body body;
		PolygonShape shape;
		FixtureDef fd;
				
		def = new BodyDef();
		def.type = t;
		def.position.set(x, y);
		
		body = world.createBody(def);
		
		shape = new PolygonShape();
		shape.setAsBox(w/2.0f, h/2.0f);
		
		fd = new FixtureDef();
		fd.shape = shape;
		fd.density = density;
		fd.friction = 0.9f;
		fd.restitution = 0.6f;
		
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = mask;
		
		body.createFixture(fd);
		
		body.resetMassData();
		
		body.setLinearDamping(2.0f);
		
		shape.dispose();
		
		return body;
	}

	/*
	 * Creates a circle
	 * 
	 * @param x 					The X-position of the circle
	 * @param y						The Y-position of the circle
	 * @param w						The width of the circle
	 * @param h						The height of the circle
	 * @param t						The type of object this entity is
	 * @param cat					The category of this entity
	 * @param mask					Bitmask for collision
	 * @return						The circle entity created
	 */
	public Body createCircle(float x, float y, float d, BodyType t, 
			short cat, short mask)
	{
		BodyDef def;
		Body body;
		CircleShape shape;
		FixtureDef fd;
		
		def = new BodyDef();
		def.type = t;
		def.position.set(x, y);
		
		body = world.createBody(def);
		
		shape = new CircleShape();
		shape.setRadius(d/2f);
		
		fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 0.5f;
		fd.friction = 0.8f;
		fd.restitution = 0.6f;
		
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = mask;
		
		body.createFixture(fd);
		
		shape.dispose();
		
		return body;
	}
	
	/*
	 * Creates a body for a LIVING player
	 * 
	 * @param x						The X-position of the player
	 * @param y						The Y-position of the player
	 * @param d						The radius of the player
	 * @param t						The BodyType of the player
	 * @param cat					The collision category of the player
	 * @param mask					The collision mask of the player
	 */
	public Body createPlayerBody(float x, float y, float d, BodyType t,
			short cat, short mask)
	{
		BodyDef def;
		Body body;
		CircleShape shape;
		FixtureDef fd;
		
		def = new BodyDef();
		def.type = t;
		def.position.set(x, y);
		
		body = world.createBody(def);
		
		shape = new CircleShape();
		shape.setRadius(d/2f);
		
		fd = new FixtureDef();
		fd.shape = shape;
		fd.density = CIRCLE_DEN;
		fd.friction = 0.8f;
		fd.restitution = 0.6f;
		
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = mask;
		//fd.filter.groupIndex = 1;
		
		body.createFixture(fd);
		
		shape.dispose();
		
		shape = new CircleShape();
		shape.setRadius(d/8.5f);
		
		fd = new FixtureDef();
		fd.shape = shape;
		fd.density = HIT_BOX_DEN;
		fd.friction = 0.001f;
		fd.restitution = 0.001f;
		
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = mask;
		
		body.createFixture(fd);
		
		shape.dispose();
		
		shape = new CircleShape();
		shape.setRadius(d/3.5f);
		
		fd.shape = shape;
		fd.density = BRIDGE_BOX_DEN;
		fd.friction = 0.001f;
		fd.restitution = 0.001f;
		
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = mask;
		
		body.createFixture(fd);
		
		shape.dispose();
		
		return body;
	}

	/*
	 * Creates a box2d revolute joint between two entities
	 * 
	 * @param b1					One of the entities to connect
	 * @param b2					The other entity to connect
	 * @param a1					Anchor position for b1 relative to b1 position
	 * @param a2					Anchor position for b2 relative to b2 position
	 */
	public void createRevoluteJoint(Body b1, Body b2, Vector2 a1, Vector2 a2)
	{
		RevoluteJointDef rjd = new RevoluteJointDef();
		rjd.localAnchorA.set(a1);
		rjd.localAnchorB.set(a2);
		rjd.bodyA = b1;
		rjd.bodyB = b2;
		world.createJoint(rjd);
	}

	/*
	 * Creates a box2d rope joint between two entities
	 * 
	 * @param b1					One of the entities to connect
	 * @param b2					The other entity to connect
	 * @param maxLength				The maximum length of the rope
	 * @return						The created rope joint
	 */
	public Joint createRopeJoint(Body b1, Body b2, float maxLength)
	{
		RopeJointDef rjd = new RopeJointDef();
		rjd.bodyA = b1;
		rjd.bodyB = b2;
		rjd.collideConnected = true;
		rjd.localAnchorA.set(Vector2.Zero);
		rjd.localAnchorB.set(Vector2.Zero);
		rjd.maxLength = maxLength;
		return world.createJoint(rjd);
	}

	/*
	 * Creates a box2d weld joint between two entities
	 * 
	 * @param b1					One of the entities to connect
	 * @param b2					The other entity to connect
	 * @param anchor				Position to anchor the entities to
	 * @return						The created weld joint
	 */
	public Joint createWeldJoint(Body b1, Body b2, Vector2 anchor)
	{
		WeldJointDef wjd = new WeldJointDef();
		
		wjd.bodyA = b1;
		wjd.bodyB = b2;
		wjd.collideConnected = false;
		wjd.localAnchorA.set(Vector2.Zero);
		wjd.localAnchorB.set(anchor);
		wjd.referenceAngle = 0.0f;
		
		return world.createJoint(wjd);
	}

	/*
	 * Creates a box2d prismatic joint
	 * 
	 * @param anchor				The first attached entity
	 * @param car					The second attached entity
	 * @param start					Anchor point relative to anchor entity
	 * @return						The created prismatic joint
	 */
	private Joint createPrismaticJoint(Body anchor, Body car, Vector2 start) 
	{
		PrismaticJointDef pjd = new PrismaticJointDef();
		
		pjd.bodyA = anchor;
		pjd.bodyB = car;
		pjd.collideConnected = false;
		pjd.enableLimit = true;
	
		pjd.localAnchorA.set(start);
		pjd.localAnchorB.set(Vector2.Zero);
		
		pjd.localAxisA.set(-1,0);
		pjd.lowerTranslation = -150;
		pjd.upperTranslation = 150;
		
		pjd.maxMotorForce = 0;
		pjd.motorSpeed = 0;
		pjd.enableMotor = false;
		
		return world.createJoint(pjd);
	}
	
	/*
	 * Creates a train entity
	 * 
	 * @param anchor				What to anchor the train to with a prismatic joint
	 * @param startX				The starting X-position of the train
	 * @param startY				The starting Y-position of the train
	 * @param endCar				Whether or not this is the a car for the last train
	 * @param left					Whether the train will be going left to right (true) or right to left (false)
	 */
	public void createCar(Body anchor, float startX, float startY, boolean endCar, boolean left)
	{
		Body car = createRectangle(startX, startY, 5, 1.5f, BodyType.DynamicBody, 
				CAT_TETRAIN, MASK_TETRAIN);
		GameEntity ge = new TrainCar(AnimationManager.trainAnims[0], 
				AnimationManager.trainAnims[1], AnimationManager.trainAnims[2], 
				EntityType.TRAIN, car, left);
		car.setUserData(ge);
		
		if(!left)
		{
			car.setTransform(car.getPosition(), (float) Math.PI);
		}
		Body wheel1 = createRectangle(startX - 1.75f, startY - 0.5f, 1, 0.125f, 
				BodyType.DynamicBody, CAT_WHEEL, MASK_WHEEL);
		Body wheel2 = createRectangle(startX - 1.75f, startY + 0.5f, 1, 0.125f, 
				BodyType.DynamicBody, CAT_WHEEL, MASK_WHEEL);
		Body wheel3 = createRectangle(startX + 1.75f, startY - 0.5f, 1, 0.125f, 
				BodyType.DynamicBody, CAT_WHEEL, MASK_WHEEL);
		Body wheel4 = createRectangle(startX + 1.75f, startY + 0.5f, 1, 0.125f, 
				BodyType.DynamicBody, CAT_WHEEL, MASK_WHEEL);
		
		ge.addToWorldLayers(CGCWorld.getLH());
		trains.add((TrainCar) ge);
		
		ge = new Wheel(null, null, null, EntityType.WHEEL, wheel1);
		wheel1.setUserData(ge);
		ge = new Wheel(null, null, null, EntityType.WHEEL, wheel2);
		wheel2.setUserData(ge);
		ge = new Wheel(null, null, null, EntityType.WHEEL, wheel3);
		wheel3.setUserData(ge);
		ge = new Wheel(null, null, null, EntityType.WHEEL, wheel4);
		wheel4.setUserData(ge);
		
		createWeldJoint(car, wheel1, new Vector2(-1.75f, 0.5f));
		createWeldJoint(car, wheel2, new Vector2(-1.75f, -0.5f));
		createWeldJoint(car, wheel3, new Vector2(1.75f, 0.5f));
		createWeldJoint(car, wheel4, new Vector2(1.75f, -0.5f));

		/*if (endCar)
		{
			if(left)
			{
				cars.add(createPrismaticJoint(anchor, car, new Vector2(startX, -5)));
			}
		}
		else
		{*/
		cars.add(createPrismaticJoint(anchor, car, new Vector2(startX, 0)));
		//}
	}

	/*
	 * Starts moving the train across the level
	 * 
	 * @param curMap				The array-index of the current map the players are on
	 */
	public void summonTrain(int curMap) 
	{
		int dir = 150;
		TrainCar te = getCurrentTrain(curMap).get(0);
		
		if (!te.directionReversed())
		{
			dir = -150;			
		}
	
		int shinkansen = 2500;
		
		for(int i = 0; i < 3; i++)
		{
			PrismaticJoint pj = ((PrismaticJoint)cars.get(curMap*3+i));
			
			if (CGCWorld.getRandom().nextInt(10000) < 3)
			{
				pj.setMaxMotorForce(shinkansen);
			}
			else
			{
				pj.setMaxMotorForce(450);
			}
			pj.setMotorSpeed(dir);
			pj.enableMotor(true);
		}
	}
	
	/*
	 * Gets the train for the specified map
	 * 
	 * @param curMap				The map to get the train for
	 * @return						The train for the map
	 */
	public Array<TrainCar> getCurrentTrain(int curMap)
	{
		if (curMap*3 >= trains.size)
		{
			return null;
		}
		
		Array<TrainCar> train = new Array<TrainCar>();
		for (int i = 0; i < 3; i++)
		{
			train.add(trains.get(curMap*3+i));
		}
		return train;
	}
	
	/*
	 * Update every train car
	 */
	public void updateTrains()
	{
		for (int i = 0; i < trains.size; i++)
		{
			TrainCar currentCar = trains.get(i);
			
			currentCar.updateTrain();

			if(currentCar.isOffCamera())
			{
				currentCar.initCleanup();
			}
		}
	}
} // End class