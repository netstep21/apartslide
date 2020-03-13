package com.zslide.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.zslide.ZummaApp;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by jdekim43 on 2016. 1. 14..
 */
public class EventLogger {

    private static final String ACTION_CALL = "call";
    private static final String ACTION_ENTER_HOMEPAGE = "enter_homepage";
    private static final String ACTION_INVITE = "invite";

    public static void invite(Context context, String type, String media) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString(FirebaseAnalytics.Param.VALUE, media);
        bundle.putString("media", media);
        ZummaApp.get(context).getAnalytics().logEvent(ACTION_INVITE, bundle);
    }

    public static void redirectMarket(Context context, String packageName) {
        String baseUrl = "http://play.google.com/store/apps/details?id=";
        if (PackageUtil.isPackageUsable(context, "com.android.vending")) {
            baseUrl = "market://details?id=";
        }
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + packageName)));
    }

    public static void action(Context context, String action) {
        ZummaApp.get(context).getAnalytics().logEvent(action, new Bundle());
    }
}
