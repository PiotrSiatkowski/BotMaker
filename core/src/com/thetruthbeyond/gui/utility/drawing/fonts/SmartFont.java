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

package com.thetruthbeyond.gui.utility.drawing.fonts;

import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * @author Peter Siatkowski
 * SmartFont simplifies drawing font to screen in top left coordinate system.
 */
public class SmartFont {
	
	private final BitmapFont font;
	private final Position position = new Position();

	private final GlyphLayout layout = new GlyphLayout();

	public SmartFont(FontType type, int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Fonts.get(type));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		
		parameter.size = size;
		parameter.characters = Coding.rendered;

		font = generator.generateFont(parameter);
		generator.dispose();
	}

	public void setColor(float r, float g, float b, float a) {
		font.setColor(r, g, b, a);
	}

	public void setColor(Color color) {
		font.setColor(color);
	}

	public void setPosition(int x, int y) {
		position.x = x;
		position.y = y;
	}

	public void setPosition(Position position) {
		this.position.x = position.x;
		this.position.y = position.y;
	}
	
	public int getWidth(String message) {
		layout.setText(font, message);
		return Math.round(layout.width);
	}
	
	public int getHeight(String message) {
		layout.setText(font, message);
		return Math.round(layout.height);
	}

	public int getDescentHeight() {
		return Math.round(font.getDescent());
	}
	
	public void draw(SmartSpriteBatch batch, String message) {
		font.draw(batch, message, position.x, Consts.SCREEN_H - position.y);
	}
	
	public void draw(SmartSpriteBatch batch, String message, int x, int y) {
		font.draw(batch, message, x, Consts.SCREEN_H - y);		
	}

	public void dispose() {
		font.dispose();
	}
}
