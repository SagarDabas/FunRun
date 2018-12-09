package com.inoxapps.funrun.gameObjects.boosters.obstacles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.Settings;
import com.inoxapps.funrun.gameObjects.Platform;
import com.inoxapps.funrun.gameObjects.Player;
import com.inoxapps.funrun.physics.CollisionSide;
import com.inoxapps.funrun.physics.DynamicGameObject;
import com.inoxapps.funrun.utils.Screen;

public class SlicerRollerObstacle extends AbstractObstacle {

	private float stateTime = 0;
	private final Sprite slicerWithoutBlood;
	private final Sprite bloodSlicer;
	private Sprite slicer;
	private float boundX;
	private float boundY;
	private float rotation;
	private Array<Sprite> sprites;
	private float hitTime;
	private float blinking;
	private int blinkingFactor;

	public SlicerRollerObstacle() {
		super(Assets.commonlevel_powerUpIcons.get(6), ObstacleType.DYNAMIC);
		hitTime = 1f;
		blinking = 1f;
		blinkingFactor = 1;
		this.setTimeLimits(6f, hitTime);
		this.rotation = -10f;
		this.restitution = 0.8f;
		this.slicerWithoutBlood = new Sprite(Assets.commonlevel_slicer);
		this.bloodSlicer = new Sprite(Assets.commonlevel_slicerBlood);
		this.slicer = slicerWithoutBlood;
		this.boundX = Assets.commonlevel_slicer.getRegionWidth() / Settings.CAM_SCALE;
		this.boundY = Assets.commonlevel_slicer.getRegionHeight() / Settings.CAM_SCALE;
		setBounds(boundX, boundY);
		setVelocity(Settings.PlayerSettings.MAX_SPEED.x + 5f, 0);
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
	public void renderDeployed(float deltaTime) {
		renderBlade();
	}

	private void renderBlade() {
		slicer.setPosition(position.x, position.y);
		slicer.setOrigin(slicer.getWidth() / 2f, slicer.getHeight() / 2f);
		slicer.rotate(rotation);
		slicer.draw(Screen.batcher, blinking);
	}

	@Override
	public void onCollision(DynamicGameObject gameObject, CollisionSide side) {
		if ((side == CollisionSide.FORWARD || side == CollisionSide.BACKWARD) && gameObject instanceof Platform) {
			rotation = -rotation;
		}
	}

	@Override
	public void renderOnHit(float deltaTime) {
		sprites.get(1).setPosition(playerHit.getPosition().x + stateTime, playerHit.getPosition().y);
		sprites.get(1).draw(Screen.batcher);

		renderBlade();

		sprites.get(0).setPosition(playerHit.getPosition().x - stateTime, playerHit.getPosition().y);
		sprites.get(0).draw(Screen.batcher);

		stateTime += deltaTime;
	}

	@Override
	protected void renderNotifications(Player playerHit, float ownerBoundsY) {
		super.renderNotifications(playerHit, ownerBoundsY);
		// TODO should only be seen to the player playing
		if (state == BoosterState.HIT && !playerHit.isAI) {
			for (Sprite sprite : Assets.commonlevel_bloodPatch)
				sprite.draw(Screen.batcher);
		}
	}

	@Override
	protected void onHit() {
		super.onHit();
		if (!playerHit.isAI)
			Assets.assets_slicer_Sound.play(Assets.soundVolume);
		playerHit.getModifier().setVelRatio(0, 0);
		playerHit.getModifier().setAccRatio(0, 0);
		slicer = bloodSlicer;
		sprites = Assets.getPlayerSprites(playerHit.type, "SLICER");
	}

	@Override
	public void reset() {
		super.reset();
		hitTime = 1f;
		blinking = 1f;
		blinkingFactor = 1;
		setVelocity(Settings.PlayerSettings.MAX_SPEED.x + 5f, 0);
		stateTime = 0;
		this.rotation = -10f;
		setAcceleration(0, 0);
		this.slicer = slicerWithoutBlood;
		setBounds(boundX, boundY);
	}

	@Override
	protected void onDeploy() {
		super.onDeploy();
		this.isCollidable = true;
	}

	@Override
	protected void resetPlayer() {
		super.resetPlayer();
		stateTime = 0;
	}

}
