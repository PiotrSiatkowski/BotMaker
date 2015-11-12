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

package com.thetruthbeyond.gui.structures.tries;

import java.util.ArrayList;

public class Tries {
	private final int R;
	private final String signs;
	private Node root;
   
	public Tries(String signs) {
		R = signs.length();
		this.signs = signs;
		
		root = new Node();
	}
	
	private class Node {
		private String value;
		private final Node[] next = new Node[R];
	}
	
	public void put(String string) {  
		while(true) { 
			try {
				root = put(root, string, 0);
				break;
			} catch(CharacterNotSupportedException exception) {
				string = string.replace(String.valueOf(exception.getNotSupportedCharacter()), " ");
			}
		}
	}
	
	@SuppressWarnings("ProhibitedExceptionCaught")
	private Node put(Node x, String string, int level) {
		try { 
			if (x == null) 
				x = new Node();
		    if (level == string.length()) {
		    	x.value = string;
		    	return x;
		    }
		    
		    int index = signs.indexOf(string.charAt(level));
		    x.next[index] = put(x.next[index], string, level + 1);
		} catch(CharacterNotSupportedException exception) {
			throw exception;
		} catch(ArrayIndexOutOfBoundsException ignored) {
			throw new CharacterNotSupportedException(string.charAt(level));
		}
		
	    return x;
	}
	
	public ArrayList<String> get(String string) {  
		ArrayList<String> accumulator = new ArrayList<>(8);
		
		while(true) { 
			try {
				get(root, string, accumulator, 0);
				break;
			} catch(CharacterNotSupportedException exception) {
				string = string.replace(String.valueOf(exception.getNotSupportedCharacter()), " ");
			}
		}
		
		return accumulator;
	}
	
	@SuppressWarnings("ProhibitedExceptionCaught")
	private void get(Node x, String string, ArrayList<String> accumulator, int level) {
		try {
			if(x == null) 
				return;
		    if(level >= string.length()) {
		    	for(Node node : x.next) {
		    		if(node != null) {
		    			if(node.value != null)
		    				accumulator.add(node.value);
		    			get(node, string, accumulator, level + 1);
		    		}
		    	}
		    } else {
		    	int index = signs.indexOf(string.charAt(level));
		    	get(x.next[index], string, accumulator, level + 1);
		    }
		} catch(CharacterNotSupportedException exception) {
			throw exception;
		} catch(ArrayIndexOutOfBoundsException ignored) {
			throw new CharacterNotSupportedException(string.charAt(level));
		}
	}
}

