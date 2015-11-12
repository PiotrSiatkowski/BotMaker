package com.thetruthbeyond.desktop;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.thetruthbeyond.botchat.BotChat;
import com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.thetruthbeyond.debug.CheckpointCounter;
import com.thetruthbeyond.gui.configuration.Consts;

public final class BotChatStarter {

    private static final int SIZE_W = 1133;
    private static final int SIZE_H = 440;

    private BotChatStarter() {}

    public static void main(String[] args) {

        if(args.length != 0 && args[0].equals("dev_mode"))
            Consts.DEV_MODE = true;
        else

        if(args.length == 0 || args.length > 1) {
            JOptionPane.showMessageDialog(null, "Please, use starter application in order to open chat window.");
            return;
        } else

        if(!args[0].equals("out_mode")) {
            JOptionPane.showMessageDialog(null, "Please, use starter application in order to open chat window.");
            return;
        }

        if(Consts.DEV_MODE)
            new Logger().writeMessage(CheckpointCounter.nextCheckpoint(), "Starter class executed.");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final BotChat program = new BotChat(null);
                JFrame frame = new LwjglFrame(program, "BotMaker", SIZE_W, SIZE_H) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void initialize() {
                        setUndecorated(true);

                        Toolkit toolkit = Toolkit.getDefaultToolkit();

                        setResizable(false);
                        setLocation((toolkit.getScreenSize().width - SIZE_W) / 2, (toolkit.getScreenSize().height - SIZE_H) / 2);

                        byte[] data = Gdx.files.classpath("Assets/Icon64.png").readBytes();
                        Image image = toolkit.createImage(data);
                        setIconImage(image);
                    }
                };

                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowIconified(WindowEvent e) {
                        program.pause();
                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {
                        program.resume();
                    }
                });

                program.setFrame(frame);
            }
        });
    }
}