package com.inoxapps.funrun.utils;

import com.inoxapps.funrun.Assets;
import com.inoxapps.funrun.PlayerPosition;

public final class CoinManager {

	private static int totalCoins = getTotalCoinsFromFile();

	private CoinManager() {
	}

	public static void updateInitialCoins() {
		totalCoins += PlayerPosition.FOURTH.coins;
	}

	public static int getTotalCoins() {
		return totalCoins;
	}

	public static boolean updateCoins(PlayerPosition position) {
		totalCoins += (position.coins - PlayerPosition.FOURTH.coins);
		if (totalCoins < 0) {
			totalCoins = 0;
			return false;
		}
		return true;
	}

	public static void addCoins(int value) {
		totalCoins += value;
	}

	public static void resetCoins() {
		totalCoins = 0;
	}

	public static void flushTotalCoins() {
		PrefBean.setIntegerPreference(PrefBean.Pref.TOTAL_SCORE, totalCoins);
	}

	public static int getTotalCoinsFromFile() {
		return PrefBean.getIntegerPreference(PrefBean.Pref.TOTAL_SCORE, 0);
	}

	public static void drawCoinHud() {
		Assets.ui_store_coins.draw(Screen.batcher);
		Assets.assets_Blackfont.draw(Screen.batcher, CoinManager.getTotalCoins() + "", 710f, 462f);
	}
}
