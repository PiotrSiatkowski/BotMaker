package com.thetruthbeyond.botchat.assets;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import java.io.BufferedReader;
import java.io.IOException;

import com.thetruthbeyond.debug.CheckpointCounter;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

// !!!To properly load file from .jar file "//" sign must be used. NOT "\" OR File.Separator!!!
public class AssetsLoader implements FileManager {

	private AssetManager manager = new AssetManager();
	
	private TextureParameter parameter = new TextureParameter();
	
	public static final String ASSETS_DIRECTORY = "Assets";
	public static final String CONFIG_DIRECTORY = "Configuration";
	public static final String[] IMAGE_EXTENSIONS = { "jpg", "png", "tif", "bmp" };
	public static final String[] SOUND_EXTENSIONS = { "wav", "ogg" };

	public void initialize() {
		parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.magFilter = Texture.TextureFilter.Linear;

		BufferedReader reader = null;
		try {
			FileHandle imageList = Gdx.files.classpath(CONFIG_DIRECTORY + "//images.txt");
			reader = new BufferedReader(imageList.reader());
			
			String fileName;
			while((fileName = reader.readLine()) != null) {
				if(Consts.DEV_MODE)
					new Logger().writeMessage(CheckpointCounter.nextCheckpoint(), "Loading: " + fileName);
				for(String extension : IMAGE_EXTENSIONS) {
					if(fileName.endsWith("." + extension)) {
						manager.load(ASSETS_DIRECTORY + "//" + fileName, Texture.class, parameter);
						break;
					}
				}
			}
			
			for(String extension : IMAGE_EXTENSIONS) {
				if(Gdx.files.classpath(ASSETS_DIRECTORY + "//" + "Portrait." + extension).exists()) {
					manager.load(ASSETS_DIRECTORY + "//" + "Portrait." + extension, Texture.class, parameter);
					break;
				}
			}

			FileHandle soundsList = Gdx.files.classpath(CONFIG_DIRECTORY + "//sounds.txt");
			reader = new BufferedReader(soundsList.reader());

			while((fileName = reader.readLine()) != null) {
				if(Consts.DEV_MODE)
					new Logger().writeMessage(CheckpointCounter.nextCheckpoint(), "Loading: " + fileName);
				for(String extension : SOUND_EXTENSIONS) {
					if(fileName.endsWith("." + extension)) {
						manager.load(ASSETS_DIRECTORY + "//" + fileName, Sound.class);
						break;
					}
				}
			}
		} catch(Exception localException) {
			new Logger().writeError(localException);
		} finally {
			try {
				if(reader != null) {
					reader.close();
				}
			} catch(IOException exception) {
				new Logger().writeError(exception);
			}
		}
		
		manager.finishLoading();
	}

	@Override @UnhandledMethod
	public void savePortrait(String name, FileHandle portrait) {}

	@Override @UnhandledMethod
	public void deletePortrait(String name) {}

	@Override @UnhandledMethod
	public FileHandle getAssetHandle(String name) {
		return null;
	}

	public FileHandle getCoding(String name) {
		return Gdx.files.classpath(CONFIG_DIRECTORY + "/Coding/" + name + ".txt");
	}

	public FileHandle getFontHandle(String name) {
		return Gdx.files.classpath(CONFIG_DIRECTORY + "/Fonts/" + name + ".ttf");
	}

	@Override @UnhandledMethod
	public String getPath(String name) {
		return null;
	}

	public void dispose() {
		manager.dispose();
	}

	public SmartTexture getTexture(String name) {
		for(String extension : IMAGE_EXTENSIONS) {
			if(manager.isLoaded(ASSETS_DIRECTORY + "//" + name + "." + extension)) {
				return new SmartTexture(manager.get(ASSETS_DIRECTORY + "//" + name + "." + extension, Texture.class));
			}
		}
		
		return null;
	}

	public Sound getSound(String name) {
		for(String extension : SOUND_EXTENSIONS) {
			if(manager.isLoaded(ASSETS_DIRECTORY + "//" + name + "." + extension)) {
				return manager.get(ASSETS_DIRECTORY + "//" + name + "." + extension, Sound.class);
			}
		}
		if(manager.isLoaded(name)) {
			return manager.get(name, Sound.class);
		}

		return null;
	}
}