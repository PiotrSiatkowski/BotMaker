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

package com.thetruthbeyond.gui.objects.widgets;

import com.thetruthbeyond.gui.interfaces.GUIRootObject;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.GUIException;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.simple.BarExitButton;
import com.thetruthbeyond.gui.objects.buttons.simple.BarHelpButton;
import com.thetruthbeyond.gui.objects.buttons.simple.BarMinimizeButton;
import com.thetruthbeyond.gui.action.emitters.OnExit;
import com.thetruthbeyond.gui.objects.tabs.AboutTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

import java.awt.*;

public class MenuBar extends Clickable {

	private final SmartTexture menu;
	
	private static final float BUTTONS_RELATIVE_Y = 0.10f;
	private static final float CUSTOMIZE_X_OFFSET = 2;
	
	private final Button exit;
	private final Button minimize;
	private final Button help;

	private final Position translation = new Position();
	private final Position frameCorner = new Position();
	private final Position pressedSpot = new Position();

	private boolean isPressed = false;

	private final GUIRootObject application;

	private final OnExit onExit;

	public MenuBar(Area area, FileManager loader, Clickable parent) {
		super(area, parent);

		// Search main application class.
		Clickable clickable = parent;
		while(true) {
			if(clickable instanceof GUIRootObject) {
				application = (GUIRootObject) clickable;
				break;
			} else

			if(clickable != null)
				clickable = clickable.getParent();
			else
				throw new GUIException("Menu bar is not connected with main application class in any hierarchy.");
		}

		menu = loader.getTexture("MenuBar");
		
		Area buttonArea = new Area();
		buttonArea.setX(area.getX() + area.getW() - (1 - BUTTONS_RELATIVE_Y) * area.getH() - CUSTOMIZE_X_OFFSET);
		buttonArea.setY(area.getY() + BUTTONS_RELATIVE_Y * area.getH());
		buttonArea.setW((1 - 2 * BUTTONS_RELATIVE_Y) * area.getH());
		buttonArea.setH((1 - 2 * BUTTONS_RELATIVE_Y) * area.getH());	
		exit = new BarExitButton(buttonArea, loader, this);
		
		buttonArea.setX(buttonArea.getX() - (1 - BUTTONS_RELATIVE_Y) * area.getH());
		minimize = new BarMinimizeButton(buttonArea, loader, this);
		
		buttonArea.setX(buttonArea.getX() - (1 - BUTTONS_RELATIVE_Y) * area.getH());
		help = new BarHelpButton(buttonArea, loader, this);

		onExit = new OnExit(this);
		addEmitter(onExit);
	}

	@Override @UnhandledMethod
	public boolean contains(Position position) {
		return super.contains(position);
	}
	
	@Override
	public boolean executeMouseHover(Position position) {
		boolean isHovered = false;

		if(exit.executeMouseHover(position))
			isHovered = true;
		if(minimize.executeMouseHover(position))
			isHovered = true;
		if(help.executeMouseHover(position))
			isHovered = true;

		return isHovered;
	}
	
	@Override
	public boolean executeMouseDrag(Position position) {
		if(isPressed) {
			translation.x = position.x;
			translation.y = position.y;

			translation.sub(pressedSpot);

			translateFrame(translation);
			return true;
		} else {
			exit.executeMouseDrag(position);
			minimize.executeMouseDrag(position);
			help.executeMouseDrag(position);

			// Always send the event further.
			return false;
		}
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(contains(position)) {
			if(minimize.executeMousePress(position)) {
				minimize.releaseState();
				application.minimizeFrame();
			} else
			
			if(exit.executeMousePress(position)) {
				onExit.signalExit();

				minimize.releaseState();
				application.closeFrame();
			} else

			if(help.executeMousePress(position))
				application.changeTab(AboutTab.class);
			else {
				pressedSpot.x = position.x;
				pressedSpot.y = position.y;

				isPressed = true;
			}
		} else
			isPressed = false;
		
		return isPressed;
	}
	
	@Override
	public boolean executeMouseRelease(Position position) {
		exit.executeMouseRelease(position);
		minimize.executeMouseRelease(position);
		help.executeMouseRelease(position);
		
		isPressed = false;

		// Always send the event further.
		return false;
	}
	
	@Override @UnhandledMethod
	public boolean executeMousePressRight(Position position) {
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseReleaseRight(Position position) {
		return false;
	}

	private void translateFrame(Position vector) {
		Point point = application.getFrameLocation();
		frameCorner.x = (int) point.getX();
		frameCorner.y = (int) point.getY();

		frameCorner.add(vector);
		application.setFrameLocation(frameCorner.x, frameCorner.y);
	}

	@Override
	public void update(float delta) {
		help.update(delta);
		minimize.update(delta);
		exit.update(delta);
	}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		batch.draw(menu, 0, 0, area.getW(), area.getH());
		
		help.draw(batch);
		minimize.draw(batch);
		exit.draw(batch);
	}
}
