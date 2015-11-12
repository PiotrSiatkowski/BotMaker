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

package com.thetruthbeyond.gui.objects.tabs.overtabs.wildcards;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import com.badlogic.gdx.graphics.Color;

import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.simple.WildcardOneButton;
import com.thetruthbeyond.gui.objects.buttons.simple.WildcardTwoButton;
import com.thetruthbeyond.gui.objects.shareable.DarknessDrawer;
import com.thetruthbeyond.gui.objects.tabs.overtabs.WildcardsTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.wildcard.StarCard;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;

public class StarWildcardsTab extends WildcardsTab {

	public StarWildcardsTab(Area area, FileManager loader, DarknessDrawer darkness, Clickable parent) {
		super(area, loader, darkness, parent);
	}
	
	@Override
	protected void initializeCards() {
		cards = new Wildcard[] {
				new StarCard("(wildcard)", Color.RED),
				new StarCard("(wildcard)", Color.BLUE),
			};
	}
	
	@Override
	protected void initializeColumns() {
		COLUMNS = 2;
		ROWS = 1;
	}
	
	@Override
	protected void initializeButtons(Area area, FileManager loader, Clickable parent) {
		buttons[0] = new WildcardOneButton(area, loader, this);
		buttons[1] = new WildcardTwoButton(area, loader, this);
	}

	@Override @UnhandledMethod
	public void dispose() {}
}
