package com.zslide.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.models.NaverUserProfileWrapper;
import com.zslide.network.NaverApi;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 15. 8. 4..
 */
public class NaverLinkActivity extends com.zslide.view.base.BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OAuthLogin.getInstance().init(this,
                getString(R.string.naver_id),
                getString(R.string.naver_secret),
                getString(R.string.naver_name));

        OAuthLogin oauthLoginModule = OAuthLogin.getInstance();
        oauthLoginModule.startOauthLoginActivity(this, new NaverLoginHandler(this));
    }

    /**
     * for naver
     */
    static class NaverLoginHandler extends OAuthLoginHandler {

        private final NaverLinkActivity activity;

        NaverLoginHandler(NaverLinkActivity activity) {
            this.activity = activity;
        }

        @Override
        public void run(boolean success) {
            if (success) {
                new NaverApi(activity).getProfile()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::finishWithResult, e -> {
                            NaverApi.ErrorHandler.handleError(e);
                            activity.setResult(RESULT_CANCELED);
                            activity.finish();
                            Toast.makeText(activity, R.string.message_failure_network, Toast.LENGTH_SHORT).show();
                        });
            } else {
                activity.setResult(RESULT_CANCELED);
                activity.finish();
            }
        }

        private void finishWithResult(NaverUserProfileWrapper.NaverUserProfile profile) {
            OAuthLogin oauthLoginModule = OAuthLogin.getInstance();
            Intent intent = new Intent();
            intent.putExtra(IntentConstants.EXTRA_ID, profile.getId());
            intent.putExtra(IntentConstants.EXTRA_REFRESH_TOKEN, oauthLoginModule.getRefreshToken(activity));
            intent.putExtra(IntentConstants.EXTRA_ACCESS_TOKEN, oauthLoginModule.getAccessToken(activity));
            intent.putExtra(IntentConstants.EXTRA_SEX, profile.getGender());
            intent.putExtra(IntentConstants.EXTRA_NAME, profile.getName());
            intent.putExtra(IntentConstants.EXTRA_NICKNAME, profile.getNickname());
            intent.putExtra(IntentConstants.EXTRA_EMAIL, profile.getEmail());
            activity.setResult(RESULT_OK, intent);
            activity.finish();
        }
    }
}