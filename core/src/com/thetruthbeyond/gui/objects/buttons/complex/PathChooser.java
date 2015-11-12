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

package com.thetruthbeyond.gui.objects.buttons.complex;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFileChooser;

import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.MemoryTextField;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;

@SuppressWarnings("AssignmentToSuperclassField")
public class PathChooser extends Button {

	private String path = "";
	private final MemoryTextField textField;
	
	public PathChooser(MemoryTextField textField, FileManager loader, Clickable parent) {
		super(parent);
		initialize(loader);
		
		this.textField = textField;
	}
	
	public PathChooser(Area area, MemoryTextField textField, FileManager loader, Clickable parent) {
		super(area, parent);
		initialize(loader);
		
		this.textField = textField;
	}

	@Override
	protected void initialize(FileManager loader) {
		button = loader.getTexture("Directory");
		buttonHover = loader.getTexture("DirectoryOn");
		shape = Shape.Oval;

		soundHover = loader.getSound("ButtonHover2");
		soundPressed = loader.getSound("ButtonClick2");
	}
	
	@Override
	public boolean executeMousePress(Position position) {
		super.executeMousePress(position);
		
		if(contains(position)) {
			final JFileChooser chooser = new JFileChooser();
		    chooser.setCurrentDirectory(new File("."));
		    chooser.setDialogTitle("CurrentBot Maker export directory selection");
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    
		    // Disable the "All files" option.
		    chooser.setAcceptAllFileFilterUsed(false);
		       
		    EventQueue.invokeLater( new Runnable() {
		    	@Override
		    	public void run() {
		    		if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				    	path = chooser.getSelectedFile().getAbsolutePath();
				    	
				    	// Remember path.
				    	textField.setInput(path);
				    	textField.confirmInput();
				    	
				    	textField.setInput(path);
				    }
		    	}
		    });

			return !path.isEmpty();
		}
	    
	    return false;
	}
}
