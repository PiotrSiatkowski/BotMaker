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

package com.thetruthbeyond.gui.utility.drawing;

import com.thetruthbeyond.gui.configuration.Consts;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Peter Siatkowski
 * Class used to simplify drawing operation in left top corner is zero in coordinate system.
 */
@SuppressWarnings("MethodWithTooManyParameters")
public class SmartSpriteBatch extends SpriteBatch {
	
	/** Drawing rectangle. */
	@Override
	public void draw(Texture region, float x, float y, float w, float h) {
		super.draw(region, x, Consts.SCREEN_H - y - h, w, h);
	}

	@Override
	public void draw(TextureRegion region, float x, float y, float w, float h) {
		super.draw(region, x, Consts.SCREEN_H - y - h, w, h);
	}
	
	public void draw(SmartTexture texture, float x, float y, float w, float h) {
		super.draw(texture.getTextureRegion(), x, Consts.SCREEN_H - y - h, w, h);
	}

	/** With rotation. */
	public void draw(TextureRegion region, float x, float y, float w, float h, float angle) {
		draw(region, x, Consts.SCREEN_H - y - h, w / 2.0f, h / 2.0f, w, h, 1.0f, 1.0f, angle);
	}
	
	public void draw(SmartTexture texture, float x, float y, float w, float h, float angle) {
		draw(texture.getTextureRegion(), x, Consts.SCREEN_H - y - h, w / 2.0f, h / 2.0f, w, h, 1.0f, 1.0f, angle);
	}
}
