package com.thetruthbeyond.gui.action.emitters;

import com.thetruthbeyond.gui.action.Emitter;
import com.thetruthbeyond.gui.action.EventEmitter;
import com.thetruthbeyond.gui.enums.ValidationFailureCause;

/**
 * Created by Siata on 2015-04-27.
 * Emitter responsible for reporting validation error.
 */
public class OnValidateFailure extends Emitter {

    // Id functionality.
    public static int Id; @Override public int getId() { return Id; }

    private ValidationFailureCause cause;

    public OnValidateFailure(EventEmitter owner) {
        super(owner);
    }

    public void signalValidationFailure(ValidationFailureCause cause) {
        this.cause = cause;
        signalEvent();
    }

    public ValidationFailureCause getCause() {
        return cause;
    }
}
