package com.thetruthbeyond.gui.action;

import com.badlogic.gdx.utils.IntMap;

/**
 * Created by Siata on 2015-04-29.
 * Default observer class core functionality.
 */
public class Observer<T extends Emitter> {

    private EventObserver observerOwner;
    private IntMap<Emitter> observed;

    public Observer(EventObserver observerOwner) {
        this.observerOwner = observerOwner;
    }

    public final void setObserverOwner(EventObserver observerOwner) {
        this.observerOwner = observerOwner;
    }

    public final EventObserver getObserverOwner() {
        return observerOwner;
    }

    public  void observeEmitter(T emitter) {
        emitter.registerObserver(this);

        // Lazy object creation.
        if(observed == null)
            observed = new IntMap<>();
        observed.put(emitter.getId(), emitter);
    }

    public void stopObserving() {
        if(observed != null) {
            for(Emitter emitter : observed.values())
                emitter.unregisterObserver(this);
            observed.clear();
        }
    }
}
