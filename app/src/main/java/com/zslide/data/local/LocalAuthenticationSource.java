package com.zslide.data.local;

import android.content.Context;

import com.zslide.data.local.base.AbstractLocalSource;

/**
 * Created by chulwoo on 2018. 1. 10..
 */

public class LocalAuthenticationSource extends AbstractLocalSource {

    private static final String KEY_API_KEY = "api_key";

    @Override
    protected String getPreferencesName() {
        return "auth";
    }

    public LocalAuthenticationSource(Context context) {
        super(context);
    }

    public String getApiKey() {
        return getString(KEY_API_KEY, "");
    }

    public void setApiKey(String apiKey) {
        putString(KEY_API_KEY, apiKey);
    }
}
