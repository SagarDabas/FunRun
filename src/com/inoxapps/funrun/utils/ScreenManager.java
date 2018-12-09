package com.inoxapps.funrun.utils;

import com.badlogic.gdx.utils.IntMap;
import com.inoxapps.funrun.ui.ScreenEnum;

public final class ScreenManager {

	private static ScreenManager instance;

	private Game game;

	private IntMap<Screen> screens;

	private ScreenManager() {
		screens = new IntMap<Screen>();
	}

	public static ScreenManager getInstance() {
		if (null == instance) {
			instance = new ScreenManager();
		}
		return instance;
	}

	public void initialize(Game game) {
		this.game = game;
	}

	public void show(ScreenEnum screen) {
		if (null == game)
			return;
		if (!screens.containsKey(screen.ordinal())) {
			screens.put(screen.ordinal(), screen.getScreenInstance());
		}
		game.setScreen(screens.get(screen.ordinal()));
	}

	public void dispose(ScreenEnum screen) {
		if (!screens.containsKey(screen.ordinal()))
			return;
		screens.remove(screen.ordinal()).dispose();
	}

	public void dispose() {
		for (Screen screen : screens.values()) {
			screen.dispose();
		}
		screens.clear();
		instance = null;
	}
}