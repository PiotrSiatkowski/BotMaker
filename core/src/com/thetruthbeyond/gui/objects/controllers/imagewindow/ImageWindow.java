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

package com.thetruthbeyond.gui.objects.controllers.imagewindow;

import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;

import org.jetbrains.annotations.Nullable;

public class ImageWindow extends Clickable {

	@Nullable
	private SmartTexture image;

	private final SmartTexture emptyImage;
	private final SmartTexture addButton;
	private final SmartTexture border;
	
	private float BACKGROUND_ALPHA = 1.0f;

	private FileManager manager;

	private boolean isReactive = true;
	private boolean isHovered = false;
	private boolean isEmpty = true;
	
	private final Area imageArea;

	private static final float SHOW_TIME = 0.6f;
	private float showTime;
	
	private String name = "";
		
	private final Sound HOVER_SOUND;

	// Reactive window attributes.
	private int BUTTON_X;
	private int BUTTON_Y;

	private JFileChooser chooser;
	private FileFilter imageFilter;

	public ImageWindow(ImageWindowConfiguration configuration, final FileManager loader, Clickable parent) {
		super(configuration, parent);
		
		emptyImage 	= loader.getTexture("EmptyPortrait");
		addButton 	= loader.getTexture("AddButton");
		border		= loader.getTexture("Border");

		if(configuration.image != null)
			image = loader.getTexture(configuration.image);

		manager = loader;

		HOVER_SOUND = loader.getSound("ButtonHover");
		
		BACKGROUND_ALPHA = configuration.backgroundAlpha;
		
		imageArea = new Area(configuration.area);
		imageArea.cutArea(BORDER_SIZE + configuration.padding);

		if(isReactive = configuration.reactive) {
			BUTTON_X = Math.round(imageArea.getX() + imageArea.getW() / 2.0f - addButton.getW() / 2.0f);
			BUTTON_Y = Math.round(imageArea.getY() + imageArea.getH() / 2.0f - addButton.getH() / 2.0f);

			// Setting convenient icon for dialog.
			chooser = new JFileChooser() {
				@Override
				protected JDialog createDialog(Component component) {
					try {
						JDialog dialog = super.createDialog(component);

						String iconPath = loader.getPath("Icon16");
						if(iconPath == null)
							throw new RuntimeException("Icon16 could not be found.");
						else if(iconPath.endsWith(".png")) {
							BufferedImage image = ImageIO.read(new File(iconPath));
							dialog.setIconImage(image);
							return dialog;
						} else
							throw new RuntimeException("Icon16 file had been found but has wrong extension.");
					} catch(IOException ignored) {
						new Logger().writeMessage("Error", "Icon16.png asset could not be loaded.");
						return null;
					} catch(RuntimeException exception) {
						new Logger().writeMessage("Error", exception.getMessage());
						return null;
					}
				}
			};

			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Portrait selection");

			imageFilter = new FileNameExtensionFilter("Image files", "jpg", "png", "tif", "bmp");
			chooser.setFileFilter(imageFilter);
			chooser.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Desktop"));

			// Disable the "All files" option.
			chooser.setAcceptAllFileFilterUsed(false);
		}
	}
	
	@Override
	public boolean contains(Position position) {
		return !(position.x < imageArea.getX() || position.x > imageArea.getX() + imageArea.getW() ||
				 position.y < imageArea.getY() || position.y > imageArea.getY() + imageArea.getH());
	}

	@Override
	public boolean executeMouseHover(Position position) {
		if(!isReactive)
			return false;
		if(contains(position)) {
			if(!isHovered) {
				HOVER_SOUND.play();
				isHovered = true;
			}
		} else
			isHovered = false;
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseDrag(Position position) {
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(!isReactive)
			return false;
		if(contains(position)) {
		    EventQueue.invokeLater( new Runnable() {
		    	@Override
		    	public void run() {
		    		if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				    	String path = chooser.getSelectedFile().getAbsolutePath();
				    	FileHandle portrait = Gdx.files.absolute(path);

						manager.deletePortrait(name);

						if(image != null && !isEmpty)
							image.dispose();
						image = null;

						manager.savePortrait(name, portrait);

				    	updateImage(name);
				    }
		    	}
		    });
		    
		    return true;
		}
	    
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseRelease(Position position) {
		return false;
	}

	public void updateImage(String botName) {
		image = null;
		
		this.name = botName;
		
		isEmpty = true;
		FileHandle botDirectory = Gdx.files.internal("Bots/" + botName);
		if(botDirectory.isDirectory())
		{
			for(FileHandle file : botDirectory.list()) {
				if(file.nameWithoutExtension().equals("Portrait") && imageFilter.accept(file.file())) {
					image = new SmartTexture(file);
					isEmpty = false;
				}
			}
		}
	}
	
	@Override
	public void update(float delta) {
		if(!isReactive)
			return;
		if(isHovered) {
			if(showTime < SHOW_TIME)
				showTime = Math.min(SHOW_TIME, showTime + delta);
		} else {
			if(showTime > 0)
				showTime = Math.max(0, showTime - delta);
		}
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		
		if(image == null) {
			batch.setColor(1.0f, 1.0f, 1.0f, BACKGROUND_ALPHA);
			batch.draw(emptyImage, imageArea.getX(), imageArea.getY(), imageArea.getW(), imageArea.getH());
		} else
			batch.draw(image, imageArea.getX(), imageArea.getY(), imageArea.getW(), imageArea.getH());
		batch.setColor(Color.WHITE);
		
		// Drawing border
		batch.draw(border, area.getX(), area.getY(), area.getW(), BORDER_SIZE);
		batch.draw(border, area.getX(), area.getY(), BORDER_SIZE, area.getH());
		batch.draw(border, area.getX(), area.getY() + area.getH() - BORDER_SIZE, area.getW(), BORDER_SIZE);
		batch.draw(border, area.getX() + area.getW() - BORDER_SIZE, area.getY(), BORDER_SIZE, area.getH());
		
		if(isHovered) {
			if(showTime < SHOW_TIME)
				batch.setColor(1.0f, 1.0f, 1.0f, showTime / SHOW_TIME);
			batch.draw(addButton, BUTTON_X, BUTTON_Y, addButton.getW(), addButton.getH());
		} else if(showTime > 0) {
			batch.setColor(1.0f, 1.0f, 1.0f, showTime / SHOW_TIME);
			batch.draw(addButton, BUTTON_X, BUTTON_Y, addButton.getW(), addButton.getH());
		}
		
		batch.setColor(Color.WHITE);
	}
}
