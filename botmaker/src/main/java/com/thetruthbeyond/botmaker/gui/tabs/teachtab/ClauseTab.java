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

import java.util.ArrayList;
import java.util.List;

import  com.thetruthbeyond.chatterbean.aiml.*;
import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

import com.badlogic.gdx.graphics.Color;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.emitters.OnReachLimit;
import com.thetruthbeyond.gui.action.emitters.OnTabSwitch;
import com.thetruthbeyond.gui.action.emitters.OnValidateFailure;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.enums.Align;
import com.thetruthbeyond.gui.interfaces.AIMLGenerator;
import com.thetruthbeyond.gui.interfaces.AIMLGeneratorArgument;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.simple.AddButton;
import com.thetruthbeyond.gui.objects.buttons.simple.ConditionOneButton;
import com.thetruthbeyond.gui.objects.buttons.simple.ConditionTwoButton;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBar;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBarConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanel;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.ContextMenuTextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.LabeledTextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.LabeledTextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.OperationTextField;
import com.thetruthbeyond.gui.objects.tabs.overtabs.OperationTab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.WildcardsTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.structures.wildcard.ConditionCard;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.tools.CommonMethod;

public class ClauseTab extends TeachSubTab implements AIMLGenerator {

	// features scroll panel. ////////////////////////////////////////////////////////
	private final ScrollPanel[] panels = new ScrollPanel[2];
	
	private static final float SCROLL_PANEL_X_PARAMETER 	= 0.16f;
	private static final float SCROLL_PANEL_Y_PARAMETER 	= 0.055f;
	private static final float SCROLL_PANEL_W_PARAMETER 	= 0.81f;
	private static final float SCROLL_PANEL_H_PARAMETER 	= 0.890f;
	private static final float SCROLL_PANEL_P_PARAMETER 	= 0.04f;
		
	private static final int SCROLL_PANEL_COLUMNS 	= 3;
	private static final int SCROLL_PANEL_ROWS 		= 3;
	
	private static final float SCROLL_PANEL_OFFSET_X_PARAMETER = 0.10f;
	private static final float SCROLL_PANEL_OFFSET_Y_PARAMETER = 0.04f;
	
	private static final float SCROLL_AREA_ALPHA = 0.70f;
	//////////////////////////////////////////////////////////////////////////////////
	
	// Labels. ///////////////////////////////////////////////////////////////////////
	private static final float LABEL_SIZE_PARAMETER	 	= 0.6f;
	//////////////////////////////////////////////////////////////////////////////////

	private static final float PANEL_SCROLL_BAR_X_PARAMETER 		= 0.970f;
	private static final float PANEL_SCROLL_BAR_Y_PARAMETER 		= 0.055f;
	private static final float PANEL_SCROLL_BAR_W_PARAMETER 		= 0.02f;
	private static final float PANEL_SCROLL_BAR_H_PARAMETER 		= 0.890f;
	//////////////////////////////////////////////////////////////////////////////////
	
	// Buttons. //////////////////////////////////////////////////////////////////////
	private final Button addButton;
	private final Button[] buttons = new Button[2];
	
	private static final float BUTTONS_P_PARAMETER =  0.010f;
	private final Color[] colors = { Color.RED, Color.BLUE };
	//////////////////////////////////////////////////////////////////////////////////

	private final WildcardsTab wildcardsTab;
	private final OperationTab operationTab;
	
	private TextField key;
	private TextField val;
	private TextField sen;
	
	private int currentCondition = 0;

	private FileManager loader;

	public ClauseTab(Area area, FileManager loader, Clickable parent, WildcardsTab wildcardsTab, OperationTab operationTab) {
		super(area, loader, parent, wildcardsTab, operationTab);

		this.loader = loader;

		this.wildcardsTab = wildcardsTab;
		this.operationTab = operationTab;

		// Configure tab widgets.
		ScrollBarConfiguration scrollBarConfiguration = configureScrollBar();

		ScrollBar[] scrollBars = new ScrollBar[2];
		for(int i = 0; i != scrollBars.length; i++)
			scrollBars[i] = new ScrollBar(scrollBarConfiguration, loader, this);

		ScrollPanelConfiguration panelConfiguration = configureScrollPanel();
		for(int i = 0; i != panels.length; i++) {
			panelConfiguration.scrollbar = scrollBars[i];
			panels[i] = new ScrollPanel(panelConfiguration, loader, this);
			panels[i].setFieldRatios( new float[] { 0.038f, 0.235f, 0.03f, 0.235f, 0.07f, 0.382f, 0.01f } );
		}

		// Initiating condition panels with first row.
		for(int i = 0; i != panels.length; i++)
			addRow(i, configureTextField());

		// Obtaining reference to first shown row.
		key = (TextField) panels[currentCondition].getObject(0);
		val = (TextField) panels[currentCondition].getObject(1);
		sen = (TextField) panels[currentCondition].getObject(2);

		Clickable clickable = panels[0].getObject(0);
		Area buttonArea = new Area(Math.round(getX() + BUTTONS_P_PARAMETER * area.getW()), 0,
								   Math.round((SCROLL_PANEL_X_PARAMETER - 2 * BUTTONS_P_PARAMETER) * area.getW()), clickable.getH());

		// Add fields just to compute buttons' position.
		TextFieldConfiguration textFieldConfiguration = configureTextField();

		panels[0].addObjectImmediately(TextField.class, textFieldConfiguration);
		panels[0].addObjectImmediately(TextField.class, textFieldConfiguration);
		panels[0].addObjectImmediately(TextField.class, textFieldConfiguration);

		panels[0].addObjectImmediately(TextField.class, textFieldConfiguration);
		panels[0].addObjectImmediately(TextField.class, textFieldConfiguration);
		panels[0].addObjectImmediately(TextField.class, textFieldConfiguration);

		buttons[0] = new ConditionOneButton(buttonArea, loader, this);
		buttons[1] = new ConditionTwoButton(buttonArea, loader, this);
		addButton  = new AddButton(buttonArea, loader, this);

		// Aggregating all buttons to align them properly.
		Button[] allButtons = new Button[buttons.length + 1];
		for(int i = 0; i != buttons.length; i++)
			allButtons[i] = buttons[i];
		allButtons[buttons.length] = addButton;

		boolean isFirstButton = true;
		int objectIndex = 0;

		for(Button button : allButtons) {
			button.changeY(panels[0].getObject(objectIndex).getY());
			objectIndex += allButtons.length;

			if(isFirstButton) {
				button.setPressed();
				isFirstButton = false;
			}
		}

		// Remove artificially added rows.
		panels[0].removeRow(1);
		panels[0].removeRow(1);
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
		
		configuration.backgroundAlpha 	= SCROLL_AREA_ALPHA;
		
		configuration.drawBackground = false;
		configuration.includeBoundaryPadding = false;
		configuration.drawScrollBarEvenIfCantBeScrolled = true;
		
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
		
		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		configuration.characters	= Coding.signs;
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border		   		= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	@SuppressWarnings("MethodMayBeStatic")
	private LabeledTextFieldConfiguration configureLabeledTextField(int column) {
		LabeledTextFieldConfiguration configuration = new LabeledTextFieldConfiguration();
		
		configuration.labelFontname		= FontType.CHAT_FONT;
		configuration.labelFontColor 	= Consts.MAIN_FONT_COLOR;
		
		if(column == 0)
			configuration.labelTitle	= "if";
		else if(column == 1)
			configuration.labelTitle	= "=";
		else if(column == 2)
			configuration.labelTitle	= "put";

		if(column == 0)
			configuration.relativeLabelPaddingX	= -0.04f;
		else if(column == 1)
			configuration.relativeLabelPaddingX	= -0.025f;
		else if(column == 2)
			configuration.relativeLabelPaddingX	= -0.02f;
		
		configuration.relativeLabelPaddingY		= +0.00f;
		
		configuration.labelSizeParameter = LABEL_SIZE_PARAMETER;
		configuration.align = Align.LeftCenter;
		
		return configuration;
	}

	private void addRow(int currentCondition, TextFieldConfiguration configuration) {
		key = panels[currentCondition].addObjectWithAppear(TextField.class, configuration);
		val = panels[currentCondition].addObjectWithAppear(TextField.class, configuration);
		sen = panels[currentCondition].addObjectWithAppear(TextField.class, configuration);

		key = panels[currentCondition].substituteObject(key, new LabeledTextField(key, configureLabeledTextField(0)));
		val = panels[currentCondition].substituteObject(val, new LabeledTextField(val, configureLabeledTextField(1)));
		sen = panels[currentCondition].substituteObject(sen, new LabeledTextField(sen, configureLabeledTextField(2)));
		sen = panels[currentCondition].substituteObject(sen, new ContextMenuTextField(wildcardsTab, sen));
		sen = panels[currentCondition].substituteObject(sen, new OperationTextField(operationTab, loader, sen));

		parent.getEmitter(OnTabSwitch.Id).registerObserver((sen.getObserver()));
		parent.getObserver().observeEmitter(sen.getEmitter(OnValidateFailure.Id));

		key.getEmitter(OnReachLimit.Id).registerObserver(getObserver());
		val.getEmitter(OnReachLimit.Id).registerObserver(getObserver());
		sen.getEmitter(OnReachLimit.Id).registerObserver(getObserver());
	}

	@Override @UnhandledMethod
	public void onTabEnter() {}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(emitterID == OnReachLimit.Id) {
			OnReachLimit onReachLimit = object.getEmitter(emitterID);
			if(onReachLimit.hasReachedOrigin())
				CommonMethod.eraseEmptyRowFrom(panels[currentCondition], SCROLL_PANEL_COLUMNS);
		}
	}

	@Override @UnhandledMethod
	public boolean isUpdateNeeded() {
		return false;
	}

	@Override
	public Wildcard getWildcard() {
		return new ConditionCard("(condition)", colors[currentCondition]).setIndex(currentCondition);
	}

	@Override
	public boolean verifyWildcardAcceptance(Wildcard wildcard) {
		return !(wildcard instanceof ConditionCard);
	}

	@Override @UnhandledMethod
	public boolean hasAimlElement(AIMLGeneratorArgument argument) {
		return true;
	}

	@Override
	public List<AIMLElement> getAimlElements(AIMLGeneratorArgument argument, Wildcard wildcard) {
		List<AIMLElement> conditions = new ArrayList<>(panels.length);
		
		for(int i = 0; i != panels.length; i++) {
			Condition condition = new Condition();
			
			int size = panels[i].getObjectsSize();

			if(size == 6 && (((TextField) panels[i].getObject(3)).getInput().isEmpty() ||
							 ((TextField) panels[i].getObject(4)).getInput().isEmpty() ||
							 ((TextField) panels[i].getObject(5)).getInput().isEmpty()))
			{
				// Only one condition. List widgets aren't needed.
				String key = ((TextField) panels[i].getObject(0)).getInput();
				String val = ((TextField) panels[i].getObject(1)).getInput();
				String sen = ((TextField) panels[i].getObject(2)).getInput();

				if(key.isEmpty() || val.isEmpty() || sen.isEmpty())
					continue;
				else {
					condition.setName(key);
					condition.setValue(val);
					condition.appendChild(new Text(sen));

					// Adding template operations.
					List<AIMLElement> operations = ((OperationTextField) panels[i].getObject(2)).getAimlElements(null, null);
					condition.appendChildren(operations);
				}
			} else {
				for(int j = 0; j != size; j += 3) {
					String key = ((TextField) panels[i].getObject(j + 0)).getInput();
					String val = ((TextField) panels[i].getObject(j + 1)).getInput();
					String sen = ((TextField) panels[i].getObject(j + 2)).getInput();

					if( !(key.isEmpty() || val.isEmpty() || sen.isEmpty()) ) {
						Li li = new Li(key, val);
						li.appendChild(new Text(sen));

						// Adding template operations.
						List<AIMLElement> operations = ((OperationTextField) panels[i].getObject(j + 2)).getAimlElements(null, null);
						li.appendChildren(operations);

						condition.appendChild(li);
					}
				}
			}

			if(!condition.getChildren().isEmpty())
				conditions.add(condition);
		}
		
		return conditions;
	}
	
	@Override @UnhandledMethod
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		for(Button button : buttons)
			if(!button.isPressed())
				button.executeMouseHover(position);
		addButton.executeMouseHover(position);
		
		panels[currentCondition].executeMouseHover(position);
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		for(Button button : buttons)
			if(!button.isPressed())
				button.executeMouseDrag(position);
		addButton.executeMouseDrag(position);
		
		panels[currentCondition].executeMouseDrag(position);
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		panels[currentCondition].executeMousePress(position);
		
		int pressedButton = -1;
		for(int i = 0; i != buttons.length; i++) {
			if(buttons[i].contains(position) && !buttons[i].isPressed()) {
				if(buttons[i].executeMousePress(position)) {
					pressedButton = currentCondition = i;
					
					int size = panels[i].getObjectsSize();
					key = (TextField) panels[i].getObject(size - 3);
					val = (TextField) panels[i].getObject(size - 2);
					sen = (TextField) panels[i].getObject(size - 1);
				}
			}
		}
		
		if(pressedButton != -1) {
			for(int i = 0; i != buttons.length; i++) {
				if(pressedButton != i)
					buttons[i].executeMouseRelease(position);
			}
		}
		
		return addButton.executeMousePress(position);
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		return panels[currentCondition].executeMouseRelease(position);
	}

	@Override
	public boolean executeMousePressRight(Position position) {
		return panels[currentCondition].executeMousePressRight(position);
	}

	@Override
	public void update(float delta) {
		for(Button button : buttons)
			button.update(delta);
		addButton.update(delta);

		panels[currentCondition].update(delta);
		
		if(!key.getInput().isEmpty() && !val.getInput().isEmpty() && !sen.getInput().isEmpty())
			addRow(currentCondition, configureTextField());
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		for(Button button : buttons)
			button.draw(batch);
		addButton.draw(batch);
		panels[currentCondition].draw(batch);
	}

	@Override @UnhandledMethod
	public void performUpdate() {}
	
	@Override @UnhandledMethod
	public void onTabLeave() {}
	
	@Override @UnhandledMethod
	public void clear() {}

	@Override @UnhandledMethod
	public void dispose() {}
}
