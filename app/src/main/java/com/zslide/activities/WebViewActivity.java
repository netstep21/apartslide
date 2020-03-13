package com.zslide.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.widget.CustomWebChromeClient;
import com.zslide.widget.CustomWebView;
import com.zslide.widget.CustomWebViewClient;

import butterknife.BindView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class WebViewActivity extends BaseActivity {
    @BindView(R.id.webView) CustomWebView webView;
    @BindView(R.id.progress) CircularProgressBar progressView;
    @BindView(R.id.toolbar) Toolbar toolBar;
    @BindView(R.id.webViewBody) FrameLayout webViewBody;

    private String title;
    private String url;
    private Boolean canGoBack;
    private Boolean noToolBar;

    private final String TAG = "WebViewActivity";

    @Override
    public String getScreenName() {
        return title;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_web;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWebView(webView);
        onNewIntent(getIntent());
        showProgress();
        noToolBarAction();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, canGoBack.toString());
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack() && canGoBack) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void load(String url) {
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        Uri data = intent.getData();

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            title = data.getQueryParameter(IntentConstants.EXTRA_TITLE);
            url = data.getQueryParameter(IntentConstants.EXTRA_URL);
            canGoBack = data.getBooleanQueryParameter(IntentConstants.EXTRA_CAN_GO_BACK, true);
            noToolBar = data.getBooleanQueryParameter(IntentConstants.EXTRA_NO_TOOLBAR, false);
        } else {
            title = intent.getStringExtra(IntentConstants.EXTRA_TITLE);
            url = intent.getStringExtra(IntentConstants.EXTRA_URL);
            canGoBack = intent.getBooleanExtra(IntentConstants.EXTRA_CAN_GO_BACK, true);
            noToolBar = intent.getBooleanExtra(IntentConstants.EXTRA_NO_TOOLBAR, false);
        }

        load(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void setupWebView(WebView webView) {
        CookieManager.getInstance().setAcceptCookie(true);
        webView.setWebViewClient(new CustomWebViewClient(this, this::showProgress, this::hideProgress));
        webView.setWebChromeClient(new CustomWebChromeClient(this));
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    protected void showProgress() {
        if (!TextUtils.isEmpty(title)) {
            showTitleProgress();
        } else {
            progressView.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgress() {
        if (!TextUtils.isEmpty(title)) {
            hideTitleProgress();
        } else {
            progressView.setVisibility(View.GONE);
        }
    }

    protected void noToolBarAction() {
        if (noToolBar) {
            toolBar.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) webViewBody.getLayoutParams();
            lp.addRule(RelativeLayout.BELOW, 0);
            webViewBody.setLayoutParams(lp);
        }
    }
}
