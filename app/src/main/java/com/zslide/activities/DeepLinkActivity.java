package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zslide.R;
import com.zslide.utils.DeepLinkRouter;
import com.zslide.utils.ZLog;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;

public class DeepLinkActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();
        AppInvite.AppInviteApi.getInvitation(googleApiClient, this, false)
                .setResultCallback(appInviteInvitationResult -> {
                    Status status = appInviteInvitationResult.getStatus();
                    ZLog.e(this, "status:  " + status.getStatusCode());
                });
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // do nothing
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_deep_link);
    }

    @Override
    protected int getLayoutResourceId() {
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        DeepLinkRouter.route(this, getIntent().getData(), false);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}