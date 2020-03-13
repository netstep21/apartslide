package com.zslide.view.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.zslide.data.model.AlertMessage;
import com.zslide.view.base.BaseActivity;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public class SplashActivity extends BaseActivity implements SplashFragment.Callback, AlertMessageDialog.Callback {

    private static final String TAG_FRAGMENT = "com.apartslide.view.splash.SplashFragment";
    private static final String TAG_UPDATE_DIALOG = "com.apartslide.view.splash.AlertMessageDialog";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, SplashFragment.newInstance(), TAG_FRAGMENT)
                .commit();
    }


    @Override
    public void showUpdateDialog(AlertMessage alertMessage) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(TAG_UPDATE_DIALOG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ft.commit();

        DialogFragment dialog = AlertMessageDialog.newInstance(alertMessage);
        dialog.show(getSupportFragmentManager(), TAG_UPDATE_DIALOG);
    }

    @Override
    public void showGoogleApiAvailabilityErrorDialog(GoogleApiAvailability googleApiAvailability) {
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getApplicationContext());
        googleApiAvailability.getErrorDialog(this, resultCode, 9000).show();
    }

    @Override
    public void onConfirm(String action) {
        SplashFragment splashFragment = (SplashFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        splashFragment.onConfirm(action);
    }


    @Override
    public void onCancel() {
        SplashFragment splashFragment = (SplashFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        splashFragment.onCancel();
    }
}
