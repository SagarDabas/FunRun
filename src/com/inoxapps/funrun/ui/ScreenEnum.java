package com.inoxapps.funrun.ui;

import java.util.ArrayList;

import com.inoxapps.funrun.gameObjects.PlayerType;
import com.inoxapps.funrun.utils.Screen;

public enum ScreenEnum {

	
	LOADING {
		@Override
		public Screen getScreenInstance() {
			return new LoadingScreen();
		}
	},

	MAIN_MENU {
		@Override
		public Screen getScreenInstance() {
			return new MainMenuScreen();
		}
	},

	GAME {
		@Override
		public Screen getScreenInstance() {
			return new GameScreen();
		}
	},

	LEVEL {
		@Override
		public Screen getScreenInstance() {
			return new LevelScreen();
		}
	},

	GAME_SELECT {
		@Override
		public Screen getScreenInstance() {
			return new GameSelectionScreen();
		}
	},

	STORE {
		@Override
		public Screen getScreenInstance() {
			return new StoreScreen();
		}
	},

	// TODO show four players at different times and also show the levels, start the game after certain time.
	SEARCHING {
		@Override
		public Screen getScreenInstance() {
			return new SearchingPlayersScreen();
		}
	};

	public abstract Screen getScreenInstance();
	
	//TODO remove these things
	public int levelSelected = 0;
	public ArrayList<PlayerType> types;
	public boolean isMultiPlayer;
	public ArrayList<String> names;

}
