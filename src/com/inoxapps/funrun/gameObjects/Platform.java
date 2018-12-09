package com.inoxapps.funrun.gameObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.inoxapps.funrun.physics.DynamicGameObject;
import com.inoxapps.funrun.utils.Screen;

public class Platform extends DynamicGameObject {

	private float count;
	private boolean isSpawnPlatform;
	private final Array<Sprite> platformSprites;
	public Sprite sprite;
	private boolean canJump;

	public Platform(Vector2 position, float width, float height, Array<Sprite> sprites) {
		super(position, width, height);
		this.isStatic = true;
		this.platformSprites = sprites;
		this.isCollidable = true;
		if (platformSprites != null) {
			this.count = ((bounds.width - 2 * platformSprites.get(1).getWidth()) / platformSprites.get(0).getWidth());
			if (count < 1)
				count = 0;
			count = (float) Math.ceil(count);
		}
	}

	public Platform(Vector2 position, float width, float height, Sprite sprite) {
		this(position, width, height, (Array<Sprite>) null);
		this.sprite = sprite;
		this.count = bounds.width / sprite.getWidth();
		count = (float) Math.floor(count);
	}

	// since same sprite is used for many platforms so position is has to be set
	// in each frame
	public void render(float deltaTime) {
		if (platformSprites != null) {
			platformSprites.get(1).setPosition(position.x, position.y);
			platformSprites.get(1).setOrigin(0, 0);
			platformSprites.get(1).setRotation(bounds.rotation);
			platformSprites.get(1).draw(Screen.batcher);

			if (count > 0) {
				platformSprites.get(0).setPosition(position.x + platformSprites.get(1).getWidth(), position.y);
				platformSprites.get(0).setOrigin(-platformSprites.get(1).getWidth(), 0);
				platformSprites.get(0).setRotation(bounds.rotation);
				platformSprites.get(0).draw(Screen.batcher);
				for (int i = 1; i < count; i++) {
					platformSprites.get(0).setPosition(
							position.x + platformSprites.get(1).getWidth() + i * platformSprites.get(0).getWidth(),
							position.y);
					platformSprites.get(0).setOrigin(
							-i * platformSprites.get(0).getWidth() - platformSprites.get(1).getWidth(), 0);
					platformSprites.get(0).setRotation(bounds.rotation);
					platformSprites.get(0).draw(Screen.batcher);
				}

				platformSprites.get(2).setPosition(
						position.x + platformSprites.get(1).getWidth() + (count) * platformSprites.get(0).getWidth(),
						position.y);
				platformSprites.get(2).setOrigin(
						-(count) * platformSprites.get(0).getWidth() - platformSprites.get(1).getWidth(), 0);
				platformSprites.get(2).setRotation(bounds.rotation);
				platformSprites.get(2).draw(Screen.batcher);
			} else {
				platformSprites.get(2).setPosition(position.x + platformSprites.get(1).getWidth(), position.y);
				platformSprites.get(2).setOrigin(-platformSprites.get(1).getWidth(), 0);
				platformSprites.get(2).setRotation(bounds.rotation);
				platformSprites.get(2).draw(Screen.batcher);
			}
		} else if (sprite != null) {
			for (int i = 0; i < count; i++) {
				sprite.setPosition(position.x + i * sprite.getWidth(), position.y);
				sprite.setOrigin(-i * sprite.getWidth(), 0);
				sprite.setRotation(bounds.rotation);
				sprite.draw(Screen.batcher);
			}
		}
	}

	public boolean isSpawnPlatform() {
		return isSpawnPlatform;
	}

	public void setSpawnPlatform(boolean isSpawnPlatform) {
		this.isSpawnPlatform = isSpawnPlatform;
	}

	public void setCanJump(boolean b) {
		this.canJump = b;
	}

	public boolean canJump() {
		return this.canJump;
	}
}
