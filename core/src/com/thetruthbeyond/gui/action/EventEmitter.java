package com.thetruthbeyond.gui.action;

import com.thetruthbeyond.gui.interfaces.GUIComponent;

/**
 * Created by Siata on 2015-04-28.
 * Interface aimed for realising emitting objects' logic.
 */
public interface EventEmitter extends GUIComponent {
    void addEmitter(Emitter emitter);
    <T extends Emitter> T getEmitter(int emitterID);
}
