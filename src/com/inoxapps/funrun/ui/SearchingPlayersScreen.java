package com.inoxapps.funrun.ui;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.FunRun;
import com.inoxapps.funrun.Settings;
import com.inoxapps.funrun.gameObjects.PlayerType;
import com.inoxapps.funrun.utils.ActionResolver;
import com.inoxapps.funrun.utils.CoinManager;
import com.inoxapps.funrun.utils.Names;
import com.inoxapps.funrun.utils.OverlapTester;
import com.inoxapps.funrun.utils.PrefBean;
import com.inoxapps.funrun.utils.Screen;
import com.inoxapps.funrun.utils.ScreenManager;
import com.inoxapps.funrun.utils.StoreManager;

//TODO refactor this class
public class SearchingPlayersScreen extends Screen {

	private float startTimer;
	private Vector3 touchPoint = new Vector3();
	private ArrayList<Integer> timeToDisplay;
	private int playersRandomIndex[];
	private ArrayList<Integer> numbers;
	private ArrayList<PlayerType> types;
	private ArrayList<String> playerNames;
	private AssetManager manager;
	private boolean isLoading;
	private boolean isDisplayed;
	private int randomLevel;

	public SearchingPlayersScreen() {
		this.timeToDisplay = new ArrayList<Integer>();
		this.types = new ArrayList<PlayerType>();
		this.playersRandomIndex = new int[Settings.PLAYER_NUMBER];
		this.playerNames = new ArrayList<String>();
		this.manager = new AssetManager();
		this.numbers = new ArrayList<Integer>(Settings.PLAYER_NUMBER - 1);
		for (int i = 0; i < Names.values().length - 1; i++) {
			numbers.add(i);
		}
		this.isDisplayed = false;
		this.isLoading = false;
		this.startTimer = 3;
		if (isNameSet)
			this.initTime();
	}

	private void initTime() {
		for (int i = 0; i < Settings.PLAYER_NUMBER - 1; i++) {
			if (MathUtils.randomBoolean())
				timeToDisplay.add(MathUtils.random(0, 300));
			else
				timeToDisplay.add(0);
			playersRandomIndex[i] = MathUtils.random(0,
					PlayerType.values().length - 1);
			types.add(PlayerType.values()[playersRandomIndex[i]]);
			playerNames.add(Names.values()[getUniqueRandomNum()].name);
		}
		timeToDisplay.add(0);
		Collections.sort(timeToDisplay);
		types.add(timeToDisplay.lastIndexOf(0),
				StoreManager.getEquippedItem().type);
		playerNames.add(timeToDisplay.lastIndexOf(0), Assets.playerName);
	}

	private int getUniqueRandomNum() {
		int index = MathUtils.random(0, numbers.size() - 1);
		int number = numbers.get(index);
		numbers.remove(index);
		return number;
	}

	private void startLoading() {
		randomLevel = MathUtils.random(0, Assets.ui_levelIcons.size - 1);
		manager.load("data/images/funrun-level-" + (randomLevel) + "-pack",
				TextureAtlas.class);
		ScreenEnum.GAME.names = playerNames;
		ScreenEnum.GAME.levelSelected = randomLevel;
		ScreenEnum.GAME.types = types;
		ScreenEnum.GAME.isMultiPlayer = true;
	}

	@Override
	public void update(float deltaTime) {
		if (isNameSet) {
			if (!isDisplayed) {
				isDisplayed = true;
				for (int i = 0; i < Settings.PLAYER_NUMBER; i++) {
					if (timeToDisplay.get(i) > 0) {
						timeToDisplay.set(i,
								(int) (timeToDisplay.get(i) - deltaTime));
						isDisplayed = isDisplayed && false;
					} else {
						isDisplayed = isDisplayed && true;
					}
				}
			} else {
				if (!isLoading) {
					startLoading();
					isLoading = true;
				} else {
					if (manager.update() && startTimer < 0f) {
						Assets.loadLevel(manager, "funrun-level-"
								+ (randomLevel) + "-pack");
						ScreenManager.getInstance().show(ScreenEnum.GAME);
					}
					startTimer -= deltaTime;
				}
			}
		}
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Screen.batcher.begin();
		Assets.ui_commonBG.draw(Screen.batcher);
		Assets.ui_multiPlayerHomeButton.draw(Screen.batcher);
		CoinManager.drawCoinHud();
		if (isNameSet) {
			for (int i = 0; i < Settings.PLAYER_NUMBER; i++) {
				if (timeToDisplay.get(i) > 0) {
					drawSprite(Assets.ui_searchingText, i);
				} else {
					drawSprite(types.get(i).ui_multiplayer_playerIcon, i);
				}
			}
			if (isLoading)
				Assets.assets_Blackfont.draw(Screen.batcher, "Starting in..."
						+ (int) Math.ceil(startTimer), 320, 475);
		}
		Screen.batcher.end();
	}

	private void drawSprite(Sprite sprite, int i) {
		if (i == 1)
			sprite.setPosition(500, 280);
		else if (i == 2)
			sprite.setPosition(150, 80);
		else if (i == 3)
			sprite.setPosition(500, 80);
		else if (i == 0)
			sprite.setPosition(150, 280);
		sprite.draw(Screen.batcher);
		if (sprite != Assets.ui_searchingText)
			Assets.assets_Blackfont.draw(Screen.batcher, playerNames.get(i),
					sprite.getX() + 50, sprite.getY());
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		guiCam.unproject(touchPoint.set(screenX, screenY, 0));
		if (OverlapTester.pointInRectangle(
				Assets.ui_multiPlayerHomeButton.getBoundingRectangle(),
				touchPoint.x, touchPoint.y)) {
			ScreenManager.getInstance().show(ScreenEnum.MAIN_MENU);
			return true;
		}
		return false;
	}

	@Override
	public void dispose() {
		ScreenManager.getInstance().dispose(ScreenEnum.SEARCHING);
	}

	@Override
	public void backKeyPressed() {
		ScreenManager.getInstance().show(ScreenEnum.MAIN_MENU);
	}

	private boolean isNameSet;

	@Override
	public void show() {

		if (FunRun.actionResolver != null) {
			if (FunRun.actionResolver.isNetworkAvailable()) {
				if (PrefBean.getStringPreference(PrefBean.Pref.NAME, "")
						.equals("You")) {
					Gdx.input.getTextInput(new TextInputListener() {

						@Override
						public void input(String text) {
							PrefBean.setStringPreference(PrefBean.Pref.NAME,
									text);
							Assets.playerName = text;
							isNameSet = true;
							SearchingPlayersScreen.this.initTime();
						}

						@Override
						public void canceled() {
							ScreenManager.getInstance().show(
									ScreenEnum.MAIN_MENU);
							isNameSet = false;
						}
					}, "Name", "You");
				} else {
					isNameSet = true;
					SearchingPlayersScreen.this.initTime();
				}
			} else {
				FunRun.actionResolver.showPopup("Internet",
						"Network not available!", "OK", "Cancel", true,
						new ActionResolver.PopupCallback() {

							@Override
							public void onClicked(boolean isYesClicked) {
								ScreenManager.getInstance().show(
										ScreenEnum.MAIN_MENU);
							}
						});
			}
		} else {
			isNameSet = true;
			SearchingPlayersScreen.this.initTime();
		}
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

}
