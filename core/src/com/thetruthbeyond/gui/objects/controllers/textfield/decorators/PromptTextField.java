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
import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.Observer;
import com.thetruthbeyond.gui.action.emitters.OnFocus;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldDecorator;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

public class PromptTextField extends TextFieldDecorator {

	private final SmartTexture prompt;
	
	private final float ALPHA;
	private final float BLINK_TIME;
	private float time;
	
	private boolean isPrompted = false;
	
	private final int Y; private final int W; private final int H;
	private final int OFFSET;
	
	public PromptTextField(TextField parent, FileManager loader, PromptTextFieldConfiguration configuration) {
		super(parent);
		
		prompt 		= loader.getTexture("Prompt");
		
		BLINK_TIME 	= configuration.blinktime;
		ALPHA		= configuration.alpha;
		OFFSET 		= configuration.promptoffset;
		
		Y = getInputY() + getBorderSize();
		W = configuration.promptWidth;
		H = getInputH() - 2 * getBorderSize();

		Observer<Emitter> observer = getObserver();
		observer.setObserverOwner(this);
		observer.observeEmitter(getEmitter(OnFocus.Id));
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		super.reactToEmittedSignal(object, emitterID);

		if(emitterID == OnFocus.Id) {
			isPrompted = true;
			time = 0.0f;
		}
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if(isFocused()) {
			if(hasChanged()) {
				isPrompted = true;
			} else {
				time += delta;
				if(time > BLINK_TIME) {
					isPrompted = !isPrompted;
					time = time % BLINK_TIME;
				}
			}
		}
	}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		super.draw(batch);
		
		if(isFocused() && isPrompted && !hasReachedFinish()) {
			batch.setColor(1.0f, 1.0f, 1.0f, ALPHA);
			batch.draw(prompt, getInputX() + getPadding() + getLineW() + OFFSET, Y, W, H);
			batch.setColor(Color.WHITE);
		}
	}
}
