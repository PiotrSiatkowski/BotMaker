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

package com.thetruthbeyond.gui.structures.wildcard;

import  com.thetruthbeyond.chatterbean.aiml.AIMLElement;
import com.thetruthbeyond.gui.interfaces.AIMLGenerator;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

public abstract class Wildcard {
	
	private final String name;
	private final Color color;
	
	public Wildcard(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}

	/** Template of AIML element connected with wildcard. */
	public abstract void fillTemplate(List<AIMLElement> elements, AIMLGenerator generator);

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Wildcard) {
			Wildcard compared = (Wildcard) obj;
			if(name.equals(compared.name) && color.equals(compared.color))
					return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() + color.hashCode();
	}
}
