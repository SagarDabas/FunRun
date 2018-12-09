package com.inoxapps.funrun;

public enum PlayerPosition {

	FIRST(10), SECOND(7), THIRD(3), FOURTH(-5);

	public final int coins;

	PlayerPosition(int coins) {
		this.coins = coins;
	}
}
