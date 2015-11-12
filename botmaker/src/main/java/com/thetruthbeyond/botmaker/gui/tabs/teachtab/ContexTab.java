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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import  com.thetruthbeyond.chatterbean.aiml.AIMLElement;
import  com.thetruthbeyond.chatterbean.aiml.That;
import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import  com.thetruthbeyond.chatterbean.AliceBot;
import  com.thetruthbeyond.chatterbean.Context;

import  com.thetruthbeyond.chatterbean.text.structures.Response;
import com.thetruthbeyond.bot.CurrentBot;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.interfaces.AIMLGenerator;
import com.thetruthbeyond.gui.interfaces.AIMLGeneratorArgument;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.controllers.label.Label;
import com.thetruthbeyond.gui.objects.controllers.label.LabelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanel;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.AutofillAllTextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.ContextMenuTextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.WildcardTextField;
import com.thetruthbeyond.gui.objects.tabs.overtabs.OperationTab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.WildcardsTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

public class ContexTab extends TeachSubTab implements AIMLGenerator {

	private static final int NUMBER_OF_REMEMBERED_RESPONSES = 50;
		
	// features scroll panel. ////////////////////////////////////////////////////////
	private final ScrollPanel panel;
		
	private static final float SCROLL_PANEL_X_PARAMETER 	= 0.011f;
	private static final float SCROLL_PANEL_Y_PARAMETER 	= 0.330f;
	private static final float SCROLL_PANEL_W_PARAMETER 	= 0.979f;
	private static final float SCROLL_PANEL_H_PARAMETER 	= 0.298f;
	private static final float SCROLL_PANEL_P_PARAMETER 	= 0.040f;
			
	private static final int SCROLL_PANEL_COLUMNS 	= 1;
	private static final int SCROLL_PANEL_ROWS 	= 1;
		
	private static final float SCROLL_AREA_ALPHA = 0.70f;
	//////////////////////////////////////////////////////////////////////////////////
		
	// ContextTab text field. //////////////////////////////////////////////////////////
	private TextField thatInput;
	/////////////////////////////////////////////////////////////////////////////////
	
	// ContextTab text field. //////////////////////////////////////////////////////////
	private final Label label;
		
	private static final float LABEL_X_PARAMETER 	= 0.02f;
	private static final float LABEL_Y_PARAMETER 	= 0.07f;
	private static final float LABEL_W_PARAMETER 	= 0.98f;
	private static final float LABEL_H_PARAMETER 	= 0.20f;
	/////////////////////////////////////////////////////////////////////////////////
	
	public ContexTab(Area area, FileManager loader, Clickable parent, WildcardsTab wildcardsTab, OperationTab operationTab) {
		super(area, loader, parent, wildcardsTab, operationTab);
		
		LabelConfiguration labelConfiguration = configureLabel();
		label = new Label(labelConfiguration, this);
		
		ScrollPanelConfiguration panelConfiguration = configureScrollPanel();
		panel = new ScrollPanel(panelConfiguration, loader, this);
		
		TextFieldConfiguration contextConfiguration = configureContextTextField();
		thatInput = panel.addObjectImmediately(TextField.class, contextConfiguration);

		thatInput = panel.substituteObject(thatInput, new WildcardTextField(thatInput));
		thatInput = panel.substituteObject(thatInput, new ContextMenuTextField(wildcardsTab, thatInput));
		thatInput = panel.substituteObject(thatInput, new AutofillAllTextField(thatInput));
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
		configuration.backgroundAlpha 	= SCROLL_AREA_ALPHA;
		
		configuration.drawBackground = false;
		configuration.includeBoundaryPadding = false;
		
		return configuration;
	}
	
	private TextFieldConfiguration configureContextTextField() {
		TextFieldConfiguration configuration = new TextFieldConfiguration();
		
		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		configuration.characters	= Coding.signs + "()";
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border				= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	private LabelConfiguration configureLabel() {
		LabelConfiguration configuration = new LabelConfiguration();
		
		configuration.area.setX(area.getX() + area.getW() * LABEL_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * LABEL_Y_PARAMETER);
		configuration.area.setW(area.getW() * LABEL_W_PARAMETER);
		configuration.area.setH(area.getH() * LABEL_H_PARAMETER);
		
		configuration.fontname		= FontType.GUI_FONT;
		configuration.color 		= Consts.MAIN_FONT_COLOR;
		configuration.label			= "Match question only if my previous answer was as follows";
		
		return configuration;
	}

	@Override
	public boolean hasAimlElement(AIMLGeneratorArgument argument) {
		return !thatInput.getInput().isEmpty();
	}

	@Override
	public List<AIMLElement> getAimlElements(AIMLGeneratorArgument argument, Wildcard wildcard) {
		String that = thatInput.getInput();
		if(that.isEmpty())
			return Collections.emptyList();
		else
			return Collections.<AIMLElement>singletonList(new That(that));
	}

	@Override
	public void onTabEnter() {
		AliceBot bot = CurrentBot.instance;

		if(bot != null) {
			Context context = bot.getContext();

			Set<String> autofillSet = new TreeSet<>();
			for(int index = 0; index != NUMBER_OF_REMEMBERED_RESPONSES; index++) {
				Response response = context.getResponses(index);
				if(response == null)
					break;
				autofillSet.add(response.getWholeExpression());
			}

			((AutofillAllTextField) thatInput).setAutofillSet(autofillSet);
		}
	}
	
	@Override @UnhandledMethod
	public boolean isUpdateNeeded() {
		return false;
	}

	@Override @UnhandledMethod
	public Wildcard getWildcard() {
		return null;
	}

	@Override
	public boolean verifyWildcardAcceptance(Wildcard wildcard) {
		return true;
	}

	@Override @UnhandledMethod
	public boolean contains(Position position) {
		return false;
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
		panel.executeMousePress(position);
		return false;
	}

	@Override
	public boolean executeMousePressRight(Position position) {
		panel.executeMousePressRight(position);
		return false;
	}
	
	@Override @UnhandledMethod
	public boolean executeMouseRelease(Position position) {
		return false;
	}

	@Override
	public void update(float delta) {
		panel.update(delta);
	}

	@Override @UnhandledMethod
	public void performUpdate() {}
	
	@Override
	public void draw(SmartSpriteBatch batch   ) {
		panel.draw(batch);
		label.draw(batch);
	}
	
	@Override @UnhandledMethod
	public void onTabLeave() {}

	@Override @UnhandledMethod
	public void clear() {}

	@Override @UnhandledMethod
	public void dispose() {}
}
