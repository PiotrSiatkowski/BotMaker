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

package com.thetruthbeyond.gui.utility.drawing.fonts;

import com.thetruthbeyond.gui.enums.FontType;

import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.badlogic.gdx.utils.IntMap;

/**
 * @author Peter Siatkowski
 */
public final class FontPool {

    private static final IntMap<SmartFont> fontsChat        = new IntMap<>(8);
    private static final IntMap<SmartFont> fontsChatItalic  = new IntMap<>(8);
    private static final IntMap<SmartFont> fontsGUI         = new IntMap<>(8);
    private static final IntMap<SmartFont> fontsGUIMenu     = new IntMap<>(8);
    private static final IntMap<SmartFont> fontsGUISupport  = new IntMap<>(8);

    private FontPool() {}

    public static SmartFont createFont(FontType type, int size) {
        if(type == FontType.CHAT_FONT) {
            if(fontsChat.containsKey(size))
                return fontsChat.get(size);
            else {
                SmartFont font = new SmartFont(type, size);
                fontsChat.put(size, font);
                return font;
            }
        } else

        if(type == FontType.CHAT_ITALIC_FONT) {
            if(fontsChatItalic.containsKey(size))
                return fontsChatItalic.get(size);
            else {
                SmartFont font = new SmartFont(type, size);
                fontsChatItalic.put(size, font);
                return font;
            }
        } else

        if(type == FontType.GUI_FONT) {
            if(fontsGUI.containsKey(size))
                return fontsGUI.get(size);
            else {
                SmartFont font = new SmartFont(type, size);
                fontsGUI.put(size, font);
                return font;
            }
        } else

        if(type == FontType.GUI_MENU_FONT) {
            if(fontsGUIMenu.containsKey(size))
                return fontsGUIMenu.get(size);
            else {
                SmartFont font = new SmartFont(type, size);
                fontsGUIMenu.put(size, font);
                return font;
            }
        } else

        if(type == FontType.GUI_SUPPORT_FONT) {
            if(fontsGUISupport.containsKey(size))
                return fontsGUISupport.get(size);
            else {
                SmartFont font = new SmartFont(type, size);
                fontsGUISupport.put(size, font);
                return font;
            }
        } else
            new Logger().writeMessage("Error", "Unknown font type has been detected in code.");

        // Default font.
        return new SmartFont(FontType.CHAT_FONT, 12);
    }

    public static void disposeFonts() {
        for(SmartFont font : fontsChat.values())
            font.dispose();
        for(SmartFont font : fontsChatItalic.values())
            font.dispose();
        for(SmartFont font : fontsGUI.values())
            font.dispose();
        for(SmartFont font : fontsGUIMenu.values())
            font.dispose();
        for(SmartFont font : fontsGUISupport.values())
            font.dispose();
    }
}
