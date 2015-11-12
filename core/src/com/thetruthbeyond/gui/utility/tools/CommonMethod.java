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

package com.thetruthbeyond.gui.utility.tools;

import com.thetruthbeyond.gui.input.Keyboard;
import com.thetruthbeyond.gui.objects.controllers.scrollpanel.ScrollPanel;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;

/**
 * Created by Peter Siatkowski on 2015-10-20.
 * Utility class for common functions that should not be implemented within any class body.
 */
public final class CommonMethod {

    private static Keyboard keyboard = Keyboard.getInstance();

    private static final float CONSIDERABLY_LARGE_AMOUNT_TIME_FOR_UPDATE = 100.f;

    public static void eraseEmptyRowFrom(ScrollPanel panel, int SCROLL_PANEL_COLUMNS) {
        // Erasing empty rows.
        if(keyboard.isKeyDown(com.badlogic.gdx.Input.Keys.BACKSPACE)) {
            int ROWS = panel.getObjectsSize() / SCROLL_PANEL_COLUMNS;

            for(int row = ROWS - 2; row >= 0; row--) {

                TextField field;

                boolean isEmpty = true; int focusedColumn = -1;
                for(int column = 0; column != SCROLL_PANEL_COLUMNS; column++) {
                    field = (TextField) panel.getObject(row, column);

                    if(field.isFocused())
                        focusedColumn = column;

                    // Check emptiness.
                    if(!field.getInput().isEmpty()) {
                        isEmpty = false;
                           break;
                    }
                }

                if(isEmpty) {
                    if(row == ROWS - 2) {
                        // Activate current last row if the deleted one was last too. Now we have one row less than before but ROWS - 2 indicates a good value!
                        isEmpty = true;
                        for(int column = 0; column != SCROLL_PANEL_COLUMNS; column++) {
                            field = (TextField) panel.getObject(ROWS - 1, column);
                            if(!field.getInput().isEmpty()) {
                                isEmpty = false;
                                break;
                            }
                        }

                        if(isEmpty) {
                            field = (TextField) panel.getObject(ROWS - 1, focusedColumn);
                            field.setFocus(true);

                            field.update(CONSIDERABLY_LARGE_AMOUNT_TIME_FOR_UPDATE);
                            panel.update(CONSIDERABLY_LARGE_AMOUNT_TIME_FOR_UPDATE);
                        }
                    }

                    panel.removeRow(row);
                }
            }
        }
    }
}
