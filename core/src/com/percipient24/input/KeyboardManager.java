/*
 * @(#)KeyboardManager.java		0.3 14/4/2
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.percipient24.cgc.ChaseApp;
import com.percipient24.enums.ControlType;
import com.percipient24.enums.Platform;

/*
 * Interprets keyboard input into game controls
 * 
 * @version 0.3 14/4/2
 * @author Christopher Rider
 * @author William Ziegler
 */
public class KeyboardManager implements InputProcessor
{
	private ControlAdapter left;
	private ControlAdapter right;
	private int keyboardType = 0; // Gotten from options menu
	private int[][] keys = {{ Keys.W, Keys.A, Keys.S, Keys.D, Keys.TAB, Keys.NUM_1, Keys.P, Keys.Z, Keys.UP, Keys.LEFT, Keys.DOWN, Keys.RIGHT,
								Keys.APOSTROPHE, Keys.ENTER, Keys.SLASH, Keys.BACKSPACE, Keys.COMMA, Keys.PERIOD, Keys.Q, Keys.SEMICOLON }, 
							{ 55, 29, 43, 33, 61, 8, 40, 74, 19, 21, 20, 22, 69, 66, 54, 67, 51, 50, 75, 47 },
							{ 51, 29, 47, 32, 61, 8, 44, 54, 19, 21, 20, 22, 75, 66, 76, 67, 55, 56, 45, 74 }};
	
	private final int LEFT_MOVE_UP = 0;
	private final int LEFT_MOVE_LEFT = 1;
	private final int LEFT_MOVE_DOWN = 2;
	private final int LEFT_MOVE_RIGHT = 3;
	private final int LEFT_PUNCH = 4;
	private final int LEFT_JUMP = 5;
	private final int LEFT_CALLOUT = 7;
	private final int PAUSE = 6;
	private final int RIGHT_MOVE_UP = 8;
	private final int RIGHT_MOVE_LEFT = 9;
	private final int RIGHT_MOVE_DOWN = 10;
	private final int RIGHT_MOVE_RIGHT = 11;
	private final int RIGHT_PUNCH = 12;
	private final int RIGHT_JUMP = 13;
	private final int RIGHT_CALLOUT = 14;
	private final int BACK = 15;
	private final int SORT_PRIM = 16;
	private final int SORT_SEC = 17;
	private final int LEFT_MENU_ACTION = 18;
	private final int RIGHT_MENU_ACTION = 19;
	
	/*
	 * Creates a new KeyboardManager object
	 * 
	 * @param newLeft				The player using the left side of the keyboard
	 * @param newRight				The player using the right side of the keyboard
	 */
	public KeyboardManager(ControlAdapter newLeft, ControlAdapter newRight)
	{
		left = newLeft;
		right = newRight;
		
		Gdx.input.setInputProcessor(this);
	}
	
	/*
	 * Sets the left side player
	 * 
	 * @param newLeft				The player now using the left side
	 */
	public void setLeft(ControlAdapter newLeft)
	{
		left = newLeft;
	}
	
	public ControlAdapter getLeft()
	{
		return left;
	}
	
	/*
	 * Sets the right side player
	 * 
	 * @param newRight				The player now using the right side
	 */
	public void setRight(ControlAdapter newRight)
	{
		right = newRight;
	}
	
	public ControlAdapter getRight()
	{
		return right;
	}

	/*
	 * Converts key presses into game input types
	 * 
	 * @see com.badlogic.gdx.InputProcessor#keyDown(int)
	 */
	public boolean keyDown(int keycode) 
	{
		// Handle left side of keyboard
		if (left != null)
		{
			if (keycode == keys[keyboardType][LEFT_MOVE_UP])
			{
				left.changeControlState(ControlType.UP, true);
			}
			if (keycode == keys[keyboardType][LEFT_MOVE_LEFT])
			{
				left.changeControlState(ControlType.LEFT, true);
			}
			if (keycode == keys[keyboardType][LEFT_MOVE_DOWN])
			{
				left.changeControlState(ControlType.DOWN, true);
			}
			if (keycode == keys[keyboardType][LEFT_MOVE_RIGHT])
			{
				left.changeControlState(ControlType.RIGHT, true);
			}
			if (keycode == keys[keyboardType][LEFT_PUNCH])
			{
				left.changeControlState(ControlType.PUNCH, true);
				left.changeControlState(ControlType.LEFT_FACE, true);
			}
			if (keycode == keys[keyboardType][LEFT_JUMP])
			{
				left.changeControlState(ControlType.JUMP, true);
			}
			if (keycode == keys[keyboardType][PAUSE])
			{
				left.changeControlState(ControlType.PAUSE, true);
			}
			if (keycode == keys[keyboardType][LEFT_CALLOUT])
			{
				left.changeControlState(ControlType.CALLOUT, true);
			}
		}
		
		// Handle right side of keyboard
		if (right != null)
		{
			if (keycode == keys[keyboardType][RIGHT_MOVE_UP])
			{
				right.changeControlState(ControlType.UP, true);
			}
			if (keycode == keys[keyboardType][RIGHT_MOVE_LEFT])
			{
				right.changeControlState(ControlType.LEFT, true);
			}
			if (keycode == keys[keyboardType][RIGHT_MOVE_DOWN])
			{
				right.changeControlState(ControlType.DOWN, true);
			}
			if (keycode == keys[keyboardType][RIGHT_MOVE_RIGHT])
			{
				right.changeControlState(ControlType.RIGHT, true);
			}
			if (keycode == keys[keyboardType][RIGHT_PUNCH])
			{
				right.changeControlState(ControlType.PUNCH, true);
				right.changeControlState(ControlType.LEFT_FACE, true);
			}
			if (keycode == keys[keyboardType][RIGHT_JUMP])
			{
				right.changeControlState(ControlType.JUMP, true);
			}
			if (keycode == keys[keyboardType][PAUSE])
			{
				right.changeControlState(ControlType.PAUSE, true);
			}
			if (keycode == keys[keyboardType][RIGHT_CALLOUT])
			{
				right.changeControlState(ControlType.CALLOUT, true);
			}
		}
		
		// Handle menu controls
		if (keycode == keys[keyboardType][RIGHT_MOVE_UP])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.MENU_UP, true);
			}
		}
		if (keycode == keys[keyboardType][LEFT_MOVE_UP])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.MENU_UP, true);
			}
		}
		if (keycode == keys[keyboardType][RIGHT_MOVE_LEFT])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.MENU_LEFT, true);
			}
		}
		if (keycode == keys[keyboardType][LEFT_MOVE_LEFT])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.MENU_LEFT, true);
			}
		}
		if (keycode == keys[keyboardType][RIGHT_MOVE_DOWN])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.MENU_DOWN, true);
			}
		}
		if (keycode == keys[keyboardType][LEFT_MOVE_DOWN])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.MENU_DOWN, true);
			}
		}
		if (keycode == keys[keyboardType][RIGHT_MOVE_RIGHT])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.MENU_RIGHT, true);
			}
		}
		if (keycode == keys[keyboardType][LEFT_MOVE_RIGHT])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.MENU_RIGHT, true);
			}
		}
		if (keycode == keys[keyboardType][RIGHT_JUMP])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.SELECT, true);
			}
		}
		if (keycode == keys[keyboardType][LEFT_JUMP])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.SELECT, true);
			}
		}
		if (keycode == keys[keyboardType][BACK])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.BACK, true);
			}
		}
		if (keycode == keys[keyboardType][LEFT_PUNCH])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.BACK, true);
			}
		}
		if (keycode == keys[keyboardType][RIGHT_MENU_ACTION])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.MENU_ACTION, true);
			}
		}
		if (keycode == keys[keyboardType][LEFT_MENU_ACTION])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.MENU_ACTION, true);
			}
		}
		if (keycode == keys[keyboardType][SORT_PRIM])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.PRIM_SORT, true);
			}
			if (right != null)
			{
				right.changeControlState(ControlType.PRIM_SORT, true);
			}
		}
		if (keycode == keys[keyboardType][SORT_SEC])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.SEC_SORT, true);
			}
			if (right != null)
			{
				right.changeControlState(ControlType.SEC_SORT, true);
			}
		}
		return false;
	}

	/*
	 * Converts key releases into game input types
	 * 
	 * @see com.badlogic.gdx.InputProcessor#keyUp(int)
	 */
	public boolean keyUp(int keycode) 
	{
		// Handle left side of keyboard
		if (left != null)
		{
			if (keycode == keys[keyboardType][LEFT_MOVE_UP])
			{
				left.changeControlState(ControlType.UP, false);
			}
			if (keycode == keys[keyboardType][LEFT_MOVE_LEFT])
			{
				left.changeControlState(ControlType.LEFT, false);
			}
			if (keycode == keys[keyboardType][LEFT_MOVE_DOWN])
			{
				left.changeControlState(ControlType.DOWN, false);
			}
			if (keycode == keys[keyboardType][LEFT_MOVE_RIGHT])
			{
				left.changeControlState(ControlType.RIGHT, false);
			}
			if (keycode == keys[keyboardType][LEFT_PUNCH])
			{
				left.changeControlState(ControlType.PUNCH, false);
				left.changeControlState(ControlType.LEFT_FACE, false);
			}
			if (keycode == keys[keyboardType][LEFT_JUMP])
			{
				left.changeControlState(ControlType.JUMP, false);
			}
			if (keycode == keys[keyboardType][PAUSE])
			{
				left.changeControlState(ControlType.PAUSE, false);
			}
			if (keycode == keys[keyboardType][LEFT_CALLOUT])
			{
				left.changeControlState(ControlType.CALLOUT, false);
			}
		}
		
		// Handle right side of keyboard
		if (right != null)
		{
			if (keycode == keys[keyboardType][RIGHT_MOVE_UP])
			{
				right.changeControlState(ControlType.UP, false);
			}
			if (keycode == keys[keyboardType][RIGHT_MOVE_LEFT])
			{
				right.changeControlState(ControlType.LEFT, false);
			}
			if (keycode == keys[keyboardType][RIGHT_MOVE_DOWN])
			{
				right.changeControlState(ControlType.DOWN, false);
			}
			if (keycode == keys[keyboardType][RIGHT_MOVE_RIGHT])
			{
				right.changeControlState(ControlType.RIGHT, false);
			}
			if (keycode == keys[keyboardType][RIGHT_PUNCH])
			{
				right.changeControlState(ControlType.PUNCH, false);
				right.changeControlState(ControlType.LEFT_FACE, false);
			}
			if (keycode == keys[keyboardType][RIGHT_JUMP])
			{
				right.changeControlState(ControlType.JUMP, false);
			}
			if (keycode == keys[keyboardType][PAUSE])
			{
				right.changeControlState(ControlType.PAUSE, false);
			}
			if (keycode == keys[keyboardType][RIGHT_CALLOUT])
			{
				right.changeControlState(ControlType.CALLOUT, false);
			}
		}
		
		// Handle menu controls
		if (keycode == keys[keyboardType][RIGHT_MOVE_UP])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.MENU_UP, false);
			}
		}
		if (keycode == keys[keyboardType][LEFT_MOVE_UP])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.MENU_UP, false);
			}
		}
		if (keycode == keys[keyboardType][RIGHT_MOVE_LEFT])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.MENU_LEFT, false);
			}
		}
		if (keycode == keys[keyboardType][LEFT_MOVE_LEFT])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.MENU_LEFT, false);
			}
		}
		if (keycode == keys[keyboardType][RIGHT_MOVE_DOWN])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.MENU_DOWN, false);
			}
		}
		if (keycode == keys[keyboardType][LEFT_MOVE_DOWN])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.MENU_DOWN, false);
			}
		}
		if (keycode == keys[keyboardType][RIGHT_MOVE_RIGHT])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.MENU_RIGHT, false);
			}
		}
		if (keycode == keys[keyboardType][LEFT_MOVE_RIGHT])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.MENU_RIGHT, false);
			}
		}
		if (keycode == keys[keyboardType][RIGHT_JUMP])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.SELECT, false);
			}
		}
		if (keycode == keys[keyboardType][LEFT_JUMP])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.SELECT, false);
			}
		}
		if (keycode == keys[keyboardType][BACK])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.BACK, false);
			}
		}
		if (keycode == keys[keyboardType][LEFT_PUNCH])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.BACK, false);
			}
		}
		if (keycode == keys[keyboardType][RIGHT_MENU_ACTION])
		{
			if (right != null)
			{
				right.changeControlState(ControlType.MENU_ACTION, false);
			}
		}
		if (keycode == keys[keyboardType][LEFT_MENU_ACTION])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.MENU_ACTION, false);
			}
		}
		if (keycode == keys[keyboardType][SORT_PRIM])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.PRIM_SORT, false);
			}
			if (right != null)
			{
				right.changeControlState(ControlType.PRIM_SORT, false);
			}
		}
		if (keycode == keys[keyboardType][SORT_SEC])
		{
			if (left != null)
			{
				left.changeControlState(ControlType.SEC_SORT, false);
			}
			if (right != null)
			{
				right.changeControlState(ControlType.SEC_SORT, false);
			}
		}
		return false;
	}
	
	/*
	 * Sets the keyboard type to use
	 * 
	 * @param newType				The keyboard type
	 */
	public void setKeyboardType(int newType)
	{
		keyboardType = newType;
	}
	
	/*
	 * Accepts keyboard inputs and adds them to a text field
	 * 
	 * @see com.badlogic.gdx.InputProcessor#keyTyped(char)
	 */
	public boolean keyTyped(char character) 
	{		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#touchDown(int, int, int, int)
	 */
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
	 */
	public boolean touchUp(int screenX, int screenY, int pointer, int button) 
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
	 */
	public boolean touchDragged(int screenX, int screenY, int pointer) 
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#mouseMoved(int, int)
	 */
	public boolean mouseMoved(int screenX, int screenY) 
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#scrolled(int)
	 */
	public boolean scrolled(int amount) 
	{
		return false;
	}
} // End class