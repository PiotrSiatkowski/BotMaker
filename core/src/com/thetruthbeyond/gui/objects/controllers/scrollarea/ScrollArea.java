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

/*
 * BotMaker - file created and updated by Piotr Siatkowski (2015).
 */

package com.thetruthbeyond.gui.objects.controllers.scrollarea;

import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.emitters.OnChooseOption;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.ScrollAreaInputType;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBar;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Message;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;
import com.thetruthbeyond.gui.utility.drawing.fonts.FontPool;
import com.thetruthbeyond.gui.utility.drawing.fonts.SmartFont;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import com.thetruthbeyond.gui.utility.gl.BUFFER_NUMBER;
import com.thetruthbeyond.gui.utility.gl.Buffers;
import org.jetbrains.annotations.Nullable;

public class ScrollArea extends BackgroundArea {
	
	protected SmartTexture messageRed;
	protected SmartTexture messageGrey;
	
	protected List<Message> messages = new LinkedList<>();
	protected List<Boolean> messagesBackground = new ArrayList<>(8);
	protected boolean isCurrentColorRed = true;
	
	protected Area textArea;
	protected int areaPadding;
	protected int textPadding;
	
	protected int MAX_MESSAGES_SHOWN;
	
	protected int messageH;
	protected int screenPointer;
	
	protected FrameBuffer buffer;
	protected Texture bufferTexture;
	
	protected ScrollBar scrollBar;
	
	protected SmartFont font;
	
	protected float backgroundAlpha;
	protected float textBackgroundAlpha;

	private final Color fontColor;

	private TextureRegion region;

	@Nullable
	protected Position pressedPosition;
	
	protected int lines;
	
	protected boolean isMissingPixelFillNeeded = false;
	
	private static final float ADDITIONAL_NO_BORDER_RELATIVE_TEXT_PADDING = 1.20f;
	
	public ScrollArea(ScrollAreaConfiguration configuration, FileManager loader, Clickable parent) {
		super(configuration, loader, parent);

		messageRed = loader.getTexture("MessageRed");
		messageGrey = loader.getTexture("MessageGrey");
		
		areaPadding = (int)(configuration.relativeAreaPadding * Math.min(area.getW(), area.getH()));
		
		MAX_MESSAGES_SHOWN = configuration.maxMessagesShown;
		
		// Eliminate too short or too long padding.
		while((configuration.area.getH() - 2 * areaPadding - 2 * BORDER_SIZE) % MAX_MESSAGES_SHOWN > 1)
			areaPadding += 1;
		
		// Estimate if there is need to put one pixel stripe filling area.
		if((configuration.area.getH() - 2 * areaPadding) % MAX_MESSAGES_SHOWN != 0)
			isMissingPixelFillNeeded = true;
		
		textArea = new Area(area);
		textArea.cutArea(areaPadding + BORDER_SIZE);
			
		messageH 		= (int)(textArea.getH() / (float) MAX_MESSAGES_SHOWN);
		textPadding		= (int)(messageH * configuration.relativeTextPadding * ADDITIONAL_NO_BORDER_RELATIVE_TEXT_PADDING);
		int fontSize 	= messageH - 2 * textPadding;
			
		font 		= FontPool.createFont(configuration.fontname, fontSize);
		fontColor 	= configuration.fontcolor;

		buffer 		= Buffers.getBuffer(BUFFER_NUMBER.ONE);

		if((scrollBar = configuration.scrollbar) != null)
			getObserver().observeEmitter(scrollBar.getEmitter(OnChooseOption.Id));
		
		backgroundAlpha 	= configuration.backgroundAlpha;
		textBackgroundAlpha = configuration.textBackgroundAlpha;
		
		// Better text alignment.
		textPadding -= Math.round(font.getDescentHeight() / 2.0f);
	}

	public void addMessage(String message) {
		addMessage(message, ScrollAreaInputType.OPPOSITE_USER);
	}

	public void addMessage(String message, ScrollAreaInputType type) {
		messages.add(0, new Message(message, font, textArea.getW() - 2 * textPadding));

		switch(type) {
			case OPPOSITE_USER: { isCurrentColorRed = !isCurrentColorRed; break; }
			case SAME_USER: { break; }
			case USER_BLUE: { isCurrentColorRed = false; break; }
			case USER_RED: { isCurrentColorRed = true; break; }
		}

		messagesBackground.add(0, isCurrentColorRed);

		lines += messages.get(0).getLinesNumber();

		if(scrollBar != null)
			restartScrollBar();
	}
	
	public void removeMessage(String original) {
		for(Message message : messages) {
			if(message.getOriginal().equals(original)) {
				lines -= message.getLinesNumber();
				messages.remove(message);
				return;
			}
		}
		
		if(scrollBar != null)
			restartScrollBar();
	}
	
	public boolean hasMessage(String original) {
		for(Message message : messages) {
			if(message.getOriginal().equals(original))
				return true;
		}
		
		return false;
	}
	
	public void clear() {
		messages.clear();
		screenPointer = 0;
	}

	private void restartScrollBar() {
		if(scrollBar != null) {
			if(lines <= MAX_MESSAGES_SHOWN)
				scrollBar.setBarNumbers(0, lines, 0);
			else
				scrollBar.setBarNumbers(lines - MAX_MESSAGES_SHOWN, MAX_MESSAGES_SHOWN, 0);
		}
		
		screenPointer = 0;	
	}

	private void updateScrollBar() {
		screenPointer = Math.max(screenPointer, 0);
		screenPointer = Math.min(screenPointer, lines * messageH - textArea.getH());

		scrollBar.setBarNumbers(lines * messageH - screenPointer - textArea.getH(), textArea.getH(), screenPointer);
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(emitterID == OnChooseOption.Id) {

			OnChooseOption onChooseOption = object.getEmitter(emitterID);
			int rest = screenPointer % messageH;

			switch(onChooseOption.getChosenOption()) {

				case OPTION_A: {
					float scrollPosition = 1 - scrollBar.getPercentagePosition();
					screenPointer = (int) (scrollPosition * (lines - MAX_MESSAGES_SHOWN) * messageH);
					updateScrollBar(); break;
				}

				case OPTION_B: {
					if(rest < messageH / 2)
						screenPointer += (messageH - rest);
					else
						screenPointer = screenPointer + (messageH - rest) + messageH;
					updateScrollBar(); break;
				}

				case OPTION_C: {
					if(rest < messageH / 2)
						screenPointer -= (messageH - rest);
					else
						screenPointer = screenPointer - (messageH - rest) - messageH;
					updateScrollBar(); break;
				}
			}
		}
	}

	@Override
	public boolean contains(Position position) {
		return !(position.x < textArea.getX() || position.x > textArea.getX() + textArea.getW() ||
				 position.y < textArea.getY() || position.y > textArea.getY() + textArea.getH());
	}

	@Override
	public boolean executeMouseHover(Position position) {
		if(scrollBar != null)
			return scrollBar.executeMouseHover(position);
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		if(lines > MAX_MESSAGES_SHOWN) {
			if(scrollBar != null && scrollBar.executeMouseDrag(position)) {
				float scrollPosition = 1 - scrollBar.getPercentagePosition();
				screenPointer = (int)(scrollPosition * (lines - MAX_MESSAGES_SHOWN) * messageH);
				return true;
			} else {
				if(pressedPosition != null) {
					int dy = pressedPosition.y - position.y;
					screenPointer -= dy;
					
					pressedPosition = new Position(position);
					updateScrollBar();
					return true;
				} else return false;
			}
		} else return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(lines > MAX_MESSAGES_SHOWN) {
			
			if(scrollBar != null && scrollBar.executeMousePress(position))
				return true;

			if(contains(position))
				pressedPosition = new Position(position);
			return false;
		} else
			return false;
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		if(scrollBar != null)
			scrollBar.executeMouseRelease(position);
		pressedPosition = null;
		return true;
	}

	@Override
	public void update(float delta) {
		if(scrollBar != null)
			scrollBar.update(delta);
	}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		Color color = batch.getColor();
		drawBackground(batch);
		
		batch.end();
			drawObjectsToBuffer(batch);
		batch.begin();
			drawBuffer(batch);
		batch.setColor(color);
		
		drawScrollBar(batch);
	}
	
	public void drawBackground(SmartSpriteBatch batch) {
		super.draw(batch);
	}
	
	public void drawObjectsToBuffer(SmartSpriteBatch batch) {
		Color color = batch.getColor();

		buffer.begin();
		batch.begin();
		
			Gdx.gl20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
			int messageYScreen = textArea.getY() + textArea.getH() + screenPointer;
			for(int i = 0; i != messages.size(); i++) {
				int potentialMessageYScreen = messageYScreen - messages.get(i).getLinesNumber() * messageH;
				if(potentialMessageYScreen > textArea.getY() + textArea.getH()) {
					messageYScreen = potentialMessageYScreen;
					continue;
				}

				if(messageYScreen < textArea.getY() - messages.get(i).getLinesNumber() * messageH)
					break;
				
				Message message = messages.get(i);
				for(int j = message.getLinesNumber() - 1; j >= 0 ; j--) {
					batch.setColor(1.0f, 1.0f, 1.0f, color.a * textBackgroundAlpha);
					
					messageYScreen -= messageH;
					
					if(i == messages.size() - 1 && isMissingPixelFillNeeded) {
						//Drawing one height pixel line to improve overall appearance.
						if(messagesBackground.get(i))
							batch.draw(messageRed, textArea.getX(), messageYScreen - 1, textArea.getW(), messageH + 1);	
						else
							batch.draw(messageGrey, textArea.getX(), messageYScreen - 1, textArea.getW(), messageH + 1);
					} else {
						if(messagesBackground.get(i))
							batch.draw(messageRed, textArea.getX(), messageYScreen, textArea.getW(), messageH);	
						else
							batch.draw(messageGrey, textArea.getX(), messageYScreen, textArea.getW(), messageH);
					}
						
					batch.setColor(color);

					font.setColor(fontColor);
					font.draw(batch, message.getLine(j), textArea.getX() + textPadding, messageYScreen + textPadding);
				}
			}
			
		batch.end();
		buffer.end();
	}
	
	public void drawBuffer(SmartSpriteBatch batch) {	
		bufferTexture = buffer.getColorBufferTexture();

		if(region == null) {
			region = new TextureRegion(bufferTexture, textArea.getX(), Consts.SCREEN_H - (textArea.getY() + textArea.getH()),
					                                  textArea.getW(), textArea.getH());
			region.flip(false, true);
		} else
			region.setTexture(bufferTexture);

		batch.draw(region, textArea.getX(), textArea.getY(), textArea.getW(), textArea.getH());
	}
	
	public void drawScrollBar(SmartSpriteBatch batch) {
		if(scrollBar != null && lines > MAX_MESSAGES_SHOWN)
			scrollBar.draw(batch);	
	}
}
