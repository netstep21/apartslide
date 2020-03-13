package com.zslide.view.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.zslide.utils.DeepLinkRouter;
import com.zslide.utils.PackageUtil;

import java.lang.ref.WeakReference;

/**
 * Created by chulwoo on 2018. 1. 5..
 */

public class FragmentNavigator implements Navigator {

    private final WeakReference<Fragment> fragmentRef;

    public FragmentNavigator(@NonNull Fragment fragment) {
        this.fragmentRef = new WeakReference<>(fragment);
    }

    private Activity getActivity() {
        return fragmentRef.get() == null ? null : fragmentRef.get().getActivity();
    }

    @Override
    public Context getContext() {
        return fragmentRef.get().getContext();
    }

    @Override
    public Intent intentFor(Class cls) {
        if (getActivity() != null) {
            return new Intent(getActivity(), cls);
        }

        return null;
    }

    @Override
    public void start(Intent intent) {
        if (fragmentRef.get() != null && intent != null) {
            fragmentRef.get().startActivity(intent);
        }
    }

    @Override
    public void startForResult(Intent intent, int requestCode) {
        if (fragmentRef.get() != null && intent != null) {
            fragmentRef.get().startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void startActivity(Class cls) {
        if (fragmentRef.get() != null) {
            Intent intent = new Intent(getActivity(), cls);
            fragmentRef.get().startActivity(intent);
        }
    }

    @Override
    public void startActivityForResult(Class cls, int requestCode) {
        if (fragmentRef.get() != null) {
            Intent intent = new Intent(getActivity(), cls);
            fragmentRef.get().startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void finish() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void navigateFrom(Uri uri) {
        if (getContext() != null) {
            DeepLinkRouter.route(getContext(), uri, false);
        }
    }

    @Override
    public void startMarket(String packageName) {
        if (getActivity() != null) {
            Activity activity = getActivity();
            String baseUrl = "http://play.google.com/store/apps/details?id=";
            if (PackageUtil.isPackageUsable(activity, "com.android.vending")) {
                baseUrl = "market://details?id=";
            }
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + packageName)));
        }
    }

    @Override
    public void showDialog(DialogFragment dialog) {
        if (fragmentRef.get() != null) {
            Fragment fragment = fragmentRef.get();
            FragmentManager fm = fragment.getFragmentManager();
            DialogFragment prevDialog = (DialogFragment) fm.findFragmentByTag("dialog");
            if (prevDialog != null && prevDialog.isAdded()) {
                prevDialog.dismiss();
            }

            dialog.show(fragment.getFragmentManager(), "dialog");
        }
    }
}