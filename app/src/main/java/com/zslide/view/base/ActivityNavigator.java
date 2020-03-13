package com.zslide.view.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.zslide.utils.DeepLinkRouter;
import com.zslide.utils.PackageUtil;

import java.lang.ref.WeakReference;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public class ActivityNavigator implements Navigator {

    private final WeakReference<AppCompatActivity> activityRef;

    public ActivityNavigator(@NonNull AppCompatActivity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    @Override
    public Context getContext() {
        return activityRef.get();
    }

    @Override
    public Intent intentFor(Class cls) {

        if (activityRef.get() != null) {
            return new Intent(activityRef.get(), cls);
        }

        return null;
    }

    @Override
    public void start(Intent intent) {
        if (activityRef.get() != null && intent != null) {
            activityRef.get().startActivity(intent);
        }
    }

    @Override
    public void startForResult(Intent intent, int requestCode) {
        if (activityRef.get() != null) {
            activityRef.get().startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void startActivity(Class cls) {
        if (activityRef.get() != null) {
            Intent intent = new Intent(activityRef.get(), cls);
            activityRef.get().startActivity(intent);
        }
    }

    @Override
    public void startActivityForResult(Class cls, int requestCode) {
        if (activityRef.get() != null) {
            Intent intent = new Intent(activityRef.get(), cls);
            activityRef.get().startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void finish() {
        if (activityRef.get() != null) {
            activityRef.get().finish();
        }
    }

    @Override
    public void navigateFrom(Uri uri) {
        if (activityRef.get() != null) {
            DeepLinkRouter.route(activityRef.get(), uri, false);
        }
    }

    @Override
    public void startMarket(String packageName) {
        if (activityRef.get() != null) {
            Activity activity = activityRef.get();
            String baseUrl = "http://play.google.com/store/apps/details?id=";
            if (PackageUtil.isPackageUsable(activity, "com.android.vending")) {
                baseUrl = "market://details?id=";
            }
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + packageName)));
        }
    }

    @Override
    public void showDialog(DialogFragment dialog) {
        if (activityRef.get() != null) {
            AppCompatActivity activity = activityRef.get();
            FragmentManager fm = activity.getSupportFragmentManager();
            DialogFragment prevDialog = (DialogFragment) fm.findFragmentByTag("dialog");
            if (prevDialog != null && prevDialog.isAdded()) {
                prevDialog.dismiss();
            }

            dialog.show(activity.getSupportFragmentManager(), "dialog");
        }
    }
}