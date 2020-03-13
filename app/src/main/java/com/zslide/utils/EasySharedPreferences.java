package com.zslide.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by chulwoo on 15. 7. 8..
 * <p>
 * {@link SharedPreferences}를 좀 더 단순하게 사용하도록 돕는 유틸 클래스
 */
public class EasySharedPreferences {

    protected SharedPreferences sharedPreferences;

    private EasySharedPreferences() {
    }

    public static EasySharedPreferences with(Context context) {
        EasySharedPreferences instance = new EasySharedPreferences();
        instance.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return instance;
    }

    public static EasySharedPreferences with(Context context, String name) {
        EasySharedPreferences instance = new EasySharedPreferences();
        instance.sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return instance;
    }

    public void putString(String key, String value) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putInt(String key, int value) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putLong(String key, long value) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void putFloat(String key, float value) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void putBoolean(String key, boolean value) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void putObject(String key, Object value) {
        if (value == null) {
            throw new NullPointerException("can't put the null references");
        }

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString(key, json);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0L);
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public float getFloat(String key) {
        return sharedPreferences.getFloat(key, 0.0f);
    }

    public float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public <T> T getObject(String key, Class<T> classOfT) {
        return getObject(key, null, (Type) classOfT);
    }

    public <T> T getObject(String key, T defaultValue, Class<T> classOfT) {
        return getObject(key, defaultValue, (Type) classOfT);
    }

    public <T> T getObject(String key, Type typeToken) {
        return getObject(key, null, typeToken);
    }

    public <T> T getObject(String key, T defaultValue, Type typeToken) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, "");
        try {
            return TextUtils.isEmpty(json) ? defaultValue : gson.fromJson(json, typeToken);
        } catch (IllegalStateException | JsonSyntaxException e) {
            remove(key);
            return defaultValue;
        } catch (Exception e) {
            ZLog.e(this, e, "failure json parsing");
            return defaultValue;

        }
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
