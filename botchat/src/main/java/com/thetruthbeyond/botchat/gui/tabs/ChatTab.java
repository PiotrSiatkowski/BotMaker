/*
 * BotMaker - file created and updated by Piotr Siatkowski (2015).
 */

package com.thetruthbeyond.botchat.gui.tabs;

import com.badlogic.gdx.audio.Sound;
import com.thetruthbeyond.chatterbean.AliceBot;
import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import com.badlogic.gdx.Input.Keys;

import com.thetruthbeyond.bot.CurrentBot;
import com.thetruthbeyond.botchat.assets.AssetsLoader;
import com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.enums.ScrollAreaInputType;
import com.thetruthbeyond.gui.input.Keyboard;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.controllers.imagewindow.ImageWindow;
import com.thetruthbeyond.gui.objects.controllers.imagewindow.ImageWindowConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.ScrollArea;
import com.thetruthbeyond.gui.objects.controllers.scrollarea.ScrollAreaConfiguration;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBar;
import com.thetruthbeyond.gui.objects.controllers.scrollbar.ScrollBarConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.MemoryTextField;
import com.thetruthbeyond.gui.objects.tabs.Tab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ChatTab extends Tab {
	
	// Scroll area. //////////////////////////////////////////////////////////////////
	private ScrollArea scrollArea;
	
	private static final float SCROLL_AREA_X_PARAMETER 	= 0.040f;
	private static final float SCROLL_AREA_Y_PARAMETER 	= 0.080f;
	private static final float SCROLL_AREA_W_PARAMETER 	= 0.664f;
	private static final float SCROLL_AREA_H_PARAMETER 	= 0.740f;
	private static final float SCROLL_AREA_P_PARAMETER 	= 0.010f;
	
	private static final float SCROLL_AREA_ALPHA 		= 0.70f;
	private static final float SCROLL_AREA_TEXT_ALPHA 	= 0.90f;
	
	private static final int MAX_MESSAGES_SHOWN			= 10;
	//////////////////////////////////////////////////////////////////////////////////
	
	// Input text field. /////////////////////////////////////////////////////////////
	private TextField textField;
	
	private static final float TEXT_FIELD_X_PARAMETER 		= 0.040f;
	private static final float TEXT_FIELD_Y_PARAMETER 		= 0.820f;
	private static final float TEXT_FIELD_W_PARAMETER 		= 0.664f;
	private static final float TEXT_FIELD_H_PARAMETER 		= 0.100f;
	//////////////////////////////////////////////////////////////////////////////////
	
	// Image window. /////////////////////////////////////////////////////////////////
	private ImageWindow imageWindow;
	
	private static final float IMAGE_WINDOW_X_PARAMETER 	= 0.700f;
	private static final float IMAGE_WINDOW_Y_PARAMETER 	= 0.080f;
	private static final float IMAGE_WINDOW_W_PARAMETER 	= 0.260f;
	private static final float IMAGE_WINDOW_H_PARAMETER 	= 0.840f;
	//////////////////////////////////////////////////////////////////////////////////
	
	// Chat area scroll bar. /////////////////////////////////////////////////////////
	private ScrollBar scrollBar;
	
	private static final float SCROLL_BAR_X_PARAMETER 		= 0.965f;
	private static final float SCROLL_BAR_Y_PARAMETER 		= 0.080f;
	private static final float SCROLL_BAR_W_PARAMETER 		= 0.016f;
	private static final float SCROLL_BAR_H_PARAMETER 		= 0.840f;
	//////////////////////////////////////////////////////////////////////////////////

	private final Queue<String> questions = new LinkedList<>();
	private final Queue<String> responses = new LinkedList<>();

	private final Queue<Float> times = new LinkedList<>();

	private static final float MINIMUM_RESPONSE_TIME_PER_LETTER = 0.025f;

	private float elapsedTime;

	private final Random random = new Random();

	private final Sound messageSound;
	private final SmartTexture background;

	private final Keyboard keyboard = Keyboard.getInstance();

	private final Logger logger = new Logger();
	
	public ChatTab(Area area, AssetsLoader loader, Clickable parent) {
		super(area, parent);
		
		background = loader.getTexture("WoodBackground");
		
		ScrollBarConfiguration barConfiguration = configureScrollBar();
		scrollBar = new ScrollBar(barConfiguration, loader, this);
		
		ScrollAreaConfiguration areaConfiguration = configureScrollArea();
		scrollArea = new ScrollArea(areaConfiguration, loader, this);
		
		TextFieldConfiguration fieldConfiguration = configureTextField();
		textField = new TextField(fieldConfiguration, loader, this);
		textField = new MemoryTextField(textField);
		textField.setFocus(true);
		
		ImageWindowConfiguration imageConfiguration = configureImageWindow();
		imageWindow = new ImageWindow(imageConfiguration, loader, this);

		messageSound = loader.getSound("MessageSound");
	}	
	
	private ScrollAreaConfiguration configureScrollArea() {
		ScrollAreaConfiguration configuration = new ScrollAreaConfiguration();
		configuration.area.setX(area.getX() + area.getW() * SCROLL_AREA_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * SCROLL_AREA_Y_PARAMETER);
		configuration.area.setW(area.getW() * SCROLL_AREA_W_PARAMETER);
		configuration.area.setH(area.getH() * SCROLL_AREA_H_PARAMETER);
		
		configuration.relativeAreaPadding	= SCROLL_AREA_P_PARAMETER;
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING;
		configuration.border				= Consts.BORDER_SIZE;
		
		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		
		configuration.maxMessagesShown 		= MAX_MESSAGES_SHOWN;
		
		configuration.backgroundAlpha 		= SCROLL_AREA_ALPHA;
		configuration.textBackgroundAlpha 	= SCROLL_AREA_TEXT_ALPHA;
		
		configuration.scrollbar = scrollBar;
		
		return configuration;
	}
	
	private TextFieldConfiguration configureTextField() {
		TextFieldConfiguration configuration = new TextFieldConfiguration();
		configuration.area.setX(area.getX() + area.getW() * TEXT_FIELD_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * TEXT_FIELD_Y_PARAMETER);
		configuration.area.setW(area.getW() * TEXT_FIELD_W_PARAMETER);
		configuration.area.setH(area.getH() * TEXT_FIELD_H_PARAMETER);
		
		configuration.fontname 		= FontType.CHAT_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		configuration.characters	= Coding.signs;
		
		configuration.relativeTextPadding	= Consts.RELATIVE_TEXT_PADDING - 0.03f;
		configuration.border				= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	private ScrollBarConfiguration configureScrollBar() {
		ScrollBarConfiguration configuration = new ScrollBarConfiguration();
		
		configuration.area.setX(area.getX() + area.getW() * SCROLL_BAR_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * SCROLL_BAR_Y_PARAMETER);
		configuration.area.setW(area.getW() * SCROLL_BAR_W_PARAMETER);
		configuration.area.setH(area.getH() * SCROLL_BAR_H_PARAMETER);	
		
		return configuration;
	}
	
	private ImageWindowConfiguration configureImageWindow() {
		ImageWindowConfiguration configuration = new ImageWindowConfiguration();
		configuration.area.setX(area.getX() + area.getW() * IMAGE_WINDOW_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * IMAGE_WINDOW_Y_PARAMETER);
		configuration.area.setW(area.getW() * IMAGE_WINDOW_W_PARAMETER);
		configuration.area.setH(area.getH() * IMAGE_WINDOW_H_PARAMETER);
		
		configuration.border			= Consts.BORDER_SIZE;
		configuration.backgroundAlpha	= SCROLL_AREA_ALPHA;

		configuration.reactive			= false;
		configuration.image				= "Portrait";
		
		return configuration;
	}
	
	@Override @UnhandledMethod
	public void onTabEnter() {}
	
	@Override
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		return scrollArea.executeMouseDrag(position);	
	}

	@Override
	public boolean executeMousePress(Position position) {
		return scrollArea.executeMousePress(position);
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		return scrollArea.executeMouseRelease(position);
	}
	
	public void update(float delta) {
		if(CurrentBot.instance == null)
			return;

		// Update main and only one text field.
		textField.update(delta);

		// Update scroll area (scroll bar) if needed.
		scrollArea.update(delta);

		if(keyboard.isKeyDown(Keys.ENTER)) {
			String input = textField.getInput();
			if(!input.isEmpty()) {
				scrollArea.addMessage(input, ScrollAreaInputType.USER_BLUE);
				questions.add(input);

				AliceBot bot = CurrentBot.instance;
				if(bot != null) {
					String response = bot.respond(input);
					responses.add(response);

					@SuppressWarnings("NonReproducibleMathCall")
					float time = (float)(MINIMUM_RESPONSE_TIME_PER_LETTER * (input.length() + response.length()) * Math.min(3, -Math.log(random.nextDouble()) + 1));
					times.add(time);

					keyboard.clearEvents();
					((MemoryTextField) textField).confirmInput();

					messageSound.play();
				}
			}
		}

		if(!responses.isEmpty()) {
			elapsedTime += delta;

			if(elapsedTime > times.peek()) {
				elapsedTime = 0.0f;

				String response = responses.poll();
				scrollArea.addMessage(response, ScrollAreaInputType.USER_RED);

				logger.appendMessage(questions.poll(), response);

				if(!times.isEmpty())
					times.poll();

				messageSound.play();
			}
		}
	}
	
	public void draw(SmartSpriteBatch batch) {
		batch.draw(background, area.getX(), area.getY(), area.getW(), area.getH());
		scrollArea.draw(batch);
		imageWindow.draw(batch);
		textField.draw(batch);
	}

	@Override @UnhandledMethod
	public void onTabLeave() {}

	@Override @UnhandledMethod
	public void clear() {}

	@Override @UnhandledMethod
	public void dispose() {}
}
