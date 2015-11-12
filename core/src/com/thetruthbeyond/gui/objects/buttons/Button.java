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

package com.thetruthbeyond.gui.objects.buttons;

import com.thetruthbeyond.gui.action.emitters.OnHover;
import com.thetruthbeyond.gui.action.emitters.OnPress;
import com.thetruthbeyond.gui.interfaces.FBODrawable;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;
import com.thetruthbeyond.gui.utility.gl.GlUtils;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;

public abstract class Button extends Clickable implements FBODrawable {

	protected enum Shape { Rectangle, Oval }
	protected enum Align { Center }
	
	protected SmartTexture button;
	protected SmartTexture buttonHover;
	protected SmartTexture buttonPressed;
	
	protected Sound soundHover;
	protected Sound soundPressed;
	
	protected static final float HOVER_TIME = 0.4f;
	protected float hoverTime = 0.0f;
	protected float hoverAlpha = 0.0f;

	private final OnPress pressEmitter;
	private final OnHover hoverEmitter;
	
	private boolean hasSize = false;
	private boolean hintFBO = false;
	
	protected Shape shape = Shape.Rectangle;
	protected Align align = Align.Center;

	protected Button(Clickable parent) { 
		super(parent);

		pressEmitter = new OnPress(this);
		addEmitter(pressEmitter);

		hoverEmitter = new OnHover(this);
		addEmitter(hoverEmitter);
	}
	
	protected Button(Area area, Clickable parent) {
		super(area, parent);

		pressEmitter = new OnPress(this);
		addEmitter(pressEmitter);

		hoverEmitter = new OnHover(this);
		addEmitter(hoverEmitter);

		hasSize = true;
	}

	protected abstract void initialize(FileManager loader);

	@Override
	public void hintFBODrawing() {
		hintFBO = true;
	}

	public void setPressed() {
		hoverEmitter.setHovered(true);
		pressEmitter.setPressed(true);
		
		hoverTime = 0.0f;
	}
	
	public boolean isPressed() { return pressEmitter.isPressed(); }
	public boolean isHovered() { return hoverEmitter.isHovered(); }

	@Override
	public int getW() { initializeSize(); return area.getW(); }
	@Override
	public int getH() { initializeSize(); return area.getH(); }

	private void initializeSize() {
		if(!hasSize && button != null) {
			area.setW(button.getW());
			area.setH(button.getH());
			hasSize = true;
		}
	}

	public void releaseState() {
		hoverEmitter.setHovered(false);
		pressEmitter.setPressed(false);
		
		hoverTime = 0.0f;
	}

	public void setPosition(float x, float y) {
		setPosition(Math.round(x), Math.round(y));
	}

	public void setPosition(int x, int y) {
		if(align == Align.Center) {
			area.setX(x - button.getW() / 2.0f);
			area.setY(y - button.getH() / 2.0f);
		}
	}

	public void setPosition(Position position) {
		if(align == Align.Center) {
			area.setX(position.x - button.getW() / 2.0f);
			area.setY(position.y - button.getH() / 2.0f);
		}
	}

	@Override
	public boolean contains(Position position) {
		int x = area.getX();
		int y = area.getY();
		
		if(shape == Shape.Rectangle || shape == Shape.Oval) {
			if(position.x > x && position.x < x + area.getW() && 
			   position.y > y && position.y < y + area.getH())
				return true;
		}
		
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		if(contains(position)) {
			if(!hoverEmitter.isHovered()) {
				if(soundHover != null)
					soundHover.play();
				hoverEmitter.setHovered(true);
			}			
		} else
			hoverEmitter.setHovered(false);
		
		return hoverEmitter.isHovered();
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		if(contains(position)) {
			if(!hoverEmitter.isHovered()) {
				if(soundHover != null)
					soundHover.play();
				hoverEmitter.setHovered(true);
			}
		} else
			hoverEmitter.setHovered(false);

		return hoverEmitter.isHovered();
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(hoverEmitter.isHovered()) {
			pressEmitter.setPressed(true);

			if(soundPressed != null)
				soundPressed.play();
			
			return true;
		}

		return false;
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		if(!contains(position))
			hoverEmitter.setHovered(false);
		pressEmitter.setPressed(false);
		return false;
	}
	
	@Override @UnhandledMethod
	public boolean executeMousePressRight(Position position) {
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseReleaseRight(Position position) {
		return false;
	}

	@Override
	public void update(float delta) {
		if(hoverEmitter.isHovered()) {
			if(hoverTime < HOVER_TIME) {
				hoverTime = Math.min(HOVER_TIME, hoverTime + delta);
				hoverAlpha = hoverTime / HOVER_TIME;
			}
		} else {
			if(hoverTime > 0) {
				hoverTime = Math.max(0, hoverTime - delta);
				hoverAlpha = hoverTime / HOVER_TIME;
			}
		}		
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		initializeSize();

		Color color = batch.getColor();
		
		if(!pressEmitter.isPressed() || buttonPressed == null) {
			if(hoverTime == 0) {				
				if(shape == Shape.Rectangle) {
					GlUtils.setIgnoreBackgroundBlendMode(batch);
						batch.draw(button, area.getX(), area.getY(), area.getW(), area.getH());
					GlUtils.setDefaultBlendMode(batch);
				} else {
					batch.draw(button, area.getX(), area.getY(), area.getW(), area.getH());
				}
			} else if(hoverTime == 1) {				
				if(shape == Shape.Rectangle) {
					GlUtils.setIgnoreBackgroundBlendMode(batch);
						batch.draw(buttonHover, area.getX(), area.getY(), area.getW(), area.getH());
					GlUtils.setDefaultBlendMode(batch);
				} else {
					batch.draw(buttonHover, area.getX(), area.getY(), area.getW(), area.getH());
				}
			} else {				
				if(shape == Shape.Rectangle) {
					GlUtils.setIgnoreBackgroundBlendMode(batch);
					batch.draw(button, area.getX(), area.getY(), area.getW(), area.getH());
					
					GlUtils.setDefaultBlendMode(batch);
					batch.setColor(1.0f, 1.0f, 1.0f, color.a * hoverAlpha);
						batch.draw(buttonHover, area.getX(), area.getY(), area.getW(), area.getH());
					if(hintFBO)
						batch.flush();
				} else {
					batch.draw(button, area.getX(), area.getY(), area.getW(), area.getH());
					batch.setColor(1.0f, 1.0f, 1.0f, color.a * hoverAlpha);
						batch.draw(buttonHover, area.getX(), area.getY(), area.getW(), area.getH());
					if(hintFBO)
						batch.flush();
				}
			}
		} else {
			if(shape == Shape.Rectangle) {
				GlUtils.setIgnoreBackgroundBlendMode(batch);
					batch.draw(buttonPressed, area.getX(), area.getY(), area.getW(), area.getH());
				GlUtils.setDefaultBlendMode(batch);	
			} else
				batch.draw(buttonPressed, area.getX(), area.getY(), area.getW(), area.getH());
		}

		if(hintFBO)
			GlUtils.setAlpha(area, color.a);
		batch.setColor(color);
	}
}
