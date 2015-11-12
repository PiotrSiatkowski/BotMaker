package com.thetruthbeyond.botchat.gui.tabs;

import  com.thetruthbeyond.chatterbean.utility.annotations.UnhandledMethod;
import com.thetruthbeyond.gui.configuration.Coding;
import com.thetruthbeyond.gui.configuration.Consts;
import com.thetruthbeyond.gui.enums.FontType;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.objects.Clickable;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.TextFieldConfiguration;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.DefaultMessageTextField;
import com.thetruthbeyond.gui.objects.controllers.textfield.decorators.PasswordTextField;
import com.thetruthbeyond.gui.objects.tabs.Tab;
import com.thetruthbeyond.gui.structures.Area;
import com.thetruthbeyond.gui.structures.Position;
import com.thetruthbeyond.gui.utility.drawing.SmartSpriteBatch;
import com.thetruthbeyond.gui.utility.drawing.SmartTexture;

public class PasswordTab extends Tab {
	
	// Input field. //////////////////////////////////////////////////////////////////////////////
	private TextField input;
	
	private static final float INPUT_FIELD_X_PARAMETER = 0.335f;
	private static final float INPUT_FIELD_Y_PARAMETER = 0.62f;
	private static final float INPUT_FIELD_W_PARAMETER = 0.33f;
	private static final float INPUT_FIELD_H_PARAMETER = 0.10f;
	private static final float INPUT_FIELD_P_PARAMETER = 0.12f;
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	// Input field. //////////////////////////////////////////////////////////////////////////////
	private SmartTexture lock;
		
	private final int LOCK_X;
	private final int LOCK_Y;
	//////////////////////////////////////////////////////////////////////////////////////////////

	private SmartTexture background;
	
	public PasswordTab(Area area, FileManager loader, Clickable parent) {
		super(area, parent);
		
		background = loader.getTexture("DarkWood");
		
		TextFieldConfiguration configuration = configureTextField();
		input = new TextField(configuration, loader, this);
		input = new PasswordTextField(input);
		input = new DefaultMessageTextField(input).setDefaultMessage("Enter password here");
		
		lock = loader.getTexture("Lock");
		
		LOCK_X = (int)(1.0 * Math.round((Consts.SCREEN_W - lock.getW()) / 2.0f));
		LOCK_Y = (int)(0.8 * Math.round((Consts.SCREEN_H - lock.getH()) / 2.0f));
	}
	
	private TextFieldConfiguration configureTextField() {
		TextFieldConfiguration configuration = new TextFieldConfiguration();
		configuration.area.setX(area.getX() + area.getW() * INPUT_FIELD_X_PARAMETER);
		configuration.area.setY(area.getY() + area.getH() * INPUT_FIELD_Y_PARAMETER);
		configuration.area.setW(area.getW() * INPUT_FIELD_W_PARAMETER);
		configuration.area.setH(area.getH() * INPUT_FIELD_H_PARAMETER);
		
		configuration.fontname 		= FontType.CHAT_ITALIC_FONT;
		configuration.fontcolor 	= Consts.MAIN_FONT_COLOR;
		configuration.characters	= Coding.signs + "*";
		
		configuration.relativeTextPadding	= INPUT_FIELD_P_PARAMETER;
		configuration.border				= Consts.BORDER_SIZE;
		
		return configuration;
	}
	
	public String getPassword() {
		return input.getInput();	
	}
	
	@Override @UnhandledMethod
	public void onTabEnter() {}
	
	@Override @UnhandledMethod
	public boolean contains(Position position) {
		return false;
	}

	@Override
	public boolean executeMouseHover(Position position) {
		input.executeMouseHover(position);
		
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseDrag(Position position) {
		return false;
	}

	@Override
	public boolean executeMousePress(Position position) {
		input.executeMousePress(position);
		
		return false;
	}

	@Override @UnhandledMethod
	public boolean executeMouseRelease(Position position) {
		return false;
	}

	@Override
	public void update(float delta) {
		input.update(delta);
	}

	@Override
	public void draw(SmartSpriteBatch batch) {
		batch.draw(background, area.getX(), area.getY(), area.getW(), area.getH());
		
		input.draw(batch);	
		batch.draw(lock, LOCK_X, LOCK_Y, lock.getW(), lock.getH());
	}
	
	@Override @UnhandledMethod
	public void onTabLeave() {}

	@Override @UnhandledMethod
	public void clear() {}

	@Override @UnhandledMethod
	public void dispose() {}
}