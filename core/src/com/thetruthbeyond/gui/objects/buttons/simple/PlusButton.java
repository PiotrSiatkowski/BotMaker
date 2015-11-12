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

package com.thetruthbeyond.gui.objects.buttons.simple;

import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.structures.Area;

@SuppressWarnings("AssignmentToSuperclassField")
public class PlusButton extends Button {

	public PlusButton(FileManager loader, Clickable parent) {
		super(parent); 
		initialize(loader);
	}
	
	public PlusButton(Area area, FileManager loader, Clickable parent) {
		super(area, parent); 
		initialize(loader);
	}

	@Override
	protected void initialize(FileManager loader) {
		button = loader.getTexture("ButtonPlus");
		buttonHover = loader.getTexture("ButtonPlusHover");
		shape = Shape.Oval;

		soundHover = loader.getSound("ButtonHover2");
		soundPressed = loader.getSound("ButtonClick2");
	}
}