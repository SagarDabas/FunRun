package com.inoxapps.funrun.gameObjects.boosters.obstacles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.Settings;
import com.inoxapps.funrun.gameObjects.Platform;
import com.inoxapps.funrun.gameObjects.PlayerState;
import com.inoxapps.funrun.utils.Screen;

public class PlaceSwitcherObstacle extends AbstractObstacle {

	private float x;
	private float y;
	private float stateTime;
	private Sprite keyFrame;
	private float offsetX;

	public PlaceSwitcherObstacle() {
		super(Assets.commonlevel_powerUpIcons.get(7), ObstacleType.DYNAMIC);
		this.useGravity = false;
		this.stateTime = 0;
		setTimeLimits(10f, Assets.commonlevel_teleportAnimation.animationDuration);
		setVelocity(Settings.PlayerSettings.MAX_SPEED.x + 10f, 0);
	}

	@Override
	protected void onDeploy() {
		super.onDeploy();
		if (!owner.isAI)
			Assets.assets_freeze_teleport_shoot_Sound.play(Assets.soundVolume);
	}

	@Override
	public void renderDeployed(float deltaTime) {
		for (int i = 9; i >= 0; i--) {
			Assets.commonlevel_playerSwitchBullet.setPosition(position.x - i * 0.5f, position.y);
			Assets.commonlevel_playerSwitchBullet.draw(Screen.batcher, (1 - i/10f));
		}
	}

	@Override
	public void renderOnHit(float deltaTime) {
		keyFrame = (Sprite) Assets.commonlevel_teleportAnimation.getKeyFrame(stateTime);
		keyFrame.setPosition(owner.getPosition().x - offsetX, owner.getPosition().y);
		keyFrame.draw(Screen.batcher);
		keyFrame.setPosition(playerHit.getPosition().x - offsetX, playerHit.getPosition().y);
		keyFrame.draw(Screen.batcher);
	}

	@Override
	public void reset() {
		super.reset();
		owner = null;
		x = 0;
		y = 0;
		setVelocity(Settings.PlayerSettings.MAX_SPEED.x + 10f, 0);
	}

	@Override
	protected void onHit() {
		super.onHit();
		playerHit.getModifier().setVelRatio(0f, 0f);
		playerHit.getModifier().setAccRatio(0f, 0f);
		owner.getModifier().setAccRatio(0f, 0f);
		owner.getModifier().setVelRatio(0f, 0f);
		owner.isHit(this);
		Platform temp = owner.getLastSpawnPlatform();
		owner.setLastSpawnPlatform(playerHit.getLastSpawnPlatform());
		playerHit.setLastSpawnPlatform(temp);
		x = owner.getPosition().x;
		y = owner.getPosition().y;
		owner.setPosition(playerHit.getPosition().x, playerHit.getPosition().y);
		playerHit.setPosition(x, y);
		isTrigger = false;
		isStatic = true;
		stateTime = 0;
		if (!playerHit.isAI || !owner.isAI) {
			Assets.assets_freeze_teleport_shoot_Sound.stop();
			Assets.assets_teleport_Sound.play(Assets.soundVolume);
		}
	}

	@Override
	protected void resetPlayer() {
		super.resetPlayer();
		owner.hitOver(true);
		changeState(BoosterState.FREE);
		stateTime = 0;
	}

	// called by obstacles.update
	@Override
	protected void updatePowerUp(float deltaTime) {
		if (state == BoosterState.DEPLOYED) {
			if (owner.getState() == PlayerState.HIT && owner.getObstacleUsed() != this) {
				changeState(BoosterState.FREE);
			}
		} else if (state == BoosterState.HIT) {
			offsetX = keyFrame.getWidth() / 2 - playerHit.getKeyFrame().getWidth() / 2;
			stateTime += deltaTime;
		}
	}
}
