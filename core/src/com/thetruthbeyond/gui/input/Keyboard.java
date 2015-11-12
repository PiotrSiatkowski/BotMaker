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

import java.util.HashSet;
import java.util.Stack;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.IntSet;

public class Keyboard extends InputAdapter {

	private static Keyboard instance;

	private String filterSigns = "";
	private final HashSet<Character> filter = new HashSet<>(12);

	private final IntSet keys = new IntSet();
	private final Stack<Character> signs = new Stack<>();

	private Keyboard() {}

	public static Keyboard getInstance() {
		if(instance == null)
			instance = new Keyboard();
		return instance;
	}

	@Override
	public boolean keyUp(int keycode) {
		keys.remove(keycode);	
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		keys.add(keycode);	
		return true;
	}
	
	public void dismissKey(int keycode) {
		keys.remove(keycode);
	}

	@Override
	public boolean keyTyped(char sign) {
		if(filter.contains(sign)) {
			signs.add(sign);	
			return true;
		} else return false;
	}
	
	public boolean isKeyUp(int keycode) {
		return !keys.contains(keycode);
	}
	
	public boolean isKeyDown(int keycode) {
		return keys.contains(keycode);
	}
	
	public void setFilter(String signs) {
		if(signs == null || signs.equals("")) {
			filterSigns = "";
			filter.clear();
		} else {
			if(!filterSigns.equals(signs)) {
				filterSigns = signs;

				filter.clear();
				for(int i = 0; i != signs.length(); i++)
					filter.add(signs.charAt(i));
			}
		}
	}
	
	public boolean isCharacterWaiting() {
		return !signs.isEmpty();
	}
	
	public Character getCharacter() {
		return signs.pop();
	}
	
	public Character peekCharacter() {
		return signs.peek();
	}
	
	public void clearEvents() {
		keys.clear();
		signs.clear();
	}
}