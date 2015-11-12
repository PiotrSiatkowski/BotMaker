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

package com.thetruthbeyond.gui.objects.controllers.scrollarea;

import com.badlogic.gdx.graphics.Color;

import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

public class BackgroundArea extends Clickable {

	protected SmartTexture canvas;
	protected SmartTexture border;
	
	protected float BACKGROUND_ALPHA;
			
	public BackgroundArea(BackgroundAreaConfiguration configuration, FileManager loader, Clickable parent) {
		super(configuration, parent);
		
		canvas = loader.getTexture("Canvas");
		border = loader.getTexture("Border");

		BACKGROUND_ALPHA = configuration.backgroundAlpha;
	}

	@Override
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		return false;
	}

	@Override
	public void update(float delta) {}

	@Override
	public void draw(SmartSpriteBatch batch) {
		Color color = batch.getColor();
		
		batch.setColor(1.0f, 1.0f, 1.0f, color.a * BACKGROUND_ALPHA);
		// Drawing background.
		batch.draw(canvas, area.getX(), area.getY(), area.getW(), area.getH());
			
		batch.setColor(color);
		// Drawing border
		batch.draw(border, area.getX(), area.getY(), area.getW(), BORDER_SIZE);
		batch.draw(border, area.getX(), area.getY(), BORDER_SIZE, area.getH());
		batch.draw(border, area.getX(), area.getY() + area.getH() - BORDER_SIZE, area.getW(), BORDER_SIZE);
		batch.draw(border, area.getX() + area.getW() - BORDER_SIZE, area.getY(), BORDER_SIZE, area.getH());
	}
}
