package com.inoxapps.funrun.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Screen {

	public static OrthographicCamera guiCam;
	public static SpriteBatch batcher;
	public static List<Notification> notifications;

	static {
		guiCam = new OrthographicCamera();
		batcher = new SpriteBatch();
	}
	
	public void setViewPort(int viewPortWidth, int viewPortHeight) {
		guiCam.viewportHeight = viewPortHeight;
		guiCam.viewportWidth = viewPortWidth;
		guiCam.position.set(guiCam.viewportWidth / 2, guiCam.viewportHeight / 2, 0);
	}

	public static void initNotifications() {
		notifications = new ArrayList<Notification>();
	}

	public static void addNotification(Notification notification) {
		notifications.add(notification);
	}

	protected void renderNotifications() {
		Screen.batcher.setProjectionMatrix(Screen.guiCam.combined);
		for (Notification notification : notifications) {
			notification.notifyScreen();
		}
	}

	public abstract void update(float deltaTime);

	public abstract void render(float deltaTime);

	public abstract void pause();

	public abstract void resume();

	public abstract void dispose();

	public abstract void backKeyPressed();

	public abstract boolean touchDown(int screenX, int screenY, int pointer, int button);

	public void show() {
		
	}

}
