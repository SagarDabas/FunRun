package com.inoxapps.funrun.gameObjects.boosters;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.Settings;
import com.inoxapps.funrun.gameObjects.Player;
import com.inoxapps.funrun.gameObjects.boosters.obstacles.AbstractObstacle;
import com.inoxapps.funrun.gameObjects.boosters.powerups.ThunderPowerUp;
import com.inoxapps.funrun.physics.CollisionSide;
import com.inoxapps.funrun.physics.DynamicGameObject;
import com.inoxapps.funrun.utils.Notification;
import com.inoxapps.funrun.utils.Screen;

public abstract class AbstractBooster extends DynamicGameObject implements Poolable {

	public enum BoosterState {
		FREE, PICKED, DEPLOYED, HIT
	};

	public final Sprite icon;
	protected BoosterState state;
	protected Player playerHit;
	private float aliveTime;
	private float hitTime;
	private float aliveTimeBackup;
	private float hitTimeBackup;
	protected Player owner;

	public AbstractBooster(Sprite icon) {
		this.icon = icon;
		this.isTrigger = false;
		this.isStatic = true;
		this.setTimeLimits(20f, 1f);
		this.changeState(BoosterState.PICKED);
		Screen.addNotification(new Notification() {
			@Override
			public void notifyScreen() {
				renderNotifications(playerHit, ownerBoundsY);
			}
		});
	}

	protected final void setTimeLimits(float aliveTime, float hitTime) {
		this.aliveTime = aliveTime;
		this.hitTime = hitTime;
		this.aliveTimeBackup = aliveTime;
		this.hitTimeBackup = hitTime;
	}

	public final void render(float deltaTime) {
		switch (state) {

		case PICKED:
			// FunRun.batcher.setProjectionMatrix(FunRun.guiCam.combined);
			// TODO render the icon for this powerUp on the deploy button
			break;

		case DEPLOYED:
			renderDeployed(deltaTime);
			break;

		case HIT:
			renderOnHit(deltaTime);
			break;

		case FREE:
			break;
		}
	}

	public final void update(float deltaTime) {
		updatePowerUp(deltaTime);
		switch (state) {
		case DEPLOYED:
			updatePosition();
			updateTimer(deltaTime);
			break;
		case HIT:
			updateHitTimer(deltaTime);
			break;
		case PICKED:
			break;
		case FREE:
			break;
		}
	}

	protected void updateHitTimer(float deltaTime) {
		hitTime -= deltaTime;
		if (hitTime <= 0) {
			if (playerHit != null) {
				hitTime = hitTimeBackup;
				onHitTimeOver();
			}
		}
	}
	
	@Override
	public void onCollision(DynamicGameObject gameObject, CollisionSide side) {
		super.onCollision(gameObject, side);
	}
	
	public void hitPlayer(Player player) {
		changeState(BoosterState.HIT);
		this.playerHit = player;
		onHit();
	}

	public final void grabbed(Player player) {
		changeState(BoosterState.PICKED);
	}

	private final void updatePosition() {
		if (position.y < 0) {
			changeState(BoosterState.FREE);
		}
	}

	private final void updateTimer(float deltaTime) {
		aliveTime -= deltaTime;
		if (aliveTime <= 0) {
			changeState(BoosterState.FREE);
		}

	}

	@Override
	public void reset() {
		hitTime = hitTimeBackup;

		aliveTime = aliveTimeBackup;

		playerHit = null;

		// no more collision
		isTrigger = false;

		isStatic = true;

		isCollidable = false;

		setVelocity(0, 0);

		setBounds(0, 0);
	}

	public final BoosterState getState() {
		return state;
	}

	protected void changeState(BoosterState state) {
		this.state = state;
	}

	public void deploy(float x, float y, Player owner) {
		this.owner = owner;
	}

	protected abstract void onHitTimeOver();

	protected abstract void updatePowerUp(float deltaTime);

	protected abstract void renderDeployed(float deltaTime);

	protected abstract void renderOnHit(float deltaTime);

	@Override
	protected void finalize() throws Throwable {
		System.out.println(this);
	}

	// TODO textbounds changes at runtime, instead store the width at first.
	private float ownerBoundsX;
	private float ownerBoundsY;
	public static List<AbstractBooster> boosterNotifications;

	protected void onHit() {
		updateNotificationBounds(playerHit);
	}

	protected void resetPlayer() {
		updateNotificationBoundsReverse();
	}

	protected void updateNotificationBoundsReverse() {
		int i = boosterNotifications.indexOf(this);
		boosterNotifications.remove(i);
		for (; i < boosterNotifications.size(); i++)
			boosterNotifications.get(i).ownerBoundsY -= Assets.getSmallIcon(icon).getHeight();
	}

	protected void updateNotificationBounds(Player playerHit) {
		ownerBoundsX = Assets.assets_Blackfont.getBounds(owner.getName()).width;
		if (boosterNotifications.size() == 0) {
			ownerBoundsY = Assets.getSmallIcon(icon).getHeight();
		} else if (boosterNotifications.size() > 0) {
			ownerBoundsY = boosterNotifications.get(boosterNotifications.size() - 1).ownerBoundsY
					+ Assets.getSmallIcon(icon).getHeight();
		}
		boosterNotifications.add(this);
	}

	protected void renderNotifications(Player playerHit, float ownerBoundsY) {
		if (state == BoosterState.HIT) {

			float playerHitBoundsX = 0;

			if (AbstractObstacle.class.isAssignableFrom(this.getClass()) || this instanceof ThunderPowerUp) {
				playerHitBoundsX = Assets.assets_Blackfont.getBounds(playerHit.getName()).width;
				Assets.assets_Blackfont.draw(Screen.batcher, playerHit.getName(),
						Settings.VIEWPORT_WIDTH - playerHitBoundsX, Settings.VIEWPORT_HEIGHT - ownerBoundsY
								+ Assets.getSmallIcon(icon).getHeight());
			}

			Assets.getSmallIcon(icon).setPosition(
					Settings.VIEWPORT_WIDTH - playerHitBoundsX - Assets.getSmallIcon(icon).getWidth(),
					Settings.VIEWPORT_HEIGHT - ownerBoundsY);

			Assets.assets_Blackfont.draw(Screen.batcher, owner.getName(), Assets.getSmallIcon(icon).getX() - ownerBoundsX,
					Settings.VIEWPORT_HEIGHT - ownerBoundsY + Assets.getSmallIcon(icon).getHeight());

			Assets.getSmallIcon(icon).draw(Screen.batcher);

		}
	}
}
