package com.zslide.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.zslide.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chulwoo on 16. 2. 12..
 * <p>
 * Based on http://developer.dramancompany.com/2015/11/리멤버의-안드로이드-6-0-m버전-대응기/
 */
public class PermissionUtil {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean checkAndRequestPermission(Activity activity, int permissionRequestCode, String... permissions) {
        String[] requiredPermissions = getRequiredPermissions(activity, permissions);

        if (requiredPermissions.length > 0 && !activity.isDestroyed()) {
            ActivityCompat.requestPermissions(activity, requiredPermissions, permissionRequestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkAndRequestPermission(Fragment fragment, int permissionRequestCode, String... permissions) {
        String[] requiredPermissions = getRequiredPermissions(fragment.getContext() != null ?
                fragment.getContext() : fragment.getActivity(), permissions);

        if (requiredPermissions.length > 0 && fragment.isAdded()) {
            fragment.requestPermissions(requiredPermissions, permissionRequestCode);
            return false;
        } else {
            return true;
        }
    }

    public static String[] getRequiredPermissions(Context context, String... permissions) {
        List<String> requiredPermissions = new ArrayList<>();

        if (context == null) return requiredPermissions.toArray(new String[1]);

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(permission);
            }
        }

        return requiredPermissions.toArray(new String[requiredPermissions.size()]);
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) return false;

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    public static void showRationalDialog(Activity activity, int message, int requestCode, DialogInterface.OnClickListener cancelListener) {
        showRationalDialog(activity, activity.getString(message), requestCode, cancelListener);
    }

    public static void showRationalDialog(Activity activity, String message, int requestCode, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.label_settings, (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:" + activity.getPackageName()));
                        activity.startActivityForResult(intent, requestCode);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();

                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        activity.startActivityForResult(intent, requestCode);
                    }
                })
                .setNegativeButton(R.string.label_cancel, cancelListener)
                .setOnCancelListener(dialog ->
                        cancelListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE));
        AlertDialog dialog = builder.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(activity.getResources().getColor(R.color.gray_a));
    }


    public static void showRationalDialog(Fragment fragment, String message, int requestCode, DialogInterface.OnClickListener cancelListener) {
        Context context = fragment.getContext() == null ? fragment.getActivity() : fragment.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.label_settings, (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:" + context.getPackageName()));
                        fragment.startActivityForResult(intent, requestCode);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();

                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        fragment.startActivityForResult(intent, requestCode);
                    }
                })
                .setNegativeButton(R.string.label_cancel, cancelListener)
                .setOnCancelListener(dialog ->
                        cancelListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE));
        AlertDialog dialog = builder.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.gray_a));
    }
}
