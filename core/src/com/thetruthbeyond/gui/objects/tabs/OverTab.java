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

package com.thetruthbeyond.gui.objects.tabs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.thetruthbeyond.gui.action.emitters.OnAppear;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.interfaces.AppearingElement;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.shareable.DarknessDrawer;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

/**
 * Created by Peter Siatkowski on 2015-10-17.
 * Type in tab hierarchy representing over tab or the tab that can be draw above the others.
 */
public abstract class OverTab extends Tab implements AppearingElement {

    private final float SHOW_TIME;

    private float showTime = 0.0f;
    private float tabAlpha = 0.0f;

    private enum State {Invisible, Visible, Appearing, Disappearing }
    private State state = State.Invisible;

    private TextureRegion region;
    private DarknessDrawer darkness;

    /** OnChangeState emitter indicates visible state {increasing or decreasing} rather then actual visibility. */
    private OnAppear onAppear;

    protected OverTab(Area area, DarknessDrawer darkness, Clickable parent) {
        super(area, parent);

        this.darkness = darkness;
        SHOW_TIME = darkness.getShowTime();

        onAppear = new OnAppear(this);
        addEmitter(onAppear);
    }

    public void show() {
        onAppear.setAppearing(true);
        state = State.Appearing;

        // Let darkness background to listen to this object.
        darkness.giveControlTo(this);
    }

    public void hide() {
        onAppear.setAppearing(false);
        state = State.Disappearing;
    }

    public final boolean isActive() {
        return state == State.Visible || state == State.Appearing;
    }

    public final boolean isVisible() {
        return isActive() || state == State.Disappearing;
    }

    @Override
    public final float getVisibilityAlpha() {
        return tabAlpha;
    }

    @Override
    public boolean contains(Position position) {
        return isActive() && area.contains(position);
    }

    @Override
    public void update(float delta) {
        if(isActive()) {
            darkness.redo(delta, this);

            showTime = Math.min(showTime + delta, SHOW_TIME);
            if(showTime == SHOW_TIME)
                state = State.Visible;

            tabAlpha = showTime / SHOW_TIME;
        } else {
            if(isVisible()) {
                darkness.undo(delta, this);

                showTime = Math.max(showTime - delta, 0.0f);
                if(showTime == 0)
                    state = State.Invisible;

                tabAlpha = showTime / SHOW_TIME;
            }
        }
    }

    @Override
    public void draw(SmartSpriteBatch batch) {
        if(isVisible())
            darkness.draw(batch, this);
    }

    protected final void drawBuffer(SmartSpriteBatch batch, FrameBuffer buffer, float alpha) {
        Texture bufferTexture = buffer.getColorBufferTexture();

        if(region == null) {
            region = new TextureRegion(bufferTexture, area.getX(), Consts.SCREEN_H - (area.getY() + area.getH()),
                                                      area.getW(), area.getH());
            region.flip(false, true);
        } else {
            region.setTexture(bufferTexture);
            region.setRegion(area.getX(), Consts.SCREEN_H - (area.getY() + area.getH()),
                             area.getW(), area.getH());
            region.flip(false, true);
        }

        batch.setColor(1.0f, 1.0f, 1.0f, alpha);
        batch.draw(region, area.getX(), area.getY(), area.getW(), area.getH());
    }

    protected abstract void drawToBuffer(SmartSpriteBatch batch);
}
