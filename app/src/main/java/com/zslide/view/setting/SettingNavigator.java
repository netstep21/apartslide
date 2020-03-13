package com.zslide.view.setting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.zslide.activities.AppSettingsActivity;
import com.zslide.activities.FamilyActivity;
import com.zslide.activities.FamilyRegistrationActivity;
import com.zslide.activities.MyAccountActivity;
import com.zslide.activities.NoticesActivity;
import com.zslide.activities.ServiceCenterActivity;
import com.zslide.view.base.Navigator;
import com.zslide.view.setting.dialog.LockerSnoozeDialog;

import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by chulwoo on 2018. 1. 5..
 */

public class SettingNavigator {

    private final Navigator navigator;

    public SettingNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public void openMyAccountPage() {
        navigator.startActivity(MyAccountActivity.class);
    }

    public void openFamilyInfoPage() {
        navigator.startActivity(FamilyActivity.class);
    }

    public void openFamilyRegistrationPage() {
        navigator.startActivity(FamilyRegistrationActivity.class);
    }

    public void openNoticesPage() {
        navigator.startActivity(NoticesActivity.class);
    }

    public void openHelpPage() {
        navigator.startActivity(ServiceCenterActivity.class);
    }

    public void openAppInfoPage() {
        navigator.startActivity(AppSettingsActivity.class);
    }

    public void startZmoney() {
        Context context = navigator.getContext();
        if (context == null) {
            return;
        }

        final String packageName = "com.mobitle.zmoney";
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));

        for (ApplicationInfo packageInfo : packages) {
            if (packageName.equals(packageInfo.packageName)) {
                context.startActivity(new Intent(pm.getLaunchIntentForPackage(packageInfo.packageName)));
                return;
            }
        }

        context.startActivity(intent);
    }

    public void showZmoneySuggestionDialog() {
        com.zslide.Navigator.startZmoneyApplicationStore(navigator.getContext(), (long) 0);
    }

    public void showLockerEnableDialog(Consumer<Integer> onSnoozeAction, Action onCancelAction) {
        navigator.showDialog(LockerSnoozeDialog.newInstance(onSnoozeAction, onCancelAction));
    }
}
