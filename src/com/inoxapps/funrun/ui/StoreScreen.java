package com.inoxapps.funrun.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.Settings;
import com.inoxapps.funrun.utils.CoinManager;
import com.inoxapps.funrun.utils.OverlapTester;
import com.inoxapps.funrun.utils.Screen;
import com.inoxapps.funrun.utils.ScreenManager;
import com.inoxapps.funrun.utils.SpritesSlider;
import com.inoxapps.funrun.utils.StoreManager;
import com.inoxapps.funrun.utils.StoreManager.Items;

public class StoreScreen extends Screen {

	private Vector3 touchPoint = new Vector3();
	private Rectangle rect = new Rectangle(0, 0, Assets.ui_store_buyButton.getWidth(),
			Assets.ui_store_buyButton.getHeight());
	private final SpritesSlider slider;

	public StoreScreen() {
		this.slider = new SpritesSlider(new Rectangle(0, 0, 800, 480), 0, getMaxX());
	}

	private float getMaxX() {
		int i = Items.values().length - 1;
		float w = Assets.ui_store_frame.getWidth();
		return i % 3 == 0 ? (i / 3) * Settings.VIEWPORT_WIDTH + 50 + w : ((i - 1) % 3 == 0 ? ((i - 1) / 3)
				* Settings.VIEWPORT_WIDTH + 300 + w : ((i - 2) % 3 == 0 ? ((i - 2) / 3) * Settings.VIEWPORT_WIDTH + 575
				+ w : 0));
	}

	@Override
	public void render(float deltaTime) {

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Screen.guiCam.update();
		Screen.batcher.setProjectionMatrix(Screen.guiCam.combined);
		Screen.batcher.begin();
		Assets.ui_commonBG.draw(Screen.batcher, 0.5f);
		Assets.ui_store_coins.draw(Screen.batcher);
		Assets.ui_store_storetext.draw(Screen.batcher);
		Assets.ui_gameSelectHomeButton.draw(Screen.batcher);
		CoinManager.drawCoinHud();
		Screen.batcher.setProjectionMatrix(slider.cam.combined);
		int factor = -1;
		for (int i = 0; i < Items.values().length; i++) {
			Items item = Items.values()[i];
			if (i % 3 == 0) {
				factor++;
				drawFrame(factor * Settings.VIEWPORT_WIDTH + 50, 80, item);
			} else if ((i - 1) % 3 == 0) {
				drawFrame(factor * Settings.VIEWPORT_WIDTH + 300, 80, item);
			} else if ((i - 2) % 3 == 0) {
				drawFrame(factor * Settings.VIEWPORT_WIDTH + 575, 80, item);
			}
		}
		Screen.batcher.setProjectionMatrix(Screen.guiCam.combined);
		if (timerLeft > 0) {
			Assets.ui_store_scrollOverLeft.draw(Screen.batcher, timerLeft);
		} else if (timerRight > 0) {
			Assets.ui_store_scrollOverRight.draw(Screen.batcher, timerRight);
		}
		Screen.batcher.end();
	}

	private void drawFrame(float x, float y, Items item) {
		Assets.ui_store_frame.setPosition(x, y);
		Assets.ui_store_frame.draw(Screen.batcher);
		item.type.ui_store_playerIcon.setPosition(x, y);
		item.type.ui_store_playerIcon.draw(Screen.batcher);
		Assets.assets_Blackfont.draw(Screen.batcher, "" + item.rate, x + 75, y + 100);
		if (!item.isItemBought()) {
			Assets.ui_store_buyButton.setPosition(x + 60, y);
			Assets.ui_store_buyButton.draw(Screen.batcher);
		} else if (item.isItemEquipped()) {
			Assets.ui_store_equipped.setPosition(x + 5, y);
			Assets.ui_store_equipped.draw(Screen.batcher);
		} else if (!item.isItemEquipped()) {
			Assets.ui_store_equipButton.setPosition(x + 35, y);
			Assets.ui_store_equipButton.draw(Screen.batcher);
		}
	}

	private float timerLeft = 0;
	private float timerRight = 0;

	@Override
	public void update(float deltaTime) {

		slider.update();

		if ((slider.isLeftLimitReached() && timerLeft < 1)) {
			timerLeft += 0.1f;
		} else if ((slider.isRightLimitReached() && timerRight < 1)) {
			timerRight += 0.1f;
		}

		if (!slider.isLeftLimitReached() && timerLeft > 0) {
			timerLeft -= 0.1f;
		}
		if (!slider.isRightLimitReached() && timerRight > 0) {
			timerRight -= 0.1f;
		}

		if (Gdx.input.justTouched()) {
			slider.cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			rect.setY(80);
			int factor = -1;
			for (int i = 0; i < Items.values().length; i++) {
				Items item = Items.values()[i];
				if (i % 3 == 0) {
					factor++;
					rect.setX(factor * Settings.VIEWPORT_WIDTH + 50);
				} else if ((i - 1) % 3 == 0) {
					rect.setX(factor * Settings.VIEWPORT_WIDTH + 300);
				} else if ((i - 2) % 3 == 0) {
					rect.setX(factor * Settings.VIEWPORT_WIDTH + 575);
				}
				if (!item.isItemBought()) {
					rect.setX(rect.x + 60);
					rect.setWidth(Assets.ui_store_buyButton.getWidth());
					if (OverlapTester.pointInRectangle(rect, touchPoint.x, touchPoint.y)) {
						if (StoreManager.buyItem(item)) {
							Assets.assets_purchaseYES_Sound.play(Assets.soundVolume);
						} else {
							Assets.assets_purchaseNO_Sound.play(Assets.soundVolume);
						}
					}
				} else if (!item.isItemEquipped()) {
					rect.setX(rect.x + 35);
					rect.setWidth(Assets.ui_store_equipButton.getWidth());
					if (OverlapTester.pointInRectangle(rect, touchPoint.x, touchPoint.y)) {
						if (StoreManager.equipItem(item)) {
							Assets.assets_button_Sound.play(Assets.soundVolume);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		guiCam.unproject(touchPoint.set(screenX, screenY, 0));

		if (OverlapTester.pointInRectangle(Assets.ui_gameSelectHomeButton.getBoundingRectangle(), touchPoint.x,
				touchPoint.y)) {
			ScreenManager.getInstance().show(ScreenEnum.MAIN_MENU);
			return true;
		}
		return false;
	}

	@Override
	public void backKeyPressed() {
		ScreenManager.getInstance().show(ScreenEnum.MAIN_MENU);
	}

	@Override
	public void dispose() {
		this.slider.endSlider();
		Screen.batcher.setProjectionMatrix(Screen.guiCam.combined);
	}

	@Override
	public void show() {
		this.slider.startSlider();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}
}
