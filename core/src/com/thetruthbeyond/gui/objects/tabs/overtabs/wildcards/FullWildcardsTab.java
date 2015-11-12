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
import com.thetruthbeyond.gui.objects.buttons.simple.*;
import com.thetruthbeyond.gui.objects.shareable.DarknessDrawer;
import com.thetruthbeyond.gui.objects.tabs.overtabs.WildcardsTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.wildcard.*;

public class FullWildcardsTab extends WildcardsTab {
	
	public FullWildcardsTab(Area area, FileManager loader, DarknessDrawer darkness, Clickable parent) {
		super(area, loader, darkness, parent);
	}
	
	@Override
	protected void initializeCards() {
		cards = new Wildcard[] {
				new StarCard("(wildcard)", Color.RED),
				new RecursionCard("(recursion)", Color.RED).setIndex(0),
				new StarCard("(wildcard)", Color.BLUE),
				new RecursionCard("(recursion)", Color.BLUE).setIndex(1),
				new ConditionCard("(condition)", Color.RED).setIndex(0),
				new RecursionCard("(recursion)", Color.GOLDENROD).setIndex(2),
				new ConditionCard("(condition)", Color.BLUE).setIndex(1),
				new RandomCard("(random)", Color.MAGENTA)
			};
	}
	
	@Override
	protected void initializeColumns() {
		COLUMNS = 2;
		ROWS = 4;
	}
	
	@Override
	protected void initializeButtons(Area area, FileManager loader, Clickable parent) {
		buttons[0] = new WildcardOneButton(area, loader, this);
		buttons[1] = new Recursion1Button(area, loader, this);
		buttons[2] = new WildcardTwoButton(area, loader, this);
		buttons[3] = new Recursion2Button(area, loader, this);
		buttons[4] = new ConditionOneButton(area, loader ,this);
		buttons[5] = new Recursion3Button(area, loader, this);
		buttons[6] = new ConditionTwoButton(area, loader, this);
		buttons[7] = new RandomOneButton(area, loader, this);
	}

	@Override @UnhandledMethod
	public void dispose() {}
}