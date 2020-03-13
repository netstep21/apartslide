package com.zslide.view.base;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by chulwoo on 2018. 1. 5..
 */

public class NavigatorFactory {

    public static Navigator create(@NonNull AppCompatActivity activity) {
        return new ActivityNavigator(activity);
    }

    public static Navigator create(@NonNull Fragment fragment) {
        return new FragmentNavigator(fragment);
    }
}
