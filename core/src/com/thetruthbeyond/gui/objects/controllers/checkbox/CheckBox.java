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

package com.thetruthbeyond.gui.objects.controllers.checkbox;

import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

public class CheckBox extends Clickable {

	private final SmartTexture textureOn;
	private final SmartTexture textureOff;
	private final SmartTexture border;
	private final SmartTexture checkBoxYes;
	
	private boolean isChecked = false;
	
	private final Area checkArea;
	
	protected CheckBox(CheckBoxConfiguration configuration, FileManager loader, Clickable parent) {
		super(configuration, parent);
		
		textureOn 	= loader.getTexture("TextFieldOn");
		textureOff 	= loader.getTexture("TextFieldOff");
		border 		= loader.getTexture("Border");
		checkBoxYes = loader.getTexture("CheckBoxYes");

		checkArea = new Area(area).cutArea(BORDER_SIZE);
	}

	public void setChecked(boolean checked) {
		isChecked = checked;
	}
	
	@Override
	public boolean contains(Position position) {
		return !(position.x < checkArea.getX() || position.x > checkArea.getX() + checkArea.getW() ||
				 position.y < checkArea.getY() || position.y > checkArea.getY() + checkArea.getH());
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
		
		if(isChecked)
			batch.draw(textureOn, area.getX(), area.getY(), area.getW(), area.getH());
		else 
			batch.draw(textureOff, area.getX(), area.getY(), area.getW(), area.getH());
		
		// Drawing border
		batch.draw(border, area.getX(), area.getY(), area.getW(), BORDER_SIZE);
		batch.draw(border, area.getX(), area.getY(), BORDER_SIZE, area.getH());
		batch.draw(border, area.getX(), area.getY() + area.getH() - BORDER_SIZE, area.getW(), BORDER_SIZE);
		batch.draw(border, area.getX() + area.getW() - BORDER_SIZE, area.getY(), BORDER_SIZE, area.getH());
		
		if(isChecked)
			batch.draw(checkBoxYes, area.getX() + 2 * BORDER_SIZE, area.getY() - 2 * BORDER_SIZE, area.getW(), area.getH());
	}
}
