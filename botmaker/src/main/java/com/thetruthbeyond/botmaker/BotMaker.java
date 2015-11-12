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

package com.thetruthbeyond.botmaker;

import  com.thetruthbeyond.chatterbean.utility.annotations.Accepted;
import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

import  com.thetruthbeyond.chatterbean.utility.logging.Logger;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.thetruthbeyond.bot.CurrentBot;
import com.thetruthbeyond.botmaker.assets.AssetsLoader;
import com.thetruthbeyond.botmaker.gui.tabs.MyBotTab;
import com.thetruthbeyond.botmaker.gui.tabs.BuildTab;
import com.thetruthbeyond.botmaker.gui.tabs.TeachTab;
import com.thetruthbeyond.botmaker.gui.tabs.TestsTab;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.StateRegistrationOffice;
import com.thetruthbeyond.gui.action.emitters.OnExit;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.objects.shareable.DarknessDrawer;
import com.thetruthbeyond.gui.utility.drawing.fonts.Fonts;
import com.thetruthbeyond.gui.input.*;
import com.thetruthbeyond.gui.input.Cursor;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.interfaces.GUIRootObject;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.tabs.*;
import com.thetruthbeyond.gui.objects.widgets.MenuBar;
import com.thetruthbeyond.gui.objects.widgets.MessageShower;
import com.thetruthbeyond.botmaker.gui.objects.widgets.TabMenu;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.fonts.FontPool;
import com.thetruthbeyond.gui.utility.gl.Buffers;

import javax.swing.*;
import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;

@Accepted
public class BotMaker extends Clickable implements ApplicationListener, GUIRootObject {

	private static final float MAX_DELTA_TIME = 0.03f;

	// Swing frame.
	private JFrame projectFrame;
	
	// Appearance.
	private MenuBar menu;
	private TabMenu buttons;

	// Tabs.
	private final AbstractMap<Class<? extends Tab>, Tab> tabs = new HashMap<>();
	private Tab currentTab;

	// Disposable.
	private SmartSpriteBatch batch;

	private Cursor cursor = Cursor.getInstance();
	private Keyboard keyboard = Keyboard.getInstance();

	private FileManager loader;

	// Public fields reachable throughout the code.
	public static MessageShower messages;
	public static DarknessDrawer darkness;

	private boolean isInitialized = false;

	public BotMaker(Clickable parent) {
		super(parent);
	}

	public void setFrame(JFrame frame) {
		projectFrame = frame;
	}

	@Override
	public void create() {
		try {
			loader = new AssetsLoader();
			loader.initialize();

			Coding.configureCharsets(loader);
			Fonts.configureFonts(loader);

			Keyboard.getInstance().setFilter(Coding.signs);

			InputMultiplexer multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(cursor);
			multiplexer.addProcessor(keyboard);
			Gdx.input.setInputProcessor(multiplexer);
		} catch(Exception exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error occured", "Cause was detected in create() method of BotMaker class.");
			logger.writeError(exception);
		}

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch(Exception ignored) {
			// If look and feel could not be found use the default one and do not report a mistake.
		}
	}

	@Override @UnhandledMethod
	public void resize(int width, int height) {}

	private void initialize() {
		try {
			batch = new SmartSpriteBatch();

			// Registration of action emitters.
			StateRegistrationOffice.registerAllIndicators();

			Area menuArea = new Area(0, 0, Consts.SCREEN_W, Consts.MENU_H);
			menu = new MenuBar(menuArea, loader, this);

			Area buttonsArea = new Area(0, Consts.MENU_H, Consts.SCREEN_W, Consts.PANE_H);
			buttons = new TabMenu(buttonsArea, loader, this);

			Area workingArea = new Area(0, Consts.MENU_H + Consts.PANE_H, Consts.SCREEN_W, Consts.SCREEN_H - (Consts.MENU_H + Consts.PANE_H));


			darkness = new DarknessDrawer(0.3f, loader, workingArea);
			messages = new MessageShower(FontType.CHAT_FONT, loader, darkness, this);

			tabs.put(MyBotTab.class, new MyBotTab(workingArea, loader, this));
			tabs.put(TestsTab.class, new TestsTab(workingArea, loader, this));
			tabs.put(TeachTab.class, new TeachTab(workingArea, loader, this));
			tabs.put(BuildTab.class, new BuildTab(workingArea, loader, this));
			tabs.put(AboutTab.class, new AboutTab(workingArea, loader, this));
			currentTab = tabs.get(MyBotTab.class);

			// Register exit event.
			getObserver().observeEmitter(menu.getEmitter(OnExit.Id));
		} catch(Exception error) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Error occured during BotMaker initialization.");
			logger.writeError(error);
		}
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(emitterID == OnExit.Id)
			currentTab.onTabLeave();
	}

	@Override
	public void render() {
		try {
			// Check whether the application has been initialized and can be rendered.
			if(!isInitialized) {
				if(Consts.initialize()) {
					initialize();
					isInitialized = true;
				} else
					return;
			}

			Cursor cursor = Cursor.getInstance();

			if(cursor.isIdle())
				executeMouseHover(cursor.getPosition());

			if(cursor.isDragged())
				executeMouseDrag(cursor.getPosition());

			if(cursor.isPressed())
				executeMousePress(cursor.getClickedPosition());

			if(cursor.isReleased())
				executeMouseRelease(cursor.getReleasedPosition());

			if(cursor.isPressedRight())
				executeMousePressRight(cursor.getClickedPositionRight());

			if(cursor.isReleasedRight())
				executeMouseReleaseRight(cursor.getReleasedPositionRight());

			update(Math.min(Gdx.graphics.getDeltaTime(), MAX_DELTA_TIME));
			draw(batch);

			// Show frame when graphic context is obtained and frame was drawn.
			if(!projectFrame.isVisible())
				projectFrame.setVisible(true);
		} catch(Exception error) {
			Logger logger = new Logger();
			logger.writeMessage("Error occured", "Cause was detected in render() method of BotMaker class.");
			logger.writeError(error);
		}
	}

	@Override
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		if(menu.executeMouseHover(position))
			return true;
		if(buttons.executeMouseHover(position))
			return true;
		return currentTab.executeMouseHover(position);
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		if(menu.executeMouseDrag(position))
			return true;
		if(buttons.executeMouseDrag(position))
			return true;
		return currentTab.executeMouseDrag(position);
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(menu.executeMousePress(position))
			return true;
		if(buttons.executeMousePress(position))
			return true;

		messages.executeMousePress(position);
		return currentTab.executeMousePress(position);
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		if(menu.executeMouseRelease(position))
			return true;
		if(buttons.executeMouseRelease(position))
			return true;
		return currentTab.executeMouseRelease(position);
	}

	@Override
	public boolean executeMousePressRight(Position position) {
		messages.executeMousePressRight(position);
		return currentTab.executeMousePressRight(position);
	}

	@Override
	public boolean executeMouseReleaseRight(Position position) {
		return currentTab.executeMouseReleaseRight(position);
	}

	@Override
	public void update(float delta) {
		menu.update(delta);
		buttons.update(delta);
		currentTab.update(delta);
		messages.update(delta);
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
			currentTab.draw(batch);
			buttons.draw(batch);
			menu.draw(batch);
		batch.end();
	}

	public <T extends Tab> void changeTab(Class<T> type) {
		if(CurrentBot.instance != null) {
			currentTab.onTabLeave();
			currentTab = tabs.get(type);
			currentTab.onTabEnter();
		} else
			messages.showMessage("Choose chatterbot from the list on the left side of screen or crete one with \"plus\" button if is empty.");
	}

	public void setFrameLocation(int x, int y) {
		projectFrame.setLocation(x, y);
	}

	public Point getFrameLocation() {
		return projectFrame.getLocation();
	}

	@SuppressWarnings("MethodMayBeStatic")
	public void closeFrame() {
		Gdx.app.exit();
	}

	public void minimizeFrame() {
		projectFrame.setState(Frame.ICONIFIED);
	}

	@Override
	public void pause() {
		cursor.setActive(false);
		Gdx.graphics.setContinuousRendering(false);
	}

	@Override
	public void resume() {
		cursor.setActive(true);
		Gdx.graphics.setContinuousRendering(true);
	}

	@Override
	public void dispose() {
		Buffers.disposeBuffers();
		FontPool.disposeFonts();

		batch.dispose();
		loader.dispose();
		currentTab.dispose();
	}
}