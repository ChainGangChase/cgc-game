/*
 * @(#)MenuTextureRegionAccessor.java		0.2 14/2/5
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */

package com.percipient24.tweens;

import com.percipient24.cgc.screens.helpers.MenuTextureRegion;

import aurelienribon.tweenengine.*;

/*
 * Enables Aurelien Ribon's tween engine to work with MenuTextureRegions
 * 
 * @version 0.2 14/2/5
 * @author William Ziegler
 */
public class MenuTextureRegionAccessor implements TweenAccessor<MenuTextureRegion>
{
	public static final int TRANSLATE_X = 1;
	public static final int WIGGLE_X = 2;
	public static final int TRANSLATE_Y_POSITIVE = 3;
	public static final int TRANSLATE_Y_NEGATIVE = 4;
	public static final int TRANSLATE_XY = 5;
	public static final int SCALE_X = 6;
	public static final int SCALE_Y = 7;
	public static final int SCALE_XY = 8;
	public static final int ROTATION = 9;
	public static final int ALPHA = 10;
	
	/*
	 * Retrieves values from the target MenuTextureRegion for later use
	 * 
	 * @param target				The MenuTextureRegion whose values to change later
	 * @param tweenType				The type of tween to apply to the MenuTextureRegion
	 * @param returnValues			The values from the MenuTextureRegion which is being changed
	 * @return						How many values in returnValues that were changed
	 */
	public int getValues(MenuTextureRegion target, int tweenType, float[] returnValues)
	{
		switch (tweenType)
		{
			case (TRANSLATE_X):
			case (WIGGLE_X):
				returnValues[0] = target.getX();
				return 1;
			case (TRANSLATE_Y_POSITIVE):
			case (TRANSLATE_Y_NEGATIVE):
				returnValues[0] = target.getY();
				return 1;
			case (TRANSLATE_XY):
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;
			case (SCALE_X):
				returnValues[0] = target.getScaleX();
				return 1;
			case (SCALE_Y):
				returnValues[0] = target.getScaleY();
				return 1;
			case (SCALE_XY):
				returnValues[0] = target.getScaleX();
				returnValues[1] = target.getScaleY();
				return 2;
			case (ROTATION):
				returnValues[0] = target.getRotation();
				return 1;
			case (ALPHA):
				returnValues[0] = target.getAlpha();
				return 1;
			default:
				return 0;
		}
	}

	/*
	 * Sets values on the target MenuTextureRegion for tweening
	 * 
	 * @param target				The MenuTextureRegion whose values to change
	 * @param tweenType				The type of tween to apply to the MenuTextureRegion
	 * @param newValues				The new values to set on the MenuTextureRegion
	 */
	public void setValues(MenuTextureRegion target, int tweenType, float[] newValues)
	{
		switch (tweenType)
		{
			case (TRANSLATE_X):
			case (WIGGLE_X):
				target.setX(newValues[0]);
				break;
			case (TRANSLATE_Y_POSITIVE):
			case (TRANSLATE_Y_NEGATIVE):
				target.setY(newValues[0]);
				break;
			case (TRANSLATE_XY):
				target.setX(newValues[0]);
				target.setY(newValues[1]);
				break;
			case (SCALE_X):
				target.setScaleX(newValues[0]);
				break;
			case (SCALE_Y):
				target.setScaleY(newValues[0]);
				break;
			case (SCALE_XY):
				target.setScaleX(newValues[0]);
				target.setScaleY(newValues[1]);
				break;
			case (ROTATION):
				target.setRotation(newValues[0]);
				break;
			case (ALPHA):
				target.setAlpha(newValues[0]);
				break;
			default:
				return;
		}
	}
} // End class