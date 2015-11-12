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

package com.thetruthbeyond.botmaker.gui.tabs;

import com.thetruthbeyond.bot.CurrentBot;
import com.thetruthbeyond.botmaker.BotMaker;
import com.thetruthbeyond.botmaker.logic.ExportThread;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.complex.PathChooser;
import com.thetruthbeyond.gui.objects.buttons.simple.WindowsPublishButton;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanel;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanelConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.LabeledTextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.LabeledTextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.MemoryTextField;
import com.thetruthbeyond.gui.objects.tabs.Tab;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

import  com.thetruthbeyond.chatterbean.AliceBot;

import com.badlogic.gdx.graphics.Color;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

import java.io.File;

import static com.thetruthbeyond.gui.configuration.Consts.RESOLUTION;
import static com.thetruthbeyond.gui.configuration.Consts.RES_1366x768;

public class BuildTab extends Tab {
	
	// Windows publish panel. //////////////////////////////////////////////////////////////
	private final ScrollPanel emptyWindowsPanel;
	
	private static final float WINDOWS_PANEL_X_PARAMETER = 0.04f;
	private static final float WINDOWS_PANEL_Y_PARAMETER = 0.08f;
	private static final float WINDOWS_PANEL_W_PARAMETER = RESOLUTION == RES_1366x768 ? 0.921f : 0.920f;
	private static final float WINDOWS_PANEL_H_PARAMETER = 0.20f;
	////////////////////////////////////////////////////////////////////////////////////////
		
	// Path text field. ////////////////////////////////////////////////////////////////////
	private final MemoryTextField windowsPath;
	
	private static final float PATH_FIELD_X_PARAMETER = 0.054f;
	private static final float PATH_FIELD_Y_PARAMETER = 0.160f;
	private static final float PATH_FIELD_W_PARAMETER = 0.800f;
	private static final float PATH_FIELD_H_PARAMETER = 0.090f;
	
	private static final float RELATIVE_LABEL_X_OFFSET	= + 0.01f;
	private static final float RELATIVE_LABEL_Y_OFFSET 	= - 0.01f;
	
	private static final float LABEL_SIZE_PARAMETER	 	= 0.6f;
	////////////////////////////////////////////////////////////////////////////////////////
	
	// File chooser. ///////////////////////////////////////////////////////////////////////
	private final Button chooser;
	
	private static final float CHOOSER_X_PARAMETER = 0.903f;
	private static final float CHOOSER_Y_PARAMETER = 0.182f;
	////////////////////////////////////////////////////////////////////////////////////////
	
	// PublishTab button. //////////////////////////////////////////////////////////////////
	private final Button windowsPublishButton;
	
	private static final float PUBLISH_X_PARAMETER = 0.105f;
	private static final float PUBLISH_Y_PARAMETER = 0.340f;
	////////////////////////////////////////////////////////////////////////////////////////
	
	private final SmartTexture background;
	private final SmartTexture loadingCircle;
	
	private String name = "";
	
	private ExportThread loading = null;
	
	private boolean isExporting = false;

	private static final float ANIMATION_TIME = 1.0f;

	private float exportTime = 0.0f;
	private float animationAngle = 0.0f;
	
	private static final float CIRCLE_Y_PARAMETER = 0.55f;
	private final int circleX;
	private final int circleY;

	private FileManager loader;

	public BuildTab(Area area, FileManager loader, Clickable parent) {
		super(area, parent);

		this.loader = loader;

		background = loader.getTexture("WoodBackgroundDark");
		loadingCircle = loader.getTexture("LoadingCircle");
		
		TextFieldConfiguration configuration = configureWindowsPathField();
		TextField windowsPath = new TextField(configuration, loader, this);
		
		LabeledTextFieldConfiguration labelConfiguration = configureWindowsPathFieldLabel();
		windowsPath = new LabeledTextField(windowsPath, labelConfiguration);
		this.windowsPath = new MemoryTextField(windowsPath);
		
		ScrollPanelConfiguration windowsPanelConfiguration = configureWindowsPanel();
		emptyWindowsPanel = new ScrollPanel(windowsPanelConfiguration, loader, this);
		
		windowsPublishButton = new WindowsPublishButton(loader, this);
		windowsPublishButton.setPosition((int)(area.getX() + area.getW() * PUBLISH_X_PARAMETER),
										 (int)(area.getY() + area.getH() * PUBLISH_Y_PARAMETER));
		
		chooser = new PathChooser(this.windowsPath, loader, this);
		chooser.setPosition( new Position((int)(area.getX() + area.getW() * CHOOSER_X_PARAMETER),
										  (int)(area.getY() + area.getH() * CHOOSER_Y_PARAMETER)));
		
		circleX = (area.getX() + area.getW() - loadingCircle.getW()) / 2;
		circleY =  area.getY() + Math.round(CIRCLE_Y_PARAMETER * area.getH());
	}
	
	private ScrollPanelConfiguration configureWindowsPanel() {
		ScrollPanelConfiguration configuration = new ScrollPanelConfiguration();
		configuration.area.setX(area.getX() + area.getW() * WINDOWS_PANEL_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * WINDOWS_PANEL_Y_PARAMETER);
		configuration.area.setW(area.getW() * WINDOWS_PANEL_W_PARAMETER);
		configuration.area.setH(area.getH() * WINDOWS_PANEL_H_PARAMETER);
		
		configuration.border			= Consts.BORDER_SIZE;
		configuration.backgroundAlpha 	= Consts.BACKGROUND_ALPHA;
		
		configuration.columnsNumber = 1;
		configuration.rowsNumber = 1;
		
		return configuration;
	}
	
	private TextFieldConfiguration configureWindowsPathField() {
		TextFieldConfiguration configuration = new TextFieldConfiguration();
		configuration.area.setX(area.getX() + area.getW() * PATH_FIELD_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * PATH_FIELD_Y_PARAMETER);
		configuration.area.setW(area.getW() * PATH_FIELD_W_PARAMETER);
		configuration.area.setH(area.getH() * PATH_FIELD_H_PARAMETER);
		
		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		configuration.characters	= Coding.signs;
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border				= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	private LabeledTextFieldConfiguration configureWindowsPathFieldLabel() {
		LabeledTextFieldConfiguration configuration = new LabeledTextFieldConfiguration();
		
		configuration.labelFontname		= FontType.GUI_FONT;
		configuration.labelFontColor 	= new Color(0.4f, 0.5f, 0.2f, 1.0f);
		configuration.labelTitle		= "Select an export directory";
		configuration.relativeLabelPaddingX	= RELATIVE_LABEL_X_OFFSET;
		configuration.relativeLabelPaddingY	= RELATIVE_LABEL_Y_OFFSET;
		
		configuration.labelSizeParameter	=	LABEL_SIZE_PARAMETER;
		
		return configuration;
	}
	
	@Override
	public void onTabEnter() {

		// Setting publish path to default.
		AliceBot bot = CurrentBot.instance;
		if(bot != null) {
			String tempName = bot.getContext().getProperty("name");

			// Name has changed.
			if(!tempName.equals(name)) {
				// Remember path.
				windowsPath.setInput(System.getProperty("user.home") + "\\Desktop");
				windowsPath.confirmInput();

				windowsPath.setInput(System.getProperty("user.home") + "\\Desktop");
			}

			if(!isExporting)
				BotMaker.messages.hide();

			name = tempName;
		}
	}
	
	@Override
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		if(isExporting)
			return false;
		windowsPublishButton.executeMouseHover(position);
		chooser.executeMouseHover(position);
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		return true;
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(isExporting)
			return false;

		boolean success = false;
		if(!chooser.executeMousePress(position)) {
			if(windowsPublishButton.contains(position)) {
				if(new File(windowsPath.getInput() + File.separator + name + ".exe").exists()) {
					BotMaker.messages.showMessage("The file with \"" + name + "\" name already exists in the indicated directory.");
					BotMaker.messages.setNeverending(false);
				} else {
					loading = new ExportThread(name, windowsPath.getInput(), loader);
					loading.start();
					isExporting = true;

					BotMaker.messages.showMessage("Your chatterbot is now being exported. It may take a few seconds.");
					BotMaker.messages.setNeverending(true);
				}

				success = true;
			}
		}

		return windowsPath.executeMousePress(position) || success;
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		windowsPath.executeMouseRelease(position);
		return true;
	}

	@Override
	public void update(float delta) {
		BotMaker.messages.update(delta);
		if(isExporting) {
			exportTime += delta;
			animationAngle = (exportTime % ANIMATION_TIME) * 360.0f;
			
			if(!loading.isAlive()) {
				isExporting = false;
				animationAngle = 0.0f;
				exportTime = 0.0f;

				if(loading.hasErrorOccured()) {
					BotMaker.messages.showMessage(loading.getErrorMessage());
					BotMaker.messages.setNeverending(false);
				} else
					BotMaker.messages.fade();
			}	
		} else {
			windowsPath.update(delta);
			windowsPublishButton.update(delta);
			chooser.update(delta);
		}
	}
	
	@Override @UnhandledMethod
	public void onTabLeave() {}

	@Override
	public void draw(SmartSpriteBatch batch) {
		batch.draw(background, area.getX(), area.getY(), area.getW(), area.getH());
		
		emptyWindowsPanel.draw(batch);
		windowsPath.draw(batch);
		windowsPublishButton.draw(batch);
		chooser.draw(batch);	
		
		BotMaker.messages.draw(batch);
		if(isExporting)
			batch.draw(loadingCircle, circleX, circleY, loadingCircle.getW(), loadingCircle.getH(), animationAngle);
	}

	@Override @UnhandledMethod
	public void dispose() {}
	
	@Override @UnhandledMethod
	public void clear() {}
}