package com.inoxapps.funrun.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.utils.CoinManager;
import com.inoxapps.funrun.utils.GenericSpriteAccessor;
import com.inoxapps.funrun.utils.OverlapTester;
import com.inoxapps.funrun.utils.Screen;
import com.inoxapps.funrun.utils.ScreenManager;

public class LevelScreen extends Screen {

	private Vector3 touchPoint = new Vector3();
	private AssetManager manager;
	private boolean isLoaded;
	private int currentScreen = 1;
	private final TweenManager tweenManager;

	public LevelScreen() {
		Tween.registerAccessor(Sprite.class,
				new GenericSpriteAccessor<Sprite>());
		this.manager = new AssetManager();
		this.tweenManager = new TweenManager();
		translate(Assets.ui_levelIcons.get(0), Direction.TO_LEFT, 0);
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Screen.batcher.begin();
		Assets.ui_selectLevelBG.draw(Screen.batcher);
		if (currentScreen > 1)
			Assets.ui_levelChangeArrowLeft.draw(Screen.batcher);
		if (currentScreen < Assets.ui_levelIcons.size)
			Assets.ui_levelChangeArrowRight.draw(Screen.batcher);
//		Assets.ui_selectLevelHomeButton.draw(Screen.batcher);
		Assets.ui_levelIcons.get(currentScreen - 1).draw(Screen.batcher);
		CoinManager.drawCoinHud();
		Screen.batcher.end();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (!translateFlag) {
			guiCam.unproject(touchPoint.set(screenX, screenY, 0));
			if (OverlapTester.pointInRectangle(Assets.ui_levelChangeArrowLeft.getBoundingRectangle(), touchPoint.x,
					touchPoint.y)) {
				if (currentScreen > 1) {
					currentScreen--;
					translate(Assets.ui_levelIcons.get(currentScreen - 1), Direction.TO_LEFT, 0);
				}
				return true;
			}
			if (OverlapTester.pointInRectangle(Assets.ui_levelChangeArrowRight.getBoundingRectangle(), touchPoint.x,
					touchPoint.y)) {
				if (currentScreen < Assets.ui_levelIcons.size) {
					currentScreen++;
					translate(Assets.ui_levelIcons.get(currentScreen - 1), Direction.TO_RIGHT, 0);
				}
				return true;
			}
			if (OverlapTester.pointInRectangle(Assets.ui_levelIcons.get(currentScreen - 1).getBoundingRectangle(),
					touchPoint.x, touchPoint.y)) {
				if (!isLoaded) {
					startLoading();
					isLoaded = true;
				}
				return true;
			}
//			if (OverlapTester.pointInRectangle(Assets.ui_selectLevelHomeButton.getBoundingRectangle(), touchPoint.x,
//					touchPoint.y)) {
//				ScreenManager.getInstance().show(ScreenEnum.MAIN_MENU);
//			}
		}
		return false;
	}

	private enum Direction {
		TO_LEFT, TO_RIGHT
	};

	private boolean translateFlag;

	private void translate(Sprite sprite, Direction direction, float y) {
		translateFlag = true;
		if (direction == Direction.TO_LEFT)
			sprite.setPosition(1.5f * Screen.guiCam.viewportWidth, y);
		else if (direction == Direction.TO_RIGHT)
			sprite.setPosition(-Screen.guiCam.viewportWidth, y);

		Tween.to(sprite, GenericSpriteAccessor.POS_XY, 1).target(0, y).setCallback(new TweenCallback() {

			@Override
			public void onEvent(int type, BaseTween<?> source) {
				translateFlag = false;
			}
		}).ease(Elastic.OUT).start(tweenManager);
	}

	private void startLoading() {
		manager.load("data/images/funrun-level-" + (currentScreen - 1) + "-pack", TextureAtlas.class);
	}

	// TODO show loading at the bottom
	@Override
	public void update(float deltaTime) {
		tweenManager.update(deltaTime);
		if (isLoaded && manager.update()) {
			Assets.loadLevel(manager, "funrun-level-" + (currentScreen - 1) + "-pack");
			ScreenEnum.GAME.levelSelected = (currentScreen - 1);
			ScreenManager.getInstance().show(ScreenEnum.GAME);
		}
	}

	@Override
	public void backKeyPressed() {
		if (!isLoaded)
			ScreenManager.getInstance().show(ScreenEnum.GAME_SELECT);
	}

	@Override
	public void dispose() {
		isLoaded = false;
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}
}
