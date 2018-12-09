package com.inoxapps.funrun.gameObjects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.inoxapps.funrun.gameObjects.boosters.AbstractBooster;
import com.inoxapps.funrun.gameObjects.boosters.obstacles.BearTrapObstacle;
import com.inoxapps.funrun.gameObjects.boosters.obstacles.BombObstacle;
import com.inoxapps.funrun.gameObjects.boosters.obstacles.FreezeBulletObstacle;
import com.inoxapps.funrun.gameObjects.boosters.obstacles.PlaceSwitcherObstacle;
import com.inoxapps.funrun.gameObjects.boosters.obstacles.SlicerRollerObstacle;
import com.inoxapps.funrun.gameObjects.boosters.powerups.ShieldPowerUp;
import com.inoxapps.funrun.gameObjects.boosters.powerups.SpeedPowerUp;
import com.inoxapps.funrun.gameObjects.boosters.powerups.ThunderPowerUp;

public class PowerUpPool extends Pool<AbstractBooster> {

	private final OrthographicCamera gamCam;
	private AbstractBooster[] boosters;

	public PowerUpPool(OrthographicCamera gameCam) {
		super(8);
		this.gamCam = gameCam;
		this.boosters = new AbstractBooster[8];
		for (int i = 0; i < 8; i++) {
			AbstractBooster booster = getRandomPowerUp(i + 1);
			this.boosters[i] = booster;
		}
	}

	private AbstractBooster getRandomPowerUp(int index) {
		switch (index) {
		case 1:
			return new ShieldPowerUp();
		case 2:
			return new ThunderPowerUp(gamCam);
		case 3:
			return new SpeedPowerUp();
		case 4:
			return new SlicerRollerObstacle();
		case 5:
			return new BombObstacle();
		case 6:
			return new PlaceSwitcherObstacle();
		case 7:
			return new FreezeBulletObstacle();
		case 8:
			return new BearTrapObstacle();
		}
		return new BearTrapObstacle();
	}

	@Override
	public AbstractBooster obtain() {
		int index = MathUtils.random(0, 7);
		AbstractBooster booster = boosters[index];
		boosters[index] = super.obtain();
		return booster;
	}

	@Override
	protected AbstractBooster newObject() {
		return getRandomPowerUp(MathUtils.random(1, 8));
	}
}
