/*
 * @(#)CGCOverlay.java		0.2 14/3/3
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.cgc.overlays;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.percipient24.cgc.Data;

/*
 * Contains basic logic for rendering HUD elements
 * 
 * @version 0.2 14/3/3
 * @author William Ziegler
 */
public class CGCOverlay
{
	protected SpriteBatch sBatch;
	protected Matrix4 overlayMatrix;
	protected boolean showElement;
	
	/*
	 * Creates a new CGCOverlay object
	 * 
	 * @param newBatch				The SpriteBatch the game is using
	 */
	public CGCOverlay(SpriteBatch newBatch)
	{
		sBatch = newBatch;
		overlayMatrix = new Matrix4().setToOrtho2D(0f, 0f, (float)Data.ACTUAL_WIDTH, (float)Data.ACTUAL_HEIGHT);
	}
	
	/*
	 * Renders this overlay
	 * 
	 * @param delta					Seconds elapsed since last frame
	 */
	public void render(float delta)
	{
		//Override this.
	}
	
	/*
	 * Sets whether or not to show this Overlay element
	 * 
	 * @param shouldShow				Whether or not to show this Overlay element
	 */
	public void setShow(boolean shouldShow)
	{
		showElement = shouldShow;
	}
	
	/*
	 * Gets whether or not to show this Overlay element
	 * 
	 * @return				Whether or not to show this Overlay element
	 */
	public boolean getShow()
	{
		return showElement;
	}
	
	/* Gets the projection matrix used by this CGCOverlay
	 * 
	 * @return				The projection matrix used by this CGCOverlay
	 */
	public Matrix4 getOverlayMatrix()
	{
		return overlayMatrix;
	}
}
