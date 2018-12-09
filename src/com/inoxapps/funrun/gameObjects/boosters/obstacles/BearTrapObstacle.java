package com.inoxapps.funrun.gameObjects.boosters.obstacles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.gameObjects.Player;
import com.inoxapps.funrun.physics.DynamicGameObject;
import com.inoxapps.funrun.utils.Screen;

public class BearTrapObstacle extends AbstractObstacle {

	private Sprite keyFrame;
	private float stateTime = 0;
	private boolean inMiddle = false;
	private Array<Sprite> sprites;

	public BearTrapObstacle() {
		super(Assets.commonlevel_powerUpIcons.get(0), ObstacleType.STATIC);
		this.setTimeLimits(10f, Assets.commonlevel_bearTrapAnimation.animationDuration + 1f);
		this.keyFrame = ((Sprite) Assets.commonlevel_bearTrapAnimation.getKeyFrame(0));
	}

	@Override
	public void renderDeployed(float deltaTime) {
		keyFrame.setPosition(position.x, position.y);
		keyFrame.draw(Screen.batcher);
	}

	@Override
	public void renderOnHit(float deltaTime) {
		keyFrame = ((Sprite) Assets.commonlevel_bearTrapAnimation.getKeyFrame(stateTime));
		keyFrame.setPosition(position.x, position.y);
		keyFrame.draw(Screen.batcher);
		if (inMiddle) {
			renderBodyParts();
		} else {
			playerHit.getKeyFrame().setPosition(playerHit.getPosition().x, playerHit.getPosition().y);
			playerHit.getKeyFrame().draw(Screen.batcher);
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

	private void renderBodyParts() {
		int i = 0;
		for (Sprite sprite : sprites) {
			sprite.setPosition(playerHit.bodyParts.get(i).getPosition().x, playerHit.bodyParts.get(i).getPosition().y);
			sprite.draw(Screen.batcher);
			i++;
		}
	}

	@Override
	public void updatePowerUp(float deltaTime) {
		if (keyFrame != null)
			setBounds(keyFrame.getWidth(), keyFrame.getHeight());
		if (state == BoosterState.HIT) {
			if (!inMiddle && playerHit != null
					&& playerHit.getPosition().x + playerHit.getBounds().width / 2 >= position.x + bounds.width / 2) {
				inMiddle = true;
				playerHit.getModifier().setVelRatio(0, 0);
				playerHit.getModifier().setAccRatio(0, 0);
				this.moveBodyParts();
			} else if (inMiddle)
				stateTime += deltaTime;
		}
	}

	@Override
	public void reset() {
		super.reset();
		keyFrame = (Sprite) Assets.commonlevel_bearTrapAnimation.getKeyFrame(0);
		stateTime = 0;
		inMiddle = false;
	}

	@Override
	protected void onHit() {
		super.onHit();
		if (!playerHit.isAI)
			Assets.assets_beartrap_Sound.play(Assets.soundVolume);
		stateTime = 0;
		sprites = Assets.getPlayerSprites(playerHit.type, "BODY");
	}

	@Override
	protected void resetPlayer() {
		super.resetPlayer();
		keyFrame = (Sprite) Assets.commonlevel_bearTrapAnimation.getKeyFrame(1);
		playerHit.resetBodyParts();
		inMiddle = false;
	}

	@Override
	protected void onDeploy() {
		super.onDeploy();
		this.isCollidable = true;
	}

	private void moveBodyParts() {
		int i = 0;
		for (DynamicGameObject part : playerHit.bodyParts) {
			part.setBounds(sprites.get(i).getWidth(), sprites.get(i).getHeight());
			part.setVelocity(MathUtils.random(-2, 2), MathUtils.random(5, 7));
			part.setPosition(position.x, position.y);
			part.isStatic = false;
			part.isCollidable = true;
			i++;
		}
	}
}
