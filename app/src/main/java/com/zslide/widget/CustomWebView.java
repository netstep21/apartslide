package com.zslide.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.zslide.data.AuthenticationManager;
import com.zslide.network.ApiConstants;
import com.zslide.utils.RequestSignature;

import java.util.HashMap;
import java.util.Map;


public class CustomWebView extends BaseWebView {

    public CustomWebView(Context context) {
        this(context, null, 0);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* Notice: ONLY Get request caught here */
    @Override
    public void loadUrl(String url) {
        Map<String, String> headers = new HashMap<>();
        this.loadUrl(url, headers);
    }

    /* Notice: ONLY Get request caught here */
    @Override
    public void loadUrl(String url, Map<String, String> headers) {
        long timeStamp = System.currentTimeMillis() / 1000L;
        headers.put("Authorization", AuthenticationManager.getInstance().getApiKey());
        headers.put("x-app-agent", ApiConstants.USER_AGENT);
        headers.put("x-app-signature", RequestSignature.get(timeStamp));
        headers.put("x-app-timestamp", Long.toString(timeStamp));
        super.loadUrl(url, headers);
    }
}
