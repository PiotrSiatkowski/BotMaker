/*
 * Copyleft (C) 2015 Piotr Siatkowski find me on Facebook;
 * This file is part of BotMaker. BotMaker is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. BotMaker is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with BotMaker (look at the
 * Documents directory); if not, either write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA, or visit
 * (http://www.gnu.org/licenses/gpl.txt).
 */

package com.thetruthbeyond.gui.utility.drawing;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
/**
 * Created by Peter Siatkowski on 2015-07-13.
 * Shaders in string form created to restrict number of files and keep them hidden.
 */
public final class Shaders {

    public static ShaderProgram SHADER_NO_ALPHA = new ShaderProgram(Shaders.VERTEX_DEFAULT, Shaders.FRAGMENT_NO_ALPHA);

    private static final String VERTEX_DEFAULT =
                      "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";"
                    + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";"
                    + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;"

                    + "uniform mat4 u_projTrans;"

                    + "varying vec4 v_color;"
                    + "varying vec2 v_texCoords;"

                    + "void main()"
                    + "{"
                    + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";"
                    + "   v_color.a = v_color.a * (256.0/255.0);"
                    + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;"
                    + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";"
                    + "}";

    private static final String FRAGMENT_NO_ALPHA =
            "#ifdef GL_ES \n"
                    + "#define LOWP lowp \n"
                    +   "precision mediump float; \n"
                    + "#else \n"
                    +   "#define LOWP \n"
                    + "#endif \n"

                    + "varying LOWP vec4 v_color;"
                    + "varying vec2 v_texCoords;"

                    + "uniform sampler2D u_texture;"

                    + "void main()"
                    + "{"
                    + "     vec4 texColor = texture2D(u_texture, v_texCoords);"

                    + "     gl_FragColor = v_color * texColor;"
                    + "     if(gl_FragColor.a >= 0.5)"
                    + "         gl_FragColor.a = 1.0;"
                    + "}";
}
