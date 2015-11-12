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
import  com.thetruthbeyond.chatterbean.aiml.*;

import com.badlogic.gdx.graphics.Color;
import com.thetruthbeyond.botmaker.gui.tabs.TeachTab;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.emitters.OnReachLimit;
import com.thetruthbeyond.gui.action.emitters.OnTabSwitch;
import com.thetruthbeyond.gui.action.emitters.OnValidateFailure;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.interfaces.AIMLGenerator;
import com.thetruthbeyond.gui.interfaces.AIMLGeneratorArgument;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.simple.AddButton;
import com.thetruthbeyond.gui.objects.controllers.label.Label;
import com.thetruthbeyond.gui.objects.controllers.label.LabelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBar;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBarConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanel;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.ContextMenuTextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.OperationTextField;
import com.thetruthbeyond.gui.objects.tabs.overtabs.WildcardsTab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.OperationTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.structures.wildcard.RandomCard;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.tools.CommonMethod;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RandomTab extends TeachSubTab implements AIMLGenerator {

	// features scroll panel. ////////////////////////////////////////////////////////
	private final ScrollPanel panel;
	
	private static final float SCROLL_PANEL_X_PARAMETER =  0.010f;
	private static final float SCROLL_PANEL_Y_PARAMETER =  0.335f;
	private static final float SCROLL_PANEL_W_PARAMETER =  0.950f;
	private static final float SCROLL_PANEL_H_PARAMETER =  0.610f;
	private static final float SCROLL_PANEL_P_PARAMETER =  0.040f;
	
	private static final float SCROLL_PANEL_OFFSET_X_PARAMETER = 0.00f;
	private static final float SCROLL_PANEL_OFFSET_Y_PARAMETER = 0.04f;
	
	private static final float SCROLL_AREA_ALPHA = 0.70f;

	private static final int SCROLL_PANEL_COLUMNS 	= 1;
	private static final int SCROLL_PANEL_ROWS 		= 2;
	//////////////////////////////////////////////////////////////////////////////////
	
	// features panel scroll bar. ////////////////////////////////////////////////////
	private final ScrollBar scrollBar;
	
	private static final float PANEL_SCROLL_BAR_X_PARAMETER = 0.970f;
	private static final float PANEL_SCROLL_BAR_Y_PARAMETER = 0.055f;
	private static final float PANEL_SCROLL_BAR_W_PARAMETER = 0.02f;
	private static final float PANEL_SCROLL_BAR_H_PARAMETER = 0.890f;
	//////////////////////////////////////////////////////////////////////////////////
	
	// Button. ///////////////////////////////////////////////////////////////////////
	private final Button addButton;
	
	private static final float BUTTON_X_PARAMETER = 0.010f;
	private static final float BUTTON_Y_PARAMETER = 0.035f;
	private static final float BUTTON_W_PARAMETER = 0.150f;
	private static final float BUTTON_H_PARAMETER = 0.280f;
	//////////////////////////////////////////////////////////////////////////////////
	
	// Label. ////////////////////////////////////////////////////////////////////////
	private final Label label;
		
	private static final float LABEL_X_PARAMETER = 0.172f;
	private static final float LABEL_W_PARAMETER = 0.800f;
	
	private static final int LABEL_PADDING = 1;
	//////////////////////////////////////////////////////////////////////////////////

	private final WildcardsTab wildcardsTab;
	private final OperationTab operationTab;

	private TextField sen;

	private FileManager loader;

	public RandomTab(Area area, FileManager loader, Clickable parent, WildcardsTab wildcardsTab, OperationTab operationTab) {
		super(area, loader, parent, wildcardsTab, operationTab);

		this.loader = loader;

		this.wildcardsTab = wildcardsTab;
		this.operationTab = operationTab;

		Area buttonArea = new Area(Math.round(BUTTON_X_PARAMETER * getX()), Math.round(BUTTON_Y_PARAMETER * getW()),
								   Math.round(BUTTON_W_PARAMETER * getW()), Math.round(BUTTON_H_PARAMETER * getH()));
		addButton  = new AddButton(buttonArea, loader, this);
		
		addButton.changeX(getX() + BUTTON_X_PARAMETER * getW());
		addButton.changeY(getY() + BUTTON_Y_PARAMETER * getH());
			
		// Configure tab widgets.
		ScrollBarConfiguration scrollBarConfiguration = configureScrollBar();
		scrollBar = new ScrollBar(scrollBarConfiguration, loader, this);
		
		ScrollPanelConfiguration panelConfiguration = configureScrollPanel();
		panel = new ScrollPanel(panelConfiguration, loader, this);
		
		// Initiating condition panels with first row.
		LabelConfiguration labelConfiguration = configureLabel();
		labelConfiguration.area.setY(addButton.getY() + LABEL_PADDING);
		labelConfiguration.area.setH(addButton.getH());
		label = new Label(labelConfiguration, this);

		TextFieldConfiguration textFieldConfiguration = configureTextField();
		addRow(textFieldConfiguration);
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
		
		configuration.scrollbar = scrollBar;
		
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
		configuration.characters	= Coding.signs + "()";
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border		   		= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	private LabelConfiguration configureLabel() {
		LabelConfiguration configuration = new LabelConfiguration();
		configuration.area.setX(area.getX() + area.getW() * LABEL_X_PARAMETER);
		configuration.area.setW(area.getW() * LABEL_W_PARAMETER);
		
		configuration.fontname 		= FontType.GUI_FONT;
		configuration.color		 	= Consts.MAIN_FONT_COLOR;
		
		configuration.label			= "put random expression in the text field below";
		
		
		return configuration;
	}

	private void addRow(TextFieldConfiguration configuration) {
		if(panel.getObjectsSize() == 1)
			sen = panel.addObjectWithAppear(TextField.class, configuration);
		else
			sen = panel.addObjectImmediately(TextField.class, configuration);

		sen = panel.substituteObject(sen, new ContextMenuTextField(wildcardsTab, sen));
		sen = panel.substituteObject(sen, new OperationTextField(operationTab, loader, sen));

		// Connecting emitters with observers.
		parent.getEmitter(OnTabSwitch.Id).registerObserver(sen.getObserver());
		parent.getEmitter(OnValidateFailure.Id).registerObserver(sen.getObserver());

		// Connect OnReachLimit emitter to delete row if needed.
		sen.getEmitter(OnReachLimit.Id).registerObserver(getObserver());
	}

	@Override @UnhandledMethod
	public void onTabEnter() {}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(emitterID == OnReachLimit.Id) {
			OnReachLimit onReachLimit = object.getEmitter(emitterID);
			if(onReachLimit.hasReachedOrigin())
				CommonMethod.eraseEmptyRowFrom(panel, SCROLL_PANEL_COLUMNS);
		}
	}

	@Override @UnhandledMethod
	public boolean isUpdateNeeded() {
		return false;
	}

	@Override @SuppressWarnings("MethodMayBeStatic")
	public Wildcard getWildcard() {
		return new RandomCard("(random)", Color.MAGENTA);
	}

	@Override
	public boolean verifyWildcardAcceptance(Wildcard wildcard) {
		return !(wildcard instanceof RandomCard);
	}

	@Override @UnhandledMethod
	public boolean hasAimlElement(AIMLGeneratorArgument argument) {
		return true;
	}

	@Override
	public List<AIMLElement> getAimlElements(AIMLGeneratorArgument argument, Wildcard ignored) {
	
		Random random = new Random();
			
		int size = panel.getObjectsSize();
			
		for(int j = 0; j != size; j ++) {
			OperationTextField fieldOTF = (OperationTextField) panel.getObject(j);
			ContextMenuTextField fieldCMTF = (ContextMenuTextField) fieldOTF.getDecoratorParent();

			List<String> words = fieldCMTF.getWords();
			if(!words.isEmpty()) {

				List<AIMLElement> templateElements = new LinkedList<>();
				StringBuilder freeText = new StringBuilder(20);

				for(int index = 0; index != words.size(); index++) {

					Wildcard wildcard = fieldCMTF.getWordAsWildcard(index);
					if(wildcard != null) {

						// Adding free text.
						if(freeText.length() != 0) {
							templateElements.add( new Text(freeText.toString()) );
							freeText.delete(0, freeText.length());
						}

						wildcard.fillTemplate(templateElements, (TeachTab) parent);
					} else
						freeText.append(words.get(index));
				}

				// Adding free text.
				if(freeText.length() != 0) {
					templateElements.add( new Text(freeText.toString()) );
					freeText.delete(0, freeText.length());
				}

				// Adding template operations.
				List<AIMLElement> operations = fieldOTF.getAimlElements(null, null);
				templateElements.addAll(operations);

				Li li = new Li();
				li.appendChildren(templateElements);
				random.appendChild(li);
			}
		}
		
		return Collections.<AIMLElement>singletonList(random);
	}

	@Override @UnhandledMethod
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		addButton.executeMouseHover(position);
		
		panel.executeMouseHover(position);
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		addButton.executeMouseDrag(position);
		
		panel.executeMouseDrag(position);
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		panel.executeMousePress(position);		
		return addButton.executeMousePress(position);
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		panel.executeMouseRelease(position);
		return false;
	}

	@Override
	public boolean executeMousePressRight(Position position) {
		panel.executeMousePressRight(position);
		return false;
	}

	@Override
	public void update(float delta) {
		addButton.update(delta);
		
		panel.update(delta);
		
		if(!sen.getInput().isEmpty())
			addRow(configureTextField());
	}

	@Override @UnhandledMethod
	public void performUpdate() {}
	
	@Override @UnhandledMethod
	public void onTabLeave() {}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		addButton.draw(batch);
		label.draw(batch);
		panel.draw(batch);
	}

	@Override @UnhandledMethod
	public void clear() {}

	@Override @UnhandledMethod
	public void dispose() {}
}