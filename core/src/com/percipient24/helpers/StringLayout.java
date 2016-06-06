/*
 * @(#)StringLayout.java		0.1 16/28/5
 * 
 * Copyright 2016, MAGIC Spell Studios, LLC
 */
package com.percipient24.helpers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/*
 * Stores GlyphLayout and String for text regions
 * 
 * @version 0.1 16/28/5
 * @author Joe Pietruch
 */
public class StringLayout 
{
	private BitmapFont font;
	private GlyphLayout layout;
	private String text;

	public StringLayout(String m, BitmapFont bf) {
		text = m;
		font = bf;
		layout = new GlyphLayout();
		updateText(text);
	}

	public void updateText(String m) {
		text = m;
		layout.setText(font, text);
	}

	public GlyphLayout getLayout() {
		layout.setText(font, text);
		return layout;
	}
}