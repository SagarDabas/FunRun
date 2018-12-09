/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.inoxapps.funrun.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.inoxapps.funrun.physics.RotatedRectangle;

public class OverlapTester {
	public static boolean overlapRectangles(Rectangle r1, Rectangle r2) {
		return (r1.x < r2.x + r2.width && r1.x + r1.width > r2.x && r1.y < r2.y + r2.height && r1.y + r1.height > r2.y);
	}

	public static boolean pointInRectangle(Rectangle r, Vector2 p) {
		return r.x <= p.x && r.x + r.width >= p.x && r.y <= p.y && r.y + r.height >= p.y;
	}

	public static boolean pointInRectangle(Rectangle r, float x, float y) {
		return r.x <= x && r.x + r.width >= x && r.y <= y && r.y + r.height >= y;
	}

	public static boolean pointInRotatedRectangle(RotatedRectangle r, Vector2 pointToRotate) {
		float cosine = MathUtils.cosDeg(-r.rotation);
		float sine = MathUtils.sinDeg(-r.rotation);
		float x1 = pointToRotate.x;
		float y1 = pointToRotate.y;
		float x = (x1 - r.getVertex(0).x) * cosine - (y1 - r.getVertex(0).y) * sine + r.getVertex(0).x;
		float y = (x1 - r.getVertex(0).x) * sine + (y1 - r.getVertex(0).y) * cosine + r.getVertex(0).y;
		return pointInRectangle(r, x, y);
	}
}
