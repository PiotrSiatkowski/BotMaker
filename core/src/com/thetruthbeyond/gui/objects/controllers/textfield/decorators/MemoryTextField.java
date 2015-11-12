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

package com.thetruthbeyond.gui.objects.controllers.textfield.decorators;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Input.Keys;

import com.thetruthbeyond.gui.input.Keyboard;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldDecorator;

public class MemoryTextField extends TextFieldDecorator {

	private static final int MEMORY_SIZE = 10;
	
	// Input field memory.
	private final List<String> memory = new LinkedList<>();
	private int lastMessageIndex = -1;

	private final Keyboard keyboard;
	
	public MemoryTextField(TextField parent) {
		super(parent);
		keyboard = Keyboard.getInstance();
	}
	
	public MemoryTextField(TextFieldDecorator parent) {
		super(parent);
		keyboard = Keyboard.getInstance();
	}
	
	@Override
	public void setInput(String input) {
		super.setInput(input);
		lastMessageIndex = -1;
	}
	
	@Override
	public void update(float delta) {		
		super.update(delta);
		
		if(isFocused())	{
			if(lastMessageIndex != -1) {
				if(hasChanged())
					lastMessageIndex = -1;
			}
			
			if(keyboard.isKeyDown(Keys.UP)) {
				if(!memory.isEmpty()) {
					lastMessageIndex = Math.min(lastMessageIndex + 1, memory.size() - 1);
					super.setInput(memory.get(lastMessageIndex));
				
					keyboard.dismissKey(Keys.UP);
				}
			} else
			
			if(keyboard.isKeyDown(Keys.DOWN)) {
				if(!memory.isEmpty()) {
					lastMessageIndex = Math.max(lastMessageIndex - 1, 0);
					super.setInput(memory.get(lastMessageIndex));
					
					keyboard.dismissKey(Keys.DOWN);
				}
			}
		}
	}
	
	public void confirmInput() {
		memory.add(0, getInput());
		if(memory.size() == MEMORY_SIZE + 1)
			memory.remove(MEMORY_SIZE);
		setInput("");
	}
}
