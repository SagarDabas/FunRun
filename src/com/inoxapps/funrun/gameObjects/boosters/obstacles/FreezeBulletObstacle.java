package com.inoxapps.funrun.gameObjects.boosters.obstacles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.Settings;
import com.inoxapps.funrun.utils.Screen;

public class FreezeBulletObstacle extends AbstractObstacle {

	private Sprite keyFrame;
	private float stateTime = 0;
	private float offsetX;
	private float freezeTime = 2f;

	public FreezeBulletObstacle() {
		super(Assets.commonlevel_powerUpIcons.get(4), ObstacleType.DYNAMIC);
		this.useGravity = false;
		setTimeLimits(10f, freezeTime + Assets.commonlevel_freezeNew.animationDuration);
		setVelocity(Settings.PlayerSettings.MAX_SPEED.x + 10f, 0);
	}

	@Override
	protected void onDeploy() {
		super.onDeploy();
		if (!owner.isAI)
			Assets.assets_freeze_teleport_shoot_Sound.play(Assets.soundVolume);
	}

	@Override
	public void updatePowerUp(float deltaTime) {
	}

	@Override
	public void renderDeployed(float deltaTime) {
		for (int i = 3; i >= 0; i--) {
			Assets.commonlevel_freezeBullet.setPosition(position.x - i * 0.8f, position.y);
			Assets.commonlevel_freezeBullet.draw(Screen.batcher, (1 - i/4f));
		}
	}

	@Override
	public void renderOnHit(float deltaTime) {

		playerHit.getKeyFrame().setPosition(playerHit.getPosition().x, playerHit.getPosition().y);
		playerHit.getKeyFrame().draw(Screen.batcher);
		keyFrame = ((Sprite) Assets.commonlevel_freezeNew.getKeyFrame(stateTime));
		offsetX = keyFrame.getWidth() / 2 - playerHit.getKeyFrame().getWidth() / 2;
		keyFrame.setPosition(playerHit.getPosition().x - offsetX, playerHit.getPosition().y);
		keyFrame.draw(Screen.batcher);

		if (freezeTime > 0)
			freezeTime -= deltaTime;
		else{
			if (!playerHit.isAI && stateTime == 0) {
				Assets.assets_freeze_Sound.stop();
				Assets.assets_freeze_out_Sound.play(Assets.soundVolume);
			}
			stateTime += deltaTime;
		}
	}

	@Override
	public void reset() {
		super.reset();
		stateTime = 0;
		setVelocity(Settings.PlayerSettings.MAX_SPEED.x + 10f, 0);
	}

	@Override
	protected void onHit() {
		super.onHit();
		if (!playerHit.isAI) {
			Assets.assets_freeze_teleport_shoot_Sound.stop();
			Assets.assets_freeze_Sound.play(Assets.soundVolume);
		}
		stateTime = 0;
		playerHit.getModifier().setVelRatio(0f, 0f);
		playerHit.getModifier().setAccRatio(0f, 0f);
		isTrigger = false;
		isStatic = true;
		freezeTime = 2f;
	}

	@Override
	protected void resetPlayer() {
		super.resetPlayer();
		stateTime = 0;
		freezeTime = 2f;
		changeState(BoosterState.FREE);
	}

}
