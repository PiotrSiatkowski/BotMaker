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

package com.thetruthbeyond.gui.objects.controllers.scrollpanel.decorators;

import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.EventObserver;
import com.thetruthbeyond.gui.action.emitters.OnFocus;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.controllers.ControllerConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanel;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanelDecorator;

import java.util.LinkedList;
import java.util.List;

public class StateScrollPanel extends ScrollPanelDecorator {

	private final List<EventObserver> observers = new LinkedList<>();

	protected int previousPointer;
	protected int targetPointer;
	
	protected static final float MOTION_TIME = 1.0f;
	
	protected float motionTime = MOTION_TIME;
	protected float currentTime = MOTION_TIME;
	
	protected final int MINIMAL_OFFSET_FOR_ANIMATION = (int)(0.01 * getObjectAreaH());
	
	public StateScrollPanel(ScrollPanel parent) {
		super(parent);
		getObserver().setObserverOwner(this);
	}

	public void addObserver(EventObserver observer) {
		observers.add(observer);
	}

	@Override @SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends ControllerConfiguration, U extends Clickable> U addObjectImmediately(Class<U> type, T configuration) {
		U object = super.addObjectImmediately(type, configuration);

		if(object.getEmitter(OnFocus.Id) != null) {
			Emitter emitter = object.getEmitter(OnFocus.Id);
			getObserver().observeEmitter(emitter);
			for(EventObserver observer : observers)
				observer.getSpecializedObserver(OnFocus.Id).observeEmitter(emitter);
		}
		
		object.setParent(this);
		return object;
	}

	@Override @SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends ControllerConfiguration, U extends Clickable> U addObjectWithAppear(Class<U> type, T configuration) {
		U object = super.addObjectWithAppear(type, configuration);

		if(object.getEmitter(OnFocus.Id) != null) {
			Emitter emitter = object.getEmitter(OnFocus.Id);
			getObserver().observeEmitter(emitter);
			for(EventObserver observer : observers)
				observer.getSpecializedObserver(OnFocus.Id).observeEmitter(emitter);
		}
		
		object.setParent(this);
		return object;
	}

	@SuppressWarnings("IfStatementWithIdenticalBranches")
	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		super.reactToEmittedSignal(object, emitterID);

		if(object instanceof Clickable && emitterID == OnFocus.Id) {

			Clickable clickable = (Clickable) object;

			if(getScrollBar() != null && getObjects().size() > getRows() * getColumns()) {

				int dy = 0;

				if(isBoundaryPaddingIncluded()) {
					if(getObjectAreaY() > clickable.getY())
						dy = -(getObjectAreaY() - clickable.getY() + getGapH());
					else

					if(clickable.getY() + getObjectH() > getObjectAreaY() + getObjectAreaH() - getGapH())
						dy = clickable.getY() + getObjectH() - (getObjectAreaY() + getObjectAreaH() - getGapH());
				} else {
					if(getObjectAreaY() > clickable.getY())
						dy = -(getObjectAreaY() - clickable.getY());
					else

					if(clickable.getY() + getObjectH() > getObjectAreaY() + getObjectAreaH())
						dy = clickable.getY() + getObjectH() - (getObjectAreaY() + getObjectAreaH());
				}

				if(Math.abs(dy) >= MINIMAL_OFFSET_FOR_ANIMATION) {
					// Animation in move. New scroll velocity is less then actual velocity.
					if(currentTime < motionTime && dy < targetPointer - previousPointer) {
						float v = (targetPointer - previousPointer) / motionTime;

						previousPointer = getStartY();
						targetPointer = previousPointer + dy;

						motionTime = dy / v;
						currentTime = 0.0f;
					} else {
						previousPointer = getStartY();
						targetPointer = previousPointer + dy;
						motionTime = MOTION_TIME;
						currentTime = 0.0f;
					}
				}
			}
		}
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if(currentTime < motionTime) {
			currentTime = Math.min(currentTime + delta, motionTime);
			float dy = (currentTime / motionTime) * (targetPointer - previousPointer);
			
			setStartY(previousPointer + Math.round(dy));
			
			getScrollBar().setBarNumbers(getStartY(), getObjectAreaH(), getTotalH() - getObjectAreaH() - getStartY());
			for(int i = 0; i != getObjects().size(); i++)
				getObjects().get(i).changeY(getPositionsY().get(i) - getStartY());
		}
	}
}
