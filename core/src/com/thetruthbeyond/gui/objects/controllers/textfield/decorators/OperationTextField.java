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

package com.thetruthbeyond.gui.objects.controllers.textfield.decorators;

import  com.thetruthbeyond.chatterbean.aiml.AIMLElement;
import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.emitters.OnAcceptComponent;
import com.thetruthbeyond.gui.action.emitters.OnPress;
import com.thetruthbeyond.gui.action.emitters.OnChangeVisibility;
import com.thetruthbeyond.gui.interfaces.AIMLGenerator;
import com.thetruthbeyond.gui.interfaces.AIMLGeneratorArgument;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.buttons.simple.OperationButton;
import com.thetruthbeyond.gui.objects.controllers.textfield.MergeDecorator;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldDecorator;
import com.thetruthbeyond.gui.objects.tabs.overtabs.OperationTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

import java.util.List;

public class OperationTextField extends TextFieldDecorator implements AIMLGenerator, AIMLGeneratorArgument {

	private final OperationButton button;
	private final OperationTab callbackTab;

	private boolean wasClicked = false;

	private MergeDecorator mergeDecorator;

	/** This field is required to override getArea() method in a proper way. */
	private Area wholeArea;

	public OperationTextField(OperationTab operationTab, FileManager loader, TextField parent) {
		super(parent);

		wholeArea = new Area(super.getArea());

		Area buttonArea = new Area();
		buttonArea.setX(getX() + super.getW() - getH());
		buttonArea.setY(getY());
		buttonArea.setW(getH());
		buttonArea.setH(getH());
		
		changeW(super.getW() - getH());
		
		button = new OperationButton(buttonArea, loader, this);

		callbackTab = operationTab;

		// Make operation tam appear when button is pressed.
		Emitter emitter = button.getEmitter(OnPress.Id);
		emitter.setEmitterOwner(this);

		callbackTab.getSpecializedObserver(OnPress.Id).observeEmitter(emitter);

		mergeDecorator = findMergeDecorator(WildcardTextField.class);
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(emitterID == OnChangeVisibility.Id) {
			OnChangeVisibility onChangeVisibility = object.getEmitter(emitterID);

			if(wasClicked && !onChangeVisibility.isVisible())
				setFocus(true);
			wasClicked = false;
		} else

		if(emitterID == OnAcceptComponent.Id) {
			if(mergeDecorator != null) {
				Wildcard wildcard = callbackTab.generateWildcard();
				mergeDecorator.obtainMergeInfo(wildcard, getClass());
			}
		}

		super.reactToEmittedSignal(object, emitterID);
	}

	@Override
	public boolean hasAimlElement(AIMLGeneratorArgument argument) {
		return callbackTab.hasAimlElement(argument);
	}

	@Override
	public List<AIMLElement> getAimlElements(AIMLGeneratorArgument argument, Wildcard wildcard) {
		return callbackTab.getAimlElements(this, wildcard);
	}
	
	@Override
	public void changeY(float y) {
		super.changeY(y);
		wholeArea.setY(getY());
		button.changeY(getY());
	}

	@Override
	public int getW() {
		return super.getW() + button.getW();
	}

	@Override
	public Area getArea() {
		return wholeArea;
	}

	@Override
	public boolean contains(Position position) {
		return super.contains(position) || button.contains(position);
	}
	
	@Override
	public boolean executeMouseHover(Position position) {
		if(super.executeMouseHover(position))
			return true;

		return button.executeMouseHover(position);
	}
	
	@Override
	public boolean executeMousePress(Position position) {
		if(contains(position)) {
			if(!isFocused())
				setFocus(true);
		} else {
			if(isFocused())
				setFocus(false);
		}

		if(button.executeMousePress(position))
			return wasClicked = true;

		return super.executeMousePress(position);
	}

	@Override
	public void setFocus(boolean focus) {
		super.setFocus(focus);

		if(focus && mergeDecorator!= null) {

			// Make callback operation tab to listen to this text field.
			OnAcceptComponent onAcceptComponent = callbackTab.getEmitter(OnAcceptComponent.Id);

			onAcceptComponent.releaseObservers();
			onAcceptComponent.registerObserver(getObserver());
		}
	}

	@Override 
	public void update(float delta) {
		super.update(delta);
		button.update(delta);
	}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		if(isFBOHinted())
			button.hintFBODrawing();
		super.draw(batch);

		button.draw(batch);
	}
}
