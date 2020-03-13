package com.zslide.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.zslide.R;

import java.lang.ref.WeakReference;

public class CustomWebChromeClient extends WebChromeClient {
    private WeakReference<Context> contextRef;

    public CustomWebChromeClient(Context context) {
        setContext(context);
    }

    private Context getContext() {
        return contextRef.get();
    }

    private void setContext(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton(R.string.label_confirm, (dialog, which) -> result.confirm())
                .setCancelable(false)
                .create()
                .show();

        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton(R.string.label_confirm, (dialog, which) -> result.confirm())
                .setNegativeButton(R.string.label_cancel, (dialog, which) -> result.cancel())
                .setCancelable(false)
                .create()
                .show();

        return true;
    }
}
