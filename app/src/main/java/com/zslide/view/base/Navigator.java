package com.zslide.view.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public interface Navigator {

    Context getContext();

    Intent intentFor(Class cls);

    void start(Intent intent);

    void startForResult(Intent intent, int requestCode);

    void startActivity(Class cls);

    void startActivityForResult(Class cls, int requestCode);

    void finish();

    void navigateFrom(Uri uri);

    void startMarket(String packageName);

    void showDialog(DialogFragment dialog);
}