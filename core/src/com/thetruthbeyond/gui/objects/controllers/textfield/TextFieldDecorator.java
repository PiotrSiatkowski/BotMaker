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

package com.thetruthbeyond.gui.objects.controllers.textfield;

import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.Observer;
import com.thetruthbeyond.gui.interfaces.Decorator;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.fonts.SmartFont;

import com.badlogic.gdx.graphics.Color;

public abstract class TextFieldDecorator extends TextField implements Decorator {

	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	private final TextField parent;
	
	public TextFieldDecorator(TextField parent) {
		this.parent = parent;
	}

	@Override
	public Clickable getRoot() {
		TextField field = getDecoratorParent();
		while(true) {
			if(field.getDecoratorParent() == null)
				return field.getParent();
			else
				field = field.getDecoratorParent();
		}
	}

	@Override
	public TextField getDecoratorParent() {
		return parent;
	}
	
	@Override
	public void setParent(Clickable parent) {
		this.parent.setParent(parent);
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		parent.reactToEmittedSignal(object, emitterID);
	}

	@Override
	public <T extends Emitter> Observer<T> getSpecializedObserver(int emitterID) {
		return parent.getSpecializedObserver(emitterID);
	}

	@Override
	public void addEmitter(Emitter emitter) {
		parent.addEmitter(emitter);
	}

	@Override
	public <T extends Emitter> T getEmitter(int emitterID) {
		return parent.getEmitter(emitterID);
	}

	@Override
	protected int getInputX() {
		return parent.getInputX();
	}
	
	@Override
	protected int getInputY() {
		return parent.getInputY();
	}
	
	@Override
	protected int getInputW() {
		return parent.getInputW();
	}
	
	@Override
	protected int getInputH() {
		return parent.getInputH();
	}
	
	@Override
	protected boolean hasChanged() {
		return parent.hasChanged();
	}
	
	@Override
	protected boolean hasReachedFinish() {
		return parent.hasReachedFinish();
	}
	
	@Override
	protected void changeW(float w) { 
		parent.changeW(w);
	}
	
	@Override
	protected void changeH(float h) { 
		parent.changeH(h);
	}

	@Override
	protected String getCharacters() {
		return parent.getCharacters();
	}

	@Override
	public boolean isFBOHinted() {
		return parent.isFBOHinted();
	}

	@Override
	protected SmartFont getFont() {
		return parent.getFont();
	}

	@Override
	protected Color getFontColor() {
		return parent.getFontColor();
	}

	@Override
	protected int getPadding() {
		return parent.getPadding();
	}
	
	@Override
	protected int getLineW() {
		return parent.getLineW();
	}
	
	@Override
	protected int getBorderSize() {
		return parent.getBorderSize();
	}
	
	@Override
	public String getInput() {
		return parent.getInput();
	}
	
	@Override
	public int getX() {
		return parent.getX();
	}
	
	@Override
	public int getY() {
		return parent.getY();
	}
	
	@Override
	public int getW() {
		return parent.getW();
	}
	
	@Override
	public int getH() {
		return parent.getH();
	}
	
	@Override
	public Area getArea() {
		return parent.getArea();
	}

	@Override
	public void changeX(float x) {
		parent.changeX(x);
	}
	
	@Override
	public void changeY(float y) {
		parent.changeY(y);
	}

	@Override
	public void hintFBODrawing() {
		parent.hintFBODrawing();
	}

	@Override
	public void setFocus(boolean focus) {
		parent.setFocus(focus);
	}
	
	@Override
	public boolean isFocused() {
		return parent.isFocused();
	}
	
	@Override
	public void appendToInput(String message) {
		parent.appendToInput(message);
	}
	
	@Override
	public void setInput(String message) {
		parent.setInput(message);
	}
	
	@Override
	public boolean contains(Position position) {
		return parent.contains(position);
	}

	@Override
	public boolean executeMouseHover(Position position) {
		return parent.executeMouseHover(position);
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		return parent.executeMouseDrag(position);
	}

	@Override
	public boolean executeMousePress(Position position) {
		return parent.executeMousePress(position);
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		return parent.executeMouseRelease(position);
	}

	@Override
	public boolean executeMousePressRight(Position position) {
		return parent.executeMousePressRight(position);
	}

	public <T extends MergeDecorator> MergeDecorator findMergeDecorator(Class<T> type) {
		// Find decorator that this class can merge with.
		TextField decorator = getDecoratorParent();
		while(decorator != null) {
			if(type.isAssignableFrom(decorator.getClass()))
				return decorator;

			decorator = decorator.getDecoratorParent();
		}

		new Logger().writeMessage("Warning", "Find merge decorator method has not found any object as probably intended.");
		return null;
	}

	@Override
	public Clickable getParent() {
		return parent.getParent();
	}
	
	@Override
	public void update(float delta) { 
		parent.update(delta);
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		parent.draw(batch);
	}

	@Override
	protected void drawBackground(SmartSpriteBatch batch) {
		parent.drawBackground(batch);
	}

	protected final void drawDecoratorParentBackground(SmartSpriteBatch batch) {
		parent.drawBackground(batch);
	}

	@Override
	protected void drawFont(SmartSpriteBatch batch) {
		parent.drawFont(batch);
	}

	protected final void drawDecoratorParentFont(SmartSpriteBatch batch) {
		parent.drawFont(batch);
	}
}
