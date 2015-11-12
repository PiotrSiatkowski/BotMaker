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

package com.thetruthbeyond.gui.objects.tabs.overtabs.operations;

import  com.thetruthbeyond.chatterbean.aiml.AIMLElement;
import  com.thetruthbeyond.chatterbean.aiml.Text;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.emitters.OnPress;
import com.thetruthbeyond.gui.action.emitters.OnReachLimit;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.interfaces.AIMLGenerator;
import com.thetruthbeyond.gui.interfaces.AIMLGeneratorArgument;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.interfaces.WildcardGenerator;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.controllers.checkbox.CheckBoxList;
import com.thetruthbeyond.gui.objects.controllers.checkbox.CheckBoxListConfiguration;
import com.thetruthbeyond.gui.objects.controllers.label.Label;
import com.thetruthbeyond.gui.objects.controllers.label.LabelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.BackgroundArea;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.BackgroundAreaConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.AutofillTextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.DefaultMessageTextField;
import com.thetruthbeyond.gui.objects.tabs.overtabs.OperationSubTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.structures.wildcard.OperationCard;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

import  com.thetruthbeyond.chatterbean.AliceBot;
import  com.thetruthbeyond.chatterbean.aiml.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectIntMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteCodeTab extends OperationSubTab implements AIMLGenerator, WildcardGenerator {

	// Panel background. //////////////////////////////////////////////////////////////////////////////
	private final BackgroundArea background;
			
	private static final float BACKGROUND_X_PARAMETER = 0.050f;
	private static final float BACKGROUND_Y_PARAMETER = 0.100f;
	private static final float BACKGROUND_W_PARAMETER = 0.902f;
	private static final float BACKGROUND_H_PARAMETER = 0.480f;
	///////////////////////////////////////////////////////////////////////////////////////////////////
		
	// Check list. ////////////////////////////////////////////////////////////////////////////////////
	private final CheckBoxList box;
		
	private static final float CHECK_BOX_LIST_X_PARAMETER = 0.81f;
	private static final float CHECK_BOX_LIST_Y_PARAMETER = 0.10f;
	private static final float CHECK_BOX_LIST_W_PARAMETER = 0.11f;
	private static final float CHECK_BOX_LIST_H_PARAMETER = 0.48f;
	///////////////////////////////////////////////////////////////////////////////////////////////////
		
	// User setting. //////////////////////////////////////////////////////////////////////////////////
	private final Label userLabel;
			
	private static final float LABEL_USER_X_PARAMETER = 0.10f;
	private static final float LABEL_USER_W_PARAMETER = 0.67f;
	///////////////////////////////////////////////////////////////////////////////////////////////////
			
	// Topic setting. /////////////////////////////////////////////////////////////////////////////////
	private final Label topicLabel;
				
	private static final float LABEL_TOPIC_X_PARAMETER = 0.10f;
	private static final float LABEL_TOPIC_W_PARAMETER = 0.68f;
	///////////////////////////////////////////////////////////////////////////////////////////////////
		
	///////////////////////////////////////////////////////////////////////////////////////////////////
	private final AutofillTextField val;

	private static final float VALUE_X_PARAMETER = 0.05f;
	private static final float VALUE_Y_PARAMETER = 0.60f;
	private static final float VALUE_W_PARAMETER = 0.90f;
	private static final float VALUE_H_PARAMETER = 0.20f;
	///////////////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////////////
	private AIMLGeneratorArgument currentClient;

	private final ObjectIntMap<AIMLGeneratorArgument> choice = new ObjectIntMap<>(20);
	private final Map<AIMLGeneratorArgument, String> values = new HashMap<>(20);

	private final Wildcard exeCard = new OperationCard("{exe}", Color.FIREBRICK);
	///////////////////////////////////////////////////////////////////////////////////////////////////

	public ExecuteCodeTab(Area area, FileManager loader, Clickable parent) {
		super(area, parent);

		TextFieldConfiguration configuration = new TextFieldConfiguration();
		configuration.area.setX(area.getX() + area.getW() * VALUE_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * VALUE_Y_PARAMETER);
		configuration.area.setW(area.getW() * VALUE_W_PARAMETER);
		configuration.area.setH(area.getH() * VALUE_H_PARAMETER);

		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		configuration.characters 	= Coding.signs;

		configuration.relativeTextPadding = Consts.RELATIVE_TEXT_PADDING;
		configuration.border = Consts.BORDER_SIZE;
		
		TextField valField = new TextField(configuration, loader, this);
		valField = new DefaultMessageTextField(valField).setDefaultMessage("New setting value.");
		val = new AutofillTextField(valField);

		val.getEmitter(OnReachLimit.Id).registerObserver(parent.getObserver());

		// Configure check box list.
		box = new CheckBoxList(configureCheckBoxList(), loader, this);

		// Configure check box list.
		LabelConfiguration labelConfiguration = configureUserLabel();
		labelConfiguration.area.setY(box.getAreaOfBox(0).getY());
		labelConfiguration.area.setH(box.getAreaOfBox(0).getH());
		userLabel = new Label(labelConfiguration, this);

		// Configure check box list.
		labelConfiguration = configureTopicLabel();
		labelConfiguration.area.setY(box.getAreaOfBox(1).getY());
		labelConfiguration.area.setH(box.getAreaOfBox(1).getH());
		topicLabel = new Label(labelConfiguration, this);
		
		BackgroundAreaConfiguration backgroundConfiguration = configureBackground();
		backgroundConfiguration.area.setH(box.getH());
		background = new BackgroundArea(backgroundConfiguration, loader, this);
	}

	private BackgroundAreaConfiguration configureBackground() {
		BackgroundAreaConfiguration configuration = new BackgroundAreaConfiguration();

		configuration.area.setX(area.getX() + BACKGROUND_X_PARAMETER * area.getW());
		configuration.area.setY(area.getY() + BACKGROUND_Y_PARAMETER * area.getH());
		configuration.area.setW(BACKGROUND_W_PARAMETER * area.getW());
		configuration.area.setH(BACKGROUND_H_PARAMETER * area.getH());
		
		configuration.border 			= BORDER_SIZE;
		configuration.backgroundAlpha 	= Consts.BACKGROUND_ALPHA;
		
		return configuration;
	}
	
	private CheckBoxListConfiguration configureCheckBoxList() {
		CheckBoxListConfiguration configuration = new CheckBoxListConfiguration();

		configuration.area.setX(area.getX() + CHECK_BOX_LIST_X_PARAMETER * area.getW());
		configuration.area.setY(area.getY() + CHECK_BOX_LIST_Y_PARAMETER * area.getH());
		configuration.area.setW(CHECK_BOX_LIST_W_PARAMETER * area.getW());
		configuration.area.setH(CHECK_BOX_LIST_H_PARAMETER * area.getH());
		
		configuration.alignVertical 		= true;
		configuration.fitHeightByMinimizing = true;
		configuration.setIdealSize 			= true;
		
		configuration.border 		= BORDER_SIZE;
		configuration.padding		= 2;
		configuration.boxes 		= 2;
		configuration.relativeGap 	= 0.05f;

		return configuration;
	}
	
	private LabelConfiguration configureUserLabel() {
		LabelConfiguration configuration = new LabelConfiguration();

		configuration.area.setX(area.getX() + LABEL_USER_X_PARAMETER * area.getW());
		configuration.area.setW(LABEL_USER_W_PARAMETER * area.getW());

		configuration.color		 = Color.OLIVE;
		configuration.fontname	 = FontType.GUI_FONT;
		configuration.label		 = "change chat user";

		return configuration;
	}
	
	private LabelConfiguration configureTopicLabel() {
		LabelConfiguration configuration = new LabelConfiguration();

		configuration.area.setX(area.getX() + LABEL_TOPIC_X_PARAMETER * area.getW());
		configuration.area.setW(LABEL_TOPIC_W_PARAMETER * area.getW());

		configuration.color		 = Color.OLIVE;
		configuration.fontname	 = FontType.GUI_FONT;
		configuration.label		 = "change chat topic";

		return configuration;
	}
	
	@Override @UnhandledMethod
	public void onTabEnter() {}

	@Override
	public void confirmTabData() {
		choice.put(currentClient, box.getCheckedIndex());
		values.put(currentClient, val.getInput());
	}

	@Override
	public void cancel_TabData() {
		box.setCheckedIndex(choice.get(currentClient, 0));
		val.setInput(values.get(currentClient));
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(object instanceof AIMLGeneratorArgument && emitterID == OnPress.Id) {
			if(currentClient != null) {
				choice.put(currentClient, box.getCheckedIndex());
				values.put(currentClient, val.getInput());
			}

			AIMLGeneratorArgument client = (AIMLGeneratorArgument) object;

			box.setCheckedIndex(choice.get(client, 0));

			if(values.containsKey(client))
				val.setInput(values.get(client));
			else
				val.setInput("");

			currentClient = client;
		}
	}

	@Override
	public Wildcard generateWildcard() {
		return exeCard;
	}

	@Override
	public boolean hasAimlElement(AIMLGeneratorArgument client) {
		if(values.containsKey(client))
			return !values.get(client).isEmpty();
		else
			return false;
	}
	
	@Override
	public List<AIMLElement> getAimlElements(AIMLGeneratorArgument client, Wildcard wildcard) {
		if(wildcard == null || wildcard.getName().equals("{exe}")) {
			if(choice.get(client, 0) == 0)
				return Collections.<AIMLElement>singletonList(new Set(AliceBot.USER, new Text(values.get(client))));
			else
				return Collections.<AIMLElement>singletonList(new Set(AliceBot.TOPIC, new Text(values.get(client))));
		} else
			return Collections.emptyList();
	}
	
	@Override @UnhandledMethod
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		box.executeMouseHover(position);
		val.executeMouseHover(position);
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		box.executeMouseDrag(position);
		val.executeMouseDrag(position);
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		box.executeMousePress(position);
		val.executeMousePress(position);
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseRelease(Position position) {
		return false;
	}

	@Override
	public void update(float delta) {
		box.update(delta);
		val.update(delta);
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		background.draw(batch);
		
		userLabel.draw(batch);
		topicLabel.draw(batch);
		
		box.draw(batch);

		val.hintFBODrawing();
		val.draw(batch);
	}

	@Override @UnhandledMethod
	public void onTabLeave() {}
	
	@Override
	public void clear() {
		val.setInput("");
	}

	@Override @UnhandledMethod
	public void dispose() {}
}
