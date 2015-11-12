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

import java.util.List;

import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.action.Observer;
import com.thetruthbeyond.gui.interfaces.Decorator;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.controllers.ControllerConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBar;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

public class ScrollPanelDecorator extends ScrollPanel implements Decorator {

	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	private final ScrollPanel parent;

	public ScrollPanelDecorator(ScrollPanel parent) {
		this.parent = parent;
	}

	@Override
	public void setParent(Clickable parent) {
		parent.setParent(parent);
	}

	@Override
	public Clickable getRoot() {
		ScrollPanel panel = getDecoratorParent();
		while(true) {
			if(panel.getDecoratorParent() == null)
				return panel.getParent();
			else
				panel = panel.getDecoratorParent();
		}
	}

	@Override
	public ScrollPanel getDecoratorParent() {
		return parent;
	}

	@Override
	public void reactToEmittedSignal(EventEmitter object, int emitterID) {
		parent.reactToEmittedSignal(object, emitterID);
	}

	@Override
	public Observer<Emitter> getObserver() {
		return parent.getObserver();
	}

	@Override
	public <T extends Emitter> Observer<T> getSpecializedObserver(int emitterID) {
		return parent.getSpecializedObserver(emitterID);
	}

	@Override
	public void addEmitter(Emitter indicator) {
		parent.addEmitter(indicator);
	}

	@Override
	public <T extends Emitter> T getEmitter(int emitterID) {
		return parent.getEmitter(emitterID);
	}
	
	@Override
	protected boolean isBoundaryPaddingIncluded() {
		return parent.isBoundaryPaddingIncluded();
	}
	
	@Override
	protected ScrollBar getScrollBar() {
		return parent.getScrollBar();
	}
	
	@Override
	protected List<Clickable> getObjects() {
		return parent.getObjects();
	}

	@Override
	protected List<Integer> getPositionsY() {
		return parent.getPositionsY();
	}
	
	@Override
	protected int getPressedObjectIndex() {
		return parent.getPressedObjectIndex();
	}
	
	@Override
	protected int getRows() {
		return parent.getRows();
	}
	
	@Override
	protected int getColumns() {
		return parent.getColumns();
	}
	
	@Override
	protected int getObjectAreaX() {
		return parent.getObjectAreaX();
	}
	
	@Override
	protected int getObjectAreaY() {
		return parent.getObjectAreaY();
	}
	
	@Override
	protected int getObjectAreaW() { 
		return parent.getObjectAreaW();
	}
	
	@Override
	protected int getObjectAreaH() { 
		return parent.getObjectAreaH();
	}
	
	@Override
	protected int getObjectH() { 
		return parent.getObjectH();
	}
	
	@Override
	protected int getGapH() {
		return parent.getGapH();
	}
	
	@Override
	public int getX() {
		return parent.getX();
	}
	
	@Override
	public int getY() {
		return parent.getY();
	}
	
	@Override
	public int getW() {
		return parent.getW();
	}
	
	@Override
	public int getH() {
		return parent.getH();
	}
	@Override
	protected void setStartY(int startY) { 
		parent.setStartY(startY);
	}
	
	@Override
	protected int getStartY() { 
		return parent.getStartY();
	}
	
	@Override
	protected void setTotalH(int totalH) { 
		parent.setTotalH(totalH);
	}
	
	@Override
	protected int getTotalH() { 
		return parent.getTotalH(); 
	}
	
	@Override
	protected void setBackgroundAlpha(float alpha) { 
		parent.setBackgroundAlpha(alpha);
	}
	
	@Override
	protected float getBackgroundAlpha() { 
		return parent.getBackgroundAlpha(); 
	}
	
	@Override
	public void clear() {
		parent.clear();
	}

	@Override
	public void setFieldRatios(float[] ratios) {
		parent.setFieldRatios(ratios);
	}

	@Override
	public void changeX(float x) {
		parent.changeX(x);
	}
	
	@Override
	public void changeY(float y) {
		parent.changeY(y);
	}
	
	@Override
	public int getObjectsSize() {
		return parent.getObjectsSize();
	}

	@Override
	public Clickable getObject(int index) {
		return parent.getObject(index);
	}

	@Override
	public Clickable getObject(int row, int column) {
		return parent.getObject(row, column);
	}

	@Override
	public <T extends Clickable> T substituteObject(Clickable object, T decorated) {
		return parent.substituteObject(object, decorated);
	}

	@Override
	public <T extends ControllerConfiguration, U extends Clickable> U addObjectImmediately(Class<U> type, T configuration) {
		return parent.addObjectImmediately(type, configuration);
	}

	@Override
	public <T extends ControllerConfiguration, U extends Clickable> U addObjectWithAppear(Class<U> type, T configuration) {
		return parent.addObjectWithAppear(type, configuration);
	}

	@Override
	public void removeRow(int row) {
		parent.removeRow(row);
	}

	@Override
	protected boolean hasUnionWith(Area compareArea) {
		return parent.hasUnionWith(compareArea);
	}
	
	@Override
	public boolean contains(Position position) {
		return parent.contains(position);
	}

	@Override
	public boolean executeMouseHover(Position position) {
		return parent.executeMouseHover(position);
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		return parent.executeMouseDrag(position);
	}

	@Override
	public boolean executeMousePress(Position position) {
		return parent.executeMousePress(position);
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		return parent.executeMouseRelease(position);
	}

	@Override
	public Clickable getParent() {
		return parent.getParent();
	}

	@Override
	public void update(float delta) {
		parent.update(delta);
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		parent.draw(batch);
	}
	
	@Override
	protected void drawBackground(SmartSpriteBatch batch) {
		parent.drawBackground(batch);
	}
	
	@Override
	protected void drawObjectsToBuffer(SmartSpriteBatch batch) {
		parent.drawObjectsToBuffer(batch);
	}
	
	@Override
	protected void drawBuffer(SmartSpriteBatch batch) {	
		parent.drawBuffer(batch);
	}
	
	@Override
	protected void drawScrollBar(SmartSpriteBatch batch) {
		parent.drawScrollBar(batch);
	}
	
}
