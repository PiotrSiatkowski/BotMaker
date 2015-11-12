/*
 * Copyleft (C) 2015 Piotr Siatkowski find me on Facebook;
 * This file is part of BotMaker. BotMaker is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. BotMaker is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with BotMaker (look at the
 * Documents directory); if not, either write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA, or visit
 * (http://www.gnu.org/licenses/gpl.txt).
 */

/*
 * BotMaker - file created and updated by Piotr Siatkowski (2015).
 */

package com.thetruthbeyond.gui.configuration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public final class Consts {

	private Consts() {}

	public static boolean initialize() {
		// Check if graphic context has been initialized.
		if(Gdx.graphics.getWidth() == 1 || Gdx.graphics.getHeight() == 1) {
			return false;
		} else {
			SCREEN_W = Gdx.graphics.getWidth();
			SCREEN_H = Gdx.graphics.getHeight();

			return true;
		}
	}

	// To be initialized.
	public static int SCREEN_W = 1;
	public static int SCREEN_H = 1;

	public static final int MENU_H = 41;
	public static final int PANE_H = 71;
	
	// Size of border in every graphical element.
	public static final int BORDER_SIZE = 4;
	
	// Relative padding fill to whole element width.
	public static final float RELATIVE_TEXT_PADDING = 0.17f;
	
	// Panels transparency level.
	public static final float BACKGROUND_ALPHA = 0.8f;

	public static final Color MAIN_FONT_COLOR = new Color(0.4f, 0.5f, 0.2f, 1.0f);
	public static final Color GREY_FONT_COLOR = new Color(0.6f, 0.6f, 0.6f, 1.0f);

	// Current resolution which this version aims.
	public static final int RESOLUTION = 1;

	// Resolution flag.
	public static final int RES_1366x768 = 1;

	// Dev mode flag.
	public static boolean DEV_MODE = false;
}
