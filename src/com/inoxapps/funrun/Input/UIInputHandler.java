package com.inoxapps.funrun.Input;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.utils.Screen;

public class UIInputHandler extends InputAdapter {

	private Screen screen;

	public UIInputHandler(Screen screen) {
		this.screen = screen;
	}

	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		boolean isTouched = screen.touchDown(screenX, screenY, pointer, button);
		if (isTouched)
			Assets.assets_button_Sound.play(Assets.soundVolume);
		return isTouched;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK) {
			screen.backKeyPressed();
			return true;
		}
		return false;
	}
}
