package com.inoxapps.funrun.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.Settings;
import com.inoxapps.funrun.utils.Screen;
import com.inoxapps.funrun.utils.ScreenManager;

public class LoadingScreen extends Screen {

	private final AssetManager manager;
	private int progress;
	private final TextureAtlas loadingAtlas;
	private final Sprite splashScreeen;
	private final AtlasSprite loadingBox;
	private final Sprite filling;

	public LoadingScreen() {
		setViewPort(Settings.VIEWPORT_WIDTH, Settings.VIEWPORT_HEIGHT);
		loadingAtlas = new TextureAtlas("data/images/funrun-splashScreen-pack");
		splashScreeen = loadingAtlas.createSprite(("bg"));
		Assets.setSplashScreen(splashScreeen);
		loadingBox = (AtlasSprite) loadingAtlas.createSprite(("loadingBox"));
		filling = loadingAtlas.createSprite(("filling"));
		manager = new AssetManager();
		startLoading();
	}

	private void startLoading() {
		manager.load("data/funrunFont.fnt", BitmapFont.class);
		manager.load("data/funrunFont1.fnt", BitmapFont.class);
		manager.load("data/sounds/button.ogg", Sound.class);
		manager.load("data/sounds/beartrap.ogg", Sound.class);
		manager.load("data/sounds/freeze-out.ogg", Sound.class);
		manager.load("data/sounds/freeze-teleport-shoot.ogg", Sound.class);
		manager.load("data/images/funrun-ui-pack", TextureAtlas.class);
		manager.load("data/images/funrun-commonlevel-pack", TextureAtlas.class);
		manager.load("data/sounds/freeze.ogg", Sound.class);
		manager.load("data/sounds/mine-blast.ogg", Sound.class);
		manager.load("data/sounds/mine-step.ogg", Sound.class);
		manager.load("data/sounds/power-collect.ogg", Sound.class);
		manager.load("data/sounds/result-screen.ogg", Sound.class);
		manager.load("data/sounds/teleport.ogg", Sound.class);
		manager.load("data/sounds/thunder.ogg", Sound.class);
		manager.load("data/sounds/slicer.ogg", Sound.class);
		manager.load("data/sounds/purchaseYES.ogg", Sound.class);
		manager.load("data/sounds/purchaseNO.ogg", Sound.class);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Screen.guiCam.update();
		Screen.batcher.setProjectionMatrix(Screen.guiCam.combined);
		Screen.batcher.begin();
		splashScreeen.draw(Screen.batcher, 0.5f);
		for (int i = 0; i < progress; i++) {
			filling.setPosition(loadingBox.getAtlasRegion().offsetX + i * filling.getRegionWidth(),
					loadingBox.getAtlasRegion().offsetY);
			filling.draw(Screen.batcher);
		}
		loadingBox.draw(Screen.batcher);

		Screen.batcher.end();
	}

	@Override
	public void update(float deltaTime) {
		if (manager.update()) {
			Assets.loadUI(manager);
			Assets.loadCommonLevel(manager);
			Assets.initAssets(manager);
			ScreenManager.getInstance().show(ScreenEnum.MAIN_MENU);
		}
		progress = (int) (manager.getProgress() * 10);
	}

	@Override
	public void backKeyPressed() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

}