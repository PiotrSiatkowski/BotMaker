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

import com.thetruthbeyond.gui.enums.Align;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldDecorator;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.fonts.FontPool;
import com.thetruthbeyond.gui.utility.drawing.fonts.SmartFont;

import com.badlogic.gdx.graphics.Color;

public class LabeledTextField extends TextFieldDecorator {

	private final SmartFont labelFont;
	private String labelTitle = "";
	
	private final int fontSize;
	
	private int titleXScreen;
	private int titleYScreen;
	
	private final Align align;
	
	private final float relativePaddingX;
	private final float relativePaddingY;

	private Color labelColor;

	public LabeledTextField(TextField parent, LabeledTextFieldConfiguration configuration) {
		super(parent);
		
		fontSize = (int)(getH() * configuration.labelSizeParameter);
		labelFont = FontPool.createFont(configuration.labelFontname, fontSize);

		labelColor = new Color(configuration.labelFontColor);

		labelTitle = configuration.labelTitle;
		
		relativePaddingX = configuration.relativeLabelPaddingX;
		relativePaddingY = configuration.relativeLabelPaddingY;
		
		align = configuration.align;
		actualizePosition(align);
	}
	
	private void actualizePosition(Align align) {
		switch(align) {
			case TopLeft:
				titleXScreen = getX() + (int)(getW() * relativePaddingX);
				titleYScreen = getY() + (int)(getH() * relativePaddingY - fontSize);
				break;

		
			case LeftCenter:
				titleXScreen = getX() + (int)(getW() * relativePaddingX - labelFont.getWidth(labelTitle));
				titleYScreen = getY() + (getH() - labelFont.getHeight(labelTitle)) / 2 + (int)(getH() * relativePaddingX);
				break;
		
			default: break;
		}
	}

	@Override
	public void changeX(float x) { 
		super.changeX(x);
		actualizePosition(align);
	}
	
	@Override
	public void changeY(float y) {
		super.changeY(y);
		actualizePosition(align);
	}

	@Override
	public void changeW(float w) {
		super.changeW(w);
		actualizePosition(align);
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		super.draw(batch);
		drawLabel(batch);
	}
	
	@Override
	public void drawBackground(SmartSpriteBatch batch) {
		super.drawBackground(batch);
		drawLabel(batch);
	}

	private void drawLabel(SmartSpriteBatch batch) {
		// Change label alpha part in order to draw it correctly as the main text field is drawn.
		labelColor.a = batch.getColor().a;

		labelFont.setColor(labelColor);
		labelFont.draw(batch, labelTitle, titleXScreen, titleYScreen);
	}
}
