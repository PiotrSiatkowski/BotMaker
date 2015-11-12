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

import com.badlogic.gdx.math.Rectangle;


public class Position {

	public static Position NullPosition = new Position(-1, -1);

	public int x;
	public int y;
	
	public Position() {
		x = 0;
		y = 0;
	}
	
	public Position(int x) {
		this.x = x;
		y = 0;
	}
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Position(Position point) {
		x = point.x;
		y = point.y;
	}
	
	public void add(Position point) {
		x += point.x;
		y += point.y;
	}
	
	public void sub(Position point) {
		x -= point.x;
		y -= point.y;
	}
	
	public void mul(float scalar) {
		x *= scalar;
		y *= scalar;
	}
	
	public void bound(Rectangle rectangle) {
		if(x < rectangle.x)
			x = (int) rectangle.x;
		else if(x > rectangle.x + rectangle.width)
			x = (int)(rectangle.x + rectangle.width);
		if(y < rectangle.y)
			y = (int) rectangle.y;
		else if(y > rectangle.y + rectangle.height)
			y = (int)(rectangle.y + rectangle.height);
	}
	
	public float distance() {
		return (float) Math.sqrt( x*x + y*y );
	}
	
	public void clear() {
		x = -1;
		y = -1;
	}
	
	public int hashCode() {
		return Integer.valueOf(x).hashCode() + Integer.valueOf(y).hashCode();
	}
	
	public boolean equals(Position position) {
        return x == position.x && y == position.y;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Position) {
			Position position = (Position) obj;
			return x == position.x && y == position.y;
		} else
			return false;
	}
}
