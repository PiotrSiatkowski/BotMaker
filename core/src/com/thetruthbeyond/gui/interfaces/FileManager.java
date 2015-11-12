package com.thetruthbeyond.gui.interfaces;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

/**
 * Created by Siata on 2015-09-15.
 * Class that can load various assets.
 */
public interface FileManager {

    void initialize();

    void savePortrait(String name, FileHandle portrait);
    void deletePortrait(String name);

    FileHandle getCoding(String name);
    FileHandle getFontHandle(String name);
    FileHandle getAssetHandle(String name);

    String getPath(String name);
    SmartTexture getTexture(String name);
    Sound getSound(String name);

    void dispose();
}
