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

package com.thetruthbeyond.botmaker.gui.tabs.teachtab;

import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.tabs.Tab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.OperationTab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.WildcardsTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;

public abstract class TeachSubTab extends Tab {

	/** This constructor aims to give standardized build method of teach sub tabs objects. */
	@SuppressWarnings("UnusedParameters")
	protected TeachSubTab(Area area, FileManager loader, Clickable parent, WildcardsTab wildcardsTab, OperationTab operationTab) {
		super(area, parent);
	}

	public abstract Wildcard getWildcard();

	public abstract boolean verifyWildcardAcceptance(Wildcard wildcard);
	public abstract boolean isUpdateNeeded();

	public abstract void performUpdate();
}
