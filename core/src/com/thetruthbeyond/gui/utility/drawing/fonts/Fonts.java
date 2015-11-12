package com.thetruthbeyond.gui.utility.drawing.fonts;

import com.badlogic.gdx.files.FileHandle;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.interfaces.FileManager;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Siata on 2015-04-09.
 * Configure fonts used in this application.
 */

@SuppressWarnings("UtilityClass")
public final class Fonts {

    private static final Map<FontType, FileHandle> FONTS = new EnumMap<>(FontType.class);

    private Fonts() {}

    public static void configureFonts(FileManager loader) {
        FONTS.put(FontType.CHAT_FONT, loader.getFontHandle("Chat Font"));
        FONTS.put(FontType.GUI_FONT, loader.getFontHandle("GUI Font"));
        FONTS.put(FontType.GUI_SUPPORT_FONT, loader.getFontHandle("GUI Support Font"));
        FONTS.put(FontType.CHAT_ITALIC_FONT, loader.getFontHandle("Chat Italic Font"));
        FONTS.put(FontType.GUI_MENU_FONT, loader.getFontHandle("GUI Menu Font"));
    }

    public static FileHandle get(FontType type) {
        return FONTS.get(type);
    }
}
