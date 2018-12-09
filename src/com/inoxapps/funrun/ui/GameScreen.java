package com.inoxapps.funrun.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.FunRun;
import com.inoxapps.funrun.GameState;
import com.inoxapps.funrun.Settings;
import com.inoxapps.funrun.World;
import com.inoxapps.funrun.WorldRenderer;
import com.inoxapps.funrun.gameObjects.Player;
import com.inoxapps.funrun.gameObjects.PlayerState;
import com.inoxapps.funrun.utils.ActionResolver;
import com.inoxapps.funrun.utils.CoinManager;
import com.inoxapps.funrun.utils.OverlapTester;
import com.inoxapps.funrun.utils.Screen;
import com.inoxapps.funrun.utils.ScreenManager;

public class GameScreen extends Screen {

	private Sprite powerUpIcon;
	private float step;
	private float step1;
	private World world;
	private WorldRenderer worldRenderer;
	private Vector3 touchPoint = new Vector3();
	private float startTimer;
	private final float totalX;
	private final float buildingGap;

	public GameScreen() {
		initGame();
		this.buildingGap = Assets.commonlevel_buildingsBackup.get(3).getWidth() + 2f;
		this.totalX = Assets.commonlevel_finishLineSprites.get(0).getX();
	}

	private void initGame() {
		Screen.initNotifications();
		this.world = new World();
		this.worldRenderer = new WorldRenderer(world);
		System.gc();
		startTimer = 3;
		step = 0;
		step1 = 0;
	}

	@Override
	public void update(float deltaTime) {
		if (world.getState() != GameState.FINISH) {
			world.update(deltaTime);
			updateFrontClouds();
			if (world.getState() == GameState.RACE
					&& (world.player.getState() == PlayerState.RUNNING || world.player.getState() == PlayerState.JUMP)) {
				updateBackground();
				updateBackClouds();
			}
		} else {
			if (jitter == 0 || jitter == 25)
				jitterFactor *= -1;
			jitter += jitterFactor;
		}
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Screen.guiCam.update();
		Screen.batcher.setProjectionMatrix(Screen.guiCam.combined);
		Screen.batcher.begin();
		switch (world.getState()) {
		case READY:
			renderBackground(deltaTime);
			renderReady(deltaTime);
			worldRenderer.render(deltaTime);
			break;
		case RACE:
			renderBackground(deltaTime);
			worldRenderer.render(deltaTime);
			renderNotifications();
			renderUI();
			break;
		case FINISH:
			renderFinish();
			break;
		}
		Screen.batcher.end();
	}

	private void updateBackground() {
		step++;
		step1++;
		if (Settings.BG_LAYER2_SPEED * step >= 4 * buildingGap + 50) {
			Assets.updateBuildings(0, 4);
			step = -4 * buildingGap / Settings.BG_LAYER2_SPEED;
		}

		if (Settings.BG_LAYER2_SPEED * step1 >= 8 * buildingGap + 50) {
			Assets.updateBuildings(4, 8);
			step1 = 0;
		}

	}

	private void renderBackground(float deltaTime) {
		Assets.commonlevel_backGround.draw(Screen.batcher);
		renderBackClouds();

		for (int i = 0; i < 4; i++) {
			Assets.commonlevel_buildings[i].setPosition(i * buildingGap - Settings.BG_LAYER2_SPEED * step, 0);
			Assets.commonlevel_buildings[i].draw(batcher, 0.8f);
		}
		for (int i = 4; i < 8; i++) {
			Assets.commonlevel_buildings[i].setPosition(i * buildingGap - Settings.BG_LAYER2_SPEED * step1, 0);
			Assets.commonlevel_buildings[i].draw(batcher, 0.8f);
		}

		renderFrontClouds();
	}

	private void updateBackClouds() {
		setCloud(Assets.commonlevel_clouds.get(0), -0.2f);
		setCloud(Assets.commonlevel_clouds.get(1), -0.2f);
		setCloud(Assets.commonlevel_clouds.get(4), -0.2f);
		if (isOutOfView(Assets.commonlevel_clouds.get(1))) {
			rearrangeCloud(Assets.commonlevel_clouds.get(0));
			rearrangeCloud(Assets.commonlevel_clouds.get(1));
		}
		if (isOutOfView(Assets.commonlevel_clouds.get(4))) {
			rearrangeCloud(Assets.commonlevel_clouds.get(4));
		}
	}

	private void updateFrontClouds() {
		setCloud(Assets.commonlevel_clouds.get(2), -2f);
		setCloud(Assets.commonlevel_clouds.get(3), -2f);
		if (isOutOfView(Assets.commonlevel_clouds.get(2))) {
			rearrangeCloud(Assets.commonlevel_clouds.get(2));
		}
		if (isOutOfView(Assets.commonlevel_clouds.get(3))) {
			rearrangeCloud(Assets.commonlevel_clouds.get(3));
		}
	}

	private void renderBackClouds() {
		Assets.commonlevel_clouds.get(0).draw(Screen.batcher);
		Assets.commonlevel_clouds.get(1).draw(Screen.batcher);
		Assets.commonlevel_clouds.get(4).draw(Screen.batcher);
	}

	private void renderFrontClouds() {
		Assets.commonlevel_clouds.get(2).draw(Screen.batcher);
		Assets.commonlevel_clouds.get(3).draw(Screen.batcher);
	}

	private void setCloud(Sprite sprite, float offset) {
		sprite.translateX(offset);
	}

	private void rearrangeCloud(Sprite sprite) {
		sprite.setPosition(sprite.getX() + Settings.VIEWPORT_WIDTH * 2, MathUtils.random() * Settings.VIEWPORT_HEIGHT
				/ 1.5f + Settings.VIEWPORT_HEIGHT / 3f - sprite.getHeight());
	}

	private boolean isOutOfView(Sprite sprite) {
		return (sprite.getX() + sprite.getWidth() < 0);
	}

	// TODO state is changed from here
	private void renderReady(float deltaTime) {
		Assets.assets_Blackfont.draw(Screen.batcher, "" + (int) Math.ceil(startTimer), 400, 470);
		if (startTimer < 0f)
			world.setState(GameState.RACE);
		startTimer -= deltaTime;
	}

	private void renderUI() {
		// Assets.commonlevel_cross.draw(Screen.batcher);
		Assets.commonlevel_deployButton.draw(Screen.batcher);
		Assets.commonlevel_jumpButton.draw(Screen.batcher);
		powerUpIcon = world.player.getPowerUpIcon();
		if (powerUpIcon != null) {
			powerUpIcon.draw(Screen.batcher);
		}
		renderPlayersPosition();
	}

	private int firstPlayerPositionY;

	private void renderFinish() {
		firstPlayerPositionY = 360;
		Assets.ui_commonBG.draw(Screen.batcher, 0.5f);
		Assets.ui_leaderboard.draw(Screen.batcher);
		Assets.ui_resultHomeButton.draw(Screen.batcher);
		Assets.ui_resultRefreshButton.draw(Screen.batcher);
		Assets.ui_resultStoreButton.draw(Screen.batcher);
		Assets.ui_resultText.draw(Screen.batcher);
		CoinManager.drawCoinHud();
		for (int i = 1; i <= Settings.PLAYER_NUMBER; i++) {
			if (i == 1)
				world.playersPositions.get(i).type.playerHead.setPosition(150, 360);
			else if (i == 2)
				world.playersPositions.get(i).type.playerHead.setPosition(50, 250);
			else if (i == 3)
				world.playersPositions.get(i).type.playerHead.setPosition(250, 220);
			else if (i == 4)
				world.playersPositions.get(i).type.playerHead.setPosition(350, 130);

			if (world.playersPositions.get(i) == world.player) {
				world.playersPositions.get(i).type.playerHead.draw(Screen.batcher, jitter / 25);
				Assets.assets_Whitefont.setColor(1, 1, 1, jitter / 25);
			} else {
				world.playersPositions.get(i).type.playerHead.draw(Screen.batcher);
				Assets.assets_Whitefont.setColor(1, 1, 1, 1);
			}

			Assets.assets_Whitefont.draw(Screen.batcher, i + "." + world.playersPositions.get(i).getName() + ": "
					+ world.playersPositions.get(i).getRaceTime() + "s", 450, firstPlayerPositionY);

			if (ScreenEnum.GAME.isMultiPlayer && world.playersPositions.get(i) == world.player
					&& world.isScoreUpdated()) {
				drawScoreOnHead(i);
			}

			firstPlayerPositionY -= 40;
		}
	}

	private float jitter;
	private float jitterFactor = -1;

	private void drawScoreOnHead(int i) {
		Assets.ui_scores.get(i - 1).setPosition(world.playersPositions.get(i).type.playerHead.getX(),
				world.playersPositions.get(i).type.playerHead.getY() + 50 + jitter);
		Assets.ui_scores.get(i - 1).draw(Screen.batcher);
	}

	private void renderPlayersPosition() {
		Assets.commonlevel_line.draw(Screen.batcher);
		for (Player player : world.players) {
			float x = (player.getPosition().x / totalX) * 500;
			player.type.playerHead.setPosition(x + 100f, 10f);
			player.type.playerHead.draw(Screen.batcher);
		}
		Assets.assets_Blackfont.draw(Screen.batcher, getPosition(world.getPlayersPosition()), 350, 470);
	}

	private String getPosition(int position) {
		String s;
		switch (position) {
		case 1:
			s = "1st";
			break;
		case 2:
			s = "2nd";
			break;
		case 3:
			s = "3rd";
			break;
		case 4:
			s = "4th";
			break;
		default:
			s = "4th";
			break;
		}
		return s;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		guiCam.unproject(touchPoint.set(screenX, screenY, 0));
		switch (world.getState()) {
		case READY:
			break;
		case RACE:
			// if
			// (OverlapTester.pointInRectangle(Assets.commonlevel_cross.getBoundingRectangle(),
			// touchPoint.x,
			// touchPoint.y)) {
			// showLeaveGamePopUp();
			// return true;
			// }
			if (OverlapTester.pointInRectangle(Assets.commonlevel_deployBounds, touchPoint.x, touchPoint.y)) {
				world.player.deploy();
				return false;
			}
			if (OverlapTester.pointInRectangle(Assets.commonlevel_jumpBounds, touchPoint.x, touchPoint.y)) {
				world.player.jump();
				return false;
			}
			break;
		case FINISH:
			if (OverlapTester.pointInRectangle(Assets.ui_resultHomeButton.getBoundingRectangle(), touchPoint.x,
					touchPoint.y)) {
				ScreenManager.getInstance().show(ScreenEnum.MAIN_MENU);
				return true;
			}

			if (OverlapTester.pointInRectangle(Assets.ui_resultRefreshButton.getBoundingRectangle(), touchPoint.x,
					touchPoint.y)) {

				if (!ScreenEnum.GAME.isMultiPlayer)
					initGame();
				else
					ScreenManager.getInstance().show(ScreenEnum.SEARCHING);
				return true;
			}
			if (OverlapTester.pointInRectangle(Assets.ui_resultStoreButton.getBoundingRectangle(), touchPoint.x,
					touchPoint.y)) {
				ScreenManager.getInstance().show(ScreenEnum.STORE);
				return true;
			}
			break;
		}
		return false;
	}

	@Override
	public void dispose() {
		Assets.disposeLevel();
		Assets.assets_Whitefont.setColor(1, 1, 1, 1);
		ScreenEnum.GAME.names = null;
		ScreenEnum.GAME.types = null;
		ScreenEnum.GAME.isMultiPlayer = false;
		Screen.batcher.setProjectionMatrix(Screen.guiCam.combined);
		ScreenManager.getInstance().dispose(ScreenEnum.GAME);
	}

	@Override
	public void backKeyPressed() {
		if (world.getState() != GameState.FINISH)
			showLeaveGamePopUp();
		else
			ScreenManager.getInstance().show(ScreenEnum.MAIN_MENU);
	}

	private void showLeaveGamePopUp() {
		FunRun.actionResolver.showPopup("Quit", "Are you sure?", "Ok", "Cancel", false,
				new ActionResolver.PopupCallback() {
					@Override
					public void onClicked(boolean isYesClicked) {
						if (isYesClicked)
							ScreenManager.getInstance().show(ScreenEnum.MAIN_MENU);
					}
				});
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}
}