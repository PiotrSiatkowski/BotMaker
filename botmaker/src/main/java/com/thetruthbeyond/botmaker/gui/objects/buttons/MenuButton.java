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

package com.thetruthbeyond.botmaker.gui.objects.buttons;

import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

import com.badlogic.gdx.graphics.Color;

public abstract class MenuButton extends Button {

	protected SmartTexture icon;
	protected SmartTexture iconHover;
	
	protected static final int ICON_OFFSET_X = 3;
	protected static final int ICON_OFFSET_Y = 3;
	
	protected MenuButton(Clickable parent) {
		super(parent);
	}
	
	protected MenuButton(Area area, Clickable parent) {
		super(area, parent);
	}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		batch.draw(button, area.getX(), area.getY(), button.getW(), button.getH());
		if(hoverTime == 0) {
			batch.draw(icon, area.getX() + ICON_OFFSET_X, area.getY() + ICON_OFFSET_Y, icon.getW(), icon.getH());
		} else if(hoverTime == 1) {
			batch.draw(iconHover, area.getX() + ICON_OFFSET_X, area.getY() + ICON_OFFSET_Y, iconHover.getW(), iconHover.getH());
		} else {
			batch.draw(icon, area.getX() + ICON_OFFSET_X, area.getY() + ICON_OFFSET_Y, icon.getW(), icon.getH());
			batch.setColor( new Color(1.0f, 1.0f, 1.0f, hoverAlpha) );
				batch.draw(iconHover, area.getX() + ICON_OFFSET_X, area.getY() + ICON_OFFSET_Y, iconHover.getW(), iconHover.getH());
			batch.setColor(Color.WHITE);
		}	
	}
}
