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

package com.thetruthbeyond.gui.utility.drawing;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Peter Siatkowski
 * Class that simplifies drawing operation via texture objects.
 */
public class SmartTexture {
	private final Texture texture;
	private final TextureRegion region;
	
	public SmartTexture(Texture texture) {
		this.texture = texture;
		this.texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		region = new TextureRegion(texture);
	}

	public SmartTexture(TextureRegion region) {
		texture = region.getTexture();
		this.region = region;
	}
	
	public SmartTexture(FileHandle handle) {
		this(new Texture(handle) );
	}
	
	public int getW() {
		return region.getRegionWidth();
	}
	
	public int getH() {
		return region.getRegionHeight();
	}
	
	public TextureRegion getTextureRegion() {
		return region;
	}
	
	public Texture getTexture() {
		return texture;
	}

	public void dispose() {
		texture.dispose();
	}
}
