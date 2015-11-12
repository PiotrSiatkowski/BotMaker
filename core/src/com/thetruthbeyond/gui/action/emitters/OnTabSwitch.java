package com.thetruthbeyond.gui.action.emitters;

import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.objects.tabs.Tab;

/**
 * Created by Siata on 2015-04-28.
 * Emitter responsible for teach tab swithc event.
 */
public class OnTabSwitch extends Emitter {

    // Id functionality.
    public static int Id; @Override public int getId() { return Id; }

    private Class<? extends Tab> type;

    public OnTabSwitch(EventEmitter owner) {
        super(owner);
    }

    public <T extends Tab> void signalSwitch(Class<T> type) {
        this.type = type;
        signalEvent();
    }

    public Class<? extends Tab> getTabType() {
        return type;
    }
}
