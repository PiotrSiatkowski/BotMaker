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

package com.thetruthbeyond.gui.objects.widgets;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import com.thetruthbeyond.gui.action.emitters.OnAppear;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.interfaces.AppearingElement;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.shareable.DarknessDrawer;
import com.thetruthbeyond.gui.structures.Message;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.fonts.FontPool;
import com.thetruthbeyond.gui.utility.drawing.fonts.SmartFont;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;

public class MessageShower extends Clickable implements AppearingElement {
	
	private static final float TEXT_WIDTH_PARAMETER = 0.8f;
	
	private static final float INTERLINE_PARAMETER = 2.0f;
	private int interline;
	
	private static final float TIME_PER_LETTER_PARAMETER = 0.08f;
	private float TIME = 4.0f;

	private final float APPEAR;
	private float time = TIME;
	
	private final DarknessDrawer darkness;
	private final SmartFont font;
	
	private Message lines;
	
	private final Color fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	
	private int MESSAGE_Y;
	
	private final int Y = Consts.MENU_H + Consts.PANE_H;
	private final int H = Consts.SCREEN_H - Y;
	private final int W = Consts.SCREEN_W;

	private boolean isNeverending = false;
	
	private final Sound messageSound;

	/** OnAppear emitter indicates visible state {increasing or decreasing} rather then actual visibility. */
	private OnAppear onAppear;

	public MessageShower(FontType type, FileManager loader, DarknessDrawer darkness, Clickable parent) {
		super(parent);

		this.darkness = darkness;
		APPEAR = darkness.getShowTime();

		int FONT_HEIGHT = (int)(0.05 * H);
		font = FontPool.createFont(type, FONT_HEIGHT);
		
		messageSound = loader.getSound("Message");

		onAppear = new OnAppear(this);
		addEmitter(onAppear);
	}

	@Override
	public float getVisibilityAlpha() {
		if(time < APPEAR)
			return time / APPEAR;
		else

		if(time > TIME - APPEAR)
			return (TIME - time) / APPEAR;
		else
			return 1;
	}

	public void hide() {
		isNeverending = false;
		lines = null;
		onAppear.setAppearing(false);
	}
	
	public void fade() {
		if(lines != null) {
			if(time < APPEAR)
				time = TIME - time;
			else
				time = TIME - APPEAR;

			onAppear.setAppearing(false);
			isNeverending = false;
		}
	}
	
	public void setNeverending(boolean neverending) {
		isNeverending = neverending;
	}
	
	public void showMessage(String message) {
		messageSound.play();
		if(lines != null && lines.getOriginal().equals(message)) {
			if(time > APPEAR)
				time = APPEAR;
			else {
				onAppear.setAppearing(true); darkness.giveControlTo(this);
			}
		} else {
			lines = new Message(message, font, (int) (W * TEXT_WIDTH_PARAMETER));
			time = 0.0f;

			TIME = TIME_PER_LETTER_PARAMETER * message.length();

			int maxHeight = 0;
			for(int i = 0, n = lines.getLinesNumber(); i != n; i++)
				maxHeight = Math.max(maxHeight, font.getHeight(lines.getLine(i)));
			interline = (int) (INTERLINE_PARAMETER * maxHeight);

			MESSAGE_Y = Y + (H - (lines.getLinesNumber() + 1) * (interline - Math.round(font.getDescentHeight() / 2.0f))) / 2 - interline;

			onAppear.setAppearing(true); darkness.giveControlTo(this);
		}
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
		if(lines != null) {
			fade();
			return true;
		} else
			return false;
	}

	@Override
	public boolean executeMousePressRight(Position position) {
		if(lines != null) {
			fade();
			return true;
		} else
			return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseRelease(Position position) {
		return false;
	}

	public void update(float delta) {
		if(lines != null) {
			if(isNeverending) {
				time = Math.min(time + delta, TIME - APPEAR);
				darkness.redo(delta, this);
			} else {
				if(time < TIME - APPEAR) {
					time = Math.min(time + delta, TIME);

					darkness.redo(delta, this);
					if(time > TIME - APPEAR)
						onAppear.setAppearing(false);
				} else {
					time = Math.min(time + delta, TIME);

					darkness.undo(delta, this);
					if(time == TIME)
						lines = null;
				}
			}
		}
	}
	
	public void draw(SmartSpriteBatch batch) {
		if(lines != null) {
			if(time <= APPEAR)
				fontColor.a = time / APPEAR;
			else

			if(time >= TIME - APPEAR)
				fontColor.a = ((TIME - time) / APPEAR);
			else
				fontColor.a = 1.0f;

			darkness.draw(batch, this);

			font.setColor(fontColor); int messageY = MESSAGE_Y;
			for(int i = 0, n = lines.getLinesNumber(); i != n; i++) {
				messageY = messageY + interline;
					
				String line = lines.getLine(i);
				int messageX = (W - font.getWidth(line)) / 2;
					
				font.draw(batch, line, messageX, messageY);
			}
		}
	}
}
