/*
 * Copyleft (C) 2015 Piotr Siatkowski find me on Facebook;
 * Copyleft (C) 2005 Helio Perroni Filho xperroni@yahoo.com ICQ: 2490863;
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

package com.thetruthbeyond.desktop;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.*;

import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.thetruthbeyond.botmaker.BotMaker;
import com.thetruthbeyond.gui.configuration.Consts;

public final class BotMakerStarter {

	private static final int SIZE_W = 833;
	private static final int SIZE_H = 512;

	private BotMakerStarter() {}

	public static void main(String[] args) {

		if(args.length != 0 && args[0].equals("dev_mode"))
			Consts.DEV_MODE = true;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
		    public void run() {
				final BotMaker program = new BotMaker(null);
				JFrame frame = new LwjglFrame(program, "BotMaker", SIZE_W, SIZE_H) {

					private static final long serialVersionUID = -5178554941783752536L;

					@Override
					protected void initialize() {
						setUndecorated(true);

						Toolkit toolkit = Toolkit.getDefaultToolkit();

						setResizable(false);
						setLocation((toolkit.getScreenSize().width - SIZE_W) / 2, (toolkit.getScreenSize().height - SIZE_H) / 2);

						Image image = toolkit.getImage("Assets/Icon64.png");
						setIconImage(image);
					}
				};

				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowIconified(WindowEvent e) {
						program.pause();
					}

					@Override
					public void windowDeiconified(WindowEvent e) {
						program.resume();
					}
				});

				program.setFrame(frame);
		    }
		});
	}
}