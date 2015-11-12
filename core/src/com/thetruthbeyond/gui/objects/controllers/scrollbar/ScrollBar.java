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

package com.thetruthbeyond.gui.objects.controllers.scrollbar;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.thetruthbeyond.gui.action.emitters.OnChooseOption;
import com.thetruthbeyond.gui.enums.Option;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.simple.ArrowBotButton;
import com.thetruthbeyond.gui.objects.buttons.simple.ArrowTopButton;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

import org.jetbrains.annotations.Nullable;

public class ScrollBar extends Clickable {

	private final SmartTexture scrollBar;
	private final SmartTexture swing;
	private final SmartTexture line;
	
	private static final float SCROLL_BAR_ALPHA 		= 0.5f;
	private static final float LINE_OFFSET_PROPORTION 	= 0.01f;
	private static final float LINE_FILL_PROPORTION 	= 0.7f;
	
	private static final int MIN_SWING_HEIGHT 	= 20;
	private static final int ARROW_HEIGHT 		= 15;
	private static final int ARROW_PADDING 		= 3;
	
	private final int SCROLL_BAR_Y;
	private final int SCROLL_BAR_H;
	
	private int swingY;
	private int swingH;
	
	private final int LINE_X;
	private final int LINE_W;
	private final int LINE_OFFSET;

	private OnChooseOption onChooseOption;
	private Sound onClick;

	private Button arrowTop;
	private Button arrowBot;

	@Nullable
	private Position pressedPosition;
	
	public ScrollBar(ScrollBarConfiguration configuration, FileManager loader, Clickable parent) {
		super(configuration.area, parent);

		scrollBar 	= loader.getTexture("ScrollBar");
		swing 		= loader.getTexture("ScrollBarSwing");
		line 		= loader.getTexture("ScrollBarLine");

		onClick		= loader.getSound("MessageSound");

		SCROLL_BAR_Y = area.getY() + ARROW_HEIGHT + ARROW_PADDING;
		SCROLL_BAR_H = area.getH() - 2 * (ARROW_HEIGHT + ARROW_PADDING);

		int ARROW_BOT_Y = SCROLL_BAR_Y + SCROLL_BAR_H + ARROW_PADDING;
		
		LINE_W = (int)(area.getW() * LINE_FILL_PROPORTION);
		LINE_X = (int)(area.getX() + (area.getW() - LINE_W) / 2.0f);
		LINE_OFFSET = (int)(SCROLL_BAR_H * LINE_OFFSET_PROPORTION);
		
		// Default values.
		swingH = SCROLL_BAR_H;
		swingY = SCROLL_BAR_Y;

		onChooseOption = new OnChooseOption(this);
		addEmitter(onChooseOption);

		arrowTop = new ArrowTopButton(new Area(area.getX(), area.getY(), area.getW(), ARROW_HEIGHT), loader, this);
		arrowBot = new ArrowBotButton(new Area(area.getX(), ARROW_BOT_Y, area.getW(), ARROW_HEIGHT), loader, this);
	}
	
	public void setBarNumbers(float above, float seen, float beneath) {
		if(seen == 0) {
			swingH = SCROLL_BAR_H;
			swingY = SCROLL_BAR_Y;
		} else {
			swingH = (int)(((seen) / (above + beneath + seen)) * SCROLL_BAR_H);
			swingY = SCROLL_BAR_Y + (int) Math.ceil(((above) / (above + beneath + seen)) * SCROLL_BAR_H);
			swingY = Math.min(SCROLL_BAR_Y + SCROLL_BAR_H - swingH, swingY);
		}
	}
	
	public float getPercentagePosition() {
		return (swingY - SCROLL_BAR_Y) / (float) (SCROLL_BAR_H - swingH);
	}

	@Override
	public boolean contains(Position position) {
		return position.x > area.getX() && position.x < area.getX() + area.getW() && position.y > swingY && position.y < swingY + swingH;
	}
	
	@Override
	public boolean executeMouseHover(Position position) {
		boolean wasHandled = false;

		if(arrowTop.executeMouseHover(position)) wasHandled = true;
		if(arrowBot.executeMouseHover(position)) wasHandled = true;

		return wasHandled;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		if(pressedPosition != null) {
			int dy = pressedPosition.y - position.y;
			swingY -= dy;
			
			swingY = Math.max(SCROLL_BAR_Y, swingY);
			swingY = Math.min(SCROLL_BAR_Y + SCROLL_BAR_H - swingH, swingY);
			pressedPosition = new Position(position);
			return true;
		} else return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(position.x > area.getX() && position.x < area.getX() + area.getW()) {
			// User has clicked on swing.
			if(position.y > swingY && position.y < swingY + swingH) {
				pressedPosition = new Position(position);
				return true;
			} else

			// User has clicked on scroll bar shadowed area.
			if(position.y > SCROLL_BAR_Y && position.y < SCROLL_BAR_Y + SCROLL_BAR_H) {
				swingY = position.y - swingH / 2;
				swingY = Math.max(SCROLL_BAR_Y, swingY);
				swingY = Math.min(SCROLL_BAR_Y + SCROLL_BAR_H - swingH, swingY);
				onChooseOption.signalOption(Option.OPTION_A);
				return true;
			} else

			// User has clicked on top arrow.
			if(arrowTop.contains(position)) {
				onChooseOption.signalOption(Option.OPTION_B);
				onClick.play();
				return true;
			} else

			// User has clicked on bottom arrow.
			if(arrowBot.contains(position)) {
				onChooseOption.signalOption(Option.OPTION_C);
				onClick.play();
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		pressedPosition = null;
		return true;
	}

	@Override
	public void update(float delta) {
		arrowTop.update(delta);
		arrowBot.update(delta);
	}

	@Override
	public void draw(SmartSpriteBatch batch) {

		// Drawing scroll bar.
		arrowTop.draw(batch);

		batch.setColor(1.0f, 1.0f, 1.0f, SCROLL_BAR_ALPHA);
			batch.draw(line, area.getX(), SCROLL_BAR_Y, area.getW(), line.getH());
			batch.draw(scrollBar, area.getX(), SCROLL_BAR_Y + line.getH(), area.getW(), SCROLL_BAR_H - 2 * line.getH());
			batch.draw(line, area.getX(), SCROLL_BAR_Y + SCROLL_BAR_H - line.getH(), area.getW(), line.getH());
		batch.setColor(Color.WHITE);

		arrowBot.draw(batch);
		
		// Drawing scroll bar swing.
		batch.draw(line, area.getX(), swingY, area.getW(), line.getH());
		batch.draw(swing, area.getX(), swingY + line.getH(), area.getW(), swingH - 2 * line.getH());
		batch.draw(line, area.getX(), swingY + swingH - line.getH(), area.getW(), line.getH());
		
		if(swingH > MIN_SWING_HEIGHT) {
			batch.draw(line, LINE_X, swingY + swingH / 2 - LINE_OFFSET, LINE_W, line.getH());
			batch.draw(line, LINE_X, swingY + swingH / 2 + LINE_OFFSET, LINE_W, line.getH());
		}
	}
}
