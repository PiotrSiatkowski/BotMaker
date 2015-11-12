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

package com.thetruthbeyond.gui.structures;

import java.util.ArrayList;
import java.util.List;

import com.thetruthbeyond.gui.utility.drawing.fonts.SmartFont;

public class Message {
	
	private final String original;
	private final List<String> lines = new ArrayList<>(2);
	
	public Message(String message, SmartFont font, int maxWidth) {
		original = message;

		int index;
		String substring = "";
			
		while(!message.isEmpty()) {
			for(index = 0; index != message.length(); index++) {
				substring = message.substring(0, index);
				if(font.getWidth(substring) > maxWidth) {
					index = index - 1;
					substring = substring.substring(0, index);
					break;
				}			
			}
	
			if(index != message.length()) {
				if(substring.indexOf(' ') != -1)
				{
					while(substring.charAt(index - 1) != ' ')
						index--;
				}
			}
				
			lines.add(message.substring(0, index));
			message = message.substring(index);
		}
	}
	
	public String getOriginal() {
		return original;
	}
	
	public String getLine(int lineNumber) {
		if(lineNumber < 0 || lineNumber >= lines.size())
			return "";
		return lines.get(lineNumber);
	}
	
	public int getLinesNumber() {
		return lines.size();
	}
}
