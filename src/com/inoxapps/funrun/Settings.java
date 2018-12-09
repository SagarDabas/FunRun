package com.inoxapps.funrun;

import com.badlogic.gdx.math.Vector2;

public interface Settings {
	float CAM_SCALE = 40;
	int VIEWPORT_WIDTH = 800;
	int VIEWPORT_HEIGHT = 480;
	float GRAVITY = -10;
	float BG_LAYER2_SPEED = 40f / CAM_SCALE;
	float BG_LAYER3_SPEED = 20f / CAM_SCALE;
	int PLAYER_NUMBER = 4;
	String APP_NAME = "FunRunPref";

	interface PlayerSettings {
		Vector2 VELOCITY = new Vector2(0, 0);
		Vector2 ACCELERATION = new Vector2(5f, 0);
		Vector2 MAX_SPEED = new Vector2(8f, 0);
		Vector2 JUMP_SPEED = new Vector2(5f, 8f);
	}
}
