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

import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Message;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;
import com.thetruthbeyond.gui.utility.gl.GlUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

public class ChoosableScrollArea extends ScrollArea {

	private static final float MARK_ALPHA = 0.45f;

	private final boolean IS_BLANK_CHOSE_ENABLED;
	
	private final Color FONT_COLOR;
	private final Color LIGHT_COLOR;
	
	private final Sound CHOSE_SOUND;

	private int messageChosen = -1;

	private Area lineArea = new Area();

	public ChoosableScrollArea(ChoosableScrollAreaConfiguration configuration, FileManager loader, Clickable parent) {
		super(configuration, loader, parent);

		IS_BLANK_CHOSE_ENABLED = configuration.isBlankChooseEnabled;

		FONT_COLOR = configuration.fontcolor;
		LIGHT_COLOR = new Color(FONT_COLOR).mul(1.40f);
		
		CHOSE_SOUND = loader.getSound("ButtonHover2");
	}

	public String getSelected() {
		if(messageChosen == -1)
			return "";
		else
			return messages.get(messageChosen).getOriginal();
	}
	
	@Override
	public void addMessage(String message) {
		super.addMessage(message);
		
		if(!IS_BLANK_CHOSE_ENABLED)
			messageChosen = messages.size() - 1;
	}

	@Override
	public void removeMessage(String original) {
		boolean isSelected = getSelected().equals(original);
		super.removeMessage(original);
		
		if(isSelected) {
			if(!messages.isEmpty()) {
				if(messageChosen - 1 < 0)
					messageChosen = messages.size() - 1;
				else
					messageChosen = messageChosen - 1;
			} else
				messageChosen = -1;
		}	
	}
	
	public void setSelected(String original) {
		for(int i = 0; i != messages.size(); i++) {
			Message message = messages.get(i);
			if(message.getOriginal().equals(original)) {	
				messageChosen = i;
				return;
			}
		}
		
		if(IS_BLANK_CHOSE_ENABLED)
			messageChosen = -1;
	}
	
	public void changeSelected(String subtitle) {
		if(messageChosen != -1) {
			messages.remove(messageChosen);
			messages.add(messageChosen, new Message(subtitle, font, textArea.getW() - 2 * textPadding));
		}
	}
	
	@Override
	public boolean executeMouseDrag(Position position) {
		if(lines > MAX_MESSAGES_SHOWN) {
			if(scrollBar != null && scrollBar.executeMouseDrag(position)) {
				float scrollPosition = 1 - scrollBar.getPercentagePosition();
				screenPointer = (int)(scrollPosition * (lines - MAX_MESSAGES_SHOWN) * messageH);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean executeMousePress(Position position) {
		if(lines > MAX_MESSAGES_SHOWN) {
			if(scrollBar != null && scrollBar.executeMousePress(position)) {
				float scrollPosition = 1 - scrollBar.getPercentagePosition();
				screenPointer = (int)(scrollPosition * (lines - MAX_MESSAGES_SHOWN) * messageH);
				return true;
			}
		}
		
		if(contains(position)) {
			int screenDy = textArea.getY() + textArea.getH() - position.y;
			int message = (int)((screenPointer + screenDy) / (float) messageH);

			if(message == messageChosen)
				return false;
			else {
				if(message >= messages.size()) {
					if(IS_BLANK_CHOSE_ENABLED)
						messageChosen = -1;
					return false;
				} else {
					messageChosen = message;
					CHOSE_SOUND.play();
					return true;
				}
			}
		} else return false;
	}
	
	@Override
	public void drawObjectsToBuffer(SmartSpriteBatch batch) {
		Color color = batch.getColor();
		
		buffer.begin();
		batch.begin();
		
			Gdx.gl20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
			int messageYScreen = textArea.getY() + textArea.getH() + screenPointer;
			
			SmartTexture background;
			for(int i = 0; i != messages.size(); i++) {
				int potentialMessageYScreen = messageYScreen - messages.get(i).getLinesNumber() * messageH;
				if(potentialMessageYScreen > textArea.getY() + textArea.getH()) {
					messageYScreen = potentialMessageYScreen;
					continue;
				}

				if(messageYScreen < textArea.getY() - messages.get(i).getLinesNumber() * messageH)
					break;
				
				Message message = messages.get(i);
				for(int j = 0; j != message.getLinesNumber(); j++) {
					if(i == messageChosen) {
						batch.setColor(MARK_ALPHA, MARK_ALPHA, MARK_ALPHA, color.a * textBackgroundAlpha);
						font.setColor(LIGHT_COLOR);
					} else {
						batch.setColor(1.0f, 1.0f, 1.0f, color.a * textBackgroundAlpha);
						font.setColor(FONT_COLOR);
					}
					
					messageYScreen -= messageH;
					
					background = messagesBackground.get(i) ? messageRed : messageGrey;
					int height = messageH;
					if(i == messages.size() - 1 && isMissingPixelFillNeeded)
						height += 1;
	
					batch.draw(background, textArea.getX(), messageYScreen, textArea.getW(), height);	
					font.draw(batch, message.getLine(j), textArea.getX() + textPadding, messageYScreen + textPadding);
					batch.flush();

					lineArea.setX(textArea.getX());
					lineArea.setY(messageYScreen);
					lineArea.setW(textArea.getW());
					lineArea.setH(height);
					GlUtils.clearAlpha(lineArea);
				}
			}
			
			batch.setColor(color);
		batch.end();
		buffer.end();
	}
}
