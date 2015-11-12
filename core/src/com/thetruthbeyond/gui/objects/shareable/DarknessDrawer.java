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

package com.thetruthbeyond.gui.objects.shareable;

import com.badlogic.gdx.graphics.Color;
import com.thetruthbeyond.gui.interfaces.AppearingElement;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.interfaces.Shareable;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

/**
 * Created by Peter Siatkowski on 2015-10-27.
 * Draws dark background behind objects or widgets.
 */
public class DarknessDrawer implements Shareable, AppearingElement {

    private final Area area;

    private static final float BLACKNESS_ALPHA = 0.8f;
    private final float SHOW_TIME;

    private final SmartTexture darkness;

    private float showTime = 0.0f;
    private float tabAlpha = 0.0f;

    private Object client;

    public DarknessDrawer(float SHOW_TIME, FileManager loader, Area area) {
        this.area = area;
        darkness = loader.getTexture("BlackBackground");

        this.SHOW_TIME = SHOW_TIME;
    }

    public float getShowTime() {
        return SHOW_TIME;
    }

    @Override
    public float getVisibilityAlpha() {
        return tabAlpha;
    }

    @Override
    public void giveControlTo(Object client) {
        this.client = client;
    }

    @Override
    public void redo(float delta, Object client) {
        if(this.client == null || this.client == client) {
            showTime = Math.min(showTime + delta, SHOW_TIME);
            tabAlpha = showTime / SHOW_TIME;
        }
    }

    @Override
    public void undo(float delta, Object client) {
        if(this.client == null || this.client == client) {
            showTime = Math.max(showTime - delta, 0.0f);
            tabAlpha = showTime / SHOW_TIME;
        }
    }

    public void draw(SmartSpriteBatch batch, Object client) {
        if(this.client == null || this.client == client) {
            batch.setColor(1.0f, 1.0f, 1.0f, tabAlpha * BLACKNESS_ALPHA);
            batch.draw(darkness, area.getX(), area.getY(), area.getW(), area.getH());
            batch.setColor(Color.WHITE);
        }
    }
}
