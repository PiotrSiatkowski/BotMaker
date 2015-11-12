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

package com.thetruthbeyond.gui.objects.controllers.label;

import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.fonts.FontPool;
import com.thetruthbeyond.gui.utility.drawing.fonts.SmartFont;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.badlogic.gdx.graphics.Color;

public class Label extends Clickable {

	private SmartFont labelFont;

	private final String label;
	private final Color color;

	private final int Y;
	
	public Label(LabelConfiguration configuration, Clickable parent) {
		super(configuration, parent);
		
		label = configuration.label;
		color = configuration.color;

		labelFont = FontPool.createFont(configuration.fontname, configuration.area.getH());

		// Setting proper font size.
		int height = configuration.area.getH();
		while(true) {
			if(height == 0)
				new Logger().writeMessage("Label error", "This label need more space to proceed.");
			int width = labelFont.getWidth(label);
			if(width > area.getW()) {
				float ratio = width / (float) area.getW();
				height = (int) Math.floor(height / ratio);

				labelFont = FontPool.createFont(configuration.fontname, height);
			} else
				break;
		}

		Y = Math.round((area.getH() - labelFont.getHeight(label)) / 2.0f);
	}

	@Override @UnhandledMethod
	public boolean contains(Position position) {
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseHover(Position position) {
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseDrag(Position position) {
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMousePress(Position position) {
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseRelease(Position position) {
		return false;
	}

	@Override @UnhandledMethod
	public void update(float delta) {}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		labelFont.setColor(color);
		labelFont.draw(batch, label, area.getX(), area.getY() + Y);
	}
}
