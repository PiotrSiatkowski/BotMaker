package com.thetruthbeyond.gui.action.emitters;

import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;

/**
 * Created by Siata on 2015-04-29.
 * Emitter responsible for completion event.
 */
public class OnFieldCompletion extends Emitter {

    // Id functionality.
    public static int Id; @Override public int getId() { return Id; }

    public OnFieldCompletion(EventEmitter owner) {
        super(owner);
    }

    public void signalCompletion(boolean reachedEnd) {
        signalEvent();
    }
}
