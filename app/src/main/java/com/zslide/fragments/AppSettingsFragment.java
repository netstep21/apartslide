package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;

import com.zslide.Config;
import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.network.ApiConstants;
import com.zslide.utils.EventLogger;
import com.zslide.utils.PackageUtil;
import com.zslide.widget.AppSettingItemView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by chulwoo on 16. 1. 12..
 * <p>
 * Updated by chulwoo on 18. 1. 10..
 * 고객센터 메뉴 일부 이동
 */
public class AppSettingsFragment extends BaseFragment {

    @BindString(R.string.label_terms_use) String USE_TERMS_LABEL;
    @BindString(R.string.label_terms_privacy) String PRIVACY_TERMS_LABEL;
    @BindString(R.string.label_terms_location) String LOCATION_TERMS_LABEL;
    @BindString(R.string.path_terms_use) String USE_TERMS_PATH;
    @BindString(R.string.path_terms_privacy) String PRIVACY_TERMS_PATH;
    @BindString(R.string.path_terms_location) String LOCATION_TERMS_PATH;

    @BindView(R.id.versionInfo) AppSettingItemView versionInfoView;

    public static AppSettingsFragment newInstance() {
        return new AppSettingsFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_app_settings;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        setupVersionInfo();
    }

    @OnClick(R.id.useTerms)
    public void showUsageTerms() {
        Navigator.startWebViewActivity(getActivity(),
                USE_TERMS_LABEL, ApiConstants.BASE_URL + USE_TERMS_PATH);
    }

    @OnClick(R.id.privacyTerms)
    public void showPrivacyTerms() {
        Navigator.startWebViewActivity(getActivity(),
                PRIVACY_TERMS_LABEL, ApiConstants.BASE_URL + PRIVACY_TERMS_PATH);
    }

    @OnClick(R.id.locationTerms)
    public void showLocationTerms() {
        Navigator.startWebViewActivity(getActivity(),
                LOCATION_TERMS_LABEL, ApiConstants.BASE_URL + LOCATION_TERMS_PATH);
    }

    protected void setupVersionInfo() {
        if (Config.VERSION_CODE < PackageUtil.getLatestVersionCode(getContext())) {
            versionInfoView.setSubTitle(getString(R.string.message_version_update));
            versionInfoView.setVisibleNewBadge(true);
            versionInfoView.setOnClickListener(v ->
                    EventLogger.redirectMarket(getContext(), getContext().getPackageName()));
        } else {
            versionInfoView.setSubTitle(getString(R.string.message_version_equal));
        }

        versionInfoView.setInfo(String.format("v%s", Config.VERSION_NAME));
    }
}
