package com.inoxapps.funrun;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.inoxapps.funrun.gameObjects.Platform;
import com.inoxapps.funrun.gameObjects.Player;
import com.inoxapps.funrun.gameObjects.PlayerState;
import com.inoxapps.funrun.gameObjects.PowerUpTrigger;
import com.inoxapps.funrun.gameObjects.boosters.AbstractBooster;
import com.inoxapps.funrun.physics.GameObject;
import com.inoxapps.funrun.utils.Screen;

public class WorldRenderer {
	private World world;
	private float stateTime;
	private Sprite keyFrame;

	public WorldRenderer(World world) {
		this.world = world;
	}

	public void render(float deltaTime) {

		world.gameCam.update();
		Screen.batcher.setProjectionMatrix(world.gameCam.combined);

		// render platforms
		for (Platform platform : world.platforms) {
			if (isInView(platform)) {
				platform.render(deltaTime);
				if (platform == world.platforms.get(world.platforms.size - 1))
					renderFinishLine(deltaTime);
			}
		}

		// render triggers
		for (PowerUpTrigger trigger : world.triggers) {
			trigger.render();
		}

		// render obstacles
		for (AbstractBooster powerUp : world.powerUps) {
			Screen.batcher.setProjectionMatrix(world.gameCam.combined);
			powerUp.render(deltaTime);
			Screen.batcher.setProjectionMatrix(world.gameCam.combined);
		}
		// render players
		for (Player player : world.players)
			player.render(deltaTime);

		if (world.player.getState() != PlayerState.HIT) {
			Assets.commonlevel_playerArrow.setPosition(world.player.getPosition().x
					+ world.player.getBounds().getWidth() / 2, world.player.getPosition().y
					+ world.player.getBounds().getHeight() + 0.1f);
			Assets.commonlevel_playerArrow.draw(Screen.batcher);
		}

	}

	private void renderFinishLine(float deltaTime) {
		keyFrame = (Sprite) Assets.commonlevel_finishLineAnim.getKeyFrame(stateTime);
		if (isInView(keyFrame)) {
			keyFrame.draw(Screen.batcher);
		}
		stateTime += deltaTime;
	}

	private boolean isInView(GameObject object) {
		return (object.getBounds().getVertices()[0].x < world.gameCam.position.x + world.gameCam.viewportWidth || object
				.getBounds().getVertices()[1].x < world.gameCam.position.x + world.gameCam.viewportWidth);
	}

	private boolean isInView(Sprite sprite) {
		return (sprite.getX() < world.gameCam.position.x + world.gameCam.viewportWidth || (sprite.getX() + sprite
				.getWidth()) < world.gameCam.position.x + world.gameCam.viewportWidth);
	}

}
