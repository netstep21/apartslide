package com.zslide.data.local.base;

import android.content.Context;

import com.zslide.utils.EasySharedPreferences;

import java.lang.reflect.Type;

/**
 * Created by chulwoo on 2018. 1. 10..
 * <p>
 * {@link AbstractLocalSource}에서 내부적으로 패키지를 prefix로 사용하므로
 * {@link AbstractLocalSource#getPreferencesName()} 구현이나 put, get 메소드에 전달하는 key에선 간단한 이름만 명시해주면 됩니다.
 */

public abstract class AbstractLocalSource {

    protected abstract String getPreferencesName();

    private final EasySharedPreferences prefs;

    protected AbstractLocalSource(Context context) {
        prefs = EasySharedPreferences.with(context, getPrefix() + getPreferencesName());
    }

    private String getPrefix() {
        return getClass().getName() + ".";
    }

    private String buildKey(String key) {
        return getPrefix() + key;
    }

    public void putString(String key, String value) {
        prefs.putString(buildKey(key), value);
    }

    public void putInt(String key, int value) {
        prefs.putInt(buildKey(key), value);
    }

    public void putLong(String key, long value) {
        prefs.putLong(buildKey(key), value);
    }

    public void putFloat(String key, float value) {
        prefs.putFloat(buildKey(key), value);
    }

    public void putBoolean(String key, boolean value) {
        prefs.putBoolean(buildKey(key), value);
    }

    public void putObject(String key, Object value) {
        prefs.putObject(buildKey(key), value);
    }

    public String getString(String key) {
        return prefs.getString(buildKey(key));
    }

    public String getString(String key, String defValue) {
        return prefs.getString(buildKey(key), defValue);
    }

    public int getInt(String key) {
        return prefs.getInt(buildKey(key));
    }

    public int getInt(String key, int defValue) {
        return prefs.getInt(buildKey(key), defValue);
    }

    public long getLong(String key) {
        return prefs.getLong(buildKey(key));
    }

    public long getLong(String key, long defValue) {
        return prefs.getLong(buildKey(key));
    }

    public float getFloat(String key) {
        return prefs.getFloat(buildKey(key));
    }

    public float getFloat(String key, float defValue) {
        return prefs.getFloat(buildKey(key));
    }

    public boolean getBoolean(String key) {
        return prefs.getBoolean(buildKey(key));
    }

    public boolean getBoolean(String key, boolean defValue) {
        return prefs.getBoolean(buildKey(key), defValue);
    }

    public <T> T getObject(String key, Class<T> classOfT) {
        return prefs.getObject(buildKey(key), classOfT);
    }

    public <T> T getObject(String key, T defaultValue, Class<T> classOfT) {
        return prefs.getObject(buildKey(key), defaultValue, classOfT);
    }

    public <T> T getObject(String key, Type typeToken) {
        return prefs.getObject(buildKey(key), typeToken);
    }

    public <T> T getObject(String key, T defaultValue, Type typeToken) {
        return prefs.getObject(buildKey(key), defaultValue, typeToken);
    }

    public void remove(String key) {
        prefs.remove(buildKey(key));
    }

    public void clear() {
        prefs.clear();
    }
}
