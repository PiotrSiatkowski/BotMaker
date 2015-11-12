package com.thetruthbeyond.gui.interfaces;

import  com.thetruthbeyond.chatterbean.aiml.AIMLElement;
import com.thetruthbeyond.gui.structures.wildcard.Wildcard;

import java.util.List;

/**
 * Created by Siata on 2015-10-01.
 * Interface aimed for all class that are able to generate aiml element; the cause of generation isn't important.
 */
public interface AIMLGenerator {
    boolean hasAimlElement(AIMLGeneratorArgument argument);
    List<AIMLElement> getAimlElements(AIMLGeneratorArgument argument, Wildcard wildcard);
}
