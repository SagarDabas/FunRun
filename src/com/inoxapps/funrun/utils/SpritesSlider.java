package com.inoxapps.funrun.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.inoxapps.funrun.Settings;

public class SpritesSlider {

	private boolean canDrag;
	private final GestureDetector detector;
	private Rectangle clipBounds;
	public final OrthographicCamera cam;
	private final InputProcessor inputProcessor;
	private final Vector3 touchPoint;
	private float maxX;
	private float minX;

	public SpritesSlider(Rectangle clipArea, float minX, float maxX) {
		this.inputProcessor = Gdx.input.getInputProcessor();
		this.minX = minX;
		this.maxX = maxX;
		this.touchPoint = new Vector3();
		this.clipBounds = clipArea;
		this.cam = new OrthographicCamera(Settings.VIEWPORT_WIDTH, Settings.VIEWPORT_HEIGHT);
		this.cam.position.set(Settings.VIEWPORT_WIDTH / 2, Settings.VIEWPORT_HEIGHT / 2, 0);
		this.detector = initGestureDetector();
	}

	private boolean isRightLimitReached;
	private boolean isLeftLimitReached;

	private GestureDetector initGestureDetector() {
		return new GestureDetector(new GestureAdapter() {

			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
				touchPoint.set(x, y, 0);
				cam.unproject(touchPoint);
				if (clipBounds.contains(touchPoint.x, touchPoint.y)) {
					canDrag = true;
				} else
					canDrag = false;
				return true;
			}

			@Override
			public boolean pan(float x, float y, float deltaX, float deltaY) {
				if (canDrag)
					if (deltaX < 0 && maxX > (clipBounds.x + clipBounds.width - deltaX)
							|| (deltaX > 0 && minX < clipBounds.x - deltaX)) {
						cam.position.x += (-deltaX);
						clipBounds.setX(cam.position.x - clipBounds.width / 2);
						isRightLimitReached = false;
						isLeftLimitReached = false;
					} else {
						if (deltaX < 0)
							isRightLimitReached = true;
						else
							isLeftLimitReached = true;
					}
				return true;
			}
		}) {
			@Override
			public boolean touchUp(float x, float y, int pointer, int button) {
				super.touchUp(x, y, pointer, button);
				isRightLimitReached = false;
				isLeftLimitReached = false;
				return true;
			}
		};
	}

	public void update() {
		cam.update();
	}

	public void startSlider() {
		if (inputProcessor instanceof InputMultiplexer)
			((InputMultiplexer) inputProcessor).addProcessor(detector);
		else {
			InputMultiplexer multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(inputProcessor);
			multiplexer.addProcessor(detector);
			Gdx.input.setInputProcessor(multiplexer);
		}
	}

	public void endSlider() {
		if (inputProcessor instanceof InputMultiplexer)
			((InputMultiplexer) inputProcessor).removeProcessor(detector);
		else {
			Gdx.input.setInputProcessor(inputProcessor);
		}
	}

	public boolean isRightLimitReached() {
		return isRightLimitReached;
	}

	public boolean isLeftLimitReached() {
		return isLeftLimitReached;
	}
}
