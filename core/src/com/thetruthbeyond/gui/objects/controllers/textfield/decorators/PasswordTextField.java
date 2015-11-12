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
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldDecorator;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

public class PasswordTextField extends TextFieldDecorator {

	private String hidden = "";
	
	public PasswordTextField(TextField parent) {
		super(parent);
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		if(hasChanged()) {
			int length = getInput().length();
			if(length == 0)
				hidden = "";
			else
				hidden = hidden.replace(".*", "*");
		}
	}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		drawBackground(batch);
		
		Color olive = Color.OLIVE;
		olive.a = batch.getColor().a;
		
		getFont().setColor(olive);
		getFont().draw(batch, hidden, getInputX() + getPadding(), getInputY() + (int)(1.5 * getPadding()));

		olive.a = 1.0f;
	}
}
