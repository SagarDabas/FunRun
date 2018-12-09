package com.inoxapps.funrun.utils;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.inoxapps.funrun.Input.UIInputHandler;

public abstract class Game implements ApplicationListener {

	private Screen screen;
	private UIInputHandler inputHandler;

	public void setScreen(Screen screen) {
		if (this.screen != null) {
			this.screen.pause();
			this.screen.dispose();
		}
		this.screen = screen;
		inputHandler.setScreen(screen);
		this.screen.show();
	}

	public Screen getScreen() {
		return screen;
	}

	@Override
	public void create() {
		inputHandler = new UIInputHandler(screen);
		Gdx.input.setInputProcessor(inputHandler);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void resume() {
		screen.resume();
	}

	@Override
	public void render() {
		screen.update(Gdx.graphics.getDeltaTime());
		screen.render(Gdx.graphics.getDeltaTime());
		// try {
		// Thread.sleep(17l);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {
		screen.pause();
	}

	@Override
	public void dispose() {
		screen.dispose();
	}
}
