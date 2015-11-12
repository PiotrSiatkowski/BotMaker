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

package com.thetruthbeyond.gui.input;

import com.thetruthbeyond.gui.structures.Position;

import com.badlogic.gdx.InputAdapter;

import static com.badlogic.gdx.Input.*;

/**
 * @author Siatek
 * Cursor class wraps the input methods and provides convenient
 * interface to control mouse and game cursor.
 * Up left corner coordinate system is used.
 */
public class Cursor extends InputAdapter {

	private static Cursor instance;

	private enum MouseState { Idle, Pressed, Dragged, Released }
	
	private final Position position = new Position();
	
	private final Position pressedPositionL = new Position();
	private final Position releasedPositionL = new Position();

	private final Position pressedPositionR = new Position();
	private final Position releasedPositionR = new Position();
	
	private MouseState eventStateL = MouseState.Idle;
	private MouseState eventStateR = MouseState.Idle;

	private boolean isInactive;

	private Cursor() {}

	public static Cursor getInstance() {
		if(instance == null)
			instance = new Cursor();
		return instance;
	}

	public Position getPosition() {
		return position;
	}
	
	public Position getClickedPosition() {
		return pressedPositionL;
	}
	
	public Position getClickedPositionRight() {
		return pressedPositionR;
	}
	
	public Position getReleasedPosition() {
		return releasedPositionL;
	}
	
	public Position getReleasedPositionRight() {
		return releasedPositionR;
	}
	
	public boolean isIdle() {
		return eventStateL == MouseState.Released || eventStateL == MouseState.Idle;
	}
	
	public boolean isDragged() {
		return eventStateL == MouseState.Pressed || eventStateL == MouseState.Dragged;
	}
	
	public boolean isPressed() {
		if(eventStateL == MouseState.Pressed) {
			eventStateL = MouseState.Dragged;
			return true;
		}
		
		return false;
	}
	
	public boolean isPressedRight() {
		if(eventStateR == MouseState.Pressed) {
			eventStateR = MouseState.Dragged;
			return true;
		}
		
		return false;
	}
	
	public boolean isReleased() {
		if(eventStateL == MouseState.Released) {
			eventStateL = MouseState.Idle;
			return true;
		}
		
		return false;
	}
	
	public boolean isReleasedRight() {
		if(eventStateR == MouseState.Released) {
			eventStateR = MouseState.Idle;
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(isInactive)
			return false;
		if(button == Buttons.LEFT) {
			pressedPositionL.x = screenX;
			pressedPositionL.y = screenY;

			eventStateL = MouseState.Pressed;	
		} else if(button == Buttons.RIGHT) {
			pressedPositionR.x = screenX;
			pressedPositionR.y = screenY;

			eventStateR = MouseState.Pressed;
		}
		
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(isInactive)
			return false;

		if(button == Buttons.LEFT) {
			eventStateL = MouseState.Released;
			pressedPositionL.x = 0;
			pressedPositionL.y = 0;
			releasedPositionL.x = screenX;
			releasedPositionL.y = screenY;
		} else if(button == Buttons.RIGHT) {
			eventStateR = MouseState.Released;
			pressedPositionR.x = 0;
			pressedPositionR.y = 0;
			releasedPositionR.x = screenX;
			releasedPositionR.y = screenY;
		}
		
		return true;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(isInactive)
			return false;

		position.x = screenX;
		position.y = screenY;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(isInactive)
			return false;
		
		position.x = screenX;
		position.y = screenY;
		return true;
	}
	
	public void setActive(boolean active) {
		isInactive = !active;
		eventStateL = MouseState.Idle;
		eventStateR = MouseState.Idle;
		
		if(!active) {
			position.x = -1;
			position.y = -1;
		}
	}
}
