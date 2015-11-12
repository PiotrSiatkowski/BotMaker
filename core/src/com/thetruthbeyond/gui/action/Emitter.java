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

package com.thetruthbeyond.gui.action;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class Emitter {

	private EventEmitter emitterOwner;
	private final List<EventObserver> observers = new LinkedList<>();
	
	/** Id functionality. */
	public abstract int getId();
		
	public Emitter(EventEmitter emitterOwner) {
		this.emitterOwner = emitterOwner;
	}

	public final void setEmitterOwner(EventEmitter emitterOwner) {
		this.emitterOwner = emitterOwner;
	}

	public final EventEmitter getEmitterOwner() {
		return emitterOwner;
	}

	protected void signalEvent() {
		for(EventObserver observer : observers)
			observer.reactToEmittedSignal(emitterOwner, getId());
	}
	
	public final void registerObserver(Observer<?> observer) {
		observers.add(observer.getObserverOwner());
	}

	public final void unregisterObserver(Observer<?> observer) {
		observers.remove(observer.getObserverOwner());
	}

	public final void releaseObservers() {
		observers.clear();
	}
}
