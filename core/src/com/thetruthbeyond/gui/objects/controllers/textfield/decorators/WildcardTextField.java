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

import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.emitters.OnAcceptComponent;
import com.thetruthbeyond.gui.action.emitters.OnValidateFailure;
import com.thetruthbeyond.gui.enums.ValidationFailureCause;
import com.thetruthbeyond.gui.objects.controllers.textfield.MergeDecorator;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldDecorator;
import com.thetruthbeyond.gui.structures.wildcard.VariableCard;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

import java.util.*;

import com.badlogic.gdx.graphics.Color;

public class WildcardTextField extends TokenizedTextField implements MergeDecorator {

	private OnValidateFailure onValidationFailure;

	private final Map<Integer, Wildcard> wildcards = new HashMap<>(8);
	
	private final Map<String, List<Wildcard>> queueMap = new HashMap<>(8);
	private final Map<String, Integer> counterMap = new HashMap<>(8);
	
	private final Set<String> regulars = new HashSet<>(4);

	private Wildcard obtainedWildcard;

	public WildcardTextField(TextField parent) {
		super(parent);
		onValidationFailure = new OnValidateFailure(this);
		addEmitter(onValidationFailure);
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(emitterID == OnAcceptComponent.Id) {
			if(hasWildcard(obtainedWildcard))
				onValidationFailure.signalValidationFailure(ValidationFailureCause.WildcardAlreadyPresent);
			else
				addWildcard(obtainedWildcard);
		}

		super.reactToEmittedSignal(object, emitterID);
	}

	@Override
	public <T extends TextFieldDecorator> void obtainMergeInfo(Object object, Class<T> type) {
		obtainedWildcard = (Wildcard) object;
	}

	public WildcardTextField markRegularExpression(String expression) {
		regulars.add(expression);
		
		updateWildcards(0);
		return this;
	}
	
	public WildcardTextField addWildcard(Wildcard wildcard) {
		String input = getInput();
		if(input.isEmpty() || input.charAt(input.length() - 1) == ' ') {
			if(getFont().getWidth(input) + getFont().getWidth(wildcard.getName()) < getW() - 2 * getPadding()) {
				appendToInput(wildcard.getName());
				wildcards.put(words.size() - 1, wildcard);
				appendToInput(" ");
			}
		} else {
			if(getFont().getWidth(input) + getFont().getWidth(" " + wildcard.getName()) < getW() - 2 * getPadding()) {
				appendToInput(" " + wildcard.getName());
				wildcards.put(words.size() - 1, wildcard);
				appendToInput(" ");
			}
		}
		
		return this;
	}
	
	public WildcardTextField markWildcard(Wildcard wildcard) {
		if(!queueMap.containsKey(wildcard.getName())) {
			queueMap.put(wildcard.getName(), new LinkedList<Wildcard>());
			counterMap.put(wildcard.getName(), 0);
		}
		
		List<Wildcard> list = queueMap.get(wildcard.getName());
		list.add(wildcard);
		
		updateWildcards(0);
		return this;
	}
	
	public Wildcard getWordAsWildcard(Integer index) {
		if(wildcards.containsKey(index))
			return wildcards.get(index);
		return null;
	}
	
	public boolean hasWildcard(Wildcard wildcard) {
		return wildcards.values().contains(wildcard);
	}

	@Override
	public void appendToInput(String input) {
		int previousWordAmount = words.size();
		super.appendToInput(input);
		updateWildcards(previousWordAmount);
	}
	
	@Override
	public void setInput(String input) {
		super.setInput(input);
		
		wildcards.clear();
		for(String name : counterMap.keySet())
			counterMap.put(name, 0);
		
		updateWildcards(0);
	}
	
	@Override
	public void update(float delta) {

		super.update(delta);
		
		if(hasChanged() && !words.isEmpty()) {
 			int lastIndex = words.size() - 1; String lastWord = words.get(lastIndex);

			// After altering input new wildcards can be created.
			boolean tryToMatchNewWildcard = true;

			if(wildcards.containsKey(lastIndex)) {
				// Get last wildcard in the field.
				String name = wildcards.get(lastIndex).getName();

				if(name.length() != lastWord.length()) {
					// Wildcard cannot be valid after one letter had been added or erased.
					wildcards.remove(lastIndex);

					if(name.length() > lastWord.length()) {
						String input = getInput();

						// Erase whole wildcard at once if one letter was erased.
						super.setInput(input.substring(0, input.length() - lastWord.length()));
						tryToMatchNewWildcard = false;
					}
				}
			}
			
			if(tryToMatchNewWildcard) {
				if(queueMap.containsKey(lastWord)) {
					List<Wildcard> list = queueMap.get(lastWord);
					int index = counterMap.get(lastWord);
					
					wildcards.put(lastIndex, list.get(index));
					counterMap.put(lastWord, (index + 1) % list.size());
				} else {
					for(String regular : regulars) {
						if(lastWord.matches(regular)) {
							wildcards.put(lastIndex, new VariableCard(lastWord, Color.MAROON));
							break;
						}
					}
				}
			}
		}
	}
	
	private void updateWildcards(int beginWordIndex) {
		for(int i = beginWordIndex, size = words.size(); i != size; i++) {
			String word = words.get(i);
			
			if(queueMap.containsKey(word)) {
				List<Wildcard> list = queueMap.get(word);
				int index = counterMap.get(word);
				
				wildcards.put(i, list.get(index));
				counterMap.put(word, (index + 1) % list.size());
			} else {
				for(String regular : regulars) {
					if(word.matches(regular)) {
						wildcards.put(i, new VariableCard(word, Color.MAROON));
						break;
					}
				}
			}
		}
	}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		drawBackground(batch);
		drawFont(batch);
	}
	
	@Override
	protected void drawFont(SmartSpriteBatch batch) {
		super.drawFont(batch);

		Color color = getFontColor();

		String accumulated = "";
		for(int i = 0; i != words.size(); i++) {
			String word = words.get(i);

			accumulated = accumulated + word;
			int max = getInputX() + getPadding() + getFont().getWidth(accumulated);

			if(wildcards.containsKey(i)) {
				Wildcard wildcard = wildcards.get(i);
				Color currentColor = wildcard.getColor();

				getFont().setColor(currentColor);
				getFont().draw(batch, word, max - getFont().getWidth(word), getInputY() + getPadding());
			} else {
				getFont().setColor(color);
				getFont().draw(batch, word, max - getFont().getWidth(word), getInputY() + getPadding());
			}
		}
	}
}
