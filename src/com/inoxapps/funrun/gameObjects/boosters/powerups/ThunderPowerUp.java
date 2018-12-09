package com.inoxapps.funrun.gameObjects.boosters.powerups;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.Settings;
import com.inoxapps.funrun.gameObjects.Player;
import com.inoxapps.funrun.gameObjects.PlayerState;
import com.inoxapps.funrun.utils.Screen;

public class ThunderPowerUp extends AbstractPowerUp {

	private float offsetX;
	private float lightningTime;
	private byte index;
	private float frameDuration;
	private float alpha;
	private boolean isLightning = false;
	private boolean isLightningOver;
	private boolean isPlayerHit[];
	public final List<Player> hitPlayers;
	private final OrthographicCamera gamCam;
	private List<Player> players;

	public ThunderPowerUp(OrthographicCamera gameCam) {
		super(Assets.commonlevel_powerUpIcons.get(5));
		this.gamCam = gameCam;
		this.hitPlayers = new ArrayList<Player>();
		this.setTimeLimits(3f);
		initValues();
	}

	// TODO should be initialised in the constructor and the players list should
	// be final.
	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	@Override
	public void updatePowerUp(float deltaTime) {

	}

	@Override
	public void renderOnHit(float deltaTime) {

		manageLightning(deltaTime);

		drawBackClouds();

		if (isLightning) {
			startLightning(deltaTime);
		}

		drawFrontClouds();

		offsetX -= 0.5f;
	}

	private void drawFrontClouds() {
		Screen.batcher.setProjectionMatrix(Screen.guiCam.combined);
		Assets.commonlevel_thunderClouds.get(0).setPosition(-offsetX, 0);
		Assets.commonlevel_thunderClouds.get(0).draw(Screen.batcher, alpha);
	}

	private void drawBackClouds() {
		Screen.batcher.setProjectionMatrix(Screen.guiCam.combined);
		Assets.commonlevel_translucent.draw(Screen.batcher, alpha);
		Assets.commonlevel_thunderClouds.get(1).setPosition(offsetX, 0);
		Assets.commonlevel_thunderClouds.get(1).draw(Screen.batcher, alpha);
	}

	private void manageLightning(float deltaTime) {
		if (!isLightning && !isLightningOver) {
			if (lightningTime > 0) {
				lightningTime -= deltaTime;
				if (alpha < 1)
					alpha += deltaTime;
			} else if (lightningTime < 0) {
				Assets.assets_thunder_Sound.play(Assets.soundVolume);
				isLightning = true;
			}
		} else if (isLightning && !isLightningOver) {
			if (lightningTime < 1f)
				lightningTime += deltaTime;
			else if (!isLightningOver) {
				isLightning = false;
				isLightningOver = true;
				resetPlayers();
			}
		} else if (!isLightning && isLightningOver) {
			if (alpha > 0)
				alpha -= deltaTime;
		}
	}

	private void startLightning(float deltaTime) {
		alpha = 1;
		int i = 0;
		Screen.batcher.setProjectionMatrix(gamCam.combined);
		for (Player player : players) {
			if (player != owner) {
				if ((player.getState() == PlayerState.RUNNING || player.getState() == PlayerState.JUMP)
						&& !player.isGuarded()) {
					hitPlayers(player);
					isPlayerHit[i] = true;
					hitPlayers.add(player);
				}
				if (isPlayerHit[i]) {
					renderLightningAndShock(player, i);
				}
			}
			i++;
		}

		if (frameDuration < 0) {
			index = (byte) (1 - index);
			frameDuration = 1 / 16f;
		} else {
			frameDuration -= deltaTime;
		}
	}

	private void renderLightningAndShock(Player player, int i) {
		Assets.commonlevel_thunder.setPosition(player.getPosition().x + player.getBounds().width / 2
				- Assets.commonlevel_thunder.getWidth() / 2, player.getPosition().y + player.getBounds().height);
		Assets.commonlevel_thunder.draw(Screen.batcher);
		Assets.commonlevel_shocks.get(index).setPosition(player.getPosition().x, player.getPosition().y);
		Assets.commonlevel_shocks.get(index).draw(Screen.batcher);
	}

	@Override
	protected void onHit() {

	}

	@Override
	protected void renderNotifications(Player playerHit, float ownerBoundsY) {
		if (isLightning && hitPlayers.size() > 0) {
			int count = 0;
			for (Player player : hitPlayers) {
				// TODO why the divide does not work
				super.renderNotifications(player, ownerBoundsY - count * Assets.getSmallIcon(icon).getHeight());
				count++;
			}
		}
	}

	private void hitPlayers(Player player) {
		player.isHit(null);
		player.useGravity = false;
		player.getModifier().setVelRatio(0, 0);
		player.getModifier().setAccRatio(0, 0);
		updateNotificationBounds(player);
	}

	@Override
	public void reset() {
		super.reset();
		initValues();
	}

	private void initValues() {
		index = 0;
		offsetX = 0;
		frameDuration = 1 / 16f;
		lightningTime = 1f;
		isLightningOver = false;
		isLightning = false;
		alpha = 0f;
		isPlayerHit = new boolean[Settings.PLAYER_NUMBER];
		setTimeLimits(3f);
	}

	private void resetPlayers() {
		int i = 0;
		for (Player player : players) {
			if (player != owner && isPlayerHit[i]) {
				updateNotificationBoundsReverse();
				player.hitOver(true);
				player.useGravity = true;
				isPlayerHit[i] = false;
				hitPlayers.remove(player);
			}
			i++;
		}
	}

	@Override
	protected void resetPlayer() {
		if (!isLightningOver)
			resetPlayers();
		initValues();
	}

}
