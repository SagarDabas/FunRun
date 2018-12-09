package com.inoxapps.funrun;

import com.inoxapps.funrun.ui.ScreenEnum;
import com.inoxapps.funrun.utils.ActionResolver;
import com.inoxapps.funrun.utils.CoinManager;
import com.inoxapps.funrun.utils.Game;
import com.inoxapps.funrun.utils.ScreenManager;

public class FunRun extends Game {

	public static ActionResolver actionResolver;

	public FunRun(ActionResolver actionResolver) {
		FunRun.actionResolver = actionResolver;
	}

	@Override
	public void create() {
		super.create();
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().show(ScreenEnum.LOADING);
	}

	@Override
	public void dispose() {
		super.dispose();
		CoinManager.flushTotalCoins();
		Assets.dispose();
		// TODO cannot use both of them together, Assets.dispose disposes the
		// sprites and batcher.dispose also disposes the same sprites and thus
		// gives the fatal 11 error. This error occurs when someone tries to
		// dispose an disposed object.
		// Screen.batcher.dispose();
		ScreenManager.getInstance().dispose();
	}

}
