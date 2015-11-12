package com.thetruthbeyond.gui;

/**
 * Created by Siata on 2015-04-10.
 * Exception reporting all objects failures.
 */
public class GUIException extends RuntimeException {
    private static final long serialVersionUID = 3585310298450232804L;

    public GUIException(String message) {
        super(message);
    }
}
