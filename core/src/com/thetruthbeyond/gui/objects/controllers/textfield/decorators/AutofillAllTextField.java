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

import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.input.Keyboard;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldDecorator;
import com.thetruthbeyond.gui.structures.tries.Tries;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Input.Keys;

import org.jetbrains.annotations.Nullable;

public class AutofillAllTextField extends TextFieldDecorator {

	protected Tries tries;
	protected int trieIndex;

	@Nullable
	protected List<String> autofill;

	private Keyboard keyboard = Keyboard.getInstance();

	public AutofillAllTextField(TextField parent) {
		super(parent);
		tries = new Tries(getCharacters());
	}
	
	public void addAutofillElement(String element) {
		tries.put(element);
		setAutofillList();
	}
	
	public void setAutofillSet(Set<String> matches) {
		tries = new Tries(getCharacters());
		for(String string : matches)
			tries.put(string);

		setAutofillList();
	}
	
	protected void setAutofillList() {
		String input = getInput();
		if(!input.isEmpty())
			autofill = tries.get(input);	
		else
			autofill = null;
	}

	@Override
	public void appendToInput(String message) {
		super.appendToInput(message);
		if(hasChanged())
			setAutofillList();
	}

	@Override
	public void setInput(String message) {
		super.setInput(message);
		if(hasChanged())
			setAutofillList();
	}

	@Override
	public void update(float delta) {
		if(isFocused()) {
			handleUpAndDown();
			
			if(keyboard.isKeyDown(Keys.RIGHT)) {
				if(autofill != null && !autofill.isEmpty()) {
					setInput(autofill.get(trieIndex));
					keyboard.dismissKey(Keys.RIGHT);
				}

				autofill = null;
				trieIndex = 0;
			}
		}
		
		super.update(delta);
		
		if(isFocused()) {
			if(getInput().isEmpty()) {
				autofill = null;
				trieIndex = 0;
			} else if(hasChanged())
				setAutofillList();
		}
	}

	protected void handleUpAndDown() {
		if(keyboard.isKeyDown(Keys.UP)) {
			if(autofill != null) {
				trieIndex = Math.min(trieIndex + 1, autofill.size() - 1);
				keyboard.dismissKey(Keys.UP);
			}
		}

		if(keyboard.isKeyDown(Keys.DOWN)) {
			if(autofill != null) {
				trieIndex = Math.max(trieIndex - 1, 0);
				keyboard.dismissKey(Keys.DOWN);
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
		if(autofill != null && !autofill.isEmpty()) {
			String expression = autofill.get(trieIndex);
			if(expression != null) {
				int max = getInputX() + getPadding() + getFont().getWidth(expression);

				String cut = expression.substring(getInput().length());

				getFont().setColor(Consts.GREY_FONT_COLOR);
				getFont().draw(batch, cut, max - getFont().getWidth(cut), getInputY() + getPadding());
			}
		}

		super.drawFont(batch);
	}
}