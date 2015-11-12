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

import java.util.LinkedList;
import java.util.List;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import com.badlogic.gdx.audio.Sound;

import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

public class CheckBoxList extends Clickable {

	private final List<CheckBox> list = new LinkedList<>();
	private int checked = 0;
	
	private final Sound sound;
	
	public CheckBoxList(CheckBoxListConfiguration configuration, FileManager loader, Clickable parent) {
		super(configuration, parent);
		
		sound = loader.getSound("ButtonHover");
		
		int gapsize, boxsize;
		if(configuration.alignVertical) {
			gapsize = Math.round(configuration.relativeGap * area.getH());

			int remaining = area.getH() - (configuration.boxes + 1) * gapsize - 2 * configuration.padding;

			if(remaining % configuration.boxes != 0) {
				if(configuration.fitHeightByMinimizing)
					area.setH(getH() - remaining % configuration.boxes);
				else
					area.setH(getH() + (configuration.boxes - remaining % configuration.boxes));
			}

			boxsize = Math.round(((area.getH() - 2 * configuration.padding) - (configuration.boxes + 1) * gapsize) / (float) configuration.boxes);
		} else {
			gapsize = Math.round(configuration.relativeGap * area.getW());

			int remaining = area.getH() - (configuration.boxes + 1) * gapsize - 2 * configuration.padding;
				
			if(remaining % configuration.boxes != 0) {
				if(configuration.fitHeightByMinimizing)
					area.setH(getH() - remaining % configuration.boxes);
				else
						area.setH(getH() + (configuration.boxes - remaining % configuration.boxes));
			}
				
			boxsize = Math.round(((area.getW() - 2 * configuration.padding) - (configuration.boxes + 1) * gapsize) / (float) configuration.boxes);
		}
		
		for(int i = 0; i != configuration.boxes; i++) {
			
			CheckBoxConfiguration config = new CheckBoxConfiguration();
			config.border = configuration.border;
			
			if(configuration.alignVertical) {
				config.area.setX(area.getX());
				config.area.setY(area.getY() + i * (gapsize + boxsize) + gapsize + configuration.padding);
				config.area.setW(area.getW());
				config.area.setH(boxsize);
			} else {
				config.area.setX(area.getX() + i * (gapsize + boxsize) + configuration.padding);
				config.area.setY(area.getY());
				config.area.setW(boxsize);
				config.area.setH(area.getH());
			}
			
			list.add(new CheckBox(config, loader, this));
		}
		
		if(!list.isEmpty())
			list.get(checked).setChecked(true);
	}

	public int getCheckedIndex() {
		return checked;
	}

	public void setCheckedIndex(int index) {
		checked = index;
	}

	public Area getAreaOfBox(int i) {
		if(i >= 0 && i < list.size())
			return list.get(i).getArea();
		else 
			return new Area();
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

	@Override
	public boolean executeMousePress(Position position) {
		for(int i = 0, size = list.size(); i != size; i++) {
			CheckBox box = list.get(i);
			
			if(box.contains(position)) {
				
				// Uncheck all boxes.
				for(int j = 0; j != size; j++)
					list.get(j).setChecked(false);
				box.setChecked(true);
				
				if(checked != i)
					sound.play();
				checked = i;
				return true;
			}
		}
		
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
		for(CheckBox box : list)
			box.draw(batch);
	}
}
