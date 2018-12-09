package com.inoxapps.funrun.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector3;
import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.utils.CoinManager;
import com.inoxapps.funrun.utils.OverlapTester;
import com.inoxapps.funrun.utils.Screen;
import com.inoxapps.funrun.utils.ScreenManager;

public class GameSelectionScreen extends Screen {

	private Vector3 touchPoint = new Vector3();
	
	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Screen.batcher.begin();
		Assets.ui_gameSelectBG.draw(Screen.batcher);
		Assets.ui_friendInviteButton.draw(Screen.batcher);
		Assets.ui_multiPlayerButton.draw(Screen.batcher);
		Assets.ui_singlePlayerButton.draw(Screen.batcher);
		Assets.ui_gameSelectHomeButton.draw(Screen.batcher);
		CoinManager.drawCoinHud();
		Screen.batcher.end();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		guiCam.unproject(touchPoint.set(screenX, screenY, 0));
//		if (OverlapTester.pointInRectangle(Assets.ui_friendInviteButton.getBoundingRectangle(), touchPoint.x,
//				touchPoint.y)) {
//			ScreenManager.getInstance().show(ScreenEnum.INVITE);
//			return true;
//		}
		if (OverlapTester.pointInRectangle(Assets.ui_multiPlayerButton.getBoundingRectangle(), touchPoint.x,
				touchPoint.y)) {
			ScreenManager.getInstance().show(ScreenEnum.SEARCHING);
			return true;
		}
		if (OverlapTester.pointInRectangle(Assets.ui_gameSelectHomeButton.getBoundingRectangle(), touchPoint.x,
				touchPoint.y)) {
			ScreenManager.getInstance().show(ScreenEnum.MAIN_MENU);
			return true;
		}
		if (OverlapTester.pointInRectangle(Assets.ui_singlePlayerButton.getBoundingRectangle(), touchPoint.x,
				touchPoint.y)) {
			ScreenManager.getInstance().show(ScreenEnum.LEVEL);
			return true;
		}
		return false;
	}

	@Override
	public void backKeyPressed() {
		ScreenManager.getInstance().show(ScreenEnum.MAIN_MENU);
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
