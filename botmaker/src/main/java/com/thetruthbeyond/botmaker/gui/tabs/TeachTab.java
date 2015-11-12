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

package com.thetruthbeyond.botmaker.gui.tabs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Set;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import  com.thetruthbeyond.chatterbean.AliceBot;
import  com.thetruthbeyond.chatterbean.Context;
import  com.thetruthbeyond.chatterbean.aiml.*;
import  com.thetruthbeyond.chatterbean.graph.Graphmaster;
import  com.thetruthbeyond.chatterbean.parser.api.AliceBotExplorerException;
import  com.thetruthbeyond.chatterbean.text.structures.Sentence;
import  com.thetruthbeyond.chatterbean.text.structures.Transformations;

import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.thetruthbeyond.bot.CurrentBot;
import com.thetruthbeyond.botmaker.BotMaker;
import com.thetruthbeyond.botmaker.gui.objects.buttons.TabButton;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.emitters.*;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.enums.ValidationFailureCause;
import com.thetruthbeyond.gui.interfaces.AIMLGenerator;
import com.thetruthbeyond.gui.interfaces.AIMLGeneratorArgument;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.simple.Submit;
import com.thetruthbeyond.botmaker.gui.objects.buttons.tabs.*;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanel;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.*;
import com.thetruthbeyond.gui.objects.tabs.Tab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.wildcards.FullWildcardsTab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.OperationTab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.wildcards.StarWildcardsTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.structures.wildcard.*;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

import com.thetruthbeyond.botmaker.gui.tabs.teachtab.*;
import com.thetruthbeyond.botmaker.logic.BotExplorer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.thetruthbeyond.gui.configuration.Consts.RESOLUTION;
import static com.thetruthbeyond.gui.configuration.Consts.RES_1366x768;

@SuppressWarnings("FieldCanBeLocal")
public class TeachTab extends Tab implements AIMLGenerator {

	// Top panel. ///////////////////////////////////////////////////////////////////
	private final ScrollPanel topPanel;
	
	private static final float TOP_PANEL_X_PARAMETER 		= 0.0400f;
	private static final float TOP_PANEL_Y_PARAMETER 		= 0.0800f;
	private static final float TOP_PANEL_W_PARAMETER 		= RESOLUTION == RES_1366x768 ? 0.921f : 0.920f;
	private static final float TOP_PANEL_H_PARAMETER 		= 0.2630f;
	private static final float TOP_PANEL_P_PARAMETER 		= 0.0000f;
	private static final float TOP_PANEL_GAP_H_PARAMETER 	= 0.0500f;
	private static final float TOP_PANEL_GAP_W_PARAMETER 	= 0.0105f;
	
	private static final int TOP_PANEL_COLUMNS = 1;
	private static final int TOP_PANEL_ROWS = 2;
	/////////////////////////////////////////////////////////////////////////////////
	
	// Question text field. /////////////////////////////////////////////////////////
	private final WildcardTextField questionWTF;
	private final AutofillTextField questionATF;
	private final DefaultMessageTextField questionDMTF;
	/////////////////////////////////////////////////////////////////////////////////
	
	// My answer text field. ////////////////////////////////////////////////////////
	private final ContextMenuTextField answerCMTF;
	private final AutofillTextField answerATF;
	private final DefaultMessageTextField answerDMTF;
	private final OperationTextField answerOTF;
	/////////////////////////////////////////////////////////////////////////////////
	
	// Bottom panel. ////////////////////////////////////////////////////////////////
	private final ScrollPanel botPanel;
	
	private static final float PANEL_X_PARAMETER 	= 0.04f;
	private static final float PANEL_Y_PARAMETER 	= 0.46f;
	private static final float PANEL_W_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.921f : 0.920f;
	private static final float PANEL_H_PARAMETER 	= 0.36f;
	/////////////////////////////////////////////////////////////////////////////////
		
	// Buttons. /////////////////////////////////////////////////////////////////////
	private final List<TabButton> buttons = new ArrayList<>(6);
	
	private static final float BUTTONS_X_PARAMETER 			= 0.045f;
	private static final float BUTTONS_Y_PARAMETER 			= 0.36f;
	private static final float BUTTONS_PRESSED_Y_PARAMETER 	= 0.38f;
	private static final float PRESS_TIME 					= 0.2f;

	private static final int BUTTON_X_OFFSET = -3;

	private final int BUTTON_START_Y;
	private final int BUTTON_DIFF_Y;

	private final float[] elapsedPressTime;
	/////////////////////////////////////////////////////////////////////////////////
		
	// Tabs. ////////////////////////////////////////////////////////////////////////
	private final Map<Class<? extends TeachSubTab>, TeachSubTab> tabs = new HashMap<>();
	private final Map<Class<? extends AIMLGenerator>, AIMLGenerator> generators = new HashMap<>();

	private TeachSubTab currentTab;
	
	private int currentTabIndex = 0;
	/////////////////////////////////////////////////////////////////////////////////
		
	// Topic field. /////////////////////////////////////////////////////////////////
	private final AutofillTextField topicATF;
	private final DefaultMessageTextField topicDMTF;
	private final Set<String> topics = new TreeSet<>();
	
	private static final float TOPIC_TEXT_FIELD_X_PARAMETER 	= 0.560f;
	private static final float TOPIC_TEXT_FIELD_Y_PARAMETER 	= 0.820f;
	private static final float TOPIC_TEXT_FIELD_W_PARAMETER 	= RESOLUTION == RES_1366x768 ? 0.401f : 0.400f;
	private static final float TOPIC_TEXT_FIELD_H_PARAMETER 	= 0.100f;
	/////////////////////////////////////////////////////////////////////////////////
	
	// Submit. //////////////////////////////////////////////////////////////////////
	private final Button submit;
	
	private static final float SUBMIT_X_PARAMETER 	= 0.040f;
	private static final float SUBMIT_Y_PARAMETER 	= 0.823f;
	/////////////////////////////////////////////////////////////////////////////////
		
	// ContextTab menu. /////////////////////////////////////////////////////////////
	private final StarWildcardsTab starcardsTab;
				
	private static final float VARIABLE_PANEL_X_PARAMETER 		= 0.00f;
	private static final float VARIABLE_PANEL_Y_PARAMETER 		= 0.00f;
	private static final float VARIABLE_PANEL_W_PARAMETER 		= 0.34f;
	private static final float VARIABLE_PANEL_H_PARAMETER 		= 0.18f;
	/////////////////////////////////////////////////////////////////////////////////
		
	// ContextTab menu. /////////////////////////////////////////////////////////////
	private final FullWildcardsTab wildcardsTab;
		
	private static final float CONTEXT_PANEL_X_PARAMETER 		= 0.00f;
	private static final float CONTEXT_PANEL_Y_PARAMETER 		= 0.00f;
	private static final float CONTEXT_PANEL_W_PARAMETER 		= 0.34f;
	private static final float CONTEXT_PANEL_H_PARAMETER 		= 0.54f;
	/////////////////////////////////////////////////////////////////////////////////
		
	// OperationTab. ////////////////////////////////////////////////////////////////
	private final OperationTab operationTab;

	private static final float OPERATION_TAB_X_PARAMETER 	= 0.20f;
	private static final float OPERATION_TAB_Y_PARAMETER 	= 0.20f;
	private static final float OPERATION_TAB_W_PARAMETER 	= 0.60f;
	private static final float OPERATION_TAB_H_PARAMETER 	= 0.60f;
	/////////////////////////////////////////////////////////////////////////////////

	// Emitters. ////////////////////////////////////////////////////////////////////
	private final OnTabSwitch onSwitch;
	private final OnValidateFailure onValidationFailure;
	/////////////////////////////////////////////////////////////////////////////////

	private AliceBot bot;
	
	private final SmartTexture background;
	private final TextureRegion mask;

	private BotExplorer explorer;

	public TeachTab(Area area, FileManager loader, Clickable parent) {
		super(area, parent);
	
		background = loader.getTexture("WoodBackground2");

		onSwitch = new OnTabSwitch(this);
		addEmitter(onSwitch);

		onValidationFailure = new OnValidateFailure(this);
		addEmitter(onValidationFailure);
		
		Area contextArea = new Area();
		contextArea.setX(area.getX() + area.getW() * CONTEXT_PANEL_X_PARAMETER);
		contextArea.setY(area.getY() + area.getH() * CONTEXT_PANEL_Y_PARAMETER);
		contextArea.setW(area.getW() * CONTEXT_PANEL_W_PARAMETER);
		contextArea.setH(area.getH() * CONTEXT_PANEL_H_PARAMETER);
		
		wildcardsTab = new FullWildcardsTab(contextArea, loader, BotMaker.darkness, this);
		
		contextArea.setX(area.getX() + area.getW() * VARIABLE_PANEL_X_PARAMETER);
		contextArea.setY(area.getY() + area.getH() * VARIABLE_PANEL_Y_PARAMETER);
		contextArea.setW(area.getW() * VARIABLE_PANEL_W_PARAMETER);
		contextArea.setH(area.getH() * VARIABLE_PANEL_H_PARAMETER);
		
		starcardsTab = new StarWildcardsTab(contextArea, loader, BotMaker.darkness, this);
		
		ScrollPanelConfiguration topPanelConfiguration = configureTopPanel();
		topPanel = new ScrollPanel(topPanelConfiguration, loader, this);
		
		// Question text field initialization. //////////////////////////////////////////////////////
		TextField questionNTF = topPanel.addObjectImmediately(TextField.class, configureTextField());
		
		questionATF  = topPanel.substituteObject(questionNTF, new AutofillTextField(questionNTF));
		questionWTF  = topPanel.substituteObject(questionATF, new WildcardTextField(questionATF));
		questionDMTF = topPanel.substituteObject(questionWTF, new DefaultMessageTextField(questionWTF));
		questionDMTF.setDefaultMessage("Type here user question.");
		
		PromptTextFieldConfiguration configurationPTFquestion = new PromptTextFieldConfiguration();
		configurationPTFquestion.alpha = 0.4f;
		configurationPTFquestion.blinktime = 0.8f;
		configurationPTFquestion.promptoffset = 0;
		TextField questionPTF = topPanel.substituteObject(questionDMTF, new PromptTextField(questionDMTF, loader, configurationPTFquestion));

		OneSentenceTextField questionOSTF = new OneSentenceTextField(questionPTF);

		Set<String> splitters = obtainSplitters();
		questionOSTF.setSentenceSplitters(splitters);

		getObserver().observeEmitter(questionOSTF.getEmitter(OnValidateFailure.Id));
		topPanel.substituteObject(questionPTF, questionOSTF);
		//////////////////////////////////////////////////////////////////////////////////////////////
		
		// Answer text field initialization. /////////////////////////////////////////////////////////
		TextField answerNTF = topPanel.addObjectImmediately(TextField.class, configureTextField());
		
		answerATF  = topPanel.substituteObject(answerNTF, new AutofillTextField(answerNTF));
		answerCMTF = topPanel.substituteObject(answerATF, new ContextMenuTextField(wildcardsTab, answerATF));
		answerDMTF = topPanel.substituteObject(answerCMTF, new DefaultMessageTextField(answerCMTF));
		answerDMTF.setDefaultMessage("Type here chatbot's answer.");

		Area operationTabArea = new Area();
		operationTabArea.setX(Math.round(getX() + getW() * OPERATION_TAB_X_PARAMETER));
		operationTabArea.setY(Math.round(getY() + getH() * OPERATION_TAB_Y_PARAMETER));
		operationTabArea.setW(Math.round(getW() * OPERATION_TAB_W_PARAMETER));
		operationTabArea.setH(Math.round(getH() * OPERATION_TAB_H_PARAMETER));

		operationTab = new OperationTab(operationTabArea, loader, this);

		answerOTF = topPanel.substituteObject(answerDMTF, new OperationTextField(operationTab, loader, answerDMTF));

		PromptTextFieldConfiguration configurationPTFanswer = new PromptTextFieldConfiguration();
		configurationPTFanswer.alpha = 0.4f;
		configurationPTFanswer.blinktime = 0.8f;
		configurationPTFanswer.promptoffset = 0;
		answerNTF = topPanel.substituteObject(answerOTF, new PromptTextField(answerOTF, loader, configurationPTFanswer));

		getObserver().observeEmitter(answerNTF.getEmitter(OnValidateFailure.Id));
		//////////////////////////////////////////////////////////////////////////////////////////////
		
		ScrollPanelConfiguration botPanelConfiguration = configureBotPanel();
		botPanel = new ScrollPanel(botPanelConfiguration, loader, this);
		
		TextFieldConfiguration topicConfiguration = configureTopicTextField();
		TextField topicInput = new TextField(topicConfiguration, loader, this);
		
		topicInput = new CapitalCaseTextField(topicInput);
		topicATF = new AutofillTextField(topicInput);
		topicDMTF = new DefaultMessageTextField(topicATF).setDefaultMessage("Type topic for that rule.");
		
		buttons.add( new ContexButton(loader, this) );
		buttons.add( new VariabButton(loader, this) );
		buttons.add( new ClauseButton(loader, this) );
		buttons.add( new RandomButton(loader, this) );
		buttons.add( new RecursButton(loader, this) );
		
		elapsedPressTime = new float[buttons.size()];
		
		int buttonX = (int)(area.getX() + area.getW() * BUTTONS_X_PARAMETER);
		int buttonY = (int)(area.getY() + area.getH() * BUTTONS_Y_PARAMETER);
		
		BUTTON_START_Y = buttonY;
		BUTTON_DIFF_Y  = (int)(area.getY() + area.getH() * BUTTONS_PRESSED_Y_PARAMETER) - BUTTON_START_Y;
		
		boolean isFirstButton = true;
		for(TabButton button : buttons) {
			if(isFirstButton) {
				Position position = new Position(buttonX + button.getW() / 2, BUTTON_START_Y + button.getH() / 2 + BUTTON_DIFF_Y);
				button.setPosition(position);
				
				// Setting press action.
				button.setPressed();
				
				// Make button already hovered.
				button.update(10);
				
				elapsedPressTime[0] = PRESS_TIME;
				isFirstButton = false;
			} else
				button.setPosition( new Position(buttonX + button.getW() / 2, BUTTON_START_Y + button.getH() / 2) );
			buttonX = buttonX + button.getW() + BUTTON_X_OFFSET;
		}
		
		submit = new Submit(loader, this);
		buttonX = (int)(area.getX() + area.getW() * SUBMIT_X_PARAMETER);
		buttonY = (int)(area.getY() + area.getH() * SUBMIT_Y_PARAMETER);
		submit.setPosition( new Position(buttonX + submit.getW() / 2, buttonY + submit.getH() / 2)  );
		
		mask = new TextureRegion(background.getTextureRegion(), (int)(area.getW() * PANEL_X_PARAMETER), (int)(area.getH() * PANEL_Y_PARAMETER), botPanel.getW(), botPanel.getH());
		
		Area tabArea = new Area(botPanel.getArea()).cutArea(Consts.BORDER_SIZE);
		
		tabs.put(ContexTab.class, new ContexTab(tabArea, loader, this, starcardsTab, operationTab));
		tabs.put(VariabTab.class, new VariabTab(tabArea, loader, this, wildcardsTab, operationTab));
		tabs.put(ClauseTab.class, new ClauseTab(tabArea, loader, this, wildcardsTab, operationTab));
		tabs.put(RandomTab.class, new RandomTab(tabArea, loader, this, wildcardsTab, operationTab));
		tabs.put(RecursTab.class, new RecursTab(tabArea, loader, this, wildcardsTab, operationTab));
		
		currentTab = tabs.get(ContexTab.class);

		generators.put(ContexTab.class, (AIMLGenerator) tabs.get(ContexTab.class));
		generators.put(ClauseTab.class, (AIMLGenerator) tabs.get(ClauseTab.class));
		generators.put(RandomTab.class, (AIMLGenerator) tabs.get(RandomTab.class));
		generators.put(RecursTab.class, (AIMLGenerator) tabs.get(RecursTab.class));
		generators.put(OperationTextField.class, answerOTF);
	}
	
	private ScrollPanelConfiguration configureTopPanel() {
		ScrollPanelConfiguration configuration = new ScrollPanelConfiguration();
		configuration.area.setX(area.getX() + area.getW() * TOP_PANEL_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * TOP_PANEL_Y_PARAMETER);
		configuration.area.setW(area.getW() * TOP_PANEL_W_PARAMETER);
		configuration.area.setH(area.getH() * TOP_PANEL_H_PARAMETER);
		
		configuration.border			= Consts.BORDER_SIZE;
		configuration.backgroundAlpha 	= Consts.BACKGROUND_ALPHA;
		
		configuration.columnsNumber = TOP_PANEL_COLUMNS;
		configuration.rowsNumber 	= TOP_PANEL_ROWS;
		
		configuration.relativeGapW		= TOP_PANEL_GAP_W_PARAMETER;
		configuration.relativeGapH		= TOP_PANEL_GAP_H_PARAMETER;
		configuration.relativePadding	= TOP_PANEL_P_PARAMETER;
		
		return configuration;
	}
	
	private TextFieldConfiguration configureTextField() {
		TextFieldConfiguration configuration = new TextFieldConfiguration();
		
		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		configuration.characters	= Coding.signs + "()[]";
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border				= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	private ScrollPanelConfiguration configureBotPanel() {
		ScrollPanelConfiguration configuration = new ScrollPanelConfiguration();
		configuration.area.setX(area.getX() + area.getW() * PANEL_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * PANEL_Y_PARAMETER);
		configuration.area.setW(area.getW() * PANEL_W_PARAMETER);
		configuration.area.setH(area.getH() * PANEL_H_PARAMETER);
		
		configuration.border			= Consts.BORDER_SIZE;
		configuration.backgroundAlpha 	= Consts.BACKGROUND_ALPHA;
		
		configuration.columnsNumber = 1;
		configuration.rowsNumber = 1;
		
		return configuration;
	}
	
	private TextFieldConfiguration configureTopicTextField() {
		TextFieldConfiguration configuration = new TextFieldConfiguration();
		configuration.area.setX(area.getX() + area.getW() * TOPIC_TEXT_FIELD_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * TOPIC_TEXT_FIELD_Y_PARAMETER);
		configuration.area.setW(area.getW() * TOPIC_TEXT_FIELD_W_PARAMETER);
		configuration.area.setW(Math.min(configuration.area.getW(), botPanel.getX() + botPanel.getW() - configuration.area.getX()));
		configuration.area.setH(area.getH() * TOPIC_TEXT_FIELD_H_PARAMETER);
		
		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= new Color(0.4f, 0.5f, 0.2f, 1.0f);
		configuration.characters	= Coding.signs;
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border				= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	@Override
	public void onTabEnter() {

		bot = CurrentBot.instance;
		if(bot != null) {
			Context context = bot.getContext();
			explorer = new BotExplorer(context.getProperty("name"));

			setAnswerAutofillSet();

			try {
				List<File> aimlFiles = explorer.getAIMLFiles();

				topics.clear();

				for(File file : aimlFiles) {
					String fullname = file.getName();
					topics.add(fullname.substring(0, fullname.indexOf('.')));
				}

				topicATF.setAutofillSet(topics);
			} catch(AliceBotExplorerException exception) {
				BotMaker.messages.showMessage("Topic set cannot be established. " + exception.getMessage());
			}
		}
	}

	private Set<String> obtainSplitters() {

		Set<String> set = new HashSet<>();

		bot = CurrentBot.instance;
		if(bot != null) {
			Context context = bot.getContext();
			explorer = new BotExplorer(context.getProperty("name"));

			try {
				InputStream stream = explorer.getSplitters();

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(stream);

				NodeList list = document.getElementsByTagName("splitter");
				for(int i = 0, n = list.getLength(); i != n; ++i) {
					Node node = list.item(i);
					if(node instanceof Element) {
						Element element = (Element) node;
						if(element.getAttribute("type").equals("sentence"))
							set.add(element.getAttribute("value"));
					}
				}
			} catch(AliceBotExplorerException | ParserConfigurationException | IOException | SAXException exception) {
				Logger logger = new Logger();
				logger.writeMessage("Warning", "Splitters file could not be properly parsed or read");
				logger.writeError(exception);

				BotMaker.messages.showMessage("Splitters file could not be properly parsed or read.");
			}
		}

		return set;
	}

	private void setAnswerAutofillSet() {

		bot = CurrentBot.instance;

		if(bot != null) {
			Context context = bot.getContext();
			explorer = new BotExplorer(context.getProperty("name"));

			Set<String> variables = new TreeSet<>();
			for(String variable : context.getPropertiesNames()) {
				if(variable.equals("id"))
					continue;
				String bracketVariable = "[" + variable + "]";
				variables.add(bracketVariable);
				answerCMTF.markWildcard(new VariableCard(bracketVariable, Color.MAROON).setReadOnly(true));
			}

			for(String variable : context.getPredicatesNames()) {
				String bracketVariable = "[" + variable + "]";
				variables.add(bracketVariable);
				answerCMTF.markWildcard(new VariableCard(bracketVariable, Color.MAROON).setReadOnly(false));
			}

			answerCMTF.markRegularExpression("\\[.+\\]");

			answerATF.setAutofillSet(variables);
		}
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(emitterID == OnValidateFailure.Id) {
			OnValidateFailure onValidateFailure = object.getEmitter(emitterID);
			if(onValidateFailure.getCause() == ValidationFailureCause.MoreThanOneSentence)
				BotMaker.messages.showMessage("You can enter only one valid sentence.");
			else

			if(onValidateFailure.getCause() == ValidationFailureCause.WildcardAlreadyPresent)
				BotMaker.messages.showMessage("There can be only one operation wildcard of any kind present in a text field.");
		} else

		if(emitterID == OnPress.Id) {
			Wildcard wildcard = wildcardsTab.getWildcard();

			// Validation.
			if(!answerCMTF.isFocused() && !currentTab.verifyWildcardAcceptance(wildcard)) {
				BotMaker.messages.showMessage("Token " + wildcard.getName() + " cannot be added to an active input field.");
				onValidationFailure.signalValidationFailure(ValidationFailureCause.RecursionAttempt);
			}

			if(wildcard instanceof StarCard) {
				// Variable wildcard validation.
				StarCard starcard = (StarCard) wildcard;
				if(!questionWTF.isFocused() && !questionWTF.hasWildcard(starcard)) {
					BotMaker.messages.showMessage("The variable wildcard must appear in the rule's pattern in the first place.");
					onValidationFailure.signalValidationFailure(ValidationFailureCause.MissingWildcard);
				} else

				if(questionWTF.isFocused() && questionWTF.hasWildcard(starcard)) {
					BotMaker.messages.showMessage("You are restricted to only one variable wildcard of each color.");
					onValidationFailure.signalValidationFailure(ValidationFailureCause.WildcardAlreadyPresent);
				} else
					assignWildcard(starcard);
			}

			wildcardsTab.hide();
		}
	}

	private void assignWildcard(StarCard starcard) {
		// Assign index number to the new starcard.
		for(int i = 0, index = 1, n = questionWTF.getWords().size(); i != n; ++i) {
			Wildcard card = questionWTF.getWordAsWildcard(i);
			if(card != null) {
				if(card.equals(starcard)) {
					starcard.setIndex(index);
					break;
				} else

				if(card instanceof StarCard)
					index++;
			}
		}
	}

	@Override @UnhandledMethod
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		if(wildcardsTab.executeMouseHover(position))
			return false;
		if(starcardsTab.executeMouseHover(position))
			return false;
		
		if(operationTab.isVisible()) {
			operationTab.executeMouseHover(position);
		} else {
			answerOTF.executeMouseHover(position);

			if(!wildcardsTab.contains(position)) {
				currentTab.executeMouseHover(position);
				submit.executeMouseHover(position);
			}
		}
		
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		if(wildcardsTab.executeMouseDrag(position))
			return false;
		if(starcardsTab.executeMouseDrag(position))
			return false;
		
		if(operationTab.isVisible()) {
			operationTab.executeMouseDrag(position);
		} else {
			if(!wildcardsTab.contains(position)) {
				currentTab.executeMouseDrag(position);
				submit.executeMouseDrag(position);
			}
		}
		
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {

		if(wildcardsTab.executeMousePress(position))
			return false;

		if(starcardsTab.executeMousePress(position))
			return false;

		if(operationTab.isVisible()) {
			operationTab.executeMousePress(position);
		} else {
			boolean breakFocus = true;
	
			// Tab switching.
			for(int i = 0; i != buttons.size(); i++) {
	
				TabButton button = buttons.get(i);
				if(currentTabIndex != i && button.contains(position)) {
					for(int j = 0; j != buttons.size(); j++)
						buttons.get(j).executeMouseRelease(Position.NullPosition);
	
					button.executeMousePress(position);
					button.executeMouseHover(position);
	
					if(currentTab.isUpdateNeeded()) {
						currentTab.performUpdate();
						if(currentTab instanceof VariabTab)
							setAnswerAutofillSet();
					}
	
					currentTab.onTabLeave();
	
					currentTab = tabs.get(button.getAssociatedTeachTabType());
					currentTabIndex = i;

					onSwitch.signalSwitch(button.getAssociatedTeachTabType());
					currentTab.onTabEnter();
					
					breakFocus = false;
					break;
				}
			}
	
			// Tab press action.
			if(currentTab.executeMousePress(position)) {
				Wildcard wildcard = currentTab.getWildcard();
				if(wildcard != null) {
					if(wildcard instanceof StarCard) {
						// Variable wildcard validation.
						StarCard starcard = (StarCard) wildcard;
						if(answerCMTF.isFocused()) {
							if(questionWTF.hasWildcard(starcard))
								assignWildcard(starcard);
							else
								BotMaker.messages.showMessage("The variable wildcard must appear in the rule's pattern in the first place.");
						} else {
							if(!questionWTF.hasWildcard(starcard))
								questionWTF.addWildcard(starcard);
							else
								BotMaker.messages.showMessage("You are restricted to only one variable wildcard of each color.");
						}
					} else
						answerCMTF.addWildcard(wildcard);
					breakFocus = false;
				}
			}
	
			if(breakFocus) {
				questionWTF.executeMousePress(position);

				if(answerOTF.executeMousePress(position)) {
					if(currentTab.isUpdateNeeded()) {
						currentTab.performUpdate();
						if(currentTab instanceof VariabTab)
							setAnswerAutofillSet();
					}
				}
			}
	
			topicDMTF.executeMousePress(position);
	
			if(submit.contains(position)) {				
				if(teachBot())
					BotMaker.messages.showMessage("The rule was successfully added.");
				else
					BotMaker.messages.showMessage("Provide valid question and expected answer in order to proceed.");
			}
		}
		
		return true;
	}

	@Override
	public boolean executeMousePressRight(Position position) {
		if(operationTab.isVisible())
			operationTab.executeMousePressRight(position);
		else

		if(starcardsTab.isVisible())
			starcardsTab.executeMousePressRight(position);
		else

		if(wildcardsTab.isVisible())
			wildcardsTab.executeMousePressRight(position);
		else

		answerCMTF.executeMousePressRight(position);
		currentTab.executeMousePressRight(position);	
		return false;
	}
	
	@Override
	public boolean executeMouseRelease(Position position) {
		if(operationTab.isVisible())
			operationTab.executeMouseRelease(position);
		else {
			topPanel.executeMouseRelease(position);
			
			currentTab.executeMouseRelease(position);
			
			topicDMTF.executeMouseRelease(position);
			submit.executeMouseRelease(position);	
		}
		
		wildcardsTab.executeMouseRelease(position);
		starcardsTab.executeMouseRelease(position);
		return false;
	}

	private boolean teachBot() {
		// Creating category.
		Category category = new Category(makePattern(), makeTemplate());

		// Rule will not going to come into existence. Report this to the user.
		if(category.getPattern().isEmpty() || !category.getTemplate().hasChildren())
			return false;
		
		// Add new phrase to topic set if any were typed.
		String topicName = topicDMTF.getInput();
		if(!topics.contains(topicName)) {
			topics.add(topicName);
			topicATF.addAutofillElement(topicName);
		}
		
		Transformations transformations = bot.getTransformations();
		Graphmaster graph = bot.getGraphmaster();
		
		// Create that element and normalize it.
		List<AIMLElement> elements = generators.get(ContexTab.class).getAimlElements(null, null);
		for(AIMLElement element : elements)
			if(element instanceof NormalizedTag)
				((NormalizedTag) element).normalizeContent(transformations);
		category.appendChildren(elements);
		
		// Create topic element and normalize it.
		Sentence topic = transformations.makeSentence(topicName);
		if(topicName.isEmpty())
			topic = Sentence.ASTERISK;
		category.appendChild( new Topic(topic.getNormalized()) );
				
		// Check if category exists.
		boolean hasCategory = graph.hasCategory(category.getMatchPath());
		if(hasCategory) {
			graph.append(category);
			explorer.updateAIML(topicName, category);	
		} else {
			graph.append(category);
			explorer.addToAIML(topicName, category);
		}
		
		return true;
	}
	
	private Pattern makePattern() {
		List<String> words = questionWTF.getWords();
		StringBuilder input = new StringBuilder(words.size() * 10);

		for(int index = 0; index != words.size(); index++) {
			Wildcard wildcard = questionWTF.getWordAsWildcard(index);
			if(wildcard != null) {
				if(wildcard instanceof StarCard) {
					// Insert whitespace to make sure that wildcard will be properly added.
					if(((StarCard) wildcard).isHighPriority())
						input.append(" _ ");
					else
						input.append(" * ");
				}	
			} else
				input.append(words.get(index));
		}
		
		Transformations transformations = bot.getTransformations();
		
		Sentence sentence = transformations.makeSentence(input.toString());
		return new Pattern(sentence.getNormalized().trim());
	}
	
	private Template makeTemplate() {	
		List<String> words = answerCMTF.getWords();
		List<AIMLElement> elements = new LinkedList<>();
		
		StringBuilder freeText = new StringBuilder(words.size() * 5);
		for(int index = 0; index != words.size(); index++) {
			
			Wildcard wildcard = answerCMTF.getWordAsWildcard(index);
			if(wildcard != null) {
				
				// Adding free text.
				if(freeText.length() != 0) {
					elements.add( new Text(freeText.toString()) );
					freeText.delete(0, freeText.length());
				}

				wildcard.fillTemplate(elements, this);
			} else
				freeText.append(words.get(index));
		}
		
		// Adding free text.
		if(freeText.length() != 0)
			elements.add( new Text(freeText.toString().trim()) );

		Template template = new Template();
		template.appendChildren(elements);

		return template;
	}

	@Override
	public boolean hasAimlElement(AIMLGeneratorArgument argument) {
		boolean hasAimlElement = false;
		for(AIMLGenerator generator : generators.values())
			hasAimlElement = generator.hasAimlElement(argument);
		return hasAimlElement;
	}

	@Override
	public List<AIMLElement> getAimlElements(AIMLGeneratorArgument argument, Wildcard wildcard) {
		switch(wildcard.getName()) {

			case "(condition)": {
				return generators.get(ClauseTab.class).getAimlElements(argument, wildcard);
			}

			case "(random)": {
				return generators.get(RandomTab.class).getAimlElements(argument, wildcard);
			}

			case "(recursion)": {
				return generators.get(RecursTab.class).getAimlElements(argument, wildcard);
			}
			
			case "{set}": case "{exe}": {
				return generators.get(OperationTextField.class).getAimlElements(argument, wildcard);
			}
		}

		return null;
	}

	@Override
	public void update(float delta) {
		if(operationTab.isVisible())
			operationTab.update(delta);
		else {
			topPanel.update(delta);
			
			for(int i = 0; i != buttons.size(); i++) {
				Button button = buttons.get(i);
				if(currentTabIndex == i) {
					if(elapsedPressTime[i] < PRESS_TIME) {
						elapsedPressTime[i] = Math.min(elapsedPressTime[i] + delta, PRESS_TIME);
						button.changeY(BUTTON_START_Y + (elapsedPressTime[i] / PRESS_TIME) * BUTTON_DIFF_Y);
					}
				} else {
					if(elapsedPressTime[i] > 0) {
						elapsedPressTime[i] = Math.max(elapsedPressTime[i] - delta, 0);
						button.changeY(BUTTON_START_Y + (elapsedPressTime[i] / PRESS_TIME) * BUTTON_DIFF_Y);
					}
				}
				
				button.update(delta);
			}
			
			currentTab.update(delta);
			topicDMTF.update(delta);
			
			submit.update(delta);

			operationTab.update(delta);
		}
		
		// Right click context menu updating.
		wildcardsTab.update(delta);
		starcardsTab.update(delta);

		BotMaker.messages.update(delta);
	}

	@Override
	public void onTabLeave() {
		currentTab.onTabLeave();
	}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		batch.draw(background, area.getX(), area.getY(), area.getW(), area.getH());

		// draw top panel with question and answer text fields.
		topPanel.draw(batch);
			
		for(Button button : buttons)
			button.draw(batch);

		batch.draw(mask, botPanel.getX(), botPanel.getY(), botPanel.getW(), botPanel.getH());

		// Draw bottom background.
		botPanel.draw(batch);

		// Draw current sub tab on background.
		currentTab.draw(batch);

		// Draw topic text field beneath.
		topicDMTF.draw(batch);	

		// Draw submit button at the very end.
		submit.draw(batch);

		// Draw operation tab if it is visible.
		operationTab.draw(batch);
		
		// Draw context menus if they can be seen.
		wildcardsTab.draw(batch);
		starcardsTab.draw(batch);

		// Draw root level message shower.
		BotMaker.messages.draw(batch);
	}
	
	@Override @UnhandledMethod
	public void clear() {}

	@Override @UnhandledMethod
	public void dispose() {}
}
