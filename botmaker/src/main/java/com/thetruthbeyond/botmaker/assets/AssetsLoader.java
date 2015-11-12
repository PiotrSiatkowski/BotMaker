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

package com.thetruthbeyond.botmaker.assets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.thetruthbeyond.botmaker.files.ThumbnailWriter;
import com.thetruthbeyond.gui.GUIException;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

import  com.thetruthbeyond.chatterbean.utility.annotations.Accepted;

import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

@Accepted
public class AssetsLoader implements FileManager {

	private final AssetManager manager = new AssetManager();
	private final TextureParameter parameter = new TextureParameter();

	private TextureAtlas atlas;

	public static final String ASSETS_FOLDER = "Assets";
	private static final String[] IMAGE_EXTENSIONS = { "jpg", "png", "tif", "bmp" };
	private static final String[] SOUND_EXTENSIONS = { "ogg", "wav" };

	private final Map<String, String> pathmap = new HashMap<>(50);

	private final Class<Texture> textureClass = Texture.class;
	private final Class<Sound> soundClass = Sound.class;

	private boolean useAtlas = true;

	@Override
	public void initialize() {
		parameter.minFilter = TextureFilter.Linear;
		parameter.magFilter = TextureFilter.Linear;
		
		// Loading all assets into memory.
		FileHandle assets = Gdx.files.internal(ASSETS_FOLDER);

		if(Consts.DEV_MODE)
			new Logger().writeMessage("Info", "Assets folder path is set to: " + assets.file().getAbsolutePath());

		try {
			Properties properties = new Properties();
			properties.load(Gdx.files.internal(ASSETS_FOLDER + "/settings.properties").read());

			useAtlas = properties.getProperty("UseAtlas").equals("true");
		} catch(IOException ignored) {
			useAtlas = false;
		}

		if(useAtlas) {
			atlas = new TextureAtlas(Gdx.files.internal(ASSETS_FOLDER + "/Images/BotMaker.atlas"));
			loadDirectory(assets.child("Sounds"));
		} else if(assets.isDirectory())
			loadDirectory(assets);
			
		manager.finishLoading();
	}
	
	private void loadDirectory(FileHandle handle) {
		// Loading assets recursively.
		for(FileHandle file : handle.list())
		{
			if(file.isDirectory())
				loadDirectory(file);
			else {
				// Loading images.
				if(!useAtlas) {
					for(String extension : IMAGE_EXTENSIONS) {
						if(file.extension().equals(extension)) {
							manager.load(file.path(), textureClass, parameter);

							if(pathmap.containsKey(file.nameWithoutExtension()))
								throw new GUIException("Name of asset: " + file.nameWithoutExtension() + " is not original");

							pathmap.put(file.nameWithoutExtension(), file.path());
							break;
						}
					}
				}
				
				// Loading sound.
				for(String extension : SOUND_EXTENSIONS) {
					if(file.extension().equals(extension)) {
						manager.load(file.path(), soundClass);
						
						if(pathmap.containsKey(file.nameWithoutExtension()))
							throw new GUIException("Name of asset: " + file.nameWithoutExtension() + " is not original");

						pathmap.put(file.nameWithoutExtension(), file.path());
						break;
					}
				}
			}
		}
	}

	@Override
	public void savePortrait(String name, FileHandle portrait) {
		FileHandle out = Gdx.files.local("Bots/" + name + "/Portrait" + "." + portrait.extension());

		ThumbnailWriter writer = new ThumbnailWriter();
		writer.createThumbnail(portrait.file(), out.file());
	}

	@Override
	public void deletePortrait(String name) {
		FileHandle botDirectory = Gdx.files.internal("Bots/" + name);

		if(botDirectory.isDirectory()) {
			for(FileHandle file : botDirectory.list()) {
				if(file.nameWithoutExtension().equals("Portrait")) {

					for(String extension : IMAGE_EXTENSIONS) {
						if(file.name().endsWith("." + extension)) {
							Gdx.files.local(file.path()).delete();
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public SmartTexture getTexture(String name) {
		if(useAtlas) {
			TextureRegion region = atlas.findRegion(name);
			return new SmartTexture(region);
		}

		if(pathmap.containsKey(name))
			return new SmartTexture(manager.get(pathmap.get(name), textureClass));

		new Logger().writeMessage("Warning", "Texture named: " + name + " can't be loaded.");
		return null;
	}

	@Override
	public Sound getSound(String name) {
		if(pathmap.containsKey(name))
			return manager.get(pathmap.get(name), soundClass);

		new Logger().writeMessage("Warning", "Sound named: " + name + " can't be loaded.");
		return null;
	}

	@Override
	public FileHandle getCoding(String name) {
		return Gdx.files.internal("Configuration/Coding/" + name + ".txt");
	}

	@Override
	public FileHandle getFontHandle(String name) {
		return Gdx.files.internal("Configuration/Fonts/" + name + ".ttf");
	}

	@Override
	public FileHandle getAssetHandle(String name) {
		if(pathmap.containsKey(name))
			return Gdx.files.internal(pathmap.get(name));
		else {
			for(String extension : IMAGE_EXTENSIONS) {
				FileHandle handle = Gdx.files.internal(ASSETS_FOLDER + "/Images/" + name + "." + extension);
				if(handle.exists())
					return handle;
			}

			return null;
		}
	}

	@Override
	public String getPath(String name) {
		if(pathmap.containsKey(name))
			return pathmap.get(name);
		else {
			FileHandle handle = Gdx.files.internal(ASSETS_FOLDER);

			FileHandle[] files = handle.list();
			for(FileHandle file : files) {
				if(file.isDirectory())
					return getPath(name, file);
				else {
					if(file.nameWithoutExtension().equals(name) && file.exists())
						return file.path();
				}
			}

			return null;
		}
	}

	private String getPath(String name, FileHandle handle) {
		FileHandle[] files = handle.list();
		for(FileHandle file : files) {
			if(file.isDirectory())
				return getPath(name, file);
			else {
				if(file.nameWithoutExtension().equals(name) && file.exists())
					return file.path();
			}
		}

		return null;
	}

	@Override
	public void dispose() {
		manager.dispose();
		atlas.dispose();
	}
}