package com.thetruthbeyond.gui.configuration;

import com.thetruthbeyond.gui.interfaces.FileManager;

/**
 * Created by Siata on 2015-04-09.
 * Charsets used in this application.
 */
public final class Coding {

    public static String signs = "";
    public static String rendered = "";
    public static String pathLetters = "";

    private Coding() {}

    public static void configureCharsets(FileManager loader) {
        signs = loader.getCoding("Characters").readString();

        if(signs.indexOf('\\') == -1)
            signs = signs + "\\";

        if(signs.indexOf('/') == -1)
            signs = signs + "/";

        if(signs.indexOf('(') == -1)
            signs = signs + "(";

        if(signs.indexOf(')') == -1)
            signs = signs + ")";

        if(signs.indexOf('"') == -1)
            signs = signs + "\"";

        if(signs.indexOf('?') == -1)
            signs = signs + "?";

        if(signs.indexOf('!') == -1)
            signs = signs + "!";

        if(signs.indexOf('.') == -1)
            signs = signs + ".";

        if(signs.indexOf(',') == -1)
            signs = signs + ",";

        if(signs.indexOf(':') == -1)
            signs = signs + ":";

        if(signs.indexOf('\'') == -1)
            signs = signs + "'";

        if(signs.indexOf('_') == -1)
            signs = signs + "_";

        if(signs.indexOf(' ') == -1)
            signs = signs + " ";

        if(signs.indexOf(';') == -1)
            signs = signs + ";";

        if(signs.indexOf('=') == -1)
            signs = signs + "=";

        if(signs.indexOf('-') == -1)
            signs = signs + "-";

        if(signs.indexOf('\'') == -1)
            signs = signs + "'";

        rendered = signs;

        if(rendered.indexOf('[') == -1)
            rendered = rendered + "[";

        if(rendered.indexOf(']') == -1)
            rendered = rendered + "]";

        if(rendered.indexOf('{') == -1)
            rendered = rendered + "{";

        if(rendered.indexOf('}') == -1)
            rendered = rendered + "}";

        pathLetters = signs.replace("*", "");
        pathLetters = pathLetters.replace("\"", "");
        pathLetters = pathLetters.replace("/", "");
        pathLetters = pathLetters.replace("\\", "");
        pathLetters = pathLetters.replace("[", "");
        pathLetters = pathLetters.replace("]", "");
        pathLetters = pathLetters.replace(":", "");
        pathLetters = pathLetters.replace(";", "");
        pathLetters = pathLetters.replace("|", "");
        pathLetters = pathLetters.replace("=", "");
        pathLetters = pathLetters.replace("?", "");
        pathLetters = pathLetters.replace("(", "");
        pathLetters = pathLetters.replace(")", "");
        pathLetters = pathLetters.replace("<", "");
        pathLetters = pathLetters.replace(">", "");
        pathLetters = pathLetters.replace("{", "");
        pathLetters = pathLetters.replace("}", "");
    }
}
