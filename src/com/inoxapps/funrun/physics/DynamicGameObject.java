package com.inoxapps.funrun.physics;

import com.badlogic.gdx.math.Vector2;

public class DynamicGameObject extends GameObject {

	protected final Vector2 velocity;
	protected final Vector2 acceleration;
	public boolean isStatic = false;
	public boolean isTrigger = false;
	public boolean useGravity = true;
	public boolean isCollidable = false;
	public float restitution = 0f;

	public DynamicGameObject(Vector2 position, float width, float height,
			Vector2 velocity, Vector2 acceleration) {
		super(position, width, height);
		this.velocity = velocity;
		this.acceleration = acceleration;
		Physics.listOfDynamicGameObject.add(this);
	}

	public DynamicGameObject(Vector2 position, float width, float height) {
		this(position, width, height, new Vector2(), new Vector2());
	}

	public DynamicGameObject(float width, float height) {
		this(new Vector2(), width, height, new Vector2(), new Vector2());
	}

	public DynamicGameObject() {
		this(new Vector2(), 0, 0, new Vector2(), new Vector2());
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(float x, float y) {
		velocity.set(x, y);
	}
	

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(float x, float y) {
		acceleration.set(x,y);
	}

	// collision among static and non-static objects.
	public void onCollision(DynamicGameObject gameObject, CollisionSide side) {
	}

	// collision among non-static objects.
	public void onTriggered(DynamicGameObject gameObject) {

	}

}
