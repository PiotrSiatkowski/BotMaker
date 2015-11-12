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

import com.thetruthbeyond.gui.action.emitters.OnChangeState;
import com.thetruthbeyond.gui.action.emitters.OnFocus;
import com.thetruthbeyond.gui.action.emitters.OnReachLimit;
import com.thetruthbeyond.gui.action.features.Focusable;
import com.thetruthbeyond.gui.input.Keyboard;
import com.thetruthbeyond.gui.interfaces.FBODrawable;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.WildcardTextField;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;
import com.thetruthbeyond.gui.utility.drawing.fonts.FontPool;
import com.thetruthbeyond.gui.utility.drawing.fonts.SmartFont;
import com.thetruthbeyond.gui.utility.gl.GlUtils;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

public class TextField extends Clickable implements Focusable, MergeDecorator, FBODrawable {

	// Event emitters.
	private OnFocus onFocus;
	private OnChangeState onChange;
	private OnReachLimit onReachLimit;

	// Textures to draw.
	private SmartTexture textureOn;
	private SmartTexture textureOff;
	private SmartTexture border;

	private final StringBuilder input = new StringBuilder(40);
	private SmartFont font;
	
	private Keyboard keyboard = Keyboard.getInstance();
	private Area inputArea;
	private Color fontColor;
	private String characters;

	// Various flags.
	private boolean blockFontDrawing = false;
	private boolean hintFBO = false;

	// Appearance details.
	private int lineWidth;
	private int padding;
	private int bordersize;

	// Erasing signs.
	private static final float BASIC_BACKSPACE_WAIT_TIME = 0.16f;
	private static final float BACKSPACE_WAIT_TIME_PARAMETER = 0.7f;
	private float BACKSPACE_WAIT_TIME = 0.16f;
	private float elapsedTime;
	
	// Handling input activation.
	private static final float ACTIVATION_TIME = 0.22f;
	private float activationTime = ACTIVATION_TIME;
	
	// For decorating purposes.
	//////////////////////////////////////////////////////////////////////////////////////////////
	protected TextField() {
		super(null);
	}

	public TextField getDecoratorParent() {
		return null;
	}

	protected SmartFont getFont() {
		return font; 
	}

	protected Color getFontColor() {
		return fontColor;
	}

	protected int getPadding() {
		return padding;
	}

	protected int getInputX() {
		return inputArea.getX();
	}

	protected int getInputY() {
		return inputArea.getY();
	}

	protected int getInputW() {
		return inputArea.getW();
	}

	protected int getInputH() {
		return inputArea.getH();
	}

	protected int getLineW() {
		return lineWidth;
	}

	protected int getBorderSize() {
		return bordersize;
	}

	protected boolean hasChanged() {
		return onChange.hasChanged();
	}

	protected boolean hasReachedOrigin() {
		return onReachLimit.hasReachedOrigin();
	}

	protected boolean hasReachedFinish() {
		return onReachLimit.hasReachedFinish();
	}

	protected void changeW(float w) {
		area.setW(w);
		inputArea.setW(w - 2 * bordersize);
	}

	protected void changeH(float h) {
		area.setH(h);
		inputArea.setH(h - 2 * bordersize);
	}

	protected String getCharacters() {
		return characters;
	}

	protected boolean isFBOHinted() {
		return hintFBO;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	
	public TextField(TextFieldConfiguration configuration, FileManager loader, Clickable parent) {
		super(configuration.area, parent);
		
		onFocus = new OnFocus(this);
		addEmitter(onFocus);

		onChange = new OnChangeState(this);
		addEmitter(onChange);

		onReachLimit = new OnReachLimit(this);
		addEmitter(onReachLimit);
		
		textureOn 	= loader.getTexture("TextFieldOn");
		textureOff 	= loader.getTexture("TextFieldOff");
		border 		= loader.getTexture("Border");
		
		padding	= Math.round(area.getH() * configuration.relativeTextPadding);
		
		bordersize 	= configuration.border;
		
		inputArea = new Area(configuration.area);
		inputArea.cutArea(bordersize);
		
		int size = inputArea.getH() - Math.round(2 * area.getH() * configuration.relativeTextPadding);
		font = FontPool.createFont(configuration.fontname, size);
		fontColor = configuration.fontcolor;
		
		// Better text alignment.
		padding -= Math.round(font.getDescentHeight() / 2.0f);

		characters = configuration.characters;
	}

	@Override
	public void setParent(Clickable parent) {
		super.setParent(parent);
	}

	@Override
	public void changeX(float x) { 
		area.setX(x);
		inputArea.setX(x + bordersize);
	}

	@Override
	public void changeY(float y) { 
		area.setY(y);
		inputArea.setY(y + bordersize);
	}

	@Override
	public void hintFBODrawing() {
		hintFBO = true;
	}

	@Override
	public void setFocus(boolean focus) {
		if(focus == !onFocus.isFocused()) {
			activationTime = 0.0f;
			BACKSPACE_WAIT_TIME = BASIC_BACKSPACE_WAIT_TIME;
		}

		onFocus.setFocus(focus);
		if(focus) {
			keyboard.clearEvents();
			keyboard.setFilter(characters);
		}
	}

	@Override
	public boolean isFocused() {
		return onFocus.isFocused();
	}

	public void appendToInput(String message) {
		if(!message.isEmpty()) {
			onReachLimit.setReachedOrigin(false);
			onReachLimit.setReachedFinish(false);

			lineWidth = font.getWidth(input.toString());
			for(int i = 0; i != message.length(); i++) {
				Character character = message.charAt(i);
				if(lineWidth < area.getW() - 2 * padding - font.getWidth(character.toString())) {
					input.append(character);
					lineWidth = font.getWidth(input.toString());
				} else {
					onReachLimit.setReachedFinish(true);
					break;
				}
			}

			onChange.setChanged(true);
		}
	}
	
	public void setInput(String message) {
		String old = input.toString();
		if(!old.equals(message)) {
			input.delete(0, input.length()); lineWidth = 0;

			if(message.isEmpty()) {
				onReachLimit.setReachedOrigin(true);
				onChange.setChanged(true);
			} else {
				onReachLimit.setReachedOrigin(false);
				onReachLimit.setReachedFinish(false);

				for(int i = 0; i != message.length(); i++) {
					Character character = message.charAt(i);
					if(lineWidth < area.getW() - 2 * padding - font.getWidth(character.toString())) {
						input.append(character);
						lineWidth = font.getWidth(input.toString());
					} else {
						onReachLimit.setReachedFinish(true);
						break;
					}
				}

				onChange.setChanged(true);
			}
		}
	}
	
	public String getInput() { 
		return input.toString(); 
	}
	
	@Override
	public boolean contains(Position position) {
		return !(position.x < inputArea.getX() || position.x > inputArea.getX() + inputArea.getW() ||
				 position.y < inputArea.getY() || position.y > inputArea.getY() + inputArea.getH());
	}

	@Override @UnhandledMethod
	public boolean executeMouseHover(Position position) {
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseDrag(Position position) {
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(contains(position)) {
			if(!onFocus.isFocused())
				setFocus(true);
		} else {
			if(onFocus.isFocused())
				setFocus(false);
		}
		
		return onFocus.isFocused();
	}

	@Override @UnhandledMethod
	public boolean executeMouseRelease(Position position) {
		return false;
	}

	@Override
	public void update(float delta) {
		if(activationTime < ACTIVATION_TIME)
			activationTime = Math.min(ACTIVATION_TIME, activationTime + delta);
		
		if(!onFocus.isFocused())
			return;
		
		if(elapsedTime < BACKSPACE_WAIT_TIME)
			elapsedTime = Math.min(elapsedTime + delta, BACKSPACE_WAIT_TIME);
		
		if(keyboard.isCharacterWaiting()) {
			Character character = keyboard.getCharacter();
			if(!onReachLimit.hasReachedFinish()) {
				if(lineWidth < inputArea.getW() - 2 * padding - font.getWidth(character.toString())) {
					input.append(character);
					lineWidth = font.getWidth(input.toString());

					onReachLimit.setReachedOrigin(false);
					onChange.setChanged(true);
				} else
					onReachLimit.setReachedFinish(true);
			}
		}
					
		if(keyboard.isKeyDown(Keys.BACKSPACE)) {
			if(input.length() > 0 && elapsedTime == BACKSPACE_WAIT_TIME) {
				input.delete(input.length() - 1, input.length());
				lineWidth = font.getWidth(input.toString());

				onChange.setChanged(true);

				if(input.length() == 0)
					onReachLimit.setReachedOrigin(true);
				else
					onReachLimit.setReachedFinish(false);

				BACKSPACE_WAIT_TIME *= BACKSPACE_WAIT_TIME_PARAMETER;
				elapsedTime = 0.0f;
			}
		} else
			BACKSPACE_WAIT_TIME = BASIC_BACKSPACE_WAIT_TIME;
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		drawBackground(batch);
		drawFont(batch);
	}
	
	/** For decoration convenience. */
	protected void drawBackground(SmartSpriteBatch batch) {
		Color previousColor = batch.getColor();
		
		GlUtils.setIgnoreBackgroundBlendMode(batch);
		
		if(activationTime == ACTIVATION_TIME) {
			if(onFocus.isFocused())
				batch.draw(textureOn, area.getX(), area.getY(), area.getW(), area.getH());
			else
				batch.draw(textureOff, area.getX(), area.getY(), area.getW(), area.getH());
			GlUtils.setDefaultBlendMode(batch);
		} else {
			if(onFocus.isFocused()) {
				batch.draw(textureOff, area.getX(), area.getY(), area.getW(), area.getH());

				GlUtils.setDefaultBlendMode(batch);
				batch.setColor(1.0f, 1.0f, 1.0f, (activationTime / ACTIVATION_TIME) * previousColor.a);
					batch.draw(textureOn, area.getX(), area.getY(), area.getW(), area.getH());
				if(hintFBO)
					batch.flush();
			} else {
				batch.draw(textureOn, area.getX(), area.getY(), area.getW(), area.getH());
				
				GlUtils.setDefaultBlendMode(batch);
				batch.setColor(1.0f, 1.0f, 1.0f, (activationTime / ACTIVATION_TIME) * previousColor.a);
					batch.draw(textureOff, area.getX(), area.getY(), area.getW(), area.getH());
				if(hintFBO)
					batch.flush();
			}			
		}
		
		batch.setColor(previousColor);
		
		// Drawing border
		batch.draw(border, area.getX(), area.getY(), area.getW(), bordersize);
		batch.draw(border, area.getX(), area.getY(), bordersize, area.getH());
		batch.draw(border, area.getX(), area.getY() + area.getH() - bordersize, area.getW(), bordersize);
		batch.draw(border, area.getX() + area.getW() - bordersize, area.getY(), bordersize, area.getH());

		if(hintFBO)
			GlUtils.setAlpha(area, previousColor.a);
		
		// When field is drawn we can flag it as unchanged.
		onChange.setChanged(false); hintFBO = false;
	}
	
	/** For decoration convenience. */
	protected void drawFont(SmartSpriteBatch batch) {
		if(!blockFontDrawing) {
			font.setColor(fontColor);
			font.draw(batch, input.toString(), inputArea.getX() + padding, inputArea.getY() + padding);
		}
	}

	@Override
	public <T extends TextFieldDecorator> void obtainMergeInfo(Object object, Class<T> type) {
		if(type.equals(WildcardTextField.class))
			blockFontDrawing = true;
	}
}
