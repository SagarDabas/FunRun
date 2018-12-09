package com.inoxapps.funrun.gameObjects;

import com.badlogic.gdx.math.Vector2;
import com.inoxapps.funrun.Settings;

public final class PlayerModifiers {

	private final Vector2 velRatio;
	private final Vector2 accRatio;
	private final Vector2 jumpRatio;
	private final Vector2 maxSpeedRatio;
	private final Player player;

	public PlayerModifiers(Player player) {
		this.velRatio = new Vector2(1, 1);
		this.accRatio = new Vector2(1, 1);
		this.jumpRatio = new Vector2(1, 1);
		this.maxSpeedRatio = new Vector2(1, 1);
		this.player = player;
	}

	public void setVelRatio(float x, float y) {
		velRatio.set(x, y);
		player.setVelocity(player.getVelocity().x, player.getVelocity().y);
	}

	public void setAccRatio(float x, float y) {
		accRatio.set(x, y);
		player.setAcceleration(Settings.PlayerSettings.ACCELERATION.x,
				Settings.PlayerSettings.ACCELERATION.y);
	}

	public void setMaxSpeedRatio(float x, float y) {
		maxSpeedRatio.set(x, y);
		player.setMaxSpeed(Settings.PlayerSettings.MAX_SPEED.x,
				Settings.PlayerSettings.MAX_SPEED.y);
	}

	public void setJumpRatio(float x, float y) {
		jumpRatio.set(x, y);
		player.setJumpSpeed(Settings.PlayerSettings.JUMP_SPEED.x,
				Settings.PlayerSettings.JUMP_SPEED.y);
	}

	public float getVelRatioX() {
		return velRatio.x;
	}

	public float getVelRatioY() {
		return velRatio.y;
	}

	public float getAccRatioX() {
		return accRatio.x;
	}

	public float getAccRatioY() {
		return accRatio.y;
	}

	public float getMaxSpeedRatioX() {
		return maxSpeedRatio.x;
	}

	public float getMaxSpeedRatioY() {
		return maxSpeedRatio.y;
	}

	public float getJumpRatioX() {
		return jumpRatio.x;
	}
	
	public float getJumpRatioY() {
		return jumpRatio.y;
	}

}
