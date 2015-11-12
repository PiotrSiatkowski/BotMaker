package com.thetruthbeyond.botmaker.gui.objects.widgets;

import com.thetruthbeyond.botmaker.gui.tabs.MyBotTab;
import com.thetruthbeyond.botmaker.gui.tabs.BuildTab;
import com.thetruthbeyond.botmaker.gui.tabs.TeachTab;
import com.thetruthbeyond.botmaker.gui.tabs.TestsTab;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.interfaces.GUIRootObject;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.GUIException;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.botmaker.gui.objects.buttons.menu.BuildButton;
import com.thetruthbeyond.botmaker.gui.objects.buttons.menu.MyBotButton;
import com.thetruthbeyond.botmaker.gui.objects.buttons.menu.TeachButton;
import com.thetruthbeyond.botmaker.gui.objects.buttons.menu.TestsButton;
import com.thetruthbeyond.gui.objects.tabs.Tab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Created by Siata on 2015-04-10.
 * Class that manages tab buttons.
 */
public class TabMenu extends Clickable {

    private final GUIRootObject application;
    private final AbstractMap<Class<? extends Tab>, Button> buttons = new HashMap<>();

    public TabMenu(Area area, FileManager loader, Clickable parent) {
        super(parent);

        buttons.put(MyBotTab.class, new MyBotButton(loader, this));
        buttons.put(TestsTab.class, new TestsButton(loader, this));
        buttons.put(TeachTab.class, new TeachButton(loader, this));
        buttons.put(BuildTab.class, new BuildButton(loader, this) );

        Class[] sequence = { MyBotTab.class, TestsTab.class, TeachTab.class, BuildTab.class };

        // Calculating buttons positions.
        int cumulatedWidth = 0;
        for(Class type : sequence)
            cumulatedWidth += buttons.get(type).getW();
        int gap = Math.round((area.getW() - cumulatedWidth) / (float)(buttons.size() + 1));

        int x = gap + buttons.get(MyBotTab.class).getW() / 2;
        for(Class type : sequence) {
            buttons.get(type).setPosition(x, area.getY() + (int) Math.floor(area.getH() / 2.0f));
            x += buttons.get(type).getW() + gap;
        }

        // Search main application class.
        Clickable clickable = parent;
        while(true) {
            if(parent instanceof GUIRootObject) {
                application = (GUIRootObject) clickable;
                break;
            } else

            if(clickable != null)
                clickable = clickable.getParent();
            else
                throw new GUIException("Tab menu is not connected with main application class in any hierarchy.");
        }
    }

    @Override
    public boolean contains(Position position) {
       return super.contains(position);
    }

    @Override
    public boolean executeMouseHover(Position position) {
        boolean hovered = false;
        for(Button button : buttons.values())
            if(button.executeMouseHover(position))
                hovered = true;

        return hovered;
    }

    @Override
    public boolean executeMouseDrag(Position position) {
        boolean dragged = false;
        for(Button button : buttons.values())
            if(button.executeMouseDrag(position))
                dragged = true;

        return dragged;
    }

    @Override
    public boolean executeMousePress(Position position) {
        for(Entry<Class<? extends Tab>, Button> entry : buttons.entrySet()) {
            if(entry.getValue().executeMousePress(position)) {
                application.changeTab(entry.getKey());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean executeMouseRelease(Position position) {
        for(Button button : buttons.values())
            button.executeMouseRelease(position);

        // Always send the event further.
        return false;
    }

    @Override
    public void update(float delta) {
        for(Button button : buttons.values())
            button.update(delta);
    }

    @Override
    public void draw(SmartSpriteBatch batch) {
        for(Button button : buttons.values())
            button.draw(batch);
    }
}
