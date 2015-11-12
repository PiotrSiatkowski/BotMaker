package com.thetruthbeyond.gui.utility.gl;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.thetruthbeyond.gui.configuration.Consts;

/**
 * Created by Siata on 2015-09-29.
 * FBO static pool.
 */
public class Buffers {

    private static FrameBuffer buffer1 = new FrameBuffer(Pixmap.Format.RGBA8888, Consts.SCREEN_W, Consts.SCREEN_H, false);
    private static FrameBuffer buffer2 = new FrameBuffer(Pixmap.Format.RGBA8888, Consts.SCREEN_W, Consts.SCREEN_H, false);

    public static FrameBuffer getBuffer(BUFFER_NUMBER number) {
        if(number == BUFFER_NUMBER.ONE)
            return buffer1;
        else
            return buffer2;
    }

    public static void disposeBuffers() {
        buffer1.dispose();
        buffer2.dispose();
    }
}
