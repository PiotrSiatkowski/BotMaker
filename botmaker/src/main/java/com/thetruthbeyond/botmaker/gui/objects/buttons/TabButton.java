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

package com.thetruthbeyond.botmaker.gui.objects.buttons;

import com.thetruthbeyond.botmaker.gui.tabs.teachtab.TeachSubTab;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.structures.Area;

/**
 * Created by Peter Siatkowski on 2015-10-15.
 * Simple method that helps to avoid unnecessary mapping from int to tab types.
 */
public abstract class TabButton extends Button {

    protected TabButton(Clickable parent) {
        super(parent);
    }

    public TabButton(Area area, Clickable parent) {
        super(area, parent);
    }

    public abstract <T extends TeachSubTab> Class<T> getAssociatedTeachTabType();
}
