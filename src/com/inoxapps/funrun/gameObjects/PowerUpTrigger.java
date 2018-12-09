package com.inoxapps.funrun.gameObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.physics.DynamicGameObject;
import com.inoxapps.funrun.utils.Screen;

public final class PowerUpTrigger extends DynamicGameObject {

	private final Sprite looks;
	private boolean isSoundPlaying;

	public PowerUpTrigger(Vector2 position, Sprite looks) {
		super(position, looks.getWidth(), looks.getHeight());
		this.looks = looks;
		this.isStatic = true;
		this.isTrigger = true;
	}

	@Override
	public void onTriggered(DynamicGameObject gameObject) {
		super.onTriggered(gameObject);
		if (!isSoundPlaying && gameObject instanceof Player && !((Player) gameObject).isAI) {
			Assets.assets_power_collect_Sound.play(Assets.soundVolume);
			isSoundPlaying = true;
		}
	}

	public void render() {
		looks.setPosition(getPosition().x, getPosition().y);
		looks.draw(Screen.batcher);
	}

}
