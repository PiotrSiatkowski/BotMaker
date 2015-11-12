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
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.sf.image4j.codec.ico.ICOEncoder;

public class IconWriter {

	private final int[] ICON_SIZES = {16, 24, 32, 48, 64, 72, 96, 128, 180};
	
	public void createIcon(File in, File out) throws IOException {

		BufferedImage image = ImageIO.read(in);
		List<BufferedImage> images = new ArrayList<>(9);
			
		// Starting from 16x16.
		for(int size : ICON_SIZES) {
			BufferedImage converted = Thumbnails.of(image).forceSize(size, size).asBufferedImage();
			images.add(converted);
		}
			
		FileOutputStream output = new FileOutputStream(out);
		ICOEncoder.write(images, output);
		output.close();
    }

	public void createIcon64png(File in, File out) throws IOException {
		BufferedImage image = ImageIO.read(in);

		BufferedImage converted = Thumbnails.of(image).forceSize(64, 64).asBufferedImage();
		ImageIO.write(converted, "png", out);
	}
}