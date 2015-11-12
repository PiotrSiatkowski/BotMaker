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

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.simple.AcceptButton;
import com.thetruthbeyond.gui.objects.buttons.simple.CancelButton;
import com.thetruthbeyond.gui.objects.controllers.label.Label;
import com.thetruthbeyond.gui.objects.controllers.label.LabelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.BackgroundArea;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.BackgroundAreaConfiguration;
import com.thetruthbeyond.gui.objects.shareable.DarknessDrawer;
import com.thetruthbeyond.gui.objects.tabs.OverTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;
import com.thetruthbeyond.gui.utility.gl.BUFFER_NUMBER;
import com.thetruthbeyond.gui.utility.gl.Buffers;
import com.thetruthbeyond.gui.utility.gl.GlUtils;

public class YesNoDialog extends OverTab {

	private static final float BACKGROUND_ALPHA = 0.5f;

	private final SmartTexture background;
	private final FrameBuffer buffer;

	///////////////////////////////////////////////////////////////////////////////////////////////////
	private final Label label;
	
	private static final float LABEL_X_PARAMETER = 0.08f;
	private static final float LABEL_Y_PARAMETER = 0.02f;
	private static final float LABEL_W_PARAMETER = 0.84f;
	private static final float LABEL_H_PARAMETER = 0.50f;
	///////////////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////////////////
	private final BackgroundArea backgroundArea;
	
	private static final float BACKGROUND_X_PARAMETER = 0.00f;
	private static final float BACKGROUND_Y_PARAMETER = 0.00f;
	private static final float BACKGROUND_W_PARAMETER = 1.00f;
	private static final float BACKGROUND_H_PARAMETER = 1.00f;
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	private final Button accept;
	private final Button cancel;
	
	private static final float BUTTON_ACCEPT_X_PARAMETER = 0.27f;
	private static final float BUTTON_CANCEL_X_PARAMETER = 0.73f;
	private static final float BUTTON_Y_PARAMETER = 0.70f;
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	public YesNoDialog(Area area, FileManager loader, DarknessDrawer darkness, Clickable parent) {
		super(area, darkness, parent);

		background = loader.getTexture("SmallWoodBackground");
		
		BackgroundAreaConfiguration configuration = new BackgroundAreaConfiguration();
		configuration.area.setX(area.getX() + BACKGROUND_X_PARAMETER * area.getW());
		configuration.area.setY(area.getY() + BACKGROUND_Y_PARAMETER * area.getH());
		configuration.area.setW(BACKGROUND_W_PARAMETER * area.getW());
		configuration.area.setH(BACKGROUND_H_PARAMETER * area.getH());
		
		configuration.border 			= BORDER_SIZE;
		configuration.backgroundAlpha 	= BACKGROUND_ALPHA;
		
		backgroundArea = new BackgroundArea(configuration, loader, this);
		
		LabelConfiguration labelConfiguration = new LabelConfiguration();
		labelConfiguration.area.setX(area.getX() + LABEL_X_PARAMETER * area.getW());
		labelConfiguration.area.setY(area.getY() + LABEL_Y_PARAMETER * area.getH());
		labelConfiguration.area.setW(LABEL_W_PARAMETER * area.getW());
		labelConfiguration.area.setH(LABEL_H_PARAMETER * area.getH());

		labelConfiguration.color = Consts.MAIN_FONT_COLOR;
		labelConfiguration.fontname = FontType.CHAT_FONT;
		labelConfiguration.label = "Are you sure?";
		
		label = new Label(labelConfiguration, this);
		
		buffer = Buffers.getBuffer(BUFFER_NUMBER.TWO);
		
		accept = new AcceptButton(loader, this);
		cancel = new CancelButton(loader, this);
		
		accept.setPosition(getX() + getW() * BUTTON_ACCEPT_X_PARAMETER, getY() + getH() * BUTTON_Y_PARAMETER);
		cancel.setPosition(getX() + getW() * BUTTON_CANCEL_X_PARAMETER, getY() + getH() * BUTTON_Y_PARAMETER);
	}
	
	@Override @UnhandledMethod
	public void onTabEnter() {}

	@Override
	public boolean executeMouseHover(Position position) {
		if(!area.contains(position)) {
			return false;
		} else {			
			accept.executeMouseHover(position);
			cancel.executeMouseHover(position);
			return true;
		}
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {				
		if(cancel.executeMousePress(position)) {
			hide();
				
			accept.releaseState();
			cancel.releaseState();
		} else if(accept.executeMousePress(position)) {
			hide();
				
			accept.releaseState();
			cancel.releaseState();
			return true;
		}
		
		return false;
	}

	@Override
	public boolean executeMouseRelease(Position position) {		
		accept.executeMouseRelease(position);
		cancel.executeMouseRelease(position);
		return false;
	}

	@Override @UnhandledMethod
	public void onTabLeave() {}
	
	@Override
	public void update(float delta) {
		super.update(delta);

		if(isVisible()) {
			accept.update(delta);
			cancel.update(delta);
		}
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		super.draw(batch);

		if(isVisible()) {
			batch.end();
			
			drawToBuffer(batch);
			
			batch.begin();
			drawBuffer(batch, buffer, getVisibilityAlpha());
			
			batch.setColor(Color.WHITE);
		}
	}

	@Override
	protected void drawToBuffer(SmartSpriteBatch batch) {
		buffer.begin();
		batch.begin();
			Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
			drawBackground(batch);
		batch.end();
		buffer.end();
		
		buffer.begin();
		batch.begin();
			label.draw(batch);
			
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
		
		backgroundArea.draw(batch);
	}
	
	@Override @UnhandledMethod
	public void clear() {}

	@Override @UnhandledMethod
	public void dispose() {}
}