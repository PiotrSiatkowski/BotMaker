package com.thetruthbeyond.gui.interfaces;

import com.thetruthbeyond.gui.objects.tabs.Tab;

import java.awt.*;

/**
 * Created by Siata on 2015-09-15.
 * Root object of GUI application client.
 */
public interface GUIRootObject {
    void minimizeFrame();

    void setFrameLocation(int x, int y);
    Point getFrameLocation();

    <T extends Tab> void changeTab(Class<T> tabType);
    void closeFrame();
}
