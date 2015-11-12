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

package com.thetruthbeyond.gui.action;

import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.interfaces.GUIRootObject;
import  com.thetruthbeyond.chatterbean.utility.logging.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.List;
import java.util.jar.JarFile;

public final class StateRegistrationOffice {

	private static int currentId = 1;

	private StateRegistrationOffice() {}

	@SuppressWarnings("rawtypes")
	public static void registerAllIndicators() {

		try {
			 com.thetruthbeyond.chatterbean.utility.reflection.Package emitterPackage = new  com.thetruthbeyond.chatterbean.utility.reflection.Package("com.thetruthbeyond.gui.action.emitters");

			List<Class<Emitter>> classes;
			if(Consts.DEV_MODE)
				classes = emitterPackage.getClassesFromSource(Emitter.class, "core");
			else {
				String jarPath = GUIRootObject.class.getProtectionDomain().getCodeSource().getLocation().getPath();

				jarPath = URLDecoder.decode(jarPath, "UTF-8");
				classes = emitterPackage.getClassesFromJar(Emitter.class, new JarFile(jarPath));
			}

			for(Class<Emitter> type : classes) {
				try {
					Field field = type.getDeclaredField("Id");
					field.setAccessible(true);

					field.setInt(null, currentId++);
					field.setAccessible(false);
				} catch(NoSuchFieldException | IllegalAccessException | IllegalArgumentException | SecurityException exception) {
					new Logger().writeMessage("Error in registering action objects.", exception.getMessage());
				}
			}
		} catch(IOException exception) {
			new Logger().writeMessage("Error", "Application jar file couldn't be found during call of \"registerAllIndicators\" method");
			new Logger().writeError(exception);
		}
	}
}