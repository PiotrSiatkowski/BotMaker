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

package com.thetruthbeyond.gui.objects.controllers.textfield.decorators;

import com.thetruthbeyond.gui.action.emitters.OnValidateFailure;
import com.thetruthbeyond.gui.enums.ValidationFailureCause;
import com.thetruthbeyond.gui.input.Keyboard;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldDecorator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Siata on 2015-10-13.
 * This decorator protects text field from writing several different sentences into it.
 */
public class OneSentenceTextField extends TextFieldDecorator {

    private Set<String> splitters = new HashSet<>();
    private Keyboard keyboard = Keyboard.getInstance();

    private OnValidateFailure onValidationFailure;

    public OneSentenceTextField(TextField parent) {
        super(parent);

        onValidationFailure = new OnValidateFailure(parent);
        addEmitter(onValidationFailure);
    }

    public void setSentenceSplitters(Set<String> splitters) {
        this.splitters = splitters;
    }

    @Override
    public void update(float delta) {
        if(isFocused() && keyboard.isCharacterWaiting()) {
            String input = getInput();
            for(String splitter : splitters) {
                if(input.endsWith(splitter)) {
                    // Dismiss waiting character.
                    keyboard.getCharacter();

                    onValidationFailure.signalValidationFailure(ValidationFailureCause.MoreThanOneSentence);
                    break;
                }
            }
        }

        super.update(delta);
    }
}
