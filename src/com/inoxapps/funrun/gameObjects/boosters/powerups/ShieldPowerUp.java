package com.inoxapps.funrun.gameObjects.boosters.powerups;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.utils.Screen;

public class ShieldPowerUp extends AbstractPowerUp {

	private final Sprite shield;
	private float hitTime;
	private float blinking;
	private int blinkingFactor;

	public ShieldPowerUp() {
		super(Assets.commonlevel_powerUpIcons.get(1));
		hitTime = 10f;
		blinking = 1f;
		blinkingFactor = 1;
		setTimeLimits(hitTime);
		this.shield = new Sprite(Assets.commonlevel_shield);
	}

	@Override
	public void renderOnHit(float deltaTime) {
		shield.setPosition(playerHit.getPosition().x - (shield.getWidth() - playerHit.getKeyFrame().getWidth()) / 2,
				playerHit.getPosition().y - (shield.getHeight() - playerHit.getKeyFrame().getHeight()) / 2);
		rotateSprite();
		shield.setOrigin(shield.getWidth() / 2f, shield.getHeight() / 2f);
		shield.rotate(-8f);

		shield.draw(Screen.batcher, blinking);
	}

	private void rotateSprite() {
		if (playerHit.getKeyFrame().getRotation() > 0)
			shield.setOrigin(shield.getWidth(), 0);
		else if (playerHit.getKeyFrame().getRotation() < 0)
			shield.setOrigin(0, 0);
		// shield.setRotation(playerHit.getKeyFrame().getRotation() +
		// shield.getRotation());
	}

	@Override
	public void updatePowerUp(float deltaTime) {
		if (state == BoosterState.HIT) {
			hitTime -= deltaTime;
			if (hitTime <= 3) {
				if (blinking >= 1 || blinking < 0)
					blinkingFactor *= -1;
				blinking += blinkingFactor * 0.1f;
			}
		}
	}

	@Override
	public void reset() {
		super.reset();
		hitTime = 10f;
		blinkingFactor = 1;
		blinking = 1;
	}
}
