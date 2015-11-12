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

package com.thetruthbeyond.gui.objects.tabs.overtabs;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.thetruthbeyond.gui.action.emitters.OnPress;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.BackgroundArea;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.BackgroundAreaConfiguration;
import com.thetruthbeyond.gui.objects.shareable.DarknessDrawer;
import com.thetruthbeyond.gui.objects.tabs.OverTab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;
import com.thetruthbeyond.gui.utility.gl.BUFFER_NUMBER;
import com.thetruthbeyond.gui.utility.gl.Buffers;
import com.thetruthbeyond.gui.utility.gl.GlUtils;

public abstract class WildcardsTab extends OverTab {
	private final BackgroundArea middleBackground;
	
	private static final int BACKGROUND_CUT = Consts.BORDER_SIZE * 2;
	
	private static final float BUTTON_PAD_W = 0.03f;
	private static final float BUTTON_PAD_H = 0.03f;
	
	private static final float BACKGROUND_ALPHA = 0.0f;
	
	private final Position cornerPosition = new Position();
	private final Position pressedPosition = new Position();
	
	private boolean isDragged = false;
	private boolean isPressed = false;
	
	private final SmartTexture canvas;
	private final SmartTexture border;
	private final SmartTexture background;
	
	private final FrameBuffer buffer;
	
	protected Button[] buttons;
	protected int[] OFFSETS_X;
	protected int[] OFFSETS_Y;
	
	private int currentCard = -1;
	
	private final OnPress pressEmitter;
	
	protected int COLUMNS = 0;
	protected int ROWS = 0;
	
	@SuppressWarnings("ZeroLengthArrayAllocation")
	protected Wildcard[] cards = new Wildcard[0];
	
	public WildcardsTab(Area area, FileManager loader, DarknessDrawer darkness, Clickable parent) {
		super(area, darkness, parent);
		
		canvas 		= loader.getTexture("Canvas");
		border 		= loader.getTexture("Border");
		background 	= loader.getTexture("SmallWoodBackground");
		
		buffer = Buffers.getBuffer(BUFFER_NUMBER.TWO);
		
		pressEmitter = new OnPress(this);
		addEmitter(pressEmitter);
		
		BackgroundAreaConfiguration configuration = new BackgroundAreaConfiguration();

		configuration.area = new Area(area).cutArea(BACKGROUND_CUT);
		
		configuration.border 			= Consts.BORDER_SIZE;
		configuration.backgroundAlpha 	= Consts.BACKGROUND_ALPHA;
		
		Area innerArea = new Area(configuration.area).cutArea(BORDER_SIZE);
		
		initializeCards();
		initializeColumns();
		
		buttons = new Button[cards.length];
		OFFSETS_X  = new int[cards.length];
		OFFSETS_Y  = new int[cards.length];
							
		middleBackground = new BackgroundArea(configuration, loader, this);
		
		int buttonW = Math.round((1 - (COLUMNS + 1) * BUTTON_PAD_W) * innerArea.getW() / COLUMNS);
		int buttonH = Math.round((1 - (ROWS + 1) * BUTTON_PAD_H) * innerArea.getH() / ROWS);
		int gapW = Math.round((BUTTON_PAD_W) * innerArea.getW());
		int gapH = Math.round((BUTTON_PAD_H) * innerArea.getH());
					
		Area buttonArea = new Area(middleBackground.getX(), middleBackground.getY(), buttonW, buttonH);
		initializeButtons(buttonArea, loader, this);
		
		int totalW = COLUMNS * buttonW + (COLUMNS + 1) * gapW;
		int totalH = ROWS * buttonH + (ROWS + 1) * gapH;
			
		if(totalW != innerArea.getW()) {
			innerArea.setW(totalW);
			configuration.area.setW(totalW + COLUMNS * BORDER_SIZE);
		}
		
		if(totalH != innerArea.getH()) {
			innerArea.setH(totalH);
			configuration.area.setH(totalH + ROWS * BORDER_SIZE);
		}
		
		OFFSETS_X[0] = gapW;
		OFFSETS_Y[0] = gapH;
		for(int i = 1; i != buttons.length; i++) {
			if(i % COLUMNS != 0) {
				OFFSETS_X[i] = OFFSETS_X[0] + gapW + buttonW;
				OFFSETS_Y[i] = OFFSETS_Y[i-1];
			} else {
				OFFSETS_X[i] = OFFSETS_X[0];
				OFFSETS_Y[i] = OFFSETS_Y[i-1] + gapH + buttonH;	
			}
			
			buttons[i].changeX(middleBackground.getX() + OFFSETS_X[i]);
			buttons[i].changeY(middleBackground.getY() + OFFSETS_Y[i]);
		}
	}
	
	protected abstract void initializeCards();
	protected abstract void initializeColumns();
	protected abstract void initializeButtons(Area area, FileManager loader, Clickable parent);
	
	@Override @UnhandledMethod
	public void onTabEnter() {}
	
	public Wildcard getWildcard() {
		if(currentCard >= 0 && currentCard < cards.length) {
			return cards[currentCard];
		} else
			return null;
	}

	@Override
	public void show() {
		show(Position.NullPosition);
	}

	public void show(Position position) {
		super.show();
		
		Clickable parent = getParent();
		
		int x = Math.round(position.x - getW() / 2.0f);
		x = Math.max(x, parent.getX());
		x = Math.min(x, parent.getX() + parent.getW() - getW());
		
		int y = Math.round(position.y - getH() / 2.0f);
		y = Math.max(y, parent.getY());
		y = Math.min(y, parent.getY() + parent.getH() - getH());
		
		changeX(x);
		changeY(y);	
		
		cornerPosition.x = x;
		cornerPosition.y = y;
	}

	@Override
	public void hide() {
		super.hide();
		isDragged = false;
	}

	@Override
	public void changeX(float x) {
		super.changeX(x);
		
		middleBackground.changeX(area.getX() + BACKGROUND_CUT);	
		
		for(int i = 0; i != buttons.length; i++)
			buttons[i].changeX(middleBackground.getX() + OFFSETS_X[i] + BORDER_SIZE);
	}
	
	@Override
	public void changeY(float y) {
		super.changeY(y);
		
		middleBackground.changeY(area.getY() + BACKGROUND_CUT);
		
		for(int i = 0; i != buttons.length; i++)
			buttons[i].changeY(middleBackground.getY() + OFFSETS_Y[i] + BORDER_SIZE);
	}
	
	@Override
	public boolean executeMouseHover(Position position) {
		if(isActive()) {
			if(!contains(position)) {
				isDragged = false;
				super.hide();
			} else {
				for(Button button : buttons)
					button.executeMouseHover(position);
			}
		}
		
		return false;
	}
	
	@Override
	public boolean executeMouseDrag(Position position) {	
		if(isActive()) {
			if(isDragged) {
				Clickable parent = getParent();
				
				if(position.x <= parent.getX() || position.x >= parent.getX() + parent.getW() ||
				   position.y <= parent.getY() || position.y >= parent.getY() + parent.getH() ) {
					
					isDragged = false;
					isPressed = false;
					
					super.hide();
				} else {
					if(!isPressed) {
						pressedPosition.x = position.x;
						pressedPosition.y = position.y;
						return true;
					} else {
						int dx = position.x - pressedPosition.x;
						int dy = position.y - pressedPosition.y;

						int cornerX = cornerPosition.x + dx;
						int cornerY = cornerPosition.y + dy;
						
						if(cornerX < parent.getX() || cornerX + getW() > parent.getX() + parent.getW()) {
							pressedPosition.x = position.x;
							
							cornerX = Math.max(cornerX, parent.getX());
							cornerX = Math.min(cornerX, parent.getX() + parent.getW() - getW());
							
							changeX(cornerX);
							
							cornerPosition.x = cornerX;
						} else {
							cornerX = Math.max(cornerX, parent.getX());
							cornerX = Math.min(cornerX, parent.getX() + parent.getW() - getW());
							
							changeX(cornerX);
						}
							
						
						if(cornerY < parent.getY() || cornerY + getH() > parent.getY() + parent.getH()) {
							pressedPosition.y = position.y;
							
							cornerY = Math.max(cornerY, parent.getY());
							cornerY = Math.min(cornerY, parent.getY() + parent.getH() - getH());
							
							changeY(cornerY);	
							
							cornerPosition.y = cornerY;
						} else {
							cornerY = Math.max(cornerY, parent.getY());
							cornerY = Math.min(cornerY, parent.getY() + parent.getH() - getH());
							
							
							changeY(cornerY);	
						}
						
						return true;
					}
				}
			} else
				return false;
		}
		
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(isActive()) {
			currentCard = -1;
			for(int i = 0; i != buttons.length; i++) {
				if(buttons[i].executeMousePress(position)) {
					currentCard = i;				
					break;
				}
			}
			
			if(currentCard >= 0) {
				pressEmitter.setPressed(true);
				return true;
			} else if(contains(position)) {				
				pressedPosition.x = position.x;
				pressedPosition.y = position.y;
				isDragged = true;
				isPressed = true;
				return false;
			}
		}
		
		return false;
	}

	@Override
	public boolean executeMouseRelease(Position position) {		
		for(Button button : buttons)
			button.executeMouseRelease(position);
		
		cornerPosition.x = getX();
		cornerPosition.y = getY();
		
		isDragged = false;
		return false;
	}

	@Override
	public boolean executeMousePressRight(Position position) {
		show(position);
		return true;
	}

	@Override
	public void update(float delta) {
		super.update(delta);

		if(isVisible())
			for(Button button : buttons)
				button.update(delta);
	}
	
	@Override
	public void draw(SmartSpriteBatch batch) {
		super.draw(batch);

		if(isVisible()) {
			batch.end();
			
			drawToBuffer(batch);
			
			batch.begin();
			drawBuffer(batch, buffer, getVisibilityAlpha());
			
			batch.setColor(Color.WHITE);
		}
	}

	@Override
	protected void drawToBuffer(SmartSpriteBatch batch) {
		buffer.begin();
		batch.begin();
			Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
			drawBackground(batch);
		batch.end();
		buffer.end();

		buffer.begin();
		batch.begin();
			middleBackground.draw(batch);

			for(Button button : buttons) {
				button.hintFBODrawing();
				button.draw(batch);
			}

		batch.end();
		
		GlUtils.clearAlpha(area);
		
		buffer.end();	
	}
	
	protected void drawBackground(SmartSpriteBatch batch) {	
		// Drawing wood background.
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		batch.draw(background, area.getX(), area.getY(), area.getW(), area.getH());
		
		// Drawing canvas background.
		batch.setColor(1.0f, 1.0f, 1.0f, BACKGROUND_ALPHA);
		batch.draw(canvas, area.getX(), area.getY(), area.getW(), area.getH());
				
		// Drawing border
		batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		batch.draw(border, area.getX(), area.getY(), area.getW(), BORDER_SIZE);
		batch.draw(border, area.getX(), area.getY(), BORDER_SIZE, area.getH());
		batch.draw(border, area.getX(), area.getY() + area.getH() - BORDER_SIZE, area.getW(), BORDER_SIZE);
		batch.draw(border, area.getX() + area.getW() - BORDER_SIZE, area.getY(), BORDER_SIZE, area.getH());
	}

	@Override @UnhandledMethod
	public void onTabLeave() {}

	@Override @UnhandledMethod
	public void clear() {}
}
