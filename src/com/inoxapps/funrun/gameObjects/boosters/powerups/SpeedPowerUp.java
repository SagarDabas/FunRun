package com.inoxapps.funrun.gameObjects.boosters.powerups;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.utils.Screen;

public class SpeedPowerUp extends AbstractPowerUp {

	private Sprite keyFrame;
	private final Vector2 playerTarget = new Vector2();
	private final Vector2[] lerpingVectors = new Vector2[5];
	private float step;

	public SpeedPowerUp() {
		super(Assets.commonlevel_powerUpIcons.get(2));
		setTimeLimits(10f);
		for (int i = 0; i < lerpingVectors.length; i++) {
			lerpingVectors[i] = new Vector2();
		}
	}

	@Override
	public void updatePowerUp(float deltaTime) {
	}

	@Override
	public void renderOnHit(float deltaTime) {
		keyFrame = playerHit.getKeyFrame();
		playerTarget.set(playerHit.getPosition().x, playerHit.getPosition().y);
		step = 0.5f;
		for (int i = 1; i <= lerpingVectors.length; i++) {
			lerpingVectors[i - 1].lerp(playerTarget, step);
			keyFrame.setPosition(lerpingVectors[i - 1].x,
					lerpingVectors[i - 1].y);
			keyFrame.draw(Screen.batcher, 1f / (i));
			step = step - 0.08f;
		}
	}

	@Override
	protected void onHit() {
		super.onHit();
		for (int i = 0; i < lerpingVectors.length; i++) {
			lerpingVectors[i].set(playerHit.getPosition().x,
					playerHit.getPosition().y);
		}		
		playerHit.getModifier().setMaxSpeedRatio(1.5f, 1f);
		playerHit.getModifier().setJumpRatio(1.5f, 1f);
	}

	@Override
	protected void resetPlayer() {
		super.resetPlayer();
		playerHit.getModifier().setMaxSpeedRatio(1f, 1f);
		playerHit.getModifier().setJumpRatio(1f, 1f);
	}

}
