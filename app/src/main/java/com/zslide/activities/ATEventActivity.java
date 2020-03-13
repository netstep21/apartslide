package com.zslide.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.fragments.ATEventFragment;
import com.zslide.fragments.ATEventLandingFragment;

import butterknife.ButterKnife;

/**
 * Created by chulwoo on 16. 8. 11..
 */
public class ATEventActivity extends BaseActivity {

    public static final String KEY_AT_EVENT_TYPE = "atevent_type";
    public static final String KEY_AT_EVENT_RECRUITER_CODE = "atevent_code";

    public static final String TYPE_ALL = "all";
    public static final String TYPE_SHINHAN = "sh";
    public static final String TYPE_KB = "kb";
    public static final String TYPE_SAMSUNG = "samsung";

    private String recruiterCode;
    private String type = TYPE_ALL;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_single_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            type = uri.getQueryParameter(IntentConstants.EXTRA_TYPE);
            recruiterCode = uri.getQueryParameter(IntentConstants.EXTRA_RECRUITER_CODE);
        } else {
            type = intent.getStringExtra(IntentConstants.EXTRA_TYPE);
            recruiterCode = intent.getStringExtra(IntentConstants.EXTRA_RECRUITER_CODE);
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, ATEventLandingFragment.newInstance())
                .commit();

        if (!TYPE_ALL.equals(type) && !TextUtils.isEmpty(type)) {
            replace(type);
        }
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_at_event);
    }

    public void replace(String type) {
        String recruiterCode = "";
        if (this.type != null && this.type.equals(type)) {
            recruiterCode = this.recruiterCode;
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, ATEventFragment.newInstance(type, recruiterCode))
                .addToBackStack(null)
                .commit();
    }
}
