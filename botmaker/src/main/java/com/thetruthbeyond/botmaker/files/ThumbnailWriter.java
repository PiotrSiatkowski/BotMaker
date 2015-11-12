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

package com.thetruthbeyond.botmaker.files;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import net.coobird.thumbnailator.Thumbnails;
import net.sf.image4j.codec.bmp.BMPEncoder;

public class ThumbnailWriter {

	private static final int THUMBNAIL_W = 256;
	private static final int THUMBNAIL_H = 256;
	
	@SuppressWarnings("MethodMayBeStatic")
	public void createThumbnail(File in, File out) {
		try {
			BufferedImage image = ImageIO.read(in);
			BufferedImage converted = Thumbnails.of(image).forceSize(THUMBNAIL_W, THUMBNAIL_H).asBufferedImage();
			
			FileOutputStream output = new FileOutputStream(out);
			BMPEncoder.write(converted, output);
			output.close();
		} catch(FileNotFoundException exception) {
			new Logger().writeMessage("Error: file for icon cannot be found.", exception.getMessage());
		} catch(IOException exception) {
			new Logger().writeMessage("Error: undefined IOException while generating thumbnail", exception.getMessage());
		}
	}
}
