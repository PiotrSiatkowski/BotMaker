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

import com.badlogic.gdx.graphics.Color;

import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldDecorator;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

public class DefaultMessageTextField extends TextFieldDecorator {

	private String defaultMessage = "";
	private boolean isInputEmpty = true;
	
	public DefaultMessageTextField(TextField parent) {
		super(parent);
	}

	public DefaultMessageTextField setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
		
		int lineWidth = getFont().getWidth("");
		for(int i = 0; i != defaultMessage.length(); i++) {
			Character character = defaultMessage.charAt(i);
			if(lineWidth < getW() - 2 * getPadding() - getFont().getWidth(character.toString())) {
				lineWidth = getFont().getWidth(defaultMessage.substring(0, i));
			} else {
				this.defaultMessage = defaultMessage.substring(0, i-1);
				break;
			}
		}
		
		return this;
	}
	
	@Override 
	public void appendToInput(String message) {
		super.appendToInput(message);
		isInputEmpty = getInput().isEmpty();
	}
	
	@Override 
	public void setInput(String message) {
		super.setInput(message);
		isInputEmpty = getInput().isEmpty();
	}
	
	@Override 
	public void update(float delta) {
		super.update(delta);
		
		if(hasChanged())
			isInputEmpty = getInput().isEmpty();
	}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		drawBackground(batch);
		drawFont(batch);
	}
	
	@Override
	protected void drawFont(SmartSpriteBatch batch) {
		if(isInputEmpty) {
			Color grey = Consts.GREY_FONT_COLOR;
			grey.a = batch.getColor().a;
			
			getFont().setColor(grey);
			getFont().draw(batch, defaultMessage, getInputX() + getPadding(), getInputY() + getPadding());

			grey.a = 1.0f;
		} else
			super.drawFont(batch);
	}
}
