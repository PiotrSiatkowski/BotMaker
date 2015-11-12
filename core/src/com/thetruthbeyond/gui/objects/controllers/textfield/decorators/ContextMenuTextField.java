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

import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.Observer;
import com.thetruthbeyond.gui.action.emitters.OnPress;
import com.thetruthbeyond.gui.action.emitters.OnValidateFailure;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.tabs.overtabs.WildcardsTab;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;

public class ContextMenuTextField extends WildcardTextField {

	private final WildcardsTab wildcardsTab;

	private boolean validationErrorHasOccured = false;

	public ContextMenuTextField(WildcardsTab wildcardsTab, TextField parent) {
		super(parent);
		this.wildcardsTab = wildcardsTab;

		Observer<Emitter> observer = getObserver();
		observer.setObserverOwner(this);
		observer.observeEmitter(wildcardsTab.getParent().getEmitter(OnValidateFailure.Id));
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		super.reactToEmittedSignal(object, emitterID);

		if(emitterID == OnValidateFailure.Id) {
			validationErrorHasOccured = true;
		} else

		if(emitterID == OnPress.Id) {
			if(isFocused()) {
				Wildcard wildcard = wildcardsTab.getWildcard();
				if(wildcard != null) {
					// Validation error could occured when some objects signal this event before OnClick event was sent.
					if(!validationErrorHasOccured){
						markWildcard(wildcard);

						String input = getInput();
						if(input.isEmpty() || input.charAt(input.length() - 1) == ' ')
							appendToInput(wildcard.getName() + " ");
						else
							appendToInput(" " + wildcard.getName() + " ");
					}
				}		
			}

			// Put this flag on false to restart validation checking.
			validationErrorHasOccured = false;
		}
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

		return super.executeMousePress(position);
	}

	@Override
	public boolean executeMousePressRight(Position position) {
		if(isFocused()) {
			wildcardsTab.show(position);
			return true;
		}
		
		return false;
	}

	@Override
	public void setFocus(boolean focus) {
		super.setFocus(focus);

		if(focus) {
			// Put this flag on false to restart validation checking.
			validationErrorHasOccured = false;

			// Make callback context wildcardsTab to listen to this text field.
			OnPress onPress = wildcardsTab.getEmitter(OnPress.Id);

			onPress.releaseObservers();
			onPress.registerObserver(wildcardsTab.getParent().getObserver());
			onPress.registerObserver(getObserver());
		}
	}
}
