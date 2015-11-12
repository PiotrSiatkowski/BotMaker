package com.thetruthbeyond.gui.action.emitters;

import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;

/**
 * Created by Siata on 2015-04-28.
 * Emitter responsible for hover events.
 */
public class OnHover extends Emitter
{
    // Id functionality.
    public static int Id; @Override public int getId() { return Id; }

    private boolean isHovered = false;

    public OnHover(EventEmitter owner) {
        super(owner);
    }

    public void setHovered(boolean pressed) {
        isHovered = pressed;
        if(isHovered)
            signalEvent();
    }

    public boolean isHovered() {
        return isHovered;
    }
}
