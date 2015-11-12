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

package com.thetruthbeyond.botmaker.gui.tabs;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.thetruthbeyond.bot.CurrentBot;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.enums.ScrollAreaInputType;
import com.thetruthbeyond.gui.input.Keyboard;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.ScrollArea;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.ScrollAreaConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBar;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBarConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.MemoryTextField;
import com.thetruthbeyond.gui.objects.tabs.Tab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import  com.thetruthbeyond.chatterbean.AliceBot;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;

import static com.thetruthbeyond.gui.configuration.Consts.RESOLUTION;
import static com.thetruthbeyond.gui.configuration.Consts.RES_1366x768;

public class TestsTab extends Tab {
	
	// Chat scroll area. /////////////////////////////////////////////////////////////
	private final ScrollArea scrollArea;
	
	private static final float SCROLL_AREA_X_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.040f : 0.040f;
	private static final float SCROLL_AREA_Y_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.080f : 0.080f;
	private static final float SCROLL_AREA_W_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.921f : 0.920f;
	private static final float SCROLL_AREA_H_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.740f : 0.740f;
	private static final float SCROLL_AREA_P_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.010f : 0.010f;
	
	private static final float SCROLL_AREA_TEXT_ALPHA 	= 1.000f;
	
	private static final int MAX_MESSAGES_SHOWN			= 10;
	//////////////////////////////////////////////////////////////////////////////////
	
	// Input text field. /////////////////////////////////////////////////////////////
	private TextField textField;
	
	private static final float TEXT_FIELD_X_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.040f : 0.040f;
	private static final float TEXT_FIELD_Y_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.820f : 0.820f;
	private static final float TEXT_FIELD_W_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.921f : 0.920f;
	private static final float TEXT_FIELD_H_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.100f : 0.100f;
	//////////////////////////////////////////////////////////////////////////////////
	
	// Chat area scroll bar. /////////////////////////////////////////////////////////
	private final ScrollBar scrollBar;
	
	private static final float SCROLL_BAR_X_PARAMETER 	= 0.9650f;
	private static final float SCROLL_BAR_Y_PARAMETER 	= 0.0800f;
	private static final float SCROLL_BAR_W_PARAMETER 	= 0.0200f;
	private static final float SCROLL_BAR_H_PARAMETER 	= 0.8400f;
	//////////////////////////////////////////////////////////////////////////////////

	private final Queue<String> questions = new LinkedList<>();
	private final Queue<String> responses = new LinkedList<>();

	private final Queue<Float> times = new LinkedList<>();
	
	private static final float MINIMUM_RESPONSE_TIME_PER_LETTER = 0.025f;

	private float elapsedTime;
	
	private final Random random = new Random();
	
	private final Sound messageSound;
	private final SmartTexture background;

	private final Keyboard keyboard= Keyboard.getInstance();

	private final Logger logger = new Logger();

	public TestsTab(Area area, FileManager loader, Clickable parent) {
		super(area, parent);

		background = loader.getTexture("LightWoodBackground");
		
		ScrollBarConfiguration barConfiguration = configureScrollBar();
		scrollBar = new ScrollBar(barConfiguration, loader, this);
		
		ScrollAreaConfiguration areaConfiguration = configureScrollArea();
		scrollArea = new ScrollArea(areaConfiguration, loader, this);
		
		TextFieldConfiguration fieldConfiguration = configureTextField();
		textField = new TextField(fieldConfiguration, loader, this);
		textField = new MemoryTextField(textField);
		textField.setFocus(true);
		
		messageSound = loader.getSound("MessageSound");
	}
	
	private ScrollAreaConfiguration configureScrollArea() {
		ScrollAreaConfiguration configuration = new ScrollAreaConfiguration();
		configuration.area.setX(area.getX() + area.getW() * SCROLL_AREA_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * SCROLL_AREA_Y_PARAMETER);
		configuration.area.setW(area.getW() * SCROLL_AREA_W_PARAMETER);
		configuration.area.setH(area.getH() * SCROLL_AREA_H_PARAMETER);
		
		configuration.relativeAreaPadding	= SCROLL_AREA_P_PARAMETER;
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border				= Consts.BORDER_SIZE;
		
		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		
		configuration.maxMessagesShown 		= MAX_MESSAGES_SHOWN;
		
		configuration.backgroundAlpha 		= Consts.BACKGROUND_ALPHA;
		configuration.textBackgroundAlpha 	= SCROLL_AREA_TEXT_ALPHA;
		
		configuration.scrollbar = scrollBar;
		
		return configuration;
	}
	
	private TextFieldConfiguration configureTextField() {
		TextFieldConfiguration configuration = new TextFieldConfiguration();
		configuration.area.setX(area.getX() + area.getW() * TEXT_FIELD_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * TEXT_FIELD_Y_PARAMETER);
		configuration.area.setW(area.getW() * TEXT_FIELD_W_PARAMETER);
		configuration.area.setH(area.getH() * TEXT_FIELD_H_PARAMETER);
		
		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING - 0.03f;
		configuration.border				= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	private ScrollBarConfiguration configureScrollBar() {
		ScrollBarConfiguration configuration = new ScrollBarConfiguration();
		
		configuration.area.setX(area.getX() + area.getW() * SCROLL_BAR_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * SCROLL_BAR_Y_PARAMETER);
		configuration.area.setW(area.getW() * SCROLL_BAR_W_PARAMETER);
		configuration.area.setH(area.getH() * SCROLL_BAR_H_PARAMETER);	
		
		return configuration;
	}
	
	@Override
	public void onTabEnter() {
		logger.openLogFile(CurrentBot.instance);

		// Setting no filter for writing in test mode.
		keyboard.setFilter(Coding.signs);
	}
	
	@Override @UnhandledMethod
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		scrollBar.executeMouseHover(position);
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		return scrollArea.executeMouseDrag(position);	
	}

	@Override
	public boolean executeMousePress(Position position) {
		return scrollArea.executeMousePress(position);
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		return scrollArea.executeMouseRelease(position);
	}
	
	@Override
	public void update(float delta) {
		if(CurrentBot.instance == null)
			return;

		// Update main and only one text field.
		textField.update(delta);

		// Update scroll area (scroll bar) if needed.
		scrollArea.update(delta);

		if(keyboard.isKeyDown(Keys.ENTER)) {
			String input = textField.getInput();
			if(!input.isEmpty()) {
				scrollArea.addMessage(input, ScrollAreaInputType.USER_BLUE);
				questions.add(input);

				AliceBot bot = CurrentBot.instance;
				if(bot != null) {
					String response = bot.respond(input);
					responses.add(response);

					@SuppressWarnings("NonReproducibleMathCall")
					float time = (float)(MINIMUM_RESPONSE_TIME_PER_LETTER * (input.length() + response.length()) * Math.min(3, -Math.log(random.nextDouble()) + 1));
					times.add(time);

					keyboard.clearEvents();
					((MemoryTextField) textField).confirmInput();

					messageSound.play();
				}
			}
		}

		if(!responses.isEmpty()) {
			elapsedTime += delta;

			if(elapsedTime > times.peek()) {
				elapsedTime = 0.0f;

				String response = responses.poll();
				scrollArea.addMessage(response, ScrollAreaInputType.USER_RED);

				logger.appendMessage(questions.poll(), response);

				if(!times.isEmpty())
					times.poll();

				messageSound.play();
			}
		}
	}
	
	@Override @UnhandledMethod
	public void onTabLeave() {
		logger.closeLogFile();
	}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		batch.draw(background, area.getX(), area.getY(), area.getW(), area.getH());
		scrollArea.draw(batch);
		textField.draw(batch);
	}
	
	@Override @UnhandledMethod
	public void clear() {}

	@Override @UnhandledMethod
	public void dispose() {}
}
