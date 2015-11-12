package com.thetruthbeyond.desktop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Siata on 2015-09-12.
 * Class designed to be the starting point of the BotChat (exported bot) project.
 */
public class BotChatLauncher {
    public static void main(String[] args) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("java -cp BotChat.jar com.thetruthbeyond.desktop.BotChatStarter \"gain_access\"");

            process.waitFor();

            String appData = new File(System.getenv("AppData")).getAbsolutePath();

            String name = args[1];
            JarFile botChat = new JarFile(name + ".jar");

            JarEntry entry = botChat.getJarEntry(name + "/configuration.xml");
            InputStream stream = botChat.getInputStream(entry);

            Properties configuration = new Properties();
            configuration.loadFromXML(stream);

            String predicatesPath = configuration.getProperty("predicates");

            File predicatesDirectory = new File(appData + "/" + name + "/" + predicatesPath);
            String[] files = predicatesDirectory.list();

            StringBuilder filesToUpdate = new StringBuilder();
            for(String fileName : files)
                filesToUpdate.append(" ").append(name).append("/").append(predicatesPath).append(fileName);

            runtime.exec("jar uf BotMaker.jar -C " + appData + filesToUpdate);
        } catch(IOException exception) {
            exception.printStackTrace();
        } catch(InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
