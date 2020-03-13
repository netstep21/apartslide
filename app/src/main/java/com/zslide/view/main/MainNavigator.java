package com.zslide.view.main;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import com.zslide.R;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.view.base.ActivityNavigator;
import com.zslide.view.base.Navigator;
import com.zslide.view.setting.SettingsActivity;

/**
 * Created by chulwoo on 2017. 12. 29..
 */

public class MainNavigator {

    private final Navigator navigator;

    public MainNavigator(AppCompatActivity activity) {
        this.navigator = new ActivityNavigator(activity);
    }

    public void navigateFrom(String target) {
        navigator.navigateFrom(Uri.parse(target));
    }

    public void openTempApartmentCompletePage() {

    }

    public void showTempApartmentWaitDialog() {
        /*SimpleAlertDialog.newInstance(activity.getString(R.string.temp_apartment_wait))
                .show(((AppCompatActivity) activity).getSupportFragmentManager(), "wait");*/
    }

    public void openZmoneyPaymentsPage() {
        /*        Navigator.startZmoneyPaymentsActivity(activity);*/
    }

    public void openFamilyRegistrationPage() {
        /*Navigator.startFamilyRegistrationActivity(activity);*/
    }

    public void openSettingsPage() {
        navigator.startActivity(SettingsActivity.class);
    }

    public void showBlurredNameLeaderDialog(Family family) {
        if (navigator.getContext() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) navigator.getContext();
            User leader = family.getLeader();
            String message = activity.getString(R.string.message_address_alert,
                    leader.getBlurredName(navigator.getContext()), leader.getBlurredPhoneNumber(true));
            SimpleAlertDialog.newInstance(message)
                    .show(activity.getSupportFragmentManager(), "alert");
        }
    }
}
