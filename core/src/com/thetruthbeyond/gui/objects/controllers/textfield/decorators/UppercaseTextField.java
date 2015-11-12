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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;

public class UppercaseTextField extends TokenizedTextField {
	
	private Set<String> exceptions = null;
	
	public UppercaseTextField(TextField parent) {
		super(parent);
	}
	
	public UppercaseTextField setException(String exception) {
		if(exceptions == null)
			exceptions = new TreeSet<>();
		exceptions.add(exception);
		return this;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if(isFocused() && hasChanged()) {

			StringBuilder builder = new StringBuilder(getInput().length());
			List<String> words = getWords();
			
			for(String word : words) {
				if(exceptions.contains(word))
					builder.append(word);
				else
					builder.append(word.toUpperCase());
			}
			
			setInput(builder.toString());
		}
	}
}