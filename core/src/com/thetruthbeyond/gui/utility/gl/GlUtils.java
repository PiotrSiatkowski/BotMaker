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

package com.thetruthbeyond.gui.utility.gl;

import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public final class GlUtils {

	private GlUtils() {}

	public static void clearAlpha(Area area) {
		Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
	    Gdx.gl.glScissor(area.getX(), Consts.SCREEN_H - area.getY() - area.getH(), area.getW(), area.getH());
		
		// Setting alpha channel throughout the image to 1.
		Gdx.gl20.glColorMask(false, false, false, true);
		Gdx.gl20.glClearColor(0, 0, 0, 1.0f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				
		Gdx.gl20.glColorMask(true, true, true, true);
		Gdx.gl20.glClearColor(0, 0, 0, 0);
		
		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
	}

	public static void setAlpha(Area area, float alpha) {
		Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
	    Gdx.gl.glScissor(area.getX(), Consts.SCREEN_H - area.getY() - area.getH(), area.getW(), area.getH());
		
		// Setting alpha channel throughout the image to alpha.
		Gdx.gl20.glColorMask(false, false, false, true);
		Gdx.gl20.glClearColor(0, 0, 0, alpha);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				
		Gdx.gl20.glColorMask(true, true, true, true);
		Gdx.gl20.glClearColor(0, 0, 0, 0);
		
		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
	}

	public static void setIgnoreBackgroundBlendMode(SmartSpriteBatch batch) {
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
	}

	public static void setDefaultBlendMode(SmartSpriteBatch batch) {
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
}