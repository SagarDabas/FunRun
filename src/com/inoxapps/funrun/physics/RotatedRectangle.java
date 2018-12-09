package com.inoxapps.funrun.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RotatedRectangle extends Rectangle {

	private static final long serialVersionUID = 1927129452652888499L;
	public Vector2[] boundsVertices;
	public float rotation;

	public RotatedRectangle(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.boundsVertices = new Vector2[4];
		initVertices();
	}

	// counterclockwise
	private void initVertices() {
		boundsVertices[0] = new Vector2(x, y);
		boundsVertices[1] = new Vector2(x + width * MathUtils.cosDeg(rotation), y + width * MathUtils.sinDeg(rotation));
		boundsVertices[2] = new Vector2(boundsVertices[1].x - height * MathUtils.sinDeg(rotation), boundsVertices[1].y
				+ height * MathUtils.cosDeg(rotation));
		boundsVertices[3] = new Vector2(x - height * MathUtils.sinDeg(rotation), y + height
				* MathUtils.cosDeg(rotation));
		
//		boundsVertices[3] = new Vector2(x, y);
//		boundsVertices[2] = new Vector2(x + width * MathUtils.cosDeg(rotation), y + width * MathUtils.sinDeg(rotation));
//		boundsVertices[1] = new Vector2(boundsVertices[2].x + height * MathUtils.sinDeg(rotation), boundsVertices[2].y
//				- height * MathUtils.cosDeg(rotation));
//		boundsVertices[0] = new Vector2(x + height * MathUtils.sinDeg(rotation), y - height
//				* MathUtils.cosDeg(rotation));

	}

	@Override
	public void set(float x, float y, float width, float height) {
		super.set(x, y, width, height);
		updateBoundsVertices();
	}

	private void updateBoundsVertices() {
		boundsVertices[0].set(x, y);
		boundsVertices[1].set(x + width * MathUtils.cosDeg(rotation), y + width * MathUtils.sinDeg(rotation));
		boundsVertices[2].set(boundsVertices[1].x - height * MathUtils.sinDeg(rotation), boundsVertices[1].y + height
				* MathUtils.cosDeg(rotation));
		boundsVertices[3].set(x - height * MathUtils.sinDeg(rotation), y + height * MathUtils.cosDeg(rotation));
		
		
//		boundsVertices[3].set(x, y);
//		boundsVertices[2].set(x + width * MathUtils.cosDeg(rotation), y + width * MathUtils.sinDeg(rotation));
//		boundsVertices[1].set(boundsVertices[2].x + height * MathUtils.sinDeg(rotation), boundsVertices[2].y
//				- height * MathUtils.cosDeg(rotation));
//		boundsVertices[0].set(x + height * MathUtils.sinDeg(rotation), y - height
//				* MathUtils.cosDeg(rotation));
	}

	public Vector2[] getVertices() {
		return boundsVertices;
	}

	public Vector2 getVertex(int number) {
		return boundsVertices[number];
	}

}
