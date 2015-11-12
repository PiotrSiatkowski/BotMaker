package com.thetruthbeyond.gui.objects.tabs;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;

import com.badlogic.gdx.Gdx;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.buttons.Button;
import com.thetruthbeyond.gui.objects.buttons.simple.MoreButton;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

/**
 * Created by Siata on 2015-04-09.
 * AboutTab tab designed to show info about the project and its author.
 */
public class AboutTab extends Tab {

    // More button. ////////////////////////////////////////////////////////////////////
    private final Button moreButton;

    private static final float BUTTON_X_PARAMETER = 0.82f;
    private static final float BUTTON_Y_PARAMETER = 0.91f;
    ////////////////////////////////////////////////////////////////////////////////////

    private final SmartTexture background;

    public AboutTab(Area area, FileManager loader, Clickable parent) {
        super(area, parent);

        background = loader.getTexture("AboutBackground");

        moreButton = new MoreButton(loader, this);
        moreButton.setPosition(area.getX() + Math.round(area.getW() * BUTTON_X_PARAMETER),
                               area.getY() + Math.round(area.getH() * BUTTON_Y_PARAMETER));
    }

    @Override @UnhandledMethod
    public void onTabEnter() {}

    @Override @UnhandledMethod
    public boolean contains(Position position) {
        return false;
    }

    @Override
    public boolean executeMouseHover(Position position) {
        return moreButton.executeMouseHover(position);
    }

    @Override
    public boolean executeMouseDrag(Position position) {
        return moreButton.executeMouseDrag(position);
    }

    @Override
    public boolean executeMousePress(Position position) {
        if(moreButton.executeMousePress(position))
            Gdx.net.openURI("http://botmaker.thetruthbeyond.com");

        return false;
    }

    @Override @UnhandledMethod
    public boolean executeMouseRelease(Position position) {
        return true;
    }

    @Override
    public void update(float delta) {
        moreButton.update(delta);
    }

    @Override
    public void draw(SmartSpriteBatch batch) {
        batch.draw(background, area.getX(), area.getY(), area.getW(), area.getH());
        moreButton.draw(batch);
    }

    @Override @UnhandledMethod
    public void onTabLeave() {}

    @Override @UnhandledMethod
    public void clear() {}

    @Override @UnhandledMethod
    public void dispose() {}
}
