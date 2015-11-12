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

package com.thetruthbeyond.gui.objects.tabs.overtabs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import  com.thetruthbeyond.chatterbean.aiml.AIMLElement;
import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import  com.thetruthbeyond.chatterbean.aiml.Think;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.Observer;
import com.thetruthbeyond.gui.action.emitters.*;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.input.Keyboard;
import com.thetruthbeyond.gui.interfaces.AIMLGenerator;
import com.thetruthbeyond.gui.interfaces.AIMLGeneratorArgument;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.interfaces.WildcardGenerator;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.simple.AcceptButton;
import com.thetruthbeyond.gui.objects.buttons.simple.CancelButton;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.ChoosableScrollArea;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.ChoosableScrollAreaConfiguration;
import com.thetruthbeyond.gui.objects.tabs.Tab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.operations.ExecuteCodeTab;
import com.thetruthbeyond.gui.objects.tabs.overtabs.operations.SetVariableTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;
import com.thetruthbeyond.gui.utility.gl.BUFFER_NUMBER;
import com.thetruthbeyond.gui.utility.gl.Buffers;
import com.thetruthbeyond.gui.utility.gl.GlUtils;

public class OperationTab extends Tab implements AIMLGenerator, WildcardGenerator {
	
	private static final float BACKGROUND_ALPHA = 0.0f;
	private static final float BLACKNESS_ALPHA = 0.8f;
	private static final float SHOW_TIME = 0.3f;
	
	private float showTime = 0.0f;

	private final FrameBuffer buffer;
	private TextureRegion region;

	private final OnChangeVisibility onChangeVisibility;
	private final OnAcceptComponent onAcceptComponent;

	private final Observer<OnPress> delegateObserver;

	private final SmartTexture canvas;
	private final SmartTexture border;
	private final SmartTexture background;
	private final SmartTexture darkness;

	// SubTab tabList. ///////////////////////////////////////////////////////////////////////////////////
	private final ChoosableScrollArea tabList;
	
	private static final float LIST_W_PARAMETER 			= 0.30f;
	private static final float SCROLL_AREA_P_PARAMETER		= 0.01f;
	private static final float SCROLL_AREA_TEXT_ALPHA 		= 1.00f;
	
	private static final int MAX_MESSAGES_SHOWN 			= 8;
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	private final Button accept;
	private final Button cancel;
	
	private static final float BUTTON_ACCEPT_X_PARAMETER	= 0.50f;
	private static final float BUTTON_CANCEL_X_PARAMETER 	= 0.80f;
	private static final float BUTTON_Y_PARAMETER 			= 0.85f;
	///////////////////////////////////////////////////////////////////////////////////////////////////

	private final Map<String, OperationSubTab> subtabs = new HashMap<>(4);
	private Tab currTab;

	private static final String SetVariable = "Set variable";
	private static final String ExecuteCode = "Execute code";

	public OperationTab(Area area, FileManager loader, Clickable parent) {
		super(area, parent);
		
		canvas 		= loader.getTexture("Canvas");
		border 		= loader.getTexture("Border");
		darkness 	= loader.getTexture("BlackBackground");
		background 	= loader.getTexture("SmallWoodBackground");
		
		buffer = Buffers.getBuffer(BUFFER_NUMBER.TWO);

		onChangeVisibility = new OnChangeVisibility(this);
		addEmitter(onChangeVisibility);

		onAcceptComponent = new OnAcceptComponent(this);
		addEmitter(onAcceptComponent);
		
		ChoosableScrollAreaConfiguration configuration = new ChoosableScrollAreaConfiguration();
		configuration.area.setX(getX());
		configuration.area.setY(getY());
		configuration.area.setW(getW() * LIST_W_PARAMETER);
		configuration.area.setH(getH());
		
		configuration.relativeAreaPadding	= SCROLL_AREA_P_PARAMETER;
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border				= BORDER_SIZE;
		
		configuration.fontname 				= FontType.CHAT_FONT;
		configuration.fontcolor 			= Consts.MAIN_FONT_COLOR;
		
		configuration.maxMessagesShown 		= MAX_MESSAGES_SHOWN;
		
		configuration.backgroundAlpha 		= Consts.BACKGROUND_ALPHA;
		configuration.textBackgroundAlpha 	= SCROLL_AREA_TEXT_ALPHA;
		
		tabList = new ChoosableScrollArea(configuration, loader, this);
		
		tabList.addMessage(SetVariable);
		tabList.addMessage(ExecuteCode);
		
		accept = new AcceptButton(loader, this);
		cancel = new CancelButton(loader, this);
		
		accept.setPosition(Math.round(getX() + getW() * BUTTON_ACCEPT_X_PARAMETER), Math.round(getY() + getH() * BUTTON_Y_PARAMETER));
		cancel.setPosition(Math.round(getX() + getW() * BUTTON_CANCEL_X_PARAMETER), Math.round(getY() + getH() * BUTTON_Y_PARAMETER));
		
		Area tabArea = new Area();
		tabArea.setX(getX() + getW() * LIST_W_PARAMETER);
		tabArea.setY(getY() + Consts.BORDER_SIZE);
		tabArea.setW(getW() * (1 - LIST_W_PARAMETER) - Consts.BORDER_SIZE);
		tabArea.setH(getH() * BUTTON_Y_PARAMETER);

		OperationSubTab variables = new SetVariableTab(tabArea, loader, this);
		variables.getObserver().observeEmitter(onChangeVisibility);
		subtabs.put(SetVariable, variables);

		OperationSubTab execute = new ExecuteCodeTab(tabArea, loader, this);
		execute.getObserver().observeEmitter(onChangeVisibility);
		subtabs.put(ExecuteCode, execute);

		currTab = subtabs.get(SetVariable);

		delegateObserver = new Observer<OnPress>(this) {
			@Override
			public void observeEmitter(OnPress emitter) {
				super.observeEmitter(emitter);

				for(OperationSubTab tab : subtabs.values())
					emitter.registerObserver(tab.getObserver());
			}
		};
	}

	@Override @UnhandledMethod
	public void onTabEnter() {}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(emitterID == OnPress.Id) {
			onChangeVisibility.setVisible(true);
		}
	}

	@Override @SuppressWarnings("unchecked")
	public <T extends Emitter> Observer<T> getSpecializedObserver(int emitterID) {
		if(emitterID == OnPress.Id)
			return (Observer<T>) delegateObserver;
		return null;
	}

	@Override
	public Wildcard generateWildcard() {
		return ((WildcardGenerator) currTab).generateWildcard();
	}

	public boolean isVisible() {
		return onChangeVisibility.isVisible();
	}

	@Override
	public boolean hasAimlElement(AIMLGeneratorArgument client) {
		for(OperationSubTab tab : subtabs.values()) {
			if(tab.hasAimlElement(client))
				return true;
		}

		return false;
	}

	public List<AIMLElement> getAimlElements(AIMLGeneratorArgument client, Wildcard wildcard) {
		Think think = new Think();
		
		for(OperationSubTab tab : subtabs.values()) {
			if(tab.hasAimlElement(client))
				think.appendChildren(tab.getAimlElements(client, wildcard));
		}

		return Collections.<AIMLElement>singletonList(think);
	}
	
	@Override
	public boolean contains(Position position) {
		return onChangeVisibility.isVisible() && area.contains(position);
	}

	@Override
	public boolean executeMouseHover(Position position) {
		if(!contains(position)) {
			return false;
		} else {
			currTab.executeMouseHover(position);
			
			accept.executeMouseHover(position);
			cancel.executeMouseHover(position);
			return true;
		}
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		if(contains(position))
			currTab.executeMouseDrag(position);
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(contains(position)) {
			if(!currTab.executeMousePress(position)) {
				if(cancel.executeMousePress(position)) {
					onChangeVisibility.setVisible(false);

					accept.releaseState();
					cancel.releaseState();

					for(OperationSubTab tab : subtabs.values())
						tab.cancel_TabData();
					Keyboard.getInstance().clearEvents();
				} else if(accept.executeMousePress(position)) {
					onChangeVisibility.setVisible(false);

					accept.executeMouseHover(Position.NullPosition);
					cancel.executeMouseHover(Position.NullPosition);

					for(OperationSubTab tab : subtabs.values())
						tab.confirmTabData();
					Keyboard.getInstance().clearEvents();

					onAcceptComponent.signalComponentAcceptance(currTab.getClass());
				} else if(tabList.executeMousePress(position)) {
					String mode = tabList.getSelected();
					currTab = subtabs.get(mode);
				}
			}
		} 
		
		return false;
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		currTab.executeMouseRelease(position);
		
		accept.executeMouseRelease(position);
		cancel.executeMouseRelease(position);
		return false;
	}
	
	@Override
	public void update(float delta) {
		if(onChangeVisibility.isVisible()) {
			if(showTime < SHOW_TIME)
				showTime = Math.min(showTime + delta, SHOW_TIME);

			tabList.update(delta);
			currTab.update(delta);
			
			accept.update(delta);
			cancel.update(delta);
		} else {
			if(showTime > 0.0f) {
				showTime = Math.max(showTime - delta, 0.0f);

				tabList.update(delta);
				currTab.update(delta);
				
				accept.update(delta);
				cancel.update(delta);
			}
		}
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		if(showTime > 0.0f) {
			// Calculating whole currTab alpha.
			float alpha = showTime / SHOW_TIME;
			
			// Drawing black background.
			batch.setColor(1.0f, 1.0f, 1.0f, alpha * BLACKNESS_ALPHA);
			batch.draw(darkness, parent.getX(), parent.getY(), parent.getW(), parent.getH());
			
			batch.end();
			
			drawToBuffer(batch);
			
			batch.begin();
			drawBuffer(batch, alpha);
			
			batch.setColor(Color.WHITE);
		}
	}
	
	protected void drawToBuffer(SmartSpriteBatch batch) {
		buffer.begin();
		batch.begin();
			Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
			drawBackground(batch);
		batch.end();
		buffer.end();

		tabList.drawObjectsToBuffer(batch);
		
		buffer.begin();
		batch.begin();
			tabList.drawBackground(batch);
			tabList.drawBuffer(batch);
			currTab.draw(batch);
			
			accept.draw(batch);
			cancel.draw(batch);
		batch.end();
		
		GlUtils.clearAlpha(area);
		
		buffer.end();	
	}
	
	protected void drawBackground(SmartSpriteBatch batch) {	
		// Drawing wood background.
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		batch.draw(background, area.getX(), area.getY(), area.getW(), area.getH());
		
		// Drawing canvas background.
		batch.setColor(1.0f, 1.0f, 1.0f, BACKGROUND_ALPHA);
		batch.draw(canvas, area.getX(), area.getY(), area.getW(), area.getH());
				
		// Drawing border
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		batch.draw(border, area.getX(), area.getY(), area.getW(), BORDER_SIZE);
		batch.draw(border, area.getX(), area.getY(), BORDER_SIZE, area.getH());
		batch.draw(border, area.getX(), area.getY() + area.getH() - BORDER_SIZE, area.getW(), BORDER_SIZE);
		batch.draw(border, area.getX() + area.getW() - BORDER_SIZE, area.getY(), BORDER_SIZE, area.getH());
	}
	
	protected void drawBuffer(SmartSpriteBatch batch, float alpha) {	
		Texture bufferTexture = buffer.getColorBufferTexture();

		if(region == null) {
			region = new TextureRegion(bufferTexture, area.getX(), Consts.SCREEN_H - (area.getY() + area.getH()),
													  area.getW(), area.getH());
			region.flip(false, true);
		} else {
			region.setTexture(bufferTexture);
			region.setRegion(area.getX(), Consts.SCREEN_H - (area.getY() + area.getH()),
							 area.getW(), area.getH());
			region.flip(false, true);
		}

		batch.setColor(1.0f, 1.0f, 1.0f, alpha);
		batch.draw(region, area.getX(), area.getY(), area.getW(), area.getH());
	}

	@Override @UnhandledMethod
	public void onTabLeave() {}

	@Override @UnhandledMethod
	public void clear() {}

	@Override @UnhandledMethod
	public void dispose() {}
}
