package com.thetruthbeyond.botchat;

import com.thetruthbeyond.bot.CurrentBot;
import  com.thetruthbeyond.chatterbean.parser.api.BotFactory;
import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.badlogic.gdx.ApplicationListener;

import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.thetruthbeyond.botchat.assets.AssetsLoader;
import com.thetruthbeyond.botchat.gui.tabs.PasswordTab;
import com.thetruthbeyond.botchat.gui.tabs.ChatTab;
import com.thetruthbeyond.debug.CheckpointCounter;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.objects.shareable.DarknessDrawer;
import com.thetruthbeyond.gui.utility.drawing.fonts.Fonts;
import com.thetruthbeyond.gui.input.Cursor;
import com.thetruthbeyond.gui.input.Keyboard;
import com.thetruthbeyond.gui.interfaces.GUIRootObject;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.tabs.AboutTab;
import com.thetruthbeyond.gui.objects.tabs.Tab;
import com.thetruthbeyond.gui.objects.widgets.MenuBar;
import com.thetruthbeyond.gui.objects.widgets.MessageShower;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.fonts.FontPool;
import com.thetruthbeyond.gui.utility.drawing.fonts.SmartFont;
import com.thetruthbeyond.gui.utility.gl.Buffers;
import com.thetruthbeyond.gui.utility.security.HashGenerator;

import java.awt.*;

public class BotChat extends Clickable implements ApplicationListener, GUIRootObject {

	private static final float MAX_DELTA_TIME = 0.03f;
	private static final float SUBSCRIPTION_PARAMETER = 0.90f;

	private static final int SUBSCRIPTION_X = 230;
	private static final int SUBSCRIPTION_Y = 14;

	// Swing frame.
	private JFrame projectFrame;

	// Disposable.
	private SmartSpriteBatch batch;
	private AssetsLoader loader;

	// Events handling.
	private Cursor cursor;
	private Keyboard keyboard;
	
	private MenuBar menu;

	private Tab passTab;
	private Tab chatTab;
	private Tab infoTab;

	private boolean passwordDetected = false;
	private boolean isPasswordNeeded = false;
	private boolean isAboutTabShowed = false;
	private boolean isInitialized = false;
	
	private final float PASSWORD_DISAPPEAR_TIME = 1.0f;
	private float time = 0.0f;
	
	private MessageShower messages;
	private SmartFont subscription;

	private String name;

	private BotExplorer explorer;
	private Logger logger = new Logger();

	public BotChat(Clickable parent) {
		super(parent);
	}

	public void setFrame(JFrame frame) {
		projectFrame = frame;
	}

	@Override
	public void create() {
		if(Consts.DEV_MODE)
			logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Executed create method of BotChat");

		loader = new AssetsLoader();
		
		try {
			loader.initialize();
			if(Consts.DEV_MODE)
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "All files loaded properly");

			Coding.configureCharsets(loader);
			if(Consts.DEV_MODE)
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Charsets configured");

			Fonts.configureFonts(loader);
			if(Consts.DEV_MODE)
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Fonts configured.");

			cursor = Cursor.getInstance();
			keyboard = Keyboard.getInstance();

			keyboard.setFilter(Coding.signs);

			name = Gdx.files.classpath(AssetsLoader.CONFIG_DIRECTORY + "/botname.txt").readString("UTF-8");
			if(Consts.DEV_MODE)
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Name loaded and its value is: " + name);

			explorer = new BotExplorer(name); CurrentBot.instance = new BotFactory().createAliceBot(name, explorer);
			if(Consts.DEV_MODE)
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Alicebot properly parsed");

			HashGenerator generator = new HashGenerator();
			String emptyHash = generator.generateHash("");
			if(!CurrentBot.instance.getPassword().equals(emptyHash))
				passwordDetected = isPasswordNeeded = true;

			// Need for subscription purpose.
			name = name.toUpperCase();

			InputMultiplexer multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(cursor);
			multiplexer.addProcessor(keyboard);
			Gdx.input.setInputProcessor(multiplexer);

			if(Consts.DEV_MODE)
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "ChatBot has been created and now it is ready to use");
		} catch(Exception exception) {
			logger.writeMessage("Error", exception.getMessage());
			logger.writeError(exception);
		}
	}

	@Override @UnhandledMethod
	public void resize(int width, int height) {}

	private void initialize() {
		batch = new SmartSpriteBatch();
		
		Area menuArea = new Area(0, 0, Consts.SCREEN_W, Consts.MENU_H);
		menu = new MenuBar(menuArea, loader, this);
			
		Area workingArea = new Area(0, Consts.MENU_H, Consts.SCREEN_W, Consts.SCREEN_H - Consts.MENU_H + 1);

		DarknessDrawer darkness = new DarknessDrawer(0.3f, loader, workingArea);
		messages = new MessageShower(FontType.CHAT_FONT, loader, darkness, this);

		chatTab = new ChatTab(workingArea, loader, this);
		passTab = new PasswordTab(workingArea, loader, this);
		infoTab = new AboutTab(workingArea, loader, this);

		subscription = FontPool.createFont(FontType.GUI_MENU_FONT, Math.round(SUBSCRIPTION_PARAMETER * Consts.MENU_H));
		subscription.setPosition(SUBSCRIPTION_X, SUBSCRIPTION_Y);
		subscription.setColor(1.0f, 0.098f, 0.565f, 1.0f);

		projectFrame.setVisible(true);
	}
	
	@Override
	public void render() {	
		try {
			if(!isInitialized) {
				if(Consts.initialize()) {
					initialize();
					isInitialized = true;

					if(Consts.DEV_MODE)
						logger.writeMessage(CheckpointCounter.nextCheckpoint(), "ChatBot class has been initialized");
				} else
					return;
			}
				
			if(cursor.isIdle())
				executeMouseHover(cursor.getPosition());
				
			if(cursor.isDragged())
				executeMouseDrag(cursor.getPosition());
				
			if(cursor.isPressed())
				executeMousePress(cursor.getClickedPosition());
				
			if(cursor.isReleased())
				executeMouseRelease(cursor.getClickedPosition());
				
			// Updating
			update(Math.min(Gdx.graphics.getDeltaTime(), MAX_DELTA_TIME));

			// Drawing
			draw(batch);

		} catch(Exception exception) {
			logger.writeMessage("Error", exception.getMessage());
			logger.writeError(exception);
			Gdx.app.exit();
		}
	}

	@Override @UnhandledMethod
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		menu.executeMouseHover(position);
		if(isAboutTabShowed)
			infoTab.executeMouseHover(position);
		else

		if(isPasswordNeeded)
			passTab.executeMouseHover(position);
		else
			chatTab.executeMouseHover(position);

		return true;
	}

	@Override
	public boolean executeMouseDrag(Position position) {
		menu.executeMouseDrag(position);
		if(isAboutTabShowed)
			infoTab.executeMouseDrag(position);
		else

		if(isPasswordNeeded)
			passTab.executeMouseDrag(position);
		else
			chatTab.executeMouseDrag(position);

		return true;
	}

	@Override
	public boolean executeMousePress(Position position) {
		if(!menu.executeMousePress(position)) {
			if(isAboutTabShowed)
				infoTab.executeMousePress(position);
			else if(isPasswordNeeded)
				passTab.executeMousePress(position);
			else
				chatTab.executeMousePress(position);
		}
		return true;
	}

	@Override
	public boolean executeMouseRelease(Position position) {
		menu.executeMouseRelease(position);
		if(isAboutTabShowed)
			infoTab.executeMouseRelease(position);
		else

		if(isPasswordNeeded)
			passTab.executeMouseRelease(position);
		else
			chatTab.executeMouseRelease(position);

		return true;
	}

	@Override
	public void update(float delta) {
		menu.update(delta);
		messages.update(delta);

		if(isAboutTabShowed) {
			infoTab.update(delta);
		} else {
			if(isPasswordNeeded) {
				if(keyboard.isKeyDown(Keys.ENTER)) {
					String input = ((PasswordTab) passTab).getPassword();

					try {
						HashGenerator generator = new HashGenerator();
						String hash = generator.generateHash(input);

						if(hash.equals(CurrentBot.instance.getPassword())) {
							isPasswordNeeded = false;
						} else {
							messages.showMessage("You have entered invalid password");
						}
					} catch(Exception exception) {
						logger.writeMessage("Error", "The problem with reading password has occured.");
					}
				}

				passTab.update(delta);

			} else {
				if(time < PASSWORD_DISAPPEAR_TIME)
					time = Math.min(time + delta, PASSWORD_DISAPPEAR_TIME);
				chatTab.update(delta);
			}
		}
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		if(isAboutTabShowed) {
			infoTab.draw(batch);
		} else {
			if(isPasswordNeeded)
				passTab.draw(batch);
			else {
				chatTab.draw(batch);

				// Password tab fading.
				if(passwordDetected && time < PASSWORD_DISAPPEAR_TIME) {
					float alpha = (PASSWORD_DISAPPEAR_TIME - time) / PASSWORD_DISAPPEAR_TIME;
					batch.setColor(1.0f, 1.0f, 1.0f, alpha);
					passTab.draw(batch);
					batch.setColor(Color.WHITE);
				}
			}
		}

		// Drawing menu bar.
		menu.draw(batch);

		// Drawing bot's name on the menu bar.
		subscription.draw(batch, name);

		// Drawing message shower content.
		messages.draw(batch);
		batch.end();
	}

	public <T extends Tab> void changeTab(Class<T> type) {
		if(type == AboutTab.class) {
			isAboutTabShowed = true;

			infoTab.onTabEnter();
			chatTab.onTabLeave();
		} else {
			isAboutTabShowed = false;

			chatTab.onTabEnter();
			infoTab.onTabLeave();
		}
	}

	@Override
	public void setFrameLocation(int x, int y) {
		projectFrame.setLocation(x, y);
	}

	@Override
	public Point getFrameLocation() {
		return projectFrame.getLocation();
	}

	@Override
	public void closeFrame() {
		if(Consts.DEV_MODE)
			new Logger().writeMessage(CheckpointCounter.nextCheckpoint(), "Saving predicates.");

		if(CurrentBot.instance != null) {
			explorer.updatePredicatesFile(CurrentBot.instance.getContext());
			explorer.close();
		}

		Gdx.app.exit();
	}

	@Override
	public void minimizeFrame() {
		projectFrame.setState(Frame.ICONIFIED);
	}

	@Override
	public void pause() {
		cursor.setActive(false);
		Gdx.graphics.setContinuousRendering(false);
	}

	@Override
	public void resume() {
		cursor.setActive(true);
		Gdx.graphics.setContinuousRendering(true);
	}

	@Override
	public void dispose() {
		batch.dispose();
		loader.dispose();

		Buffers.disposeBuffers();
		FontPool.disposeFonts();
	}
}