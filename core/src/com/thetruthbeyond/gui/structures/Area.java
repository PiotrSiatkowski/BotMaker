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

public class Area {
	private int x;
	private int y;
	private int w;
	private int h;
	
	public Area() {}
	
	public Area(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public Area(Area area) {
		x = area.x;
		y = area.y;
		w = area.w;
		h = area.h;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getW() {
		return w;
	}
	
	public int getH() {
		return h;
	}
	
	public void setX(float x) {
		this.x = Math.round(x);
	}
	
	public void setY(float y) {
		this.y = Math.round(y);
	}
	
	public void setW(float w) {
		this.w = Math.round(w);
	}
	
	public void setH(float h) {
		this.h = Math.round(h);
	}
	
	public void translate(Position vector) {
		x += vector.x;
		y += vector.y;
	}
	
	public boolean contains(Position position) {
		return !(position.x < x || position.x > x + w || position.y < y || position.y > y + h);
	}
	
	public Area cutArea(int depth) {
		x = x + depth;
		y = y + depth;
		w = w - 2 * depth;
		h = h - 2 * depth;
		return this;
	}
}
