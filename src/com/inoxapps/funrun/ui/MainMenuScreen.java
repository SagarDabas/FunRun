package com.inoxapps.funrun.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector3;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.FunRun;
import com.inoxapps.funrun.utils.ActionResolver;
import com.inoxapps.funrun.utils.CoinManager;
import com.inoxapps.funrun.utils.OverlapTester;
import com.inoxapps.funrun.utils.PrefBean;
import com.inoxapps.funrun.utils.Screen;
import com.inoxapps.funrun.utils.ScreenManager;
import com.inoxapps.funrun.utils.StoreManager;

public class MainMenuScreen extends Screen {

	private Vector3 touchPoint = new Vector3();

	public MainMenuScreen() {
		if (StoreManager.getEquippedItem() == null)
			StoreManager.equipDefaultItem();
		if (PrefBean.getStringPreference(PrefBean.Pref.NAME, "").length() == 0) {
			Gdx.input.getTextInput(new TextInputListener() {

				@Override
				public void input(String text) {
					PrefBean.setStringPreference(PrefBean.Pref.NAME, text);
					Assets.playerName = text;
					CoinManager.addCoins(100);
				}

				@Override
				public void canceled() {
					PrefBean.setStringPreference(PrefBean.Pref.NAME, "You");
					Assets.playerName = "You";
					CoinManager.addCoins(100);
				}
			}, "Name", "You");
		}
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Screen.batcher.begin();
		Assets.splashScreen.draw(Screen.batcher);
		Assets.ui_playButton.draw(Screen.batcher);
		Assets.ui_storeButton.draw(Screen.batcher);
		Assets.ui_soundBox.draw(Screen.batcher);
		CoinManager.drawCoinHud();
		if (Assets.soundVolume == 1)
			Assets.ui_soundOn.draw(Screen.batcher);
		else
			Assets.ui_soundOff.draw(Screen.batcher);
		Screen.batcher.end();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		guiCam.unproject(touchPoint.set(screenX, screenY, 0));
		if (OverlapTester.pointInRectangle(Assets.ui_soundBox.getBoundingRectangle(), touchPoint.x, touchPoint.y)) {
			Assets.setSoundsVolume();
			return true;
		}
		if (OverlapTester.pointInRectangle(Assets.ui_playButton.getBoundingRectangle(), touchPoint.x, touchPoint.y)) {
			ScreenManager.getInstance().show(ScreenEnum.GAME_SELECT);
			return true;
		}
		if (OverlapTester.pointInRectangle(Assets.ui_storeButton.getBoundingRectangle(), touchPoint.x, touchPoint.y)) {
			ScreenManager.getInstance().show(ScreenEnum.STORE);
			return true;
		}
		return false;
	}

	@Override
	public void backKeyPressed() {
		FunRun.actionResolver.showPopup("Quit", "Are you sure?", "Ok", "Cancel", false,
				new ActionResolver.PopupCallback() {
					@Override
					public void onClicked(boolean isYesClicked) {
						if (isYesClicked)
							FunRun.actionResolver.gameFinish();
					}
				});
	}

	@Override
	public void update(float deltaTime) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
	}

}
