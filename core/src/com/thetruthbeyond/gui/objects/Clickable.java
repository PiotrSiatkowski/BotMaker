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

package com.thetruthbeyond.gui.objects;

import com.thetruthbeyond.gui.interfaces.GUIComponent;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.objects.controllers.ControllerConfiguration;
import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.EventObserver;
import com.thetruthbeyond.gui.action.Observer;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

import com.badlogic.gdx.utils.IntMap;

public abstract class Clickable implements EventEmitter, EventObserver, GUIComponent {

	protected final int BORDER_SIZE;

	protected Area area;
	protected Clickable parent;

	private Observer<Emitter> observer;
	private IntMap<Emitter> emitters;

	protected Clickable(Clickable parent) {
		area = new Area();

		this.parent = parent;
		BORDER_SIZE = Consts.BORDER_SIZE;
	}

	protected Clickable(Area area, Clickable parent) {
		this.area = new Area(area);

		this.parent = parent;
		BORDER_SIZE = Consts.BORDER_SIZE;
	}

	protected Clickable(ControllerConfiguration configuration, Clickable parent) {
		area = new Area(configuration.area);

		this.parent = parent;
		BORDER_SIZE = configuration.border;
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {}

	@Override
	public Observer<Emitter> getObserver() {
		if(observer == null)
			observer = new Observer<>(this);
		return observer;
	}

	@Override
	public <T extends Emitter> Observer<T> getSpecializedObserver(int emitterID) {
		return null;
	}

	@Override
	public void addEmitter(Emitter emitter) {
		if(emitters == null)
			emitters = new IntMap<>();
		emitters.put(emitter.getId(), emitter);
	}

	@Override @SuppressWarnings("unchecked")
	public <T extends Emitter> T getEmitter(int emitterID) {
		if(emitters == null)
			emitters = new IntMap<>();
		return (T) emitters.get(emitterID, null);
	}

	public void setParent(Clickable parent) { this.parent = parent; }
	public Clickable getParent() { return parent; }
	
	public boolean contains(Position position) {
		return area.contains(position);
	}

	public abstract boolean executeMouseHover(Position position);	
	
	public abstract boolean executeMouseDrag(Position position);
	public abstract boolean executeMousePress(Position position);
	public abstract boolean executeMouseRelease(Position position);
	
	public boolean executeMouseDragRight(Position position) { return false; }
	public boolean executeMousePressRight(Position position) { return false; }	
	public boolean executeMouseReleaseRight(Position position) { return false; }
	
	public abstract void update(float delta);
	public abstract void draw(SmartSpriteBatch batch);
	
	public int getX() { return area.getX(); }
	public int getY() { return area.getY(); }
	public int getW() { return area.getW(); }
	public int getH() { return area.getH(); }
		
	public void changeX(float x) { area.setX(x); }
	public void changeY(float y) { area.setY(y); }
	
	public Area getArea() { return area; }
}
