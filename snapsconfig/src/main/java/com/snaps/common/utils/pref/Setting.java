package com.snaps.common.utils.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.CrashlyticsBridge;


public class Setting {

	public static void testCrash() {
		sendContextNullLog();
	}

	private static void sendContextNullLog() {
		CrashlyticsBridge crashlyticsBridge = CrashlyticsBridge.getInstance();
		if (crashlyticsBridge == null) return;

		String builder = "SharedPreferences getSP() context is null";

		crashlyticsBridge.sendTextLog("Setting.java", builder);
	}

	private static void appendTextLog(String log) {
		CrashlyticsBridge crashlyticsBridge = CrashlyticsBridge.getInstance();
		if (crashlyticsBridge == null) return;

		String builder = "SharedPreferences log : " + log;

		crashlyticsBridge.appendTextLog("Setting.java", builder);
	}

	public static SharedPreferences getSP(Context context) {
		if (context == null) {
			sendContextNullLog();
			return null;
		}
		return context.getSharedPreferences((Config.isSnapsBitween() ? "snapsBetweenSetting" : "snapsSetting"), Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(context);
	}
	public static SharedPreferences.Editor getEditor(Context context) {
		if (context == null) {
			sendContextNullLog();
			return null;
		}

		SharedPreferences sp = getSP(context);
		if (sp == null) return null;

		return sp.edit();
	}
	
	public static void set(Context context, String key, String value) {
		SharedPreferences.Editor editor = getEditor(context);
		if (editor == null) return;
		editor.putString(key, value).commit();
	}
	public static void sets(Context context, String[] keys, String[] values) {
		SharedPreferences.Editor editor = getEditor(context);
		if (editor == null) return;

		if (keys.length != values.length)
			return;
		for (int i=0; i<keys.length; i++)
			editor.putString(keys[i], values[i]);
		editor.commit();
	}
	public static void set(Context context, String key, int value) {
		if (context == null)
			appendTextLog(key);
		SharedPreferences.Editor editor = getEditor(context);
		if (editor == null) return;
		editor.putInt(key, value).commit();
	}
	public static void set(Context context, String key, float value) {
		if (context == null)
			appendTextLog(key);
		SharedPreferences.Editor editor = getEditor(context);
		if (editor == null) return;
		editor.putFloat(key, value).commit();
	}
	public static void set(Context context, String key, boolean value) {
		if (context == null)
			appendTextLog(key);
		SharedPreferences.Editor editor = getEditor(context);
		if (editor == null) return;
		editor.putBoolean(key, value).commit();
	}

	public static void set(Context context, String key, long value) {
		if (context == null)
			appendTextLog(key);
		SharedPreferences.Editor editor = getEditor(context);
		if (editor == null) return;
		editor.putLong(key, value).commit();
	}

	public static String getString(Context context, String key) {
		if (context == null)
			appendTextLog(key);
		SharedPreferences editor = getSP(context);
		if (editor == null) return "";
		return editor.getString(key, "");
	}
	public static String getString(Context context, String key, String def) {
		if (context == null)
			appendTextLog(key);
		SharedPreferences editor = getSP(context);
		if (editor == null) return "";
		return editor.getString(key, def);
	}
	public static int getInt(Context context, String key) {
		if (context == null)
			appendTextLog(key);
		SharedPreferences editor = getSP(context);
		if (editor == null) return 0;
		return editor.getInt(key, 0);
	}
	public static float getFloat(Context context, String key) {
		if (context == null)
			appendTextLog(key);
		SharedPreferences editor = getSP(context);
		if (editor == null) return 0;
		return editor.getFloat(key, 0);
	}
	public static boolean getBoolean(Context context, String key) {
		if (context == null)
			appendTextLog(key);
		SharedPreferences sp = getSP(context);
		return sp != null && sp.getBoolean(key, false);
	}
	public static boolean getBoolean(Context context, String key, boolean def) {
		if (context == null)
			appendTextLog(key);
		SharedPreferences editor = getSP(context);
		return editor != null && editor.getBoolean(key, def);
	}
	public static long getLong(Context context, String key) {
		if (context == null)
			appendTextLog(key);
		SharedPreferences editor = getSP(context);
		if (editor == null) return 0;
		return editor.getLong(key, 0);
	}
}
