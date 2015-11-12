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
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

import com.badlogic.gdx.Input.Keys;

public class AutofillTextField extends AutofillAllTextField {

	private int wordIndex;
	private Keyboard keyboard = Keyboard.getInstance();

	public AutofillTextField(TextField parent) {
		super(parent);
	}
	
	@Override
	protected void setAutofillList() {
		if(!getInput().isEmpty()) {
			String input = getInput();
			int lastSpaceIndex = input.lastIndexOf(' ');
			
			String lastWord;
			if(lastSpaceIndex != -1)
				lastWord = input.substring(lastSpaceIndex + 1, input.length());
			else
				lastWord = input.substring(0, input.length());

			if(!lastWord.isEmpty()) {
				wordIndex = lastWord.length();
			
				trieIndex = 0;
				autofill = tries.get(lastWord);	
			} else
				autofill = null;
		} else
			autofill = null;
	}

	@Override
	public void update(float delta) {
		if(isFocused()) {
			handleUpAndDown();

			if(keyboard.isKeyDown(Keys.RIGHT)) {
				if(autofill != null && !autofill.isEmpty()) {
					appendToInput(autofill.get(trieIndex).substring(wordIndex));
					keyboard.dismissKey(Keys.RIGHT);
				}

				autofill = null;
				trieIndex = 0;
			}
		}

		getDecoratorParent().update(delta);

		if(isFocused()) {
			if(getInput().isEmpty()) {
				autofill = null;
				trieIndex = 0;
			} else if(hasChanged())
				setAutofillList();
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
			String word = autofill.get(trieIndex);
			String fill = word.substring(wordIndex, word.length());

			int max = getInputX() + getPadding() + getFont().getWidth(getInput() + fill);

			getFont().setColor(Consts.GREY_FONT_COLOR);
			getFont().draw(batch, fill, max - getFont().getWidth(fill), getInputY() + getPadding());
		}

		drawDecoratorParentFont(batch);
	}
}
