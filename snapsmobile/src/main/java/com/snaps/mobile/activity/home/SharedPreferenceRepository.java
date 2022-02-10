package com.snaps.mobile.activity.home;

import android.content.Context;

import com.snaps.common.utils.pref.Setting;

public class SharedPreferenceRepository {

    private Context mContext;

    public SharedPreferenceRepository(Context context) {
        this.mContext = context;
    }

    public void set(String key, String value) {
        Setting.set(mContext, key, value);
    }

    public void set(String key, int value) {
        Setting.set(mContext, key, value);
    }

    public void set(String key, float value) {
        Setting.set(mContext, key, value);
    }

    public void set(String key, boolean value) {
        Setting.set(mContext, key, value);
    }

    public void set(String key, long value) {
        Setting.set(mContext, key, value);
    }

    public String getString(String key) {
        return Setting.getString(mContext, key);
    }

    public String getString(String key, String def) {
        return Setting.getString(mContext, key, def);
    }

    public int getInt(String key) {
        return Setting.getInt(mContext, key);
    }

    public float getFloat(String key) {
        return Setting.getFloat(mContext, key);
    }

    public boolean getBoolean(String key) {
        return Setting.getBoolean(mContext, key);
    }

    public boolean getBoolean(String key, boolean def) {
        return Setting.getBoolean(mContext, key, def);
    }

    public long getLong(String key) {
        return Setting.getLong(mContext, key);
    }

}
