package com.inoxapps.funrun.gameObjects.boosters.obstacles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.gameObjects.Player;
import com.inoxapps.funrun.physics.DynamicGameObject;
import com.inoxapps.funrun.utils.Screen;

public class BombObstacle extends AbstractObstacle {

	private Sprite keyFrame;
	private float stateTime = 0;
	private boolean inMiddle = false;
	private boolean isBombAnimationStarted = false;
	private float bombButtonPressed = 0.5f;
	private Array<Sprite> sprites;

	public BombObstacle() {
		super(Assets.commonlevel_powerUpIcons.get(3), ObstacleType.STATIC);
		this.setTimeLimits(10f, bombButtonPressed + Assets.commonlevel_bombAnimation.animationDuration);
		this.keyFrame = Assets.commonlevel_bombSprites.get(0);
	}

	@Override
	public void updatePowerUp(float deltaTime) {

		if (keyFrame != null)
			setBounds(keyFrame.getWidth(), keyFrame.getHeight());

		if (state == BoosterState.HIT) {
			if (!inMiddle && playerHit != null
					&& playerHit.getPosition().x + playerHit.getBounds().width / 2 >= position.x + bounds.width / 2) {
				inMiddle = true;
				playerHit.getModifier().setVelRatio(0.05f, 0.05f);
				playerHit.getModifier().setAccRatio(0f, 0f);
				this.keyFrame = Assets.commonlevel_bombSprites.get(1);
			}
		}

		if (!isBombAnimationStarted && inMiddle) {
			if (bombButtonPressed <= 0) {
				this.isBombAnimationStarted = true;
				this.moveBodyParts();
				if (!playerHit.isAI) {
					Assets.assets_mine_step_Sound.stop();
					Assets.assets_mine_blast_Sound.play(Assets.soundVolume);
				}
			}
			bombButtonPressed -= deltaTime;
		}
	}

	@Override
	protected void renderNotifications(Player playerHit, float ownerBoundsY) {
		super.renderNotifications(playerHit, ownerBoundsY);
		if (state == BoosterState.HIT && !playerHit.isAI) {
			for (Sprite sprite : Assets.commonlevel_bloodPatch)
				sprite.draw(Screen.batcher);
		}
	}

	@Override
	public void renderDeployed(float deltaTime) {
		keyFrame.setPosition(position.x, position.y);
		keyFrame.draw(Screen.batcher);
	}

	@Override
	public void renderOnHit(float deltaTime) {
		keyFrame.setPosition(position.x, position.y);
		keyFrame.draw(Screen.batcher);

		if (inMiddle && isBombAnimationStarted) {
			keyFrame = ((Sprite) Assets.commonlevel_bombAnimation.getKeyFrame(stateTime));
			stateTime += deltaTime;
			renderBodyParts();
		} else {
			playerHit.getKeyFrame().setPosition(playerHit.getPosition().x, playerHit.getPosition().y);
			playerHit.getKeyFrame().draw(Screen.batcher);
		}
	}

	private void renderBodyParts() {
		int i = 0;
		for (Sprite sprite : sprites) {
			sprite.setPosition(playerHit.bodyParts.get(i).getPosition().x, playerHit.bodyParts.get(i).getPosition().y);
			sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
			sprite.rotate(2f);
			sprite.draw(Screen.batcher);
			i++;
		}
	}

	@Override
	public void reset() {
		super.reset();
		keyFrame = Assets.commonlevel_bombSprites.get(0);
		stateTime = 0;
		inMiddle = false;
		isBombAnimationStarted = false;
		bombButtonPressed = 0.5f;
	}

	@Override
	protected void onHit() {
		super.onHit();
		if (!playerHit.isAI)
			Assets.assets_mine_step_Sound.play(Assets.soundVolume);
		stateTime = 0;
		sprites = Assets.getPlayerSprites(playerHit.type, "BODY");
	}

	@Override
	protected void resetPlayer() {
		super.resetPlayer();
		playerHit.getModifier().setAccRatio(1, 1);
		keyFrame = Assets.commonlevel_bombSprites.get(0);
		isBombAnimationStarted = false;
		bombButtonPressed = 0.5f;
		inMiddle = false;
		playerHit.resetBodyParts();
		for (Sprite sprite : sprites)
			sprite.setRotation(0);
	}

	private void moveBodyParts() {
		int i = 0;
		for (DynamicGameObject part : playerHit.bodyParts) {
			part.setBounds(sprites.get(i).getWidth(), sprites.get(i).getHeight());
			part.setVelocity(MathUtils.random(-5, 5), MathUtils.random(5, 10));
			part.setPosition(position.x, position.y);
			part.isStatic = false;
			i++;
		}
	}

	@Override
	protected void onDeploy() {
		super.onDeploy();
		this.isCollidable = true;
	}

}
