/*
 * @(#)MenuTextureRegionAccessor.java		0.2 14/2/5
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.tweens;

import com.percipient24.cgc.overlays.Transition;

import aurelienribon.tweenengine.*;

/*
 * Enables Aurelien Ribon's tween engine to work with Transitions
 * 
 * @version 0.2 14/2/5
 * @author William Ziegler
 */
public class TransitionAccessor implements TweenAccessor<Transition>
{
	public static final int TRANSLATE_X = 1;
	public static final int TRANSLATE_Y_POSITIVE = 2;
	public static final int TRANSLATE_Y_NEGATIVE = 3;
	public static final int TRANSLATE_XY = 4;
	
	/*
	 * Retrieves values from the target Transition for later use
	 * 
	 * @param target				The Transition whose values to change later
	 * @param tweenType				The type of tween to apply to the Transition
	 * @param returnValues			The values from the Transition which is being changed
	 * @return						How many values in returnValues that were changed
	 */
	public int getValues(Transition target, int tweenType, float[] returnValues)
	{
		switch (tweenType)
		{
			case (TRANSLATE_X):
				returnValues[0] = target.getXPosition();
				return 1;
			case (TRANSLATE_Y_POSITIVE):
			case (TRANSLATE_Y_NEGATIVE):
				returnValues[0] = target.getYPosition();
				return 1;
			case (TRANSLATE_XY):
				returnValues[0] = target.getXPosition();
				returnValues[1] = target.getYPosition();
				return 2;
			default:
				return 0;
		}
	}

	/*
	 * Sets values on the target Transition for tweening
	 * 
	 * @param target				The Transition whose values to change
	 * @param tweenType				The type of tween to apply to the Transition
	 * @param newValues				The new values to set on the Transition
	 */
	public void setValues(Transition target, int tweenType, float[] newValues)
	{
		switch (tweenType)
		{
			case (TRANSLATE_X):
				target.setXPosition(newValues[0]);
				break;
			case (TRANSLATE_Y_POSITIVE):
			case (TRANSLATE_Y_NEGATIVE):
				target.setYPosition(newValues[0]);
				break;
			case (TRANSLATE_XY):
				target.setXPosition(newValues[0]);
				target.setYPosition(newValues[1]);
				break;
			default:
				return;
		}
	}
} // End class