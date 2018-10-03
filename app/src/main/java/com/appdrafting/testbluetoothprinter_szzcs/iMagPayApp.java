package com.appdrafting.testbluetoothprinter_szzcs;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import java.util.Locale;

public final class iMagPayApp extends Application {
	private final static String TAG = "iMagPayApp";
	public final static String LOCAL_EN = "en";
	public final static String LOCAL_CN = "zh";

	private SharedPreferences _preferences = null;

	@Override
	public void onCreate() {
		super.onCreate();
		_preferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
	}

	public String getLanguage() {
		return get("language", Locale.getDefault().getLanguage());
	}

	public void setLanguage(String language) {
		if (LOCAL_CN.equalsIgnoreCase(language))
			Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
		else
			Locale.setDefault(Locale.ENGLISH);
		Configuration config = getResources().getConfiguration();
		config.locale = Locale.getDefault();
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		getResources().updateConfiguration(config, metrics);
		set("language", language);
	}

	private String get(String key, String defaultValue) {
		return _preferences.getString(key, defaultValue);
	}

	private void set(String key, String value) {
		commit(key, value);
	}

	private void commit(String key, String value) {
		Editor editor = _preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
}