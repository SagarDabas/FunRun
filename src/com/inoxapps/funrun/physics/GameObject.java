package com.inoxapps.funrun.physics;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;

public class GameObject {
	protected final Vector2 position;
	protected final RotatedRectangle bounds;
	protected HashMap<CollisionSide, RotatedRectangle> collisionBounds;

	public GameObject(Vector2 position, float width, float height) {
		this.position = position;
		this.bounds = new RotatedRectangle(position.x, position.y, width, height);
		this.collisionBounds = new HashMap<CollisionSide, RotatedRectangle>(4);
		initCollisionBounds();
	}

	
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
		setBounds(bounds.width, bounds.height);
	}

	public void setBounds(float width, float height) {
		bounds.set(position.x, position.y, width, height);
		setCollisionBounds();
	}

	public void setRotation(float rotation) {
		bounds.rotation = rotation;
		setBounds(bounds.width, bounds.height);
	}

	// counterclockwise
	private void initCollisionBounds() {
		collisionBounds.put(CollisionSide.DOWNWARD, new RotatedRectangle(bounds.x + Physics.collisionCheckingOffset,
				bounds.y, bounds.width - 2 * Physics.collisionCheckingOffset, bounds.height
						- Physics.collisionCheckingOffset));

		collisionBounds.put(CollisionSide.FORWARD, new RotatedRectangle(bounds.x + Physics.collisionCheckingOffset,
				bounds.y + Physics.collisionCheckingOffset, bounds.width - Physics.collisionCheckingOffset,
				bounds.height - 2 * Physics.collisionCheckingOffset));

		collisionBounds.put(CollisionSide.UPWARD, new RotatedRectangle(bounds.x + Physics.collisionCheckingOffset,
				bounds.y + Physics.collisionCheckingOffset, bounds.width - 2 * Physics.collisionCheckingOffset,
				bounds.height - Physics.collisionCheckingOffset));

		collisionBounds.put(CollisionSide.BACKWARD, new RotatedRectangle(bounds.x, bounds.y
				+ Physics.collisionCheckingOffset, bounds.width - Physics.collisionCheckingOffset, bounds.height - 2
				* Physics.collisionCheckingOffset));

	}

	private void setCollisionBounds() {

		collisionBounds.get(CollisionSide.DOWNWARD).set(bounds.x + Physics.collisionCheckingOffset, bounds.y,
				bounds.width - 2 * Physics.collisionCheckingOffset, bounds.height - Physics.collisionCheckingOffset);

		collisionBounds.get(CollisionSide.FORWARD).set(bounds.x + Physics.collisionCheckingOffset,
				bounds.y + Physics.collisionCheckingOffset, bounds.width - Physics.collisionCheckingOffset,
				bounds.height - 2 * Physics.collisionCheckingOffset);

		collisionBounds.get(CollisionSide.UPWARD).set(bounds.x + Physics.collisionCheckingOffset,
				bounds.y + Physics.collisionCheckingOffset, bounds.width - 2 * Physics.collisionCheckingOffset,
				bounds.height - Physics.collisionCheckingOffset);

		collisionBounds.get(CollisionSide.BACKWARD).set(bounds.x, bounds.y + Physics.collisionCheckingOffset,
				bounds.width - Physics.collisionCheckingOffset, bounds.height - 2 * Physics.collisionCheckingOffset);
	}

	public Vector2 getPosition() {
		return position;
	}

	public RotatedRectangle getBounds() {
		return bounds;
	}

	public RotatedRectangle getCollisionBound(CollisionSide side) {
		return collisionBounds.get(side);
	}
}
