package com.thetruthbeyond.gui.action.features;

/**
 * Created by Siata on 2015-04-28.
 * Objects with this feature can gain and lose focus.
 */
public interface Focusable {
    void setFocus(boolean focus);
    boolean isFocused();
}
