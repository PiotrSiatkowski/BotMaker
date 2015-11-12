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

package com.thetruthbeyond.botmaker.gui.tabs.teachtab;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

import  com.thetruthbeyond.chatterbean.AliceBot;
import  com.thetruthbeyond.chatterbean.Context;

import com.badlogic.gdx.graphics.Color;
import com.thetruthbeyond.bot.CurrentBot;
import com.thetruthbeyond.botmaker.logic.BotExplorer;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.emitters.OnChangeState;
import com.thetruthbeyond.gui.action.emitters.OnReachLimit;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.simple.WildcardOneButton;
import com.thetruthbeyond.gui.objects.buttons.simple.WildcardTwoButton;
import com.thetruthbeyond.gui.objects.controllers.checkbox.CheckBoxList;
import com.thetruthbeyond.gui.objects.controllers.checkbox.CheckBoxListConfiguration;
import com.thetruthbeyond.gui.objects.controllers.label.Label;
import com.thetruthbeyond.gui.objects.controllers.label.LabelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.line.BorderLine;
import com.thetruthbeyond.gui.objects.controllers.line.BorderLineConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBar;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBarConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanel;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.DefaultMessageTextField;
import com.thetruthbeyond.gui.objects.tabs.overtabs.OperationTab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.WildcardsTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.structures.wildcard.StarCard;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.tools.CommonMethod;

public class VariabTab extends TeachSubTab {
	
	// features scroll panel. /////////////////////////////////////
	private final ScrollPanel panel;
	
	private static final float SCROLL_PANEL_X_PARAMETER = 0.375f;
	private static final float SCROLL_PANEL_Y_PARAMETER = 0.055f;
	private static final float SCROLL_PANEL_W_PARAMETER = 0.595f;
	private static final float SCROLL_PANEL_H_PARAMETER = 0.890f;
	private static final float SCROLL_PANEL_P_PARAMETER = 0.040f;

	private static final int SCROLL_PANEL_COLUMNS = 2;
	private static final int SCROLL_PANEL_ROWS = 3;

	private static final float SCROLL_PANEL_OFFSET_X_PARAMETER = 0.10f;
	private static final float SCROLL_PANEL_OFFSET_Y_PARAMETER = 0.04f;

	private static final float SCROLL_AREA_ALPHA = 0.70f;
	///////////////////////////////////////////////////////////////
	
	// features panel scroll bar. /////////////////////////////////
	private final ScrollBar scrollBar;
	
	private static final float PANEL_SCROLL_BAR_X_PARAMETER = 0.970f;
	private static final float PANEL_SCROLL_BAR_Y_PARAMETER = 0.055f;
	private static final float PANEL_SCROLL_BAR_W_PARAMETER = 0.020f;
	private static final float PANEL_SCROLL_BAR_H_PARAMETER = 0.890f;
	///////////////////////////////////////////////////////////////
	
	// Buttons.
	private final Button[] buttons = new Button[2];
	
	private static final float BUTTON_Y_PARAMETER = 0.045f;
	private static final float BUTTON_W_PARAMETER = 0.365f;
	
	private final Color[] colors = { Color.RED, Color.BLUE };
	///////////////////////////////////////////////////////////////
	
	// Separators. ////////////////////////////////////////////////
	private final BorderLine[] separators = new BorderLine[2];
	
	private static final float LINE_W_PARAMETER 	= 0.006f;
	
	private static final float LINE_1_Y = 0.367f;
	private static final float LINE_1_W = 0.367f;
	private static final float LINE_2_X = 0.367f;
	////////////////////////////////////////////////////////////////
	
	// Check list. /////////////////////////////////////////////////
	private final CheckBoxList boxes;
	
	private static final float CHECK_BOX_LIST_X_PARAMETER = 0.31f;
	private static final float CHECK_BOX_LIST_W_PARAMETER = 0.05f;
	////////////////////////////////////////////////////////////////
	
	// Low priority label. /////////////////////////////////////////////////
	private final Label lowPriority;
		
	private static final float LABEL_LOW_X_PARAMETER = 0.01f;
	private static final float LABEL_LOW_W_PARAMETER = 0.28f;
	////////////////////////////////////////////////////////////////
		
	// Low priority label. /////////////////////////////////////////////////
	private final Label highPriority;
			
	private static final float LABEL_HIGH_X_PARAMETER = 0.01f;
	private static final float LABEL_HIGH_W_PARAMETER = 0.28f;
	////////////////////////////////////////////////////////////////
	
	private TextField key;
	private TextField val;

	private int currentWildcard = 0;

	private Context currentContext;

	private boolean updatePredicates = false;
	
	public VariabTab(Area area, FileManager loader, Clickable parent, WildcardsTab wildcardsTab, OperationTab operationTab) {
		super(area, loader, parent, wildcardsTab, operationTab);

		buttons[0] = new WildcardOneButton(loader, this);
		buttons[1] = new WildcardTwoButton(loader, this);

		int totalW = 0;
		for(Button button : buttons)
			totalW += button.getW();

		int gapW = Math.round((BUTTON_W_PARAMETER * getW() - totalW) / (buttons.length + 1));
		int buttonX = gapW;

		for(Button button : buttons) {
			button.changeX(getX() + buttonX);
			button.changeY(getY() + BUTTON_Y_PARAMETER * area.getH());
			buttonX += gapW + button.getW();
		}

		// Configure border lines.
		BorderLineConfiguration lineConfiguration = new BorderLineConfiguration();
		lineConfiguration.area.setX(area.getX());
		lineConfiguration.area.setY(area.getY() + LINE_1_Y * area.getH());
		lineConfiguration.area.setW(LINE_1_W * area.getW());
		lineConfiguration.area.setH(LINE_W_PARAMETER * area.getW());
		separators[0] = new BorderLine(lineConfiguration, loader, this);
		
		lineConfiguration.area.setX(area.getX() + LINE_2_X * area.getW());
		lineConfiguration.area.setY(area.getY());
		lineConfiguration.area.setW(LINE_W_PARAMETER * area.getW());
		lineConfiguration.area.setH(area.getH());
		separators[1] = new BorderLine(lineConfiguration, loader, this);
				
		// Configure scroll bar.
		ScrollBarConfiguration scrollBarConfiguration = configureScrollBar();
		scrollBar = new ScrollBar(scrollBarConfiguration, loader, this);

		// Configure scroll panel.
		ScrollPanelConfiguration panelConfiguration = configureScrollPanel();
		panel = new ScrollPanel(panelConfiguration, loader, this);
		panel.setFieldRatios(new float[] { 0.48f, 0.48f });

		// Configure check box list.
		CheckBoxListConfiguration boxesConfiguration = configureCheckBoxList();
		boxesConfiguration.area.setY(separators[0].getY() + separators[0].getH());
		boxesConfiguration.area.setH(area.getY() + area.getH() - boxesConfiguration.area.getY());
		boxes = new CheckBoxList(boxesConfiguration, loader, this);

		// Configure check box list.
		LabelConfiguration labelConfiguration = configureLowPriorityLabel();
		labelConfiguration.area.setY(boxes.getAreaOfBox(0).getY());
		labelConfiguration.area.setH(boxes.getAreaOfBox(0).getH());
		lowPriority = new Label(labelConfiguration, this);

		// Configure check box list.
		labelConfiguration = configureHighPriorityLabel();
		labelConfiguration.area.setY(boxes.getAreaOfBox(1).getY());
		labelConfiguration.area.setH(boxes.getAreaOfBox(1).getH());
		highPriority = new Label(labelConfiguration, this);
	}

	private ScrollPanelConfiguration configureScrollPanel() {
		ScrollPanelConfiguration configuration = new ScrollPanelConfiguration();
		configuration.area.setX(area.getX() + area.getW() * SCROLL_PANEL_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * SCROLL_PANEL_Y_PARAMETER);
		configuration.area.setW(area.getW() * SCROLL_PANEL_W_PARAMETER);
		configuration.area.setH(area.getH() * SCROLL_PANEL_H_PARAMETER);

		configuration.border = Consts.BORDER_SIZE;
		configuration.columnsNumber = SCROLL_PANEL_COLUMNS;
		configuration.rowsNumber = SCROLL_PANEL_ROWS;
		configuration.relativePadding = SCROLL_PANEL_P_PARAMETER;
		configuration.relativeGapW = SCROLL_PANEL_OFFSET_X_PARAMETER;
		configuration.relativeGapH = SCROLL_PANEL_OFFSET_Y_PARAMETER;

		configuration.backgroundAlpha = SCROLL_AREA_ALPHA;

		configuration.drawBackground = false;
		configuration.includeBoundaryPadding = false;

		configuration.scrollbar = scrollBar;

		return configuration;
	}

	private ScrollBarConfiguration configureScrollBar() {
		ScrollBarConfiguration configuration = new ScrollBarConfiguration();

		configuration.area.setX(area.getX() + area.getW() * PANEL_SCROLL_BAR_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * PANEL_SCROLL_BAR_Y_PARAMETER);

		configuration.area.setW(area.getW() * PANEL_SCROLL_BAR_W_PARAMETER);
		configuration.area.setH(area.getH() * PANEL_SCROLL_BAR_H_PARAMETER);

		return configuration;
	}

	private TextFieldConfiguration configureTextField() {
		TextFieldConfiguration configuration = new TextFieldConfiguration();

		configuration.fontname = FontType.CHAT_FONT;
		configuration.fontcolor = Consts.MAIN_FONT_COLOR;
		configuration.characters = Coding.signs;

		configuration.relativeTextPadding = Consts.RELATIVE_TEXT_PADDING;
		configuration.border = Consts.BORDER_SIZE;

		return configuration;
	}

	private CheckBoxListConfiguration configureCheckBoxList() {
		CheckBoxListConfiguration configuration = new CheckBoxListConfiguration();

		configuration.area.setX(area.getX() + CHECK_BOX_LIST_X_PARAMETER * area.getW());
		configuration.area.setW(CHECK_BOX_LIST_W_PARAMETER * area.getW());
		
		configuration.alignVertical 		= true;
		configuration.fitHeightByMinimizing = true;
		configuration.setIdealSize 			= true;
		
		configuration.border 		= Consts.BORDER_SIZE;
		configuration.boxes 		= 2;
		configuration.relativeGap 	= 0.04f;

		return configuration;
	}
	
	private LabelConfiguration configureLowPriorityLabel() {
		LabelConfiguration configuration = new LabelConfiguration();

		configuration.area.setX(area.getX() + LABEL_LOW_X_PARAMETER * area.getW());
		configuration.area.setW(LABEL_LOW_W_PARAMETER * area.getW());

		configuration.color		 = Color.OLIVE;
		configuration.fontname	 = FontType.GUI_FONT;
		configuration.label		 = "Low priority";

		return configuration;
	}
	
	private LabelConfiguration configureHighPriorityLabel() {
		LabelConfiguration configuration = new LabelConfiguration();

		configuration.area.setX(area.getX() + LABEL_HIGH_X_PARAMETER * area.getW());
		configuration.area.setW(LABEL_HIGH_W_PARAMETER * area.getW());

		configuration.color		 = Color.OLIVE;
		configuration.fontname	 = FontType.GUI_FONT;
		configuration.label		 = "High priority";

		return configuration;
	}

	private void addRow(TextFieldConfiguration configuration) {
		key = panel.addObjectImmediately(TextField.class, configuration);
		val = panel.addObjectImmediately(TextField.class, configuration);

		key = panel.substituteObject(key, new DefaultMessageTextField(key).setDefaultMessage("Variable's name."));
		val = panel.substituteObject(val, new DefaultMessageTextField(val).setDefaultMessage("Variable's value."));

		key.getEmitter(OnChangeState.Id).registerObserver(getObserver());
		val.getEmitter(OnChangeState.Id).registerObserver(getObserver());

		key.getEmitter(OnReachLimit.Id).registerObserver(getObserver());
		val.getEmitter(OnReachLimit.Id).registerObserver(getObserver());
	}

	@Override
	public void onTabEnter() {
		AliceBot bot = CurrentBot.instance;

		if(bot != null) {
			currentContext = bot.getContext();
			TextFieldConfiguration textFieldConfiguration = configureTextField();

			panel.clear();
			for(String predicate : currentContext.getPredicatesNames()) {

				addRow(textFieldConfiguration);

				key.setInput(predicate);
				val.setInput(currentContext.getPredicate(predicate));
			}

			// Add empty row.
			addRow(textFieldConfiguration);
		}
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(emitterID == OnChangeState.Id)
			updatePredicates = true;
		if(emitterID == OnReachLimit.Id) {
			OnReachLimit onReachLimit = object.getEmitter(emitterID);
			if(onReachLimit.hasReachedOrigin())
				CommonMethod.eraseEmptyRowFrom(panel, SCROLL_PANEL_COLUMNS);
		}
	}

	@Override
	public Wildcard getWildcard() {
		StarCard card = new StarCard("(wildcard)", colors[currentWildcard]);
		card.setHighPriority(boxes.getCheckedIndex() != 0);
		return card;
	}

	@Override
	public boolean verifyWildcardAcceptance(Wildcard wildcard) {
		return true;
	}

	@Override
	public boolean isUpdateNeeded() {
		return updatePredicates;
	}

	@Override
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		for(Button button : buttons)
			button.executeMouseHover(position);

		panel.executeMouseHover(position);
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		for(Button button : buttons)
			if(!button.isHovered())
				button.executeMouseDrag(position);

		panel.executeMouseDrag(position);
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		panel.executeMousePress(position);
		boxes.executeMousePress(position);
		
		for(int i = 0; i != buttons.length; i++) {
			if(buttons[i].contains(position)) {
				currentWildcard = i;
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		panel.executeMouseRelease(position);
		return false;
	}

	@Override
	public void update(float delta) {
		for(Button button : buttons)
			button.update(delta);

		panel.update(delta);

		if(!key.getInput().isEmpty() && !val.getInput().isEmpty())
			addRow(configureTextField());

	}

	@Override
	public void performUpdate() {
		int size = panel.getObjectsSize();
		
		// Clear predicates and set new ones.
		currentContext.clearPredicates();
		for(int i = 0; i != size; i += 2) {
			String propertyName  = ( (TextField) panel.getObject(i)).getInput();
			String propertyValue = ( (TextField) panel.getObject(i + 1)).getInput();
			
			if(propertyName.isEmpty() || propertyValue.isEmpty())
				continue;
			currentContext.setPredicate(propertyName, propertyValue);
		}
		
		BotExplorer explorer = new BotExplorer(currentContext.getProperty("name"));
		explorer.updatePredicatesFile(currentContext);
		
		updatePredicates = false;
	}
	
	@Override
	public void onTabLeave() {
		performUpdate();
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		for(Button button : buttons)
			button.draw(batch);
		
		panel.draw(batch);
		
		for(BorderLine line : separators)
			line.draw(batch);
		boxes.draw(batch);
		
		lowPriority.draw(batch);
		highPriority.draw(batch);
		
		scrollBar.draw(batch);
	}

	@Override @UnhandledMethod
	public void clear() {}

	@Override @UnhandledMethod
	public void dispose() {}
}
