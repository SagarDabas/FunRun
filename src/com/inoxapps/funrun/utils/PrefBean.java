package com.inoxapps.funrun.utils;

import java.util.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.inoxapps.funrun.Settings;

public class PrefBean {

	public static Preferences preference = Gdx.app.getPreferences(Settings.APP_NAME);

	public static enum Pref {
		MUSIC, SOUND, TOTAL_SCORE, NAME
	}

	public static void setIntegerPreference(Pref key, int value) {
		preference.putInteger(key.name(), value);
		preference.flush();
	}

	public static int getIntegerPreference(Pref key, int correspond) {
		correspond = preference.getInteger(key.name(), correspond);
		preference.flush();
		return correspond;
	}

	public static void setStringPreference(Pref key, String value) {
		preference.putString(key.name(), value);
		preference.flush();
	}

	public static String getStringPreference(Pref key, String value) {
		value = preference.getString(key.name(), value);
		preference.flush();
		return value;
	}

	public static void setBooleanPreference(Pref key, boolean value) {
		preference.putBoolean(key.name(), value);
		preference.flush();
	}

	public static boolean getBooleanPreference(Pref key, boolean correspond) {
		correspond = preference.getBoolean(key.name(), correspond);
		preference.flush();
		return correspond;
	}

	public static void setBooleanPreference(StoreManager.Items key1, StoreManager.Item_Status key2, boolean value) {
		preference.putBoolean(key1.name() + key2.name(), value);
		preference.flush();
	}

	public static boolean getBooleanPreference(StoreManager.Items key1, StoreManager.Item_Status key2,
			boolean correspond) {
		correspond = preference.getBoolean(key1.name() + key2.name(), correspond);
		preference.flush();
		return correspond;
	}

	public static long getLongPreference(Pref key, long correspond) {
		correspond = preference.getLong(key.name(), correspond);
		preference.flush();
		return correspond;
	}

	public static void setLongPreference(Pref key, long correspond) {
		preference.putLong(key.name(), correspond);
		preference.flush();
	}

	public static void setHashMapPreference(Map<String, ?> correspond) {
		preference.put(correspond);
		preference.flush();
	}

	public static float getFloatPreference(Pref key, float correspond) {
		correspond = preference.getFloat(key.name(), correspond);
		preference.flush();
		return correspond;
	}

	public static void setFloatPreference(Pref key, float correspond) {
		preference.putFloat(key.name(), correspond);
		preference.flush();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getHashMapPreference(Map<String, Object> correspond) {
		correspond = (Map<String, Object>) preference.get();
		return correspond;
	}
}
