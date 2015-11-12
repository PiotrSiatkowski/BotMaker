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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import  com.thetruthbeyond.chatterbean.AliceBot;
import  com.thetruthbeyond.chatterbean.Context;
import  com.thetruthbeyond.chatterbean.parser.api.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.thetruthbeyond.bot.CurrentBot;
import com.thetruthbeyond.botmaker.BotMaker;
import com.thetruthbeyond.botmaker.logic.BotExplorer;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.emitters.OnFocus;
import com.thetruthbeyond.gui.action.emitters.OnReachLimit;
import com.thetruthbeyond.gui.action.observers.FocusObserver;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.input.Keyboard;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.simple.MinusButton;
import com.thetruthbeyond.gui.objects.buttons.simple.PlusButton;
import com.thetruthbeyond.gui.objects.controllers.imagewindow.ImageWindow;
import com.thetruthbeyond.gui.objects.controllers.imagewindow.ImageWindowConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.ChoosableScrollArea;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.ChoosableScrollAreaConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBar;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBarConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanel;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.decorators.StateScrollPanel;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldDecorator;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.CapitalCaseTextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.DefaultMessageTextField;
import com.thetruthbeyond.gui.objects.tabs.Tab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.YesNoDialog;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;
import com.thetruthbeyond.gui.utility.tools.CommonMethod;

import static com.thetruthbeyond.gui.configuration.Consts.RESOLUTION;
import static com.thetruthbeyond.gui.configuration.Consts.RES_1366x768;

public class MyBotTab extends Tab {
	
	// Name text fields. ///////////////////////////////////////////////////////////////
	private TextFieldDecorator nameField;
	
	private static final float NAME_COLUMN_X_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.289f : 0.290f;
	private static final float NAME_COLUMN_Y_PARAMETER 	= 0.67f;
	private static final float NAME_COLUMN_W_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.241f : 0.240f;
	private static final float NAME_COLUMN_H_PARAMETER 	= 0.11f;
	////////////////////////////////////////////////////////////////////////////////////
	
	// Password text field. ////////////////////////////////////////////////////////////
	private final TextFieldDecorator passField;
	
	private static final float PASSWORD_COLUMN_X_PARAMETER = RESOLUTION == RES_1366x768 ? 0.289f : 0.290f;
	private static final float PASSWORD_COLUMN_Y_PARAMETER = 0.81f;
	private static final float PASSWORD_COLUMN_W_PARAMETER = RESOLUTION == RES_1366x768 ? 0.241f : 0.240f;
	private static final float PASSWORD_COLUMN_H_PARAMETER = 0.11f;
	////////////////////////////////////////////////////////////////////////////////////
		
	// Bots chose scroll area. /////////////////////////////////////////////////////////
	private final ChoosableScrollArea scrollArea;
	
	private static final float SCROLL_AREA_X_PARAMETER 	= 0.04f;
	private static final float SCROLL_AREA_Y_PARAMETER 	= 0.18f;
	private static final float SCROLL_AREA_W_PARAMETER 	= 0.23f;
	private static final float SCROLL_AREA_H_PARAMETER 	= 0.74f;
	private static final float SCROLL_AREA_P_PARAMETER	= 0.01f;
	
	private static final float SCROLL_AREA_TEXT_ALPHA 	= 1.00f;
	
	private static final int MAX_MESSAGES_SHOWN 		= 10;
	////////////////////////////////////////////////////////////////////////////////////
	
	// Portrait image window. //////////////////////////////////////////////////////////
	private final ImageWindow imageWindow;
	
	private static final float IMAGE_WINDOW_X_PARAMETER = RESOLUTION == RES_1366x768 ? 0.289f : 0.290f;
	private static final float IMAGE_WINDOW_Y_PARAMETER = 0.08f;
	private static final float IMAGE_WINDOW_W_PARAMETER = RESOLUTION == RES_1366x768 ? 0.241f : 0.240f;
	private static final float IMAGE_WINDOW_H_PARAMETER = 0.55f;
	////////////////////////////////////////////////////////////////////////////////////
	
	// Features scroll panel. //////////////////////////////////////////////////////////
	private final StateScrollPanel scrollPane;
	
	private static final float SCROLL_PANEL_X_PARAMETER = 0.55f;
	private static final float SCROLL_PANEL_Y_PARAMETER = 0.08f;
	private static final float SCROLL_PANEL_W_PARAMETER = 0.41f;
	private static final float SCROLL_PANEL_H_PARAMETER = 0.84f;
	private static final float SCROLL_PANEL_P_PARAMETER = 0.02f;
	
	private static final int SCROLL_PANEL_COLUMNS = 2;
	private static final int SCROLL_PANEL_ROWS = 7;
	
	private static final float SCROLL_PANEL_OFFSET_X_PARAMETER = 0.01f;
	private static final float SCROLL_PANEL_OFFSET_Y_PARAMETER = 0.02f;
	////////////////////////////////////////////////////////////////////////////////////
	
	// Features panel scroll bar. //////////////////////////////////////////////////////
	private final ScrollBar paneScrollBar;
	
	private static final float PANEL_SCROLL_BAR_X_PARAMETER = 0.965f;
	private static final float PANEL_SCROLL_BAR_Y_PARAMETER = 0.08f;
	private static final float PANEL_SCROLL_BAR_W_PARAMETER = 0.02f;
	private static final float PANEL_SCROLL_BAR_H_PARAMETER = 0.84f;
	////////////////////////////////////////////////////////////////////////////////////
	
	// Features list scroll bar. ///////////////////////////////////////////////////////
	private final ScrollBar listScrollBar;
	
	private static final float LIST_SCROLL_BAR_X_PARAMETER = 0.015f;
	private static final float LIST_SCROLL_BAR_Y_PARAMETER = 0.08f;
	private static final float LIST_SCROLL_BAR_W_PARAMETER = 0.02f;
	private static final float LIST_SCROLL_BAR_H_PARAMETER = 0.84f;
	////////////////////////////////////////////////////////////////////////////////////
	
	// Affirmation dialog. /////////////////////////////////////////////////////////////
	private final YesNoDialog affirmationDialog;
		
	private static final float DIALOG_X_PARAMETER = 0.35f;
	private static final float DIALOG_Y_PARAMETER = 0.35f;
	private static final float DIALOG_W_PARAMETER = 0.30f;
	private static final float DIALOG_H_PARAMETER = 0.25f;
	////////////////////////////////////////////////////////////////////////////////////
	
	// Bots buttons. ///////////////////////////////////////////////////////////////////
	private final Button addBotButton;
	private final Button subBotButton;
	
	private static final float BOTS_BUTTON_P_PARAMETER_RELATIVE_TO_THEIR_HEIGHT = 0.09f;
	////////////////////////////////////////////////////////////////////////////////////
	
	private final SmartTexture background;
	private final Map<String, AliceBot> bots = new HashMap<>(10);
	
	private BotFactory factory;
	
	private DefaultMessageTextField lastAddedKey;
	private DefaultMessageTextField lastAddedValue;
	
	private final FocusObserver focusObserver = new FocusObserver(this);

	private Keyboard keyboard = Keyboard.getInstance();

	public MyBotTab(Area area, FileManager loader, Clickable parent) {
		super(area, parent);

		background = loader.getTexture("WoodBackground");
		
		TextFieldConfiguration nameConfiguration = configureNameField();
		TextField nameTextField = new TextField(nameConfiguration, loader, this);
		
		nameField = new CapitalCaseTextField(nameTextField);
		nameField = new DefaultMessageTextField(nameField).setDefaultMessage("Enter name");
		
		TextFieldConfiguration passwordConfiguration = configurePasswordField();
		TextField passwordTextField = new TextField(passwordConfiguration, loader, this);
		passField = new DefaultMessageTextField(passwordTextField).setDefaultMessage("Password");
						
		ImageWindowConfiguration imageConfiguration = configureImageWindow();
		imageWindow = new ImageWindow(imageConfiguration, loader, this);
		
		ScrollBarConfiguration scrollBarConfiguration1 = configurePanelScrollBar();
		paneScrollBar = new ScrollBar(scrollBarConfiguration1, loader, this);
		
		ScrollBarConfiguration scrollBarConfiguration2 = configureListScrollBar();
		listScrollBar = new ScrollBar(scrollBarConfiguration2, loader, this);
		
		ScrollPanelConfiguration panelConfiguration = configureScrollPanel();
		ScrollPanel scrollPanel = new ScrollPanel(panelConfiguration, loader, this);
		
		this.scrollPane = new StateScrollPanel(scrollPanel);
		this.scrollPane.addObserver(focusObserver);
		
		ChoosableScrollAreaConfiguration scrollAreaConfiguration = configureScrollArea();
		scrollArea = new ChoosableScrollArea(scrollAreaConfiguration, loader, this);
		
		focusObserver.getSpecializedObserver(OnFocus.Id).observeEmitter(nameField.getEmitter(OnFocus.Id));
		focusObserver.getSpecializedObserver(OnFocus.Id).observeEmitter(passField.getEmitter(OnFocus.Id));
		
		Area dialogArea = new Area();
		dialogArea.setX(area.getX() + DIALOG_X_PARAMETER * area.getW());
		dialogArea.setY(area.getY() + DIALOG_Y_PARAMETER * area.getH());
		dialogArea.setW(DIALOG_W_PARAMETER * area.getW());
		dialogArea.setH(DIALOG_H_PARAMETER * area.getH());
		affirmationDialog = new YesNoDialog(dialogArea, loader, BotMaker.darkness, this);

		try {
			factory = new BotFactory();

			// Filling bot list.
			FileHandle handle = Gdx.files.internal("Bots");
			if(handle.isDirectory()) {
				boolean isFirst = true;
				for(FileHandle file : handle.list()) {
					if(file.isDirectory()) {
						String name = file.nameWithoutExtension();
						scrollArea.addMessage(name);

						try {
							AliceBot bot = factory.createAliceBot(name , new BotExplorer(name));
							bots.put(name, bot);

							if(isFirst) {
								CurrentBot.instance = bots.get(name);
								fillTabWithBotInfo();
								isFirst = false;
							}
						} catch(AliceBotParserException exception) {
							new Logger().writeMessage("Error: Alice CurrentBot Parser Exception occured while parsing bot named: " + name, exception.getMessage());
							new Logger().writeError(exception);

						} catch(AliceBotExplorerException exception) {
							new Logger().writeMessage("Error: Alice CurrentBot Explorer Exception occured while parsing bot named: " + name, exception.getMessage());
							new Logger().writeError(exception);
						}
					}
				}
			}
		} catch(AliceBotParserConfigurationException ignored) {
			BotMaker.messages.showMessage("Chatterbot cannot be parsed and configurated properly.");
		}

		int w = scrollArea.getW();
		int h = scrollArea.getY() - imageWindow.getY();
		int p = Math.round(h * BOTS_BUTTON_P_PARAMETER_RELATIVE_TO_THEIR_HEIGHT);

		Area buttonArea = new Area();

		// Setting common y, w and h sizes.
		buttonArea.setY(imageWindow.getY()); buttonArea.setW(Math.round((w - p) / 2.0f)); buttonArea.setH(h - p);

		// Setting x for the first button.
		buttonArea.setX(scrollArea.getX());
		addBotButton = new PlusButton(buttonArea, loader, this);

		// Setting x for the second button.
		buttonArea.setX(scrollArea.getX() + addBotButton.getW() + p);
		subBotButton = new MinusButton(buttonArea, loader, this);
	}

	private ChoosableScrollAreaConfiguration configureScrollArea() {
		ChoosableScrollAreaConfiguration configuration = new ChoosableScrollAreaConfiguration();
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
		
		configuration.scrollbar = listScrollBar;
		
		return configuration;
	}
	
	TextFieldConfiguration configureNameField() {
		TextFieldConfiguration configuration = new TextFieldConfiguration();
		configuration.area.setX(area.getX() + area.getW() * NAME_COLUMN_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * NAME_COLUMN_Y_PARAMETER);
		configuration.area.setW(area.getW() * NAME_COLUMN_W_PARAMETER);
		configuration.area.setH(area.getH() * NAME_COLUMN_H_PARAMETER);

		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		configuration.characters	= Coding.pathLetters;
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border				= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	TextFieldConfiguration configurePasswordField() {
		TextFieldConfiguration configuration = new TextFieldConfiguration();
		configuration.area.setX(area.getX() + area.getW() * PASSWORD_COLUMN_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * PASSWORD_COLUMN_Y_PARAMETER);
		configuration.area.setW(area.getW() * PASSWORD_COLUMN_W_PARAMETER);
		configuration.area.setH(area.getH() * PASSWORD_COLUMN_H_PARAMETER);
		
		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		configuration.characters	= Coding.signs;
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border				= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	private ImageWindowConfiguration configureImageWindow() {
		ImageWindowConfiguration configuration = new ImageWindowConfiguration();
		configuration.area.setX(area.getX() + area.getW() * IMAGE_WINDOW_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * IMAGE_WINDOW_Y_PARAMETER);
		configuration.area.setW(area.getW()  * IMAGE_WINDOW_W_PARAMETER);
		configuration.area.setH(area.getH() * IMAGE_WINDOW_H_PARAMETER);
		
		configuration.border			= Consts.BORDER_SIZE;
		configuration.backgroundAlpha	= Consts.BACKGROUND_ALPHA;
		
		return configuration;
	}
	
	private ScrollPanelConfiguration configureScrollPanel() {
		ScrollPanelConfiguration configuration = new ScrollPanelConfiguration();	
		configuration.area.setX(area.getX() + area.getW() * SCROLL_PANEL_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * SCROLL_PANEL_Y_PARAMETER);
		configuration.area.setW(area.getW() * SCROLL_PANEL_W_PARAMETER);
		configuration.area.setH(area.getH() * SCROLL_PANEL_H_PARAMETER);
		
		configuration.border			= Consts.BORDER_SIZE;
		configuration.columnsNumber		= SCROLL_PANEL_COLUMNS;
		configuration.rowsNumber		= SCROLL_PANEL_ROWS;
		configuration.relativePadding	= SCROLL_PANEL_P_PARAMETER;
		configuration.relativeGapW 		= SCROLL_PANEL_OFFSET_X_PARAMETER;
		configuration.relativeGapH 		= SCROLL_PANEL_OFFSET_Y_PARAMETER;
		
		configuration.backgroundAlpha 	= Consts.BACKGROUND_ALPHA;
		
		configuration.scrollbar			= paneScrollBar;
		
		return configuration;
	}
	
	private ScrollBarConfiguration configurePanelScrollBar() {
		ScrollBarConfiguration configuration = new ScrollBarConfiguration();
		
		configuration.area.setX(area.getX() + area.getW() * PANEL_SCROLL_BAR_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * PANEL_SCROLL_BAR_Y_PARAMETER);
		
		configuration.area.setW(area.getW() * PANEL_SCROLL_BAR_W_PARAMETER);
		configuration.area.setH(area.getH() * PANEL_SCROLL_BAR_H_PARAMETER);
	
		return configuration;
	}
	
	private ScrollBarConfiguration configureListScrollBar() {
		ScrollBarConfiguration configuration = new ScrollBarConfiguration();
		
		configuration.area.setX(area.getX() + area.getW() * LIST_SCROLL_BAR_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * LIST_SCROLL_BAR_Y_PARAMETER);
		
		configuration.area.setW(area.getW() * LIST_SCROLL_BAR_W_PARAMETER);
		configuration.area.setH(area.getH() * LIST_SCROLL_BAR_H_PARAMETER);
	
		return configuration;
	}
	
	private TextFieldConfiguration configureTextField() {
		TextFieldConfiguration configuration = new TextFieldConfiguration();
		
		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		configuration.characters	= Coding.signs;
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border		   		= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	@Override @UnhandledMethod
	public void onTabEnter() {}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(emitterID == OnFocus.Id)
			focusObserver.reactToEmittedSignal(object, emitterID);
		else

		if(emitterID == OnReachLimit.Id) {
			OnReachLimit onReachLimit = object.getEmitter(emitterID);
			if(onReachLimit.hasReachedOrigin())
				CommonMethod.eraseEmptyRowFrom(scrollPane, SCROLL_PANEL_COLUMNS);
		}
	}

	private void addNewBot() {	
		// Setting unique name.
		String name = getUniqueName();
		
		scrollArea.addMessage(name);
		scrollArea.setSelected(name);
		
		AliceBot bot;
		try {
			// Generating new bot files structure.
			BotExplorer explorer = new BotExplorer(name);
			explorer.createBotStructure();
			
			// Saving previous active bot.
			confirmBotInfo();
			
			// Creating new bot.
			bot = factory.createAliceBot(name, explorer);

			bots.put(name, bot);
			
			// Setting new bot as current.
			CurrentBot.instance = bots.get(name);
						 
			// Updating name to the one generated before.
			if(CurrentBot.instance != null) {
				Context context = CurrentBot.instance.getContext();
				context.setProperty("name", name);
				context.setProperty("id", name);
				explorer.updatePropertiesFile(context);

				// Filling tab with new bot info.
				fillTabWithBotInfo();
			} else {
				BotMaker.messages.showMessage("New chatterbot cannot be created.");
			}
		} catch(AliceBotParserException ignored) {
			BotMaker.messages.showMessage("One of many chatterbot's files are corrupted. Chatterbot cannot be parsed.");
		} catch(AliceBotExplorerException ignored) {
			BotMaker.messages.showMessage("Path to chatterbot's configuration files is wrong or had been changed.");
		}
	}
	
	private String getUniqueName() {
		String name = "Mybot";
		int index = 1;
		while(scrollArea.hasMessage(name + index))
			index++;
		
		name = name + index;
		return name;
	}
	
	private void removeBot() {
		String name = scrollArea.getSelected();
		if(!name.isEmpty()) {
			BotExplorer explorer = new BotExplorer(name);
			explorer.deleteBotStructure();
		}
		
		scrollArea.removeMessage(name);
		
		String selected = scrollArea.getSelected();
		if(!selected.isEmpty()) {
			// Filling tab with new bot info.
			CurrentBot.instance = bots.get(selected);
			fillTabWithBotInfo();
		} else {
			CurrentBot.instance = null;
			clearTab();
		}
	}
		
	private void clearTab() {
		nameField.setInput("");
		passField.setInput("");
		scrollPane.clear();
		imageWindow.updateImage("");
	}
	
	@Override
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		scrollPane.executeMouseHover(position);
		scrollArea.executeMouseHover(position);

		if(affirmationDialog.isActive()) {
			affirmationDialog.executeMouseHover(position);
			return false;
		}
		
		if(CurrentBot.instance != null)
			imageWindow.executeMouseHover(position);

		addBotButton.executeMouseHover(position);
		subBotButton.executeMouseHover(position);
		
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		if(affirmationDialog.isActive()) {
			affirmationDialog.executeMouseRelease(position);
			return false;
		}
	
		scrollArea.executeMouseDrag(position);
		scrollPane.executeMouseDrag(position);
		return true;
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(affirmationDialog.isActive()) {
			if(affirmationDialog.executeMousePress(position)) 
				removeBot();
			
			return false;
		}
		
		if(CurrentBot.instance == null) {
			if(addBotButton.contains(position))
				addNewBot();
			return false;
		}
		
		focusObserver.dropFocus();
		
		if(addBotButton.contains(position))
			addNewBot();
		if(subBotButton.contains(position))
			affirmationDialog.show();
		
		if(scrollArea.contains(position)) {
			if(nameField.getInput().isEmpty()) {
				String name = getUniqueName();
				nameField.setInput(name);
				scrollArea.changeSelected(name);
			}
			
			if(scrollArea.executeMousePress(position)) {
				// Choosing new current bot.
				String name = scrollArea.getSelected();
				confirmBotInfo();
				CurrentBot.instance = bots.get(name);

				focusObserver.stopObserving();

				focusObserver.getSpecializedObserver(OnFocus.Id).observeEmitter(nameField.getEmitter(OnFocus.Id));
				focusObserver.getSpecializedObserver(OnFocus.Id).observeEmitter(passField.getEmitter(OnFocus.Id));

				fillTabWithBotInfo();
			}
		}

		// Password and name field press event.
		nameField.executeMousePress(position);
		passField.executeMousePress(position);

		// Scroll panel and image window press event.
		scrollPane.executeMousePress(position);
		imageWindow.executeMousePress(position);
		
		return false;
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		if(affirmationDialog.isActive()) {
			affirmationDialog.executeMouseRelease(position);
			return false;
		}
				
		scrollArea.executeMouseRelease(position);
		listScrollBar.executeMousePress(position);
		paneScrollBar.executeMouseRelease(position);
		return true;
	}

	@Override
	public void update(float delta) {	
		if(keyboard.isCharacterWaiting()) {
			if(nameField.isFocused()) {
				
				// Input verification. Cannot set the existing name.
				Character character = keyboard.peekCharacter();
				String input = nameField.getInput();
	
				if(scrollArea.hasMessage(input + character) || input.isEmpty() && character.equals(' '))
					// Dismiss character.
					keyboard.getCharacter();
				else if(input.length() >= 0) {
					nameField.update(delta);
					
					String modifiedInput = nameField.getInput();
					if(input.length() != modifiedInput.length())
						scrollArea.changeSelected(modifiedInput);
				}
			}
		}
					
		if(keyboard.isKeyDown(Keys.BACKSPACE)) {
			nameField.update(delta);
			if(nameField.isFocused()) {
				// Input verification. Cannot set the existing name.
				String input = nameField.getInput();
				if(!scrollArea.hasMessage(input))
					scrollArea.changeSelected(input);
				else if(!input.isEmpty())
					nameField.setInput(input.substring(0, input.length() - 1));
				keyboard.dismissKey(Keys.BACKSPACE);
			}
		}
			
		if(keyboard.isKeyDown(Keys.TAB)) {
			focusObserver.switchFocus();
			keyboard.dismissKey(Keys.TAB);
		}
		
		scrollArea.update(delta);
		nameField.update(delta);
		passField.update(delta);
		imageWindow.update(delta);
		scrollPane.update(delta);
		listScrollBar.update(delta);
		paneScrollBar.update(delta);
		addBotButton.update(delta);
		subBotButton.update(delta);
		
		if(CurrentBot.instance != null && !lastAddedKey.getInput().isEmpty()) {
			TextFieldConfiguration textFieldConfiguration = configureTextField();
			TextField field   = scrollPane.addObjectWithAppear(TextField.class, textFieldConfiguration);
			lastAddedKey   	  = scrollPane.substituteObject(field, new DefaultMessageTextField(field));
			lastAddedKey.setDefaultMessage("Property");

			getObserver().observeEmitter(lastAddedKey.getEmitter(OnReachLimit.Id));
			
			field = scrollPane.addObjectWithAppear(TextField.class, textFieldConfiguration);
			lastAddedValue = scrollPane.substituteObject(field, new DefaultMessageTextField(field));
			lastAddedValue.setDefaultMessage("Value");

			getObserver().observeEmitter(lastAddedValue.getEmitter(OnReachLimit.Id));
		}
		
		affirmationDialog.update(delta);
	}
	
	@Override
	public void onTabLeave() {
		if(CurrentBot.instance != null) {
			if(nameField.getInput().isEmpty()) {
				String name = getUniqueName();
				nameField.setInput(name);
				scrollArea.changeSelected(name);
			}

			confirmBotInfo();
		}
	}
	
	private void fillTabWithBotInfo() {
		if(CurrentBot.instance != null) {
			Context context = CurrentBot.instance.getContext();

			scrollPane.clear();

			TextFieldConfiguration textFieldConfiguration = configureTextField();

			for(String property : context.getPropertiesNames()) {
				if(property.equals("id"))
					continue;
				if(property.equals("name"))
					continue;

				TextField field = scrollPane.addObjectImmediately(TextField.class, textFieldConfiguration);
				lastAddedKey = scrollPane.substituteObject(field, new DefaultMessageTextField(field));
				lastAddedKey.setDefaultMessage("Property");
				lastAddedKey.setInput(property);

				getObserver().observeEmitter(lastAddedKey.getEmitter(OnReachLimit.Id));

				field = scrollPane.addObjectImmediately(TextField.class, textFieldConfiguration);
				lastAddedValue = scrollPane.substituteObject(field, new DefaultMessageTextField(field));
				lastAddedValue.setDefaultMessage("Value");
				lastAddedValue.setInput(context.getProperty(property));

				getObserver().observeEmitter(lastAddedValue.getEmitter(OnReachLimit.Id));
			}

			TextField field = scrollPane.addObjectImmediately(TextField.class, textFieldConfiguration);
			lastAddedKey = scrollPane.substituteObject(field, new DefaultMessageTextField(field));
			lastAddedKey.setDefaultMessage("Property");

			getObserver().observeEmitter(lastAddedKey.getEmitter(OnReachLimit.Id));

			field = scrollPane.addObjectImmediately(TextField.class, textFieldConfiguration);
			lastAddedValue = scrollPane.substituteObject(field, new DefaultMessageTextField(field));
			lastAddedValue.setDefaultMessage("Value");

			getObserver().observeEmitter(lastAddedValue.getEmitter(OnReachLimit.Id));

			nameField.setInput(context.getProperty("name"));
			passField.setInput(CurrentBot.instance.getPassword());
			imageWindow.updateImage(context.getProperty("name"));
		}
	}
	
	private void confirmBotInfo() {
		if(CurrentBot.instance != null) {

			int size = scrollPane.getObjectsSize();

			// Obtain current context and old name.
			Context context = CurrentBot.instance.getContext();
			String oldName = context.getProperty("name");

			// Clear properties and set new ones.
			context.clearProperties();
			for(int i = 0; i != size; i += 2) {
				String propertyName = ((TextField) scrollPane.getObject(i)).getInput();
				String propertyValue = ((TextField) scrollPane.getObject(i + 1)).getInput();

				if(propertyName.isEmpty() || propertyValue.isEmpty())
					continue;
				context.setProperty(propertyName, propertyValue);
			}

			// Update robot name reference.
			String newName = nameField.getInput();
			context.setProperty("name", newName);
			if(!newName.equals(oldName)) {
				bots.remove(oldName);
				bots.put(newName, CurrentBot.instance);
			}

			// Updating properties.
			BotExplorer explorer = new BotExplorer(oldName);
			explorer.updatePropertiesFile(context);

			// Updating password.
			CurrentBot.instance.setPassword(passField.getInput());

			Properties configuration = explorer.getConfigurationMap();
			configuration.setProperty("password", CurrentBot.instance.getPassword());
			explorer.setConfigurationMap(configuration);
		}
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		batch.draw(background, area.getX(), area.getY(), area.getW(), area.getH());
		
		scrollArea.draw(batch);

		nameField.draw(batch);
		passField.draw(batch);

		imageWindow.draw(batch);
		scrollPane.draw(batch);
		
		addBotButton.draw(batch);
		subBotButton.draw(batch);

		BotMaker.messages.draw(batch);
		affirmationDialog.draw(batch);
	}

	@Override @UnhandledMethod
	public void dispose() {}

	@Override @UnhandledMethod
	public void clear() {}
}