package com.inoxapps.funrun.physics;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.collision.Ray;
import com.inoxapps.funrun.utils.OverlapTester;

public class Physics {

	private Physics() {
	}

	public static float deltaTime;
	public static float gravity;
	public static ArrayList<DynamicGameObject> listOfDynamicGameObject;
	public static float collisionCheckingOffset;

	/*
	 * It might happen that collision may go undetected, follow either of the
	 * two steps : 1. Increase collsionCheckingOffset or 2. decrease deltaTime
	 * 
	 * Make sure to use the same size image for an object for each frame(like in
	 * animation), otherwise the results might be weird
	 */

	public static void initPhysics() {
		deltaTime = 2f;
		gravity = -9.81f;
		collisionCheckingOffset = 0.3f;
		listOfDynamicGameObject = new ArrayList<DynamicGameObject>();
	}

	public static void updatePosition(DynamicGameObject gameObject, float deltaTime) {

		if (gameObject.useGravity) {
			gameObject.getPosition().add(
					gameObject.getVelocity().x * deltaTime + (0.5f) * gameObject.getAcceleration().x * deltaTime
							* deltaTime,
					gameObject.getVelocity().y * deltaTime + (0.5f) * (gameObject.getAcceleration().y + gravity)
							* deltaTime * deltaTime);
		} else {
			gameObject.getPosition().add(
					gameObject.getVelocity().x * deltaTime + (0.5f) * gameObject.getAcceleration().x * deltaTime
							* deltaTime,
					gameObject.getVelocity().y * deltaTime + (0.5f) * (gameObject.getAcceleration().y) * deltaTime
							* deltaTime);
		}

	}

	public static void updateVelocity(DynamicGameObject gameObject, float deltaTime) {

		if (gameObject.useGravity) {
			gameObject.getVelocity().add(gameObject.getAcceleration().x * deltaTime,
					(gameObject.getAcceleration().y + gravity) * deltaTime);
		} else {
			gameObject.getVelocity().add(gameObject.getAcceleration().x * deltaTime,
					(gameObject.getAcceleration().y) * deltaTime);
		}

	}

	private static void updateProperties(DynamicGameObject gameObject, float deltaTime) {
		updateVelocity(gameObject, deltaTime);
		updatePosition(gameObject, deltaTime);
		gameObject.setBounds(gameObject.getBounds().width, gameObject.getBounds().height);
	}

	// Cannot use foreach loop because if somebody creates an dynamic
	// object in the collision or trigger callback, then it will return the
	// concurrent modification error.
	public static void update(float deltaTime) {
		for (int i = 0; i < listOfDynamicGameObject.size(); i++) {
			DynamicGameObject firstObj = listOfDynamicGameObject.get(i);
			// only non-static objects are collided and triggered
			if (!firstObj.isStatic) {
				updateProperties(firstObj, deltaTime);
				for (int j = 0; j < listOfDynamicGameObject.size(); j++) {
					DynamicGameObject otherObj = listOfDynamicGameObject.get(j);
					// first has to be non-triggered and other has to be
					// triggered
					checkIfTriggered(firstObj, otherObj);
					// first and second object have to be collidable for
					// collision
					if (firstObj.isCollidable && otherObj.isCollidable && otherObj.isStatic) {
						if (checkForwardSlopeCollision(firstObj, otherObj)) {
						} else if (checkBackwardSlopeCollision(firstObj, otherObj)) {
						} else if (checkDownwardCollision(firstObj, otherObj)) {
						} else if (checkForwardCollision(firstObj, otherObj)) {
						} else if (checkUpwardCollision(firstObj, otherObj)) {
						} else if (checkBackwardCollision(firstObj, otherObj)) {
						}
					}
				}
			}
		}
	}

	private static void checkIfTriggered(DynamicGameObject firstObj, DynamicGameObject otherObj) {
		if (!firstObj.isTrigger && otherObj.isTrigger) {
			if (OverlapTester.overlapRectangles(firstObj.getBounds(), otherObj.getBounds())) {
				firstObj.onTriggered(otherObj);
				otherObj.onTriggered(firstObj);
			}
		}
	}

	private static boolean checkBackwardSlopeCollision(DynamicGameObject firstObj, DynamicGameObject otherObj) {
		if (otherObj.getBounds().rotation < 0
				&& OverlapTester.pointInRotatedRectangle(otherObj.getBounds(), firstObj.getBounds().getVertex(0))) {

			float w = (-firstObj.getBounds().getVertex(0).x + otherObj.getBounds().getVertex(2).x)
					/ MathUtils.cosDeg(-otherObj.getBounds().rotation);

			firstObj.setVelocity(firstObj.getVelocity().x,
					-firstObj.getVelocity().x * MathUtils.cosDeg(-otherObj.getBounds().rotation));
			firstObj.setPosition(firstObj.getPosition().x,
					otherObj.getBounds().getVertex(2).y + w * MathUtils.sinDeg(-otherObj.getBounds().rotation));

			firstObj.onCollision(otherObj, CollisionSide.DOWNWARD);
			otherObj.onCollision(firstObj, CollisionSide.DOWNWARD);
			return true;
		}
		return false;
	}

	private static boolean checkForwardSlopeCollision(DynamicGameObject firstObj, DynamicGameObject otherObj) {
		if (otherObj.getBounds().rotation > 0
				&& OverlapTester.pointInRotatedRectangle(otherObj.getBounds(), firstObj.getBounds().getVertex(1))) {

			float w = (firstObj.getBounds().getVertex(1).x - otherObj.getBounds().getVertex(3).x)
					/ MathUtils.cosDeg(otherObj.getBounds().rotation);

			firstObj.setVelocity(firstObj.getVelocity().x, -firstObj.getVelocity().y * firstObj.restitution);
			firstObj.setPosition(firstObj.getPosition().x,
					otherObj.getBounds().getVertex(3).y + w * MathUtils.sinDeg(otherObj.getBounds().rotation));

			firstObj.onCollision(otherObj, CollisionSide.DOWNWARD);
			otherObj.onCollision(firstObj, CollisionSide.DOWNWARD);
			return true;
		}
		return false;
	}

	private static boolean checkDownwardCollision(DynamicGameObject dynObj, DynamicGameObject staticObj) {

		if (staticObj.getBounds().rotation == 0
				&& dynObj.getVelocity().y < 0
				&& OverlapTester.overlapRectangles(dynObj.getCollisionBound(CollisionSide.DOWNWARD),
						staticObj.getCollisionBound(CollisionSide.UPWARD))) {

			dynObj.setVelocity(dynObj.getVelocity().x, -dynObj.getVelocity().y * dynObj.restitution);
			dynObj.setPosition(
					dynObj.getPosition().x,
					staticObj.getCollisionBound(CollisionSide.UPWARD).y
							+ staticObj.getCollisionBound(CollisionSide.UPWARD).height);

			dynObj.onCollision(staticObj, CollisionSide.DOWNWARD);
			staticObj.onCollision(dynObj, CollisionSide.DOWNWARD);
			return true;
		}
		return false;
	}

	private static boolean checkForwardCollision(DynamicGameObject dynObj, DynamicGameObject staticObj) {
		if (staticObj.getBounds().rotation == 0
				&& dynObj.getVelocity().x > 0
				&& OverlapTester.overlapRectangles(dynObj.getCollisionBound(CollisionSide.FORWARD),
						staticObj.getCollisionBound(CollisionSide.BACKWARD))) {

			dynObj.setVelocity(-dynObj.getVelocity().x * dynObj.restitution, dynObj.getVelocity().y);
			dynObj.setPosition(
					staticObj.getCollisionBound(CollisionSide.BACKWARD).x
							- dynObj.getCollisionBound(CollisionSide.FORWARD).width, dynObj.getPosition().y);

			dynObj.onCollision(staticObj, CollisionSide.FORWARD);
			staticObj.onCollision(dynObj, CollisionSide.FORWARD);
			return true;
		}
		return false;
	}

	private static boolean checkUpwardCollision(DynamicGameObject dynObj, DynamicGameObject staticObj) {
		if (staticObj.getBounds().rotation == 0
				&& dynObj.getVelocity().y > 0
				&& OverlapTester.overlapRectangles(dynObj.getCollisionBound(CollisionSide.UPWARD),
						staticObj.getCollisionBound(CollisionSide.DOWNWARD))) {

			dynObj.setVelocity(dynObj.getVelocity().x, -dynObj.getVelocity().y * dynObj.restitution);
			dynObj.setPosition(
					dynObj.getPosition().x,
					staticObj.getCollisionBound(CollisionSide.DOWNWARD).y
							- dynObj.getCollisionBound(CollisionSide.UPWARD).height);

			dynObj.onCollision(staticObj, CollisionSide.UPWARD);
			staticObj.onCollision(dynObj, CollisionSide.UPWARD);
			return true;
		}
		return false;
	}

	private static boolean checkBackwardCollision(DynamicGameObject dynObj, DynamicGameObject staticObj) {
		if (staticObj.getBounds().rotation == 0
				&& dynObj.getVelocity().x < 0
				&& OverlapTester.overlapRectangles(dynObj.getCollisionBound(CollisionSide.BACKWARD),
						staticObj.getCollisionBound(CollisionSide.FORWARD))) {

			dynObj.setVelocity(-dynObj.getVelocity().x * dynObj.restitution, dynObj.getVelocity().y);
			dynObj.setPosition(
					staticObj.getCollisionBound(CollisionSide.BACKWARD).x
							+ staticObj.getCollisionBound(CollisionSide.BACKWARD).width, dynObj.getPosition().y);

			dynObj.onCollision(staticObj, CollisionSide.BACKWARD);
			staticObj.onCollision(dynObj, CollisionSide.BACKWARD);
			return true;
		}
		return false;
	}

	public static boolean intersectRayBoundsFast(Ray ray, Rectangle box) {
		float a, b;
		float min, max;
		float divX = 1 / ray.direction.x;
		float divY = 1 / ray.direction.y;

		a = (box.x - ray.origin.x) * divX;
		b = (box.x + box.width - ray.origin.x) * divX;
		if (a < b) {
			min = a;
			max = b;
		} else {
			min = b;
			max = a;
		}

		a = (box.y - ray.origin.y) * divY;
		b = (box.height - ray.origin.y) * divY;
		if (a > b) {
			float t = a;
			a = b;
			b = t;
		}

		if (a > min)
			min = a;
		if (b < max)
			max = b;

		return (max >= 0) && (max >= min);
	}

}
