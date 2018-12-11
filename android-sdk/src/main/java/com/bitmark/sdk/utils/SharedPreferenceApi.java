package com.bitmark.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.bitmark.sdk.BuildConfig;

/**
 * @author Hieu Pham
 * @since 12/7/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public class SharedPreferenceApi {

    private static final String PREF_NAME = BuildConfig.APPLICATION_ID;

    private final SharedPreferences sharedPreferences;

    public SharedPreferenceApi(Context context) {
        this(context, PREF_NAME);
    }

    public SharedPreferenceApi(Context context, String prefName) {
        sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public <T> void put(String key, T data) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (data instanceof Integer) editor.putInt(key, (Integer) data);
        else if (data instanceof Long) editor.putLong(key, (Long) data);
        else if (data instanceof Float) editor.putFloat(key, (Float) data);
        else if (data instanceof Boolean) editor.putBoolean(key, (Boolean) data);
        else if (data instanceof String) editor.putString(key, (String) data);
        else throw new IllegalArgumentException("Un-support type of data");
        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        if (clazz == String.class) {
            return (T) sharedPreferences.getString(key, "");
        } else if (clazz == Boolean.class) {
            return (T) Boolean.valueOf(sharedPreferences.getBoolean(key, false));
        } else if (clazz == Float.class) {
            return (T) Float.valueOf(sharedPreferences.getFloat(key, 0));
        } else if (clazz == Integer.class) {
            return (T) Integer.valueOf(sharedPreferences.getInt(key, 0));
        } else if (clazz == Long.class) {
            return (T) Long.valueOf(sharedPreferences.getLong(key, 0));
        } else {
            return null;
        }
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
