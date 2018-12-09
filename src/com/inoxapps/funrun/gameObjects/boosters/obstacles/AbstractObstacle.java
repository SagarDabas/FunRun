package com.inoxapps.funrun.gameObjects.boosters.obstacles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.inoxapps.funrun.gameObjects.Player;
import com.inoxapps.funrun.gameObjects.PlayerState;
import com.inoxapps.funrun.gameObjects.boosters.AbstractBooster;

public abstract class AbstractObstacle extends AbstractBooster {

	public final ObstacleType TYPE;

	public AbstractObstacle(Sprite icon, ObstacleType type) {
		super(icon);
		this.TYPE = type;
	}

	public final void deploy(float x, float y, Player owner) {
		super.deploy(x, y, owner);
		this.setPosition(x + owner.getBounds().width / 2, y + owner.getBounds().height / 2);
		this.isStatic = false;
		this.isTrigger = true;
		this.changeState(BoosterState.DEPLOYED);
		this.onDeploy();
	}

	protected void onDeploy() {

	}
	
	//TODO placeSwitcher dependency
	public final boolean canHit(Player player) {
		if(state != BoosterState.HIT && player != owner){
			if(!(this instanceof PlaceSwitcherObstacle)){
				return true;
			}
			else
				return owner.getState() != PlayerState.HIT;
		}
		return false;
	}

	// TODO check the order of statements with PlaceSwitcherObstacle
	@Override
	public final  void onHitTimeOver() {
//		super.onHitTimeOver();
		changeState(BoosterState.DEPLOYED);
		playerHit.hitOver(false);
		resetPlayer();
	}

}
