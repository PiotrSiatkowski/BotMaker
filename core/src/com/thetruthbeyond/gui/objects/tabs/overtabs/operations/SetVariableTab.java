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
import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import  com.thetruthbeyond.chatterbean.aiml.Set;

import com.badlogic.gdx.graphics.Color;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetVariableTab extends OperationSubTab implements AIMLGenerator, WildcardGenerator {

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private final AutofillTextField nam;
	
	private static final float NAME_X_PARAMETER = 0.050f;
	private static final float NAME_Y_PARAMETER = 0.100f;
	private static final float NAME_W_PARAMETER = 0.902f;
	private static final float NAME_H_PARAMETER = 0.220f;
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	private final AutofillTextField val;

	private static final float VALUE_X_PARAMETER = 0.050f;
	private static final float VALUE_Y_PARAMETER = 0.350f;
	private static final float VALUE_W_PARAMETER = 0.902f;
	private static final float VALUE_H_PARAMETER = 0.220f;
	////////////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private AIMLGeneratorArgument currentClient;

	private final Map<AIMLGeneratorArgument, String> nams = new HashMap<>(20);
	private final Map<AIMLGeneratorArgument, String> vals = new HashMap<>(20);

	private final Wildcard setCard = new OperationCard("{set}", Color.FIREBRICK);
	////////////////////////////////////////////////////////////////////////////////////////////////////

	public SetVariableTab(Area area, FileManager loader, Clickable parent) {
		super(area, parent);
		
		TextFieldConfiguration configuration = new TextFieldConfiguration();
		configuration.area.setX(area.getX() + area.getW() * NAME_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * NAME_Y_PARAMETER);
		configuration.area.setW(area.getW() * NAME_W_PARAMETER);
		configuration.area.setH(area.getH() * NAME_H_PARAMETER);
		
		configuration.fontname 				= FontType.CHAT_FONT;
		configuration.fontcolor 			= Consts.MAIN_FONT_COLOR;
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border				= Consts.BORDER_SIZE;

		configuration.characters			= Coding.signs;

		TextField namField = new TextField(configuration, loader, this);
		namField = new DefaultMessageTextField(namField).setDefaultMessage("Variable's name.");
		nam = new AutofillTextField(namField);

		nam.getEmitter(OnReachLimit.Id).registerObserver(parent.getObserver());

		configuration.area.setX(area.getX() + area.getW() * VALUE_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * VALUE_Y_PARAMETER);
		configuration.area.setW(area.getW() * VALUE_W_PARAMETER);
		configuration.area.setH(area.getH() * VALUE_H_PARAMETER);
		
		TextField valField = new TextField(configuration, loader ,this);
		valField = new DefaultMessageTextField(valField).setDefaultMessage("Variable's value.");
		val = new AutofillTextField(valField);
	}

	@Override @UnhandledMethod
	public void onTabEnter() {}

	@Override
	public void confirmTabData() {
		nams.put(currentClient, nam.getInput());
		vals.put(currentClient, val.getInput());
	}

	@Override
	public void cancel_TabData() {
		nam.setInput(nams.get(currentClient));
		val.setInput(vals.get(currentClient));
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(object instanceof AIMLGeneratorArgument && emitterID == OnPress.Id) {
			if(currentClient != null) {
				nams.put(currentClient, nam.getInput());
				vals.put(currentClient, val.getInput());
			}

			AIMLGeneratorArgument client = (AIMLGeneratorArgument) object;

			if(nams.containsKey(client))
				nam.setInput(nams.get(client));
			else
				nam.setInput("");

			if(vals.containsKey(client))
				val.setInput(vals.get(client));
			else
				val.setInput("");

			currentClient = client;
		}
	}

	@Override
	public Wildcard generateWildcard() {
		return setCard;
	}

	@Override
	public boolean hasAimlElement(AIMLGeneratorArgument client) {
		String nam, val;
		if(nams.containsKey(client))
			nam = nams.get(client);
		else
			return false;

		if(vals.containsKey(client))
			val = nams.get(client);
		else
			return false;

		return !nam.isEmpty() && !val.isEmpty();
	}

	@Override
	public List<AIMLElement> getAimlElements(AIMLGeneratorArgument client, Wildcard wildcard) {
		if(wildcard == null || wildcard.getName().equals("{set}"))
			return Collections.<AIMLElement>singletonList(new Set(nams.get(client), new Text(vals.get(client))));
		else
			return Collections.emptyList();
	}
	
	@Override @UnhandledMethod
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		nam.executeMouseHover(position);
		val.executeMouseHover(position);
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		nam.executeMouseDrag(position);
		val.executeMouseDrag(position);
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		nam.executeMousePress(position);
		val.executeMousePress(position);
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseRelease(Position position) {
		return false;
	}

	@Override
	public void update(float delta) {
		nam.update(delta);
		val.update(delta);
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		nam.hintFBODrawing();
		nam.draw(batch);

		val.hintFBODrawing();
		val.draw(batch);
	}

	@Override @UnhandledMethod
	public void onTabLeave() {}
	
	@Override
	public void clear() {
		nam.setInput("");
		val.setInput("");
	}

	@Override @UnhandledMethod
	public void dispose() {}
}
