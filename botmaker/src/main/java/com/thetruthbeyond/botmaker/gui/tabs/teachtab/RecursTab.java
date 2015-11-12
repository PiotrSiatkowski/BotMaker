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

import java.util.*;
import java.util.Set;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import  com.thetruthbeyond.chatterbean.AliceBot;
import  com.thetruthbeyond.chatterbean.aiml.*;

import com.badlogic.gdx.graphics.Color;
import com.thetruthbeyond.bot.CurrentBot;
import com.thetruthbeyond.botmaker.gui.tabs.TeachTab;
import com.thetruthbeyond.gui.action.emitters.OnTabSwitch;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.interfaces.AIMLGenerator;
import com.thetruthbeyond.gui.interfaces.AIMLGeneratorArgument;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.simple.Recursion1Button;
import com.thetruthbeyond.gui.objects.buttons.simple.Recursion3Button;
import com.thetruthbeyond.gui.objects.buttons.simple.Recursion2Button;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanel;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldDecorator;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.*;
import com.thetruthbeyond.gui.objects.tabs.overtabs.OperationTab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.WildcardsTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.structures.wildcard.RecursionCard;
import com.thetruthbeyond.gui.structures.wildcard.StarCard;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

public class RecursTab extends TeachSubTab implements AIMLGenerator {

	// Features scroll panel. ////////////////////////////////////////////////////////////
	private final ScrollPanel panel;
	
	private static final float SCROLL_PANEL_X_PARAMETER = 0.160f;
	private static final float SCROLL_PANEL_Y_PARAMETER = 0.055f;
	private static final float SCROLL_PANEL_W_PARAMETER = 0.830f;
	private static final float SCROLL_PANEL_H_PARAMETER = 0.890f;
	private static final float SCROLL_PANEL_P_PARAMETER = 0.060f;
	
	private static final int SCROLL_PANEL_COLUMNS = 1;
	private static final int SCROLL_PANEL_ROWS 	  = 3;
	
	private static final float SCROLL_PANEL_OFFSET_X_PARAMETER = 0.00f;
	private static final float SCROLL_PANEL_OFFSET_Y_PARAMETER = 0.04f;
	
	private static final float SCROLL_AREA_ALPHA = 0.70f;
	//////////////////////////////////////////////////////////////////////////////////////
	
	// Button. ///////////////////////////////////////////////////////////////////////////
	private final Button[] buttons = new Button[3];
	private final Color[] colors = { Color.RED, Color.BLUE, Color.GOLDENROD };
	
	private static final float BUTTONS_P_PARAMETER =  0.010f;
	//////////////////////////////////////////////////////////////////////////////////////
		
	private static final String DEFAULT_MESSAGE = "Choose rule you would like to use.";

	private final ContextMenuTextField[] inputCMTF = new ContextMenuTextField[3];
	private final AutofillAllTextField[] inputAATF = new AutofillAllTextField[3];
	
	private int currentIndex = 0;

	public RecursTab(Area area, FileManager loader, Clickable parent, WildcardsTab wildcardsTab, OperationTab operationTab) {
		super(area, loader, parent, wildcardsTab, operationTab);

		ScrollPanelConfiguration panelConfiguration = configureScrollPanel();
		panel = new ScrollPanel(panelConfiguration, loader, this);

		// Initiating recursion panel.
		TextFieldConfiguration textFieldConfiguration = configureTextField();
		configureInput(0, textFieldConfiguration, wildcardsTab);
		configureInput(1, textFieldConfiguration, wildcardsTab);
		configureInput(2, textFieldConfiguration, wildcardsTab);

		Clickable clickable = panel.getObject(0);
		Area buttonArea = new Area(Math.round(getX() + BUTTONS_P_PARAMETER * area.getW()), 0, 
								   Math.round((SCROLL_PANEL_X_PARAMETER - 2 * BUTTONS_P_PARAMETER) * area.getW()), clickable.getH());
		
		// Aligning buttons.
		buttons[0] = new Recursion1Button(buttonArea, loader, this);
		buttons[1] = new Recursion2Button(buttonArea, loader, this);
		buttons[2] = new Recursion3Button(buttonArea, loader, this);
		
		for(int i = 0; i != panel.getObjectsSize(); i++) {
			Button button = buttons[i];
			button.changeY(panel.getObject(i).getY());
		}

		OnTabSwitch emitter = parent.getEmitter(OnTabSwitch.Id);
		emitter.registerObserver(inputAATF[0].getObserver());
		emitter.registerObserver(inputAATF[1].getObserver());
		emitter.registerObserver(inputAATF[2].getObserver());
	}

	private void configureInput(int index, TextFieldConfiguration configuration, WildcardsTab wildcardsTab) {
		TextField input =  panel.addObjectImmediately(TextField.class, configuration);

		inputCMTF[index] = new ContextMenuTextField(wildcardsTab, input);
		inputCMTF[index].markWildcard(new StarCard("(wildcard)", Color.PURPLE));

		UppercaseTextField uppercase = new UppercaseTextField(inputCMTF[index]);
		uppercase.setException("(wildcard)");
		uppercase.setException("(recursion)");
		uppercase.setException("(condition)");
		uppercase.setException("(random)");

		TextFieldDecorator decorator = new InputFilterTextField(uppercase, "()");
		decorator = new DefaultMessageTextField(decorator).setDefaultMessage(DEFAULT_MESSAGE);

		inputAATF[index] = new AutofillAllTextField(decorator);

		// Substitute generated text field.
		panel.substituteObject(input, inputAATF[index]);

		// Register observers.
		parent.getEmitter(OnTabSwitch.Id).registerObserver(inputAATF[index].getObserver());
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
	
	@Override
	public void onTabEnter() {
		AliceBot bot = CurrentBot.instance;

		if(bot != null) {
			Set<String> transformed = new TreeSet<>();
			Set<String> patterns = bot.getGraphmaster().getAllPatterns();

			for(String string : patterns)
				transformed.add(string.replaceAll("\\*|_", "(wildcard)"));

			inputAATF[0].setAutofillSet(transformed);
			inputAATF[1].setAutofillSet(transformed);
			inputAATF[2].setAutofillSet(transformed);
		}
	}

	@Override @UnhandledMethod
	public boolean isUpdateNeeded() {
		return false;
	}

	@Override
	public Wildcard getWildcard() {
		return new RecursionCard("(recursion)", colors[currentIndex]).setIndex(currentIndex);
	}

	@Override
	public boolean verifyWildcardAcceptance(Wildcard wildcard) {
		return !(wildcard instanceof RecursionCard);
	}

	@Override
	public boolean hasAimlElement(AIMLGeneratorArgument argument) {
		for(TextField field : inputAATF)
			if(!field.getInput().isEmpty())
				return true;

		return false;
	}

	@Override
	public List<AIMLElement> getAimlElements(AIMLGeneratorArgument argument, Wildcard wildcard) {

		if(wildcard.getName().equals("(recursion)")) {
			int inputIndex = ((RecursionCard) wildcard).getIndex();

			List<AIMLElement> templateElements = new LinkedList<>();

			List<String> words = inputCMTF[inputIndex].getWords();
			if(!words.isEmpty()) {
				StringBuilder freeText = new StringBuilder(20);

				for(int index = 0; index != words.size(); index++) {

					Wildcard card = inputCMTF[inputIndex].getWordAsWildcard(index);
					if(card != null) {
						// Adding free text.
						if(freeText.length() != 0) {
							String text = freeText.toString();
							text = text.replaceAll("\\(wildcard\\)", "");
							text = text.replaceAll("\\s{2,}", " ");
							text = text.trim();

							templateElements.add(new Text(text));
							freeText.delete(0, freeText.length());
						}

						wildcard.fillTemplate(templateElements, (TeachTab) parent);
					} else
						freeText.append(words.get(index));
				}

				// Adding free text.
				if(freeText.length() != 0) {
					String text = freeText.toString();
					text = text.replaceAll("\\(wildcard\\)", "");
					text = text.replaceAll("\\s{2,}", " ");
					text = text.trim();

					templateElements.add(new Text(text));
					freeText.delete(0, freeText.length());
				}
			}

			if(templateElements.isEmpty())
				return Collections.emptyList();
			else {
				Srai srai = new Srai();
				srai.appendChildren(templateElements);
				return Collections.<AIMLElement>singletonList(srai);
			}
		} else
			return Collections.emptyList();
	}

	@Override @UnhandledMethod
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
			button.executeMouseDrag(position);
		
		panel.executeMouseDrag(position);
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		panel.executeMousePress(position);	
		
		for(int index = 0; index != buttons.length; index++) {
			Button button = buttons[index];
			if(button.executeMousePress(position)) {
				currentIndex = index;
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		for(Button button : buttons)
			button.executeMouseRelease(position);
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
		for(Button button : buttons)
			button.update(delta);
		
		panel.update(delta);
	}

	@Override @UnhandledMethod
	public void performUpdate() {}
	
	@Override @UnhandledMethod
	public void onTabLeave() {}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		for(Button button : buttons)
			button.draw(batch);
		panel.draw(batch);
	}

	@Override @UnhandledMethod
	public void clear() {}

	@Override @UnhandledMethod
	public void dispose() {}
}
