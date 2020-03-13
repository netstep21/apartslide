package com.zslide.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.utils.PackageUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jdekim43 on 2016. 1. 13..
 */
public class VersionInfoFragment extends BaseFragment {

    @BindView(R.id.currentVersion) TextView currentVersionView;
    @BindView(R.id.newVersion) TextView newVersionView;
    @BindView(R.id.updateButton) Button updateButton;

    private int currentVersionCode;
    private String currentVersionName;
    private int latestVersionCode;
    private String latestVersionName;

    public static VersionInfoFragment newInstance() {
        return new VersionInfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        currentVersionCode = PackageUtil.getVersionCode(context);
        currentVersionName = PackageUtil.getVersionName(context);
        latestVersionCode = PackageUtil.getLatestVersionCode(context);
        latestVersionName = PackageUtil.getLatestVersionName(context);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_checkversion;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        currentVersionView.setText(String.format("v%s", currentVersionName));
        newVersionView.setText(String.format("v%s", latestVersionName));
        setupUpdateButton(currentVersionCode < latestVersionCode);
    }

    private void setupUpdateButton(boolean needUpdate) {
        updateButton.setEnabled(needUpdate);
        updateButton.setText((needUpdate) ?
                R.string.message_version_update : R.string.message_version_equal);
    }

    @OnClick(R.id.updateButton)
    public void update() {
        final String appPackageName = getContext().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
