package com.zslide.view.splash;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.zslide.data.AuthenticationManager;
import com.zslide.utils.DeepLinkRouter;
import com.zslide.view.auth.AuthActivity;
import com.zslide.view.base.ActivityNavigator;
import com.zslide.view.base.Navigator;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public class SplashNavigator {

    private Uri deepLink;
    private final Navigator navigator;

    public SplashNavigator(AppCompatActivity activity) {
        Intent intent = activity.getIntent();
        this.deepLink = (intent == null) ? null : intent.getData();
        this.navigator = new ActivityNavigator(activity);
    }

    public Context getContext() {
        return navigator.getContext();
    }

    public void openMain() {
        Intent intent = navigator.intentFor(com.zslide.activities.MainActivity.class);
        navigator.start(intent);
    }

    public void openLogin() {
        Intent intent = navigator.intentFor(AuthActivity.class);
        navigator.start(intent);
    }

    public void openMarket(String packageName) {
        navigator.startMarket(packageName);
    }

    public void route() {
        if (deepLink != null && !TextUtils.isEmpty(deepLink.getPath())
                && DeepLinkRouter.route(getContext(), deepLink, false)) {
            navigator.finish();
        } else {
            if (AuthenticationManager.getInstance().isLoggedIn()) {
                openMain();
            } else {
                openLogin();
            }
        }
    }
}
