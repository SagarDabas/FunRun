package com.inoxapps.funrun.gameObjects.boosters.powerups;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.inoxapps.funrun.gameObjects.Player;
import com.inoxapps.funrun.gameObjects.boosters.AbstractBooster;

public abstract class AbstractPowerUp extends AbstractBooster {

	public AbstractPowerUp(Sprite icon) {
		super(icon);
	}

	protected final void setTimeLimits(float hitTime) {
		super.setTimeLimits(0, hitTime);
	}

	@Override
	public final void onHitTimeOver() {
//		super.onHitTimeOver();
		changeState(BoosterState.FREE);
		if (!(this instanceof ThunderPowerUp))
			playerHit.hitOver(true);
		resetPlayer();
	}

	@Override
	protected final void renderDeployed(float deltaTime) {
		// Never comes in this state
	}
	
	@Override
	public final  void deploy(float x, float y, Player owner) {
		super.deploy(x, y, owner);
		super.hitPlayer(owner);
	}
}
