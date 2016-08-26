/*
 * @(#)Conductor.java		0.2 14/2/27
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.entities.boss;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.percipient24.cgc.art.TextureAnimationDrawer;
import com.percipient24.helpers.LayerHandler;
import com.percipient24.cgc.CGCWorld;
import com.percipient24.cgc.entities.Track;
import com.percipient24.cgc.entities.players.Player;
import com.percipient24.cgc.entities.players.Prisoner;
import com.percipient24.cgc.screens.Options;
import com.percipient24.enums.EntityType;

/**
 * Handles the logic for the Train Rush map
 * 
 * @version 0.2 14/2/27
 * @author Clayton Andrews
 * @author Joe Pietruch
 */

public class Conductor extends Boss
{
		
	private Body[] trainAnchors;				//the anchor list used for spawning trains
	private Track[] tracks;						//the track list used to animate tracks that will have trains on them
    private Queue<Float> activeDelays;          //the queue of train delays; contains the delays to be assigned to each generated train
    
	//initializeConductor variables
    private float totalDelay = 0;				//accumulator variable used for ensuring that the amount of delays generated doesn't exceed timeTilVictory
    private int numSpawnsNeeded;				//the maximum number of trains needing to be generated for the map
    private float min_delay = 1.5f;				//the smallest gap between trains
    private float max_delay = 5.0f;				//the largest gap between trains
    // TODO : Set min_delay and max_delay based on difficulty settings
    
    //Update Loop variables
    private boolean isPaused = false;			//used to toggle the internal pause of the update loop
    private float timeTilVictory = 90;			//the time, in seconds, until the boss fight ends
    private float currentDelay = 0;				//used to count down the time until the next train is summoned
    
	private int trainIndex = 0;					//index of the current train to summon using CGCWorld.getBF().summonTrain(int)
    
	private boolean playersWarned = false;
	private boolean imminentDanger = false;
	private boolean trainsSummoned = false;
	
	private int trackDifficultyId = 0;
	private Array<Player> players;
	private int nextTrainCode = 1;
	
	// int[difficulty][trackfull][index] ---> trackCode
	private final int[][][] trackMaps = {
							/* easy */			{
								/*1*/  {1, 3, 5, 7, 9, 17, 33, 35},
								/*2*/  {2, 3, 6, 7, 10, 14, 18, 34, 35},
								/*4*/  {4, 5, 6, 7, 12, 14, 20, 36, 52},
								/*8*/  {8, 9, 10, 12, 14, 24, 40, 56},
								/*16*/ {16, 17, 18, 20, 24, 48, 52, 56},
								/*32*/ {32, 33, 34, 35, 36, 40, 48, 52, 56},
								/*L*/  {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16, 17, 18, 20, 24, 32, 33, 34, 35, 36, 40, 48, 52, 56}
							},
							/* medium */		{
								/*1*/  {11, 13, 15, 19, 25, 49, 51, 57},
								/*2*/  {11, 15, 19, 22, 26, 50, 51},
								/*4*/  {13, 15, 22, 28, 44},
								/*8*/  {11, 13, 15, 25, 26, 28, 44, 57},
								/*16*/ {19, 22, 25, 26, 28, 49, 50, 51, 57},
								/*32*/ {44, 49, 50, 51, 57},
								/*L*/  {11, 13, 15, 19, 22, 25, 26, 28, 44, 49, 50, 51, 57}
							},
							/* hard */			{
								/*1*/  {21, 23, 27, 29, 37, 39, 41, 45, 53},
								/*2*/  {23, 27, 30, 38, 39, 42, 54, 58},
								/*4*/  {21, 23, 29, 30, 37, 38, 39, 45, 53, 54, 60},
								/*8*/  {27, 29, 30, 41, 42, 45, 58, 60},
								/*16*/ {21, 23, 27, 29, 30, 53, 54, 58, 60},
								/*32*/ {37, 38, 39, 41, 42, 45, 53, 54, 58, 60},
								/*L*/  {21, 23, 27, 29, 30, 37, 38, 39, 41, 42, 45, 53, 54, 58, 60}
							},
							/* devilish */		{
								/*1*/  {31, 43, 47, 55, 59, 61},
								/*2*/  {31, 43, 46, 47, 55, 59, 62},
								/*4*/  {31, 46, 47, 55, 61, 62},
								/*8*/  {31, 46, 47, 55, 61, 62},
								/*16*/ {31, 55, 59, 61, 62},
								/*32*/ {43, 46, 47, 55, 59, 61, 62},
								/*L*/  {31, 43, 46, 47, 55, 59, 61, 62}
							}
						};

    /**
	 * Creates a new Conductor object
	 */
	public Conductor(Body[] trainAnchorList, Track[] tracksList, Array<Player> players) 
	{
		/*
		 * @param newLowAnimation		The Animation for the bottom of this object
		 * @param newMidAnimation		The Animation for the middle of this object
		 * @param newHighAnimation		The Animation for the top of this object
		 * @param pEntityType			The type of entity this object is
		 * @param attachedBody			The Body object that represents this GameEntity in the world
		 */
		super(null, null, null, EntityType.DEFAULT, null);
		this.players = players;

        trainAnchors = trainAnchorList;
        tracks = tracksList;

        this.setDifficultyMods();
        initializeConductor();
	}
	
	/**
	 * Initialize and queue the data needed for the Train Rush map
	 */
	private void initializeConductor()
    {
        activeDelays = new LinkedList<Float>();
        

        //set initial train delay so players don't get nailed by a train right away
        float randomDelay = MathUtils.random(2f, 4.5f);
        currentDelay = randomDelay; //initialize the first train delay for use in update()
        totalDelay += randomDelay;
        activeDelays.add(randomDelay);
        
        do
        {
        	// as we get closer to timeTilVictory
        	// the randomDelay gradually approaches minDelay
        	float delayPercentage = totalDelay / timeTilVictory;
            randomDelay =  this.max_delay - delayPercentage * (this.max_delay-this.min_delay);
            activeDelays.add(randomDelay);
            totalDelay += randomDelay;
        } while(totalDelay < timeTilVictory);
        
		turnOffAllTracks();
    }
	
	/**
	 * References Options.storedDifficultyOption to adjust boss difficulty.
	 */
	private void setDifficultyMods()
	{
		switch(Options.storedDifficultyOption)
		{
		case 0:
			// slow delay
			this.min_delay = 3.5f;
			this.max_delay = 5.0f;
			// easy tracks
			trackDifficultyId = 0;
			break;
		case 1:
			// slow delay
			this.min_delay = 3.5f;
			this.max_delay = 5.0f;
			// medium tracks
			trackDifficultyId = 1;
			break;
		case 2:
			// medium delay
			this.min_delay = 3.0f;
			this.max_delay = 4.5f;
			// medium tracks
			trackDifficultyId = 1;
			break;
		case 3:
			// fast delay
			this.min_delay = 2.5f;
			this.max_delay = 4.0f;
			// hard tracks
			trackDifficultyId = 2;
			break;
		case 4:
			// fast delay
			this.min_delay = 2.0f;
			this.max_delay = 3.0f;
			// harder tracks
			trackDifficultyId = 3;
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * Update function
	 * 
	 * @param deltaTime				The time since the last update	
	 */
	public void update(float deltaTime)
	{
		if(!isPaused && !defeated)
		{
			//animateTracks(deltaTime);
			timeTilVictory -= deltaTime;
            currentDelay -= deltaTime;

			if(timeTilVictory <= 0.0f)
			{
				// convicts win!
				defeated = true;
			}
			else if(currentDelay <= this.min_delay && !playersWarned)
			{
				// start the track animations
				turnOnTracks(chooseNextTrackCodeByPlayers());
				playersWarned = true;
			}
			else if(currentDelay <= 1.0f && !trainsSummoned)
			{
				// summon this batch of trains
                callTrains(nextTrainCode);
				trainsSummoned = true;
			}
			else if(currentDelay <= 0.0625f && !imminentDanger)
			{
				// remove the track animations because the trains are about to arrive
				turnOffAllTracks();
				imminentDanger = true;
			}
			else if(currentDelay <= 0.0f)
            {
                
                // reset track animations for the next round
                turnOffAllTracks(); // just in case
                playersWarned = false;
                imminentDanger = false;
                trainsSummoned = false;
                
                // reset timer information
                activeDelays.remove();
                currentDelay = activeDelays.peek();
            }
		}
	}
	
	private int chooseNextTrackCodeByPlayers() {
		float averageY = 0;
		float count = 0;
		
		for(int i = 0; i < players.size; i++)
		{
			Player p = players.get(i);
			if(p instanceof Prisoner && p.isAlive())
			{
				averageY += p.getBody().getPosition().y;
				count++;
			}
		}
		
		if(count > 0)
		{
			averageY = averageY/count;
		}
		
		int trackNum = -1;
		
		for(int i = 0; i < tracks.length; i++)
		{
			Track t = tracks[i];
			if(averageY > t.getBody().getPosition().y - 0.5f)
			{
				trackNum = i;
			}
		}
		
		trackNum = trackNum == -1 ? 0 : trackNum;
		
		int[] selectables = trackMaps[trackDifficultyId][trackNum];
		nextTrainCode = selectables[MathUtils.random(0, selectables.length-1)];
		
		return nextTrainCode;
	}

	/**
	 * Sets the blink animation to all tracks that match trackCode
	 * @param trackCode	bitmask where 1 is train and 0 is safe
	 */
	private void turnOnTracks(int trackCode) {
		for(int i = 0; i < this.tracks.length; i++) {
			if ((1<<i & trackCode) != 0){
				this.tracks[i].setLowAnim(TextureAnimationDrawer.trackAnim);
			}
		}
	}
	
	/**
	 * Sets all tracks back to normal
	 */
	private void turnOffAllTracks() {
		for(int i = 0; i < this.tracks.length; i++) {
			this.tracks[i].setLowAnim(TextureAnimationDrawer.trackSolid);
		}
	}

	/**
	 * Summons a train in a given direction (determined by GetNextByte()) upon the track passed in
	 *
	 * @param trackCode				bitmask where 1 is train and 0 is safe
	 */
	private void callTrains(int trackCode)
	{
		boolean directionFlipped = false;
		
		for(int i = 0; i < tracks.length; i++)
		{
			// 
			if ((1<<i & trackCode) != 0){
				if(!directionFlipped)
				{
					// Right to Left train
					CGCWorld.getBF().createCar(trainAnchors[i], 50, 1.25f + (i * 1.7f), false, true);
					CGCWorld.getBF().createCar(trainAnchors[i], 55.5f, 1.25f + (i * 1.7f), false, true);
					CGCWorld.getBF().createCar(trainAnchors[i], 61, 1.25f + (i * 1.7f), false, true);
				}
				else
				{
					// Left to Right train
					CGCWorld.getBF().createCar(trainAnchors[i], -30, 1.25f + (i * 1.7f), false, false);
					CGCWorld.getBF().createCar(trainAnchors[i], -35.5f, 1.25f + (i * 1.7f), false, false);
					CGCWorld.getBF().createCar(trainAnchors[i], -41, 1.25f + (i * 1.7f), false, false);
				}
				// TODO : decide if we always weave trains
				directionFlipped = !directionFlipped;
				
				//call the train...
				CGCWorld.getBF().summonTrain(trainIndex);
				trainIndex++;
			}
		}
	}
	
	/**
	 * Have the boss pause - Override
	 */
	public void pause()
	{
		isPaused = true;
	}
	
	/**
	 * Have the boss resume - Override
	 */
	public void resume()
	{
		isPaused = false;
	}

	
	/**
	 * Timestep-based update method
	 */
	public void step(float deltaTime, int layer) 
	{
		for(int n = 0; n < tracks.length; n++)
		{
			tracks[n].step(deltaTime, 0);//TODO check on this layer value
		}
	}
	
	/**
	 * Move the boss - Override
	 * Update function
	 * 
	 * @param deltaTime				Seconds elapsed since the last frame	
	 */
	public void move(float delta)
	{
		
	}
	
	/**
	 * Have the boss fire - Override
	 */
	public void fire()
	{
		
	}
	

	/**
	 * @see com.percipient24.cgc.entities.GameEntity#addToWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	@Override
	public void addToWorldLayers(LayerHandler lh)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see com.percipient24.cgc.entities.GameEntity#removeFromWorldLayers(com.percipient24.helpers.BodyFactory)
	 */
	@Override
	public void removeFromWorldLayers(LayerHandler lh)
	{
		// TODO Auto-generated method stub

	}
}
