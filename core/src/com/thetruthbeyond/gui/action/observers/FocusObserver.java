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

package com.thetruthbeyond.gui.action.observers;

import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.EventObserver;
import com.thetruthbeyond.gui.action.Observer;
import com.thetruthbeyond.gui.action.emitters.OnFocus;
import com.thetruthbeyond.gui.action.features.Focusable;

import java.util.LinkedList;
import java.util.List;

public class FocusObserver extends Observer<OnFocus> implements EventObserver {

	private final List<Focusable> focusables = new LinkedList<>();
	private int index = -1;

	public FocusObserver(EventObserver observerOwner) {
		super(observerOwner);
	}

	@Override
	public Observer<Emitter> getObserver() {
		return null;
	}

	@Override @SuppressWarnings("unchecked")
	public <T extends Emitter> Observer<T> getSpecializedObserver(int emitterID) {
		if(emitterID == OnFocus.Id)
			return (Observer<T>) this;
		return null;
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(object instanceof Focusable) {
			Focusable focusable = (Focusable) object;
			if(focusable.isFocused())
				index = focusables.indexOf(focusable);
		}
	}

	@Override
	public void observeEmitter(OnFocus emitter) {
		super.observeEmitter(emitter);

		EventEmitter object = emitter.getEmitterOwner();
		if(object instanceof Focusable)
			focusables.add((Focusable) object);
	}

	@Override
	public void stopObserving() {
		super.stopObserving();
		focusables.clear();
	}

	public void dropFocus() {
		index = -1;
	}
	
	public void switchFocus() {
		if(!focusables.isEmpty()) {
			if(index != -1)
				focusables.get(index).setFocus(false);
			index = (index + 1) % focusables.size();
			focusables.get(index).setFocus(true);
		}
	}
}
