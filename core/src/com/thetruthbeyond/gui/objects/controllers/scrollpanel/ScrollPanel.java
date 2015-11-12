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

/*
 * BotMaker - file created and updated by Piotr Siatkowski (2015).
 */

package com.thetruthbeyond.gui.objects.controllers.scrollpanel;

import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.emitters.OnChooseOption;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.interfaces.FBODrawable;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.controllers.ControllerConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBar;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.thetruthbeyond.gui.utility.gl.BUFFER_NUMBER;
import com.thetruthbeyond.gui.utility.gl.Buffers;

/**
 * Scroll panel class is written in top-down y coordinate system. StartY is negative if panel is scrolled down.
 */
public class ScrollPanel extends Clickable {
	
	private SmartTexture canvas;
	private SmartTexture border;

	private int startY;
	private int totalH;

	private final List<Clickable> objects = new ArrayList<>(8);
	private final List<Integer> positionsY = new ArrayList<>(8);
	
	private Area objectArea;
	private int padding;
	private int borderSize;

	private int gapH;
	private int objectH;
	
	private int rows;
	private int columns;
	
	private FrameBuffer buffer;
	private TextureRegion region;

	private ScrollBar scrollBar;
	
	private float backgroundAlpha;
	
	private static final float APPEAR_TIME = 1.0f;
	private float appearTime = 0.0f;

	private float[] ratios = null;
	private float[] gaps   = null;
	
	private boolean drawBackground = true;
	private boolean includeBoundaryPadding = true;
	private boolean drawScrollBarEbenIfCantBeScrolled = false;
	
	private int pressedObjectIndex;

	private FileManager loader;

	// For decorating purposes. ///////////////////////////////////////////////////////////////////////
	protected ScrollPanel() { super(null); }

	public ScrollPanel getDecoratorParent() {
		return null;
	}

	protected ScrollBar getScrollBar() {
		return scrollBar;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected List<Clickable> getObjects() {
		return objects;
	}
	
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected List<Integer> getPositionsY() {
		return positionsY;
	}
	
	protected int getPressedObjectIndex() {
		return pressedObjectIndex;
	}
	
	protected int getRows() {
		return rows;
	}
	
	protected int getColumns() {
		return columns;
	}
	
	protected int getObjectAreaX() {
		return objectArea.getX();
	}
	
	protected int getObjectAreaY() {
		return objectArea.getY();
	}
	
	protected int getObjectAreaW() { 
		return objectArea.getW(); 
	}
	
	protected int getObjectAreaH() { 
		return objectArea.getH(); 
	}
	
	protected int getObjectH() { 
		return objectH; 
	}
	
	protected int getGapH() {
		return gapH; 
	}

	protected void setStartY(int startY) {
		this.startY = startY;
	}
	
	protected int getStartY() { 
		return startY; 
	}

	protected void setTotalH(int totalH) {
		this.totalH = totalH;
	}
	
	protected int getTotalH() { 
		return totalH; 
	}
	
	protected float getBackgroundAlpha() { 
		return backgroundAlpha; 
	}
	
	protected void setBackgroundAlpha(float alpha) { 
		backgroundAlpha = alpha; 
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	public ScrollPanel(ScrollPanelConfiguration configuration, FileManager loader, Clickable parent) {
		super(configuration, parent);

		canvas = loader.getTexture("Canvas");
		border = loader.getTexture("Border");

		this.loader = loader;

		borderSize 	= configuration.border;
		
		padding 	= Math.round(configuration.relativePadding * Math.min(area.getW(), area.getH()));				
		
		objectArea = new Area(configuration.area);
		if(configuration.drawBackground)
			objectArea.cutArea(padding + borderSize);
			
		columns = configuration.columnsNumber;
		rows 	= configuration.rowsNumber;	
		
		gapH 	= Math.round(objectArea.getH() * configuration.relativeGapH);	
		
		if(configuration.letResizeArea) {
			int gapsNumber = includeBoundaryPadding ? rows + 1 : rows - 1;
			objectH = Math.round((objectArea.getH() - gapsNumber * gapH) / (float) rows);

			int freePixels = objectArea.getH() - gapsNumber * gapH - rows * objectH;
			if(freePixels != 0) {
				if(freePixels > gapsNumber / 2.0f) {
					objectH = objectH + 1;
					area.setH(area.getH() + (freePixels - rows));
					objectArea.setH(objectArea.getH() + (freePixels - rows));
				} else {
					area.setH(area.getH() - freePixels);
					objectArea.setH(objectArea.getH() - freePixels);
				}
			}
		} else {
			if(configuration.includeBoundaryPadding) {
				objectH = Math.round((objectArea.getH() - (rows + 1) * gapH) / (float) rows);
				
				// Estimate more proper size of gap or element height.
				int freePixels = objectArea.getH() - (rows + 1) * gapH - rows * objectH;
				if(freePixels != 0) {
					if(freePixels > rows / 2)
						objectH = objectH + 1;
					else
						gapH = gapH + 1;
				}
			} else {
				objectH = Math.round((objectArea.getH() - (rows - 1) * gapH) / (float) rows);
				
				// Estimate more proper size of gap or element height.
				int freePixels = objectArea.getH() - (rows - 1) * gapH - rows * objectH;
				if(freePixels != 0) {
					if(freePixels > rows / 2)
						objectH = objectH + 1;
					else
						gapH = gapH + 1;
				}
			}
		}
		
		buffer = Buffers.getBuffer(BUFFER_NUMBER.ONE);
		scrollBar = configuration.scrollbar;
		
		backgroundAlpha = configuration.backgroundAlpha;
		
		// Calculate field and horizontal gap width ratios.
		ratios = new float[columns];
		float defaultW = (1 - (columns + 1) * configuration.relativeGapW) / columns;
		for(int i = 0; i != columns; i++)
			ratios[i] = defaultW;
		
		gaps = new float[columns + 1];
		for(int i = 0; i != columns + 1; i++)
			gaps[i] = configuration.relativeGapW;
		
		totalH = objectArea.getH();

		if((scrollBar = configuration.scrollbar) != null)
			getObserver().observeEmitter(scrollBar.getEmitter(OnChooseOption.Id));

		drawBackground = configuration.drawBackground;
		includeBoundaryPadding = configuration.includeBoundaryPadding;
		drawScrollBarEbenIfCantBeScrolled = configuration.drawScrollBarEvenIfCantBeScrolled;
	}
	
	protected boolean isBoundaryPaddingIncluded() {
		return includeBoundaryPadding;
	}

	private void updateScrollBar() {
		startY = Math.max(startY, 0);
		startY = Math.min(startY, totalH - objectArea.getH());

		scrollBar.setBarNumbers(startY, objectArea.getH(), Math.max(0, totalH - startY - objectArea.getH()));
	}

	private void readOffsetFromScrollBar() {
		float scrollPosition = scrollBar.getPercentagePosition();
		startY = (int)(scrollPosition * (totalH - objectArea.getH()));
	}

	private void updatePositionOfObjects() {
		for(int i = 0; i != objects.size(); i++)
			objects.get(i).changeY(positionsY.get(i) - startY);
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		if(emitterID == OnChooseOption.Id) {

			OnChooseOption onChooseOption = object.getEmitter(emitterID);
			int h = gapH + objectH; int rest = startY % h;

			switch(onChooseOption.getChosenOption()) {

				case OPTION_A: {
					readOffsetFromScrollBar(); updatePositionOfObjects(); break;
				}

				case OPTION_B: {
					if(rest - gapH < objectH / 2)
						startY = startY - rest - h;
					else
						startY -= rest;
					updateScrollBar(); updatePositionOfObjects(); break;
				}

				case OPTION_C: {
					if(rest - gapH < objectH / 2)
						startY = startY + rest + h;
					else
						startY += rest;
					updateScrollBar(); updatePositionOfObjects(); break;
				}
			}
		}
	}

	public void setFieldRatios(float[] ratios) {
		if(ratios.length == columns) {
			float totalW = 0.0f;
			for(int i = 0; i != columns; i++)
				totalW += ratios[i];
			
			// Normalization.
			if(totalW > 1) {
				for(int i = 0; i != columns; i++)
					ratios[i] /= totalW;
				gaps = new float[columns + 1];
			} else {
				gaps = new float[columns +  1];
				float gapW = (1 - totalW) / (columns + 1);
				for(int i = 0; i != columns + 1; i++)
					gaps[i] = gapW;
			}
			
			this.ratios = ratios;
		} else {
			float totalW = 0.0f;
			for(int i = 0; i != ratios.length; i++)
				totalW += ratios[i];
			
			if(totalW > 1) {
				for(int i = 0; i != ratios.length; i++)
					ratios[i] /= totalW;
			}
			
			for(int i = 0, j = 0; i < ratios.length; i += 2, j++)
				gaps[j] = ratios[i];

			for(int i = 1, j = 0; i < ratios.length; i += 2, j++)
				this.ratios[j] = ratios[i];
		}
	}
	
	@Override
	public void changeX(float x) {
		int dx =  Math.round(x) - area.getX();
		
		objectArea.setX(objectArea.getX() + dx);
		for(int i = 0; i != objects.size(); i++) {
			Clickable object = objects.get(i);
			object.changeX(object.getX() + dx);
		}
		
		area.setX(x);
		objectArea.setY(x + padding + borderSize);
	}
	
	@Override
	public void changeY(float y) {
		int dy = Math.round(y) - area.getY();

		for(int i = 0; i != objects.size(); i++) {
			Clickable object = objects.get(i);
			object.changeY(object.getY() + dy);
		}
		
		area.setY(y);
		objectArea.setY(y + padding + borderSize);
	}
	
	public int getObjectsSize() {
		return objects.size();
	}

	public Clickable getObject(int index) {
		return objects.get(index);
	}
	
	public Clickable getObject(int row, int column) {
		return objects.get(row * columns + column);
	}
	
	public <T extends Clickable> T substituteObject(Clickable object, T decorated) {
		int index = objects.indexOf(object);
		objects.set(index, decorated);	
		return decorated;
	}
	
	public <T extends ControllerConfiguration, U extends Clickable> U addObjectImmediately(Class<U> type, T configuration) {
		return addObject(type, configuration, false);
	}
	
	public <T extends ControllerConfiguration, U extends Clickable> U addObjectWithAppear(Class<U> type, T configuration) {
		return addObject(type, configuration, true);
	}
	
	protected <T extends ControllerConfiguration, U extends Clickable> U addObject(Class<U> type, T configuration, boolean isAppear) {
		int size = objects.size();
		
		int column  = size % columns;
		int row		= size / columns;
		
		try {
			Constructor<U> constructor;
			
			if(configuration != null)
				constructor = type.getConstructor(configuration.getClass(), FileManager.class, Clickable.class);
			else
				constructor = type.getConstructor(Area.class, FileManager.class, Clickable.class);
			
			float currentX = 0.0f;
			for(int i = 0; i < column; i++)
				currentX += ratios[i];
			for(int i = 0; i < column + 1; i++)
				currentX += gaps[i];
			
			Area area = new Area();
			area.setX(objectArea.getX() + objectArea.getW() * currentX);
			if(includeBoundaryPadding)
				area.setY(row * objectH + (row + 1) * gapH + objectArea.getY());
			else
				area.setY(row * objectH + row * gapH + objectArea.getY());
			area.setW(ratios[column] * objectArea.getW());
			area.setH(objectH);
			
			U object;
			if(configuration != null) {
				configuration.area = area;
				object = constructor.newInstance(configuration, loader, this);
			} else
				object = constructor.newInstance(area, loader, this);
			
			objects.add(object);		
			positionsY.add(object.getY());

			object.changeY(object.getY() - startY);
			
			// Set new bar ratios only if new row is added.
			if(scrollBar != null) {
				if(objects.size() > rows * columns && (columns == 1 || objects.size() % columns == 1)) {
					totalH = totalH + objectH + gapH;
					updateScrollBar();
				}
			}
			
			if(isAppear) {
				appearTime = 0.0f;
			} else
				appearTime = APPEAR_TIME;
			
			return object;
		} catch(InstantiationException exception) {
			new Logger().writeMessage("Error: ScrollPanel -> Instantation Esception", exception.getMessage());
		} catch(IllegalAccessException exception) {
			new Logger().writeMessage("Error: ScrollPanel -> Illegal Access Exception", exception.getMessage());
		} catch(IllegalArgumentException exception) {
			new Logger().writeMessage("Error: ScrollPanel -> Illegal Argument Exception", exception.getMessage());
		} catch(InvocationTargetException exception) {
			new Logger().writeMessage("Error: ScrollPanel -> Invocation Target Exception", exception.getMessage());
		} catch(NoSuchMethodException exception) {
			new Logger().writeMessage("Error: ScrollPanel -> No such Method Exception", exception.getMessage());
		} catch(SecurityException exception) {
			new Logger().writeMessage("Error: ScrollPanel -> Security Exception", exception.getMessage());
		}
		
		return null;
	}

	public void removeRow(int row) {
		if(row * columns > objects.size())
			return;

		if(objects.size() > rows * columns) {
			totalH = totalH - objectH - gapH;
			updateScrollBar();
		}
				
		// Removing objects.
		for(int column = columns - 1; column >= 0; column--)
			objects.remove(row * columns + column);

		// Removing last positions.
		for(int column = 0; column != columns; ++column)
			positionsY.remove(positionsY.size() - 1);

//		readOffsetFromScrollBar();
		updatePositionOfObjects();
		
		// Updating position.
		for(int i = row * columns; i < objects.size(); ++i)
			objects.get(i).changeY(positionsY.get(i) - startY);
	}
	
	protected boolean hasUnionWith(Area compareArea) {
		if(compareArea.getX() >= area.getX() && compareArea.getX() <= area.getX() + area.getW() && 
		   compareArea.getY() >= area.getY() + startY && compareArea.getY() <= area.getY() + area.getH())
			return true;
		if(compareArea.getX() + compareArea.getW() >= area.getX() && compareArea.getX() + compareArea.getW() <= area.getX() + area.getW() && 
		   compareArea.getY() >= area.getY() && compareArea.getY() <= area.getY() + area.getH())
			return true;
		if(compareArea.getX() >= area.getX() && compareArea.getX() <= area.getX() + area.getW() && 
		   compareArea.getY() + compareArea.getH() >= area.getY() && compareArea.getY() + compareArea.getH() <= area.getY() + area.getH())
			return true;
		return compareArea.getX() + compareArea.getW() >= area.getX() && compareArea.getX() + compareArea.getW() <= area.getX() + area.getW() &&
			   compareArea.getY() + compareArea.getH() >= area.getY() && compareArea.getY() + compareArea.getH() <= area.getY() + area.getH();
	}
	
	@Override
	public boolean contains(Position position) {
		return !(position.x <= objectArea.getX() || position.x >= objectArea.getX() + objectArea.getW() ||
				 position.y <= objectArea.getY() || position.y >= objectArea.getY() + objectArea.getH());
	}

	@Override
	public boolean executeMouseHover(Position position) {
		for(Clickable object : objects)
			object.executeMouseHover(position);
		if(scrollBar != null)
			scrollBar.executeMouseHover(position);
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {		
		if(scrollBar != null && scrollBar.executeMouseDrag(position)) {
			readOffsetFromScrollBar();
			updatePositionOfObjects();
			return true;
		} else
			return contains(position);
	}

	@Override
	public boolean executeMousePress(Position position) {	
		
		boolean isFieldPressed = false;
		for(int i = 0; i != objects.size(); i++) {
			Clickable object = objects.get(i);
			if(object.executeMousePress(position)) {
				isFieldPressed = true;
				pressedObjectIndex = i;
			}
		}

		if(isFieldPressed)
			return true;

		return scrollBar != null && scrollBar.executeMousePress(position);

	}

	@Override
	public boolean executeMousePressRight(Position position) {	
		for(int i = 0; i != objects.size(); i++) {
			Clickable object = objects.get(i);
			object.executeMousePressRight(position);
		}
		
		return false;
	}
	
	
	@Override
	public boolean executeMouseRelease(Position position) {
		if(scrollBar != null)
			scrollBar.executeMouseRelease(position);
		return true;
	}
	
	@Override
	public void update(float delta) {
		if(appearTime < APPEAR_TIME)
			appearTime = Math.min(APPEAR_TIME, appearTime + delta);
		
		for(Clickable object : objects)
			object.update(delta);

		if(scrollBar != null)
			scrollBar.update(delta);
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		drawBackground(batch);

		if(!objects.isEmpty()) {
			batch.end();
				drawObjectsToBuffer(batch);
			batch.begin();
				drawBuffer(batch);
		}
		
		drawScrollBar(batch);
	}
	
	protected void drawBackground(SmartSpriteBatch batch) {
		if(drawBackground) {
			Color color = batch.getColor();

			batch.setColor(1.0f, 1.0f, 1.0f, color.a * backgroundAlpha);
			batch.draw(canvas, area.getX(), area.getY(), area.getW(), area.getH());
				
			batch.setColor(color);
			
			// Drawing border
			batch.draw(border, area.getX(), area.getY(), area.getW(), borderSize);
			batch.draw(border, area.getX(), area.getY(), borderSize, area.getH());
			batch.draw(border, area.getX(), area.getY() + area.getH() - borderSize, area.getW(), borderSize);
			batch.draw(border, area.getX() + area.getW() - borderSize, area.getY(), borderSize, area.getH());
		}
	}
	
	protected void drawObjectsToBuffer(SmartSpriteBatch batch) {
		Color color = batch.getColor();

		float alpha = color.a * appearTime / APPEAR_TIME;
		buffer.begin();
			batch.begin();
			Gdx.graphics.getGL20().glClearColor(0.6f, 0.6f, 0.0f, 0.0f);
			Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			for(int i = 0; i != objects.size(); i++) {
				Clickable object = objects.get(i);
				if(!hasUnionWith(object.getArea()))
					continue;
	
				if(i >= objects.size() - columns)
					batch.setColor(1.0f, 1.0f, 1.0f, alpha);
				else
					batch.setColor(color);

				if(object instanceof FBODrawable)
					((FBODrawable) object).hintFBODrawing();
				object.draw(batch);
			}
			
			batch.setColor(Color.WHITE);
			batch.end();

		buffer.end();
	}
	
	protected void drawBuffer(SmartSpriteBatch batch) {

		Texture bufferTexture = buffer.getColorBufferTexture();
		if(region == null) {
			region = new TextureRegion(bufferTexture, objectArea.getX(), Consts.SCREEN_H - (objectArea.getY() + objectArea.getH()),
													  objectArea.getW(), objectArea.getH());
			region.flip(false, true);
		} else
			region.setTexture(bufferTexture);

		batch.draw(region, objectArea.getX(), objectArea.getY(), objectArea.getW(), objectArea.getH());
		batch.setColor(Color.WHITE);
	}
	
	protected void drawScrollBar(SmartSpriteBatch batch) {
		if(scrollBar != null && (drawScrollBarEbenIfCantBeScrolled || objects.size() > rows * columns))
			scrollBar.draw(batch);
	}

	public void clear() {
		objects.clear();
		positionsY.clear();

		startY = 0;
		totalH = objectArea.getH();
	}
}
